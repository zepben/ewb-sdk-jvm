/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.networktrace

import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.cim.iec61970.base.core.PhaseCode
import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.cim.iec61970.base.wires.BusbarSection
import com.zepben.evolve.services.network.tracing.connectivity.NominalPhasePath
import com.zepben.evolve.services.network.tracing.networktrace.conditions.Conditions
import com.zepben.evolve.services.network.tracing.networktrace.conditions.Conditions.stopAtOpen
import com.zepben.evolve.services.network.tracing.networktrace.operators.NetworkStateOperators
import com.zepben.evolve.services.network.tracing.networktrace.trackers.NetworkTraceTracker
import com.zepben.evolve.services.network.tracing.networktrace.trackers.RecursiveNetworkTraceTracker
import com.zepben.evolve.services.network.tracing.networktrace.trackers.TerminalNetworkTraceTracker
import com.zepben.evolve.services.network.tracing.networktrace.trackers.toPhasesSet
import com.zepben.evolve.services.network.tracing.traversal.*

/**
 * A [Traversal] implementation specifically designed to trace connected [Terminal]s of [ConductingEquipment] in a network.
 *
 * This trace manages the complexity of network connectivity, especially in cases where connectivity is not straightforward,
 * such as with [BusbarSection]s and [Clamp]s. It checks the in service flag of equipment and only steps to equipment that is marked as in service.
 * It also provides the optional ability to trace only specific phases.
 *
 * Steps are represented by a [NetworkTraceStep], which contains a [StepPath] and allows associating arbitrary data with each step.
 * The arbitrary data for each step is computed via a [ComputeNextT] or [ComputeNextTWithPaths] function provided at construction.
 * The trace invokes these functions when queueing each item and stores the result with the next step.
 *
 * When traversing, this trace will step on every connected terminal, as long as they match all the traversal conditions.
 * Each step is classified as either an external step or an internal step:
 *
 * - **External Step**: Moves from one terminal to another with different [Terminal.conductingEquipment].
 * - **Internal Step**: Moves between terminals within the same [Terminal.conductingEquipment].
 *
 * Often, you may want to act upon a [ConductingEquipment] only once, rather than multiple times for each internal and external terminal step.
 * To achieve this, [actionType] to [NetworkTraceActionType.FIRST_STEP_ON_EQUIPMENT]. With this type enabled, the trace will only call step actions and
 * stop conditions once for each [ConductingEquipment], regardless of how many terminals it has. However, queue conditions are always called for each terminal
 * step regardless of the flag, since the trace is terminal connectivity-based, and not calling queue conditions for every step can disrupt the traversal.
 *
 * The network trace is state-aware by requiring an instance of [NetworkStateOperators].
 * This allows traversal conditions and step actions to query and act upon state-based properties and functions of equipment in the network when required.
 *
 * @param T the type of [NetworkTraceStep.data]
 */
// TODO [Review]: Rename to NetworkTraversal and make abstract with no run or addStartItem functions. Create new NetworkTrace class that has those functions.
//                This allows us to create descendant classes such as DownstreamTree that can inherit NetworkTraversal which allows you to configure the
//                trace with additional step actions and conditions which could be very handy while still maintaining all the Traversal interface functions.
//                Caveat: We need to remove the ability to clear conditions and step actions.
class NetworkTrace<T> private constructor(
    val networkStateOperators: NetworkStateOperators,
    queueType: QueueType<NetworkTraceStep<T>, NetworkTrace<T>>,
    parent: NetworkTrace<T>? = null,
    private val actionType: NetworkTraceActionType,
) : Traversal<NetworkTraceStep<T>, NetworkTrace<T>>(queueType, parent) {

    private val tracker: NetworkTraceTracker = if (queueType !is BranchingQueueType)
        TerminalNetworkTraceTracker()
    else
        RecursiveNetworkTraceTracker(parent?.tracker as? RecursiveNetworkTraceTracker, TerminalNetworkTraceTracker())

    internal constructor(
        networkStateOperators: NetworkStateOperators,
        queue: TraversalQueue<NetworkTraceStep<T>>,
        actionType: NetworkTraceActionType,
        computeNextT: ComputeNextT<T>,
    ) : this(
        networkStateOperators,
        BasicQueueType(NetworkTraceQueueNext.basic(networkStateOperators::isInService, computeNextT.withType(actionType)), queue),
        null,
        actionType,
    )

    internal constructor(
        networkStateOperators: NetworkStateOperators,
        queue: TraversalQueue<NetworkTraceStep<T>>,
        actionType: NetworkTraceActionType,
        computeNextT: ComputeNextTWithPaths<T>,
    ) : this(
        networkStateOperators,
        BasicQueueType(NetworkTraceQueueNext.basic(networkStateOperators::isInService, computeNextT.withType(actionType)), queue),
        null,
        actionType,
    )

    internal constructor(
        networkStateOperators: NetworkStateOperators,
        queueFactory: () -> TraversalQueue<NetworkTraceStep<T>>,
        branchQueueFactory: () -> TraversalQueue<NetworkTrace<T>>,
        actionType: NetworkTraceActionType,
        parent: NetworkTrace<T>?,
        computeNextT: ComputeNextT<T>,
    ) : this(
        networkStateOperators,
        BranchingQueueType(
            NetworkTraceQueueNext.branching(networkStateOperators::isInService, computeNextT.withType(actionType)),
            queueFactory,
            branchQueueFactory
        ),
        parent,
        actionType,
    )

    internal constructor(
        networkStateOperators: NetworkStateOperators,
        queueFactory: () -> TraversalQueue<NetworkTraceStep<T>>,
        branchQueueFactory: () -> TraversalQueue<NetworkTrace<T>>,
        actionType: NetworkTraceActionType,
        parent: NetworkTrace<T>?,
        computeNextT: ComputeNextTWithPaths<T>,
    ) : this(
        networkStateOperators,
        BranchingQueueType(
            NetworkTraceQueueNext.branching(networkStateOperators::isInService, computeNextT.withType(actionType)),
            queueFactory,
            branchQueueFactory
        ),
        parent,
        actionType,
    )

    /**
     * Adds a starting [Terminal] to the trace with the associated step data. Tracing will be only external from this terminal and not trace internally back
     * through its conducting equipment.
     *
     * @param start The starting terminal for the trace.
     * @param data The data associated with the start step.
     * @param phases Phases to trace; `null` to ignore phases.
     */
    fun addStartItem(start: Terminal, data: T, phases: PhaseCode? = null): NetworkTrace<T> {
        val startPath = StepPath(start, start, 0, 0, startNominalPhasePath(phases))
        addStartItem(NetworkTraceStep(startPath, data))
        return this
    }

    /**
     * Adds all terminals of the given [ConductingEquipment] as starting points in the trace, with the associated data.
     * Tracing will be only external from each terminal and not trace internally back through the conducting equipment.
     *
     * @param start The starting equipment whose terminals will be added to the trace.
     * @param data The data associated with each terminal start step.
     * @param phases Phases to trace; `null` to ignore phases.
     */
    fun addStartItem(start: ConductingEquipment, data: T, phases: PhaseCode? = null): NetworkTrace<T> {
        start.terminals.forEach { addStartItem(it, data, phases) }
        return this
    }

    /**
     * Runs the network trace starting adding the given [Terminal] to the start items.
     *
     * @param start The starting terminal for the trace.
     * @param data The data associated with the start step.
     * @param phases Phases to trace; `null` to ignore phases.
     * @param canStopOnStartItem Indicates whether the trace should check stop conditions on start items.
     */
    fun run(start: Terminal, data: T, phases: PhaseCode? = null, canStopOnStartItem: Boolean = true): NetworkTrace<T> {
        addStartItem(start, data, phases)
        run(canStopOnStartItem)
        return this
    }

    /**
     * Runs the network trace starting adding all terminals [Terminal]s of the start equipment to the start items.
     *
     * @param start The starting equipment whose terminals will be used as start items for the trace.
     * @param data The data associated with the start step.
     * @param phases Phases to trace; `null` to ignore phases.
     * @param canStopOnStartItem Indicates whether the trace should check stop conditions on start items.
     */
    fun run(start: ConductingEquipment, data: T, phases: PhaseCode? = null, canStopOnStartItem: Boolean = true): NetworkTrace<T> {
        addStartItem(start, data, phases)
        run(canStopOnStartItem)
        return this
    }

    // TODO [Review]: Should these just be called addCondition / addStepAction still because they don't conflict with the base ones? Or maybe addStateCondition would be better?

    /**
     * Adds a traversal condition to the trace using the trace's [NetworkStateOperators] as the receiver.
     *
     * This overload primarily exists to enable a DSL-like syntax for adding predefined traversal conditions to the trace.
     * For example, to configure the trace to stop at open points using the [Conditions.stopAtOpen] factory, you can use:
     *
     * ```kotlin
     * trace.addNetworkCondition { stopAtOpen() }
     * ```
     *
     * @param condition A lambda function that returns a traversal condition.
     * @return This [NetworkTrace] instance.
     */
    fun addNetworkCondition(condition: NetworkStateOperators.() -> TraversalCondition<NetworkTraceStep<T>>): NetworkTrace<T> {
        addCondition(networkStateOperators.condition())
        return this
    }

    /**
     * Adds a queue condition to the trace, as per [Traversal.addQueueCondition], however, the [NetworkTraceQueueCondition] variant also has the
     * [NetworkStateOperators] passed into it when requiring access to network state to fulfill the condition.
     *
     * @param condition The condition to determine whether to queue the next item.
     * @return This [NetworkTrace] instance.
     */
    fun addNetworkQueueCondition(condition: NetworkTraceQueueCondition<T>): NetworkTrace<T> {
        addQueueCondition { next, nextContext, current, currentContext ->
            condition.shouldQueue(next, nextContext, current, currentContext, networkStateOperators)
        }
        return this
    }

    /**
     * Adds a stop condition to the trace, as per [Traversal.addStopCondition], however, the [NetworkTraceStopCondition] variant also has the
     * [NetworkStateOperators] passed into it when requiring access to network state to fulfill the condition.
     *
     * @param condition The condition to determine whether to queue the next item.
     * @return This [NetworkTrace] instance.
     */
    fun addNetworkStopCondition(condition: NetworkTraceStopCondition<T>): NetworkTrace<T> {
        addStopCondition { item, context ->
            condition.shouldStop(item, context, networkStateOperators)
        }
        return this
    }

    /**
     * Adds a step condition to the trace, as per [Traversal.addStepAction], however, the [NetworkTraceStopCondition] variant also has the
     * [NetworkStateOperators] passed into it when requiring access to network state within the action.
     *
     * @param action The condition to determine whether to queue the next item.
     * @return This [NetworkTrace] instance.
     */
    fun addNetworkStepAction(action: NetworkTraceStepAction<T>): NetworkTrace<T> {
        addStepAction { item, ctx -> action.apply(item, ctx, networkStateOperators) }
        return this
    }

    override fun canActionItem(item: NetworkTraceStep<T>, context: StepContext): Boolean {
        return actionType.canActionItem(item, context, tracker::hasVisited)
    }

    override fun onReset() {
        tracker.clear()
    }

    override fun canVisitItem(item: NetworkTraceStep<T>, context: StepContext): Boolean {
        return tracker.visit(item.path.toTerminal, item.path.nominalPhasePaths.toPhasesSet())
    }

    override fun getDerivedThis(): NetworkTrace<T> = this
    override fun createNewThis(): NetworkTrace<T> = NetworkTrace(networkStateOperators, queueType, this, actionType)

    private fun startNominalPhasePath(phases: PhaseCode?): List<NominalPhasePath> =
        phases?.singlePhases?.map { NominalPhasePath(it, it) } ?: emptyList()

}

// TODO [Review]: Should computeNextT still be called for every step regardless on actionStepType because queueing / queue conditions are always
//                run for every step regardless of actionStepType?
private fun <T> ComputeNextT<T>.withType(actionType: NetworkTraceActionType): ComputeNextT<T> =
    when (actionType) {
        NetworkTraceActionType.ALL_STEPS -> this
        NetworkTraceActionType.FIRST_STEP_ON_EQUIPMENT -> ComputeNextT { currentStep, currentContext, nextPath ->
            // We just pass the data along on internal steps as a first step on equipment will always happen on an external step.
            if (nextPath.tracedInternally) currentStep.data
            else compute(currentStep, currentContext, nextPath)
        }
    }

private fun <T> ComputeNextTWithPaths<T>.withType(actionType: NetworkTraceActionType): ComputeNextTWithPaths<T> =
    when (actionType) {
        NetworkTraceActionType.ALL_STEPS -> this
        NetworkTraceActionType.FIRST_STEP_ON_EQUIPMENT -> ComputeNextTWithPaths { currentStep, currentContext, nextPath, nextPaths ->
            if (nextPath.tracedInternally) currentStep.data
            else compute(currentStep, currentContext, nextPath, nextPaths)
        }
    }

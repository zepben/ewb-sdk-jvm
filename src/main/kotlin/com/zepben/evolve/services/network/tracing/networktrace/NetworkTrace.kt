/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.networktrace

import com.zepben.evolve.annotations.ZepbenExperimental
import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.cim.iec61970.base.core.PhaseCode
import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.cim.iec61970.base.wires.AcLineSegment
import com.zepben.evolve.cim.iec61970.base.wires.BusbarSection
import com.zepben.evolve.cim.iec61970.base.wires.Clamp
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind
import com.zepben.evolve.services.network.tracing.connectivity.NominalPhasePath
import com.zepben.evolve.services.network.tracing.networktrace.conditions.Conditions
import com.zepben.evolve.services.network.tracing.networktrace.conditions.Conditions.stopAtOpen
import com.zepben.evolve.services.network.tracing.networktrace.conditions.NetworkTraceQueueCondition
import com.zepben.evolve.services.network.tracing.networktrace.conditions.NetworkTraceStopCondition
import com.zepben.evolve.services.network.tracing.networktrace.operators.NetworkStateOperators
import com.zepben.evolve.services.network.tracing.traversal.*
import org.slf4j.Logger

/**
 * A [Traversal] implementation specifically designed to trace connected [Terminal]s of [ConductingEquipment] in a network.
 *
 * This trace manages the complexity of network connectivity, especially in cases where connectivity is not straightforward,
 * such as with [BusbarSection]s and [Clamp]s. It checks the in service flag of equipment and only steps to equipment that is marked as in service.
 * It also provides the optional ability to trace only specific phases.
 *
 * Steps are represented by a [NetworkTraceStep], which contains a [NetworkTraceStep.Path] and allows associating arbitrary data with each step.
 * The arbitrary data for each step is computed via a [ComputeData] or [ComputeDataWithPaths] function provided at construction.
 * The trace invokes these functions when queueing each item and stores the result with the next step.
 *
 * When traversing, this trace will step on every connected terminal, as long as they match all the traversal conditions.
 * Each step is classified as either an external step or an internal step:
 *
 * - **External Step**: Moves from one terminal to another with different [Terminal.conductingEquipment].
 * - **Internal Step**: Moves between terminals within the same [Terminal.conductingEquipment].
 *
 * Often, you may want to act upon a [ConductingEquipment] only once, rather than multiple times for each internal and external terminal step.
 * To achieve this, set [actionType] to [NetworkTraceActionType.FIRST_STEP_ON_EQUIPMENT]. With this type, the trace will only call step actions and
 * conditions once for each [ConductingEquipment], regardless of how many terminals it has. However, queue conditions can be configured to be called
 * differently for each condition as continuing the trace can rely on different conditions based on an external or internal step. For example, not
 * queuing past open switches should happen on an internal step, thus if the trace is configured with FIRST_STEP_ON_EQUIPMENT, it will by default only
 * action the first external step to each equipment, and thus the provided [Conditions.stopAtOpen] condition overrides the default behaviour such that
 * it is called on all internal steps.
 *
 * The network trace is state-aware by requiring an instance of [NetworkStateOperators].
 * This allows traversal conditions and step actions to query and act upon state-based properties and functions of equipment in the network when required.
 *
 * 'Branching' traversals are also supported allowing tracing both ways around loops in the network. When using a branching instance, a new 'branch'
 * is created for each terminal when a step has two or more terminals it can step to. That is on an internal step, if the equipment has more than 2 terminals
 * and more than 2 terminals will be queued, a branch will be created for each terminal. On an external step, if 2 or more terminals are to be queued,
 * a branch will be created for each terminal.
 * If you do not need to trace loops both ways or have no loops, do not use a branching instance as it is less efficient than the non-branching one.
 *
 * To create instances of this class, use the factory methods provided in the [Tracing] object.
 *
 * @param T the type of [NetworkTraceStep.data]
 */
// Constructor is private as the other constructors deal with creating different instances for different use cases.
class NetworkTrace<T> private constructor(
    val networkStateOperators: NetworkStateOperators,
    queueType: QueueType<NetworkTraceStep<T>, NetworkTrace<T>>,
    parent: NetworkTrace<T>? = null,
    private val actionType: NetworkTraceActionType,
    debugLogger: Logger?,
    override val name: String
) : Traversal<NetworkTraceStep<T>, NetworkTrace<T>>(queueType, parent, debugLogger) {

    // Setting initial capacity greater than default of 16 for non-branching traces as we suspect a majority of network traces will step on more than 12
    // terminals (load factor is 0.75). Not sure what a sensible initial capacity actually is, but 16 just felt too small.
    private val tracker: NetworkTraceTracker = NetworkTraceTracker(
        when (queueType) {
            is BasicQueueType<*, *> -> 256
            is BranchingQueueType<*, *> -> 16
        }
    )

    // Non-branching instance that takes a normal compute data
    internal constructor(
        networkStateOperators: NetworkStateOperators,
        queue: TraversalQueue<NetworkTraceStep<T>>,
        actionType: NetworkTraceActionType,
        debugLogger: Logger?,
        name: String,
        computeData: ComputeData<T>,
    ) : this(
        networkStateOperators,
        BasicQueueType(NetworkTraceQueueNext.Basic(networkStateOperators, computeData.withActionType(actionType)), queue),
        null,
        actionType,
        debugLogger,
        name,
    )

    // Non-branching instance that takes a compute data requiring all next step paths.
    @ZepbenExperimental
    internal constructor(
        networkStateOperators: NetworkStateOperators,
        queue: TraversalQueue<NetworkTraceStep<T>>,
        actionType: NetworkTraceActionType,
        debugLogger: Logger?,
        name: String,
        computeNextT: ComputeDataWithPaths<T>,
    ) : this(
        networkStateOperators,
        BasicQueueType(NetworkTraceQueueNext.Basic(networkStateOperators, computeNextT.withActionType(actionType)), queue),
        null,
        actionType,
        debugLogger,
        name,
    )

    // Branching instance that takes a regular compute data.
    internal constructor(
        networkStateOperators: NetworkStateOperators,
        queueFactory: () -> TraversalQueue<NetworkTraceStep<T>>,
        branchQueueFactory: () -> TraversalQueue<NetworkTrace<T>>,
        actionType: NetworkTraceActionType,
        debugLogger: Logger?,
        name: String,
        parent: NetworkTrace<T>?,
        computeData: ComputeData<T>,
    ) : this(
        networkStateOperators,
        BranchingQueueType(
            NetworkTraceQueueNext.Branching(networkStateOperators, computeData.withActionType(actionType)),
            queueFactory,
            branchQueueFactory
        ),
        parent,
        actionType,
        debugLogger,
        name,
    )

    // Branching instance that takes a compute data requiring all next step paths.
    @ZepbenExperimental
    internal constructor(
        networkStateOperators: NetworkStateOperators,
        queueFactory: () -> TraversalQueue<NetworkTraceStep<T>>,
        branchQueueFactory: () -> TraversalQueue<NetworkTrace<T>>,
        actionType: NetworkTraceActionType,
        debugLogger: Logger?,
        name: String,
        parent: NetworkTrace<T>?,
        computeNextT: ComputeDataWithPaths<T>,
    ) : this(
        networkStateOperators,
        BranchingQueueType(
            NetworkTraceQueueNext.Branching(networkStateOperators, computeNextT.withActionType(actionType)),
            queueFactory,
            branchQueueFactory
        ),
        parent,
        actionType,
        debugLogger,
        name,
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
        // We have a special case when starting specifically on a clamp terminal that we mark it as having traversed the segment such that it
        // will only trace externally from the clamp terminal. This behaves differently to when the whole Clamp is added as a start item.
        val traversedAcLineSegment = when (val ce = start.conductingEquipment) {
            is Clamp -> ce.acLineSegment
            else -> null
        }

        addStartItem(start, data, phases, traversedAcLineSegment)
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
        when (start) {
            // If we start on an AcLineSegment, we queue the segments terminals, and all its Cut and Clamp terminals as if we have traversed the segment,
            // so the next steps will be external from all the terminals "belonging" to the segment.
            is AcLineSegment -> {
                val startTerminals = start.terminals + start.clamps.mapNotNull { it.terminals.firstOrNull() } + start.cuts.flatMap { it.terminals }
                startTerminals.forEach { addStartItem(it, data, phases, start) }
            }

            // We don't have a special case for Clamp here because we say if you start from the whole Clamp rather than its terminal specifically,
            // we want to trace externally from it and traverse its segment.
            else -> {
                start.terminals.forEach { addStartItem(it, data, phases, null) }
            }
        }

        return this
    }

    /**
     * Adds the given [NetworkTraceStep.Path] as starting points in the trace, with the associated data.
     * Tracing will continue from the path externally or internally depending on the path provided.
     *
     * NOTE: The given path will be stepped on, but that may mean the `fromTerminal` is skipped from your trace, so
     *       take care to handle this use case.
     *
     * @param startPath The path to start the trace from.
     * @param data The data associated with each terminal start step.
     */
    fun addStartItem(startPath: NetworkTraceStep.Path, data: T) {
        addStartItem(NetworkTraceStep(startPath, 0, 0, data))
    }

    private fun addStartItem(start: Terminal, data: T, phases: PhaseCode? = null, traversedAcLineSegment: AcLineSegment?) {
        addStartItem(NetworkTraceStep.Path(start, start, traversedAcLineSegment, startNominalPhasePath(phases)), data)
    }

    /**
     * Runs the network trace adding the given [Terminal] to the start items.
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
     * Runs the network trace adding all terminals [Terminal]s of the start equipment to the start items.
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

    /**
     * Runs the network trace adding the [startPath] to the start items.
     *
     * @param startPath The path to start the trace from.
     * @param data The data associated with the start step.
     * @param canStopOnStartItem Indicates whether the trace should check stop conditions on start items.
     */
    fun run(startPath: NetworkTraceStep.Path, data: T, canStopOnStartItem: Boolean = true): NetworkTrace<T> {
        addStartItem(startPath, data)
        run(canStopOnStartItem)
        return this
    }

    /**
     * Adds a traversal condition to the trace using the trace's [NetworkStateOperators] as the receiver.
     *
     * This overload primarily exists to enable a DSL-like syntax for adding predefined traversal conditions to the trace.
     * For example, to configure the trace to stop at open points using the [Conditions.stopAtOpen] factory, you can use:
     *
     * ```kotlin
     * trace.addCondition { stopAtOpen() }
     * ```
     *
     * @param condition A lambda function that returns a traversal condition.
     * @return This [NetworkTrace] instance.
     */
    fun addCondition(condition: NetworkStateOperators.() -> TraversalCondition<NetworkTraceStep<T>>): NetworkTrace<T> {
        addCondition(networkStateOperators.condition())
        return this
    }

    /**
     * Adds a [QueueCondition] to the traversal. However, before registering it with the traversal, it will make sure that the queue condition
     * is only checked on step types relevant to the [NetworkTraceActionType] assigned to this instance. That is when:
     *
     * - [actionType] is [NetworkTraceActionType.ALL_STEPS] the condition will be checked on all steps.
     * - [actionType] is [NetworkTraceActionType.FIRST_STEP_ON_EQUIPMENT] the condition will be checked on external steps.
     *
     * However, if the [condition] is an instance of [NetworkTraceQueueCondition] the [NetworkTraceQueueCondition.stepType] will be honoured.
     *
     * @param condition The queue condition to add.
     * @return The current traversal instance.
     */
    override fun addQueueCondition(condition: QueueCondition<NetworkTraceStep<T>>): NetworkTrace<T> {
        return super.addQueueCondition(condition.toNetworkTraceQueueCondition(actionType.defaultConditionStepType(), false))
    }

    /**
     * Adds a [QueueCondition] to the traversal that will only check steps that match the given [stepType].
     *
     * If the [condition] is a [NetworkTraceQueueCondition] the [NetworkTraceQueueCondition.stepType] will be ignored and the type passed into
     * this function will be used instead.
     *
     * @param condition The queue condition to add.
     * @param stepType The step type where the queue condition is checked.
     * @return The current traversal instance.
     */
    fun addQueueCondition(stepType: NetworkTraceStep.Type, condition: QueueCondition<NetworkTraceStep<T>>): NetworkTrace<T> {
        return addQueueCondition(condition.toNetworkTraceQueueCondition(stepType, true))
    }

    /**
     * Adds a [StopCondition] to the traversal. However, before registering it with the traversal, it will make sure that the stop condition
     * is only checked on step types relevant to the [NetworkTraceActionType] assigned to this instance. That is when:
     *
     * - [actionType] is [NetworkTraceActionType.ALL_STEPS] the condition will be checked on all steps.
     * - [actionType] is [NetworkTraceActionType.FIRST_STEP_ON_EQUIPMENT] the condition will be checked on external steps.
     *
     * However, if the [condition] is an instance of [NetworkTraceStopCondition] the [NetworkTraceStopCondition.stepType] will be honoured.
     *
     * @param condition The stop condition to add.
     * @return The current traversal instance.
     */
    override fun addStopCondition(condition: StopCondition<NetworkTraceStep<T>>): NetworkTrace<T> {
        return super.addStopCondition(condition.toNetworkTraceStopCondition(actionType.defaultConditionStepType(), false))
    }

    /**
     * Adds a [StopCondition] to the traversal that will only check steps that match the given [stepType].
     *
     * If the [condition] is a [NetworkTraceStopCondition] the [NetworkTraceStopCondition.stepType] will be ignored and the type passed into
     * this function will be used instead.
     *
     * @param condition The stop condition to add.
     * @param stepType The step type where the stop condition is checked.
     * @return The current traversal instance.
     */
    fun addStopCondition(stepType: NetworkTraceStep.Type, condition: StopCondition<NetworkTraceStep<T>>): NetworkTrace<T> {
        return addStopCondition(condition.toNetworkTraceStopCondition(stepType, true))
    }

    override fun canActionItem(item: NetworkTraceStep<T>, context: StepContext): Boolean {
        return actionType.canActionItem(item, context, ::hasVisited)
    }

    override fun onReset() {
        tracker.clear()
    }

    override fun canVisitItem(item: NetworkTraceStep<T>, context: StepContext): Boolean {
        return visit(item.path.toTerminal, item.path.nominalPhasePaths.toPhasesSet())
    }

    override fun getDerivedThis(): NetworkTrace<T> = this
    override fun createNewThis(): NetworkTrace<T> = NetworkTrace(networkStateOperators, queueType, this, actionType, debugLogger = null, name)

    private fun startNominalPhasePath(phases: PhaseCode?): List<NominalPhasePath> =
        phases?.singlePhases?.map { NominalPhasePath(it, it) } ?: emptyList()

    private fun hasVisited(terminal: Terminal, phases: Set<SinglePhaseKind>): Boolean {
        // We could do this with the following code:
        //    parent?.hasVisited(terminal, phases) == true || tracker.hasVisited(terminal, phases)
        //
        // Although this reads nicer, it risks causing stack overflows.

        var parent = parent
        while (parent != null) {
            // NOTE: Make sure you call the parents tracker, not the parent directly to avoid exponential blowouts with long chain traces.
            if (parent.tracker.hasVisited(terminal, phases))
                return true
            parent = parent.parent
        }

        return tracker.hasVisited(terminal, phases)
    }

    private fun visit(terminal: Terminal, phases: Set<SinglePhaseKind>): Boolean {
        // We could do this with the following code:
        //    (parent?.hasVisited(terminal, phases) != true) && tracker.visit(terminal, phases)
        //
        // Although this reads nicer, it risks causing stack overflows.
        var parent = parent
        while (parent != null) {
            // NOTE: Make sure you call the parents tracker, not the parent directly to avoid exponential blowouts with long chain traces.
            if (parent.tracker.hasVisited(terminal, phases))
                return false
            parent = parent.parent
        }

        return tracker.visit(terminal, phases)
    }

    private fun QueueCondition<NetworkTraceStep<T>>.toNetworkTraceQueueCondition(stepType: NetworkTraceStep.Type, overrideStepType: Boolean) =
        when {
            this is NetworkTraceQueueCondition<T> && !overrideStepType -> this
            else -> NetworkTraceQueueCondition.delegateTo(stepType, this@toNetworkTraceQueueCondition)
        }

    private fun StopCondition<NetworkTraceStep<T>>.toNetworkTraceStopCondition(stepType: NetworkTraceStep.Type, overrideStepType: Boolean) =
        when {
            this is NetworkTraceStopCondition<T> && !overrideStepType -> this
            else -> NetworkTraceStopCondition.delegateTo(stepType, this@toNetworkTraceStopCondition)
        }

    private fun NetworkTraceActionType.defaultConditionStepType() = when (this) {
        NetworkTraceActionType.ALL_STEPS -> NetworkTraceStep.Type.ALL
        NetworkTraceActionType.FIRST_STEP_ON_EQUIPMENT -> NetworkTraceStep.Type.EXTERNAL
    }

}

private fun <T> ComputeData<T>.withActionType(actionType: NetworkTraceActionType): ComputeData<T> =
    when (actionType) {
        NetworkTraceActionType.ALL_STEPS -> this
        NetworkTraceActionType.FIRST_STEP_ON_EQUIPMENT -> ComputeData { currentStep, currentContext, nextPath ->
            // We just pass the data along on internal steps as a first step on equipment will always happen on an external step.
            if (nextPath.tracedInternally) currentStep.data
            else computeNext(currentStep, currentContext, nextPath)
        }
    }

@ZepbenExperimental
private fun <T> ComputeDataWithPaths<T>.withActionType(actionType: NetworkTraceActionType): ComputeDataWithPaths<T> =
    when (actionType) {
        NetworkTraceActionType.ALL_STEPS -> this
        NetworkTraceActionType.FIRST_STEP_ON_EQUIPMENT -> ComputeDataWithPaths { currentStep, currentContext, nextPath, nextPaths ->
            // We just pass the data along on internal steps as a first step on equipment will always happen on an external step.
            if (nextPath.tracedInternally) currentStep.data
            else computeNext(currentStep, currentContext, nextPath, nextPaths)
        }
    }

/*
 * Copyright 2023 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.networktrace

import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.cim.iec61970.base.core.PhaseCode
import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.services.network.tracing.connectivity.NominalPhasePath
import com.zepben.evolve.services.network.tracing.networktrace.operators.NetworkStateOperators
import com.zepben.evolve.services.network.tracing.traversal.*

class NetworkTrace<T> private constructor(
    val networkStateOperators: NetworkStateOperators,
    queueType: QueueType<NetworkTraceStep<T>, NetworkTrace<T>>,
    parent: NetworkTrace<T>? = null,
    private val onlyActionEquipment: Boolean,
) : Traversal<NetworkTraceStep<T>, NetworkTrace<T>>(queueType, { NetworkTraceTracker.terminalTracker() }, parent) {

    private val equipmentTracker: RecursiveTracker<NetworkTraceStep<T>>? =
        if (onlyActionEquipment) RecursiveTracker(parent?.equipmentTracker, NetworkTraceTracker.equipmentTracker()) else null

    internal constructor(
        networkStateOperators: NetworkStateOperators,
        queue: TraversalQueue<NetworkTraceStep<T>>,
        onlyActionEquipment: Boolean,
        computeNextT: ComputeNextT<T>,
    ) : this(
        networkStateOperators,
        BasicQueueType(NetworkTraceQueueNext.basic(networkStateOperators::isInService, computeNextT.wrapped(onlyActionEquipment)), queue),
        null,
        onlyActionEquipment
    )

    internal constructor(
        networkStateOperators: NetworkStateOperators,
        queue: TraversalQueue<NetworkTraceStep<T>>,
        onlyActionEquipment: Boolean,
        computeNextT: ComputeNextTWithPaths<T>,
    ) : this(
        networkStateOperators,
        BasicQueueType(NetworkTraceQueueNext.basic(networkStateOperators::isInService, computeNextT.wrapped(onlyActionEquipment)), queue),
        null,
        onlyActionEquipment
    )

    internal constructor(
        networkStateOperators: NetworkStateOperators,
        queueFactory: () -> TraversalQueue<NetworkTraceStep<T>>,
        branchQueueFactory: () -> TraversalQueue<NetworkTrace<T>>,
        onlyActionEquipment: Boolean,
        parent: NetworkTrace<T>?,
        computeNextT: ComputeNextT<T>,
    ) : this(
        networkStateOperators,
        BranchingQueueType(
            NetworkTraceQueueNext.branching(networkStateOperators::isInService, computeNextT.wrapped(onlyActionEquipment)),
            queueFactory,
            branchQueueFactory
        ),
        parent,
        onlyActionEquipment
    )

    internal constructor(
        networkStateOperators: NetworkStateOperators,
        queueFactory: () -> TraversalQueue<NetworkTraceStep<T>>,
        branchQueueFactory: () -> TraversalQueue<NetworkTrace<T>>,
        onlyActionEquipment: Boolean,
        parent: NetworkTrace<T>?,
        computeNextT: ComputeNextTWithPaths<T>,
    ) : this(
        networkStateOperators,
        BranchingQueueType(
            NetworkTraceQueueNext.branching(networkStateOperators::isInService, computeNextT.wrapped(onlyActionEquipment)),
            queueFactory,
            branchQueueFactory
        ),
        parent,
        onlyActionEquipment
    )

    fun addStartItem(start: Terminal, context: T, phases: PhaseCode? = null) {
        val startPath = StepPath(start, start, 0, 0, startNominalPhasePath(phases))
        addStartItem(NetworkTraceStep(startPath, context))
    }

    fun addStartItem(start: ConductingEquipment, context: T, phases: PhaseCode? = null) {
        start.terminals.forEach { addStartItem(it, context, phases) }
    }

    fun run(start: Terminal, context: T, phases: PhaseCode? = null, canStopOnStartItem: Boolean = true) {
        addStartItem(start, context, phases)
        run(canStopOnStartItem)
    }

    fun run(start: ConductingEquipment, context: T, phases: PhaseCode? = null, canStopOnStartItem: Boolean = true) {
        addStartItem(start, context, phases)
        run(canStopOnStartItem)
    }

    // TODO [Review]: Should these just be called addCondition / addStepAction still because they don't conflict with the base ones?

    fun addNetworkCondition(block: NetworkStateOperators.() -> TraversalCondition<NetworkTraceStep<T>>): NetworkTrace<T> {
        addCondition(networkStateOperators.block())
        return this
    }

    fun addNetworkQueueCondition(condition: NetworkTraceQueueCondition<T>): NetworkTrace<T> {
        addQueueCondition { next, nextContext, current, currentContext ->
            condition.shouldQueue(next, nextContext, current, currentContext, networkStateOperators)
        }
        return this
    }

    fun addNetworkStopCondition(condition: NetworkTraceStopCondition<T>): NetworkTrace<T> {
        addStopCondition { item, context ->
            condition.shouldStop(item, context, networkStateOperators)
        }
        return this
    }

    fun addNetworkStepAction(action: NetworkTraceStepAction<T>): NetworkTrace<T> {
        addStepAction { item, ctx -> action.apply(item, ctx, networkStateOperators) }
        return this
    }

    override fun canActionItem(item: NetworkTraceStep<T>, context: StepContext): Boolean {
        return context.isStartItem or (equipmentTracker?.visit(item) ?: super.canActionItem(item, context))
    }

    override fun onReset() {
        equipmentTracker?.clear()
    }

    override fun getDerivedThis(): NetworkTrace<T> = this
    override fun createNewThis(): NetworkTrace<T> = NetworkTrace(networkStateOperators, queueType, this, onlyActionEquipment)

    private fun startNominalPhasePath(phases: PhaseCode?): List<NominalPhasePath> =
        phases?.singlePhases?.map { NominalPhasePath(it, it) } ?: emptyList()

}

fun NetworkTrace<Unit>.addStartItem(start: Terminal, phases: PhaseCode? = null) {
    addStartItem(start, Unit, phases)
}

fun NetworkTrace<Unit>.addStartItem(start: ConductingEquipment, phases: PhaseCode? = null) {
    start.terminals.forEach { addStartItem(it, Unit, phases) }
}

fun NetworkTrace<Unit>.run(start: Terminal, phases: PhaseCode? = null, canStopOnStartItem: Boolean = true) {
    this.run(start, Unit, phases, canStopOnStartItem)
}

fun NetworkTrace<Unit>.run(start: ConductingEquipment, phases: PhaseCode? = null, canStopOnStartItem: Boolean = true) {
    this.run(start, Unit, phases, canStopOnStartItem)
}

private fun <T> ComputeNextT<T>.wrapped(onlyActionEquipment: Boolean): ComputeNextT<T> = if (onlyActionEquipment) {
    ComputeNextT { currentStep: NetworkTraceStep<T>, currentContext: StepContext, nextPath: StepPath ->
        if (nextPath.tracedInternally)
            currentStep.data
        else
            this.compute(currentStep, currentContext, nextPath)
    }
} else {
    this
}

private fun <T> ComputeNextTWithPaths<T>.wrapped(onlyActionEquipment: Boolean): ComputeNextTWithPaths<T> = if (onlyActionEquipment) {
    ComputeNextTWithPaths { currentStep: NetworkTraceStep<T>, currentContext: StepContext, nextPath: StepPath, nextPaths: List<StepPath> ->
        if (nextPath.tracedInternally)
            currentStep.data
        else
            this.compute(currentStep, currentContext, nextPath, nextPaths)
    }
} else {
    this
}

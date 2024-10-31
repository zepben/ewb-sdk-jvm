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

    fun run(start: Terminal, context: T, phases: PhaseCode? = null, canStopOnStartItem: Boolean = true) {
        val startPath = StepPath(start, start, 0, 0, startNominalPhasePath(phases))
        run(NetworkTraceStep(startPath, context), canStopOnStartItem)
    }

    fun run(start: ConductingEquipment, context: T, phases: PhaseCode? = null, canStopOnStartItem: Boolean = true) {
        start.terminals.forEach { terminal ->
            val startPath = StepPath(terminal, terminal, 0, 0, startNominalPhasePath(phases))
            addStartItem(NetworkTraceStep(startPath, context))
        }

        run(canStopOnStartItem)
    }

    // TODO [Review]: Should this just be addCondition?
    fun addNetworkCondition(block: NetworkStateOperators.() -> TraversalCondition<NetworkTraceStep<T>>): NetworkTrace<T> {
        addCondition(networkStateOperators.block())
        return this
    }

    fun addNetworkStepAction(action: (NetworkTraceStep<T>, StepContext, NetworkStateOperators) -> Unit): NetworkTrace<T> {
        addStepAction { item, ctx -> action(item, ctx, networkStateOperators) }
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

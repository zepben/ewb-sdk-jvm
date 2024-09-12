/*
 * Copyright 2023 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.networktrace

import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.services.network.tracing.traversalV2.RecursiveTracker
import com.zepben.evolve.services.network.tracing.traversalV2.StepContext
import com.zepben.evolve.services.network.tracing.traversalV2.Traversal
import com.zepben.evolve.services.network.tracing.traversals.TraversalQueue

class NetworkTrace<T> private constructor(
    queueType: QueueType<NetworkTraceStep<T>, NetworkTrace<T>>,
    parent: NetworkTrace<T>? = null,
    private val onlyActionEquipment: Boolean
) : Traversal<NetworkTraceStep<T>, NetworkTrace<T>>(queueType, { NetworkTraceTracker.terminalTracker() }, parent) {

    private val equipmentTracker: RecursiveTracker<NetworkTraceStep<T>>? =
        if (onlyActionEquipment) RecursiveTracker(parent?.equipmentTracker, NetworkTraceTracker.equipmentTracker()) else null

    internal constructor(
        queue: TraversalQueue<NetworkTraceStep<T>>,
        onlyActionEquipment: Boolean,
        computeNextT: ComputeNextT<T>,
    ) : this(BasicQueueType(NetworkTraceQueueNext.basic(computeNextT.wrapped(onlyActionEquipment)), queue), null, onlyActionEquipment)

    internal constructor(
        queue: TraversalQueue<NetworkTraceStep<T>>,
        onlyActionEquipment: Boolean,
        computeNextT: ComputeNextTWithPaths<T>,
    ) : this(BasicQueueType(NetworkTraceQueueNext.basic(computeNextT.wrapped(onlyActionEquipment)), queue), null, onlyActionEquipment)

    internal constructor(
        queueFactory: () -> TraversalQueue<NetworkTraceStep<T>>,
        branchQueueFactory: () -> TraversalQueue<NetworkTrace<T>>,
        onlyActionEquipment: Boolean,
        parent: NetworkTrace<T>?,
        computeNextT: ComputeNextT<T>,
    ) : this(
        BranchingQueueType(NetworkTraceQueueNext.branching(computeNextT.wrapped(onlyActionEquipment)), queueFactory, branchQueueFactory),
        parent,
        onlyActionEquipment
    )

    internal constructor(
        queueFactory: () -> TraversalQueue<NetworkTraceStep<T>>,
        branchQueueFactory: () -> TraversalQueue<NetworkTrace<T>>,
        onlyActionEquipment: Boolean,
        parent: NetworkTrace<T>?,
        computeNextT: ComputeNextTWithPaths<T>,
    ) : this(
        BranchingQueueType(NetworkTraceQueueNext.branching(computeNextT.wrapped(onlyActionEquipment)), queueFactory, branchQueueFactory),
        parent,
        onlyActionEquipment
    )

    fun run(start: Terminal, context: T, canStopOnStartItem: Boolean = true) {
        addStartItem(NetworkTraceStep(TerminalToTerminalPath(start, start, 0, 0), context))
        run(canStopOnStartItem)
    }

    fun run(start: ConductingEquipment, context: T, canStopOnStartItem: Boolean = true) {
        start.terminals.forEach {
            addStartItem(NetworkTraceStep(TerminalToTerminalPath(it, it, 0, 0), context))
        }
        run(canStopOnStartItem)
    }

    override fun canActionItem(item: NetworkTraceStep<T>, context: StepContext): Boolean {
        return context.isStartItem or (equipmentTracker?.visit(item) ?: super.canActionItem(item, context))
    }

    override fun onReset() {
        equipmentTracker?.clear()
    }

    override fun getDerivedThis(): NetworkTrace<T> = this
    override fun createNewThis(): NetworkTrace<T> = NetworkTrace(queueType, this, onlyActionEquipment)

}

fun NetworkTrace<Unit>.run(start: Terminal, canStopOnStartItem: Boolean = true) {
    this.run(start, Unit, canStopOnStartItem)
}

fun NetworkTrace<Unit>.run(start: ConductingEquipment, canStopOnStartItem: Boolean = true) {
    this.run(start, Unit, canStopOnStartItem)
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

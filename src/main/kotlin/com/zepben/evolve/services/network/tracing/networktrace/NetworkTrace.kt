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
import com.zepben.evolve.services.network.tracing.traversalV2.Traversal
import com.zepben.evolve.services.network.tracing.traversals.Tracker
import com.zepben.evolve.services.network.tracing.traversals.TraversalQueue

class NetworkTrace<T> private constructor(
    queueType: QueueType<NetworkTraceStep<T>, NetworkTrace<T>>,
    trackerFactory: () -> Tracker<NetworkTraceStep<T>>,
    parent: NetworkTrace<T>?
) : Traversal<NetworkTraceStep<T>, NetworkTrace<T>>(queueType, trackerFactory, parent) {

    internal constructor(
        queue: TraversalQueue<NetworkTraceStep<T>>,
        tracker: Tracker<NetworkTraceStep<T>>,
        queueNext: QueueNext<NetworkTraceStep<T>>,
    ) : this(BasicQueueType(queueNext, queue), { tracker }, null)

    internal constructor(
        queueFactory: () -> TraversalQueue<NetworkTraceStep<T>>,
        branchQueueFactory: () -> TraversalQueue<NetworkTrace<T>>,
        trackerFactory: () -> Tracker<NetworkTraceStep<T>>,
        branchingQueueNext: BranchingQueueNext<NetworkTraceStep<T>>,
    ) : this(BranchingQueueType(branchingQueueNext, queueFactory, branchQueueFactory), trackerFactory, null)

    fun run(start: Terminal, canStopOnStartItem: Boolean = true, context: T) {
        addStartItem(NetworkTraceStep(TerminalToTerminalPath(start, start, 0, 0), context))
        run(canStopOnStartItem)
    }

    fun run(start: ConductingEquipment, canStopOnStartItem: Boolean = true, context: T) {
        start.terminals.forEach {
            addStartItem(NetworkTraceStep(TerminalToTerminalPath(it, it, 0, 0), context))
        }
        run(canStopOnStartItem)
    }

    override fun getDerivedThis(): NetworkTrace<T> = this

    override fun createNewThis(): NetworkTrace<T> {
        return NetworkTrace(queueType, trackerFactory, this)
    }
}

fun NetworkTrace<Unit>.run(start: Terminal, canStopOnStartItem: Boolean = true) {
    this.run(start, canStopOnStartItem, Unit)
}

fun NetworkTrace<Unit>.run(start: ConductingEquipment, canStopOnStartItem: Boolean = true) {
    this.run(start, canStopOnStartItem, Unit)
}
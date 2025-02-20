/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.networktrace.conditions

import com.zepben.evolve.cim.iec61970.base.wires.Cut
import com.zepben.evolve.services.network.tracing.feeder.FeederDirection
import com.zepben.evolve.services.network.tracing.feeder.FeederDirection.*
import com.zepben.evolve.services.network.tracing.networktrace.NetworkTraceStep
import com.zepben.evolve.services.network.tracing.networktrace.operators.NetworkStateOperators
import com.zepben.evolve.services.network.tracing.traversal.QueueCondition
import com.zepben.evolve.services.network.tracing.traversal.StepContext

internal class DirectionCondition<T>(
    val direction: FeederDirection,
    val stateOperators: NetworkStateOperators
) : QueueCondition<NetworkTraceStep<T>> {

    private val getDirection = stateOperators::getDirection

    init {
        require(direction != CONNECTOR) { "A direction of CONNECTOR is not currently supported." }
    }

    override fun shouldQueue(nextItem: NetworkTraceStep<T>, nextContext: StepContext, currentItem: NetworkTraceStep<T>, currentContext: StepContext): Boolean =
        shouldQueue(nextItem.path)

    private fun shouldQueue(path: NetworkTraceStep.Path): Boolean {
        // Cuts do weird things with directions depending on if they are energised from an external connection, or through a "closed" cut. To prevent
        // dealing with this awful mess, it is much simpler to just ask if anything else past it needs queueing. This could be made to short-circuit
        // for traversing downstream, but the code is much more complex to only save one extra step.
        return if (path.toEquipment is Cut) {
            usableNextPaths(path).any { furtherPath -> shouldQueue(furtherPath) }
        } else if (path.tracedInternally || path.didTraverseAcLineSegment) {
            direction in getDirection(path.toTerminal)
        } else {
            direction.complementaryExternalDirection() in getDirection(path.toTerminal)
        }
    }

    private fun usableNextPaths(path: NetworkTraceStep.Path) =
        stateOperators.nextPaths(path).filterNot { nextPath -> nextPath.tracedInternally && stateOperators.isOpen(path.toEquipment) }

    override fun shouldQueueStartItem(item: NetworkTraceStep<T>): Boolean =
        direction in getDirection(item.path.toTerminal)

    private fun FeederDirection.complementaryExternalDirection(): FeederDirection = when (this) {
        NONE -> NONE
        UPSTREAM -> DOWNSTREAM
        DOWNSTREAM -> UPSTREAM
        BOTH -> BOTH
        CONNECTOR -> CONNECTOR
    }

}

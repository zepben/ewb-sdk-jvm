/*
 * Copyright 2023 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.networktrace.conditions

import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.services.network.tracing.feeder.FeederDirection
import com.zepben.evolve.services.network.tracing.feeder.FeederDirection.*
import com.zepben.evolve.services.network.tracing.networktrace.NetworkTraceStep
import com.zepben.evolve.services.network.tracing.traversal.QueueCondition
import com.zepben.evolve.services.network.tracing.traversal.StepContext

internal class DirectionCondition<T>(
    private val direction: FeederDirection,
    private val getDirection: (Terminal) -> FeederDirection
) : QueueCondition<NetworkTraceStep<T>> {

    override fun shouldQueue(nextItem: NetworkTraceStep<T>, nextContext: StepContext, currentItem: NetworkTraceStep<T>, currentContext: StepContext): Boolean {
        val path = nextItem.path
        return if (path.tracedInternally) {
            direction in getDirection(path.toTerminal)
        } else {
            direction.complementaryExternalDirection() in getDirection(path.toTerminal)
        }
    }

    override fun shouldQueueStartItem(item: NetworkTraceStep<T>): Boolean =
        direction in getDirection(item.path.toTerminal)

    private fun FeederDirection.complementaryExternalDirection(): FeederDirection = when (this) {
        NONE -> NONE
        BOTH -> BOTH
        UPSTREAM -> DOWNSTREAM
        DOWNSTREAM -> UPSTREAM
    }

}

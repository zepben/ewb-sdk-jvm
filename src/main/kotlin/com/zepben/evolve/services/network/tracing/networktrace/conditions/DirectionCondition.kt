/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.networktrace.conditions

import com.zepben.evolve.cim.iec61970.base.wires.Clamp
import com.zepben.evolve.cim.iec61970.base.wires.Cut
import com.zepben.evolve.services.network.tracing.feeder.FeederDirection
import com.zepben.evolve.services.network.tracing.feeder.FeederDirection.*
import com.zepben.evolve.services.network.tracing.networktrace.NetworkTraceStep
import com.zepben.evolve.services.network.tracing.networktrace.NetworkTraceStepPathProvider
import com.zepben.evolve.services.network.tracing.networktrace.operators.NetworkStateOperators
import com.zepben.evolve.services.network.tracing.traversal.QueueCondition
import com.zepben.evolve.services.network.tracing.traversal.StepContext

internal class DirectionCondition<T>(
    val direction: FeederDirection,
    val stateOperators: NetworkStateOperators
) : QueueCondition<NetworkTraceStep<T>> {

    private val getDirection = stateOperators::getDirection
    private val networkTraceStepPathProvider = NetworkTraceStepPathProvider(stateOperators)

    override fun shouldQueue(nextItem: NetworkTraceStep<T>, nextContext: StepContext, currentItem: NetworkTraceStep<T>, currentContext: StepContext): Boolean {
        val path = nextItem.path

        val test = if (path.fromEquipment is Clamp) {
            if (direction == UPSTREAM) {
                if (path.didTraverseAcLineSegment) {
                    if (path.toEquipment is Cut) {
                        direction in getDirection(path.toTerminal) &&
                            path.toTerminal.connectedTerminals()
                                .any { ct -> direction.complementaryExternalDirection() in getDirection(ct) }
                    } else
                        direction in getDirection(path.toTerminal)
                } else {
                    direction.complementaryExternalDirection() in getDirection(path.toTerminal)
                }
            } else if (direction == DOWNSTREAM) {
                if (path.didTraverseAcLineSegment) {
                    if (path.toEquipment is Cut) {
                        direction.complementaryExternalDirection() in getDirection(path.toTerminal)
                    } else
                        direction in getDirection(path.toTerminal)
                } else {
                    direction.complementaryExternalDirection() in getDirection(path.toTerminal)
                }
            } else if (direction == BOTH) {
                if (path.toEquipment is Cut) {
                    if (path.didTraverseAcLineSegment)
                        path.toTerminal.connectedTerminals().any { ct -> direction.complementaryExternalDirection() in getDirection(ct) }
                    else {
                        networkTraceStepPathProvider.nextPaths(path).any { furtherPath ->
                            direction.complementaryExternalDirection() in getDirection(furtherPath.toTerminal)
                        }
                    }
                } else
                    direction in getDirection(path.toTerminal)
            } else {
                direction in getDirection(path.toTerminal)
            }
        } else if (path.toEquipment is Clamp) {
        } else if (path.toEquipment is Cut) {
            if (direction == UPSTREAM) {
                if (path.didTraverseAcLineSegment)
                    direction in getDirection(path.toTerminal) && path.toTerminal.connectedTerminals()
                        .any { ct -> direction.complementaryExternalDirection() in getDirection(ct) }
                else
                    direction in getDirection(path.toTerminal)
            }
        }

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
        UPSTREAM -> DOWNSTREAM
        DOWNSTREAM -> UPSTREAM
        BOTH -> BOTH
        CONNECTOR -> CONNECTOR
    }

}

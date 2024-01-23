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
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind
import com.zepben.evolve.services.network.tracing.OpenTest
import com.zepben.evolve.services.network.tracing.feeder.FeederDirection
import com.zepben.evolve.services.network.tracing.networktrace.conditions.*
import com.zepben.evolve.services.network.tracing.traversalV2.Traversal
import com.zepben.evolve.services.network.tracing.traversals.Tracker
import com.zepben.evolve.services.network.tracing.traversals.TraversalQueue

class NetworkTrace<T>(
    queueNext: QueueNext<T>,
    queue: TraversalQueue<NetworkTraceStep<T>>,
    tracker: Tracker<NetworkTraceStep<T>>,
) : Traversal<NetworkTraceStep<T>, NetworkTrace<T>>(queueNext, queue, tracker) {

    fun interface QueueNext<T> : Traversal.QueueNext<NetworkTraceStep<T>, NetworkTrace<T>>

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

    fun normallyUpstream(): NetworkTrace<T> {
        addQueueCondition(DirectionCondition(FeederDirection.UPSTREAM, Terminal::normalFeederDirection))
        return this
    }

    fun currentlyUpstream(): NetworkTrace<T> {
        addQueueCondition(DirectionCondition(FeederDirection.UPSTREAM, Terminal::currentFeederDirection))
        return this
    }

    fun normallyDownstream(): NetworkTrace<T> {
        addQueueCondition(DirectionCondition(FeederDirection.DOWNSTREAM, Terminal::normalFeederDirection))
        return this
    }

    fun currentlyDownstream(): NetworkTrace<T> {
        addQueueCondition(DirectionCondition(FeederDirection.DOWNSTREAM, Terminal::currentFeederDirection))
        return this
    }

    fun stopAtNormallyOpen(phase: SinglePhaseKind? = null): NetworkTrace<T> {
        addQueueCondition(OpenCondition(OpenTest.NORMALLY_OPEN, phase))
        return this
    }

    fun stopAtCurrentlyOpen(phase: SinglePhaseKind? = null): NetworkTrace<T> {
        addQueueCondition(OpenCondition(OpenTest.CURRENTLY_OPEN, phase))
        return this
    }

    fun limitEquipmentSteps(limit: Int): NetworkTrace<T> {
        addStopCondition(EquipmentStepLimitCondition(limit))
        return this
    }

    fun limitEquipmentSteps(limit: Int, equipmentType: Class<out ConductingEquipment>): NetworkTrace<T> {
        addStopCondition(EquipmentTypeStepLimitCondition(limit, equipmentType))
        return this
    }

    fun withPhases(phases: PhaseCode): NetworkTrace<T> {
        addQueueCondition(PhaseCondition(phases))
        return this
    }

    override fun getDerivedThis(): NetworkTrace<T> = this
}

fun NetworkTrace<Unit>.run(start: Terminal, canStopOnStartItem: Boolean = true) {
    this.run(start, canStopOnStartItem, Unit)
}

fun NetworkTrace<Unit>.run(start: ConductingEquipment, canStopOnStartItem: Boolean = true) {
    this.run(start, canStopOnStartItem, Unit)
}

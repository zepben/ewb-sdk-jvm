/*
 * Copyright 2023 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.networktrace.conditions

import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind
import com.zepben.evolve.cim.iec61970.base.wires.Switch
import com.zepben.evolve.services.network.tracing.networktrace.NetworkTraceStep
import com.zepben.evolve.services.network.tracing.traversal.QueueCondition
import com.zepben.evolve.services.network.tracing.traversal.StepContext

// NOTE: This is a queue condition and not a stop condition because once cuts and jumpers are added to the network model we need the 'next' terminal
//       to determine if the step goes through an open circuit.
internal class OpenCondition<T>(
    val isOpen: (Switch, SinglePhaseKind?) -> Boolean,
    val phase: SinglePhaseKind? = null
) : QueueCondition<NetworkTraceStep<T>> {
    override fun shouldQueue(nextItem: NetworkTraceStep<T>, nextContext: StepContext, currentItem: NetworkTraceStep<T>, currentContext: StepContext): Boolean =
        if (nextItem.path.tracedInternally && nextItem.path.toEquipment is Switch) {
            !isOpen(nextItem.path.toEquipment, phase)
        } else {
            true
        }

    override fun shouldQueueStartItem(item: NetworkTraceStep<T>): Boolean {
        return true
    }
}

/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.network.tracing.networktrace.conditions

import com.zepben.ewb.cim.iec61970.base.wires.ShuntCompensator
import com.zepben.ewb.services.network.tracing.networktrace.NetworkTraceStep
import com.zepben.ewb.services.network.tracing.traversal.StepContext

internal object ShuntCompensatorCondition {

    /**
     * A [NetworkTraceQueueCondition] that prevents the network trace from queueing between the normal and grounding terminals
     * of a [ShuntCompensator].
     */
    internal class StopOnGround<T> : NetworkTraceQueueCondition<T>(NetworkTraceStep.Type.INTERNAL) {

        override fun shouldQueueMatchedStep(
            nextItem: NetworkTraceStep<T>,
            nextContext: StepContext,
            currentItem: NetworkTraceStep<T>,
            currentContext: StepContext,
        ): Boolean =
            // Queue everything that isn't an internal traversal across a `ShuntCompensator` involving its `groundingTerminal`.
            when (val sc = nextItem.path.toEquipment as? ShuntCompensator) {
                null -> true
                else -> with(nextItem.path) {
                    tracedExternally || ((toTerminal != sc.groundingTerminal) && (fromTerminal != sc.groundingTerminal))
                }
            }

    }

}

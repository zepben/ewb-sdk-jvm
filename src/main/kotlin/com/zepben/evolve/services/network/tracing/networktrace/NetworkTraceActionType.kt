/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.networktrace

import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind
import com.zepben.evolve.services.network.tracing.traversal.StepContext

/**
 * Options to configure when a [NetworkTrace] actions a [NetworkTraceStep].
 */
enum class NetworkTraceActionType {
    /**
     * All steps visited during a [NetworkTrace] will be actioned.
     */
    ALL_STEPS {
        override fun canActionItem(item: NetworkTraceStep<*>, context: StepContext, hasTracked: (Terminal, Set<SinglePhaseKind>) -> Boolean): Boolean {
            return true
        }
    },

    /**
     * Only actions steps where the `toEquipment` on the [NetworkTraceStep.path] has not been visited before on the phases within the [NetworkTraceStep.path].
     * This means that all [NetworkTraceStep.type] of [NetworkTraceStep.Type.INTERNAL] will never be actioned as a first visit will always occur on an
     * external step, except if the step is a start item in the trace.
     */
    FIRST_STEP_ON_EQUIPMENT {
        override fun canActionItem(item: NetworkTraceStep<*>, context: StepContext, hasTracked: (Terminal, Set<SinglePhaseKind>) -> Boolean): Boolean {
            val phases = item.path.nominalPhasePaths.toPhasesSet()
            return item.path.toTerminal.otherTerminals().none { hasTracked(it, phases) }
        }
    }
    ;

    internal abstract fun canActionItem(item: NetworkTraceStep<*>, context: StepContext, hasTracked: (Terminal, Set<SinglePhaseKind>) -> Boolean): Boolean
}

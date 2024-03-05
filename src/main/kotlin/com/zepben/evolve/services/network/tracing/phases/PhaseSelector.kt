/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.phases

import com.zepben.evolve.cim.iec61970.base.core.Terminal

/**
 * Functional interface that can be used by traces to specify which [PhaseStatus] to use.
 * See [SetPhases] or [RemovePhases] for example usage.
 */
fun interface PhaseSelector {

    fun phases(terminal: Terminal): PhaseStatus

    // Constant common implements of PhaseSelector
    companion object {

        @JvmField
        val NORMAL_PHASES: PhaseSelector = PhaseSelector { terminal -> terminal.tracedPhases.normal }

        @JvmField
        val CURRENT_PHASES: PhaseSelector = PhaseSelector { terminal -> terminal.tracedPhases.current }

    }

}

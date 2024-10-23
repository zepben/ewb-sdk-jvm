/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.networktrace.operators

import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.services.network.tracing.phases.PhaseStatus

interface PhaseStateOperators {
    fun phaseStatus(terminal: Terminal): PhaseStatus

    companion object {
        val NORMAL = object : PhaseStateOperators {
            override fun phaseStatus(terminal: Terminal): PhaseStatus = terminal.normalPhases
        }

        val CURRENT = object : PhaseStateOperators {
            override fun phaseStatus(terminal: Terminal): PhaseStatus = terminal.currentPhases
        }
    }
}

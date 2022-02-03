/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network.tracing.phases

import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind

/**
 * Functional interface that can be used by traces to specify which phase status to use.
 * See [SetPhases] for example usage.
 */
fun interface PhaseSelector {

    fun status(terminal: Terminal, nominalPhase: SinglePhaseKind): PhaseStatus

    // Constant common implements of ActivePhaseSelector
    companion object {

        @JvmField
        val NORMAL_PHASES: PhaseSelector = PhaseSelector { terminal: Terminal, nominalPhase: SinglePhaseKind ->
            object : PhaseStatus {

                override val phase: SinglePhaseKind
                    get() = terminal.tracedPhases.phaseNormal(nominalPhase)

                override val direction: FeederDirection
                    get() = terminal.tracedPhases.directionNormal(nominalPhase)

                override fun set(singlePhaseKind: SinglePhaseKind, direction: FeederDirection): Boolean =
                    terminal.tracedPhases.setNormal(singlePhaseKind, direction, nominalPhase)

                override fun add(singlePhaseKind: SinglePhaseKind, direction: FeederDirection): Boolean =
                    terminal.tracedPhases.addNormal(singlePhaseKind, direction, nominalPhase)

                override fun remove(singlePhaseKind: SinglePhaseKind, direction: FeederDirection): Boolean =
                    terminal.tracedPhases.removeNormal(singlePhaseKind, direction, nominalPhase)

                override fun remove(singlePhaseKind: SinglePhaseKind): Boolean =
                    terminal.tracedPhases.removeNormal(singlePhaseKind, nominalPhase)
            }
        }

        @JvmField
        val CURRENT_PHASES: PhaseSelector = PhaseSelector { terminal: Terminal, nominalPhase: SinglePhaseKind ->
            object : PhaseStatus {
                override val phase: SinglePhaseKind
                    get() = terminal.tracedPhases.phaseCurrent(nominalPhase)

                override val direction: FeederDirection
                    get() = terminal.tracedPhases.directionCurrent(nominalPhase)

                override fun set(singlePhaseKind: SinglePhaseKind, direction: FeederDirection): Boolean =
                    terminal.tracedPhases.setCurrent(singlePhaseKind, direction, nominalPhase)

                override fun add(singlePhaseKind: SinglePhaseKind, direction: FeederDirection): Boolean =
                    terminal.tracedPhases.addCurrent(singlePhaseKind, direction, nominalPhase)

                override fun remove(singlePhaseKind: SinglePhaseKind, direction: FeederDirection): Boolean =
                    terminal.tracedPhases.removeCurrent(singlePhaseKind, direction, nominalPhase)

                override fun remove(singlePhaseKind: SinglePhaseKind): Boolean =
                    terminal.tracedPhases.removeCurrent(singlePhaseKind, nominalPhase)
            }
        }

    }

}

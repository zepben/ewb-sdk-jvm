/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.phases

import com.zepben.evolve.cim.iec61970.base.core.PhaseCode
import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind as SPK

@Deprecated(
    "Traced phases are now stored directly as PhaseStatus on the terminal",
    replaceWith = ReplaceWith("/* `Terminal.normalPhases` for the normal traced phase status, `Terminal.currentPhases` for the current trace phase status */")
)
class TracedPhases(private val terminal: Terminal) {

    /**
     * The traced phases in the normal state of the network.
     */
    @Deprecated(
        message = "Use normalPhases directly on the terminal.",
        replaceWith = ReplaceWith("terminal.normalPhases"),
    )
    val normal: PhaseStatus get() = terminal.normalPhases

    /**
     * The traced phases in the current state of the network.
     */
    @Deprecated(
        message = "Use currentPhases directly on the terminal.",
        replaceWith = ReplaceWith("terminal.currentPhases"),
    )
    val current: PhaseStatus get() = terminal.currentPhases

    @Deprecated(
        message = "Use normalPhases directly on the terminal.",
        replaceWith = ReplaceWith("terminal.normalPhases.set"),
    )
    fun setNormal(nominalPhase: SPK, singlePhaseKind: SPK): Boolean =
        normal.set(nominalPhase, singlePhaseKind)

    @Deprecated(
        message = "Use currentPhases directly on the terminal.",
        replaceWith = ReplaceWith("terminal.currentPhases.set"),
    )
    fun setCurrent(nominalPhase: SPK, singlePhaseKind: SPK): Boolean =
        current.set(nominalPhase, singlePhaseKind)

    override fun toString(): String {
        val normal = PhaseCode.ABCN.singlePhases.joinToString(prefix = "{", postfix = "}") { "${normal(it)}" }
        val current = PhaseCode.ABCN.singlePhases.joinToString(prefix = "{", postfix = "}") { "${current(it)}" }
        return "TracedPhases(normal=$normal, current=$current)"
    }

    // Java interop
    @Deprecated(
        message = "Use normalPhases directly on the terminal.",
        replaceWith = ReplaceWith("terminal.normalPhases.get"),
    )
    fun normal(nominalPhase: SPK): SPK = normal[nominalPhase]

    @Deprecated(
        message = "Use currentPhases directly on the terminal.",
        replaceWith = ReplaceWith("terminal.currentPhases.get"),
    )
    fun current(nominalPhase: SPK): SPK = current[nominalPhase]

}

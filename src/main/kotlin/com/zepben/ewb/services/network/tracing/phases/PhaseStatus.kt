/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.network.tracing.phases

import com.zepben.ewb.cim.iec61970.base.core.PhaseCode
import com.zepben.ewb.cim.iec61970.base.core.Terminal
import com.zepben.ewb.cim.iec61970.base.wires.SinglePhaseKind

/**
 * Class that holds the traced phase statuses for a nominal phase on a [Terminal].
 *
 * @property phaseStatusInternal
 */
class PhaseStatus(private val terminal: Terminal) {

    /**
     * The underlying implementation value tracking the phase status for nominal phases of a terminal.
     * It is exposed internally for data serialisation and debugging within official EWB libraries and utilities.
     *
     * This property should be considered internal and not for public use as the underlying
     * data structure to store the status could change at any time (and thus be a breaking change).
     * Use directly at your own risk.
     *
     * See [TracedPhasesBitManipulation] for details on bit representation for phaseStatusInternal and how we track phases status.
     */
    internal var phaseStatusInternal: UShort = 0u

    /**
     * Get the traced phase for the specified [nominalPhase].
     *
     * @param nominalPhase The nominal phase you are interested in querying.
     */
    operator fun get(nominalPhase: SinglePhaseKind): SinglePhaseKind =
        TracedPhasesBitManipulation.get(phaseStatusInternal, nominalPhase.validate())

    /**
     * Set the traced phase for the specified [nominalPhase].
     *
     * @param nominalPhase The nominal phase you are interested in updating.
     * @param singlePhaseKind The phase you wish to set for this [nominalPhase]. Specify [SinglePhaseKind.NONE] to clear the phase.
     *
     * @return True if the phase is updated, otherwise false.
     */
    operator fun set(nominalPhase: SinglePhaseKind, singlePhaseKind: SinglePhaseKind): Boolean =
        get(nominalPhase).let {
            if (it == singlePhaseKind)
                false
            else if ((it == SinglePhaseKind.NONE) || (singlePhaseKind == SinglePhaseKind.NONE)) {
                phaseStatusInternal = TracedPhasesBitManipulation.set(
                    phaseStatusInternal,
                    nominalPhase,
                    singlePhaseKind
                )
                true
            } else
                throw UnsupportedOperationException("Crossing Phases.")
        }

    override fun toString(): String {
        val codes = PhaseCode.ABCN.singlePhases.joinToString(prefix = "{", postfix = "}") { "${get(it)}" }
        return "PhaseStatus($codes)"
    }

    private fun SinglePhaseKind.validate(): SinglePhaseKind {
        when (this) {
            SinglePhaseKind.A, SinglePhaseKind.B, SinglePhaseKind.C, SinglePhaseKind.N, SinglePhaseKind.X, SinglePhaseKind.Y, SinglePhaseKind.s1, SinglePhaseKind.s2 -> return this
            SinglePhaseKind.NONE, SinglePhaseKind.INVALID -> throw IllegalArgumentException("INTERNAL ERROR: Phase $this is invalid.")
        }
    }

    /**
     * Get the traced phase for each nominal phase as a [PhaseCode].
     *
     * @return The [PhaseCode] if the combination of phases makes sense, otherwise null.
     */
    fun asPhaseCode(): PhaseCode? {
        if (terminal.phases == PhaseCode.NONE)
            return PhaseCode.NONE

        val tracedPhases = terminal.phases.singlePhases.map { get(it) }
        val phases = tracedPhases.toSet()

        return if (SinglePhaseKind.NONE in phases)
            PhaseCode.NONE.takeIf { phases.size == 1 }
        else if (phases.size == tracedPhases.size)
            PhaseCode.fromSinglePhases(phases)
        else
            null
    }

}

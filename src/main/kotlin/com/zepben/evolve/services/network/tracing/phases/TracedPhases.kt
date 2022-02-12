/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network.tracing.phases

import com.zepben.evolve.cim.iec61970.base.core.PhaseCode
import com.zepben.evolve.services.network.tracing.phases.TracedPhasesBitManipulation.get
import com.zepben.evolve.services.network.tracing.phases.TracedPhasesBitManipulation.set
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind as SPK

/**
 * Class that holds the traced phase statuses for the current and normal state of the network.
 *
 * Traced phase status:
 * |     integer      |
 * | 16 bits |16 bits |
 * | current | normal |
 *
 * See [TracedPhasesBitManipulation] for details on bit representation for normal and current status.
 *
 * @property phaseStatusInternal The underlying implementation value tracking the phase statuses for the current and normal state of the network.
 *                               It is primarily used for data serialisation and debugging within official evolve libraries and utilities.
 *
 *                               NOTE: This property should be considered evolve internal and not for public use as the underlying
 *                               data structure to store the status could change at any time (and thus be a breaking change).
 *                               Use at your own risk.
 */
data class TracedPhases(
    internal var phaseStatusInternal: UInt = 0u
) {

    private val normalMask: UInt = 0x0000ffff.toUInt()
    private val currentMask: UInt = 0xffff0000.toUInt()
    private val currentShift = 16

    /**
     * The traced phases in the normal state of the network.
     */
    val normal: PhaseStatus = object : PhaseStatus {
        override operator fun get(nominalPhase: SPK): SPK = normal(nominalPhase)
        override operator fun set(nominalPhase: SPK, singlePhaseKind: SPK): Boolean = setNormal(nominalPhase, singlePhaseKind)
    }

    /**
     * The traced phases in the current state of the network.
     */
    val current: PhaseStatus = object : PhaseStatus {
        override operator fun get(nominalPhase: SPK): SPK = current(nominalPhase)
        override operator fun set(nominalPhase: SPK, singlePhaseKind: SPK): Boolean = setCurrent(nominalPhase, singlePhaseKind)
    }


    fun setNormal(nominalPhase: SPK, singlePhaseKind: SPK): Boolean =
        normal(nominalPhase).let {
            if (it == singlePhaseKind)
                false
            else if ((it == SPK.NONE) || (singlePhaseKind == SPK.NONE)) {
                phaseStatusInternal = (phaseStatusInternal and currentMask) or set(phaseStatusInternal, nominalPhase, singlePhaseKind)
                true
            } else
                throw UnsupportedOperationException("Crossing Phases.")
        }

    fun setCurrent(nominalPhase: SPK, singlePhaseKind: SPK): Boolean =
        current(nominalPhase).let {
            if (it == singlePhaseKind)
                false
            else if ((it == SPK.NONE) || (singlePhaseKind == SPK.NONE)) {
                phaseStatusInternal =
                    (phaseStatusInternal and normalMask) or (set(phaseStatusInternal shr currentShift, nominalPhase, singlePhaseKind) shl currentShift)
                true
            } else
                throw UnsupportedOperationException("Crossing Phases.")
        }

    override fun toString(): String {
        val normal = PhaseCode.ABCN.singlePhases.joinToString(prefix = "{", postfix = "}") { "${normal(it)}" }
        val current = PhaseCode.ABCN.singlePhases.joinToString(prefix = "{", postfix = "}") { "${current(it)}" }
        return "TracedPhases(normal=$normal, current=$current)"
    }

    // Java interop
    fun normal(nominalPhase: SPK): SPK = get(phaseStatusInternal, nominalPhase.validate())
    fun current(nominalPhase: SPK): SPK = get(phaseStatusInternal shr currentShift, nominalPhase.validate())

    private fun SPK.validate(): SPK {
        when (this) {
            SPK.A, SPK.B, SPK.C, SPK.N, SPK.X, SPK.Y, SPK.s1, SPK.s2 -> return this
            SPK.NONE, SPK.INVALID -> throw IllegalArgumentException("INTERNAL ERROR: Phase $this is invalid.")
        }
    }

}

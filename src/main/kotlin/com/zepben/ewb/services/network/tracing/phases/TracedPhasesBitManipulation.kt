/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.network.tracing.phases

import com.zepben.ewb.cim.iec61970.base.wires.SinglePhaseKind


/**
 * Class that performs the bit manipulation for the traced phase statuses.
 * Each byte in an int is used to store all possible phases and directions for a nominal phase.
 * Each byte has 2 bits that represent the direction for a phase. If none of those bits are set the direction is equal to NONE.
 * Use the figures below as a reference.
 * <p>
 * Network state phase status:
 *                |              16 bits              |
 *                | 4 bits | 4 bits | 4 bits | 4 bits |
 * Nominal phase: |   N    |    C   | B/Y/s2 | A/X/s1 |
 * <p>
 * Each nominal phase (actual phase):
 *               |                 4 bits                |
 *               |  1 bit  |  1 bit  |  1 bit  |  1 bit  |
 * Actual Phase: |    N    |    C    |    B    |    A    |
 */
internal object TracedPhasesBitManipulation {

    /**
     * Bitwise mask for selecting the actual phases from a nominal phase A/B/C/N, X/Y/N or s1/s2/N
     */
    private val nominalPhaseMasks = listOf(0x000f, 0x00f0, 0x0f00, 0xf000).map { it.toUInt() }

    /**
     * Bitwise mask for setting the presence of an actual phase
     */
    private val phaseMasks = listOf(1u, 2u, 4u, 8u)

    @JvmStatic
    fun get(status: UShort, nominalPhase: SinglePhaseKind): SinglePhaseKind {
        return when ((status.toUInt() shr nominalPhase.byteSelector()) and 15u) {
            1u -> SinglePhaseKind.A
            2u -> SinglePhaseKind.B
            4u -> SinglePhaseKind.C
            8u -> SinglePhaseKind.N
            else -> SinglePhaseKind.NONE
        }
    }

    @JvmStatic
    fun set(status: UShort, nominalPhase: SinglePhaseKind, singlePhaseKind: SinglePhaseKind): UShort {
        val newStatus = if (singlePhaseKind == SinglePhaseKind.NONE)
            (status.toUInt() and nominalPhaseMasks[nominalPhase.maskIndex].inv())
        else
            (status.toUInt() and nominalPhaseMasks[nominalPhase.maskIndex].inv()) or singlePhaseKind.shiftedValue(nominalPhase)

        return newStatus.toUShort()
    }

    private fun SinglePhaseKind.byteSelector() = maskIndex * 4

    private fun SinglePhaseKind.shiftedValue(nominalPhase: SinglePhaseKind) =
        phaseMasks[maskIndex] shl nominalPhase.byteSelector()

}

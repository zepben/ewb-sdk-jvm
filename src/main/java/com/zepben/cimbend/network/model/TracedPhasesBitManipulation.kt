/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 * This file is part of evolve-sdk-jvm.
 *
 * evolve-sdk-jvm is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * evolve-sdk-jvm is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with evolve-sdk-jvm.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.zepben.cimbend.network.model

import com.zepben.cimbend.cim.iec61970.base.wires.SinglePhaseKind
import com.zepben.cimbend.network.model.PhaseDirection.Companion.from



/**
 * Class that performs the bit manipulation for the traced phase statuses.
 * Each byte in an int is used to store all possible phases and directions for a nominal phase.
 * Each byte has 2 bits that represent the direction for a phase. If none of those bits are set the direction is equal to NONE.
 * Use the figures below as a reference.
 * <p>
 * Traced phase status:
 * |          integer          |
 * | byte | byte | byte | byte |
 * |  N   |   C  |  B   |  A   |
 * <p>
 * Each nominal phase:
 *               |                 byte                  |
 *               |  2bits  |  2bits  |  2bits  |  2bits  |
 * Actual Phase: |    N    |    C    |   B/Y   |   A/X   |
 * Direction:    |OUT | IN |OUT | IN |OUT | IN |OUT | IN |
 */
class TracedPhasesBitManipulation {

    companion object {
        private val coreMasks = intArrayOf(0x000000ff, 0x0000ff00, 0x00ff0000, -0x1000000)
        private const val DIR_MASK = 3

        private const val PHASE_A_IN = 1
        private const val PHASE_A_OUT = 2
        private const val PHASE_A_BOTH = 3
        private const val PHASE_B_IN = 4
        private const val PHASE_B_OUT = 8
        private const val PHASE_B_BOTH = 12
        private const val PHASE_C_IN = 16
        private const val PHASE_C_OUT = 32
        private const val PHASE_C_BOTH = 48
        private const val PHASE_N_IN = 64
        private const val PHASE_N_OUT = 128
        private const val PHASE_N_BOTH = 192

        @JvmStatic
        fun phase(status: Int, nominalPhase: SinglePhaseKind): SinglePhaseKind {
            return when ((status ushr nominalPhase.byteSelector()) and 0xff) {
                PHASE_A_IN, PHASE_A_OUT, PHASE_A_BOTH -> SinglePhaseKind.A
                PHASE_B_IN, PHASE_B_OUT, PHASE_B_BOTH -> SinglePhaseKind.B
                PHASE_C_IN, PHASE_C_OUT, PHASE_C_BOTH -> SinglePhaseKind.C
                PHASE_N_IN, PHASE_N_OUT, PHASE_N_BOTH -> SinglePhaseKind.N
                else -> SinglePhaseKind.NONE
            }
        }

        @JvmStatic
        fun direction(status: Int, nominalPhase: SinglePhaseKind): PhaseDirection {
            val dirValue = (status ushr phase(status, nominalPhase).positionShift(nominalPhase)) and DIR_MASK
            return from(dirValue)
        }

        @JvmStatic
        fun set(status: Int, singlePhaseKind: SinglePhaseKind, direction: PhaseDirection, nominalPhase: SinglePhaseKind): Int {
            return (status and coreMasks[nominalPhase.maskIndex()].inv()) or direction.shiftedValue(singlePhaseKind, nominalPhase)
        }

        @JvmStatic
        fun add(status: Int, singlePhaseKind: SinglePhaseKind, direction: PhaseDirection, nominalPhase: SinglePhaseKind): Int {
            return status or direction.shiftedValue(singlePhaseKind, nominalPhase)
        }

        @JvmStatic
        fun remove(status: Int, singlePhaseKind: SinglePhaseKind, direction: PhaseDirection, nominalPhase: SinglePhaseKind): Int {
            return status and direction.shiftedValue(singlePhaseKind, nominalPhase).inv()
        }

        @JvmStatic
        fun remove(status: Int, nominalPhase: SinglePhaseKind): Int {
            return status and coreMasks[nominalPhase.maskIndex()].inv()
        }

        private fun SinglePhaseKind.positionShift(nominalPhase: SinglePhaseKind) = phaseSelector() + nominalPhase.byteSelector()

        private fun SinglePhaseKind.byteSelector() = maskIndex() * 8

        private fun SinglePhaseKind.phaseSelector() = 2 * (value() - 1).coerceAtLeast(0)

        private fun PhaseDirection.shiftedValue(singlePhaseKind: SinglePhaseKind, nominalPhase: SinglePhaseKind) =
            value() shl singlePhaseKind.positionShift(nominalPhase)
    }
}

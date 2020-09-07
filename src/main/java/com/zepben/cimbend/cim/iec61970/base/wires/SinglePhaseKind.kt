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
package com.zepben.cimbend.cim.iec61970.base.wires

import com.zepben.cimbend.cim.iec61970.base.wires.SinglePhaseKind.*

/**
 * Enumeration of single phase identifiers. Allows designation of single phases for both transmission and distribution equipment, circuits and loads.
 *
 * @property NONE no phase specified.
 * @property A Phase A.
 * @property B Phase B.
 * @property C Phase C.
 * @property N Neutral.
 * @property X An unknown primary phase.
 * @property Y An unknown primary phase.
 * @property INVALID Invalid phase. Caused by trying to energise with multiple phases simultaneously.
 */
enum class SinglePhaseKind(private val value: Int, private val maskIndex: Int) {

    NONE(0, -1),
    A(1, 0),
    B(2, 1),
    C(3, 2),
    N(4, 3),
    X(5, 0),
    Y(6, 1),
    INVALID(7, -1);

    companion object {
        private val phasesByValues: List<SinglePhaseKind> = enumValues<SinglePhaseKind>().sortedBy { it.value }

        @JvmStatic
        fun get(value: Int): SinglePhaseKind {
            return if (value < 0 || value > 6) INVALID else phasesByValues[value]
        }
    }

    private val bitMask = if (maskIndex >= 0) 1 shl maskIndex else 0

    fun value(): Int {
        return value
    }

    fun maskIndex(): Int {
        return maskIndex
    }

    fun bitMask(): Int {
        return bitMask
    }
}

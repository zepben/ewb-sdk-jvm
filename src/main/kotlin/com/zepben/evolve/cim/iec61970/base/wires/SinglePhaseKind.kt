/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.cim.iec61970.base.wires

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
 * @property s1 Secondary phase 1.
 * @property s2 Secondary phase 2.
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
    s1(7, 0),
    s2(8, 1),
    INVALID(9, -1);

    companion object {
        private val phasesByValues: List<SinglePhaseKind> = enumValues<SinglePhaseKind>().sortedBy { it.value }

        @JvmStatic
        fun get(value: Int): SinglePhaseKind {
            return if (value < 0 || value >= INVALID.value) INVALID else phasesByValues[value]
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

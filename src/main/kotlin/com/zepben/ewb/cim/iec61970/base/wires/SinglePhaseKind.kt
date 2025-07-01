/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.wires

import com.zepben.ewb.cim.extensions.ZBEX
import com.zepben.ewb.cim.iec61970.base.core.PhaseCode

/**
 * Enumeration of single phase identifiers. Allows designation of single phases for both transmission and distribution equipment, circuits and loads.
 *
 * @property value The unique value of the [SinglePhaseKind] that can be used to serialise the value.
 * @property maskIndex The index in the bitmask representation. This overlaps between known and unknown phases.
 * @property bitMask The bitmask representation of this [SinglePhaseKind], based on its [maskIndex].
 */
@Suppress("EnumEntryName")
enum class SinglePhaseKind(val value: Int, val maskIndex: Int) {

    /**
     * no phase specified.
     */
    NONE(0, -1),

    /**
     * Phase A.
     */
    A(1, 0),

    /**
     * Phase B.
     */
    B(2, 1),

    /**
     * Phase C.
     */
    C(3, 2),

    /**
     * Neutral.
     */
    N(4, 3),

    /**
     * An unknown primary phase.
     */
    X(5, 0),

    /**
     * An unknown primary phase.
     */
    Y(6, 1),

    /**
     * Secondary phase 1.
     */
    s1(7, 0),

    /**
     * Secondary phase 2.
     */
    s2(8, 1),

    /**
     * Invalid phase. Caused by trying to energise with multiple phases simultaneously.
     */
    @ZBEX
    INVALID(9, -1);

    companion object {
        private val phasesByValues: List<SinglePhaseKind> = enumValues<SinglePhaseKind>().sortedBy { it.value }

        /**
         * Indexed operator to get a [SinglePhaseKind] by its [value].
         */
        @JvmStatic
        operator fun get(value: Int): SinglePhaseKind {
            return if (value < 0 || value >= INVALID.value) INVALID else phasesByValues[value]
        }
    }

    val bitMask: Int = if (maskIndex >= 0) 1 shl maskIndex else 0

    /**
     * Operator to add a [SinglePhaseKind] into a returned [PhaseCode].
     *
     * @param phase The [SinglePhaseKind] to add to this [SinglePhaseKind].
     * @return The [PhaseCode] with the two [SinglePhaseKind] values, or [PhaseCode.NONE] if the addition is invalid.
     */
    operator fun plus(phase: SinglePhaseKind): PhaseCode = PhaseCode.fromSinglePhases(setOf(this) + phase)

    /**
     * Operator to add this [SinglePhaseKind] into a [PhaseCode].
     *
     * @param phaseCode The [PhaseCode] to add to this [SinglePhaseKind].
     * @return The [PhaseCode] with the two [SinglePhaseKind] values, or [PhaseCode.NONE] if the addition is invalid.
     */
    operator fun plus(phaseCode: PhaseCode): PhaseCode = PhaseCode.fromSinglePhases(setOf(this) + phaseCode.singlePhases)

    /**
     * Operator to remove a [SinglePhaseKind] from this [SinglePhaseKind].
     *
     * @param phase The [SinglePhaseKind] to remove from this [SinglePhaseKind].
     * @return The [PhaseCode] representation of this [SinglePhaseKind] values, or [PhaseCode.NONE] if the removed phase matched.
     */
    operator fun minus(phase: SinglePhaseKind): PhaseCode = PhaseCode.fromSinglePhases(setOf(this) - phase)

    /**
     * Operator to remove all [SinglePhaseKind] in a [PhaseCode] from this [SinglePhaseKind].
     *
     * @param phaseCode The [PhaseCode] to remove from this [SinglePhaseKind].
     * @return The [PhaseCode] representation of this [SinglePhaseKind] values, or [PhaseCode.NONE] if any of the removed phase matched.
     */
    operator fun minus(phaseCode: PhaseCode): PhaseCode = PhaseCode.fromSinglePhases(setOf(this) - phaseCode.singlePhases.toSet())

}

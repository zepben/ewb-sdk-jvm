/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.wires

import com.zepben.evolve.services.common.extensions.asUnmodifiable
import com.zepben.evolve.services.common.extensions.typeNameAndMRID
import java.util.function.BiConsumer

/**
 * Impedance and admittance parameters per unit length for n-wire unbalanced lines, in matrix form.
 */
class PerLengthPhaseImpedance @JvmOverloads constructor(mRID: String = "") : PerLengthImpedance(mRID) {

    private var _phaseImpedanceData: MutableList<PhaseImpedanceData>? = null

    val phaseImpedanceData: List<PhaseImpedanceData> get() = _phaseImpedanceData.asUnmodifiable()

    /**
     * Get the number of entries in the [PhaseImpedanceData] collection.
     */
    fun numPhaseImpedanceData(): Int = _phaseImpedanceData?.size ?: 0

    /**
     * Get only the diagonal elements of the matrix, i.e toPhase == fromPhase.
     */
    fun diagonal(): List<PhaseImpedanceData>? = _phaseImpedanceData?.filter { it.toPhase == it.fromPhase }

    /**
     * Get the matrix entry for the corresponding to and from phases.
     * @param fromPhase The from phase to lookup.
     * @param toPhase The to phase to lookup.
     */
    fun getData(fromPhase: SinglePhaseKind, toPhase: SinglePhaseKind): PhaseImpedanceData? =
        _phaseImpedanceData?.find { it.fromPhase == fromPhase && it.toPhase == toPhase }

    /**
     * Java interop forEachIndexed. Perform the specified action against each [PhaseImpedanceData].
     *
     * @param action The action to perform on each [PhaseImpedanceData]
     */
    fun forEachPhaseImpedanceData(action: BiConsumer<Int, PhaseImpedanceData>) {
        _phaseImpedanceData?.forEachIndexed(action::accept)
    }

    /**
     * Add a [PhaseImpedanceData] to this [PerLengthPhaseImpedance]
     * @param phaseImpedanceData The [PhaseImpedanceData] to add
     */
    fun addPhaseImpedanceData(phaseImpedanceData: PhaseImpedanceData): PerLengthPhaseImpedance {
        require(
            _phaseImpedanceData.isNullOrEmpty()
                || _phaseImpedanceData?.none { pid -> pid.fromPhase == phaseImpedanceData.fromPhase && pid.toPhase == phaseImpedanceData.toPhase } == true) {
            "Unable to add PhaseImpedanceData to ${typeNameAndMRID()}. " +
                "A PhaseImpedanceData with fromPhase ${phaseImpedanceData.fromPhase} and toPhase ${phaseImpedanceData.toPhase} already exists in " +
                "this PerLengthPhaseImpedance."
        }

        _phaseImpedanceData = _phaseImpedanceData ?: mutableListOf()
        _phaseImpedanceData!!.add(phaseImpedanceData)

        return this
    }

    /**
     * Remove a [PhaseImpedanceData] from this [PerLengthPhaseImpedance]
     * @param phaseImpedanceData The [PhaseImpedanceData] to remove.
     * @return true if the [phaseImpedanceData] was removed.
     */
    fun removePhaseImpedanceData(phaseImpedanceData: PhaseImpedanceData): Boolean {
        val ret = _phaseImpedanceData?.remove(phaseImpedanceData) == true
        if (_phaseImpedanceData.isNullOrEmpty()) _phaseImpedanceData = null
        return ret
    }

    /**
     * Clear all [PhaseImpedanceData] from this [PerLengthPhaseImpedance]
     */
    fun clearPhaseImpedanceData(): PerLengthPhaseImpedance {
        _phaseImpedanceData = null
        return this
    }
}

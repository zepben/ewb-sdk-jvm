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

    private var _data: MutableList<PhaseImpedanceData>? = null

    val data: List<PhaseImpedanceData> get() = _data.asUnmodifiable()

    /**
     * Get the number of entries in the [PhaseImpedanceData] collection.
     */
    fun numData(): Int = _data?.size ?: 0

    /**
     * Get only the diagonal elements of the matrix, i.e toPhase == fromPhase.
     */
    fun diagonal(): List<PhaseImpedanceData>? = _data?.filter { it.toPhase == it.fromPhase }

    /**
     * Get the matrix entry for the corresponding to and from phases.
     * @param fromPhase The from phase to lookup.
     * @param toPhase The to phase to lookup.
     */
    fun getData(fromPhase: SinglePhaseKind, toPhase: SinglePhaseKind): PhaseImpedanceData? =
        _data?.find { it.fromPhase == fromPhase && it.toPhase == toPhase }

    /**
     * Java interop forEachIndexed. Perform the specified action against each [PhaseImpedanceData].
     *
     * @param action The action to perform on each [PhaseImpedanceData]
     */
    fun forEachData(action: BiConsumer<Int, PhaseImpedanceData>) {
        _data?.forEachIndexed(action::accept)
    }

    /**
     * Add a [PhaseImpedanceData] to this [PerLengthPhaseImpedance]
     * @param phaseImpedanceData The [PhaseImpedanceData] to add
     */
    fun addData(phaseImpedanceData: PhaseImpedanceData): PerLengthPhaseImpedance {
        require(
            _data.isNullOrEmpty()
                || _data?.none { pid -> pid.fromPhase == phaseImpedanceData.fromPhase && pid.toPhase == phaseImpedanceData.toPhase } == true) {
            "Unable to add PhaseImpedanceData to ${typeNameAndMRID()}. " +
                "A PhaseImpedanceData with fromPhase ${phaseImpedanceData.fromPhase} and toPhase ${phaseImpedanceData.toPhase} already exists in " +
                "this PerLengthPhaseImpedance."
        }

        _data = _data ?: mutableListOf()
        _data!!.add(phaseImpedanceData)

        return this
    }

    /**
     * Remove a [PhaseImpedanceData] from this [PerLengthPhaseImpedance]
     * @param phaseImpedanceData The [PhaseImpedanceData] to remove.
     * @return true if the [phaseImpedanceData] was removed.
     */
    fun removeData(phaseImpedanceData: PhaseImpedanceData): Boolean {
        val ret = _data?.remove(phaseImpedanceData) == true
        if (_data.isNullOrEmpty()) _data = null
        return ret
    }

    /**
     * Clear all [PhaseImpedanceData] from this [PerLengthPhaseImpedance]
     */
    fun clearData(): PerLengthPhaseImpedance {
        _data = null
        return this
    }
}

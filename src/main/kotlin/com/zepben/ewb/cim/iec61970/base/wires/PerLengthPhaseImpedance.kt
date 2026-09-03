/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.wires

import com.zepben.ewb.boilerplate.relations.PhaseImpedanceDataList

/**
 * Impedance and admittance parameters per unit length for n-wire unbalanced lines, in matrix form.
 *
 * @property data All data that belong to this conductor phase impedance.
 */
class PerLengthPhaseImpedance(mRID: String) : PerLengthImpedance(mRID) {

    private var _data: MutableList<PhaseImpedanceData>? = null

    val data: PhaseImpedanceDataList
        get() = PhaseImpedanceDataList(
            { _data },
            { _data = it },
            { validateData(it) }
        )

    fun validateData(phaseImpedanceData: PhaseImpedanceData) {
        require(
            _data.isNullOrEmpty()
                || _data?.none { pid -> pid.fromPhase == phaseImpedanceData.fromPhase && pid.toPhase == phaseImpedanceData.toPhase } == true,
        ) {
            "Unable to add PhaseImpedanceData to ${typeNameAndMRID()}. " +
                "A PhaseImpedanceData with fromPhase ${phaseImpedanceData.fromPhase} and toPhase ${phaseImpedanceData.toPhase} already exists in " +
                "this PerLengthPhaseImpedance."
        }
    }

    /**
     * Get only the diagonal elements of the matrix, i.e toPhase == fromPhase.
     */
    fun diagonal(): List<PhaseImpedanceData>? = _data?.let { data.diagonal() }

    // region deprecated list boilerplate
    //
    // ("region/endregion" is an IntelliJ feature letting you hide the entire thing)
    // This boilerplate exists solely to enable backwards compatibility.
    // It will be removed eventually.
    // Every single method simply forwards the call to the corresponding list.

    // region data boilerplate

    /**
     * Get the number of entries in the [PhaseImpedanceData] collection.
     */
    @Deprecated(
        message = "Use data.size instead.",
        replaceWith = ReplaceWith("data.size")
    )
    fun numData(): Int = _data?.size ?: 0

    /**
     * Get the matrix entry for the corresponding to and from phases.
     *
     * @param fromPhase The "from" phase to lookup.
     * @param toPhase The "to" phase to lookup.
     * @return The matching [PhaseImpedanceData] or null if none was found.
     */
    @Deprecated(
        message = "Use data.get(fromPhase, toPhase) instead.",
        replaceWith = ReplaceWith("data.get(fromPhase, toPhase)")
    )
    fun getData(fromPhase: SinglePhaseKind, toPhase: SinglePhaseKind): PhaseImpedanceData? =
        _data?.find { it.fromPhase == fromPhase && it.toPhase == toPhase }

    /**
     * Add a [PhaseImpedanceData] to this [PerLengthPhaseImpedance]
     * @param phaseImpedanceData The [PhaseImpedanceData] to add
     * @return This [PerLengthPhaseImpedance] for fluent use.
     */
    @Deprecated(
        message = "Use data.add(phaseImpedanceData) instead.",
        replaceWith = ReplaceWith("also { it.data.add(phaseImpedanceData) }")
    )
    fun addData(phaseImpedanceData: PhaseImpedanceData): PerLengthPhaseImpedance {
        require(
            _data.isNullOrEmpty()
                || _data?.none { pid -> pid.fromPhase == phaseImpedanceData.fromPhase && pid.toPhase == phaseImpedanceData.toPhase } == true,
        ) {
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
    @Deprecated(
        message = "Use data.remove(phaseImpedanceData) instead.",
        replaceWith = ReplaceWith("data.remove(phaseImpedanceData)")
    )
    fun removeData(phaseImpedanceData: PhaseImpedanceData): Boolean {
        val ret = _data?.remove(phaseImpedanceData) == true
        if (_data.isNullOrEmpty()) _data = null
        return ret
    }

    /**
     * Clear all [PhaseImpedanceData] from this [PerLengthPhaseImpedance]
     */
    @Deprecated(
        message = "Use data.clear() instead.",
        replaceWith = ReplaceWith("data.clear()")
    )
    fun clearData(): PerLengthPhaseImpedance {
        _data = null
        return this
    }

    // endregion

    // endregion

}

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

    @Deprecated(
        message = "Use data.size instead.",
        replaceWith = ReplaceWith("data.size")
    )
    fun numData(): Int = data.size

    @Deprecated(
        message = "Use data.get(fromPhase, toPhase) instead.",
        replaceWith = ReplaceWith("data.get(fromPhase, toPhase)")
    )
    fun getData(fromPhase: SinglePhaseKind, toPhase: SinglePhaseKind): PhaseImpedanceData? = data.get(fromPhase, toPhase)

    @Deprecated(
        message = "Use data.add(phaseImpedanceData) instead.",
        replaceWith = ReplaceWith("also { it.data.add(phaseImpedanceData) }")
    )
    fun addData(phaseImpedanceData: PhaseImpedanceData): PerLengthPhaseImpedance = apply {
        data.add(phaseImpedanceData)
    }

    @Deprecated(
        message = "Use data.remove(phaseImpedanceData) instead.",
        replaceWith = ReplaceWith("data.remove(phaseImpedanceData)")
    )
    fun removeData(phaseImpedanceData: PhaseImpedanceData): Boolean = data.remove(phaseImpedanceData)

    @Deprecated(
        message = "Use data.clear() instead.",
        replaceWith = ReplaceWith("data.clear()")
    )
    fun clearData(): PerLengthPhaseImpedance = apply {
        data.clear()
    }

    // endregion

    // endregion

}

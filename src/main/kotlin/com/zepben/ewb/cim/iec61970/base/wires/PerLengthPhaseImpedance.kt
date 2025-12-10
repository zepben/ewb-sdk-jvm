/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.wires

import com.zepben.ewb.services.common.extensions.asUnmodifiable
import com.zepben.ewb.services.common.extensions.typeNameAndMRID

/**
 * Impedance and admittance parameters per unit length for n-wire unbalanced lines, in matrix form.
 */
class PerLengthPhaseImpedance @JvmOverloads constructor(mRID: String = "") : PerLengthImpedance(mRID) {

    private var _data: MutableList<com.zepben.ewb.cim.iec61970.base.wires.PhaseImpedanceData>? = null

    val data: List<com.zepben.ewb.cim.iec61970.base.wires.PhaseImpedanceData> get() = _data.asUnmodifiable()

    @Deprecated("BOILERPLATE: Use data.size instead")
    fun numData(): Int = data.size

    /**
     * Get only the diagonal elements of the matrix, i.e toPhase == fromPhase.
     */
    fun diagonal(): List<com.zepben.ewb.cim.iec61970.base.wires.PhaseImpedanceData>? = _data?.filter { it.toPhase == it.fromPhase }

    /**
     * Get the matrix entry for the corresponding to and from phases.
     *
     * @param fromPhase The "from" phase to lookup.
     * @param toPhase The "to" phase to lookup.
     * @return The matching [PhaseImpedanceData] or null if none was found.
     */
    fun getData(fromPhase: SinglePhaseKind, toPhase: SinglePhaseKind): com.zepben.ewb.cim.iec61970.base.wires.PhaseImpedanceData? =
        _data?.find { it.fromPhase == fromPhase && it.toPhase == toPhase }

    /**
     * Add a [PhaseImpedanceData] to this [PerLengthPhaseImpedance]
     * @param phaseImpedanceData The [PhaseImpedanceData] to add
     * @return This [PerLengthPhaseImpedance] for fluent use.
     */
    fun addData(phaseImpedanceData: com.zepben.ewb.cim.iec61970.base.wires.PhaseImpedanceData): PerLengthPhaseImpedance {
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
    fun removeData(phaseImpedanceData: com.zepben.ewb.cim.iec61970.base.wires.PhaseImpedanceData): Boolean {
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

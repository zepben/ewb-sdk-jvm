/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.wires

import com.zepben.ewb.services.common.extensions.*
import com.zepben.ewb.testing.MRIDListWrapper

/**
 * A wire or combination of wires, with consistent electrical characteristics, building a single electrical system, used to carry alternating current
 * between points in the power system.
 *
 * For symmetrical, transposed 3ph lines, it is sufficient to use  attributes of the line segment, which describe impedances and admittances for the
 * entire length of the segment. Additionally, impedances can be computed by using length and associated per length impedances.
 *
 * The BaseVoltage at the two ends of ACLineSegments in a Line shall have the same BaseVoltage.nominalVoltage. However, boundary lines  may have
 * slightly different BaseVoltage.nominalVoltages and  variation is allowed. Larger voltage difference in general requires use of an equivalent branch.
 *
 * @property perLengthImpedance Per-length impedance of this line segment.
 * @property perLengthPhaseImpedance Per-length phase impedance of this line segment.
 * @property perLengthSequenceImpedance Per-length sequence impedance of this line segment.
 * @property cuts Cuts applied to the line segment.
 * @property clamps The clamps connected to the line segment.
 */
class AcLineSegment @JvmOverloads constructor(mRID: String = "") : Conductor(mRID) {

    override val maxTerminals: Int get() = 2

    var perLengthImpedance: PerLengthImpedance? = null
    private var _cuts: MutableList<Cut>? = null
    private var _clamps: MutableList<Clamp>? = null

    var perLengthSequenceImpedance: PerLengthSequenceImpedance?
        get() = perLengthImpedance as? PerLengthSequenceImpedance
        set(it) {
            perLengthImpedance = it
        }

    var perLengthPhaseImpedance: PerLengthPhaseImpedance?
        get() = perLengthImpedance as? PerLengthPhaseImpedance
        set(it) {
            perLengthImpedance = it
        }

    val cuts: MRIDListWrapper<Cut>
        get() = MRIDListWrapper(
            getter = { _cuts },
            setter = { _cuts = it },
            validate = ::validateCut)

    /**
     * Get the number of entries in the [Cut] collection.
     */
    fun numCuts(): Int = _cuts?.size ?: 0

    @Deprecated("BOILERPLATE: Use cuts.getByMRID(mRID) instead")
    fun getCut(mRID: String): Cut? = cuts.getByMRID(mRID)

    @Deprecated("BOILERPLATE: Use cuts.add(cut) instead")
    fun addCut(cut: Cut): AcLineSegment {
        cuts.add(cut)
        return this
    }

    @Deprecated("BOILERPLATE: Use cuts.remove(cut) instead")
    fun removeCut(cut: Cut): Boolean = cuts.remove(cut)

    @Deprecated("BOILERPLATE: Use cuts.clear() instead")
    fun clearCuts(): AcLineSegment {
        cuts.clear()
        return this
    }

    val clamps: MRIDListWrapper<Clamp>
        get() = MRIDListWrapper(
            getter = { _clamps },
            setter = { _clamps = it },
            validate = ::validateClamp)

    @Deprecated("BOILERPLATE: Use clamps.size instead")
    fun numClamps(): Int = clamps.size

    @Deprecated("BOILERPLATE: Use clamps.getByMRID(mRID) instead")
    fun getClamp(mRID: String): Clamp? = clamps.getByMRID(mRID)

    @Deprecated("BOILERPLATE: Use clamps.add(clamp) instead")
    fun addClamp(clamp: Clamp): AcLineSegment {
        clamps.add(clamp)
        return this
    }

    @Deprecated("BOILERPLATE: Use clamps.remove(clamp) instead")
    fun removeClamp(clamp: Clamp): Boolean = clamps.remove(clamp)

    @Deprecated("BOILERPLATE: Use clamps.clear() instead")
    fun clearClamps(): AcLineSegment {
        clamps.clear()
        return this
    }

    private fun validateCut(cut: Cut): Boolean {
        if (cut.acLineSegment == null)
            cut.acLineSegment = this

        require(cut.acLineSegment === this) {
            "${cut.typeNameAndMRID()} `acLineSegment` property references ${cut.acLineSegment!!.typeNameAndMRID()}, expected ${typeNameAndMRID()}."
        }
        return false
    }

    private fun validateClamp(clamp: Clamp): Boolean {
        if (clamp.acLineSegment == null)
            clamp.acLineSegment = this

        require(clamp.acLineSegment === this) {
            "${clamp.typeNameAndMRID()} `acLineSegment` property references ${clamp.acLineSegment!!.typeNameAndMRID()}, expected ${typeNameAndMRID()}."
        }
        return false
    }

}

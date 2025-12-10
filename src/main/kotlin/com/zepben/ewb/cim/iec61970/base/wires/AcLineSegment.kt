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
            setter = { _cuts = it })

    /**
     * Get the number of entries in the [Cut] collection.
     */
    fun numCuts(): Int = _cuts?.size ?: 0

    @Deprecated("BOILERPLATE: Use cuts.getByMRID(mRID) instead")
    fun getCut(mRID: String): Cut? = cuts.getByMRID(mRID)

    /**
     * Add a [Cut] to this [AcLineSegment]
     *
     * @return This [AcLineSegment] for fluent use
     */
    fun addCut(cut: Cut): AcLineSegment {
        if (validateCut(cut))
            return this

        _cuts = _cuts ?: mutableListOf()
        _cuts!!.add(cut)

        return this
    }

    /**
     * Remove a [Cut] from this [AcLineSegment]
     *
     * @param cut The [Cut] to remove
     * @return true if [cut] is removed from the collection
     */
    fun removeCut(cut: Cut): Boolean {
        val ret = _cuts.safeRemove(cut)
        if (_cuts.isNullOrEmpty()) _cuts = null
        return ret
    }

    /**
     * Clear all [Cut]'s from this [AcLineSegment]
     *
     * @return This [AcLineSegment] for fluent use
     */
    fun clearCuts(): AcLineSegment {
        _cuts = null
        return this
    }

    val clamps: MRIDListWrapper<Clamp>
        get() = MRIDListWrapper(
            getter = { _clamps },
            setter = { _clamps = it })

    /**
     * Get the number of entries in the [Clamp] collection.
     */
    fun numClamps(): Int = _clamps?.size ?: 0

    @Deprecated("BOILERPLATE: Use clamps.getByMRID(mRID) instead")
    fun getClamp(mRID: String): Clamp? = clamps.getByMRID(mRID)

    /**
     * Add a [Clamp] to this [AcLineSegment]
     *
     * @return This [AcLineSegment] for fluent use
     */
    fun addClamp(clamp: Clamp): AcLineSegment {
        if (validateClamp(clamp))
            return this

        _clamps = _clamps ?: mutableListOf()
        _clamps!!.add(clamp)

        return this
    }

    /**
     * Remove a [Clamp] from this [AcLineSegment]
     *
     * @param clamp The [Clamp] to remove
     * @return true if [clamp] is removed from the collection
     */
    fun removeClamp(clamp: Clamp): Boolean {
        val ret = _clamps.safeRemove(clamp)
        if (_clamps.isNullOrEmpty()) _clamps = null
        return ret
    }

    /**
     * Clear all [Clamp]'s from this [AcLineSegment]
     *
     * @return This [AcLineSegment] for fluent use
     */
    fun clearClamps(): AcLineSegment {
        _clamps = null
        return this
    }

    private fun validateCut(cut: Cut): Boolean {
        if (validateReference(cut, ::getCut, "A Cut"))
            return true

        if (cut.acLineSegment == null)
            cut.acLineSegment = this

        require(cut.acLineSegment === this) {
            "${cut.typeNameAndMRID()} `acLineSegment` property references ${cut.acLineSegment!!.typeNameAndMRID()}, expected ${typeNameAndMRID()}."
        }
        return false
    }

    private fun validateClamp(clamp: Clamp): Boolean {
        if (validateReference(clamp, ::getClamp, "A Clamp"))
            return true

        if (clamp.acLineSegment == null)
            clamp.acLineSegment = this

        require(clamp.acLineSegment === this) {
            "${clamp.typeNameAndMRID()} `acLineSegment` property references ${clamp.acLineSegment!!.typeNameAndMRID()}, expected ${typeNameAndMRID()}."
        }
        return false
    }

}

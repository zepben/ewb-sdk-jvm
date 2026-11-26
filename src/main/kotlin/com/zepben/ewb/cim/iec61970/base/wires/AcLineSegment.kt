/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.wires

import com.zepben.ewb.boilerplate.Backfill
import com.zepben.ewb.boilerplate.collections.LazyMridList
import com.zepben.ewb.boilerplate.collections.interfaces.MridList
import com.zepben.ewb.boilerplate.relations.AcLineSegmentPhaseList
import com.zepben.ewb.cim.iec61968.assetinfo.WireInfo
import com.zepben.ewb.services.common.extensions.getByMRID
import com.zepben.ewb.services.common.extensions.safeRemove
import com.zepben.ewb.services.common.extensions.validateReference

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
 * @property phases The individual phase models for this AcLineSegment.
 */
class AcLineSegment(mRID: String) : Conductor(mRID) {

    override val maxTerminals: Int get() = 2

    var perLengthImpedance: PerLengthImpedance? = null
    private var _cuts: MutableList<Cut>? = null
    private var _clamps: MutableList<Clamp>? = null
    private var _phases: MutableList<AcLineSegmentPhase>? = null

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

    val cuts: MridList<Cut> get() = LazyMridList(
        getter = { _cuts },
        setter = { _cuts = it },
        owner = this,
        elementDescription = "A Cut",
        backfill = Backfill(
            { it.acLineSegment },
            { it, acls -> it.acLineSegment = acls },
            Cut::acLineSegment
        )
    )

    val clamps: MridList<Clamp> get() = LazyMridList(
        getter = { _clamps },
        setter = { _clamps = it },
        owner = this,
        elementDescription = "A Clamp",
        backfill = Backfill(
            { it.acLineSegment },
            { it, acls -> it.acLineSegment = acls },
            Clamp::acLineSegment
        )
    )

    /**
     * The individual phase models for this AcLineSegment. The returned collection is read only.
     */
    val phases: AcLineSegmentPhaseList
        get() = AcLineSegmentPhaseList(
            getter = { _phases },
            setter = { _phases = it },
            owner = this,
            elementDescription = "An AcLineSegmentPhase",
            backfill = Backfill(
                { it.acLineSegment },
                { it, acls -> it.acLineSegment = acls },
                AcLineSegmentPhase::acLineSegment
            ),
            sortBy = { it.sequenceNumber }
        )

    /**
     * Retrieve the WireInfo associated with the requested [phase]. If no specific [WireInfo] is available for the given [phase], [AcLineSegment.assetInfo] will be returned.
     *
     * @param phase the phase to retrieve [WireInfo] for.
     */
    fun wireInfoForPhase(phase: SinglePhaseKind): WireInfo? = phases.find { it.phase == phase }?.assetInfo ?: assetInfo

    // region deprecated list boilerplate
    //
    // ("region/endregion" is an IntelliJ feature letting you hide the entire thing)
    // This boilerplate exists solely to enable backwards compatibility.
    // It will be removed eventually.
    // Every single method simply forwards the call to the corresponding list.

    // region cuts boilerplate

    @Deprecated("Helper for a deprecated function")
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

    /**
     * Get the number of entries in the [Cut] collection.
     */
    @Deprecated(
        message = "Use cuts.size instead.",
        replaceWith = ReplaceWith("cuts.size")
    )
    fun numCuts(): Int = _cuts?.size ?: 0

    /**
     * Get the [Cut] of this [AcLineSegment] represented by [mRID]
     *
     * @param mRID the mRID of the required [Cut]
     * @return The [Cut] with the specified [mRID] if it exists, otherwise null
     */
    @Deprecated(
        message = "Use cuts.getByMRID(mRID) instead.",
        replaceWith = ReplaceWith("cuts.getByMRID(mRID)")
    )
    fun getCut(mRID: String): Cut? = _cuts.getByMRID(mRID)

    /**
     * Add a [Cut] to this [AcLineSegment]
     *
     * @return This [AcLineSegment] for fluent use
     */
    @Deprecated(
        message = "Use cuts.add(cut) instead.",
        replaceWith = ReplaceWith("also { it.cuts.add(cut) }")
    )
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
    @Deprecated(
        message = "Use cuts.remove(cut) instead.",
        replaceWith = ReplaceWith("cuts.remove(cut)")
    )
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
    @Deprecated(
        message = "Use cuts.clear() instead.",
        replaceWith = ReplaceWith("cuts.clear()")
    )
    fun clearCuts(): AcLineSegment {
        _cuts = null
        return this
    }

    // endregion

    // region clamps boilerplate

    @Deprecated("Helper for a deprecated function")
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

    /**
     * Get the number of entries in the [Clamp] collection.
     */
    @Deprecated(
        message = "Use clamps.size instead.",
        replaceWith = ReplaceWith("clamps.size")
    )
    fun numClamps(): Int = _clamps?.size ?: 0

    /**
     * Get the [Clamp] of this [AcLineSegment] represented by [mRID]
     *
     * @param mRID the mRID of the required [Clamp]
     * @return The [Clamp] with the specified [mRID] if it exists, otherwise null
     */
    @Deprecated(
        message = "Use clamps.getByMRID(mRID) instead.",
        replaceWith = ReplaceWith("clamps.getByMRID(mRID)")
    )
    fun getClamp(mRID: String): Clamp? = _clamps.getByMRID(mRID)

    /**
     * Add a [Clamp] to this [AcLineSegment]
     *
     * @return This [AcLineSegment] for fluent use
     */
    @Deprecated(
        message = "Use clamps.add(clamp) instead.",
        replaceWith = ReplaceWith("also { it.clamps.add(clamp) }")
    )
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
    @Deprecated(
        message = "Use clamps.remove(clamp) instead.",
        replaceWith = ReplaceWith("clamps.remove(clamp)")
    )
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
    @Deprecated(
        message = "Use clamps.clear() instead.",
        replaceWith = ReplaceWith("clamps.clear()")
    )
    fun clearClamps(): AcLineSegment {
        _clamps = null
        return this
    }

    // endregion

    // region phases boilerplate

    /**
     * Get the number of entries in the [AcLineSegmentPhase] collection.
     */
    @Deprecated(
        message = "Use phases.size instead.",
        replaceWith = ReplaceWith("phases.size")
    )
    fun numPhases(): Int = _phases?.size ?: 0

    /**
     * The individual phase models for this AcLineSegment.
     *
     * @param mRID the mRID of the required [AcLineSegmentPhase]
     * @return The [AcLineSegmentPhase] with the specified [mRID] if it exists, otherwise null
     */
    @Deprecated(
        message = "Use phases.getByMRID(mRID) instead.",
        replaceWith = ReplaceWith("phases.getByMRID(mRID)")
    )
    fun getPhase(mRID: String): AcLineSegmentPhase? = _phases?.getByMRID(mRID)

    /**
     * The individual phase models for this AcLineSegment.
     *
     * @param phase the phase of the required [AcLineSegmentPhase]
     * @return The [AcLineSegmentPhase] with the specified [phase] if it exists, otherwise null
     */
    @Deprecated(
        message = "Use phases.getByPhase(phase) instead.",
        replaceWith = ReplaceWith("getByPhase(phase)")
    )
    fun getPhase(phase: SinglePhaseKind): AcLineSegmentPhase? = _phases?.find { it.phase == phase }

    /**
     * Add an [AcLineSegmentPhase] to this [AcLineSegment].
     *
     * @param phase The [AcLineSegmentPhase] to add.
     * @return This [AcLineSegment] for fluent use.
     */
    @Deprecated(
        message = "Use phases.add(phase) instead.",
        replaceWith = ReplaceWith("also { it.phases.add(phase) }")
    )
    fun addPhase(phase: AcLineSegmentPhase): AcLineSegment {
        if (validateReference(phase, ::getPhase, "An AcLineSegmentPhase"))
            return this

        if (phase.acLineSegment == null)
            phase.acLineSegment = this

        require(phase.acLineSegment === this) {
            "${phase.typeNameAndMRID()} `acLineSegment` property references ${phase.acLineSegment!!.typeNameAndMRID()}, expected ${typeNameAndMRID()}."
        }

        _phases = _phases ?: mutableListOf()
        _phases!!.add(phase)
        _phases!!.sortBy { it.sequenceNumber }

        return this
    }

    /**
     * Remove an [AcLineSegmentPhase] from this [AcLineSegment].
     *
     * @param phase The [AcLineSegmentPhase] to remove.
     * @return true if [phase] is removed from the collection.
     */
    @Deprecated(
        message = "Use phases.remove(phase) instead.",
        replaceWith = ReplaceWith("phases.remove(phase)")
    )
    fun removePhase(phase: AcLineSegmentPhase): Boolean {
        val ret = _phases?.remove(phase) == true
        if (_phases.isNullOrEmpty()) _phases = null
        return ret
    }

    /**
     * Clear all [AcLineSegmentPhase]'s from this [AcLineSegment].
     *
     * @return This [AcLineSegment] for fluent use.
     */
    @Deprecated(
        message = "Use phases.clear() instead.",
        replaceWith = ReplaceWith("phases.clear()")
    )
    fun clearPhases(): AcLineSegment {
        _phases = null
        return this
    }

    // endregion

    // endregion

}

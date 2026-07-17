/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.wires

import com.zepben.ewb.boilerplate.Backfill
import com.zepben.ewb.boilerplate.LazyMridList
import com.zepben.ewb.boilerplate.MridCollection
import com.zepben.ewb.cim.iec61968.assetinfo.WireInfo

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

    val cuts: MridCollection<Cut> get() = LazyMridList(
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

    val clamps: MridCollection<Clamp> get() = LazyMridList(
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
    val phases: AcLineSegmentPhaseList get() = LazyMridList(
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

    @Deprecated(
        message = "Use cuts.size instead.",
        replaceWith = ReplaceWith("cuts.size")
    )
    fun numCuts(): Int = cuts.size

    @Deprecated(
        message = "Use cuts.getByMRID(mRID) instead.",
        replaceWith = ReplaceWith("cuts.getByMRID(mRID)")
    )
    fun getCut(mRID: String): Cut? = cuts.getByMrid(mRID)

    @Deprecated(
        message = "Use cuts.add(cut) instead.",
        replaceWith = ReplaceWith("also { it.cuts.add(cut) }")
    )
    fun addCut(cut: Cut): AcLineSegment {
        cuts.add(cut)
        return this
    }

    @Deprecated(
        message = "Use cuts.remove(cut) instead.",
        replaceWith = ReplaceWith("cuts.remove(cut)")
    )
    fun removeCut(cut: Cut): Boolean = cuts.remove(cut)

    @Deprecated(
        message = "Use cuts.clear() instead.",
        replaceWith = ReplaceWith("cuts.clear()")
    )
    fun clearCuts(): AcLineSegment {
        cuts.clear()
        return this
    }

    // endregion

    // region clamps boilerplate

    @Deprecated(
        message = "Use clamps.size instead.",
        replaceWith = ReplaceWith("clamps.size")
    )
    fun numClamps(): Int = clamps.size

    @Deprecated(
        message = "Use clamps.getByMRID(mRID) instead.",
        replaceWith = ReplaceWith("clamps.getByMRID(mRID)")
    )
    fun getClamp(mRID: String): Clamp? = clamps.getByMrid(mRID)

    @Deprecated(
        message = "Use clamps.add(clamp) instead.",
        replaceWith = ReplaceWith("also { it.clamps.add(clamp) }")
    )
    fun addClamp(clamp: Clamp): AcLineSegment {
        clamps.add(clamp)
        return this
    }

    @Deprecated(
        message = "Use clamps.remove(clamp) instead.",
        replaceWith = ReplaceWith("clamps.remove(clamp)")
    )
    fun removeClamp(clamp: Clamp): Boolean = clamps.remove(clamp)

    @Deprecated(
        message = "Use clamps.clear() instead.",
        replaceWith = ReplaceWith("clamps.clear()")
    )
    fun clearClamps(): AcLineSegment {
        clamps.clear()
        return this
    }

    // endregion

    // region phases boilerplate

    @Deprecated(
        message = "Use phases.size instead.",
        replaceWith = ReplaceWith("phases.size")
    )
    fun numPhases(): Int = phases.size

    @Deprecated(
        message = "Use phases.getByMRID(mRID) instead.",
        replaceWith = ReplaceWith("phases.getByMRID(mRID)")
    )
    fun getPhase(mRID: String): AcLineSegmentPhase? = phases.getByMrid(mRID)

    @Deprecated(
        message = "Use phases.getByPhase(phase) instead.",
        replaceWith = ReplaceWith("getByPhase(phase)")
    )
    fun getPhase(phase: SinglePhaseKind): AcLineSegmentPhase? = phases.getByPhase(phase)

    @Deprecated(
        message = "Use phases.add(phase) instead.",
        replaceWith = ReplaceWith("also { it.phases.add(phase) }")
    )
    fun addPhase(phase: AcLineSegmentPhase): AcLineSegment {
        phases.add(phase)
        return this
    }

    @Deprecated(
        message = "Use phases.remove(phase) instead.",
        replaceWith = ReplaceWith("phases.remove(phase)")
    )
    fun removePhase(phase: AcLineSegmentPhase): Boolean = phases.remove(phase)

    @Deprecated(
        message = "Use phases.clear() instead.",
        replaceWith = ReplaceWith("phases.clear()")
    )
    fun clearPhases(): AcLineSegment {
        phases.clear()
        return this
    }

    // endregion

    // endregion
}

typealias AcLineSegmentPhaseList = MridCollection<AcLineSegmentPhase>

/**
 * The individual phase models for an AcLineSegment.
 *
 * @param phase the phase of the required [AcLineSegmentPhase]
 * @return The [AcLineSegmentPhase] with the specified [phase] if it exists, otherwise null
 */
fun AcLineSegmentPhaseList.getByPhase(phase: SinglePhaseKind): AcLineSegmentPhase? = firstOrNull { it.phase == phase }

/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.boilerplate.relations

import com.zepben.ewb.boilerplate.Backfill
import com.zepben.ewb.boilerplate.collections.LazyMridList
import com.zepben.ewb.cim.iec61970.base.wires.AcLineSegment
import com.zepben.ewb.cim.iec61970.base.wires.AcLineSegmentPhase
import com.zepben.ewb.cim.iec61970.base.wires.SinglePhaseKind

/** A list of [AcLineSegmentPhase] for a given [AcLineSegment]. */
class AcLineSegmentPhaseList(
    getter: () -> MutableList<AcLineSegmentPhase>?,
    setter: (MutableList<AcLineSegmentPhase>?) -> Unit,
    owner: AcLineSegment,
    elementDescription: String,
    backfill: Backfill<AcLineSegmentPhase, AcLineSegment>? = null,
    validate: ((AcLineSegmentPhase) -> Unit)? = null,
    sortBy: ((AcLineSegmentPhase) -> Comparable<*>?)? = null
) : LazyMridList<AcLineSegmentPhase, AcLineSegment>(
    getter = getter,
    setter = setter,
    owner = owner,
    elementDescription = elementDescription,
    backfill = backfill,
    validate = validate,
    sortBy = sortBy,
) {

    /**
     * The individual phase models for an AcLineSegment.
     *
     * @param phase the phase of the required [AcLineSegmentPhase]
     * @return The [AcLineSegmentPhase] with the specified [phase] if it exists, otherwise null
     */
    fun getByPhase(
        phase: SinglePhaseKind,
    ): AcLineSegmentPhase? =
        firstOrNull { it.phase == phase }
}

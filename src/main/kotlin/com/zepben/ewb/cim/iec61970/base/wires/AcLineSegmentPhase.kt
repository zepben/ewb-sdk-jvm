/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.wires

import com.zepben.ewb.cim.iec61968.assetinfo.WireInfo
import com.zepben.ewb.cim.iec61970.base.core.PowerSystemResource

/**
 * Represents a single wire of an alternating current line segment.
 *
 * @property phase The phase connection of the wire at both ends.
 * @property sequenceNumber Number designation for this line segment phase. Each line segment phase within a line segment should have a unique sequence number. This is useful for unbalanced modelling to bind the mathematical model (PhaseImpedanceData of PerLengthPhaseImpedance) with the connectivity model (this class) and the physical model (WirePosition) without tight coupling.
 * @property acLineSegment The line segment to which the phase belongs.
 * @property assetInfo The wire info for this phase of the AcLineSegment
 */
class AcLineSegmentPhase(mRID: String) : PowerSystemResource(mRID) {


    var phase: SinglePhaseKind = SinglePhaseKind.X

    var sequenceNumber: Int? = null

    var acLineSegment: AcLineSegment? = null
        set(value) {
            field =
                if (field == null || field === value) value else throw IllegalStateException("acLineSegment has already been set to $field. Cannot set this field again")
        }

    override var assetInfo: WireInfo? = null

}

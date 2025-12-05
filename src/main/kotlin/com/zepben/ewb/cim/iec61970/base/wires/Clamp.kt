/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.wires

import com.zepben.ewb.cim.iec61970.base.core.ConductingEquipment

/**
 * A Clamp is a galvanic connection at a line segment where other equipment is connected. A Clamp does not cut the line segment. A Clamp is ConductingEquipment
 * and has one Terminal with an associated ConnectivityNode. Any other ConductingEquipment can be connected to the Clamp ConnectivityNode.
 *
 * @property lengthFromTerminal1 The length to the place where the clamp is located starting from side one of the line segment, i.e. the line segment terminal
 * with sequence number equal to 1.
 * @property acLineSegment The line segment to which the clamp is connected.
 */
class Clamp(mRID: String) : ConductingEquipment(mRID) {

    var lengthFromTerminal1: Double? = null
    var acLineSegment: AcLineSegment? = null

    override val maxTerminals: Int get() = 1

}

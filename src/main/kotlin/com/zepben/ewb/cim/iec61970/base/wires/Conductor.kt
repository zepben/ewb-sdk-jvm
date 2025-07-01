/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.wires

import com.zepben.ewb.cim.extensions.ZBEX
import com.zepben.ewb.cim.iec61968.assetinfo.CableInfo
import com.zepben.ewb.cim.iec61968.assetinfo.WireInfo
import com.zepben.ewb.cim.iec61968.assets.AssetInfo
import com.zepben.ewb.cim.iec61970.base.core.ConductingEquipment

/**
 * Combination of conducting material with consistent electrical characteristics, building a single electrical system, used to carry current
 * between points in the power system.
 *
 * @property length Segment length for calculating line section capabilities.
 * @property designTemperature [ZBEX] The temperature in degrees Celsius for the network design of this conductor.
 * @property designRating [ZBEX] The current rating in Amperes at the specified design temperature that can be used without the conductor breaching physical network
 *   design limits.
 */
abstract class Conductor(mRID: String = "") : ConductingEquipment(mRID) {
    var length: Double? = null
        set(value) {
            require((value == null) || (value >= 0) || value.isNaN()) { "Conductor length cannot be negative." }
            field = value
        }

    @ZBEX
    var designTemperature: Int? = null

    @ZBEX
    var designRating: Double? = null

    /**
     * Override the [AssetInfo] as [WireInfo].
     */
    override var assetInfo: WireInfo? = null

    /**
     * @return Convenience function to check if the [WireInfo] is [CableInfo].
     */
    val isUnderground: Boolean get() = assetInfo is CableInfo
}

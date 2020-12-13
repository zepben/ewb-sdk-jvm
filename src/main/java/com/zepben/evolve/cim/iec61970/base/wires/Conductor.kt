/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.cim.iec61970.base.wires

import com.zepben.evolve.cim.iec61968.assetinfo.CableInfo
import com.zepben.evolve.cim.iec61968.assetinfo.WireInfo
import com.zepben.evolve.cim.iec61968.assets.AssetInfo
import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import java.lang.Double.isNaN

/**
 * Combination of conducting material with consistent electrical characteristics, building a single electrical system, used to carry current
 * between points in the power system.
 *
 * @property length Segment length for calculating line section capabilities.
 */
abstract class Conductor(mRID: String = "") : ConductingEquipment(mRID) {

    var length: Double = 0.0
        set(value) {
            require((value >= 0) || isNaN(value)) { "Conductor length cannot be negative." }
            field = value
        }

    /**
     * Override the [AssetInfo] as [WireInfo].
     */
    override var assetInfo: WireInfo? = null

    /**
     * @return Convenience function to check if the [WireInfo] is [CableInfo].
     */
    val isUnderground: Boolean get() = assetInfo is CableInfo
}

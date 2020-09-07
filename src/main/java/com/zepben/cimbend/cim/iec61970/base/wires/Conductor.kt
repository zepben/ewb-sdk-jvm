/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 * This file is part of evolve-sdk-jvm.
 *
 * evolve-sdk-jvm is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * evolve-sdk-jvm is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with evolve-sdk-jvm.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.zepben.cimbend.cim.iec61970.base.wires

import com.zepben.cimbend.cim.iec61968.assetinfo.CableInfo
import com.zepben.cimbend.cim.iec61968.assetinfo.WireInfo
import com.zepben.cimbend.cim.iec61968.assets.AssetInfo
import com.zepben.cimbend.cim.iec61970.base.core.ConductingEquipment
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

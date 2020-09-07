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
package com.zepben.cimbend.cim.iec61968.assetinfo

import com.zepben.cimbend.cim.iec61968.assets.AssetInfo

/**
 * Wire data that can be specified per line segment phase, or for the line segment as a whole in case its phases all have the same wire characteristics.
 *
 * @property ratedCurrent Current carrying capacity of the wire under stated thermal conditions.
 * @property material Conductor material
 */
abstract class WireInfo(mRID: String = "") : AssetInfo(mRID) {

    var ratedCurrent: Int = 0
    var material: WireMaterialKind = WireMaterialKind.UNKNOWN
}

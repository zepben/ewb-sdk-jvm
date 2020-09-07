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
package com.zepben.cimbend.cim.iec61970.base.core

import com.zepben.cimbend.cim.iec61968.assets.AssetInfo
import com.zepben.cimbend.cim.iec61968.common.Location

/**
 *  Abstract class, should only be used through subclasses.
 *  A power system resource can be an item of equipment such as a switch, an equipment container containing many individual
 *  items of equipment such as a substation, or an organisational entity such as sub-control area. Power system resources
 *  can have measurements associated.
 *
 * @property assetInfo Datasheet information for this power system resource.
 * @property location Location of this power system resource.
 * @property numControls Number of Control's known to associate with this [PowerSystemResource]
 */
abstract class PowerSystemResource(mRID: String = "") : IdentifiedObject(mRID) {

    open val assetInfo: AssetInfo? get() = null
    var location: Location? = null
    var numControls: Int = 0

    /**
     * @return True if this [PowerSystemResource] has at least 1 Control associated with it, false otherwise.
     */
    fun hasControls() = numControls > 0

}


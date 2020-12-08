/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.cim.iec61970.base.core

import com.zepben.evolve.cim.iec61968.assets.AssetInfo
import com.zepben.evolve.cim.iec61968.common.Location

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


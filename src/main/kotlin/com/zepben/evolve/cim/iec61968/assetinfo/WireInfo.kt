/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.cim.iec61968.assetinfo

import com.zepben.evolve.cim.iec61968.assets.AssetInfo

/**
 * Wire data that can be specified per line segment phase, or for the line segment as a whole in case its phases all have the same wire characteristics.
 *
 * @property ratedCurrent Current carrying capacity of the wire under stated thermal conditions.
 * @property material Conductor material
 */
abstract class WireInfo(mRID: String = "") : AssetInfo(mRID) {

    var ratedCurrent: Int? = null
    var material: WireMaterialKind = WireMaterialKind.UNKNOWN
}

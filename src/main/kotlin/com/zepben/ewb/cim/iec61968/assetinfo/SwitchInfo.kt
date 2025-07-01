/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61968.assetinfo

import com.zepben.ewb.cim.iec61968.assets.AssetInfo

/**
 * Switch datasheet information.
 *
 * @property ratedInterruptingTime Switch rated interrupting time in seconds.
 */
class SwitchInfo @JvmOverloads constructor(mRID: String = "") : AssetInfo(mRID) {

    var ratedInterruptingTime: Double? = null

}

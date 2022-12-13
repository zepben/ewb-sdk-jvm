/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61968.infiec61968.infassetinfo

import com.zepben.evolve.cim.iec61968.assets.AssetInfo

/**
 * Current Relay Datasheet Information.
 *
 * @property curveSetting The type of curve used for the Current Relay.
 */
class CurrentRelayInfo(mRID: String = "") : AssetInfo(mRID) {

    var curveSetting: String? = null

}

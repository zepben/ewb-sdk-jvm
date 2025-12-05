/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61968.metering

import com.zepben.ewb.cim.iec61968.assets.AssetFunction

/**
 * Function performed by an end device such as a meter, communication equipment, controllers, etc.
 *
 *  @property enabled True if the function is enabled.
 */
abstract class EndDeviceFunction(mRID: String) : AssetFunction(mRID) {

    var enabled: Boolean? = null

}

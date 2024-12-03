/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61968.metering

import com.zepben.evolve.cim.iec61968.assets.AssetFunction

/**
 * Function performed by an end device such as a meter, communication equipment, controllers, etc.
 *
 *  @property endDevice The EndDevice to which this EndDeviceFunction take place.
 *  @property enabled True if the function is enabled.
 */
abstract class EndDeviceFunction(mRID: String = "") : AssetFunction(mRID) {

    var endDevice: EndDevice? = null
    var enabled: Boolean? = true

}

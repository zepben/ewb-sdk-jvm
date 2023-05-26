/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61968.assetinfo

import com.zepben.evolve.cim.iec61968.assets.AssetInfo

/**
 * Properties of shunt capacitor, shunt reactor or switchable bank of shunt capacitor or reactor assets.
 *
 * @property maxPowerLoss Maximum allowed apparent power loss in watts.
 * @property ratedCurrent Rated current in amperes.
 * @property ratedReactivePower Rated reactive power in volt-amperes reactive.
 * @property ratedVoltage Rated voltage in volts.
 */
class ShuntCompensatorInfo(mRID: String = "") : AssetInfo(mRID) {

    var maxPowerLoss: Int? = null
    var ratedCurrent: Int? = null
    var ratedReactivePower: Int? = null
    var ratedVoltage: Int? = null

}

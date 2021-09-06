/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61968.assetinfo

import com.zepben.evolve.cim.iec61968.assets.AssetInfo
import com.zepben.evolve.cim.iec61970.base.wires.TransformerStarImpedance
import com.zepben.evolve.cim.iec61970.base.wires.WindingConnection
import com.zepben.evolve.services.network.ResistanceReactance
import com.zepben.evolve.services.network.mergeIfIncomplete
import kotlin.math.round
import kotlin.math.sqrt

/**
 * Properties of shunt capacitor, shunt reactor or switchable bank of shunt capacitor or reactor assets.
 *
 * @property maxPowerLoss Maximum allowed apparent power loss.
 * @property ratedCurrent Rated current.
 * @property ratedReactivePower Rated reactive power.
 * @property ratedVoltage Rated voltage.
 */
class ShuntCompensatorInfo(mRID: String = "") : AssetInfo(mRID) {

    var maxPowerLoss: Int? = null
    var ratedCurrent: Int? = null
    var ratedReactivePower: Int? = null
    var ratedVoltage: Int? = null

}

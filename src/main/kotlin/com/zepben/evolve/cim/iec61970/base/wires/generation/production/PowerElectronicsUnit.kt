/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.wires.generation.production

import com.zepben.evolve.cim.iec61970.base.core.Equipment
import com.zepben.evolve.cim.iec61970.base.wires.PowerElectronicsConnection

/**
 * A generating unit or battery or aggregation that connects to the AC network using power electronics rather than rotating machines.
 *
 * @property powerElectronicsConnection An AC network connection may have several power electronics units connecting through it.
 * @property maxP Maximum active power limit. This is the maximum (nameplate) limit for the unit.
 * @property minP Minimum active power limit. This is the minimum (nameplate) limit for the unit.
 */
abstract class PowerElectronicsUnit(mRID: String = "") : Equipment(mRID) {

    var powerElectronicsConnection: PowerElectronicsConnection? = null
        set(value) {
            field =
                if (field == null || field === value) value else throw IllegalStateException("powerElectronicsConnection has already been set to $field. Cannot set this field again")
        }

    var maxP: Int? = null
    var minP: Int? = null
}

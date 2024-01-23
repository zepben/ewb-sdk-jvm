/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.protection

import com.zepben.evolve.cim.iec61970.base.core.IdentifiedObject
import com.zepben.evolve.cim.iec61970.base.domain.UnitSymbol

/**
 * The threshold settings for a given relay.
 *
 * @property unitSymbol The unit of the value.
 * @property value The value of the setting, e.g voltage, current, etc.
 */
class RelaySetting(mRID: String = "") : IdentifiedObject(mRID) {

    var unitSymbol: UnitSymbol = UnitSymbol.NONE
    var value: Double = 0.0

}

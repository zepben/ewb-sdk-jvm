/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.wires.generation.production

/**
 * An electrochemical energy storage device.
 *
 * @property batteryState The current state of the battery (charging, full, etc.).
 * @property ratedE Full energy storage capacity of the battery in watt hours (Wh). The attribute shall be a positive value.
 * @property storedE Amount of energy currently stored in watt hours (Wh). The attribute shall be a positive value or zero and lower than [BatteryUnit.ratedE].
 */
class BatteryUnit @JvmOverloads constructor(mRID: String = "") : PowerElectronicsUnit(mRID) {

    var batteryState: BatteryStateKind = BatteryStateKind.UNKNOWN
    var ratedE: Long? = null
    var storedE: Long? = null
}

/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.extensions.iec61970.base.wires

import com.zepben.evolve.cim.extensions.ZBEX
import com.zepben.evolve.cim.iec61970.base.wires.RegulatingControl

/**
 * Describes behaviour specific to controlling batteries.
 *
 * @property chargingRate Charging rate (input power) in percentage of maxP. (Unit: PerCent)
 * @property dischargingRate Discharge rate (output power) in percentage of maxP. (Unit: PerCent)
 * @property reservePercent Percentage of the rated storage capacity that should be reserved during normal operations. This reserve acts as a safeguard, preventing the energy level
 * @property controlMode Mode of operation for the dispatch (charging/discharging) function of BatteryControl.
 */
@ZBEX
class BatteryControl @JvmOverloads constructor(mRID: String = "") : RegulatingControl(mRID) {

    var chargingRate: Double? = null
    var dischargingRate: Double? = null
    var reservePercent: Double? = null
    var controlMode: BatteryControlMode = BatteryControlMode.UNKNOWN

}

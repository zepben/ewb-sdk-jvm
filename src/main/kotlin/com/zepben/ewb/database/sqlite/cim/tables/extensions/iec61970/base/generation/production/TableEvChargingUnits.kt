/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.tables.extensions.iec61970.base.generation.production

import com.zepben.ewb.database.sqlite.cim.tables.iec61970.base.generation.production.TablePowerElectronicsUnits

class TableEvChargingUnits : TablePowerElectronicsUnits() {

    override val name: String = "ev_charging_units"

}

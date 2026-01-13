/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.extensions.iec61970.base.generation.production

import com.zepben.ewb.database.sql.cim.tables.iec61970.base.generation.production.TablePowerElectronicsUnits

/**
 * A class representing the `EvChargingUnit` columns required for the database table.
 */
class TableEvChargingUnits : TablePowerElectronicsUnits() {

    override val name: String = "ev_charging_units"

}

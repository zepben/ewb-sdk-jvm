/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.iec61970.base.generation.production

import com.zepben.ewb.database.sql.cim.tables.iec61970.base.core.TableEquipment
import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NULL

/**
 * A class representing the `PowerElectronicsUnit` columns required for the database table.
 *
 * @property POWER_ELECTRONICS_CONNECTION_MRID An AC network connection may have several power electronics units connecting through it.
 * @property MAX_P Maximum active power limit. This is the maximum (nameplate) limit for the unit.
 * @property MIN_P Minimum active power limit. This is the minimum (nameplate) limit for the unit.
 */
@Suppress("PropertyName")
abstract class TablePowerElectronicsUnits : TableEquipment() {

    val POWER_ELECTRONICS_CONNECTION_MRID: Column = Column(++columnIndex, "power_electronics_connection_mrid", Column.Type.STRING, NULL)
    val MAX_P: Column = Column(++columnIndex, "max_p", Column.Type.INTEGER, NULL)
    val MIN_P: Column = Column(++columnIndex, "min_p", Column.Type.INTEGER, NULL)

    init {
        addNonUniqueIndexes(
            listOf(POWER_ELECTRONICS_CONNECTION_MRID)
        )
    }

}

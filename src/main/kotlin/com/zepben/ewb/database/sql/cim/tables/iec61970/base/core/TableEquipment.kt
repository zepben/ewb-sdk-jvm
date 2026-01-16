/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.iec61970.base.core

import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NULL

/**
 * A class representing the `Equipment` columns required for the database table.
 *
 * @property IN_SERVICE If true, the equipment is in service.
 * @property NORMALLY_IN_SERVICE If true, the equipment is _normally_ in service.
 * @property COMMISSIONED_DATE The date this equipment was commissioned into service.
 */
@Suppress("PropertyName")
abstract class TableEquipment : TablePowerSystemResources() {

    val NORMALLY_IN_SERVICE: Column = Column(++columnIndex, "normally_in_service", Column.Type.BOOLEAN, NULL)
    val IN_SERVICE: Column = Column(++columnIndex, "in_service", Column.Type.BOOLEAN, NULL)
    val COMMISSIONED_DATE: Column = Column(++columnIndex, "commissioned_date", Column.Type.STRING, NULL)

}

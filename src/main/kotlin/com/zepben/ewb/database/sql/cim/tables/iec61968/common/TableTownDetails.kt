/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.iec61968.common

import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NULL
import com.zepben.ewb.database.sql.common.tables.Column.Type.STRING
import com.zepben.ewb.database.sql.common.tables.SqlTable

/**
 * A class representing the TownDetail columns required for the database table.
 *
 * @property TOWN_NAME The town name.
 * @property STATE_OR_PROVINCE The name of the state or province.
 * @property COUNTRY The name of the country.
 */
@Suppress("PropertyName")
abstract class TableTownDetails : SqlTable() {

    val TOWN_NAME: Column = Column(++columnIndex, "town_name", STRING, NULL)
    val STATE_OR_PROVINCE: Column = Column(++columnIndex, "state_or_province", STRING, NULL)
    val COUNTRY: Column = Column(++columnIndex, "country", STRING, NULL)

}

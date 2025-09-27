/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.tables.iec61968.common

import com.zepben.ewb.database.sql.Column
import com.zepben.ewb.database.sql.Column.Nullable.NULL
import com.zepben.ewb.database.sql.Column.Type.BOOLEAN
import com.zepben.ewb.database.sql.Column.Type.STRING
import com.zepben.ewb.database.sqlite.common.SqliteTable

/**
 * A class representing the TelephoneNumber columns required for the database table.
 *
 * @property AREA_CODE A column storing the area or region code.
 * @property CITY_CODE A column storing the city code.
 * @property COUNTRY_CODE A column storing the country code.
 * @property DIAL_OUT A column storing the dial out code, for instance to call outside an enterprise.
 * @property EXTENSION A column storing the extension for this telephone number.
 * @property INTERNATIONAL_PREFIX A column storing the prefix used when calling an international number.
 * @property LOCAL_NUMBER A column storing the main (local) part of this telephone number.
 * @property IS_PRIMARY A column storing indicating if this phone number is the primary number.
 * @property DESCRIPTION A column storing the description for phone number, e.g: home, work, mobile.
 */
@Suppress("PropertyName")
abstract class TableTelephoneNumbers : SqliteTable() {

    val AREA_CODE: Column = Column(++columnIndex, "area_code", STRING, NULL)
    val CITY_CODE: Column = Column(++columnIndex, "city_code", STRING, NULL)
    val COUNTRY_CODE: Column = Column(++columnIndex, "country_code", STRING, NULL)
    val DIAL_OUT: Column = Column(++columnIndex, "dial_out", STRING, NULL)
    val EXTENSION: Column = Column(++columnIndex, "extension", STRING, NULL)
    val INTERNATIONAL_PREFIX: Column = Column(++columnIndex, "international_prefix", STRING, NULL)
    val LOCAL_NUMBER: Column = Column(++columnIndex, "local_number", STRING, NULL)
    val IS_PRIMARY: Column = Column(++columnIndex, "is_primary", BOOLEAN, NULL)
    val DESCRIPTION: Column = Column(++columnIndex, "description", STRING, NULL)

}

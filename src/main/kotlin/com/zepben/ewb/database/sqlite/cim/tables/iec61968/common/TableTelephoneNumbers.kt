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
import com.zepben.ewb.database.sqlite.common.SqliteTable

@Suppress("PropertyName")
abstract class TableTelephoneNumbers : SqliteTable() {

    val AREA_CODE: Column = Column(++columnIndex, "area_code", "TEXT", NULL)
    val CITY_CODE: Column = Column(++columnIndex, "city_code", "TEXT", NULL)
    val COUNTRY_CODE: Column = Column(++columnIndex, "country_code", "TEXT", NULL)
    val DIAL_OUT: Column = Column(++columnIndex, "dial_out", "TEXT", NULL)
    val EXTENSION: Column = Column(++columnIndex, "extension", "TEXT", NULL)
    val INTERNATIONAL_PREFIX: Column = Column(++columnIndex, "international_prefix", "TEXT", NULL)
    val LOCAL_NUMBER: Column = Column(++columnIndex, "local_number", "TEXT", NULL)
    val IS_PRIMARY: Column = Column(++columnIndex, "is_primary", "BOOLEAN", NULL)
    val DESCRIPTION: Column = Column(++columnIndex, "description", "TEXT", NULL)

    override val name: String = "telephone_numbers"

}
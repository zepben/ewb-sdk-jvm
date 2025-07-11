/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.tables.iec61968.common

import com.zepben.ewb.database.sql.Column
import com.zepben.ewb.database.sql.Column.Nullable.*
import com.zepben.ewb.database.sqlite.common.SqliteTable

@Suppress("PropertyName")
abstract class TableElectronicAddresses : SqliteTable() {

    val EMAIL_1: Column = Column(++columnIndex, "email_1", "TEXT", NULL)
    val IS_PRIMARY: Column = Column(++columnIndex, "is_primary", "BOOLEAN", NOT_NULL)
    val DESCRIPTION: Column = Column(++columnIndex, "description", "TEXT", NULL)

    override val name: String = "electronic_addresses"

}
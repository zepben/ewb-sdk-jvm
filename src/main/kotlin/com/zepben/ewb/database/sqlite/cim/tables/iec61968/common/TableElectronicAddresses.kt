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
 * A class representing the ElectronicAddress columns required for the database table.
 *
 * @property EMAIL_1 A column storing the primary email address.
 * @property IS_PRIMARY A column storing whether this email is the primary email address of the contact.
 * @property DESCRIPTION A column storing a description for this email, e.g: work, personal.
 */
@Suppress("PropertyName")
abstract class TableElectronicAddresses : SqliteTable() {

    val EMAIL_1: Column = Column(++columnIndex, "email_1", STRING, NULL)
    val IS_PRIMARY: Column = Column(++columnIndex, "is_primary", BOOLEAN, NULL)
    val DESCRIPTION: Column = Column(++columnIndex, "description", STRING, NULL)

}

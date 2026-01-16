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
import com.zepben.ewb.database.sql.common.tables.Column.Type.BOOLEAN
import com.zepben.ewb.database.sql.common.tables.Column.Type.STRING
import com.zepben.ewb.database.sql.common.tables.SqlTable

/**
 * A class representing the ElectronicAddress columns required for the database table.
 *
 * @property EMAIL_1 The primary email address.
 * @property IS_PRIMARY Whether this email is the primary email address of the contact.
 * @property DESCRIPTION A description for this email, e.g: work, personal.
 */
@Suppress("PropertyName")
abstract class TableElectronicAddresses : SqlTable() {

    val EMAIL_1: Column = Column(++columnIndex, "email_1", STRING, NULL)
    val IS_PRIMARY: Column = Column(++columnIndex, "is_primary", BOOLEAN, NULL)
    val DESCRIPTION: Column = Column(++columnIndex, "description", STRING, NULL)

}

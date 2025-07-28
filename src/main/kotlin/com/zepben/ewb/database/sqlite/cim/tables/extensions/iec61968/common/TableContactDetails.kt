/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.tables.extensions.iec61968.common

import com.zepben.ewb.database.sql.Column
import com.zepben.ewb.database.sql.Column.Nullable.NULL
import com.zepben.ewb.database.sqlite.common.SqliteTable

@Suppress("PropertyName")
abstract class TableContactDetails : SqliteTable() {

    val ID: Column = Column(++columnIndex, "id", "TEXT", NULL)
    val CONTACT_TYPE: Column = Column(++columnIndex, "contact_type", "TEXT", NULL)
    val FIRST_NAME: Column = Column(++columnIndex, "first_name", "TEXT", NULL)
    val LAST_NAME: Column = Column(++columnIndex, "last_name", "TEXT", NULL)
    val PREFERRED_CONTACT_METHOD: Column = Column(++columnIndex, "preferred_contact_method", "TEXT", NULL)
    val IS_PRIMARY: Column = Column(++columnIndex, "is_primary", "TEXT", NULL)
    val BUSINESS_NAME: Column = Column(++columnIndex, "business_name", "TEXT", NULL)

}

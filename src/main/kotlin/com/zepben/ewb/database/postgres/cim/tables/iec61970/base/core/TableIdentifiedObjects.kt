/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.postgres.cim.tables.iec61970.base.core

import com.zepben.ewb.database.postgres.common.PostgresTable
import com.zepben.ewb.database.sql.Column
import com.zepben.ewb.database.sql.Column.Nullable.NOT_NULL
import com.zepben.ewb.database.sql.Column.Nullable.NULL

// TODO: This is straight up copy pasta, with a delete, as some of the classes going into PG inherit from IO... not sure if this is right,
//  but the alternative i saw was messing with where IO was inheriting and stuff...
@Suppress("PropertyName")
abstract class TableIdentifiedObjects : PostgresTable() {

    val MRID: Column = Column(++columnIndex, "mrid", Column.Type.STRING, NOT_NULL)
    val NAME: Column = Column(++columnIndex, "name", Column.Type.STRING, NULL)
    val DESCRIPTION: Column = Column(++columnIndex, "description", Column.Type.STRING, NULL)

    init {
        addUniqueIndexes(
            listOf(MRID)
        )

        addNonUniqueIndexes(
            listOf(NAME)
        )
    }

}

/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.tables.associations

import com.zepben.ewb.database.sql.Column
import com.zepben.ewb.database.sql.Column.Nullable.NOT_NULL
import com.zepben.ewb.database.sqlite.common.SqliteTable

/**
 * A class representing the association between Equipment and OperationalRestrictions.
 *
 * @property EQUIPMENT_MRID A column storing the mRID of Equipment.
 * @property OPERATIONAL_RESTRICTION_MRID A column storing the mRID of OperationalRestrictions.
 */
@Suppress("PropertyName")
class TableEquipmentOperationalRestrictions : SqliteTable() {

    val EQUIPMENT_MRID: Column = Column(++columnIndex, "equipment_mrid", "TEXT", NOT_NULL)
    val OPERATIONAL_RESTRICTION_MRID: Column = Column(++columnIndex, "operational_restriction_mrid", "TEXT", NOT_NULL)

    override val name: String = "equipment_operational_restrictions"

    override val uniqueIndexColumns: MutableList<List<Column>> =
        super.uniqueIndexColumns.apply {
            add(listOf(EQUIPMENT_MRID, OPERATIONAL_RESTRICTION_MRID))
        }

    override val nonUniqueIndexColumns: MutableList<List<Column>> =
        super.nonUniqueIndexColumns.apply {
            add(listOf(EQUIPMENT_MRID))
            add(listOf(OPERATIONAL_RESTRICTION_MRID))
        }

}

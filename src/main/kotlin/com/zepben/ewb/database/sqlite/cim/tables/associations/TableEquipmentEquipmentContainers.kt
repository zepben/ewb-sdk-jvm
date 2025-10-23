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
 * A class representing the association between Equipment and EquipmentContainers.
 *
 * @property EQUIPMENT_MRID A column storing the mRID of Equipment.
 * @property EQUIPMENT_CONTAINER_MRID A column storing the mRID of EquipmentContainers.
 */
@Suppress("PropertyName")
class TableEquipmentEquipmentContainers : SqliteTable() {

    val EQUIPMENT_MRID: Column = Column(++columnIndex, "equipment_mrid", Column.Type.STRING, NOT_NULL)
    val EQUIPMENT_CONTAINER_MRID: Column = Column(++columnIndex, "equipment_container_mrid", Column.Type.STRING, NOT_NULL)

    override val name: String = "equipment_equipment_containers"

    init {
        addUniqueIndexes(
            listOf(EQUIPMENT_MRID, EQUIPMENT_CONTAINER_MRID)
        )

        addNonUniqueIndexes(
            listOf(EQUIPMENT_MRID),
            listOf(EQUIPMENT_CONTAINER_MRID)
        )
    }

}

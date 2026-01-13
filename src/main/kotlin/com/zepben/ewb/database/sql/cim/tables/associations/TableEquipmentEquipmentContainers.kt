/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.associations

import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NOT_NULL
import com.zepben.ewb.database.sql.common.tables.SqlTable

/**
 * A class representing the association between Equipment and EquipmentContainers.
 *
 * @property EQUIPMENT_MRID The mRID of Equipment.
 * @property EQUIPMENT_CONTAINER_MRID The mRID of EquipmentContainers.
 */
@Suppress("PropertyName")
class TableEquipmentEquipmentContainers : SqlTable() {

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

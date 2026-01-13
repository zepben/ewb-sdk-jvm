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
 * A class representing the association between Equipment and OperationalRestrictions.
 *
 * @property EQUIPMENT_MRID The mRID of Equipment.
 * @property OPERATIONAL_RESTRICTION_MRID The mRID of OperationalRestrictions.
 */
@Suppress("PropertyName")
class TableEquipmentOperationalRestrictions : SqlTable() {

    val EQUIPMENT_MRID: Column = Column(++columnIndex, "equipment_mrid", Column.Type.STRING, NOT_NULL)
    val OPERATIONAL_RESTRICTION_MRID: Column = Column(++columnIndex, "operational_restriction_mrid", Column.Type.STRING, NOT_NULL)

    override val name: String = "equipment_operational_restrictions"

    init {
        addUniqueIndexes(
            listOf(EQUIPMENT_MRID, OPERATIONAL_RESTRICTION_MRID)
        )

        addNonUniqueIndexes(
            listOf(EQUIPMENT_MRID),
            listOf(OPERATIONAL_RESTRICTION_MRID)
        )
    }

}

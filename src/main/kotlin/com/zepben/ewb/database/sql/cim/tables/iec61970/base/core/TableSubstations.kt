/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.iec61970.base.core

import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NULL

/**
 * A class representing the `Substation` columns required for the database table.
 *
 * @property SUB_GEOGRAPHICAL_REGION_MRID The SubGeographicalRegion containing the substation.
 */
@Suppress("PropertyName")
class TableSubstations : TableEquipmentContainers() {

    val SUB_GEOGRAPHICAL_REGION_MRID: Column = Column(++columnIndex, "sub_geographical_region_mrid", Column.Type.STRING, NULL)

    override val name: String = "substations"

    init {
        addNonUniqueIndexes(
            listOf(SUB_GEOGRAPHICAL_REGION_MRID)
        )
    }

}

/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.extensions.iec61970.base.wires

import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NOT_NULL
import com.zepben.ewb.database.sql.common.tables.SqlTable

/**
 * A class representing the `PowerTransformerEnd.RatedS` columns required for the database table.
 *
 * @property POWER_TRANSFORMER_END_MRID The PowerTransformerEnd this rating applies to
 * @property COOLING_TYPE The cooling type for this rating.
 * @property RATED_S The normal apparent power rating for this cooling type.
 */
@Suppress("PropertyName")
class TablePowerTransformerEndRatings : SqlTable() {

    val POWER_TRANSFORMER_END_MRID: Column = Column(++columnIndex, "power_transformer_end_mrid", Column.Type.STRING, NOT_NULL)
    val COOLING_TYPE: Column = Column(++columnIndex, "cooling_type", Column.Type.STRING, NOT_NULL)
    val RATED_S: Column = Column(++columnIndex, "rated_s", Column.Type.INTEGER, NOT_NULL)

    override val name: String = "power_transformer_end_ratings"

    init {
        addUniqueIndexes(
            listOf(POWER_TRANSFORMER_END_MRID, COOLING_TYPE)
        )

        addNonUniqueIndexes(
            listOf(POWER_TRANSFORMER_END_MRID)
        )
    }

}

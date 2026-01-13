/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.extensions.iec61968.assetinfo

import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NOT_NULL
import com.zepben.ewb.database.sql.common.tables.SqlTable

/**
 * A class representing the `RelayInfo.RecloseDelays` columns required for the database table.
 *
 * @property RELAY_INFO_MRID The relay info for the delay.
 * @property RECLOSE_DELAY The reclose delays for this curve and relay type.
 * @property SEQUENCE_NUMBER The reclose step, and the value is the overall delay time.
 */
@Suppress("PropertyName")
class TableRecloseDelays : SqlTable() {

    val RELAY_INFO_MRID: Column = Column(++columnIndex, "relay_info_mrid", Column.Type.STRING, NOT_NULL)
    val RECLOSE_DELAY: Column = Column(++columnIndex, "reclose_delay", Column.Type.DOUBLE, NOT_NULL)
    val SEQUENCE_NUMBER: Column = Column(++columnIndex, "sequence_number", Column.Type.INTEGER, NOT_NULL)

    override val name: String = "reclose_delays"

    override val selectSql: String =
        "${super.selectSql} ORDER BY relay_info_mrid, sequence_number ASC;"

    init {
        addUniqueIndexes(
            listOf(RELAY_INFO_MRID, SEQUENCE_NUMBER)
        )

        addNonUniqueIndexes(
            listOf(RELAY_INFO_MRID)
        )
    }

}

/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.tables.iec61968.metering

import com.zepben.ewb.database.sql.Column
import com.zepben.ewb.database.sql.Column.Nullable.NOT_NULL
import com.zepben.ewb.database.sqlite.cim.tables.extensions.iec61968.common.TableContactDetails

@Suppress("PropertyName")
class TableUsagePointContactDetails : TableContactDetails() {

    val USAGE_POINT_MRID: Column = Column(++columnIndex, "usage_point_mrid", "TEXT", NOT_NULL)

    override val name: String = "usage_point_contact_details"

    override val uniqueIndexColumns: MutableList<List<Column>> =
        super.uniqueIndexColumns.apply {
            add(listOf(USAGE_POINT_MRID, ID))
        }

    override val nonUniqueIndexColumns: MutableList<List<Column>> =
        super.nonUniqueIndexColumns.apply {
            add(listOf(USAGE_POINT_MRID))
        }

}

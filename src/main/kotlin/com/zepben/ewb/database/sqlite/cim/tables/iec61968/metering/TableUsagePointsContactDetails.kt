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
import com.zepben.ewb.database.sql.Column.Type.STRING
import com.zepben.ewb.database.sqlite.cim.tables.extensions.iec61968.common.TableContactDetails

/**
 * A class representing the UsagePoint to ContactDetails association columns required for the database table.
 *
 * @property USAGE_POINT_MRID A column that stores the identifier of the usage point associated with the contact details.
 */
@Suppress("PropertyName")
class TableUsagePointsContactDetails : TableContactDetails() {

    val USAGE_POINT_MRID: Column = Column(++columnIndex, "usage_point_mrid", STRING, NOT_NULL)

    override val name: String = "usage_points_contact_details"

    init {
        addNonUniqueIndexes(
            listOf(USAGE_POINT_MRID)
        )
    }

}

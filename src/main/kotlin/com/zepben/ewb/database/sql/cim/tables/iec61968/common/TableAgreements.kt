/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.iec61968.common

import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NULL
import com.zepben.ewb.database.sql.common.tables.Column.Type.STRING

/**
 * A class representing the Agreement columns required for the database table.
 *
 * @property VALIDITY_INTERVAL_START The date and time interval this agreement is valid (from going into effect...).
 * @property VALIDITY_INTERVAL_END The date and time interval this agreement is valid (...to termination).
 */
@Suppress("PropertyName")
abstract class TableAgreements : TableDocuments() {

    val VALIDITY_INTERVAL_START: Column = Column(++columnIndex, "validity_interval_start", STRING, NULL)
    val VALIDITY_INTERVAL_END: Column = Column(++columnIndex, "validity_interval_end", STRING, NULL)

}

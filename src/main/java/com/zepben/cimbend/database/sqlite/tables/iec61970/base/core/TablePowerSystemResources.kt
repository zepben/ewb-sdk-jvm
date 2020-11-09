/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.database.sqlite.tables.iec61970.base.core

import com.zepben.cimbend.database.Column
import com.zepben.cimbend.database.Column.Nullable.NOT_NULL
import com.zepben.cimbend.database.Column.Nullable.NULL

@Suppress("PropertyName")
abstract class TablePowerSystemResources : TableIdentifiedObjects() {

    val LOCATION_MRID = Column(++columnIndex, "location_mrid", "TEXT", NULL)
    val NUM_CONTROLS = Column(++columnIndex, "num_controls", "INTEGER", NOT_NULL)

}

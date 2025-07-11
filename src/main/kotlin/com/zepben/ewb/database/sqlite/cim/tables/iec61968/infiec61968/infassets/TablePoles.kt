/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.tables.iec61968.infiec61968.infassets

import com.zepben.ewb.database.sql.Column
import com.zepben.ewb.database.sql.Column.Nullable.NOT_NULL
import com.zepben.ewb.database.sqlite.cim.tables.iec61968.assets.TableStructures

@Suppress("PropertyName")
class TablePoles : TableStructures() {

    val CLASSIFICATION: Column = Column(++columnIndex, "classification", "TEXT", NOT_NULL)

    override val name: String = "poles"

}

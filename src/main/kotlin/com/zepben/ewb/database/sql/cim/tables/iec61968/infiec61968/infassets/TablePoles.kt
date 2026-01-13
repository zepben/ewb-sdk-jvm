/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.iec61968.infiec61968.infassets

import com.zepben.ewb.database.sql.cim.tables.iec61968.assets.TableStructures
import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NULL

/**
 * A class representing the `Pole` columns required for the database table.
 *
 * @property CLASSIFICATION Pole class: 1, 2, 3, 4, 5, 6, 7, H1, H2, Other, Unknown.
 */
@Suppress("PropertyName")
class TablePoles : TableStructures() {

    val CLASSIFICATION: Column = Column(++columnIndex, "classification", Column.Type.STRING, NULL)

    override val name: String = "poles"

}

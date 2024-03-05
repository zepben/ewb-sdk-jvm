/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.tables.iec61968.assets

import com.zepben.evolve.database.sqlite.tables.Column
import com.zepben.evolve.database.sqlite.tables.Column.Nullable.NOT_NULL

@Suppress("PropertyName")
class TablePoles : TableStructures() {

    var CLASSIFICATION: Column = Column(++columnIndex, "classification", "TEXT", NOT_NULL)

    override fun name(): String {
        return "poles"
    }

    override val tableClass: Class<TablePoles> = this.javaClass
    override val tableClassInstance: TablePoles = this

}

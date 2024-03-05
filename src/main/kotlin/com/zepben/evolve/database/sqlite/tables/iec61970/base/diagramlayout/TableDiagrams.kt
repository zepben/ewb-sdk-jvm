/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.tables.iec61970.base.diagramlayout

import com.zepben.evolve.database.sqlite.tables.Column
import com.zepben.evolve.database.sqlite.tables.Column.Nullable.NOT_NULL
import com.zepben.evolve.database.sqlite.tables.iec61970.base.core.TableIdentifiedObjects

@Suppress("PropertyName")
class TableDiagrams : TableIdentifiedObjects() {

    val DIAGRAM_STYLE: Column = Column(++columnIndex, "diagram_style", "TEXT", NOT_NULL)
    val ORIENTATION_KIND: Column = Column(++columnIndex, "orientation_kind", "TEXT", NOT_NULL)

    override fun name(): String {
        return "diagrams"
    }

    override val tableClass: Class<TableDiagrams> = this.javaClass
    override val tableClassInstance: TableDiagrams = this

}

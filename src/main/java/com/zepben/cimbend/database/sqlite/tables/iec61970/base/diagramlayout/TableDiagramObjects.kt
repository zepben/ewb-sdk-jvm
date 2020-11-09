/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.database.sqlite.tables.iec61970.base.diagramlayout

import com.zepben.cimbend.database.Column
import com.zepben.cimbend.database.Column.Nullable.NOT_NULL
import com.zepben.cimbend.database.Column.Nullable.NULL
import com.zepben.cimbend.database.sqlite.tables.iec61970.base.core.TableIdentifiedObjects

@Suppress("PropertyName")
class TableDiagramObjects : TableIdentifiedObjects() {

    val IDENTIFIED_OBJECT_MRID = Column(++columnIndex, "identified_object_mrid", "TEXT", NULL)
    val DIAGRAM_MRID = Column(++columnIndex, "diagram_mrid", "TEXT", NULL)
    val STYLE = Column(++columnIndex, "style", "TEXT", NOT_NULL)
    val ROTATION = Column(++columnIndex, "rotation", "NUMBER", NOT_NULL)

    override fun name(): String {
        return "diagram_objects"
    }

    override fun nonUniqueIndexColumns(): MutableList<List<Column>> {
        val cols = super.nonUniqueIndexColumns()

        cols.add(listOf(IDENTIFIED_OBJECT_MRID))
        cols.add(listOf(DIAGRAM_MRID))

        return cols
    }

    override val tableClass = this.javaClass
    override val tableClassInstance = this

}

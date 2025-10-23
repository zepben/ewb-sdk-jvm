/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.tables.iec61970.base.diagramlayout

import com.zepben.ewb.database.sql.Column
import com.zepben.ewb.database.sql.Column.Nullable.NOT_NULL
import com.zepben.ewb.database.sql.Column.Nullable.NULL
import com.zepben.ewb.database.sqlite.cim.tables.iec61970.base.core.TableIdentifiedObjects

@Suppress("PropertyName")
class TableDiagramObjects : TableIdentifiedObjects() {

    val IDENTIFIED_OBJECT_MRID: Column = Column(++columnIndex, "identified_object_mrid", Column.Type.STRING, NULL)
    val DIAGRAM_MRID: Column = Column(++columnIndex, "diagram_mrid", Column.Type.STRING, NULL)
    val STYLE: Column = Column(++columnIndex, "style", Column.Type.STRING, NULL)
    val ROTATION: Column = Column(++columnIndex, "rotation", Column.Type.DOUBLE, NOT_NULL)

    override val name: String = "diagram_objects"

    init {
        addNonUniqueIndexes(
            listOf(IDENTIFIED_OBJECT_MRID),
            listOf(DIAGRAM_MRID)
        )
    }

}

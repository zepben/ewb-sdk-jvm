/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.iec61970.base.diagramlayout

import com.zepben.ewb.database.sql.cim.tables.iec61970.base.core.TableIdentifiedObjects
import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NOT_NULL

/**
 * A class representing the `Diagram` columns required for the database table.
 *
 * @property DIAGRAM_STYLE A Diagram may have a DiagramStyle.
 * @property ORIENTATION_KIND Coordinate system orientation of the diagram.
 */
@Suppress("PropertyName")
class TableDiagrams : TableIdentifiedObjects() {

    val DIAGRAM_STYLE: Column = Column(++columnIndex, "diagram_style", Column.Type.STRING, NOT_NULL)
    val ORIENTATION_KIND: Column = Column(++columnIndex, "orientation_kind", Column.Type.STRING, NOT_NULL)

    override val name: String = "diagrams"

}

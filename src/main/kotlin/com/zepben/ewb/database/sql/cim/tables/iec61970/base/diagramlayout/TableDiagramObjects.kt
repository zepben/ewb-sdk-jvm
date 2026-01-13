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
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NULL

/**
 * A class representing the `DiagramObject` columns required for the database table.
 * @property IDENTIFIED_OBJECT_MRID The domain object to which this diagram object is associated.
 * @property DIAGRAM_MRID A diagram object is part of a diagram.
 * @property STYLE A diagram object has a style associated that provides a reference for the style used in the originating system.
 * @property ROTATION Sets the angle of rotation of the diagram object.  Zero degrees is pointing to the top of the diagram.  Rotation is clockwise.
 */
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

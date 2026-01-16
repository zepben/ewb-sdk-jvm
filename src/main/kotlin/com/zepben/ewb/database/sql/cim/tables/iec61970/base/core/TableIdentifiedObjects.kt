/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.iec61970.base.core

import com.zepben.ewb.cim.iec61970.base.core.IdentifiedObject
import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NOT_NULL
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NULL
import com.zepben.ewb.database.sql.common.tables.SqlTable

/**
 * A class representing the `IdentifiedObject` columns required for the database table.
 *
 * @property MRID Master resource identifier issued by a model authority. The mRID is unique within an exchange context. Global uniqueness
 *                is easily achieved by using a UUID,  as specified in RFC 4122, for the mRID. The use of UUID is strongly recommended.
 *                For CIMXML data files in RDF syntax conforming to IEC 61970-552 Edition 1, the mRID is mapped to rdf:ID or rdf:about attributes
 *                that identify CIM object elements.
 * @property NAME is any free human-readable and possibly non-unique text naming the object.
 * @property DESCRIPTION a free human-readable text describing or naming the object. It may be non-unique and may not correlate to a naming hierarchy.
 * @property NUM_DIAGRAM_OBJECTS Number of DiagramObject's known to associate with this [IdentifiedObject]
 */
@Suppress("PropertyName")
abstract class TableIdentifiedObjects : SqlTable() {

    val MRID: Column = Column(++columnIndex, "mrid", Column.Type.STRING, NOT_NULL)
    val NAME: Column = Column(++columnIndex, "name", Column.Type.STRING, NULL)
    val DESCRIPTION: Column = Column(++columnIndex, "description", Column.Type.STRING, NULL)
    val NUM_DIAGRAM_OBJECTS: Column = Column(++columnIndex, "num_diagram_objects", Column.Type.INTEGER, NULL)

    init {
        addUniqueIndexes(
            listOf(MRID)
        )

        addNonUniqueIndexes(
            listOf(NAME)
        )
    }

}

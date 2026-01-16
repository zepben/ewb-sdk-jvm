/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.diagram

import com.zepben.ewb.cim.iec61970.base.diagramlayout.Diagram
import com.zepben.ewb.cim.iec61970.base.diagramlayout.DiagramObject
import com.zepben.ewb.database.sql.cim.BaseServiceWriter
import com.zepben.ewb.services.diagram.DiagramService

/**
 * A class for writing a [DiagramService] into the database.
 *
 * @param databaseTables The [DiagramDatabaseTables] to add to the database.
 */
internal class DiagramServiceWriter(
    databaseTables: DiagramDatabaseTables,
    override val writer: DiagramCimWriter = DiagramCimWriter(databaseTables)
) : BaseServiceWriter<DiagramService>(writer) {

    override fun DiagramService.writeService(): Boolean =
        writeEach<DiagramObject>(writer::write) and
            writeEach<Diagram>(writer::write)

}

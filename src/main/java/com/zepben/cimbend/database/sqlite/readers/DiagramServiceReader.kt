/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.cimbend.database.sqlite.readers

import com.zepben.cimbend.database.sqlite.tables.iec61970.base.diagramlayout.TableDiagramObjectPoints
import com.zepben.cimbend.database.sqlite.tables.iec61970.base.diagramlayout.TableDiagramObjects
import com.zepben.cimbend.database.sqlite.tables.iec61970.base.diagramlayout.TableDiagrams
import com.zepben.cimbend.diagram.DiagramService
import java.sql.Statement


/**
 * Class for reading a [DiagramService] from the database.
 *
 * @property getStatement provider of statements for the connection.
 */
class DiagramServiceReader constructor(getStatement: () -> Statement) : BaseServiceReader(getStatement) {

    fun load(reader: DiagramCIMReader): Boolean {
        var status = true

        status = status and loadEach<TableDiagrams>("diagrams", reader::load)
        status = status and loadEach<TableDiagramObjects>("diagram objects", reader::load)
        status = status and loadEach<TableDiagramObjectPoints>("diagram object points", reader::load)

        return status
    }

}

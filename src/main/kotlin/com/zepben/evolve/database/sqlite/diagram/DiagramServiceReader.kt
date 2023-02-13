/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.diagram

import com.zepben.evolve.database.sqlite.common.BaseServiceReader
import com.zepben.evolve.database.sqlite.common.DatabaseTables
import com.zepben.evolve.database.sqlite.tables.iec61970.base.diagramlayout.TableDiagramObjectPoints
import com.zepben.evolve.database.sqlite.tables.iec61970.base.diagramlayout.TableDiagramObjects
import com.zepben.evolve.database.sqlite.tables.iec61970.base.diagramlayout.TableDiagrams
import com.zepben.evolve.services.diagram.DiagramService
import java.sql.Statement


/**
 * Class for reading a [DiagramService] from the database.
 *
 * @property getStatement provider of statements for the connection.
 */
class DiagramServiceReader(
    databaseTables: DatabaseTables,
    reader: DiagramCIMReader,
    getStatement: () -> Statement,
) : BaseServiceReader<DiagramCIMReader>(databaseTables, getStatement, reader) {

    override fun load(): Boolean {
        var status = loadNameTypes(reader)

        status = status and loadEach("diagrams", TableDiagrams(), reader::load)
        status = status and loadEach("diagram objects", TableDiagramObjects(), reader::load)
        status = status and loadEach("diagram object points", TableDiagramObjectPoints(), reader::load)

        status = status and loadNames(reader)

        return status
    }

}

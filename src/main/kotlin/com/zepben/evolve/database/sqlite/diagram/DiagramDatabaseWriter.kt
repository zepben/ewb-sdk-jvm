/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.diagram

import com.zepben.evolve.database.sqlite.common.DatabaseWriter
import com.zepben.evolve.database.sqlite.common.MetadataCollectionWriter
import com.zepben.evolve.services.common.meta.MetadataCollection
import com.zepben.evolve.services.diagram.DiagramService
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.Statement


/**
 * @property databaseFile the filename of the database to write.
 * @property getConnection provider of the connection to the specified database.
 * @property getStatement provider of statements for the connection.
 * @property getPreparedStatement provider of prepared statements for the connection.
 * @property savedCommonMRIDs Note this doesn't work if it's not common across all Service based database writers
 */
class DiagramDatabaseWriter @JvmOverloads constructor(
    service: DiagramService,
    metadataCollection: MetadataCollection,
    databaseFile: String,
    savedCommonMRIDs: MutableSet<String>,
    diagramServiceWriter: DiagramServiceWriter = DiagramServiceWriter(
        service,
        DiagramCIMWriter(diagramDatabaseTables),
        { savedCommonMRIDs.contains(it) },
        { savedCommonMRIDs.add(it) }
    ),
    metadataCollectionWriter: MetadataCollectionWriter = MetadataCollectionWriter(metadataCollection),
    getConnection: (String) -> Connection = DriverManager::getConnection,
    getStatement: (Connection) -> Statement = Connection::createStatement,
    getPreparedStatement: (Connection, String) -> PreparedStatement = Connection::prepareStatement,
) : DatabaseWriter<DiagramServiceWriter>(
    diagramDatabaseTables,
    diagramServiceWriter,
    { savedCommonMRIDs.contains(it) },
    { savedCommonMRIDs.add(it) },
    metadataCollectionWriter,
    databaseFile,
    getConnection,
    getStatement,
    getPreparedStatement
)

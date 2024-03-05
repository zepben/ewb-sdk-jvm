/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.diagram

import com.zepben.evolve.database.sqlite.common.DatabaseReader
import com.zepben.evolve.database.sqlite.common.MetadataCollectionReader
import com.zepben.evolve.database.sqlite.common.MetadataEntryReader
import com.zepben.evolve.database.sqlite.common.metadataDatabaseTables
import com.zepben.evolve.database.sqlite.upgrade.UpgradeRunner
import com.zepben.evolve.services.common.meta.MetadataCollection
import com.zepben.evolve.services.diagram.DiagramService
import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement
import java.util.*


/**
 * @property databaseFile the filename of the database to write.
 * @property getConnection provider of the connection to the specified database.
 * @property getStatement provider of statements for the connection.
 * @property getPreparedStatement provider of prepared statements for the connection.
 * @property savedCommonMRIDs Note this doesn't work if it's not common across all Service based database writers
 */
class DiagramDatabaseReader(
    val diagramService: DiagramService,
    metadataCollection: MetadataCollection,
    databaseFile: String,
    getConnection: (String) -> Connection = DriverManager::getConnection,
    getStatement: (Connection) -> Statement = Connection::createStatement,
    upgradeRunner: UpgradeRunner = UpgradeRunner(getConnection, getStatement),
    metadataCollectionReader: MetadataCollectionReader = MetadataCollectionReader(
        metadataDatabaseTables,
        MetadataEntryReader(metadataCollection)
    ) { getStatement(getConnection("jdbc:sqlite:$databaseFile")) },
    diagramServiceReader: DiagramServiceReader = DiagramServiceReader(
        diagramDatabaseTables,
        DiagramCIMReader(diagramService)
    ) { getStatement(getConnection("jdbc:sqlite:$databaseFile")) },
) : DatabaseReader<DiagramServiceReader>(
    diagramDatabaseTables,
    diagramServiceReader,
    databaseFile,
    metadataCollectionReader,
    upgradeRunner,
)

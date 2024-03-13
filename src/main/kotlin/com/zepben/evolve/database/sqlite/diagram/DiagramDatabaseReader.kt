/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.diagram

import com.zepben.evolve.database.sqlite.common.BaseDatabaseReader
import com.zepben.evolve.database.sqlite.common.MetadataCollectionReader
import com.zepben.evolve.database.sqlite.upgrade.EwbDatabaseType
import com.zepben.evolve.database.sqlite.upgrade.UpgradeRunner
import com.zepben.evolve.services.common.meta.MetadataCollection
import com.zepben.evolve.services.diagram.DiagramService
import java.sql.Connection

/**
 * A class for reading the [DiagramService] objects and [MetadataCollection] from our diagram database.
 *
 * @param databaseFile The filename of the database to read.
 * @param metadata The [MetadataCollection] to populate with metadata from the database.
 * @param service The [DiagramService] to populate with CIM objects from the database.
 */
class DiagramDatabaseReader @JvmOverloads constructor(
    databaseFile: String,
    metadata: MetadataCollection,
    override val service: DiagramService,
    tables: DiagramDatabaseTables = DiagramDatabaseTables(),
    createMetadataReader: (Connection) -> MetadataCollectionReader = { connection ->
        MetadataCollectionReader(metadata, tables, connection)
    },
    createServiceReader: (Connection) -> DiagramServiceReader = { connection ->
        DiagramServiceReader(service, tables, connection)
    },
    upgradeRunner: UpgradeRunner = UpgradeRunner()
) : BaseDatabaseReader(
    databaseFile,
    createMetadataReader,
    createServiceReader,
    service,
    upgradeRunner,
    EwbDatabaseType.DIAGRAM
)

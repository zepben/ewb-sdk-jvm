/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.diagram

import com.zepben.ewb.database.sql.cim.CimDatabaseWriter
import com.zepben.ewb.database.sql.cim.metadata.MetadataCollectionWriter
import com.zepben.ewb.database.sql.generators.SqlGenerator
import com.zepben.ewb.database.sql.initialisers.DatabaseInitialiser
import com.zepben.ewb.database.sql.initialisers.SqliteDatabaseInitialiser
import com.zepben.ewb.services.common.meta.MetadataCollection
import com.zepben.ewb.services.diagram.DiagramService

/**
 * A class for writing the [DiagramService] objects and [MetadataCollection] to our network database.
 */
class DiagramDatabaseWriter internal constructor(
    override val databaseInitialiser: DatabaseInitialiser<DiagramDatabaseTables>,
    override val databaseTables: DiagramDatabaseTables,
) : CimDatabaseWriter<DiagramDatabaseTables, DiagramService>(
    ::MetadataCollectionWriter,
    ::DiagramServiceWriter
) {

    /**
     * Convenience constructor for connecting to an SQLite database.
     *
     * @param databaseFile the filename of the database to write.
     */
    constructor(
        databaseFile: String,
    ) : this(SqliteDatabaseInitialiser(databaseFile), DiagramDatabaseTables())

    /**
     * @param databaseInitialiser The hooks used to initilise the database.
     * @param sqlGenerator The SQL generator used to format queries used to read/write these tables to/from the database.
     */
    constructor(
        databaseInitialiser: DatabaseInitialiser<DiagramDatabaseTables>,
        sqlGenerator: SqlGenerator,
    ) : this(databaseInitialiser, DiagramDatabaseTables(sqlGenerator))

}

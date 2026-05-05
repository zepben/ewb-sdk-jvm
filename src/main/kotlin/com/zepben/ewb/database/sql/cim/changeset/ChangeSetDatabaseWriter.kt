/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.changeset

import com.zepben.ewb.database.sql.cim.CimDatabaseWriter
import com.zepben.ewb.database.sql.cim.metadata.MetadataCollectionWriter
import com.zepben.ewb.database.sql.generators.SqlGenerator
import com.zepben.ewb.database.sql.initialisers.DatabaseInitialiser
import com.zepben.ewb.database.sql.initialisers.SqliteDatabaseInitialiser
import com.zepben.ewb.services.common.meta.MetadataCollection
import com.zepben.ewb.services.variant.VariantService

/**
 * A class for writing the [VariantService] objects and [MetadataCollection] to our network database.
 */
class ChangeSetDatabaseWriter internal constructor(
    override val databaseInitialiser: DatabaseInitialiser<ChangeSetDatabaseTables>,
    override val databaseTables: ChangeSetDatabaseTables,
) : CimDatabaseWriter<ChangeSetDatabaseTables, VariantService>(
    ::MetadataCollectionWriter,
    ::ChangeSetServiceWriter,
) {

    /**
     * Convenience constructor for connecting to an SQLite database.
     *
     * @param databaseInitialiser The hooks used to initilise the database.
     */
    constructor(
        databaseFile: String,
    ) : this(SqliteDatabaseInitialiser(databaseFile), ChangeSetDatabaseTables())

    /**
     * @param databaseInitialiser The hooks used to initilise the database.
     * @property sqlGenerator The SQL generator used to format queries used to read/write these tables to/from the database.
     */
    constructor(
        databaseInitialiser: DatabaseInitialiser<ChangeSetDatabaseTables>,
        sqlGenerator: SqlGenerator,
    ) : this(databaseInitialiser, ChangeSetDatabaseTables(sqlGenerator))

}

/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.network

import com.zepben.ewb.database.sql.cim.CimDatabaseWriter
import com.zepben.ewb.database.sql.cim.metadata.MetadataCollectionWriter
import com.zepben.ewb.database.sql.generators.SqlGenerator
import com.zepben.ewb.database.sql.initialisers.DatabaseInitialiser
import com.zepben.ewb.database.sql.initialisers.SqliteDatabaseInitialiser
import com.zepben.ewb.services.common.meta.MetadataCollection
import com.zepben.ewb.services.network.NetworkService

/**
 * A class for writing the [NetworkService] objects and [MetadataCollection] to our network database.
 */
class NetworkDatabaseWriter internal constructor(
    override val databaseInitialiser: DatabaseInitialiser<NetworkDatabaseTables>,
    override val databaseTables: NetworkDatabaseTables,
) : CimDatabaseWriter<NetworkDatabaseTables, NetworkService>(
    ::MetadataCollectionWriter,
    ::NetworkServiceWriter
) {

    /**
     * Convenience constructor for connecting to an SQLite database.
     *
     * @param databaseFile the filename of the database to write.
     */
    constructor(
        databaseFile: String,
    ) : this(SqliteDatabaseInitialiser(databaseFile), NetworkDatabaseTables())

    /**
     * @param databaseInitialiser The hooks used to initilise the database.
     * @param sqlGenerator The SQL generator used to format queries used to read/write these tables to/from the database.
     */
    constructor(
        databaseInitialiser: DatabaseInitialiser<NetworkDatabaseTables>,
        sqlGenerator: SqlGenerator,
    ) : this(databaseInitialiser, NetworkDatabaseTables(sqlGenerator))

}

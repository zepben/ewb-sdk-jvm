/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.customer

import com.zepben.ewb.database.sql.cim.CimDatabaseWriter
import com.zepben.ewb.database.sql.cim.metadata.MetadataCollectionWriter
import com.zepben.ewb.database.sql.generators.SqlGenerator
import com.zepben.ewb.database.sql.initialisers.DatabaseInitialiser
import com.zepben.ewb.database.sql.initialisers.SqliteDatabaseInitialiser
import com.zepben.ewb.services.common.meta.MetadataCollection
import com.zepben.ewb.services.customer.CustomerService

/**
 * A class for writing the [CustomerService] objects and [MetadataCollection] to our network database.
 */
class CustomerDatabaseWriter internal constructor(
    override val databaseInitialiser: DatabaseInitialiser<CustomerDatabaseTables>,
    override val databaseTables: CustomerDatabaseTables,
) : CimDatabaseWriter<CustomerDatabaseTables, CustomerService>(
    ::MetadataCollectionWriter,
    ::CustomerServiceWriter
) {

    /**
     * Convenience constructor for connecting to an SQLite database.
     *
     * @param databaseFile the filename of the database to write.
     */
    constructor(
        databaseFile: String,
    ) : this(SqliteDatabaseInitialiser(databaseFile), CustomerDatabaseTables())

    /**
     * @param databaseInitialiser The hooks used to initilise the database.
     * @param sqlGenerator The SQL generator used to format queries used to read/write these tables to/from the database.
     */
    constructor(
        databaseInitialiser: DatabaseInitialiser<CustomerDatabaseTables>,
        sqlGenerator: SqlGenerator,
    ) : this(databaseInitialiser, CustomerDatabaseTables(sqlGenerator))

}

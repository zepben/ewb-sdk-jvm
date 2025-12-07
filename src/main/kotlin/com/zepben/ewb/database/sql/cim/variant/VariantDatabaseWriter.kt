/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.variant

import com.zepben.ewb.database.sql.cim.CimDatabaseWriter
import com.zepben.ewb.database.sql.cim.metadata.MetadataCollectionWriter
import com.zepben.ewb.database.sql.generators.SqlGenerator
import com.zepben.ewb.database.sql.initialisers.DatabaseInitialiser
import com.zepben.ewb.database.sql.initialisers.NoOpDatabaseInitialiser
import com.zepben.ewb.services.common.meta.MetadataCollection
import com.zepben.ewb.services.variant.VariantService
import java.sql.Connection

/**
 * A class for writing the [VariantService] objects and [MetadataCollection] to our network database.
 */
class VariantDatabaseWriter internal constructor(
    override val databaseInitialiser: DatabaseInitialiser<VariantDatabaseTables>,
    override val databaseTables: VariantDatabaseTables,
) : CimDatabaseWriter<VariantDatabaseTables, VariantService>(
    ::MetadataCollectionWriter,
    ::VariantServiceWriter,
) {

    /**
     * Convenience constructor for connecting to a Postgres database.
     *
     * @param getConnection Provider of the connection to the metrics database.
     */
    constructor(
        getConnection: () -> Connection,
    ) : this(NoOpDatabaseInitialiser(getConnection), VariantDatabaseTables())

    /**
     * @param databaseInitialiser The hooks used to initilise the database.
     * @property sqlGenerator The SQL generator used to format queries used to read/write these tables to/from the database.
     */
    constructor(
        databaseInitialiser: DatabaseInitialiser<VariantDatabaseTables>,
        sqlGenerator: SqlGenerator,
    ) : this(databaseInitialiser, VariantDatabaseTables(sqlGenerator))

}

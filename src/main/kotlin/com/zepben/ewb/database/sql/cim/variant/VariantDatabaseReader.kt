/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.variant

import com.zepben.ewb.database.sql.cim.BaseServiceReader
import com.zepben.ewb.database.sql.cim.CimDatabaseReader
import com.zepben.ewb.database.sql.cim.metadata.MetadataCollectionReader
import com.zepben.ewb.services.common.meta.MetadataCollection
import com.zepben.ewb.services.variant.VariantService
import java.sql.Connection

/**
 * A class for reading the [VariantService] objects and [MetadataCollection] from our variant database.
 *
 * @param connection The connection to the database.
 * @param databaseDescription The description of the database for logging (e.g. filename).
 * @param databaseTables The tables to read.
 * @param createMetadataReader A factory function to create the [MetadataCollectionReader] used to read from the database.
 * @param createServiceReader A factory function to create the service reader used to read from the database.
 */
class VariantDatabaseReader internal constructor(
    connection: Connection,
    databaseDescription: String,
    databaseTables: VariantDatabaseTables,
    createMetadataReader: (VariantDatabaseTables, Connection) -> MetadataCollectionReader,
    createServiceReader: (VariantDatabaseTables, Connection) -> BaseServiceReader<VariantService>,
) : CimDatabaseReader<VariantDatabaseTables, VariantService>(
    connection,
    databaseDescription,
    databaseTables,
    createMetadataReader,
    createServiceReader,
){

    /**
     * @param connection The connection to the database.
     * @param databaseDescription The description of the database for logging (e.g. filename).
     */
    constructor(
        connection: Connection,
        databaseDescription: String
    ) : this(
        connection,
        databaseDescription,
        VariantDatabaseTables(),
        ::MetadataCollectionReader,
        ::VariantServiceReader
    )

}

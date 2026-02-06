/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim

import com.zepben.ewb.database.sql.cim.metadata.MetadataCollectionWriter
import com.zepben.ewb.database.sql.common.BaseDatabaseWriter
import com.zepben.ewb.services.common.BaseService

/**
 * A base class for writing objects to one of our CIM databases.
 *
 * @param TTables The type of [CimDatabaseTables] supported by this writer.
 * @param TService The type of [BaseService] supported by this writer.
 *
 * @param createMetadataWriter Factory for creating the [MetadataCollectionWriter] to use.
 * @param createServiceWriter Factory for creating the [BaseServiceWriter] to use.
 */
abstract class CimDatabaseWriter<TTables : CimDatabaseTables, TService : BaseService> internal constructor(
    private val createMetadataWriter: (TTables) -> MetadataCollectionWriter,
    private val createServiceWriter: (TTables) -> BaseServiceWriter<TService>
) : BaseDatabaseWriter<TTables>() {

    /**
     * Write metadata and service.
     *
     * @param data The [TService] containing the metadata and service to write.
     * @return true if the [data] was successfully written to the database, otherwise false.
     */
    fun write(data: TService): Boolean = connectAndWrite { createMetadataWriter(databaseTables).write(data.metadata) and createServiceWriter(databaseTables).write(data) }
}

/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.cim

import com.zepben.evolve.database.sql.BaseCollectionReader
import com.zepben.evolve.database.sqlite.cim.tables.iec61970.base.core.TableNameTypes
import com.zepben.evolve.database.sqlite.cim.tables.iec61970.base.core.TableNames
import com.zepben.evolve.services.common.BaseService
import java.sql.Connection

/**
 * A base class for reading items stored in a [BaseService] from the database.
 *
 * @param databaseTables The tables available in the database.
 * @param connection A connection to the database.
 *
 * @property reader The [CimReader] used to read the objects from the database.
 */
internal abstract class BaseServiceReader<TService : BaseService>(
    databaseTables: CimDatabaseTables,
    connection: Connection,
    protected open val reader: CimReader<TService>,
) : BaseCollectionReader<TService>(databaseTables, connection) {

    final override fun read(data: TService): Boolean =
        readService(data) and
            readEach<TableNameTypes>(data, reader::read) and
            readEach<TableNames>(data, reader::read)

    /**
     * Read the service specific objects from the database.
     *
     * @return true if the objects were successfully read from the database, otherwise false
     */
    abstract fun readService(service: TService): Boolean

}

/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.common

import com.zepben.evolve.database.sqlite.tables.iec61970.base.core.TableNameTypes
import com.zepben.evolve.database.sqlite.tables.iec61970.base.core.TableNames
import com.zepben.evolve.services.common.BaseService
import java.sql.Connection

/**
 * A base class for reading items stored in a [BaseService] from the database.
 *
 * @param databaseTables The tables available in the database.
 * @param connection A connection to the database.
 *
 * @property reader The [BaseCimReader] used to load the objects from the database.
 */
abstract class BaseServiceReader(
    databaseTables: BaseDatabaseTables,
    connection: Connection,
    protected open val reader: BaseCimReader,
) : BaseCollectionReader(databaseTables, connection) {

    final override fun load(): Boolean =
        //todo try reorder to name types after doLoad
        loadEach<TableNameTypes>(reader::load)
            .andDoLoad()
            .andLoadEach<TableNames>(reader::load)

    /**
     * Load the service specific objects from the database.
     *
     * @return true if the objects were successfully loaded from the database, otherwise false
     */
    abstract fun doLoad(): Boolean

    private fun Boolean.andDoLoad(): Boolean =
        this and doLoad()

}

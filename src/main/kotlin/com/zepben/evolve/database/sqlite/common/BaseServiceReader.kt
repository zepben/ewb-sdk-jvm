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
import java.sql.Statement

abstract class BaseServiceReader<R : BaseCIMReader>(
    databaseTables: BaseDatabaseTables,
    getStatement: () -> Statement,
    val reader: R,
) : BaseCollectionReader(databaseTables, getStatement) {

    final override fun load(): Boolean =
        loadEach<TableNameTypes>(reader::load)
            .andDoLoad()
            .andLoadEach<TableNames>(reader::load)

    abstract fun doLoad(): Boolean

    private fun Boolean.andDoLoad(): Boolean =
        this and doLoad()

}

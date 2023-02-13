/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.common

import com.zepben.evolve.database.sqlite.tables.SqliteTable
import com.zepben.evolve.database.sqlite.tables.TableMetadataDataSources
import com.zepben.evolve.database.sqlite.tables.TableVersion

val metadataDatabaseTables = object : DatabaseTables() {
    override val tables: Map<Class<out SqliteTable>, SqliteTable> = listOf(
        TableMetadataDataSources(),
        TableVersion(),
    ).associateBy { it::class.java }
}

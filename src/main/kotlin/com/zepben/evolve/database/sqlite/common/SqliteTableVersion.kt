/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.common

import com.zepben.evolve.database.sql.Column
import com.zepben.evolve.database.sql.Column.Nullable.NOT_NULL
import com.zepben.evolve.database.sql.TableVersion
import com.zepben.evolve.database.sqlite.cim.tables.SqliteTable

/**
 * Code representation of the `version` table in an SQLite database.
 *
 * @property supportedVersion The supported schema version.
 */
class SqliteTableVersion(override val supportedVersion: Int) : TableVersion, SqliteTable() {

    override val VERSION: Column = Column(++columnIndex, "version", "TEXT", NOT_NULL)

    override val name: String = "version"

}

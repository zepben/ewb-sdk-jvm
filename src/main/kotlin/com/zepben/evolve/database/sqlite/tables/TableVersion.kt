/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.tables

import com.zepben.evolve.database.sqlite.tables.Column.Nullable.NOT_NULL

@Suppress("PropertyName")
class TableVersion : SqliteTable() {

    val SUPPORTED_VERSION: Int = 50

    val VERSION: Column = Column(++columnIndex, "version", "TEXT", NOT_NULL)

    override val name: String = "version"

}

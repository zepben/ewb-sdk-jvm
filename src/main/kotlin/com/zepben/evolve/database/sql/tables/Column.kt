/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sql.tables

/**
 * Represents a column in a database table.
 */
class Column @JvmOverloads internal constructor(
    val queryIndex: Int,
    val name: String,
    val type: String,
    val nullable: Nullable = Nullable.NONE
) {

    enum class Nullable {
        NONE, NOT_NULL, NULL;
    }

    init {
        require(queryIndex >= 0) { "You cannot use a negative query indexes." }
    }

}

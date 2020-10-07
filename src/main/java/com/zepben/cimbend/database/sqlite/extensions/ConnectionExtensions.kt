/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.database.sqlite.extensions

import java.sql.Connection
import java.sql.Statement


internal fun Connection.configureBatch(getStatement: (Connection) -> Statement): Connection {
    getStatement(this).use { statement ->
        statement.executeUpdate("PRAGMA journal_mode = OFF")
        statement.executeUpdate("PRAGMA synchronous = OFF")
    }

    autoCommit = false

    return this
}

/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.database

import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement

@Throws(SQLException::class)
fun Statement.executeConfiguredQuery(sql: String): ResultSet {
    this.queryTimeout = 30
    this.fetchSize = 10000

    val results = this.executeQuery(sql)
    if (!results.isClosed)
        results.fetchDirection = ResultSet.FETCH_FORWARD

    return results
}

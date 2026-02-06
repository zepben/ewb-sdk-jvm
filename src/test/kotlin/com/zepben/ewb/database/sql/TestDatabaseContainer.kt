/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql

import org.testcontainers.containers.PostgreSQLContainer
import java.sql.Connection
import java.sql.DriverManager

object TestDatabaseContainer {
    private val databaseContainer: PostgreSQLContainer<out PostgreSQLContainer<*>> by lazy {
        PostgreSQLContainer("postgres:$POSTGRES_VERSION").also { c ->
            Runtime.getRuntime().addShutdownHook(Thread {
                c.stop()
            })
            c.start()
        }
    }

    fun resetDatabaseContainer() {
        databaseContainer.stop()
        databaseContainer.start()
    }

    fun getConnection(): Connection =
        DriverManager.getConnection(databaseContainer.jdbcUrl + "&user=${databaseContainer.username}&password=${databaseContainer.password}")

    private const val POSTGRES_VERSION = "14.1"
}

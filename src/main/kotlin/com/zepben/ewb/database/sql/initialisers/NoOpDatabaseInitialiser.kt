/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.initialisers

import com.zepben.ewb.database.sql.common.BaseDatabaseTables
import org.slf4j.Logger
import java.sql.Connection
import java.sql.DriverManager

/**
 * A database initialiser for use when the schema is managed by an external tool, e.g. Liquibase.
 *
 * @param getConnection A factory returning the connection to the database to be initialised.
 */
class NoOpDatabaseInitialiser<TTables : BaseDatabaseTables>(
    private val getConnection: () -> Connection,
) : DatabaseInitialiser<TTables> {

    /**
     * @param connectionString A connection string that can be used to connect to the database being initialised.
     */
    constructor(
        connectionString: String,
    ) : this({ DriverManager.getConnection(connectionString) })

    override fun beforeConnect(logger: Logger): Boolean = true

    override fun connect(): Connection = getConnection()

    override fun afterConnectBeforePrepare(connection: Connection, databaseTables: TTables, logger: Logger): Boolean = true

    override fun afterWriteBeforeCommit(connection: Connection, databaseTables: TTables, logger: Logger): Boolean = true

}

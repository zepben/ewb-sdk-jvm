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

/**
 * An interface for all database initialisation wrappers.
 */
interface DatabaseInitialiser<TTables : BaseDatabaseTables> {

    /**
     * Code to execute before a connection is made to the database.
     *
     * This is the ideal time to be doing things such as taking backups of files, or removing existing files if they should be replaced.
     *
     * @param logger The [Logger] to send any progress or error messages to.
     * @return true if the processing was successful, otherwise false.
     */
    fun beforeConnect(logger: Logger): Boolean

    /**
     * Connect to the database.
     *
     * @return A [Connection] to the database being used.
     */
    fun connect(): Connection

    /**
     * Code to execute after the connection has been made to the database, but before any statements are prepared.
     *
     * This is the ideal time to do any schema creation.
     *
     * @param connection A [Connection] to the database.
     * @param databaseTables The [TTables] being used to initialise the database.
     * @param logger The [Logger] to send any progress or error messages to.
     * @return true if the processing was successful, otherwise false.
     */
    fun afterConnectBeforePrepare(connection: Connection, databaseTables: TTables, logger: Logger): Boolean

    /**
     * Code to execute after the data has been written to the database, but before the transaction is commited.
     *
     * This is the ideal time enable any indexes left out in the schema creation for performance reasons.
     *
     * @param connection A [Connection] to the database.
     * @param databaseTables The [TTables] being used to initialise the database.
     * @param logger The [Logger] to send any progress or error messages to.
     * @return true if the processing was successful, otherwise false.
     */
    fun afterWriteBeforeCommit(connection: Connection, databaseTables: TTables, logger: Logger): Boolean

}

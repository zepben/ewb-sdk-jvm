/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.common

import com.zepben.ewb.database.sql.extensions.logFailure
import com.zepben.ewb.database.sql.extensions.tryExecuteSingleUpdate
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.sql.PreparedStatement
import java.sql.SQLException

/**
 * A base class for writing entries into tables of the database.
 */
abstract class BaseEntryWriter {

    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    /**
     * Helper function for writing the entry to the database and logging any failures.
     *
     * @receiver The [PreparedStatement] to execute.
     * @param description A description of the object being written to use for logging of failures.
     *
     * @return true if the entry was successfully written to the database, otherwise false.
     */
    @Throws(SQLException::class)
    protected fun PreparedStatement.tryExecuteSingleUpdate(description: String): Boolean =
        tryExecuteSingleUpdate {
            logFailure(logger, description)
        }

}

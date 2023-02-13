/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.database.sqlite.common

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.sql.PreparedStatement
import java.sql.SQLException


abstract class BaseWriter {

    private val logger: Logger = LoggerFactory.getLogger(javaClass)
    private val failedIds: MutableSet<String> = mutableSetOf()

    /************ HELPERS ************/
    @Throws(SQLException::class)
    protected fun tryExecuteSingleUpdate(query: PreparedStatement, id: String, description: String): Boolean =
        WriteValidator.tryExecuteSingleUpdate(query) {
            failedIds.add(id)
            WriteValidator.logFailure(logger, query, description)
        }

}

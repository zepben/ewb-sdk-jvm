/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.writers

import com.zepben.evolve.database.sqlite.extensions.executeSingleUpdate
import com.zepben.evolve.database.sqlite.extensions.parameters
import com.zepben.evolve.database.sqlite.extensions.sql
import org.slf4j.Logger
import java.sql.PreparedStatement
import java.sql.SQLException

object WriteValidator {

    inline fun <T> validateSave(
        it: T,
        saver: (T) -> Boolean,
        onSaveFailure: (Exception) -> Unit
    ): Boolean {
        return try {
            saver(it)
        } catch (e: SQLException) {
            onSaveFailure(e)
            return false
        }
    }

    @Throws(SQLException::class)
    fun tryExecuteSingleUpdate(query: PreparedStatement, onError: () -> Unit): Boolean {
        if (query.executeSingleUpdate())
            return true

        onError()

        return false
    }

    fun logFailure(logger: Logger, query: PreparedStatement, description: String) {
        logger.warn(
            "Failed to save $description.\n" +
                "SQL: ${query.sql()}\n" +
                "Fields: ${query.parameters()}"
        )
    }

}

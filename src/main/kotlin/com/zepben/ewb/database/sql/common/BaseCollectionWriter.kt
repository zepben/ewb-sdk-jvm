/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.common

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.sql.SQLException

/**
 * A base class for writing collections of object collections to a database.
 *
 * @property logger The logger to use for this collection reader.
 */
internal abstract class BaseCollectionWriter {

    protected val logger: Logger = LoggerFactory.getLogger(javaClass)

    /**
     * Write each of the [items] to the database.
     *
     * NOTE: This function does not short circuit and all items will be attempted, even if one fails.
     *
     * @param T The type of object to write.
     * @param items The collection of items to write.
     * @param writer A callback for saving each object to the database.
     * @param onWriteFailure A callback that should be used to indicate there was a failure in the [writer]. You should pass the object that was being written, and
     *   the exception that caused the failure.
     *
     * @return true if all [items] were successfully written, otherwise false.
     */
    protected fun <T> writeEach(items: Iterable<T>, writer: (T) -> Boolean, onWriteFailure: (T, Exception) -> Unit): Boolean {
        var status = true

        items.forEach {
            status = status and validateWrite(it, writer) { e -> onWriteFailure(it, e) }
        }

        return status
    }

    /**
     * Validate that a `write` actually works, and convert all exceptions into failures with a callback.
     *
     * @param T The type of object to write.
     * @param it The object instance to write.
     * @param writer The callback that will write the object to the database.
     * @param onWriteFailure A callback if an exception was thrown by the [writer].
     *
     * @return true if the writer successfully written the object to the database, otherwise false.
     */
    protected fun <T> validateWrite(it: T, writer: (T) -> Boolean, onWriteFailure: (Exception) -> Unit): Boolean {
        return try {
            writer(it)
        } catch (e: SQLException) {
            onWriteFailure(e)
            return false
        }
    }

}

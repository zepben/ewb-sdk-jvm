/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.common

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.sql.SQLException

/**
 * A base class for writing collections of object collections to a database.
 *
 * @property logger The logger to use for this collection reader.
 */
abstract class BaseCollectionWriter {

    protected val logger: Logger = LoggerFactory.getLogger(javaClass)

    /**
     * Save all the objects for the available collections.
     *
     * @return true if the save was successful, otherwise false.
     */
    abstract fun save(): Boolean

    /**
     * Helper function for chaining [saveEach] calls using the [and] operator in a more readable manner.
     *
     * @param T The type of object to save.
     * @param items The collection of items to save.
     * @param saver A callback for saving each object to the database.
     * @param onSaveFailure A callback that should be used to indicate there was a failure in the [saver]. You should pass the object that was being saved, and
     *   the exception that caused the failure.
     *
     * @return true if all [items] were successfully saved.
     */
    protected fun <T> Boolean.andSaveEach(items: Iterable<T>, saver: (T) -> Boolean, onSaveFailure: (T, Exception) -> Unit): Boolean =
        this and saveEach(items, saver, onSaveFailure)

    /**
     * Save each of the [items] to the database.
     *
     * NOTE: This function does not short circuit and all items will be attempted, even if one fails.
     *
     * @param T The type of object to save.
     * @param items The collection of items to save.
     * @param saver A callback for saving each object to the database.
     * @param onSaveFailure A callback that should be used to indicate there was a failure in the [saver]. You should pass the object that was being saved, and
     *   the exception that caused the failure.
     *
     * @return true if all [items] were successfully saved, otherwise false.
     */
    protected fun <T> saveEach(items: Iterable<T>, saver: (T) -> Boolean, onSaveFailure: (T, Exception) -> Unit): Boolean {
        var status = true

        items.forEach {
            status = status and validateSave(it, saver) { e -> onSaveFailure(it, e) }
        }

        return status
    }

    /**
     * Validate that a save actually works, and convert all exceptions into failures with a callback.
     *
     * @param T The type of object to save.
     * @param it The object instance to save.
     * @param saver The callback that will save the object to the database.
     * @param onSaveFailure A callback if an exception was thrown by the [saver].
     *
     * @return true if the saver successfully saved the object to the database, otherwise false.
     */
    protected fun <T> validateSave(it: T, saver: (T) -> Boolean, onSaveFailure: (Exception) -> Unit): Boolean {
        return try {
            saver(it)
        } catch (e: SQLException) {
            onSaveFailure(e)
            return false
        }
    }

}

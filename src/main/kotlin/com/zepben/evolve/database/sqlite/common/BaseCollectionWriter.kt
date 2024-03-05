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

abstract class BaseCollectionWriter {

    protected val logger: Logger = LoggerFactory.getLogger(javaClass)

    abstract fun save(): Boolean

    protected fun <T> Boolean.andSaveEach(items: Iterable<T>, saver: (T) -> Boolean, onSaveFailure: (T, Exception) -> Unit): Boolean =
        this and saveEach(items, saver, onSaveFailure)

    protected fun <T> saveEach(items: Iterable<T>, saver: (T) -> Boolean, onSaveFailure: (T, Exception) -> Unit): Boolean {
        var status = true

        items.forEach {
            status = status and validateSave(it, saver) { e -> onSaveFailure(it, e) }
        }

        return status
    }

    protected fun <T> validateSave(it: T, saver: (T) -> Boolean, onSaveFailure: (Exception) -> Unit): Boolean {
        return try {
            saver(it)
        } catch (e: SQLException) {
            onSaveFailure(e)
            return false
        }
    }

}

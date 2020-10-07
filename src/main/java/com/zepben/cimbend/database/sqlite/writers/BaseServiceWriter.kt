/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.database.sqlite.writers

import com.zepben.cimbend.cim.iec61970.base.core.IdentifiedObject
import com.zepben.cimbend.common.BaseService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.sql.SQLException

abstract class BaseServiceWriter<T: BaseService, W: BaseCIMWriter>(protected val hasCommon: (String) -> Boolean,
                                                                   protected val addCommon: (String) -> Boolean) {

    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    abstract fun save(service: T, writer: W): Boolean

    protected inline fun <reified S : IdentifiedObject> trySaveCommon(save: (S) -> Boolean, obj: S): Boolean {
        if (hasCommon(obj.mRID))
            return true

        if (!validateSave(obj, save, "organisation"))
            return false

        return addCommon(obj.mRID)
    }

    protected inline fun <reified S : IdentifiedObject> validateSave(it: S, saver: (S) -> Boolean, description: String): Boolean {
        return try {
            saver(it)
        } catch (e: SQLException) {
            logSaveFailure(it.name, it.mRID, e, description)
        }
    }

    protected fun logSaveFailure(name: String = "", mRID: String, e: Exception, description: String): Boolean {
        logger.error("Failed to save {} '{}' [{}]: {}", description, name, mRID, e.message)
        return false
    }
}

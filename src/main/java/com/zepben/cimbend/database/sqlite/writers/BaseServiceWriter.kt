/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 * This file is part of evolve-sdk-jvm.
 *
 * evolve-sdk-jvm is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * evolve-sdk-jvm is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with evolve-sdk-jvm.  If not, see <https://www.gnu.org/licenses/>.
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

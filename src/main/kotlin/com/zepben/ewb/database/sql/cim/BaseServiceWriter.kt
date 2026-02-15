/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim

import com.zepben.ewb.cim.iec61970.base.core.Identifiable
import com.zepben.ewb.cim.iec61970.base.core.IdentifiedObject
import com.zepben.ewb.cim.iec61970.base.core.Name
import com.zepben.ewb.cim.iec61970.base.core.NameType
import com.zepben.ewb.database.sql.common.BaseCollectionWriter
import com.zepben.ewb.services.common.BaseService

/**
 * A base class for writing object from a [BaseService] into the database.
 *
 * @property writer The [BaseServiceWriter] used to actually write the objects to the database.
 */
internal abstract class BaseServiceWriter<TService : BaseService>(
    protected open val writer: CimWriter
) : BaseCollectionWriter<TService>() {

    final override fun write(data: TService): Boolean =
        data.writeNameTypes() and
            data.writeService()

    /**
     * Write the service specific objects to the database.
     *
     * @receiver The [BaseService] to write to the database.
     * @return true if the objects were successfully written to the database, otherwise false
     */
    protected abstract fun TService.writeService(): Boolean

    /**
     * Write each object of the specified type using the provided [writer].
     *
     * @receiver The [BaseService] to write to the database.
     * @param T The type of object to write to the database.
     * @param writer The callback used to write the objects to the database. Will be called once for each object and should return true if the object is
     *   successfully written to the database.
     *
     * @return true if all objects are successfully written to the database, otherwise false.
     */
    protected inline fun <reified T : Identifiable> TService.writeEach(noinline writer: (T) -> Boolean): Boolean {
        var status = true
        sequenceOf<T>().forEach { status = status && validateWrite(it, writer) }
        return status
    }

    /**
     * Validate that an object is actually written to the database, logging an error if anything goes wrong.
     *
     * @param T The type of object being written.
     * @param it The object being written.
     * @param writer The callback actually saving the object to the database.
     *
     * @return true if the object is successfully written to the database, otherwise false.
     */
    protected inline fun <reified T : Identifiable> validateWrite(it: T, noinline writer: (T) -> Boolean): Boolean {
        return validateWrite(it, writer) { e ->
            logger.error("Failed to write ${it.typeNameAndMRID()}: ${e.message}")
        }
    }

    private fun TService.writeNameTypes(): Boolean {
        var status = true

        nameTypes.forEach {
            status = status and validateWrite(it, writer::write)

            it.names.forEach { name ->
                status = status and validateWrite(name, writer::write)
            }
        }

        return status
    }

    private fun validateWrite(nameType: NameType, writer: (nameType: NameType) -> Boolean): Boolean {
        return validateWrite(nameType, writer) { e ->
            logValidationError(nameType, nameType.name, e)
        }
    }

    private fun validateWrite(name: Name, writer: (name: Name) -> Boolean): Boolean {
        return validateWrite(name, writer) { e ->
            logValidationError(name, name.name, e)
        }
    }

    private fun logValidationError(obj: Any, desc: String, e: Exception) =
        logger.error("Failed to write ${obj.javaClass.simpleName} $desc: ${e.message}")

}

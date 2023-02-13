/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.common

import com.zepben.evolve.cim.iec61970.base.core.IdentifiedObject
import com.zepben.evolve.cim.iec61970.base.core.Name
import com.zepben.evolve.cim.iec61970.base.core.NameType
import com.zepben.evolve.services.common.BaseService
import com.zepben.evolve.services.common.extensions.typeNameAndMRID
import kotlin.reflect.KClass

abstract class BaseServiceWriter<T : BaseService, W : BaseCIMWriter>(
    val service: T,
    val writer: W,
    protected val hasCommon: (String) -> Boolean,
    protected val addCommon: (String) -> Boolean
) : BaseCollectionWriter() {

    final override fun save(): Boolean {
        return saveNameTypes() and doSave()
    }

    protected abstract fun doSave(): Boolean

    inline fun <reified S : IdentifiedObject> saveEach(saver: (S) -> Boolean): Boolean {
        var status = true
        service.sequenceOf<S>().forEach { status = status && validateSave(it, saver) }
        return status
    }

    private fun saveNameTypes(): Boolean {
        var status = true
        service.nameTypes.forEach {
            val typeNameId = "NameType:${it.name}"
            if (!hasCommon(typeNameId)) {
                status = status and (validateSave(it, writer::save) && addCommon(typeNameId))
            }

            it.names.forEach { name ->
                val nameId = "Name:${name.type.name}:${name.name}:${name.identifiedObject.mRID}"
                if (!hasCommon(nameId)) {
                    status = status and (validateSave(name, writer::save) && addCommon(nameId))
                }
            }
        }

        return status
    }

    protected inline fun <reified S : IdentifiedObject> trySaveCommon(save: (S) -> Boolean, obj: S): Boolean {
        if (hasCommon(obj.mRID))
            return true

        if (!validateSave(obj, save))
            return false

        return addCommon(obj.mRID)
    }

    inline fun <reified T : IdentifiedObject> validateSave(
        it: T,
        saver: (T) -> Boolean
    ): Boolean {
        return WriteValidator.validateSave(it, saver) { e ->
            logger.error("Failed to save ${it.typeNameAndMRID()}: ${e.message}")
        }
    }

    inline fun <reified T : IdentifiedObject> validateSave(
        clazz: KClass<T>,
        it: T,
        saver: (T) -> Boolean
    ): Boolean {
        return WriteValidator.validateSave(it, saver) { e ->
            logger.error("Failed to save ${it.typeNameAndMRID()}: ${e.message}")
        }
    }

    private inline fun validateSave(
        nameType: NameType,
        saver: (nameType: NameType) -> Boolean
    ): Boolean {
        return WriteValidator.validateSave(nameType, saver) { e ->
            logger.error("Failed to save ${nameType.javaClass.simpleName} ${nameType.name}: ${e.message}")
        }
    }

    private inline fun validateSave(
        name: Name,
        saver: (name: Name) -> Boolean
    ): Boolean {
        return WriteValidator.validateSave(name, saver) { e ->
            logger.error("Failed to save ${name.javaClass.simpleName} ${name.name}: ${e.message}")
        }
    }

}

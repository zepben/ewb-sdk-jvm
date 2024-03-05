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

abstract class BaseServiceWriter<S : BaseService, W : BaseCIMWriter>(
    val service: S,
    val writer: W
) : BaseCollectionWriter() {

    final override fun save(): Boolean =
        saveNameTypes()
            .andDoSave()

    protected abstract fun doSave(): Boolean

    protected inline fun <reified T : IdentifiedObject> Boolean.andSaveEach(noinline saver: (T) -> Boolean): Boolean =
        this and saveEach(saver)

    protected inline fun <reified T : IdentifiedObject> saveEach(noinline saver: (T) -> Boolean): Boolean {
        var status = true
        service.sequenceOf<T>().forEach { status = status && validateSave(it, saver) }
        return status
    }

    private fun saveNameTypes(): Boolean {
        var status = true

        service.nameTypes.forEach {
            status = status and validateSave(it, writer::save)

            it.names.forEach { name ->
                status = status and validateSave(name, writer::save)
            }
        }

        return status
    }

    protected inline fun <reified T : IdentifiedObject> validateSave(it: T, noinline saver: (T) -> Boolean): Boolean {
        return validateSave(it, saver) { e ->
            logger.error("Failed to save ${it.typeNameAndMRID()}: ${e.message}")
        }
    }

    private fun validateSave(nameType: NameType, saver: (nameType: NameType) -> Boolean): Boolean {
        return validateSave(nameType, saver) { e ->
            logger.error("Failed to save ${nameType.javaClass.simpleName} ${nameType.name}: ${e.message}")
        }
    }

    private fun validateSave(name: Name, saver: (name: Name) -> Boolean): Boolean {
        return validateSave(name, saver) { e ->
            logger.error("Failed to save ${name.javaClass.simpleName} ${name.name}: ${e.message}")
        }
    }

    private fun Boolean.andDoSave(): Boolean =
        this and doSave()

}

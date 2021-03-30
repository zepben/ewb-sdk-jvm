/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.writers

import com.zepben.evolve.database.sqlite.writers.WriteValidator.validateSave
import com.zepben.evolve.services.common.meta.MetadataCollection
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class MetadataCollectionWriter {

    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    fun save(metadataCollection: MetadataCollection, writer: MetaDataEntryWriter): Boolean {
        var status = true

        metadataCollection.dataSources.forEach {
            status = status and validateSave(it, writer::save) { e -> logger.error("Failed to save DataSource '${it.source}': ${e.message}") }
        }

        return status
    }

}

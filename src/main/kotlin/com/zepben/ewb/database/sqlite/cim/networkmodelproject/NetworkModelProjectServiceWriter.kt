/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.networkmodelproject

import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.ChangeSet
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.DataSet
import com.zepben.ewb.database.sqlite.cim.BaseServiceWriter
import com.zepben.ewb.services.networkmodelproject.NetworkModelProjectService

/**
 * A class for writing a [NetworkModelProjectService] into the database.
 *
 * @param databaseTables The [NetworkModelProjectDatabaseTables] to add to the database.
 */
internal class NetworkModelProjectServiceWriter(
    databaseTables: NetworkModelProjectDatabaseTables,
    override val writer: NetworkModelProjectCimWriter = NetworkModelProjectCimWriter(databaseTables)
) : BaseServiceWriter<NetworkModelProjectService>(writer) {

    override fun NetworkModelProjectService.writeService(): Boolean =
        writeEachDataSet<ChangeSet>(writer::write)

    protected inline fun <reified T : DataSet> NetworkModelProjectService.writeEachDataSet(noinline writer: (T) -> Boolean): Boolean {
        var status = true
        sequenceOf<T>().forEach { status = status && validateWrite(it, writer) }
        return status
    }

    protected inline fun <reified T : DataSet> validateWrite(it: T, noinline writer: (T) -> Boolean): Boolean {
        return validateWrite(it, writer) { e ->
            logger.error("Failed to write ${it.typeNameAndMRID()}: ${e.message}")
        }
    }

}

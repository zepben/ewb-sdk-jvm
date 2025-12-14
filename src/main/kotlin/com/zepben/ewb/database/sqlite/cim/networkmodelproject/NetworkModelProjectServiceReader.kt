/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.networkmodelproject

import com.zepben.ewb.database.sql.BaseCollectionReader
import com.zepben.ewb.database.sqlite.cim.tables.iec61970.infiec61970.part303.genericdataset.TableChangeSets
import com.zepben.ewb.services.networkmodelproject.NetworkModelProjectService
import java.sql.Connection

internal class NetworkModelProjectServiceReader(
        databaseTables: NetworkModelProjectDatabaseTables,
        connection: Connection,
        val reader: NetworkModelProjectCimReader = NetworkModelProjectCimReader()
    ) : BaseCollectionReader<NetworkModelProjectService>(databaseTables, connection) {

    override fun read(data: NetworkModelProjectService): Boolean =
        readService(data)

    fun readService(service: NetworkModelProjectService): Boolean =
        readEach<TableChangeSets>(service, reader::read)

}
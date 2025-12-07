/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.changeset

import com.zepben.ewb.database.sql.cim.BaseServiceReader
import com.zepben.ewb.database.sql.cim.tables.iec61970.infiec61970.part303.genericdataset.TableChangeSets
import com.zepben.ewb.services.variant.VariantService
import java.sql.Connection

/**
 * A class for reading the [VariantService] from the database.
 *
 * @param databaseTables The tables available in the database.
 * @param connection A connection to the database.
 */
internal class ChangeSetServiceReader(
    databaseTables: ChangeSetDatabaseTables,
    connection: Connection,
    override val reader: ChangeSetCimReader = ChangeSetCimReader()
    ) : BaseServiceReader<VariantService>(databaseTables, connection, reader) {

    override fun readService(service: VariantService): Boolean =
        readEach<TableChangeSets>(service, reader::read)

}

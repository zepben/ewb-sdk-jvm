/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.variant

import com.zepben.ewb.database.sql.cim.BaseServiceReader
import com.zepben.ewb.database.sql.cim.tables.extensions.iec61970.infpart303.networkmodelprojects.TableNetworkModelProjects
import com.zepben.ewb.database.sql.cim.tables.iec61970.infiec61970.infpart303.networkmodelprojects.TableAnnotatedProjectDependencies
import com.zepben.ewb.services.variant.VariantService
import java.sql.Connection

/**
 * A class for reading the [VariantService] from the database.
 *
 * @param databaseTables The tables available in the database.
 * @param connection A connection to the database.
 */
internal class VariantServiceReader(
    databaseTables: VariantDatabaseTables,
    connection: Connection,
    override val reader: VariantCimReader = VariantCimReader()
) : BaseServiceReader<VariantService>(databaseTables, connection, reader) {

    override fun readService(service: VariantService): Boolean =
        readEach<TableNetworkModelProjects>(service, reader::read) and
            readEach<TableAnnotatedProjectDependencies>(service, reader::read)

}

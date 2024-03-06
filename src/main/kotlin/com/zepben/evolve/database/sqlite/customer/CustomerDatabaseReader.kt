/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.customer

import com.zepben.evolve.database.sqlite.common.BaseDatabaseReader
import com.zepben.evolve.database.sqlite.common.MetadataCollectionReader
import com.zepben.evolve.database.sqlite.upgrade.UpgradeRunner
import com.zepben.evolve.services.common.meta.MetadataCollection
import com.zepben.evolve.services.customer.CustomerService
import java.sql.Connection

/**
 * A class for reading the [CustomerService] objects and [MetadataCollection] from our customer database.
 *
 * @param databaseFile The filename of the database to read.
 * @param metadata The [MetadataCollection] to populate with metadata from the database.
 * @param service The [CustomerService] to populate with CIM objects from the database.
 */
class CustomerDatabaseReader(
    databaseFile: String,
    metadata: MetadataCollection,
    service: CustomerService,
    tables: CustomerDatabaseTables = CustomerDatabaseTables(),
    createMetadataReader: (Connection) -> MetadataCollectionReader = { connection ->
        MetadataCollectionReader(metadata, tables, connection)
    },
    createServiceReader: (Connection) -> CustomerServiceReader = { connection ->
        CustomerServiceReader(service, tables, connection)
    },
    upgradeRunner: UpgradeRunner = UpgradeRunner()
) : BaseDatabaseReader(
    databaseFile,
    createMetadataReader,
    createServiceReader,
    upgradeRunner,
)

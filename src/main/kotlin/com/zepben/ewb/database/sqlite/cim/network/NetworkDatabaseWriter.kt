/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.network

import com.zepben.ewb.database.sqlite.cim.CimDatabaseWriter
import com.zepben.ewb.database.sqlite.cim.metadata.MetadataCollectionWriter
import com.zepben.ewb.services.common.meta.MetadataCollection
import com.zepben.ewb.services.network.NetworkService

/**
 * A class for writing the [NetworkService] objects and [MetadataCollection] to our network database.
 *
 * @param databaseFile the filename of the database to write.
 */
class NetworkDatabaseWriter(
    databaseFile: String
) : CimDatabaseWriter<NetworkDatabaseTables, NetworkService>(
    databaseFile,
    NetworkDatabaseTables(),
    ::MetadataCollectionWriter,
    ::NetworkServiceWriter
)

/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.diagram

import com.zepben.ewb.database.sqlite.cim.CimDatabaseWriter
import com.zepben.ewb.database.sqlite.cim.metadata.MetadataCollectionWriter
import com.zepben.ewb.services.common.meta.MetadataCollection
import com.zepben.ewb.services.diagram.DiagramService

/**
 * A class for writing the [DiagramService] objects and [MetadataCollection] to our diagram database.
 *
 * @param databaseFile the filename of the database to write.
 */
class DiagramDatabaseWriter(
    databaseFile: String
) : CimDatabaseWriter<DiagramDatabaseTables, DiagramService>(
    databaseFile,
    DiagramDatabaseTables(),
    ::MetadataCollectionWriter,
    ::DiagramServiceWriter
)

/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.readers

import com.zepben.evolve.services.common.meta.MetadataCollection
import java.sql.Statement


/**
 * Class for reading the [MetadataCollection] from the database.
 *
 * @property getStatement provider of statements for the connection.
 */
class MetadataCollectionReader constructor(getStatement: () -> Statement) : BaseServiceReader(getStatement) {

    fun load(reader: MetadataEntryReader): Boolean {
        var status = true

        status = status and loadEach("metadata data sources", reader::load)

        return status
    }

}

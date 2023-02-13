/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

/*
 * Copyright (c) Zeppelin Bend Pty Ltd (Zepben) 2023 - All Rights Reserved.
 * Unauthorized use, copy, or distribution of this file or its contents, via any medium is strictly prohibited.
 */

package com.zepben.evolve.database.sqlite.common

import com.zepben.evolve.database.sqlite.tables.iec61970.base.core.TableNameTypes
import com.zepben.evolve.database.sqlite.tables.iec61970.base.core.TableNames
import java.sql.Statement


abstract class BaseServiceReader<R : BaseCIMReader>(
    databaseTables: DatabaseTables,
    getStatement: () -> Statement,
    val reader: R,
) : BaseCollectionReader(databaseTables, getStatement) {

    fun loadNameTypes(reader: BaseCIMReader): Boolean {
        var status = true
        status = status and loadEach("name type", TableNameTypes(), reader::load)

        return status
    }

    fun loadNames(reader: BaseCIMReader): Boolean {
        var status = true
        status = status and loadEach("name", TableNames(), reader::load)

        return status
    }


}

/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim

import com.zepben.ewb.database.sql.BaseDatabaseTables
import com.zepben.ewb.database.sqlite.cim.tables.TableMetadataDataSources
import com.zepben.ewb.database.sqlite.cim.tables.iec61970.base.core.TableNameTypes
import com.zepben.ewb.database.sqlite.cim.tables.iec61970.base.core.TableNames
import com.zepben.ewb.database.sqlite.cim.tables.tableCimVersion
import com.zepben.ewb.database.sqlite.common.SqliteTable

/**
 * The base collection of tables for all our CIM databases.
 */
open class CimDatabaseTables internal constructor() : BaseDatabaseTables() {

    override val includedTables: Sequence<SqliteTable> = sequenceOf(
        tableCimVersion,
        TableMetadataDataSources(),
        TableNameTypes(),
        TableNames()
    )

}

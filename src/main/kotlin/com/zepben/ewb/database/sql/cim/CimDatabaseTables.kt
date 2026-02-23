/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim

import com.zepben.ewb.database.sql.cim.tables.TableMetadataDataSources
import com.zepben.ewb.database.sql.cim.tables.iec61970.base.core.TableNameTypes
import com.zepben.ewb.database.sql.cim.tables.iec61970.base.core.TableNames
import com.zepben.ewb.database.sql.cim.tables.tableCimVersion
import com.zepben.ewb.database.sql.common.BaseDatabaseTables
import com.zepben.ewb.database.sql.common.tables.SqlTable
import com.zepben.ewb.database.sql.common.tables.TableVersion

/**
 * The base collection of tables for all our CIM databases.
 */
abstract class CimDatabaseTables internal constructor(
    tableVersion: TableVersion = tableCimVersion,
) : BaseDatabaseTables() {

    override val includedTables: Sequence<SqlTable> = sequenceOf(
        tableVersion,
        TableMetadataDataSources(),
        TableNameTypes(),
        TableNames()
    )

}

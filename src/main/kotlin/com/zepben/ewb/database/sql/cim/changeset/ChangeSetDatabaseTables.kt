/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.changeset

import com.zepben.ewb.database.sql.cim.CimDatabaseTables
import com.zepben.ewb.database.sql.cim.tables.iec61970.infiec61970.part303.genericdataset.*
import com.zepben.ewb.database.sql.common.tables.SqlTable
import com.zepben.ewb.database.sql.generators.SqlGenerator
import com.zepben.ewb.database.sql.generators.SqliteGenerator

/**
 * The collection of tables for our network model project databases.
 */
class ChangeSetDatabaseTables(
    override val sqlGenerator: SqlGenerator = SqliteGenerator
) : CimDatabaseTables() {

    override val includedTables: Sequence<SqlTable> =
        super.includedTables + sequenceOf(
            TableChangeSets(),
            TableObjectCreations(),
            TableObjectDeletions(),
            TableObjectModifications(),
            TableObjectReverseModifications(),
        )
}

/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.networkmodelproject

import com.zepben.ewb.database.sqlite.cim.CimDatabaseTables
import com.zepben.ewb.database.sqlite.cim.tables.iec61970.infiec61970.part303.genericdataset.TableChangeSetChangeSetMembers
import com.zepben.ewb.database.sqlite.cim.tables.iec61970.infiec61970.part303.genericdataset.TableChangeSets
import com.zepben.ewb.database.sqlite.cim.tables.iec61970.infiec61970.part303.genericdataset.TableObjectCreations
import com.zepben.ewb.database.sqlite.cim.tables.iec61970.infiec61970.part303.genericdataset.TableObjectDeletions
import com.zepben.ewb.database.sqlite.cim.tables.iec61970.infiec61970.part303.genericdataset.TableObjectModifications
import com.zepben.ewb.database.sqlite.cim.tables.iec61970.infiec61970.part303.genericdataset.TableObjectReverseModifications
import com.zepben.ewb.database.sqlite.common.SqliteTable

/**
 * The collection of tables for our network model project databases.
 */
class NetworkModelProjectDatabaseTables : CimDatabaseTables() {

    override val includedTables: Sequence<SqliteTable> =
        super.includedTables + sequenceOf(
            TableChangeSetChangeSetMembers(),
            TableChangeSets(),
            TableObjectCreations(),
            TableObjectDeletions(),
            TableObjectModifications(),
            TableObjectReverseModifications(),
        )
}
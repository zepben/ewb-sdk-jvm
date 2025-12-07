/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.variant

import com.zepben.ewb.database.sql.cim.CimDatabaseTables
import com.zepben.ewb.database.sql.cim.tables.extensions.iec61970.infpart303.networkmodelprojects.TableNetworkModelProjectNetworkModelProjectComponents
import com.zepben.ewb.database.sql.cim.tables.extensions.iec61970.infpart303.networkmodelprojects.TableNetworkModelProjects
import com.zepben.ewb.database.sql.cim.tables.iec61970.infiec61970.infpart303.networkmodelprojects.TableAnnotatedProjectDependencies
import com.zepben.ewb.database.sql.cim.tables.iec61970.infiec61970.infpart303.networkmodelprojects.TableNetworkModelProjectStageEquipmentContainers
import com.zepben.ewb.database.sql.cim.tables.iec61970.infiec61970.infpart303.networkmodelprojects.TableNetworkModelProjectStages
import com.zepben.ewb.database.sql.cim.variant.tables.tableVariantsVersion
import com.zepben.ewb.database.sql.common.tables.SqlTable
import com.zepben.ewb.database.sql.generators.PostgresGenerator
import com.zepben.ewb.database.sql.generators.SqlGenerator

/**
 * The collection of tables for our network model project databases.
 */
class VariantDatabaseTables(
    override val sqlGenerator: SqlGenerator = PostgresGenerator
) : CimDatabaseTables(tableVariantsVersion) {

    override val includedTables: Sequence<SqlTable> =
        super.includedTables + sequenceOf(
            TableNetworkModelProjectNetworkModelProjectComponents(),
            TableNetworkModelProjects(),
            TableAnnotatedProjectDependencies(),
            TableNetworkModelProjectStageEquipmentContainers(),
            TableNetworkModelProjectStages(),
        )

}

/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.postgres.cim.networkmodelproject

import com.zepben.ewb.database.postgres.cim.tables.extensions.iec61970.infpart303.networkmodelprojects.TableNetworkModelProjectNetworkModelProjectComponents
import com.zepben.ewb.database.postgres.cim.tables.extensions.iec61970.infpart303.networkmodelprojects.TableNetworkModelProjects
import com.zepben.ewb.database.postgres.cim.tables.iec61970.infiec61970.infpart303.networkmodelprojects.TableAnnotatedProjectDependencies
import com.zepben.ewb.database.postgres.cim.tables.iec61970.infiec61970.infpart303.networkmodelprojects.TableNetworkModelProjectStageEquipmentContainers
import com.zepben.ewb.database.postgres.cim.tables.iec61970.infiec61970.infpart303.networkmodelprojects.TableNetworkModelProjectStages
import com.zepben.ewb.database.sql.BaseDatabaseTables
import com.zepben.ewb.database.sql.SqlTable

/**
 * The collection of tables for our network model project databases.
 */
class NetworkModelProjectDatabaseTables : BaseDatabaseTables() {

    override val includedTables: Sequence<SqlTable> =
        super.includedTables + sequenceOf(
            TableNetworkModelProjectNetworkModelProjectComponents(),
            TableNetworkModelProjects(),
            TableAnnotatedProjectDependencies(),
            TableNetworkModelProjectStageEquipmentContainers(),
            TableNetworkModelProjectStages(),
        )

}

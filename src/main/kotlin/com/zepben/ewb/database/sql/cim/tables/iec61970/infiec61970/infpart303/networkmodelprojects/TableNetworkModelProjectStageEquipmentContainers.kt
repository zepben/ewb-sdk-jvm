/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.iec61970.infiec61970.infpart303.networkmodelprojects

import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.SqlTable

/**
 * A class representing the association between Equipment and EquipmentContainers.
 *
 * @property NETWORK_MODEL_PROJECT_STAGE_MRID A column storing the mRID of NetworkModelProject.
 * @property EQUIPMENT_CONTAINER_MRID A column storing the mRID of EquipmentContainers.
 */
@Suppress("PropertyName")
class TableNetworkModelProjectStageEquipmentContainers : SqlTable() {

    val NETWORK_MODEL_PROJECT_STAGE_MRID: Column = Column(++columnIndex, "network_model_project_stage_mrid", Column.Type.STRING, Column.Nullable.NOT_NULL)
    val EQUIPMENT_CONTAINER_MRID: Column = Column(++columnIndex, "equipment_container_mrid", Column.Type.STRING, Column.Nullable.NOT_NULL)

    override val name: String = "network_model_project_stage_equipment_containers"

    init {
        addUniqueIndexes(
            listOf(NETWORK_MODEL_PROJECT_STAGE_MRID, EQUIPMENT_CONTAINER_MRID)
        )

        addNonUniqueIndexes(
            listOf(NETWORK_MODEL_PROJECT_STAGE_MRID),
            listOf(EQUIPMENT_CONTAINER_MRID)
        )
    }

}

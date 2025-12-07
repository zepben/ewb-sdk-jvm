/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.postgres.cim.tables.iec61970.infiec61970.infpart303.networkmodelprojects

import com.zepben.ewb.database.postgres.cim.tables.iec61970.base.core.TableIdentifiedObjects
import com.zepben.ewb.database.sql.Column
import com.zepben.ewb.database.sql.Column.Nullable


@Suppress("PropertyName")
class TableNetworkModelProjectStages : TableIdentifiedObjects() {

    val PLANNED_COMMISSION_DATE: Column = Column(++columnIndex, "planned_commission_date", Column.Type.TIMESTAMP, Nullable.NULL)
    val COMMISSIONED_DATE: Column = Column(++columnIndex, "commissioned_date", Column.Type.TIMESTAMP, Nullable.NULL)
    val CONFIDENCE_LEVEL: Column = Column(++columnIndex, "confidence_level", Column.Type.INTEGER, Nullable.NULL)
    val BASE_MODEL_VERSION: Column = Column(++columnIndex, "base_model_version", Column.Type.STRING, Nullable.NULL)
    val LAST_CONFLICT_CHECKED_AT: Column = Column(++columnIndex, "last_conflict_checked_at", Column.Type.TIMESTAMP, Nullable.NULL)
    val USER_COMMENTS: Column = Column(++columnIndex, "user_comments", Column.Type.STRING, Nullable.NULL)
    val CHANGE_SET_MRID: Column = Column(++columnIndex, "change_set_mrid", Column.Type.STRING, Nullable.NULL)
    val DEPENDENT_ON_STAGE_MRID: Column = Column(++columnIndex, "dependent_on_stage_mrid", Column.Type.STRING, Nullable.NULL)
    val DEPENDING_STAGE_MRID: Column = Column(++columnIndex, "depending_stage_mrid", Column.Type.STRING, Nullable.NULL)
    // TODO: conflicts... delete?
    // TODO: EquipmentContainers

    override val name: String = "network_model_project_stages"

    init {
        addUniqueIndexes(listOf(BASE_MODEL_VERSION, CHANGE_SET_MRID))

        addNonUniqueIndexes(
            listOf(BASE_MODEL_VERSION, MRID),
            listOf(CHANGE_SET_MRID, MRID)
        )
    }
}
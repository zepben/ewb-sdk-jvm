/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.iec61970.infiec61970.infpart303.networkmodelprojects

import com.zepben.ewb.database.sql.cim.tables.iec61970.base.core.TableIdentifiedObjects
import com.zepben.ewb.database.sql.common.tables.Column

/**
 * A class representing the NetworkModelProjectStage columns required for the database table.
 *
 * @property PLANNED_COMMISSION_DATE A column storing the date expected for this stage to be commissioned.
 * @property COMMISSIONED_DATE A column storing the date this stage was commissioned.
 * @property CONFIDENCE_LEVEL A column storing the percentage confidence that this project will be committed to.
 * @property BASE_MODEL_VERSION A column storing the version of the base model this stage was imported against.
 * @property LAST_CONFLICT_CHECKED_AT A column storing the time the last conflict check occured.
 * @property USER_COMMENTS A column storing user comments.
 * @property CHANGE_SET_MRID A column storing the mRID of the ChangeSet for this NetworkModelProjectStage.
 */
@Suppress("PropertyName")
class TableNetworkModelProjectStages : TableIdentifiedObjects() {

    val PLANNED_COMMISSION_DATE: Column = Column(++columnIndex, "planned_commission_date", Column.Type.TIMESTAMP, Column.Nullable.NULL)
    val COMMISSIONED_DATE: Column = Column(++columnIndex, "commissioned_date", Column.Type.TIMESTAMP, Column.Nullable.NULL)
    val CONFIDENCE_LEVEL: Column = Column(++columnIndex, "confidence_level", Column.Type.INTEGER, Column.Nullable.NULL)
    val BASE_MODEL_VERSION: Column = Column(++columnIndex, "base_model_version", Column.Type.STRING, Column.Nullable.NULL)
    val LAST_CONFLICT_CHECKED_AT: Column = Column(++columnIndex, "last_conflict_checked_at", Column.Type.TIMESTAMP, Column.Nullable.NULL)
    val USER_COMMENTS: Column = Column(++columnIndex, "user_comments", Column.Type.STRING, Column.Nullable.NULL)
    val CHANGE_SET_MRID: Column = Column(++columnIndex, "change_set_mrid", Column.Type.STRING, Column.Nullable.NULL)

    override val name: String = "network_model_project_stages"

    init {
        addUniqueIndexes(listOf(BASE_MODEL_VERSION, CHANGE_SET_MRID))

        addNonUniqueIndexes(
            listOf(BASE_MODEL_VERSION, MRID),
            listOf(CHANGE_SET_MRID, MRID)
        )
    }
}

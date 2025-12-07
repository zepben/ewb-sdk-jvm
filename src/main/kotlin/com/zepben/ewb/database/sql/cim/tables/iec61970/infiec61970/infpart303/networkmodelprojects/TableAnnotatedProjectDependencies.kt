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
 * A class representing the AnnotatedProjectDependency columns required for the database table.
 *
 * @property DEPENDENCY_TYPE A column storing the dependency relationship between the two classes.
 * @property DEPENDENCY_DEPENDENT_ON_STAGE_MRID A column storing the mRID of the NetworkModelProjectStage representing the "to" stage.
 * @property DEPENDENCY_DEPENDING_STAGE_MRID A column storing the mRID of the NetworkModelProjectStage representing the "from" stage.
 */
@Suppress("PropertyName")
class TableAnnotatedProjectDependencies : TableIdentifiedObjects() {

    val DEPENDENCY_TYPE: Column = Column(++columnIndex, "dependency_type", Column.Type.STRING, Column.Nullable.NOT_NULL)
    val DEPENDENCY_DEPENDENT_ON_STAGE_MRID: Column = Column(++columnIndex, "dependency_dependent_on_stage_mrid", Column.Type.STRING, Column.Nullable.NOT_NULL)
    val DEPENDENCY_DEPENDING_STAGE_MRID: Column = Column(++columnIndex, "dependency_depending_on_stage_mrid", Column.Type.STRING, Column.Nullable.NOT_NULL)

    override val name: String = "annotated_project_dependencies"

    init {
        addUniqueIndexes(
            listOf(
                DEPENDENCY_DEPENDENT_ON_STAGE_MRID,
                DEPENDENCY_DEPENDING_STAGE_MRID,
                DEPENDENCY_TYPE
            )
        )
    }

}

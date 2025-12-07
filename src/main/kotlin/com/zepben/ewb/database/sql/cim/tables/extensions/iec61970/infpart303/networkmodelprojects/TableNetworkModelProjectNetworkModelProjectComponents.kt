/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.extensions.iec61970.infpart303.networkmodelprojects

import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.SqlTable

/**
 * A class representing the association between NetworkModelProject and NetworkModelProjectComponent.
 *
 * @property NETWORK_MODEL_PROJECT_MRID A column storing the mRID of NetworkModelProject.
 * @property NETWORK_MODEL_PROJECT_COMPONENT_MRID A column storing the mRID of NetworkModelProjectComponents.
 */
@Suppress("PropertyName")
class TableNetworkModelProjectNetworkModelProjectComponents : SqlTable() {

    val NETWORK_MODEL_PROJECT_MRID: Column = Column(++columnIndex, "network_model_project_mrid", Column.Type.STRING, Column.Nullable.NOT_NULL)
    val NETWORK_MODEL_PROJECT_COMPONENT_MRID: Column = Column(++columnIndex, "network_model_project_component_mrid", Column.Type.STRING, Column.Nullable.NOT_NULL)

    override val name: String ="network_model_project_network_model_project_components"

    init {
        addUniqueIndexes(
            listOf(NETWORK_MODEL_PROJECT_MRID, NETWORK_MODEL_PROJECT_COMPONENT_MRID)
        )

        addNonUniqueIndexes(
            listOf(NETWORK_MODEL_PROJECT_MRID),
            listOf(NETWORK_MODEL_PROJECT_COMPONENT_MRID)
        )
    }

}

/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.iec61970.infiec61970.part303.genericdataset

import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NULL
import com.zepben.ewb.database.sql.common.tables.Column.Type.STRING

@Suppress("PropertyName")
class TableChangeSets : TableDataSets() {

    val NETWORK_MODEL_PROJECT_STAGE_MRID: Column = Column(++columnIndex, "network_model_project_stage_mrid", STRING, NULL)

    override val name: String = "change_sets"

}

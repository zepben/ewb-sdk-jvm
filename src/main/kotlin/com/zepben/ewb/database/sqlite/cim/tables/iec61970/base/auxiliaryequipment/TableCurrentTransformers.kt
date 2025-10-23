/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.tables.iec61970.base.auxiliaryequipment

import com.zepben.ewb.database.sql.Column
import com.zepben.ewb.database.sql.Column.Nullable.NULL

@Suppress("PropertyName")
class TableCurrentTransformers : TableSensors() {

    val CURRENT_TRANSFORMER_INFO_MRID: Column = Column(++columnIndex, "current_transformer_info_mrid", Column.Type.STRING, NULL)
    val CORE_BURDEN: Column = Column(++columnIndex, "core_burden", Column.Type.INTEGER, NULL)

    override val name: String = "current_transformers"

}

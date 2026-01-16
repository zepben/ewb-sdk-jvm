/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.iec61970.base.auxiliaryequipment

import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NULL

/**
 * A class representing the `CurrentTransformer` columns required for the database table.
 *
 * @property CURRENT_TRANSFORMER_INFO_MRID Datasheet information for this current transformer.
 * @property CORE_BURDEN Power burden of the CT core in watts.
 */
@Suppress("PropertyName")
class TableCurrentTransformers : TableSensors() {

    val CURRENT_TRANSFORMER_INFO_MRID: Column = Column(++columnIndex, "current_transformer_info_mrid", Column.Type.STRING, NULL)
    val CORE_BURDEN: Column = Column(++columnIndex, "core_burden", Column.Type.INTEGER, NULL)

    override val name: String = "current_transformers"

}

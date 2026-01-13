/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.iec61970.base.auxiliaryequipment

import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NOT_NULL
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NULL

/**
 * A class representing the `PotentialTransformer` columns required for the database table.
 *
 * @property POTENTIAL_TRANSFORMER_INFO_MRID Datasheet information for this potential transformer.
 * @property TYPE Potential transformer construction type.
 */
@Suppress("PropertyName")
class TablePotentialTransformers : TableSensors() {

    val POTENTIAL_TRANSFORMER_INFO_MRID: Column = Column(++columnIndex, "potential_transformer_info_mrid", Column.Type.STRING, NULL)
    val TYPE: Column = Column(++columnIndex, "type", Column.Type.STRING, NOT_NULL)

    override val name: String = "potential_transformers"

}

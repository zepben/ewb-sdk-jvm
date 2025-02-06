/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.cim.tables.iec61970.base.auxiliaryequipment

import com.zepben.evolve.database.sql.Column
import com.zepben.evolve.database.sql.Column.Nullable.NOT_NULL
import com.zepben.evolve.database.sql.Column.Nullable.NULL

@Suppress("PropertyName")
class TablePotentialTransformers : TableSensors() {

    val POTENTIAL_TRANSFORMER_INFO_MRID: Column = Column(++columnIndex, "potential_transformer_info_mrid", "TEXT", NULL)
    val TYPE: Column = Column(++columnIndex, "type", "TEXT", NOT_NULL)

    override val name: String = "potential_transformers"

}

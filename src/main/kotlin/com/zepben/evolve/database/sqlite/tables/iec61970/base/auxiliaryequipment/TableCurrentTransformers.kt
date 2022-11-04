/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.tables.iec61970.base.auxiliaryequipment

import com.zepben.evolve.database.sqlite.tables.Column
import com.zepben.evolve.database.sqlite.tables.Column.Nullable.NULL

@Suppress("PropertyName")
class TableCurrentTransformers : TableSensors() {

    val CURRENT_TRANSFORMER_INFO_MRID = Column(++columnIndex, "current_transformer_info_mrid", "TEXT", NULL)
    val CORE_BURDEN = Column(++columnIndex, "core_burden", "INTEGER", NULL)

    override fun name(): String {
        return "current_transformers"
    }

    override val tableClass = this.javaClass
    override val tableClassInstance = this

}

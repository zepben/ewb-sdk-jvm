/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.database.sqlite.tables.iec61970.base.wires

import com.zepben.evolve.database.sqlite.tables.Column
import com.zepben.evolve.database.sqlite.tables.Column.Nullable.NOT_NULL
import com.zepben.evolve.database.sqlite.tables.Column.Nullable.NULL
import com.zepben.evolve.database.sqlite.tables.iec61970.base.core.TableConductingEquipment

@Suppress("PropertyName")
class TablePowerTransformers : TableConductingEquipment() {

    val VECTOR_GROUP = Column(++columnIndex, "vector_group", "TEXT", NOT_NULL)
    val TRANSFORMER_UTILISATION = Column(++columnIndex, "transformer_utilisation", "NUMBER", NULL)
    val CONSTRUCTION_KIND = Column(++columnIndex, "construction_kind", "TEXT", NOT_NULL)
    val FUNCTION = Column(++columnIndex, "function", "TEXT", NOT_NULL)
    val POWER_TRANSFORMER_INFO_MRID = Column(++columnIndex, "power_transformer_info_mrid", "TEXT", NULL)

    override fun name(): String {
        return "power_transformers"
    }

    override val tableClass = this.javaClass
    override val tableClassInstance = this

}

/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.database.sqlite.tables.iec61968.assetinfo

import com.zepben.evolve.database.sqlite.tables.Column
import com.zepben.evolve.database.sqlite.tables.Column.Nullable.NOT_NULL
import com.zepben.evolve.database.sqlite.tables.Column.Nullable.NULL
import com.zepben.evolve.database.sqlite.tables.iec61968.assets.TableAssetInfo

@Suppress("PropertyName")
class TableTransformerEndInfo : TableAssetInfo() {

    val CONNECTION_KIND = Column(++columnIndex, "connection_kind", "TEXT", NOT_NULL)
    val EMERGENCY_S = Column(++columnIndex, "emergency_s", "INT", NOT_NULL)
    val END_NUMBER = Column(++columnIndex, "end_number", "INT", NOT_NULL)
    val INSULATION_U = Column(++columnIndex, "insulation_u", "INT", NOT_NULL)
    val PHASE_ANGLE_CLOCK = Column(++columnIndex, "phase_angle_clock", "INT", NOT_NULL)
    val R = Column(++columnIndex, "r", "NUMBER", NOT_NULL)
    val RATED_S = Column(++columnIndex, "rated_s", "INT", NOT_NULL)
    val RATED_U = Column(++columnIndex, "rated_u", "INT", NOT_NULL)
    val SHORT_TERM_S = Column(++columnIndex, "short_term_s", "INT", NOT_NULL)
    val TRANSFORMER_TANK_INFO_MRID = Column(++columnIndex, "transformer_tank_info_mrid", "TEXT", NULL)

    override fun name(): String {
        return "transformer_end_info"
    }

    override fun nonUniqueIndexColumns(): MutableList<List<Column>> {
        val cols = super.nonUniqueIndexColumns()

        cols.add(listOf(TRANSFORMER_TANK_INFO_MRID))

        return cols
    }

    override val tableClass = this.javaClass
    override val tableClassInstance = this

}

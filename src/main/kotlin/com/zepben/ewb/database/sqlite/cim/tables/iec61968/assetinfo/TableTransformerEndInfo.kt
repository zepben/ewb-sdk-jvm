/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.tables.iec61968.assetinfo

import com.zepben.ewb.database.sql.Column
import com.zepben.ewb.database.sql.Column.Nullable.NOT_NULL
import com.zepben.ewb.database.sql.Column.Nullable.NULL
import com.zepben.ewb.database.sqlite.cim.tables.iec61968.assets.TableAssetInfo

@Suppress("PropertyName")
class TableTransformerEndInfo : TableAssetInfo() {

    val CONNECTION_KIND: Column = Column(++columnIndex, "connection_kind", Column.Type.STRING, NOT_NULL)
    val EMERGENCY_S: Column = Column(++columnIndex, "emergency_s", Column.Type.INTEGER, NULL)
    val END_NUMBER: Column = Column(++columnIndex, "end_number", Column.Type.INTEGER, NOT_NULL)
    val INSULATION_U: Column = Column(++columnIndex, "insulation_u", Column.Type.INTEGER, NULL)
    val PHASE_ANGLE_CLOCK: Column = Column(++columnIndex, "phase_angle_clock", Column.Type.INTEGER, NULL)
    val R: Column = Column(++columnIndex, "r", Column.Type.DOUBLE, NULL)
    val RATED_S: Column = Column(++columnIndex, "rated_s", Column.Type.INTEGER, NULL)
    val RATED_U: Column = Column(++columnIndex, "rated_u", Column.Type.INTEGER, NULL)
    val SHORT_TERM_S: Column = Column(++columnIndex, "short_term_s", Column.Type.INTEGER, NULL)
    val TRANSFORMER_TANK_INFO_MRID: Column = Column(++columnIndex, "transformer_tank_info_mrid", Column.Type.STRING, NULL)
    val ENERGISED_END_NO_LOAD_TESTS: Column = Column(++columnIndex, "energised_end_no_load_tests", Column.Type.STRING, NULL)
    val ENERGISED_END_SHORT_CIRCUIT_TESTS: Column = Column(++columnIndex, "energised_end_short_circuit_tests", Column.Type.STRING, NULL)
    val GROUNDED_END_SHORT_CIRCUIT_TESTS: Column = Column(++columnIndex, "grounded_end_short_circuit_tests", Column.Type.STRING, NULL)
    val OPEN_END_OPEN_CIRCUIT_TESTS: Column = Column(++columnIndex, "open_end_open_circuit_tests", Column.Type.STRING, NULL)
    val ENERGISED_END_OPEN_CIRCUIT_TESTS: Column = Column(++columnIndex, "energised_end_open_circuit_tests", Column.Type.STRING, NULL)

    override val name: String = "transformer_end_info"

    init {
        addNonUniqueIndexes(
            listOf(TRANSFORMER_TANK_INFO_MRID),
            listOf(ENERGISED_END_NO_LOAD_TESTS),
            listOf(ENERGISED_END_SHORT_CIRCUIT_TESTS),
            listOf(GROUNDED_END_SHORT_CIRCUIT_TESTS),
            listOf(OPEN_END_OPEN_CIRCUIT_TESTS),
            listOf(ENERGISED_END_OPEN_CIRCUIT_TESTS)
        )
    }

}

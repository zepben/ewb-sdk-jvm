/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.tables.iec61968.assetinfo

import com.zepben.ewb.database.sql.cim.tables.iec61968.assets.TableAssetInfo
import com.zepben.ewb.database.sql.common.tables.Column
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NOT_NULL
import com.zepben.ewb.database.sql.common.tables.Column.Nullable.NULL

/**
 * A class representing the `TransformerEndInfo` columns required for the database table.
 *
 * @property CONNECTION_KIND Kind of connection.
 * @property EMERGENCY_S Apparent power that the winding can carry under emergency conditions (also called long-term emergency power) in volt amperes (VA).
 * @property END_NUMBER Number for this transformer end, corresponding to the end's order in the PowerTransformer.vectorGroup attribute. Highest voltage
 *                     winding should be 1.
 * @property INSULATION_U Basic insulation level voltage rating in volts (V).
 * @property PHASE_ANGLE_CLOCK Winding phase angle where 360 degrees are represented with clock hours, so the valid values are {0, ..., 11}. For example,
 *                           to express the second winding in code 'Dyn11', set attributes as follows: 'endNumber'=2, 'connectionKind' = Yn and
 *                           'phaseAngleClock' = 11.
 * @property R DC resistance in ohms.
 * @property RATED_S Normal apparent power rating in volt amperes (VA).
 * @property RATED_U Rated voltage: phase-phase for three-phase windings, and either phase-phase or phase-neutral for single-phase windings in volts (V).
 * @property SHORT_TERM_S Apparent power that this winding can carry for a short period of time (in emergency) in volt amperes (VA).
 * @property TRANSFORMER_TANK_INFO_MRID Transformer tank data that this end description is part of.
 * @property ENERGISED_END_NO_LOAD_TESTS All no-load test measurements in which this transformer end was energised.
 * @property ENERGISED_END_SHORT_CIRCUIT_TESTS All short-circuit test measurements in which this transformer end was short-circuited.
 * @property GROUNDED_END_SHORT_CIRCUIT_TESTS All short-circuit test measurements in which this transformer end was energised.
 * @property OPEN_END_OPEN_CIRCUIT_TESTS All open-circuit test measurements in which this transformer end was not excited.
 * @property ENERGISED_END_OPEN_CIRCUIT_TESTS All open-circuit test measurements in which this transformer end was excited.
 */
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

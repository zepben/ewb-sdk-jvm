/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.upgrade.changesets

/*
 * The next ChangeSetValidator should be implemented here as ChangeSet44Validator.
 * Boilerplate for it is commented out for convenience.
 * (Remove this comment after doing so)
 */
//object ChangeSet44Validator : ChangeSetValidator {
//
//    override fun setUpStatements(): List<String> = listOf(
////        "INSERT INTO energy_sources (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, base_voltage_mrid, active_power, reactive_power, voltage_angle, voltage_magnitude, p_max, p_min, r, r0, rn, x, x0, xn) VALUES ('id1', '', '', 0, null, 0, 1, 1, null, null, null, null, null, null, null, null, null, null, null, null, null)",
////        "INSERT INTO energy_sources (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, base_voltage_mrid, active_power, reactive_power, voltage_angle, voltage_magnitude, p_max, p_min, r, r0, rn, x, x0, xn) VALUES ('id2', '', '', 0, null, 0, 1, 1, null, null, null, null, null, null, null, null, null, null, null, null, null)",
////        "INSERT INTO energy_source_phases (mrid, name, description, num_diagram_objects, location_mrid, num_controls, energy_source_mrid, phase) VALUES ('p1', '', '', 0, null, 0, 'id1', 'A')",
//    )
//
//    override fun populateStatements(): List<String> = listOf(
////        "INSERT INTO energy_sources (mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service, in_service, base_voltage_mrid, active_power, reactive_power, voltage_angle, voltage_magnitude, p_max, p_min, r, r0, rn, x, x0, xn, is_external_grid, r_min, rn_min, r0_min, x_min, xn_min, x0_min, r_max, rn_max, r0_max, x_max, xn_max, x0_max) VALUES ('id3', '', '', 0, null, 0, 1, 1, null, null, null, null, null, null, null, null, null, null, null, null, null, 0, null, null, null, null, null, null, null, null, null, null, null, null)",
//    )
//
//    override fun validate(statement: Statement) {
////         ensureIndexes()
////         validateRows(statement, "SELECT * FROM energy_sources", { rs ->
////            assertThat(rs.getString("mrid"), equalTo("id1"))
////            assertThat(rs.getBoolean("is_external_grid"), equalTo(true))
////        }, { rs ->
////            assertThat(rs.getString("mrid"), equalTo("id2"))
////            assertThat(rs.getBoolean("is_external_grid"), equalTo(false))
////        }, { rs ->
////            assertThat(rs.getString("mrid"), equalTo("id3"))
////        })
//    }
//
//    override fun tearDownStatements(): List<String> = listOf(
////        "DELETE FROM energy_sources",
////        "DELETE FROM energy_source_phases",
//    )
//
//}

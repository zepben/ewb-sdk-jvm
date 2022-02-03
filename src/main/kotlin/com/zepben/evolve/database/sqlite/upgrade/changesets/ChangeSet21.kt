/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.database.sqlite.upgrade.changesets

import com.zepben.evolve.database.sqlite.upgrade.ChangeSet

internal fun changeSet21() = ChangeSet(21) {
    listOf(
        *`Create measurement tables`,
        *`Remove num_measurements columns`
    )
}

@Suppress("ObjectPropertyName")
private val `Create measurement tables` = arrayOf(
    "CREATE TABLE analogs(mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, power_system_resource_mrid TEXT NULL, remote_source_mrid TEXT NULL, terminal_mrid TEXT NULL, phases TEXT NOT NULL, unit_symbol TEXT NOT NULL, positive_flow_in BOOLEAN NOT NULL)",
    "CREATE TABLE accumulators(mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, power_system_resource_mrid TEXT NULL, remote_source_mrid TEXT NULL, terminal_mrid TEXT NULL, phases TEXT NOT NULL, unit_symbol TEXT NOT NULL)",
    "CREATE TABLE discretes(mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, power_system_resource_mrid TEXT NULL, remote_source_mrid TEXT NULL, terminal_mrid TEXT NULL, phases TEXT NOT NULL, unit_symbol TEXT NOT NULL)",
    "INSERT INTO analogs (mrid, name, description, num_diagram_objects, power_system_resource_mrid, phases, unit_symbol, positive_flow_in) SELECT mrid, name, description, num_diagram_objects, power_system_resource_mrid, 'ABC', 'NONE', false FROM measurements",
    "DROP TABLE IF EXISTS measurements",
)

@Suppress("ObjectPropertyName")
private val `Remove num_measurements columns` = arrayOf(
    "ALTER TABLE circuits DROP COLUMN num_measurements",
    "ALTER TABLE feeders DROP COLUMN num_measurements",
    "ALTER TABLE substations DROP COLUMN num_measurements",
    "ALTER TABLE sites DROP COLUMN num_measurements",
    "ALTER TABLE energy_source_phases DROP COLUMN num_measurements",
    "ALTER TABLE power_transformers DROP COLUMN num_measurements",
    "ALTER TABLE jumpers DROP COLUMN num_measurements",
    "ALTER TABLE fuses DROP COLUMN num_measurements",
    "ALTER TABLE ac_line_segments DROP COLUMN num_measurements",
    "ALTER TABLE energy_consumers DROP COLUMN num_measurements",
    "ALTER TABLE breakers DROP COLUMN num_measurements",
    "ALTER TABLE reclosers DROP COLUMN num_measurements",
    "ALTER TABLE ratio_tap_changers DROP COLUMN num_measurements",
    "ALTER TABLE linear_shunt_compensators DROP COLUMN num_measurements",
    "ALTER TABLE energy_consumer_phases DROP COLUMN num_measurements",
    "ALTER TABLE junctions DROP COLUMN num_measurements",
    "ALTER TABLE energy_sources DROP COLUMN num_measurements",
    "ALTER TABLE disconnectors DROP COLUMN num_measurements",
    "ALTER TABLE fault_indicators DROP COLUMN num_measurements"
)

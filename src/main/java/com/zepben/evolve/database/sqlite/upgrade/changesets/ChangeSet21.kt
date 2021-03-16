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
        "CREATE TABLE analogs(mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, power_system_resource_mrid TEXT NULL, remote_source_mrid TEXT NULL, terminal_mrid TEXT NULL, phases TEXT NOT NULL, unit_symbol TEXT NOT NULL, positive_flow_in BOOLEAN NOT NULL)",
        "CREATE TABLE accumulators(mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, power_system_resource_mrid TEXT NULL, remote_source_mrid TEXT NULL, terminal_mrid TEXT NULL, phases TEXT NOT NULL, unit_symbol TEXT NOT NULL)",
        "CREATE TABLE discretes(mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, power_system_resource_mrid TEXT NULL, remote_source_mrid TEXT NULL, terminal_mrid TEXT NULL, phases TEXT NOT NULL, unit_symbol TEXT NOT NULL)",
        "INSERT INTO analogs (mrid, name, description, num_diagram_objects, power_system_resource_mrid, phases, unit_symbol, positive_flow_in) SELECT mrid, name, description, num_diagram_objects, power_system_resource_mrid, 'ABC', 'NONE', false FROM measurements",
        "DROP TABLE IF EXISTS measurements"
    )
}

/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.database.sqlite.upgrade

internal fun changeSet15() = ChangeSet(15) {
    listOf(
        "CREATE TABLE circuits(mrid TEXT NOT NULL, name TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, location_mrid TEXT NULL, num_controls INTEGER NOT NULL, num_measurements INTEGER NOT NULL, loop_mrid TEXT NULL)",
        "CREATE TABLE circuits_substations(circuit_mrid TEXT NOT NULL, substation_mrid TEXT NOT NULL)",
        "CREATE TABLE circuits_terminals(circuit_mrid TEXT NOT NULL, terminal_mrid TEXT NOT NULL)",
        "CREATE TABLE loops(mrid TEXT NOT NULL, name TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL)",
        "CREATE TABLE loops_substations(loop_mrid TEXT NOT NULL, substation_mrid TEXT NOT NULL, relationship TEXT NOT NULL)"
    )
}

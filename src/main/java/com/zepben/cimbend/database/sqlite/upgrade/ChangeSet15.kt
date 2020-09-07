/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 * This file is part of evolve-sdk-jvm.
 *
 * evolve-sdk-jvm is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * evolve-sdk-jvm is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with evolve-sdk-jvm.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.zepben.cimbend.database.sqlite.upgrade

internal fun changeSet15() = ChangeSet(15) {
    listOf(
        "CREATE TABLE circuits(mrid TEXT NOT NULL, name TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, location_mrid TEXT NULL, num_controls INTEGER NOT NULL, num_measurements INTEGER NOT NULL, loop_mrid TEXT NULL)",
        "CREATE TABLE circuits_substations(circuit_mrid TEXT NOT NULL, substation_mrid TEXT NOT NULL)",
        "CREATE TABLE circuits_terminals(circuit_mrid TEXT NOT NULL, terminal_mrid TEXT NOT NULL)",
        "CREATE TABLE loops(mrid TEXT NOT NULL, name TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL)",
        "CREATE TABLE loops_substations(loop_mrid TEXT NOT NULL, substation_mrid TEXT NOT NULL, relationship TEXT NOT NULL)"
    )
}

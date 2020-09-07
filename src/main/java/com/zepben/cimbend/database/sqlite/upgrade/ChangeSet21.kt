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

internal fun changeSet21() = ChangeSet(21) {
    listOf(
        "CREATE TABLE analogs(mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, power_system_resource_mrid TEXT NULL, remote_source_mrid TEXT NULL, terminal_mrid TEXT NULL, phases TEXT NOT NULL, unit_symbol TEXT NOT NULL, positive_flow_in BOOLEAN NOT NULL)",
        "CREATE TABLE accumulators(mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, power_system_resource_mrid TEXT NULL, remote_source_mrid TEXT NULL, terminal_mrid TEXT NULL, phases TEXT NOT NULL, unit_symbol TEXT NOT NULL)",
        "CREATE TABLE discretes(mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, power_system_resource_mrid TEXT NULL, remote_source_mrid TEXT NULL, terminal_mrid TEXT NULL, phases TEXT NOT NULL, unit_symbol TEXT NOT NULL)",
        "INSERT INTO analogs (mrid, name, description, num_diagram_objects, power_system_resource_mrid, phases, unit_symbol, positive_flow_in) SELECT mrid, name, description, num_diagram_objects, power_system_resource_mrid, 'ABC', 'NONE', false FROM measurements",
        "DROP TABLE IF EXISTS measurements"
    )
}

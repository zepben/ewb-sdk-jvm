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

internal fun changeSet18() = ChangeSet(18) {
    listOf(
        "ALTER TABLE ac_line_segments ADD description TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE asset_owners ADD description TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE base_voltages ADD description TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE breakers ADD description TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE cable_info ADD description TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE circuits ADD description TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE connectivity_nodes ADD description TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE controls ADD description TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE customer_agreements ADD description TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE customers ADD description TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE diagram_objects ADD description TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE diagrams ADD description TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE disconnectors ADD description TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE energy_consumer_phases ADD description TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE energy_consumers ADD description TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE energy_source_phases ADD description TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE energy_sources ADD description TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE fault_indicators ADD description TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE feeders ADD description TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE fuses ADD description TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE geographical_regions ADD description TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE jumpers ADD description TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE junctions ADD description TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE linear_shunt_compensators ADD description TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE locations ADD description TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE loops ADD description TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE measurements ADD description TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE meters ADD description TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE operational_restrictions ADD description TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE organisations ADD description TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE overhead_wire_info ADD description TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE per_length_sequence_impedances ADD description TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE poles ADD description TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE power_transformer_ends ADD description TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE power_transformers ADD description TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE pricing_structures ADD description TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE ratio_tap_changers ADD description TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE reclosers ADD description TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE remote_controls ADD description TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE remote_sources ADD description TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE sites ADD description TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE streetlights ADD description TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE sub_geographical_regions ADD description TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE substations ADD description TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE tariffs ADD description TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE terminals ADD description TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE usage_points ADD description TEXT NOT NULL DEFAULT ''"
    )
}

/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.database.sqlite.upgrade.changesets

import com.zepben.evolve.database.sqlite.upgrade.ChangeSet

internal fun changeSet29() = ChangeSet(29) {
    listOf(
        *`Convert battery_unit rated_e and stored_e columns to integer`,
        *`Convert power_transformer_ends power_transformer_mrid, R, R0, X and X0 columns to be nullable`,
        *`Create transformer_end_info table`,
        *`Create transformer_tank_info table`,
        *`Create transformer_star_impedance table`
    )
}

@Suppress("ObjectPropertyName")
private val `Convert battery_unit rated_e and stored_e columns to integer` = arrayOf(
    "ALTER TABLE battery_unit RENAME TO _battery_unit_old;",
    """            
    CREATE TABLE battery_unit(
        mrid TEXT NOT NULL, 
        name TEXT NOT NULL, 
        description TEXT NOT NULL, 
        num_diagram_objects INTEGER NOT NULL, 
        location_mrid TEXT NULL, 
        num_controls INTEGER NOT NULL, 
        normally_in_service BOOLEAN, 
        in_service BOOLEAN, 
        power_electronics_connection_mrid TEXT NULL, 
        max_p NUMBER NOT NULL, 
        min_p NUMBER NOT NULL, 
        battery_state TEXT NOT NULL, 
        rated_e INTEGER NOT NULL, 
        stored_e INTEGER NOT NULL
    );
    """,
    """
    INSERT INTO battery_unit (
        mrid, 
        name, 
        description, 
        num_diagram_objects, 
        location_mrid, 
        num_controls, 
        normally_in_service, 
        in_service, 
        power_electronics_connection_mrid, 
        max_p, 
        min_p, 
        battery_state, 
        rated_e, 
        stored_e)
    SELECT 
        mrid, 
        name, 
        description, 
        num_diagram_objects, 
        location_mrid, 
        num_controls, 
        normally_in_service, 
        in_service, 
        power_electronics_connection_mrid, 
        max_p, 
        min_p, 
        battery_state, 
        rated_e, 
        stored_e
    FROM _battery_unit_old;
    """,
    "DROP TABLE _battery_unit_old;",
    "CREATE UNIQUE INDEX battery_unit_mrid ON battery_unit (mrid)",
    "CREATE UNIQUE INDEX battery_unit_power_electronics_connection_mrid ON battery_unit (power_electronics_connection_mrid)",
    "CREATE INDEX battery_unit_name ON battery_unit (name)"
)

@Suppress("ObjectPropertyName")
private val `Convert power_transformer_ends power_transformer_mrid, R, R0, X and X0 columns to be nullable` = arrayOf(
    "ALTER TABLE power_transformer_ends RENAME TO _power_transformer_ends_old;",
    """
    CREATE TABLE power_transformer_ends(
        mrid TEXT NOT NULL, 
        name TEXT NOT NULL, 
        description TEXT NOT NULL, 
        num_diagram_objects INTEGER NOT NULL, 
        end_number INTEGER NOT NULL, 
        terminal_mrid TEXT NULL, 
        base_voltage_mrid TEXT NULL, 
        grounded BOOLEAN NOT NULL, 
        r_ground NUMBER NOT NULL, 
        x_ground NUMBER NOT NULL, 
        star_impedance_mrid TEXT NULL,
        power_transformer_mrid TEXT NULL, 
        connection_kind TEXT NOT NULL, 
        phase_angle_clock INTEGER NOT NULL, 
        b NUMBER NOT NULL, 
        b0 NUMBER NOT NULL, 
        g NUMBER NOT NULL, 
        g0 NUMBER NOT NULL, 
        R NUMBER NULL, 
        R0 NUMBER NULL, 
        rated_s INTEGER NOT NULL, 
        rated_u INTEGER NOT NULL, 
        X NUMBER NULL, 
        X0 NUMBER NULL
    );
    """,
    """
    INSERT INTO power_transformer_ends (
        mrid, 
        name, 
        description, 
        num_diagram_objects, 
        end_number, 
        terminal_mrid, 
        base_voltage_mrid, 
        grounded, 
        r_ground, 
        x_ground, 
        star_impedance_mrid,
        power_transformer_mrid, 
        connection_kind, 
        phase_angle_clock, 
        b, 
        b0, 
        g, 
        g0, 
        R, 
        R0, 
        rated_s, 
        rated_u, 
        X, 
        X0)
    SELECT
        mrid, 
        name, 
        description, 
        num_diagram_objects, 
        end_number, 
        terminal_mrid, 
        base_voltage_mrid, 
        grounded, 
        r_ground, 
        x_ground, 
        null,
        power_transformer_mrid, 
        connection_kind, 
        phase_angle_clock, 
        b, 
        b0, 
        g, 
        g0, 
        R, 
        R0, 
        rated_s, 
        rated_u, 
        X, 
        X0
    FROM _power_transformer_ends_old;
    """,
    "DROP TABLE _power_transformer_ends_old;",
    "CREATE UNIQUE INDEX power_transformer_ends_mrid ON power_transformer_ends (mrid)",
    "CREATE UNIQUE INDEX power_transformer_ends_power_transformer_mrid_end_number ON power_transformer_ends (power_transformer_mrid, end_number)",
    "CREATE INDEX power_transformer_ends_name ON power_transformer_ends (name)",
    "CREATE INDEX power_transformer_ends_power_transformer_mrid ON power_transformer_ends (power_transformer_mrid)",
    "CREATE INDEX power_transformer_ends_star_impedance_mrid ON power_transformer_ends (star_impedance_mrid)"
)

@Suppress("ObjectPropertyName")
private val `Create transformer_end_info table` = arrayOf(
    "CREATE TABLE transformer_end_info(mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, connection_kind TEXT NOT NULL, emergency_s INT NOT NULL, end_number INT NOT NULL, insulation_u INT NOT NULL, phase_angle_clock INT NOT NULL, r NUMBER NOT NULL, rated_s INT NOT NULL, rated_u INT NOT NULL, short_term_s INT NOT NULL, transformer_tank_info_mrid TEXT NULL)",
    "CREATE UNIQUE INDEX transformer_end_info_mrid ON transformer_end_info (mrid)",
    "CREATE INDEX transformer_end_info_name ON transformer_end_info (name)",
    "CREATE INDEX transformer_end_info_transformer_tank_info_mrid ON transformer_end_info (transformer_tank_info_mrid)",
)

@Suppress("ObjectPropertyName")
private val `Create transformer_tank_info table` = arrayOf(
    "CREATE TABLE transformer_tank_info(mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, power_transformer_info_mrid TEXT NULL)",
    "CREATE UNIQUE INDEX transformer_tank_info_mrid ON transformer_tank_info (mrid)",
    "CREATE INDEX transformer_tank_info_name ON transformer_tank_info (name)",
    "CREATE INDEX transformer_tank_info_power_transformer_info_mrid ON transformer_tank_info (power_transformer_info_mrid)",
)

@Suppress("ObjectPropertyName")
private val `Create transformer_star_impedance table` = arrayOf(
    "CREATE TABLE transformer_star_impedance(mrid TEXT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, num_diagram_objects INTEGER NOT NULL, R NUMBER NULL, R0 NUMBER NULL, X NUMBER NULL, X0 NUMBER NULL, transformer_end_info_mrid TEXT NULL)",
    "CREATE UNIQUE INDEX transformer_star_impedance_mrid ON transformer_star_impedance (mrid)",
    "CREATE UNIQUE INDEX transformer_star_impedance_transformer_end_info_mrid ON transformer_star_impedance (transformer_end_info_mrid)",
    "CREATE INDEX transformer_star_impedance_name ON transformer_star_impedance (name)",
)

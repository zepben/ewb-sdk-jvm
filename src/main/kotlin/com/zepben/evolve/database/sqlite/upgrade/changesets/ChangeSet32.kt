/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.database.sqlite.upgrade.changesets

import com.zepben.evolve.database.sqlite.upgrade.ChangeSet

internal fun changeSet32() = ChangeSet(32) {
    listOf(
        *`Change nullability of fields in cable_info table`,
        *`Change nullability of fields in no_load_tests table`,
        *`Change nullability of fields in open_circuit_tests table`,
        *`Change nullability of fields in overhead_wire_info table`,
        *`Change nullability of fields in short_circuit_tests table`,
        *`Change nullability of fields in transformer_end_info table`,
        *`Change nullability of fields in power_electronics_wind_unit table`,
        *`Change nullability of fields in energy_consumer_phases table`,
        *`Change nullability of fields in linear_shunt_compensators table`,
        *`Change nullability of fields in per_length_sequence_impedances table`,
        *`Change nullability of fields in power_electronics_connection table`,
        *`Change nullability of fields in power_electronics_connection_phase table`,
        *`Change nullability of fields in power_transformer_ends table`,
    )
}

@Suppress("ObjectPropertyName")
private val `Change nullability of fields in cable_info table` = arrayOf(
    "ALTER TABLE cable_info RENAME TO cable_info_old",
    """
        CREATE TABLE cable_info (
            mrid TEXT NOT NULL,
            name TEXT NOT NULL,
            description TEXT NOT NULL,
            num_diagram_objects INTEGER NOT NULL,
            rated_current NUMBER NULL,
            material TEXT NOT NULL
        )
        """,
    """
        INSERT INTO cable_info
        SELECT mrid, name, description, num_diagram_objects, rated_current, material
        FROM cable_info_old
        """,
    "DROP TABLE cable_info_old",
    "CREATE UNIQUE INDEX cable_info_mrid ON cable_info (mrid)",
    "CREATE INDEX cable_info_name ON cable_info (name)",
)

@Suppress("ObjectPropertyName")
private val `Change nullability of fields in no_load_tests table` = arrayOf(
    "ALTER TABLE no_load_tests RENAME TO no_load_tests_old",
    """
        CREATE TABLE no_load_tests (
            mrid TEXT NOT NULL,
            name TEXT NOT NULL,
            description TEXT NOT NULL,
            num_diagram_objects INTEGER NOT NULL,
            base_power INTEGER NULL,
            temperature NUMBER NULL,
            energised_end_voltage INTEGER NULL,
            exciting_current NUMBER NULL,
            exciting_current_zero NUMBER NULL,
            loss INTEGER NULL,
            loss_zero INTEGER NULL
        )
        """,
    """
        INSERT INTO no_load_tests
        SELECT mrid, name, description, num_diagram_objects, base_power, temperature, energised_end_voltage, exciting_current, exciting_current_zero, loss, loss_zero
        FROM no_load_tests_old
        """,
    "DROP TABLE no_load_tests_old",
    "CREATE UNIQUE INDEX no_load_tests_mrid ON no_load_tests (mrid)",
    "CREATE INDEX no_load_tests_name ON no_load_tests (name)",
)

@Suppress("ObjectPropertyName")
private val `Change nullability of fields in open_circuit_tests table` = arrayOf(
    "ALTER TABLE open_circuit_tests RENAME TO open_circuit_tests_old",
    """
        CREATE TABLE open_circuit_tests (
            mrid TEXT NOT NULL,
            name TEXT NOT NULL,
            description TEXT NOT NULL,
            num_diagram_objects INTEGER NOT NULL,
            base_power INTEGER NULL,
            temperature NUMBER NULL,
            energised_end_step INTEGER NULL,
            energised_end_voltage INTEGER NULL,
            open_end_step INTEGER NULL,
            open_end_voltage INTEGER NULL,
            phase_shift NUMBER NULL
        )
        """,
    """
        INSERT INTO open_circuit_tests
        SELECT mrid, name, description, num_diagram_objects, base_power, temperature, energised_end_step, energised_end_voltage, open_end_step, open_end_voltage, phase_shift
        FROM open_circuit_tests_old
        """,
    "DROP TABLE open_circuit_tests_old",
    "CREATE UNIQUE INDEX open_circuit_tests_mrid ON open_circuit_tests (mrid)",
    "CREATE INDEX open_circuit_tests_name ON open_circuit_tests (name)",
)

@Suppress("ObjectPropertyName")
private val `Change nullability of fields in overhead_wire_info table` = arrayOf(
    "ALTER TABLE overhead_wire_info RENAME TO overhead_wire_info_old",
    """
        CREATE TABLE overhead_wire_info (
            mrid TEXT NOT NULL,
            name TEXT NOT NULL,
            description TEXT NOT NULL,
            num_diagram_objects INTEGER NOT NULL,
            rated_current NUMBER NULL,
            material TEXT NOT NULL
        )
        """,
    """
        INSERT INTO overhead_wire_info
        SELECT mrid, name, description, num_diagram_objects, rated_current, material
        FROM overhead_wire_info_old
        """,
    "DROP TABLE overhead_wire_info_old",
    "CREATE UNIQUE INDEX overhead_wire_info_mrid ON overhead_wire_info (mrid)",
    "CREATE INDEX overhead_wire_info_name ON overhead_wire_info (name)",
)

@Suppress("ObjectPropertyName")
private val `Change nullability of fields in short_circuit_tests table` = arrayOf(
    "ALTER TABLE short_circuit_tests RENAME TO short_circuit_tests_old",
    """
        CREATE TABLE short_circuit_tests (
            mrid TEXT NOT NULL,
            name TEXT NOT NULL,
            description TEXT NOT NULL,
            num_diagram_objects INTEGER NOT NULL,
            base_power INTEGER NULL,
            temperature NUMBER NULL,
            current NUMBER NULL,
            energised_end_step INTEGER NULL,
            grounded_end_step INTEGER NULL,
            leakage_impedance NUMBER NULL,
            leakage_impedance_zero NUMBER NULL,
            loss INTEGER NULL,
            loss_zero INTEGER NULL,
            power INTEGER NULL,
            voltage NUMBER NULL,
            voltage_ohmic_part NUMBER NULL
        )
        """,
    """
        INSERT INTO short_circuit_tests
        SELECT mrid, name, description, num_diagram_objects, base_power, temperature, current, energised_end_step, grounded_end_step, leakage_impedance, leakage_impedance_zero, loss, loss_zero, power, voltage, voltage_ohmic_part
        FROM short_circuit_tests_old
        """,
    "DROP TABLE short_circuit_tests_old",
    "CREATE UNIQUE INDEX short_circuit_tests_mrid ON short_circuit_tests (mrid)",
    "CREATE INDEX short_circuit_tests_name ON short_circuit_tests (name)",
)

@Suppress("ObjectPropertyName")
private val `Change nullability of fields in transformer_end_info table` = arrayOf(
    "ALTER TABLE transformer_end_info RENAME TO transformer_end_info_old",
    """
        CREATE TABLE transformer_end_info (
            mrid TEXT NOT NULL,
            name TEXT NOT NULL,
            description TEXT NOT NULL,
            num_diagram_objects INTEGER NOT NULL,
            connection_kind TEXT NOT NULL,
            emergency_s INTEGER NULL,
            end_number INTEGER NOT NULL,
            insulation_u INTEGER NULL,
            phase_angle_clock INTEGER NULL,
            r NUMBER NULL,
            rated_s INTEGER NULL,
            rated_u INTEGER NULL,
            short_term_s INTEGER NULL,
            transformer_tank_info_mrid TEXT NULL,
            energised_end_no_load_tests TEXT NULL,
            energised_end_short_circuit_tests TEXT NULL,
            grounded_end_short_circuit_tests TEXT NULL,
            open_end_open_circuit_tests TEXT NULL,
            energised_end_open_circuit_tests TEXT NULL
        )
        """,
    """
        INSERT INTO transformer_end_info
        SELECT mrid, name, description, num_diagram_objects, connection_kind, emergency_s, end_number, insulation_u, phase_angle_clock, r, rated_s, rated_u, short_term_s, transformer_tank_info_mrid, energised_end_no_load_tests, energised_end_short_circuit_tests, grounded_end_short_circuit_tests, open_end_open_circuit_tests, energised_end_open_circuit_tests
        FROM transformer_end_info_old
        """,
    "DROP TABLE transformer_end_info_old",
    "CREATE UNIQUE INDEX transformer_end_info_mrid ON transformer_end_info (mrid)",
    "CREATE INDEX transformer_end_info_name ON transformer_end_info (name)",
    "CREATE INDEX transformer_end_info_transformer_tank_info_mrid ON transformer_end_info (transformer_tank_info_mrid)",
    "CREATE INDEX transformer_end_info_energised_end_no_load_tests ON transformer_end_info (energised_end_no_load_tests)",
    "CREATE INDEX transformer_end_info_energised_end_short_circuit_tests ON transformer_end_info (energised_end_short_circuit_tests)",
    "CREATE INDEX transformer_end_info_grounded_end_short_circuit_tests ON transformer_end_info (grounded_end_short_circuit_tests)",
    "CREATE INDEX transformer_end_info_open_end_open_circuit_tests ON transformer_end_info (open_end_open_circuit_tests)",
    "CREATE INDEX transformer_end_info_energised_end_open_circuit_tests ON transformer_end_info (energised_end_open_circuit_tests)",

    // ########################
    // ### streetlights ###
    // ########################
    "ALTER TABLE streetlights RENAME TO streetlights_old",
    """
        CREATE TABLE streetlights (
            mrid TEXT NOT NULL,
            name TEXT NOT NULL,
            description TEXT NOT NULL,
            num_diagram_objects INTEGER NOT NULL,
            location_mrid TEXT NULL,
            pole_mrid TEXT NULL,
            lamp_kind TEXT NOT NULL,
            light_rating NUMBER NULL
        )
        """,
    """
        INSERT INTO streetlights
        SELECT mrid, name, description, num_diagram_objects, location_mrid, pole_mrid, lamp_kind, light_rating
        FROM streetlights_old
        """,
    "DROP TABLE streetlights_old",
    "CREATE UNIQUE INDEX streetlights_mrid ON streetlights (mrid)",
    "CREATE INDEX streetlights_name ON streetlights (name)",

    // #####################
    // ### customers ###
    // #####################
    "ALTER TABLE customers RENAME TO customers_old",
    """
        CREATE TABLE customers (
            mrid TEXT NOT NULL,
            name TEXT NOT NULL,
            description TEXT NOT NULL,
            num_diagram_objects INTEGER NOT NULL,
            organisation_mrid TEXT NULL,
            kind TEXT NOT NULL,
            num_end_devices INTEGER NULL
        )
        """,
    """
        INSERT INTO customers
        SELECT mrid, name, description, num_diagram_objects, organisation_mrid, kind, num_end_devices
        FROM customers_old
        """,
    "DROP TABLE customers_old",
    "CREATE UNIQUE INDEX customers_mrid ON customers (mrid)",
    "CREATE INDEX customers_name ON customers (name)",

    // ####################
    // ### battery_unit ###
    // ####################
    "ALTER TABLE battery_unit RENAME TO battery_unit_old",
    """
        CREATE TABLE battery_unit (
            mrid TEXT NOT NULL,
            name TEXT NOT NULL,
            description TEXT NOT NULL,
            num_diagram_objects INTEGER NOT NULL,
            location_mrid TEXT NULL,
            num_controls INTEGER NOT NULL,
            normally_in_service BOOLEAN, in_service BOOLEAN, power_electronics_connection_mrid TEXT NULL,
            max_p NUMBER NULL,
            min_p NUMBER NULL,
            battery_state TEXT NOT NULL,
            rated_e INTEGER NULL,
            stored_e INTEGER NULL
        )
        """,
    """
        INSERT INTO battery_unit
        SELECT mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service BOOLEAN, in_service BOOLEAN, power_electronics_connection_mrid, max_p, min_p, battery_state, rated_e, stored_e
        FROM battery_unit_old
        """,
    "DROP TABLE battery_unit_old",
    "CREATE UNIQUE INDEX battery_unit_mrid ON battery_unit (mrid)",
    "CREATE UNIQUE INDEX battery_unit_power_electronics_connection_mrid ON battery_unit (power_electronics_connection_mrid)",
    "CREATE INDEX battery_unit_name ON battery_unit (name)",

    // ##########################
    // ### photo_voltaic_unit ###
    // ##########################
    "ALTER TABLE photo_voltaic_unit RENAME TO photo_voltaic_unit_old",
    """
        CREATE TABLE photo_voltaic_unit (
            mrid TEXT NOT NULL,
            name TEXT NOT NULL,
            description TEXT NOT NULL,
            num_diagram_objects INTEGER NOT NULL,
            location_mrid TEXT NULL,
            num_controls INTEGER NOT NULL,
            normally_in_service BOOLEAN, in_service BOOLEAN, power_electronics_connection_mrid TEXT NULL,
            max_p NUMBER NULL,
            min_p NUMBER NULL
        )
        """,
    """
        INSERT INTO photo_voltaic_unit
        SELECT mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service BOOLEAN, in_service BOOLEAN, power_electronics_connection_mrid, max_p, min_p
        FROM photo_voltaic_unit_old
        """,
    "DROP TABLE photo_voltaic_unit_old",
    "CREATE UNIQUE INDEX photo_voltaic_unit_mrid ON photo_voltaic_unit (mrid)",
    "CREATE UNIQUE INDEX photo_voltaic_unit_power_electronics_connection_mrid ON photo_voltaic_unit (power_electronics_connection_mrid)",
    "CREATE INDEX photo_voltaic_unit_name ON photo_voltaic_unit (name)",
)

@Suppress("ObjectPropertyName")
private val `Change nullability of fields in power_electronics_wind_unit table` = arrayOf(
    "ALTER TABLE power_electronics_wind_unit RENAME TO power_electronics_wind_unit_old",
    """
        CREATE TABLE power_electronics_wind_unit (
            mrid TEXT NOT NULL,
            name TEXT NOT NULL,
            description TEXT NOT NULL,
            num_diagram_objects INTEGER NOT NULL,
            location_mrid TEXT NULL,
            num_controls INTEGER NOT NULL,
            normally_in_service BOOLEAN, in_service BOOLEAN, power_electronics_connection_mrid TEXT NULL,
            max_p NUMBER NULL,
            min_p NUMBER NULL
        )
        """,
    """
        INSERT INTO power_electronics_wind_unit
        SELECT mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service BOOLEAN, in_service BOOLEAN, power_electronics_connection_mrid, max_p, min_p
        FROM power_electronics_wind_unit_old
        """,
    "DROP TABLE power_electronics_wind_unit_old",
    "CREATE UNIQUE INDEX power_electronics_wind_unit_mrid ON power_electronics_wind_unit (mrid)",
    "CREATE UNIQUE INDEX power_electronics_wind_unit_power_electronics_connection_mrid ON power_electronics_wind_unit (power_electronics_connection_mrid)",
    "CREATE INDEX power_electronics_wind_unit_name ON power_electronics_wind_unit (name)",
)

@Suppress("ObjectPropertyName")
private val `Change nullability of fields in energy_consumer_phases table` = arrayOf(
    "ALTER TABLE energy_consumer_phases RENAME TO energy_consumer_phases_old",
    """
        CREATE TABLE energy_consumer_phases (
            mrid TEXT NOT NULL,
            name TEXT NOT NULL,
            description TEXT NOT NULL,
            num_diagram_objects INTEGER NOT NULL,
            location_mrid TEXT NULL,
            num_controls INTEGER NOT NULL,
            energy_consumer_mrid TEXT NOT NULL,
            phase TEXT NOT NULL,
            p NUMBER NULL,
            q NUMBER NULL,
            p_fixed NUMBER NULL,
            q_fixed NUMBER NULL
        )
        """,
    """
        INSERT INTO energy_consumer_phases
        SELECT mrid, name, description, num_diagram_objects, location_mrid, num_controls, energy_consumer_mrid, phase, p, q, p_fixed, q_fixed
        FROM energy_consumer_phases_old
        """,
    "DROP TABLE energy_consumer_phases_old",
    "CREATE UNIQUE INDEX energy_consumer_phases_mrid ON energy_consumer_phases (mrid)",
    "CREATE UNIQUE INDEX energy_consumer_phases_energy_consumer_mrid_phase ON energy_consumer_phases (energy_consumer_mrid, phase)",
    "CREATE INDEX energy_consumer_phases_name ON energy_consumer_phases (name)",
    "CREATE INDEX energy_consumer_phases_energy_consumer_mrid ON energy_consumer_phases (energy_consumer_mrid)",

    // ########################
    // ### energy_consumers ###
    // ########################
    "ALTER TABLE energy_consumers RENAME TO energy_consumers_old",
    """
        CREATE TABLE energy_consumers (
            mrid TEXT NOT NULL,
            name TEXT NOT NULL,
            description TEXT NOT NULL,
            num_diagram_objects INTEGER NOT NULL,
            location_mrid TEXT NULL,
            num_controls INTEGER NOT NULL,
            normally_in_service BOOLEAN, in_service BOOLEAN, base_voltage_mrid TEXT NULL,
            customer_count INTEGER NULL,
            grounded BOOLEAN NOT NULL,
            p NUMBER NULL,
            q NUMBER NULL,
            p_fixed NUMBER NULL,
            q_fixed NUMBER NULL,
            phase_connection TEXT NOT NULL
        )
        """,
    """
        INSERT INTO energy_consumers
        SELECT mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service BOOLEAN, in_service BOOLEAN, base_voltage_mrid, customer_count, grounded, p, q, p_fixed, q_fixed, phase_connection
        FROM energy_consumers_old
        """,
    "DROP TABLE energy_consumers_old",
    "CREATE UNIQUE INDEX energy_consumers_mrid ON energy_consumers (mrid)",
    "CREATE INDEX energy_consumers_name ON energy_consumers (name)",

    // ######################
    // ### energy_sources ###
    // ######################
    "ALTER TABLE energy_sources RENAME TO energy_sources_old",
    """
        CREATE TABLE energy_sources (
            mrid TEXT NOT NULL,
            name TEXT NOT NULL,
            description TEXT NOT NULL,
            num_diagram_objects INTEGER NOT NULL,
            location_mrid TEXT NULL,
            num_controls INTEGER NOT NULL,
            normally_in_service BOOLEAN, in_service BOOLEAN, base_voltage_mrid TEXT NULL,
            active_power NUMBER NULL,
            reactive_power NUMBER NULL,
            voltage_angle NUMBER NULL,
            voltage_magnitude NUMBER NULL,
            p_max NUMBER NULL,
            p_min NUMBER NULL,
            r NUMBER NULL,
            r0 NUMBER NULL,
            rn NUMBER NULL,
            x NUMBER NULL,
            x0 NUMBER NULL,
            xn NUMBER NULL
        )
        """,
    """
        INSERT INTO energy_sources
        SELECT mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service BOOLEAN, in_service BOOLEAN, base_voltage_mrid, active_power, reactive_power, voltage_angle, voltage_magnitude, p_max, p_min, r, r0, rn, x, x0, xn
        FROM energy_sources_old
        """,
    "DROP TABLE energy_sources_old",
    "CREATE UNIQUE INDEX energy_sources_mrid ON energy_sources (mrid)",
    "CREATE INDEX energy_sources_name ON energy_sources (name)",
)

@Suppress("ObjectPropertyName")
private val `Change nullability of fields in linear_shunt_compensators table` = arrayOf(
    "ALTER TABLE linear_shunt_compensators RENAME TO linear_shunt_compensators_old",
    """
        CREATE TABLE linear_shunt_compensators (
            mrid TEXT NOT NULL,
            name TEXT NOT NULL,
            description TEXT NOT NULL,
            num_diagram_objects INTEGER NOT NULL,
            location_mrid TEXT NULL,
            num_controls INTEGER NOT NULL,
            normally_in_service BOOLEAN, in_service BOOLEAN, base_voltage_mrid TEXT NULL,
            control_enabled BOOLEAN, grounded BOOLEAN NOT NULL,
            nom_u INTEGER NULL,
            phase_connection TEXT NOT NULL,
            sections NUMBER NULL,
            b0_per_section NUMBER NULL,
            b_per_section NUMBER NULL,
            g0_per_section NUMBER NULL,
            g_per_section NUMBER NULL
        )
        """,
    """
        INSERT INTO linear_shunt_compensators
        SELECT mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service BOOLEAN, in_service BOOLEAN, base_voltage_mrid, control_enabled BOOLEAN, grounded, nom_u, phase_connection, sections, b0_per_section, b_per_section, g0_per_section, g_per_section
        FROM linear_shunt_compensators_old
        """,
    "DROP TABLE linear_shunt_compensators_old",
    "CREATE UNIQUE INDEX linear_shunt_compensators_mrid ON linear_shunt_compensators (mrid)",
    "CREATE INDEX linear_shunt_compensators_name ON linear_shunt_compensators (name)",
)

@Suppress("ObjectPropertyName")
private val `Change nullability of fields in per_length_sequence_impedances table` = arrayOf(
    "ALTER TABLE per_length_sequence_impedances RENAME TO per_length_sequence_impedances_old",
    """
        CREATE TABLE per_length_sequence_impedances (
            mrid TEXT NOT NULL,
            name TEXT NOT NULL,
            description TEXT NOT NULL,
            num_diagram_objects INTEGER NOT NULL,
            r NUMBER NULL,
            x NUMBER NULL,
            r0 NUMBER NULL,
            x0 NUMBER NULL,
            bch NUMBER NULL,
            gch NUMBER NULL,
            b0ch NUMBER NULL,
            g0ch NUMBER NULL
        )
        """,
    """
        INSERT INTO per_length_sequence_impedances
        SELECT mrid, name, description, num_diagram_objects, r, x, r0, x0, bch, gch, b0ch, g0ch
        FROM per_length_sequence_impedances_old
        """,
    "DROP TABLE per_length_sequence_impedances_old",
    "CREATE UNIQUE INDEX per_length_sequence_impedances_mrid ON per_length_sequence_impedances (mrid)",
    "CREATE INDEX per_length_sequence_impedances_name ON per_length_sequence_impedances (name)",
)

@Suppress("ObjectPropertyName")
private val `Change nullability of fields in power_electronics_connection table` = arrayOf(
    "ALTER TABLE power_electronics_connection RENAME TO power_electronics_connection_old",
    """
        CREATE TABLE power_electronics_connection (
            mrid TEXT NOT NULL,
            name TEXT NOT NULL,
            description TEXT NOT NULL,
            num_diagram_objects INTEGER NOT NULL,
            location_mrid TEXT NULL,
            num_controls INTEGER NOT NULL,
            normally_in_service BOOLEAN, in_service BOOLEAN, base_voltage_mrid TEXT NULL,
            control_enabled BOOLEAN, max_i_fault NUMBER NULL,
            max_q NUMBER NULL,
            min_q NUMBER NULL,
            p NUMBER NULL,
            q NUMBER NULL,
            rated_s NUMBER NULL,
            rated_u NUMBER NULL
        )
        """,
    """
        INSERT INTO power_electronics_connection
        SELECT mrid, name, description, num_diagram_objects, location_mrid, num_controls, normally_in_service BOOLEAN, in_service BOOLEAN, base_voltage_mrid, control_enabled BOOLEAN, max_i_fault, max_q, min_q, p, q, rated_s, rated_u
        FROM power_electronics_connection_old
        """,
    "DROP TABLE power_electronics_connection_old",
    "CREATE UNIQUE INDEX power_electronics_connection_mrid ON power_electronics_connection (mrid)",
    "CREATE INDEX power_electronics_connection_name ON power_electronics_connection (name)",
)

@Suppress("ObjectPropertyName")
private val `Change nullability of fields in power_electronics_connection_phase table` = arrayOf(
    "ALTER TABLE power_electronics_connection_phase RENAME TO power_electronics_connection_phase_old",
    """
        CREATE TABLE power_electronics_connection_phase (
            mrid TEXT NOT NULL,
            name TEXT NOT NULL,
            description TEXT NOT NULL,
            num_diagram_objects INTEGER NOT NULL,
            location_mrid TEXT NULL,
            num_controls INTEGER NOT NULL,
            power_electronics_connection_mrid TEXT NULL,
            p NUMBER NULL,
            phase TEXT NOT NULL,
            q NUMBER NULL
        )
        """,
    """
        INSERT INTO power_electronics_connection_phase
        SELECT mrid, name, description, num_diagram_objects, location_mrid, num_controls, power_electronics_connection_mrid, p, phase, q
        FROM power_electronics_connection_phase_old
        """,
    "DROP TABLE power_electronics_connection_phase_old",
    "CREATE UNIQUE INDEX power_electronics_connection_phase_mrid ON power_electronics_connection_phase (mrid)",
    "CREATE UNIQUE INDEX power_electronics_connection_phase_power_electronics_connection_mrid ON power_electronics_connection_phase (power_electronics_connection_mrid)",
    "CREATE INDEX power_electronics_connection_phase_name ON power_electronics_connection_phase (name)",
)

@Suppress("ObjectPropertyName")
private val `Change nullability of fields in power_transformer_ends table` = arrayOf(
    "ALTER TABLE power_transformer_ends RENAME TO power_transformer_ends_old",
    """
        CREATE TABLE power_transformer_ends (
            mrid TEXT NOT NULL,
            name TEXT NOT NULL,
            description TEXT NOT NULL,
            num_diagram_objects INTEGER NOT NULL,
            end_number INTEGER NOT NULL,
            terminal_mrid TEXT NULL,
            base_voltage_mrid TEXT NULL,
            grounded BOOLEAN NOT NULL,
            r_ground NUMBER NULL,
            x_ground NUMBER NULL,
            star_impedance_mrid TEXT NULL,
            power_transformer_mrid TEXT NULL,
            connection_kind TEXT NOT NULL,
            phase_angle_clock INTEGER NULL,
            b NUMBER NULL,
            b0 NUMBER NULL,
            g NUMBER NULL,
            g0 NUMBER NULL,
            R NUMBER NULL,
            R0 NUMBER NULL,
            rated_s INTEGER NULL,
            rated_u INTEGER NULL,
            X NUMBER NULL,
            X0 NUMBER NULL
        )
        """,
    """
        INSERT INTO power_transformer_ends
        SELECT mrid, name, description, num_diagram_objects, end_number, terminal_mrid, base_voltage_mrid, grounded, r_ground, x_ground, star_impedance_mrid, power_transformer_mrid, connection_kind, phase_angle_clock, b, b0, g, g0, R, R0, rated_s, rated_u, X, X0
        FROM power_transformer_ends_old
        """,
    "DROP TABLE power_transformer_ends_old",
    "CREATE UNIQUE INDEX power_transformer_ends_mrid ON power_transformer_ends (mrid)",
    "CREATE UNIQUE INDEX power_transformer_ends_power_transformer_mrid_end_number ON power_transformer_ends (power_transformer_mrid, end_number)",
    "CREATE INDEX power_transformer_ends_name ON power_transformer_ends (name)",
    "CREATE INDEX power_transformer_ends_star_impedance_mrid ON power_transformer_ends (star_impedance_mrid)",
    "CREATE INDEX power_transformer_ends_power_transformer_mrid ON power_transformer_ends (power_transformer_mrid)",

    // ##########################
    // ### ratio_tap_changers ###
    // ##########################
    "ALTER TABLE ratio_tap_changers RENAME TO ratio_tap_changers_old",
    """
        CREATE TABLE ratio_tap_changers (
            mrid TEXT NOT NULL,
            name TEXT NOT NULL,
            description TEXT NOT NULL,
            num_diagram_objects INTEGER NOT NULL,
            location_mrid TEXT NULL,
            num_controls INTEGER NOT NULL,
            control_enabled BOOLEAN NOT NULL,
            high_step INTEGER NULL,
            low_step INTEGER NULL,
            neutral_step INTEGER NULL,
            neutral_u INTEGER NULL,
            normal_step INTEGER NULL,
            step NUMBER NULL,
            transformer_end_mrid TEXT NULL,
            step_voltage_increment NUMBER NULL
        )
        """,
    """
        INSERT INTO ratio_tap_changers
        SELECT mrid, name, description, num_diagram_objects, location_mrid, num_controls, control_enabled, high_step, low_step, neutral_step, neutral_u, normal_step, step, transformer_end_mrid, step_voltage_increment
        FROM ratio_tap_changers_old
        """,
    "DROP TABLE ratio_tap_changers_old",
    "CREATE UNIQUE INDEX ratio_tap_changers_mrid ON ratio_tap_changers (mrid)",
    "CREATE UNIQUE INDEX ratio_tap_changers_transformer_end_mrid ON ratio_tap_changers (transformer_end_mrid)",
    "CREATE INDEX ratio_tap_changers_name ON ratio_tap_changers (name)",
)

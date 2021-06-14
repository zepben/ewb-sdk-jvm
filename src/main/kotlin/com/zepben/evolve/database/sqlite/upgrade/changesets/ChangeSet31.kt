/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.database.sqlite.upgrade.changesets

import com.zepben.evolve.database.sqlite.upgrade.ChangeSet

internal fun changeSet31() = ChangeSet(31) {
    listOf(
        """CREATE TABLE no_load_tests (
            mrid TEXT NOT NULL,
            name TEXT NOT NULL,
            description TEXT NOT NULL,
            num_diagram_objects INTEGER NOT NULL,
            base_power INTEGER NOT NULL,
            temperature NUMBER NOT NULL,
            energised_end_voltage INTEGER NOT NULL,
            exciting_current NUMBER NOT NULL,
            exciting_current_zero NUMBER NOT NULL,
            loss INTEGER NOT NULL,
            loss_zero INTEGER NOT NULL
        )""",
        "CREATE UNIQUE INDEX no_load_tests_mrid ON no_load_tests (mrid)",
        "CREATE INDEX no_load_tests_name ON no_load_tests (name)",

        """CREATE TABLE open_circuit_tests (
            mrid TEXT NOT NULL,
            name TEXT NOT NULL,
            description TEXT NOT NULL,
            num_diagram_objects INTEGER NOT NULL,
            base_power INTEGER NOT NULL,
            temperature NUMBER NOT NULL,
            energised_end_step INTEGER NOT NULL,
            energised_end_voltage INTEGER NOT NULL,
            open_end_step INTEGER NOT NULL,
            open_end_voltage INTEGER NOT NULL,
            phase_shift NUMBER NOT NULL
        )""",
        "CREATE UNIQUE INDEX open_circuit_tests_mrid ON open_circuit_tests (mrid)",
        "CREATE INDEX open_circuit_tests_name ON open_circuit_tests (name)",

        """CREATE TABLE short_circuit_tests (
            mrid TEXT NOT NULL,
            name TEXT NOT NULL,
            description TEXT NOT NULL,
            num_diagram_objects INTEGER NOT NULL,
            base_power INTEGER NOT NULL,
            temperature NUMBER NOT NULL,
            current INTEGER NOT NULL,
            energised_end_step INTEGER NOT NULL,
            grounded_end_step INTEGER NOT NULL,
            leakage_impedance NUMBER NOT NULL,
            leakage_impedance_zero NUMBER NOT NULL,
            loss INTEGER NOT NULL,
            loss_zero INTEGER NOT NULL,
            power INTEGER NOT NULL,
            voltage NUMBER NOT NULL,
            voltage_ohmic_part NUMBER NOT NULL
        )""",
        "CREATE UNIQUE INDEX short_circuit_tests_mrid ON short_circuit_tests (mrid)",
        "CREATE INDEX short_circuit_tests_name ON short_circuit_tests (name)",

        "ALTER TABLE transformer_end_info ADD energised_end_no_load_tests TEXT NULL",
        "ALTER TABLE transformer_end_info ADD energised_end_short_circuit_tests TEXT NULL",
        "ALTER TABLE transformer_end_info ADD grounded_end_short_circuit_tests TEXT NULL",
        "ALTER TABLE transformer_end_info ADD open_end_open_circuit_tests TEXT NULL",
        "ALTER TABLE transformer_end_info ADD energised_end_open_circuit_tests TEXT NULL",

        "CREATE INDEX transformer_end_info_energised_end_no_load_tests ON transformer_end_info (energised_end_no_load_tests)",
        "CREATE INDEX transformer_end_info_energised_end_short_circuit_tests ON transformer_end_info (energised_end_short_circuit_tests)",
        "CREATE INDEX transformer_end_info_grounded_end_short_circuit_tests ON transformer_end_info (grounded_end_short_circuit_tests)",
        "CREATE INDEX transformer_end_info_open_end_open_circuit_tests ON transformer_end_info (open_end_open_circuit_tests)",
        "CREATE INDEX transformer_end_info_energised_end_open_circuit_tests ON transformer_end_info (energised_end_open_circuit_tests)",

        "CREATE UNIQUE INDEX energy_consumer_phases_energy_consumer_mrid_phase ON energy_consumer_phases (energy_consumer_mrid, phase)"
    )
}

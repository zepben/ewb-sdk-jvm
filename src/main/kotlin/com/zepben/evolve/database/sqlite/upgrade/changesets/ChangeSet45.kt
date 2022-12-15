/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.upgrade.changesets

import com.zepben.evolve.database.sqlite.upgrade.ChangeSet

internal fun changeSet45() = ChangeSet(45) {
    listOf(
        *`Create new columns on existing tables`,
        *`Create switch_info table`,
        *`Create current_relay_info table`,
        *`Create current_relays table`,
        *`Create reclose_sequences table`,
        *`Create protection_equipment_protected_switches table`,
    )
}

@Suppress("ObjectPropertyName")
private val `Create new columns on existing tables` = arrayOf(
    "ALTER TABLE breakers ADD switch_info_mrid TEXT NULL;",
    "ALTER TABLE disconnectors ADD switch_info_mrid TEXT NULL;",
    "ALTER TABLE fuses ADD switch_info_mrid TEXT NULL;",
    "ALTER TABLE jumpers ADD switch_info_mrid TEXT NULL;",
    "ALTER TABLE load_break_switches ADD switch_info_mrid TEXT NULL;",
    "ALTER TABLE reclosers ADD switch_info_mrid TEXT NULL;",

    "ALTER TABLE breakers ADD rated_current INTEGER NULL;",
    "ALTER TABLE disconnectors ADD rated_current INTEGER NULL;",
    "ALTER TABLE fuses ADD rated_current INTEGER NULL;",
    "ALTER TABLE jumpers ADD rated_current INTEGER NULL;",
    "ALTER TABLE load_break_switches ADD rated_current INTEGER NULL;",
    "ALTER TABLE reclosers ADD rated_current INTEGER NULL;",

    "ALTER TABLE breakers ADD breaking_capacity INTEGER NULL;",
    "ALTER TABLE load_break_switches ADD breaking_capacity INTEGER NULL;",
    "ALTER TABLE reclosers ADD breaking_capacity INTEGER NULL;",

    "ALTER TABLE breakers ADD in_transit_time NUMBER NULL;",
)

@Suppress("ObjectPropertyName")
private val `Create switch_info table` = arrayOf(
    """
    CREATE TABLE switch_info (
        mrid TEXT NOT NULL,
        name TEXT NOT NULL,
        description TEXT NOT NULL,
        num_diagram_objects INTEGER NOT NULL,
        rated_interrupting_time NUMBER NULL
    );
    """.trimIndent(),
    "CREATE UNIQUE INDEX switch_info_mrid ON switch_info (mrid);",
    "CREATE INDEX switch_info_name ON switch_info (name);"
)

@Suppress("ObjectPropertyName")
private val `Create current_relay_info table` = arrayOf(
    """
    CREATE TABLE current_relay_info (
        mrid TEXT NOT NULL,
        name TEXT NOT NULL,
        description TEXT NOT NULL,
        num_diagram_objects INTEGER NOT NULL,
        curve_setting TEXT 
    );
    """.trimIndent(),
    "CREATE UNIQUE INDEX current_relay_info_mrid ON current_relay_info (mrid);",
    "CREATE INDEX current_relay_info_name ON current_relay_info (name);"
)

@Suppress("ObjectPropertyName")
private val `Create current_relays table` = arrayOf(
    """
    CREATE TABLE current_relays (
        mrid TEXT NOT NULL,
        name TEXT NOT NULL,
        description TEXT NOT NULL,
        num_diagram_objects INTEGER NOT NULL,
        location_mrid TEXT NULL,
        num_controls INTEGER NOT NULL,
        normally_in_service BOOLEAN,
        in_service BOOLEAN,
        relay_delay_time NUMBER NULL,
        protection_kind TEXT NOT NULL,
        current_limit_1 NUMBER NULL,
        inverse_time_flag BOOLEAN NULL,
        time_delay_1 NUMBER NULL,
        current_relay_info_mrid TEXT NULL
    );
    """.trimIndent(),
    "CREATE UNIQUE INDEX current_relays_mrid ON current_relays (mrid);",
    "CREATE INDEX current_relays_name ON current_relays (name);"
)

@Suppress("ObjectPropertyName")
private val `Create reclose_sequences table` = arrayOf(
    """
    CREATE TABLE reclose_sequences (
        mrid TEXT NOT NULL,
        name TEXT NOT NULL,
        description TEXT NOT NULL,
        num_diagram_objects INTEGER NOT NULL,
        protected_switch_mrid TEXT NOT NULL,
        reclose_delay NUMBER NULL,
        reclose_step INTEGER NULL
    );
    """.trimIndent(),
    "CREATE UNIQUE INDEX reclose_sequences_mrid ON reclose_sequences (mrid);",
    "CREATE INDEX reclose_sequences_name ON reclose_sequences (name);"
)

@Suppress("ObjectPropertyName")
private val `Create protection_equipment_protected_switches table` = arrayOf(
    """
    CREATE TABLE protection_equipment_protected_switches (
        protection_equipment_mrid TEXT NOT NULL,
        protected_switch_mrid TEXT NOT NULL
    );
    """.trimIndent(),
    """
    CREATE UNIQUE INDEX protection_equipment_protected_switches_protection_equipment_mrid_protected_switch_mrid
    ON protection_equipment_protected_switches (protection_equipment_mrid, protected_switch_mrid);
    """.trimIndent(),
    "CREATE INDEX protection_equipment_protected_switches_protection_equipment_mrid ON protection_equipment_protected_switches (protection_equipment_mrid);",
    "CREATE INDEX protection_equipment_protected_switches_protected_switch_mrid ON protection_equipment_protected_switches (protected_switch_mrid);"
)

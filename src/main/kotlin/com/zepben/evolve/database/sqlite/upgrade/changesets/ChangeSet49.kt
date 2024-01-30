/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.upgrade.changesets

import com.zepben.evolve.database.sqlite.upgrade.ChangeSet

internal fun changeSet49() = ChangeSet(
    49,
    sql
)

// TODO fill in column specs for new tables and add association tables
private val sql = listOf(
    """CREATE TABLE distance_relays (
        
    );""".trimMargin(),
    "CREATE UNIQUE INDEX distance_relays_mrid ON distance_relays (mrid);",
    "CREATE INDEX distance_relays_name ON distance_relays (name);",

    """CREATE TABLE protection_relay_schemes (
        
    );""".trimMargin(),
    "CREATE UNIQUE INDEX protection_relay_schemes_mrid ON protection_relay_schemes (mrid);",
    "CREATE INDEX protection_relay_schemes_name ON protection_relay_schemes (name);",

    """CREATE TABLE protection_relay_systems (
        
    );""".trimMargin(),
    "CREATE UNIQUE INDEX protection_relay_systems_mrid ON protection_relay_systems (mrid);",
    "CREATE INDEX protection_relay_systems_name ON protection_relay_systems (name);",

    """CREATE TABLE voltage_relays (
        
    );""".trimMargin(),
    "CREATE UNIQUE INDEX voltage_relays_mrid ON voltage_relays (mrid);",
    "CREATE INDEX voltage_relays_name ON voltage_relays (name);",

    """CREATE TABLE grounds (
        
    );""".trimMargin(),
    "CREATE UNIQUE INDEX grounds_mrid ON grounds (mrid);",
    "CREATE INDEX grounds_name ON grounds (name);",

    """CREATE TABLE ground_disconnectors (
        
    );""".trimMargin(),
    "CREATE UNIQUE INDEX ground_disconnectors_mrid ON ground_disconnectors (mrid);",
    "CREATE INDEX ground_disconnectors_name ON ground_disconnectors (name);",

    """CREATE TABLE series_compensators (
        
    );""".trimMargin(),
    "CREATE UNIQUE INDEX series_compensators_mrid ON series_compensators (mrid);",
    "CREATE INDEX series_compensators_name ON series_compensators (name);",

    "ALTER TABLE reclose_delays RENAME COLUMN current_relay_info_mrid to relay_info_mrid;",
    "ALTER TABLE current_relay_info RENAME TO relay_info;",
    "ALTER TABLE current_relays ADD model TEXT NULL;",
    "ALTER TABLE current_relays ADD reclosing BOOLEAN NULL;",
    "ALTER TABLE fuses ADD function_mrid TEXT NULL;",
    "ALTER TABLE regulating_controls ADD rated_current NUMBER NULL;",
    "DROP TABLE protection_equipment_protected_switches;"
)

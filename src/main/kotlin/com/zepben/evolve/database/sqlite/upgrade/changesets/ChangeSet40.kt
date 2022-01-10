/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.database.sqlite.upgrade.changesets

import com.zepben.evolve.database.sqlite.upgrade.ChangeSet

internal fun changeSet40() = ChangeSet(40) {
    listOf(
        *`Drop power_electronics_connection_mrid column from power electronics connection phase table`,
        *`Drop power_electronics_connection_mrid column from battery unit table`,
        *`Drop power_electronics_connection_mrid column from photo voltaic unit table`,
        *`Drop power_electronics_connection_mrid column from power electronics wind unit table`,
        *`Create association table for power electronics connection and power electronics connection phase`,
        *`Create association table for power electronics connection and power electronics unit`
    )
}

//TODO Make sure Original association is moved to the new association tables
@Suppress("ObjectPropertyName")
private val `Drop power_electronics_connection_mrid column from power electronics connection phase table` = arrayOf(
    "DROP INDEX power_electronics_connection_phase_power_electronics_connection_mrid",
    "ALTER TABLE power_electronics_connection_phase DROP COLUMN power_electronics_connection_mrid",
)

@Suppress("ObjectPropertyName")
private val `Drop power_electronics_connection_mrid column from battery unit table` = arrayOf(
    "DROP INDEX battery_unit_power_electronics_connection_mrid",
    "ALTER TABLE battery_unit DROP COLUMN power_electronics_connection_mrid",
)

@Suppress("ObjectPropertyName")
private val `Drop power_electronics_connection_mrid column from photo voltaic unit table` = arrayOf(

    "DROP INDEX photo_voltaic_unit_power_electronics_connection_mrid",
    "ALTER TABLE photo_voltaic_unit DROP COLUMN power_electronics_connection_mrid",
)

@Suppress("ObjectPropertyName")
private val `Drop power_electronics_connection_mrid column from power electronics wind unit table` = arrayOf(
    "DROP INDEX power_electronics_wind_unit_power_electronics_connection_mrid",
    "ALTER TABLE power_electronics_wind_unit DROP COLUMN power_electronics_connection_mrid",
)

@Suppress("ObjectPropertyName")
private val `Create association table for power electronics connection and power electronics connection phase` = arrayOf(
    """
        CREATE TABLE power_electronics_connections_power_electronics_connection_phases (
            power_electronics_connection_mrid TEXT NOT NULL,
            power_electronics_connection_phase_mrid TEXT NOT NULL
        )
    """,
    "CREATE UNIQUE INDEX power_electronics_connections_power_electronics_connection_phases_pecmrid_pecpmrid " +
        "ON power_electronics_connections_power_electronics_connection_phases (power_electronics_connection_mrid, power_electronics_connection_phase_mrid)",
    "CREATE INDEX power_electronics_connections_power_electronics_connection_phases_pecmrid " +
        "ON power_electronics_connections_power_electronics_connection_phases (power_electronics_connection_mrid)",
    "CREATE INDEX power_electronics_connections_power_electronics_connection_phases_pecpmrid " +
        "ON power_electronics_connections_power_electronics_connection_phases (power_electronics_connection_phase_mrid)",
)

@Suppress("ObjectPropertyName")
private val `Create association table for power electronics connection and power electronics unit` = arrayOf(
    """
        CREATE TABLE power_electronics_connections_power_electronics_units (
            power_electronics_connection_mrid TEXT NOT NULL,
            power_electronics_unit_mrid TEXT NOT NULL
        )
    """,
    "CREATE UNIQUE INDEX power_electronics_connections_power_electronics_units_pecmrid_peumrid ON " +
        "power_electronics_connections_power_electronics_units (power_electronics_connection_mrid, power_electronics_unit_mrid)",
    "CREATE INDEX power_electronics_connections_power_electronics_units_pecmrid ON " +
        "power_electronics_connections_power_electronics_units (power_electronics_connection_mrid)",
    "CREATE INDEX power_electronics_connections_power_electronics_units_peumrid ON " +
        "power_electronics_connections_power_electronics_units (power_electronics_unit_mrid)",
)

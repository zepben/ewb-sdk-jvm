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
        *`Change unique index of power_electronics_connection_mrid on battery unit table to non-unique`,
        *`Change unique index of power_electronics_connection_mrid on photo voltaic unit table to non-unique`,
        *`Change unique index of power_electronics_connection_mrid on power electronics wind unit table to non-unique`,
        *`Change unique index of power_electronics_connection_mrid on power electronics connection phase table to non-unique`

    )
}

@Suppress("ObjectPropertyName")
private val `Change unique index of power_electronics_connection_mrid on battery unit table to non-unique` = arrayOf(

    "DROP INDEX battery_unit_power_electronics_connection_mrid",
    "CREATE INDEX battery_unit_power_electronics_connection_mrid ON battery_unit (power_electronics_connection_mrid)"

)


@Suppress("ObjectPropertyName")
private val `Change unique index of power_electronics_connection_mrid on photo voltaic unit table to non-unique` = arrayOf(

    "DROP INDEX photo_voltaic_unit_power_electronics_connection_mrid",
    "CREATE INDEX photo_voltaic_unit_power_electronics_connection_mrid ON photo_voltaic_unit (power_electronics_connection_mrid)"

)


@Suppress("ObjectPropertyName")
private val `Change unique index of power_electronics_connection_mrid on power electronics wind unit table to non-unique` = arrayOf(

    "DROP INDEX power_electronics_wind_unit_power_electronics_connection_mrid",
    "CREATE INDEX power_electronics_wind_unit_power_electronics_connection_mrid ON power_electronics_wind_unit (power_electronics_connection_mrid)"

)


@Suppress("ObjectPropertyName")
private val `Change unique index of power_electronics_connection_mrid on power electronics connection phase table to non-unique` = arrayOf(

    "DROP INDEX power_electronics_connection_phase_power_electronics_connection_mrid",
    "CREATE INDEX power_electronics_connection_phase_power_electronics_connection_mrid ON power_electronics_connection_phase (power_electronics_connection_mrid)"

)
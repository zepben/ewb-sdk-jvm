/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.database.sqlite.upgrade.changesets

import com.zepben.evolve.database.sqlite.upgrade.ChangeSet

internal fun changeSet38() = ChangeSet(38) {
    listOf(
        *`Change base_voltage column to nominal_voltage in base_voltage table`,
        *`Change power_system_resource_mrid column to control_mrid in remote_controls table`,
        *`Change power_system_resource_mrid column to measurement_mrid in remote_sources table`
    )
}

@Suppress("ObjectPropertyName")
private val `Change base_voltage column to nominal_voltage in base_voltage table` = arrayOf(
    "ALTER TABLE base_voltages RENAME base_voltage TO nominal_voltage"
)

@Suppress("ObjectPropertyName")
private val `Change power_system_resource_mrid column to control_mrid in remote_controls table` = arrayOf(
    "ALTER TABLE remote_controls RENAME power_system_resource_mrid TO control_mrid"
)

@Suppress("ObjectPropertyName")
private val `Change power_system_resource_mrid column to measurement_mrid in remote_sources table` = arrayOf(
    "ALTER TABLE remote_sources RENAME power_system_resource_mrid TO measurement_mrid"
)

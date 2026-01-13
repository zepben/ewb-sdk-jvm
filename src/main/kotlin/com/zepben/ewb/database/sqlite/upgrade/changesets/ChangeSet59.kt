/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.upgrade.changesets

import com.zepben.ewb.database.paths.DatabaseType
import com.zepben.ewb.database.sqlite.upgrade.Change
import com.zepben.ewb.database.sqlite.upgrade.ChangeSet

internal fun changeSet59() = ChangeSet(
    59,
    listOf(
        // Network Change
        `Create asset to power system resource association table`
    )
)

// ###################
// # Network Changes #
// ###################

@Suppress("ObjectPropertyName")
private val `Create asset to power system resource association table` = Change(
    listOf(
        """CREATE TABLE assets_power_system_resources (
            asset_mrid TEXT NOT_NULL,
            power_system_resource_mrid TEXT NOT_NULL
        );""".trimIndent(),
        "CREATE UNIQUE INDEX asset_mrid_power_system_resource_mrid ON assets_power_system_resources (asset_mrid, power_system_resource_mrid);",
        "CREATE INDEX assets_power_system_resources_asset_mrid ON assets_power_system_resources (asset_mrid);",
        "CREATE INDEX assets_power_system_resources_power_system_resource_mrid ON assets_power_system_resources (power_system_resource_mrid);"
    ),
    targetDatabases = setOf(DatabaseType.NETWORK_MODEL)
)

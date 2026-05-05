/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.upgrade.changesets

import com.zepben.ewb.database.paths.DatabaseType
import com.zepben.ewb.database.sqlite.upgrade.Change
import com.zepben.ewb.database.sqlite.upgrade.ChangeSet

internal fun changeSet65() = ChangeSet(
    65,
    listOf(
        `Add normal feeder direction to terminals table`,
    )
)

// ###################
// # Network Changes #
// ###################

@Suppress("ObjectPropertyName")
private val `Add normal feeder direction to terminals table` = Change(
    listOf(
        "ALTER TABLE terminals ADD COLUMN normal_feeder_direction TEXT NULL;",
    ),
    targetDatabases = setOf(DatabaseType.NETWORK_MODEL)
)


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

internal fun changeSet53() = ChangeSet(
    53,
    listOf(
        // Network Changes

        `Add design rating columns for conductors`
    )
)

// ###################
// # Network Changes #
// ###################

@Suppress("ObjectPropertyName")
private val `Add design rating columns for conductors` = Change(
    listOf(
        "ALTER TABLE ac_line_segments ADD COLUMN design_temperature INTEGER NULL;",
        "ALTER TABLE ac_line_segments ADD COLUMN design_rating NUMBER NULL;",
    ),
    targetDatabases = setOf(DatabaseType.NETWORK_MODEL)
)

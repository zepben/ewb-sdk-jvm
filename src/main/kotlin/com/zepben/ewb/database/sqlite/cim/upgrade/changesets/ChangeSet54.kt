/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.upgrade.changesets

import com.zepben.ewb.database.paths.DatabaseType
import com.zepben.ewb.database.sqlite.cim.upgrade.Change
import com.zepben.ewb.database.sqlite.cim.upgrade.ChangeSet

internal fun changeSet54() = ChangeSet(
    54,
    listOf(
        // Customer Changes

        `Add special need columns for customers`
    )
)

// ###################
// # Customer Changes #
// ###################

@Suppress("ObjectPropertyName")
private val `Add special need columns for customers` = Change(
    listOf(
        "ALTER TABLE customers ADD COLUMN special_need TEXT NULL;"
    ),
    targetDatabases = setOf(DatabaseType.CUSTOMER)
)

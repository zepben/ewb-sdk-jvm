/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.cim.upgrade.changesets

import com.zepben.evolve.database.paths.DatabaseType
import com.zepben.evolve.database.sqlite.cim.upgrade.Change
import com.zepben.evolve.database.sqlite.cim.upgrade.ChangeSet

internal fun changeSet56() = ChangeSet(
    56,
    listOf(
        // Network Changes.
        `Change switch rated_current to float`,
    )
)

// ###################
// # Network Changes #
// ###################

@Suppress("ObjectPropertyName")
private val `Change switch rated_current to float` = Change(
    listOf(
        "breakers",
        "disconnectors",
        "fuses",
        "ground_disconnectors",
        "jumpers",
        "load_break_switches",
        "reclosers",
    ).flatMap { table ->
        listOf(
            "ALTER TABLE $table ADD COLUMN new_rated_current NUMBER NULL DEFAULT NULL;",
            "UPDATE $table SET new_rated_current = rated_current * 1.0 where rated_current IS NOT NULL;",
            "ALTER TABLE $table DROP COLUMN rated_current;",
            "ALTER TABLE $table RENAME COLUMN new_rated_current TO rated_current;",
        )
    },
    targetDatabases = setOf(DatabaseType.NETWORK_MODEL)
)

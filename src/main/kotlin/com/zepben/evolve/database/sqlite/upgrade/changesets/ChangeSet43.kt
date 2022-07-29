/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.database.sqlite.upgrade.changesets

import com.zepben.evolve.database.sqlite.upgrade.ChangeSet

internal fun changeSet43() = ChangeSet(43) {
    listOf(
        *`Add lv feeder table`,
        *`Add feeder to lv feeder association table`
    )
}

@Suppress("ObjectPropertyName")
private val `Add lv feeder table` = arrayOf(
    """
    CREATE TABLE lv_feeders (
        mrid TEXT NOT NULL,
        name TEXT NOT NULL,
        description TEXT NOT NULL DEFAULT '',
        num_diagram_objects INTEGER NOT NULL,
        location_mrid TEXT NULL,
        num_controls INTEGER NOT NULL,
        normal_head_terminal_mrid TEXT NULL
    )
    """
)

@Suppress("ObjectPropertyName")
private val `Add feeder to lv feeder association table` = arrayOf(
    """
    CREATE TABLE feeder_lv_feeders (
        feeder_mrid TEXT NOT NULL,
        lv_feeder_mrid TEXT NOT NULL
    )
    """
)

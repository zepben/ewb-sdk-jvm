/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.database.sqlite.upgrade.changesets

import com.zepben.evolve.database.sqlite.upgrade.ChangeSet

internal fun changeSet39() = ChangeSet(39) {
    listOf(
        *`Add power transformer fields`
    )
}

@Suppress("ObjectPropertyName")
private val `Add power transformer fields` = arrayOf(
    "ALTER TABLE power_transformers ADD construction_kind TEXT NOT NULL",
    "ALTER TABLE power_transformers ADD function TEXT NOT NULL"
)

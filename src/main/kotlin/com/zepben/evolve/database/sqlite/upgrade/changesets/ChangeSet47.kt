/*
 * Copyright 2023 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.upgrade.changesets

import com.zepben.evolve.database.sqlite.upgrade.ChangeSet

internal fun changeSet47() = ChangeSet(
    47,
    `Delete all reclose sequences`
)

// Non-null columns cannot be added to existing tables without specifying a default, which cannot be removed later.
// This has the side effect of allowing the field to be unspecified when inserting new entries, unlike other non-null fields in our schema.
// This is why an entire new table is created instead, with the values from the old table copied over.
// We can revisit this when SQLite adds proper support for ALTER COLUMN.
@Suppress("ObjectPropertyName")

private val `Delete all reclose sequences` = listOf(
    "DROP INDEX IF EXISTS reclose_sequences_mrid;",
    "DROP INDEX IF EXISTS reclose_sequences_name;",
    "DROP TABLE IF EXISTS reclose_sequences;",
)

/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.database.sqlite.upgrade.changesets

import com.zepben.evolve.database.sqlite.upgrade.ChangeSet

internal fun changeSet30() = ChangeSet(30) {
    listOf(
        *`Create names table`,
        *`Create name_types table`
    )
}

@Suppress("ObjectPropertyName")
private val `Create names table` = arrayOf(
    "CREATE TABLE names(name TEXT NOT NULL, identified_object_mrid TEXT NOT NULL, name_type_name TEXT NOT NULL)",
    "CREATE UNIQUE INDEX names_identified_object_mrid_name_type_name_name ON names (identified_object_mrid, name_type_name, name)",
    "CREATE INDEX names_identified_object_mrid ON names (identified_object_mrid)",
    "CREATE INDEX names_name ON names (name)",
    "CREATE INDEX names_name_type_name ON names (name_type_name)",
)

@Suppress("ObjectPropertyName")
private val `Create name_types table` = arrayOf(
    "CREATE TABLE name_types(name TEXT NOT NULL, description TEXT NULL)",
    "CREATE UNIQUE INDEX name_types_name ON name_types (name)",
)

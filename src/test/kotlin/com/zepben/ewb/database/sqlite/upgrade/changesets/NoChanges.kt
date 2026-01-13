/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.upgrade.changesets

import com.zepben.ewb.database.paths.DatabaseType
import java.sql.Statement

/**
 * Helper class for changeset that do not impact a database.
 *
 * @param databaseType The type of database that shouldn't have changes.
 * @param version The version for the [databaseType] that shouldn't have changes.
 */
class NoChanges(
    databaseType: DatabaseType,
    version: Int
) : ChangeSetValidator(databaseType, version, expectChanges = false) {

    override fun setUpStatements(): List<String> = emptyList()
    override fun populateStatements(): List<String> = emptyList()
    override fun validateChanges(statement: Statement) {}
    override fun tearDownStatements(): List<String> = emptyList()

}

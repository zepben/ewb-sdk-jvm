/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.cim.upgrade.changesets.diagram

import com.zepben.evolve.database.paths.DatabaseType
import com.zepben.evolve.database.sqlite.cim.upgrade.changesets.ChangeSet50Helpers
import com.zepben.evolve.database.sqlite.cim.upgrade.changesets.ChangeSetValidator
import java.sql.Statement

object ChangeSet50DiagramValidator : ChangeSetValidator(DatabaseType.DIAGRAM, 50) {

    override fun setUpStatements(): List<String> = ChangeSet50Helpers.setUpStatements

    // We do not need to populate anything as we are not changing any of the table structures.
    override fun populateStatements(): List<String> = emptyList()

    override fun validateChanges(statement: Statement) {
        //
        // NOTE: We are being lazy and assuming if the table was left behind, then so were its indexes.
        //
        val (expectedTables, unexpectedTables) = ChangeSet50Helpers.tables(DatabaseType.DIAGRAM)
        ensureTables(statement, *expectedTables.toTypedArray(), present = true)
        ensureTables(statement, *unexpectedTables.toTypedArray(), present = false)

        ChangeSet50Helpers.ensureNames(statement, DatabaseType.DIAGRAM)
    }

    override fun tearDownStatements(): List<String> =
        ChangeSet50Helpers.tables(DatabaseType.DIAGRAM).first.map {
            "DELETE FROM $it"
        }

}

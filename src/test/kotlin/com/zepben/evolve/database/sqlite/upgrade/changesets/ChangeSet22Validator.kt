/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.upgrade.changesets

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import java.sql.Statement

object ChangeSet22Validator : ChangeSetValidator {

    override fun setUpStatements(): List<String> = emptyList()

    override fun populateStatements(): List<String> = listOf(
        """
        INSERT INTO metadata_data_sources(
            source, version, timestamp
        ) VALUES (
            'source', 'version', 'timestamp'
        )
        """
    )

    override fun validate(statement: Statement) {
        validateRows(statement, "select * from metadata_data_sources",
            { rs ->
                assertThat(rs.getString("source"), equalTo("source"))
                assertThat(rs.getString("version"), equalTo("version"))
                assertThat(rs.getString("timestamp"), equalTo("timestamp"))
            }
        )
    }

    override fun tearDownStatements(): List<String> = listOf(
        "DELETE FROM metadata_data_sources"
    )

}

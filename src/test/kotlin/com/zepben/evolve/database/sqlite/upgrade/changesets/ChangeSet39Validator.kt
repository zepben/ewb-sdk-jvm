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

object ChangeSet39Validator : ChangeSetValidator {

    override fun setUpStatements(): List<String> = listOf(
        """
        INSERT INTO power_transformers (
            mrid, name, description, num_controls, vector_group
        ) VALUES ( 
            'id1', '', '', 0, 'unknown'
        )
        """
    )

    override fun populateStatements(): List<String> = listOf(
        """
        INSERT INTO power_transformers (
            mrid, name, description, num_controls, vector_group, construction_kind, function
        ) VALUES ( 
            'id2', '', '', 0, 'unknown', 'overhead', 'autotransformer'
        )
        """,
        """
        INSERT INTO power_transformers (
            mrid, name, description, num_controls, vector_group, construction_kind, function
        ) VALUES ( 
            'id3', '', '', 0, 'unknown', 'underground', 'secondaryTransformer'
        )
        """
    )

    override fun validate(statement: Statement) {
        validateRows(statement, "select * from power_transformers", { rs ->
            assertThat(rs.getString("mrid"), equalTo("id1"))
            assertThat(rs.getString("construction_kind"), equalTo("unknown"))
            assertThat(rs.getString("function"), equalTo("other"))
        }, { rs ->
            assertThat(rs.getString("mrid"), equalTo("id2"))
            assertThat(rs.getString("construction_kind"), equalTo("overhead"))
            assertThat(rs.getString("function"), equalTo("autotransformer"))
        }, { rs ->
            assertThat(rs.getString("mrid"), equalTo("id3"))
            assertThat(rs.getString("construction_kind"), equalTo("underground"))
            assertThat(rs.getString("function"), equalTo("secondaryTransformer"))
        })
    }

    override fun tearDownStatements(): List<String> = listOf(
        "DELETE FROM power_transformers"
    )

}

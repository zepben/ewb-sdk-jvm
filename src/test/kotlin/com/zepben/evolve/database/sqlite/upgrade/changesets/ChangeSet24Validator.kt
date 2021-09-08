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
import kotlin.test.fail

object ChangeSet24Validator : ChangeSetValidator {

    override fun setUpStatements(): List<String> = listOf(
        "INSERT INTO power_transformers VALUES('power_transformer_0','pt_0','description_0',1,NULL,0,0,0,0,NULL,'UNKNOWN',0)",
        "INSERT INTO power_transformers VALUES('power_transformer_1','pt_1','description_1',1,NULL,0,0,0,0,NULL,'UNKNOWN',1.1999999999999999555)"
    )

    override fun populateStatements(): List<String> = emptyList()

    override fun validate(statement: Statement) {
        statement.executeQuery("pragma table_info('power_transformers');").use rs@{ rs ->
            while (rs.next()) {
                if (rs.getString("name") == "transformer_utilisation") {
                    assertThat(rs.getString("notnull"), equalTo("0"))
                    return@rs
                }
            }
            fail()
        }

        validateRows(statement, "select transformer_utilisation from power_transformers",
            { assertThat(it.getDouble("transformer_utilisation"), equalTo(0.0)) },
            { assertThat(it.getDouble("transformer_utilisation"), equalTo(1.2)) }
        )
    }

    override fun tearDownStatements(): List<String> = listOf(
        "DELETE FROM power_transformers"
    )

}

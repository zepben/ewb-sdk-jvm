/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.upgrade.changesets

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import java.sql.Statement

object ChangeSet44Validator : ChangeSetValidator {

    override fun setUpStatements(): List<String> = emptyList()

    override fun populateStatements(): List<String> = listOf(
        "INSERT INTO current_transformer_info " +
            "(accuracy_class, accuracy_limit, core_count, ct_class, knee_point_voltage, max_ratio_denominator, max_ratio_numerator, " +
            "nominal_ratio_denominator, nominal_ratio_numerator, primary_ratio, rated_current, secondary_fls_rating, secondary_ratio, usage) " +
            "VALUES ('A', 1.1, 2, 'CT', 3, 4.4, 5.5, 6.6, 7.7, 8.8, 9, 10, 11.11, 'U')",
        "INSERT INTO potential_transformer_info " +
            "(accuracy_class, nominal_ratio_denominator, nominal_ratio_numerator, primary_ratio, pt_class, rated_voltage, secondary_ratio) " +
            "VALUES ('A', 1.1, 2.2, 3.3, 'PT', 4, 5.5)",
        "INSERT INTO current_transformers (core_burden) VALUES (1)",
        "INSERT INTO potential_transformers (type) VALUES ('T')"
    )

    override fun validate(statement: Statement) {
        ensureIndexes(statement)
        validateRows(statement, "SELECT * FROM current_transformer_info", { rs ->
            assertThat(rs.getString("accuracy_class"), equalTo("A"))
            assertThat(rs.getDouble("accuracy_limit"), equalTo(1.1))
            assertThat(rs.getInt("core_count"), equalTo(2))
            assertThat(rs.getString("ct_class"), equalTo("CT"))
            assertThat(rs.getInt("knee_point_voltage"), equalTo(3))
            assertThat(rs.getDouble("max_ratio_denominator"), equalTo(4.4))
            assertThat(rs.getDouble("max_ratio_numerator"), equalTo(5.5))
            assertThat(rs.getDouble("nominal_ratio_denominator"), equalTo(6.6))
            assertThat(rs.getDouble("nominal_ratio_numerator"), equalTo(7.7))
            assertThat(rs.getDouble("primary_ratio"), equalTo(8.8))
            assertThat(rs.getInt("rated_current"), equalTo(9))
            assertThat(rs.getInt("secondary_fls_rating"), equalTo(10))
            assertThat(rs.getDouble("secondary_ratio"), equalTo(11.11))
            assertThat(rs.getString("usage"), equalTo("U"))
        })
        validateRows(statement, "SELECT * FROM potential_transformer_info", { rs ->
            assertThat(rs.getString("accuracy_class"), equalTo("A"))
            assertThat(rs.getDouble("nominal_ratio_denominator"), equalTo(1.1))
            assertThat(rs.getDouble("nominal_ratio_numerator"), equalTo(2.2))
            assertThat(rs.getDouble("primary_ratio"), equalTo(3.3))
            assertThat(rs.getString("pt_class"), equalTo("PT"))
            assertThat(rs.getInt("rated_voltage"), equalTo(4))
            assertThat(rs.getDouble("secondary_ratio"), equalTo(5.5))
        })
        validateRows(statement, "SELECT * FROM current_transformers", { rs ->
            assertThat(rs.getInt("core_burden"), equalTo(1))
        })
        validateRows(statement, "SELECT * FROM potential_transformers", { rs ->
            assertThat(rs.getString("type"), equalTo("T"))
        })
    }

    override fun tearDownStatements(): List<String> = listOf(
        "DELETE FROM current_transformer_info",
        "DELETE FROM potential_transformer_info",
        "DELETE FROM current_transformers",
        "DELETE FROM potential_transformers"
    )

}

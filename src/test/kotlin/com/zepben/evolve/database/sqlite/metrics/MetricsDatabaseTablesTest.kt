/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.metrics

import com.google.common.reflect.ClassPath
import com.zepben.evolve.database.postgres.metrics.MetricsDatabaseTables
import com.zepben.evolve.database.sqlite.cim.tables.MissingTableConfigException
import com.zepben.evolve.database.sqlite.cim.tables.SqliteTable
import com.zepben.evolve.database.sqlite.common.SqliteTableVersion
import com.zepben.testutils.exception.ExpectException.Companion.expect
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import java.lang.reflect.Modifier

internal class MetricsDatabaseTablesTest {

    private val tables = MetricsDatabaseTables()

    @Test
    internal fun `all tables are used by at least one database`() {
        val allFinalTables = ClassPath.from(ClassLoader.getSystemClassLoader())
            .getTopLevelClassesRecursive("com.zepben.evolve.database.sqlite.metrics.tables")
            .asSequence()
            .map { it.load() }
            .filter { !Modifier.isAbstract(it.modifiers) }
            .filter { SqliteTable::class.java.isAssignableFrom(it) }
            .map { it.simpleName }
            .toSet()

        val usedTables = tables.tables.keys
            .filter { it != SqliteTableVersion::class }
            .map { it.simpleName!! }
            .toSet()

        assertThat(usedTables, equalTo(allFinalTables))
    }

    @Test
    internal fun `throws on missing tables`() {
        expect { tables.getTable<MissingTable>() }.toThrow<MissingTableConfigException>()
    }

    private class MissingTable : SqliteTable() {
        override val name: String = error("this should never be called")
    }

}

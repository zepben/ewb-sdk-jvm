/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim

import com.google.common.reflect.ClassPath
import com.zepben.ewb.database.sql.MissingTableConfigException
import com.zepben.ewb.database.sql.TableVersion
import com.zepben.ewb.database.sqlite.cim.customer.CustomerDatabaseTables
import com.zepben.ewb.database.sqlite.cim.diagram.DiagramDatabaseTables
import com.zepben.ewb.database.sqlite.cim.network.NetworkDatabaseTables
import com.zepben.ewb.database.sqlite.common.SqliteTable
import com.zepben.testutils.exception.ExpectException.Companion.expect
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import java.lang.reflect.Modifier
import kotlin.reflect.full.isSubclassOf

internal class CimDatabaseTablesTest {

    private val tables = CimDatabaseTables()

    @Test
    internal fun `all tables are used by at least one database`() {
        val allFinalTables = ClassPath.from(ClassLoader.getSystemClassLoader())
            .getTopLevelClassesRecursive("com.zepben.ewb.database.sqlite.cim.tables")
            .asSequence()
            .map { it.load() }
            .filter { !Modifier.isAbstract(it.modifiers) }
            .filter { SqliteTable::class.java.isAssignableFrom(it) }
            .map { it.simpleName }
            .toSet()

        val usedTables = sequenceOf(CustomerDatabaseTables(), DiagramDatabaseTables(), NetworkDatabaseTables())
            .flatMap { it.tables.keys }
            .filter { !it.isSubclassOf(TableVersion::class) }
            .map { it.simpleName!! }
            .toSet()

        assertThat(usedTables, equalTo(allFinalTables))
    }

    @Test
    internal fun `throws on missing tables`() {
        expect { tables.getTable<MissingTable>() }.toThrow<MissingTableConfigException>()
        expect { tables.getInsert<MissingTable>() }.toThrow<MissingTableConfigException>()
    }

    private class MissingTable : SqliteTable() {
        override val name: String = error("this should never be called")
    }

}

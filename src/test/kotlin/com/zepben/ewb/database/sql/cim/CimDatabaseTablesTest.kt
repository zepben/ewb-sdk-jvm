/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim

import com.google.common.reflect.ClassPath
import com.zepben.ewb.database.sql.cim.customer.CustomerDatabaseTables
import com.zepben.ewb.database.sql.cim.diagram.DiagramDatabaseTables
import com.zepben.ewb.database.sql.cim.network.NetworkDatabaseTables
import com.zepben.ewb.database.sql.common.MissingTableConfigException
import com.zepben.ewb.database.sql.common.tables.SqlTable
import com.zepben.ewb.database.sql.common.tables.TableVersion
import com.zepben.ewb.database.sql.generators.SqlGenerator
import com.zepben.testutils.exception.ExpectException.Companion.expect
import io.mockk.mockk
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import java.lang.reflect.Modifier
import kotlin.reflect.full.isSubclassOf

internal class CimDatabaseTablesTest {

    private val tables = object : CimDatabaseTables() {
        override val sqlGenerator: SqlGenerator
            // Unused by CimDatabaseTables internally, so just return a mockk with no configuration that will break if it is actually used.
            get() = mockk()
    }

    @Test
    internal fun `all tables are used by at least one database`() {
        val allFinalTables = ClassPath.from(ClassLoader.getSystemClassLoader())
            .getTopLevelClassesRecursive("com.zepben.ewb.database.sql.cim.tables")
            .asSequence()
            .map { it.load() }
            .filter { !Modifier.isAbstract(it.modifiers) }
            .filter { SqlTable::class.java.isAssignableFrom(it) }
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

    private class MissingTable : SqlTable() {
        override val name: String = error("this should never be called")
    }

}

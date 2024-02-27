/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite

import com.google.common.reflect.ClassPath
import com.zepben.evolve.database.sqlite.tables.MissingTableConfigException
import com.zepben.evolve.database.sqlite.tables.SqliteTable
import com.zepben.testutils.exception.ExpectException.Companion.expect
import org.junit.jupiter.api.Test
import java.lang.reflect.Modifier
import kotlin.reflect.KClass

internal class DatabaseTablesTest {

    private val tables = DatabaseTables()

    @Test
    internal fun `has all tables`() {
        expect { tables.getTable(MissingTable::class.java) }.toThrow<MissingTableConfigException>()

        ClassPath.from(ClassLoader.getSystemClassLoader())
            .getTopLevelClassesRecursive("com.zepben.evolve.database.sqlite.tables")
            .asSequence()
            .map { it.load() }
            .filter { !Modifier.isAbstract(it.modifiers) }
            .filter { SqliteTable::class.java.isAssignableFrom(it) }
            .filterIsInstance<KClass<out SqliteTable>>()
            .sortedBy { it.simpleName }
            .forEach { tables.getTable(it.java) }
    }

    private class MissingTable : SqliteTable() {
        override fun name(): String {
            error("this should never be called")
        }

        override val tableClass: Class<out SqliteTable> get() = error("this should never be called")
        override val tableClassInstance: SqliteTable get() = error("this should never be called")
    }
}

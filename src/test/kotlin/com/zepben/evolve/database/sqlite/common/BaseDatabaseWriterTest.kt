/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.common

import com.zepben.evolve.database.sqlite.cim.tables.SqliteTable
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.nio.file.Files
import java.nio.file.Paths
import java.sql.DriverManager

internal class BaseDatabaseWriterTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    private val dbTestFile = "src/test/data/dbTest.sqlite"

    @BeforeEach
    internal fun beforeEach() {
        Files.deleteIfExists(Paths.get(dbTestFile))
    }

    @AfterEach
    internal fun afterEach() {
        Files.deleteIfExists(Paths.get(dbTestFile))
    }

    @Test
    internal fun persistFileMatchingVersion() {
        assertThat("Persistent writer should be able to write new file", persistentWriter(1).save())
        assertThat("Persistent writer should be able to write to db file with matching version", persistentWriter(1).save())
    }

    @Test
    internal fun persistFileDifferentVersion() {
        assertThat("Persistent writer should be able to write new file", persistentWriter(1).save())
        assertThat("Persistent writer shouldn't write to db file with mismatched version", !persistentWriter(2).save())
        assertThat(systemErr.log, containsString("Unsupported version in database file (got 1, expected 2)"))
    }

    private fun persistentWriter(version: Int) : BaseDatabaseWriter = object : BaseDatabaseWriter(
        dbTestFile,
        object : BaseDatabaseTables() {
            override val includedTables: Sequence<SqliteTable> = sequenceOf(TableVersion(version))
        },
        DriverManager::getConnection,
        persistFile = true
    ) {
        override fun populateTables(): Boolean = true
    }

}

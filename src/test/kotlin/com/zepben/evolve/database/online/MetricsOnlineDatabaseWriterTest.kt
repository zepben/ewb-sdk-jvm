/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.online

import com.zepben.evolve.database.sqlite.common.TableVersion
import com.zepben.evolve.database.sqlite.extensions.configureBatch
import com.zepben.evolve.database.sqlite.metrics.MetricsDatabaseTables
import com.zepben.evolve.database.sqlite.metrics.MetricsSchemaTest
import com.zepben.evolve.database.sqlite.metrics.MetricsWriter
import com.zepben.evolve.metrics.IngestionJob
import io.mockk.*
import org.hamcrest.MatcherAssert
import org.junit.jupiter.api.Test
import java.sql.Connection
import java.sql.DriverManager
import java.util.*

internal class MetricsOnlineDatabaseWriterTest : MetricsSchemaTest() {

    private val conn = mockk<Connection> {
        every { close() } just Runs
        every { commit() } just Runs
    }
    private val databaseTables = mockk<MetricsDatabaseTables> {
        every { prepareInsertStatements(conn) } just Runs
    }
    private val ingestionJob = IngestionJob(UUID.randomUUID())

    @Test
    internal fun callsWriter() = validateSaveWithVersions(1, 1) { result ->
        MatcherAssert.assertThat("Should have saved successfully", result)
        verifySequence {
            databaseTables.tables
            databaseTables.prepareInsertStatements(conn)
            MetricsWriter(ingestionJob, databaseTables).save()
        }
    }

    @Test
    internal fun throwsIfLocalVersionIsTooOld() = validateSaveWithVersions(1, 2) { result ->
        MatcherAssert.assertThat("Save should not be successful if local version is too old", !result)
    }

    @Test
    internal fun throwsIfOnlineVersionIsTooOld() = validateSaveWithVersions(2, 1) { result ->
        MatcherAssert.assertThat("Save should not be successful if online version is too old", !result)
    }

    override fun save(file: String, job: IngestionJob): Boolean =
        MetricsOnlineDatabaseWriter { DriverManager.getConnection("jdbc:sqlite:$file") }.save(job)

    private fun validateSaveWithVersions(localVersion: Int, remoteVersion: Int, resultAction: (Boolean) -> Unit) = mockkStatic(Connection::configureBatch) {
        every { conn.configureBatch() } returns conn
        every { databaseTables.tables } returns mapOf(TableVersion::class to TableVersion(localVersion))

        val result = MetricsOnlineDatabaseWriter(
            { conn },
            databaseTables,
            schemaUtils = mockk { every { getVersion(conn) } returns remoteVersion },
        ) { _, _ ->
            mockk { every { save() } returns true }
        }.save(ingestionJob)

        resultAction(result)
    }

}

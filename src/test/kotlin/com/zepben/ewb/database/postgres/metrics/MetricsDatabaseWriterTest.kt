/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.postgres.metrics

import com.zepben.ewb.database.postgres.common.PostgresTableVersion
import com.zepben.ewb.metrics.IngestionJob
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import java.sql.Connection
import java.util.*
import kotlin.io.path.createFile
import kotlin.io.path.exists
import kotlin.io.path.notExists

internal class MetricsDatabaseWriterTest {

    @TempDir
    lateinit var modelPath: Path

    private val connection = mockk<Connection> {
        justRun { autoCommit = false }
        justRun { commit() }
        justRun { close() }
    }
    private val tables = mockk<MetricsDatabaseTables> {
        every { tables } returns mapOf(PostgresTableVersion::class to mockk<PostgresTableVersion> {
            every { supportedVersion } returns 1
            every { getVersion(connection) } returns 1
        })
        justRun { prepareInsertStatements(connection) }
    }

    private val uuid = UUID.randomUUID()
    private val job = IngestionJob(uuid, mockk())

    private val writer = mockk<MetricsWriter> {
        every { write(any()) } returns true
    }

    @Test
    internal fun callsWriter() {
        val result = MetricsDatabaseWriter(
            { connection },
            tables,
            modelPath = null,
            createMetricsWriter = { writer }
        ).write(job)

        assertThat("Should have written successfully", result)
        assertThat("Job ID file shouldn't exist with no path", modelPath.resolve("$uuid.$JOB_ID_FILE_EXTENSION").notExists())

        verify { writer.write(job) }
    }

    @Test
    internal fun createsJobIdFile() {
        MetricsDatabaseWriter(
            { connection },
            tables,
            modelPath = modelPath,
            createMetricsWriter = { writer }
        ).write(job)

        assertThat("Job ID file should exist", modelPath.resolve("$uuid.$JOB_ID_FILE_EXTENSION").exists())

        verify { writer.write(job) }
    }

    @Test
    internal fun deletesExistingJobIdFiles() {
        val uuid2 = UUID.randomUUID()
        val uuid3 = UUID.randomUUID()

        modelPath.resolve("$uuid2.$JOB_ID_FILE_EXTENSION").createFile()
        modelPath.resolve("$uuid3.$JOB_ID_FILE_EXTENSION").createFile()

        MetricsDatabaseWriter(
            { connection },
            tables,
            modelPath = modelPath,
            createMetricsWriter = { writer }
        ).write(job)

        assertThat("Old job ID file should be deleted", modelPath.resolve("$uuid2.$JOB_ID_FILE_EXTENSION").notExists())
        assertThat("Old job ID file should be deleted", modelPath.resolve("$uuid3.$JOB_ID_FILE_EXTENSION").notExists())
        assertThat("New job ID file should exist", modelPath.resolve("$uuid.$JOB_ID_FILE_EXTENSION").exists())

        verify { writer.write(job) }
    }

}

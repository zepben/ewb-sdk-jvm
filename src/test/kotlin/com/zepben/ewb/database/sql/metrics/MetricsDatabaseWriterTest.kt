/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.metrics

import com.zepben.ewb.database.sql.common.tables.TableVersion
import com.zepben.ewb.database.sql.initialisers.NoOpDatabaseInitialiser
import com.zepben.ewb.metrics.IngestionJob
import com.zepben.testutils.junit.SystemLogExtension
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import java.sql.Connection
import java.util.*
import kotlin.io.path.createFile
import kotlin.io.path.exists
import kotlin.io.path.notExists

internal class MetricsDatabaseWriterTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @TempDir
    lateinit var modelPath: Path

    private val connection = mockk<Connection> {
        justRun { autoCommit = false }
        justRun { commit() }
        justRun { close() }
    }
    private val connectionyStuff = NoOpDatabaseInitialiser<MetricsDatabaseTables> {
        connection
    }

    private val tables = mockk<MetricsDatabaseTables> {
        every { tables } returns mapOf(TableVersion::class to mockk<TableVersion> {
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
            tables,
            connectionyStuff,
            modelPath = null,
            createMetricsWriter = { writer }
        ).write(job)

        assertThat("Should have written successfully", result)
        assertThat("Job ID file shouldn't exist with no path", modelPath.resolve("$uuid.${JOB_ID_FILE_EXTENSION}").notExists())

        verify { writer.write(job) }
    }

    @Test
    internal fun createsJobIdFile() {
        MetricsDatabaseWriter(
            tables,
            connectionyStuff,
            modelPath = modelPath,
            createMetricsWriter = { writer }
        ).write(job)

        assertThat("Job ID file should exist", modelPath.resolve("$uuid.${JOB_ID_FILE_EXTENSION}").exists())

        verify { writer.write(job) }
    }

    @Test
    internal fun deletesExistingJobIdFiles() {
        val uuid2 = UUID.randomUUID()
        val uuid3 = UUID.randomUUID()

        modelPath.resolve("$uuid2.${JOB_ID_FILE_EXTENSION}").createFile()
        modelPath.resolve("$uuid3.${JOB_ID_FILE_EXTENSION}").createFile()

        MetricsDatabaseWriter(
            tables,
            connectionyStuff,
            modelPath = modelPath,
            createMetricsWriter = { writer }
        ).write(job)

        assertThat("Old job ID file should be deleted", modelPath.resolve("$uuid2.${JOB_ID_FILE_EXTENSION}").notExists())
        assertThat("Old job ID file should be deleted", modelPath.resolve("$uuid3.${JOB_ID_FILE_EXTENSION}").notExists())
        assertThat("New job ID file should exist", modelPath.resolve("$uuid.${JOB_ID_FILE_EXTENSION}").exists())

        verify { writer.write(job) }
    }

    @Test
    internal fun createsDirectoriesIfNeeded() {
        val innerPath = modelPath.resolve("inner1").resolve("inner2")

        val success = MetricsDatabaseWriter(
            { connection },
            tables,
            modelPath = innerPath,
            createMetricsWriter = { writer }
        ).write(job)

        assertThat("write should be successful", success)
        assertThat("Job ID file should exist", innerPath.resolve("$uuid.$JOB_ID_FILE_EXTENSION").exists())

        verify { writer.write(job) }
    }

    @Test
    internal fun failsIfFilePreventsDirectoryCreation() {
        val innerPath = modelPath.resolve("inner1").resolve("inner2")
        modelPath.resolve("inner1").createFile()

        val success = MetricsDatabaseWriter(
            { connection },
            tables,
            modelPath = innerPath,
            createMetricsWriter = { writer }
        ).write(job)

        assertThat("write should be unsuccessful", !success)

        verify { writer.write(job) }
    }

}

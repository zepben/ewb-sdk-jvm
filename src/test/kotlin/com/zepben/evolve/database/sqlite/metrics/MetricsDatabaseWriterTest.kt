/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.metrics

import com.zepben.evolve.database.postgres.metrics.JOB_ID_FILE_EXTENSION
import com.zepben.evolve.database.postgres.metrics.MetricsDatabaseWriter
import com.zepben.evolve.database.postgres.metrics.MetricsWriter
import com.zepben.evolve.metrics.IngestionJob
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import kotlin.io.path.createFile
import kotlin.io.path.exists
import kotlin.io.path.notExists

internal class MetricsDatabaseWriterTest {

    @TempDir
    lateinit var modelPath: Path

    private val databaseFile = "databaseFile"
    private val uuid = UUID.randomUUID()
    private val job = IngestionJob(uuid, mockk())

    private val writer = mockk<MetricsWriter> {
        every { write(any()) } returns true
    }

    @BeforeEach
    internal fun beforeEach() {
        Files.deleteIfExists(Paths.get(databaseFile))
    }

    @AfterEach
    internal fun afterEach() {
        Files.deleteIfExists(Paths.get(databaseFile))
    }

    @Test
    internal fun callsWriter() {
        val result = MetricsDatabaseWriter(
            databaseFile,
            modelPath = null,
            createMetricsWriter = { writer }
        ).write(job)

        assertThat("Should have writen successfully", result)
        assertThat("Job ID file shouldn't exist with no path", modelPath.resolve("$uuid.$JOB_ID_FILE_EXTENSION").notExists())

        verify { writer.write(job) }
    }

    @Test
    internal fun createsJobIdFile() {
        MetricsDatabaseWriter(
            databaseFile,
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
            databaseFile,
            modelPath = modelPath,
            createMetricsWriter = { writer }
        ).write(job)

        assertThat("Old job ID file should be deleted", modelPath.resolve("$uuid2.$JOB_ID_FILE_EXTENSION").notExists())
        assertThat("Old job ID file should be deleted", modelPath.resolve("$uuid3.$JOB_ID_FILE_EXTENSION").notExists())
        assertThat("New job ID file should exist", modelPath.resolve("$uuid.$JOB_ID_FILE_EXTENSION").exists())

        verify { writer.write(job) }
    }

}

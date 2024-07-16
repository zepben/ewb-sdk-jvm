/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.metrics

import com.zepben.evolve.metrics.IngestionJob
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import java.util.*
import kotlin.io.path.exists

internal class MetricsDatabaseWriterTest {

    @TempDir
    lateinit var modelPath: Path

    private val writer = mockk<MetricsWriter> {
        every { save() } returns true
    }

    @Test
    internal fun callsWriter() {
        val result = MetricsDatabaseWriter(
            "databaseFile",
            mockk(), // ingestion job isn't actually used to create the MetricsWriter
            metricsWriter = writer
        ).saveSchema()

        assertThat("Should have saved successfully", result)

        verify { writer.save() }
    }

    @Test
    internal fun createsJobIdFile() {
        val uuid = UUID.randomUUID()

        MetricsDatabaseWriter(
            "databaseFile",
            IngestionJob(uuid),
            modelPath = modelPath,
            metricsWriter = writer
        ).saveSchema()

        assertThat("Job ID file should exist", modelPath.resolve(uuid.toString()).exists())

        verify { writer.save() }
    }

}

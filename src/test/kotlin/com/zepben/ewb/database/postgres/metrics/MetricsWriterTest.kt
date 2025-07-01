/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.postgres.metrics

import com.zepben.ewb.metrics.*
import com.zepben.testutils.junit.SystemLogExtension
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.sqlite.SQLiteErrorCode
import org.sqlite.SQLiteException
import java.time.Instant
import java.util.*

internal class MetricsWriterTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    private val metadata = IngestionMetadata(Instant.EPOCH, "N/A", "test", "0.0.0")
    private val job = IngestionJob(UUID.randomUUID(), metadata).apply {
        sources["abc"]
        networkMetrics[TotalNetworkContainer]
    }
    private val metricsEntryWriter = mockk<MetricsEntryWriter> {
        every { write(any(), any<IngestionMetadata>()) } returns true
        every { writeSource(any(), any<JobSource>()) } returns true
        every { writeMetric(any(), any<NetworkMetric>()) } returns true
    }
    private val metricsWriter = MetricsWriter(mockk(), metricsEntryWriter)

    @Test
    internal fun `passes objects through to the metrics entry writer`() {
        metricsWriter.write(job)

        verify(exactly = 1) {
            metricsEntryWriter.write(job.id, metadata)
            metricsEntryWriter.writeSource(job.id, job.sources.entries.first())
            metricsEntryWriter.writeMetric(job.id, job.networkMetrics.entries.first())
        }
    }

    @Test
    internal fun `source failure message`() {
        every { metricsEntryWriter.writeSource(any(), any<JobSource>()) } throws SQLiteException("message", SQLiteErrorCode.SQLITE_ERROR)
        metricsWriter.write(job)

        assertThat(systemErr.log, containsString("Failed to write job source"))
        assertThat(systemErr.log, containsString("message"))
    }

    @Test
    internal fun `metric failure message`() {
        every { metricsEntryWriter.writeMetric(any(), any<NetworkMetric>()) } throws SQLiteException("message", SQLiteErrorCode.SQLITE_ERROR)
        metricsWriter.write(job)

        assertThat(systemErr.log, containsString("Failed to write metric"))
        assertThat(systemErr.log, containsString("message"))
    }

    @Test
    internal fun `write returns false if missing job metadata`() {
        every { metricsEntryWriter.write(any(), any<IngestionMetadata>()) } returns false
        assertThat("Ingestion job without metadata should not write", !metricsWriter.write(job))
    }

}

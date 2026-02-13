/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.metrics

import com.zepben.ewb.metrics.*
import com.zepben.ewb.metrics.variants.VariantMetricEntry
import com.zepben.ewb.metrics.variants.VariantMetricKind
import com.zepben.ewb.metrics.variants.VariantMetrics
import com.zepben.testutils.junit.SystemLogExtension
import io.mockk.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.sqlite.SQLiteErrorCode
import org.sqlite.SQLiteException
import java.sql.Connection
import java.sql.PreparedStatement
import java.time.Instant
import java.util.*

internal class MetricsWriterTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    private val metadata = IngestionMetadata(Instant.EPOCH, "N/A", "test", "0.0.0")
    private val ingestionJob = IngestionJob(UUID.randomUUID(), metadata).apply {
        sources["abc"]
        networkMetrics[TotalNetworkContainer]
    }
    private val metricsEntryWriter = mockk<MetricsEntryWriter> {
        every { write(any(), any<IngestionMetadata>()) } returns true
        every { writeSource(any(), any<JobSource>()) } returns true
        every { writeMetric(any(), any<NetworkMetric>()) } returns true
        every { writeVariantMetricEntry(any(), any(), any(), any(), any<VariantMetricEntry>()) } returns true
    }
    private val metricsWriter = MetricsWriter(mockk(), metricsEntryWriter)

    @Test
    internal fun `passes objects through to the metrics entry writer`() {
        metricsWriter.write(ingestionJob)

        verify(exactly = 1) {
            metricsEntryWriter.write(ingestionJob.id, metadata)
            metricsEntryWriter.writeSource(ingestionJob.id, ingestionJob.sources.entries.first())
            metricsEntryWriter.writeMetric(ingestionJob.id, ingestionJob.networkMetrics.entries.first())
        }
    }

    @Test
    internal fun `source failure message`() {
        every { metricsEntryWriter.writeSource(any(), any<JobSource>()) } throws SQLiteException("message", SQLiteErrorCode.SQLITE_ERROR)
        metricsWriter.write(ingestionJob)

        assertThat(systemErr.log, containsString("Failed to write job source"))
        assertThat(systemErr.log, containsString("message"))
    }

    @Test
    internal fun `metrics entry writer can write source with null metadata timestamp`() {
        val insert = mockk<PreparedStatement>().apply {
            justRun { setObject(1, any()) }
            justRun { setString(2, any()) }
            justRun { setTimestamp(3, any()) }
            justRun { setObject(4, any()) }
            every { executeUpdate() } returns 1
        }

        MetricsEntryWriter(
            MetricsDatabaseTables().apply {
                prepareInsertStatements(
                    mockk<Connection> {
                        justRun { autoCommit = false }
                        justRun { commit() }
                        justRun { close() }
                        every { prepareStatement(any()) } returns insert
                    })
            }
        ).writeSource(UUID.randomUUID(), jobSource(timestampVal = null))

        verify { insert.setTimestamp(3, null) }
    }

    private fun jobSource(keyVal: String = "", timestampVal: Instant? = Instant.EPOCH, fileHashVal: ByteArray = ByteArray(0)): JobSource =
        mockk<JobSource>().apply {
            every { key } returns keyVal
            every { value } returns mockk<SourceMetadata>().apply {
                every { timestamp } returns timestampVal
                every { fileHash } returns fileHashVal
            }
        }

    @Test
    internal fun `metric failure message`() {
        every { metricsEntryWriter.writeMetric(any(), any<NetworkMetric>()) } throws SQLiteException("message", SQLiteErrorCode.SQLITE_ERROR)
        metricsWriter.write(ingestionJob)

        assertThat(systemErr.log, containsString("Failed to write metric"))
        assertThat(systemErr.log, containsString("message"))
    }

    @Test
    internal fun `write returns false if missing job metadata`() {
        every { metricsEntryWriter.write(any(), any<IngestionMetadata>()) } returns false
        assertThat("Ingestion job without metadata should not write", !metricsWriter.write(ingestionJob))
    }

    @Test
    internal fun `passes variant metrics through to the metrics entry writer`() {
        val variantMetrics = VariantMetrics(
            "projectId", "networkStageId", "1234", "changeSetId1",
            listOf(
                VariantMetricEntry(VariantMetricKind.CONFLICT, "test", 7, listOf("mrid1", "mrid2")),
                VariantMetricEntry(VariantMetricKind.ASSET, "test2", 8, emptyList())
            )
        )

        metricsWriter.write(variantMetrics)

        verifySequence {
            metricsEntryWriter.writeVariantMetricEntry(
                variantMetrics.networkModelProjectId,
                variantMetrics.networkModelProjectStageId,
                variantMetrics.baseModelVersion,
                variantMetrics.changeSetId,
                variantMetrics.metrics[0]
            )
            metricsEntryWriter.writeVariantMetricEntry(
                variantMetrics.networkModelProjectId,
                variantMetrics.networkModelProjectStageId,
                variantMetrics.baseModelVersion,
                variantMetrics.changeSetId,
                variantMetrics.metrics[1]
            )
        }
    }
}

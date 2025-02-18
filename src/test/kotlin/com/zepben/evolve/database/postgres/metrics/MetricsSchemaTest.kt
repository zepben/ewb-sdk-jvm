/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.postgres.metrics

import com.zepben.evolve.database.postgres.metrics.tables.tableMetricsVersion
import com.zepben.evolve.metrics.*
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.sql.DriverManager
import java.sql.Timestamp
import java.time.Instant
import java.util.*

internal class MetricsSchemaTest {

    @JvmField
    @RegisterExtension
    val systemOut: SystemLogExtension = SystemLogExtension.SYSTEM_OUT.captureLog().muteOnSuccess()

    private val uuid = UUID.randomUUID()

    private val connection = getConnection()

    private fun getConnection() = DriverManager.getConnection("jdbc:h2:mem:metrics;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH")

    @BeforeEach
    internal fun createSchema() {
        // The MetricsDatabaseWriter assumes that the schema has been created already, so we create it here
        connection.createStatement().use { statement ->
            MetricsDatabaseTables().forEachTable {
                statement.executeUpdate(it.createTableSql)
            }

            // Add the version number to the database.
            connection.prepareStatement(tableMetricsVersion.preparedInsertSql).use { insert ->
                insert.setInt(tableMetricsVersion.VERSION.queryIndex, tableMetricsVersion.supportedVersion)
                insert.executeUpdate()
            }
        }
    }

    @AfterEach
    internal fun closeConnection() = connection.close()

    @Test
    internal fun `writes job metadata`() = validateJob(
        baseJob(),
        "jobs",
        listOf(uuid, Timestamp.from(Instant.EPOCH), "source", "application", "applicationVersion")
    )

    @Test
    internal fun `writes sources`() = validateJob(
        baseJob().apply {
            sources["abc"].timestamp = Instant.EPOCH
            sources["abc"].fileHash = "xyz".toByteArray()
        },
        "job_sources",
        listOf(uuid, "abc", Timestamp.from(Instant.EPOCH), "xyz".toByteArray())
    )

    @Test
    internal fun `writes global metric`() = validateJob(
        baseJob().apply {
            networkMetrics[TotalNetworkContainer]["abc"] = 1.2
        },
        "network_container_metrics",
        listOf(uuid, "GLOBAL", "", "TOTAL", "abc", 1.2)
    )

    @Test
    internal fun `writes Feeder metric`() = validateJob(
        baseJob().apply {
            networkMetrics[PartialNetworkContainer(NetworkLevel.Feeder, "fdr", "feeder")]["abc"] = 1.2
        },
        "network_container_metrics",
        listOf(uuid, "fdr", "feeder", "Feeder", "abc", 1.2)
    )

    @Test
    internal fun `writes Feeder and FeederTotal metrics`() = validateJob(
        baseJob().apply {
            networkMetrics[PartialNetworkContainer(NetworkLevel.Feeder, "fdr", "feeder")]["abc"] = 1.2
            networkMetrics[PartialNetworkContainer(NetworkLevel.FeederTotal, "fdr", "feeder")]["abc"] = 1.3
        },
        "network_container_metrics",
        listOf(uuid, "fdr", "feeder", "Feeder", "abc", 1.2),
        listOf(uuid, "fdr", "feeder", "FeederTotal", "abc", 1.3)
    )

    @Test
    internal fun `writes Substation and SubstationTotal metrics`() = validateJob(
        baseJob().apply {
            networkMetrics[PartialNetworkContainer(NetworkLevel.Substation, "sub", "substation")]["abc"] = 1.2
            networkMetrics[PartialNetworkContainer(NetworkLevel.SubstationTotal, "sub", "substation")]["abc"] = 1.3
        },
        "network_container_metrics",
        listOf(uuid, "sub", "substation", "Substation", "abc", 1.2),
        listOf(uuid, "sub", "substation", "SubstationTotal", "abc", 1.3),
    )

    private fun baseJob() = IngestionJob(uuid, metadata = IngestionMetadata(Instant.EPOCH, "source", "application", "applicationVersion"))

    private fun validateJob(expectedJob: IngestionJob, tableName: String, vararg rows: List<Any>) {
        val result = MetricsDatabaseWriter(::getConnection).write(expectedJob)
        assertThat("Database should have been written", result)

        getConnection().use { connection ->
            connection.createStatement().use { statement ->
                statement.executeQuery("SELECT * FROM $tableName").use { rs ->
                    rows.forEachIndexed { i, values ->
                        assertThat("Row no. ${i + 1} should exist for $tableName", rs.next())
                        values.forEachIndexed { j, value ->
                            assertThat(rs.getObject(j + 1), equalTo(value))
                        }
                    }
                }
            }
        }
    }

}

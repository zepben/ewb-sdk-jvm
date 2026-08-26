/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.metrics

import com.zepben.ewb.database.sql.TestDatabaseContainer
import com.zepben.ewb.database.sql.metrics.tables.tableMetricsVersion
import com.zepben.ewb.metrics.*
import com.zepben.ewb.metrics.dataquality.DataQualityIssue
import com.zepben.ewb.metrics.dataquality.DataQualityIssueCallout
import com.zepben.ewb.metrics.dataquality.DataQualityIssueCategory
import com.zepben.ewb.metrics.dataquality.DataQualityIssueStatus
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.sql.Timestamp
import java.time.Instant
import java.util.*


internal class MetricsSchemaTest {

    companion object {
        @JvmField
        @RegisterExtension
        val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()
    }

    private val uuid = UUID.randomUUID()
    private val tables = MetricsDatabaseTables()

    private fun getConnection() = TestDatabaseContainer.getConnection()

    @BeforeEach
    // NOTE: this pattern must be used instead of `getConnection.use` to ensure the same in-memory DB is used for each of these tests.
    internal fun createSchema() {
        // The MetricsDatabaseWriter assumes that the schema has been created already, so we create it here
        TestDatabaseContainer.getConnection().use { conn ->
            conn.createStatement().use { statement ->
                tables.forEachTable {
                    statement.executeUpdate(tables.sqlGenerator.createTableSql(it))
                }

                // Add the version number to the database.
                conn.prepareStatement(tableMetricsVersion.preparedInsertSql).use { insert ->
                    insert.setInt(tableMetricsVersion.VERSION.queryIndex, tableMetricsVersion.supportedVersion)
                    insert.executeUpdate()
                }
            }
        }
    }

    @AfterEach
    internal fun removeSchema() {
        // Since we created the schema, we should be a good citizen and clean it up. This allows us to reuse the same
        // test container and prevent the ~2-second startup delay on each test.
        TestDatabaseContainer.getConnection().use { conn ->
            conn.createStatement().use { statement ->
                tables.forEachTable {
                    statement.executeUpdate("DROP TABLE IF EXISTS ${it.name}")
                }
            }
        }
    }

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
        listOf(uuid, "GLOBAL", null, "TOTAL", "abc", 1.2)
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

    @Test
    internal fun `writes data quality issue category`() {
        val categoryId = UUID.randomUUID()
        val category = DataQualityIssueCategory(
            id = categoryId.toString(),
            name = "Missing Data",
            description = "Data is absent from the model"
        )

        val result = MetricsDatabaseWriter(::getConnection).write(category)
        assertThat("Category should have been written", result)

        validateTable(
            "data_quality_issue_categories",
            listOf(categoryId, "Missing Data", "Data is absent from the model")
        )
    }

    @Test
    internal fun `writes data quality issue category with null description`() {
        val categoryId = UUID.randomUUID()
        val category = DataQualityIssueCategory(
            id = categoryId.toString(),
            name = "Topology"
        )

        val result = MetricsDatabaseWriter(::getConnection).write(category)
        assertThat("Category should have been written", result)

        validateTable(
            "data_quality_issue_categories",
            listOf(categoryId, "Topology", null)
        )
    }

    @Test
    internal fun `writes data quality issue`() {
        val issueId = UUID.randomUUID()
        val categoryId = UUID.randomUUID()

        MetricsDatabaseWriter(::getConnection).write(
            DataQualityIssueCategory(id = categoryId.toString(), name = "Test Category")
        )

        val issue = DataQualityIssue(
            id = issueId.toString(),
            status = DataQualityIssueStatus.CREATED,
            createdAt = kotlin.time.Instant.fromEpochMilliseconds(0),
            createdBy = String(),
            updatedAt = kotlin.time.Instant.fromEpochMilliseconds(0),
            updatedBy = String(),
            networkModelCreatedAgainst = String(),
            name = "Bad connectivity",
            description = "Disconnected segment found",
            associatedAssets = listOf("asset-001"),
            annotationGeoJson = """{"type":"Point","coordinates":[144.9,-37.8]}""",
            categoryId = categoryId.toString(),
            severity = 2,
            priority = 1
        )

        val result = MetricsDatabaseWriter(::getConnection).write(issue)
        assertThat("Issue should have been written", result)

        getConnection().use { conn ->
            conn.createStatement().use { stmt ->
                stmt.executeQuery("SELECT id, status, name, severity, priority FROM data_quality_issues").use { rs ->
                    assertThat("Row should exist", rs.next())
                    assertThat(rs.getObject(1), equalTo(issueId as Any))
                    assertThat(rs.getString(2), equalTo("CREATED"))
                    assertThat(rs.getString(3), equalTo("Bad connectivity"))
                    assertThat(rs.getInt(4), equalTo(2))
                    assertThat(rs.getInt(5), equalTo(1))
                }
            }
        }
    }

    @Test
    internal fun `writes data quality issue with assets and callouts`() {
        val issueId = UUID.randomUUID()
        val categoryId = UUID.randomUUID()
        val calloutId = UUID.randomUUID()

        MetricsDatabaseWriter(::getConnection).write(
            DataQualityIssueCategory(id = categoryId.toString(), name = "Test")
        )

        val callout = DataQualityIssueCallout(
            id = calloutId.toString(),
            dataQualityIssueId = issueId.toString(),
            longitude = 144.9,
            latitude = -37.8,
            positionX = 50.0,
            positionY = 25.0,
            width = 200.0,
            height = 100.0,
            label = "A",
            description = "Check this area",
            colour = "#FF0000"
        )

        val issue = DataQualityIssue(
            id = issueId.toString(),
            status = DataQualityIssueStatus.CREATED,
            createdAt = kotlin.time.Instant.fromEpochMilliseconds(0),
            createdBy = String(),
            updatedAt = kotlin.time.Instant.fromEpochMilliseconds(0),
            updatedBy = String(),
            networkModelCreatedAgainst = String(),
            name = "Bad connectivity",
            description = "Disconnected segment found",
            associatedAssets = listOf("asset-mrid-001"),
            annotationGeoJson = """{"type":"Point"}""",
            categoryId = categoryId.toString(),
            severity = 2,
            priority = 1
        )

        val result = MetricsDatabaseWriter(::getConnection).write(issue, listOf(callout))
        assertThat("Issue with assets and callouts should have been written", result)

        getConnection().use { conn ->
            conn.createStatement().use { stmt ->
                stmt.executeQuery("SELECT data_quality_issue_id, asset_mrid FROM data_quality_issue_assets").use { rs ->
                    assertThat("Asset row should exist", rs.next())
                    assertThat(rs.getObject(1), equalTo(issueId as Any))
                    assertThat(rs.getString(2), equalTo("asset-mrid-001"))
                }
            }
            conn.createStatement().use { stmt ->
                stmt.executeQuery("SELECT id, data_quality_issue_id, longitude, latitude, label, colour FROM data_quality_issue_callouts").use { rs ->
                    assertThat("Callout row should exist", rs.next())
                    assertThat(rs.getObject(1), equalTo(calloutId as Any))
                    assertThat(rs.getObject(2), equalTo(issueId as Any))
                    assertThat(rs.getDouble(3), equalTo(144.9))
                    assertThat(rs.getDouble(4), equalTo(-37.8))
                    assertThat(rs.getString(5), equalTo("A"))
                    assertThat(rs.getString(6), equalTo("#FF0000"))
                }
            }
        }
    }

    @Test
    internal fun `writes data quality issue callout with null optional fields`() {
        val issueId = UUID.randomUUID()
        val categoryId = UUID.randomUUID()
        val calloutId = UUID.randomUUID()

        MetricsDatabaseWriter(::getConnection).write(
            DataQualityIssueCategory(id = categoryId.toString(), name = "Test")
        )

        val callout = DataQualityIssueCallout(
            id = calloutId.toString(),
            dataQualityIssueId = issueId.toString(),
            longitude = 145.0,
            latitude = -37.7,
            positionX = 10.0,
            positionY = 20.0,
            width = 150.0,
            height = 80.0,
            colour = "#00FF00"
        )

        val issue = DataQualityIssue(
            id = issueId.toString(),
            status = DataQualityIssueStatus.CREATED,
            createdAt = kotlin.time.Instant.fromEpochMilliseconds(0),
            createdBy = String(),
            updatedAt = kotlin.time.Instant.fromEpochMilliseconds(0),
            updatedBy = String(),
            networkModelCreatedAgainst = String(),
            name = "Test issue",
            description = "For null callout fields",
            associatedAssets = emptyList(),
            annotationGeoJson = """{"type":"Point"}""",
            categoryId = categoryId.toString(),
            severity = 1,
            priority = 1
        )

        val result = MetricsDatabaseWriter(::getConnection).write(issue, listOf(callout))
        assertThat("Callout should have been written", result)

        getConnection().use { conn ->
            conn.createStatement().use { stmt ->
                stmt.executeQuery("SELECT label, description FROM data_quality_issue_callouts").use { rs ->
                    assertThat("Row should exist", rs.next())
                    assertThat(rs.getString(1), equalTo(null))
                    assertThat(rs.getString(2), equalTo(null))
                }
            }
        }
    }

    private fun baseJob() = IngestionJob(uuid, metadata = IngestionMetadata(Instant.EPOCH, "source", "application", "applicationVersion"))

    private fun validateJob(expectedJob: IngestionJob, tableName: String, vararg rows: List<Any?>) {
        val result = MetricsDatabaseWriter(::getConnection).write(expectedJob)
        assertThat("Database should have been written", result)

        validateTable(tableName, *rows)
    }

    private fun validateTable(tableName: String, vararg rows: List<Any?>) {
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

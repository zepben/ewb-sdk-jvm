/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.metrics

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import com.zepben.evolve.database.sqlite.common.TableVersion
import com.zepben.evolve.database.sqlite.metrics.tables.tableMetricsVersion
import com.zepben.evolve.metrics.IngestionJob
import io.mockk.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import java.sql.Connection
import java.util.*
import javax.sql.DataSource

internal class MetricsDataSourceWriterTest : MetricsSchemaTest() {

    @Test
    internal fun callsWriter() {
        val dataSource = mockk<DataSource>()
        val connection = mockk<Connection>()
        val databaseTables = mockk<MetricsDatabaseTables> {
            every { prepareInsertStatements(connection) } just Runs
            every { tables } returns mapOf(TableVersion::class to tableMetricsVersion)
        }
        val ingestionJob = IngestionJob(UUID.randomUUID())
        mockkConstructor(MetricsWriter::class) {
            every { constructedWith<MetricsWriter>(EqMatcher(ingestionJob), EqMatcher(databaseTables), AllAnyMatcher<MetricsEntryWriter>()).save() } returns true
            val result = MetricsDataSourceWriter(dataSource, databaseTables).populateTables(connection, ingestionJob)

            assertThat("Should have saved successfully", result)

            verifySequence {
                databaseTables.tables
                databaseTables.prepareInsertStatements(connection)
                MetricsWriter(ingestionJob, databaseTables).save()
            }
        }
    }

    override fun save(file: String, job: IngestionJob): Boolean {
        val hikariConfig = HikariConfig().apply {
            jdbcUrl = "jdbc:sqlite:$file"
        }
        return HikariDataSource(hikariConfig).use { dataSource ->
            MetricsDataSourceWriter(dataSource).save(job)
        }
    }

}

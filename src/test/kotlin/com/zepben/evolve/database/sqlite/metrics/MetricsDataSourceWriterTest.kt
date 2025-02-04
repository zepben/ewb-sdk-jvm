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
import com.zepben.evolve.database.sqlite.common.SchemaUtils
import com.zepben.evolve.database.sqlite.common.TableVersion
import com.zepben.evolve.database.sqlite.extensions.configureBatch
import com.zepben.evolve.database.sqlite.metrics.tables.tableMetricsVersion
import com.zepben.evolve.metrics.IngestionJob
import io.mockk.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import java.sql.Connection
import java.util.*
import javax.sql.DataSource

internal class MetricsDataSourceWriterTest : MetricsSchemaTest() {

    @AfterEach
    internal fun unmockAll() {
        clearAllMocks()
    }

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

    @Test
    internal fun throwsIfLocalVersionIsTooOld() {
        mockkStatic(Connection::configureBatch)
        val conn = mockk<Connection> {
            every { close() } just Runs
            every { configureBatch() } returns this
        }
        val dataSource = mockk<DataSource> { every { connection } returns conn }
        val databaseTables = mockk<MetricsDatabaseTables> {
            every { prepareInsertStatements(conn) } just Runs
            every { tables } returns mapOf(TableVersion::class to TableVersion(1))
        }
        val ingestionJob = IngestionJob(UUID.randomUUID())
        mockkConstructor(SchemaUtils::class) {
            every { constructedWith<SchemaUtils>(EqMatcher(databaseTables), AllAnyMatcher<Logger>()).getVersion(conn) } returns 2

            val result = MetricsDataSourceWriter(dataSource, databaseTables).save(ingestionJob)

            assertThat("Save should not be successful if local version is too old", !result)
        }
    }

    @Test
    internal fun throwsIfOnlineVersionIsTooOld() {
        mockkStatic(Connection::configureBatch)
        val conn = mockk<Connection> {
            every { close() } just Runs
            every { configureBatch() } returns this
        }
        val dataSource = mockk<DataSource> { every { connection } returns conn }
        val databaseTables = mockk<MetricsDatabaseTables> {
            every { prepareInsertStatements(conn) } just Runs
            every { tables } returns mapOf(TableVersion::class to TableVersion(2))
        }
        val ingestionJob = IngestionJob(UUID.randomUUID())
        mockkConstructor(SchemaUtils::class) {
            every { constructedWith<SchemaUtils>(EqMatcher(databaseTables), AllAnyMatcher<Logger>()).getVersion(conn) } returns 1

            val result = MetricsDataSourceWriter(dataSource, databaseTables).save(ingestionJob)

            assertThat("Save should not be successful if online version is too old", !result)
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

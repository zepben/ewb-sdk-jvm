/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim

import com.zepben.ewb.cim.iec61970.base.core.IdentifiedObject
import com.zepben.ewb.database.sql.cim.tables.tableCimVersion
import com.zepben.ewb.services.common.BaseService
import com.zepben.ewb.services.common.BaseServiceComparator
import com.zepben.ewb.services.common.extensions.typeNameAndMRID
import com.zepben.ewb.services.common.meta.MetadataCollection
import com.zepben.ewb.services.common.testdata.SchemaServices
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Paths
import java.sql.Connection
import java.sql.DriverManager

/**
 * Base class for CIM service database schema tests
 */
abstract class CimDatabaseSchemaTest<
    TService : BaseService,
    TWriter : CimDatabaseWriter<*, TService>,
    TReader : CimDatabaseReader<*, TService>,
    TComparator : BaseServiceComparator,
    TMaxsCoolObject: Any,
    >(
    private val tableVersion: TableVersion = tableCimVersion,
    private val schemaTestFile: String = "src/test/data/schemaTest.sqlite",
    private val databaseInitialiser: DatabaseInitialiser<CimDatabaseTables> = SqliteDatabaseInitialiser(schemaTestFile),
    private val describeObject: TMaxsCoolObject.() -> String,
    private val addToService: TService.(TMaxsCoolObject) -> Boolean,
) {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    protected val logger: Logger = LoggerFactory.getLogger(javaClass)

    abstract fun createService(): TService
    abstract fun createWriter(filename: String): TWriter
    abstract fun createReader(connection: Connection, databaseDescription: String): TReader
    abstract fun createComparator(): TComparator
    abstract fun createIdentifiedObject(): TMaxsCoolObject

    @BeforeEach
    internal fun beforeEach() {
        Files.deleteIfExists(Paths.get(schemaTestFile))
    }

    @AfterEach
    internal fun afterEach() {
        Files.deleteIfExists(Paths.get(schemaTestFile))
    }

    @Test
    internal fun `test metadata data source schema`() {
        // Put a copy of the metadata into an appropriate service for comparison.
        val service = createService().apply {
            SchemaServices.createDataSourceTestServices().dataSources.forEach { metadata.add(it) }
        }

        validateSchema(service)
    }

    @Test
    internal fun `check for error on duplicate id`() {
        val writeService = createService()
        val readService = createService()

        val obj = createIdentifiedObject()
        assertThat("Should have added", writeService.addToService(obj))
        assertThat("Should have added", readService.addToService(obj))

        validateWriteRead(writeService, readService = readService)

        assertThat(
            systemErr.log,
            containsString("Failed to read ${obj.describeObject()}. Unable to add to service '${readService.name}': duplicate MRID")
        )
    }

    protected fun validateSchema(expectedService: TService) {
        validateWriteRead(expectedService) { readService ->
            validateMetadata(readService.metadata, expectedService.metadata)
            validateService(readService, expectedService, createComparator())
        }
    }

    protected fun validateWriteRead(
        writeService: TService = createService(),
        readService: TService = createService(),
        validateRead: ((TService) -> Unit)? = null
    ) {
        assertThat("Database should have been written", createWriter(schemaTestFile).write(writeService))

        if (databaseInitialiser is SqliteDatabaseInitialiser) {
            assertThat(systemErr.log, containsString("Creating database schema v${tableVersion.supportedVersion}"))
            assertThat("Database should now exist", Files.exists(Paths.get(schemaTestFile)))
        } else if (databaseInitialiser is NoOpDatabaseInitialiser) {
            assertThat(systemErr.log, containsString("Committing..."))
            assertThat(systemErr.log, containsString("Done."))
        }

        systemErr.clearCapturedLog()
        val status = databaseInitialiser.connect().use {
            createReader(it, schemaTestFile).read(readService)
        }

        if (validateRead != null) {
            assertThat("Database read should have succeeded", status)
            validateRead(readService)
        } else
            assertThat("database read should have failed", !status)
    }

    protected fun validateUnresolvedFailure(expectedSource: String, expectedTarget: String, addDeferredReference: TService.() -> Unit) {
        // Add an unresolved reference that should trigger the after read check.
        val service = createService().apply { addDeferredReference() }

        validateWriteRead(readService = service)

        assertThat(
            systemErr.log,
            containsString(
                "Unresolved references were found in ${service.name} service after read - this should not occur. " +
                    "Failing reference was from $expectedSource resolving $expectedTarget"
            )
        )
    }

    private fun validateMetadata(metadata: MetadataCollection, expectedMetadataCollection: MetadataCollection) {
        assertThat(metadata.dataSources, containsInAnyOrder(*expectedMetadataCollection.dataSources.toTypedArray()))
    }

    private fun validateService(service: TService, expectedService: TService, serviceComparator: TComparator) {
        val differences = serviceComparator.compare(service, expectedService)

        if (differences.modifications().isNotEmpty())
            System.err.println(differences.toString())

        assertThat("unexpected objects found in read service: ${differences.missingFromTarget()}", differences.missingFromTarget(), empty())
        assertThat("unexpected modifications ${differences.modifications()}", differences.modifications(), anEmptyMap())
        assertThat("objects missing from read service: ${differences.missingFromSource()}", differences.missingFromSource(), empty())
    }

}

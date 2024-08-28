/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.cim

import com.zepben.evolve.cim.iec61970.base.core.IdentifiedObject
import com.zepben.evolve.database.sqlite.cim.tables.tableCimVersion
import com.zepben.evolve.services.common.BaseService
import com.zepben.evolve.services.common.BaseServiceComparator
import com.zepben.evolve.services.common.extensions.typeNameAndMRID
import com.zepben.evolve.services.common.meta.MetadataCollection
import com.zepben.evolve.services.common.testdata.SchemaServices
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
abstract class CimDatabaseSchemaTest<TService : BaseService, TWriter : CimDatabaseWriter, TReader : CimDatabaseReader, TComparator : BaseServiceComparator> {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    protected val logger: Logger = LoggerFactory.getLogger(javaClass)
    private val schemaTestFile = "src/test/data/schemaTest.sqlite"

    abstract fun createService(): TService
    abstract fun createWriter(filename: String, metadata: MetadataCollection, service: TService): TWriter
    abstract fun createReader(connection: Connection, metadata: MetadataCollection, service: TService, databaseDescription: String): TReader
    abstract fun createComparator(): TComparator
    abstract fun createIdentifiedObject(): IdentifiedObject

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
        validateSchema(expectedMetadata = SchemaServices.createDataSourceTestServices())
    }

    @Test
    internal fun `check for error on duplicate id`() {
        val writeService = createService()
        val readService = createService()

        val identifiedObject = createIdentifiedObject()
        assertThat("Should have added", writeService.tryAdd(identifiedObject))
        assertThat("Should have added", readService.tryAdd(identifiedObject))

        validateWriteRead(writeService, readService = readService)

        assertThat(
            systemErr.log,
            containsString("Failed to load ${identifiedObject.typeNameAndMRID()}. Unable to add to service '${readService.name}': duplicate MRID")
        )
    }

    protected fun validateSchema(expectedService: TService = createService(), expectedMetadata: MetadataCollection = MetadataCollection()) {
        validateWriteRead(expectedService, expectedMetadata) { readService, readMetadata ->
            validateMetadata(readMetadata, expectedMetadata)
            validateService(readService, expectedService, createComparator())
        }
    }

    protected fun validateWriteRead(
        writeService: TService = createService(),
        writeMetadata: MetadataCollection = MetadataCollection(),
        readService: TService = createService(),
        readMetadata: MetadataCollection = MetadataCollection(),
        validateRead: ((TService, MetadataCollection) -> Unit)? = null
    ) {
        assertThat("Database should have been saved", createWriter(schemaTestFile, writeMetadata, writeService).save())

        assertThat(systemErr.log, containsString("Creating database schema v${tableCimVersion.supportedVersion}"))
        assertThat("Database should now exist", Files.exists(Paths.get(schemaTestFile)))

        systemErr.clearCapturedLog()
        val status = DriverManager.getConnection("jdbc:sqlite:$schemaTestFile").use { connection ->
            createReader(connection, readMetadata, readService, schemaTestFile).load()
        }

        if (validateRead != null) {
            assertThat("Database read should have succeeded", status)
            validateRead(readService, readMetadata)
        } else
            assertThat("database read should have failed", !status)
    }

    protected fun validateUnresolvedFailure(expectedSource: String, expectedTarget: String, addDeferredReference: TService.() -> Unit) {
        // Add an unresolved reference that should trigger the post load check.
        val service = createService().apply { addDeferredReference() }

        validateWriteRead(readService = service)

        assertThat(
            systemErr.log,
            containsString(
                "Unresolved references were found in ${service.name} service after load - this should not occur. " +
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

        assertThat("unexpected objects found in loaded service", differences.missingFromTarget(), empty())
        assertThat("unexpected modifications", differences.modifications(), anEmptyMap())
        assertThat("objects missing from loaded service", differences.missingFromSource(), empty())
    }

}
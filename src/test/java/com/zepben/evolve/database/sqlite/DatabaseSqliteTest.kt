/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.database.sqlite

import com.zepben.evolve.cim.iec61968.customers.Customer
import com.zepben.evolve.cim.iec61970.base.core.IdentifiedObject
import com.zepben.evolve.cim.iec61970.base.diagramlayout.Diagram
import com.zepben.evolve.cim.iec61970.base.diagramlayout.DiagramObject
import com.zepben.evolve.cim.iec61970.base.wires.Junction
import com.zepben.evolve.database.sqlite.tables.TableVersion
import com.zepben.evolve.services.common.BaseService
import com.zepben.evolve.services.common.BaseServiceComparator
import com.zepben.evolve.services.common.extensions.typeNameAndMRID
import com.zepben.evolve.services.common.meta.MetadataCollection
import com.zepben.evolve.services.customer.CustomerService
import com.zepben.evolve.services.customer.CustomerServiceComparator
import com.zepben.evolve.services.diagram.DiagramService
import com.zepben.evolve.services.diagram.DiagramServiceComparator
import com.zepben.evolve.services.network.NetworkModelTestUtil
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.NetworkServiceComparator
import com.zepben.evolve.services.network.testdata.SchemaTestNetwork
import com.zepben.evolve.services.network.testdata.StupidlyLargeNetwork
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.slf4j.LoggerFactory
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths

class DatabaseSqliteTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @BeforeEach
    @Throws(IOException::class)
    fun setup() {
        Files.deleteIfExists(Paths.get(SCHEMA_TEST_FILE))
    }

    @AfterEach
    @Throws(IOException::class)
    fun teardown() {
        Files.deleteIfExists(Paths.get(SCHEMA_TEST_FILE))
    }

    @Test
    @Disabled
    fun checkMemoryUsage() {
        val databaseFileName = "src/test/data/enmac_extract.sqlite"

        assertThat(Files.exists(Paths.get(databaseFileName)), equalTo(true))

        val metadataCollection = MetadataCollection()
        val networkLoaded = NetworkService()
        val diagramLoaded = DiagramService()
        val customerService = CustomerService()

        assertThat(
            DatabaseReader(databaseFileName).load(metadataCollection, networkLoaded, diagramLoaded, customerService),
            equalTo(true)
        )

        logger.info("Sleeping...")
        try {
            Thread.sleep(Long.MAX_VALUE)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    @Test
    fun testStupidlyLargeSchema() {
        validateSchema(StupidlyLargeNetwork.create())

        assertThat(
            systemErr.log,
            containsString("Primary source 'primary source' [primary_source] has been assigned to the following feeders: normal [f1], current [f2]")
        )
    }

    /************ IEC61968 ASSET INFO ************/

    @Test
    fun `test PowerTransformerInfo schema`() {
        validateSchema(SchemaTestNetwork.createPowerTransformerInfoTestServices())
    }

    /************ IEC61968 ASSETS ************/

    @Test
    fun `test Pole schema`() {
        validateSchema(SchemaTestNetwork.createPoleTestServices())
    }

    @Test
    fun `test Streetlight schema`() {
        validateSchema(SchemaTestNetwork.createStreetlightTestServices())
    }

    /************ IEC61970 WIRES ************/

    @Test
    fun `test BusbarSection schema`() {
        validateSchema(SchemaTestNetwork.createBusbarSectionServices())
    }

    @Test
    fun `test PowerTransformer schema`() {
        validateSchema(SchemaTestNetwork.createPowerTransformerTestServices())
    }

    @Test
    fun `test LoadBreakSwitch schema`() {
        validateSchema(SchemaTestNetwork.createLoadBreakSwitchTestServices())
    }

    @Test
    fun `test Breaker schema`() {
        validateSchema(SchemaTestNetwork.createBreakerTestServices())
    }

    /************ IEC61970 WIRES GENERATION PRODUCTION ************/

    @Test
    fun `test BatteryUnit schema`() {
        validateSchema(SchemaTestNetwork.createBatteryUnitTestServices())
    }

    @Test
    fun `test PhotoVoltaic schema`() {
        validateSchema(SchemaTestNetwork.createPhotoVoltaicUnitTestServices())
    }

    @Test
    fun `test PowerElectronicsConnection schema`() {
        validateSchema(SchemaTestNetwork.createPowerElectronicsConnectionTestServices())
    }

    @Test
    fun `test PowerElectronicsConnectionPhase schema`() {
        validateSchema(SchemaTestNetwork.createPowerElectronicsConnectionPhaseTestServices())
    }

    @Test
    fun `test PowerElectronicsWindUnit schema`() {
        validateSchema(SchemaTestNetwork.createPowerElectronicsWindUnitTestServices())
    }

    /************ IEC61970 InfIEC61970 ************/

    @Test
    fun testCircuitSchema() {
        validateSchema(SchemaTestNetwork.createCircuitTestServices())
    }

    @Test
    fun testLoopSchema() {
        validateSchema(SchemaTestNetwork.createLoopTestServices())
    }

    /************ OTHER ************/

    @Test
    fun testMetadataDataSourceSchema() {
        validateSchema(SchemaTestNetwork.createDataSourceTestServices())
    }

    @Test
    internal fun `check for error on duplicate id added to customer service`() {
        val writeServices = NetworkModelTestUtil.Services()
        val readServices = NetworkModelTestUtil.Services()

        val customer = Customer("customer1")
        writeServices.customerService.add(customer)
        readServices.customerService.add(customer)

        testDuplicateMridError(writeServices, readServices, readServices.customerService, customer)
    }

    @Test
    internal fun `check for error on duplicate id added to diagram service`() {
        val writeServices = NetworkModelTestUtil.Services()
        val readServices = NetworkModelTestUtil.Services()

        val diagram = Diagram("diagram1")
        writeServices.diagramService.add(diagram)
        readServices.diagramService.add(diagram)

        testDuplicateMridError(writeServices, readServices, readServices.diagramService, diagram)
    }

    @Test
    internal fun `check for error on duplicate id added to network service`() {
        val writeServices = NetworkModelTestUtil.Services()
        val readServices = NetworkModelTestUtil.Services()

        val junction = Junction("junction1")
        writeServices.networkService.add(junction)
        readServices.networkService.add(junction)

        testDuplicateMridError(writeServices, readServices, readServices.networkService, junction)
    }

    private fun testDuplicateMridError(
        writeServices: NetworkModelTestUtil.Services,
        readServices: NetworkModelTestUtil.Services,
        serviceWithDuplicate: BaseService,
        duplicate: IdentifiedObject
    ) {
        val expectedError =
            "Failed to load ${duplicate.typeNameAndMRID()}. Unable to add to service '${serviceWithDuplicate.name}': duplicate MRID"

        testWriteRead(
            writeServices,
            readServices,
            { success -> assertThat(success, equalTo(true)) },
            { success ->
                assertThat(success, equalTo(false))
                assertThat(systemErr.log, containsString(expectedError))
            }
        )
    }

    private fun validateSchema(services: NetworkModelTestUtil.Services) {
        assertThat(systemErr.logLines.size, equalTo(0))

        val (expectedMetadata, expectedNetworkService, expectedDiagramService, expectedCustomerService) = services

        assertThat(
            DatabaseWriter(SCHEMA_TEST_FILE).save(
                expectedMetadata,
                mutableListOf(
                    expectedNetworkService,
                    expectedDiagramService,
                    expectedCustomerService
                )
            ),
            equalTo(true)
        )

        assertThat(systemErr.log, containsString("Creating database schema v${TableVersion().SUPPORTED_VERSION}"))
        assertThat(Files.exists(Paths.get(SCHEMA_TEST_FILE)), equalTo(true))

        val metadataCollection = MetadataCollection()
        val networkService = NetworkService()
        val diagramService = DiagramService()
        val customerService = CustomerService()

        assertThat(
            DatabaseReader(SCHEMA_TEST_FILE).load(metadataCollection, networkService, diagramService, customerService),
            equalTo(true)
        )

        validateMetadata(metadataCollection, expectedMetadata)
        validateService(networkService, expectedNetworkService) { NetworkServiceComparator() }
        validateService(diagramService, expectedDiagramService) { DiagramServiceComparator() }
        validateService(customerService, expectedCustomerService) { CustomerServiceComparator() }
        expectedDiagramService.sequenceOf<DiagramObject>()
            .filter { it.identifiedObjectMRID != null }
            .forEach { assertThat(diagramService.getDiagramObjects(it.identifiedObjectMRID!!), not(empty())) }
    }

    private fun testWriteRead(
        writeServices: NetworkModelTestUtil.Services,
        readServices: NetworkModelTestUtil.Services,
        validateWrite: (Boolean) -> Unit,
        validateRead: (Boolean) -> Unit
    ) {
        assertThat(systemErr.logLines.size, equalTo(0))
        val (writeMetadata, writeNetworkService, writeDiagramService, writeCustomerService) = writeServices

        validateWrite(
            DatabaseWriter(SCHEMA_TEST_FILE).save(
                writeMetadata,
                mutableListOf(
                    writeNetworkService,
                    writeDiagramService,
                    writeCustomerService
                )
            )
        )

        if (!Files.exists(Paths.get(SCHEMA_TEST_FILE)))
            return

        val (readMetadata, readNetworkService, readDiagramService, readCustomerService) = readServices
        validateRead(
            DatabaseReader(SCHEMA_TEST_FILE).load(
                readMetadata,
                readNetworkService,
                readDiagramService,
                readCustomerService
            )
        )
    }

    private fun validateMetadata(metadataCollection: MetadataCollection, expectedMetadataCollection: MetadataCollection) {
        assertThat(metadataCollection.dataSources, containsInAnyOrder(*expectedMetadataCollection.dataSources.toTypedArray()))
    }

    private fun validateService(
        service: BaseService,
        expectedService: BaseService,
        getComparator: () -> BaseServiceComparator
    ) {
        val differences = getComparator().compare(service, expectedService)

        System.err.println(differences.toString())

        assertThat("objects missing from actual network", differences.missingFromTarget(), empty())
        assertThat("unexpected modifications", differences.modifications(), anEmptyMap())
        assertThat("unexpected objects found in actual network", differences.missingFromSource(), empty())
    }

    companion object {
        private val logger = LoggerFactory.getLogger(DatabaseSqliteTest::class.java)
        private const val SCHEMA_TEST_FILE = "src/test/data/schemaTest.sqlite"
    }
}

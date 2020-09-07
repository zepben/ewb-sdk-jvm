/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 * This file is part of evolve-sdk-jvm.
 *
 * evolve-sdk-jvm is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * evolve-sdk-jvm is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with evolve-sdk-jvm.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.zepben.cimbend.database.sqlite

import com.zepben.cimbend.cim.iec61968.customers.Customer
import com.zepben.cimbend.cim.iec61970.base.core.IdentifiedObject
import com.zepben.cimbend.cim.iec61970.base.diagramlayout.Diagram
import com.zepben.cimbend.cim.iec61970.base.diagramlayout.DiagramObject
import com.zepben.cimbend.cim.iec61970.base.wires.Junction
import com.zepben.cimbend.common.BaseService
import com.zepben.cimbend.common.BaseServiceComparator
import com.zepben.cimbend.common.extensions.typeNameAndMRID
import com.zepben.cimbend.customer.CustomerService
import com.zepben.cimbend.customer.CustomerServiceComparator
import com.zepben.cimbend.database.sqlite.tables.TableVersion
import com.zepben.cimbend.diagram.DiagramService
import com.zepben.cimbend.diagram.DiagramServiceComparator
import com.zepben.cimbend.measurement.MeasurementService
import com.zepben.cimbend.network.NetworkModelTestUtil
import com.zepben.cimbend.network.NetworkService
import com.zepben.cimbend.network.NetworkServiceComparator
import com.zepben.cimbend.network.SchemaTestNetwork
import com.zepben.test.util.junit.SystemLogExtension
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

        val networkLoaded = NetworkService()
        val diagramLoaded = DiagramService()
        val customerService = CustomerService()

        assertThat(
            DatabaseReader(databaseFileName).load(networkLoaded, diagramLoaded, customerService),
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
        validateSchema(SchemaTestNetwork.createStupidlyLargeServices())

        assertThat(
            systemErr.log,
            containsString("Primary source 'primary source' [primary_source] has been assigned to the following feeders: normal [f1], current [f2]")
        )
    }

    @Test
    fun `test Pole Schema`() {
        validateSchema(SchemaTestNetwork.createPoleTestServices())
    }


    @Test
    fun `test Streetlight Schema`() {
        validateSchema(SchemaTestNetwork.createStreetlightTestServices())
    }

    @Test
    fun testCircuitSchema() {
        validateSchema(SchemaTestNetwork.createCircuitTestServices())
    }

    @Test
    fun testLoopSchema() {
        validateSchema(SchemaTestNetwork.createLoopTestServices())
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

    private fun testDuplicateMridError(writeServices: NetworkModelTestUtil.Services,
                                       readServices: NetworkModelTestUtil.Services,
                                       serviceWithDuplicate: BaseService,
                                       duplicate: IdentifiedObject) {
        val expectedError = "Failed to load ${duplicate.typeNameAndMRID()}. Unable to add to service '${serviceWithDuplicate.name}': duplicate MRID"

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

        val (expectedNetworkService, expectedDiagramService, expectedCustomerService) = services

        assertThat(
            DatabaseWriter(SCHEMA_TEST_FILE).save(
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

        val networkService = NetworkService()
        val diagramService = DiagramService()
        val customerService = CustomerService()

        assertThat(DatabaseReader(SCHEMA_TEST_FILE).load(networkService, diagramService, customerService), equalTo(true))

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
        val (writeNetworkService, writeDiagramService, writeCustomerService) = writeServices

        validateWrite(
            DatabaseWriter(SCHEMA_TEST_FILE).save(
                mutableListOf(
                    writeNetworkService,
                    writeDiagramService,
                    writeCustomerService
                )
            ))

        if (!Files.exists(Paths.get(SCHEMA_TEST_FILE)))
            return

        val (readNetworkService, readDiagramService, readCustomerService) = readServices
        validateRead(
            DatabaseReader(SCHEMA_TEST_FILE).load(
                readNetworkService,
                readDiagramService,
                readCustomerService
            ))
    }

    private fun validateService(service: BaseService, expectedService: BaseService, getComparator: () -> BaseServiceComparator) {
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

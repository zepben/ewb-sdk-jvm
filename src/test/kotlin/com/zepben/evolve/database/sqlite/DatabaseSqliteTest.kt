/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.database.sqlite

import com.zepben.evolve.cim.iec61968.assetinfo.*
import com.zepben.evolve.cim.iec61968.assets.AssetOwner
import com.zepben.evolve.cim.iec61968.assets.Pole
import com.zepben.evolve.cim.iec61968.assets.Streetlight
import com.zepben.evolve.cim.iec61968.common.Location
import com.zepben.evolve.cim.iec61968.common.Organisation
import com.zepben.evolve.cim.iec61968.customers.Customer
import com.zepben.evolve.cim.iec61968.customers.CustomerAgreement
import com.zepben.evolve.cim.iec61968.customers.PricingStructure
import com.zepben.evolve.cim.iec61968.customers.Tariff
import com.zepben.evolve.cim.iec61968.metering.Meter
import com.zepben.evolve.cim.iec61968.metering.UsagePoint
import com.zepben.evolve.cim.iec61968.operations.OperationalRestriction
import com.zepben.evolve.cim.iec61970.base.auxiliaryequipment.FaultIndicator
import com.zepben.evolve.cim.iec61970.base.core.*
import com.zepben.evolve.cim.iec61970.base.diagramlayout.Diagram
import com.zepben.evolve.cim.iec61970.base.diagramlayout.DiagramObject
import com.zepben.evolve.cim.iec61970.base.equivalents.EquivalentBranch
import com.zepben.evolve.cim.iec61970.base.meas.Accumulator
import com.zepben.evolve.cim.iec61970.base.meas.Analog
import com.zepben.evolve.cim.iec61970.base.meas.Control
import com.zepben.evolve.cim.iec61970.base.meas.Discrete
import com.zepben.evolve.cim.iec61970.base.scada.RemoteControl
import com.zepben.evolve.cim.iec61970.base.scada.RemoteSource
import com.zepben.evolve.cim.iec61970.base.wires.*
import com.zepben.evolve.cim.iec61970.base.wires.generation.production.BatteryUnit
import com.zepben.evolve.cim.iec61970.base.wires.generation.production.PhotoVoltaicUnit
import com.zepben.evolve.cim.iec61970.base.wires.generation.production.PowerElectronicsWindUnit
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.Circuit
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.Loop
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.LvFeeder
import com.zepben.evolve.database.sqlite.tables.TableVersion
import com.zepben.evolve.services.common.BaseService
import com.zepben.evolve.services.common.BaseServiceComparator
import com.zepben.evolve.services.common.extensions.typeNameAndMRID
import com.zepben.evolve.services.common.meta.MetadataCollection
import com.zepben.evolve.services.common.testdata.fillFieldsCommon
import com.zepben.evolve.services.customer.CustomerService
import com.zepben.evolve.services.customer.CustomerServiceComparator
import com.zepben.evolve.services.customer.testdata.fillFields
import com.zepben.evolve.services.diagram.DiagramService
import com.zepben.evolve.services.diagram.DiagramServiceComparator
import com.zepben.evolve.services.diagram.testdata.fillFields
import com.zepben.evolve.services.network.NetworkModelTestUtil
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.NetworkServiceComparator
import com.zepben.evolve.services.network.testdata.SchemaNetworks
import com.zepben.evolve.services.network.testdata.StupidlyLargeNetwork
import com.zepben.evolve.services.network.testdata.fillFields
import com.zepben.evolve.services.network.tracing.Tracing
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
import java.nio.file.Path
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
    fun loadRealFile() {
        systemErr.unmute()

        // Put the name of the database you want to load in src/test/resources/text-database.txt
        val databaseFileName = Files.readString(Path.of("src", "test", "resources", "test-database.txt")).trim().trim('"')

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
        // TODO - This needs to be replaced with a test for assigning to feeders and checking the below error. This should be
        //        done in a separate task to monitor code coverage drops.
        validateSchema(StupidlyLargeNetwork.create())

        assertThat(
            systemErr.log,
            containsString("External grid source 'primary source' [primary_source] has been assigned to the following feeders: normal [f1], current [f2]")
        )
    }

    @Test
    fun `test schema for each supported type`() {
        /************ IEC61968 ASSET INFO ************/
        validateSchema(SchemaNetworks.networkServicesOf(::CableInfo, CableInfo::fillFields))
        validateSchema(SchemaNetworks.networkServicesOf(::NoLoadTest, NoLoadTest::fillFields))
        validateSchema(SchemaNetworks.networkServicesOf(::OpenCircuitTest, OpenCircuitTest::fillFields))
        validateSchema(SchemaNetworks.networkServicesOf(::OverheadWireInfo, OverheadWireInfo::fillFields))
        validateSchema(SchemaNetworks.networkServicesOf(::PowerTransformerInfo, PowerTransformerInfo::fillFields))
        validateSchema(SchemaNetworks.networkServicesOf(::ShortCircuitTest, ShortCircuitTest::fillFields))
        validateSchema(SchemaNetworks.networkServicesOf(::ShuntCompensatorInfo, ShuntCompensatorInfo::fillFields))
        validateSchema(SchemaNetworks.networkServicesOf(::TransformerEndInfo, TransformerEndInfo::fillFields))
        validateSchema(SchemaNetworks.networkServicesOf(::TransformerTankInfo, TransformerTankInfo::fillFields))

        /************ IEC61968 ASSETS ************/
        validateSchema(SchemaNetworks.networkServicesOf(::AssetOwner, AssetOwner::fillFields))
        validateSchema(SchemaNetworks.networkServicesOf(::Pole, Pole::fillFields))
        validateSchema(SchemaNetworks.networkServicesOf(::Streetlight, Streetlight::fillFields))

        /************ IEC61968 CUSTOMERS ************/
        validateSchema(SchemaNetworks.customerServicesOf(::Customer, Customer::fillFields))
        validateSchema(SchemaNetworks.customerServicesOf(::CustomerAgreement, CustomerAgreement::fillFields))
        validateSchema(SchemaNetworks.customerServicesOf(::PricingStructure, PricingStructure::fillFields))
        validateSchema(SchemaNetworks.customerServicesOf(::Tariff, Tariff::fillFields))

        /************ IEC61968 METERING ************/
        validateSchema(SchemaNetworks.networkServicesOf(::Meter, Meter::fillFields))
        validateSchema(SchemaNetworks.networkServicesOf(::UsagePoint, UsagePoint::fillFields))

        /************ IEC61968 COMMON ************/
        validateSchema(SchemaNetworks.networkServicesOf(::Location, Location::fillFields))
        validateSchema(SchemaNetworks.networkServicesOf(::Organisation, Organisation::fillFieldsCommon))
        validateSchema(SchemaNetworks.customerServicesOf(::Organisation, Organisation::fillFieldsCommon))

        /************ IEC61968 OPERATIONS ************/
        validateSchema(SchemaNetworks.networkServicesOf(::OperationalRestriction, OperationalRestriction::fillFields))

        /************ IEC61970 BASE AUXILIARY EQUIPMENT ************/
        validateSchema(SchemaNetworks.networkServicesOf(::FaultIndicator, FaultIndicator::fillFields))

        /************ IEC61970 BASE CORE ************/
        validateSchema(SchemaNetworks.networkServicesOf(::BaseVoltage, BaseVoltage::fillFields))
        validateSchema(SchemaNetworks.networkServicesOf(::ConnectivityNode, ConnectivityNode::fillFields))
        validateSchema(SchemaNetworks.networkServicesOf(::Feeder, Feeder::fillFields).apply { Tracing.setDirection().run(networkService) })
        validateSchema(SchemaNetworks.networkServicesOf(::GeographicalRegion, GeographicalRegion::fillFields))
        validateSchema(SchemaNetworks.networkServicesOf(::Site, Site::fillFields))
        validateSchema(SchemaNetworks.networkServicesOf(::SubGeographicalRegion, SubGeographicalRegion::fillFields))
        validateSchema(SchemaNetworks.networkServicesOf(::Substation, Substation::fillFields))
        validateSchema(SchemaNetworks.networkServicesOf(::Terminal, Terminal::fillFields))

        /************ IEC61970 BASE DIAGRAM LAYOUT ************/
        validateSchema(SchemaNetworks.diagramServicesOf(::Diagram, Diagram::fillFields))
        validateSchema(SchemaNetworks.diagramServicesOf(::DiagramObject, DiagramObject::fillFields))

        /************ IEC61970 BASE EQUIVALENTS ************/
        validateSchema(SchemaNetworks.networkServicesOf(::EquivalentBranch, EquivalentBranch::fillFields))

        /************ IEC61970 BASE MEAS ************/
        validateSchema(SchemaNetworks.networkServicesOf(::Accumulator, Accumulator::fillFields))
        // validateSchema(SchemaNetworks.measurementServicesOf(::AccumulatorValue, AccumulatorValue::fillFields))
        validateSchema(SchemaNetworks.networkServicesOf(::Analog, Analog::fillFields))
        // validateSchema(SchemaNetworks.measurementServicesOf(::AnalogValue, AnalogValue::fillFields))
        validateSchema(SchemaNetworks.networkServicesOf(::Control, Control::fillFields))
        validateSchema(SchemaNetworks.networkServicesOf(::Discrete, Discrete::fillFields))
        // validateSchema(SchemaNetworks.measurementServicesOf(::DiscreteValue, DiscreteValue::fillFields))

        /************ IEC61970 BASE SCADA ************/
        validateSchema(SchemaNetworks.networkServicesOf(::RemoteControl, RemoteControl::fillFields))
        validateSchema(SchemaNetworks.networkServicesOf(::RemoteSource, RemoteSource::fillFields))

        /************ IEC61970 BASE WIRES GENERATION PRODUCTION ************/
        validateSchema(SchemaNetworks.networkServicesOf(::BatteryUnit, BatteryUnit::fillFields))
        validateSchema(SchemaNetworks.networkServicesOf(::PhotoVoltaicUnit, PhotoVoltaicUnit::fillFields))
        validateSchema(SchemaNetworks.networkServicesOf(::PowerElectronicsConnection, PowerElectronicsConnection::fillFields))
        validateSchema(SchemaNetworks.networkServicesOf(::PowerElectronicsConnectionPhase, PowerElectronicsConnectionPhase::fillFields))
        validateSchema(SchemaNetworks.networkServicesOf(::PowerElectronicsWindUnit, PowerElectronicsWindUnit::fillFields))

        /************ IEC61970 BASE WIRES ************/
        validateSchema(SchemaNetworks.networkServicesOf(::AcLineSegment, AcLineSegment::fillFields))
        validateSchema(SchemaNetworks.networkServicesOf(::Breaker, Breaker::fillFields))
        validateSchema(SchemaNetworks.networkServicesOf(::BusbarSection, BusbarSection::fillFields))
        validateSchema(SchemaNetworks.networkServicesOf(::Disconnector, Disconnector::fillFields))
        validateSchema(SchemaNetworks.networkServicesOf(::EnergyConsumer, EnergyConsumer::fillFields))
        validateSchema(SchemaNetworks.networkServicesOf(::EnergyConsumerPhase, EnergyConsumerPhase::fillFields))
        validateSchema(SchemaNetworks.networkServicesOf(::EnergySource, EnergySource::fillFields).apply { Tracing.setPhases().run(networkService) })
        validateSchema(SchemaNetworks.networkServicesOf(::EnergySourcePhase, EnergySourcePhase::fillFields))
        validateSchema(SchemaNetworks.networkServicesOf(::Fuse, Fuse::fillFields))
        validateSchema(SchemaNetworks.networkServicesOf(::Jumper, Jumper::fillFields))
        validateSchema(SchemaNetworks.networkServicesOf(::Junction, Junction::fillFields))
        validateSchema(SchemaNetworks.networkServicesOf(::LinearShuntCompensator, LinearShuntCompensator::fillFields))
        validateSchema(SchemaNetworks.networkServicesOf(::LoadBreakSwitch, LoadBreakSwitch::fillFields))
        validateSchema(SchemaNetworks.networkServicesOf(::PerLengthSequenceImpedance, PerLengthSequenceImpedance::fillFields))
        validateSchema(SchemaNetworks.networkServicesOf(::PowerTransformer, PowerTransformer::fillFields))
        validateSchema(SchemaNetworks.networkServicesOf(::PowerTransformerEnd, PowerTransformerEnd::fillFields))
        validateSchema(SchemaNetworks.networkServicesOf(::RatioTapChanger, RatioTapChanger::fillFields))
        validateSchema(SchemaNetworks.networkServicesOf(::Recloser, Recloser::fillFields))
        validateSchema(SchemaNetworks.networkServicesOf(::TransformerStarImpedance, TransformerStarImpedance::fillFields))

        /************ IEC61970 InfIEC61970 ************/
        validateSchema(SchemaNetworks.networkServicesOf(::Circuit, Circuit::fillFields))
        validateSchema(SchemaNetworks.networkServicesOf(::Loop, Loop::fillFields))
        validateSchema(SchemaNetworks.networkServicesOf(::LvFeeder, LvFeeder::fillFields))
    }

    @Test
    fun testMetadataDataSourceSchema() {
        validateSchema(SchemaNetworks.createDataSourceTestServices())
    }

    @Test
    fun `test Name and NameType schema`() {
        validateSchema(SchemaNetworks.createNameTestServices())
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
        systemErr.clearCapturedLog()

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

        if (differences.modifications().isNotEmpty())
            System.err.println(differences.toString())

        assertThat("unexpected objects found in loaded network", differences.missingFromTarget(), empty())
        assertThat("unexpected modifications", differences.modifications(), anEmptyMap())
        assertThat("objects missing from loaded network", differences.missingFromSource(), empty())
    }

    companion object {
        private val logger = LoggerFactory.getLogger(DatabaseSqliteTest::class.java)
        private const val SCHEMA_TEST_FILE = "src/test/data/schemaTest.sqlite"
    }

}

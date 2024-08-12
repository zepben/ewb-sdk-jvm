/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.cim.network

import com.zepben.evolve.cim.iec61968.assetinfo.*
import com.zepben.evolve.cim.iec61968.assets.AssetOwner
import com.zepben.evolve.cim.iec61968.assets.Pole
import com.zepben.evolve.cim.iec61968.assets.Streetlight
import com.zepben.evolve.cim.iec61968.common.*
import com.zepben.evolve.cim.iec61968.infiec61968.infassetinfo.CurrentTransformerInfo
import com.zepben.evolve.cim.iec61968.infiec61968.infassetinfo.PotentialTransformerInfo
import com.zepben.evolve.cim.iec61968.infiec61968.infassetinfo.RelayInfo
import com.zepben.evolve.cim.iec61968.metering.Meter
import com.zepben.evolve.cim.iec61968.metering.UsagePoint
import com.zepben.evolve.cim.iec61968.operations.OperationalRestriction
import com.zepben.evolve.cim.iec61970.base.auxiliaryequipment.CurrentTransformer
import com.zepben.evolve.cim.iec61970.base.auxiliaryequipment.FaultIndicator
import com.zepben.evolve.cim.iec61970.base.auxiliaryequipment.PotentialTransformer
import com.zepben.evolve.cim.iec61970.base.core.*
import com.zepben.evolve.cim.iec61970.base.equivalents.EquivalentBranch
import com.zepben.evolve.cim.iec61970.base.meas.Accumulator
import com.zepben.evolve.cim.iec61970.base.meas.Analog
import com.zepben.evolve.cim.iec61970.base.meas.Control
import com.zepben.evolve.cim.iec61970.base.meas.Discrete
import com.zepben.evolve.cim.iec61970.base.protection.*
import com.zepben.evolve.cim.iec61970.base.scada.RemoteControl
import com.zepben.evolve.cim.iec61970.base.scada.RemoteSource
import com.zepben.evolve.cim.iec61970.base.wires.*
import com.zepben.evolve.cim.iec61970.base.wires.generation.production.BatteryUnit
import com.zepben.evolve.cim.iec61970.base.wires.generation.production.PhotoVoltaicUnit
import com.zepben.evolve.cim.iec61970.base.wires.generation.production.PowerElectronicsWindUnit
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.Circuit
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.Loop
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.LvFeeder
import com.zepben.evolve.cim.iec61970.infiec61970.wires.generation.production.EvChargingUnit
import com.zepben.evolve.database.sqlite.cim.CimDatabaseSchemaTest
import com.zepben.evolve.services.common.Resolvers
import com.zepben.evolve.services.common.meta.MetadataCollection
import com.zepben.evolve.services.common.testdata.SchemaServices
import com.zepben.evolve.services.common.testdata.fillFieldsCommon
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.NetworkServiceComparator
import com.zepben.evolve.services.network.testdata.fillFields
import com.zepben.evolve.services.network.testdata.stupid.StupidlyLargeNetwork
import com.zepben.evolve.services.network.tracing.Tracing
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.sql.Connection
import java.sql.DriverManager

class NetworkDatabaseSchemaTest : CimDatabaseSchemaTest<NetworkService, NetworkDatabaseWriter, NetworkDatabaseReader, NetworkServiceComparator>() {

    override fun createService(): NetworkService = NetworkService()

    override fun createWriter(filename: String, metadata: MetadataCollection, service: NetworkService): NetworkDatabaseWriter =
        NetworkDatabaseWriter(filename, metadata, service)

    override fun createReader(
        connection: Connection,
        metadata: MetadataCollection,
        service: NetworkService,
        databaseDescription: String
    ): NetworkDatabaseReader =
        NetworkDatabaseReader(connection, metadata, service, databaseDescription)

    override fun createComparator(): NetworkServiceComparator = NetworkServiceComparator()

    override fun createIdentifiedObject(): IdentifiedObject = Junction()

    @Test
    @Disabled
    fun loadRealFile() {
        systemErr.unmute()

        // Put the name of the database you want to load in src/test/resources/test-network-database.txt
        val databaseFile = Files.readString(Path.of("src", "test", "resources", "test-network-database.txt")).trim().trim('"')

        assertThat("database must exist", Files.exists(Paths.get(databaseFile)))

        val metadata = MetadataCollection()
        val networkService = NetworkService()

        DriverManager.getConnection("jdbc:sqlite:$databaseFile").use { connection ->
            assertThat("Database should have loaded", NetworkDatabaseReader(connection, metadata, networkService, databaseFile).load())
        }

        logger.info("Sleeping...")
        try {
            Thread.sleep(Long.MAX_VALUE)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    @Test
    internal fun testStupidlyLargeSchema() {
        // TODO - This needs to be replaced with a test for assigning to feeders and checking the below error. This should be
        //        done in a separate task to monitor code coverage drops.
        validateSchema(StupidlyLargeNetwork.create().networkService)

        assertThat(
            systemErr.log,
            containsString("External grid source 'primary source' [primary_source] has been assigned to the following feeders: normal [f1], current [f2]")
        )
    }

    @Test
    internal fun `test schema for each supported type`() {
        /************ IEC61968 ASSET INFO ************/
        validateSchema(SchemaServices.networkServicesOf(::CableInfo, CableInfo::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::NoLoadTest, NoLoadTest::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::OpenCircuitTest, OpenCircuitTest::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::OverheadWireInfo, OverheadWireInfo::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::PowerTransformerInfo, PowerTransformerInfo::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::ShortCircuitTest, ShortCircuitTest::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::ShuntCompensatorInfo, ShuntCompensatorInfo::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::SwitchInfo, SwitchInfo::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::TransformerEndInfo, TransformerEndInfo::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::TransformerTankInfo, TransformerTankInfo::fillFields))

        /************ IEC61968 ASSETS ************/
        validateSchema(SchemaServices.networkServicesOf(::AssetOwner, AssetOwner::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::Pole, Pole::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::Streetlight, Streetlight::fillFields))

        /************ IEC61968 infIEC61968 InfAssetInfo ************/
        validateSchema(SchemaServices.networkServicesOf(::RelayInfo, RelayInfo::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::CurrentTransformerInfo, CurrentTransformerInfo::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::PotentialTransformerInfo, PotentialTransformerInfo::fillFields))

        /************ IEC61968 METERING ************/
        validateSchema(SchemaServices.networkServicesOf(::Meter, Meter::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::UsagePoint, UsagePoint::fillFields))

        /************ IEC61968 COMMON ************/
        validateSchema(SchemaServices.networkServicesOf(::Location, Location::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::Organisation, Organisation::fillFieldsCommon))

        /************ IEC61968 OPERATIONS ************/
        validateSchema(SchemaServices.networkServicesOf(::OperationalRestriction, OperationalRestriction::fillFields))

        /************ IEC61970 BASE AUXILIARY EQUIPMENT ************/
        validateSchema(SchemaServices.networkServicesOf(::CurrentTransformer, CurrentTransformer::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::FaultIndicator, FaultIndicator::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::PotentialTransformer, PotentialTransformer::fillFields))

        /************ IEC61970 BASE CORE ************/
        validateSchema(SchemaServices.networkServicesOf(::BaseVoltage, BaseVoltage::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::ConnectivityNode, ConnectivityNode::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::Feeder, Feeder::fillFields).also { Tracing.setDirection().run(it) })
        validateSchema(SchemaServices.networkServicesOf(::GeographicalRegion, GeographicalRegion::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::Site, Site::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::SubGeographicalRegion, SubGeographicalRegion::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::Substation, Substation::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::Terminal, Terminal::fillFields))

        /************ IEC61970 BASE EQUIVALENTS ************/
        validateSchema(SchemaServices.networkServicesOf(::EquivalentBranch, EquivalentBranch::fillFields))

        /************ IEC61970 BASE MEAS ************/
        validateSchema(SchemaServices.networkServicesOf(::Accumulator, Accumulator::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::Analog, Analog::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::Control, Control::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::Discrete, Discrete::fillFields))

        /************ IEC61970 Base Protection ************/
        validateSchema(SchemaServices.networkServicesOf(::CurrentRelay, CurrentRelay::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::DistanceRelay, DistanceRelay::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::ProtectionRelayScheme, ProtectionRelayScheme::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::ProtectionRelaySystem, ProtectionRelaySystem::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::VoltageRelay, VoltageRelay::fillFields))

        /************ IEC61970 BASE SCADA ************/
        validateSchema(SchemaServices.networkServicesOf(::RemoteControl, RemoteControl::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::RemoteSource, RemoteSource::fillFields))

        /************ IEC61970 BASE WIRES GENERATION PRODUCTION ************/
        validateSchema(SchemaServices.networkServicesOf(::BatteryUnit, BatteryUnit::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::PhotoVoltaicUnit, PhotoVoltaicUnit::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::PowerElectronicsConnection, PowerElectronicsConnection::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::PowerElectronicsConnectionPhase, PowerElectronicsConnectionPhase::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::PowerElectronicsWindUnit, PowerElectronicsWindUnit::fillFields))

        /************ IEC61970 BASE WIRES ************/
        validateSchema(SchemaServices.networkServicesOf(::AcLineSegment, AcLineSegment::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::Breaker, Breaker::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::BusbarSection, BusbarSection::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::Disconnector, Disconnector::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::EnergyConsumer, EnergyConsumer::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::EnergyConsumerPhase, EnergyConsumerPhase::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::EnergySource, EnergySource::fillFields).also { Tracing.setPhases().run(it) })
        validateSchema(SchemaServices.networkServicesOf(::EnergySourcePhase, EnergySourcePhase::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::Fuse, Fuse::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::Ground, Ground::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::GroundDisconnector, GroundDisconnector::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::GroundingImpedance, GroundingImpedance::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::Jumper, Jumper::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::Junction, Junction::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::LinearShuntCompensator, LinearShuntCompensator::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::LoadBreakSwitch, LoadBreakSwitch::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::PerLengthSequenceImpedance, PerLengthSequenceImpedance::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::PetersenCoil, PetersenCoil::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::PowerTransformer, PowerTransformer::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::PowerTransformerEnd, PowerTransformerEnd::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::RatioTapChanger, RatioTapChanger::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::ReactiveCapabilityCurve, ReactiveCapabilityCurve::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::Recloser, Recloser::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::SeriesCompensator, SeriesCompensator::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::SynchronousMachine, SynchronousMachine::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::TapChangerControl, TapChangerControl::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::TransformerStarImpedance, TransformerStarImpedance::fillFields))

        /************ IEC61970 InfIEC61970 BASE WIRES GENERATION PRODUCTION ************/
        validateSchema(SchemaServices.networkServicesOf(::EvChargingUnit, EvChargingUnit::fillFields))

        /************ IEC61970 InfIEC61970 Feeder ************/
        validateSchema(SchemaServices.networkServicesOf(::Circuit, Circuit::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::Loop, Loop::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::LvFeeder, LvFeeder::fillFields))
    }

    @Test
    internal fun `test Name and NameType schema`() {
        validateSchema(SchemaServices.createNameTestService<NetworkService, Junction>())
    }

    @Test
    internal fun `post process fails with unresolved references`() {
        validateUnresolvedFailure("PowerElectronicsConnection pec1", "RegulatingControl tcc") {
            resolveOrDeferReference(Resolvers.regulatingControl(PowerElectronicsConnection("pec1")), "tcc")
        }
    }

    @Test
    internal fun `only loads street address fields if required`() {
        // This test is here to make sure the database reading correctly removes the parts of loaded street addresses that are not filled out.
        val writeService = NetworkService().apply {
            add(Location(mRID = "loc1").apply { mainAddress = StreetAddress(townDetail = TownDetail(), streetDetail = StreetDetail()) })
        }

        validateWriteRead(writeService) { readService, _ ->
            assertThat(
                "Expected a default street address as blank parts should have been removed during teh database read",
                readService.get<Location>("loc1")!!.mainAddress,
                equalTo(StreetAddress())
            )
        }
    }

}

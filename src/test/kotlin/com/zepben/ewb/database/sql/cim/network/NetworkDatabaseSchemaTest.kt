/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sql.cim.network

import com.zepben.ewb.cim.extensions.iec61968.assetinfo.RelayInfo
import com.zepben.ewb.cim.extensions.iec61968.metering.PanDemandResponseFunction
import com.zepben.ewb.cim.extensions.iec61970.base.core.HvCustomer
import com.zepben.ewb.cim.extensions.iec61970.base.core.Site
import com.zepben.ewb.cim.extensions.iec61970.base.feeder.Loop
import com.zepben.ewb.cim.extensions.iec61970.base.feeder.LvFeeder
import com.zepben.ewb.cim.extensions.iec61970.base.feeder.LvSubstation
import com.zepben.ewb.cim.extensions.iec61970.base.generation.production.EvChargingUnit
import com.zepben.ewb.cim.extensions.iec61970.base.protection.*
import com.zepben.ewb.cim.extensions.iec61970.base.wires.BatteryControl
import com.zepben.ewb.cim.iec61968.assetinfo.*
import com.zepben.ewb.cim.iec61968.assets.AssetOwner
import com.zepben.ewb.cim.iec61968.assets.Streetlight
import com.zepben.ewb.cim.iec61968.common.*
import com.zepben.ewb.cim.iec61968.infiec61968.infassetinfo.CurrentTransformerInfo
import com.zepben.ewb.cim.iec61968.infiec61968.infassetinfo.PotentialTransformerInfo
import com.zepben.ewb.cim.iec61968.infiec61968.infassets.Pole
import com.zepben.ewb.cim.iec61968.metering.Meter
import com.zepben.ewb.cim.iec61968.metering.UsagePoint
import com.zepben.ewb.cim.iec61968.operations.OperationalRestriction
import com.zepben.ewb.cim.iec61970.base.auxiliaryequipment.CurrentTransformer
import com.zepben.ewb.cim.iec61970.base.auxiliaryequipment.FaultIndicator
import com.zepben.ewb.cim.iec61970.base.auxiliaryequipment.PotentialTransformer
import com.zepben.ewb.cim.iec61970.base.core.*
import com.zepben.ewb.cim.iec61970.base.equivalents.EquivalentBranch
import com.zepben.ewb.cim.iec61970.base.generation.production.BatteryUnit
import com.zepben.ewb.cim.iec61970.base.generation.production.PhotoVoltaicUnit
import com.zepben.ewb.cim.iec61970.base.generation.production.PowerElectronicsWindUnit
import com.zepben.ewb.cim.iec61970.base.meas.Accumulator
import com.zepben.ewb.cim.iec61970.base.meas.Analog
import com.zepben.ewb.cim.iec61970.base.meas.Control
import com.zepben.ewb.cim.iec61970.base.meas.Discrete
import com.zepben.ewb.cim.iec61970.base.protection.CurrentRelay
import com.zepben.ewb.cim.iec61970.base.scada.RemoteControl
import com.zepben.ewb.cim.iec61970.base.scada.RemoteSource
import com.zepben.ewb.cim.iec61970.base.wires.*
import com.zepben.ewb.cim.iec61970.infiec61970.feeder.Circuit
import com.zepben.ewb.database.sql.cim.CimDatabaseSchemaTest
import com.zepben.ewb.services.common.BaseService
import com.zepben.ewb.services.common.Resolvers
import com.zepben.ewb.services.common.extensions.typeNameAndMRID
import com.zepben.ewb.services.common.testdata.SchemaServices
import com.zepben.ewb.services.common.testdata.fillFieldsCommon
import com.zepben.ewb.services.common.testdata.generateId
import com.zepben.ewb.services.network.NetworkService
import com.zepben.ewb.services.network.NetworkServiceComparator
import com.zepben.ewb.services.network.testdata.*
import com.zepben.ewb.services.network.testdata.stupid.StupidlyLargeNetwork
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

class NetworkDatabaseSchemaTest : CimDatabaseSchemaTest<
    NetworkService,
    NetworkDatabaseWriter,
    NetworkDatabaseReader,
    NetworkServiceComparator,
    IdentifiedObject
>(
    describeObject = IdentifiedObject::typeNameAndMRID,
    addToService = BaseService::tryAdd
){

    override fun createService(): NetworkService = NetworkService()

    override fun createWriter(filename: String): NetworkDatabaseWriter =
        NetworkDatabaseWriter(filename)

    override fun createReader(connection: Connection, databaseDescription: String): NetworkDatabaseReader =
        NetworkDatabaseReader(connection, databaseDescription)

    override fun createComparator(): NetworkServiceComparator = NetworkServiceComparator()

    override fun createIdentifiedObject(): IdentifiedObject = Junction(generateId())

    @Test
    @Disabled
    fun readRealFile() {
        systemErr.unmute()

        // Put the name of the database you want to read in src/test/resources/test-network-database.txt
        val databaseFile = Files.readString(Path.of("src", "test", "resources", "test-network-database.txt")).trim().trim('"')

        assertThat("database must exist", Files.exists(Paths.get(databaseFile)))

        val networkService = NetworkService()

        DriverManager.getConnection("jdbc:sqlite:$databaseFile").use { connection ->
            assertThat("Database should have read", NetworkDatabaseReader(connection, databaseFile).read(networkService))
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
        // ##################################
        // # Extensions IEC61968 Asset Info #
        // ##################################

        validateSchema(SchemaServices.networkServicesOf(::RelayInfo, RelayInfo::fillFields))

        // ################################
        // # Extensions IEC61968 Metering #
        // ################################

        validateSchema(SchemaServices.networkServicesOf(::PanDemandResponseFunction, PanDemandResponseFunction::fillFields))

        // #################################
        // # Extensions IEC61970 Base Core #
        // #################################

        validateSchema(SchemaServices.networkServicesOf(::HvCustomer, HvCustomer::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::Site, Site::fillFields))

        // ###################################
        // # Extensions IEC61970 Base Feeder #
        // ###################################

        validateSchema(SchemaServices.networkServicesOf(::Loop, Loop::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::LvFeeder, LvFeeder::fillFields).also { it.assignEquipmentToLvFeeders() })
        validateSchema(SchemaServices.networkServicesOf(::LvSubstation, LvSubstation::fillFields).also { it.assignEquipmentToFeeders() })

        // ##################################################
        // # Extensions IEC61970 Base Generation Production #
        // ##################################################

        validateSchema(SchemaServices.networkServicesOf(::EvChargingUnit, EvChargingUnit::fillFields))

        // #######################################
        // # Extensions IEC61970 Base Protection #
        // #######################################

        validateSchema(SchemaServices.networkServicesOf(::DirectionalCurrentRelay, DirectionalCurrentRelay::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::DistanceRelay, DistanceRelay::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::ProtectionRelayScheme, ProtectionRelayScheme::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::ProtectionRelaySystem, ProtectionRelaySystem::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::VoltageRelay, VoltageRelay::fillFields))

        // ##################################
        // # Extensions IEC61970 Base Wires #
        // ##################################

        validateSchema(SchemaServices.networkServicesOf(::BatteryControl, BatteryControl::fillFields))

        // #######################
        // # IEC61968 Asset Info #
        // #######################

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

        // ###################
        // # IEC61968 Assets #
        // ###################

        validateSchema(SchemaServices.networkServicesOf(::AssetOwner, AssetOwner::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::Streetlight, Streetlight::fillFields))

        // ###################
        // # IEC61968 Common #
        // ###################

        validateSchema(SchemaServices.networkServicesOf(::Location, Location::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::Organisation, Organisation::fillFieldsCommon))

        // #####################################
        // # IEC61968 infIEC61968 InfAssetInfo #
        // #####################################

        validateSchema(SchemaServices.networkServicesOf(::CurrentTransformerInfo, CurrentTransformerInfo::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::PotentialTransformerInfo, PotentialTransformerInfo::fillFields))

        // ##################################
        // # IEC61968 infIEC61968 InfAssets #
        // ##################################

        validateSchema(SchemaServices.networkServicesOf(::Pole, Pole::fillFields))

        // #####################
        // # IEC61968 Metering #
        // #####################

        validateSchema(SchemaServices.networkServicesOf(::Meter, Meter::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::UsagePoint, UsagePoint::fillFields))

        // #######################
        // # IEC61968 Operations #
        // #######################

        validateSchema(SchemaServices.networkServicesOf(::OperationalRestriction, OperationalRestriction::fillFields))

        // #####################################
        // # IEC61970 Base Auxiliary Equipment #
        // #####################################

        validateSchema(SchemaServices.networkServicesOf(::CurrentTransformer, CurrentTransformer::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::FaultIndicator, FaultIndicator::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::PotentialTransformer, PotentialTransformer::fillFields))

        // ######################
        // # IEC61970 Base Core #
        // ######################

        validateSchema(SchemaServices.networkServicesOf(::BaseVoltage, BaseVoltage::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::ConnectivityNode, ConnectivityNode::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::Feeder, Feeder::fillFields).also {
            it.assignEquipmentToFeeders()
            it.setFeederDirections()
        })
        validateSchema(SchemaServices.networkServicesOf(::GeographicalRegion, GeographicalRegion::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::SubGeographicalRegion, SubGeographicalRegion::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::Substation, Substation::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::Terminal, Terminal::fillFields))

        // #############################
        // # IEC61970 Base Equivalents #
        // #############################

        validateSchema(SchemaServices.networkServicesOf(::EquivalentBranch, EquivalentBranch::fillFields))

        // #######################################
        // # IEC61970 Base Generation Production #
        // #######################################

        validateSchema(SchemaServices.networkServicesOf(::BatteryUnit, BatteryUnit::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::PhotoVoltaicUnit, PhotoVoltaicUnit::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::PowerElectronicsWindUnit, PowerElectronicsWindUnit::fillFields))

        // ######################
        // # IEC61970 Base Meas #
        // ######################

        validateSchema(SchemaServices.networkServicesOf(::Accumulator, Accumulator::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::Analog, Analog::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::Control, Control::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::Discrete, Discrete::fillFields))

        // ############################
        // # IEC61970 Base Protection #
        // ############################

        validateSchema(SchemaServices.networkServicesOf(::CurrentRelay, CurrentRelay::fillFields))

        // #######################
        // # IEC61970 Base Scada #
        // #######################

        validateSchema(SchemaServices.networkServicesOf(::RemoteControl, RemoteControl::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::RemoteSource, RemoteSource::fillFields))

        // #######################
        // # IEC61970 Base Wires #
        // #######################

        validateSchema(SchemaServices.networkServicesOf(::AcLineSegment, AcLineSegment::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::AcLineSegmentPhase, AcLineSegmentPhase::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::Breaker, Breaker::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::BusbarSection, BusbarSection::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::Clamp, Clamp::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::Cut, Cut::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::Disconnector, Disconnector::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::EnergyConsumer, EnergyConsumer::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::EnergyConsumerPhase, EnergyConsumerPhase::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::EnergySource, EnergySource::fillFields).also { it.setPhases() })
        validateSchema(SchemaServices.networkServicesOf(::EnergySourcePhase, EnergySourcePhase::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::Fuse, Fuse::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::Ground, Ground::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::GroundDisconnector, GroundDisconnector::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::GroundingImpedance, GroundingImpedance::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::Jumper, Jumper::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::Junction, Junction::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::LinearShuntCompensator, LinearShuntCompensator::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::LoadBreakSwitch, LoadBreakSwitch::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::PerLengthPhaseImpedance, PerLengthPhaseImpedance::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::PerLengthSequenceImpedance, PerLengthSequenceImpedance::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::PetersenCoil, PetersenCoil::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::PowerElectronicsConnection, PowerElectronicsConnection::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::PowerElectronicsConnectionPhase, PowerElectronicsConnectionPhase::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::PowerTransformer, PowerTransformer::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::PowerTransformerEnd, PowerTransformerEnd::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::RatioTapChanger, RatioTapChanger::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::ReactiveCapabilityCurve, ReactiveCapabilityCurve::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::Recloser, Recloser::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::SeriesCompensator, SeriesCompensator::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::StaticVarCompensator, StaticVarCompensator::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::SynchronousMachine, SynchronousMachine::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::TapChangerControl, TapChangerControl::fillFields))
        validateSchema(SchemaServices.networkServicesOf(::TransformerStarImpedance, TransformerStarImpedance::fillFields))

        // ###############################
        // # IEC61970 InfIEC61970 Feeder #
        // ###############################

        validateSchema(SchemaServices.networkServicesOf(::Circuit, Circuit::fillFields))
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
    internal fun `only reads street address fields if required`() {
        // This test is here to make sure the database reading correctly removes the parts of street addresses that are not filled out.
        val writeService = NetworkService().apply {
            add(Location(mRID = "loc1").apply { mainAddress = StreetAddress(townDetail = TownDetail(), streetDetail = StreetDetail()) })
        }

        validateWriteRead(writeService) { readService ->
            assertThat(
                "Expected a default street address as blank parts should have been removed during the database read",
                readService.get<Location>("loc1")!!.mainAddress,
                equalTo(StreetAddress())
            )
        }
    }

    @Test
    internal fun `reads street address with empty string fields`() {
        // This test is here to make sure the database reading correctly removes the parts of street addresses that are not filled out.
        val emptys = StreetAddress(postalCode = "", poBox = "", townDetail = TownDetail("", ""), streetDetail = StreetDetail("", "", "", "", "", "", ""))
        val writeService = NetworkService().apply {
            add(
                Location(mRID = "loc1").apply {
                    mainAddress = emptys
                }
            )
        }

        validateWriteRead(writeService) { readService ->
            assertThat(
                "Expected a street address with all empty strings for every property",
                readService.get<Location>("loc1")!!.mainAddress,
                equalTo(emptys)
            )
        }
    }

}

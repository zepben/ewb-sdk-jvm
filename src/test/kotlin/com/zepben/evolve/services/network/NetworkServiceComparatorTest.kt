/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network

import com.zepben.evolve.cim.iec61968.assetinfo.*
import com.zepben.evolve.cim.iec61968.assets.*
import com.zepben.evolve.cim.iec61968.common.Location
import com.zepben.evolve.cim.iec61968.common.PositionPoint
import com.zepben.evolve.cim.iec61968.common.StreetAddress
import com.zepben.evolve.cim.iec61968.common.TownDetail
import com.zepben.evolve.cim.iec61968.infiec61968.infassetinfo.*
import com.zepben.evolve.cim.iec61968.infiec61968.infcommon.Ratio
import com.zepben.evolve.cim.iec61968.metering.EndDevice
import com.zepben.evolve.cim.iec61968.metering.Meter
import com.zepben.evolve.cim.iec61968.metering.UsagePoint
import com.zepben.evolve.cim.iec61968.operations.OperationalRestriction
import com.zepben.evolve.cim.iec61970.base.auxiliaryequipment.*
import com.zepben.evolve.cim.iec61970.base.core.*
import com.zepben.evolve.cim.iec61970.base.domain.UnitSymbol
import com.zepben.evolve.cim.iec61970.base.equivalents.EquivalentBranch
import com.zepben.evolve.cim.iec61970.base.equivalents.EquivalentEquipment
import com.zepben.evolve.cim.iec61970.base.meas.*
import com.zepben.evolve.cim.iec61970.base.protection.*
import com.zepben.evolve.cim.iec61970.base.scada.RemoteControl
import com.zepben.evolve.cim.iec61970.base.scada.RemotePoint
import com.zepben.evolve.cim.iec61970.base.scada.RemoteSource
import com.zepben.evolve.cim.iec61970.base.wires.*
import com.zepben.evolve.cim.iec61970.base.wires.generation.production.*
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.Circuit
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.Loop
import com.zepben.evolve.cim.iec61970.infiec61970.wires.generation.production.EvChargingUnit
import com.zepben.evolve.services.common.*
import com.zepben.evolve.services.network.tracing.feeder.FeederDirection
import com.zepben.evolve.utils.ServiceComparatorValidator
import org.junit.jupiter.api.Test
import java.time.Instant

@Suppress("SameParameterValue")
internal class NetworkServiceComparatorTest : BaseServiceComparatorTest() {

    override val comparatorValidator: ServiceComparatorValidator<NetworkService, NetworkServiceComparator> = ServiceComparatorValidator(
        { NetworkService() },
        { options -> NetworkServiceComparator(options) }
    )

    /************ IEC61968 ASSET INFO ************/

    @Test
    internal fun compareCableInfo() {
        compareWireInfo { CableInfo(mRID = it) }
    }

    @Test
    internal fun compareNoLoadTest() {
        compareTransformerTest { NoLoadTest(it) }

        comparatorValidator.validateProperty(NoLoadTest::energisedEndVoltage, { NoLoadTest(it) }, { 1 }, { 2 })
        comparatorValidator.validateProperty(NoLoadTest::excitingCurrent, { NoLoadTest(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(NoLoadTest::excitingCurrentZero, { NoLoadTest(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(NoLoadTest::loss, { NoLoadTest(it) }, { 1 }, { 2 })
        comparatorValidator.validateProperty(NoLoadTest::lossZero, { NoLoadTest(it) }, { 1 }, { 2 })
    }

    @Test
    internal fun compareOpenCircuitTest() {
        compareTransformerTest { OpenCircuitTest(it) }

        comparatorValidator.validateProperty(OpenCircuitTest::energisedEndStep, { OpenCircuitTest(it) }, { 1 }, { 2 })
        comparatorValidator.validateProperty(OpenCircuitTest::energisedEndVoltage, { OpenCircuitTest(it) }, { 1 }, { 2 })
        comparatorValidator.validateProperty(OpenCircuitTest::openEndStep, { OpenCircuitTest(it) }, { 1 }, { 2 })
        comparatorValidator.validateProperty(OpenCircuitTest::openEndVoltage, { OpenCircuitTest(it) }, { 1 }, { 2 })
        comparatorValidator.validateProperty(OpenCircuitTest::phaseShift, { OpenCircuitTest(it) }, { 1.0 }, { 2.0 })
    }

    @Test
    internal fun compareOverheadWireInfo() {
        compareWireInfo { OverheadWireInfo(mRID = it) }
    }

    @Test
    internal fun comparePowerTransformerInfo() {
        compareAssetInfo { PowerTransformerInfo(mRID = it) }

        comparatorValidator.validateCollection(
            PowerTransformerInfo::transformerTankInfos,
            PowerTransformerInfo::addTransformerTankInfo,
            { PowerTransformerInfo(it) },
            { TransformerTankInfo("tti1") },
            { TransformerTankInfo("tti2") })
    }

    @Test
    internal fun compareShortCircuitTest() {
        compareTransformerTest { ShortCircuitTest(it) }

        comparatorValidator.validateProperty(ShortCircuitTest::current, { ShortCircuitTest(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(ShortCircuitTest::energisedEndStep, { ShortCircuitTest(it) }, { 1 }, { 2 })
        comparatorValidator.validateProperty(ShortCircuitTest::groundedEndStep, { ShortCircuitTest(it) }, { 1 }, { 2 })
        comparatorValidator.validateProperty(ShortCircuitTest::leakageImpedance, { ShortCircuitTest(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(ShortCircuitTest::leakageImpedanceZero, { ShortCircuitTest(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(ShortCircuitTest::loss, { ShortCircuitTest(it) }, { 1 }, { 2 })
        comparatorValidator.validateProperty(ShortCircuitTest::lossZero, { ShortCircuitTest(it) }, { 1 }, { 2 })
        comparatorValidator.validateProperty(ShortCircuitTest::power, { ShortCircuitTest(it) }, { 1 }, { 2 })
        comparatorValidator.validateProperty(ShortCircuitTest::voltage, { ShortCircuitTest(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(ShortCircuitTest::voltageOhmicPart, { ShortCircuitTest(it) }, { 1.0 }, { 2.0 })
    }

    @Test
    internal fun compareTransformerEndInfo() {
        compareAssetInfo { TransformerEndInfo(it) }

        comparatorValidator.validateProperty(TransformerEndInfo::connectionKind, { TransformerEndInfo(it) }, { WindingConnection.D }, { WindingConnection.Y })
        comparatorValidator.validateProperty(TransformerEndInfo::emergencyS, { TransformerEndInfo(it) }, { 1 }, { 2 })
        comparatorValidator.validateProperty(TransformerEndInfo::endNumber, { TransformerEndInfo(it) }, { 1 }, { 2 })
        comparatorValidator.validateProperty(TransformerEndInfo::insulationU, { TransformerEndInfo(it) }, { 1 }, { 2 })
        comparatorValidator.validateProperty(TransformerEndInfo::phaseAngleClock, { TransformerEndInfo(it) }, { 1 }, { 2 })
        comparatorValidator.validateProperty(TransformerEndInfo::r, { TransformerEndInfo(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(TransformerEndInfo::ratedS, { TransformerEndInfo(it) }, { 1 }, { 2 })
        comparatorValidator.validateProperty(TransformerEndInfo::ratedU, { TransformerEndInfo(it) }, { 1 }, { 2 })
        comparatorValidator.validateProperty(TransformerEndInfo::shortTermS, { TransformerEndInfo(it) }, { 1 }, { 2 })

        comparatorValidator.validateProperty(
            TransformerEndInfo::transformerStarImpedance,
            { TransformerEndInfo(it) },
            { TransformerStarImpedance("tsi1") },
            { TransformerStarImpedance("tsi2") }
        )
    }

    @Test
    internal fun compareTransformerTankInfo() {
        compareAssetInfo { TransformerTankInfo(it) }

        comparatorValidator.validateCollection(
            TransformerTankInfo::transformerEndInfos,
            TransformerTankInfo::addTransformerEndInfo,
            { TransformerTankInfo(it) },
            { TransformerEndInfo("tei1") },
            { TransformerEndInfo("tei2") })
    }

    private fun compareTransformerTest(createTransformerTest: (String) -> TransformerTest) {
        compareIdentifiedObject(createTransformerTest)

        comparatorValidator.validateProperty(TransformerTest::basePower, createTransformerTest, { 1 }, { 2 })
        comparatorValidator.validateProperty(TransformerTest::temperature, createTransformerTest, { 1.0 }, { 2.0 })
    }

    private fun compareWireInfo(createWireInfo: (String) -> WireInfo) {
        compareAssetInfo(createWireInfo)

        comparatorValidator.validateProperty(WireInfo::ratedCurrent, createWireInfo, { 1 }, { 2 })
        comparatorValidator.validateProperty(WireInfo::material, createWireInfo, { WireMaterialKind.aluminum }, { WireMaterialKind.copperCadmium })
    }

    /************ IEC61968 ASSETS ************/

    private fun compareAsset(createAsset: (String) -> Asset) {
        compareIdentifiedObject(createAsset)

        comparatorValidator.validateCollection(Asset::organisationRoles, Asset::addOrganisationRole, createAsset, { AssetOwner("a1") }, { AssetOwner("a2") })
        comparatorValidator.validateProperty(Asset::location, createAsset, { Location("l1") }, { Location("l2") })
    }

    private fun compareAssetContainer(createAssetContainer: (String) -> AssetContainer) {
        compareAsset(createAssetContainer)
    }

    private fun compareAssetInfo(createAssetInfo: (String) -> AssetInfo) {
        compareIdentifiedObject(createAssetInfo)
    }

    private fun compareAssetOrganisationRole(createAssetOrganisationRole: (String) -> AssetOrganisationRole) {
        compareOrganisationRole(createAssetOrganisationRole)
    }

    @Test
    internal fun compareAssetOwner() {
        compareAssetOrganisationRole { AssetOwner(mRID = it) }
    }

    @Test
    internal fun comparePole() {
        compareStructure { Pole(mRID = it) }
        comparatorValidator.validateProperty(Pole::classification, { Pole(it) }, { "c1" }, { "c2" })
        comparatorValidator.validateCollection(Pole::streetlights, Pole::addStreetlight, { Pole(it) }, { Streetlight("sl1") }, { Streetlight("sl2") })
    }

    @Test
    internal fun compareStreetlight() {
        compareAsset { Streetlight(mRID = it) }

        comparatorValidator.validateProperty(
            Streetlight::lampKind,
            { Streetlight(it) },
            { StreetlightLampKind.HIGH_PRESSURE_SODIUM },
            { StreetlightLampKind.MERCURY_VAPOR })
        comparatorValidator.validateProperty(Streetlight::lightRating, { Streetlight(it) }, { 1 }, { 2 })
        comparatorValidator.validateProperty(Streetlight::pole, { Streetlight(it) }, { Pole("x") }, { Pole("y") })
    }

    private fun compareStructure(createStructure: (String) -> Structure) {
        compareAssetContainer(createStructure)
    }

    /************ IEC61968 COMMON ************/

    @Test
    internal fun compareLocation() {
        compareIdentifiedObject { Location(it) }

        comparatorValidator.validateProperty(
            Location::mainAddress,
            { Location(it) },
            { StreetAddress(townDetail = TownDetail("town", "state")) },
            { StreetAddress(townDetail = TownDetail("other", "state")) })
        comparatorValidator.validateIndexedCollection(
            Location::points,
            Location::addPoint,
            { Location(it) },
            { PositionPoint(1.0, 2.0) },
            { PositionPoint(3.0, 4.0) })
    }

    /************ IEC61968 infIEC61968 InfAssetInfo ************/

    @Test
    internal fun compareRelayInfo() {
        compareAssetInfo { RelayInfo(it) }

        comparatorValidator.validateProperty(RelayInfo::curveSetting, { RelayInfo(it) }, { "first" }, { "second" })
        comparatorValidator.validateProperty(RelayInfo::recloseFast, { RelayInfo(it) }, { false }, { true })
        comparatorValidator.validateIndexedCollection(
            RelayInfo::recloseDelays,
            RelayInfo::addDelay,
            { RelayInfo(it) },
            { 1.0 },
            { 2.0 }
        )
    }

    @Test
    internal fun compareCurrentTransformerInfo() {
        compareAssetInfo { CurrentTransformerInfo(it) }

        comparatorValidator.validateProperty(CurrentTransformerInfo::accuracyClass, { CurrentTransformerInfo(it) }, { "first" }, { "second" })
        comparatorValidator.validateProperty(CurrentTransformerInfo::accuracyLimit, { CurrentTransformerInfo(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(CurrentTransformerInfo::coreCount, { CurrentTransformerInfo(it) }, { 1 }, { 2 })
        comparatorValidator.validateProperty(CurrentTransformerInfo::ctClass, { CurrentTransformerInfo(it) }, { "first" }, { "second" })
        comparatorValidator.validateProperty(CurrentTransformerInfo::kneePointVoltage, { CurrentTransformerInfo(it) }, { 1 }, { 2 })
        comparatorValidator.validateProperty(CurrentTransformerInfo::maxRatio, { CurrentTransformerInfo(it) }, { Ratio(1.0, 1.0) }, { Ratio(2.0, 2.0) })
        comparatorValidator.validateProperty(CurrentTransformerInfo::nominalRatio, { CurrentTransformerInfo(it) }, { Ratio(1.0, 1.0) }, { Ratio(2.0, 2.0) })
        comparatorValidator.validateProperty(CurrentTransformerInfo::primaryRatio, { CurrentTransformerInfo(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(CurrentTransformerInfo::ratedCurrent, { CurrentTransformerInfo(it) }, { 1 }, { 2 })
        comparatorValidator.validateProperty(CurrentTransformerInfo::secondaryFlsRating, { CurrentTransformerInfo(it) }, { 1 }, { 2 })
        comparatorValidator.validateProperty(CurrentTransformerInfo::secondaryRatio, { CurrentTransformerInfo(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(CurrentTransformerInfo::usage, { CurrentTransformerInfo(it) }, { "first" }, { "second" })
    }

    @Test
    internal fun comparePotentialTransformerInfo() {
        compareAssetInfo { PotentialTransformerInfo(it) }

        comparatorValidator.validateProperty(PotentialTransformerInfo::accuracyClass, { PotentialTransformerInfo(it) }, { "first" }, { "second" })
        comparatorValidator.validateProperty(PotentialTransformerInfo::nominalRatio, { PotentialTransformerInfo(it) }, { Ratio(1.0, 1.0) }, { Ratio(2.0, 2.0) })
        comparatorValidator.validateProperty(PotentialTransformerInfo::primaryRatio, { PotentialTransformerInfo(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(PotentialTransformerInfo::ptClass, { PotentialTransformerInfo(it) }, { "first" }, { "second" })
        comparatorValidator.validateProperty(PotentialTransformerInfo::ratedVoltage, { PotentialTransformerInfo(it) }, { 1 }, { 2 })
        comparatorValidator.validateProperty(PotentialTransformerInfo::secondaryRatio, { PotentialTransformerInfo(it) }, { 1.0 }, { 2.0 })
    }

    /************ IEC61968 METERING ************/

    private fun compareEndDevice(createEndDevice: (String) -> EndDevice) {
        compareAssetContainer(createEndDevice)

        comparatorValidator.validateProperty(EndDevice::customerMRID, createEndDevice, { "customer1" }, { "customer2" })
        comparatorValidator.validateProperty(EndDevice::serviceLocation, createEndDevice, { Location("l1") }, { Location("l2") })
        comparatorValidator.validateCollection(
            EndDevice::usagePoints,
            EndDevice::addUsagePoint,
            createEndDevice,
            { UsagePoint("up1") },
            { UsagePoint("up2") },
            NetworkServiceComparatorOptions.all().copy(compareLvSimplification = false),
            optionsStopCompare = true
        )
    }

    @Test
    internal fun compareMeter() {
        compareEndDevice { Meter(mRID = it) }
    }

    @Test
    internal fun compareUsagePoint() {
        compareIdentifiedObject { UsagePoint(it) }

        comparatorValidator.validateProperty(UsagePoint::usagePointLocation, { UsagePoint(it) }, { Location("l1") }, { Location("l2") })
        comparatorValidator.validateProperty(UsagePoint::isVirtual, { UsagePoint(it) }, { false }, { true })
        comparatorValidator.validateProperty(UsagePoint::connectionCategory, { UsagePoint(it) }, { "first" }, { "second" })
        comparatorValidator.validateProperty(UsagePoint::ratedPower, { UsagePoint(it) }, { 1 }, { 2 })
        comparatorValidator.validateProperty(UsagePoint::approvedInverterCapacity, { UsagePoint(it) }, { 1 }, { 2 })
        comparatorValidator.validateCollection(
            UsagePoint::endDevices,
            UsagePoint::addEndDevice,
            { UsagePoint(it) },
            { Meter("m1") },
            { Meter("m2") },
            NetworkServiceComparatorOptions.all().copy(compareLvSimplification = false),
            optionsStopCompare = true
        )

        comparatorValidator.validateCollection(
            UsagePoint::equipment,
            UsagePoint::addEquipment,
            { UsagePoint(it) },
            { Junction("j1") },
            { Junction("j2") },
            NetworkServiceComparatorOptions.all().copy(compareLvSimplification = false),
            optionsStopCompare = true
        )
    }

    /************ IEC61968 OPERATIONS ************/

    @Test
    internal fun compareOperationalRestriction() {
        compareDocument { OperationalRestriction(it) }

        comparatorValidator.validateCollection(
            OperationalRestriction::equipment,
            OperationalRestriction::addEquipment,
            { OperationalRestriction(it) },
            { Junction("j1") },
            { Junction("j2") })
    }

    /************ IEC61970 BASE AUXILIARY EQUIPMENT ************/

    private fun compareAuxiliaryEquipment(createAuxiliaryEquipment: (String) -> AuxiliaryEquipment) {
        compareEquipment(createAuxiliaryEquipment)

        comparatorValidator.validateProperty(
            AuxiliaryEquipment::terminal,
            createAuxiliaryEquipment,
            { Terminal("t1") },
            { Terminal("t2") },
            NetworkServiceComparatorOptions.all().copy(compareTerminals = false),
            optionsStopCompare = true
        )
    }

    @Test
    internal fun compareCurrentTransformer() {
        compareSensor { CurrentTransformer(it) }

        comparatorValidator.validateProperty(
            CurrentTransformer::assetInfo,
            { CurrentTransformer(it) },
            { CurrentTransformerInfo("cti1") },
            { CurrentTransformerInfo("cti2") }
        )
        comparatorValidator.validateProperty(CurrentTransformer::coreBurden, { CurrentTransformer(it) }, { 1 }, { 2 })
    }

    @Test
    internal fun compareFaultIndicator() {
        compareAuxiliaryEquipment { FaultIndicator(it) }
    }

    @Test
    internal fun comparePotentialTransformer() {
        compareSensor { PotentialTransformer(it) }

        comparatorValidator.validateProperty(
            PotentialTransformer::assetInfo,
            { PotentialTransformer(it) },
            { PotentialTransformerInfo("vti1") },
            { PotentialTransformerInfo("vti2") }
        )
        comparatorValidator.validateProperty(
            PotentialTransformer::type,
            { PotentialTransformer(it) },
            { PotentialTransformerKind.capacitiveCoupling },
            { PotentialTransformerKind.inductive }
        )
    }

    private fun compareSensor(createSensor: (String) -> Sensor) {
        compareAuxiliaryEquipment(createSensor)

        comparatorValidator.validateCollection(
            Sensor::relayFunctions, Sensor::addRelayFunction, createSensor, { CurrentRelay("cr1") }, { CurrentRelay("cr2") }
        )
    }

    /************ IEC61970 BASE CORE ************/

    private fun compareAcDcTerminal(createAcDcTerminal: (String) -> AcDcTerminal) {
        compareIdentifiedObject(createAcDcTerminal)
    }

    @Test
    internal fun compareBaseVoltage() {
        compareIdentifiedObject { BaseVoltage(it) }

        comparatorValidator.validateProperty(BaseVoltage::nominalVoltage, { BaseVoltage(it) }, { 1 }, { 2 })
    }

    private fun compareConductingEquipment(createConductingEquipment: (String) -> ConductingEquipment) {
        compareEquipment(createConductingEquipment)

        comparatorValidator.validateProperty(ConductingEquipment::baseVoltage, createConductingEquipment, { BaseVoltage("b1") }, { BaseVoltage("b2") })
        comparatorValidator.validateIndexedCollection(
            ConductingEquipment::terminals, ConductingEquipment::addTerminal,
            createConductingEquipment,
            { Terminal(mRID = "1").apply { conductingEquipment = it } },
            { Terminal(mRID = "2").apply { conductingEquipment = it } },
            Terminal::conductingEquipment.setter,
            NetworkServiceComparatorOptions.all().copy(compareTerminals = false), optionsStopCompare = true
        )
    }

    @Test
    internal fun compareConnectivityNode() {
        compareIdentifiedObject { ConnectivityNode(it) }

        comparatorValidator.validateCollection(
            ConnectivityNode::terminals,
            ConnectivityNode::addTerminal,
            { ConnectivityNode(it) },
            { Terminal("1") },
            { Terminal("2") })
    }

    private fun compareConnectivityNodeContainer(createConnectivityNodeContainer: (String) -> ConnectivityNodeContainer) {
        comparePowerSystemResource(createConnectivityNodeContainer)
    }

    private fun compareEquipment(createEquipment: (String) -> Equipment) {
        comparePowerSystemResource(createEquipment)

        comparatorValidator.validateProperty(Equipment::inService, createEquipment, { true }, { false })
        comparatorValidator.validateProperty(Equipment::normallyInService, createEquipment, { true }, { false })
        comparatorValidator.validateProperty(Equipment::commissionedDate, createEquipment, { Instant.MIN }, { Instant.MAX })
        comparatorValidator.validateCollection(Equipment::containers, Equipment::addContainer, createEquipment, { Site("s1") }, { Site("s2") })

        comparatorValidator.validateCollection(Equipment::usagePoints, Equipment::addUsagePoint, createEquipment, { UsagePoint("u1") }, { UsagePoint("u2") })

        comparatorValidator.validateCollection(
            Equipment::operationalRestrictions,
            Equipment::addOperationalRestriction,
            createEquipment,
            { OperationalRestriction("o1") },
            { OperationalRestriction("o2") })

        comparatorValidator.validateCollection(
            Equipment::currentContainers,
            Equipment::addCurrentContainer,
            createEquipment,
            { Feeder("f1") },
            { Feeder("f2") })
    }

    private fun compareEquipmentContainer(createEquipmentContainer: (String) -> EquipmentContainer) {
        compareConnectivityNodeContainer(createEquipmentContainer)

        comparatorValidator.validateCollection(
            EquipmentContainer::equipment,
            EquipmentContainer::addEquipment,
            createEquipmentContainer,
            { Junction("j1") },
            { Junction("j2") })
    }

    @Test
    internal fun compareFeeder() {
        compareEquipmentContainer { Feeder(it) }

        comparatorValidator.validateProperty(Feeder::normalHeadTerminal, { Feeder(it) }, { Terminal("t1") }, { Terminal("t2") })
        comparatorValidator.validateProperty(Feeder::normalEnergizingSubstation, { Feeder(it) }, { Substation("s1") }, { Substation("s2") })
        comparatorValidator.validateCollection(Feeder::currentEquipment, Feeder::addCurrentEquipment, { Feeder(it) }, { Junction("j1") }, { Junction("j2") })
    }

    @Test
    internal fun compareGeographicalRegion() {
        compareIdentifiedObject { GeographicalRegion(it) }

        comparatorValidator.validateCollection(
            GeographicalRegion::subGeographicalRegions,
            GeographicalRegion::addSubGeographicalRegion,
            { GeographicalRegion(it) },
            { SubGeographicalRegion("sg1") },
            { SubGeographicalRegion("sg2") })
    }

    private fun comparePowerSystemResource(createPowerSystemResource: (String) -> PowerSystemResource) {
        compareIdentifiedObject(createPowerSystemResource)

        comparatorValidator.validateProperty(PowerSystemResource::location, createPowerSystemResource, { Location("l1") }, { Location("l2") })
        comparatorValidator.validateProperty(PowerSystemResource::numControls, createPowerSystemResource, { 1 }, { 2 })
    }

    @Test
    internal fun compareSite() {
        compareEquipmentContainer { Site(it) }
    }

    @Test
    internal fun compareSubGeographicalRegion() {
        compareIdentifiedObject { SubGeographicalRegion(it) }

        comparatorValidator.validateProperty(
            SubGeographicalRegion::geographicalRegion,
            { SubGeographicalRegion(it) },
            { GeographicalRegion("g1") },
            { GeographicalRegion("g2") })

        comparatorValidator.validateCollection(
            SubGeographicalRegion::substations,
            SubGeographicalRegion::addSubstation,
            { SubGeographicalRegion(it) },
            { Substation("s1") },
            { Substation("s2") })
    }

    @Test
    internal fun compareSubstation() {
        compareEquipmentContainer { Substation(it) }

        comparatorValidator.validateProperty(
            Substation::subGeographicalRegion,
            { Substation(it) },
            { SubGeographicalRegion("sg1") },
            { SubGeographicalRegion("sg2") })

        comparatorValidator.validateCollection(Substation::feeders, Substation::addFeeder, { Substation(it) }, { Feeder("f1") }, { Feeder("f2") })
    }

    @Test
    internal fun compareTerminal() {
        compareAcDcTerminal { Terminal(it) }

        comparatorValidator.validateProperty(Terminal::phases, { Terminal(it) }, { PhaseCode.ABC }, { PhaseCode.ABCN })

        comparatorValidator.validateValProperty(
            Terminal::connectivityNode, { Terminal(it) },
            { terminal, _ -> terminal.connect(ConnectivityNode("c1")) }, { terminal, _ -> terminal.connect(ConnectivityNode("c2")) })

        comparatorValidator.validateProperty(Terminal::conductingEquipment, { Terminal(it) }, { Junction("j1") }, { Junction("j2") })
        comparatorValidator.validateProperty(Terminal::normalFeederDirection, { Terminal(it) }, { FeederDirection.UPSTREAM }, { FeederDirection.DOWNSTREAM })
        comparatorValidator.validateProperty(Terminal::currentFeederDirection, { Terminal(it) }, { FeederDirection.UPSTREAM }, { FeederDirection.DOWNSTREAM })

        sequenceOf(
            0x00000001 to 0x00000002,
            0x00000010 to 0x00000020,
            0x00000100 to 0x00000200,
            0x00001000 to 0x00002000,
            0x00010000 to 0x00020000,
            0x00100000 to 0x00200000,
            0x01000000 to 0x02000000,
            0x10000000 to 0x20000000,
        ).forEach { (first, second) ->
            comparatorValidator.validateValProperty(
                Terminal::tracedPhases,
                { Terminal(it) },
                { _, tracedPhases -> tracedPhases.phaseStatusInternal = first.toUInt() },
                { _, tracedPhases -> tracedPhases.phaseStatusInternal = second.toUInt() }
            )
        }

    }

    /************ IEC61970 BASE EQUIVALENTS ************/

    @Test
    internal fun compareEquivalentBranch() {
        compareEquivalentEquipment { EquivalentBranch(it) }

        comparatorValidator.validateProperty(EquivalentBranch::negativeR12, { EquivalentBranch(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(EquivalentBranch::negativeR21, { EquivalentBranch(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(EquivalentBranch::negativeX12, { EquivalentBranch(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(EquivalentBranch::negativeX21, { EquivalentBranch(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(EquivalentBranch::positiveR12, { EquivalentBranch(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(EquivalentBranch::positiveR21, { EquivalentBranch(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(EquivalentBranch::positiveX12, { EquivalentBranch(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(EquivalentBranch::positiveX21, { EquivalentBranch(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(EquivalentBranch::r, { EquivalentBranch(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(EquivalentBranch::r21, { EquivalentBranch(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(EquivalentBranch::x, { EquivalentBranch(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(EquivalentBranch::x21, { EquivalentBranch(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(EquivalentBranch::zeroR12, { EquivalentBranch(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(EquivalentBranch::zeroR21, { EquivalentBranch(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(EquivalentBranch::zeroX12, { EquivalentBranch(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(EquivalentBranch::zeroX21, { EquivalentBranch(it) }, { 1.0 }, { 2.0 })
    }

    private fun compareEquivalentEquipment(createEquivalentEquipment: (String) -> EquivalentEquipment) {
        compareConductingEquipment(createEquivalentEquipment)
    }

    /************ IEC61970 BASE MEAS ************/

    @Test
    internal fun compareAccumulator() {
        compareMeasurement { Accumulator(it) }
    }

    @Test
    internal fun compareAnalog() {
        compareMeasurement { Analog(it) }
        comparatorValidator.validateProperty(Analog::positiveFlowIn, { Analog(it) }, { true }, { false })
    }

    @Test
    internal fun compareDiscrete() {
        compareMeasurement { Discrete(it) }
    }

    private fun compareMeasurement(createMeasurement: (String) -> Measurement) {
        compareIdentifiedObject { createMeasurement(it) }

        comparatorValidator.validateProperty(Measurement::powerSystemResourceMRID, { createMeasurement(it) }, { "psr1" }, { "psr2" })
        comparatorValidator.validateProperty(Measurement::terminalMRID, { createMeasurement(it) }, { "terminal1" }, { "terminal2" })
        comparatorValidator.validateProperty(Measurement::remoteSource, { createMeasurement(it) }, { RemoteSource("rs1") }, { RemoteSource("rs2") })
        comparatorValidator.validateProperty(Measurement::phases, { createMeasurement(it) }, { PhaseCode.ABCN }, { PhaseCode.ABC })
        comparatorValidator.validateProperty(Measurement::unitSymbol, { createMeasurement(it) }, { UnitSymbol.HENRYS }, { UnitSymbol.HOURS })
    }

    /************ IEC61970 Base Protection ************/

    @Test
    internal fun compareCurrentRelay() {
        compareProtectionRelayFunction { CurrentRelay(it) }

        comparatorValidator.validateProperty(CurrentRelay::currentLimit1, { CurrentRelay(it) }, { 1.1 }, { 2.2 })
        comparatorValidator.validateProperty(CurrentRelay::inverseTimeFlag, { CurrentRelay(it) }, { false }, { true })
        comparatorValidator.validateProperty(CurrentRelay::timeDelay1, { CurrentRelay(it) }, { 1.1 }, { 2.2 })
    }

    @Test
    internal fun compareDistanceRelay() {
        compareProtectionRelayFunction { DistanceRelay(it) }

        comparatorValidator.validateProperty(DistanceRelay::backwardBlind, { DistanceRelay(it) }, { 1.1 }, { 2.2 })
        comparatorValidator.validateProperty(DistanceRelay::backwardReach, { DistanceRelay(it) }, { 1.1 }, { 2.2 })
        comparatorValidator.validateProperty(DistanceRelay::backwardReactance, { DistanceRelay(it) }, { 1.1 }, { 2.2 })
        comparatorValidator.validateProperty(DistanceRelay::forwardBlind, { DistanceRelay(it) }, { 1.1 }, { 2.2 })
        comparatorValidator.validateProperty(DistanceRelay::forwardReach, { DistanceRelay(it) }, { 1.1 }, { 2.2 })
        comparatorValidator.validateProperty(DistanceRelay::forwardReactance, { DistanceRelay(it) }, { 1.1 }, { 2.2 })
        comparatorValidator.validateProperty(DistanceRelay::operationPhaseAngle1, { DistanceRelay(it) }, { 1.1 }, { 2.2 })
        comparatorValidator.validateProperty(DistanceRelay::operationPhaseAngle2, { DistanceRelay(it) }, { 1.1 }, { 2.2 })
        comparatorValidator.validateProperty(DistanceRelay::operationPhaseAngle3, { DistanceRelay(it) }, { 1.1 }, { 2.2 })
    }

    private fun compareProtectionRelayFunction(createProtectionRelayFunction: (String) -> ProtectionRelayFunction) {
        comparePowerSystemResource(createProtectionRelayFunction)

        comparatorValidator.validateProperty(ProtectionRelayFunction::assetInfo, createProtectionRelayFunction, { RelayInfo("ri1") }, { RelayInfo("ri2") })
        comparatorValidator.validateProperty(ProtectionRelayFunction::model, createProtectionRelayFunction, { "model1" }, { "model2" })
        comparatorValidator.validateProperty(ProtectionRelayFunction::reclosing, createProtectionRelayFunction, { false }, { true })
        comparatorValidator.validateProperty(ProtectionRelayFunction::relayDelayTime, createProtectionRelayFunction, { 1.1 }, { 2.2 })
        comparatorValidator.validateProperty(
            ProtectionRelayFunction::protectionKind,
            createProtectionRelayFunction,
            { ProtectionKind.FREQ },
            { ProtectionKind.DISTANCE }
        )
        comparatorValidator.validateProperty(ProtectionRelayFunction::directable, createProtectionRelayFunction, { false }, { true })
        comparatorValidator.validateProperty(
            ProtectionRelayFunction::powerDirection,
            createProtectionRelayFunction,
            { PowerDirectionKind.FORWARD },
            { PowerDirectionKind.REVERSE }
        )
        comparatorValidator.validateIndexedCollection(
            ProtectionRelayFunction::timeLimits,
            ProtectionRelayFunction::addTimeLimit,
            createProtectionRelayFunction,
            { 1.1 },
            { 2.2 }
        )
        comparatorValidator.validateIndexedCollection(
            ProtectionRelayFunction::thresholds,
            ProtectionRelayFunction::addThreshold,
            createProtectionRelayFunction,
            { RelaySetting(UnitSymbol.V, 1.1) },
            { RelaySetting(UnitSymbol.V, 2.2) }
        )
        comparatorValidator.validateCollection(
            ProtectionRelayFunction::protectedSwitches,
            ProtectionRelayFunction::addProtectedSwitch,
            createProtectionRelayFunction,
            { Breaker("b1") },
            { Breaker("b2") }
        )
        comparatorValidator.validateCollection(
            ProtectionRelayFunction::sensors,
            ProtectionRelayFunction::addSensor,
            createProtectionRelayFunction,
            { CurrentTransformer("ct1") },
            { CurrentTransformer("ct2") }
        )
        comparatorValidator.validateCollection(
            ProtectionRelayFunction::schemes,
            ProtectionRelayFunction::addScheme,
            createProtectionRelayFunction,
            { ProtectionRelayScheme("prs1") },
            { ProtectionRelayScheme("prs2") }
        )
    }

    @Test
    internal fun compareProtectionRelayScheme() {
        compareIdentifiedObject { ProtectionRelayScheme(it) }

        comparatorValidator.validateProperty(
            ProtectionRelayScheme::system,
            { ProtectionRelayScheme(it) },
            { ProtectionRelaySystem("prsys1") },
            { ProtectionRelaySystem("prsys2") }
        )
        comparatorValidator.validateCollection(
            ProtectionRelayScheme::functions,
            ProtectionRelayScheme::addFunction,
            { ProtectionRelayScheme(it) },
            { CurrentRelay("cr1") },
            { CurrentRelay("cr2") }
        )
    }

    @Test
    internal fun compareProtectionRelaySystem() {
        compareEquipment { ProtectionRelaySystem(it) }

        comparatorValidator.validateProperty(
            ProtectionRelaySystem::protectionKind, { ProtectionRelaySystem(it) },
            { ProtectionKind.FREQ }, { ProtectionKind.DISTANCE }
        )
        comparatorValidator.validateCollection(
            ProtectionRelaySystem::schemes,
            ProtectionRelaySystem::addScheme,
            { ProtectionRelaySystem(it) },
            { ProtectionRelayScheme("prs1") },
            { ProtectionRelayScheme("prs2") }
        )
    }

    @Test
    internal fun compareVoltageRelay() {
        compareProtectionRelayFunction { VoltageRelay(it) }
    }

    /************ IEC61970 BASE SCADA ************/

    @Test
    internal fun compareRemoteControl() {
        compareRemotePoint { RemoteControl(it) }

        comparatorValidator.validateProperty(RemoteControl::control, { RemoteControl(it) }, { Control("c1") }, { Control("c2") })
    }

    private fun compareRemotePoint(createIdObj: (String) -> RemotePoint) {
        compareIdentifiedObject { createIdObj(it) }
    }

    @Test
    internal fun compareRemoteSource() {
        compareRemotePoint { RemoteSource(it) }

        comparatorValidator.validateProperty(
            RemoteSource::measurement,
            { RemoteSource(it) },
            { object : Measurement("m1") {} },
            { object : Measurement("m2") {} })
    }

    /************ IEC61970 BASE WIRES GENERATION PRODUCTION ************/

    @Test
    internal fun compareBatteryUnit() {
        comparePowerElectronicsUnit { BatteryUnit(it) }

        comparatorValidator.validateProperty(BatteryUnit::batteryState, { BatteryUnit(it) }, { BatteryStateKind.charging }, { BatteryStateKind.discharging })
        comparatorValidator.validateProperty(BatteryUnit::ratedE, { BatteryUnit(it) }, { 1L }, { 2L })
        comparatorValidator.validateProperty(BatteryUnit::storedE, { BatteryUnit(it) }, { 1L }, { 2L })
    }

    @Test
    internal fun comparePhotoVoltaicUnit() {
        comparePowerElectronicsUnit { PhotoVoltaicUnit(it) }
    }

    private fun comparePowerElectronicsUnit(createPowerElectronicsUnit: (String) -> PowerElectronicsUnit) {
        compareEquipment(createPowerElectronicsUnit)

        comparatorValidator.validateProperty(
            PowerElectronicsUnit::powerElectronicsConnection,
            createPowerElectronicsUnit,
            { PowerElectronicsConnection("pec1") },
            { PowerElectronicsConnection("pec2") })
        comparatorValidator.validateProperty(PowerElectronicsUnit::maxP, createPowerElectronicsUnit, { 1 }, { 2 })
        comparatorValidator.validateProperty(PowerElectronicsUnit::minP, createPowerElectronicsUnit, { 1 }, { 2 })
    }

    @Test
    internal fun comparePowerElectronicsWindUnit() {
        comparePowerElectronicsUnit { PowerElectronicsWindUnit(it) }
    }

    /************ IEC61970 BASE WIRES ************/

    @Test
    internal fun compareAcLineSegment() {
        compareConductor { AcLineSegment(it) }

        comparatorValidator.validateProperty(
            AcLineSegment::perLengthSequenceImpedance,
            { AcLineSegment(it) },
            { PerLengthSequenceImpedance("p1") },
            { PerLengthSequenceImpedance("p2") })
    }

    @Test
    internal fun compareBreaker() {
        compareProtectedSwitch { Breaker(it) }

        comparatorValidator.validateProperty(Breaker::inTransitTime, { Breaker(it) }, { 1.1 }, { 2.2 })
    }

    @Test
    internal fun compareBusbarSection() {
        compareConnector { BusbarSection(it) }
    }

    private fun compareConductor(createConductor: (String) -> Conductor) {
        compareConductingEquipment(createConductor)

        comparatorValidator.validateProperty(Conductor::length, createConductor, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(Conductor::assetInfo, createConductor, { CableInfo("c1") }, { CableInfo("c2") })
    }

    private fun compareConnector(createConnector: (String) -> Connector) {
        compareConductingEquipment(createConnector)
    }

    @Test
    internal fun compareDisconnector() {
        compareSwitch { Disconnector(it) }
    }

    private fun compareEnergyConnection(createEnergyConnection: (String) -> EnergyConnection) {
        compareConductingEquipment(createEnergyConnection)
    }

    @Test
    internal fun compareEnergyConsumer() {
        compareEnergyConnection { EnergyConsumer(it) }

        comparatorValidator.validateProperty(EnergyConsumer::customerCount, { EnergyConsumer(it) }, { 1 }, { 2 })
        comparatorValidator.validateProperty(EnergyConsumer::grounded, { EnergyConsumer(it) }, { true }, { false })
        comparatorValidator.validateProperty(EnergyConsumer::p, { EnergyConsumer(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(EnergyConsumer::pFixed, { EnergyConsumer(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(
            EnergyConsumer::phaseConnection,
            { EnergyConsumer(it) },
            { PhaseShuntConnectionKind.I },
            { PhaseShuntConnectionKind.D })
        comparatorValidator.validateProperty(EnergyConsumer::q, { EnergyConsumer(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(EnergyConsumer::qFixed, { EnergyConsumer(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateCollection(
            EnergyConsumer::phases,
            EnergyConsumer::addPhase,
            { EnergyConsumer(it) },
            { EnergyConsumerPhase("ecp1") },
            { EnergyConsumerPhase("ecp2") })
    }

    @Test
    internal fun compareEnergyConsumerPhase() {
        comparePowerSystemResource { EnergyConsumerPhase(it) }

        comparatorValidator.validateProperty(
            EnergyConsumerPhase::energyConsumer,
            { EnergyConsumerPhase(it) },
            { EnergyConsumer("ec1") },
            { EnergyConsumer("ec2") })

        comparatorValidator.validateProperty(EnergyConsumerPhase::phase, { EnergyConsumerPhase(it) }, { SinglePhaseKind.A }, { SinglePhaseKind.B })
        comparatorValidator.validateProperty(EnergyConsumerPhase::p, { EnergyConsumerPhase(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(EnergyConsumerPhase::pFixed, { EnergyConsumerPhase(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(EnergyConsumerPhase::q, { EnergyConsumerPhase(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(EnergyConsumerPhase::qFixed, { EnergyConsumerPhase(it) }, { 1.0 }, { 2.0 })
    }

    @Test
    internal fun compareEnergySource() {
        compareEnergyConnection { EnergySource(it) }

        comparatorValidator.validateProperty(EnergySource::activePower, { EnergySource(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(EnergySource::reactivePower, { EnergySource(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(EnergySource::voltageAngle, { EnergySource(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(EnergySource::voltageMagnitude, { EnergySource(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(EnergySource::pMax, { EnergySource(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(EnergySource::pMin, { EnergySource(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(EnergySource::r, { EnergySource(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(EnergySource::r0, { EnergySource(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(EnergySource::rn, { EnergySource(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(EnergySource::x, { EnergySource(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(EnergySource::x0, { EnergySource(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(EnergySource::xn, { EnergySource(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(EnergySource::isExternalGrid, { EnergySource(it) }, { false }, { true })
        comparatorValidator.validateProperty(EnergySource::rMin, { EnergySource(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(EnergySource::rnMin, { EnergySource(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(EnergySource::r0Min, { EnergySource(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(EnergySource::xMin, { EnergySource(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(EnergySource::xnMin, { EnergySource(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(EnergySource::x0Min, { EnergySource(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(EnergySource::rMax, { EnergySource(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(EnergySource::rnMax, { EnergySource(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(EnergySource::r0Max, { EnergySource(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(EnergySource::xMax, { EnergySource(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(EnergySource::xnMax, { EnergySource(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(EnergySource::x0Max, { EnergySource(it) }, { 1.0 }, { 2.0 })

        comparatorValidator.validateCollection(
            EnergySource::phases,
            EnergySource::addPhase,
            { EnergySource(it) },
            { EnergySourcePhase("ecp1") },
            { EnergySourcePhase("ecp2") })
    }

    @Test
    internal fun compareEnergySourcePhase() {
        comparePowerSystemResource { EnergySourcePhase(it) }

        comparatorValidator.validateProperty(EnergySourcePhase::phase, { EnergySourcePhase(it) }, { SinglePhaseKind.A }, { SinglePhaseKind.B })
        comparatorValidator.validateProperty(EnergySourcePhase::energySource, { EnergySourcePhase(it) }, { EnergySource("es1") }, { EnergySource("es2") })
    }

    @Test
    internal fun compareFuse() {
        compareSwitch { Fuse(it) }

        comparatorValidator.validateProperty(Fuse::function, { Fuse(it) }, { CurrentRelay("cr1") }, { CurrentRelay("cr2") })
    }

    @Test
    internal fun compareGround() {
        compareConductingEquipment { Ground(it) }
    }

    @Test
    internal fun compareGroundDisconnector() {
        compareSwitch { GroundDisconnector(it) }
    }

    @Test
    internal fun compareJumper() {
        compareSwitch { Jumper(it) }
    }

    @Test
    internal fun compareJunction() {
        compareConnector { Junction(it) }
    }

    private fun compareLine(createLine: (String) -> Line) {
        compareEquipmentContainer(createLine)
    }

    @Test
    internal fun compareLinearShuntCompensator() {
        compareShuntCompensator { LinearShuntCompensator(it) }


        comparatorValidator.validateProperty(LinearShuntCompensator::b0PerSection, { LinearShuntCompensator(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(LinearShuntCompensator::bPerSection, { LinearShuntCompensator(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(LinearShuntCompensator::g0PerSection, { LinearShuntCompensator(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(LinearShuntCompensator::gPerSection, { LinearShuntCompensator(it) }, { 1.0 }, { 2.0 })
    }

    private fun comparePerLengthImpedance(createPerLengthImpedance: (String) -> PerLengthImpedance) {
        comparePerLengthLineParameter(createPerLengthImpedance)
    }

    private fun comparePerLengthLineParameter(createPerLengthLineParameter: (String) -> PerLengthLineParameter) {
        compareIdentifiedObject(createPerLengthLineParameter)
    }

    @Test
    internal fun comparePerLengthSequenceImpedance() {
        comparePerLengthImpedance { PerLengthSequenceImpedance(it) }

        comparatorValidator.validateProperty(PerLengthSequenceImpedance::r, { PerLengthSequenceImpedance(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(PerLengthSequenceImpedance::x, { PerLengthSequenceImpedance(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(PerLengthSequenceImpedance::bch, { PerLengthSequenceImpedance(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(PerLengthSequenceImpedance::gch, { PerLengthSequenceImpedance(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(PerLengthSequenceImpedance::r0, { PerLengthSequenceImpedance(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(PerLengthSequenceImpedance::x0, { PerLengthSequenceImpedance(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(PerLengthSequenceImpedance::b0ch, { PerLengthSequenceImpedance(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(PerLengthSequenceImpedance::g0ch, { PerLengthSequenceImpedance(it) }, { 1.0 }, { 2.0 })
    }

    @Test
    internal fun comparePowerElectronicsConnection() {
        compareRegulatingCondEq { PowerElectronicsConnection(it) }

        comparatorValidator.validateCollection(
            PowerElectronicsConnection::phases,
            PowerElectronicsConnection::addPhase,
            { PowerElectronicsConnection(it) },
            { PowerElectronicsConnectionPhase("pecp1") },
            { PowerElectronicsConnectionPhase("pecp2") })
        comparatorValidator.validateCollection(
            PowerElectronicsConnection::units,
            PowerElectronicsConnection::addUnit,
            { PowerElectronicsConnection(it) },
            { object : PowerElectronicsUnit("peu1") {} },
            { object : PowerElectronicsUnit("peu2") {} })

        comparatorValidator.validateProperty(PowerElectronicsConnection::maxIFault, { PowerElectronicsConnection(it) }, { 1 }, { 2 })
        comparatorValidator.validateProperty(PowerElectronicsConnection::maxQ, { PowerElectronicsConnection(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(PowerElectronicsConnection::minQ, { PowerElectronicsConnection(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(PowerElectronicsConnection::p, { PowerElectronicsConnection(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(PowerElectronicsConnection::q, { PowerElectronicsConnection(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(PowerElectronicsConnection::ratedS, { PowerElectronicsConnection(it) }, { 1 }, { 2 })
        comparatorValidator.validateProperty(PowerElectronicsConnection::ratedU, { PowerElectronicsConnection(it) }, { 1 }, { 2 })
        comparatorValidator.validateProperty(PowerElectronicsConnection::inverterStandard, { PowerElectronicsConnection(it) }, { "a" }, { "b" })
        comparatorValidator.validateProperty(PowerElectronicsConnection::sustainOpOvervoltLimit, { PowerElectronicsConnection(it) }, { 1 }, { 2 })
        comparatorValidator.validateProperty(PowerElectronicsConnection::stopAtOverFreq, { PowerElectronicsConnection(it) }, { 1.0f }, { 2.0f })
        comparatorValidator.validateProperty(PowerElectronicsConnection::stopAtUnderFreq, { PowerElectronicsConnection(it) }, { 1.0f }, { 2.0f })
        comparatorValidator.validateProperty(PowerElectronicsConnection::invVoltWattRespMode, { PowerElectronicsConnection(it) }, { null }, { true })
        comparatorValidator.validateProperty(PowerElectronicsConnection::invWattRespV1, { PowerElectronicsConnection(it) }, { 200 }, { 201 })
        comparatorValidator.validateProperty(PowerElectronicsConnection::invWattRespV2, { PowerElectronicsConnection(it) }, { 216 }, { 217 })
        comparatorValidator.validateProperty(PowerElectronicsConnection::invWattRespV3, { PowerElectronicsConnection(it) }, { 235 }, { 236 })
        comparatorValidator.validateProperty(PowerElectronicsConnection::invWattRespV4, { PowerElectronicsConnection(it) }, { 244 }, { 245 })
        comparatorValidator.validateProperty(PowerElectronicsConnection::invWattRespPAtV1, { PowerElectronicsConnection(it) }, { 1.0f }, { 0.0f })
        comparatorValidator.validateProperty(PowerElectronicsConnection::invWattRespPAtV2, { PowerElectronicsConnection(it) }, { 1.0f }, { 0.0f })
        comparatorValidator.validateProperty(PowerElectronicsConnection::invWattRespPAtV3, { PowerElectronicsConnection(it) }, { 1.0f }, { 0.0f })
        comparatorValidator.validateProperty(PowerElectronicsConnection::invWattRespPAtV4, { PowerElectronicsConnection(it) }, { 0.0f }, { 0.2f })
        comparatorValidator.validateProperty(PowerElectronicsConnection::invVoltVarRespMode, { PowerElectronicsConnection(it) }, { null }, { true })
        comparatorValidator.validateProperty(PowerElectronicsConnection::invVarRespV1, { PowerElectronicsConnection(it) }, { 200 }, { 300 })
        comparatorValidator.validateProperty(PowerElectronicsConnection::invVarRespV2, { PowerElectronicsConnection(it) }, { 200 }, { 300 })
        comparatorValidator.validateProperty(PowerElectronicsConnection::invVarRespV3, { PowerElectronicsConnection(it) }, { 200 }, { 300 })
        comparatorValidator.validateProperty(PowerElectronicsConnection::invVarRespV4, { PowerElectronicsConnection(it) }, { 200 }, { 300 })
        comparatorValidator.validateProperty(PowerElectronicsConnection::invVarRespQAtV1, { PowerElectronicsConnection(it) }, { 0.0f }, { 0.1f })
        comparatorValidator.validateProperty(PowerElectronicsConnection::invVarRespQAtV2, { PowerElectronicsConnection(it) }, { 0.0f }, { 0.1f })
        comparatorValidator.validateProperty(PowerElectronicsConnection::invVarRespQAtV3, { PowerElectronicsConnection(it) }, { 0.0f }, { 0.1f })
        comparatorValidator.validateProperty(PowerElectronicsConnection::invVarRespQAtV4, { PowerElectronicsConnection(it) }, { 0.0f }, { -0.1f })
        comparatorValidator.validateProperty(PowerElectronicsConnection::invReactivePowerMode, { PowerElectronicsConnection(it) }, { null }, { true })
        comparatorValidator.validateProperty(PowerElectronicsConnection::invFixReactivePower, { PowerElectronicsConnection(it) }, { 0.0f }, { -0.1f })
    }

    @Test
    internal fun comparePowerElectronicsConnectionPhase() {
        comparePowerSystemResource { PowerElectronicsConnectionPhase(it) }

        comparatorValidator.validateProperty(
            PowerElectronicsConnectionPhase::phase,
            { PowerElectronicsConnectionPhase(it) },
            { SinglePhaseKind.A },
            { SinglePhaseKind.B })
        comparatorValidator.validateProperty(
            PowerElectronicsConnectionPhase::powerElectronicsConnection,
            { PowerElectronicsConnectionPhase(it) },
            { PowerElectronicsConnection("pec1") },
            { PowerElectronicsConnection("pec2") })
    }

    @Test
    internal fun comparePowerTransformer() {
        compareConductingEquipment { PowerTransformer(it) }

        comparatorValidator.validateProperty(
            PowerTransformer::assetInfo,
            { PowerTransformer(it) },
            { PowerTransformerInfo("pti1") },
            { PowerTransformerInfo("pti2") }
        )
        comparatorValidator.validateProperty(PowerTransformer::vectorGroup, { PowerTransformer(it) }, { VectorGroup.DYN11 }, { VectorGroup.D0 })
        comparatorValidator.validateProperty(PowerTransformer::transformerUtilisation, { PowerTransformer(it) }, { 0.1 }, { 0.9 })
        comparatorValidator.validateProperty(
            PowerTransformer::constructionKind,
            { PowerTransformer(it) },
            { TransformerConstructionKind.padmounted },
            { TransformerConstructionKind.overhead }
        )
        comparatorValidator.validateProperty(
            PowerTransformer::function,
            { PowerTransformer(it) },
            { TransformerFunctionKind.distributionTransformer },
            { TransformerFunctionKind.isolationTransformer }
        )

        comparatorValidator.validateIndexedCollection(
            PowerTransformer::ends, PowerTransformer::addEnd, { PowerTransformer(it) },
            { PowerTransformerEnd(mRID = "pte1") }, { PowerTransformerEnd(mRID = "pte2") }, PowerTransformerEnd::powerTransformer.setter
        )
    }

    @Test
    internal fun comparePowerTransformerEnd() {
        compareTransformerEnd { PowerTransformerEnd(it) }

        comparatorValidator.validateProperty(
            PowerTransformerEnd::powerTransformer,
            { PowerTransformerEnd(it) },
            { PowerTransformer("pt1") },
            { PowerTransformer("pt2") }
        )
        comparatorValidator.validateProperty(PowerTransformerEnd::b, { PowerTransformerEnd(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(PowerTransformerEnd::b0, { PowerTransformerEnd(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(PowerTransformerEnd::connectionKind, { PowerTransformerEnd(it) }, { WindingConnection.A }, { WindingConnection.D })
        comparatorValidator.validateProperty(PowerTransformerEnd::g, { PowerTransformerEnd(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(PowerTransformerEnd::g0, { PowerTransformerEnd(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(PowerTransformerEnd::phaseAngleClock, { PowerTransformerEnd(it) }, { 1 }, { 2 })
        comparatorValidator.validateProperty(PowerTransformerEnd::r, { PowerTransformerEnd(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(PowerTransformerEnd::r0, { PowerTransformerEnd(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(PowerTransformerEnd::ratedS, { PowerTransformerEnd(it) }, { 1 }, { 2 }, expectedDifferences = setOf("sRatings"))
        comparatorValidator.validateProperty(PowerTransformerEnd::ratedU, { PowerTransformerEnd(it) }, { 1 }, { 2 })
        comparatorValidator.validateProperty(PowerTransformerEnd::x, { PowerTransformerEnd(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(PowerTransformerEnd::x0, { PowerTransformerEnd(it) }, { 1.0 }, { 2.0 })

        comparatorValidator.validateProperty(PowerTransformerEnd::r, { PowerTransformerEnd(it) }, { 1.0 }, { Double.NaN })
        comparatorValidator.validateProperty(PowerTransformerEnd::r0, { PowerTransformerEnd(it) }, { 1.0 }, { Double.NaN })
        comparatorValidator.validateProperty(PowerTransformerEnd::x, { PowerTransformerEnd(it) }, { 1.0 }, { Double.NaN })
        comparatorValidator.validateProperty(PowerTransformerEnd::x0, { PowerTransformerEnd(it) }, { 1.0 }, { Double.NaN })

        comparatorValidator.validateIndexedCollection(
            PowerTransformerEnd::sRatings,
            PowerTransformerEnd::addRating,
            { PowerTransformerEnd(it) },
            { TransformerEndRatedS(TransformerCoolingType.UNKNOWN_COOLING_TYPE, 1) },
            { TransformerEndRatedS(TransformerCoolingType.UNKNOWN_COOLING_TYPE, 2) },
            expectedDifferences = setOf("ratedS")
        )

    }

    private fun compareProtectedSwitch(createProtectedSwitch: (String) -> ProtectedSwitch) {
        compareSwitch(createProtectedSwitch)

        comparatorValidator.validateProperty(ProtectedSwitch::breakingCapacity, createProtectedSwitch, { 1 }, { 2 })
        comparatorValidator.validateCollection(
            ProtectedSwitch::relayFunctions,
            ProtectedSwitch::addRelayFunction,
            createProtectedSwitch,
            { object : ProtectionRelayFunction("prf1") {} },
            { object : ProtectionRelayFunction("prf2") {} }
        )
    }

    @Test
    internal fun compareRatioTapChanger() {
        compareTapChanger { RatioTapChanger(it) }

        comparatorValidator.validateProperty(
            RatioTapChanger::transformerEnd,
            { RatioTapChanger(it) },
            { PowerTransformerEnd("pte1") },
            { PowerTransformerEnd("pte2") })

        comparatorValidator.validateProperty(RatioTapChanger::stepVoltageIncrement, { RatioTapChanger(it) }, { 1.0 }, { 2.0 })
    }

    @Test
    internal fun compareRecloser() {
        compareProtectedSwitch { Recloser(it) }
    }

    private fun compareRegulatingCondEq(createRegulatingCondEq: (String) -> RegulatingCondEq) {
        compareEnergyConnection(createRegulatingCondEq)

        comparatorValidator.validateProperty(RegulatingCondEq::controlEnabled, createRegulatingCondEq, { false }, { true })
    }

    private fun compareRegulatingControl(createRegulatingControl: (String) -> RegulatingControl) {
        comparePowerSystemResource(createRegulatingControl)

        comparatorValidator.validateProperty(RegulatingControl::discrete, createRegulatingControl, { false }, { true })
        comparatorValidator.validateProperty(
            RegulatingControl::mode,
            createRegulatingControl,
            { RegulatingControlModeKind.voltage },
            { RegulatingControlModeKind.UNKNOWN_CONTROL_MODE })
        comparatorValidator.validateProperty(RegulatingControl::monitoredPhase, createRegulatingControl, { PhaseCode.ABC }, { PhaseCode.A })
        comparatorValidator.validateProperty(RegulatingControl::targetDeadband, createRegulatingControl, { 1.0f }, { 2.0f })
        comparatorValidator.validateProperty(RegulatingControl::targetValue, createRegulatingControl, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(RegulatingControl::enabled, createRegulatingControl, { false }, { true })
        comparatorValidator.validateProperty(RegulatingControl::maxAllowedTargetValue, createRegulatingControl, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(RegulatingControl::minAllowedTargetValue, createRegulatingControl, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(RegulatingControl::ratedCurrent, createRegulatingControl, { 1.1 }, { 2.2 })
        comparatorValidator.validateProperty(RegulatingControl::terminal, createRegulatingControl, { Terminal("t1") }, { Terminal("t2") })

        comparatorValidator.validateCollection(
            RegulatingControl::regulatingCondEqs,
            RegulatingControl::addRegulatingCondEq,
            createRegulatingControl,
            { object : RegulatingCondEq("rce1") {} },
            { object : RegulatingCondEq("rce2") {} }
        )
    }

    @Test
    internal fun compareSeriesCompensator() {
        compareConductingEquipment { SeriesCompensator(it) }

        comparatorValidator.validateProperty(SeriesCompensator::r, { SeriesCompensator(it) }, { 1.1 }, { 2.2 })
        comparatorValidator.validateProperty(SeriesCompensator::r0, { SeriesCompensator(it) }, { 1.1 }, { 2.2 })
        comparatorValidator.validateProperty(SeriesCompensator::x, { SeriesCompensator(it) }, { 1.1 }, { 2.2 })
        comparatorValidator.validateProperty(SeriesCompensator::x0, { SeriesCompensator(it) }, { 1.1 }, { 2.2 })
        comparatorValidator.validateProperty(SeriesCompensator::varistorRatedCurrent, { SeriesCompensator(it) }, { 1 }, { 2 })
        comparatorValidator.validateProperty(SeriesCompensator::varistorVoltageThreshold, { SeriesCompensator(it) }, { 1 }, { 2 })
    }

    private fun compareShuntCompensator(createShuntCompensator: (String) -> ShuntCompensator) {
        compareRegulatingCondEq(createShuntCompensator)

        comparatorValidator.validateProperty(
            ShuntCompensator::assetInfo,
            createShuntCompensator,
            { ShuntCompensatorInfo("sci1") },
            { ShuntCompensatorInfo("sci2") }
        )
        comparatorValidator.validateProperty(ShuntCompensator::grounded, createShuntCompensator, { false }, { true })
        comparatorValidator.validateProperty(ShuntCompensator::nomU, createShuntCompensator, { 1 }, { 2 })
        comparatorValidator.validateProperty(
            ShuntCompensator::phaseConnection,
            createShuntCompensator,
            { PhaseShuntConnectionKind.D },
            { PhaseShuntConnectionKind.G })
        comparatorValidator.validateProperty(ShuntCompensator::sections, createShuntCompensator, { 1.0 }, { 2.0 })
    }

    private fun compareSwitch(createSwitch: (String) -> Switch) {
        compareConductingEquipment(createSwitch)

        comparatorValidator.validateProperty(Switch::assetInfo, createSwitch, { SwitchInfo("si1") }, { SwitchInfo("si2") })
        comparatorValidator.validateProperty(Switch::ratedCurrent, createSwitch, { 1 }, { 2 })

        val closedSwitch = createSwitch("mRID").apply { setNormallyOpen(false); setOpen(true) }
        val openSwitch = createSwitch("mRID").apply { setNormallyOpen(true); setOpen(false) }

        val difference = ObjectDifference(closedSwitch, openSwitch).apply {
            differences["isNormallyOpen"] = ValueDifference(
                PhaseCode.ABCN.singlePhases.associateWith { false },
                PhaseCode.ABCN.singlePhases.associateWith { true })

            differences["isOpen"] = ValueDifference(
                PhaseCode.ABCN.singlePhases.associateWith { true },
                PhaseCode.ABCN.singlePhases.associateWith { false })
        }

        comparatorValidator.validateCompare(closedSwitch, openSwitch, expectModification = difference)
    }

    private fun compareTapChanger(createTapChanger: (String) -> TapChanger) {
        comparePowerSystemResource(createTapChanger)

        comparatorValidator.validateProperty(TapChanger::controlEnabled, createTapChanger, { true }, { false })
        comparatorValidator.validateProperty(TapChanger::highStep, createTapChanger, { 1 }, { 2 })
        comparatorValidator.validateProperty(TapChanger::lowStep, { createTapChanger(it).apply { highStep = 10 } }, { 0 }, { 1 })
        comparatorValidator.validateProperty(TapChanger::neutralStep, { createTapChanger(it).apply { highStep = 10 } }, { 1 }, { 2 })
        comparatorValidator.validateProperty(TapChanger::neutralU, createTapChanger, { 1 }, { 2 })
        comparatorValidator.validateProperty(TapChanger::normalStep, { createTapChanger(it).apply { highStep = 10 } }, { 1 }, { 2 })
        comparatorValidator.validateProperty(TapChanger::step, { createTapChanger(it).apply { highStep = 10 } }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(
            TapChanger::tapChangerControl,
            { createTapChanger(it).apply { highStep = 10 } },
            { TapChangerControl("tcc1") },
            { TapChangerControl("tcc2") })
    }

    @Test
    internal fun compareTapChangerControl() {
        compareRegulatingControl { TapChangerControl(it) }

        comparatorValidator.validateProperty(TapChangerControl::limitVoltage, { TapChangerControl(it) }, { 1 }, { 2 })
        comparatorValidator.validateProperty(TapChangerControl::lineDropCompensation, { TapChangerControl(it) }, { null }, { true })
        comparatorValidator.validateProperty(TapChangerControl::lineDropR, { TapChangerControl(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(TapChangerControl::lineDropX, { TapChangerControl(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(TapChangerControl::reverseLineDropR, { TapChangerControl(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(TapChangerControl::reverseLineDropX, { TapChangerControl(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(TapChangerControl::forwardLDCBlocking, { TapChangerControl(it) }, { null }, { true })
        comparatorValidator.validateProperty(TapChangerControl::timeDelay, { TapChangerControl(it) }, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(TapChangerControl::coGenerationEnabled, { TapChangerControl(it) }, { null }, { true })
    }

    private fun compareTransformerEnd(createTransformerEnd: (String) -> TransformerEnd) {
        compareIdentifiedObject(createTransformerEnd)

        comparatorValidator.validateProperty(TransformerEnd::grounded, createTransformerEnd, { true }, { false })
        comparatorValidator.validateProperty(TransformerEnd::rGround, createTransformerEnd, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(TransformerEnd::xGround, createTransformerEnd, { 1.0 }, { 2.0 })
        comparatorValidator.validateProperty(TransformerEnd::baseVoltage, createTransformerEnd, { BaseVoltage("bv1") }, { BaseVoltage("b21") })
        comparatorValidator.validateProperty(TransformerEnd::ratioTapChanger, createTransformerEnd, { RatioTapChanger("rtc1") }, { RatioTapChanger("rtc2") })
        comparatorValidator.validateProperty(TransformerEnd::terminal, createTransformerEnd, { Terminal("t1") }, { Terminal("t2") })
        comparatorValidator.validateProperty(
            TransformerEnd::starImpedance,
            createTransformerEnd,
            { TransformerStarImpedance("tsi1") },
            { TransformerStarImpedance("tsi2") })
    }

    @Test
    internal fun compareEvChargingUnit() {
        comparePowerElectronicsUnit { EvChargingUnit(it) }
    }

    @Test
    internal fun compareCircuit() {
        compareLine { Circuit(it) }

        comparatorValidator.validateProperty(Circuit::loop, { Circuit(it) }, { Loop("l1") }, { Loop("l2") })

        comparatorValidator.validateCollection(Circuit::endTerminals, Circuit::addEndTerminal, { Circuit(it) }, { Terminal("t1") }, { Terminal("t2") })

        comparatorValidator.validateCollection(Circuit::endSubstations, Circuit::addEndSubstation, { Circuit(it) }, { Substation("s1") }, { Substation("s2") })
    }

    @Test
    internal fun compareLoop() {
        compareIdentifiedObject { Loop(it) }

        comparatorValidator.validateCollection(Loop::circuits, Loop::addCircuit, { Loop(it) }, { Circuit("c1") }, { Circuit("c2") })
        comparatorValidator.validateCollection(Loop::substations, Loop::addSubstation, { Loop(it) }, { Substation("s1") }, { Substation("s2") })
        comparatorValidator.validateCollection(
            Loop::energizingSubstations,
            Loop::addEnergizingSubstation,
            { Loop(it) },
            { Substation("s1") },
            { Substation("s2") })
    }

}

/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network

import com.zepben.evolve.cim.iec61968.assetinfo.*
import com.zepben.evolve.cim.iec61968.assets.*
import com.zepben.evolve.cim.iec61968.common.Location
import com.zepben.evolve.cim.iec61968.infiec61968.infassetinfo.CurrentTransformerInfo
import com.zepben.evolve.cim.iec61968.infiec61968.infassetinfo.PotentialTransformerInfo
import com.zepben.evolve.cim.iec61968.infiec61968.infassetinfo.RelayInfo
import com.zepben.evolve.cim.iec61968.metering.EndDevice
import com.zepben.evolve.cim.iec61968.metering.Meter
import com.zepben.evolve.cim.iec61968.metering.UsagePoint
import com.zepben.evolve.cim.iec61968.operations.OperationalRestriction
import com.zepben.evolve.cim.iec61970.base.auxiliaryequipment.*
import com.zepben.evolve.cim.iec61970.base.core.*
import com.zepben.evolve.cim.iec61970.base.equivalents.EquivalentBranch
import com.zepben.evolve.cim.iec61970.base.equivalents.EquivalentEquipment
import com.zepben.evolve.cim.iec61970.base.meas.*
import com.zepben.evolve.cim.iec61970.base.protection.*
import com.zepben.evolve.cim.iec61970.base.scada.RemoteControl
import com.zepben.evolve.cim.iec61970.base.scada.RemotePoint
import com.zepben.evolve.cim.iec61970.base.scada.RemoteSource
import com.zepben.evolve.cim.iec61970.base.wires.*
import com.zepben.evolve.cim.iec61970.base.wires.generation.production.BatteryUnit
import com.zepben.evolve.cim.iec61970.base.wires.generation.production.PhotoVoltaicUnit
import com.zepben.evolve.cim.iec61970.base.wires.generation.production.PowerElectronicsUnit
import com.zepben.evolve.cim.iec61970.base.wires.generation.production.PowerElectronicsWindUnit
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.Circuit
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.Loop
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.LvFeeder
import com.zepben.evolve.cim.iec61970.infiec61970.wires.generation.production.EvChargingUnit
import com.zepben.evolve.services.common.BaseServiceComparator
import com.zepben.evolve.services.common.ObjectDifference
import com.zepben.evolve.services.common.ValueDifference
import com.zepben.evolve.services.common.compareValues

/**
 * @param options Indicates which optional checks to perform
 */
//
// NOTE: Unused functions have been suppressed for this class as they are access by reflection rather than directly. This
//       means they are always flagged as unused. By suppressing the warning it also means you might not be testing every
//       function, so make sure you check the code coverage
//
@Suppress("unused")
class NetworkServiceComparator @JvmOverloads constructor(
    private var options: NetworkServiceComparatorOptions = NetworkServiceComparatorOptions.all()
) : BaseServiceComparator() {

    /************ IEC61968 ASSET INFO ************/

    private fun compareCableInfo(source: CableInfo, target: CableInfo): ObjectDifference<CableInfo> =
        ObjectDifference(source, target).apply { compareWireInfo() }

    private fun compareNoLoadTest(source: NoLoadTest, target: NoLoadTest): ObjectDifference<NoLoadTest> =
        ObjectDifference(source, target).apply {
            compareTransformerTest()

            compareValues(NoLoadTest::energisedEndVoltage, NoLoadTest::excitingCurrent, NoLoadTest::excitingCurrentZero, NoLoadTest::loss, NoLoadTest::lossZero)
        }

    private fun compareOpenCircuitTest(source: OpenCircuitTest, target: OpenCircuitTest): ObjectDifference<OpenCircuitTest> =
        ObjectDifference(source, target).apply {
            compareTransformerTest()

            compareValues(
                OpenCircuitTest::energisedEndStep,
                OpenCircuitTest::energisedEndVoltage,
                OpenCircuitTest::openEndStep,
                OpenCircuitTest::openEndVoltage,
                OpenCircuitTest::phaseShift
            )
        }

    private fun compareOverheadWireInfo(source: OverheadWireInfo, target: OverheadWireInfo): ObjectDifference<OverheadWireInfo> =
        ObjectDifference(source, target).apply { compareWireInfo() }

    private fun comparePowerTransformerInfo(source: PowerTransformerInfo, target: PowerTransformerInfo): ObjectDifference<PowerTransformerInfo> =
        ObjectDifference(source, target).apply {
            compareAssetInfo()

            compareIdReferenceCollections(PowerTransformerInfo::transformerTankInfos)
        }

    private fun compareShortCircuitTest(source: ShortCircuitTest, target: ShortCircuitTest): ObjectDifference<ShortCircuitTest> =
        ObjectDifference(source, target).apply {
            compareTransformerTest()

            compareValues(
                ShortCircuitTest::current,
                ShortCircuitTest::energisedEndStep,
                ShortCircuitTest::groundedEndStep,
                ShortCircuitTest::leakageImpedance,
                ShortCircuitTest::leakageImpedanceZero,
                ShortCircuitTest::loss,
                ShortCircuitTest::lossZero,
                ShortCircuitTest::power,
                ShortCircuitTest::voltage,
                ShortCircuitTest::voltageOhmicPart
            )
        }

    private fun compareShuntCompensatorInfo(source: ShuntCompensatorInfo, target: ShuntCompensatorInfo): ObjectDifference<ShuntCompensatorInfo> =
        ObjectDifference(source, target).apply {
            compareAssetInfo()

            compareValues(
                ShuntCompensatorInfo::maxPowerLoss,
                ShuntCompensatorInfo::ratedCurrent,
                ShuntCompensatorInfo::ratedReactivePower,
                ShuntCompensatorInfo::ratedVoltage
            )
        }

    private fun compareSwitchInfo(source: SwitchInfo, target: SwitchInfo): ObjectDifference<SwitchInfo> =
        ObjectDifference(source, target).apply {
            compareAssetInfo()

            compareValues(SwitchInfo::ratedInterruptingTime)
        }

    private fun compareTransformerEndInfo(source: TransformerEndInfo, target: TransformerEndInfo): ObjectDifference<TransformerEndInfo> =
        ObjectDifference(source, target).apply {
            compareAssetInfo()

            compareIdReferences(
                TransformerEndInfo::transformerStarImpedance,
                TransformerEndInfo::energisedEndNoLoadTests,
                TransformerEndInfo::energisedEndShortCircuitTests,
                TransformerEndInfo::groundedEndShortCircuitTests,
                TransformerEndInfo::openEndOpenCircuitTests,
                TransformerEndInfo::energisedEndOpenCircuitTests
            )
            compareValues(
                TransformerEndInfo::connectionKind,
                TransformerEndInfo::emergencyS,
                TransformerEndInfo::endNumber,
                TransformerEndInfo::insulationU,
                TransformerEndInfo::phaseAngleClock,
                TransformerEndInfo::r,
                TransformerEndInfo::ratedS,
                TransformerEndInfo::ratedU,
                TransformerEndInfo::shortTermS
            )
        }

    private fun compareTransformerTankInfo(source: TransformerTankInfo, target: TransformerTankInfo): ObjectDifference<TransformerTankInfo> =
        ObjectDifference(source, target).apply {
            compareAssetInfo()

            compareIdReferenceCollections(TransformerTankInfo::transformerEndInfos)
        }

    private fun ObjectDifference<out TransformerTest>.compareTransformerTest(): ObjectDifference<out TransformerTest> =
        apply {
            compareIdentifiedObject()

            compareValues(TransformerTest::basePower, TransformerTest::temperature)
        }

    private fun ObjectDifference<out WireInfo>.compareWireInfo(): ObjectDifference<out WireInfo> =
        apply {
            compareAssetInfo()

            compareValues(WireInfo::ratedCurrent, WireInfo::material)
        }

    /************ IEC61968 ASSETS ************/

    private fun ObjectDifference<out Asset>.compareAsset(): ObjectDifference<out Asset> =
        apply {
            compareIdentifiedObject()

            compareIdReferences(Asset::location)
            compareIdReferenceCollections(Asset::organisationRoles)
        }

    private fun ObjectDifference<out AssetContainer>.compareAssetContainer(): ObjectDifference<out AssetContainer> =
        apply { compareAsset() }

    private fun ObjectDifference<out AssetInfo>.compareAssetInfo(): ObjectDifference<out AssetInfo> =
        apply { compareIdentifiedObject() }

    private fun ObjectDifference<out AssetOrganisationRole>.compareAssetOrganisationRole(): ObjectDifference<out AssetOrganisationRole> =
        apply { compareOrganisationRole() }

    private fun compareAssetOwner(source: AssetOwner, target: AssetOwner): ObjectDifference<AssetOwner> =
        ObjectDifference(source, target).apply { compareAssetOrganisationRole() }

    private fun ObjectDifference<out Structure>.compareStructure(): ObjectDifference<out Structure> =
        apply { compareAssetContainer() }

    private fun comparePole(source: Pole, target: Pole): ObjectDifference<Pole> =
        ObjectDifference(source, target).apply {
            compareStructure()

            compareValues(Pole::classification)
            compareIdReferenceCollections(Pole::streetlights)
        }

    private fun compareStreetlight(source: Streetlight, target: Streetlight): ObjectDifference<Streetlight> =
        ObjectDifference(source, target).apply {
            compareAsset()

            compareValues(Streetlight::lightRating, Streetlight::lampKind)
            compareIdReferences(Streetlight::pole)
        }

    /************ IEC61968 COMMON ************/

    private fun compareLocation(source: Location, target: Location): ObjectDifference<Location> =
        ObjectDifference(source, target).apply {
            compareIdentifiedObject()

            compareValues(Location::mainAddress)
            compareIndexedValueCollections(Location::points)
        }

    /************ IEC61968 infIEC61968 InfAssetInfo ************/

    private fun compareRelayInfo(source: RelayInfo, target: RelayInfo): ObjectDifference<RelayInfo> =
        ObjectDifference(source, target).apply {
            compareAssetInfo()

            compareValues(RelayInfo::curveSetting, RelayInfo::recloseFast)
            compareIndexedValueCollections(RelayInfo::recloseDelays)
        }

    private fun compareCurrentTransformerInfo(source: CurrentTransformerInfo, target: CurrentTransformerInfo): ObjectDifference<CurrentTransformerInfo> =
        ObjectDifference(source, target).apply {
            compareAssetInfo()

            compareValues(
                CurrentTransformerInfo::accuracyClass,
                CurrentTransformerInfo::accuracyLimit,
                CurrentTransformerInfo::coreCount,
                CurrentTransformerInfo::ctClass,
                CurrentTransformerInfo::kneePointVoltage,
                CurrentTransformerInfo::maxRatio,
                CurrentTransformerInfo::nominalRatio,
                CurrentTransformerInfo::primaryRatio,
                CurrentTransformerInfo::ratedCurrent,
                CurrentTransformerInfo::secondaryFlsRating,
                CurrentTransformerInfo::secondaryRatio,
                CurrentTransformerInfo::usage
            )
        }

    private fun comparePotentialTransformerInfo(
        source: PotentialTransformerInfo,
        target: PotentialTransformerInfo
    ): ObjectDifference<PotentialTransformerInfo> =
        ObjectDifference(source, target).apply {
            compareAssetInfo()

            compareValues(
                PotentialTransformerInfo::accuracyClass,
                PotentialTransformerInfo::nominalRatio,
                PotentialTransformerInfo::primaryRatio,
                PotentialTransformerInfo::ptClass,
                PotentialTransformerInfo::ratedVoltage,
                PotentialTransformerInfo::secondaryRatio
            )
        }

    /************ IEC61968 METERING ************/

    private fun ObjectDifference<out EndDevice>.compareEndDevice(): ObjectDifference<out EndDevice> =
        apply {
            compareAssetContainer()

            if (options.compareLvSimplification)
                compareIdReferenceCollections(EndDevice::usagePoints)

            compareValues(EndDevice::customerMRID)
            compareIdReferences(EndDevice::serviceLocation)
        }

    private fun compareMeter(source: Meter, target: Meter): ObjectDifference<Meter> =
        ObjectDifference(source, target).apply { compareEndDevice() }

    private fun compareUsagePoint(source: UsagePoint, target: UsagePoint): ObjectDifference<UsagePoint> =
        ObjectDifference(source, target).apply {
            compareIdentifiedObject()

            compareIdReferences(UsagePoint::usagePointLocation)
            compareValues(
                UsagePoint::isVirtual,
                UsagePoint::connectionCategory,
                UsagePoint::ratedPower,
                UsagePoint::approvedInverterCapacity,
                UsagePoint::phaseCode
            )
            if (options.compareLvSimplification)
                compareIdReferenceCollections(UsagePoint::equipment, UsagePoint::endDevices)
        }

    /************ IEC61968 OPERATIONS ************/

    private fun compareOperationalRestriction(source: OperationalRestriction, target: OperationalRestriction): ObjectDifference<OperationalRestriction> =
        ObjectDifference(source, target).apply {
            compareDocument()

            compareIdReferenceCollections(OperationalRestriction::equipment)
        }

    /************ IEC61970 BASE AUXILIARY EQUIPMENT ************/

    private fun ObjectDifference<out AuxiliaryEquipment>.compareAuxiliaryEquipment(): ObjectDifference<out AuxiliaryEquipment> =
        apply {
            compareEquipment()

            if (options.compareTerminals)
                compareIdReferences(AuxiliaryEquipment::terminal)
        }

    private fun compareCurrentTransformer(source: CurrentTransformer, target: CurrentTransformer): ObjectDifference<CurrentTransformer> =
        ObjectDifference(source, target).apply {
            compareSensor()

            compareValues(CurrentTransformer::coreBurden)
        }

    private fun compareFaultIndicator(source: FaultIndicator, target: FaultIndicator): ObjectDifference<FaultIndicator> =
        ObjectDifference(source, target).apply { compareAuxiliaryEquipment() }

    private fun comparePotentialTransformer(source: PotentialTransformer, target: PotentialTransformer): ObjectDifference<PotentialTransformer> =
        ObjectDifference(source, target).apply {
            compareSensor()

            compareValues(PotentialTransformer::type)
        }

    private fun ObjectDifference<out Sensor>.compareSensor(): ObjectDifference<out Sensor> =
        apply {
            compareAuxiliaryEquipment()

            compareIdReferenceCollections(Sensor::relayFunctions)
        }

    /************ IEC61970 BASE CORE ************/

    private fun ObjectDifference<out AcDcTerminal>.compareAcDcTerminal(): ObjectDifference<out AcDcTerminal> =
        apply { compareIdentifiedObject() }

    private fun compareBaseVoltage(source: BaseVoltage, target: BaseVoltage): ObjectDifference<BaseVoltage> =
        ObjectDifference(source, target).apply {
            compareIdentifiedObject()

            compareValues(BaseVoltage::nominalVoltage)
        }

    private fun ObjectDifference<out ConductingEquipment>.compareConductingEquipment(): ObjectDifference<out ConductingEquipment> =
        apply {
            compareEquipment()

            compareIdReferences(ConductingEquipment::baseVoltage)
            if (options.compareTerminals)
                compareIndexedIdReferenceCollections(ConductingEquipment::terminals)
        }

    private fun compareConnectivityNode(source: ConnectivityNode, target: ConnectivityNode): ObjectDifference<ConnectivityNode> =
        ObjectDifference(source, target).apply {
            compareIdentifiedObject()

            compareIdReferenceCollections(ConnectivityNode::terminals)
        }

    private fun ObjectDifference<out ConnectivityNodeContainer>.compareConnectivityNodeContainer(): ObjectDifference<out ConnectivityNodeContainer> =
        apply { comparePowerSystemResource() }

    private fun ObjectDifference<out Curve>.compareCurve(): ObjectDifference<out Curve> =
        apply {
            compareIdentifiedObject()

            compareIndexedValueCollections(Curve::data)
        }

    private fun ObjectDifference<out Equipment>.compareEquipment(): ObjectDifference<out Equipment> =
        apply {
            comparePowerSystemResource()

            compareValues(Equipment::inService, Equipment::normallyInService, Equipment::commissionedDate)

            if (options.compareEquipmentContainers)
                compareIdReferenceCollections(Equipment::containers)

            if (options.compareLvSimplification)
                compareIdReferenceCollections(Equipment::usagePoints)

            compareIdReferenceCollections(Equipment::operationalRestrictions)

            if (options.compareEquipmentContainers)
                compareIdReferenceCollections(Equipment::currentContainers)
        }

    private fun ObjectDifference<out EquipmentContainer>.compareEquipmentContainer(): ObjectDifference<out EquipmentContainer> =
        apply {
            compareConnectivityNodeContainer()

            compareIdReferenceCollections(EquipmentContainer::equipment)
        }

    private fun compareFeeder(source: Feeder, target: Feeder): ObjectDifference<Feeder> =
        ObjectDifference(source, target).apply {
            compareEquipmentContainer()

            compareIdReferences(Feeder::normalHeadTerminal, Feeder::normalEnergizingSubstation)
            compareIdReferenceCollections(Feeder::normalEnergizedLvFeeders)
            if (options.compareFeederEquipment)
                compareIdReferenceCollections(Feeder::currentEquipment)
        }

    private fun compareGeographicalRegion(source: GeographicalRegion, target: GeographicalRegion): ObjectDifference<GeographicalRegion> =
        ObjectDifference(source, target).apply {
            compareIdentifiedObject()

            compareIdReferenceCollections(GeographicalRegion::subGeographicalRegions)
        }

    private fun ObjectDifference<out PowerSystemResource>.comparePowerSystemResource(): ObjectDifference<out PowerSystemResource> =
        apply {
            compareIdentifiedObject()

            compareIdReferences(PowerSystemResource::assetInfo, PowerSystemResource::location)
            compareValues(PowerSystemResource::numControls)
        }

    private fun compareSite(source: Site, target: Site): ObjectDifference<Site> =
        ObjectDifference(source, target).apply {
            if (options.compareEquipmentContainers) {
                compareEquipmentContainer()
            }
        }

    private fun compareSubGeographicalRegion(source: SubGeographicalRegion, target: SubGeographicalRegion): ObjectDifference<SubGeographicalRegion> =
        ObjectDifference(source, target).apply {
            compareIdentifiedObject()

            compareIdReferences(SubGeographicalRegion::geographicalRegion)
            compareIdReferenceCollections(SubGeographicalRegion::substations)
        }

    private fun compareSubstation(source: Substation, target: Substation): ObjectDifference<Substation> =
        ObjectDifference(source, target).apply {
            compareEquipmentContainer()

            compareIdReferences(Substation::subGeographicalRegion)
            compareIdReferenceCollections(Substation::feeders)
        }

    private fun compareTerminal(source: Terminal, target: Terminal): ObjectDifference<Terminal> =
        ObjectDifference(source, target).apply {
            compareAcDcTerminal()

            compareIdReferences(Terminal::conductingEquipment, Terminal::connectivityNode)
            compareValues(Terminal::phases, Terminal::sequenceNumber, Terminal::normalFeederDirection, Terminal::currentFeederDirection)

            // TracedPhases is not comparable directly
            addIfDifferent(Terminal::tracedPhases.name, Terminal::tracedPhases.compareValues(source, target) { it.phaseStatusInternal })
        }

    /************ IEC61970 BASE EQUIVALENTS ************/

    private fun compareEquivalentBranch(source: EquivalentBranch, target: EquivalentBranch): ObjectDifference<EquivalentBranch> =
        ObjectDifference(source, target).apply {
            compareEquivalentEquipment()

            compareValues(
                EquivalentBranch::negativeR12,
                EquivalentBranch::negativeR21,
                EquivalentBranch::negativeX12,
                EquivalentBranch::negativeX21,
                EquivalentBranch::positiveR12,
                EquivalentBranch::positiveR21,
                EquivalentBranch::positiveX12,
                EquivalentBranch::positiveX21,
                EquivalentBranch::r,
                EquivalentBranch::r21,
                EquivalentBranch::x,
                EquivalentBranch::x21,
                EquivalentBranch::zeroR12,
                EquivalentBranch::zeroR21,
                EquivalentBranch::zeroX12,
                EquivalentBranch::zeroX21
            )
        }

    private fun ObjectDifference<out EquivalentEquipment>.compareEquivalentEquipment(): ObjectDifference<out EquivalentEquipment> =
        apply { compareConductingEquipment() }

    /************ IEC61970 BASE MEAS ************/

    private fun compareAccumulator(source: Accumulator, target: Accumulator): ObjectDifference<Accumulator> =
        ObjectDifference(source, target).apply {
            compareMeasurement()
        }

    private fun compareAnalog(source: Analog, target: Analog): ObjectDifference<Analog> =
        ObjectDifference(source, target).apply {
            compareMeasurement()

            compareValues(Analog::positiveFlowIn)
        }

    private fun compareControl(source: Control, target: Control): ObjectDifference<Control> =
        ObjectDifference(source, target).apply {
            compareIoPoint()

            compareValues(Control::powerSystemResourceMRID)
            compareIdReferences(Control::remoteControl)
        }

    private fun compareDiscrete(source: Discrete, target: Discrete): ObjectDifference<Discrete> =
        ObjectDifference(source, target).apply {
            compareMeasurement()
        }

    private fun ObjectDifference<out IoPoint>.compareIoPoint(): ObjectDifference<out IoPoint> =
        apply { compareIdentifiedObject() }

    private fun ObjectDifference<out Measurement>.compareMeasurement(): ObjectDifference<out Measurement> =
        apply {
            compareIdentifiedObject()

            compareValues(Measurement::powerSystemResourceMRID, Measurement::unitSymbol, Measurement::phases, Measurement::terminalMRID)
            compareIdReferences(Measurement::remoteSource)
        }

    /************ IEC61970 Base Protection ************/

    private fun compareCurrentRelay(source: CurrentRelay, target: CurrentRelay): ObjectDifference<CurrentRelay> =
        ObjectDifference(source, target).apply {
            compareProtectionRelayFunction()

            compareValues(CurrentRelay::currentLimit1, CurrentRelay::inverseTimeFlag, CurrentRelay::timeDelay1)
        }

    private fun compareDistanceRelay(source: DistanceRelay, target: DistanceRelay): ObjectDifference<DistanceRelay> =
        ObjectDifference(source, target).apply {
            compareProtectionRelayFunction()

            compareValues(
                DistanceRelay::backwardBlind,
                DistanceRelay::backwardReach,
                DistanceRelay::backwardReactance,
                DistanceRelay::forwardBlind,
                DistanceRelay::forwardReach,
                DistanceRelay::forwardReactance,
                DistanceRelay::operationPhaseAngle1,
                DistanceRelay::operationPhaseAngle2,
                DistanceRelay::operationPhaseAngle3
            )
        }

    private fun ObjectDifference<out ProtectionRelayFunction>.compareProtectionRelayFunction(): ObjectDifference<out ProtectionRelayFunction> =
        apply {
            comparePowerSystemResource()

            compareValues(
                ProtectionRelayFunction::model,
                ProtectionRelayFunction::reclosing,
                ProtectionRelayFunction::relayDelayTime,
                ProtectionRelayFunction::protectionKind,
                ProtectionRelayFunction::directable,
                ProtectionRelayFunction::powerDirection
            )
            compareIndexedValueCollections(ProtectionRelayFunction::timeLimits, ProtectionRelayFunction::thresholds)
            compareIdReferenceCollections(
                ProtectionRelayFunction::protectedSwitches,
                ProtectionRelayFunction::sensors,
                ProtectionRelayFunction::schemes
            )
        }

    private fun compareProtectionRelayScheme(source: ProtectionRelayScheme, target: ProtectionRelayScheme) =
        ObjectDifference(source, target).apply {
            compareIdentifiedObject()

            compareIdReferences(ProtectionRelayScheme::system)
            compareIdReferenceCollections(ProtectionRelayScheme::functions)
        }

    private fun compareProtectionRelaySystem(source: ProtectionRelaySystem, target: ProtectionRelaySystem) =
        ObjectDifference(source, target).apply {
            compareEquipment()

            compareValues(ProtectionRelaySystem::protectionKind)
            compareIdReferenceCollections(ProtectionRelaySystem::schemes)
        }

    private fun compareVoltageRelay(source: VoltageRelay, target: VoltageRelay) =
        ObjectDifference(source, target).apply {
            compareProtectionRelayFunction()
        }

    /************ IEC61970 BASE SCADA ************/

    private fun compareRemoteControl(source: RemoteControl, target: RemoteControl): ObjectDifference<RemoteControl> =
        ObjectDifference(source, target).apply {
            compareRemotePoint()

            compareIdReferences(RemoteControl::control)
        }

    private fun ObjectDifference<out RemotePoint>.compareRemotePoint(): ObjectDifference<out RemotePoint> =
        apply { compareIdentifiedObject() }

    private fun compareRemoteSource(source: RemoteSource, target: RemoteSource): ObjectDifference<RemoteSource> =
        ObjectDifference(source, target).apply {
            compareRemotePoint()

            compareIdReferences(RemoteSource::measurement)
        }

    /************ IEC61970 BASE WIRES GENERATION PRODUCTION ************/

    private fun compareBatteryUnit(source: BatteryUnit, target: BatteryUnit): ObjectDifference<BatteryUnit> =
        ObjectDifference(source, target).apply {
            comparePowerElectronicsUnit()

            compareValues(BatteryUnit::batteryState, BatteryUnit::ratedE, BatteryUnit::storedE)
        }

    private fun comparePhotoVoltaicUnit(source: PhotoVoltaicUnit, target: PhotoVoltaicUnit): ObjectDifference<PhotoVoltaicUnit> =
        ObjectDifference(source, target).apply {
            comparePowerElectronicsUnit()
        }

    private fun ObjectDifference<out PowerElectronicsUnit>.comparePowerElectronicsUnit(): ObjectDifference<out PowerElectronicsUnit> =
        apply {
            compareEquipment()

            compareIdReferences(PowerElectronicsUnit::powerElectronicsConnection)
            compareValues(PowerElectronicsUnit::maxP, PowerElectronicsUnit::minP)
        }

    private fun comparePowerElectronicsWindUnit(
        source: PowerElectronicsWindUnit,
        target: PowerElectronicsWindUnit
    ): ObjectDifference<PowerElectronicsWindUnit> =
        ObjectDifference(source, target).apply {
            comparePowerElectronicsUnit()
        }

    /************ IEC61970 BASE WIRES ************/

    private fun compareAcLineSegment(source: AcLineSegment, target: AcLineSegment): ObjectDifference<AcLineSegment> =
        ObjectDifference(source, target).apply {
            compareConductor()

            compareIdReferences(AcLineSegment::perLengthSequenceImpedance)
        }

    private fun compareBreaker(source: Breaker, target: Breaker): ObjectDifference<Breaker> =
        ObjectDifference(source, target).apply {
            compareProtectedSwitch()

            compareValues(Breaker::inTransitTime)
        }

    private fun compareLoadBreakSwitch(source: LoadBreakSwitch, target: LoadBreakSwitch): ObjectDifference<LoadBreakSwitch> =
        ObjectDifference(source, target).apply { compareProtectedSwitch() }

    private fun compareBusbarSection(source: BusbarSection, target: BusbarSection): ObjectDifference<BusbarSection> =
        ObjectDifference(source, target).apply { compareConnector() }

    private fun ObjectDifference<out Conductor>.compareConductor(): ObjectDifference<out Conductor> =
        apply {
            compareConductingEquipment()

            compareValues(Conductor::length, Conductor::designTemperature, Conductor::designRating)
        }

    private fun ObjectDifference<out Connector>.compareConnector(): ObjectDifference<out Connector> =
        apply { compareConductingEquipment() }

    private fun compareDisconnector(source: Disconnector, target: Disconnector): ObjectDifference<Disconnector> =
        ObjectDifference(source, target).apply { compareSwitch() }

    private fun ObjectDifference<out EarthFaultCompensator>.compareEarthFaultCompensator(): ObjectDifference<out EarthFaultCompensator> =
        apply {
            compareConductingEquipment()

            compareValues(EarthFaultCompensator::r)
        }

    private fun ObjectDifference<out EnergyConnection>.compareEnergyConnection(): ObjectDifference<out EnergyConnection> =
        apply { compareConductingEquipment() }

    private fun compareEnergyConsumer(source: EnergyConsumer, target: EnergyConsumer): ObjectDifference<EnergyConsumer> =
        ObjectDifference(source, target).apply {
            compareEnergyConnection()

            compareIdReferenceCollections(EnergyConsumer::phases)
            compareValues(
                EnergyConsumer::customerCount,
                EnergyConsumer::grounded,
                EnergyConsumer::p,
                EnergyConsumer::pFixed,
                EnergyConsumer::phaseConnection,
                EnergyConsumer::q,
                EnergyConsumer::qFixed
            )
        }

    private fun compareEnergyConsumerPhase(source: EnergyConsumerPhase, target: EnergyConsumerPhase): ObjectDifference<EnergyConsumerPhase> =
        ObjectDifference(source, target).apply {
            comparePowerSystemResource()

            compareIdReferences(EnergyConsumerPhase::energyConsumer)
            compareValues(EnergyConsumerPhase::phase, EnergyConsumerPhase::p, EnergyConsumerPhase::pFixed, EnergyConsumerPhase::q, EnergyConsumerPhase::qFixed)
        }

    private fun compareEnergySource(source: EnergySource, target: EnergySource): ObjectDifference<EnergySource> =
        ObjectDifference(source, target).apply {
            compareEnergyConnection()

            compareIdReferenceCollections(EnergySource::phases)
            compareValues(
                EnergySource::activePower,
                EnergySource::reactivePower,
                EnergySource::voltageAngle,
                EnergySource::voltageMagnitude,
                EnergySource::pMax,
                EnergySource::pMin,
                EnergySource::r,
                EnergySource::r0,
                EnergySource::rn,
                EnergySource::x,
                EnergySource::x0,
                EnergySource::xn,
                EnergySource::isExternalGrid,
                EnergySource::rMin,
                EnergySource::rnMin,
                EnergySource::r0Min,
                EnergySource::xMin,
                EnergySource::xnMin,
                EnergySource::x0Min,
                EnergySource::rMax,
                EnergySource::rnMax,
                EnergySource::r0Max,
                EnergySource::xMax,
                EnergySource::xnMax,
                EnergySource::x0Max
            )
        }

    private fun compareEnergySourcePhase(source: EnergySourcePhase, target: EnergySourcePhase): ObjectDifference<EnergySourcePhase> =
        ObjectDifference(source, target).apply {
            comparePowerSystemResource()

            compareIdReferences(EnergySourcePhase::energySource)
            compareValues(EnergySourcePhase::phase)
        }

    private fun compareFuse(source: Fuse, target: Fuse): ObjectDifference<Fuse> =
        ObjectDifference(source, target).apply {
            compareSwitch()

            compareIdReferences(Fuse::function)
        }

    private fun compareGround(source: Ground, target: Ground): ObjectDifference<Ground> =
        ObjectDifference(source, target).apply {
            compareConductingEquipment()
        }

    private fun compareGroundDisconnector(source: GroundDisconnector, target: GroundDisconnector): ObjectDifference<GroundDisconnector> =
        ObjectDifference(source, target).apply {
            compareSwitch()
        }

    private fun compareGroundingImpedance(source: GroundingImpedance, target: GroundingImpedance): ObjectDifference<GroundingImpedance> =
        ObjectDifference(source, target).apply {
            compareEarthFaultCompensator()

            compareValues(GroundingImpedance::x)
        }

    private fun compareJumper(source: Jumper, target: Jumper): ObjectDifference<Jumper> =
        ObjectDifference(source, target).apply { compareSwitch() }

    private fun compareJunction(source: Junction, target: Junction): ObjectDifference<Junction> =
        ObjectDifference(source, target).apply { compareConnector() }

    private fun ObjectDifference<out Line>.compareLine(): ObjectDifference<out Line> =
        apply {
            compareEquipmentContainer()
        }

    private fun compareLinearShuntCompensator(source: LinearShuntCompensator, target: LinearShuntCompensator): ObjectDifference<LinearShuntCompensator> =
        ObjectDifference(source, target).apply {
            compareShuntCompensator()

            compareValues(
                LinearShuntCompensator::b0PerSection,
                LinearShuntCompensator::bPerSection,
                LinearShuntCompensator::g0PerSection,
                LinearShuntCompensator::gPerSection
            )
        }

    private fun ObjectDifference<out PerLengthImpedance>.comparePerLengthImpedance(): ObjectDifference<out PerLengthImpedance> =
        apply { comparePerLengthLineParameter() }

    private fun ObjectDifference<out PerLengthLineParameter>.comparePerLengthLineParameter(): ObjectDifference<out PerLengthLineParameter> =
        apply { compareIdentifiedObject() }

    private fun comparePerLengthSequenceImpedance(
        source: PerLengthSequenceImpedance,
        target: PerLengthSequenceImpedance
    ): ObjectDifference<PerLengthSequenceImpedance> =
        ObjectDifference(source, target).apply {
            comparePerLengthImpedance()

            compareValues(
                PerLengthSequenceImpedance::r,
                PerLengthSequenceImpedance::x,
                PerLengthSequenceImpedance::bch,
                PerLengthSequenceImpedance::gch,
                PerLengthSequenceImpedance::r0,
                PerLengthSequenceImpedance::x0,
                PerLengthSequenceImpedance::b0ch,
                PerLengthSequenceImpedance::g0ch
            )
        }

    private fun comparePetersenCoil(source: PetersenCoil, target: PetersenCoil): ObjectDifference<PetersenCoil> =
        ObjectDifference(source, target).apply {
            compareEarthFaultCompensator()

            compareValues(PetersenCoil::xGroundNominal)
        }

    private fun comparePowerElectronicsConnection(
        source: PowerElectronicsConnection,
        target: PowerElectronicsConnection
    ): ObjectDifference<PowerElectronicsConnection> =
        ObjectDifference(source, target).apply {
            compareRegulatingCondEq()

            compareIdReferenceCollections(PowerElectronicsConnection::units, PowerElectronicsConnection::phases)
            compareValues(
                PowerElectronicsConnection::maxIFault,
                PowerElectronicsConnection::maxQ,
                PowerElectronicsConnection::minQ,
                PowerElectronicsConnection::p,
                PowerElectronicsConnection::q,
                PowerElectronicsConnection::ratedS,
                PowerElectronicsConnection::ratedU,
                PowerElectronicsConnection::inverterStandard,
                PowerElectronicsConnection::sustainOpOvervoltLimit,
                PowerElectronicsConnection::stopAtOverFreq,
                PowerElectronicsConnection::stopAtUnderFreq,
                PowerElectronicsConnection::invVoltWattRespMode,
                PowerElectronicsConnection::invWattRespV1,
                PowerElectronicsConnection::invWattRespV2,
                PowerElectronicsConnection::invWattRespV3,
                PowerElectronicsConnection::invWattRespV4,
                PowerElectronicsConnection::invWattRespPAtV1,
                PowerElectronicsConnection::invWattRespPAtV2,
                PowerElectronicsConnection::invWattRespPAtV3,
                PowerElectronicsConnection::invWattRespPAtV4,
                PowerElectronicsConnection::invVoltVarRespMode,
                PowerElectronicsConnection::invVarRespV1,
                PowerElectronicsConnection::invVarRespV2,
                PowerElectronicsConnection::invVarRespV3,
                PowerElectronicsConnection::invVarRespV4,
                PowerElectronicsConnection::invVarRespQAtV1,
                PowerElectronicsConnection::invVarRespQAtV2,
                PowerElectronicsConnection::invVarRespQAtV3,
                PowerElectronicsConnection::invVarRespQAtV4,
                PowerElectronicsConnection::invReactivePowerMode,
                PowerElectronicsConnection::invFixReactivePower
            )
        }

    private fun comparePowerElectronicsConnectionPhase(
        source: PowerElectronicsConnectionPhase,
        target: PowerElectronicsConnectionPhase
    ): ObjectDifference<PowerElectronicsConnectionPhase> =
        ObjectDifference(source, target).apply {
            comparePowerSystemResource()

            compareIdReferences(PowerElectronicsConnectionPhase::powerElectronicsConnection)
            compareValues(PowerElectronicsConnectionPhase::p, PowerElectronicsConnectionPhase::phase, PowerElectronicsConnectionPhase::q)
        }

    private fun comparePowerTransformer(source: PowerTransformer, target: PowerTransformer): ObjectDifference<PowerTransformer> =
        ObjectDifference(source, target).apply {
            compareConductingEquipment()

            compareIndexedIdReferenceCollections(PowerTransformer::ends)
            compareValues(
                PowerTransformer::vectorGroup,
                PowerTransformer::transformerUtilisation,
                PowerTransformer::constructionKind,
                PowerTransformer::function
            )
        }

    private fun comparePowerTransformerEnd(source: PowerTransformerEnd, target: PowerTransformerEnd): ObjectDifference<PowerTransformerEnd> =
        ObjectDifference(source, target).apply {
            compareTransformerEnd()

            compareIdReferences(PowerTransformerEnd::powerTransformer)
            compareValues(
                PowerTransformerEnd::b,
                PowerTransformerEnd::b0,
                PowerTransformerEnd::connectionKind,
                PowerTransformerEnd::g,
                PowerTransformerEnd::g0,
                PowerTransformerEnd::phaseAngleClock,
                PowerTransformerEnd::r,
                PowerTransformerEnd::r0,
                PowerTransformerEnd::ratedS,
                PowerTransformerEnd::ratedU,
                PowerTransformerEnd::x,
                PowerTransformerEnd::x0
            )

            compareIndexedValueCollections(PowerTransformerEnd::sRatings)
        }

    private fun ObjectDifference<out ProtectedSwitch>.compareProtectedSwitch(): ObjectDifference<out ProtectedSwitch> =
        apply {
            compareSwitch()

            compareValues(ProtectedSwitch::breakingCapacity)
            compareIdReferenceCollections(ProtectedSwitch::relayFunctions)
        }

    private fun compareRatioTapChanger(source: RatioTapChanger, target: RatioTapChanger): ObjectDifference<RatioTapChanger> =
        ObjectDifference(source, target).apply {
            compareTapChanger()

            compareIdReferences(RatioTapChanger::transformerEnd)
            compareValues(RatioTapChanger::stepVoltageIncrement)
        }

    private fun compareRecloser(source: Recloser, target: Recloser): ObjectDifference<Recloser> =
        ObjectDifference(source, target).apply { compareProtectedSwitch() }

    private fun ObjectDifference<out RegulatingCondEq>.compareRegulatingCondEq(): ObjectDifference<out RegulatingCondEq> =
        apply {
            compareEnergyConnection()

            compareValues(RegulatingCondEq::controlEnabled)
            compareIdReferences(RegulatingCondEq::regulatingControl)
        }

    private fun ObjectDifference<out RegulatingControl>.compareRegulatingControl(): ObjectDifference<out RegulatingControl> =
        apply {
            comparePowerSystemResource()

            compareValues(
                RegulatingControl::discrete,
                RegulatingControl::mode,
                RegulatingControl::monitoredPhase,
                RegulatingControl::targetDeadband,
                RegulatingControl::targetValue,
                RegulatingControl::enabled,
                RegulatingControl::maxAllowedTargetValue,
                RegulatingControl::minAllowedTargetValue,
                RegulatingControl::ratedCurrent
            )
            compareIdReferences(RegulatingControl::terminal)
            compareIdReferenceCollections(RegulatingControl::regulatingCondEqs)
        }

    private fun compareReactiveCapabilityCurve(source: ReactiveCapabilityCurve, target: ReactiveCapabilityCurve): ObjectDifference<ReactiveCapabilityCurve> =
        ObjectDifference(source, target).apply { compareCurve() }

    private fun ObjectDifference<out RotatingMachine>.compareRotatingMachine(): ObjectDifference<out RotatingMachine> =
        apply {
            compareRegulatingCondEq()

            compareValues(RotatingMachine::ratedPowerFactor, RotatingMachine::ratedS, RotatingMachine::ratedU, RotatingMachine::p, RotatingMachine::q)
        }

    private fun compareSeriesCompensator(source: SeriesCompensator, target: SeriesCompensator): ObjectDifference<SeriesCompensator> =
        ObjectDifference(source, target).apply {
            compareConductingEquipment()

            compareValues(
                SeriesCompensator::r,
                SeriesCompensator::r0,
                SeriesCompensator::x,
                SeriesCompensator::x0,
                SeriesCompensator::varistorRatedCurrent,
                SeriesCompensator::varistorVoltageThreshold
            )
        }

    private fun ObjectDifference<out ShuntCompensator>.compareShuntCompensator(): ObjectDifference<out ShuntCompensator> =
        apply {
            compareRegulatingCondEq()

            compareValues(ShuntCompensator::grounded, ShuntCompensator::nomU, ShuntCompensator::phaseConnection, ShuntCompensator::sections)
        }

    private fun ObjectDifference<out Switch>.compareSwitch(): ObjectDifference<out Switch> =
        apply {
            compareConductingEquipment()

            compareValues(Switch::ratedCurrent)
            addIfDifferent("isNormallyOpen", compareOpenStatus(source, target, Switch::isNormallyOpen))
            addIfDifferent("isOpen", compareOpenStatus(source, target, Switch::isOpen))
        }

    private fun ObjectDifference<out TapChanger>.compareTapChanger(): ObjectDifference<out TapChanger> =
        apply {
            comparePowerSystemResource()

            compareValues(
                TapChanger::controlEnabled,
                TapChanger::neutralU,
                TapChanger::highStep,
                TapChanger::lowStep,
                TapChanger::neutralStep,
                TapChanger::normalStep,
                TapChanger::step
            )
            compareIdReferences(TapChanger::tapChangerControl)
        }

    private fun compareSynchronousMachine(source: SynchronousMachine, target: SynchronousMachine): ObjectDifference<SynchronousMachine> =
        ObjectDifference(source, target).apply {
            compareRotatingMachine()

            compareValues(
                SynchronousMachine::baseQ,
                SynchronousMachine::condenserP,
                SynchronousMachine::earthing,
                SynchronousMachine::earthingStarPointR,
                SynchronousMachine::earthingStarPointX,
                SynchronousMachine::ikk,
                SynchronousMachine::maxQ,
                SynchronousMachine::maxU,
                SynchronousMachine::minQ,
                SynchronousMachine::minU,
                SynchronousMachine::mu,
                SynchronousMachine::r,
                SynchronousMachine::r0,
                SynchronousMachine::r2,
                SynchronousMachine::satDirectSubtransX,
                SynchronousMachine::satDirectSyncX,
                SynchronousMachine::satDirectTransX,
                SynchronousMachine::x0,
                SynchronousMachine::x2,
                SynchronousMachine::type,
                SynchronousMachine::operatingMode
            )
            compareIdReferenceCollections(SynchronousMachine::curves)
        }

    private fun compareTapChangerControl(source: TapChangerControl, target: TapChangerControl): ObjectDifference<TapChangerControl> =
        ObjectDifference(source, target).apply {
            compareRegulatingControl()

            compareValues(
                TapChangerControl::limitVoltage,
                TapChangerControl::lineDropCompensation,
                TapChangerControl::lineDropR,
                TapChangerControl::lineDropX,
                TapChangerControl::reverseLineDropR,
                TapChangerControl::reverseLineDropX,
                TapChangerControl::forwardLDCBlocking,
                TapChangerControl::timeDelay,
                TapChangerControl::coGenerationEnabled
            )
        }

    private fun ObjectDifference<out TransformerEnd>.compareTransformerEnd(): ObjectDifference<out TransformerEnd> =
        apply {
            compareIdentifiedObject()

            compareValues(TransformerEnd::grounded, TransformerEnd::rGround, TransformerEnd::xGround, TransformerEnd::endNumber)
            compareIdReferences(TransformerEnd::baseVoltage, TransformerEnd::ratioTapChanger, TransformerEnd::terminal, TransformerEnd::starImpedance)
        }

    private fun compareTransformerStarImpedance(
        source: TransformerStarImpedance,
        target: TransformerStarImpedance
    ): ObjectDifference<TransformerStarImpedance> =
        ObjectDifference(source, target).apply {
            compareIdentifiedObject()

            compareValues(TransformerStarImpedance::r, TransformerStarImpedance::r0, TransformerStarImpedance::x, TransformerStarImpedance::x0)
            compareIdReferences(TransformerStarImpedance::transformerEndInfo)
        }

    /************ IEC61970 InfIEC61970 Feeder ************/

    private fun compareCircuit(source: Circuit, target: Circuit): ObjectDifference<Circuit> =
        ObjectDifference(source, target).apply {
            compareLine()

            compareIdReferences(Circuit::loop)
            compareIdReferenceCollections(Circuit::endTerminals, Circuit::endSubstations)
        }

    private fun compareLoop(source: Loop, target: Loop): ObjectDifference<Loop> =
        ObjectDifference(source, target).apply {
            compareIdentifiedObject()

            compareIdReferenceCollections(Loop::circuits, Loop::substations, Loop::energizingSubstations)
        }

    private fun compareLvFeeder(source: LvFeeder, target: LvFeeder): ObjectDifference<LvFeeder> =
        ObjectDifference(source, target).apply {
            compareEquipmentContainer()

            compareIdReferences(LvFeeder::normalHeadTerminal)
            compareIdReferenceCollections(LvFeeder::normalEnergizingFeeders)
            if (options.compareFeederEquipment)
                compareIdReferenceCollections(LvFeeder::currentEquipment)
        }

    /************ IEC61970 InfIEC61970 WIRES GENERATION PRODUCTION ************/

    private fun compareEvChargingUnit(source: EvChargingUnit, target: EvChargingUnit): ObjectDifference<EvChargingUnit> =
        ObjectDifference(source, target).apply {
            comparePowerElectronicsUnit()
        }

    private fun compareOpenStatus(source: Switch, target: Switch, openTest: (Switch, SinglePhaseKind) -> Boolean): ValueDifference? {
        val sourceStatus = PhaseCode.ABCN.singlePhases.associateWith { openTest(source, it) }
        val targetStatus = PhaseCode.ABCN.singlePhases.associateWith { openTest(target, it) }

        return if (sourceStatus != targetStatus) {
            ValueDifference(sourceStatus, targetStatus)
        } else {
            null
        }
    }

}

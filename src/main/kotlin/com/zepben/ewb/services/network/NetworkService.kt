/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.network

import com.zepben.ewb.cim.extensions.iec61968.assetinfo.RelayInfo
import com.zepben.ewb.cim.extensions.iec61968.metering.PanDemandResponseFunction
import com.zepben.ewb.cim.extensions.iec61970.base.core.Site
import com.zepben.ewb.cim.extensions.iec61970.base.feeder.Loop
import com.zepben.ewb.cim.extensions.iec61970.base.feeder.LvFeeder
import com.zepben.ewb.cim.extensions.iec61970.base.generation.production.EvChargingUnit
import com.zepben.ewb.cim.extensions.iec61970.base.protection.DistanceRelay
import com.zepben.ewb.cim.extensions.iec61970.base.protection.ProtectionRelayScheme
import com.zepben.ewb.cim.extensions.iec61970.base.protection.ProtectionRelaySystem
import com.zepben.ewb.cim.extensions.iec61970.base.protection.VoltageRelay
import com.zepben.ewb.cim.extensions.iec61970.base.wires.BatteryControl
import com.zepben.ewb.cim.iec61968.assetinfo.*
import com.zepben.ewb.cim.iec61968.assets.AssetOwner
import com.zepben.ewb.cim.iec61968.assets.Streetlight
import com.zepben.ewb.cim.iec61968.common.Location
import com.zepben.ewb.cim.iec61968.common.Organisation
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
import com.zepben.ewb.cim.iec61970.base.meas.*
import com.zepben.ewb.cim.iec61970.base.protection.CurrentRelay
import com.zepben.ewb.cim.iec61970.base.scada.RemoteControl
import com.zepben.ewb.cim.iec61970.base.scada.RemoteSource
import com.zepben.ewb.cim.iec61970.base.wires.*
import com.zepben.ewb.cim.iec61970.infiec61970.feeder.Circuit
import com.zepben.ewb.services.common.BaseService
import com.zepben.ewb.services.common.meta.MetadataCollection
import com.zepben.ewb.services.network.tracing.connectivity.ConnectivityResult
import com.zepben.ewb.services.network.tracing.connectivity.TerminalConnectivityConnected
import kotlin.reflect.KClass

/**
 * Maintains an in-memory model of the network.
 */
class NetworkService(metadata: MetadataCollection = MetadataCollection()) : BaseService("network", metadata) {

    private enum class ProcessStatus {
        PROCESSED, SKIPPED, INVALID
    }

    private var autoConnectivityNodeIndex = 0

    @Suppress("UNCHECKED_CAST")
    private val _connectivityNodes: MutableMap<String, ConnectivityNode> = objectsByType.computeIfAbsent(ConnectivityNode::class) {
        mutableMapOf()
    } as MutableMap<String, ConnectivityNode>

    private val _measurements: MutableMap<String, MutableList<Measurement>> = mutableMapOf()

    // ##################################
    // # Extensions IEC61968 Asset Info #
    // ##################################

    fun add(relayInfo: RelayInfo): Boolean = super.add(relayInfo)
    fun remove(relayInfo: RelayInfo): Boolean = super.remove(relayInfo)

    // ################################
    // # Extensions IEC61968 Metering #
    // ################################

    fun add(panDemandResponseFunction: PanDemandResponseFunction): Boolean = super.add(panDemandResponseFunction)
    fun remove(panDemandResponseFunction: PanDemandResponseFunction, cascade: Boolean = false): Boolean = super.removeInternal(panDemandResponseFunction, cascade)

    // #################################
    // # Extensions IEC61970 Base Core #
    // #################################

    fun add(site: Site): Boolean = super.add(site)
    fun remove(site: Site): Boolean = super.remove(site)

    // ###################################
    // # Extensions IEC61970 Base Feeder #
    // ###################################

    fun add(loop: Loop): Boolean = super.add(loop)
    fun remove(loop: Loop): Boolean = super.remove(loop)

    fun add(lvFeeder: LvFeeder): Boolean = super.add(lvFeeder)
    fun remove(lvFeeder: LvFeeder): Boolean = super.remove(lvFeeder)

    // ##################################################
    // # Extensions IEC61970 Base Generation Production #
    // ##################################################

    fun add(evChargingUnit: EvChargingUnit): Boolean = super.add(evChargingUnit)
    fun remove(evChargingUnit: EvChargingUnit): Boolean = super.remove(evChargingUnit)

    // #######################################
    // # Extensions IEC61970 Base Protection #
    // #######################################

    fun add(distanceRelay: DistanceRelay): Boolean = super.add(distanceRelay)
    fun remove(distanceRelay: DistanceRelay): Boolean = super.remove(distanceRelay)

    fun add(protectionRelayScheme: ProtectionRelayScheme): Boolean = super.add(protectionRelayScheme)
    fun remove(protectionRelayScheme: ProtectionRelayScheme): Boolean = super.remove(protectionRelayScheme)

    fun add(protectionRelaySystem: ProtectionRelaySystem): Boolean = super.add(protectionRelaySystem)
    fun remove(protectionRelaySystem: ProtectionRelaySystem): Boolean = super.remove(protectionRelaySystem)

    fun add(voltageRelay: VoltageRelay): Boolean = super.add(voltageRelay)
    fun remove(voltageRelay: VoltageRelay): Boolean = super.remove(voltageRelay)

    // ##################################
    // # Extensions IEC61970 Base Wires #
    // ##################################

    fun add(batteryControl: BatteryControl): Boolean = super.add(batteryControl)
    fun remove(batteryControl: BatteryControl, cascade: Boolean = false): Boolean = super.removeInternal(batteryControl, cascade)

    // #######################
    // # IEC61968 Asset Info #
    // #######################

    fun add(cableInfo: CableInfo): Boolean = super.add(cableInfo)
    fun remove(cableInfo: CableInfo, cascade: Boolean = false): Boolean = super.removeInternal(cableInfo, cascade)

    fun add(noLoadTest: NoLoadTest): Boolean = super.add(noLoadTest)
    fun remove(noLoadTest: NoLoadTest, cascade: Boolean = false): Boolean = super.removeInternal(noLoadTest, cascade)

    fun add(openCircuitTest: OpenCircuitTest): Boolean = super.add(openCircuitTest)
    fun remove(openCircuitTest: OpenCircuitTest, cascade: Boolean = false): Boolean = super.removeInternal(openCircuitTest, cascade)

    fun add(overheadWireInfo: OverheadWireInfo): Boolean = super.add(overheadWireInfo)
    fun remove(overheadWireInfo: OverheadWireInfo, cascade: Boolean = false): Boolean = super.removeInternal(overheadWireInfo, cascade)

    fun add(powerTransformerInfo: PowerTransformerInfo): Boolean = super.add(powerTransformerInfo)
    fun remove(powerTransformerInfo: PowerTransformerInfo, cascade: Boolean = false): Boolean = super.removeInternal(powerTransformerInfo, cascade)

    fun add(shortCircuitTest: ShortCircuitTest): Boolean = super.add(shortCircuitTest)
    fun remove(shortCircuitTest: ShortCircuitTest, cascade: Boolean = false): Boolean = super.removeInternal(shortCircuitTest, cascade)

    fun add(shuntCompensatorInfo: ShuntCompensatorInfo): Boolean = super.add(shuntCompensatorInfo)
    fun remove(shuntCompensatorInfo: ShuntCompensatorInfo, cascade: Boolean = false): Boolean = super.removeInternal(shuntCompensatorInfo, cascade)

    fun add(switchInfo: SwitchInfo): Boolean = super.add(switchInfo)
    fun remove(switchInfo: SwitchInfo, cascade: Boolean = false): Boolean = super.removeInternal(switchInfo, cascade)

    fun add(transformerEndInfo: TransformerEndInfo): Boolean = super.add(transformerEndInfo)
    fun remove(transformerEndInfo: TransformerEndInfo, cascade: Boolean = false): Boolean = super.removeInternal(transformerEndInfo, cascade)

    fun add(transformerTankInfo: TransformerTankInfo): Boolean = super.add(transformerTankInfo)
    fun remove(transformerTankInfo: TransformerTankInfo, cascade: Boolean = false): Boolean = super.removeInternal(transformerTankInfo, cascade)

    // ###################
    // # IEC61968 Assets #
    // ###################

    fun add(assetOwner: AssetOwner): Boolean = super.add(assetOwner)
    fun remove(assetOwner: AssetOwner, cascade: Boolean = false): Boolean = super.removeInternal(assetOwner, cascade)

    fun add(pole: Pole): Boolean = super.add(pole)
    fun remove(pole: Pole, cascade: Boolean = false): Boolean = super.removeInternal(pole, cascade)

    fun add(streetlight: Streetlight): Boolean = super.add(streetlight)
    fun remove(streetlight: Streetlight, cascade: Boolean = false): Boolean = super.removeInternal(streetlight, cascade)

    // ###################
    // # IEC61968 Common #
    // ###################

    fun add(location: Location): Boolean = super.add(location)
    fun remove(location: Location, cascade: Boolean = false): Boolean = super.removeInternal(location, cascade)

    fun add(organisation: Organisation): Boolean = super.add(organisation)
    fun remove(organisation: Organisation, cascade: Boolean = false): Boolean = super.removeInternal(organisation, cascade)

    // #####################################
    // # IEC61968 infIEC61968 InfAssetInfo #
    // #####################################

    fun add(relayInfo: RelayInfo): Boolean = super.add(relayInfo)
    fun remove(relayInfo: RelayInfo, cascade: Boolean = false): Boolean = super.removeInternal(relayInfo, cascade)

    fun add(currentTransformerInfo: CurrentTransformerInfo): Boolean = super.add(currentTransformerInfo)
    fun remove(currentTransformerInfo: CurrentTransformerInfo, cascade: Boolean = false): Boolean = super.removeInternal(currentTransformerInfo, cascade)

    fun add(potentialTransformerInfo: PotentialTransformerInfo): Boolean = super.add(potentialTransformerInfo)
    fun remove(potentialTransformerInfo: PotentialTransformerInfo, cascade: Boolean = false): Boolean = super.removeInternal(potentialTransformerInfo, cascade)

    // ##################################
    // # IEC61968 infIEC61968 InfAssets #
    // ##################################

    fun add(pole: Pole): Boolean = super.add(pole)
    fun remove(pole: Pole): Boolean = super.remove(pole)

    // #####################
    // # IEC61968 Metering #
    // #####################

    fun add(meter: Meter): Boolean = super.add(meter)
    fun remove(meter: Meter, cascade: Boolean = false): Boolean = super.removeInternal(meter, cascade)

    fun add(usagePoint: UsagePoint): Boolean = super.add(usagePoint)
    fun remove(usagePoint: UsagePoint, cascade: Boolean = false): Boolean = super.removeInternal(usagePoint, cascade)

    // #######################
    // # IEC61968 Operations #
    // #######################

    fun add(operationalRestriction: OperationalRestriction): Boolean = super.add(operationalRestriction)
    fun remove(operationalRestriction: OperationalRestriction, cascade: Boolean = false): Boolean = super.removeInternal(operationalRestriction, cascade)

    // #####################################
    // # IEC61970 Base Auxiliary Equipment #
    // #####################################

    fun add(currentTransformer: CurrentTransformer): Boolean = super.add(currentTransformer)
    fun remove(currentTransformer: CurrentTransformer, cascade: Boolean = false): Boolean = super.removeInternal(currentTransformer, cascade)

    fun add(faultIndicator: FaultIndicator): Boolean = super.add(faultIndicator)
    fun remove(faultIndicator: FaultIndicator, cascade: Boolean = false): Boolean = super.removeInternal(faultIndicator, cascade)

    fun add(potentialTransformer: PotentialTransformer): Boolean = super.add(potentialTransformer)
    fun remove(potentialTransformer: PotentialTransformer, cascade: Boolean = false): Boolean = super.removeInternal(potentialTransformer, cascade)

    // ######################
    // # IEC61970 Base Core #
    // ######################

    fun add(baseVoltage: BaseVoltage): Boolean = super.add(baseVoltage)
    fun remove(baseVoltage: BaseVoltage, cascade: Boolean = false): Boolean = super.removeInternal(baseVoltage, cascade)

    fun add(connectivityNode: ConnectivityNode): Boolean = super.add(connectivityNode)
    fun remove(connectivityNode: ConnectivityNode, cascade: Boolean = false): Boolean = super.removeInternal(connectivityNode, cascade)

    fun add(feeder: Feeder): Boolean = super.add(feeder)
    fun remove(feeder: Feeder, cascade: Boolean = false): Boolean = super.removeInternal(feeder, cascade)

    fun add(geographicalRegion: GeographicalRegion): Boolean = super.add(geographicalRegion)
    fun remove(geographicalRegion: GeographicalRegion, cascade: Boolean = false): Boolean = super.removeInternal(geographicalRegion, cascade)

    fun add(site: Site): Boolean = super.add(site)
    fun remove(site: Site, cascade: Boolean = false): Boolean = super.removeInternal(site, cascade)

    fun add(subGeographicalRegion: SubGeographicalRegion): Boolean = super.add(subGeographicalRegion)
    fun remove(subGeographicalRegion: SubGeographicalRegion, cascade: Boolean = false): Boolean = super.removeInternal(subGeographicalRegion, cascade)

    fun add(substation: Substation): Boolean = super.add(substation)
    fun remove(substation: Substation, cascade: Boolean = false): Boolean = super.removeInternal(substation, cascade)

    fun add(terminal: Terminal): Boolean = super.add(terminal)
    fun remove(terminal: Terminal, cascade: Boolean = false): Boolean = super.removeInternal(terminal, cascade)

    // #############################
    // # IEC61970 Base Equivalents #
    // #############################

    fun add(equivalentBranch: EquivalentBranch): Boolean = super.add(equivalentBranch)
    fun remove(equivalentBranch: EquivalentBranch, cascade: Boolean = false): Boolean = super.removeInternal(equivalentBranch, cascade)

    // ######################
    // # IEC61970 Base Meas #
    // ######################

    fun add(accumulator: Accumulator): Boolean = indexMeasurement(accumulator) && super.add(accumulator)
    fun remove(accumulator: Accumulator, cascade: Boolean = false): Boolean {
        removeMeasurementIndex(accumulator)
        return super.removeInternal(accumulator, cascade)
    }

    fun add(analog: Analog): Boolean = indexMeasurement(analog) && super.add(analog)
    fun remove(analog: Analog, cascade: Boolean = false): Boolean {
        removeMeasurementIndex(analog)
        return super.removeInternal(analog, cascade)
    }

    fun add(control: Control): Boolean = super.add(control)
    fun remove(control: Control, cascade: Boolean = false): Boolean = super.removeInternal(control, cascade)

    fun add(discrete: Discrete): Boolean = indexMeasurement(discrete) && super.add(discrete)
    fun remove(discrete: Discrete, cascade: Boolean = false): Boolean {
        removeMeasurementIndex(discrete)
        return super.removeInternal(discrete, cascade)
    }

    // ############################
    // # IEC61970 Base Protection #
    // ############################

    fun add(currentRelay: CurrentRelay): Boolean = super.add(currentRelay)
    fun remove(currentRelay: CurrentRelay, cascade: Boolean = false): Boolean = super.removeInternal(currentRelay, cascade)

    fun add(distanceRelay: DistanceRelay): Boolean = super.add(distanceRelay)
    fun remove(distanceRelay: DistanceRelay, cascade: Boolean = false): Boolean = super.removeInternal(distanceRelay, cascade)

    fun add(protectionRelayScheme: ProtectionRelayScheme): Boolean = super.add(protectionRelayScheme)
    fun remove(protectionRelayScheme: ProtectionRelayScheme, cascade: Boolean = false): Boolean = super.removeInternal(protectionRelayScheme, cascade)

    fun add(protectionRelaySystem: ProtectionRelaySystem): Boolean = super.add(protectionRelaySystem)
    fun remove(protectionRelaySystem: ProtectionRelaySystem, cascade: Boolean = false): Boolean = super.removeInternal(protectionRelaySystem, cascade)

    fun add(voltageRelay: VoltageRelay): Boolean = super.add(voltageRelay)
    fun remove(voltageRelay: VoltageRelay, cascade: Boolean = false): Boolean = super.removeInternal(voltageRelay, cascade)

    // #######################
    // # IEC61970 Base Scada #
    // #######################

    fun add(remoteControl: RemoteControl): Boolean = super.add(remoteControl)
    fun remove(remoteControl: RemoteControl, cascade: Boolean = false): Boolean = super.removeInternal(remoteControl, cascade)

    fun add(remoteSource: RemoteSource): Boolean = super.add(remoteSource)
    fun remove(remoteSource: RemoteSource, cascade: Boolean = false): Boolean = super.removeInternal(remoteSource, cascade)

    // #######################################
    // # IEC61970 Base Generation Production #
    // #######################################

    fun add(batteryUnit: BatteryUnit): Boolean = super.add(batteryUnit)
    fun remove(batteryUnit: BatteryUnit, cascade: Boolean = false): Boolean = super.removeInternal(batteryUnit, cascade)

    fun add(photoVoltaicUnit: PhotoVoltaicUnit): Boolean = super.add(photoVoltaicUnit)
    fun remove(photoVoltaicUnit: PhotoVoltaicUnit, cascade: Boolean = false): Boolean = super.removeInternal(photoVoltaicUnit, cascade)

    fun add(powerElectronicsWindUnit: PowerElectronicsWindUnit): Boolean = super.add(powerElectronicsWindUnit)
    fun remove(powerElectronicsWindUnit: PowerElectronicsWindUnit): Boolean = super.removeInternal(powerElectronicsWindUnit)

    // #######################
    // # IEC61970 Base Wires #
    // #######################

    fun add(acLineSegment: AcLineSegment): Boolean = super.add(acLineSegment)
    fun remove(acLineSegment: AcLineSegment, cascade: Boolean = false): Boolean = super.removeInternal(acLineSegment, cascade)

    fun add(breaker: Breaker): Boolean = super.add(breaker)
    fun remove(breaker: Breaker, cascade: Boolean = false): Boolean = super.removeInternal(breaker, cascade)

    fun add(busbarSection: BusbarSection): Boolean = super.add(busbarSection)
    fun remove(busbarSection: BusbarSection, cascade: Boolean = false): Boolean = super.removeInternal(busbarSection, cascade)

    fun add(disconnector: Disconnector): Boolean = super.add(disconnector)
    fun remove(disconnector: Disconnector, cascade: Boolean = false): Boolean = super.removeInternal(disconnector, cascade)

    fun add(clamp: Clamp): Boolean = super.add(clamp)
    fun remove(clamp: Clamp, cascade: Boolean = false): Boolean = super.removeInternal(clamp, cascade)

    fun add(cut: Cut): Boolean = super.add(cut)
    fun remove(cut: Cut, cascade: Boolean = false): Boolean = super.removeInternal(cut, cascade)

    fun add(energyConsumer: EnergyConsumer): Boolean = super.add(energyConsumer)
    fun remove(energyConsumer: EnergyConsumer, cascade: Boolean = false): Boolean = super.removeInternal(energyConsumer, cascade)

    fun add(energyConsumerPhase: EnergyConsumerPhase): Boolean = super.add(energyConsumerPhase)
    fun remove(energyConsumerPhase: EnergyConsumerPhase, cascade: Boolean = false): Boolean = super.removeInternal(energyConsumerPhase, cascade)

    fun add(energySource: EnergySource): Boolean = super.add(energySource)
    fun remove(energySource: EnergySource, cascade: Boolean = false): Boolean = super.removeInternal(energySource, cascade)

    fun add(energySourcePhase: EnergySourcePhase): Boolean = super.add(energySourcePhase)
    fun remove(energySourcePhase: EnergySourcePhase, cascade: Boolean = false): Boolean = super.removeInternal(energySourcePhase, cascade)

    fun add(fuse: Fuse): Boolean = super.add(fuse)
    fun remove(fuse: Fuse, cascade: Boolean = false): Boolean = super.removeInternal(fuse, cascade)

    fun add(ground: Ground): Boolean = super.add(ground)
    fun remove(ground: Ground, cascade: Boolean = false): Boolean = super.removeInternal(ground, cascade)

    fun add(groundDisconnector: GroundDisconnector): Boolean = super.add(groundDisconnector)
    fun remove(groundDisconnector: GroundDisconnector, cascade: Boolean = false): Boolean = super.removeInternal(groundDisconnector, cascade)

    fun add(groundingImpedance: GroundingImpedance): Boolean = super.add(groundingImpedance)
    fun remove(groundingImpedance: GroundingImpedance, cascade: Boolean = false): Boolean = super.removeInternal(groundingImpedance, cascade)

    fun add(jumper: Jumper): Boolean = super.add(jumper)
    fun remove(jumper: Jumper, cascade: Boolean = false): Boolean = super.removeInternal(jumper, cascade)

    fun add(junction: Junction): Boolean = super.add(junction)
    fun remove(junction: Junction, cascade: Boolean = false): Boolean = super.removeInternal(junction, cascade)

    fun add(linearShuntCompensator: LinearShuntCompensator): Boolean = super.add(linearShuntCompensator)
    fun remove(linearShuntCompensator: LinearShuntCompensator, cascade: Boolean = false): Boolean = super.removeInternal(linearShuntCompensator, cascade)

    fun add(loadBreakSwitch: LoadBreakSwitch): Boolean = super.add(loadBreakSwitch)
    fun remove(loadBreakSwitch: LoadBreakSwitch, cascade: Boolean = false): Boolean = super.removeInternal(loadBreakSwitch, cascade)

    fun add(perLengthPhaseImpedance: PerLengthPhaseImpedance): Boolean = super.add(perLengthPhaseImpedance)
    fun remove(perLengthPhaseImpedance: PerLengthPhaseImpedance, cascade: Boolean = false): Boolean = super.removeInternal(perLengthPhaseImpedance, cascade)

    fun add(perLengthSequenceImpedance: PerLengthSequenceImpedance): Boolean = super.add(perLengthSequenceImpedance)
    fun remove(perLengthSequenceImpedance: PerLengthSequenceImpedance, cascade: Boolean = false): Boolean = super.removeInternal(perLengthSequenceImpedance, cascade)

    fun add(petersenCoil: PetersenCoil): Boolean = super.add(petersenCoil)
    fun remove(petersenCoil: PetersenCoil, cascade: Boolean = false): Boolean = super.removeInternal(petersenCoil, cascade)

    fun add(powerElectronicsConnection: PowerElectronicsConnection): Boolean = super.add(powerElectronicsConnection)
    fun remove(powerElectronicsConnection: PowerElectronicsConnection, cascade: Boolean = false): Boolean = super.removeInternal(powerElectronicsConnection, cascade)

    fun add(powerElectronicsConnectionPhase: PowerElectronicsConnectionPhase): Boolean = super.add(powerElectronicsConnectionPhase)
    fun remove(powerElectronicsConnectionPhase: PowerElectronicsConnectionPhase, cascade: Boolean = false): Boolean = super.removeInternal(powerElectronicsConnectionPhase, cascade)

    fun add(powerTransformer: PowerTransformer): Boolean = super.add(powerTransformer)
    fun remove(powerTransformer: PowerTransformer, cascade: Boolean = false): Boolean = super.removeInternal(powerTransformer, cascade)

    fun add(powerTransformerEnd: PowerTransformerEnd): Boolean = super.add(powerTransformerEnd)
    fun remove(powerTransformerEnd: PowerTransformerEnd, cascade: Boolean = false): Boolean = super.removeInternal(powerTransformerEnd, cascade)

    fun add(ratioTapChanger: RatioTapChanger): Boolean = super.add(ratioTapChanger)
    fun remove(ratioTapChanger: RatioTapChanger, cascade: Boolean = false): Boolean = super.removeInternal(ratioTapChanger, cascade)

    fun add(reactiveCapabilityCurve: ReactiveCapabilityCurve): Boolean = super.add(reactiveCapabilityCurve)
    fun remove(reactiveCapabilityCurve: ReactiveCapabilityCurve, cascade: Boolean = false): Boolean = super.removeInternal(reactiveCapabilityCurve, cascade)

    fun add(recloser: Recloser): Boolean = super.add(recloser)
    fun remove(recloser: Recloser, cascade: Boolean = false): Boolean = super.removeInternal(recloser, cascade)

    fun add(seriesCompensator: SeriesCompensator): Boolean = super.add(seriesCompensator)
    fun remove(seriesCompensator: SeriesCompensator, cascade: Boolean = false): Boolean = super.removeInternal(seriesCompensator, cascade)

    fun add(staticVarCompensator: StaticVarCompensator): Boolean = super.add(staticVarCompensator)
    fun remove(staticVarCompensator: StaticVarCompensator, cascade: Boolean = false): Boolean = super.removeInternal(staticVarCompensator, cascade)

    fun add(synchronousMachine: SynchronousMachine): Boolean = super.add(synchronousMachine)
    fun remove(synchronousMachine: SynchronousMachine, cascade: Boolean = false): Boolean = super.removeInternal(synchronousMachine, cascade)

    fun add(tapChangerControl: TapChangerControl): Boolean = super.add(tapChangerControl)
    fun remove(tapChangerControl: TapChangerControl, cascade: Boolean = false): Boolean = super.removeInternal(tapChangerControl, cascade)

    fun add(transformerStarImpedance: TransformerStarImpedance): Boolean = super.add(transformerStarImpedance)
    fun remove(transformerStarImpedance: TransformerStarImpedance, cascade: Boolean = false): Boolean = super.removeInternal(transformerStarImpedance, cascade)

    // ###############################
    // # IEC61970 InfIEC61970 Feeder #
    // ###############################

    fun add(circuit: Circuit): Boolean = super.add(circuit)
    fun remove(circuit: Circuit, cascade: Boolean = false): Boolean = super.removeInternal(circuit, cascade)

    fun add(loop: Loop): Boolean = super.add(loop)
    fun remove(loop: Loop, cascade: Boolean = false): Boolean = super.removeInternal(loop, cascade)

    fun add(lvFeeder: LvFeeder): Boolean = super.add(lvFeeder)
    fun remove(lvFeeder: LvFeeder, cascade: Boolean = false): Boolean = super.removeInternal(lvFeeder, cascade)

    // ###############################
    // # IEC61970 InfIEC61970 WIRES.GENERATION.PRODUCTION #
    // ###############################

    fun add(evChargingUnit: EvChargingUnit): Boolean = super.add(evChargingUnit)
    fun remove(evChargingUnit: EvChargingUnit, cascade: Boolean = false): Boolean = super.removeInternal(evChargingUnit, cascade)

    /**
     * Get all measurements of type [T] associated with the given [mRID].
     *
     * The [mRID] should be either a [PowerSystemResource] or a [Terminal] MRID that is assigned to the corresponding
     * fields on the measurements.
     */
    inline fun <reified T : Measurement> getMeasurements(mRID: String): List<T> = getMeasurements(mRID, T::class)

    /**
     * Get all measurements of type [measurementClass] associated with the given [mRID].
     *
     * The [mRID] should be either a [PowerSystemResource] or a [Terminal] MRID that is assigned to the corresponding
     * fields on the measurements.
     */
    fun <T : Measurement> getMeasurements(mRID: String, measurementClass: KClass<T>): List<T> =
        getMeasurements(mRID, measurementClass.java)

    /**
     * Get all measurements of type [measurementClass] associated with the given [mRID].
     *
     * The [mRID] should be either a [PowerSystemResource] or a [Terminal] MRID that is assigned to the corresponding
     * fields on the measurements.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun <T : Measurement> getMeasurements(mRID: String, measurementClass: Class<T>): List<T> =
        _measurements[mRID]?.filterIsInstance(measurementClass) ?: emptyList()

    private fun indexMeasurement(measurement: Measurement, mRID: String?): Boolean {
        if (mRID.isNullOrEmpty())
            return true

        _measurements[mRID]?.let { measurements ->
            measurements.find { m -> m.mRID == measurement.mRID } ?: run {
                measurements.add(measurement)
                return true
            }
        } ?: run {
            _measurements[mRID] = mutableListOf(measurement)
            return true
        }

        return false
    }

    private fun indexMeasurement(measurement: Measurement) =
        indexMeasurement(measurement, measurement.terminalMRID) && indexMeasurement(measurement, measurement.powerSystemResourceMRID)

    private fun removeMeasurementIndex(measurement: Measurement) {
        _measurements[measurement.terminalMRID]?.remove(measurement)
        _measurements[measurement.powerSystemResourceMRID]?.remove(measurement)
    }

    /**
     * Get a connectivityNode by its mRID, or create it if it doesn't already exist in the network.
     * @param mRID The mRID of the [ConnectivityNode]
     * @return The [ConnectivityNode]
     */
    fun getOrPutConnectivityNode(mRID: String): ConnectivityNode {
        return _connectivityNodes.getOrPut(mRID) { createConnectivityNode(mRID) }
    }

    fun connect(terminal1: Terminal, terminal2: Terminal): Boolean {
        val status = attemptToReuseConnection(terminal1, terminal2)
        if (status == ProcessStatus.PROCESSED) return true
        else if (status == ProcessStatus.INVALID) return false

        val connectivityNode = _connectivityNodes.computeIfAbsent(generateConnectivityNodeId()) { mRID: String -> createConnectivityNode(mRID) }
        connect(terminal2, connectivityNode)
        connect(terminal1, connectivityNode)

        return true
    }

    fun connect(terminal: Terminal, connectivityNodeId: String?): Boolean {
        if (connectivityNodeId.isNullOrBlank()) return false

        var connectivityNode = terminal.connectivityNode
        if (connectivityNode != null) return connectivityNodeId == connectivityNode.mRID

        connectivityNode = _connectivityNodes.computeIfAbsent(connectivityNodeId) { mRID: String -> createConnectivityNode(mRID) }
        connect(terminal, connectivityNode)
        return true
    }

    fun disconnect(terminal: Terminal) {
        val connectivityNode = terminal.connectivityNode ?: return
        connectivityNode.removeTerminal(terminal)
        terminal.disconnect()

        if (connectivityNode.numTerminals() == 0) _connectivityNodes.remove(connectivityNode.mRID)
    }

    fun disconnect(connectivityNodeId: String) {
        val connectivityNode = _connectivityNodes[connectivityNodeId] ?: return

        connectivityNode.terminals.forEach { it.disconnect() }
        connectivityNode.clearTerminals()

        _connectivityNodes.remove(connectivityNode.mRID)
    }

    private fun createConnectivityNode(mRID: String = ""): ConnectivityNode {
        return ConnectivityNode(mRID)
    }

    //
    // NOTE: This function is package private to allow test cases to check for connectivity nodes.
    //
    fun containsConnectivityNode(connectivityNode: String): Boolean {
        return _connectivityNodes.containsKey(connectivityNode)
    }

    private fun attemptToReuseConnection(terminal1: Terminal, terminal2: Terminal): ProcessStatus {
        val connectivityNode1 = terminal1.connectivityNode
        val connectivityNode2 = terminal2.connectivityNode
        if (connectivityNode1 != null) {
            if (connectivityNode2 != null) {
                if (connectivityNode1 == connectivityNode2) return ProcessStatus.PROCESSED
            } else if (connect(terminal2, connectivityNode1.mRID)) return ProcessStatus.PROCESSED
            return ProcessStatus.INVALID
        } else if (connectivityNode2 != null) {
            return if (connect(terminal1, connectivityNode2.mRID)) ProcessStatus.PROCESSED else ProcessStatus.INVALID
        }
        return ProcessStatus.SKIPPED
    }

    private fun generateConnectivityNodeId(): String {
        var id: String
        do {
            id = "generated_cn_" + autoConnectivityNodeIndex++
        } while (_connectivityNodes.containsKey(id))
        return id
    }

    private fun connect(terminal: Terminal, connectivityNode: ConnectivityNode) {
        terminal.connect(connectivityNode)
        connectivityNode.addTerminal(terminal)
    }

    companion object {

        /**
         * Find the connected [ConductingEquipment] for each [Terminal] of [conductingEquipment] using only the phases of the specified [phaseCode].
         *
         * @param conductingEquipment The [ConductingEquipment] to process.
         * @param phaseCode The [PhaseCode] specifying which phases should be used for the connectivity check.
         * @return A list of [ConnectivityResult] specifying the connections between [conductingEquipment] and the connected [ConductingEquipment]
         */
        @JvmStatic
        fun connectedEquipment(conductingEquipment: ConductingEquipment, phaseCode: PhaseCode): List<ConnectivityResult> =
            connectedEquipment(conductingEquipment, phaseCode.singlePhases.toSet())

        /**
         * Find the connected [ConductingEquipment] for each [Terminal] of [conductingEquipment] using only the specified [phases].
         *
         * @param conductingEquipment The [ConductingEquipment] to process.
         * @param phases A collection of [SinglePhaseKind] specifying which phases should be used for the connectivity check. If omitted,
         *               all valid phases will be used.
         * @return A list of [ConnectivityResult] specifying the connections between [conductingEquipment] and the connected [ConductingEquipment]
         */
        @JvmStatic
        @JvmOverloads
        fun connectedEquipment(conductingEquipment: ConductingEquipment, phases: Set<SinglePhaseKind>? = null): List<ConnectivityResult> =
            conductingEquipment.terminals.flatMap { connectedTerminals(it, phases ?: it.phases.singlePhases) }

        /**
         * Find the connected [Terminal]s for the specified [terminal] using only the phases of the specified [phaseCode].
         *
         * @param terminal The [Terminal] to process.
         * @param phaseCode The [PhaseCode] specifying which phases should be used for the connectivity check.
         * @return A list of [ConnectivityResult] specifying the connections between [terminal] and the connected [Terminal]s
         */
        @JvmStatic
        fun connectedTerminals(terminal: Terminal, phaseCode: PhaseCode): List<ConnectivityResult> =
            connectedTerminals(terminal, phaseCode.singlePhases)

        /**
         * Find the connected [Terminal]s for the specified [terminal] using only the specified [phases].
         *
         * @param terminal The [Terminal] to process.
         * @param phases A collection of [SinglePhaseKind] specifying which phases should be used for the connectivity check. If omitted,
         *               all valid phases will be used.
         * @return A list of [ConnectivityResult] specifying the connections between [terminal] and the connected [Terminal]s
         */
        @JvmStatic
        @JvmOverloads
        fun connectedTerminals(terminal: Terminal, phases: Iterable<SinglePhaseKind> = terminal.phases.singlePhases): List<ConnectivityResult> =
            TerminalConnectivityConnected.connectedTerminals(terminal, phases)

    }

}

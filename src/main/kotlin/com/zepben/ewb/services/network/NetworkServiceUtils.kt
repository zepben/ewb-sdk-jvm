/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */


@file:JvmName("NetworkServiceUtils")

package com.zepben.ewb.services.network

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
import com.zepben.ewb.cim.iec61968.common.Location
import com.zepben.ewb.cim.iec61968.common.Organisation
import com.zepben.ewb.cim.iec61968.infiec61968.infassetinfo.CurrentTransformerInfo
import com.zepben.ewb.cim.iec61968.infiec61968.infassetinfo.PotentialTransformerInfo
import com.zepben.ewb.cim.iec61968.infiec61968.infassets.Pole
import com.zepben.ewb.cim.iec61968.metering.Meter
import com.zepben.ewb.cim.iec61968.metering.UsagePoint
import com.zepben.ewb.cim.iec61968.operations.OperationalRestriction
import com.zepben.ewb.cim.iec61970.base.auxiliaryequipment.AuxiliaryEquipment
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
import com.zepben.ewb.services.customer.CustomerService

/**
 * A function that provides an exhaustive `when` style statement for all [Identifiable] leaf types supported by
 * the [NetworkService]. If the provided [identifiable] is not supported by the service the [isOther] handler
 * is invoked which by default will throw an [IllegalArgumentException]
 *
 * By using this function, you acknowledge that if any new types are added to the customer service, and thus this
 * function, it will cause a compilation error when updating to the new version. This should reduce errors due to
 * missed handling of new types introduced to the model. As this is intended behaviour it generally will not be
 * considered a breaking change in terms of semantic versioning of this library.
 *
 * If it is not critical that all types within the service are always handled, it is recommended to use a typical
 * `when` statement (Kotlin) or if-else branch (Java) and update new cases as required without breaking your code.
 *
 * @param identifiable The identified object to handle.
 * @param isBatteryUnit Handler when the [identifiable] is a [BatteryUnit]
 * @param isPhotoVoltaicUnit Handler when the [identifiable] is a [PhotoVoltaicUnit]
 * @param isPowerElectronicsWindUnit Handler when the [identifiable] is a [PowerElectronicsWindUnit]
 * @param isAcLineSegment Handler when the [identifiable] is an [AcLineSegment]
 * @param isAssetOwner Handler when the [identifiable] is an [AssetOwner]
 * @param isBaseVoltage Handler when the [identifiable] is a [BaseVoltage]
 * @param isBreaker Handler when the [identifiable] is a [Breaker]
 * @param isLoadBreakSwitch Handler when the [identifiable] is a [LoadBreakSwitch]
 * @param isBusbarSection Handler when the [identifiable] is a [BusbarSection]
 * @param isCableInfo Handler when the [identifiable] is a [CableInfo]
 * @param isCircuit Handler when the [identifiable] is a [Circuit]
 * @param isConnectivityNode Handler when the [identifiable] is a [ConnectivityNode]
 * @param isDisconnector Handler when the [identifiable] is a [Disconnector]
 * @param isEnergyConsumer Handler when the [identifiable] is an [EnergyConsumer]
 * @param isEnergyConsumerPhase Handler when the [identifiable] is an [EnergyConsumerPhase]
 * @param isEnergySource Handler when the [identifiable] is an [EnergySource]
 * @param isEnergySourcePhase Handler when the [identifiable] is an [EnergySourcePhase]
 * @param isFaultIndicator Handler when the [identifiable] is a [FaultIndicator]
 * @param isFeeder Handler when the [identifiable] is a [Feeder]
 * @param isFuse Handler when the [identifiable] is a [Fuse]
 * @param isGeographicalRegion Handler when the [identifiable] is a [GeographicalRegion]
 * @param isJumper Handler when the [identifiable] is a [Jumper]
 * @param isJunction Handler when the [identifiable] is a [Junction]
 * @param isLinearShuntCompensator Handler when the [identifiable] is a [LinearShuntCompensator]
 * @param isLocation Handler when the [identifiable] is a [Location]
 * @param isLvFeeder Handler when the [identifiable] is an [LvFeeder]
 * @param isLoop Handler when the [identifiable] is a [Loop]
 * @param isMeter Handler when the [identifiable] is a [Meter]
 * @param isOperationalRestriction Handler when the [identifiable] is an [OperationalRestriction]
 * @param isOrganisation Handler when the [identifiable] is an [Organisation]
 * @param isOverheadWireInfo Handler when the [identifiable] is an [OverheadWireInfo]
 * @param isPerLengthSequenceImpedance Handler when the [identifiable] is a [PerLengthSequenceImpedance]
 * @param isPole Handler when the [identifiable] is a [Pole]
 * @param isPowerElectronicsConnection Handler when the [identifiable] is a [PowerElectronicsConnection]
 * @param isPowerElectronicsConnectionPhase Handler when the [identifiable] is a [PowerElectronicsConnectionPhase]
 * @param isPowerTransformer Handler when the [identifiable] is a [PowerTransformer]
 * @param isPowerTransformerEnd Handler when the [identifiable] is a [PowerTransformerEnd]
 * @param isPowerTransformerInfo Handler when the [identifiable] is a [PowerTransformerInfo]
 * @param isRatioTapChanger Handler when the [identifiable] is a [RatioTapChanger]
 * @param isRecloser Handler when the [identifiable] is a [Recloser]
 * @param isSite Handler when the [identifiable] is a [Site]
 * @param isStreetlight Handler when the [identifiable] is a [Streetlight]
 * @param isSubGeographicalRegion Handler when the [identifiable] is a [SubGeographicalRegion]
 * @param isSubstation Handler when the [identifiable] is a [Substation]
 * @param isTerminal Handler when the [identifiable] is a [Terminal]
 * @param isUsagePoint Handler when the [identifiable] is a [UsagePoint]
 * @param isControl Handler when the [identifiable] is a [Control]
 * @param isAnalog Handler when the [identifiable] is an [Analog]
 * @param isAccumulator Handler when the [identifiable] is an [Accumulator]
 * @param isDiscrete Handler when the [identifiable] is a [Discrete]
 * @param isRemoteControl Handler when the [identifiable] is a [RemoteControl]
 * @param isRemoteSource Handler when the [identifiable] is a [RemoteSource]
 * @param isTransformerEndInfo Handler when the [identifiable] is a [TransformerEndInfo]
 * @param isTransformerStarImpedance Handler when the [identifiable] is a [TransformerStarImpedance]
 * @param isTransformerTankInfo Handler when the [identifiable] is a [TransformerTankInfo]
 * @param isNoLoadTest Handler when the [identifiable] is a [NoLoadTest]
 * @param isOpenCircuitTest Handler when the [identifiable] is an [OpenCircuitTest]
 * @param isShortCircuitTest Handler when the [identifiable] is a [ShortCircuitTest]
 * @param isEquivalentBranch Handler when the [identifiable] is an [EquivalentBranch]
 * @param isShuntCompensatorInfo Handler when the [identifiable] is a [ShuntCompensatorInfo]
 * @param isCurrentTransformerInfo Handler when the [identifiable] is a [CurrentTransformerInfo]
 * @param isPotentialTransformerInfo Handler when the [identifiable] is a [PotentialTransformerInfo]
 * @param isCurrentTransformer Handler when the [identifiable] is a [CurrentTransformer]
 * @param isPotentialTransformer Handler when the [identifiable] is a [PotentialTransformer]
 * @param isSwitchInfo Handler when the [identifiable] is a [SwitchInfo]
 * @param isRelayInfo Handler when the [identifiable] is a [RelayInfo]
 * @param isCurrentRelay Handler when the [identifiable] is a [CurrentRelay]
 * @param isEvChargingUnit Handler when the [identifiable] is an [EvChargingUnit]
 * @param isTapChangerControl Handler when the [identifiable] is a [TapChangerControl]
 * @param isSeriesCompensator Handler when the [identifiable] is a [SeriesCompensator]
 * @param isGround Handler when the [identifiable] is a [Ground]
 * @param isGroundDisconnector Handler when the [identifiable] is a [GroundDisconnector]
 * @param isProtectionRelayScheme Handler when the [identifiable] is a [ProtectionRelayScheme]
 * @param isProtectionRelaySystem Handler when the [identifiable] is a [ProtectionRelaySystem]
 * @param isVoltageRelay Handler when the [identifiable] is a [VoltageRelay]
 * @param isDistanceRelay Handler when the [identifiable] is a [DistanceRelay]
 * @param isGroundingImpedance Handler when the [identifiable] is a [GroundingImpedance]
 * @param isPetersenCoil Handler when the [identifiable] is a [PetersenCoil]
 * @param isReactiveCapabilityCurve Handler when the [identifiable] is a [ReactiveCapabilityCurve]
 * @param isSynchronousMachine Handler when the [identifiable] is a [SynchronousMachine]
 * @param isPanDemandResponseFunction Handler when the [identifiable] is a [PanDemandResponseFunction]
 * @param isBatteryControl Handler when the [identifiable] is a [BatteryControl]
 * @param isStaticVarCompensator Handler when the [identifiable] is a [StaticVarCompensator]
 * @param isPerLengthPhaseImpedance Handler when the [identifiable] is a [PerLengthPhaseImpedance]
 * @param isCut Handler when the [identifiable] is a [Cut]
 * @param isClamp Handler when the [identifiable] is a [Clamp]
 * @param isDirectionalCurrentRelay Handler when the [identifiable] is a [DirectionalCurrentRelay]
 * @param isLvSubstation Handler when the [identifiable] is a [LvSubstation]
 * @param isHvCustomer Handler when the [identifiable] is a [HvCustomer]
 * @param isAcLineSegmentPhase Handler when the [identifiable] is a [AcLineSegmentPhase]
 * @param isOther Handler when the [identifiable] is not supported by the [CustomerService].
 */
@JvmOverloads
inline fun <R> whenNetworkServiceObject(
    identifiable: Identifiable,
    isBatteryUnit: (BatteryUnit) -> R,
    isPhotoVoltaicUnit: (PhotoVoltaicUnit) -> R,
    isPowerElectronicsWindUnit: (PowerElectronicsWindUnit) -> R,
    isAcLineSegment: (AcLineSegment) -> R,
    isAssetOwner: (AssetOwner) -> R,
    isBaseVoltage: (BaseVoltage) -> R,
    isBreaker: (Breaker) -> R,
    isLoadBreakSwitch: (LoadBreakSwitch) -> R,
    isBusbarSection: (BusbarSection) -> R,
    isCableInfo: (CableInfo) -> R,
    isCircuit: (Circuit) -> R,
    isConnectivityNode: (ConnectivityNode) -> R,
    isDisconnector: (Disconnector) -> R,
    isEnergyConsumer: (EnergyConsumer) -> R,
    isEnergyConsumerPhase: (EnergyConsumerPhase) -> R,
    isEnergySource: (EnergySource) -> R,
    isEnergySourcePhase: (EnergySourcePhase) -> R,
    isFaultIndicator: (FaultIndicator) -> R,
    isFeeder: (Feeder) -> R,
    isFuse: (Fuse) -> R,
    isGeographicalRegion: (GeographicalRegion) -> R,
    isJumper: (Jumper) -> R,
    isJunction: (Junction) -> R,
    isLinearShuntCompensator: (LinearShuntCompensator) -> R,
    isLocation: (Location) -> R,
    isLoop: (Loop) -> R,
    isLvFeeder: (LvFeeder) -> R,
    isMeter: (Meter) -> R,
    isOperationalRestriction: (OperationalRestriction) -> R,
    isOrganisation: (Organisation) -> R,
    isOverheadWireInfo: (OverheadWireInfo) -> R,
    isPerLengthSequenceImpedance: (PerLengthSequenceImpedance) -> R,
    isPole: (Pole) -> R,
    isPowerElectronicsConnection: (PowerElectronicsConnection) -> R,
    isPowerElectronicsConnectionPhase: (PowerElectronicsConnectionPhase) -> R,
    isPowerTransformer: (PowerTransformer) -> R,
    isPowerTransformerEnd: (PowerTransformerEnd) -> R,
    isPowerTransformerInfo: (PowerTransformerInfo) -> R,
    isRatioTapChanger: (RatioTapChanger) -> R,
    isRecloser: (Recloser) -> R,
    isSite: (Site) -> R,
    isStreetlight: (Streetlight) -> R,
    isSubGeographicalRegion: (SubGeographicalRegion) -> R,
    isSubstation: (Substation) -> R,
    isTerminal: (Terminal) -> R,
    isUsagePoint: (UsagePoint) -> R,
    isControl: (Control) -> R,
    isAnalog: (Analog) -> R,
    isAccumulator: (Accumulator) -> R,
    isDiscrete: (Discrete) -> R,
    isRemoteControl: (RemoteControl) -> R,
    isRemoteSource: (RemoteSource) -> R,
    isTransformerEndInfo: (TransformerEndInfo) -> R,
    isTransformerStarImpedance: (TransformerStarImpedance) -> R,
    isTransformerTankInfo: (TransformerTankInfo) -> R,
    isNoLoadTest: (NoLoadTest) -> R,
    isOpenCircuitTest: (OpenCircuitTest) -> R,
    isShortCircuitTest: (ShortCircuitTest) -> R,
    isEquivalentBranch: (EquivalentBranch) -> R,
    isShuntCompensatorInfo: (ShuntCompensatorInfo) -> R,
    isCurrentTransformerInfo: (CurrentTransformerInfo) -> R,
    isPotentialTransformerInfo: (PotentialTransformerInfo) -> R,
    isCurrentTransformer: (CurrentTransformer) -> R,
    isPotentialTransformer: (PotentialTransformer) -> R,
    isSwitchInfo: (SwitchInfo) -> R,
    isRelayInfo: (RelayInfo) -> R,
    isCurrentRelay: (CurrentRelay) -> R,
    isEvChargingUnit: (EvChargingUnit) -> R,
    isTapChangerControl: (TapChangerControl) -> R,
    isSeriesCompensator: (SeriesCompensator) -> R,
    isGround: (Ground) -> R,
    isGroundDisconnector: (GroundDisconnector) -> R,
    isProtectionRelayScheme: (ProtectionRelayScheme) -> R,
    isProtectionRelaySystem: (ProtectionRelaySystem) -> R,
    isVoltageRelay: (VoltageRelay) -> R,
    isDistanceRelay: (DistanceRelay) -> R,
    isGroundingImpedance: (GroundingImpedance) -> R,
    isPetersenCoil: (PetersenCoil) -> R,
    isReactiveCapabilityCurve: (ReactiveCapabilityCurve) -> R,
    isSynchronousMachine: (SynchronousMachine) -> R,
    isPanDemandResponseFunction: (PanDemandResponseFunction) -> R,
    isBatteryControl: (BatteryControl) -> R,
    isStaticVarCompensator: (StaticVarCompensator) -> R,
    isPerLengthPhaseImpedance: (PerLengthPhaseImpedance) -> R,
    isCut: (Cut) -> R,
    isClamp: (Clamp) -> R,
    isDirectionalCurrentRelay: (DirectionalCurrentRelay) -> R,
    isLvSubstation: (LvSubstation) -> R,
    isHvCustomer: (HvCustomer) -> R,
    isAcLineSegmentPhase: (AcLineSegmentPhase) -> R,
    isOther: (Identifiable) -> R = { throw IllegalArgumentException("Identifiable type ${it::class} is not supported by the network service") }
): R = when (identifiable) {
    is BatteryUnit -> isBatteryUnit(identifiable)
    is PhotoVoltaicUnit -> isPhotoVoltaicUnit(identifiable)
    is PowerElectronicsWindUnit -> isPowerElectronicsWindUnit(identifiable)
    is AcLineSegment -> isAcLineSegment(identifiable)
    is AssetOwner -> isAssetOwner(identifiable)
    is BaseVoltage -> isBaseVoltage(identifiable)
    is Breaker -> isBreaker(identifiable)
    is LoadBreakSwitch -> isLoadBreakSwitch(identifiable)
    is BusbarSection -> isBusbarSection(identifiable)
    is CableInfo -> isCableInfo(identifiable)
    is Circuit -> isCircuit(identifiable)
    is ConnectivityNode -> isConnectivityNode(identifiable)
    is Disconnector -> isDisconnector(identifiable)
    is EnergyConsumer -> isEnergyConsumer(identifiable)
    is EnergyConsumerPhase -> isEnergyConsumerPhase(identifiable)
    is EnergySource -> isEnergySource(identifiable)
    is EnergySourcePhase -> isEnergySourcePhase(identifiable)
    is FaultIndicator -> isFaultIndicator(identifiable)
    is Feeder -> isFeeder(identifiable)
    is Fuse -> isFuse(identifiable)
    is GeographicalRegion -> isGeographicalRegion(identifiable)
    is Jumper -> isJumper(identifiable)
    is Junction -> isJunction(identifiable)
    is LinearShuntCompensator -> isLinearShuntCompensator(identifiable)
    is Location -> isLocation(identifiable)
    is Loop -> isLoop(identifiable)
    is LvFeeder -> isLvFeeder(identifiable)
    is Meter -> isMeter(identifiable)
    is OperationalRestriction -> isOperationalRestriction(identifiable)
    is Organisation -> isOrganisation(identifiable)
    is OverheadWireInfo -> isOverheadWireInfo(identifiable)
    is PerLengthSequenceImpedance -> isPerLengthSequenceImpedance(identifiable)
    is Pole -> isPole(identifiable)
    is PowerElectronicsConnection -> isPowerElectronicsConnection(identifiable)
    is PowerElectronicsConnectionPhase -> isPowerElectronicsConnectionPhase(identifiable)
    is PowerTransformer -> isPowerTransformer(identifiable)
    is PowerTransformerEnd -> isPowerTransformerEnd(identifiable)
    is PowerTransformerInfo -> isPowerTransformerInfo(identifiable)
    is RatioTapChanger -> isRatioTapChanger(identifiable)
    is Recloser -> isRecloser(identifiable)
    is Site -> isSite(identifiable)
    is Streetlight -> isStreetlight(identifiable)
    is SubGeographicalRegion -> isSubGeographicalRegion(identifiable)
    is Substation -> isSubstation(identifiable)
    is Terminal -> isTerminal(identifiable)
    is UsagePoint -> isUsagePoint(identifiable)
    is Control -> isControl(identifiable)
    is Analog -> isAnalog(identifiable)
    is Accumulator -> isAccumulator(identifiable)
    is Discrete -> isDiscrete(identifiable)
    is RemoteControl -> isRemoteControl(identifiable)
    is RemoteSource -> isRemoteSource(identifiable)
    is TransformerEndInfo -> isTransformerEndInfo(identifiable)
    is TransformerStarImpedance -> isTransformerStarImpedance(identifiable)
    is TransformerTankInfo -> isTransformerTankInfo(identifiable)
    is NoLoadTest -> isNoLoadTest(identifiable)
    is OpenCircuitTest -> isOpenCircuitTest(identifiable)
    is ShortCircuitTest -> isShortCircuitTest(identifiable)
    is EquivalentBranch -> isEquivalentBranch(identifiable)
    is ShuntCompensatorInfo -> isShuntCompensatorInfo(identifiable)
    is CurrentTransformerInfo -> isCurrentTransformerInfo(identifiable)
    is PotentialTransformerInfo -> isPotentialTransformerInfo(identifiable)
    is CurrentTransformer -> isCurrentTransformer(identifiable)
    is PotentialTransformer -> isPotentialTransformer(identifiable)
    is SwitchInfo -> isSwitchInfo(identifiable)
    is RelayInfo -> isRelayInfo(identifiable)
    is CurrentRelay -> isCurrentRelay(identifiable)
    is EvChargingUnit -> isEvChargingUnit(identifiable)
    is TapChangerControl -> isTapChangerControl(identifiable)
    is SeriesCompensator -> isSeriesCompensator(identifiable)
    is Ground -> isGround(identifiable)
    is GroundDisconnector -> isGroundDisconnector(identifiable)
    is ProtectionRelayScheme -> isProtectionRelayScheme(identifiable)
    is ProtectionRelaySystem -> isProtectionRelaySystem(identifiable)
    is VoltageRelay -> isVoltageRelay(identifiable)
    is DistanceRelay -> isDistanceRelay(identifiable)
    is GroundingImpedance -> isGroundingImpedance(identifiable)
    is PetersenCoil -> isPetersenCoil(identifiable)
    is ReactiveCapabilityCurve -> isReactiveCapabilityCurve(identifiable)
    is SynchronousMachine -> isSynchronousMachine(identifiable)
    is PanDemandResponseFunction -> isPanDemandResponseFunction(identifiable)
    is BatteryControl -> isBatteryControl(identifiable)
    is StaticVarCompensator -> isStaticVarCompensator(identifiable)
    is PerLengthPhaseImpedance -> isPerLengthPhaseImpedance(identifiable)
    is Cut -> isCut(identifiable)
    is Clamp -> isClamp(identifiable)
    is DirectionalCurrentRelay -> isDirectionalCurrentRelay(identifiable)
    is LvSubstation -> isLvSubstation(identifiable)
    is HvCustomer -> isHvCustomer(identifiable)
    is AcLineSegmentPhase -> isAcLineSegmentPhase(identifiable)
    else -> isOther(identifiable)
}

/**
 * A map of all [AuxiliaryEquipment] in the [NetworkService] indexed by their terminals.
 */
internal val NetworkService.auxEquipmentByTerminal: Map<Terminal, List<AuxiliaryEquipment>>
    get() = sequenceOf<AuxiliaryEquipment>()
        .filter { it.terminal != null }
        .groupBy { it.terminal!! }

/**
 * A set of all [ConductingEquipment] in the [NetworkService] that are at the top of a feeder.
 */
internal val NetworkService.feederStartPoints: Set<ConductingEquipment>
    get() = sequenceOf<Feeder>()
        .mapNotNull { it.normalHeadTerminal?.conductingEquipment }
        .toSet()

/**
 * A set of all [ConductingEquipment] in the [NetworkService] that are at the top of an LV feeder.
 */
internal val NetworkService.lvFeederStartPoints: Set<ConductingEquipment>
    get() = sequenceOf<LvFeeder>()
        .mapNotNull { it.normalHeadTerminal?.conductingEquipment }
        .toSet()

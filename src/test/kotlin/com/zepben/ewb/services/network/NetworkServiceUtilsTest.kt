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
import com.zepben.ewb.cim.iec61970.base.meas.Accumulator
import com.zepben.ewb.cim.iec61970.base.meas.Analog
import com.zepben.ewb.cim.iec61970.base.meas.Control
import com.zepben.ewb.cim.iec61970.base.meas.Discrete
import com.zepben.ewb.cim.iec61970.base.protection.CurrentRelay
import com.zepben.ewb.cim.iec61970.base.scada.RemoteControl
import com.zepben.ewb.cim.iec61970.base.scada.RemoteSource
import com.zepben.ewb.cim.iec61970.base.wires.*
import com.zepben.ewb.cim.iec61970.infiec61970.feeder.Circuit
import com.zepben.ewb.services.common.verifyWhenServiceFunctionSupportsAllServiceTypes
import com.zepben.testutils.junit.SystemLogExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class NetworkServiceUtilsTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun `supports all network service types`() {
        verifyWhenServiceFunctionSupportsAllServiceTypes(NetworkService().supportedKClasses, ::whenNetworkServiceObjectProxy)
    }

    // Function references to functions with generics are not yet supported, so we take a copy of the function that has a concrete type and pass through.
    // If you get failed tests about missing IdentifiedObject types, first update the proxied function, then update this one to match.
    internal fun whenNetworkServiceObjectProxy(
        identifiedObject: IdentifiedObject,
        isBatteryUnit: (BatteryUnit) -> String,
        isPhotoVoltaicUnit: (PhotoVoltaicUnit) -> String,
        isPowerElectronicsWindUnit: (PowerElectronicsWindUnit) -> String,
        isAcLineSegment: (AcLineSegment) -> String,
        isAssetOwner: (AssetOwner) -> String,
        isBaseVoltage: (BaseVoltage) -> String,
        isBreaker: (Breaker) -> String,
        isLoadBreakSwitch: (LoadBreakSwitch) -> String,
        isBusbarSection: (BusbarSection) -> String,
        isCableInfo: (CableInfo) -> String,
        isCircuit: (Circuit) -> String,
        isConnectivityNode: (ConnectivityNode) -> String,
        isDisconnector: (Disconnector) -> String,
        isEnergyConsumer: (EnergyConsumer) -> String,
        isEnergyConsumerPhase: (EnergyConsumerPhase) -> String,
        isEnergySource: (EnergySource) -> String,
        isEnergySourcePhase: (EnergySourcePhase) -> String,
        isFaultIndicator: (FaultIndicator) -> String,
        isFeeder: (Feeder) -> String,
        isFuse: (Fuse) -> String,
        isGeographicalRegion: (GeographicalRegion) -> String,
        isJumper: (Jumper) -> String,
        isJunction: (Junction) -> String,
        isLinearShuntCompensator: (LinearShuntCompensator) -> String,
        isLocation: (Location) -> String,
        isLoop: (Loop) -> String,
        isLvFeeder: (LvFeeder) -> String,
        isMeter: (Meter) -> String,
        isOperationalRestriction: (OperationalRestriction) -> String,
        isOrganisation: (Organisation) -> String,
        isOverheadWireInfo: (OverheadWireInfo) -> String,
        isPerLengthSequenceImpedance: (PerLengthSequenceImpedance) -> String,
        isPole: (Pole) -> String,
        isPowerElectronicsConnection: (PowerElectronicsConnection) -> String,
        isPowerElectronicsConnectionPhase: (PowerElectronicsConnectionPhase) -> String,
        isPowerTransformer: (PowerTransformer) -> String,
        isPowerTransformerEnd: (PowerTransformerEnd) -> String,
        isPowerTransformerInfo: (PowerTransformerInfo) -> String,
        isRatioTapChanger: (RatioTapChanger) -> String,
        isRecloser: (Recloser) -> String,
        isSite: (Site) -> String,
        isStreetlight: (Streetlight) -> String,
        isSubGeographicalRegion: (SubGeographicalRegion) -> String,
        isSubstation: (Substation) -> String,
        isTerminal: (Terminal) -> String,
        isUsagePoint: (UsagePoint) -> String,
        isControl: (Control) -> String,
        isAnalog: (Analog) -> String,
        isAccumulator: (Accumulator) -> String,
        isDiscrete: (Discrete) -> String,
        isRemoteControl: (RemoteControl) -> String,
        isRemoteSource: (RemoteSource) -> String,
        isTransformerEndInfo: (TransformerEndInfo) -> String,
        isTransformerStarImpedance: (TransformerStarImpedance) -> String,
        isTransformerTankInfo: (TransformerTankInfo) -> String,
        isNoLoadTest: (NoLoadTest) -> String,
        isOpenCircuitTest: (OpenCircuitTest) -> String,
        isShortCircuitTest: (ShortCircuitTest) -> String,
        isEquivalentBranch: (EquivalentBranch) -> String,
        isShuntCompensatorInfo: (ShuntCompensatorInfo) -> String,
        isCurrentTransformerInfo: (CurrentTransformerInfo) -> String,
        isPotentialTransformerInfo: (PotentialTransformerInfo) -> String,
        isCurrentTransformer: (CurrentTransformer) -> String,
        isPotentialTransformer: (PotentialTransformer) -> String,
        isSwitchInfo: (SwitchInfo) -> String,
        isRelayInfo: (RelayInfo) -> String,
        isCurrentRelay: (CurrentRelay) -> String,
        isEvChargingUnit: (EvChargingUnit) -> String,
        isTapChangerControl: (TapChangerControl) -> String,
        isSeriesCompensator: (SeriesCompensator) -> String,
        isGround: (Ground) -> String,
        isGroundDisconnector: (GroundDisconnector) -> String,
        isProtectionRelayScheme: (ProtectionRelayScheme) -> String,
        isProtectionRelaySystem: (ProtectionRelaySystem) -> String,
        isVoltageRelay: (VoltageRelay) -> String,
        isDistanceRelay: (DistanceRelay) -> String,
        isGroundingImpedance: (GroundingImpedance) -> String,
        isPetersenCoil: (PetersenCoil) -> String,
        isReactiveCapabilityCurve: (ReactiveCapabilityCurve) -> String,
        isSynchronousMachine: (SynchronousMachine) -> String,
        isPanDemandResponseFunction: (PanDemandResponseFunction) -> String,
        isBatteryControl: (BatteryControl) -> String,
        isStaticVarCompensator: (StaticVarCompensator) -> String,
        isPerLengthPhaseImpedance: (PerLengthPhaseImpedance) -> String,
        isCut: (Cut) -> String,
        isClamp: (Clamp) -> String,
        isOther: (IdentifiedObject) -> String
    ): String = whenNetworkServiceObject(
        identifiedObject,
        isBatteryUnit = isBatteryUnit,
        isPhotoVoltaicUnit = isPhotoVoltaicUnit,
        isPowerElectronicsWindUnit = isPowerElectronicsWindUnit,
        isAcLineSegment = isAcLineSegment,
        isAssetOwner = isAssetOwner,
        isBaseVoltage = isBaseVoltage,
        isBreaker = isBreaker,
        isLoadBreakSwitch = isLoadBreakSwitch,
        isBusbarSection = isBusbarSection,
        isCableInfo = isCableInfo,
        isCircuit = isCircuit,
        isConnectivityNode = isConnectivityNode,
        isDisconnector = isDisconnector,
        isEnergyConsumer = isEnergyConsumer,
        isEnergyConsumerPhase = isEnergyConsumerPhase,
        isEnergySource = isEnergySource,
        isEnergySourcePhase = isEnergySourcePhase,
        isFaultIndicator = isFaultIndicator,
        isFeeder = isFeeder,
        isFuse = isFuse,
        isGeographicalRegion = isGeographicalRegion,
        isJumper = isJumper,
        isJunction = isJunction,
        isLinearShuntCompensator = isLinearShuntCompensator,
        isLocation = isLocation,
        isLoop = isLoop,
        isLvFeeder = isLvFeeder,
        isMeter = isMeter,
        isOperationalRestriction = isOperationalRestriction,
        isOrganisation = isOrganisation,
        isOverheadWireInfo = isOverheadWireInfo,
        isPerLengthSequenceImpedance = isPerLengthSequenceImpedance,
        isPole = isPole,
        isPowerElectronicsConnection = isPowerElectronicsConnection,
        isPowerElectronicsConnectionPhase = isPowerElectronicsConnectionPhase,
        isPowerTransformer = isPowerTransformer,
        isPowerTransformerEnd = isPowerTransformerEnd,
        isPowerTransformerInfo = isPowerTransformerInfo,
        isRatioTapChanger = isRatioTapChanger,
        isRecloser = isRecloser,
        isSite = isSite,
        isStreetlight = isStreetlight,
        isSubGeographicalRegion = isSubGeographicalRegion,
        isSubstation = isSubstation,
        isTerminal = isTerminal,
        isUsagePoint = isUsagePoint,
        isControl = isControl,
        isAnalog = isAnalog,
        isAccumulator = isAccumulator,
        isDiscrete = isDiscrete,
        isRemoteControl = isRemoteControl,
        isRemoteSource = isRemoteSource,
        isTransformerEndInfo = isTransformerEndInfo,
        isTransformerStarImpedance = isTransformerStarImpedance,
        isTransformerTankInfo = isTransformerTankInfo,
        isNoLoadTest = isNoLoadTest,
        isOpenCircuitTest = isOpenCircuitTest,
        isShortCircuitTest = isShortCircuitTest,
        isEquivalentBranch = isEquivalentBranch,
        isShuntCompensatorInfo = isShuntCompensatorInfo,
        isCurrentTransformerInfo = isCurrentTransformerInfo,
        isPotentialTransformerInfo = isPotentialTransformerInfo,
        isCurrentTransformer = isCurrentTransformer,
        isPotentialTransformer = isPotentialTransformer,
        isSwitchInfo = isSwitchInfo,
        isRelayInfo = isRelayInfo,
        isCurrentRelay = isCurrentRelay,
        isEvChargingUnit = isEvChargingUnit,
        isTapChangerControl = isTapChangerControl,
        isSeriesCompensator = isSeriesCompensator,
        isGround = isGround,
        isGroundDisconnector = isGroundDisconnector,
        isProtectionRelayScheme = isProtectionRelayScheme,
        isProtectionRelaySystem = isProtectionRelaySystem,
        isVoltageRelay = isVoltageRelay,
        isDistanceRelay = isDistanceRelay,
        isGroundingImpedance = isGroundingImpedance,
        isPetersenCoil = isPetersenCoil,
        isReactiveCapabilityCurve = isReactiveCapabilityCurve,
        isSynchronousMachine = isSynchronousMachine,
        isPanDemandResponseFunction = isPanDemandResponseFunction,
        isBatteryControl = isBatteryControl,
        isStaticVarCompensator = isStaticVarCompensator,
        isPerLengthPhaseImpedance = isPerLengthPhaseImpedance,
        isCut = isCut,
        isClamp = isClamp,
        isOther = isOther
    )

}

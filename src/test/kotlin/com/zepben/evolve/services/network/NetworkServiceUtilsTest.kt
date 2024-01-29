/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network

import com.zepben.evolve.cim.iec61968.assetinfo.*
import com.zepben.evolve.cim.iec61968.assets.AssetOwner
import com.zepben.evolve.cim.iec61968.assets.Pole
import com.zepben.evolve.cim.iec61968.assets.Streetlight
import com.zepben.evolve.cim.iec61968.common.Location
import com.zepben.evolve.cim.iec61968.common.Organisation
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
import com.zepben.evolve.services.common.InvokeChecker
import com.zepben.evolve.services.common.InvokedChecker
import com.zepben.evolve.services.common.NeverInvokedChecker
import com.zepben.evolve.services.common.verifyWhenServiceObjectFunctionSupportsAllServiceObjectTypes
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class NetworkServiceUtilsTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    // Function references to functions with generics are not yet supported.
    // So, we take a copy of the function that has a concrete type and pass through.
    // If you get failed tests about missing IdentifiedObject types, first update the proxied function,
    // then update this one to match and then update the tests.
    private fun whenNetworkServiceObjectProxy(
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
        isOther = isOther
    )

    private fun whenNetworkServiceObjectTester(
        identifiedObject: IdentifiedObject,
        isBatteryUnit: InvokeChecker<BatteryUnit> = NeverInvokedChecker(),
        isPhotoVoltaicUnit: InvokeChecker<PhotoVoltaicUnit> = NeverInvokedChecker(),
        isPowerElectronicsWindUnit: InvokeChecker<PowerElectronicsWindUnit> = NeverInvokedChecker(),
        isAcLineSegment: InvokeChecker<AcLineSegment> = NeverInvokedChecker(),
        isAssetOwner: InvokeChecker<AssetOwner> = NeverInvokedChecker(),
        isBaseVoltage: InvokeChecker<BaseVoltage> = NeverInvokedChecker(),
        isBreaker: InvokeChecker<Breaker> = NeverInvokedChecker(),
        isLoadBreakSwitch: InvokeChecker<LoadBreakSwitch> = NeverInvokedChecker(),
        isBusbarSection: InvokeChecker<BusbarSection> = NeverInvokedChecker(),
        isCableInfo: InvokeChecker<CableInfo> = NeverInvokedChecker(),
        isCircuit: InvokeChecker<Circuit> = NeverInvokedChecker(),
        isConnectivityNode: InvokeChecker<ConnectivityNode> = NeverInvokedChecker(),
        isDisconnector: InvokeChecker<Disconnector> = NeverInvokedChecker(),
        isEnergyConsumer: InvokeChecker<EnergyConsumer> = NeverInvokedChecker(),
        isEnergyConsumerPhase: InvokeChecker<EnergyConsumerPhase> = NeverInvokedChecker(),
        isEnergySource: InvokeChecker<EnergySource> = NeverInvokedChecker(),
        isEnergySourcePhase: InvokeChecker<EnergySourcePhase> = NeverInvokedChecker(),
        isFaultIndicator: InvokeChecker<FaultIndicator> = NeverInvokedChecker(),
        isFeeder: InvokeChecker<Feeder> = NeverInvokedChecker(),
        isFuse: InvokeChecker<Fuse> = NeverInvokedChecker(),
        isGeographicalRegion: InvokeChecker<GeographicalRegion> = NeverInvokedChecker(),
        isJumper: InvokeChecker<Jumper> = NeverInvokedChecker(),
        isJunction: InvokeChecker<Junction> = NeverInvokedChecker(),
        isLinearShuntCompensator: InvokeChecker<LinearShuntCompensator> = NeverInvokedChecker(),
        isLocation: InvokeChecker<Location> = NeverInvokedChecker(),
        isLoop: InvokeChecker<Loop> = NeverInvokedChecker(),
        isLvFeeder: InvokeChecker<LvFeeder> = NeverInvokedChecker(),
        isMeter: InvokeChecker<Meter> = NeverInvokedChecker(),
        isOperationalRestriction: InvokeChecker<OperationalRestriction> = NeverInvokedChecker(),
        isOrganisation: InvokeChecker<Organisation> = NeverInvokedChecker(),
        isOverheadWireInfo: InvokeChecker<OverheadWireInfo> = NeverInvokedChecker(),
        isPerLengthSequenceImpedance: InvokeChecker<PerLengthSequenceImpedance> = NeverInvokedChecker(),
        isPole: InvokeChecker<Pole> = NeverInvokedChecker(),
        isPowerElectronicsConnection: InvokeChecker<PowerElectronicsConnection> = NeverInvokedChecker(),
        isPowerElectronicsConnectionPhase: InvokeChecker<PowerElectronicsConnectionPhase> = NeverInvokedChecker(),
        isPowerTransformer: InvokeChecker<PowerTransformer> = NeverInvokedChecker(),
        isPowerTransformerEnd: InvokeChecker<PowerTransformerEnd> = NeverInvokedChecker(),
        isPowerTransformerInfo: InvokeChecker<PowerTransformerInfo> = NeverInvokedChecker(),
        isRatioTapChanger: InvokeChecker<RatioTapChanger> = NeverInvokedChecker(),
        isRecloser: InvokeChecker<Recloser> = NeverInvokedChecker(),
        isSite: InvokeChecker<Site> = NeverInvokedChecker(),
        isStreetlight: InvokeChecker<Streetlight> = NeverInvokedChecker(),
        isSubGeographicalRegion: InvokeChecker<SubGeographicalRegion> = NeverInvokedChecker(),
        isSubstation: InvokeChecker<Substation> = NeverInvokedChecker(),
        isTerminal: InvokeChecker<Terminal> = NeverInvokedChecker(),
        isUsagePoint: InvokeChecker<UsagePoint> = NeverInvokedChecker(),
        isControl: InvokeChecker<Control> = NeverInvokedChecker(),
        isAnalog: InvokeChecker<Analog> = NeverInvokedChecker(),
        isAccumulator: InvokeChecker<Accumulator> = NeverInvokedChecker(),
        isDiscrete: InvokeChecker<Discrete> = NeverInvokedChecker(),
        isRemoteControl: InvokeChecker<RemoteControl> = NeverInvokedChecker(),
        isRemoteSource: InvokeChecker<RemoteSource> = NeverInvokedChecker(),
        isTransformerEndInfo: InvokeChecker<TransformerEndInfo> = NeverInvokedChecker(),
        isTransformerStarImpedance: InvokeChecker<TransformerStarImpedance> = NeverInvokedChecker(),
        isTransformerTankInfo: InvokeChecker<TransformerTankInfo> = NeverInvokedChecker(),
        isNoLoadTest: InvokeChecker<NoLoadTest> = NeverInvokedChecker(),
        isOpenCircuitTest: InvokeChecker<OpenCircuitTest> = NeverInvokedChecker(),
        isShortCircuitTest: InvokeChecker<ShortCircuitTest> = NeverInvokedChecker(),
        isEquivalentBranch: InvokeChecker<EquivalentBranch> = NeverInvokedChecker(),
        isShuntCompensatorInfo: InvokeChecker<ShuntCompensatorInfo> = NeverInvokedChecker(),
        isCurrentTransformerInfo: InvokeChecker<CurrentTransformerInfo> = NeverInvokedChecker(),
        isPotentialTransformerInfo: InvokeChecker<PotentialTransformerInfo> = NeverInvokedChecker(),
        isCurrentTransformer: InvokeChecker<CurrentTransformer> = NeverInvokedChecker(),
        isPotentialTransformer: InvokeChecker<PotentialTransformer> = NeverInvokedChecker(),
        isSwitchInfo: InvokeChecker<SwitchInfo> = NeverInvokedChecker(),
        isRelayInfo: InvokeChecker<RelayInfo> = NeverInvokedChecker(),
        isCurrentRelay: InvokeChecker<CurrentRelay> = NeverInvokedChecker(),
        isEvChargingUnit: InvokeChecker<EvChargingUnit> = NeverInvokedChecker(),
        isTapChangerControl: InvokeChecker<TapChangerControl> = NeverInvokedChecker(),
        isSeriesCompensator: InvokeChecker<SeriesCompensator> = NeverInvokedChecker(),
        isGround: InvokeChecker<Ground> = NeverInvokedChecker(),
        isGroundDisconnector: InvokeChecker<GroundDisconnector> = NeverInvokedChecker(),
        isProtectionRelayScheme: InvokeChecker<ProtectionRelayScheme> = NeverInvokedChecker(),
        isProtectionRelaySystem: InvokeChecker<ProtectionRelaySystem> = NeverInvokedChecker(),
        isVoltageRelay: InvokeChecker<VoltageRelay> = NeverInvokedChecker(),
        isDistanceRelay: InvokeChecker<DistanceRelay> = NeverInvokedChecker(),
        isOther: InvokeChecker<IdentifiedObject> = NeverInvokedChecker()
    ) {
        val returnValue = whenNetworkServiceObjectProxy(
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
            isOther = isOther
        )

        assertThat(returnValue, equalTo(identifiedObject.toString()))
        isBatteryUnit.verifyInvoke()
        isPhotoVoltaicUnit.verifyInvoke()
        isPowerElectronicsWindUnit.verifyInvoke()
        isAcLineSegment.verifyInvoke()
        isAssetOwner.verifyInvoke()
        isBaseVoltage.verifyInvoke()
        isBreaker.verifyInvoke()
        isLoadBreakSwitch.verifyInvoke()
        isBusbarSection.verifyInvoke()
        isCableInfo.verifyInvoke()
        isCircuit.verifyInvoke()
        isConnectivityNode.verifyInvoke()
        isDisconnector.verifyInvoke()
        isEnergyConsumer.verifyInvoke()
        isEnergyConsumerPhase.verifyInvoke()
        isEnergySource.verifyInvoke()
        isEnergySourcePhase.verifyInvoke()
        isFaultIndicator.verifyInvoke()
        isFeeder.verifyInvoke()
        isFuse.verifyInvoke()
        isGeographicalRegion.verifyInvoke()
        isJumper.verifyInvoke()
        isJunction.verifyInvoke()
        isLinearShuntCompensator.verifyInvoke()
        isLocation.verifyInvoke()
        isLoop.verifyInvoke()
        isLvFeeder.verifyInvoke()
        isMeter.verifyInvoke()
        isOperationalRestriction.verifyInvoke()
        isOrganisation.verifyInvoke()
        isOverheadWireInfo.verifyInvoke()
        isPerLengthSequenceImpedance.verifyInvoke()
        isPole.verifyInvoke()
        isPowerElectronicsConnection.verifyInvoke()
        isPowerElectronicsConnectionPhase.verifyInvoke()
        isPowerTransformer.verifyInvoke()
        isPowerTransformerEnd.verifyInvoke()
        isPowerTransformerInfo.verifyInvoke()
        isRatioTapChanger.verifyInvoke()
        isRecloser.verifyInvoke()
        isSite.verifyInvoke()
        isStreetlight.verifyInvoke()
        isSubGeographicalRegion.verifyInvoke()
        isSubstation.verifyInvoke()
        isTerminal.verifyInvoke()
        isUsagePoint.verifyInvoke()
        isControl.verifyInvoke()
        isAnalog.verifyInvoke()
        isAccumulator.verifyInvoke()
        isDiscrete.verifyInvoke()
        isRemoteControl.verifyInvoke()
        isRemoteSource.verifyInvoke()
        isTransformerEndInfo.verifyInvoke()
        isTransformerStarImpedance.verifyInvoke()
        isTransformerTankInfo.verifyInvoke()
        isNoLoadTest.verifyInvoke()
        isOpenCircuitTest.verifyInvoke()
        isShortCircuitTest.verifyInvoke()
        isEquivalentBranch.verifyInvoke()
        isShuntCompensatorInfo.verifyInvoke()
        isCurrentTransformerInfo.verifyInvoke()
        isPotentialTransformerInfo.verifyInvoke()
        isCurrentTransformer.verifyInvoke()
        isPotentialTransformer.verifyInvoke()
        isSwitchInfo.verifyInvoke()
        isRelayInfo.verifyInvoke()
        isCurrentRelay.verifyInvoke()
        isEvChargingUnit.verifyInvoke()
        isTapChangerControl.verifyInvoke()
        isOther.verifyInvoke()
    }

    @Test
    fun `supports all network service types`() {
        verifyWhenServiceObjectFunctionSupportsAllServiceObjectTypes(NetworkService().supportedKClasses, ::whenNetworkServiceObjectProxy)
    }

    @Test
    internal fun `invokes correct function`() {
        BatteryUnit().also { whenNetworkServiceObjectTester(it, isBatteryUnit = InvokedChecker(it)) }
        PhotoVoltaicUnit().also { whenNetworkServiceObjectTester(it, isPhotoVoltaicUnit = InvokedChecker(it)) }
        PowerElectronicsWindUnit().also { whenNetworkServiceObjectTester(it, isPowerElectronicsWindUnit = InvokedChecker(it)) }
        AcLineSegment().also { whenNetworkServiceObjectTester(it, isAcLineSegment = InvokedChecker(it)) }
        AssetOwner().also { whenNetworkServiceObjectTester(it, isAssetOwner = InvokedChecker(it)) }
        BaseVoltage().also { whenNetworkServiceObjectTester(it, isBaseVoltage = InvokedChecker(it)) }
        Breaker().also { whenNetworkServiceObjectTester(it, isBreaker = InvokedChecker(it)) }
        LoadBreakSwitch().also { whenNetworkServiceObjectTester(it, isLoadBreakSwitch = InvokedChecker(it)) }
        BusbarSection().also { whenNetworkServiceObjectTester(it, isBusbarSection = InvokedChecker(it)) }
        CableInfo().also { whenNetworkServiceObjectTester(it, isCableInfo = InvokedChecker(it)) }
        Circuit().also { whenNetworkServiceObjectTester(it, isCircuit = InvokedChecker(it)) }
        ConnectivityNode().also { whenNetworkServiceObjectTester(it, isConnectivityNode = InvokedChecker(it)) }
        Disconnector().also { whenNetworkServiceObjectTester(it, isDisconnector = InvokedChecker(it)) }
        EnergyConsumer().also { whenNetworkServiceObjectTester(it, isEnergyConsumer = InvokedChecker(it)) }
        EnergyConsumerPhase().also { whenNetworkServiceObjectTester(it, isEnergyConsumerPhase = InvokedChecker(it)) }
        EnergySource().also { whenNetworkServiceObjectTester(it, isEnergySource = InvokedChecker(it)) }
        EnergySourcePhase().also { whenNetworkServiceObjectTester(it, isEnergySourcePhase = InvokedChecker(it)) }
        FaultIndicator().also { whenNetworkServiceObjectTester(it, isFaultIndicator = InvokedChecker(it)) }
        Feeder().also { whenNetworkServiceObjectTester(it, isFeeder = InvokedChecker(it)) }
        Fuse().also { whenNetworkServiceObjectTester(it, isFuse = InvokedChecker(it)) }
        GeographicalRegion().also { whenNetworkServiceObjectTester(it, isGeographicalRegion = InvokedChecker(it)) }
        Jumper().also { whenNetworkServiceObjectTester(it, isJumper = InvokedChecker(it)) }
        Junction().also { whenNetworkServiceObjectTester(it, isJunction = InvokedChecker(it)) }
        LinearShuntCompensator().also { whenNetworkServiceObjectTester(it, isLinearShuntCompensator = InvokedChecker(it)) }
        Location().also { whenNetworkServiceObjectTester(it, isLocation = InvokedChecker(it)) }
        Loop().also { whenNetworkServiceObjectTester(it, isLoop = InvokedChecker(it)) }
        LvFeeder().also { whenNetworkServiceObjectTester(it, isLvFeeder = InvokedChecker(it)) }
        Meter().also { whenNetworkServiceObjectTester(it, isMeter = InvokedChecker(it)) }
        OperationalRestriction().also { whenNetworkServiceObjectTester(it, isOperationalRestriction = InvokedChecker(it)) }
        Organisation().also { whenNetworkServiceObjectTester(it, isOrganisation = InvokedChecker(it)) }
        OverheadWireInfo().also { whenNetworkServiceObjectTester(it, isOverheadWireInfo = InvokedChecker(it)) }
        PerLengthSequenceImpedance().also { whenNetworkServiceObjectTester(it, isPerLengthSequenceImpedance = InvokedChecker(it)) }
        Pole().also { whenNetworkServiceObjectTester(it, isPole = InvokedChecker(it)) }
        PowerElectronicsConnection().also { whenNetworkServiceObjectTester(it, isPowerElectronicsConnection = InvokedChecker(it)) }
        PowerElectronicsConnectionPhase().also { whenNetworkServiceObjectTester(it, isPowerElectronicsConnectionPhase = InvokedChecker(it)) }
        PowerTransformer().also { whenNetworkServiceObjectTester(it, isPowerTransformer = InvokedChecker(it)) }
        PowerTransformerEnd().also { whenNetworkServiceObjectTester(it, isPowerTransformerEnd = InvokedChecker(it)) }
        PowerTransformerInfo().also { whenNetworkServiceObjectTester(it, isPowerTransformerInfo = InvokedChecker(it)) }
        RatioTapChanger().also { whenNetworkServiceObjectTester(it, isRatioTapChanger = InvokedChecker(it)) }
        Recloser().also { whenNetworkServiceObjectTester(it, isRecloser = InvokedChecker(it)) }
        Site().also { whenNetworkServiceObjectTester(it, isSite = InvokedChecker(it)) }
        Streetlight().also { whenNetworkServiceObjectTester(it, isStreetlight = InvokedChecker(it)) }
        SubGeographicalRegion().also { whenNetworkServiceObjectTester(it, isSubGeographicalRegion = InvokedChecker(it)) }
        Substation().also { whenNetworkServiceObjectTester(it, isSubstation = InvokedChecker(it)) }
        Terminal().also { whenNetworkServiceObjectTester(it, isTerminal = InvokedChecker(it)) }
        UsagePoint().also { whenNetworkServiceObjectTester(it, isUsagePoint = InvokedChecker(it)) }
        Control().also { whenNetworkServiceObjectTester(it, isControl = InvokedChecker(it)) }
        Analog().also { whenNetworkServiceObjectTester(it, isAnalog = InvokedChecker(it)) }
        Accumulator().also { whenNetworkServiceObjectTester(it, isAccumulator = InvokedChecker(it)) }
        Discrete().also { whenNetworkServiceObjectTester(it, isDiscrete = InvokedChecker(it)) }
        RemoteControl().also { whenNetworkServiceObjectTester(it, isRemoteControl = InvokedChecker(it)) }
        RemoteSource().also { whenNetworkServiceObjectTester(it, isRemoteSource = InvokedChecker(it)) }
        TransformerEndInfo().also { whenNetworkServiceObjectTester(it, isTransformerEndInfo = InvokedChecker(it)) }
        TransformerStarImpedance().also { whenNetworkServiceObjectTester(it, isTransformerStarImpedance = InvokedChecker(it)) }
        TransformerTankInfo().also { whenNetworkServiceObjectTester(it, isTransformerTankInfo = InvokedChecker(it)) }
        NoLoadTest().also { whenNetworkServiceObjectTester(it, isNoLoadTest = InvokedChecker(it)) }
        OpenCircuitTest().also { whenNetworkServiceObjectTester(it, isOpenCircuitTest = InvokedChecker(it)) }
        ShortCircuitTest().also { whenNetworkServiceObjectTester(it, isShortCircuitTest = InvokedChecker(it)) }
        EquivalentBranch().also { whenNetworkServiceObjectTester(it, isEquivalentBranch = InvokedChecker(it)) }
        ShuntCompensatorInfo().also { whenNetworkServiceObjectTester(it, isShuntCompensatorInfo = InvokedChecker(it)) }
        CurrentTransformerInfo().also { whenNetworkServiceObjectTester(it, isCurrentTransformerInfo = InvokedChecker(it)) }
        PotentialTransformerInfo().also { whenNetworkServiceObjectTester(it, isPotentialTransformerInfo = InvokedChecker(it)) }
        CurrentTransformer().also { whenNetworkServiceObjectTester(it, isCurrentTransformer = InvokedChecker(it)) }
        PotentialTransformer().also { whenNetworkServiceObjectTester(it, isPotentialTransformer = InvokedChecker(it)) }
        SwitchInfo().also { whenNetworkServiceObjectTester(it, isSwitchInfo = InvokedChecker(it)) }
        RelayInfo().also { whenNetworkServiceObjectTester(it, isRelayInfo = InvokedChecker(it)) }
        CurrentRelay().also { whenNetworkServiceObjectTester(it, isCurrentRelay = InvokedChecker(it)) }
        EvChargingUnit().also { whenNetworkServiceObjectTester(it, isEvChargingUnit = InvokedChecker(it)) }
        TapChangerControl().also { whenNetworkServiceObjectTester(it, isTapChangerControl = InvokedChecker(it)) }
        SeriesCompensator().also { whenNetworkServiceObjectTester(it, isSeriesCompensator = InvokedChecker(it)) }
        Ground().also { whenNetworkServiceObjectTester(it, isGround = InvokedChecker(it)) }
        GroundDisconnector().also { whenNetworkServiceObjectTester(it, isGroundDisconnector = InvokedChecker(it)) }
        ProtectionRelayScheme().also { whenNetworkServiceObjectTester(it, isProtectionRelayScheme = InvokedChecker(it)) }
        ProtectionRelaySystem().also { whenNetworkServiceObjectTester(it, isProtectionRelaySystem = InvokedChecker(it)) }
        VoltageRelay().also { whenNetworkServiceObjectTester(it, isVoltageRelay = InvokedChecker(it)) }
        DistanceRelay().also { whenNetworkServiceObjectTester(it, isDistanceRelay = InvokedChecker(it)) }
        object : IdentifiedObject() {}.also { whenNetworkServiceObjectTester(it, isOther = InvokedChecker(it)) }
    }
}

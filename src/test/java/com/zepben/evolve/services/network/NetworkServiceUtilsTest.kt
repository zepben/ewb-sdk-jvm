/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network

import com.zepben.evolve.cim.iec61968.assetinfo.CableInfo
import com.zepben.evolve.cim.iec61968.assetinfo.OverheadWireInfo
import com.zepben.evolve.cim.iec61968.assets.AssetOwner
import com.zepben.evolve.cim.iec61968.assets.Pole
import com.zepben.evolve.cim.iec61968.assets.Streetlight
import com.zepben.evolve.cim.iec61968.common.Location
import com.zepben.evolve.cim.iec61968.common.Organisation
import com.zepben.evolve.cim.iec61968.metering.Meter
import com.zepben.evolve.cim.iec61968.metering.UsagePoint
import com.zepben.evolve.cim.iec61968.operations.OperationalRestriction
import com.zepben.evolve.cim.iec61970.base.auxiliaryequipment.FaultIndicator
import com.zepben.evolve.cim.iec61970.base.core.*
import com.zepben.evolve.cim.iec61970.base.meas.Accumulator
import com.zepben.evolve.cim.iec61970.base.meas.Analog
import com.zepben.evolve.cim.iec61970.base.meas.Control
import com.zepben.evolve.cim.iec61970.base.meas.Discrete
import com.zepben.evolve.cim.iec61970.base.scada.RemoteControl
import com.zepben.evolve.cim.iec61970.base.scada.RemoteSource
import com.zepben.evolve.cim.iec61970.base.wires.*
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.Circuit
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.Loop
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
        isAcLineSegment: (AcLineSegment) -> String,
        isAssetOwner: (AssetOwner) -> String,
        isBaseVoltage: (BaseVoltage) -> String,
        isBreaker: (Breaker) -> String,
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
        isMeter: (Meter) -> String,
        isOperationalRestriction: (OperationalRestriction) -> String,
        isOrganisation: (Organisation) -> String,
        isOverheadWireInfo: (OverheadWireInfo) -> String,
        isPerLengthSequenceImpedance: (PerLengthSequenceImpedance) -> String,
        isPole: (Pole) -> String,
        isPowerTransformer: (PowerTransformer) -> String,
        isPowerTransformerEnd: (PowerTransformerEnd) -> String,
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
        isOther: (IdentifiedObject) -> String
    ): String = whenNetworkServiceObject(
        identifiedObject,
        isAcLineSegment = isAcLineSegment,
        isAssetOwner = isAssetOwner,
        isBaseVoltage = isBaseVoltage,
        isBreaker = isBreaker,
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
        isMeter = isMeter,
        isOperationalRestriction = isOperationalRestriction,
        isOrganisation = isOrganisation,
        isOverheadWireInfo = isOverheadWireInfo,
        isPerLengthSequenceImpedance = isPerLengthSequenceImpedance,
        isPole = isPole,
        isPowerTransformer = isPowerTransformer,
        isPowerTransformerEnd = isPowerTransformerEnd,
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
        isOther = isOther
    )

    private fun whenNetworkServiceObjectTester(
        identifiedObject: IdentifiedObject,
        isAcLineSegment: InvokeChecker<AcLineSegment> = NeverInvokedChecker(),
        isAssetOwner: InvokeChecker<AssetOwner> = NeverInvokedChecker(),
        isBaseVoltage: InvokeChecker<BaseVoltage> = NeverInvokedChecker(),
        isBreaker: InvokeChecker<Breaker> = NeverInvokedChecker(),
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
        isMeter: InvokeChecker<Meter> = NeverInvokedChecker(),
        isOperationalRestriction: InvokeChecker<OperationalRestriction> = NeverInvokedChecker(),
        isOrganisation: InvokeChecker<Organisation> = NeverInvokedChecker(),
        isOverheadWireInfo: InvokeChecker<OverheadWireInfo> = NeverInvokedChecker(),
        isPerLengthSequenceImpedance: InvokeChecker<PerLengthSequenceImpedance> = NeverInvokedChecker(),
        isPole: InvokeChecker<Pole> = NeverInvokedChecker(),
        isPowerTransformer: InvokeChecker<PowerTransformer> = NeverInvokedChecker(),
        isPowerTransformerEnd: InvokeChecker<PowerTransformerEnd> = NeverInvokedChecker(),
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
        isOther: InvokeChecker<IdentifiedObject> = NeverInvokedChecker()
    ) {
        val returnValue = whenNetworkServiceObjectProxy(
            identifiedObject,
            isAcLineSegment = isAcLineSegment,
            isAssetOwner = isAssetOwner,
            isBaseVoltage = isBaseVoltage,
            isBreaker = isBreaker,
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
            isMeter = isMeter,
            isOperationalRestriction = isOperationalRestriction,
            isOrganisation = isOrganisation,
            isOverheadWireInfo = isOverheadWireInfo,
            isPerLengthSequenceImpedance = isPerLengthSequenceImpedance,
            isPole = isPole,
            isPowerTransformer = isPowerTransformer,
            isPowerTransformerEnd = isPowerTransformerEnd,
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
            isOther = isOther
        )

        assertThat(returnValue, equalTo(identifiedObject.toString()))
        isAcLineSegment.verifyInvoke()
        isAssetOwner.verifyInvoke()
        isBaseVoltage.verifyInvoke()
        isBreaker.verifyInvoke()
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
        isMeter.verifyInvoke()
        isOperationalRestriction.verifyInvoke()
        isOrganisation.verifyInvoke()
        isOverheadWireInfo.verifyInvoke()
        isPerLengthSequenceImpedance.verifyInvoke()
        isPole.verifyInvoke()
        isPowerTransformer.verifyInvoke()
        isPowerTransformerEnd.verifyInvoke()
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
        isOther.verifyInvoke()
    }

    @Test
    fun `supports all diagram service types`() {
        verifyWhenServiceObjectFunctionSupportsAllServiceObjectTypes(NetworkService().supportedKClasses, ::whenNetworkServiceObjectProxy)
    }

    @Test
    internal fun `invokes correct function`() {
        AcLineSegment().also { whenNetworkServiceObjectTester(it, isAcLineSegment = InvokedChecker(it)) }
        AssetOwner().also { whenNetworkServiceObjectTester(it, isAssetOwner = InvokedChecker(it)) }
        BaseVoltage().also { whenNetworkServiceObjectTester(it, isBaseVoltage = InvokedChecker(it)) }
        Breaker().also { whenNetworkServiceObjectTester(it, isBreaker = InvokedChecker(it)) }
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
        Meter().also { whenNetworkServiceObjectTester(it, isMeter = InvokedChecker(it)) }
        OperationalRestriction().also { whenNetworkServiceObjectTester(it, isOperationalRestriction = InvokedChecker(it)) }
        Organisation().also { whenNetworkServiceObjectTester(it, isOrganisation = InvokedChecker(it)) }
        OverheadWireInfo().also { whenNetworkServiceObjectTester(it, isOverheadWireInfo = InvokedChecker(it)) }
        PerLengthSequenceImpedance().also { whenNetworkServiceObjectTester(it, isPerLengthSequenceImpedance = InvokedChecker(it)) }
        Pole().also { whenNetworkServiceObjectTester(it, isPole = InvokedChecker(it)) }
        PowerTransformer().also { whenNetworkServiceObjectTester(it, isPowerTransformer = InvokedChecker(it)) }
        PowerTransformerEnd().also { whenNetworkServiceObjectTester(it, isPowerTransformerEnd = InvokedChecker(it)) }
        RatioTapChanger().also { whenNetworkServiceObjectTester(it, isRatioTapChanger = InvokedChecker(it)) }
        Recloser().also { whenNetworkServiceObjectTester(it, isRecloser = InvokedChecker(it)) }
        Site().also { whenNetworkServiceObjectTester(it, isSite = InvokedChecker(it)) }
        Streetlight().also { whenNetworkServiceObjectTester(it, isStreetlight = InvokedChecker(it)) }
        SubGeographicalRegion().also { whenNetworkServiceObjectTester(it, isSubGeographicalRegion = InvokedChecker(it)) }
        Substation().also { whenNetworkServiceObjectTester(it, isSubstation = InvokedChecker(it)) }
        Terminal().also { whenNetworkServiceObjectTester(it, isTerminal = InvokedChecker(it)) }
        UsagePoint().also { whenNetworkServiceObjectTester(it, isUsagePoint = InvokedChecker(it)) }
        object : IdentifiedObject() {}.also { whenNetworkServiceObjectTester(it, isOther = InvokedChecker(it)) }
    }
}


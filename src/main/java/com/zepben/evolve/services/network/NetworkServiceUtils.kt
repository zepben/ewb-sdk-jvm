/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */


@file:JvmName("NetworkServiceUtils")

package com.zepben.evolve.services.network

import com.zepben.evolve.cim.iec61968.assetinfo.CableInfo
import com.zepben.evolve.cim.iec61968.assetinfo.OverheadWireInfo
import com.zepben.evolve.cim.iec61968.assetinfo.PowerTransformerInfo
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
import com.zepben.evolve.services.customer.CustomerService

/**
 * A function that provides an exhaustive `when` style statement for all [IdentifiedObject] leaf types supported by
 * the [NetworkService]. If the provided [identifiedObject] is not supported by the service the [isOther] handler
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
 * @param identifiedObject The identified object to handle.
 * @param isAcLineSegment Handler when the [identifiedObject] is a [AcLineSegment]
 * @param isAssetOwner Handler when the [identifiedObject] is a [AssetOwner]
 * @param isBaseVoltage Handler when the [identifiedObject] is a [BaseVoltage]
 * @param isBreaker Handler when the [identifiedObject] is a [Breaker]
 * @param isCableInfo Handler when the [identifiedObject] is a [CableInfo]
 * @param isCircuit Handler when the [identifiedObject] is a [Circuit]
 * @param isConnectivityNode Handler when the [identifiedObject] is a [ConnectivityNode]
 * @param isDisconnector Handler when the [identifiedObject] is a [Disconnector]
 * @param isEnergyConsumer Handler when the [identifiedObject] is a [EnergyConsumer]
 * @param isEnergyConsumerPhase Handler when the [identifiedObject] is a [EnergyConsumerPhase]
 * @param isEnergySource Handler when the [identifiedObject] is a [EnergySource]
 * @param isEnergySourcePhase Handler when the [identifiedObject] is a [EnergySourcePhase]
 * @param isFaultIndicator Handler when the [identifiedObject] is a [FaultIndicator]
 * @param isFeeder Handler when the [identifiedObject] is a [Feeder]
 * @param isFuse Handler when the [identifiedObject] is a [Fuse]
 * @param isGeographicalRegion Handler when the [identifiedObject] is a [GeographicalRegion]
 * @param isJumper Handler when the [identifiedObject] is a [Jumper]
 * @param isJunction Handler when the [identifiedObject] is a [Junction]
 * @param isLinearShuntCompensator Handler when the [identifiedObject] is a [LinearShuntCompensator]
 * @param isLocation Handler when the [identifiedObject] is a [Location]
 * @param isLoop Handler when the [identifiedObject] is a [Loop]
 * @param isMeter Handler when the [identifiedObject] is a [Meter]
 * @param isOperationalRestriction Handler when the [identifiedObject] is a [OperationalRestriction]
 * @param isOrganisation Handler when the [identifiedObject] is a [Organisation]
 * @param isOverheadWireInfo Handler when the [identifiedObject] is a [OverheadWireInfo]
 * @param isPerLengthSequenceImpedance Handler when the [identifiedObject] is a [PerLengthSequenceImpedance]
 * @param isPole Handler when the [identifiedObject] is a [Pole]
 * @param isPowerTransformer Handler when the [identifiedObject] is a [PowerTransformer]
 * @param isPowerTransformerEnd Handler when the [identifiedObject] is a [PowerTransformerEnd]
 * @param isPowerTransformerInfo Handler when the [identifiedObject] is a [PowerTransformerInfo]
 * @param isRatioTapChanger Handler when the [identifiedObject] is a [RatioTapChanger]
 * @param isRecloser Handler when the [identifiedObject] is a [Recloser]
 * @param isSite Handler when the [identifiedObject] is a [Site]
 * @param isStreetlight Handler when the [identifiedObject] is a [Streetlight]
 * @param isSubGeographicalRegion Handler when the [identifiedObject] is a [SubGeographicalRegion]
 * @param isSubstation Handler when the [identifiedObject] is a [Substation]
 * @param isTerminal Handler when the [identifiedObject] is a [Terminal]
 * @param isUsagePoint Handler when the [identifiedObject] is a [UsagePoint]
 * @param isControl Handler when the [identifiedObject] is a [Control]
 * @param isAnalog Handler when the [identifiedObject] is an [Analog]
 * @param isAccumulator Handler when the [identifiedObject] is an [Accumulator]
 * @param isDiscrete Handler when the [identifiedObject] is a [Discrete]
 * @param isRemoteControl Handler when the [identifiedObject] is a [RemoteControl]
 * @param isRemoteSource Handler when the [identifiedObject] is a [RemoteSource]
 * @param isOther Handler when the [identifiedObject] is not supported by the [CustomerService].
 */
@JvmOverloads
inline fun <R> whenNetworkServiceObject(
    identifiedObject: IdentifiedObject,
    isAcLineSegment: (AcLineSegment) -> R,
    isAssetOwner: (AssetOwner) -> R,
    isBaseVoltage: (BaseVoltage) -> R,
    isBreaker: (Breaker) -> R,
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
    isMeter: (Meter) -> R,
    isOperationalRestriction: (OperationalRestriction) -> R,
    isOrganisation: (Organisation) -> R,
    isOverheadWireInfo: (OverheadWireInfo) -> R,
    isPerLengthSequenceImpedance: (PerLengthSequenceImpedance) -> R,
    isPole: (Pole) -> R,
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
    isOther: (IdentifiedObject) -> R = { idObj: IdentifiedObject ->
        throw IllegalArgumentException("Identified object type ${idObj::class} is not supported by the network service")
    }
): R = when (identifiedObject) {
    is AcLineSegment -> isAcLineSegment(identifiedObject)
    is AssetOwner -> isAssetOwner(identifiedObject)
    is BaseVoltage -> isBaseVoltage(identifiedObject)
    is Breaker -> isBreaker(identifiedObject)
    is CableInfo -> isCableInfo(identifiedObject)
    is Circuit -> isCircuit(identifiedObject)
    is ConnectivityNode -> isConnectivityNode(identifiedObject)
    is Disconnector -> isDisconnector(identifiedObject)
    is EnergyConsumer -> isEnergyConsumer(identifiedObject)
    is EnergyConsumerPhase -> isEnergyConsumerPhase(identifiedObject)
    is EnergySource -> isEnergySource(identifiedObject)
    is EnergySourcePhase -> isEnergySourcePhase(identifiedObject)
    is FaultIndicator -> isFaultIndicator(identifiedObject)
    is Feeder -> isFeeder(identifiedObject)
    is Fuse -> isFuse(identifiedObject)
    is GeographicalRegion -> isGeographicalRegion(identifiedObject)
    is Jumper -> isJumper(identifiedObject)
    is Junction -> isJunction(identifiedObject)
    is LinearShuntCompensator -> isLinearShuntCompensator(identifiedObject)
    is Location -> isLocation(identifiedObject)
    is Loop -> isLoop(identifiedObject)
    is Meter -> isMeter(identifiedObject)
    is OperationalRestriction -> isOperationalRestriction(identifiedObject)
    is Organisation -> isOrganisation(identifiedObject)
    is OverheadWireInfo -> isOverheadWireInfo(identifiedObject)
    is PerLengthSequenceImpedance -> isPerLengthSequenceImpedance(identifiedObject)
    is Pole -> isPole(identifiedObject)
    is PowerTransformer -> isPowerTransformer(identifiedObject)
    is PowerTransformerEnd -> isPowerTransformerEnd(identifiedObject)
    is PowerTransformerInfo -> isPowerTransformerInfo(identifiedObject)
    is RatioTapChanger -> isRatioTapChanger(identifiedObject)
    is Recloser -> isRecloser(identifiedObject)
    is Site -> isSite(identifiedObject)
    is Streetlight -> isStreetlight(identifiedObject)
    is SubGeographicalRegion -> isSubGeographicalRegion(identifiedObject)
    is Substation -> isSubstation(identifiedObject)
    is Terminal -> isTerminal(identifiedObject)
    is UsagePoint -> isUsagePoint(identifiedObject)
    is Control -> isControl(identifiedObject)
    is Analog -> isAnalog(identifiedObject)
    is Accumulator -> isAccumulator(identifiedObject)
    is Discrete -> isDiscrete(identifiedObject)
    is RemoteControl -> isRemoteControl(identifiedObject)
    is RemoteSource -> isRemoteSource(identifiedObject)
    else -> isOther(identifiedObject)
}

/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.testdata

import com.zepben.evolve.cim.extensions.iec61968.metering.PanDemandResponseFunction
import com.zepben.evolve.cim.extensions.iec61970.base.wires.BatteryControl
import com.zepben.evolve.cim.extensions.iec61970.base.wires.BatteryControlMode
import com.zepben.evolve.cim.iec61968.assetinfo.*
import com.zepben.evolve.cim.iec61968.assets.*
import com.zepben.evolve.cim.iec61968.common.*
import com.zepben.evolve.cim.iec61968.infiec61968.infassetinfo.*
import com.zepben.evolve.cim.iec61968.infiec61968.infcommon.Ratio
import com.zepben.evolve.cim.iec61968.metering.*
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
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.LvFeeder
import com.zepben.evolve.cim.iec61970.infiec61970.protection.PowerDirectionKind
import com.zepben.evolve.cim.iec61970.infiec61970.protection.ProtectionKind
import com.zepben.evolve.cim.iec61970.infiec61970.wires.generation.production.EvChargingUnit
import com.zepben.evolve.services.common.testdata.fillFieldsCommon
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.tracing.feeder.FeederDirection
import java.time.Instant
import java.util.*

/************ EXTENSION IEC61968 METERING ************/

fun PanDemandResponseFunction.fillFields(service: NetworkService, includeRuntime: Boolean = true): PanDemandResponseFunction {
    (this as EndDeviceFunction).fillFields(service, includeRuntime)

    kind = EndDeviceFunctionKind.autonomousDst
    controlledAppliance = ControlledAppliance(
        ControlledAppliance.Appliance.ELECTRIC_VEHICLE,
        ControlledAppliance.Appliance.GENERATION_SYSTEM,
        ControlledAppliance.Appliance.INTERIOR_LIGHTING,
        ControlledAppliance.Appliance.MANAGED_COMMERCIAL_INDUSTRIAL_LOAD,
        ControlledAppliance.Appliance.SIMPLE_MISC_LOAD,
        ControlledAppliance.Appliance.STRIP_AND_BASEBOARD_HEATER,
    )

    return this
}

/************ EXTENSION IEC61970 BASE WIRES ************/

fun BatteryControl.fillFields(service: NetworkService, includeRuntime: Boolean = true): BatteryControl {
    (this as RegulatingControl).fillFields(service, includeRuntime)

    chargingRate = 1.0
    dischargingRate = 2.0
    reservePercent = 3.0
    controlMode = BatteryControlMode.time

    return this
}

/************ IEC61968 ASSET INFO ************/

fun AssetInfo.fillFields(service: NetworkService, includeRuntime: Boolean = true): AssetInfo {
    (this as IdentifiedObject).fillFieldsCommon(service, includeRuntime)
    return this
}

fun CableInfo.fillFields(service: NetworkService, includeRuntime: Boolean = true): CableInfo {
    (this as WireInfo).fillFields(service, includeRuntime)
    return this
}

fun NoLoadTest.fillFields(service: NetworkService, includeRuntime: Boolean = true): NoLoadTest {
    (this as TransformerTest).fillFields(service, includeRuntime)

    energisedEndVoltage = 1
    excitingCurrent = 2.2
    excitingCurrentZero = 3.3
    loss = 4
    lossZero = 5

    return this
}

fun OpenCircuitTest.fillFields(service: NetworkService, includeRuntime: Boolean = true): OpenCircuitTest {
    (this as TransformerTest).fillFields(service, includeRuntime)

    energisedEndStep = 1
    energisedEndVoltage = 2
    openEndStep = 3
    openEndVoltage = 4
    phaseShift = 5.5

    return this
}

fun OverheadWireInfo.fillFields(service: NetworkService, includeRuntime: Boolean = true): OverheadWireInfo {
    (this as WireInfo).fillFields(service, includeRuntime)
    return this
}

fun PowerTransformerInfo.fillFields(service: NetworkService, includeRuntime: Boolean = true): PowerTransformerInfo {
    (this as AssetInfo).fillFields(service, includeRuntime)

    @Suppress("unused")
    for (i in 0..1) {
        addTransformerTankInfo(TransformerTankInfo().also {
            it.powerTransformerInfo = this
            service.add(it)
        })
    }

    return this
}

fun ShortCircuitTest.fillFields(service: NetworkService, includeRuntime: Boolean = true): ShortCircuitTest {
    (this as TransformerTest).fillFields(service, includeRuntime)

    current = 1.1
    energisedEndStep = 2
    groundedEndStep = 3
    leakageImpedance = 4.4
    leakageImpedanceZero = 5.5
    loss = 6
    lossZero = 7
    power = 8
    voltage = 9.9
    voltageOhmicPart = 10.01

    return this
}

fun ShuntCompensatorInfo.fillFields(service: NetworkService, includeRuntime: Boolean = true): ShuntCompensatorInfo {
    (this as AssetInfo).fillFields(service, includeRuntime)

    maxPowerLoss = 1
    ratedCurrent = 2
    ratedReactivePower = 3
    ratedVoltage = 4

    return this
}

fun SwitchInfo.fillFields(service: NetworkService, includeRuntime: Boolean = true): SwitchInfo {
    (this as AssetInfo).fillFields(service, includeRuntime)

    ratedInterruptingTime = 1.1

    return this
}

fun TransformerEndInfo.fillFields(service: NetworkService, includeRuntime: Boolean = true): TransformerEndInfo {
    (this as AssetInfo).fillFields(service, includeRuntime)

    connectionKind = WindingConnection.D
    emergencyS = 1
    endNumber = 2
    insulationU = 3
    phaseAngleClock = 4
    r = 5.0
    ratedS = 6
    ratedU = 7
    shortTermS = 8

    transformerTankInfo = TransformerTankInfo().also {
        it.addTransformerEndInfo(this)
        service.add(it)
    }
    transformerStarImpedance = TransformerStarImpedance().also {
        it.transformerEndInfo = this
        service.add(it)
    }

    energisedEndNoLoadTests = NoLoadTest().also { service.add(it) }
    energisedEndShortCircuitTests = ShortCircuitTest().also { service.add(it) }
    groundedEndShortCircuitTests = ShortCircuitTest().also { service.add(it) }
    openEndOpenCircuitTests = OpenCircuitTest().also { service.add(it) }
    energisedEndOpenCircuitTests = OpenCircuitTest().also { service.add(it) }

    return this
}

fun TransformerTankInfo.fillFields(service: NetworkService, includeRuntime: Boolean = true): TransformerTankInfo {
    (this as AssetInfo).fillFields(service, includeRuntime)

    powerTransformerInfo = PowerTransformerInfo().also {
        it.addTransformerTankInfo(this)
        service.add(it)
    }

    @Suppress("unused")
    for (i in 0..1) {
        addTransformerEndInfo(TransformerEndInfo().also {
            it.transformerTankInfo = this
            service.add(it)
        })
    }

    return this
}

fun TransformerTest.fillFields(service: NetworkService, includeRuntime: Boolean = true): TransformerTest {
    (this as IdentifiedObject).fillFieldsCommon(service, includeRuntime)

    basePower = 1
    temperature = 2.2

    return this
}

fun WireInfo.fillFields(service: NetworkService, includeRuntime: Boolean = true): WireInfo {
    (this as AssetInfo).fillFields(service, includeRuntime)

    material = WireMaterialKind.aaac
    ratedCurrent = 123

    return this
}

/************ IEC61968 ASSETS ************/

fun Asset.fillFields(service: NetworkService, includeRuntime: Boolean = true): Asset {
    (this as IdentifiedObject).fillFieldsCommon(service, includeRuntime)

    addOrganisationRole(AssetOwner().also { service.add(it) })
    location = Location().also { service.add(it) }

    return this
}

fun AssetContainer.fillFields(service: NetworkService, includeRuntime: Boolean = true): AssetContainer {
    (this as Asset).fillFields(service, includeRuntime)
    return this
}

fun AssetFunction.fillFields(service: NetworkService, includeRuntime: Boolean = true): AssetFunction {
    (this as IdentifiedObject).fillFieldsCommon(service, includeRuntime)
    return this
}

fun AssetOrganisationRole.fillFields(service: NetworkService, includeRuntime: Boolean = true): AssetOrganisationRole {
    (this as OrganisationRole).fillFieldsCommon(service, includeRuntime)
    return this
}

fun AssetOwner.fillFields(service: NetworkService, includeRuntime: Boolean = true): AssetOwner {
    (this as AssetOrganisationRole).fillFields(service, includeRuntime)
    return this
}

fun Pole.fillFields(service: NetworkService, includeRuntime: Boolean = true): Pole {
    (this as Structure).fillFields(service, includeRuntime)

    classification = "classification"

    addStreetlight(Streetlight().also {
        it.pole = this
        service.add(it)
    })

    return this
}

fun Streetlight.fillFields(service: NetworkService, includeRuntime: Boolean = true): Streetlight {
    (this as Asset).fillFields(service, includeRuntime)

    pole = Pole().also {
        it.addStreetlight(this)
        service.add(it)
    }
    lampKind = StreetlightLampKind.MERCURY_VAPOR
    lightRating = 20

    return this
}

fun Structure.fillFields(service: NetworkService, includeRuntime: Boolean = true): Structure {
    (this as AssetContainer).fillFields(service, includeRuntime)
    return this
}

/************ IEC61968 COMMON ************/

fun Location.fillFields(service: NetworkService, includeRuntime: Boolean = true): Location {
    (this as IdentifiedObject).fillFieldsCommon(service, includeRuntime)

    mainAddress = StreetAddress("1234", TownDetail("town", "state"), "5678", StreetDetail("a", "b", "c", "d", "e", "f", "g"))
    for (i in 0..1)
        addPoint(PositionPoint(i.toDouble(), i.toDouble()))

    return this
}

/************ IEC61968 infIEC61968 InfAssetInfo ************/

fun RelayInfo.fillFields(service: NetworkService, includeRuntime: Boolean = true): RelayInfo {
    (this as AssetInfo).fillFields(service, includeRuntime)

    curveSetting = "curveSetting"
    recloseFast = true
    addDelays(1.0, 2.0, 3.0)

    return this
}

fun CurrentTransformerInfo.fillFields(service: NetworkService, includeRuntime: Boolean = true): CurrentTransformerInfo {
    (this as AssetInfo).fillFields(service, includeRuntime)

    accuracyClass = "accuracyClass"
    accuracyLimit = 1.1
    coreCount = 2
    ctClass = "ctClass"
    kneePointVoltage = 3
    maxRatio = Ratio(4.4, 5.5)
    nominalRatio = Ratio(6.6, 7.7)
    primaryRatio = 8.8
    ratedCurrent = 9
    secondaryFlsRating = 10
    secondaryRatio = 11.11
    usage = "usage"

    return this
}

fun PotentialTransformerInfo.fillFields(service: NetworkService, includeRuntime: Boolean = true): PotentialTransformerInfo {
    (this as AssetInfo).fillFields(service, includeRuntime)

    accuracyClass = "accuracyClass"
    nominalRatio = Ratio(1.1, 2.2)
    primaryRatio = 3.3
    ptClass = "ptClass"
    ratedVoltage = 4
    secondaryRatio = 5.5

    return this
}

/************ IEC61968 METERING ************/

fun EndDevice.fillFields(service: NetworkService, includeRuntime: Boolean = true): EndDevice {
    (this as AssetContainer).fillFields(service, includeRuntime)

    @Suppress("unused")
    for (i in 0..1) {
        addUsagePoint(UsagePoint().also {
            it.addEndDevice(this)
            service.add(it)
        })
    }

    customerMRID = UUID.randomUUID().toString()
    serviceLocation = Location().also { service.add(it) }

    addFunction(PanDemandResponseFunction().also { service.add(it) })

    return this
}

fun EndDeviceFunction.fillFields(service: NetworkService, includeRuntime: Boolean = true): EndDeviceFunction {
    (this as AssetFunction).fillFields(service, includeRuntime)

    enabled = false

    return this
}

fun Meter.fillFields(service: NetworkService, includeRuntime: Boolean = true): Meter {
    (this as EndDevice).fillFields(service, includeRuntime)
    return this
}

fun UsagePoint.fillFields(service: NetworkService, includeRuntime: Boolean = true): UsagePoint {
    (this as IdentifiedObject).fillFieldsCommon(service, includeRuntime)

    usagePointLocation = Location().also { service.add(it) }
    isVirtual = true
    connectionCategory = "connectionCategory"
    ratedPower = 2000
    approvedInverterCapacity = 5000
    phaseCode = PhaseCode.AN

    @Suppress("unused")
    for (i in 0..1) {
        addEquipment(Junction().also {
            it.addUsagePoint(this)
            service.add(it)
        })

        addEndDevice(Meter().also {
            it.addUsagePoint(this)
            service.add(it)
        })
    }

    return this
}

/************ IEC61968 OPERATIONS ************/

fun OperationalRestriction.fillFields(service: NetworkService, includeRuntime: Boolean = true): OperationalRestriction {
    (this as Document).fillFieldsCommon(service, includeRuntime)

    @Suppress("unused")
    for (i in 0..1) {
        addEquipment(Junction().also {
            it.addOperationalRestriction(this)
            service.add(it)
        })
    }

    return this
}

/************ IEC61970 BASE AUXILIARY EQUIPMENT ************/

fun AuxiliaryEquipment.fillFields(service: NetworkService, includeRuntime: Boolean = true): AuxiliaryEquipment {
    (this as Equipment).fillFields(service, includeRuntime)

    terminal = Terminal().also {
        service.add(it)
    }

    return this
}

fun CurrentTransformer.fillFields(service: NetworkService, includeRuntime: Boolean = true): CurrentTransformer {
    (this as Sensor).fillFields(service, includeRuntime)

    assetInfo = CurrentTransformerInfo().also {
        service.add(it)
    }
    coreBurden = 1

    return this
}

fun FaultIndicator.fillFields(service: NetworkService, includeRuntime: Boolean = true): FaultIndicator {
    (this as AuxiliaryEquipment).fillFields(service, includeRuntime)
    return this
}

fun PotentialTransformer.fillFields(service: NetworkService, includeRuntime: Boolean = true): PotentialTransformer {
    (this as Sensor).fillFields(service, includeRuntime)

    assetInfo = PotentialTransformerInfo().also {
        service.add(it)
    }
    type = PotentialTransformerKind.capacitiveCoupling

    return this
}

fun Sensor.fillFields(service: NetworkService, includeRuntime: Boolean = true): Sensor {
    (this as AuxiliaryEquipment).fillFields(service, includeRuntime)

    @Suppress("unused")
    for (i in 0..1) {
        addRelayFunction(CurrentRelay().also {
            it.addSensor(this)
            service.add(it)
        })
    }

    return this
}

/************ IEC61970 BASE CORE ************/

fun AcDcTerminal.fillFields(service: NetworkService, includeRuntime: Boolean = true): AcDcTerminal {
    (this as IdentifiedObject).fillFieldsCommon(service, includeRuntime)
    return this
}

fun BaseVoltage.fillFields(service: NetworkService, includeRuntime: Boolean = true): BaseVoltage {
    (this as IdentifiedObject).fillFieldsCommon(service, includeRuntime)

    nominalVoltage = 1

    return this
}

fun ConnectivityNodeContainer.fillFields(service: NetworkService, includeRuntime: Boolean = true): ConnectivityNodeContainer {
    (this as PowerSystemResource).fillFields(service, includeRuntime)
    return this
}

fun ConnectivityNode.fillFields(service: NetworkService, includeRuntime: Boolean = true): ConnectivityNode {
    (this as IdentifiedObject).fillFieldsCommon(service, includeRuntime)

    @Suppress("unused")
    for (i in 0..1)
        addTerminal(Terminal().also {
            it.connectivityNode = this
            service.add(it)
        })

    return this
}

fun EquipmentContainer.fillFields(service: NetworkService, includeRuntime: Boolean = true): EquipmentContainer {
    (this as ConnectivityNodeContainer).fillFields(service, includeRuntime)

    @Suppress("unused")
    for (i in 0..1)
        addEquipment(Junction().also {
            it.addContainer(this)
            service.add(it)
        })

    return this
}

fun Feeder.fillFields(service: NetworkService, includeRuntime: Boolean = true): Feeder {
    (this as EquipmentContainer).fillFields(service, includeRuntime)

    normalHeadTerminal = Terminal().also { service.add(it) }
    normalEnergizingSubstation = Substation().also {
        it.addFeeder(this)
        service.add(it)
    }

    if (includeRuntime) {
        @Suppress("unused")
        for (i in 0..1)
            addNormalEnergizedLvFeeder(LvFeeder().also {
                it.addNormalEnergizingFeeder(this)
                service.add(it)
            })

        @Suppress("unused")
        for (i in 0..1)
            addCurrentEquipment(Junction().also {
                it.addCurrentContainer(this)
                service.add(it)
            })
    } else {
        equipment.forEach { it.removeContainer(this) }
        clearEquipment()
    }

    return this
}

fun GeographicalRegion.fillFields(service: NetworkService, includeRuntime: Boolean = true): GeographicalRegion {
    (this as IdentifiedObject).fillFieldsCommon(service, includeRuntime)

    @Suppress("unused")
    for (i in 0..1)
        addSubGeographicalRegion(SubGeographicalRegion().also { service.add(it) })

    return this
}

fun PowerSystemResource.fillFields(service: NetworkService, includeRuntime: Boolean = true): PowerSystemResource {
    (this as IdentifiedObject).fillFieldsCommon(service, includeRuntime)

    location = Location().apply { addPoint(PositionPoint(3.3, 4.4)) }.also { service.add(it) }
    numControls = 5

    return this
}

fun Equipment.fillFields(service: NetworkService, includeRuntime: Boolean = true): Equipment {
    (this as PowerSystemResource).fillFields(service, includeRuntime)

    inService = false
    normallyInService = false
    commissionedDate = Instant.MIN

    @Suppress("unused")
    for (i in 0..1) {
        addUsagePoint(UsagePoint().also {
            it.addEquipment(this)
            service.add(it)
        })

        addOperationalRestriction(OperationalRestriction().also {
            it.addEquipment(this)
            service.add(it)
        })

        addContainer(Circuit().also {
            it.addEquipment(this)
            service.add(it)
        })

        if (includeRuntime) {
            addCurrentContainer(Feeder().also {
                it.addEquipment(this)
                service.add(it)
            })
        }
    }

    return this
}

fun ConductingEquipment.fillFields(service: NetworkService, includeRuntime: Boolean = true): ConductingEquipment {
    (this as Equipment).fillFields(service, includeRuntime)

    baseVoltage = BaseVoltage().also { service.add(it) }

    @Suppress("unused")
    for (i in 0..1)
        addTerminal(Terminal().also { service.add(it) })

    return this
}

fun Site.fillFields(service: NetworkService, includeRuntime: Boolean = true): Site {
    (this as EquipmentContainer).fillFields(service, includeRuntime)
    return this
}

fun SubGeographicalRegion.fillFields(service: NetworkService, includeRuntime: Boolean = true): SubGeographicalRegion {
    (this as IdentifiedObject).fillFieldsCommon(service, includeRuntime)

    geographicalRegion = GeographicalRegion().also {
        it.addSubGeographicalRegion(this)
        service.add(it)
    }

    @Suppress("unused")
    for (i in 0..1)
        addSubstation(Substation().also { service.add(it) })

    return this
}

fun Substation.fillFields(service: NetworkService, includeRuntime: Boolean = true): Substation {
    (this as EquipmentContainer).fillFields(service, includeRuntime)

    subGeographicalRegion = SubGeographicalRegion().also {
        it.addSubstation(this)
        service.add(it)
    }

    @Suppress("unused")
    for (i in 0..1) {
        addFeeder(Feeder().also { service.add(it) })

        addLoop(Loop().also {
            it.addSubstation(this)
            service.add(it)
        })

        addEnergizedLoop(Loop().also {
            it.addEnergizingSubstation(this)
            service.add(it)
        })

        addCircuit(Circuit().also {
            it.addEndSubstation(this)
            service.add(it)
        })
    }

    return this
}

fun Terminal.fillFields(service: NetworkService, includeRuntime: Boolean = true): Terminal {
    (this as AcDcTerminal).fillFields(service, includeRuntime)

    conductingEquipment = Junction().also { service.add(it) }
    conductingEquipment?.addTerminal(this)

    phases = PhaseCode.X
    sequenceNumber = 1
    connectivityNode = ConnectivityNode().also {
        it.addTerminal(this)
        service.add(it)
    }

    if (includeRuntime) {
        normalPhases.phaseStatusInternal = 1u
        currentPhases.phaseStatusInternal = 2u
        normalFeederDirection = FeederDirection.UPSTREAM
        currentFeederDirection = FeederDirection.DOWNSTREAM
    }

    return this
}

/************ IEC61970 BASE EQUIVALENTS ************/

fun EquivalentBranch.fillFields(service: NetworkService, includeRuntime: Boolean = true): EquivalentBranch {
    (this as EquivalentEquipment).fillFields(service, includeRuntime)

    negativeR12 = 1.1
    negativeR21 = 2.2
    negativeX12 = 3.3
    negativeX21 = 4.4
    positiveR12 = 5.5
    positiveR21 = 6.6
    positiveX12 = 7.7
    positiveX21 = 8.8
    r = 9.9
    r21 = 10.01
    x = 11.11
    x21 = 12.21
    zeroR12 = 13.31
    zeroR21 = 14.41
    zeroX12 = 15.51
    zeroX21 = 16.61

    return this
}

fun EquivalentEquipment.fillFields(service: NetworkService, includeRuntime: Boolean = true): EquivalentEquipment {
    (this as ConductingEquipment).fillFields(service, includeRuntime)
    return this
}

/************ IEC61970 BASE MEAS ************/

fun Analog.fillFields(service: NetworkService, includeRuntime: Boolean = true): Analog {
    (this as Measurement).fillFields(service, includeRuntime)

    positiveFlowIn = true

    return this
}

fun Accumulator.fillFields(service: NetworkService, includeRuntime: Boolean = true): Accumulator {
    (this as Measurement).fillFields(service, includeRuntime)
    return this
}

fun Control.fillFields(service: NetworkService, includeRuntime: Boolean = true): Control {
    (this as IoPoint).fillFields(service, includeRuntime)

    powerSystemResourceMRID = "1234"
    remoteControl = RemoteControl().also {
        it.control = this
        service.add((it))
    }

    return this
}

fun Discrete.fillFields(service: NetworkService, includeRuntime: Boolean = true): Discrete {
    (this as Measurement).fillFields(service, includeRuntime)
    return this
}

fun IoPoint.fillFields(service: NetworkService, includeRuntime: Boolean = true): IoPoint {
    (this as IdentifiedObject).fillFieldsCommon(service, includeRuntime)
    return this
}

fun Measurement.fillFields(service: NetworkService, includeRuntime: Boolean = true): Measurement {
    (this as IdentifiedObject).fillFieldsCommon(service, includeRuntime)

    powerSystemResourceMRID = PowerTransformer().mRID
    remoteSource = RemoteSource().also {
        remoteSource = it
        it.measurement = this
    }.also {
        service.add(it)
    }

    terminalMRID = Terminal().mRID
    phases = PhaseCode.ABCN
    unitSymbol = UnitSymbol.HENRYS

    return this
}

/************ IEC61970 Base Protection ************/

fun CurrentRelay.fillFields(service: NetworkService, includeRuntime: Boolean = true): CurrentRelay {
    (this as ProtectionRelayFunction).fillFields(service, includeRuntime)

    currentLimit1 = 1.1
    inverseTimeFlag = true
    timeDelay1 = 2.2

    return this
}

fun DistanceRelay.fillFields(service: NetworkService, includeRuntime: Boolean = true): DistanceRelay {
    (this as ProtectionRelayFunction).fillFields(service, includeRuntime)

    backwardBlind = 1.1
    backwardReach = 2.2
    backwardReactance = 3.3
    forwardBlind = 4.4
    forwardReach = 5.5
    forwardReactance = 6.6
    operationPhaseAngle1 = 7.7
    operationPhaseAngle2 = 8.8
    operationPhaseAngle3 = 9.9

    return this
}

fun ProtectionRelayFunction.fillFields(service: NetworkService, includeRuntime: Boolean = true): ProtectionRelayFunction {
    (this as PowerSystemResource).fillFields(service, includeRuntime)

    assetInfo = RelayInfo().also { service.add(it) }

    model = "model"
    reclosing = true
    relayDelayTime = 1.1
    protectionKind = ProtectionKind.DISTANCE
    directable = true
    powerDirection = PowerDirectionKind.FORWARD

    for (i in 0..1) {
        addTimeLimit(i.toDouble())
        addThreshold(RelaySetting(UnitSymbol.entries[i], i.toDouble(), "setting $i"))
        addProtectedSwitch(Breaker().also {
            it.addRelayFunction(this)
            service.add(it)
        })
        addSensor(CurrentTransformer().also {
            it.addRelayFunction(this)
            service.add(it)
        })
        addScheme(ProtectionRelayScheme().also {
            it.addFunction(this)
            service.add(it)
        })
    }

    return this
}

fun ProtectionRelayScheme.fillFields(service: NetworkService, includeRuntime: Boolean = true): ProtectionRelayScheme {
    (this as IdentifiedObject).fillFieldsCommon(service, includeRuntime)

    system = ProtectionRelaySystem().also {
        it.addScheme(this)
        service.add(it)
    }

    @Suppress("unused")
    for (i in 0..1) {
        addFunction(CurrentRelay().also {
            it.addScheme(this)
            service.add(it)
        })
    }

    return this
}

fun ProtectionRelaySystem.fillFields(service: NetworkService, includeRuntime: Boolean = true): ProtectionRelaySystem {
    (this as Equipment).fillFieldsCommon(service, includeRuntime)

    protectionKind = ProtectionKind.DISTANCE

    @Suppress("unused")
    for (i in 0..1) {
        addScheme(ProtectionRelayScheme().also {
            it.system = this
            service.add(it)
        })
    }

    return this
}

fun VoltageRelay.fillFields(service: NetworkService, includeRuntime: Boolean = true): VoltageRelay {
    (this as ProtectionRelayFunction).fillFields(service, includeRuntime)
    return this
}


/************ IEC61970 BASE SCADA ************/

fun RemoteControl.fillFields(service: NetworkService, includeRuntime: Boolean = true): RemoteControl {
    (this as RemotePoint).fillFields(service, includeRuntime)

    control = Control().also {
        it.remoteControl = this
        service.add(it)
    }

    return this
}

fun RemotePoint.fillFields(service: NetworkService, includeRuntime: Boolean = true): RemotePoint {
    (this as IdentifiedObject).fillFieldsCommon(service, includeRuntime)
    return this
}

fun RemoteSource.fillFields(service: NetworkService, includeRuntime: Boolean = true): RemoteSource {
    (this as RemotePoint).fillFields(service, includeRuntime)

    measurement = Discrete().also {
        it.remoteSource = this
        service.add(it)
    }

    return this
}

/************ IEC61970 BASE WIRES GENERATION PRODUCTION ************/

fun BatteryUnit.fillFields(service: NetworkService, includeRuntime: Boolean = true): BatteryUnit {
    (this as PowerElectronicsUnit).fillFields(service, includeRuntime)

    batteryState = BatteryStateKind.charging
    ratedE = 1L
    storedE = 2L

    addControl(BatteryControl().also { service.add(it) })

    return this
}

fun PhotoVoltaicUnit.fillFields(service: NetworkService, includeRuntime: Boolean = true): PhotoVoltaicUnit {
    (this as PowerElectronicsUnit).fillFields(service, includeRuntime)
    return this
}

fun PowerElectronicsConnection.fillFields(service: NetworkService, includeRuntime: Boolean = true): PowerElectronicsConnection {
    (this as RegulatingCondEq).fillFields(service, includeRuntime)

    maxIFault = 1
    maxQ = 2.0
    minQ = 3.0
    p = 4.0
    q = 5.0
    ratedS = 6
    ratedU = 7
    inverterStandard = "TEST"
    sustainOpOvervoltLimit = 8
    stopAtOverFreq = 10.0f
    stopAtUnderFreq = 5.0f
    invVoltWattRespMode = false
    invWattRespV1 = 200
    invWattRespV2 = 216
    invWattRespV3 = 235
    invWattRespV4 = 244
    invWattRespPAtV1 = 0.1f
    invWattRespPAtV2 = 0.2f
    invWattRespPAtV3 = 0.3f
    invWattRespPAtV4 = 0.1f
    invVoltVarRespMode = false
    invVarRespV1 = 200
    invVarRespV2 = 200
    invVarRespV3 = 300
    invVarRespV4 = 300
    invVarRespQAtV1 = 0.6f
    invVarRespQAtV2 = -1.0f
    invVarRespQAtV3 = 1.0f
    invVarRespQAtV4 = -0.6f
    invReactivePowerMode = false
    invFixReactivePower = -1.0f

    return this
}

fun PowerElectronicsConnectionPhase.fillFields(service: NetworkService, includeRuntime: Boolean = true): PowerElectronicsConnectionPhase {
    (this as PowerSystemResource).fillFields(service, includeRuntime)

    powerElectronicsConnection = PowerElectronicsConnection().also {
        it.addPhase(this)
        service.add(it)
    }
    p = 1.0
    phase = SinglePhaseKind.B
    q = 2.0

    return this
}

fun PowerElectronicsUnit.fillFields(service: NetworkService, includeRuntime: Boolean = true): PowerElectronicsUnit {
    (this as Equipment).fillFields(service, includeRuntime)

    powerElectronicsConnection = PowerElectronicsConnection().also {
        it.addUnit(this)
        service.add(it)
    }
    maxP = 1
    minP = 2

    return this
}

fun PowerElectronicsWindUnit.fillFields(service: NetworkService, includeRuntime: Boolean = true): PowerElectronicsWindUnit {
    (this as PowerElectronicsUnit).fillFields(service, includeRuntime)
    return this
}

/************ IEC61970 BASE WIRES ************/

fun AcLineSegment.fillFields(service: NetworkService, includeRuntime: Boolean = true): AcLineSegment {
    (this as Conductor).fillFields(service, includeRuntime)

    perLengthSequenceImpedance = PerLengthSequenceImpedance().also { service.add(it) }

    return this
}

fun Breaker.fillFields(service: NetworkService, includeRuntime: Boolean = true): Breaker {
    (this as ProtectedSwitch).fillFields(service, includeRuntime)

    inTransitTime = 1.1

    return this
}

fun BusbarSection.fillFields(service: NetworkService, includeRuntime: Boolean = true): BusbarSection {
    (this as Connector).fillFields(service, includeRuntime)
    return this
}

fun Conductor.fillFields(service: NetworkService, includeRuntime: Boolean = true): Conductor {
    (this as ConductingEquipment).fillFields(service, includeRuntime)

    length = 1.1
    designTemperature = 2
    designRating = 3.3
    assetInfo = CableInfo().also { service.add(it) }

    return this
}

fun Connector.fillFields(service: NetworkService, includeRuntime: Boolean = true): Connector {
    (this as ConductingEquipment).fillFields(service, includeRuntime)
    return this
}

fun Curve.fillFields(service: NetworkService, includeRuntime: Boolean = true): Curve {
    (this as IdentifiedObject).fillFieldsCommon(service, includeRuntime)

    addData(1f, 1f)

    return this
}

fun Disconnector.fillFields(service: NetworkService, includeRuntime: Boolean = true): Disconnector {
    (this as Switch).fillFields(service, includeRuntime)
    return this
}

fun EarthFaultCompensator.fillFields(service: NetworkService, includeRuntime: Boolean = true): EarthFaultCompensator {
    (this as ConductingEquipment).fillFields(service, includeRuntime)

    r = 1.0

    return this
}

fun EnergyConnection.fillFields(service: NetworkService, includeRuntime: Boolean = true): EnergyConnection {
    (this as ConductingEquipment).fillFields(service, includeRuntime)
    return this
}

fun EnergyConsumer.fillFields(service: NetworkService, includeRuntime: Boolean = true): EnergyConsumer {
    (this as EnergyConnection).fillFields(service, includeRuntime)

    for (i in 0..1) {
        addPhase(EnergyConsumerPhase().also {
            it.phase = SinglePhaseKind.get(i)
            service.add(it)
        })
    }

    customerCount = 1
    grounded = true
    p = 2.2
    pFixed = 3.3
    phaseConnection = PhaseShuntConnectionKind.G
    q = 4.4
    qFixed = 5.5

    return this
}

fun EnergyConsumerPhase.fillFields(service: NetworkService, includeRuntime: Boolean = true): EnergyConsumerPhase {
    (this as PowerSystemResource).fillFields(service, includeRuntime)

    energyConsumer = EnergyConsumer().also {
        it.addPhase(this)
        service.add(it)
    }

    phase = SinglePhaseKind.A
    p = 1.1
    pFixed = 2.2
    q = 3.3
    qFixed = 4.4

    return this
}

fun EnergySource.fillFields(service: NetworkService, includeRuntime: Boolean = true): EnergySource {
    (this as EnergyConnection).fillFields(service, includeRuntime)

    for (i in 0..1) {
        addPhase(EnergySourcePhase().also {
            it.phase = SinglePhaseKind.get(i)
            service.add(it)
        })
    }

    activePower = 1.1
    reactivePower = 2.2
    voltageAngle = 3.3
    voltageMagnitude = 4.4
    pMax = 5.5
    pMin = 6.6
    r = 7.7
    r0 = 8.8
    rn = 9.9
    x = 10.01
    x0 = 11.11
    xn = 12.21
    isExternalGrid = true
    rMin = 13.31
    rnMin = 14.41
    r0Min = 15.51
    xMin = 16.61
    xnMin = 17.71
    x0Min = 18.81
    rMax = 19.91
    rnMax = 20.02
    r0Max = 21.12
    xMax = 22.22
    xnMax = 23.32
    x0Max = 24.42

    return this
}

fun EnergySourcePhase.fillFields(service: NetworkService, includeRuntime: Boolean = true): EnergySourcePhase {
    (this as PowerSystemResource).fillFields(service, includeRuntime)

    energySource = EnergySource().also {
        it.addPhase(this)
        service.add(it)
    }

    phase = SinglePhaseKind.A

    return this
}

fun Fuse.fillFields(service: NetworkService, includeRuntime: Boolean = true): Fuse {
    (this as Switch).fillFields(service, includeRuntime)

    function = VoltageRelay().also { service.add(it) }

    return this
}

fun Ground.fillFields(service: NetworkService, includeRuntime: Boolean = true): Ground {
    (this as ConductingEquipment).fillFields(service, includeRuntime)
    return this
}

fun GroundingImpedance.fillFields(service: NetworkService, includeRuntime: Boolean = true): GroundingImpedance {
    (this as EarthFaultCompensator).fillFields(service, includeRuntime)

    x = 1.0

    return this
}

fun GroundDisconnector.fillFields(service: NetworkService, includeRuntime: Boolean = true): GroundDisconnector {
    (this as Switch).fillFields(service, includeRuntime)
    return this
}

fun Jumper.fillFields(service: NetworkService, includeRuntime: Boolean = true): Jumper {
    (this as Switch).fillFields(service, includeRuntime)
    return this
}

fun Junction.fillFields(service: NetworkService, includeRuntime: Boolean = true): Junction {
    (this as Connector).fillFields(service, includeRuntime)
    return this
}

fun Line.fillFields(service: NetworkService, includeRuntime: Boolean = true): Line {
    (this as EquipmentContainer).fillFields(service, includeRuntime)
    return this
}

fun LinearShuntCompensator.fillFields(service: NetworkService, includeRuntime: Boolean = true): LinearShuntCompensator {
    (this as ShuntCompensator).fillFields(service, includeRuntime)

    b0PerSection = 1.1
    bPerSection = 2.2
    g0PerSection = 3.3
    gPerSection = 4.4

    return this
}

fun LoadBreakSwitch.fillFields(service: NetworkService, includeRuntime: Boolean = true): LoadBreakSwitch {
    (this as ProtectedSwitch).fillFields(service, includeRuntime)
    return this
}

fun PerLengthImpedance.fillFields(service: NetworkService, includeRuntime: Boolean = true): PerLengthImpedance {
    (this as PerLengthLineParameter).fillFields(service, includeRuntime)
    return this
}

fun PerLengthLineParameter.fillFields(service: NetworkService, includeRuntime: Boolean = true): PerLengthLineParameter {
    (this as IdentifiedObject).fillFieldsCommon(service, includeRuntime)
    return this
}

fun PerLengthPhaseImpedance.fillFields(service: NetworkService, includeRuntime: Boolean = true): PerLengthPhaseImpedance {
    (this as PerLengthImpedance).fillFields(service, includeRuntime)

    for (i in 0..1)
        addData(PhaseImpedanceData(SinglePhaseKind.get(i), SinglePhaseKind.get(i), i.toDouble(), i.toDouble(), i.toDouble(), i.toDouble()))

    return this
}

fun PerLengthSequenceImpedance.fillFields(service: NetworkService, includeRuntime: Boolean = true): PerLengthSequenceImpedance {
    (this as PerLengthImpedance).fillFields(service, includeRuntime)

    r = 1.1
    x = 2.2
    bch = 3.3
    gch = 4.4
    r0 = 5.5
    x0 = 6.6
    b0ch = 7.7
    g0ch = 8.8

    return this
}

fun PetersenCoil.fillFields(service: NetworkService, includeRuntime: Boolean = true): PetersenCoil {
    (this as EarthFaultCompensator).fillFields(service, includeRuntime)

    xGroundNominal = 1.0

    return this
}

fun PowerTransformer.fillFields(service: NetworkService, includeRuntime: Boolean = true): PowerTransformer {
    (this as ConductingEquipment).fillFields(service, includeRuntime)

    vectorGroup = VectorGroup.DD0
    transformerUtilisation = 1.0
    constructionKind = TransformerConstructionKind.aerial
    function = TransformerFunctionKind.voltageRegulator
    assetInfo = PowerTransformerInfo().also { service.add(it) }

    @Suppress("unused")
    for (i in 0..1) {
        addEnd(PowerTransformerEnd().also {
            it.powerTransformer = this
            service.add(it)
        })
    }

    return this
}

fun PowerTransformerEnd.fillFields(service: NetworkService, includeRuntime: Boolean = true): PowerTransformerEnd {
    (this as TransformerEnd).fillFields(service, includeRuntime)

    powerTransformer = PowerTransformer().also { service.add(it) }
    powerTransformer?.addEnd(this)

    b = 1.0
    b0 = 2.0
    connectionKind = WindingConnection.Zn
    g = 3.0
    g0 = 4.0
    phaseAngleClock = 5
    r = 6.0
    r0 = 7.0
    addRating(8, TransformerCoolingType.UNKNOWN_COOLING_TYPE)
    ratedU = 9
    x = 10.0
    x0 = 11.0

    return this
}

fun ProtectedSwitch.fillFields(service: NetworkService, includeRuntime: Boolean = true): ProtectedSwitch {
    (this as Switch).fillFields(service, includeRuntime)

    breakingCapacity = 1

    @Suppress("unused")
    for (i in 0..1) {
        addRelayFunction(CurrentRelay().also {
            it.addProtectedSwitch(this)
            service.add(it)
        })
    }

    return this
}

fun RatioTapChanger.fillFields(service: NetworkService, includeRuntime: Boolean = true): RatioTapChanger {
    (this as TapChanger).fillFields(service, includeRuntime)

    transformerEnd = PowerTransformerEnd().also {
        it.ratioTapChanger = this
        service.add(it)
    }
    stepVoltageIncrement = 1.1

    return this
}

fun ReactiveCapabilityCurve.fillFields(service: NetworkService, includeRuntime: Boolean = true): ReactiveCapabilityCurve {
    (this as Curve).fillFields(service, includeRuntime)

    return this
}

fun Recloser.fillFields(service: NetworkService, includeRuntime: Boolean = true): Recloser {
    (this as ProtectedSwitch).fillFields(service, includeRuntime)
    return this
}

fun RegulatingCondEq.fillFields(service: NetworkService, includeRuntime: Boolean = true): RegulatingCondEq {
    (this as EnergyConnection).fillFields(service, includeRuntime)

    controlEnabled = false
    regulatingControl = TapChangerControl().also { it.addRegulatingCondEq(this); service.add(it) }

    return this
}

fun RegulatingControl.fillFields(service: NetworkService, includeRuntime: Boolean = true): RegulatingControl {
    (this as PowerSystemResource).fillFields(service, includeRuntime)

    ctPrimary = 1.0
    minTargetDeadband = 2.0
    discrete = false
    mode = RegulatingControlModeKind.voltage
    monitoredPhase = PhaseCode.ABC
    targetDeadband = 2.0f
    targetValue = 100.0
    enabled = true
    maxAllowedTargetValue = 200.0
    minAllowedTargetValue = 50.0
    ratedCurrent = 10.0
    terminal = Terminal().also { service.add(it) }
    addRegulatingCondEq(PowerElectronicsConnection().also { it.regulatingControl = this; service.add(it) })

    return this
}

fun RotatingMachine.fillFields(service: NetworkService, includeRuntime: Boolean = true): RotatingMachine {
    (this as RegulatingCondEq).fillFields(service, includeRuntime)

    ratedPowerFactor = 1.1
    ratedS = 2.2
    ratedU = 3
    p = 4.4
    q = 5.5

    return this
}

fun SeriesCompensator.fillFields(service: NetworkService, includeRuntime: Boolean = true): SeriesCompensator {
    (this as ConductingEquipment).fillFields(service, includeRuntime)

    r = 1.1
    r0 = 2.2
    x = 3.3
    x0 = 4.4
    varistorRatedCurrent = 5
    varistorVoltageThreshold = 6

    return this
}

fun ShuntCompensator.fillFields(service: NetworkService, includeRuntime: Boolean = true): ShuntCompensator {
    (this as RegulatingCondEq).fillFields(service, includeRuntime)

    assetInfo = ShuntCompensatorInfo().also { service.add((it)) }

    grounded = true
    nomU = 1
    phaseConnection = PhaseShuntConnectionKind.I
    sections = 2.2

    return this
}

fun StaticVarCompensator.fillFields(service: NetworkService, includeRuntime: Boolean = true): StaticVarCompensator {
    (this as RegulatingCondEq).fillFields(service, includeRuntime)

    capacitiveRating = 1.0
    inductiveRating = 2.0
    q = 3.0
    svcControlMode = SVCControlMode.reactivePower
    voltageSetPoint = 4

    return this
}

fun Switch.fillFields(service: NetworkService, includeRuntime: Boolean = true): Switch {
    (this as ConductingEquipment).fillFields(service, includeRuntime)

    assetInfo = SwitchInfo().also { service.add(it) }
    ratedCurrent = 1.1

    setNormallyOpen(true)
    setOpen(true)
    // when un-ganged support is added to protobuf
    //    normalOpen = 2
    //    open = 3

    return this
}

fun SynchronousMachine.fillFields(service: NetworkService, includeRuntime: Boolean = true): SynchronousMachine {
    (this as RotatingMachine).fillFields(service, includeRuntime)

    addCurve(ReactiveCapabilityCurve().also { service.add(it) })

    baseQ = 1.1
    condenserP = 2
    earthing = true
    earthingStarPointR = 3.3
    earthingStarPointX = 4.4
    ikk = 5.5
    maxQ = 6.6
    maxU = 7
    minQ = 8.8
    minU = 9
    mu = 10.10
    r = 11.11
    r0 = 12.12
    r2 = 13.13
    satDirectSubtransX = 14.14
    satDirectSyncX = 15.15
    satDirectTransX = 16.16
    x0 = 17.17
    x2 = 18.18
    type = SynchronousMachineKind.generatorOrMotor
    operatingMode = SynchronousMachineKind.generator

    return this
}

fun TapChanger.fillFields(service: NetworkService, includeRuntime: Boolean = true): TapChanger {
    (this as PowerSystemResource).fillFields(service, includeRuntime)

    controlEnabled = false
    highStep = 22
    lowStep = 1
    neutralStep = 2
    neutralU = 3
    normalStep = 4
    step = 5.5
    tapChangerControl = TapChangerControl().also { service.add(it) }

    return this
}

fun TapChangerControl.fillFields(service: NetworkService, includeRuntime: Boolean = true): TapChangerControl {
    (this as RegulatingControl).fillFields(service, includeRuntime)

    limitVoltage = 1000
    lineDropCompensation = true
    lineDropR = 10.0
    lineDropX = 4.0
    reverseLineDropR = 1.0
    reverseLineDropX = 1.0
    forwardLDCBlocking = true
    timeDelay = 5.3
    coGenerationEnabled = false

    return this
}

fun TransformerEnd.fillFields(service: NetworkService, includeRuntime: Boolean = true): TransformerEnd {
    (this as IdentifiedObject).fillFieldsCommon(service, includeRuntime)

    terminal = Terminal().also { service.add(it) }
    ratioTapChanger = RatioTapChanger().also {
        it.transformerEnd = this
        service.add(it)
    }
    baseVoltage = BaseVoltage().also { service.add(it) }
    grounded = true
    rGround = 1.0
    xGround = 2.0
    endNumber = 1
    starImpedance = TransformerStarImpedance().also { service.add(it) }

    return this
}

fun TransformerStarImpedance.fillFields(service: NetworkService, includeRuntime: Boolean = true): TransformerStarImpedance {
    (this as IdentifiedObject).fillFieldsCommon(service, includeRuntime)

    r = 1.0
    r0 = 2.0
    x = 3.0
    x0 = 4.0

    transformerEndInfo = TransformerEndInfo().also {
        it.transformerStarImpedance = this
        service.add(it)
    }

    return this
}

/************ IEC61970 InfIEC61970 Feeder ************/

fun Circuit.fillFields(service: NetworkService, includeRuntime: Boolean = true): Circuit {
    (this as Line).fillFields(service, includeRuntime)

    loop = Loop().also {
        it.addCircuit(this)
        service.add(it)
    }

    @Suppress("unused")
    for (i in 0..1) {
        addEndTerminal(Terminal().also { service.add(it) })
        addEndSubstation(Substation().also {
            it.addCircuit(this)
            service.add(it)
        })
    }


    return this
}

fun Loop.fillFields(service: NetworkService, includeRuntime: Boolean = true): Loop {
    (this as IdentifiedObject).fillFieldsCommon(service, includeRuntime)

    @Suppress("unused")
    for (i in 0..1) {
        addCircuit(Circuit().also {
            it.loop = this
            service.add(it)
        })

        addSubstation(Substation().also {
            it.addLoop(this)
            service.add(it)
        })

        addEnergizingSubstation(Substation().also {
            it.addEnergizedLoop(this)
            service.add(it)
        })
    }

    return this
}

fun LvFeeder.fillFields(service: NetworkService, includeRuntime: Boolean = true): LvFeeder {
    (this as EquipmentContainer).fillFields(service, includeRuntime)

    normalHeadTerminal = Terminal().also { service.add(it) }

    if (includeRuntime) {
        @Suppress("unused")
        for (i in 0..1)
            addNormalEnergizingFeeder(Feeder().also {
                it.addNormalEnergizedLvFeeder(this)
                service.add(it)
            })

        @Suppress("unused")
        for (i in 0..1)
            addCurrentEquipment(Junction().also {
                it.addCurrentContainer(this)
                service.add(it)
            })
    } else {
        equipment.forEach { it.removeContainer(this) }
        clearEquipment()
    }

    return this
}

/************ IEC61970 InfIEC61970 WIRES GENERATION PRODUCTION ************/

fun EvChargingUnit.fillFields(service: NetworkService, includeRuntime: Boolean = true): EvChargingUnit {
    (this as PowerElectronicsUnit).fillFields(service, includeRuntime)
    return this
}

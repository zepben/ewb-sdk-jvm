/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.network.testdata

import com.zepben.ewb.cim.extensions.iec61968.assetinfo.RelayInfo
import com.zepben.ewb.cim.extensions.iec61968.common.ContactDetails
import com.zepben.ewb.cim.extensions.iec61968.common.ContactMethodType
import com.zepben.ewb.cim.extensions.iec61968.metering.PanDemandResponseFunction
import com.zepben.ewb.cim.extensions.iec61970.base.core.HvCustomer
import com.zepben.ewb.cim.extensions.iec61970.base.core.Site
import com.zepben.ewb.cim.extensions.iec61970.base.feeder.Loop
import com.zepben.ewb.cim.extensions.iec61970.base.feeder.LvFeeder
import com.zepben.ewb.cim.extensions.iec61970.base.feeder.LvSubstation
import com.zepben.ewb.cim.extensions.iec61970.base.generation.production.EvChargingUnit
import com.zepben.ewb.cim.extensions.iec61970.base.protection.*
import com.zepben.ewb.cim.extensions.iec61970.base.wires.BatteryControl
import com.zepben.ewb.cim.extensions.iec61970.base.wires.BatteryControlMode
import com.zepben.ewb.cim.extensions.iec61970.base.wires.TransformerCoolingType
import com.zepben.ewb.cim.extensions.iec61970.base.wires.VectorGroup
import com.zepben.ewb.cim.iec61968.assetinfo.*
import com.zepben.ewb.cim.iec61968.assets.*
import com.zepben.ewb.cim.iec61968.common.*
import com.zepben.ewb.cim.iec61968.infiec61968.infassetinfo.CurrentTransformerInfo
import com.zepben.ewb.cim.iec61968.infiec61968.infassetinfo.PotentialTransformerInfo
import com.zepben.ewb.cim.iec61968.infiec61968.infassetinfo.TransformerConstructionKind
import com.zepben.ewb.cim.iec61968.infiec61968.infassetinfo.TransformerFunctionKind
import com.zepben.ewb.cim.iec61968.infiec61968.infassets.Pole
import com.zepben.ewb.cim.iec61968.infiec61968.infassets.StreetlightLampKind
import com.zepben.ewb.cim.iec61968.infiec61968.infcommon.Ratio
import com.zepben.ewb.cim.iec61968.metering.*
import com.zepben.ewb.cim.iec61968.operations.OperationalRestriction
import com.zepben.ewb.cim.iec61970.base.auxiliaryequipment.*
import com.zepben.ewb.cim.iec61970.base.core.*
import com.zepben.ewb.cim.iec61970.base.domain.UnitSymbol
import com.zepben.ewb.cim.iec61970.base.equivalents.EquivalentBranch
import com.zepben.ewb.cim.iec61970.base.equivalents.EquivalentEquipment
import com.zepben.ewb.cim.iec61970.base.generation.production.*
import com.zepben.ewb.cim.iec61970.base.meas.*
import com.zepben.ewb.cim.iec61970.base.protection.CurrentRelay
import com.zepben.ewb.cim.iec61970.base.scada.RemoteControl
import com.zepben.ewb.cim.iec61970.base.scada.RemotePoint
import com.zepben.ewb.cim.iec61970.base.scada.RemoteSource
import com.zepben.ewb.cim.iec61970.base.wires.*
import com.zepben.ewb.cim.iec61970.infiec61970.feeder.Circuit
import com.zepben.ewb.services.common.testdata.fillFieldsCommon
import com.zepben.ewb.services.common.testdata.generateId
import com.zepben.ewb.services.network.NetworkService
import com.zepben.ewb.services.network.tracing.feeder.FeederDirection
import java.time.Instant
import java.util.*

// ##################################
// # Extensions IEC61968 Asset Info #
// ##################################

fun RelayInfo.fillFields(service: NetworkService, includeRuntime: Boolean = true): RelayInfo {
    (this as AssetInfo).fillFields(service, includeRuntime)

    curveSetting = "curveSetting"
    recloseFast = true
    addDelays(1.0, 2.0, 3.0)

    return this
}

// ##############################
// # Extensions IEC61968 Common #
// ##############################

fun ContactDetails.fillFields(): ContactDetails {
    contactAddress = StreetAddress(
        postalCode = "1",
        townDetail = TownDetail(name = "2", stateOrProvince = "3"),
        poBox = "4",
        streetDetail = StreetDetail(
            buildingName = "5",
            floorIdentification = "6",
            name = "7",
            number = "8",
            suiteNumber = "9",
            type = "10",
            displayAddress = "11"
        )
    )
    contactType = "12"
    firstName = "13"
    lastName = "14"
    preferredContactMethod = ContactMethodType.LETTER
    isPrimary = true
    businessName = "15"

    @Suppress("unused")
    for (i in 0..1) {
        addPhoneNumber(
            TelephoneNumber(
                areaCode = "$i-1",
                cityCode = "2",
                countryCode = "3",
                dialOut = "4",
                extension = "5",
                internationalPrefix = "6",
                localNumber = "7",
                isPrimary = true,
                description = "8"
            )
        )

        addElectronicAddress(ElectronicAddress(email1 = "$i-1", isPrimary = true, description = "2"))
    }

    return this
}

// ################################
// # Extensions IEC61968 Metering #
// ################################

fun PanDemandResponseFunction.fillFields(service: NetworkService, includeRuntime: Boolean = true): PanDemandResponseFunction {
    (this as EndDeviceFunction).fillFields(service, includeRuntime)

    kind = EndDeviceFunctionKind.autonomousDst
    appliance = ControlledAppliance(
        ControlledAppliance.Appliance.ELECTRIC_VEHICLE,
        ControlledAppliance.Appliance.GENERATION_SYSTEM,
        ControlledAppliance.Appliance.INTERIOR_LIGHTING,
        ControlledAppliance.Appliance.MANAGED_COMMERCIAL_INDUSTRIAL_LOAD,
        ControlledAppliance.Appliance.SIMPLE_MISC_LOAD,
        ControlledAppliance.Appliance.STRIP_AND_BASEBOARD_HEATER,
    )

    return this
}

// ################################
// # Extension IEC61970 Base Core #
// ################################

fun HvCustomer.fillFields(service: NetworkService, includeRuntime: Boolean = true): HvCustomer {
    (this as EquipmentContainer).fillFields(service, includeRuntime)
    return this
}

fun Site.fillFields(service: NetworkService, includeRuntime: Boolean = true): Site {
    (this as EquipmentContainer).fillFields(service, includeRuntime)
    return this
}

// ##################################
// # Extension IEC61970 Base Feeder #
// ##################################

fun Loop.fillFields(service: NetworkService, includeRuntime: Boolean = true): Loop {
    (this as IdentifiedObject).fillFieldsCommon(service, includeRuntime)

    @Suppress("unused")
    for (i in 0..1) {
        addCircuit(Circuit(generateId()).also {
            it.loop = this
            service.add(it)
        })

        addSubstation(Substation(generateId()).also {
            it.addLoop(this)
            service.add(it)
        })

        addEnergizingSubstation(Substation(generateId()).also {
            it.addEnergizedLoop(this)
            service.add(it)
        })
    }

    return this
}

fun LvFeeder.fillFields(service: NetworkService, includeRuntime: Boolean = true): LvFeeder {
    (this as EquipmentContainer).fillFields(service, includeRuntime)

    normalHeadTerminal = Terminal(generateId()).apply {
        conductingEquipment = equipment.filterIsInstance<ConductingEquipment>().first()
        conductingEquipment!!.addTerminal(this)
    }.also { service.add(it) }

    normalEnergizingLvSubstation = LvSubstation(generateId()).also {
        it.addNormalEnergizedLvFeeder(this)
        service.add(it)
    }

    if (includeRuntime) {
        @Suppress("unused")
        for (i in 0..1) {
            addNormalEnergizingFeeder(Feeder(generateId()).also {
                it.addNormalEnergizedLvFeeder(this)
                service.add(it)
            })

            addCurrentEnergizingFeeder(Feeder(generateId()).also {
                it.addCurrentEnergizedLvFeeder(this)
                service.add(it)
            })

            addCurrentEquipment(Junction(generateId()).also {
                it.addCurrentContainer(this)
                service.add(it)
            })
        }
    } else {
        equipment.forEach { it.removeContainer(this) }
        clearEquipment()
    }

    return this
}

fun LvSubstation.fillFields(service: NetworkService, includeRuntime: Boolean = true): LvSubstation {
    (this as EquipmentContainer).fillFields(service, includeRuntime)

    if (includeRuntime) {
        @Suppress("unused")
        for (i in 0..1) {
            addNormalEnergizingFeeder(Feeder(generateId()).also {
                it.addNormalEnergizedLvSubstation(this)
                service.add(it)
            })

            addCurrentEnergizingFeeder(Feeder(generateId()).also {
                it.addCurrentEnergizedLvSubstation(this)
                service.add(it)
            })

            addCurrentEquipment(Junction(generateId()).also {
                it.addCurrentContainer(this)
                service.add(it)
            })

            addNormalEnergizedLvFeeder(LvFeeder(generateId()).also {
                it.normalEnergizingLvSubstation = this
                service.add(it)
            })

        }
    } else {
        equipment.forEach { it.removeContainer(this) }
        clearEquipment()
    }

    return this
}

// #################################################
// # Extension IEC61970 Base Generation Production #
// #################################################

fun EvChargingUnit.fillFields(service: NetworkService, includeRuntime: Boolean = true): EvChargingUnit {
    (this as PowerElectronicsUnit).fillFields(service, includeRuntime)
    return this
}

// ######################################
// # Extension IEC61970 Base Protection #
// ######################################

fun DirectionalCurrentRelay.fillFields(service: NetworkService, includeRuntime: Boolean = true): DirectionalCurrentRelay {
    (this as ProtectionRelayFunction).fillFields(service, includeRuntime)

    directionalCharacteristicAngle = 1.1
    polarizingQuantityType = PolarizingQuantityType.NEGATIVE_SEQUENCE_VOLTAGE
    relayElementPhase = PhaseCode.ABCN
    minimumPickupCurrent = 2.2
    currentLimit1 = 3.3
    inverseTimeFlag = true
    timeDelay1 = 4.4

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

    assetInfo = RelayInfo(generateId()).also { service.add(it) }

    model = "model"
    reclosing = true
    relayDelayTime = 1.1
    protectionKind = ProtectionKind.DISTANCE
    directable = true
    powerDirection = PowerDirectionKind.FORWARD

    for (i in 0..1) {
        addTimeLimit(i.toDouble())
        addThreshold(RelaySetting(UnitSymbol.entries[i], i.toDouble(), "setting $i"))
        addProtectedSwitch(Breaker(generateId()).also {
            it.addRelayFunction(this)
            service.add(it)
        })
        addSensor(CurrentTransformer(generateId()).also {
            it.addRelayFunction(this)
            service.add(it)
        })
        addScheme(ProtectionRelayScheme(generateId()).also {
            it.addFunction(this)
            service.add(it)
        })
    }

    return this
}

fun ProtectionRelayScheme.fillFields(service: NetworkService, includeRuntime: Boolean = true): ProtectionRelayScheme {
    (this as IdentifiedObject).fillFieldsCommon(service, includeRuntime)

    system = ProtectionRelaySystem(generateId()).also {
        it.addScheme(this)
        service.add(it)
    }

    @Suppress("unused")
    for (i in 0..1) {
        addFunction(CurrentRelay(generateId()).also {
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
        addScheme(ProtectionRelayScheme(generateId()).also {
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

// #################################
// # Extension IEC61970 Base Wires #
// #################################

fun BatteryControl.fillFields(service: NetworkService, includeRuntime: Boolean = true): BatteryControl {
    (this as RegulatingControl).fillFields(service, includeRuntime)

    chargingRate = 1.0
    dischargingRate = 2.0
    reservePercent = 3.0
    controlMode = BatteryControlMode.time

    return this
}

// #######################
// # IEC61968 Asset Info #
// #######################

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
        addTransformerTankInfo(TransformerTankInfo(generateId()).also {
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

    transformerTankInfo = TransformerTankInfo(generateId()).also {
        it.addTransformerEndInfo(this)
        service.add(it)
    }
    transformerStarImpedance = TransformerStarImpedance(generateId()).also {
        it.transformerEndInfo = this
        service.add(it)
    }

    energisedEndNoLoadTests = NoLoadTest(generateId()).also { service.add(it) }
    energisedEndShortCircuitTests = ShortCircuitTest(generateId()).also { service.add(it) }
    groundedEndShortCircuitTests = ShortCircuitTest(generateId()).also { service.add(it) }
    openEndOpenCircuitTests = OpenCircuitTest(generateId()).also { service.add(it) }
    energisedEndOpenCircuitTests = OpenCircuitTest(generateId()).also { service.add(it) }

    return this
}

fun TransformerTankInfo.fillFields(service: NetworkService, includeRuntime: Boolean = true): TransformerTankInfo {
    (this as AssetInfo).fillFields(service, includeRuntime)

    powerTransformerInfo = PowerTransformerInfo(generateId()).also {
        it.addTransformerTankInfo(this)
        service.add(it)
    }

    @Suppress("unused")
    for (i in 0..1) {
        addTransformerEndInfo(TransformerEndInfo(generateId()).also {
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
    sizeDescription = "6.7"
    strandCount = "8"
    coreStrandCount = "4"
    insulated = true
    insulationMaterial = WireInsulationKind.doubleWireArmour
    insulationThickness = 1.2

    return this
}

// ###################
// # IEC61968 Assets #
// ###################

fun Asset.fillFields(service: NetworkService, includeRuntime: Boolean = true): Asset {
    (this as IdentifiedObject).fillFieldsCommon(service, includeRuntime)

    addOrganisationRole(AssetOwner(generateId()).also { service.add(it) })
    for (i in 0..1)
        addPowerSystemResource(Junction(generateId()).also {
            it.addAsset(this)
            service.add(it)
        })

    location = Location(generateId()).also { service.add(it) }

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

fun AssetInfo.fillFields(service: NetworkService, includeRuntime: Boolean = true): AssetInfo {
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

fun Streetlight.fillFields(service: NetworkService, includeRuntime: Boolean = true): Streetlight {
    (this as Asset).fillFields(service, includeRuntime)

    pole = Pole(generateId()).also {
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

// ###################
// # IEC61968 Common #
// ###################

fun Location.fillFields(service: NetworkService, includeRuntime: Boolean = true): Location {
    (this as IdentifiedObject).fillFieldsCommon(service, includeRuntime)

    mainAddress = StreetAddress("1234", TownDetail("town", "state"), "5678", StreetDetail("a", "b", "c", "d", "e", "f", "g"))
    for (i in 0..1)
        addPoint(PositionPoint(i.toDouble(), i.toDouble()))

    return this
}

// #####################################
// # IEC61968 infIEC61968 InfAssetInfo #
// #####################################

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

// ##################################
// # IEC61968 infIEC61968 InfAssets #
// ##################################

fun Pole.fillFields(service: NetworkService, includeRuntime: Boolean = true): Pole {
    (this as Structure).fillFields(service, includeRuntime)

    classification = "classification"

    addStreetlight(Streetlight(generateId()).also {
        it.pole = this
        service.add(it)
    })

    return this
}

// #####################
// # IEC61968 Metering #
// #####################

fun EndDevice.fillFields(service: NetworkService, includeRuntime: Boolean = true): EndDevice {
    (this as AssetContainer).fillFields(service, includeRuntime)

    @Suppress("unused")
    for (i in 0..1) {
        addUsagePoint(UsagePoint(generateId()).also {
            it.addEndDevice(this)
            service.add(it)
        })
    }

    customerMRID = UUID.randomUUID().toString()
    serviceLocation = Location(generateId()).also { service.add(it) }

    addFunction(PanDemandResponseFunction(generateId()).also { service.add(it) })

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

    usagePointLocation = Location(generateId()).also { service.add(it) }
    isVirtual = true
    connectionCategory = "connectionCategory"
    ratedPower = 2000
    approvedInverterCapacity = 5000
    phaseCode = PhaseCode.AN

    @Suppress("unused")
    for (i in 0..1) {
        addEquipment(Junction(generateId()).also {
            it.addUsagePoint(this)
            service.add(it)
        })

        addEndDevice(Meter(generateId()).also {
            it.addUsagePoint(this)
            service.add(it)
        })

        addContact(ContactDetails().fillFields())
    }

    return this
}

// #######################
// # IEC61968 Operations #
// #######################

fun OperationalRestriction.fillFields(service: NetworkService, includeRuntime: Boolean = true): OperationalRestriction {
    (this as Document).fillFieldsCommon(service, includeRuntime)

    @Suppress("unused")
    for (i in 0..1) {
        addEquipment(Junction(generateId()).also {
            it.addOperationalRestriction(this)
            service.add(it)
        })
    }

    return this
}

// #####################################
// # IEC61970 Base Auxiliary Equipment #
// #####################################

fun AuxiliaryEquipment.fillFields(service: NetworkService, includeRuntime: Boolean = true): AuxiliaryEquipment {
    (this as Equipment).fillFields(service, includeRuntime)

    terminal = Terminal(generateId()).also {
        service.add(it)
    }

    return this
}

fun CurrentTransformer.fillFields(service: NetworkService, includeRuntime: Boolean = true): CurrentTransformer {
    (this as Sensor).fillFields(service, includeRuntime)

    assetInfo = CurrentTransformerInfo(generateId()).also {
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

    assetInfo = PotentialTransformerInfo(generateId()).also {
        service.add(it)
    }
    type = PotentialTransformerKind.capacitiveCoupling

    return this
}

fun Sensor.fillFields(service: NetworkService, includeRuntime: Boolean = true): Sensor {
    (this as AuxiliaryEquipment).fillFields(service, includeRuntime)

    @Suppress("unused")
    for (i in 0..1) {
        addRelayFunction(CurrentRelay(generateId()).also {
            it.addSensor(this)
            service.add(it)
        })
    }

    return this
}

// ######################
// # IEC61970 Base Core #
// ######################

fun AcDcTerminal.fillFields(service: NetworkService, includeRuntime: Boolean = true): AcDcTerminal {
    (this as IdentifiedObject).fillFieldsCommon(service, includeRuntime)
    return this
}

fun BaseVoltage.fillFields(service: NetworkService, includeRuntime: Boolean = true): BaseVoltage {
    (this as IdentifiedObject).fillFieldsCommon(service, includeRuntime)

    nominalVoltage = 1

    return this
}

fun ConductingEquipment.fillFields(service: NetworkService, includeRuntime: Boolean = true): ConductingEquipment {
    (this as Equipment).fillFields(service, includeRuntime)

    baseVoltage = BaseVoltage(generateId()).also { service.add(it) }

    @Suppress("unused")
    for (i in 0..(1.coerceAtMost(maxTerminals - 1)))
        addTerminal(Terminal(generateId()).also { service.add(it) })

    return this
}

fun ConnectivityNode.fillFields(service: NetworkService, includeRuntime: Boolean = true): ConnectivityNode {
    (this as IdentifiedObject).fillFieldsCommon(service, includeRuntime)

    @Suppress("unused")
    for (i in 0..1)
        addTerminal(Terminal(generateId()).also {
            it.connectivityNode = this
            service.add(it)
        })

    return this
}

fun ConnectivityNodeContainer.fillFields(service: NetworkService, includeRuntime: Boolean = true): ConnectivityNodeContainer {
    (this as PowerSystemResource).fillFields(service, includeRuntime)
    return this
}

fun Curve.fillFields(service: NetworkService, includeRuntime: Boolean = true): Curve {
    (this as IdentifiedObject).fillFieldsCommon(service, includeRuntime)

    addData(1f, 1f)

    return this
}

fun Equipment.fillFields(service: NetworkService, includeRuntime: Boolean = true): Equipment {
    (this as PowerSystemResource).fillFields(service, includeRuntime)

    inService = false
    normallyInService = false
    commissionedDate = Instant.MIN

    @Suppress("unused")
    for (i in 0..1) {
        addUsagePoint(UsagePoint(generateId()).also {
            it.addEquipment(this)
            service.add(it)
        })

        addOperationalRestriction(OperationalRestriction(generateId()).also {
            it.addEquipment(this)
            service.add(it)
        })

        addContainer(Circuit(generateId()).also {
            it.addEquipment(this)
            service.add(it)
        })

        if (includeRuntime) {
            addCurrentContainer(Feeder(generateId()).also {
                it.addEquipment(this)
                service.add(it)
            })
        }
    }

    return this
}

fun EquipmentContainer.fillFields(service: NetworkService, includeRuntime: Boolean = true): EquipmentContainer {
    (this as ConnectivityNodeContainer).fillFields(service, includeRuntime)

    @Suppress("unused")
    for (i in 0..1)
        addEquipment(Junction(generateId()).also {
            it.addContainer(this)
            service.add(it)
        })

    return this
}

fun Feeder.fillFields(service: NetworkService, includeRuntime: Boolean = true): Feeder {
    (this as EquipmentContainer).fillFields(service, includeRuntime)

    normalHeadTerminal = Terminal(generateId()).apply {
        conductingEquipment = equipment.filterIsInstance<ConductingEquipment>().first()
        conductingEquipment!!.addTerminal(this)
    }.also { service.add(it) }

    normalEnergizingSubstation = Substation(generateId()).also {
        it.addFeeder(this)
        service.add(it)
    }

    if (includeRuntime) {
        @Suppress("unused")
        for (i in 0..1) {
            addNormalEnergizedLvFeeder(LvFeeder(generateId()).also {
                it.addNormalEnergizingFeeder(this)
                service.add(it)
            })

            addCurrentEnergizedLvFeeder(LvFeeder(generateId()).also {
                it.addCurrentEnergizingFeeder(this)
                service.add(it)
            })

            addNormalEnergizedLvSubstation(LvSubstation(generateId()).also {
                it.addNormalEnergizingFeeder(this)
                service.add(it)
            })

            addCurrentEnergizedLvSubstation(LvSubstation(generateId()).also {
                it.addCurrentEnergizingFeeder(this)
                service.add(it)
            })

            addCurrentEquipment(Junction(generateId()).also {
                it.addCurrentContainer(this)
                service.add(it)
            })
        }
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
        addSubGeographicalRegion(SubGeographicalRegion(generateId()).also { service.add(it) })

    return this
}

fun PowerSystemResource.fillFields(service: NetworkService, includeRuntime: Boolean = true): PowerSystemResource {
    (this as IdentifiedObject).fillFieldsCommon(service, includeRuntime)

    location = Location(generateId()).apply { addPoint(PositionPoint(3.3, 4.4)) }.also { service.add(it) }
    numControls = 5

    for (i in 0..1)
        addAsset(Pole(generateId()).also {
            it.addPowerSystemResource(this)
            service.add(it)
        })

    return this
}

fun SubGeographicalRegion.fillFields(service: NetworkService, includeRuntime: Boolean = true): SubGeographicalRegion {
    (this as IdentifiedObject).fillFieldsCommon(service, includeRuntime)

    geographicalRegion = GeographicalRegion(generateId()).also {
        it.addSubGeographicalRegion(this)
        service.add(it)
    }

    @Suppress("unused")
    for (i in 0..1)
        addSubstation(Substation(generateId()).also { service.add(it) })

    return this
}

fun Substation.fillFields(service: NetworkService, includeRuntime: Boolean = true): Substation {
    (this as EquipmentContainer).fillFields(service, includeRuntime)

    subGeographicalRegion = SubGeographicalRegion(generateId()).also {
        it.addSubstation(this)
        service.add(it)
    }

    @Suppress("unused")
    for (i in 0..1) {
        addFeeder(Feeder(generateId()).also { service.add(it) })

        addLoop(Loop(generateId()).also {
            it.addSubstation(this)
            service.add(it)
        })

        addEnergizedLoop(Loop(generateId()).also {
            it.addEnergizingSubstation(this)
            service.add(it)
        })

        addCircuit(Circuit(generateId()).also {
            it.addEndSubstation(this)
            service.add(it)
        })
    }

    return this
}

fun Terminal.fillFields(service: NetworkService, includeRuntime: Boolean = true): Terminal {
    (this as AcDcTerminal).fillFields(service, includeRuntime)

    conductingEquipment = Junction(generateId()).also { service.add(it) }
    conductingEquipment?.addTerminal(this)

    phases = PhaseCode.X
    sequenceNumber = 1
    connectivityNode = ConnectivityNode(generateId()).also {
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

// #############################
// # IEC61970 Base Equivalents #
// #############################

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

// #######################################
// # IEC61970 Base Generation Production #
// #######################################

fun BatteryUnit.fillFields(service: NetworkService, includeRuntime: Boolean = true): BatteryUnit {
    (this as PowerElectronicsUnit).fillFields(service, includeRuntime)

    batteryState = BatteryStateKind.charging
    ratedE = 1L
    storedE = 2L

    addControl(BatteryControl(generateId()).also { service.add(it) })

    return this
}

fun PhotoVoltaicUnit.fillFields(service: NetworkService, includeRuntime: Boolean = true): PhotoVoltaicUnit {
    (this as PowerElectronicsUnit).fillFields(service, includeRuntime)
    return this
}

fun PowerElectronicsUnit.fillFields(service: NetworkService, includeRuntime: Boolean = true): PowerElectronicsUnit {
    (this as Equipment).fillFields(service, includeRuntime)

    powerElectronicsConnection = PowerElectronicsConnection(generateId()).also {
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

// ######################
// # IEC61970 Base Meas #
// ######################

fun Accumulator.fillFields(service: NetworkService, includeRuntime: Boolean = true): Accumulator {
    (this as Measurement).fillFields(service, includeRuntime)
    return this
}

fun Analog.fillFields(service: NetworkService, includeRuntime: Boolean = true): Analog {
    (this as Measurement).fillFields(service, includeRuntime)

    positiveFlowIn = true

    return this
}

fun Control.fillFields(service: NetworkService, includeRuntime: Boolean = true): Control {
    (this as IoPoint).fillFields(service, includeRuntime)

    powerSystemResourceMRID = "1234"
    remoteControl = RemoteControl(generateId()).also {
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

    powerSystemResourceMRID = generateId()
    remoteSource = RemoteSource(generateId()).also {
        remoteSource = it
        it.measurement = this
    }.also {
        service.add(it)
    }

    terminalMRID = generateId()
    phases = PhaseCode.ABCN
    unitSymbol = UnitSymbol.HENRYS

    return this
}

// ############################
// # IEC61970 Base Protection #
// ############################

fun CurrentRelay.fillFields(service: NetworkService, includeRuntime: Boolean = true): CurrentRelay {
    (this as ProtectionRelayFunction).fillFields(service, includeRuntime)

    currentLimit1 = 1.1
    inverseTimeFlag = true
    timeDelay1 = 2.2

    return this
}

// #######################
// # IEC61970 Base Scada #
// #######################

fun RemoteControl.fillFields(service: NetworkService, includeRuntime: Boolean = true): RemoteControl {
    (this as RemotePoint).fillFields(service, includeRuntime)

    control = Control(generateId()).also {
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

    measurement = Discrete(generateId()).also {
        it.remoteSource = this
        service.add(it)
    }

    return this
}

// #######################
// # IEC61970 Base Wires #
// #######################

fun AcLineSegment.fillFields(service: NetworkService, includeRuntime: Boolean = true): AcLineSegment {
    (this as Conductor).fillFields(service, includeRuntime)

    perLengthImpedance = PerLengthSequenceImpedance(generateId()).also { service.add(it) }
    @Suppress("unused")
    for (i in 0..1) {
        addCut(Cut(generateId()).also { service.add(it) })
        addClamp(Clamp(generateId()).also { service.add(it) })
        addPhase(AcLineSegmentPhase(generateId()).also {
            it.phase = SinglePhaseKind[i]
            service.add(it)
        })
    }

    return this
}

fun AcLineSegmentPhase.fillFields(service: NetworkService, includeRuntime: Boolean = true): AcLineSegmentPhase {
    (this as PowerSystemResource).fillFields(service, includeRuntime)

    acLineSegment = AcLineSegment(generateId()).also {
        it.addPhase(this)
        service.add(it)
    }

    phase = SinglePhaseKind.A
    sequenceNumber = 0
    assetInfo = OverheadWireInfo(generateId()).also { service.add(it) }

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

fun Clamp.fillFields(service: NetworkService, includeRuntime: Boolean = true): Clamp {
    (this as ConductingEquipment).fillFields(service, includeRuntime)

    lengthFromTerminal1 = 1.1
    acLineSegment = AcLineSegment(generateId()).also {
        it.addClamp(this)
        service.add(it)
    }

    return this
}

fun Conductor.fillFields(service: NetworkService, includeRuntime: Boolean = true): Conductor {
    (this as ConductingEquipment).fillFields(service, includeRuntime)

    length = 1.1
    designTemperature = 2
    designRating = 3.3
    assetInfo = CableInfo(generateId()).also { service.add(it) }

    return this
}

fun Connector.fillFields(service: NetworkService, includeRuntime: Boolean = true): Connector {
    (this as ConductingEquipment).fillFields(service, includeRuntime)
    return this
}

fun Cut.fillFields(service: NetworkService, includeRuntime: Boolean = true): Cut {
    (this as Switch).fillFields(service, includeRuntime)

    lengthFromTerminal1 = 1.1
    acLineSegment = AcLineSegment(generateId()).also {
        it.addCut(this)
        service.add(it)
    }

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
        addPhase(EnergyConsumerPhase(generateId()).also {
            it.phase = SinglePhaseKind[i]
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

    energyConsumer = EnergyConsumer(generateId()).also {
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
        addPhase(EnergySourcePhase(generateId()).also {
            it.phase = SinglePhaseKind[i]
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

    energySource = EnergySource(generateId()).also {
        it.addPhase(this)
        service.add(it)
    }

    phase = SinglePhaseKind.A

    return this
}

fun Fuse.fillFields(service: NetworkService, includeRuntime: Boolean = true): Fuse {
    (this as Switch).fillFields(service, includeRuntime)

    function = VoltageRelay(generateId()).also { service.add(it) }

    return this
}

fun Ground.fillFields(service: NetworkService, includeRuntime: Boolean = true): Ground {
    (this as ConductingEquipment).fillFields(service, includeRuntime)
    return this
}

fun GroundDisconnector.fillFields(service: NetworkService, includeRuntime: Boolean = true): GroundDisconnector {
    (this as Switch).fillFields(service, includeRuntime)
    return this
}

fun GroundingImpedance.fillFields(service: NetworkService, includeRuntime: Boolean = true): GroundingImpedance {
    (this as EarthFaultCompensator).fillFields(service, includeRuntime)

    x = 1.0

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
        addData(
            PhaseImpedanceData(
                SinglePhaseKind[i],
                SinglePhaseKind[i],
                i.toDouble(),
                i.toDouble(),
                i.toDouble(),
                i.toDouble()
            )
        )

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

    powerElectronicsConnection = PowerElectronicsConnection(generateId()).also {
        it.addPhase(this)
        service.add(it)
    }
    p = 1.0
    phase = SinglePhaseKind.B
    q = 2.0

    return this
}

fun PowerTransformer.fillFields(service: NetworkService, includeRuntime: Boolean = true): PowerTransformer {
    (this as ConductingEquipment).fillFields(service, includeRuntime)

    vectorGroup = VectorGroup.DD0
    transformerUtilisation = 1.0
    constructionKind = TransformerConstructionKind.aerial
    function = TransformerFunctionKind.voltageRegulator
    assetInfo = PowerTransformerInfo(generateId()).also { service.add(it) }

    @Suppress("unused")
    for (i in 0..1) {
        addEnd(PowerTransformerEnd(generateId()).also {
            it.powerTransformer = this
            service.add(it)
        })
    }

    return this
}

fun PowerTransformerEnd.fillFields(service: NetworkService, includeRuntime: Boolean = true): PowerTransformerEnd {
    (this as TransformerEnd).fillFields(service, includeRuntime)

    powerTransformer = PowerTransformer(generateId()).also { service.add(it) }
    powerTransformer?.addEnd(this)

    b = 1.0
    b0 = 2.0
    connectionKind = WindingConnection.Zn
    g = 3.0
    g0 = 4.0
    phaseAngleClock = 5
    r = 6.0
    r0 = 7.0
    addRating(8, TransformerCoolingType.UNKNOWN)
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
        addRelayFunction(CurrentRelay(generateId()).also {
            it.addProtectedSwitch(this)
            service.add(it)
        })
    }

    return this
}

fun RatioTapChanger.fillFields(service: NetworkService, includeRuntime: Boolean = true): RatioTapChanger {
    (this as TapChanger).fillFields(service, includeRuntime)

    transformerEnd = PowerTransformerEnd(generateId()).also {
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
    regulatingControl = TapChangerControl(generateId()).also { it.addRegulatingCondEq(this); service.add(it) }

    return this
}

fun RegulatingControl.fillFields(service: NetworkService, includeRuntime: Boolean = true): RegulatingControl {
    (this as PowerSystemResource).fillFields(service, includeRuntime)

    discrete = false
    mode = RegulatingControlModeKind.voltage
    monitoredPhase = PhaseCode.ABC
    targetDeadband = 2.0f
    targetValue = 100.0
    enabled = true
    maxAllowedTargetValue = 200.0
    minAllowedTargetValue = 50.0
    ratedCurrent = 10.0
    terminal = Terminal(generateId()).also { service.add(it) }
    ctPrimary = 1.0
    minTargetDeadband = 2.0
    addRegulatingCondEq(PowerElectronicsConnection(generateId()).also { it.regulatingControl = this; service.add(it) })

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

    assetInfo = ShuntCompensatorInfo(generateId()).also { service.add((it)) }

    grounded = true
    nomU = 1
    phaseConnection = PhaseShuntConnectionKind.I
    sections = 2.2
    groundingTerminal = terminals.last()

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

    assetInfo = SwitchInfo(generateId()).also { service.add(it) }
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

    addCurve(ReactiveCapabilityCurve(generateId()).also { service.add(it) })

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
    tapChangerControl = TapChangerControl(generateId()).also { service.add(it) }

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

    terminal = Terminal(generateId()).also { service.add(it) }
    ratioTapChanger = RatioTapChanger(generateId()).also {
        it.transformerEnd = this
        service.add(it)
    }
    baseVoltage = BaseVoltage(generateId()).also { service.add(it) }
    grounded = true
    rGround = 1.0
    xGround = 2.0
    endNumber = 1
    starImpedance = TransformerStarImpedance(generateId()).also { service.add(it) }

    return this
}

fun TransformerStarImpedance.fillFields(service: NetworkService, includeRuntime: Boolean = true): TransformerStarImpedance {
    (this as IdentifiedObject).fillFieldsCommon(service, includeRuntime)

    r = 1.0
    r0 = 2.0
    x = 3.0
    x0 = 4.0

    transformerEndInfo = TransformerEndInfo(generateId()).also {
        it.transformerStarImpedance = this
        service.add(it)
    }

    return this
}

// ###############################
// # IEC61970 InfIEC61970 Feeder #
// ###############################

fun Circuit.fillFields(service: NetworkService, includeRuntime: Boolean = true): Circuit {
    (this as Line).fillFields(service, includeRuntime)

    loop = Loop(generateId()).also {
        it.addCircuit(this)
        service.add(it)
    }

    @Suppress("unused")
    for (i in 0..1) {
        addEndTerminal(Terminal(generateId()).also { service.add(it) })
        addEndSubstation(Substation(generateId()).also {
            it.addCircuit(this)
            service.add(it)
        })
    }


    return this
}

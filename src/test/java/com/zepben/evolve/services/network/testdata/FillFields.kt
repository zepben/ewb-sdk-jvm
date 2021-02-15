/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network.testdata

import com.zepben.evolve.cim.iec61968.assetinfo.PowerTransformerInfo
import com.zepben.evolve.cim.iec61968.assets.*
import com.zepben.evolve.cim.iec61968.common.Location
import com.zepben.evolve.cim.iec61968.metering.EndDevice
import com.zepben.evolve.cim.iec61968.metering.Meter
import com.zepben.evolve.cim.iec61968.metering.UsagePoint
import com.zepben.evolve.cim.iec61968.operations.OperationalRestriction
import com.zepben.evolve.cim.iec61970.base.core.*
import com.zepben.evolve.cim.iec61970.base.domain.UnitSymbol
import com.zepben.evolve.cim.iec61970.base.meas.Accumulator
import com.zepben.evolve.cim.iec61970.base.meas.Analog
import com.zepben.evolve.cim.iec61970.base.meas.Discrete
import com.zepben.evolve.cim.iec61970.base.meas.Measurement
import com.zepben.evolve.cim.iec61970.base.wires.*
import com.zepben.evolve.cim.iec61970.base.wires.generation.production.*
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.Circuit
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.Loop
import com.zepben.evolve.services.network.NetworkModelTestUtil.Companion.createFeeder
import com.zepben.evolve.services.network.NetworkModelTestUtil.Companion.createJunction
import com.zepben.evolve.services.network.NetworkModelTestUtil.Companion.createRemoteSource
import com.zepben.evolve.services.network.NetworkModelTestUtil.Companion.createSubstation
import com.zepben.evolve.services.network.NetworkModelTestUtil.Companion.locationOf
import com.zepben.evolve.services.network.NetworkService
import java.util.*

/************ IEC61968 ASSET INFO ************/

fun PowerTransformerInfo.fillFields(): PowerTransformerInfo {
    (this as AssetInfo).fillFields()
    return this
}

fun AssetInfo.fillFields() {
    (this as IdentifiedObject).fillFields()
}

/************ IEC61968 ASSETS ************/
fun Asset.fillFields(networkService: NetworkService) {
    val ao = AssetOwner()
    networkService.add(ao)
    addOrganisationRole(ao)
    location = Location().also { networkService.add(it) }
    (this as IdentifiedObject).fillFields()
}

fun AssetContainer.fillFields(networkService: NetworkService) {
    (this as Asset).fillFields(networkService)
}

fun Pole.fillFields(networkService: NetworkService): Pole {
    classification = "classification"

    val streetlight = Streetlight()
    streetlight.pole = this
    networkService.add(streetlight)
    addStreetlight(streetlight)
    (this as Structure).fillFields(networkService)
    return this
}

fun Streetlight.fillFields(networkService: NetworkService): Streetlight {
    pole = Pole()
    pole!!.addStreetlight(this)
    networkService.add(pole!!)
    lampKind = StreetlightLampKind.MERCURY_VAPOR
    lightRating = 20
    (this as Asset).fillFields(networkService)
    return this
}

fun Structure.fillFields(networkService: NetworkService) {
    (this as AssetContainer).fillFields(networkService)
}

/************ IEC61968 METERING ************/

fun EndDevice.fillFields(networkService: NetworkService) {
    for (i in 0..1) {
        val usagePoint = UsagePoint()
        addUsagePoint(usagePoint)
        usagePoint.addEndDevice(this)
        networkService.add(usagePoint)
    }

    customerMRID = UUID.randomUUID().toString()
    serviceLocation = Location().also { networkService.add(it) }
    (this as AssetContainer).fillFields(networkService)
}

fun Meter.fillFields(networkService: NetworkService): Meter {
    (this as EndDevice).fillFields(networkService)
    return this
}


/************ IEC61970 CORE ************/

fun ConnectivityNodeContainer.fillFields(networkService: NetworkService): ConnectivityNodeContainer {
    (this as PowerSystemResource).fillFields(networkService)
    return this
}

fun EquipmentContainer.fillFields(networkService: NetworkService) {
    for (i in 0..1)
        createJunction(networkService, i, this, null)

    (this as ConnectivityNodeContainer).fillFields(networkService)
}

private fun IdentifiedObject.fillFields() {
    name = "1"
    description = "the description"
    numDiagramObjects = 2
}

fun PowerSystemResource.fillFields(networkService: NetworkService) {
    location = locationOf(3.3, 4.4)
    networkService.add(location!!)

    numControls = 5

    (this as IdentifiedObject).fillFields()
}

fun Equipment.fillFields(networkService: NetworkService, includeRuntime: Boolean = true) {
    inService = false
    normallyInService = false

    for (i in 0..1) {
        val usagePoint = UsagePoint()
        networkService.add(usagePoint)
        addUsagePoint(usagePoint)
        usagePoint.addEquipment(this)
    }

    for (i in 0..1) {
        val operationalRestriction = OperationalRestriction()
        networkService.add(operationalRestriction)
        addOperationalRestriction(operationalRestriction)
        operationalRestriction.addEquipment(this)
    }

    for (i in 0..1) {
        val container = Circuit()
        networkService.add(container)
        addContainer(container)
        container.addEquipment(this)
    }

    if (includeRuntime) {
        for (i in 0..1) {
            val feeder = Feeder()
            networkService.add(feeder)
            addCurrentFeeder(feeder)
            feeder.addEquipment(this)
        }
    }

    (this as PowerSystemResource).fillFields(networkService)
}

fun ConductingEquipment.fillFields(networkService: NetworkService, includeRuntime: Boolean = true) {
    val bv = BaseVoltage()
    networkService.add(bv)
    baseVoltage = bv

    for (i in 0..1) {
        val terminal = Terminal()
        terminal.conductingEquipment = this
        addTerminal(terminal)
        networkService.add(terminal)
    }

    (this as Equipment).fillFields(networkService, includeRuntime)
}

fun Substation.fillFields(networkService: NetworkService): Substation {
    subGeographicalRegion = SubGeographicalRegion()
    subGeographicalRegion!!.addSubstation(this)
    networkService.add(subGeographicalRegion!!)

    for (i in 0..1)
        createFeeder(networkService, i, null, this)

    for (i in 0..1) {
        val loop = Loop()
        networkService.add(loop)
        addLoop(loop)
        loop.addSubstation(this)
    }

    for (i in 0..1) {
        val loop = Loop()
        networkService.add(loop)
        addEnergizedLoop(loop)
        loop.addEnergizingSubstation(this)
    }

    for (i in 0..1) {
        val circuit = Circuit()
        networkService.add(circuit)
        addCircuit(circuit)
        circuit.addEndSubstation(this)
    }

    (this as EquipmentContainer).fillFields()
    return this
}

/************ IEC61970 WIRES GENERATION PRODUCTION ************/

fun BatteryUnit.fillFields(networkService: NetworkService): BatteryUnit {
    batteryState = BatteryStateKind.charging
    ratedE = 1.0
    storedE = 2.0

    (this as PowerElectronicsUnit).fillFields(networkService)
    return this
}

fun PhotoVoltaicUnit.fillFields(networkService: NetworkService): PhotoVoltaicUnit {
    (this as PowerElectronicsUnit).fillFields(networkService)
    return this
}

fun PowerElectronicsConnection.fillFields(networkService: NetworkService): PowerElectronicsConnection {
    maxIFault = 1
    maxQ = 2.0
    minQ = 3.0
    p = 4.0
    q = 5.0
    ratedS = 6
    ratedU = 7

    (this as RegulatingCondEq).fillFields(networkService, false)
    return this
}

fun PowerElectronicsConnectionPhase.fillFields(networkService: NetworkService): PowerElectronicsConnectionPhase {
    val pec = PowerElectronicsConnection().also {
        networkService.add(it)
        it.addPhase(this)
    }
    powerElectronicsConnection = pec
    p = 1.0
    phase = SinglePhaseKind.B
    q = 2.0

    (this as PowerSystemResource).fillFields(networkService)
    return this
}

fun PowerElectronicsUnit.fillFields(networkService: NetworkService): PowerElectronicsUnit {
    val pec = PowerElectronicsConnection().also {
        networkService.add(it)
        it.addUnit(this)
    }
    maxP = 1
    minP = 2
    powerElectronicsConnection = pec

    (this as Equipment).fillFields(networkService, false)
    return this
}

fun PowerElectronicsWindUnit.fillFields(networkService: NetworkService): PowerElectronicsWindUnit {
    (this as PowerElectronicsUnit).fillFields(networkService)
    return this
}

/************ IEC61970 WIRES ************/

fun BusbarSection.fillFields(networkService: NetworkService): BusbarSection {
    (this as Connector).fillFields(networkService, false)
    return this
}

fun Line.fillFields(networkService: NetworkService): Line {
    (this as EquipmentContainer).fillFields(networkService)
    return this
}

fun Breaker.fillFields(): Breaker {
    (this as ProtectedSwitch).fillFields()
    return this
}

fun LoadBreakSwitch.fillFields(): LoadBreakSwitch {
    (this as ProtectedSwitch).fillFields()
    return this
}

fun ProtectedSwitch.fillFields(): ProtectedSwitch {
    (this as Switch).fillFields()
    return this
}

fun Switch.fillFields(): Switch {
    normalOpen = 1
    open = 1
    return this
}

fun PowerTransformer.fillFields(networkService: NetworkService, includeRuntime: Boolean = true): PowerTransformer {
    transformerUtilisation = 1.0
    vectorGroup = VectorGroup.DD0
    assetInfo = PowerTransformerInfo().fillFields().also { networkService.add(it) }

    (this as ConductingEquipment).fillFields(networkService, includeRuntime)
    return this
}

fun TransformerEnd.fillFields(networkService: NetworkService): TransformerEnd {
    val term = Terminal()
    val rtc = RatioTapChanger()
    rtc.transformerEnd = this
    val bv = BaseVoltage()
    networkService.add(term)
    networkService.add(rtc)
    networkService.add(bv)
    terminal = term
    ratioTapChanger = rtc
    baseVoltage = bv
    grounded = true
    rGround = 1.0
    xGround = 2.0
    endNumber = 1

    (this as IdentifiedObject).fillFields()
    return this
}

fun PowerTransformerEnd.fillFields(networkService: NetworkService): PowerTransformerEnd {
    val pt = PowerTransformer()
    powerTransformer = pt
    pt.addEnd(this)
    networkService.add(pt)
    b = 1.0
    b0 = 2.0
    r = 3.0
    r0 = 4.0
    x = 5.0
    x0 = 6.0
    g = 7.0
    g0 = 8.0
    ratedS = 100
    ratedU = 4000
    phaseAngleClock = 3
    connectionKind = WindingConnection.D

    (this as TransformerEnd).fillFields(networkService)
    return this
}

/************ IEC61970 InfIEC61970 ************/

fun Circuit.fillFields(networkService: NetworkService): Circuit {
    loop = Loop()
    loop!!.addCircuit(this)
    networkService.add(loop!!)

    for (i in 1..2)
        addEndTerminal(createTerminal(networkService, null, PhaseCode.A, i))

    for (i in 0..1) {
        val substation = createSubstation(networkService, i, null)
        addEndSubstation(substation)
        substation.addCircuit(this)
    }

    (this as Line).fillFields(networkService)
    return this
}

fun Loop.fillFields(networkService: NetworkService): Loop {
    for (i in 0..1) {
        val circuit = Circuit()
        addCircuit(circuit)
        circuit.loop = this
        networkService.add(circuit)
    }

    for (i in 0..1) {
        val substation = createSubstation(networkService, i, null)
        addSubstation(substation)
        substation.addLoop(this)
    }

    for (i in 2..3) {
        val substation = createSubstation(networkService, i, null)
        addEnergizingSubstation(substation)
        substation.addLoop(this)
    }

    (this as IdentifiedObject).fillFields()
    return this
}

/************ IEC61970 MEASUREMENT ************/
private fun Measurement.fillFields(networkService: NetworkService) {
    powerSystemResourceMRID = PowerTransformer().mRID
    remoteSource = createRemoteSource(networkService, this)
    terminalMRID = Terminal().mRID
    phases = PhaseCode.ABCN
    unitSymbol = UnitSymbol.HENRYS
    (this as IdentifiedObject).fillFields()
}

fun Analog.fillFields(networkService: NetworkService): Analog {
    (this as Measurement).fillFields(networkService)
    return this
}

fun Accumulator.fillFields(networkService: NetworkService): Accumulator {
    (this as Measurement).fillFields(networkService)
    return this
}

fun Discrete.fillFields(networkService: NetworkService): Discrete {
    (this as Measurement).fillFields(networkService)
    return this
}

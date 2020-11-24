/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.network

import com.zepben.cimbend.cim.iec61968.assets.*
import com.zepben.cimbend.cim.iec61968.common.Location
import com.zepben.cimbend.cim.iec61968.metering.EndDevice
import com.zepben.cimbend.cim.iec61968.metering.Meter
import com.zepben.cimbend.cim.iec61968.metering.UsagePoint
import com.zepben.cimbend.cim.iec61968.operations.OperationalRestriction
import com.zepben.cimbend.cim.iec61970.base.core.*
import com.zepben.cimbend.cim.iec61970.base.domain.UnitSymbol
import com.zepben.cimbend.cim.iec61970.base.meas.*
import com.zepben.cimbend.cim.iec61970.base.wires.Line
import com.zepben.cimbend.cim.iec61970.base.wires.PowerTransformer
import com.zepben.cimbend.cim.iec61970.base.wires.VectorGroup
import com.zepben.cimbend.cim.iec61970.infiec61970.feeder.Circuit
import com.zepben.cimbend.cim.iec61970.infiec61970.feeder.Loop
import com.zepben.cimbend.network.NetworkModelTestUtil.Companion.createFeeder
import com.zepben.cimbend.network.NetworkModelTestUtil.Companion.createJunction
import com.zepben.cimbend.network.NetworkModelTestUtil.Companion.createRemoteSource
import com.zepben.cimbend.network.NetworkModelTestUtil.Companion.createSubstation
import com.zepben.cimbend.network.NetworkModelTestUtil.Companion.locationOf
import com.zepben.cimbend.testdata.TestDataCreators.createTerminal
import java.time.Instant
import java.util.*

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

fun ConnectivityNodeContainer.fillFields(networkService: NetworkService) = (this as PowerSystemResource).fillFields(networkService)

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

fun Equipment.fillFields(networkService: NetworkService) {
    inService = false
    normallyInService = false

    for (i in 0..1) {
        val usagePoint = UsagePoint()
        addUsagePoint(usagePoint)
        usagePoint.addEquipment(this)
        networkService.add(usagePoint)
    }

    for (i in 0..1) {
        val operationalRestriction = OperationalRestriction()
        addOperationalRestriction(operationalRestriction)
        operationalRestriction.addEquipment(this)
        networkService.add(operationalRestriction)
    }

    for (i in 0..1) {
        val container = Circuit()
        addContainer(container)
        container.addEquipment(this)
        networkService.add(container)
    }

    for (i in 0..1) {
        val feeder = Feeder()
        addCurrentFeeder(feeder)
        feeder.addEquipment(this)
        networkService.add(feeder)
    }

    (this as PowerSystemResource).fillFields(networkService)
}

fun ConductingEquipment.fillFields(networkService: NetworkService) {
    baseVoltage = BaseVoltage()

    for (i in 0..1) {
        val terminal = Terminal()
        terminal.conductingEquipment = this
        addTerminal(terminal)
        networkService.add(terminal)
    }

    (this as Equipment).fillFields(networkService)
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

/************ IEC61970 WIRES ************/

fun Line.fillFields(networkService: NetworkService) = (this as EquipmentContainer).fillFields(networkService)

fun PowerTransformer.fillFields(networkService: NetworkService): PowerTransformer {
    transformerUtilisation = 1.0
    vectorGroup = VectorGroup.DD0

    (this as ConductingEquipment).fillFields(networkService)
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

private fun MeasurementValue.fillFields() {
    timeStamp = Instant.now()
}

fun AnalogValue.fillFields(networkService: NetworkService): AnalogValue {
    value = 2.3
    analogMRID = Analog().mRID
    (this as MeasurementValue).fillFields()
    return this
}

fun AccumulatorValue.fillFields(networkService: NetworkService): AccumulatorValue {
    value = 23u
    accumulatorMRID = Analog().mRID
    (this as MeasurementValue).fillFields()
    return this
}

fun DiscreteValue.fillFields(networkService: NetworkService): DiscreteValue {
    value = 23
    discreteMRID = Analog().mRID
    (this as MeasurementValue).fillFields()
    return this
}

/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network.testdata

import com.zepben.evolve.cim.iec61968.assetinfo.PowerTransformerInfo
import com.zepben.evolve.cim.iec61968.assetinfo.TransformerEndInfo
import com.zepben.evolve.cim.iec61968.assetinfo.TransformerTankInfo
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
import com.zepben.evolve.services.network.NetworkModelTestUtil.Companion.createRemoteSource
import com.zepben.evolve.services.network.NetworkModelTestUtil.Companion.locationOf
import com.zepben.evolve.services.network.NetworkService
import java.util.*

/************ IEC61968 ASSET INFO ************/

fun PowerTransformerInfo.fillFields(networkService: NetworkService, includeRuntime: Boolean = true): PowerTransformerInfo {
    (this as AssetInfo).fillFields(networkService, includeRuntime)

    for (i in 0..1) {
        addTransformerTankInfo(TransformerTankInfo().also {
            it.powerTransformerInfo = this
            networkService.add(it)
        })
    }

    return this
}

fun TransformerEndInfo.fillFields(networkService: NetworkService, includeRuntime: Boolean = true): TransformerEndInfo {
    (this as AssetInfo).fillFields(networkService, includeRuntime)

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
        networkService.add(it)
    }
    transformerStarImpedance = TransformerStarImpedance().also {
        it.transformerEndInfo = this
        networkService.add(it)
    }

    return this
}

fun TransformerTankInfo.fillFields(networkService: NetworkService, includeRuntime: Boolean = true): TransformerTankInfo {
    (this as AssetInfo).fillFields(networkService, includeRuntime)

    powerTransformerInfo = PowerTransformerInfo().also {
        it.addTransformerTankInfo(this)
        networkService.add(it)
    }

    for (i in 0..1) {
        addTransformerEndInfo(TransformerEndInfo().also {
            it.transformerTankInfo = this
            networkService.add(it)
        })
    }

    return this
}

fun AssetInfo.fillFields(networkService: NetworkService, includeRuntime: Boolean) {
    (this as IdentifiedObject).fillFields(networkService, includeRuntime)
}

/************ IEC61968 ASSETS ************/
fun Asset.fillFields(networkService: NetworkService, includeRuntime: Boolean) {
    (this as IdentifiedObject).fillFields(networkService, includeRuntime)

    addOrganisationRole(AssetOwner().also { networkService.add(it) })
    location = Location().also { networkService.add(it) }
}

fun AssetContainer.fillFields(networkService: NetworkService, includeRuntime: Boolean) {
    (this as Asset).fillFields(networkService, includeRuntime)
}

fun Pole.fillFields(networkService: NetworkService, includeRuntime: Boolean = true): Pole {
    (this as Structure).fillFields(networkService, includeRuntime)

    classification = "classification"

    addStreetlight(Streetlight().also {
        it.pole = this
        networkService.add(it)
    })

    return this
}

fun Streetlight.fillFields(networkService: NetworkService, includeRuntime: Boolean = true): Streetlight {
    (this as Asset).fillFields(networkService, includeRuntime)

    pole = Pole().also {
        it.addStreetlight(this)
        networkService.add(it)
    }
    lampKind = StreetlightLampKind.MERCURY_VAPOR
    lightRating = 20

    return this
}

fun Structure.fillFields(networkService: NetworkService, includeRuntime: Boolean) {
    (this as AssetContainer).fillFields(networkService, includeRuntime)
}

/************ IEC61968 METERING ************/

fun EndDevice.fillFields(networkService: NetworkService, includeRuntime: Boolean) {
    (this as AssetContainer).fillFields(networkService, includeRuntime)

    for (i in 0..1) {
        addUsagePoint(UsagePoint().also {
            it.addEndDevice(this)
            networkService.add(it)
        })
    }

    customerMRID = UUID.randomUUID().toString()
    serviceLocation = Location().also { networkService.add(it) }
}

fun Meter.fillFields(networkService: NetworkService, includeRuntime: Boolean = true): Meter {
    (this as EndDevice).fillFields(networkService, includeRuntime)
    return this
}


/************ IEC61970 CORE ************/

fun ConnectivityNodeContainer.fillFields(networkService: NetworkService, includeRuntime: Boolean) {
    (this as PowerSystemResource).fillFields(networkService, includeRuntime)
}

fun EquipmentContainer.fillFields(networkService: NetworkService, includeRuntime: Boolean) {
    (this as ConnectivityNodeContainer).fillFields(networkService, includeRuntime)

    for (i in 0..1)
        addEquipment(Junction().also {
            it.addContainer(this)
            networkService.add(it)
        })
}

//
// Note: `includeRuntime` is here for consistency to match with all other methods
//
private fun IdentifiedObject.fillFields(networkService: NetworkService, @Suppress("UNUSED_PARAMETER") includeRuntime: Boolean) {
    name = "1"
    description = "the description"
    numDiagramObjects = 2

    for (i in 0..1) {
        val nameType = networkService.getNameType("name_type $i") ?: NameType("name_type $i").apply {
            description = "name_type_${i}_description"
        }
        networkService.addNameType(nameType)
        val name = nameType.getOrAddName("name_$i", this)
        addName(name)
    }
}

fun PowerSystemResource.fillFields(networkService: NetworkService, includeRuntime: Boolean) {
    (this as IdentifiedObject).fillFields(networkService, includeRuntime)

    location = locationOf(3.3, 4.4)
    networkService.add(location!!)

    numControls = 5
}

fun Equipment.fillFields(networkService: NetworkService, includeRuntime: Boolean) {
    (this as PowerSystemResource).fillFields(networkService, includeRuntime)

    inService = false
    normallyInService = false

    for (i in 0..1) {
        addUsagePoint(UsagePoint().also {
            it.addEquipment(this)
            networkService.add(it)
        })

        addOperationalRestriction(OperationalRestriction().also {
            it.addEquipment(this)
            networkService.add(it)
        })

        addContainer(Circuit().also {
            it.addEquipment(this)
            networkService.add(it)
        })

        if (includeRuntime) {
            addCurrentFeeder(Feeder().also {
                it.addEquipment(this)
                networkService.add(it)
            })
        }
    }
}

fun ConductingEquipment.fillFields(networkService: NetworkService, includeRuntime: Boolean) {
    (this as Equipment).fillFields(networkService, includeRuntime)

    baseVoltage = BaseVoltage().also { networkService.add(it) }

    for (i in 0..1) {
        addTerminal(Terminal().also {
            it.conductingEquipment = this
            networkService.add(it)
        })
    }
}

fun Substation.fillFields(networkService: NetworkService, includeRuntime: Boolean = true): Substation {
    (this as EquipmentContainer).fillFields(networkService, includeRuntime)

    subGeographicalRegion = SubGeographicalRegion().also {
        it.addSubstation(this)
        networkService.add(it)
    }

    for (i in 0..1) {
        addFeeder(Feeder().also {
            it.normalEnergizingSubstation = this
            networkService.add(it)
        })

        addLoop(Loop().also {
            it.addSubstation(this)
            networkService.add(it)
        })

        addEnergizedLoop(Loop().also {
            it.addEnergizingSubstation(this)
            networkService.add(it)
        })

        addCircuit(Circuit().also {
            it.addEndSubstation(this)
            networkService.add(it)
        })
    }

    return this
}

/************ IEC61970 WIRES GENERATION PRODUCTION ************/

fun BatteryUnit.fillFields(networkService: NetworkService, includeRuntime: Boolean = true): BatteryUnit {
    (this as PowerElectronicsUnit).fillFields(networkService, includeRuntime)

    batteryState = BatteryStateKind.charging
    ratedE = 1L
    storedE = 2L

    return this
}

fun PhotoVoltaicUnit.fillFields(networkService: NetworkService, includeRuntime: Boolean = true): PhotoVoltaicUnit {
    (this as PowerElectronicsUnit).fillFields(networkService, includeRuntime)
    return this
}

fun PowerElectronicsConnection.fillFields(networkService: NetworkService, includeRuntime: Boolean = true): PowerElectronicsConnection {
    (this as RegulatingCondEq).fillFields(networkService, includeRuntime)

    maxIFault = 1
    maxQ = 2.0
    minQ = 3.0
    p = 4.0
    q = 5.0
    ratedS = 6
    ratedU = 7

    return this
}

fun PowerElectronicsConnectionPhase.fillFields(networkService: NetworkService, includeRuntime: Boolean = true): PowerElectronicsConnectionPhase {
    (this as PowerSystemResource).fillFields(networkService, includeRuntime)

    powerElectronicsConnection = PowerElectronicsConnection().also {
        it.addPhase(this)
        networkService.add(it)
    }
    p = 1.0
    phase = SinglePhaseKind.B
    q = 2.0

    return this
}

fun PowerElectronicsUnit.fillFields(networkService: NetworkService, includeRuntime: Boolean = true): PowerElectronicsUnit {
    (this as Equipment).fillFields(networkService, includeRuntime)

    powerElectronicsConnection = PowerElectronicsConnection().also {
        it.addUnit(this)
        networkService.add(it)
    }
    maxP = 1
    minP = 2

    return this
}

fun PowerElectronicsWindUnit.fillFields(networkService: NetworkService, includeRuntime: Boolean = true): PowerElectronicsWindUnit {
    (this as PowerElectronicsUnit).fillFields(networkService, includeRuntime)
    return this
}

/************ IEC61970 WIRES ************/

fun BusbarSection.fillFields(networkService: NetworkService, includeRuntime: Boolean = true): BusbarSection {
    (this as Connector).fillFields(networkService, includeRuntime)
    return this
}

fun Line.fillFields(networkService: NetworkService, includeRuntime: Boolean) {
    (this as EquipmentContainer).fillFields(networkService, includeRuntime)
}

fun Breaker.fillFields(networkService: NetworkService, includeRuntime: Boolean = true): Breaker {
    (this as ProtectedSwitch).fillFields(networkService, includeRuntime)
    return this
}

fun LoadBreakSwitch.fillFields(networkService: NetworkService, includeRuntime: Boolean = true): LoadBreakSwitch {
    (this as ProtectedSwitch).fillFields(networkService, includeRuntime)
    return this
}

fun ProtectedSwitch.fillFields(networkService: NetworkService, includeRuntime: Boolean) {
    (this as Switch).fillFields(networkService, includeRuntime)
}

fun Switch.fillFields(networkService: NetworkService, includeRuntime: Boolean) {
    (this as ConductingEquipment).fillFields(networkService, includeRuntime)

    normalOpen = 1
    open = 2
}

fun PowerTransformer.fillFields(networkService: NetworkService, includeRuntime: Boolean = true): PowerTransformer {
    (this as ConductingEquipment).fillFields(networkService, includeRuntime)

    transformerUtilisation = 1.0
    vectorGroup = VectorGroup.DD0
    assetInfo = PowerTransformerInfo().also { networkService.add(it) }

    return this
}

fun PowerTransformerEnd.fillFields(networkService: NetworkService, includeRuntime: Boolean = true): PowerTransformerEnd {
    (this as TransformerEnd).fillFields(networkService, includeRuntime)

    powerTransformer = PowerTransformer().also { networkService.add(it) }
    powerTransformer?.addEnd(this)

    b = 1.0
    b0 = 2.0
    connectionKind = WindingConnection.Zn
    g = 3.0
    g0 = 4.0
    phaseAngleClock = 5
    r = 6.0
    r0 = 7.0
    ratedS = 8
    ratedU = 9
    x = 10.0
    x0 = 11.0

    return this
}

fun TransformerEnd.fillFields(networkService: NetworkService, includeRuntime: Boolean) {
    (this as IdentifiedObject).fillFields(networkService, includeRuntime)

    terminal = Terminal().also { networkService.add(it) }
    ratioTapChanger = RatioTapChanger().also {
        it.transformerEnd = this
        networkService.add(it)
    }
    baseVoltage = BaseVoltage().also { networkService.add(it) }
    grounded = true
    rGround = 1.0
    xGround = 2.0
    endNumber = 1
    starImpedance = TransformerStarImpedance().also { networkService.add(it) }
}

fun TransformerStarImpedance.fillFields(networkService: NetworkService, includeRuntime: Boolean = true): TransformerStarImpedance {
    (this as IdentifiedObject).fillFields(networkService, includeRuntime)

    r = 1.0
    r0 = 2.0
    x = 3.0
    x0 = 4.0

    transformerEndInfo = TransformerEndInfo().also {
        it.transformerStarImpedance = this
        networkService.add(it)
    }

    return this
}

/************ IEC61970 InfIEC61970 ************/

fun Circuit.fillFields(networkService: NetworkService, includeRuntime: Boolean = true): Circuit {
    (this as Line).fillFields(networkService, includeRuntime)

    loop = Loop().also {
        it.addCircuit(this)
        networkService.add(it)
    }

    for (i in 0..1) {
        addEndTerminal(Terminal().also { networkService.add(it) })
        addEndSubstation(Substation().also {
            it.addCircuit(this)
            networkService.add(it)
        })
    }


    return this
}

fun Loop.fillFields(networkService: NetworkService, includeRuntime: Boolean = true): Loop {
    (this as IdentifiedObject).fillFields(networkService, includeRuntime)

    for (i in 0..1) {
        addCircuit(Circuit().also {
            it.loop = this
            networkService.add(it)
        })

        addSubstation(Substation().also {
            it.addLoop(this)
            networkService.add(it)
        })

        addEnergizingSubstation(Substation().also {
            it.addEnergizedLoop(this)
            networkService.add(it)
        })
    }

    return this
}

/************ IEC61970 MEASUREMENT ************/

fun Measurement.fillFields(networkService: NetworkService, includeRuntime: Boolean) {
    (this as IdentifiedObject).fillFields(networkService, includeRuntime)

    powerSystemResourceMRID = PowerTransformer().mRID
    remoteSource = createRemoteSource(networkService, this)
    terminalMRID = Terminal().mRID
    phases = PhaseCode.ABCN
    unitSymbol = UnitSymbol.HENRYS
}

fun Analog.fillFields(networkService: NetworkService, includeRuntime: Boolean = true): Analog {
    (this as Measurement).fillFields(networkService, includeRuntime)
    return this
}

fun Accumulator.fillFields(networkService: NetworkService, includeRuntime: Boolean = true): Accumulator {
    (this as Measurement).fillFields(networkService, includeRuntime)
    return this
}

fun Discrete.fillFields(networkService: NetworkService, includeRuntime: Boolean = true): Discrete {
    (this as Measurement).fillFields(networkService, includeRuntime)
    return this
}

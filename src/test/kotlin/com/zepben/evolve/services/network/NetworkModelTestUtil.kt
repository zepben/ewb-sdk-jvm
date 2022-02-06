/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network

import com.zepben.evolve.cim.iec61968.assetinfo.OverheadWireInfo
import com.zepben.evolve.cim.iec61968.assetinfo.WireInfo
import com.zepben.evolve.cim.iec61968.common.Location
import com.zepben.evolve.cim.iec61968.common.PositionPoint
import com.zepben.evolve.cim.iec61968.common.StreetAddress
import com.zepben.evolve.cim.iec61968.common.TownDetail
import com.zepben.evolve.cim.iec61968.metering.Meter
import com.zepben.evolve.cim.iec61968.metering.UsagePoint
import com.zepben.evolve.cim.iec61968.operations.OperationalRestriction
import com.zepben.evolve.cim.iec61970.base.core.*
import com.zepben.evolve.cim.iec61970.base.diagramlayout.Diagram
import com.zepben.evolve.cim.iec61970.base.diagramlayout.DiagramObject
import com.zepben.evolve.cim.iec61970.base.diagramlayout.DiagramObjectPoint
import com.zepben.evolve.cim.iec61970.base.domain.UnitSymbol
import com.zepben.evolve.cim.iec61970.base.meas.Accumulator
import com.zepben.evolve.cim.iec61970.base.meas.Analog
import com.zepben.evolve.cim.iec61970.base.meas.Discrete
import com.zepben.evolve.cim.iec61970.base.meas.Measurement
import com.zepben.evolve.cim.iec61970.base.scada.RemoteSource
import com.zepben.evolve.cim.iec61970.base.wires.*
import com.zepben.evolve.services.common.meta.MetadataCollection
import com.zepben.evolve.services.customer.CustomerService
import com.zepben.evolve.services.diagram.DiagramService
import com.zepben.evolve.services.measurement.MeasurementService
import com.zepben.evolve.services.network.testdata.createTerminal
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import java.time.Instant

@Suppress("SameParameterValue", "BooleanLiteralArgument", "MemberVisibilityCanBePrivate")
class NetworkModelTestUtil {

    data class Services constructor(
        val metadataCollection: MetadataCollection = MetadataCollection(),
        val networkService: NetworkService = NetworkService(),
        val diagramService: DiagramService = DiagramService(),
        val customerService: CustomerService = CustomerService(),
        val measurementService: MeasurementService = MeasurementService()
    )

    companion object {
        private val baseVoltagesByNominalVoltage = mutableMapOf<Int, BaseVoltage>()

        //
        // NOTE: Feeder start points are not feeder CB's.
        //
        fun createFeederStart(network: NetworkService, num: Int) =
            Junction("fs$num")
                .apply {
                    name = "Feeder Start $num"
                }.also {
                    val energySource = createEnergySource(network, it.mRID + "_source", "")
                    network.connect(createTerminal(network, energySource, PhaseCode.A, 1), createTerminal(network, it, PhaseCode.A, 1))
                    assertThat(network.add(it), equalTo(true))
                }

        fun createSwitch(
            network: NetworkService,
            num: Int,
            normalCoreStatus: Boolean = false,
            currentCoreStatus: Boolean = false,
            equipmentContainer: EquipmentContainer? = null,
            location: Location? = null
        ): Breaker =
            Breaker("s$num")
                .apply {
                    name = "Switch $num"
                    setNormallyOpen(isNormallyOpen = normalCoreStatus)
                    setOpen(isOpen = currentCoreStatus)
                    equipmentContainer?.let { addContainer(it) }
                    this.location = location
                    location?.let { network.add(it) }
                }.also {
                    equipmentContainer?.addEquipment(it)
                    assertThat(network.add(it), equalTo(true))
                }

        fun createEnergySource(network: NetworkService, mRID: String, name: String, phases: PhaseCode = PhaseCode.A) =
            EnergySource(mRID)
                .apply {
                    this.name = name
                }.also {
                    phases.singlePhases.forEach { phase ->
                        val energySourcePhase = EnergySourcePhase().apply { this.phase = phase; energySource = it; it.addPhase(this) }
                        assertThat(network.add(energySourcePhase), equalTo(true))
                        it.addPhase(energySourcePhase)
                    }
                    assertThat(network.add(it), equalTo(true))
                }

        fun createJunction(network: NetworkService, num: Int, equipmentContainer: EquipmentContainer, location: Location?) =
            Junction("j$num")
                .apply {
                    name = "Junction $num"
                    addContainer(equipmentContainer)
                    this.location = location
                    location?.let { network.add(it) }
                }.also {
                    equipmentContainer.addEquipment(it)
                    assertThat(network.add(it), equalTo(true))
                }

        fun createTransformer(network: NetworkService, num: Int, equipmentContainer: EquipmentContainer, location: Location?) =
            PowerTransformer("t$num")
                .apply {
                    name = "Transformer $num"
                    addContainer(equipmentContainer)
                    this.location = location
                    location?.let { network.add(it) }
                }.also {
                    equipmentContainer.addEquipment(it)
                    assertThat(network.add(it), equalTo(true))
                }

        fun createAcLineSegment(
            network: NetworkService,
            num: Int,
            source: Terminal,
            dest: Terminal,
            wireInfo: WireInfo,
            equipmentContainer: EquipmentContainer? = null,
            location: Location? = null,
            phases: PhaseCode = PhaseCode.A
        ) =
            AcLineSegment("c$num")
                .apply {
                    name = "AC line segment $num"
                    assetInfo = wireInfo
                    this.location = location
                    equipmentContainer?.let { addContainer(it) }
                }.also {
                    network.connect(createTerminal(network, it, phases, 1), source)
                    network.connect(createTerminal(network, it, phases, 2), dest)
                    equipmentContainer?.addEquipment(it)
                    assertThat(network.add(it), equalTo(true))
                    location?.let { loc -> assertThat(network.add(loc), equalTo(true)) }
                }

        fun createService(
            network: NetworkService,
            num: Int,
            source: Terminal,
            dest: Terminal,
            equipmentContainer: EquipmentContainer,
            phases: PhaseCode = PhaseCode.A
        ) =
            AcLineSegment("service$num").apply {
                name = "Service $num"
                addContainer(equipmentContainer)
                assetInfo = network["serviceWireInfo"] ?: createOverheadWireInfo(network, "serviceWireInfo")
            }.also {
                network.connect(createTerminal(network, it, phases, 1), source)
                network.connect(createTerminal(network, it, phases, 2), dest)
                equipmentContainer.addEquipment(it)
                assertThat(network.add(it), equalTo(true))
            }

        fun createOverheadWireInfo(network: NetworkService, mRID: String = "") =
            OverheadWireInfo(mRID)
                .also {
                    assertThat(network.add(it), equalTo(true))
                }

        fun createGeographicalRegion(network: NetworkService, num: Int) =
            GeographicalRegion("b$num")
                .apply {
                    name = "Business $num"
                }.also {
                    assertThat(network.add(it), equalTo(true))
                }

        fun createSubGeographicalRegion(network: NetworkService, num: Int, geographicalRegion: GeographicalRegion) =
            SubGeographicalRegion("r$num")
                .apply {
                    name = "Sub Geographical Region $num"
                    this.geographicalRegion = geographicalRegion
                }.also {
                    geographicalRegion.addSubGeographicalRegion(it)
                    assertThat(network.add(it), equalTo(true))
                }

        fun createSubstation(network: NetworkService, num: Int, subGeographicalRegion: SubGeographicalRegion?) =
            Substation("z$num")
                .apply {
                    name = "Substation $num"
                    this.subGeographicalRegion = subGeographicalRegion
                }.also {
                    subGeographicalRegion?.addSubstation(it)
                    assertThat(network.add(it), equalTo(true))
                }

        fun createFeeder(network: NetworkService, num: Int, feederStart: ConductingEquipment?, substation: Substation?) =
            Feeder("f$num")
                .apply {
                    normalHeadTerminal = feederStart?.let { it.getTerminal(1)!! }
                    name = "Feeder $num"
                    normalEnergizingSubstation = substation
                }.also {
                    feederStart?.let { fs ->
                        setNormalFeeder(it, fs)
                        setCurrentFeeder(it, fs)
                    }
                    substation?.addFeeder(it)
                    assertThat(network.add(it), equalTo(true))
                }

        fun createSite(network: NetworkService, num: Int) =
            Site("site$num")
                .apply {
                    name = "Site $num"
                }.also {
                    assertThat(network.add(it), equalTo(true))
                }

        fun createOperationalRestriction(network: NetworkService, num: Int) =
            OperationalRestriction("OperationalRestriction$num")
                .apply {
                    name = "Operational Restriction $num"
                    authorName = "author$num"
                    createdDateTime = Instant.MIN.plusSeconds(num.toLong())
                    title = "title$num"
                    type = "type$num"
                    status = "status$num"
                    comment = "comment$num"
                }.also {
                    assertThat(network.add(it), equalTo(true))
                }

        fun setNormalFeeder(feeder: Feeder, vararg assets: ConductingEquipment) =
            assets.forEach {
                it.addContainer(feeder)
                feeder.addEquipment(it)
            }

        fun setCurrentFeeder(feeder: Feeder, vararg assets: ConductingEquipment) =
            assets.forEach {
                it.addCurrentFeeder(feeder)
                feeder.addCurrentEquipment(it)
            }

        fun addLvSimplification(transformer: PowerTransformer, usagePoint: UsagePoint, vararg meters: Meter) {
            transformer.addUsagePoint(usagePoint)
            usagePoint.addEquipment(transformer)
            meters.forEach {
                usagePoint.addEndDevice(it)
                it.addUsagePoint(usagePoint)
            }
        }

        fun locationOf(postcode: Int, state: String, locality: String) =
            Location().apply {
                mainAddress = StreetAddress(postcode.toString(), TownDetail(locality, state))
            }

        fun locationOf(vararg coords: Double) = Location().apply {
            for (i in coords.indices step 2)
                addPoint(PositionPoint(coords[i], coords[i + 1]))
        }

        fun baseVoltageOf(voltage: Int) = baseVoltagesByNominalVoltage.computeIfAbsent(voltage) {
            BaseVoltage().apply { nominalVoltage = it }
        }

        fun createDiagram(diagramService: DiagramService, mRID: String): Diagram {
            val diagram = Diagram(mRID)
            diagramService.add(diagram)
            return diagram
        }

        fun createDiagramObject(
            diagram: Diagram,
            identifiedObject: IdentifiedObject,
            diagramObjectStyle: String?,
            x: Double,
            y: Double,
            x2: Double? = null,
            y2: Double? = null
        ): DiagramObject {
            return DiagramObject().apply {
                diagram.addDiagramObject(this)

                identifiedObjectMRID = identifiedObject.mRID
                style = diagramObjectStyle

                addPoint(DiagramObjectPoint(x, y))
                x2?.let { addPoint(DiagramObjectPoint(it, y2!!)) }
            }
        }

        fun createRemoteSource(network: NetworkService, meas: Measurement): RemoteSource {
            return RemoteSource()
                .apply {
                    meas.remoteSource = this
                    measurement = meas
                }
                .also { assertThat(network.add(it), equalTo(true)) }
        }

        fun createAnalog(
            network: NetworkService,
            termMRID: String? = null,
            psr: PowerSystemResource? = null,
            isRemote: Boolean = false
        ): Analog {
            val meas = Analog()
                .apply {
                    powerSystemResourceMRID = psr?.mRID
                    terminalMRID = termMRID
                    phases = PhaseCode.XYN
                    unitSymbol = UnitSymbol.HOURS
                    positiveFlowIn = true
                }.also {
                    assertThat(network.add(it), equalTo(true))
                }

            if (isRemote)
                createRemoteSource(network, meas)

            return meas
        }


        fun createAccumulator(
            network: NetworkService,
            termMRID: String? = null,
            psr: PowerSystemResource? = null,
            isRemote: Boolean = false
        ): Accumulator {
            val meas = Accumulator()
                .apply {
                    powerSystemResourceMRID = psr?.mRID
                    terminalMRID = termMRID
                    phases = PhaseCode.XYN
                    unitSymbol = UnitSymbol.HOURS
                }.also {
                    assertThat(network.add(it), equalTo(true))
                }

            if (isRemote)
                createRemoteSource(network, meas)

            return meas
        }

        fun createDiscrete(
            network: NetworkService,
            termMRID: String? = null,
            psr: PowerSystemResource? = null,
            isRemote: Boolean = false
        ): Discrete {
            val meas = Discrete()
                .apply {
                    powerSystemResourceMRID = psr?.mRID
                    terminalMRID = termMRID
                    phases = PhaseCode.XYN
                    unitSymbol = UnitSymbol.HOURS
                }.also {
                    assertThat(network.add(it), equalTo(true))
                }
            if (isRemote)
                createRemoteSource(network, meas)

            return meas
        }
    }
}

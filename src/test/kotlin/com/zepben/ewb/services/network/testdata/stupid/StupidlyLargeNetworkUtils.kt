/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.network.testdata.stupid

import com.zepben.ewb.cim.extensions.iec61970.base.core.Site
import com.zepben.ewb.cim.iec61968.assetinfo.OverheadWireInfo
import com.zepben.ewb.cim.iec61968.assetinfo.WireInfo
import com.zepben.ewb.cim.iec61968.common.Location
import com.zepben.ewb.cim.iec61968.common.PositionPoint
import com.zepben.ewb.cim.iec61968.common.StreetAddress
import com.zepben.ewb.cim.iec61968.common.TownDetail
import com.zepben.ewb.cim.iec61968.metering.Meter
import com.zepben.ewb.cim.iec61968.metering.UsagePoint
import com.zepben.ewb.cim.iec61968.operations.OperationalRestriction
import com.zepben.ewb.cim.iec61970.base.core.*
import com.zepben.ewb.cim.iec61970.base.diagramlayout.Diagram
import com.zepben.ewb.cim.iec61970.base.diagramlayout.DiagramObject
import com.zepben.ewb.cim.iec61970.base.diagramlayout.DiagramObjectPoint
import com.zepben.ewb.cim.iec61970.base.domain.UnitSymbol
import com.zepben.ewb.cim.iec61970.base.meas.Accumulator
import com.zepben.ewb.cim.iec61970.base.meas.Analog
import com.zepben.ewb.cim.iec61970.base.meas.Discrete
import com.zepben.ewb.cim.iec61970.base.meas.Measurement
import com.zepben.ewb.cim.iec61970.base.scada.RemoteSource
import com.zepben.ewb.cim.iec61970.base.wires.*
import com.zepben.ewb.services.common.testdata.generateId
import com.zepben.ewb.services.diagram.DiagramService
import com.zepben.ewb.services.network.NetworkService
import com.zepben.ewb.services.network.testdata.createTerminal
import org.hamcrest.MatcherAssert.assertThat
import java.time.Instant

@Suppress("SameParameterValue", "MemberVisibilityCanBePrivate")
class StupidlyLargeNetworkUtils {

    companion object {
        private val baseVoltagesByNominalVoltage = mutableMapOf<Int, BaseVoltage>()

        //
        // NOTE: Feeder start points are not feeder CB's.
        //
        fun createFeederStart(network: NetworkService, num: Int): Junction =
            Junction("fs$num")
                .apply {
                    name = "Feeder Start $num"
                }.also {
                    val energySource = createEnergySource(network, it.mRID + "_source", "")
                    network.connect(createTerminal(network, energySource, PhaseCode.A, 1), createTerminal(network, it, PhaseCode.A, 1))
                    assertThat("Initial add should return true", network.add(it))
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
                    assertThat("Initial add should return true", network.add(it))
                }

        fun createEnergySource(network: NetworkService, mRID: String, name: String, phases: PhaseCode = PhaseCode.A): EnergySource =
            EnergySource(mRID)
                .apply {
                    this.name = name
                }.also {
                    phases.singlePhases.forEach { phase ->
                        val energySourcePhase = EnergySourcePhase(generateId()).apply { this.phase = phase; energySource = it; it.addPhase(this) }
                        assertThat("Initial add should return true", network.add(energySourcePhase))
                        it.addPhase(energySourcePhase)
                    }
                    assertThat("Initial add should return true", network.add(it))
                }

        fun createJunction(network: NetworkService, num: Int, equipmentContainer: EquipmentContainer, location: Location?): Junction =
            Junction("j$num")
                .apply {
                    name = "Junction $num"
                    addContainer(equipmentContainer)
                    this.location = location
                    location?.let { network.add(it) }
                }.also {
                    equipmentContainer.addEquipment(it)
                    assertThat("Initial add should return true", network.add(it))
                }

        fun createTransformer(network: NetworkService, num: Int, equipmentContainer: EquipmentContainer, location: Location?): PowerTransformer =
            PowerTransformer("t$num")
                .apply {
                    name = "Transformer $num"
                    addContainer(equipmentContainer)
                    this.location = location
                    location?.let { network.add(it) }
                }.also {
                    equipmentContainer.addEquipment(it)
                    assertThat("Initial add should return true", network.add(it))
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
        ): AcLineSegment =
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
                    assertThat("Initial add should return true", network.add(it))
                    location?.let { loc -> assertThat("Initial add should return true", network.add(loc)) }
                }

        fun createService(
            network: NetworkService,
            num: Int,
            source: Terminal,
            dest: Terminal,
            equipmentContainer: EquipmentContainer,
            phases: PhaseCode = PhaseCode.A
        ): AcLineSegment =
            AcLineSegment("service$num").apply {
                name = "Service $num"
                addContainer(equipmentContainer)
                assetInfo = network["serviceWireInfo"] ?: createOverheadWireInfo(network, "serviceWireInfo")
            }.also {
                network.connect(createTerminal(network, it, phases, 1), source)
                network.connect(createTerminal(network, it, phases, 2), dest)
                equipmentContainer.addEquipment(it)
                assertThat("Initial add should return true", network.add(it))
            }

        fun createOverheadWireInfo(network: NetworkService, mRID: String): OverheadWireInfo =
            OverheadWireInfo(mRID)
                .also {
                    assertThat("Initial add should return true", network.add(it))
                }

        fun createGeographicalRegion(network: NetworkService, num: Int): GeographicalRegion =
            GeographicalRegion("b$num")
                .apply {
                    name = "Business $num"
                }.also {
                    assertThat("Initial add should return true", network.add(it))
                }

        fun createSubGeographicalRegion(network: NetworkService, num: Int, geographicalRegion: GeographicalRegion): SubGeographicalRegion =
            SubGeographicalRegion("r$num")
                .apply {
                    name = "Sub Geographical Region $num"
                    this.geographicalRegion = geographicalRegion
                }.also {
                    geographicalRegion.addSubGeographicalRegion(it)
                    assertThat("Initial add should return true", network.add(it))
                }

        fun createSubstation(network: NetworkService, num: Int, subGeographicalRegion: SubGeographicalRegion?): Substation =
            Substation("z$num")
                .apply {
                    name = "Substation $num"
                    this.subGeographicalRegion = subGeographicalRegion
                }.also {
                    subGeographicalRegion?.addSubstation(it)
                    assertThat("Initial add should return true", network.add(it))
                }

        fun createFeeder(network: NetworkService, num: Int, feederStart: ConductingEquipment?, substation: Substation?): Feeder =
            Feeder("f$num")
                .apply {
                    normalHeadTerminal = feederStart?.t1
                    name = "Feeder $num"
                    normalEnergizingSubstation = substation
                }.also {
                    feederStart?.let { fs ->
                        setNormalFeeder(it, fs)
                        setCurrentFeeder(it, fs)
                    }
                    substation?.addFeeder(it)
                    assertThat("Initial add should return true", network.add(it))
                }

        fun createSite(network: NetworkService, num: Int): Site =
            Site("site$num")
                .apply {
                    name = "Site $num"
                }.also {
                    assertThat("Initial add should return true", network.add(it))
                }

        fun createOperationalRestriction(network: NetworkService, num: Int): OperationalRestriction =
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
                    assertThat("Initial add should return true", network.add(it))
                }

        fun setNormalFeeder(feeder: Feeder, vararg assets: ConductingEquipment): Unit =
            assets.forEach {
                it.addContainer(feeder)
                feeder.addEquipment(it)
            }

        fun setCurrentFeeder(feeder: Feeder, vararg assets: ConductingEquipment): Unit =
            assets.forEach {
                it.addCurrentContainer(feeder)
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

        fun locationOf(postcode: Int, state: String, locality: String): Location =
            Location(generateId()).apply {
                mainAddress = StreetAddress(postcode.toString(), TownDetail(locality, state))
            }

        fun locationOf(vararg coords: Double): Location = Location(generateId()).apply {
            for (i in coords.indices step 2)
                addPoint(PositionPoint(coords[i], coords[i + 1]))
        }

        fun baseVoltageOf(voltage: Int): BaseVoltage = baseVoltagesByNominalVoltage.computeIfAbsent(voltage) {
            BaseVoltage(generateId()).apply { nominalVoltage = it }
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
            return DiagramObject(generateId()).apply {
                diagram.addDiagramObject(this)

                identifiedObjectMRID = identifiedObject.mRID
                style = diagramObjectStyle

                addPoint(DiagramObjectPoint(x, y))
                x2?.let { addPoint(DiagramObjectPoint(it, y2!!)) }
            }
        }

        fun createRemoteSource(network: NetworkService, meas: Measurement): RemoteSource {
            return RemoteSource(generateId())
                .apply {
                    meas.remoteSource = this
                    measurement = meas
                }
                .also { assertThat("Initial add should return true", network.add(it)) }
        }

        fun createAnalog(
            network: NetworkService,
            termMRID: String? = null,
            psr: PowerSystemResource? = null,
            isRemote: Boolean = false
        ): Analog {
            val meas = Analog(generateId())
                .apply {
                    powerSystemResourceMRID = psr?.mRID
                    terminalMRID = termMRID
                    phases = PhaseCode.XYN
                    unitSymbol = UnitSymbol.HOURS
                    positiveFlowIn = true
                }.also {
                    assertThat("Initial add should return true", network.add(it))
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
            val meas = Accumulator(generateId())
                .apply {
                    powerSystemResourceMRID = psr?.mRID
                    terminalMRID = termMRID
                    phases = PhaseCode.XYN
                    unitSymbol = UnitSymbol.HOURS
                }.also {
                    assertThat("Initial add should return true", network.add(it))
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
            val meas = Discrete(generateId())
                .apply {
                    powerSystemResourceMRID = psr?.mRID
                    terminalMRID = termMRID
                    phases = PhaseCode.XYN
                    unitSymbol = UnitSymbol.HOURS
                }.also {
                    assertThat("Initial add should return true", network.add(it))
                }
            if (isRemote)
                createRemoteSource(network, meas)

            return meas
        }
    }
}

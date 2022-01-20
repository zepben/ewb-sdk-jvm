/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network.testdata

import com.zepben.evolve.cim.iec61968.assetinfo.CableInfo
import com.zepben.evolve.cim.iec61968.assetinfo.OverheadWireInfo
import com.zepben.evolve.cim.iec61968.assetinfo.PowerTransformerInfo
import com.zepben.evolve.cim.iec61968.common.Location
import com.zepben.evolve.cim.iec61968.common.StreetAddress
import com.zepben.evolve.cim.iec61968.common.StreetDetail
import com.zepben.evolve.cim.iec61968.common.TownDetail
import com.zepben.evolve.cim.iec61968.customers.*
import com.zepben.evolve.cim.iec61968.metering.Meter
import com.zepben.evolve.cim.iec61968.metering.UsagePoint
import com.zepben.evolve.cim.iec61968.operations.OperationalRestriction
import com.zepben.evolve.cim.iec61970.base.auxiliaryequipment.FaultIndicator
import com.zepben.evolve.cim.iec61970.base.core.GeographicalRegion
import com.zepben.evolve.cim.iec61970.base.core.PhaseCode
import com.zepben.evolve.cim.iec61970.base.core.SubGeographicalRegion
import com.zepben.evolve.cim.iec61970.base.core.Substation
import com.zepben.evolve.cim.iec61970.base.wires.*
import com.zepben.evolve.services.common.meta.MetadataCollection
import com.zepben.evolve.services.customer.CustomerService
import com.zepben.evolve.services.diagram.DiagramService
import com.zepben.evolve.services.measurement.MeasurementService
import com.zepben.evolve.services.network.NetworkModelTestUtil
import com.zepben.evolve.services.network.NetworkModelTestUtil.Companion.baseVoltageOf
import com.zepben.evolve.services.network.NetworkModelTestUtil.Companion.createAcLineSegment
import com.zepben.evolve.services.network.NetworkModelTestUtil.Companion.createAccumulator
import com.zepben.evolve.services.network.NetworkModelTestUtil.Companion.createAnalog
import com.zepben.evolve.services.network.NetworkModelTestUtil.Companion.createDiagram
import com.zepben.evolve.services.network.NetworkModelTestUtil.Companion.createDiagramObject
import com.zepben.evolve.services.network.NetworkModelTestUtil.Companion.createDiscrete
import com.zepben.evolve.services.network.NetworkModelTestUtil.Companion.createEnergySource
import com.zepben.evolve.services.network.NetworkModelTestUtil.Companion.createFeeder
import com.zepben.evolve.services.network.NetworkModelTestUtil.Companion.createFeederStart
import com.zepben.evolve.services.network.NetworkModelTestUtil.Companion.createGeographicalRegion
import com.zepben.evolve.services.network.NetworkModelTestUtil.Companion.createJunction
import com.zepben.evolve.services.network.NetworkModelTestUtil.Companion.createOperationalRestriction
import com.zepben.evolve.services.network.NetworkModelTestUtil.Companion.createService
import com.zepben.evolve.services.network.NetworkModelTestUtil.Companion.createSite
import com.zepben.evolve.services.network.NetworkModelTestUtil.Companion.createSubGeographicalRegion
import com.zepben.evolve.services.network.NetworkModelTestUtil.Companion.createSubstation
import com.zepben.evolve.services.network.NetworkModelTestUtil.Companion.createSwitch
import com.zepben.evolve.services.network.NetworkModelTestUtil.Companion.createTransformer
import com.zepben.evolve.services.network.NetworkModelTestUtil.Companion.locationOf
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.tracing.Tracing
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo

@Suppress("SameParameterValue", "BooleanLiteralArgument")
object StupidlyLargeNetwork {

    fun create(): NetworkModelTestUtil.Services {
        val networkService = NetworkService()
        val diagramService = DiagramService()
        val customerService = CustomerService()
        val measurementService = MeasurementService()

        addSampleNetwork(networkService, diagramService, customerService)

        // ###############################################################################
        // #                                                                             #
        // # NOTE: From here on the items are not in the Visio diagram as they are for   #
        // #       testing combinations in data rather than being a real network.        #
        // #                                                                             #
        // ###############################################################################

        val geographicalRegion2 = createGeographicalRegion(networkService, 2)
        val geographicalRegion3 = createGeographicalRegion(networkService, 3)
        createGeographicalRegion(networkService, 4)

        val subGeographicalRegion2 = createSubGeographicalRegion(networkService, 2, geographicalRegion2)
        val subGeographicalRegion3 = createSubGeographicalRegion(networkService, 3, geographicalRegion2)
        createSubGeographicalRegion(networkService, 4, geographicalRegion2)
        createSubGeographicalRegion(networkService, 5, geographicalRegion3)
        createSubGeographicalRegion(networkService, 6, geographicalRegion3)

        val substation3 = createSubstation(networkService, 3, subGeographicalRegion2)
        createSubstation(networkService, 4, subGeographicalRegion2)
        createSubstation(networkService, 5, subGeographicalRegion2)
        createSubstation(networkService, 6, subGeographicalRegion3)

        val feederStart4 = createFeederStart(networkService, 4)
        val feederStart5 = createFeederStart(networkService, 5)
        val feederStart6 = createFeederStart(networkService, 6)
        createFeeder(networkService, 4, feederStart4, substation3)
        createFeeder(networkService, 5, feederStart5, substation3)
        createFeeder(networkService, 6, feederStart6, substation3)

        val singleVoltage = baseVoltageOf(11000)
        val tripleVoltage = arrayOf(baseVoltageOf(33000), baseVoltageOf(11000), baseVoltageOf(6600))

        networkService.add(singleVoltage)
        tripleVoltage.forEach { networkService.add(it) }

        val oh1 = OverheadWireInfo("overhead1")
        val oh2 = OverheadWireInfo("overhead2")

        networkService.add(oh1)
        networkService.add(oh2)

        val perLengthSequenceImpedance = PerLengthSequenceImpedance("per_length_sequence_impedance")
        val perLengthSequenceImpedance2 = PerLengthSequenceImpedance("per_length_sequence_impedance_with_material")

        networkService.add(perLengthSequenceImpedance)
        networkService.add(perLengthSequenceImpedance2)

        val aSwitchLoc = locationOf(2905, "ACT", "Fyshwick")
        val transformerLoc = locationOf(2601, "ACT", "Canberra")
        val node2Loc = locationOf(2601, "ACT", "Canberra")

        val sourceNode = createEnergySource(networkService, "sourceNode", "", PhaseCode.ABCN).apply { baseVoltage = singleVoltage }
        val acLineSegment101 = AcLineSegment("acLineSegment101").apply {
            name = "AC Lne Segment 101"
            baseVoltage = singleVoltage
            length = 5.0
            this.perLengthSequenceImpedance = perLengthSequenceImpedance
            this.assetInfo = oh1
        }
        val aSwitch = Breaker("switch").apply {
            name = "Switch"
            location = aSwitchLoc
            baseVoltage = singleVoltage
        }
        val acLineSegment202 = AcLineSegment("acLineSegment202").apply {
            name = "AC Lne Segment 202"
            this.perLengthSequenceImpedance = perLengthSequenceImpedance2
            this.assetInfo = oh1
        }

        val transformerInfo = PowerTransformerInfo("transformer_info")
        val isoTransformerInfo1 = PowerTransformerInfo("id_iso_transformer1_info")

        val transformer = PowerTransformer("transformer").apply {
            name = "Transformer"
            location = transformerLoc
            baseVoltage = tripleVoltage[0]
            assetInfo = transformerInfo
        }
        val isoTransformer1 = PowerTransformer("id_iso_transformer1").apply {
            name = "iso_transformer1"
            assetInfo = isoTransformerInfo1
        }
        val powerTransformer2 = PowerTransformer("id_iso_transformer2").apply {
            name = "iso_transformer2"
        }
        val powerTransformer3 = PowerTransformer("id_iso_transformer3").apply {
            name = "iso_transformer3"
        }
        val acLineSegment303 = AcLineSegment("acLineSegment303").apply {
            name = "AC lne segment 303"
            assetInfo = oh1
        }
        val node1 = Junction("node1").apply {
            name = "Node 1"
        }
        val node2 = Junction("node2").apply {
            name = "Node 2"
            location = node2Loc
        }
        val node3 = Junction("node3").apply { name = "Node 3" }

        createTerminals(networkService, transformer, 3, PhaseCode.ABCN)

        val diagram0 = createDiagram(diagramService, "0")
        val diagram1 = createDiagram(diagramService, "1")

        diagramService.add(createDiagramObject(diagram0, sourceNode, "ENERGY_SOURCE", 5.0, 35.0))
        diagramService.add(createDiagramObject(diagram0, acLineSegment101, "CONDUCTOR_11000", 35.0, 32.0, 10.0, 32.0))
        diagramService.add(createDiagramObject(diagram0, acLineSegment101, "CONDUCTOR_11000", 35.0, 34.0, 10.0, 34.0))
        diagramService.add(createDiagramObject(diagram0, acLineSegment101, "CONDUCTOR_11000", 35.0, 36.0, 10.0, 36.0))
        diagramService.add(createDiagramObject(diagram0, acLineSegment101, "CONDUCTOR_11000", 35.0, 38.0, 10.0, 38.0))
        diagramService.add(createDiagramObject(diagram0, aSwitch, "CB", 40.0, 35.0))
        diagramService.add(createDiagramObject(diagram0, acLineSegment202, "CONDUCTOR_UNKNOWN", 70.0, 32.0, 45.0, 32.0))
        diagramService.add(createDiagramObject(diagram0, acLineSegment202, "CONDUCTOR_UNKNOWN", 70.0, 34.0, 45.0, 34.0))
        diagramService.add(createDiagramObject(diagram0, acLineSegment202, "CONDUCTOR_UNKNOWN", 70.0, 36.0, 45.0, 36.0))
        diagramService.add(createDiagramObject(diagram0, acLineSegment202, "CONDUCTOR_UNKNOWN", 70.0, 38.0, 45.0, 38.0))
        diagramService.add(createDiagramObject(diagram0, transformer, "DIST_TRANSFORMER", 75.0, 35.0))
        diagramService.add(createDiagramObject(diagram0, acLineSegment303, "CONDUCTOR_UNKNOWN", 100.0, 34.0, 80.0, 34.0))
        diagramService.add(createDiagramObject(diagram0, acLineSegment303, "CONDUCTOR_UNKNOWN", 105.0, 27.0, 100.0, 34.0))
        diagramService.add(createDiagramObject(diagram0, acLineSegment303, "CONDUCTOR_UNKNOWN", 100.0, 36.0, 80.0, 36.0))
        diagramService.add(createDiagramObject(diagram0, node1, "JUNCTION", 110.0, 27.0))
        diagramService.add(createDiagramObject(diagram1, node1, "JUNCTION", 0.0, 0.0))
        diagramService.add(createDiagramObject(diagram1, node1, "JUNCTION", 0.0, 0.0))
        diagramService.add(createDiagramObject(diagram1, node1, "JUNCTION", 1000.0, 0.0))
        diagramService.add(createDiagramObject(diagram1, node1, "JUNCTION", 1.5, 1001.1).apply { rotation = 22.5 })
        diagramService.add(createDiagramObject(diagram1, acLineSegment101, "CONDUCTOR_11000", 2.0, 1002.0, 1.0, 1001.0))
        diagramService.add(createDiagramObject(diagram1, acLineSegment101, "CONDUCTOR_11000", 2.0, 1002.0, 1.0, 1001.0))
        diagramService.add(createDiagramObject(diagram1, acLineSegment101, "CONDUCTOR_11000", 2.4, 24.3, 1.5, 0.1))

        networkService.add(sourceNode)
        networkService.add(acLineSegment101)
        networkService.add(aSwitchLoc)
        networkService.add(aSwitch)
        networkService.add(acLineSegment202)
        networkService.add(transformerLoc)
        networkService.add(transformerInfo)
        networkService.add(transformer)
        networkService.add(acLineSegment303)
        networkService.add(node1)
        networkService.add(node2Loc)
        networkService.add(node2)
        networkService.add(node3)
        networkService.add(isoTransformer1)
        networkService.add(isoTransformerInfo1)
        networkService.add(powerTransformer2)
        networkService.add(powerTransformer3)

        // This terminal is left un-wired for testing purposes
        createTerminal(networkService, node3, PhaseCode.A, 1)

        networkService.connect(
            createTerminal(networkService, sourceNode, PhaseCode.ABCN, 1),
            createTerminal(networkService, acLineSegment101, PhaseCode.ABCN, 1)
        )
        networkService.connect(createTerminal(networkService, acLineSegment101, PhaseCode.ABCN, 2), createTerminal(networkService, aSwitch, PhaseCode.ABCN, 1))
        networkService.connect(createTerminal(networkService, aSwitch, PhaseCode.ABCN, 2), createTerminal(networkService, acLineSegment202, PhaseCode.ABCN, 1))
        networkService.connect(createTerminal(networkService, acLineSegment202, PhaseCode.ABCN, 2), transformer.getTerminal(2)!!)
        networkService.connect(transformer.getTerminal(3)!!, createTerminal(networkService, acLineSegment303, PhaseCode.BC, 1))
        networkService.connect(createTerminal(networkService, acLineSegment303, PhaseCode.BC, 2), "cn_2")
        networkService.connect(createTerminal(networkService, node1, PhaseCode.C, 1), "cn_2")
        networkService.connect(createTerminal(networkService, node1, PhaseCode.B, 2), "cn_2")

        val multiTerminalAcLineSegment1 = AcLineSegment("multiTerminalAcLineSegment1").apply { name = "multiTerminalAcLineSegment1"; assetInfo = oh2 }
        val multiTerminalAcLineSegment2 = AcLineSegment("multiTerminalAcLineSegment2").apply { assetInfo = oh2; length = Double.NaN }
        val multiTerminalNode1 = Junction("multiTerminalNode1").apply { name = "multiTerminalNode1" }
        val multiTerminalNode2 = Junction("multiTerminalNode2").apply { name = "multiTerminalNode2" }
        val multiTerminalNode3 = Junction("multiTerminalNode3").apply { name = "multiTerminalNode3" }

        networkService.connect(
            createTerminal(networkService, multiTerminalAcLineSegment1, PhaseCode.A, 1),
            createTerminal(networkService, multiTerminalAcLineSegment2, PhaseCode.A, 1)
        )
        networkService.connect(
            createTerminal(networkService, multiTerminalAcLineSegment1, PhaseCode.A, 2),
            createTerminal(networkService, multiTerminalNode1, PhaseCode.A, 1)
        )
        networkService.connect(
            createTerminal(networkService, multiTerminalAcLineSegment1, PhaseCode.A, 3),
            createTerminal(networkService, multiTerminalNode2, PhaseCode.A, 1)
        )
        networkService.connect(
            createTerminal(networkService, multiTerminalAcLineSegment1, PhaseCode.A, 4),
            createTerminal(networkService, multiTerminalNode3, PhaseCode.A, 1)
        )

        networkService.add(multiTerminalAcLineSegment1)
        networkService.add(multiTerminalAcLineSegment2)
        networkService.add(multiTerminalNode1)
        networkService.add(multiTerminalNode2)
        networkService.add(multiTerminalNode3)

        val transformerWithTypeNone = PowerTransformer("transformerWithTypeNone")
        val transformerWithTypeDist = PowerTransformer("transformerWithTypeDist")
        val transformerWithTypeIso = PowerTransformer("transformerWithTypeIso")
        val transformerWithTypeReg = PowerTransformer("transformerWithTypeReg")
        val transformerWithTypeNonRevReg = PowerTransformer("transformerWithTypeNonRevReg")
        val transformerWithTypeZone = PowerTransformer("transformerWithTypeZone")

        diagramService.add(createDiagramObject(diagram0, transformerWithTypeDist, "DIST_TRANSFORMER", 110.0, 27.0))
        diagramService.add(createDiagramObject(diagram0, transformerWithTypeIso, "ISO_TRANSFORMER", 110.0, 27.0))
        diagramService.add(createDiagramObject(diagram0, transformerWithTypeReg, "REVERSIBLE_REGULATOR", 110.0, 27.0))
        diagramService.add(createDiagramObject(diagram0, transformerWithTypeNonRevReg, "NON_REVERSIBLE_REGULATOR", 110.0, 27.0))
        diagramService.add(createDiagramObject(diagram0, transformerWithTypeZone, "ZONE_TRANSFORMER", 110.0, 27.0))

        networkService.add(transformerWithTypeNone)
        networkService.add(transformerWithTypeDist)
        networkService.add(transformerWithTypeIso)
        networkService.add(transformerWithTypeReg)
        networkService.add(transformerWithTypeNonRevReg)
        networkService.add(transformerWithTypeZone)

        Tracing.setPhases().run(networkService)
        Tracing.assignEquipmentContainersToFeeders().run(networkService)

        networkService.add(OperationalRestriction("OperationalRestriction2").apply {
            name = "Operational Restriction 2"
            authorName = "author2"
            createdDateTime = null
            title = "title2"
            type = "type2"
            status = "status2"
            comment = "comment2"
        })

        // Measurements
        createAnalog(networkService, transformer.getTerminal(1)?.mRID, isoTransformer1)
        createAccumulator(networkService, transformer.getTerminal(1)?.mRID, isoTransformer1)
        createDiscrete(networkService, transformer.getTerminal(1)?.mRID, isoTransformer1)

        return NetworkModelTestUtil.Services(MetadataCollection(), networkService, diagramService, customerService, measurementService)
    }

    private fun addSampleNetwork(networkService: NetworkService, diagramService: DiagramService, customerService: CustomerService) {
        // See doc/TestSchemaNetwork.vsdx for a graphic of the following network.
        val site1 = createSite(networkService, 1)
        val site2 = createSite(networkService, 2)
        val site3 = createSite(networkService, 3)
        val site4 = createSite(networkService, 4)
        val site5 = createSite(networkService, 5)
        val site6 = createSite(networkService, 6)
        val site7 = createSite(networkService, 7)
        val site8 = createSite(networkService, 8)

        val feederStart1 = createFeederStart(networkService, 1)
        val feederStart2 = createFeederStart(networkService, 2)
        val feederStart3 = createFeederStart(networkService, 3)

        val switch1 = createSwitch(networkService, 1, false, false, site2, locationOf(1.0, 0.0))
        val switch2 = createSwitch(networkService, 2, false, true, site2)
        val switch3 = createSwitch(networkService, 3, true, true, site2)
        val switch4 = createSwitch(networkService, 4, true, false, site6)

        val primarySource = createEnergySource(networkService, "primary_source", "primary source")
        networkService.connect(createTerminal(networkService, primarySource, PhaseCode.A, 1), createTerminal(networkService, switch4, PhaseCode.A, 1))

        val geographicalRegion = GeographicalRegion()
        val subGeographicalRegion =
            SubGeographicalRegion().also { it.geographicalRegion = geographicalRegion; geographicalRegion.addSubGeographicalRegion(it) }
        val substation = Substation().also { it.subGeographicalRegion = subGeographicalRegion; subGeographicalRegion.addSubstation(it) }

        networkService.add(geographicalRegion)
        networkService.add(subGeographicalRegion)
        networkService.add(substation)

        val breaker1: Breaker =
            Breaker("id_breaker1").apply { name = "breaker1"; addContainer(substation); createTerminal(networkService, this, PhaseCode.A, 1) }
        val breaker2: Breaker = Breaker("id_breaker2").apply { name = "breaker2" }
        val disconnector1 = Disconnector("id_disconnector1").apply { name = "disconnector" }
        val disconnector2 = Disconnector("id_disconnector2").apply { name = "disconnector" }
        val fuse1 = Fuse("id_fuse").apply { name = "fuse" }
        val recloser1 = Recloser("id_recloser").apply { name = "recloser" }
        val faultIndicator1 = FaultIndicator(", id_fault_indicator").apply {
            name = "fault_indicator"
            terminal = createTerminal(networkService, recloser1, PhaseCode.A, 1)
        }
        val jumper1 = Jumper("id_jumper").apply { name = "jumper" }
        createEnergySource(networkService, "id_energy_source", "energy_source")
        val linearShuntCompensator1 = LinearShuntCompensator("id_shunt_compensator").apply { name = "shunt_compensator" }
        val energyConsumer1 = EnergyConsumer("id_energy_consumer").apply { name = "energy_consumer" }
        val usagePoint = UsagePoint(energyConsumer1.mRID + "-up").apply { name = energyConsumer1.name + " usage point" }
        val acme = createAssetOwner(networkService, customerService, "acme")
        val hvLocation = locationOf(1234, "STATE", "locality")
        val hvMeter = Meter().apply {
            name = "acme_1111"
            addOrganisationRole(acme)
            serviceLocation = hvLocation
        }

        networkService.add(breaker1)
        networkService.add(breaker2)
        networkService.add(disconnector1)
        networkService.add(disconnector2)
        networkService.add(fuse1)
        networkService.add(recloser1)
        networkService.add(faultIndicator1)
        networkService.add(jumper1)
        networkService.add(linearShuntCompensator1)
        networkService.add(energyConsumer1)
        networkService.add(usagePoint)
        networkService.add(hvMeter)
        networkService.add(hvLocation)

        substation.addEquipment(breaker1)
        energyConsumer1.addUsagePoint(usagePoint)
        usagePoint.addEquipment(energyConsumer1)
        usagePoint.addEndDevice(hvMeter)
        hvMeter.addUsagePoint(usagePoint)

        val junction1 = createJunction(networkService, 1, site1, locationOf(0.0, 3.0))
        val junction2 = createJunction(networkService, 2, site1, null)
        val junction3 = createJunction(networkService, 3, site2, null)
        val junction4 = createJunction(networkService, 4, site3, null)
        val junction5 = createJunction(networkService, 5, site4, null)
        val junction6 = createJunction(networkService, 6, site5, null)
        val junction7 = createJunction(networkService, 7, site7, null)
        val junction8 = createJunction(networkService, 8, site7, null)
        val junction9 = createJunction(networkService, 9, site8, null)
        val junction10 = createJunction(networkService, 10, site8, null)

        val transformer1 = createTransformer(networkService, 1, site1, locationOf(1.0, 10.0))
        val transformer2 = createTransformer(networkService, 2, site1, null)
        val transformer3 = createTransformer(networkService, 3, site3, null)
        val transformer4 = createTransformer(networkService, 4, site4, null)
        val transformer5 = createTransformer(networkService, 5, site5, null)
        val transformer6 = createTransformer(networkService, 6, site7, null)
        val transformer7 = createTransformer(networkService, 7, site7, null)
        val transformer8 = createTransformer(networkService, 8, site8, null)
        val transformer9 = createTransformer(networkService, 9, site8, locationOf(1234, "STATE", "tx location"))

        val regulator1 = PowerTransformer("id_regulator1").apply { name = "regulator1" }
        val regulator2 = PowerTransformer("id_regulator2").apply { name = "regulator2" }

        val powerTransformer1 = PowerTransformer("id_zone_transformer").apply { name = "zone_transformer" }

        networkService.add(regulator1)
        networkService.add(regulator2)
        networkService.add(powerTransformer1)

        addLvSimplification(networkService, customerService)

        val ug1 = CableInfo("ug1")
        val ug2 = CableInfo("ug2")
        val oh1 = OverheadWireInfo("oh1")
        val oh2 = OverheadWireInfo("oh2")

        networkService.add(ug1)
        networkService.add(ug2)
        networkService.add(oh1)
        networkService.add(oh2)

        createAcLineSegment(
            networkService,
            1,
            createTerminal(networkService, feederStart1, PhaseCode.A, 1),
            createTerminal(networkService, junction1, PhaseCode.A, 1),
            oh1,
            null,
            locationOf(1.1, 0.1, 2.2, 0.1, 3.3, 0.3)
        )
        createAcLineSegment(
            networkService,
            2,
            createTerminal(networkService, junction1, PhaseCode.A, 2),
            createTerminal(networkService, junction2, PhaseCode.A, 1),
            oh2,
            site1
        )
        createAcLineSegment(
            networkService,
            3,
            createTerminal(networkService, junction2, PhaseCode.A, 2),
            createTerminal(networkService, switch1, PhaseCode.A, 1),
            ug1,
            null,
            null
        )
        createAcLineSegment(
            networkService,
            4,
            createTerminal(networkService, switch1, PhaseCode.A, 2),
            createTerminal(networkService, junction3, PhaseCode.A, 1),
            ug2,
            site2
        )
        createAcLineSegment(
            networkService,
            5,
            createTerminal(networkService, junction3, PhaseCode.A, 2),
            createTerminal(networkService, switch2, PhaseCode.A, 1),
            oh1,
            site2
        )
        createAcLineSegment(
            networkService,
            6,
            createTerminal(networkService, junction3, PhaseCode.A, 3),
            createTerminal(networkService, switch3, PhaseCode.A, 1),
            oh1,
            site2
        )
        createAcLineSegment(
            networkService,
            7,
            createTerminal(networkService, switch2, PhaseCode.A, 2),
            createTerminal(networkService, junction4, PhaseCode.A, 1),
            oh1,
            null,
            null
        )
        createAcLineSegment(
            networkService,
            8,
            createTerminal(networkService, junction4, PhaseCode.A, 2),
            createTerminal(networkService, junction5, PhaseCode.A, 1),
            oh2,
            null,
            null
        )
        createAcLineSegment(
            networkService,
            9,
            createTerminal(networkService, junction5, PhaseCode.A, 2),
            createTerminal(networkService, junction6, PhaseCode.A, 1),
            ug1,
            null,
            null
        )
        createAcLineSegment(
            networkService,
            10,
            createTerminal(networkService, junction6, PhaseCode.A, 2),
            createTerminal(networkService, switch4, PhaseCode.A, 1),
            oh1,
            null,
            null
        )
        createAcLineSegment(
            networkService,
            11,
            createTerminal(networkService, switch4, PhaseCode.A, 2),
            createTerminal(networkService, junction7, PhaseCode.A, 1),
            ug1,
            null,
            null
        )
        createAcLineSegment(
            networkService,
            12,
            createTerminal(networkService, junction7, PhaseCode.A, 2),
            createTerminal(networkService, junction8, PhaseCode.A, 1),
            ug2,
            site7
        )
        createAcLineSegment(
            networkService,
            13,
            createTerminal(networkService, junction8, PhaseCode.A, 2),
            createTerminal(networkService, feederStart2, PhaseCode.A, 1),
            oh1
        )
        createAcLineSegment(
            networkService,
            14,
            createTerminal(networkService, switch3, PhaseCode.A, 2),
            createTerminal(networkService, junction9, PhaseCode.A, 1),
            oh1,
            null,
            null
        )
        createAcLineSegment(
            networkService,
            15,
            createTerminal(networkService, junction9, PhaseCode.A, 2),
            createTerminal(networkService, junction10, PhaseCode.A, 1),
            oh1,
            site8,
            null
        )
        createAcLineSegment(
            networkService,
            16,
            createTerminal(networkService, junction10, PhaseCode.A, 2),
            createTerminal(networkService, feederStart3, PhaseCode.A, 1),
            oh1
        )
        createService(
            networkService,
            1,
            createTerminal(networkService, junction1, PhaseCode.A, 3),
            createTerminal(networkService, transformer1, PhaseCode.A, 1),
            site1
        )
        createService(
            networkService,
            2,
            createTerminal(networkService, junction2, PhaseCode.A, 3),
            createTerminal(networkService, transformer2, PhaseCode.A, 1),
            site1
        )
        createService(
            networkService,
            3,
            createTerminal(networkService, junction4, PhaseCode.A, 3),
            createTerminal(networkService, transformer3, PhaseCode.A, 1),
            site3
        )
        createService(
            networkService,
            4,
            createTerminal(networkService, junction5, PhaseCode.A, 3),
            createTerminal(networkService, transformer4, PhaseCode.A, 1),
            site4
        )
        createService(
            networkService,
            5,
            createTerminal(networkService, junction6, PhaseCode.A, 3),
            createTerminal(networkService, transformer5, PhaseCode.A, 1),
            site5
        )
        createService(
            networkService,
            6,
            createTerminal(networkService, junction7, PhaseCode.A, 3),
            createTerminal(networkService, transformer6, PhaseCode.A, 1),
            site7
        )
        createService(
            networkService,
            7,
            createTerminal(networkService, junction8, PhaseCode.A, 3),
            createTerminal(networkService, transformer7, PhaseCode.A, 1),
            site7
        )
        createService(
            networkService,
            8,
            createTerminal(networkService, junction9, PhaseCode.A, 3),
            createTerminal(networkService, transformer8, PhaseCode.A, 1),
            site8
        )
        createService(
            networkService,
            9,
            createTerminal(networkService, junction10, PhaseCode.A, 3),
            createTerminal(networkService, transformer9, PhaseCode.A, 1),
            site8
        )

        val geographicalRegion1 = createGeographicalRegion(networkService, 1)
        val subGeographicalRegion1 = createSubGeographicalRegion(networkService, 1, geographicalRegion1)
        val substation1 = createSubstation(networkService, 1, subGeographicalRegion1)
        createSubstation(networkService, 2, subGeographicalRegion1)

        createFeeder(networkService, 1, feederStart1, substation1)
        createFeeder(networkService, 2, feederStart2, substation1)
        createFeeder(networkService, 3, feederStart3, substation1)

        val restriction1 = createOperationalRestriction(networkService, 1)
        breaker1.addOperationalRestriction(restriction1)
        restriction1.addEquipment(breaker1)

        val diagram = createDiagram(diagramService, "100")

        diagramService.add(createDiagramObject(diagram, faultIndicator1, "FAULT_INDICATOR", 1.0, 1.0))
        diagramService.add(createDiagramObject(diagram, disconnector2, "DISCONNECTOR", 0.0, 0.0))

        assertThat(diagramService.getDiagramObjects(disconnector1.mRID), equalTo(emptyList()))
        assertThat(diagramService.getDiagramObjects(disconnector2.mRID).first().style, equalTo("DISCONNECTOR"))
    }

    /*
     * The duplicated nmi (n1) results in a many to many mapping with the current mapping format (August 2017).
     * TX3 and TX4 allow testing of parallel transformers causing a one usage point to many TX mapping (appeared in mapping August 2017).
    *
    *           TX1         TX2               TX3    TX4
    *            8           8                 8      8
    *            |           |                 |      |
    *        ==========/====================/==========
    *        |                   |                 |
    *        + 1, A, n1          + 2, B, n1        + 7, G, --
    *        |                   |
    *        + 3, C, n2          + 4, D, n3
    *        |
    *        + 5, E, --
    *        |    F, --
    *        |
    *        + 6, --
    */
    private fun addLvSimplification(network: NetworkService, customerService: CustomerService) {
        val tx1: PowerTransformer = network["t1"]!!
        val tx2: PowerTransformer = network["t2"]!!
        val tx3: PowerTransformer = network["t3"]!!
        val tx4: PowerTransformer = network["t4"]!!

        val usagePoint1 = UsagePoint("1")
        val usagePoint2 = UsagePoint("2")
        val usagePoint3 = UsagePoint("3")
        val usagePoint4 = UsagePoint("4")
        val usagePoint5 = UsagePoint("5")
        val usagePoint6 = UsagePoint("6")
        val usagePoint7 = UsagePoint("7")

        val acme1 = createAssetOwner(network, customerService, "acme1")
        val acme2 = createAssetOwner(network, customerService, "acme2")
        val acme3 = createAssetOwner(network, customerService, "acme3")

        val tariff = Tariff().apply { name = "r" }
        val pricingStructure = PricingStructure().apply { addTariff(tariff) }
        val customer = Customer().apply { kind = CustomerKind.residential }
        CustomerAgreement().apply { this.customer = customer }.addPricingStructure(pricingStructure)

        val location = Location()
        val locationN1A1 = Location().apply {
            name = "n1";
            mainAddress = StreetAddress("a1")
        }
        val locationN1A2 = Location().apply {
            name = "n1";
            mainAddress = StreetAddress("a2")
        }
        val locationN2 = Location().apply {
            name = "n2";
            mainAddress = StreetAddress("")
        }
        val locationN3 = Location().apply {
            name = "n3";
            mainAddress = StreetAddress("")
        }

        val meter1: Meter = Meter("A").apply { name = "meter1"; addOrganisationRole(acme1); customerMRID = customer.mRID; serviceLocation = locationN1A1 }
        val meter2: Meter = Meter("B").apply { name = "meter2"; addOrganisationRole(acme1); customerMRID = customer.mRID; serviceLocation = locationN1A2 }
        val meter3: Meter = Meter("C").apply { name = "meter1"; addOrganisationRole(acme2); customerMRID = customer.mRID; serviceLocation = locationN2 }
        val meter4: Meter = Meter("D").apply { name = "meter2"; addOrganisationRole(acme2); customerMRID = customer.mRID; serviceLocation = locationN3 }
        val meter5: Meter = Meter("E").apply { name = "meter3"; addOrganisationRole(acme2); customerMRID = customer.mRID; serviceLocation = location }
        val meter6: Meter = Meter("F").apply { name = "meter4"; addOrganisationRole(acme2); customerMRID = customer.mRID; serviceLocation = location }
        val meter7: Meter = Meter("G").apply { name = "meter1"; addOrganisationRole(acme3); customerMRID = customer.mRID; serviceLocation = location }

        network.add(usagePoint1)
        network.add(usagePoint2)
        network.add(usagePoint3)
        network.add(usagePoint4)
        network.add(usagePoint5)
        network.add(usagePoint6)
        network.add(usagePoint7)

        network.add(meter1)
        network.add(meter2)
        network.add(meter3)
        network.add(meter4)
        network.add(meter5)
        network.add(meter6)
        network.add(meter7)

        network.add(location)
        network.add(locationN1A1)
        network.add(locationN1A2)
        network.add(locationN2)
        network.add(locationN3)

        //
        // NOTE: Due to the mapping issue described above, meter1 and meter2 are mapped to both tx1 and tx2
        //       to reflect the IRL data we receive, which is contrary to what the diagram indicates.
        //
        NetworkModelTestUtil.addLvSimplification(tx1, usagePoint1, meter1, meter2)
        NetworkModelTestUtil.addLvSimplification(tx1, usagePoint3, meter3)
        NetworkModelTestUtil.addLvSimplification(tx1, usagePoint5, meter5, meter6)
        NetworkModelTestUtil.addLvSimplification(tx1, usagePoint6)
        NetworkModelTestUtil.addLvSimplification(tx2, usagePoint2, meter1, meter2)
        NetworkModelTestUtil.addLvSimplification(tx2, usagePoint4, meter4)
        NetworkModelTestUtil.addLvSimplification(tx3, usagePoint7, meter7)
        NetworkModelTestUtil.addLvSimplification(tx4, usagePoint7, meter7)
    }

}

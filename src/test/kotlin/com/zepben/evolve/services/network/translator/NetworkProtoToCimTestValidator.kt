/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network.translator

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
import com.zepben.evolve.cim.iec61970.base.scada.RemoteSource
import com.zepben.evolve.cim.iec61970.base.wires.*
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.Circuit
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.Loop
import com.zepben.evolve.services.network.NetworkService
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import com.zepben.protobuf.cim.iec61968.assetinfo.PowerTransformerInfo as PBPowerTransformerInfo
import com.zepben.protobuf.cim.iec61968.assetinfo.TransformerEndInfo as PBTransformerEndInfo
import com.zepben.protobuf.cim.iec61968.assetinfo.TransformerTankInfo as PBTransformerTankInfo
import com.zepben.protobuf.cim.iec61968.assets.Asset as PBAsset
import com.zepben.protobuf.cim.iec61968.assets.AssetContainer as PBAssetContainer
import com.zepben.protobuf.cim.iec61968.assets.AssetInfo as PBAssetInfo
import com.zepben.protobuf.cim.iec61968.assets.Pole as PBPole
import com.zepben.protobuf.cim.iec61968.assets.Structure as PBStructure
import com.zepben.protobuf.cim.iec61968.metering.EndDevice as PBEndDevice
import com.zepben.protobuf.cim.iec61968.metering.Meter as PBMeter
import com.zepben.protobuf.cim.iec61970.base.core.ConductingEquipment as PBConductingEquipment
import com.zepben.protobuf.cim.iec61970.base.core.ConnectivityNodeContainer as PBConnectivityNodeContainer
import com.zepben.protobuf.cim.iec61970.base.core.Equipment as PBEquipment
import com.zepben.protobuf.cim.iec61970.base.core.EquipmentContainer as PBEquipmentContainer
import com.zepben.protobuf.cim.iec61970.base.core.IdentifiedObject as PBIdentifiedObject
import com.zepben.protobuf.cim.iec61970.base.core.PhaseCode as PBPhaseCode
import com.zepben.protobuf.cim.iec61970.base.core.PowerSystemResource as PBPowerSystemResource
import com.zepben.protobuf.cim.iec61970.base.core.Substation as PBSubstation
import com.zepben.protobuf.cim.iec61970.base.domain.UnitSymbol as PBUnitSymbol
import com.zepben.protobuf.cim.iec61970.base.meas.Accumulator as PBAccumulator
import com.zepben.protobuf.cim.iec61970.base.meas.Analog as PBAnalog
import com.zepben.protobuf.cim.iec61970.base.meas.Discrete as PBDiscrete
import com.zepben.protobuf.cim.iec61970.base.meas.Measurement as PBMeasurement
import com.zepben.protobuf.cim.iec61970.base.wires.BusbarSection as PBBusbarSection
import com.zepben.protobuf.cim.iec61970.base.wires.Connector as PBConnector
import com.zepben.protobuf.cim.iec61970.base.wires.Line as PBLine
import com.zepben.protobuf.cim.iec61970.base.wires.LoadBreakSwitch as PBLoadBreakSwitch
import com.zepben.protobuf.cim.iec61970.base.wires.PowerTransformer as PBPowerTransformer
import com.zepben.protobuf.cim.iec61970.base.wires.PowerTransformerEnd as PBPowerTransformerEnd
import com.zepben.protobuf.cim.iec61970.base.wires.ProtectedSwitch as PBProtectedSwitch
import com.zepben.protobuf.cim.iec61970.base.wires.Switch as PBSwitch
import com.zepben.protobuf.cim.iec61970.base.wires.TransformerEnd as PBTransformerEnd
import com.zepben.protobuf.cim.iec61970.base.wires.TransformerStarImpedance as PBTransformerStarImpedance
import com.zepben.protobuf.cim.iec61970.base.wires.VectorGroup as PBVectorGroup
import com.zepben.protobuf.cim.iec61970.base.wires.WindingConnection as PBWindingConnection
import com.zepben.protobuf.cim.iec61970.infiec61970.feeder.Circuit as PBCircuit
import com.zepben.protobuf.cim.iec61970.infiec61970.feeder.Loop as PBLoop

class NetworkProtoToCimTestValidator(val network: NetworkService) {

    inline fun <reified T : Asset> validate(pb: PBAsset.Builder, fromPb: () -> T): T {
        network.add(AssetOwner("role1"))
        network.add(AssetOwner("role2"))
        network.add(Location("assetLocation"))

        pb.addOrganisationRoleMRIDs("role1")
        pb.addOrganisationRoleMRIDs("role2")
        pb.locationMRID = "assetLocation"

        val cim = validate(pb.ioBuilder, fromPb)

        assertThat(cim.numOrganisationRoles(), equalTo(2))
        assertThat(cim.getOrganisationRole("role1"), equalTo(network["role1"]))
        assertThat(cim.getOrganisationRole("role2"), equalTo(network["role2"]))
        assertThat(cim.location, equalTo(network["assetLocation"]))
        return cim
    }

    inline fun <reified T : AssetContainer> validate(pb: PBAssetContainer.Builder, fromPb: () -> T): T =
        validate(pb.atBuilder, fromPb)

    inline fun <reified T : EndDevice> validate(pb: PBEndDevice.Builder, fromPb: () -> T): T {
        network.add(UsagePoint("up1"))
        network.add(UsagePoint("up2"))
        network.add(Location("customerServiceLocation"))

        pb.addUsagePointMRIDs("up1")
        pb.addUsagePointMRIDs("up2")
        pb.customerMRID = "customer"
        pb.serviceLocationMRID = "customerServiceLocation"

        val cim = validate(pb.acBuilder, fromPb)

        assertThat(cim.numUsagePoints(), equalTo(2))
        assertThat(cim.getUsagePoint("up1"), equalTo(network["up1"]))
        assertThat(cim.getUsagePoint("up2"), equalTo(network["up2"]))
        assertThat(cim.customerMRID, equalTo("customer"))
        assertThat(cim.serviceLocation, equalTo(network["customerServiceLocation"]))

        return cim
    }

    inline fun validate(pb: PBMeter.Builder, fromPb: () -> Meter): Meter = validate(pb.edBuilder, fromPb)

    inline fun <reified T : ConnectivityNodeContainer> validate(pb: PBConnectivityNodeContainer.Builder, fromPb: () -> T): T =
        validate(pb.psrBuilder, fromPb)

    inline fun <reified T : EquipmentContainer> validate(pb: PBEquipmentContainer.Builder, fromPb: () -> T): T {
        val cim = validate(pb.cncBuilder, fromPb)

        assertThat(cim.numEquipment(), equalTo(0))

        return cim
    }

    inline fun <reified T : IdentifiedObject> validate(pb: PBIdentifiedObject.Builder, fromPb: () -> T): T {
        pb.mrid = "mrid"
        pb.name = "name"
        pb.description = "description"
        pb.numDiagramObjects = 2

        val cim = fromPb()

        assertThat(cim.mRID, equalTo("mrid"))
        assertThat(cim.name, equalTo("name"))
        assertThat(cim.description, equalTo("description"))
        assertThat(cim.numDiagramObjects, equalTo(2))

        return cim
    }

    inline fun <reified T : PowerSystemResource> validate(pb: PBPowerSystemResource.Builder, fromPb: () -> T): T {
        network.add(Location("location1"))

        pb.locationMRID = "location1"
        pb.numControls = 1

        val cim = validate(pb.ioBuilder, fromPb)

        // We only check for blank asset info here, values will be checked in derived types.
        if (pb.assetInfoMRID.isBlank())
            assertThat(cim.assetInfo, nullValue())

        assertThat(cim.location, equalTo(network["location1"]))
        assertThat(cim.numControls, equalTo(1))

        return cim
    }

    inline fun <reified T : Equipment> validate(pb: PBEquipment.Builder, fromPb: () -> T): T {
        pb.inService = false
        pb.normallyInService = false

        network.add(Circuit("eqc1"))
        network.add(Substation("eqc2"))
        pb.addAllEquipmentContainerMRIDs(listOf("eqc1", "eqc2"))

        network.add(UsagePoint("up1"))
        network.add(UsagePoint("up2"))
        pb.addAllUsagePointMRIDs(listOf("up1", "up2"))

        network.add(OperationalRestriction("or1"))
        network.add(OperationalRestriction("or2"))
        pb.addAllOperationalRestrictionMRIDs(listOf("or1", "or2"))

        network.add(Feeder("cf1"))
        network.add(Feeder("cf2"))
        pb.addAllCurrentFeederMRIDs(listOf("cf1", "cf2"))

        val cim = validate(pb.psrBuilder, fromPb)

        assertThat(cim.inService, equalTo(false))
        assertThat(cim.normallyInService, equalTo(false))

        assertThat(cim.containers, hasSize(2))
        assertThat(cim.getContainer("eqc1"), equalTo(network["eqc1"]))
        assertThat(cim.getContainer("eqc2"), equalTo(network["eqc2"]))

        assertThat(cim.usagePoints, hasSize(2))
        assertThat(cim.getUsagePoint("up1"), equalTo(network["up1"]))
        assertThat(cim.getUsagePoint("up2"), equalTo(network["up2"]))

        assertThat(cim.operationalRestrictions, hasSize(2))
        assertThat(cim.getOperationalRestriction("or1"), equalTo(network["or1"]))
        assertThat(cim.getOperationalRestriction("or2"), equalTo(network["or2"]))

        assertThat(cim.currentFeeders, hasSize(2))
        assertThat(cim.getCurrentFeeder("cf1"), equalTo(network["cf1"]))
        assertThat(cim.getCurrentFeeder("cf2"), equalTo(network["cf2"]))

        return cim
    }

    inline fun <reified T : ConductingEquipment> validate(pb: PBConductingEquipment.Builder, fromPb: () -> T): T {
        network.add(BaseVoltage("bv1"))
        pb.baseVoltageMRID = "bv1"

        network.add(Terminal("t1"))
        network.add(Terminal("t2"))
        pb.addAllTerminalMRIDs(listOf("t1", "t2"))

        val cim = validate(pb.eqBuilder, fromPb)

        assertThat(cim.baseVoltage, equalTo(network["bv1"]))
        assertThat(cim.getTerminal("t1"), equalTo(network["t1"]))
        assertThat(cim.getTerminal("t2"), equalTo(network["t2"]))

        return cim
    }

    inline fun <reified T : Connector> validate(pb: PBConnector.Builder, fromPb: () -> T): T = validate(pb.ceBuilder, fromPb)

    inline fun validate(pb: PBBusbarSection.Builder, fromPb: () -> BusbarSection): BusbarSection = validate(pb.cnBuilder, fromPb)

    inline fun validate(pb: PBPowerTransformer.Builder, fromPb: () -> PowerTransformer): PowerTransformer {
        pb.vectorGroup = PBVectorGroup.D0
        pb.transformerUtilisation = 0.9

        network.add(PowerTransformerEnd("pte1"))
        network.add(PowerTransformerEnd("pte2"))
        pb.addAllPowerTransformerEndMRIDs(listOf("pte1", "pte2"))

        val cim = validate(pb.ceBuilder, fromPb)

        assertThat(cim.vectorGroup, equalTo(VectorGroup.D0))
        assertThat(cim.transformerUtilisation, equalTo(0.9))
        assertThat(cim.getEnd("pte1"), equalTo(network["pte1"]))
        assertThat(cim.getEnd("pte2"), equalTo(network["pte2"]))

        return cim
    }

    inline fun validate(pb: PBLoadBreakSwitch.Builder, fromPb: () -> LoadBreakSwitch): LoadBreakSwitch = validate(pb.psBuilder, fromPb)

    inline fun <reified T : ProtectedSwitch> validate(pb: PBProtectedSwitch.Builder, fromPb: () -> T): T = validate(pb.swBuilder, fromPb)

    inline fun <reified T : Switch> validate(pb: PBSwitch.Builder, fromPb: () -> T): T {
        pb.normalOpen = true
        pb.open = true

        val cim = validate(pb.ceBuilder, fromPb)

        assertThat(cim.isNormallyOpen(), equalTo(true))
        assertThat(cim.isOpen(), equalTo(true))

        return cim
    }

    inline fun validate(pb: PBSubstation.Builder, fromPb: () -> Substation): Substation {
        network.add(SubGeographicalRegion("sgr1"))
        network.add(Feeder("feeder1"))
        network.add(Feeder("feeder2"))
        network.add(Loop("loop1"))
        network.add(Loop("loop2"))
        network.add(Loop("energizedLoop1"))
        network.add(Loop("energizedLoop2"))
        network.add(Circuit("circuit1"))
        network.add(Circuit("circuit2"))

        pb.subGeographicalRegionMRID = "sgr1"
        pb.addNormalEnergizedFeederMRIDs("feeder1")
        pb.addNormalEnergizedFeederMRIDs("feeder2")
        pb.addLoopMRIDs("loop1")
        pb.addLoopMRIDs("loop2")
        pb.addNormalEnergizedLoopMRIDs("energizedLoop1")
        pb.addNormalEnergizedLoopMRIDs("energizedLoop2")
        pb.addCircuitMRIDs("circuit1")
        pb.addCircuitMRIDs("circuit2")

        val cim = validate(pb.ecBuilder, fromPb)

        assertThat(cim.subGeographicalRegion, equalTo(network["sgr1"]))
        assertThat(cim.numFeeders(), equalTo(2))
        assertThat(cim.getFeeder("feeder1"), equalTo(network["feeder1"]))
        assertThat(cim.getFeeder("feeder2"), equalTo(network["feeder2"]))
        assertThat(cim.numLoops(), equalTo(2))
        assertThat(cim.getLoop("loop1"), equalTo(network["loop1"]))
        assertThat(cim.getLoop("loop2"), equalTo(network["loop2"]))
        assertThat(cim.numEnergizedLoops(), equalTo(2))
        assertThat(cim.getEnergizedLoop("energizedLoop1"), equalTo(network["energizedLoop1"]))
        assertThat(cim.getEnergizedLoop("energizedLoop2"), equalTo(network["energizedLoop2"]))
        assertThat(cim.numCircuits(), equalTo(2))
        assertThat(cim.getCircuit("circuit1"), equalTo(network["circuit1"]))
        assertThat(cim.getCircuit("circuit2"), equalTo(network["circuit2"]))

        return cim
    }

    inline fun <reified T : Line> validate(pb: PBLine.Builder, fromPb: () -> T): T =
        validate(pb.ecBuilder, fromPb)

    inline fun validate(pb: PBCircuit.Builder, fromPb: () -> Circuit): Circuit {
        network.add(Loop("loop1"))
        network.add(Terminal("terminal1"))
        network.add(Terminal("terminal2"))
        network.add(Substation("substation1"))
        network.add(Substation("substation2"))

        pb.loopMRID = "loop1"
        pb.addEndTerminalMRIDs("terminal1")
        pb.addEndTerminalMRIDs("terminal2")
        pb.addEndSubstationMRIDs("substation1")
        pb.addEndSubstationMRIDs("substation2")

        val cim = validate(pb.lBuilder, fromPb)

        assertThat(cim.loop, equalTo(network["loop1"]))
        assertThat(cim.numEndTerminals(), equalTo(2))
        assertThat(cim.getEndTerminal("terminal1"), equalTo(network["terminal1"]))
        assertThat(cim.getEndTerminal("terminal2"), equalTo(network["terminal2"]))
        assertThat(cim.numEndSubstations(), equalTo(2))
        assertThat(cim.getEndSubstation("substation1"), equalTo(network["substation1"]))
        assertThat(cim.getEndSubstation("substation2"), equalTo(network["substation2"]))

        return cim
    }

    inline fun validate(pb: PBLoop.Builder, fromPb: () -> Loop): Loop {
        network.add(Circuit("circuit1"))
        network.add(Circuit("circuit2"))
        network.add(Substation("substation1"))
        network.add(Substation("substation2"))
        network.add(Substation("energizingSub1"))
        network.add(Substation("energizingSub2"))

        pb.addCircuitMRIDs("circuit1")
        pb.addCircuitMRIDs("circuit2")
        pb.addSubstationMRIDs("substation1")
        pb.addSubstationMRIDs("substation2")
        pb.addNormalEnergizingSubstationMRIDs("energizingSub1")
        pb.addNormalEnergizingSubstationMRIDs("energizingSub2")

        val cim = validate(pb.ioBuilder, fromPb)

        assertThat(cim.numCircuits(), equalTo(2))
        assertThat(cim.getCircuit("circuit1"), equalTo(network["circuit1"]))
        assertThat(cim.getCircuit("circuit2"), equalTo(network["circuit2"]))
        assertThat(cim.numSubstations(), equalTo(2))
        assertThat(cim.getSubstation("substation1"), equalTo(network["substation1"]))
        assertThat(cim.getSubstation("substation2"), equalTo(network["substation2"]))
        assertThat(cim.numEnergizingSubstations(), equalTo(2))
        assertThat(cim.getEnergizingSubstation("energizingSub1"), equalTo(network["energizingSub1"]))
        assertThat(cim.getEnergizingSubstation("energizingSub2"), equalTo(network["energizingSub2"]))

        return cim
    }

    inline fun validate(pb: PBPole.Builder, fromPb: () -> Pole): Pole {
        network.add(Streetlight("streetlight1"))
        network.add(Streetlight("streetlight2"))

        pb.classification = "classification"
        pb.addAllStreetlightMRIDs(listOf("streetlight1", "streetlight2"))

        val cim = validate(pb.stBuilder, fromPb)

        assertThat(cim.classification, equalTo("classification"))
        assertThat(cim.numStreetlights(), equalTo(2))
        assertThat(cim.getStreetlight("streetlight1"), equalTo(network["streetlight1"]))
        assertThat(cim.getStreetlight("streetlight2"), equalTo(network["streetlight2"]))

        return cim
    }

    inline fun <reified T : Measurement> validate(pb: PBMeasurement.Builder, fromPb: () -> T): T {
        pb.terminalMRID = "terminal1"
        pb.powerSystemResourceMRID = "psr1"
        pb.remoteSourceMRID = "rs1"
        pb.phases = PBPhaseCode.ABC
        pb.unitSymbol = PBUnitSymbol.N


        val cim = validate(pb.ioBuilder, fromPb)

        val rs = RemoteSource("rs1")
        network.add(rs)

        assertThat(cim.terminalMRID, equalTo("terminal1"))
        assertThat(cim.powerSystemResourceMRID, equalTo("psr1"))

        assertThat(cim.remoteSource, equalTo(rs))
        assertThat(cim.phases, equalTo(PhaseCode.ABC))
        assertThat(cim.unitSymbol, equalTo(UnitSymbol.N))

        return cim
    }

    inline fun validate(pb: PBAnalog.Builder, fromPb: () -> Analog): Analog {
        pb.positiveFlowIn = true
        val cim = validate(pb.measurementBuilder, fromPb)
        assertThat(cim.positiveFlowIn, equalTo(true))

        return cim
    }

    inline fun validate(pb: PBAccumulator.Builder, fromPb: () -> Accumulator): Accumulator = validate(pb.measurementBuilder, fromPb)

    inline fun validate(pb: PBDiscrete.Builder, fromPb: () -> Discrete): Discrete = validate(pb.measurementBuilder.ioBuilder, fromPb)

    inline fun <reified T : Structure> validate(pb: PBStructure.Builder, fromPb: () -> T): T = validate(pb.acBuilder, fromPb)

    inline fun <reified T : AssetInfo> validate(pb: PBAssetInfo.Builder, fromPb: () -> T): T = validate(pb.ioBuilder, fromPb)

    inline fun validate(pb: PBPowerTransformerInfo.Builder, fromPb: () -> PowerTransformerInfo): PowerTransformerInfo {
        network.add(TransformerTankInfo("info1"))
        network.add(TransformerTankInfo("info2"))

        pb.addTransformerTankInfoMRIDs("info1")
        pb.addTransformerTankInfoMRIDs("info2")

        val cim = validate(pb.aiBuilder, fromPb)

        assertThat(cim.transformerTankInfos, containsInAnyOrder(*getObjects<TransformerTankInfo>("info1", "info2")))

        return cim
    }

    inline fun validate(pb: PBTransformerEndInfo.Builder, fromPb: () -> TransformerEndInfo): TransformerEndInfo {
        network.add(TransformerTankInfo("info1"))
        network.add(TransformerStarImpedance("star1"))

        pb.connectionKind = PBWindingConnection.D
        pb.emergencyS = 1
        pb.endNumber = 2
        pb.insulationU = 3
        pb.phaseAngleClock = 4
        pb.r = 5.0
        pb.ratedS = 6
        pb.ratedU = 7
        pb.shortTermS = 8

        pb.transformerTankInfoMRID = "info1"
        pb.transformerStarImpedanceMRID = "star1"

        val cim = validate(pb.aiBuilder, fromPb)

        assertThat(cim.connectionKind, equalTo(WindingConnection.D))
        assertThat(cim.emergencyS, equalTo(1))
        assertThat(cim.endNumber, equalTo(2))
        assertThat(cim.insulationU, equalTo(3))
        assertThat(cim.phaseAngleClock, equalTo(4))
        assertThat(cim.r, equalTo(5.0))
        assertThat(cim.ratedS, equalTo(6))
        assertThat(cim.ratedU, equalTo(7))
        assertThat(cim.shortTermS, equalTo(8))

        assertThat(cim.transformerTankInfo, equalTo(network["info1"]))
        assertThat(cim.transformerStarImpedance, equalTo(network["star1"]))

        return cim
    }

    inline fun validate(pb: PBTransformerTankInfo.Builder, fromPb: () -> TransformerTankInfo): TransformerTankInfo {
        network.add(TransformerEndInfo("info1"))
        network.add(TransformerEndInfo("info2"))

        pb.addTransformerEndInfoMRIDs("info1")
        pb.addTransformerEndInfoMRIDs("info2")

        val cim = validate(pb.aiBuilder, fromPb)

        assertThat(cim.transformerEndInfos, containsInAnyOrder(*getObjects<TransformerEndInfo>("info1", "info2")))

        return cim
    }

    inline fun validate(pb: PBPowerTransformerEnd.Builder, fromPb: () -> PowerTransformerEnd): PowerTransformerEnd {
        network.add(PowerTransformer("tx1"))

        pb.powerTransformerMRID = "tx1"
        pb.b = 1.0
        pb.b0 = 2.0
        pb.connectionKind = PBWindingConnection.UNKNOWN_WINDING
        pb.g = 3.0
        pb.g0 = 4.0
        pb.phaseAngleClock = 5
        pb.r = 6.0
        pb.r0 = 7.0
        pb.ratedS = 8
        pb.ratedU = 9
        pb.x = 10.0
        pb.x0 = 11.0

        val cim = validate(pb.teBuilder, fromPb)

        assertThat(cim.powerTransformer, equalTo(network["tx1"]))
        assertThat(cim.b, equalTo(1.0))
        assertThat(cim.b0, equalTo(2.0))
        assertThat(cim.connectionKind, equalTo(WindingConnection.UNKNOWN_WINDING))
        assertThat(cim.g, equalTo(3.0))
        assertThat(cim.g0, equalTo(4.0))
        assertThat(cim.phaseAngleClock, equalTo(5))
        assertThat(cim.r, equalTo(6.0))
        assertThat(cim.r0, equalTo(7.0))
        assertThat(cim.ratedS, equalTo(8))
        assertThat(cim.ratedU, equalTo(9))
        assertThat(cim.x, equalTo(10.0))
        assertThat(cim.x0, equalTo(11.0))

        return cim
    }

    inline fun <reified T : TransformerEnd> validate(pb: PBTransformerEnd.Builder, fromPb: () -> T): T {
        network.add(BaseVoltage("bv1"))
        network.add(RatioTapChanger("rtc1"))
        network.add(Terminal("term1"))
        network.add(TransformerStarImpedance("star1"))

        pb.grounded = true
        pb.rGround = 1.0
        pb.xGround = 2.0
        pb.baseVoltageMRID = "bv1"
        pb.ratioTapChangerMRID = "rtc1"
        pb.terminalMRID = "term1"
        pb.endNumber = 0
        pb.starImpedanceMRID = "star1"

        val cim = validate(pb.ioBuilder, fromPb)

        assertThat(cim.grounded, equalTo(true))
        assertThat(cim.rGround, equalTo(1.0))
        assertThat(cim.xGround, equalTo(2.0))
        assertThat(cim.baseVoltage, equalTo(network["bv1"]))
        assertThat(cim.ratioTapChanger, equalTo(network["rtc1"]))
        assertThat(cim.terminal, equalTo(network["term1"]))
        assertThat(cim.endNumber, equalTo(0))
        assertThat(cim.starImpedance, equalTo(network["star1"]))

        return cim
    }

    inline fun validate(pb: PBTransformerStarImpedance.Builder, fromPb: () -> TransformerStarImpedance): TransformerStarImpedance {
        network.add(TransformerEndInfo("info1"))

        pb.r = 1.0
        pb.r0 = 2.0
        pb.x = 3.0
        pb.x0 = 4.0
        pb.transformerEndInfoMRID = "info1"

        val cim = validate(pb.ioBuilder, fromPb)

        assertThat(cim.r, equalTo(1.0))
        assertThat(cim.r0, equalTo(2.0))
        assertThat(cim.x, equalTo(3.0))
        assertThat(cim.x0, equalTo(4.0))
        assertThat(cim.transformerEndInfo, equalTo(network["info1"]))

        return cim
    }

    inline fun <reified T : IdentifiedObject> getObjects(vararg mRIDs: String) = listOf(*mRIDs).map<String, T?> { network[it] }.toTypedArray()

}

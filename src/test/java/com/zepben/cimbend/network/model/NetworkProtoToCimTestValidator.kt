/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.network.model

import com.zepben.cimbend.cim.iec61968.assets.*
import com.zepben.cimbend.cim.iec61968.common.Location
import com.zepben.cimbend.cim.iec61968.metering.EndDevice
import com.zepben.cimbend.cim.iec61968.metering.Meter
import com.zepben.cimbend.cim.iec61968.metering.UsagePoint
import com.zepben.cimbend.cim.iec61968.operations.OperationalRestriction
import com.zepben.cimbend.cim.iec61970.base.core.*
import com.zepben.cimbend.cim.iec61970.base.domain.UnitSymbol
import com.zepben.cimbend.cim.iec61970.base.meas.Accumulator
import com.zepben.cimbend.cim.iec61970.base.meas.Analog
import com.zepben.cimbend.cim.iec61970.base.meas.Discrete
import com.zepben.cimbend.cim.iec61970.base.meas.Measurement
import com.zepben.cimbend.cim.iec61970.base.scada.RemoteSource
import com.zepben.cimbend.cim.iec61970.base.wires.Breaker
import com.zepben.cimbend.cim.iec61970.base.wires.Line
import com.zepben.cimbend.cim.iec61970.base.wires.PowerTransformer
import com.zepben.cimbend.cim.iec61970.base.wires.PowerTransformerEnd
import com.zepben.cimbend.cim.iec61970.infiec61970.feeder.Circuit
import com.zepben.cimbend.cim.iec61970.infiec61970.feeder.Loop
import com.zepben.cimbend.network.NetworkService
import com.zepben.protobuf.cim.iec61970.base.wires.VectorGroup
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import com.zepben.protobuf.cim.iec61968.assets.Asset.Builder as PBAssetBuilder
import com.zepben.protobuf.cim.iec61968.assets.AssetContainer.Builder as PBAssetContainerBuilder
import com.zepben.protobuf.cim.iec61968.assets.Pole as PBPole
import com.zepben.protobuf.cim.iec61968.assets.Structure as PBStructure
import com.zepben.protobuf.cim.iec61968.metering.EndDevice.Builder as PBEndDeviceBuilder
import com.zepben.protobuf.cim.iec61968.metering.Meter.Builder as PBMeterBuilder
import com.zepben.protobuf.cim.iec61970.base.core.ConductingEquipment.Builder as PBConductingEquipmentBuilder
import com.zepben.protobuf.cim.iec61970.base.core.ConnectivityNodeContainer.Builder as PBConnectivityNodeContainerBuilder
import com.zepben.protobuf.cim.iec61970.base.core.Equipment.Builder as PBEquipmentBuilder
import com.zepben.protobuf.cim.iec61970.base.core.EquipmentContainer.Builder as PBEquipmentContainerBuilder
import com.zepben.protobuf.cim.iec61970.base.core.IdentifiedObject.Builder as PBIdentifiedObjectBuilder
import com.zepben.protobuf.cim.iec61970.base.core.PhaseCode as PBPhaseCode
import com.zepben.protobuf.cim.iec61970.base.core.PowerSystemResource.Builder as PBPowerSystemResourceBuilder
import com.zepben.protobuf.cim.iec61970.base.core.Substation.Builder as PBSubstationBuilder
import com.zepben.protobuf.cim.iec61970.base.domain.UnitSymbol as PBUnitSymbol
import com.zepben.protobuf.cim.iec61970.base.meas.Accumulator as PBAccumulator
import com.zepben.protobuf.cim.iec61970.base.meas.Analog as PBAnalog
import com.zepben.protobuf.cim.iec61970.base.meas.Discrete as PBDiscrete
import com.zepben.protobuf.cim.iec61970.base.meas.Measurement as PBMeasurement
import com.zepben.protobuf.cim.iec61970.base.wires.Line.Builder as PBLineBuilder
import com.zepben.protobuf.cim.iec61970.base.wires.PowerTransformer.Builder as PBPowerTransformerBuilder
import com.zepben.protobuf.cim.iec61970.infiec61970.feeder.Circuit.Builder as PBCircuitBuilder
import com.zepben.protobuf.cim.iec61970.infiec61970.feeder.Loop.Builder as PBLoopBuilder

class NetworkProtoToCimTestValidator(val network: NetworkService) {

    inline fun <reified T : Asset> validate(pb: PBAssetBuilder, fromPb: () -> T): T {
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

    inline fun <reified T : AssetContainer> validate(pb: PBAssetContainerBuilder, fromPb: () -> T): T =
        validate(pb.atBuilder, fromPb)

    inline fun <reified T : EndDevice> validate(pb: PBEndDeviceBuilder, fromPb: () -> T): T {
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

    inline fun validate(pb: PBMeterBuilder, fromPb: () -> Meter): Meter = validate(pb.edBuilder, fromPb)

    inline fun <reified T : ConnectivityNodeContainer> validate(pb: PBConnectivityNodeContainerBuilder, fromPb: () -> T): T =
        validate(pb.psrBuilder, fromPb)

    inline fun <reified T : EquipmentContainer> validate(pb: PBEquipmentContainerBuilder, fromPb: () -> T): T {
        network.add(Breaker("breaker1"))
        network.add(Breaker("breaker2"))

        pb.addEquipmentMRIDs("breaker1")
        pb.addEquipmentMRIDs("breaker2")

        val cim = validate(pb.cncBuilder, fromPb)

        assertThat(cim.numEquipment(), equalTo(2))
        assertThat(cim.getEquipment("breaker1"), equalTo(network["breaker1"]))
        assertThat(cim.getEquipment("breaker2"), equalTo(network["breaker2"]))

        return cim
    }

    inline fun <reified T : IdentifiedObject> validate(pb: PBIdentifiedObjectBuilder, fromPb: () -> T): T {
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

    inline fun <reified T : PowerSystemResource> validate(pb: PBPowerSystemResourceBuilder, fromPb: () -> T): T {
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

    inline fun <reified T : Equipment> validate(pb: PBEquipmentBuilder, fromPb: () -> T): T {
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

    inline fun <reified T : ConductingEquipment> validate(pb: PBConductingEquipmentBuilder, fromPb: () -> T): T {
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

    inline fun validate(pb: PBPowerTransformerBuilder, fromPb: () -> PowerTransformer): PowerTransformer {
        pb.vectorGroup = VectorGroup.D0
        pb.transformerUtilisation = 0.9

        network.add(PowerTransformerEnd("pte1"))
        network.add(PowerTransformerEnd("pte2"))
        pb.addAllPowerTransformerEndMRIDs(listOf("pte1", "pte2"))

        val cim = validate(pb.ceBuilder, fromPb)

        assertThat(cim.vectorGroup.name, equalTo(VectorGroup.D0.name))
        assertThat(cim.transformerUtilisation, equalTo(0.9))
        assertThat(cim.getEnd("pte1"), equalTo(network["pte1"]))
        assertThat(cim.getEnd("pte2"), equalTo(network["pte2"]))

        return cim
    }

    inline fun validate(pb: PBSubstationBuilder, fromPb: () -> Substation): Substation {
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

    inline fun <reified T : Line> validate(pb: PBLineBuilder, fromPb: () -> T): T =
        validate(pb.ecBuilder, fromPb)

    inline fun validate(pb: PBCircuitBuilder, fromPb: () -> Circuit): Circuit {
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

    inline fun validate(pb: PBLoopBuilder, fromPb: () -> Loop): Loop {
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

    inline fun <reified T: Measurement> validate(pb: PBMeasurement.Builder, fromPb: () -> T): T {
        pb.terminalMRID = "terminal1"
        pb.powerSystemResourceMRID = "psr1"
        pb.remoteSourceMRID = "rs1"
        pb.phases = PBPhaseCode.ABC
        pb.unitSymbol = PBUnitSymbol.N


        val cim = validate(pb.ioBuilder, fromPb)

        val rs = RemoteSource("rs1")
        network.add(rs)

        assertThat(cim.terminalMRID, `is`("terminal1"))
        assertThat(cim.powerSystemResourceMRID, `is`("psr1"))

        assertThat(cim.remoteSource, equalTo(rs))
        assertThat(cim.phases, `is`(PhaseCode.ABC))
        assertThat(cim.unitSymbol, `is`(UnitSymbol.N))

        return cim
    }

    inline fun validate(pb: PBAnalog.Builder, fromPb: () -> Analog): Analog {
        pb.positiveFlowIn = true
        val cim = validate(pb.measurementBuilder, fromPb)
        assertThat(cim.positiveFlowIn, `is`(true))

        return cim
    }

    inline fun validate(pb: PBAccumulator.Builder, fromPb: () -> Accumulator): Accumulator = validate(pb.measurementBuilder, fromPb)

    inline fun validate(pb: PBDiscrete.Builder, fromPb: () -> Discrete): Discrete = validate(pb.measurementBuilder.ioBuilder, fromPb)

    inline fun <reified T : Structure> validate(pb: PBStructure.Builder, fromPb: () -> T): T = validate(pb.acBuilder, fromPb)
}

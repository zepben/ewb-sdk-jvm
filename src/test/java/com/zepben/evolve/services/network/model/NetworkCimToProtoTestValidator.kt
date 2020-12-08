/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network.model

import com.google.protobuf.ProtocolStringList
import com.google.protobuf.Timestamp
import com.zepben.evolve.cim.iec61968.assets.Asset
import com.zepben.evolve.cim.iec61968.assets.AssetContainer
import com.zepben.evolve.cim.iec61968.assets.Pole
import com.zepben.evolve.cim.iec61968.assets.Structure
import com.zepben.evolve.cim.iec61968.metering.EndDevice
import com.zepben.evolve.cim.iec61968.metering.Meter
import com.zepben.evolve.cim.iec61970.base.core.*
import com.zepben.evolve.cim.iec61970.base.meas.*
import com.zepben.evolve.cim.iec61970.base.wires.*
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.Circuit
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.Loop
import com.zepben.evolve.services.common.translator.toTimestamp
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import com.zepben.protobuf.cim.iec61968.assets.Asset as PBAsset
import com.zepben.protobuf.cim.iec61968.assets.AssetContainer as PBAssetContainer
import com.zepben.protobuf.cim.iec61968.assets.Pole as PBPole
import com.zepben.protobuf.cim.iec61968.assets.Structure as PBStructure
import com.zepben.protobuf.cim.iec61968.metering.EndDevice as PBEndDevive
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
import com.zepben.protobuf.cim.iec61970.base.meas.AccumulatorValue as PBAccumulatorValue
import com.zepben.protobuf.cim.iec61970.base.meas.Analog as PBAnalog
import com.zepben.protobuf.cim.iec61970.base.meas.AnalogValue as PBAnalogValue
import com.zepben.protobuf.cim.iec61970.base.meas.Discrete as PBDiscrete
import com.zepben.protobuf.cim.iec61970.base.meas.DiscreteValue as PBDiscreteValue
import com.zepben.protobuf.cim.iec61970.base.meas.Measurement as PBMeasurement
import com.zepben.protobuf.cim.iec61970.base.meas.MeasurementValue as PBMeasurementValue
import com.zepben.protobuf.cim.iec61970.base.wires.Line as PBLine
import com.zepben.protobuf.cim.iec61970.base.wires.PowerTransformer as PBPowerTransformer
import com.zepben.protobuf.cim.iec61970.base.wires.PowerTransformerEnd as PBPowerTransformerEnd
import com.zepben.protobuf.cim.iec61970.base.wires.TransformerEnd as PBTransformerEnd
import com.zepben.protobuf.cim.iec61970.infiec61970.feeder.Circuit as PBCircuit
import com.zepben.protobuf.cim.iec61970.infiec61970.feeder.Loop as PBLoop

internal class NetworkCimToProtoTestValidator {

    fun validate(cim: ConnectivityNodeContainer, pb: PBConnectivityNodeContainer) {
        validate(cim, pb.psr)
    }

    fun validate(cim: EquipmentContainer, pb: PBEquipmentContainer) {
        validate(cim, pb.cnc)

        validateMRIDList(cim.equipment, pb.equipmentMRIDsList)
    }

    fun validate(cim: IdentifiedObject, pb: PBIdentifiedObject) {
        assertThat(pb.mrid, equalTo(cim.mRID))
        assertThat(pb.name, equalTo(cim.name))
        assertThat(pb.description, equalTo(cim.description))
        assertThat(pb.numDiagramObjects, equalTo(cim.numDiagramObjects))
    }

    fun validate(cim: PowerSystemResource, pb: PBPowerSystemResource) {
        validate(cim, pb.io)

        validateMRID(cim.assetInfo, pb.assetInfoMRID)
        validateMRID(cim.location, pb.locationMRID)
        assertThat(pb.numControls, equalTo(cim.numControls))
    }

    fun validate(cim: Equipment, pb: PBEquipment) {
        validate(cim as PowerSystemResource, pb.psr)

        assertThat(pb.inService, equalTo(cim.inService))
        assertThat(pb.normallyInService, equalTo(cim.normallyInService))
        validateMRIDList(cim.containers, pb.equipmentContainerMRIDsList)
        validateMRIDList(cim.usagePoints, pb.usagePointMRIDsList)
        validateMRIDList(cim.operationalRestrictions, pb.operationalRestrictionMRIDsList)
        validateMRIDList(cim.currentFeeders, pb.currentFeederMRIDsList)
    }

    fun validate(cim: ConductingEquipment, pb: PBConductingEquipment) {
        validate(cim, pb.eq)

        validateMRID(cim.baseVoltage, pb.baseVoltageMRID)
        validateMRIDList(cim.terminals, pb.terminalMRIDsList)
    }

    fun validate(cim: PowerTransformer, pb: PBPowerTransformer) {
        validate(cim, pb.ce)

        validateMRIDList(cim.ends, pb.powerTransformerEndMRIDsList)
        assertThat(VectorGroup.valueOf(pb.vectorGroup.name), equalTo(cim.vectorGroup))
        assertThat(pb.transformerUtilisation, equalTo(cim.transformerUtilisation))
    }

    fun validate(cim: TransformerEnd, pb: PBTransformerEnd) {
        validate(cim, pb.io)

        validateMRID(cim.baseVoltage, pb.baseVoltageMRID)
        validateMRID(cim.ratioTapChanger, pb.ratioTapChangerMRID)
        validateMRID(cim.terminal, pb.terminalMRID)
        assertThat(cim.grounded, equalTo(pb.grounded))
        assertThat(cim.rGround, equalTo(pb.rGround))
        assertThat(cim.xGround, equalTo(pb.xGround))
        assertThat(cim.endNumber, equalTo(pb.endNumber))
    }

    fun validate(cim: PowerTransformerEnd, pb: PBPowerTransformerEnd) {
        validate(cim, pb.te)

        assertThat(cim.b, equalTo(pb.b))
        assertThat(cim.g, equalTo(pb.g))
        assertThat(cim.b0, equalTo(pb.b0))
        assertThat(cim.g0, equalTo(pb.g0))
        assertThat(cim.r, equalTo(pb.r))
        assertThat(cim.r0, equalTo(pb.r0))
        assertThat(cim.x, equalTo(pb.x))
        assertThat(cim.x0, equalTo(pb.x0))
        assertThat(WindingConnection.valueOf(pb.connectionKind.name), equalTo(cim.connectionKind))
        assertThat(cim.ratedS, equalTo(pb.ratedS))
        assertThat(cim.ratedU, equalTo(pb.ratedU))
        assertThat(cim.phaseAngleClock, equalTo(pb.phaseAngleClock))
    }

    fun validate(cim: Substation, pb: PBSubstation) {
        validate(cim, pb.ec)

        validateMRID(cim.subGeographicalRegion, pb.subGeographicalRegionMRID)
        validateMRIDList(cim.feeders, pb.normalEnergizedFeederMRIDsList)
        validateMRIDList(cim.loops, pb.loopMRIDsList)
        validateMRIDList(cim.energizedLoops, pb.normalEnergizedLoopMRIDsList)
        validateMRIDList(cim.circuits, pb.circuitMRIDsList)
    }

    fun validate(cim: Line, pb: PBLine) {
        validate(cim, pb.ec)
    }

    fun validate(cim: Circuit, pb: PBCircuit) {
        validate(cim, pb.l)

        validateMRID(cim.loop, pb.loopMRID)
        validateMRIDList(cim.endTerminals, pb.endTerminalMRIDsList)
        validateMRIDList(cim.endSubstations, pb.endSubstationMRIDsList)
    }

    fun validate(cim: Loop, pb: PBLoop) {
        validate(cim, pb.io)

        validateMRIDList(cim.circuits, pb.circuitMRIDsList)
        validateMRIDList(cim.substations, pb.substationMRIDsList)
        validateMRIDList(cim.energizingSubstations, pb.normalEnergizingSubstationMRIDsList)
    }

    fun validate(cim: Meter, pb: PBMeter) {
        validate(cim, pb.ed)
    }

    fun validate(cim: EndDevice, pb: PBEndDevive) {
        validate(cim, pb.ac)

        validateMRIDList(cim.usagePoints, pb.usagePointMRIDsList)
        validateMRID(cim.customerMRID, pb.customerMRID)
        validateMRID(cim.serviceLocation, pb.serviceLocationMRID)
    }

    fun validate(cim: AssetContainer, pb: PBAssetContainer) {
        validate(cim, pb.at)
    }

    fun validate(cim: Asset, pb: PBAsset) {
        validate(cim, pb.io)

        validateMRIDList(cim.organisationRoles, pb.organisationRoleMRIDsList)
        validateMRID(cim.location, pb.locationMRID)
    }

    fun validate(cim: Pole, pb: PBPole) {
        validate(cim, pb.st)

        assertThat(cim.classification, equalTo(pb.classification))
        validateMRIDList(cim.streetlights, pb.streetlightMRIDsList)
    }

    private fun validate(cim: Structure, pb: PBStructure) {
        validate(cim, pb.ac)
    }

    private fun validateMRID(mrid: String?, pb: String) {
        mrid?.let { assertThat(pb, equalTo(it)) } ?: assertThat(pb, emptyString())
    }

    private fun validateMRID(cim: IdentifiedObject?, pb: String) {
        validateMRID(cim?.mRID, pb)
    }

    private fun validateMRIDList(cim: Collection<IdentifiedObject>, pb: ProtocolStringList) {
        assertThat(pb.size, equalTo(cim.size))
        if (cim.isNotEmpty())
            assertThat(pb, containsInAnyOrder(*cim.stream().map { it.mRID }.toArray()))
    }

    private fun validateMRIDList(cim: List<IdentifiedObject>, pb: ProtocolStringList) {
        assertThat(pb.size, equalTo(cim.size))
        if (cim.isNotEmpty())
            assertThat(pb, contains(*cim.stream().map { it.mRID }.toArray()))
    }

    private fun validate(cim: PhaseCode, pb: PBPhaseCode) {
        assertThat(PBPhaseCode.valueOf(cim.name), `is`(pb))
    }

    fun validate(cim: Measurement, pb: PBMeasurement) {
        validateMRID(cim.terminalMRID, pb.terminalMRID)
        validateMRID(cim.powerSystemResourceMRID, pb.powerSystemResourceMRID)
        validateMRID(cim.remoteSource?.mRID, pb.remoteSourceMRID)
        validate(cim.phases, pb.phases)
        assertThat(PBUnitSymbol.valueOf(cim.unitSymbol.name), `is`(pb.unitSymbol))
        validate(cim, pb.io)
    }

    fun validate(cim: Analog, pb: PBAnalog) {
        validate(cim as Measurement, pb.measurement)
        assertThat(cim.positiveFlowIn, `is`(pb.positiveFlowIn))
    }

    fun validate(cim: Accumulator, pb: PBAccumulator) {
        validate(cim as Measurement, pb.measurement)
    }

    fun validate(cim: Discrete, pb: PBDiscrete) {
        validate(cim as Measurement, pb.measurement)
    }

    fun validate(cim: MeasurementValue, pb: PBMeasurementValue) {
        cim.timeStamp?.let { assertThat(pb.timeStamp, equalTo(it.toTimestamp())) } ?: assertThat(pb.timeStamp, equalTo(Timestamp.getDefaultInstance()))
    }

    fun validate(cim: AnalogValue, pb: PBAnalogValue) {
        validateMRID(cim.analogMRID, pb.analogMRID)
        assertThat(cim.value, `is`(pb.value))
        validate(cim as MeasurementValue, pb.mv)
    }

    fun validate(cim: AccumulatorValue, pb: PBAccumulatorValue) {
        validateMRID(cim.accumulatorMRID, pb.accumulatorMRID)
        assertThat(cim.value.toInt(), `is`(pb.value))
        validate(cim as MeasurementValue, pb.mv)
    }

    fun validate(cim: DiscreteValue, pb: PBDiscreteValue) {
        validateMRID(cim.discreteMRID, pb.discreteMRID)
        assertThat(cim.value, `is`(pb.value))
        validate(cim as MeasurementValue, pb.mv)
    }
}

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
import com.zepben.evolve.cim.iec61968.metering.EndDevice
import com.zepben.evolve.cim.iec61968.metering.Meter
import com.zepben.evolve.cim.iec61970.base.core.*
import com.zepben.evolve.cim.iec61970.base.domain.UnitSymbol
import com.zepben.evolve.cim.iec61970.base.meas.Accumulator
import com.zepben.evolve.cim.iec61970.base.meas.Analog
import com.zepben.evolve.cim.iec61970.base.meas.Discrete
import com.zepben.evolve.cim.iec61970.base.meas.Measurement
import com.zepben.evolve.cim.iec61970.base.wires.*
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.Circuit
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.Loop
import com.zepben.evolve.services.common.UNKNOWN_DOUBLE
import com.zepben.evolve.utils.validateMRID
import com.zepben.evolve.utils.validateMRIDList
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
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
import com.zepben.protobuf.cim.iec61970.base.core.PowerSystemResource as PBPowerSystemResource
import com.zepben.protobuf.cim.iec61970.base.core.Substation as PBSubstation
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
import com.zepben.protobuf.cim.iec61970.infiec61970.feeder.Circuit as PBCircuit
import com.zepben.protobuf.cim.iec61970.infiec61970.feeder.Loop as PBLoop

internal class NetworkCimToProtoTestValidator {

    fun validate(cim: ConnectivityNodeContainer, pb: PBConnectivityNodeContainer) {
        validate(cim, pb.psr)
    }

    fun validate(cim: EquipmentContainer, pb: PBEquipmentContainer) {
        validate(cim, pb.cnc)
    }

    fun validate(cim: IdentifiedObject, pb: PBIdentifiedObject) {
        assertThat(pb.mrid, equalTo(cim.mRID))
        assertThat(pb.name, equalTo(cim.name))
        assertThat(pb.description, equalTo(cim.description))
        assertThat(pb.numDiagramObjects, equalTo(cim.numDiagramObjects))
    }

    fun validate(cim: PowerSystemResource, pb: PBPowerSystemResource) {
        validate(cim, pb.io)

        validateMRID(pb.assetInfoMRID, cim.assetInfo)
        validateMRID(pb.locationMRID, cim.location)
        assertThat(pb.numControls, equalTo(cim.numControls))
    }

    fun validate(cim: Equipment, pb: PBEquipment) {
        validate(cim as PowerSystemResource, pb.psr)

        assertThat(pb.inService, equalTo(cim.inService))
        assertThat(pb.normallyInService, equalTo(cim.normallyInService))
        validateMRIDList(pb.equipmentContainerMRIDsList, cim.containers)
        validateMRIDList(pb.usagePointMRIDsList, cim.usagePoints)
        validateMRIDList(pb.operationalRestrictionMRIDsList, cim.operationalRestrictions)
        validateMRIDList(pb.currentFeederMRIDsList, cim.currentFeeders)
    }

    fun validate(cim: ConductingEquipment, pb: PBConductingEquipment) {
        validate(cim, pb.eq)

        validateMRID(pb.baseVoltageMRID, cim.baseVoltage)
        validateMRIDList(pb.terminalMRIDsList, cim.terminals)
    }

    fun validate(cim: Connector, pb: PBConnector) {
        validate(cim, pb.ce)
    }

    fun validate(cim: BusbarSection, pb: PBBusbarSection) {
        validate(cim, pb.cn)
    }

    fun validate(cim: LoadBreakSwitch, pb: PBLoadBreakSwitch) {
        validate(cim, pb.ps)
    }

    fun validate(cim: ProtectedSwitch, pb: PBProtectedSwitch) {
        validate(cim, pb.sw)
    }

    fun validate(cim: Switch, pb: PBSwitch) {
        validate(cim, pb.ce)
        assertThat(pb.open, equalTo(cim.isOpen()))
        assertThat(pb.normalOpen, equalTo(cim.isNormallyOpen()))
    }

    fun validate(cim: PowerTransformer, pb: PBPowerTransformer) {
        validate(cim, pb.ce)

        validateMRIDList(pb.powerTransformerEndMRIDsList, cim.ends)
        assertThat(VectorGroup.valueOf(pb.vectorGroup.name), equalTo(cim.vectorGroup))
        assertThat(pb.transformerUtilisation, equalTo(cim.transformerUtilisation))
    }

    fun validate(cim: TransformerEnd, pb: PBTransformerEnd) {
        validate(cim, pb.io)

        validateMRID(pb.baseVoltageMRID, cim.baseVoltage)
        validateMRID(pb.ratioTapChangerMRID, cim.ratioTapChanger)
        validateMRID(pb.terminalMRID, cim.terminal)
        assertThat(pb.grounded, equalTo(cim.grounded))
        assertThat(pb.rGround, equalTo(cim.rGround))
        assertThat(pb.xGround, equalTo(cim.xGround))
        assertThat(pb.endNumber, equalTo(cim.endNumber))
    }

    fun validate(cim: PowerTransformerEnd, pb: PBPowerTransformerEnd) {
        validate(cim, pb.te)

        assertThat(pb.b, equalTo(cim.b))
        assertThat(pb.g, equalTo(cim.g))
        assertThat(pb.b0, equalTo(cim.b0))
        assertThat(pb.g0, equalTo(cim.g0))
        assertThat(pb.r, equalTo(cim.r ?: UNKNOWN_DOUBLE))
        assertThat(pb.r0, equalTo(cim.r0 ?: UNKNOWN_DOUBLE))
        assertThat(pb.x, equalTo(cim.x ?: UNKNOWN_DOUBLE))
        assertThat(pb.x0, equalTo(cim.x0 ?: UNKNOWN_DOUBLE))
        assertThat(WindingConnection.valueOf(pb.connectionKind.name), equalTo(cim.connectionKind))
        assertThat(pb.ratedS, equalTo(cim.ratedS))
        assertThat(pb.ratedU, equalTo(cim.ratedU))
        assertThat(pb.phaseAngleClock, equalTo(cim.phaseAngleClock))
    }

    fun validate(cim: Substation, pb: PBSubstation) {
        validate(cim, pb.ec)

        validateMRID(pb.subGeographicalRegionMRID, cim.subGeographicalRegion)
        validateMRIDList(pb.normalEnergizedFeederMRIDsList, cim.feeders)
        validateMRIDList(pb.loopMRIDsList, cim.loops)
        validateMRIDList(pb.normalEnergizedLoopMRIDsList, cim.energizedLoops)
        validateMRIDList(pb.circuitMRIDsList, cim.circuits)
    }

    fun validate(cim: Line, pb: PBLine) {
        validate(cim, pb.ec)
    }

    fun validate(cim: Circuit, pb: PBCircuit) {
        validate(cim, pb.l)

        validateMRID(pb.loopMRID, cim.loop)
        validateMRIDList(pb.endTerminalMRIDsList, cim.endTerminals)
        validateMRIDList(pb.endSubstationMRIDsList, cim.endSubstations)
    }

    fun validate(cim: Loop, pb: PBLoop) {
        validate(cim, pb.io)

        validateMRIDList(pb.circuitMRIDsList, cim.circuits)
        validateMRIDList(pb.substationMRIDsList, cim.substations)
        validateMRIDList(pb.normalEnergizingSubstationMRIDsList, cim.energizingSubstations)
    }

    fun validate(cim: Meter, pb: PBMeter) {
        validate(cim, pb.ed)
    }

    fun validate(cim: EndDevice, pb: PBEndDevice) {
        validate(cim, pb.ac)

        validateMRIDList(pb.usagePointMRIDsList, cim.usagePoints)
        validateMRID(pb.customerMRID, cim.customerMRID)
        validateMRID(pb.serviceLocationMRID, cim.serviceLocation)
    }

    fun validate(cim: AssetContainer, pb: PBAssetContainer) {
        validate(cim, pb.at)
    }

    fun validate(cim: Asset, pb: PBAsset) {
        validate(cim, pb.io)

        validateMRIDList(pb.organisationRoleMRIDsList, cim.organisationRoles)
        validateMRID(pb.locationMRID, cim.location)
    }

    fun validate(cim: Pole, pb: PBPole) {
        validate(cim, pb.st)

        assertThat(pb.classification, equalTo(cim.classification))
        validateMRIDList(pb.streetlightMRIDsList, cim.streetlights)
    }

    private fun validate(cim: Structure, pb: PBStructure) {
        validate(cim, pb.ac)
    }

    fun validate(cim: Measurement, pb: PBMeasurement) {
        validate(cim, pb.io)

        validateMRID(pb.terminalMRID, cim.terminalMRID)
        validateMRID(pb.powerSystemResourceMRID, cim.powerSystemResourceMRID)
        validateMRID(pb.remoteSourceMRID, cim.remoteSource)
        assertThat(PhaseCode.valueOf(pb.phases.name), equalTo(cim.phases))
        assertThat(UnitSymbol.valueOf(pb.unitSymbol.name), equalTo(cim.unitSymbol))
    }

    fun validate(cim: Analog, pb: PBAnalog) {
        validate(cim as Measurement, pb.measurement)

        assertThat(pb.positiveFlowIn, equalTo(cim.positiveFlowIn))
    }

    fun validate(cim: Accumulator, pb: PBAccumulator) {
        validate(cim as Measurement, pb.measurement)
    }

    fun validate(cim: Discrete, pb: PBDiscrete) {
        validate(cim as Measurement, pb.measurement)
    }

    fun validate(cim: PowerTransformerInfo, pb: PBPowerTransformerInfo) {
        validate(cim as AssetInfo, pb.ai)

        validateMRIDList(pb.transformerTankInfoMRIDsList, cim.transformerTankInfos)
    }

    fun validate(cim: TransformerTankInfo, pb: PBTransformerTankInfo) {
        validate(cim as AssetInfo, pb.ai)

        validateMRID(pb.powerTransformerInfoMRID, cim.powerTransformerInfo)
        validateMRIDList(pb.transformerEndInfoMRIDsList, cim.transformerEndInfos)
    }

    fun validate(cim: TransformerEndInfo, pb: PBTransformerEndInfo) {
        validate(cim as AssetInfo, pb.ai)

        assertThat(WindingConnection.valueOf(pb.connectionKind.name), equalTo(cim.connectionKind))
        assertThat(pb.emergencyS, equalTo(cim.emergencyS))
        assertThat(pb.endNumber, equalTo(cim.endNumber))
        assertThat(pb.insulationU, equalTo(cim.insulationU))
        assertThat(pb.phaseAngleClock, equalTo(cim.phaseAngleClock))
        assertThat(pb.r, equalTo(cim.r))
        assertThat(pb.ratedS, equalTo(cim.ratedS))
        assertThat(pb.ratedU, equalTo(cim.ratedU))
        assertThat(pb.shortTermS, equalTo(cim.shortTermS))

        validateMRID(pb.transformerTankInfoMRID, cim.transformerTankInfo)
        validateMRID(pb.transformerStarImpedanceMRID, cim.transformerStarImpedance)
    }

    private fun validate(cim: AssetInfo, pb: PBAssetInfo) {
        validate(cim as IdentifiedObject, pb.io)
    }

    fun validate(cim: TransformerStarImpedance, pb: PBTransformerStarImpedance) {
        validate(cim as IdentifiedObject, pb.io)

        assertThat(pb.r, equalTo(cim.r ?: UNKNOWN_DOUBLE))
        assertThat(pb.r0, equalTo(cim.r0 ?: UNKNOWN_DOUBLE))
        assertThat(pb.x, equalTo(cim.x ?: UNKNOWN_DOUBLE))
        assertThat(pb.x0, equalTo(cim.x0 ?: UNKNOWN_DOUBLE))

        validateMRID(pb.transformerEndInfoMRID, cim.transformerEndInfo)
    }

}

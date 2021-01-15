/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network.translator

import com.zepben.evolve.cim.iec61968.assetinfo.CableInfo
import com.zepben.evolve.cim.iec61968.assetinfo.OverheadWireInfo
import com.zepben.evolve.cim.iec61968.assetinfo.PowerTransformerInfo
import com.zepben.evolve.cim.iec61968.assetinfo.WireInfo
import com.zepben.evolve.cim.iec61968.assets.*
import com.zepben.evolve.cim.iec61968.common.Location
import com.zepben.evolve.cim.iec61968.common.PositionPoint
import com.zepben.evolve.cim.iec61968.common.StreetAddress
import com.zepben.evolve.cim.iec61968.common.TownDetail
import com.zepben.evolve.cim.iec61968.metering.EndDevice
import com.zepben.evolve.cim.iec61968.metering.Meter
import com.zepben.evolve.cim.iec61968.metering.UsagePoint
import com.zepben.evolve.cim.iec61968.operations.OperationalRestriction
import com.zepben.evolve.cim.iec61970.base.auxiliaryequipment.AuxiliaryEquipment
import com.zepben.evolve.cim.iec61970.base.auxiliaryequipment.FaultIndicator
import com.zepben.evolve.cim.iec61970.base.core.*
import com.zepben.evolve.cim.iec61970.base.meas.*
import com.zepben.evolve.cim.iec61970.base.scada.RemoteControl
import com.zepben.evolve.cim.iec61970.base.scada.RemotePoint
import com.zepben.evolve.cim.iec61970.base.scada.RemoteSource
import com.zepben.evolve.cim.iec61970.base.wires.*
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.Circuit
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.Loop
import com.zepben.evolve.services.common.translator.BaseCimToProto
import com.zepben.evolve.services.common.translator.toPb
import com.zepben.protobuf.cim.iec61968.assetinfo.WireMaterialKind
import com.zepben.protobuf.cim.iec61970.base.wires.PhaseShuntConnectionKind
import com.zepben.protobuf.cim.iec61970.base.wires.SinglePhaseKind
import com.zepben.protobuf.cim.iec61970.base.wires.VectorGroup
import com.zepben.protobuf.cim.iec61970.base.wires.WindingConnection
import com.zepben.protobuf.cim.iec61968.assetinfo.CableInfo as PBCableInfo
import com.zepben.protobuf.cim.iec61968.assetinfo.OverheadWireInfo as PBOverheadWireInfo
import com.zepben.protobuf.cim.iec61968.assetinfo.PowerTransformerInfo as PPowerTransformerInfo
import com.zepben.protobuf.cim.iec61968.assetinfo.WireInfo as PBWireInfo
import com.zepben.protobuf.cim.iec61968.assets.Asset as PBAsset
import com.zepben.protobuf.cim.iec61968.assets.AssetContainer as PBAssetContainer
import com.zepben.protobuf.cim.iec61968.assets.AssetInfo as PBAssetInfo
import com.zepben.protobuf.cim.iec61968.assets.AssetOrganisationRole as PBAssetOrganisationRole
import com.zepben.protobuf.cim.iec61968.assets.AssetOwner as PBAssetOwner
import com.zepben.protobuf.cim.iec61968.assets.Pole as PBPole
import com.zepben.protobuf.cim.iec61968.assets.Streetlight as PBStreetlight
import com.zepben.protobuf.cim.iec61968.assets.StreetlightLampKind as PBStreetlightLampKind
import com.zepben.protobuf.cim.iec61968.assets.Structure as PBStructure
import com.zepben.protobuf.cim.iec61968.common.Location as PBLocation
import com.zepben.protobuf.cim.iec61968.common.PositionPoint as PBPositionPoint
import com.zepben.protobuf.cim.iec61968.common.StreetAddress as PBStreetAddress
import com.zepben.protobuf.cim.iec61968.common.TownDetail as PBTownDetail
import com.zepben.protobuf.cim.iec61968.metering.EndDevice as PBEndDevice
import com.zepben.protobuf.cim.iec61968.metering.Meter as PBMeter
import com.zepben.protobuf.cim.iec61968.metering.UsagePoint as PBUsagePoint
import com.zepben.protobuf.cim.iec61968.operations.OperationalRestriction as PBOperationalRestriction
import com.zepben.protobuf.cim.iec61970.base.auxiliaryequipment.AuxiliaryEquipment as PBAuxiliaryEquipment
import com.zepben.protobuf.cim.iec61970.base.auxiliaryequipment.FaultIndicator as PBFaultIndicator
import com.zepben.protobuf.cim.iec61970.base.core.AcDcTerminal as PBAcDcTerminal
import com.zepben.protobuf.cim.iec61970.base.core.BaseVoltage as PBBaseVoltage
import com.zepben.protobuf.cim.iec61970.base.core.ConductingEquipment as PBConductingEquipment
import com.zepben.protobuf.cim.iec61970.base.core.ConnectivityNode as PBConnectivityNode
import com.zepben.protobuf.cim.iec61970.base.core.ConnectivityNodeContainer as PBConnectivityNodeContainer
import com.zepben.protobuf.cim.iec61970.base.core.Equipment as PBEquipment
import com.zepben.protobuf.cim.iec61970.base.core.EquipmentContainer as PBEquipmentContainer
import com.zepben.protobuf.cim.iec61970.base.core.Feeder as PBFeeder
import com.zepben.protobuf.cim.iec61970.base.core.GeographicalRegion as PBGeographicalRegion
import com.zepben.protobuf.cim.iec61970.base.core.PhaseCode as PBPhaseCode
import com.zepben.protobuf.cim.iec61970.base.core.PowerSystemResource as PBPowerSystemResource
import com.zepben.protobuf.cim.iec61970.base.core.Site as PBSite
import com.zepben.protobuf.cim.iec61970.base.core.SubGeographicalRegion as PBSubGeographicalRegion
import com.zepben.protobuf.cim.iec61970.base.core.Substation as PBSubstation
import com.zepben.protobuf.cim.iec61970.base.core.Terminal as PBTerminal
import com.zepben.protobuf.cim.iec61970.base.domain.UnitSymbol as PBUnitSymbol
import com.zepben.protobuf.cim.iec61970.base.meas.Accumulator as PBAccumulator
import com.zepben.protobuf.cim.iec61970.base.meas.Analog as PBAnalog
import com.zepben.protobuf.cim.iec61970.base.meas.Control as PBControl
import com.zepben.protobuf.cim.iec61970.base.meas.Discrete as PBDiscrete
import com.zepben.protobuf.cim.iec61970.base.meas.IoPoint as PBIoPoint
import com.zepben.protobuf.cim.iec61970.base.meas.Measurement as PBMeasurement
import com.zepben.protobuf.cim.iec61970.base.scada.RemoteControl as PBRemoteControl
import com.zepben.protobuf.cim.iec61970.base.scada.RemotePoint as PBRemotePoint
import com.zepben.protobuf.cim.iec61970.base.scada.RemoteSource as PBRemoteSource
import com.zepben.protobuf.cim.iec61970.base.wires.AcLineSegment as PBAcLineSegment
import com.zepben.protobuf.cim.iec61970.base.wires.Breaker as PBBreaker
import com.zepben.protobuf.cim.iec61970.base.wires.Conductor as PBConductor
import com.zepben.protobuf.cim.iec61970.base.wires.Connector as PBConnector
import com.zepben.protobuf.cim.iec61970.base.wires.Disconnector as PBDisconnector
import com.zepben.protobuf.cim.iec61970.base.wires.EnergyConnection as PBEnergyConnection
import com.zepben.protobuf.cim.iec61970.base.wires.EnergyConsumer as PBEnergyConsumer
import com.zepben.protobuf.cim.iec61970.base.wires.EnergyConsumerPhase as PBEnergyConsumerPhase
import com.zepben.protobuf.cim.iec61970.base.wires.EnergySource as PBEnergySource
import com.zepben.protobuf.cim.iec61970.base.wires.EnergySourcePhase as PBEnergySourcePhase
import com.zepben.protobuf.cim.iec61970.base.wires.Fuse as PBFuse
import com.zepben.protobuf.cim.iec61970.base.wires.Jumper as PBJumper
import com.zepben.protobuf.cim.iec61970.base.wires.Junction as PBJunction
import com.zepben.protobuf.cim.iec61970.base.wires.Line as PBLine
import com.zepben.protobuf.cim.iec61970.base.wires.LinearShuntCompensator as PBLinearShuntCompensator
import com.zepben.protobuf.cim.iec61970.base.wires.PerLengthImpedance as PBPerLengthImpedance
import com.zepben.protobuf.cim.iec61970.base.wires.PerLengthLineParameter as PBPerLengthLineParameter
import com.zepben.protobuf.cim.iec61970.base.wires.PerLengthSequenceImpedance as PBPerLengthSequenceImpedance
import com.zepben.protobuf.cim.iec61970.base.wires.PowerTransformer as PBPowerTransformer
import com.zepben.protobuf.cim.iec61970.base.wires.PowerTransformerEnd as PBPowerTransformerEnd
import com.zepben.protobuf.cim.iec61970.base.wires.ProtectedSwitch as PBProtectedSwitch
import com.zepben.protobuf.cim.iec61970.base.wires.RatioTapChanger as PBRatioTapChanger
import com.zepben.protobuf.cim.iec61970.base.wires.Recloser as PBRecloser
import com.zepben.protobuf.cim.iec61970.base.wires.RegulatingCondEq as PBRegulatingCondEq
import com.zepben.protobuf.cim.iec61970.base.wires.ShuntCompensator as PBShuntCompensator
import com.zepben.protobuf.cim.iec61970.base.wires.Switch as PBSwitch
import com.zepben.protobuf.cim.iec61970.base.wires.TapChanger as PBTapChanger
import com.zepben.protobuf.cim.iec61970.base.wires.TransformerEnd as PBTransformerEnd
import com.zepben.protobuf.cim.iec61970.infiec61970.feeder.Circuit as PBCircuit
import com.zepben.protobuf.cim.iec61970.infiec61970.feeder.Loop as PBLoop

/************ IEC61968 ASSET INFO ************/
fun toPb(cim: CableInfo, pb: PBCableInfo.Builder): PBCableInfo.Builder =
    pb.apply { toPb(cim, wiBuilder) }

fun toPb(cim: OverheadWireInfo, pb: PBOverheadWireInfo.Builder): PBOverheadWireInfo.Builder =
    pb.apply { toPb(cim, wiBuilder) }

fun toPb(cim: PowerTransformerInfo, pb: PPowerTransformerInfo.Builder): PPowerTransformerInfo.Builder =
    pb.apply { toPb(cim, aiBuilder) }

fun toPb(cim: WireInfo, pb: PBWireInfo.Builder): PBWireInfo.Builder =
    pb.apply {
        ratedCurrent = cim.ratedCurrent
        material = WireMaterialKind.valueOf(cim.material.name)
        toPb(cim, aiBuilder)
    }

/************ IEC61968 ASSETS ************/
fun toPb(cim: Asset, pb: PBAsset.Builder): PBAsset.Builder =
    pb.apply {
        cim.location?.let { locationMRID = it.mRID } ?: clearLocationMRID()
        clearOrganisationRoleMRIDs()
        cim.organisationRoles.forEach { addOrganisationRoleMRIDs(it.mRID) }
        toPb(cim, ioBuilder)
    }

fun toPb(cim: AssetContainer, pb: PBAssetContainer.Builder): PBAssetContainer.Builder =
    pb.apply { toPb(cim, atBuilder) }

fun toPb(cim: AssetInfo, pb: PBAssetInfo.Builder): PBAssetInfo.Builder =
    pb.apply { toPb(cim, ioBuilder) }

fun toPb(cim: AssetOrganisationRole, pb: PBAssetOrganisationRole.Builder): PBAssetOrganisationRole.Builder =
    pb.apply { toPb(cim, orBuilder) }

fun toPb(cim: AssetOwner, pb: PBAssetOwner.Builder): PBAssetOwner.Builder =
    pb.apply { toPb(cim, aorBuilder) }

fun toPb(cim: Pole, pb: PBPole.Builder): PBPole.Builder =
    pb.apply {
        classification = cim.classification
        cim.streetlights.forEach { addStreetlightMRIDs(it.mRID) }
        toPb(cim, stBuilder)
    }

fun toPb(cim: Streetlight, pb: PBStreetlight.Builder): PBStreetlight.Builder =
    pb.apply {
        lightRating = cim.lightRating
        lampKind = PBStreetlightLampKind.valueOf(cim.lampKind.name)
        cim.pole?.let { poleMRID = it.mRID } ?: clearPoleMRID()
        toPb(cim, atBuilder)
    }

fun toPb(cim: Structure, pb: PBStructure.Builder): PBStructure.Builder =
    pb.apply { toPb(cim, acBuilder) }

/************ IEC61968 COMMON ************/
fun toPb(cim: Location, pb: PBLocation.Builder): PBLocation.Builder =
    pb.apply {
        cim.mainAddress?.let { toPb(it, mainAddressBuilder) } ?: clearMainAddress()
        clearPositionPoints()
        cim.points.forEachIndexed { i, point -> addPositionPointsBuilder(i).apply { toPb(point, this) } }
        toPb(cim, ioBuilder)
    }

fun toPb(cim: PositionPoint, pb: PBPositionPoint.Builder): PBPositionPoint.Builder =
    pb.apply {
        xPosition = cim.xPosition
        yPosition = cim.yPosition
    }

fun toPb(cim: StreetAddress, pb: PBStreetAddress.Builder): PBStreetAddress.Builder =
    pb.apply {
        postalCode = cim.postalCode
        cim.townDetail?.let { toPb(it, townDetailBuilder) } ?: clearTownDetail()
    }

fun toPb(cim: TownDetail, pb: PBTownDetail.Builder): PBTownDetail.Builder =
    pb.apply {
        name = cim.name
        stateOrProvince = cim.stateOrProvince
    }

/************ IEC61968 METERING ************/
fun toPb(cim: EndDevice, pb: PBEndDevice.Builder): PBEndDevice.Builder =
    pb.apply {
        clearUsagePointMRIDs()
        cim.usagePoints.forEach { addUsagePointMRIDs(it.mRID) }
        cim.customerMRID?.let { customerMRID = it } ?: clearCustomerMRID()
        cim.serviceLocation?.let { serviceLocationMRID = it.mRID } ?: clearServiceLocationMRID()
        toPb(cim, acBuilder)
    }

fun toPb(cim: Meter, pb: PBMeter.Builder): PBMeter.Builder =
    pb.apply {
        toPb(cim, edBuilder)
    }

fun toPb(cim: UsagePoint, pb: PBUsagePoint.Builder): PBUsagePoint.Builder =
    pb.apply {
        cim.usagePointLocation?.let { usagePointLocationMRID = it.mRID } ?: clearUsagePointLocationMRID()
        clearEquipmentMRIDs()
        cim.equipment.forEach { addEquipmentMRIDs(it.mRID) }
        clearEndDeviceMRIDs()
        cim.endDevices.forEach { addEndDeviceMRIDs(it.mRID) }
        toPb(cim, ioBuilder)
    }

/************ IEC61968 OPERATIONS ************/
fun toPb(cim: OperationalRestriction, pb: PBOperationalRestriction.Builder): PBOperationalRestriction.Builder =
    pb.apply {
        clearEquipmentMRIDs()
        cim.equipment.forEach { addEquipmentMRIDs(it.mRID) }
        toPb(cim, docBuilder)
    }

/************ IEC61970 AUXILIARY EQUIPMENT ************/
fun toPb(cim: AuxiliaryEquipment, pb: PBAuxiliaryEquipment.Builder): PBAuxiliaryEquipment.Builder =
    pb.apply {
        cim.terminal?.let { terminalMRID = it.mRID } ?: clearTerminalMRID()
        toPb(cim, eqBuilder)
    }

fun toPb(cim: FaultIndicator, pb: PBFaultIndicator.Builder): PBFaultIndicator.Builder =
    pb.apply { toPb(cim, aeBuilder) }


/************ IEC61970 CORE ************/
fun toPb(cim: AcDcTerminal, pb: PBAcDcTerminal.Builder): PBAcDcTerminal.Builder =
    pb.apply { toPb(cim, ioBuilder) }

fun toPb(cim: BaseVoltage, pb: PBBaseVoltage.Builder): PBBaseVoltage.Builder =
    pb.apply {
        nominalVoltage = cim.nominalVoltage
        toPb(cim, ioBuilder)
    }

fun toPb(cim: ConductingEquipment, pb: PBConductingEquipment.Builder): PBConductingEquipment.Builder =
    pb.apply {
        cim.baseVoltage?.let { baseVoltageMRID = it.mRID } ?: clearBaseVoltageMRID()
        clearTerminalMRIDs()
        cim.terminals.forEach { addTerminalMRIDs(it.mRID) }
        toPb(cim, eqBuilder)
    }

fun toPb(cim: ConnectivityNode, pb: PBConnectivityNode.Builder): PBConnectivityNode.Builder =
    pb.apply {
        clearTerminalMRIDs()
        cim.terminals.forEach { addTerminalMRIDs(it.mRID) }
        toPb(cim, ioBuilder)
    }

fun toPb(cim: ConnectivityNodeContainer, pb: PBConnectivityNodeContainer.Builder): PBConnectivityNodeContainer.Builder =
    pb.apply { toPb(cim, psrBuilder) }

fun toPb(cim: Equipment, pb: PBEquipment.Builder): PBEquipment.Builder =
    pb.apply {
        inService = cim.inService
        normallyInService = cim.normallyInService

        clearEquipmentContainerMRIDs()
        cim.containers.forEach { addEquipmentContainerMRIDs(it.mRID) }

        clearUsagePointMRIDs()
        cim.usagePoints.forEach { addUsagePointMRIDs(it.mRID) }

        clearOperationalRestrictionMRIDs()
        cim.operationalRestrictions.forEach { addOperationalRestrictionMRIDs(it.mRID) }

        clearCurrentFeederMRIDs()
        cim.currentFeeders.forEach { addCurrentFeederMRIDs(it.mRID) }

        toPb(cim, psrBuilder)
    }

fun toPb(cim: EquipmentContainer, pb: PBEquipmentContainer.Builder): PBEquipmentContainer.Builder =
    pb.apply {
        clearEquipmentMRIDs()
        cim.equipment.forEach { addEquipmentMRIDs(it.mRID) }
        toPb(cim, cncBuilder)
    }

fun toPb(cim: Feeder, pb: PBFeeder.Builder): PBFeeder.Builder =
    pb.apply {
        cim.normalHeadTerminal?.let { normalHeadTerminalMRID = it.mRID } ?: clearNormalHeadTerminalMRID()
        cim.normalEnergizingSubstation?.let { normalEnergizingSubstationMRID = it.mRID } ?: clearNormalEnergizingSubstationMRID()

        clearCurrentEquipmentMRIDs()
        cim.currentEquipment.forEach { addCurrentEquipmentMRIDs(it.mRID) }

        toPb(cim, ecBuilder)
    }

fun toPb(cim: GeographicalRegion, pb: PBGeographicalRegion.Builder): PBGeographicalRegion.Builder =
    pb.apply {
        clearSubGeographicalRegionMRIDs()
        cim.subGeographicalRegions.forEach { addSubGeographicalRegionMRIDs(it.mRID) }
        toPb(cim, ioBuilder)
    }

fun toPb(cim: PowerSystemResource, pb: PBPowerSystemResource.Builder): PBPowerSystemResource.Builder =
    pb.apply {
        cim.location?.let { locationMRID = it.mRID } ?: clearLocationMRID()
        cim.assetInfo?.let { assetInfoMRID = it.mRID } ?: clearAssetInfoMRID()
        numControls = cim.numControls
        toPb(cim, ioBuilder)
    }

fun toPb(cim: Site, pb: PBSite.Builder): PBSite.Builder =
    pb.apply { toPb(cim, ecBuilder) }

fun toPb(cim: SubGeographicalRegion, pb: PBSubGeographicalRegion.Builder): PBSubGeographicalRegion.Builder =
    pb.apply {
        cim.geographicalRegion?.let { geographicalRegionMRID = it.mRID } ?: clearGeographicalRegionMRID()
        clearSubstationMRIDs()
        cim.substations.forEach { addSubstationMRIDs(it.mRID) }
        toPb(cim, ioBuilder)
    }

fun toPb(cim: Substation, pb: PBSubstation.Builder): PBSubstation.Builder =
    pb.apply {
        cim.subGeographicalRegion?.let { subGeographicalRegionMRID = it.mRID } ?: clearSubGeographicalRegionMRID()
        clearNormalEnergizedFeederMRIDs()
        cim.feeders.forEach { addNormalEnergizedFeederMRIDs(it.mRID) }
        clearLoopMRIDs()
        cim.loops.forEach { addLoopMRIDs(it.mRID) }
        clearNormalEnergizedLoopMRIDs()
        cim.energizedLoops.forEach { addNormalEnergizedLoopMRIDs(it.mRID) }
        clearCircuitMRIDs()
        cim.circuits.forEach { addCircuitMRIDs(it.mRID) }
        toPb(cim, ecBuilder)
    }

fun toPb(cim: Terminal, pb: PBTerminal.Builder): PBTerminal.Builder =
    pb.apply {
        cim.conductingEquipment?.let { conductingEquipmentMRID = it.mRID } ?: clearConductingEquipmentMRID()
        cim.connectivityNodeId()?.let { connectivityNodeMRID = it } ?: clearConnectivityNodeMRID()
        phases = com.zepben.protobuf.cim.iec61970.base.core.PhaseCode.valueOf(cim.phases.name)
        tracedPhasesBuilder.normalStatus = cim.tracedPhases.normalStatusInternal
        tracedPhasesBuilder.currentStatus = cim.tracedPhases.currentStatusInternal
        sequenceNumber = cim.sequenceNumber
        toPb(cim, adBuilder)
    }

/************ IEC61970 WIRES ************/
fun toPb(cim: AcLineSegment, pb: PBAcLineSegment.Builder): PBAcLineSegment.Builder =
    pb.apply {
        cim.perLengthSequenceImpedance?.let { perLengthSequenceImpedanceMRID = it.mRID } ?: clearPerLengthSequenceImpedanceMRID()
        toPb(cim, cdBuilder)
    }

fun toPb(cim: Breaker, pb: PBBreaker.Builder): PBBreaker.Builder =
    pb.apply { toPb(cim, swBuilder) }

fun toPb(cim: Conductor, pb: PBConductor.Builder): PBConductor.Builder =
    pb.apply {
        length = cim.length
        toPb(cim, ceBuilder)
    }

fun toPb(cim: Connector, pb: PBConnector.Builder): PBConnector.Builder =
    pb.apply { toPb(cim, ceBuilder) }

fun toPb(cim: Disconnector, pb: PBDisconnector.Builder): PBDisconnector.Builder =
    pb.apply { toPb(cim, swBuilder) }

fun toPb(cim: EnergyConnection, pb: PBEnergyConnection.Builder): PBEnergyConnection.Builder =
    pb.apply { toPb(cim, ceBuilder) }

fun toPb(cim: EnergyConsumer, pb: PBEnergyConsumer.Builder): PBEnergyConsumer.Builder =
    pb.apply {
        clearEnergyConsumerPhasesMRIDs()
        cim.phases.forEach { addEnergyConsumerPhasesMRIDs(it.mRID) }
        customerCount = cim.customerCount
        grounded = cim.grounded
        p = cim.p
        pFixed = cim.pFixed
        phaseConnection = PhaseShuntConnectionKind.Enum.valueOf(cim.phaseConnection.name)
        q = cim.q
        qFixed = cim.qFixed
        toPb(cim, ecBuilder)
    }

fun toPb(cim: EnergyConsumerPhase, pb: PBEnergyConsumerPhase.Builder): PBEnergyConsumerPhase.Builder =
    pb.apply {
        cim.energyConsumer?.let { energyConsumerMRID = it.mRID } ?: clearEnergyConsumerMRID()
        phase = SinglePhaseKind.valueOf(cim.phase.name)
        p = cim.p
        pFixed = cim.pFixed
        q = cim.q
        qFixed = cim.qFixed
        toPb(cim, psrBuilder)
    }

fun toPb(cim: EnergySource, pb: PBEnergySource.Builder): PBEnergySource.Builder =
    pb.apply {
        clearEnergySourcePhasesMRIDs()
        cim.phases.forEach { addEnergySourcePhasesMRIDs(it.mRID) }
        activePower = cim.activePower
        reactivePower = cim.reactivePower
        voltageAngle = cim.voltageAngle
        voltageMagnitude = cim.voltageMagnitude
        r = cim.r
        x = cim.x
        pMax = cim.pMax
        pMin = cim.pMin
        r0 = cim.r0
        rn = cim.rn
        x0 = cim.x0
        xn = cim.xn
        toPb(cim, ecBuilder)
    }

fun toPb(cim: EnergySourcePhase, pb: PBEnergySourcePhase.Builder): PBEnergySourcePhase.Builder =
    pb.apply {
        cim.energySource?.let { energySourceMRID = it.mRID } ?: clearEnergySourceMRID()
        phase = SinglePhaseKind.valueOf(cim.phase.name)
        toPb(cim, psrBuilder)
    }

fun toPb(cim: Fuse, pb: PBFuse.Builder): PBFuse.Builder =
    pb.apply { toPb(cim, swBuilder) }

fun toPb(cim: Jumper, pb: PBJumper.Builder): PBJumper.Builder =
    pb.apply { toPb(cim, swBuilder) }

fun toPb(cim: Junction, pb: PBJunction.Builder): PBJunction.Builder =
    pb.apply { toPb(cim, cnBuilder) }

fun toPb(cim: Line, pb: PBLine.Builder): PBLine.Builder =
    pb.apply { toPb(cim, ecBuilder) }

fun toPb(cim: LinearShuntCompensator, pb: PBLinearShuntCompensator.Builder): PBLinearShuntCompensator.Builder =
    pb.apply {
        b0PerSection = cim.b0PerSection
        bPerSection = cim.bPerSection
        g0PerSection = cim.g0PerSection
        gPerSection = cim.gPerSection
        toPb(cim, scBuilder)
    }

fun toPb(cim: PerLengthLineParameter, pb: PBPerLengthLineParameter.Builder): PBPerLengthLineParameter.Builder =
    pb.apply { toPb(cim, ioBuilder) }

fun toPb(cim: PerLengthImpedance, pb: PBPerLengthImpedance.Builder): PBPerLengthImpedance.Builder =
    pb.apply { toPb(cim, lpBuilder) }

fun toPb(cim: PerLengthSequenceImpedance, pb: PBPerLengthSequenceImpedance.Builder): PBPerLengthSequenceImpedance.Builder =
    pb.apply {
        r = cim.r
        x = cim.x
        r0 = cim.r0
        x0 = cim.x0
        bch = cim.bch
        gch = cim.gch
        b0Ch = cim.b0ch
        g0Ch = cim.g0ch
        toPb(cim, pliBuilder)
    }

fun toPb(cim: PowerTransformer, pb: PBPowerTransformer.Builder): PBPowerTransformer.Builder =
    pb.apply {
        clearPowerTransformerEndMRIDs()
        cim.ends.forEach { addPowerTransformerEndMRIDs(it.mRID) }
        vectorGroup = VectorGroup.valueOf(cim.vectorGroup.name)
        transformerUtilisation = cim.transformerUtilisation
        toPb(cim, ceBuilder)
    }

fun toPb(cim: PowerTransformerEnd, pb: PBPowerTransformerEnd.Builder): PBPowerTransformerEnd.Builder =
    pb.apply {
        cim.powerTransformer?.let { powerTransformerMRID = it.mRID } ?: clearPowerTransformerMRID()
        ratedS = cim.ratedS
        ratedU = cim.ratedU
        r = cim.r
        r0 = cim.r0
        x = cim.x
        x0 = cim.x0
        connectionKind = WindingConnection.valueOf(cim.connectionKind.name)
        b = cim.b
        b0 = cim.b0
        g = cim.g
        g0 = cim.g0
        phaseAngleClock = cim.phaseAngleClock
        toPb(cim, teBuilder)
    }

fun toPb(cim: ProtectedSwitch, pb: PBProtectedSwitch.Builder): PBProtectedSwitch.Builder =
    pb.apply { toPb(cim, swBuilder) }

fun toPb(cim: RatioTapChanger, pb: PBRatioTapChanger.Builder): PBRatioTapChanger.Builder =
    pb.apply {
        cim.transformerEnd?.let { transformerEndMRID = it.mRID } ?: clearTransformerEndMRID()
        stepVoltageIncrement = cim.stepVoltageIncrement
        toPb(cim, tcBuilder)
    }

fun toPb(cim: Recloser, pb: PBRecloser.Builder): PBRecloser.Builder =
    pb.apply { toPb(cim, swBuilder) }


fun toPb(cim: RegulatingCondEq, pb: PBRegulatingCondEq.Builder): PBRegulatingCondEq.Builder =
    pb.apply {
        controlEnabled = cim.controlEnabled
        toPb(cim, ecBuilder)
    }

fun toPb(cim: ShuntCompensator, pb: PBShuntCompensator.Builder): PBShuntCompensator.Builder =
    pb.apply {
        sections = cim.sections
        grounded = cim.grounded
        nomU = cim.nomU
        phaseConnection = PhaseShuntConnectionKind.Enum.valueOf(cim.phaseConnection.name)
        toPb(cim, rceBuilder)
    }

fun toPb(cim: Switch, pb: PBSwitch.Builder): PBSwitch.Builder =
    pb.apply {
        normalOpen = cim.isNormallyOpen()
        open = cim.isOpen()
        toPb(cim, ceBuilder)
    }

fun toPb(cim: TapChanger, pb: PBTapChanger.Builder): PBTapChanger.Builder =
    pb.apply {
        highStep = cim.highStep
        lowStep = cim.lowStep
        step = cim.step
        neutralStep = cim.neutralStep
        neutralU = cim.neutralU
        normalStep = cim.normalStep
        controlEnabled = cim.controlEnabled
        toPb(cim, psrBuilder)
    }

fun toPb(cim: TransformerEnd, pb: PBTransformerEnd.Builder): PBTransformerEnd.Builder =
    pb.apply {
        cim.terminal?.let { terminalMRID = it.mRID } ?: clearTerminalMRID()
        cim.baseVoltage?.let { baseVoltageMRID = it.mRID } ?: clearBaseVoltageMRID()
        cim.ratioTapChanger?.let { ratioTapChangerMRID = it.mRID } ?: clearRatioTapChangerMRID()
        endNumber = cim.endNumber
        grounded = cim.grounded
        rGround = cim.rGround
        xGround = cim.xGround
        toPb(cim, ioBuilder)
    }

fun toPb(cim: Circuit, pb: PBCircuit.Builder): PBCircuit.Builder =
    pb.apply {
        cim.loop?.let { loopMRID = it.mRID } ?: clearLoopMRID()

        clearEndTerminalMRIDs()
        cim.endTerminals.forEach { addEndTerminalMRIDs(it.mRID) }

        clearEndSubstationMRIDs()
        cim.endSubstations.forEach { addEndSubstationMRIDs(it.mRID) }

        toPb(cim, lBuilder)
    }

fun toPb(cim: Loop, pb: PBLoop.Builder): PBLoop.Builder =
    pb.apply {
        clearCircuitMRIDs()
        cim.circuits.forEach { addCircuitMRIDs(it.mRID) }

        clearSubstationMRIDs()
        cim.substations.forEach { addSubstationMRIDs(it.mRID) }

        clearNormalEnergizingSubstationMRIDs()
        cim.energizingSubstations.forEach { addNormalEnergizingSubstationMRIDs(it.mRID) }

        toPb(cim, ioBuilder)
    }

/************ IEC61970 MEAS ************/
fun toPb(cim: Control, pb: PBControl.Builder): PBControl.Builder =
    pb.apply {
        cim.remoteControl?.let { remoteControlMRID = it.mRID } ?: clearRemoteControlMRID()
        cim.powerSystemResourceMRID?.let { powerSystemResourceMRID = it } ?: clearPowerSystemResourceMRID()
        toPb(cim, ipBuilder)
    }

fun toPb(cim: IoPoint, pb: PBIoPoint.Builder): PBIoPoint.Builder = pb.apply { toPb(cim, ioBuilder) }

fun toPb(cim: Accumulator, pb: PBAccumulator.Builder): PBAccumulator.Builder = pb.apply { toPb(cim, measurementBuilder) }

fun toPb(cim: Analog, pb: PBAnalog.Builder): PBAnalog.Builder =
    pb.apply {
        positiveFlowIn = cim.positiveFlowIn
        toPb(cim, measurementBuilder)
    }

fun toPb(cim: Discrete, pb: PBDiscrete.Builder): PBDiscrete.Builder = pb.apply { toPb(cim, measurementBuilder) }

fun toPb(cim: Measurement, pb: PBMeasurement.Builder): PBMeasurement.Builder =
    pb.apply {
        cim.remoteSource?.let { remoteSourceMRID = it.mRID } ?: clearRemoteSourceMRID()
        cim.powerSystemResourceMRID?.let { powerSystemResourceMRID = it } ?: clearPowerSystemResourceMRID()
        toPb(cim, ioBuilder)
        cim.terminalMRID?.let { terminalMRID = it } ?: clearTerminalMRID()
        phases = PBPhaseCode.valueOf(cim.phases.name)
        unitSymbol = PBUnitSymbol.valueOf(cim.unitSymbol.name)
    }

/************ IEC61970 SCADA ************/
fun toPb(cim: RemoteControl, pb: PBRemoteControl.Builder): PBRemoteControl.Builder =
    pb.apply {
        cim.control?.let { controlMRID = it.mRID } ?: clearControlMRID()
        toPb(cim, rpBuilder)
    }

fun toPb(cim: RemotePoint, pb: PBRemotePoint.Builder): PBRemotePoint.Builder =
    pb.apply { toPb(cim, ioBuilder) }

fun toPb(cim: RemoteSource, pb: PBRemoteSource.Builder): PBRemoteSource.Builder =
    pb.apply {
        cim.measurement?.let { measurementMRID = it.mRID } ?: clearMeasurementMRID()
        toPb(cim, pb.rpBuilder)
    }

/************ Extensions ************/

fun CableInfo.toPb(): PBCableInfo = toPb(this, PBCableInfo.newBuilder()).build()
fun OverheadWireInfo.toPb(): PBOverheadWireInfo = toPb(this, PBOverheadWireInfo.newBuilder()).build()
fun Meter.toPb(): PBMeter = toPb(this, PBMeter.newBuilder()).build()
fun OperationalRestriction.toPb(): PBOperationalRestriction = toPb(this, PBOperationalRestriction.newBuilder()).build()
fun AssetOwner.toPb(): PBAssetOwner = toPb(this, PBAssetOwner.newBuilder()).build()
fun Pole.toPb(): PBPole = toPb(this, PBPole.newBuilder()).build()
fun Streetlight.toPb(): PBStreetlight = toPb(this, PBStreetlight.newBuilder()).build()
fun Location.toPb(): PBLocation = toPb(this, PBLocation.newBuilder()).build()
fun UsagePoint.toPb(): PBUsagePoint = toPb(this, PBUsagePoint.newBuilder()).build()
fun ConnectivityNode.toPb(): PBConnectivityNode = toPb(this, PBConnectivityNode.newBuilder()).build()
fun BaseVoltage.toPb(): PBBaseVoltage = toPb(this, PBBaseVoltage.newBuilder()).build()
fun Junction.toPb(): PBJunction = toPb(this, PBJunction.newBuilder()).build()
fun AcLineSegment.toPb(): PBAcLineSegment = toPb(this, PBAcLineSegment.newBuilder()).build()
fun LinearShuntCompensator.toPb(): PBLinearShuntCompensator = toPb(this, PBLinearShuntCompensator.newBuilder()).build()
fun EnergyConsumer.toPb(): PBEnergyConsumer = toPb(this, PBEnergyConsumer.newBuilder()).build()
fun EnergySource.toPb(): PBEnergySource = toPb(this, PBEnergySource.newBuilder()).build()
fun PowerTransformer.toPb(): PBPowerTransformer = toPb(this, PBPowerTransformer.newBuilder()).build()
fun Disconnector.toPb(): PBDisconnector = toPb(this, PBDisconnector.newBuilder()).build()
fun Fuse.toPb(): PBFuse = toPb(this, PBFuse.newBuilder()).build()
fun Jumper.toPb(): PBJumper = toPb(this, PBJumper.newBuilder()).build()
fun Recloser.toPb(): PBRecloser = toPb(this, PBRecloser.newBuilder()).build()
fun Breaker.toPb(): PBBreaker = toPb(this, PBBreaker.newBuilder()).build()
fun FaultIndicator.toPb(): PBFaultIndicator = toPb(this, PBFaultIndicator.newBuilder()).build()
fun Feeder.toPb(): PBFeeder = toPb(this, PBFeeder.newBuilder()).build()
fun Site.toPb(): PBSite = toPb(this, PBSite.newBuilder()).build()
fun Substation.toPb(): PBSubstation = toPb(this, PBSubstation.newBuilder()).build()
fun EnergySourcePhase.toPb(): PBEnergySourcePhase = toPb(this, PBEnergySourcePhase.newBuilder()).build()
fun EnergyConsumerPhase.toPb(): PBEnergyConsumerPhase = toPb(this, PBEnergyConsumerPhase.newBuilder()).build()
fun RatioTapChanger.toPb(): PBRatioTapChanger = toPb(this, PBRatioTapChanger.newBuilder()).build()
fun GeographicalRegion.toPb(): PBGeographicalRegion = toPb(this, PBGeographicalRegion.newBuilder()).build()
fun SubGeographicalRegion.toPb(): PBSubGeographicalRegion = toPb(this, PBSubGeographicalRegion.newBuilder()).build()
fun Terminal.toPb(): PBTerminal = toPb(this, PBTerminal.newBuilder()).build()
fun PerLengthSequenceImpedance.toPb(): PBPerLengthSequenceImpedance = toPb(this, PBPerLengthSequenceImpedance.newBuilder()).build()
fun PowerTransformerEnd.toPb(): PBPowerTransformerEnd = toPb(this, PBPowerTransformerEnd.newBuilder()).build()
fun PowerTransformerInfo.toPb(): PPowerTransformerInfo = toPb(this, PPowerTransformerInfo.newBuilder()).build()
fun Circuit.toPb(): PBCircuit = toPb(this, PBCircuit.newBuilder()).build()
fun Loop.toPb(): PBLoop = toPb(this, PBLoop.newBuilder()).build()
fun Control.toPb(): PBControl = toPb(this, PBControl.newBuilder()).build()
fun Analog.toPb(): PBAnalog = toPb(this, PBAnalog.newBuilder()).build()
fun Accumulator.toPb(): PBAccumulator = toPb(this, PBAccumulator.newBuilder()).build()
fun Discrete.toPb(): PBDiscrete = toPb(this, PBDiscrete.newBuilder()).build()
fun RemoteControl.toPb(): PBRemoteControl = toPb(this, PBRemoteControl.newBuilder()).build()
fun RemoteSource.toPb(): PBRemoteSource = toPb(this, PBRemoteSource.newBuilder()).build()

/************ Class for Java friendly usage ************/

class NetworkCimToProto : BaseCimToProto() {

    fun toPb(cableInfo: CableInfo): PBCableInfo = cableInfo.toPb()
    fun toPb(overheadWireInfo: OverheadWireInfo): PBOverheadWireInfo = overheadWireInfo.toPb()
    fun toPb(meter: Meter): PBMeter = meter.toPb()
    fun toPb(operationalRestriction: OperationalRestriction): PBOperationalRestriction = operationalRestriction.toPb()
    fun toPb(assetOwner: AssetOwner): PBAssetOwner = assetOwner.toPb()
    fun toPb(pole: Pole): PBPole = pole.toPb()
    fun toPb(streetlight: Streetlight): PBStreetlight = streetlight.toPb()
    fun toPb(location: Location): PBLocation = location.toPb()
    fun toPb(usagePoint: UsagePoint): PBUsagePoint = usagePoint.toPb()
    fun toPb(connectivityNode: ConnectivityNode): PBConnectivityNode = connectivityNode.toPb()
    fun toPb(baseVoltage: BaseVoltage): PBBaseVoltage = baseVoltage.toPb()
    fun toPb(junction: Junction): PBJunction = junction.toPb()
    fun toPb(acLineSegment: AcLineSegment): PBAcLineSegment = acLineSegment.toPb()
    fun toPb(linearShuntCompensator: LinearShuntCompensator): PBLinearShuntCompensator = linearShuntCompensator.toPb()
    fun toPb(energyConsumer: EnergyConsumer): PBEnergyConsumer = energyConsumer.toPb()
    fun toPb(energySource: EnergySource): PBEnergySource = energySource.toPb()
    fun toPb(powerTransformer: PowerTransformer): PBPowerTransformer = powerTransformer.toPb()
    fun toPb(disconnector: Disconnector): PBDisconnector = disconnector.toPb()
    fun toPb(fuse: Fuse): PBFuse = fuse.toPb()
    fun toPb(jumper: Jumper): PBJumper = jumper.toPb()
    fun toPb(recloser: Recloser): PBRecloser = recloser.toPb()
    fun toPb(breaker: Breaker): PBBreaker = breaker.toPb()
    fun toPb(faultIndicator: FaultIndicator): PBFaultIndicator = faultIndicator.toPb()
    fun toPb(feeder: Feeder): PBFeeder = feeder.toPb()
    fun toPb(site: Site): PBSite = site.toPb()
    fun toPb(substation: Substation): PBSubstation = substation.toPb()
    fun toPb(energySourcePhase: EnergySourcePhase): PBEnergySourcePhase = energySourcePhase.toPb()
    fun toPb(energyConsumerPhase: EnergyConsumerPhase): PBEnergyConsumerPhase = energyConsumerPhase.toPb()
    fun toPb(ratioTapChanger: RatioTapChanger): PBRatioTapChanger = ratioTapChanger.toPb()
    fun toPb(geographicalRegion: GeographicalRegion): PBGeographicalRegion = geographicalRegion.toPb()
    fun toPb(subGeographicalRegion: SubGeographicalRegion): PBSubGeographicalRegion = subGeographicalRegion.toPb()
    fun toPb(terminal: Terminal): PBTerminal = terminal.toPb()
    fun toPb(perLengthSequenceImpedance: PerLengthSequenceImpedance): PBPerLengthSequenceImpedance =
        perLengthSequenceImpedance.toPb()

    fun toPb(powerTransformerEnd: PowerTransformerEnd): PBPowerTransformerEnd = powerTransformerEnd.toPb()
    fun toPb(circuit: Circuit): PBCircuit = circuit.toPb()
    fun toPb(loop: Loop): PBLoop = loop.toPb()
    fun toPb(control: Control): PBControl = control.toPb()
    fun toPb(analog: Analog): PBAnalog = analog.toPb()
    fun toPb(accumulator: Accumulator): PBAccumulator = accumulator.toPb()
    fun toPb(discrete: Discrete): PBDiscrete = discrete.toPb()
    fun toPb(remoteControl: RemoteControl): PBRemoteControl = remoteControl.toPb()
    fun toPb(remoteSource: RemoteSource): PBRemoteSource = remoteSource.toPb()
}

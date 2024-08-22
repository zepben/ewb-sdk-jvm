/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.translator

import com.google.protobuf.NullValue
import com.zepben.evolve.cim.iec61968.assetinfo.*
import com.zepben.evolve.cim.iec61968.assets.*
import com.zepben.evolve.cim.iec61968.common.*
import com.zepben.evolve.cim.iec61968.infiec61968.infassetinfo.CurrentTransformerInfo
import com.zepben.evolve.cim.iec61968.infiec61968.infassetinfo.PotentialTransformerInfo
import com.zepben.evolve.cim.iec61968.infiec61968.infassetinfo.RelayInfo
import com.zepben.evolve.cim.iec61968.infiec61968.infcommon.Ratio
import com.zepben.evolve.cim.iec61968.metering.EndDevice
import com.zepben.evolve.cim.iec61968.metering.Meter
import com.zepben.evolve.cim.iec61968.metering.UsagePoint
import com.zepben.evolve.cim.iec61968.operations.OperationalRestriction
import com.zepben.evolve.cim.iec61970.base.auxiliaryequipment.*
import com.zepben.evolve.cim.iec61970.base.core.*
import com.zepben.evolve.cim.iec61970.base.equivalents.EquivalentBranch
import com.zepben.evolve.cim.iec61970.base.equivalents.EquivalentEquipment
import com.zepben.evolve.cim.iec61970.base.meas.*
import com.zepben.evolve.cim.iec61970.base.protection.*
import com.zepben.evolve.cim.iec61970.base.scada.RemoteControl
import com.zepben.evolve.cim.iec61970.base.scada.RemotePoint
import com.zepben.evolve.cim.iec61970.base.scada.RemoteSource
import com.zepben.evolve.cim.iec61970.base.wires.*
import com.zepben.evolve.cim.iec61970.base.wires.generation.production.BatteryUnit
import com.zepben.evolve.cim.iec61970.base.wires.generation.production.PhotoVoltaicUnit
import com.zepben.evolve.cim.iec61970.base.wires.generation.production.PowerElectronicsUnit
import com.zepben.evolve.cim.iec61970.base.wires.generation.production.PowerElectronicsWindUnit
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.Circuit
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.Loop
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.LvFeeder
import com.zepben.evolve.cim.iec61970.infiec61970.wires.generation.production.EvChargingUnit
import com.zepben.evolve.services.common.*
import com.zepben.evolve.services.common.translator.BaseCimToProto
import com.zepben.evolve.services.common.translator.toPb
import com.zepben.evolve.services.common.translator.toTimestamp
import com.zepben.protobuf.cim.iec61968.assetinfo.WireMaterialKind
import com.zepben.protobuf.cim.iec61968.infiec61968.infassetinfo.TransformerConstructionKind
import com.zepben.protobuf.cim.iec61968.infiec61968.infassetinfo.TransformerFunctionKind
import com.zepben.protobuf.cim.iec61970.base.auxiliaryequipment.PotentialTransformerKind
import com.zepben.protobuf.cim.iec61970.base.wires.PhaseShuntConnectionKind
import com.zepben.protobuf.cim.iec61970.base.wires.SinglePhaseKind
import com.zepben.protobuf.cim.iec61970.base.wires.SynchronousMachineKind
import com.zepben.protobuf.cim.iec61970.base.wires.TransformerCoolingType
import com.zepben.protobuf.cim.iec61970.base.wires.VectorGroup
import com.zepben.protobuf.cim.iec61970.base.wires.WindingConnection
import com.zepben.protobuf.cim.iec61970.base.wires.generation.production.BatteryStateKind
import com.zepben.protobuf.cim.iec61970.infiec61970.protection.PowerDirectionKind
import com.zepben.protobuf.network.model.FeederDirection
import com.zepben.protobuf.cim.iec61968.assetinfo.CableInfo as PBCableInfo
import com.zepben.protobuf.cim.iec61968.assetinfo.NoLoadTest as PBNoLoadTest
import com.zepben.protobuf.cim.iec61968.assetinfo.OpenCircuitTest as PBOpenCircuitTest
import com.zepben.protobuf.cim.iec61968.assetinfo.OverheadWireInfo as PBOverheadWireInfo
import com.zepben.protobuf.cim.iec61968.assetinfo.PowerTransformerInfo as PBPowerTransformerInfo
import com.zepben.protobuf.cim.iec61968.assetinfo.ShortCircuitTest as PBShortCircuitTest
import com.zepben.protobuf.cim.iec61968.assetinfo.ShuntCompensatorInfo as PBShuntCompensatorInfo
import com.zepben.protobuf.cim.iec61968.assetinfo.SwitchInfo as PBSwitchInfo
import com.zepben.protobuf.cim.iec61968.assetinfo.TransformerEndInfo as PBTransformerEndInfo
import com.zepben.protobuf.cim.iec61968.assetinfo.TransformerTankInfo as PBTransformerTankInfo
import com.zepben.protobuf.cim.iec61968.assetinfo.TransformerTest as PBTransformerTest
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
import com.zepben.protobuf.cim.iec61968.common.StreetDetail as PBStreetDetail
import com.zepben.protobuf.cim.iec61968.common.TownDetail as PBTownDetail
import com.zepben.protobuf.cim.iec61968.infiec61968.infassetinfo.CurrentTransformerInfo as PBCurrentTransformerInfo
import com.zepben.protobuf.cim.iec61968.infiec61968.infassetinfo.PotentialTransformerInfo as PBPotentialTransformerInfo
import com.zepben.protobuf.cim.iec61968.infiec61968.infassetinfo.RelayInfo as PBRelayInfo
import com.zepben.protobuf.cim.iec61968.infiec61968.infcommon.Ratio as PBRatio
import com.zepben.protobuf.cim.iec61968.metering.EndDevice as PBEndDevice
import com.zepben.protobuf.cim.iec61968.metering.Meter as PBMeter
import com.zepben.protobuf.cim.iec61968.metering.UsagePoint as PBUsagePoint
import com.zepben.protobuf.cim.iec61968.operations.OperationalRestriction as PBOperationalRestriction
import com.zepben.protobuf.cim.iec61970.base.auxiliaryequipment.AuxiliaryEquipment as PBAuxiliaryEquipment
import com.zepben.protobuf.cim.iec61970.base.auxiliaryequipment.CurrentTransformer as PBCurrentTransformer
import com.zepben.protobuf.cim.iec61970.base.auxiliaryequipment.FaultIndicator as PBFaultIndicator
import com.zepben.protobuf.cim.iec61970.base.auxiliaryequipment.PotentialTransformer as PBPotentialTransformer
import com.zepben.protobuf.cim.iec61970.base.auxiliaryequipment.Sensor as PBSensor
import com.zepben.protobuf.cim.iec61970.base.core.AcDcTerminal as PBAcDcTerminal
import com.zepben.protobuf.cim.iec61970.base.core.BaseVoltage as PBBaseVoltage
import com.zepben.protobuf.cim.iec61970.base.core.ConductingEquipment as PBConductingEquipment
import com.zepben.protobuf.cim.iec61970.base.core.ConnectivityNode as PBConnectivityNode
import com.zepben.protobuf.cim.iec61970.base.core.ConnectivityNodeContainer as PBConnectivityNodeContainer
import com.zepben.protobuf.cim.iec61970.base.core.Curve as PBCurve
import com.zepben.protobuf.cim.iec61970.base.core.CurveData as PBCurveData
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
import com.zepben.protobuf.cim.iec61970.base.equivalents.EquivalentBranch as PBEquivalentBranch
import com.zepben.protobuf.cim.iec61970.base.equivalents.EquivalentEquipment as PBEquivalentEquipment
import com.zepben.protobuf.cim.iec61970.base.meas.Accumulator as PBAccumulator
import com.zepben.protobuf.cim.iec61970.base.meas.Analog as PBAnalog
import com.zepben.protobuf.cim.iec61970.base.meas.Control as PBControl
import com.zepben.protobuf.cim.iec61970.base.meas.Discrete as PBDiscrete
import com.zepben.protobuf.cim.iec61970.base.meas.IoPoint as PBIoPoint
import com.zepben.protobuf.cim.iec61970.base.meas.Measurement as PBMeasurement
import com.zepben.protobuf.cim.iec61970.base.protection.CurrentRelay as PBCurrentRelay
import com.zepben.protobuf.cim.iec61970.base.protection.DistanceRelay as PBDistanceRelay
import com.zepben.protobuf.cim.iec61970.base.protection.ProtectionRelayFunction as PBProtectionRelayFunction
import com.zepben.protobuf.cim.iec61970.base.protection.ProtectionRelayScheme as PBProtectionRelayScheme
import com.zepben.protobuf.cim.iec61970.base.protection.ProtectionRelaySystem as PBProtectionRelaySystem
import com.zepben.protobuf.cim.iec61970.base.protection.RelaySetting as PBRelaySetting
import com.zepben.protobuf.cim.iec61970.base.protection.VoltageRelay as PBVoltageRelay
import com.zepben.protobuf.cim.iec61970.base.scada.RemoteControl as PBRemoteControl
import com.zepben.protobuf.cim.iec61970.base.scada.RemotePoint as PBRemotePoint
import com.zepben.protobuf.cim.iec61970.base.scada.RemoteSource as PBRemoteSource
import com.zepben.protobuf.cim.iec61970.base.wires.AcLineSegment as PBAcLineSegment
import com.zepben.protobuf.cim.iec61970.base.wires.Breaker as PBBreaker
import com.zepben.protobuf.cim.iec61970.base.wires.BusbarSection as PBBusbarSection
import com.zepben.protobuf.cim.iec61970.base.wires.Conductor as PBConductor
import com.zepben.protobuf.cim.iec61970.base.wires.Connector as PBConnector
import com.zepben.protobuf.cim.iec61970.base.wires.Disconnector as PBDisconnector
import com.zepben.protobuf.cim.iec61970.base.wires.EarthFaultCompensator as PBEarthFaultCompensator
import com.zepben.protobuf.cim.iec61970.base.wires.EnergyConnection as PBEnergyConnection
import com.zepben.protobuf.cim.iec61970.base.wires.EnergyConsumer as PBEnergyConsumer
import com.zepben.protobuf.cim.iec61970.base.wires.EnergyConsumerPhase as PBEnergyConsumerPhase
import com.zepben.protobuf.cim.iec61970.base.wires.EnergySource as PBEnergySource
import com.zepben.protobuf.cim.iec61970.base.wires.EnergySourcePhase as PBEnergySourcePhase
import com.zepben.protobuf.cim.iec61970.base.wires.Fuse as PBFuse
import com.zepben.protobuf.cim.iec61970.base.wires.Ground as PBGround
import com.zepben.protobuf.cim.iec61970.base.wires.GroundDisconnector as PBGroundDisconnector
import com.zepben.protobuf.cim.iec61970.base.wires.GroundingImpedance as PBGroundingImpedance
import com.zepben.protobuf.cim.iec61970.base.wires.PetersenCoil as PBPetersenCoil
import com.zepben.protobuf.cim.iec61970.base.wires.Jumper as PBJumper
import com.zepben.protobuf.cim.iec61970.base.wires.Junction as PBJunction
import com.zepben.protobuf.cim.iec61970.base.wires.Line as PBLine
import com.zepben.protobuf.cim.iec61970.base.wires.LinearShuntCompensator as PBLinearShuntCompensator
import com.zepben.protobuf.cim.iec61970.base.wires.LoadBreakSwitch as PBLoadBreakSwitch
import com.zepben.protobuf.cim.iec61970.base.wires.PerLengthImpedance as PBPerLengthImpedance
import com.zepben.protobuf.cim.iec61970.base.wires.PerLengthLineParameter as PBPerLengthLineParameter
import com.zepben.protobuf.cim.iec61970.base.wires.PerLengthSequenceImpedance as PBPerLengthSequenceImpedance
import com.zepben.protobuf.cim.iec61970.base.wires.PowerElectronicsConnection as PBPowerElectronicsConnection
import com.zepben.protobuf.cim.iec61970.base.wires.PowerElectronicsConnectionPhase as PBPowerElectronicsConnectionPhase
import com.zepben.protobuf.cim.iec61970.base.wires.PowerTransformer as PBPowerTransformer
import com.zepben.protobuf.cim.iec61970.base.wires.PowerTransformerEnd as PBPowerTransformerEnd
import com.zepben.protobuf.cim.iec61970.base.wires.ProtectedSwitch as PBProtectedSwitch
import com.zepben.protobuf.cim.iec61970.base.wires.RatioTapChanger as PBRatioTapChanger
import com.zepben.protobuf.cim.iec61970.base.wires.RotatingMachine as PBRotatingMachine
import com.zepben.protobuf.cim.iec61970.base.wires.ReactiveCapabilityCurve as PBReactiveCapabilityCurve
import com.zepben.protobuf.cim.iec61970.base.wires.SynchronousMachine as PBSynchronousMachine
import com.zepben.protobuf.cim.iec61970.base.wires.Recloser as PBRecloser
import com.zepben.protobuf.cim.iec61970.base.wires.RegulatingCondEq as PBRegulatingCondEq
import com.zepben.protobuf.cim.iec61970.base.wires.RegulatingControl as PBRegulatingControl
import com.zepben.protobuf.cim.iec61970.base.wires.RegulatingControlModeKind as PBRegulatingControlModeKind
import com.zepben.protobuf.cim.iec61970.base.wires.SeriesCompensator as PBSeriesCompensator
import com.zepben.protobuf.cim.iec61970.base.wires.ShuntCompensator as PBShuntCompensator
import com.zepben.protobuf.cim.iec61970.base.wires.Switch as PBSwitch
import com.zepben.protobuf.cim.iec61970.base.wires.TapChanger as PBTapChanger
import com.zepben.protobuf.cim.iec61970.base.wires.TapChangerControl as PBTapChangerControl
import com.zepben.protobuf.cim.iec61970.base.wires.TransformerEnd as PBTransformerEnd
import com.zepben.protobuf.cim.iec61970.base.wires.TransformerEndRatedS as PBTransformerEndRatedS
import com.zepben.protobuf.cim.iec61970.base.wires.TransformerStarImpedance as PBTransformerStarImpedance
import com.zepben.protobuf.cim.iec61970.base.wires.generation.production.BatteryUnit as PBBatteryUnit
import com.zepben.protobuf.cim.iec61970.base.wires.generation.production.PhotoVoltaicUnit as PBPhotoVoltaicUnit
import com.zepben.protobuf.cim.iec61970.base.wires.generation.production.PowerElectronicsUnit as PBPowerElectronicsUnit
import com.zepben.protobuf.cim.iec61970.base.wires.generation.production.PowerElectronicsWindUnit as PBPowerElectronicsWindUnit
import com.zepben.protobuf.cim.iec61970.infiec61970.feeder.Circuit as PBCircuit
import com.zepben.protobuf.cim.iec61970.infiec61970.feeder.Loop as PBLoop
import com.zepben.protobuf.cim.iec61970.infiec61970.feeder.LvFeeder as PBLvFeeder
import com.zepben.protobuf.cim.iec61970.infiec61970.protection.ProtectionKind as PBProtectionKind
import com.zepben.protobuf.cim.iec61970.infiec61970.wires.generation.production.EvChargingUnit as PBEvChargingUnit

/************ IEC61968 ASSET INFO ************/

fun toPb(cim: CableInfo, pb: PBCableInfo.Builder): PBCableInfo.Builder =
    pb.apply { toPb(cim, wiBuilder) }

fun toPb(cim: OverheadWireInfo, pb: PBOverheadWireInfo.Builder): PBOverheadWireInfo.Builder =
    pb.apply { toPb(cim, wiBuilder) }

fun toPb(cim: NoLoadTest, pb: PBNoLoadTest.Builder): PBNoLoadTest.Builder =
    pb.apply {
        energisedEndVoltage = cim.energisedEndVoltage ?: UNKNOWN_INT
        excitingCurrent = cim.excitingCurrent ?: UNKNOWN_DOUBLE
        excitingCurrentZero = cim.excitingCurrentZero ?: UNKNOWN_DOUBLE
        loss = cim.loss ?: UNKNOWN_INT
        lossZero = cim.lossZero ?: UNKNOWN_INT
        toPb(cim, ttBuilder)
    }

fun toPb(cim: OpenCircuitTest, pb: PBOpenCircuitTest.Builder): PBOpenCircuitTest.Builder =
    pb.apply {
        energisedEndStep = cim.energisedEndStep ?: UNKNOWN_INT
        energisedEndVoltage = cim.energisedEndVoltage ?: UNKNOWN_INT
        openEndStep = cim.openEndStep ?: UNKNOWN_INT
        openEndVoltage = cim.openEndVoltage ?: UNKNOWN_INT
        phaseShift = cim.phaseShift ?: UNKNOWN_DOUBLE
        toPb(cim, ttBuilder)
    }

fun toPb(cim: PowerTransformerInfo, pb: PBPowerTransformerInfo.Builder): PBPowerTransformerInfo.Builder =
    pb.apply {
        clearTransformerTankInfoMRIDs()
        cim.transformerTankInfos.forEach { addTransformerTankInfoMRIDs(it.mRID) }
        toPb(cim, aiBuilder)
    }

fun toPb(cim: ShortCircuitTest, pb: PBShortCircuitTest.Builder): PBShortCircuitTest.Builder =
    pb.apply {
        current = cim.current ?: UNKNOWN_DOUBLE
        energisedEndStep = cim.energisedEndStep ?: UNKNOWN_INT
        groundedEndStep = cim.groundedEndStep ?: UNKNOWN_INT
        leakageImpedance = cim.leakageImpedance ?: UNKNOWN_DOUBLE
        leakageImpedanceZero = cim.leakageImpedanceZero ?: UNKNOWN_DOUBLE
        loss = cim.loss ?: UNKNOWN_INT
        lossZero = cim.lossZero ?: UNKNOWN_INT
        power = cim.power ?: UNKNOWN_INT
        voltage = cim.voltage ?: UNKNOWN_DOUBLE
        voltageOhmicPart = cim.voltageOhmicPart ?: UNKNOWN_DOUBLE
        toPb(cim, ttBuilder)
    }


fun toPb(cim: ShuntCompensatorInfo, pb: PBShuntCompensatorInfo.Builder): PBShuntCompensatorInfo.Builder =
    pb.apply {
        maxPowerLoss = cim.maxPowerLoss ?: UNKNOWN_INT
        ratedCurrent = cim.ratedCurrent ?: UNKNOWN_INT
        ratedReactivePower = cim.ratedReactivePower ?: UNKNOWN_INT
        ratedVoltage = cim.ratedVoltage ?: UNKNOWN_INT

        toPb(cim, aiBuilder)
    }

fun toPb(cim: SwitchInfo, pb: PBSwitchInfo.Builder): PBSwitchInfo.Builder =
    pb.apply {
        ratedInterruptingTime = cim.ratedInterruptingTime ?: UNKNOWN_DOUBLE
        toPb(cim, aiBuilder)
    }

fun toPb(cim: TransformerEndInfo, pb: PBTransformerEndInfo.Builder): PBTransformerEndInfo.Builder =
    pb.apply {
        connectionKind = WindingConnection.valueOf(cim.connectionKind.name)
        emergencyS = cim.emergencyS ?: UNKNOWN_INT
        endNumber = cim.endNumber
        insulationU = cim.insulationU ?: UNKNOWN_INT
        phaseAngleClock = cim.phaseAngleClock ?: UNKNOWN_INT
        r = cim.r ?: UNKNOWN_DOUBLE
        ratedS = cim.ratedS ?: UNKNOWN_INT
        ratedU = cim.ratedU ?: UNKNOWN_INT
        shortTermS = cim.shortTermS ?: UNKNOWN_INT

        cim.transformerTankInfo?.let { transformerTankInfoMRID = it.mRID } ?: clearTransformerTankInfoMRID()
        cim.transformerStarImpedance?.let { transformerStarImpedanceMRID = it.mRID } ?: clearTransformerStarImpedanceMRID()
        cim.energisedEndNoLoadTests?.let { energisedEndNoLoadTestsMRID = it.mRID } ?: clearEnergisedEndNoLoadTestsMRID()
        cim.energisedEndShortCircuitTests?.let { energisedEndShortCircuitTestsMRID = it.mRID } ?: clearEnergisedEndShortCircuitTestsMRID()
        cim.groundedEndShortCircuitTests?.let { groundedEndShortCircuitTestsMRID = it.mRID } ?: clearGroundedEndShortCircuitTestsMRID()
        cim.openEndOpenCircuitTests?.let { openEndOpenCircuitTestsMRID = it.mRID } ?: clearOpenEndOpenCircuitTestsMRID()
        cim.energisedEndOpenCircuitTests?.let { energisedEndOpenCircuitTestsMRID = it.mRID } ?: clearEnergisedEndOpenCircuitTestsMRID()

        toPb(cim, aiBuilder)
    }

fun toPb(cim: TransformerTankInfo, pb: PBTransformerTankInfo.Builder): PBTransformerTankInfo.Builder =
    pb.apply {
        cim.powerTransformerInfo?.let { powerTransformerInfoMRID = it.mRID } ?: clearPowerTransformerInfoMRID()
        clearTransformerEndInfoMRIDs()
        cim.transformerEndInfos.forEach { addTransformerEndInfoMRIDs(it.mRID) }
        toPb(cim, aiBuilder)
    }

fun toPb(cim: TransformerTest, pb: PBTransformerTest.Builder): PBTransformerTest.Builder =
    pb.apply {
        basePower = cim.basePower ?: UNKNOWN_INT
        temperature = cim.temperature ?: UNKNOWN_DOUBLE
        toPb(cim, ioBuilder)
    }

fun toPb(cim: WireInfo, pb: PBWireInfo.Builder): PBWireInfo.Builder =
    pb.apply {
        ratedCurrent = cim.ratedCurrent ?: UNKNOWN_INT
        material = WireMaterialKind.valueOf(cim.material.name)
        toPb(cim, aiBuilder)
    }

fun CableInfo.toPb(): PBCableInfo = toPb(this, PBCableInfo.newBuilder()).build()
fun NoLoadTest.toPb(): PBNoLoadTest = toPb(this, PBNoLoadTest.newBuilder()).build()
fun OpenCircuitTest.toPb(): PBOpenCircuitTest = toPb(this, PBOpenCircuitTest.newBuilder()).build()
fun OverheadWireInfo.toPb(): PBOverheadWireInfo = toPb(this, PBOverheadWireInfo.newBuilder()).build()
fun PowerTransformerInfo.toPb(): PBPowerTransformerInfo = toPb(this, PBPowerTransformerInfo.newBuilder()).build()
fun ShortCircuitTest.toPb(): PBShortCircuitTest = toPb(this, PBShortCircuitTest.newBuilder()).build()
fun ShuntCompensatorInfo.toPb(): PBShuntCompensatorInfo = toPb(this, PBShuntCompensatorInfo.newBuilder()).build()
fun SwitchInfo.toPb(): PBSwitchInfo = toPb(this, PBSwitchInfo.newBuilder()).build()
fun TransformerEndInfo.toPb(): PBTransformerEndInfo = toPb(this, PBTransformerEndInfo.newBuilder()).build()
fun TransformerTankInfo.toPb(): PBTransformerTankInfo = toPb(this, PBTransformerTankInfo.newBuilder()).build()

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
        clearStreetlightMRIDs()
        cim.streetlights.forEach { addStreetlightMRIDs(it.mRID) }
        toPb(cim, stBuilder)
    }

fun toPb(cim: Streetlight, pb: PBStreetlight.Builder): PBStreetlight.Builder =
    pb.apply {
        lightRating = cim.lightRating ?: UNKNOWN_UINT
        lampKind = PBStreetlightLampKind.valueOf(cim.lampKind.name)
        cim.pole?.let { poleMRID = it.mRID } ?: clearPoleMRID()
        toPb(cim, atBuilder)
    }

fun toPb(cim: Structure, pb: PBStructure.Builder): PBStructure.Builder =
    pb.apply { toPb(cim, acBuilder) }

fun AssetOwner.toPb(): PBAssetOwner = toPb(this, PBAssetOwner.newBuilder()).build()
fun Pole.toPb(): PBPole = toPb(this, PBPole.newBuilder()).build()
fun Streetlight.toPb(): PBStreetlight = toPb(this, PBStreetlight.newBuilder()).build()

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
        poBox = cim.poBox
        cim.streetDetail?.let { toPb(it, streetDetailBuilder) } ?: clearStreetDetail()
    }

fun toPb(cim: StreetDetail, pb: PBStreetDetail.Builder): PBStreetDetail.Builder =
    pb.apply {
        buildingName = cim.buildingName
        floorIdentification = cim.floorIdentification
        name = cim.name
        number = cim.number
        suiteNumber = cim.suiteNumber
        type = cim.type
        displayAddress = cim.displayAddress
    }

fun toPb(cim: TownDetail, pb: PBTownDetail.Builder): PBTownDetail.Builder =
    pb.apply {
        cim.name?.let { name = it } ?: clearName()
        cim.stateOrProvince?.let { stateOrProvince = it } ?: clearStateOrProvince()
    }

fun Location.toPb(): PBLocation = toPb(this, PBLocation.newBuilder()).build()

/************ IEC61968 infIEC61968 InfAssetInfo ************/

fun toPb(cim: RelayInfo, pb: PBRelayInfo.Builder): PBRelayInfo.Builder =
    pb.apply {
        cim.curveSetting?.let { curveSetting = it } ?: clearCurveSetting()
        cim.recloseFast?.let { recloseFastSet = it } ?: run { recloseFastNull = NullValue.NULL_VALUE }
        cim.recloseDelays.forEach { addRecloseDelays(it) }
        toPb(cim, aiBuilder)
    }

fun toPb(cim: CurrentTransformerInfo, pb: PBCurrentTransformerInfo.Builder): PBCurrentTransformerInfo.Builder =
    pb.apply {
        cim.accuracyClass?.let { accuracyClass = it } ?: clearAccuracyClass()
        accuracyLimit = cim.accuracyLimit ?: UNKNOWN_DOUBLE
        coreCount = cim.coreCount ?: UNKNOWN_INT
        cim.ctClass?.let { ctClass = it } ?: clearCtClass()
        kneePointVoltage = cim.kneePointVoltage ?: UNKNOWN_INT
        cim.maxRatio?.let { toPb(it, maxRatioBuilder) } ?: clearMaxRatio()
        cim.nominalRatio?.let { toPb(it, nominalRatioBuilder) } ?: clearNominalRatio()
        primaryRatio = cim.primaryRatio ?: UNKNOWN_DOUBLE
        ratedCurrent = cim.ratedCurrent ?: UNKNOWN_INT
        secondaryFlsRating = cim.secondaryFlsRating ?: UNKNOWN_INT
        secondaryRatio = cim.secondaryRatio ?: UNKNOWN_DOUBLE
        cim.usage?.let { usage = it } ?: clearUsage()
        toPb(cim, aiBuilder)
    }

fun toPb(cim: PotentialTransformerInfo, pb: PBPotentialTransformerInfo.Builder): PBPotentialTransformerInfo.Builder =
    pb.apply {
        cim.accuracyClass?.let { accuracyClass = it } ?: clearAccuracyClass()
        cim.nominalRatio?.let { toPb(it, nominalRatioBuilder) } ?: clearNominalRatio()
        primaryRatio = cim.primaryRatio ?: UNKNOWN_DOUBLE
        cim.ptClass?.let { ptClass = it } ?: clearPtClass()
        ratedVoltage = cim.ratedVoltage ?: UNKNOWN_INT
        secondaryRatio = cim.secondaryRatio ?: UNKNOWN_DOUBLE
        toPb(cim, aiBuilder)
    }

fun RelayInfo.toPb(): PBRelayInfo = toPb(this, PBRelayInfo.newBuilder()).build()
fun CurrentTransformerInfo.toPb(): PBCurrentTransformerInfo = toPb(this, PBCurrentTransformerInfo.newBuilder()).build()
fun PotentialTransformerInfo.toPb(): PBPotentialTransformerInfo = toPb(this, PBPotentialTransformerInfo.newBuilder()).build()

/************ IEC61968 infIEC61968 InfCommon ************/

fun toPb(cim: Ratio, pb: PBRatio.Builder): PBRatio.Builder =
    pb.apply {
        denominator = cim.denominator
        numerator = cim.numerator
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
        isVirtual = cim.isVirtual
        ratedPower = cim.ratedPower ?: UNKNOWN_INT
        approvedInverterCapacity = cim.approvedInverterCapacity ?: UNKNOWN_INT
        cim.connectionCategory?.let { connectionCategory = it } ?: clearConnectionCategory()
        clearEquipmentMRIDs()
        cim.equipment.forEach { addEquipmentMRIDs(it.mRID) }
        clearEndDeviceMRIDs()
        cim.endDevices.forEach { addEndDeviceMRIDs(it.mRID) }
        toPb(cim, ioBuilder)
    }

fun Meter.toPb(): PBMeter = toPb(this, PBMeter.newBuilder()).build()
fun UsagePoint.toPb(): PBUsagePoint = toPb(this, PBUsagePoint.newBuilder()).build()

/************ IEC61968 OPERATIONS ************/

fun toPb(cim: OperationalRestriction, pb: PBOperationalRestriction.Builder): PBOperationalRestriction.Builder =
    pb.apply { toPb(cim, docBuilder) }

fun OperationalRestriction.toPb(): PBOperationalRestriction = toPb(this, PBOperationalRestriction.newBuilder()).build()

/************ IEC61970 BASE AUXILIARY EQUIPMENT ************/

fun toPb(cim: AuxiliaryEquipment, pb: PBAuxiliaryEquipment.Builder): PBAuxiliaryEquipment.Builder =
    pb.apply {
        cim.terminal?.let { terminalMRID = it.mRID } ?: clearTerminalMRID()
        toPb(cim, eqBuilder)
    }

fun toPb(cim: CurrentTransformer, pb: PBCurrentTransformer.Builder): PBCurrentTransformer.Builder =
    pb.apply {
        coreBurden = cim.coreBurden ?: UNKNOWN_INT
        toPb(cim, snBuilder)
    }

fun toPb(cim: FaultIndicator, pb: PBFaultIndicator.Builder): PBFaultIndicator.Builder =
    pb.apply { toPb(cim, aeBuilder) }

fun toPb(cim: PotentialTransformer, pb: PBPotentialTransformer.Builder): PBPotentialTransformer.Builder =
    pb.apply {
        type = PotentialTransformerKind.valueOf(cim.type.name)
        toPb(cim, snBuilder)
    }

fun toPb(cim: Sensor, pb: PBSensor.Builder): PBSensor.Builder =
    pb.apply {
        cim.relayFunctions.forEach { addRelayFunctionMRIDs(it.mRID) }
        toPb(cim, aeBuilder)
    }

fun CurrentTransformer.toPb(): PBCurrentTransformer = toPb(this, PBCurrentTransformer.newBuilder()).build()
fun FaultIndicator.toPb(): PBFaultIndicator = toPb(this, PBFaultIndicator.newBuilder()).build()
fun PotentialTransformer.toPb(): PBPotentialTransformer = toPb(this, PBPotentialTransformer.newBuilder()).build()

/************ IEC61970 BASE CORE ************/

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
    pb.apply { toPb(cim, ioBuilder) }

fun toPb(cim: ConnectivityNodeContainer, pb: PBConnectivityNodeContainer.Builder): PBConnectivityNodeContainer.Builder =
    pb.apply { toPb(cim, psrBuilder) }

fun toPb(cim: Curve, pb: PBCurve.Builder): PBCurve.Builder =
    pb.apply {
        cim.data.forEachIndexed { i, data -> addCurveDataBuilder(i).apply { toPb(data, this) } }
        toPb(cim, ioBuilder)
    }

fun toPb(cim: CurveData, pb: PBCurveData.Builder): PBCurveData.Builder =
    pb.apply {
        xvalue = cim.xValue
        y1Value = cim.y1Value
        cim.y2Value?.let { y2Value = it }
        cim.y3Value?.let { y3Value = it }
    }

fun toPb(cim: Equipment, pb: PBEquipment.Builder): PBEquipment.Builder =
    pb.apply {
        inService = cim.inService
        normallyInService = cim.normallyInService

        cim.commissionedDate.toTimestamp()?.let { commissionedDate = it } ?: clearCommissionedDate()

        clearEquipmentContainerMRIDs()
        cim.containers.forEach { addEquipmentContainerMRIDs(it.mRID) }

        clearUsagePointMRIDs()
        cim.usagePoints.forEach { addUsagePointMRIDs(it.mRID) }

        clearOperationalRestrictionMRIDs()
        cim.operationalRestrictions.forEach { addOperationalRestrictionMRIDs(it.mRID) }

        clearCurrentContainerMRIDs()
        cim.currentContainers.forEach { addCurrentContainerMRIDs(it.mRID) }

        toPb(cim, psrBuilder)
    }

fun toPb(cim: EquipmentContainer, pb: PBEquipmentContainer.Builder): PBEquipmentContainer.Builder =
    pb.apply {
        toPb(cim, cncBuilder)
    }

fun toPb(cim: Feeder, pb: PBFeeder.Builder): PBFeeder.Builder =
    pb.apply {
        cim.normalHeadTerminal?.let { normalHeadTerminalMRID = it.mRID } ?: clearNormalHeadTerminalMRID()
        cim.normalEnergizingSubstation?.let { normalEnergizingSubstationMRID = it.mRID } ?: clearNormalEnergizingSubstationMRID()

        clearNormalEnergizedLvFeederMRIDs()
        cim.normalEnergizedLvFeeders.forEach { addNormalEnergizedLvFeederMRIDs(it.mRID) }

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
        cim.connectivityNodeId?.let { connectivityNodeMRID = it } ?: clearConnectivityNodeMRID()
        phases = PBPhaseCode.valueOf(cim.phases.name)
        sequenceNumber = cim.sequenceNumber
        normalFeederDirection = FeederDirection.valueOf(cim.normalFeederDirection.name)
        currentFeederDirection = FeederDirection.valueOf(cim.currentFeederDirection.name)
        tracedPhases = cim.tracedPhases.phaseStatusInternal.toInt()
        toPb(cim, adBuilder)
    }

fun BaseVoltage.toPb(): PBBaseVoltage = toPb(this, PBBaseVoltage.newBuilder()).build()
fun ConnectivityNode.toPb(): PBConnectivityNode = toPb(this, PBConnectivityNode.newBuilder()).build()
fun Feeder.toPb(): PBFeeder = toPb(this, PBFeeder.newBuilder()).build()
fun GeographicalRegion.toPb(): PBGeographicalRegion = toPb(this, PBGeographicalRegion.newBuilder()).build()
fun Site.toPb(): PBSite = toPb(this, PBSite.newBuilder()).build()
fun SubGeographicalRegion.toPb(): PBSubGeographicalRegion = toPb(this, PBSubGeographicalRegion.newBuilder()).build()
fun Substation.toPb(): PBSubstation = toPb(this, PBSubstation.newBuilder()).build()
fun Terminal.toPb(): PBTerminal = toPb(this, PBTerminal.newBuilder()).build()

/************ IEC61970 BASE EQUIVALENTS ************/

fun toPb(cim: EquivalentBranch, pb: PBEquivalentBranch.Builder): PBEquivalentBranch.Builder =
    pb.apply {
        negativeR12 = cim.negativeR12 ?: UNKNOWN_DOUBLE
        negativeR21 = cim.negativeR21 ?: UNKNOWN_DOUBLE
        negativeX12 = cim.negativeX12 ?: UNKNOWN_DOUBLE
        negativeX21 = cim.negativeX21 ?: UNKNOWN_DOUBLE
        positiveR12 = cim.positiveR12 ?: UNKNOWN_DOUBLE
        positiveR21 = cim.positiveR21 ?: UNKNOWN_DOUBLE
        positiveX12 = cim.positiveX12 ?: UNKNOWN_DOUBLE
        positiveX21 = cim.positiveX21 ?: UNKNOWN_DOUBLE
        r = cim.r ?: UNKNOWN_DOUBLE
        r21 = cim.r21 ?: UNKNOWN_DOUBLE
        x = cim.x ?: UNKNOWN_DOUBLE
        x21 = cim.x21 ?: UNKNOWN_DOUBLE
        zeroR12 = cim.zeroR12 ?: UNKNOWN_DOUBLE
        zeroR21 = cim.zeroR21 ?: UNKNOWN_DOUBLE
        zeroX12 = cim.zeroX12 ?: UNKNOWN_DOUBLE
        zeroX21 = cim.zeroX21 ?: UNKNOWN_DOUBLE
        toPb(cim, eeBuilder)
    }

fun toPb(cim: EquivalentEquipment, pb: PBEquivalentEquipment.Builder): PBEquivalentEquipment.Builder =
    pb.apply { toPb(cim, ceBuilder) }

fun EquivalentBranch.toPb(): PBEquivalentBranch = toPb(this, PBEquivalentBranch.newBuilder()).build()

/************ IEC61970 BASE MEAS ************/

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

fun Accumulator.toPb(): PBAccumulator = toPb(this, PBAccumulator.newBuilder()).build()
fun Analog.toPb(): PBAnalog = toPb(this, PBAnalog.newBuilder()).build()
fun Control.toPb(): PBControl = toPb(this, PBControl.newBuilder()).build()
fun Discrete.toPb(): PBDiscrete = toPb(this, PBDiscrete.newBuilder()).build()

/************ IEC61970 Base Protection ************/

fun toPb(cim: CurrentRelay, pb: PBCurrentRelay.Builder): PBCurrentRelay.Builder =
    pb.apply {
        currentLimit1 = cim.currentLimit1 ?: UNKNOWN_DOUBLE
        cim.inverseTimeFlag?.let { inverseTimeFlagSet = it } ?: run { inverseTimeFlagNull = NullValue.NULL_VALUE }
        timeDelay1 = cim.timeDelay1 ?: UNKNOWN_DOUBLE
        toPb(cim, prfBuilder)
    }

fun toPb(cim: DistanceRelay, pb: PBDistanceRelay.Builder): PBDistanceRelay.Builder =
    pb.apply {
        backwardBlind = cim.backwardBlind ?: UNKNOWN_DOUBLE
        backwardReach = cim.backwardReach ?: UNKNOWN_DOUBLE
        backwardReactance = cim.backwardReactance ?: UNKNOWN_DOUBLE
        forwardBlind = cim.forwardBlind ?: UNKNOWN_DOUBLE
        forwardReach = cim.forwardReach ?: UNKNOWN_DOUBLE
        forwardReactance = cim.forwardReactance ?: UNKNOWN_DOUBLE
        operationPhaseAngle1 = cim.operationPhaseAngle1 ?: UNKNOWN_DOUBLE
        operationPhaseAngle2 = cim.operationPhaseAngle2 ?: UNKNOWN_DOUBLE
        operationPhaseAngle3 = cim.operationPhaseAngle3 ?: UNKNOWN_DOUBLE
        toPb(cim, prfBuilder)
    }

fun toPb(cim: ProtectionRelayFunction, pb: PBProtectionRelayFunction.Builder): PBProtectionRelayFunction.Builder =
    pb.apply {
        cim.model?.let { model = it } ?: clearModel()
        cim.reclosing?.also { reclosingSet = it } ?: run { reclosingNull = NullValue.NULL_VALUE }
        relayDelayTime = cim.relayDelayTime ?: UNKNOWN_DOUBLE
        protectionKind = PBProtectionKind.valueOf(cim.protectionKind.name)
        cim.directable?.also { directableSet = it } ?: run { directableNull = NullValue.NULL_VALUE }
        powerDirection = PowerDirectionKind.valueOf(cim.powerDirection.name)
        cim.timeLimits.forEach { addTimeLimits(it) }
        cim.thresholds.forEach { addThresholds(toPb(it)) }
        cim.protectedSwitches.forEach { addProtectedSwitchMRIDs(it.mRID) }
        cim.sensors.forEach { addSensorMRIDs(it.mRID) }
        cim.schemes.forEach { addSchemeMRIDs(it.mRID) }
        toPb(cim, psrBuilder)
    }

fun toPb(cim: ProtectionRelayScheme, pb: PBProtectionRelayScheme.Builder): PBProtectionRelayScheme.Builder =
    pb.apply {
        cim.system?.let { systemMRID = it.mRID } ?: clearSystemMRID()
        cim.functions.forEach { addFunctionMRIDs(it.mRID) }
        toPb(cim, ioBuilder)
    }

fun toPb(cim: ProtectionRelaySystem, pb: PBProtectionRelaySystem.Builder): PBProtectionRelaySystem.Builder =
    pb.apply {
        protectionKind = PBProtectionKind.valueOf(cim.protectionKind.name)
        cim.schemes.forEach { addSchemeMRIDs(it.mRID) }
        toPb(cim, eqBuilder)
    }

fun toPb(cim: RelaySetting): PBRelaySetting.Builder =
    PBRelaySetting.newBuilder().apply {
        unitSymbol = PBUnitSymbol.valueOf(cim.unitSymbol.name)
        value = cim.value
        cim.name?.let { name = it } ?: clearName()
    }

fun toPb(cim: VoltageRelay, pb: PBVoltageRelay.Builder): PBVoltageRelay.Builder =
    pb.apply {
        toPb(cim, prfBuilder)
    }

fun CurrentRelay.toPb(): PBCurrentRelay = toPb(this, PBCurrentRelay.newBuilder()).build()
fun DistanceRelay.toPb(): PBDistanceRelay = toPb(this, PBDistanceRelay.newBuilder()).build()
fun ProtectionRelayScheme.toPb(): PBProtectionRelayScheme = toPb(this, PBProtectionRelayScheme.newBuilder()).build()
fun ProtectionRelaySystem.toPb(): PBProtectionRelaySystem = toPb(this, PBProtectionRelaySystem.newBuilder()).build()
fun VoltageRelay.toPb(): PBVoltageRelay = toPb(this, PBVoltageRelay.newBuilder()).build()

/************ IEC61970 BASE SCADA ************/

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
        toPb(cim, rpBuilder)
    }

fun RemoteControl.toPb(): PBRemoteControl = toPb(this, PBRemoteControl.newBuilder()).build()
fun RemoteSource.toPb(): PBRemoteSource = toPb(this, PBRemoteSource.newBuilder()).build()

/************ IEC61970 BASE WIRES GENERATION PRODUCTION ************/

fun toPb(cim: PowerElectronicsUnit, pb: PBPowerElectronicsUnit.Builder): PBPowerElectronicsUnit.Builder =
    pb.apply {
        cim.powerElectronicsConnection?.let { powerElectronicsConnectionMRID = it.mRID } ?: clearPowerElectronicsConnectionMRID()
        maxP = cim.maxP ?: UNKNOWN_INT
        minP = cim.minP ?: UNKNOWN_INT
        toPb(cim, eqBuilder)
    }

fun toPb(cim: BatteryUnit, pb: PBBatteryUnit.Builder): PBBatteryUnit.Builder =
    pb.apply {
        batteryState = BatteryStateKind.valueOf(cim.batteryState.name)
        ratedE = cim.ratedE ?: UNKNOWN_LONG
        storedE = cim.storedE ?: UNKNOWN_LONG
        toPb(cim, peuBuilder)
    }

fun toPb(cim: PhotoVoltaicUnit, pb: PBPhotoVoltaicUnit.Builder): PBPhotoVoltaicUnit.Builder =
    pb.apply {
        toPb(cim, peuBuilder)
    }

fun toPb(cim: PowerElectronicsWindUnit, pb: PBPowerElectronicsWindUnit.Builder): PBPowerElectronicsWindUnit.Builder =
    pb.apply {
        toPb(cim, peuBuilder)
    }

fun BatteryUnit.toPb(): PBBatteryUnit = toPb(this, PBBatteryUnit.newBuilder()).build()
fun PhotoVoltaicUnit.toPb(): PBPhotoVoltaicUnit = toPb(this, PBPhotoVoltaicUnit.newBuilder()).build()
fun PowerElectronicsWindUnit.toPb(): PBPowerElectronicsWindUnit = toPb(this, PBPowerElectronicsWindUnit.newBuilder()).build()

/************ IEC61970 BASE WIRES ************/

fun toPb(cim: AcLineSegment, pb: PBAcLineSegment.Builder): PBAcLineSegment.Builder =
    pb.apply {
        cim.perLengthSequenceImpedance?.let { perLengthSequenceImpedanceMRID = it.mRID } ?: clearPerLengthSequenceImpedanceMRID()
        toPb(cim, cdBuilder)
    }

fun toPb(cim: Breaker, pb: PBBreaker.Builder): PBBreaker.Builder =
    pb.apply {
        inTransitTime = cim.inTransitTime ?: UNKNOWN_DOUBLE
        toPb(cim, swBuilder)
    }

fun toPb(cim: LoadBreakSwitch, pb: PBLoadBreakSwitch.Builder): PBLoadBreakSwitch.Builder =
    pb.apply { toPb(cim, psBuilder) }

fun toPb(cim: BusbarSection, pb: PBBusbarSection.Builder): PBBusbarSection.Builder =
    pb.apply { toPb(cim, cnBuilder) }

fun toPb(cim: Conductor, pb: PBConductor.Builder): PBConductor.Builder =
    pb.apply {
        length = cim.length ?: UNKNOWN_DOUBLE
        designTemperature = cim.designTemperature ?: UNKNOWN_INT
        designRating = cim.designRating ?: UNKNOWN_DOUBLE
        toPb(cim, ceBuilder)
    }

fun toPb(cim: Connector, pb: PBConnector.Builder): PBConnector.Builder =
    pb.apply { toPb(cim, ceBuilder) }

fun toPb(cim: Disconnector, pb: PBDisconnector.Builder): PBDisconnector.Builder =
    pb.apply { toPb(cim, swBuilder) }

fun toPb(cim: EarthFaultCompensator, pb: PBEarthFaultCompensator.Builder): PBEarthFaultCompensator.Builder =
    pb.apply {
        r = cim.r ?: UNKNOWN_DOUBLE
        toPb(cim, ceBuilder)
    }

fun toPb(cim: EnergyConnection, pb: PBEnergyConnection.Builder): PBEnergyConnection.Builder =
    pb.apply { toPb(cim, ceBuilder) }

fun toPb(cim: EnergyConsumer, pb: PBEnergyConsumer.Builder): PBEnergyConsumer.Builder =
    pb.apply {
        clearEnergyConsumerPhasesMRIDs()
        cim.phases.forEach { addEnergyConsumerPhasesMRIDs(it.mRID) }
        customerCount = cim.customerCount ?: UNKNOWN_INT
        grounded = cim.grounded
        p = cim.p ?: UNKNOWN_DOUBLE
        pFixed = cim.pFixed ?: UNKNOWN_DOUBLE
        phaseConnection = PhaseShuntConnectionKind.Enum.valueOf(cim.phaseConnection.name)
        q = cim.q ?: UNKNOWN_DOUBLE
        qFixed = cim.qFixed ?: UNKNOWN_DOUBLE
        toPb(cim, ecBuilder)
    }

fun toPb(cim: EnergyConsumerPhase, pb: PBEnergyConsumerPhase.Builder): PBEnergyConsumerPhase.Builder =
    pb.apply {
        cim.energyConsumer?.let { energyConsumerMRID = it.mRID } ?: clearEnergyConsumerMRID()
        phase = SinglePhaseKind.valueOf(cim.phase.name)
        p = cim.p ?: UNKNOWN_DOUBLE
        pFixed = cim.pFixed ?: UNKNOWN_DOUBLE
        q = cim.q ?: UNKNOWN_DOUBLE
        qFixed = cim.qFixed ?: UNKNOWN_DOUBLE
        toPb(cim, psrBuilder)
    }

fun toPb(cim: EnergySource, pb: PBEnergySource.Builder): PBEnergySource.Builder =
    pb.apply {
        clearEnergySourcePhasesMRIDs()
        cim.phases.forEach { addEnergySourcePhasesMRIDs(it.mRID) }

        activePower = cim.activePower ?: UNKNOWN_DOUBLE
        reactivePower = cim.reactivePower ?: UNKNOWN_DOUBLE
        voltageAngle = cim.voltageAngle ?: UNKNOWN_DOUBLE
        voltageMagnitude = cim.voltageMagnitude ?: UNKNOWN_DOUBLE
        r = cim.r ?: UNKNOWN_DOUBLE
        x = cim.x ?: UNKNOWN_DOUBLE
        pMax = cim.pMax ?: UNKNOWN_DOUBLE
        pMin = cim.pMin ?: UNKNOWN_DOUBLE
        r0 = cim.r0 ?: UNKNOWN_DOUBLE
        rn = cim.rn ?: UNKNOWN_DOUBLE
        x0 = cim.x0 ?: UNKNOWN_DOUBLE
        xn = cim.xn ?: UNKNOWN_DOUBLE
        isExternalGrid = cim.isExternalGrid
        rMin = cim.rMin ?: UNKNOWN_DOUBLE
        rnMin = cim.rnMin ?: UNKNOWN_DOUBLE
        r0Min = cim.r0Min ?: UNKNOWN_DOUBLE
        xMin = cim.xMin ?: UNKNOWN_DOUBLE
        xnMin = cim.xnMin ?: UNKNOWN_DOUBLE
        x0Min = cim.x0Min ?: UNKNOWN_DOUBLE
        rMax = cim.rMax ?: UNKNOWN_DOUBLE
        rnMax = cim.rnMax ?: UNKNOWN_DOUBLE
        r0Max = cim.r0Max ?: UNKNOWN_DOUBLE
        xMax = cim.xMax ?: UNKNOWN_DOUBLE
        xnMax = cim.xnMax ?: UNKNOWN_DOUBLE
        x0Max = cim.x0Max ?: UNKNOWN_DOUBLE

        toPb(cim, ecBuilder)
    }

fun toPb(cim: EnergySourcePhase, pb: PBEnergySourcePhase.Builder): PBEnergySourcePhase.Builder =
    pb.apply {
        cim.energySource?.let { energySourceMRID = it.mRID } ?: clearEnergySourceMRID()
        phase = SinglePhaseKind.valueOf(cim.phase.name)
        toPb(cim, psrBuilder)
    }

fun toPb(cim: Fuse, pb: PBFuse.Builder): PBFuse.Builder =
    pb.apply {
        cim.function?.let { functionMRID = it.mRID } ?: clearFunctionMRID()
        toPb(cim, swBuilder)
    }

fun toPb(cim: Ground, pb: PBGround.Builder): PBGround.Builder =
    pb.apply { toPb(cim, ceBuilder) }

fun toPb(cim: GroundDisconnector, pb: PBGroundDisconnector.Builder): PBGroundDisconnector.Builder =
    pb.apply { toPb(cim, swBuilder) }

fun toPb(cim: GroundingImpedance, pb: PBGroundingImpedance.Builder): PBGroundingImpedance.Builder =
    pb.apply {
        x = cim.x ?: UNKNOWN_DOUBLE
        toPb(cim, efcBuilder)
    }

fun toPb(cim: PetersenCoil, pb: PBPetersenCoil.Builder): PBPetersenCoil.Builder =
    pb.apply {
        xGroundNominal = cim.xGroundNominal ?: UNKNOWN_DOUBLE
        toPb(cim, efcBuilder)
    }

fun toPb(cim: Jumper, pb: PBJumper.Builder): PBJumper.Builder =
    pb.apply { toPb(cim, swBuilder) }

fun toPb(cim: Junction, pb: PBJunction.Builder): PBJunction.Builder =
    pb.apply { toPb(cim, cnBuilder) }

fun toPb(cim: Line, pb: PBLine.Builder): PBLine.Builder =
    pb.apply { toPb(cim, ecBuilder) }

fun toPb(cim: LinearShuntCompensator, pb: PBLinearShuntCompensator.Builder): PBLinearShuntCompensator.Builder =
    pb.apply {
        b0PerSection = cim.b0PerSection ?: UNKNOWN_DOUBLE
        bPerSection = cim.bPerSection ?: UNKNOWN_DOUBLE
        g0PerSection = cim.g0PerSection ?: UNKNOWN_DOUBLE
        gPerSection = cim.gPerSection ?: UNKNOWN_DOUBLE
        toPb(cim, scBuilder)
    }

fun toPb(cim: PerLengthLineParameter, pb: PBPerLengthLineParameter.Builder): PBPerLengthLineParameter.Builder =
    pb.apply { toPb(cim, ioBuilder) }

fun toPb(cim: PerLengthImpedance, pb: PBPerLengthImpedance.Builder): PBPerLengthImpedance.Builder =
    pb.apply { toPb(cim, lpBuilder) }

fun toPb(cim: PerLengthSequenceImpedance, pb: PBPerLengthSequenceImpedance.Builder): PBPerLengthSequenceImpedance.Builder =
    pb.apply {
        r = cim.r ?: UNKNOWN_DOUBLE
        x = cim.x ?: UNKNOWN_DOUBLE
        r0 = cim.r0 ?: UNKNOWN_DOUBLE
        x0 = cim.x0 ?: UNKNOWN_DOUBLE
        bch = cim.bch ?: UNKNOWN_DOUBLE
        gch = cim.gch ?: UNKNOWN_DOUBLE
        b0Ch = cim.b0ch ?: UNKNOWN_DOUBLE
        g0Ch = cim.g0ch ?: UNKNOWN_DOUBLE
        toPb(cim, pliBuilder)
    }

fun toPb(cim: PowerElectronicsConnection, pb: PBPowerElectronicsConnection.Builder): PBPowerElectronicsConnection.Builder =
    pb.apply {
        clearPowerElectronicsUnitMRIDs()
        cim.units.forEach { addPowerElectronicsUnitMRIDs(it.mRID) }

        clearPowerElectronicsConnectionPhaseMRIDs()
        cim.phases.forEach { addPowerElectronicsConnectionPhaseMRIDs(it.mRID) }

        maxIFault = cim.maxIFault ?: UNKNOWN_INT
        maxQ = cim.maxQ ?: UNKNOWN_DOUBLE
        minQ = cim.minQ ?: UNKNOWN_DOUBLE
        p = cim.p ?: UNKNOWN_DOUBLE
        q = cim.q ?: UNKNOWN_DOUBLE
        ratedS = cim.ratedS ?: UNKNOWN_INT
        ratedU = cim.ratedU ?: UNKNOWN_INT
        cim.inverterStandard?.also { inverterStandard = it } ?: clearInverterStandard()
        sustainOpOvervoltLimit = cim.sustainOpOvervoltLimit ?: UNKNOWN_INT
        stopAtOverFreq = cim.stopAtOverFreq ?: UNKNOWN_FLOAT
        stopAtUnderFreq = cim.stopAtUnderFreq ?: UNKNOWN_FLOAT
        cim.invVoltWattRespMode?.let { invVoltWattRespModeSet = it } ?: run { invVoltWattRespModeNull = NullValue.NULL_VALUE }
        invWattRespV1 = cim.invWattRespV1 ?: UNKNOWN_INT
        invWattRespV2 = cim.invWattRespV2 ?: UNKNOWN_INT
        invWattRespV3 = cim.invWattRespV3 ?: UNKNOWN_INT
        invWattRespV4 = cim.invWattRespV4 ?: UNKNOWN_INT
        invWattRespPAtV1 = cim.invWattRespPAtV1 ?: UNKNOWN_FLOAT
        invWattRespPAtV2 = cim.invWattRespPAtV2 ?: UNKNOWN_FLOAT
        invWattRespPAtV3 = cim.invWattRespPAtV3 ?: UNKNOWN_FLOAT
        invWattRespPAtV4 = cim.invWattRespPAtV4 ?: UNKNOWN_FLOAT
        cim.invVoltVarRespMode?.let { invVoltVarRespModeSet = it } ?: run { invVoltVarRespModeNull = NullValue.NULL_VALUE }
        invVarRespV1 = cim.invVarRespV1 ?: UNKNOWN_INT
        invVarRespV2 = cim.invVarRespV2 ?: UNKNOWN_INT
        invVarRespV3 = cim.invVarRespV3 ?: UNKNOWN_INT
        invVarRespV4 = cim.invVarRespV4 ?: UNKNOWN_INT
        invVarRespQAtV1 = cim.invVarRespQAtV1 ?: UNKNOWN_FLOAT
        invVarRespQAtV2 = cim.invVarRespQAtV2 ?: UNKNOWN_FLOAT
        invVarRespQAtV3 = cim.invVarRespQAtV3 ?: UNKNOWN_FLOAT
        invVarRespQAtV4 = cim.invVarRespQAtV4 ?: UNKNOWN_FLOAT
        cim.invReactivePowerMode?.let { invReactivePowerModeSet = it } ?: run { invReactivePowerModeNull = NullValue.NULL_VALUE }
        invFixReactivePower = cim.invFixReactivePower ?: UNKNOWN_FLOAT
        toPb(cim, rceBuilder)
    }

fun toPb(cim: PowerElectronicsConnectionPhase, pb: PBPowerElectronicsConnectionPhase.Builder): PBPowerElectronicsConnectionPhase.Builder =
    pb.apply {
        cim.powerElectronicsConnection?.let { powerElectronicsConnectionMRID = it.mRID } ?: clearPowerElectronicsConnectionMRID()
        p = cim.p ?: UNKNOWN_DOUBLE
        phase = SinglePhaseKind.valueOf(cim.phase.name)
        q = cim.q ?: UNKNOWN_DOUBLE
        toPb(cim, psrBuilder)
    }

fun toPb(cim: PowerTransformer, pb: PBPowerTransformer.Builder): PBPowerTransformer.Builder =
    pb.apply {
        clearPowerTransformerEndMRIDs()
        cim.ends.forEach { addPowerTransformerEndMRIDs(it.mRID) }
        vectorGroup = VectorGroup.valueOf(cim.vectorGroup.name)
        transformerUtilisation = cim.transformerUtilisation ?: UNKNOWN_DOUBLE
        constructionKind = TransformerConstructionKind.valueOf(cim.constructionKind.name)
        function = TransformerFunctionKind.valueOf(cim.function.name)
        toPb(cim, ceBuilder)
    }

fun toPb(cim: PowerTransformerEnd, pb: PBPowerTransformerEnd.Builder): PBPowerTransformerEnd.Builder =
    pb.apply {
        cim.powerTransformer?.let { powerTransformerMRID = it.mRID } ?: clearPowerTransformerMRID()
        cim.sRatings.forEach { addRatings(toPb(it)) }
        ratedU = cim.ratedU ?: UNKNOWN_INT
        r = cim.r ?: UNKNOWN_DOUBLE
        r0 = cim.r0 ?: UNKNOWN_DOUBLE
        x = cim.x ?: UNKNOWN_DOUBLE
        x0 = cim.x0 ?: UNKNOWN_DOUBLE
        connectionKind = WindingConnection.valueOf(cim.connectionKind.name)
        b = cim.b ?: UNKNOWN_DOUBLE
        b0 = cim.b0 ?: UNKNOWN_DOUBLE
        g = cim.g ?: UNKNOWN_DOUBLE
        g0 = cim.g0 ?: UNKNOWN_DOUBLE
        phaseAngleClock = cim.phaseAngleClock ?: UNKNOWN_INT
        toPb(cim, teBuilder)
    }

fun toPb(cim: RotatingMachine, pb: PBRotatingMachine.Builder): PBRotatingMachine.Builder =
    pb.apply {
        ratedPowerFactor = cim.ratedPowerFactor ?: UNKNOWN_DOUBLE
        ratedS = cim.ratedS ?: UNKNOWN_DOUBLE
        ratedU = cim.ratedU ?: UNKNOWN_DOUBLE
        p = cim.p ?: UNKNOWN_DOUBLE
        q = cim.q ?: UNKNOWN_DOUBLE
        toPb(cim, rceBuilder)
    }

fun toPb(cim: SeriesCompensator, pb: PBSeriesCompensator.Builder): PBSeriesCompensator.Builder =
    pb.apply {
        r = cim.r ?: UNKNOWN_DOUBLE
        r0 = cim.r0 ?: UNKNOWN_DOUBLE
        x = cim.x ?: UNKNOWN_DOUBLE
        x0 = cim.x0 ?: UNKNOWN_DOUBLE
        varistorRatedCurrent = cim.varistorRatedCurrent ?: UNKNOWN_INT
        varistorVoltageThreshold = cim.varistorVoltageThreshold ?: UNKNOWN_INT
        toPb(cim, ceBuilder)
    }

fun toPb(cim: SynchronousMachine, pb: PBSynchronousMachine.Builder): PBSynchronousMachine.Builder =
    pb.apply {
        baseQ = cim.baseQ ?: UNKNOWN_DOUBLE
        condenserP = cim.condenserP ?: UNKNOWN_INT
        cim.earthing?.let { earthing = it }
        earthingStarPointR = cim.earthingStarPointR ?: UNKNOWN_DOUBLE
        earthingStarPointX = cim.earthingStarPointX ?: UNKNOWN_DOUBLE
        ikk = cim.ikk ?: UNKNOWN_DOUBLE
        maxQ = cim.maxQ ?: UNKNOWN_DOUBLE
        maxU = cim.maxU ?: UNKNOWN_INT
        minQ = cim.minQ ?: UNKNOWN_DOUBLE
        minU = cim.minU ?: UNKNOWN_INT
        mu = cim.mu ?: UNKNOWN_DOUBLE
        r = cim.r ?: UNKNOWN_DOUBLE
        r0 = cim.r0 ?: UNKNOWN_DOUBLE
        r2 = cim.r2 ?: UNKNOWN_DOUBLE
        satDirectSubtransX = cim.satDirectSubtransX ?: UNKNOWN_DOUBLE
        satDirectSyncX = cim.satDirectSyncX ?: UNKNOWN_DOUBLE
        satDirectTransX = cim.satDirectTransX ?: UNKNOWN_DOUBLE
        x0 = cim.x0 ?: UNKNOWN_DOUBLE
        x2 = cim.x2 ?: UNKNOWN_DOUBLE
        type = SynchronousMachineKind.Enum.valueOf(cim.type.name)
        operatingMode = SynchronousMachineKind.Enum.valueOf(cim.operatingMode.name)

        toPb(cim, rmBuilder)
    }

fun toPb(cim: TransformerEndRatedS): PBTransformerEndRatedS.Builder =
    PBTransformerEndRatedS.newBuilder().apply {
        ratedS = cim.ratedS
        coolingType = TransformerCoolingType.valueOf(cim.coolingType.name)
    }

fun toPb(cim: ProtectedSwitch, pb: PBProtectedSwitch.Builder): PBProtectedSwitch.Builder =
    pb.apply {
        cim.relayFunctions.forEach { addRelayFunctionMRIDs(it.mRID) }
        breakingCapacity = cim.breakingCapacity ?: UNKNOWN_INT
        toPb(cim, swBuilder)
    }

fun toPb(cim: RatioTapChanger, pb: PBRatioTapChanger.Builder): PBRatioTapChanger.Builder =
    pb.apply {
        cim.transformerEnd?.let { transformerEndMRID = it.mRID } ?: clearTransformerEndMRID()
        stepVoltageIncrement = cim.stepVoltageIncrement ?: UNKNOWN_DOUBLE
        toPb(cim, tcBuilder)
    }

fun toPb(cim: ReactiveCapabilityCurve, pb: PBReactiveCapabilityCurve.Builder): PBReactiveCapabilityCurve.Builder =
    pb.apply {
        toPb(cim, cBuilder)
    }

fun toPb(cim: Recloser, pb: PBRecloser.Builder): PBRecloser.Builder =
    pb.apply { toPb(cim, swBuilder) }


fun toPb(cim: RegulatingCondEq, pb: PBRegulatingCondEq.Builder): PBRegulatingCondEq.Builder =
    pb.apply {
        controlEnabled = cim.controlEnabled
        cim.regulatingControl?.also { regulatingControlMRID = it.mRID } ?: clearRegulatingControlMRID()
        toPb(cim, ecBuilder)
    }

fun toPb(cim: RegulatingControl, pb: PBRegulatingControl.Builder): PBRegulatingControl.Builder =
    pb.apply {
        cim.discrete?.let { discreteSet = it } ?: run { discreteNull = NullValue.NULL_VALUE }
        mode = PBRegulatingControlModeKind.valueOf(cim.mode.name)
        monitoredPhase = PBPhaseCode.valueOf(cim.monitoredPhase.name)
        targetDeadband = cim.targetDeadband ?: UNKNOWN_FLOAT
        targetValue = cim.targetValue ?: UNKNOWN_DOUBLE
        cim.enabled?.let { enabledSet = it } ?: run { enabledNull = NullValue.NULL_VALUE }
        maxAllowedTargetValue = cim.maxAllowedTargetValue ?: UNKNOWN_DOUBLE
        minAllowedTargetValue = cim.minAllowedTargetValue ?: UNKNOWN_DOUBLE
        ratedCurrent = cim.ratedCurrent ?: UNKNOWN_DOUBLE
        cim.terminal?.also { terminalMRID = it.mRID } ?: clearTerminalMRID()
        clearRegulatingCondEqMRIDs()
        cim.regulatingCondEqs.forEach { addRegulatingCondEqMRIDs(it.mRID) }

        toPb(cim, psrBuilder)
    }

fun toPb(cim: ShuntCompensator, pb: PBShuntCompensator.Builder): PBShuntCompensator.Builder =
    pb.apply {
        sections = cim.sections ?: UNKNOWN_DOUBLE
        grounded = cim.grounded
        nomU = cim.nomU ?: UNKNOWN_INT
        phaseConnection = PhaseShuntConnectionKind.Enum.valueOf(cim.phaseConnection.name)
        toPb(cim, rceBuilder)
    }

fun toPb(cim: Switch, pb: PBSwitch.Builder): PBSwitch.Builder =
    pb.apply {
        ratedCurrent = cim.ratedCurrent ?: UNKNOWN_UINT
        normalOpen = cim.isNormallyOpen()
        open = cim.isOpen()
        // when unganged support is added to protobuf
        // normalOpen = cim.normalOpen
        // open = cim.open
        toPb(cim, ceBuilder)
    }

fun toPb(cim: TapChanger, pb: PBTapChanger.Builder): PBTapChanger.Builder =
    pb.apply {
        highStep = cim.highStep ?: UNKNOWN_INT
        lowStep = cim.lowStep ?: UNKNOWN_INT
        step = cim.step ?: UNKNOWN_DOUBLE
        neutralStep = cim.neutralStep ?: UNKNOWN_INT
        neutralU = cim.neutralU ?: UNKNOWN_INT
        normalStep = cim.normalStep ?: UNKNOWN_INT
        controlEnabled = cim.controlEnabled
        cim.tapChangerControl?.also { tapChangerControlMRID = it.mRID } ?: clearTapChangerControlMRID()

        toPb(cim, psrBuilder)
    }

fun toPb(cim: TapChangerControl, pb: PBTapChangerControl.Builder): PBTapChangerControl.Builder =
    pb.apply {
        limitVoltage = cim.limitVoltage ?: UNKNOWN_INT
        cim.lineDropCompensation?.let { lineDropCompensationSet = it } ?: run { lineDropCompensationNull = NullValue.NULL_VALUE }
        lineDropR = cim.lineDropR ?: UNKNOWN_DOUBLE
        lineDropX = cim.lineDropX ?: UNKNOWN_DOUBLE
        reverseLineDropR = cim.reverseLineDropR ?: UNKNOWN_DOUBLE
        reverseLineDropX = cim.reverseLineDropX ?: UNKNOWN_DOUBLE

        cim.forwardLDCBlocking?.let { forwardLDCBlockingSet = it } ?: run { forwardLDCBlockingNull = NullValue.NULL_VALUE }

        timeDelay = cim.timeDelay ?: UNKNOWN_DOUBLE

        cim.coGenerationEnabled?.let { coGenerationEnabledSet = it } ?: run { coGenerationEnabledNull = NullValue.NULL_VALUE }

        toPb(cim, rcBuilder)
    }

fun toPb(cim: TransformerEnd, pb: PBTransformerEnd.Builder): PBTransformerEnd.Builder =
    pb.apply {
        cim.terminal?.let { terminalMRID = it.mRID } ?: clearTerminalMRID()
        cim.baseVoltage?.let { baseVoltageMRID = it.mRID } ?: clearBaseVoltageMRID()
        cim.ratioTapChanger?.let { ratioTapChangerMRID = it.mRID } ?: clearRatioTapChangerMRID()
        cim.starImpedance?.let { starImpedanceMRID = it.mRID } ?: clearStarImpedanceMRID()
        endNumber = cim.endNumber
        grounded = cim.grounded
        rGround = cim.rGround ?: UNKNOWN_DOUBLE
        xGround = cim.xGround ?: UNKNOWN_DOUBLE
        toPb(cim, ioBuilder)
    }

fun toPb(cim: TransformerStarImpedance, pb: PBTransformerStarImpedance.Builder): PBTransformerStarImpedance.Builder =
    pb.apply {
        cim.transformerEndInfo?.let { transformerEndInfoMRID = it.mRID } ?: clearTransformerEndInfoMRID()
        r = cim.r ?: UNKNOWN_DOUBLE
        r0 = cim.r0 ?: UNKNOWN_DOUBLE
        x = cim.x ?: UNKNOWN_DOUBLE
        x0 = cim.x0 ?: UNKNOWN_DOUBLE
        toPb(cim, ioBuilder)
    }

fun AcLineSegment.toPb(): PBAcLineSegment = toPb(this, PBAcLineSegment.newBuilder()).build()
fun Breaker.toPb(): PBBreaker = toPb(this, PBBreaker.newBuilder()).build()
fun BusbarSection.toPb(): PBBusbarSection = toPb(this, PBBusbarSection.newBuilder()).build()
fun Disconnector.toPb(): PBDisconnector = toPb(this, PBDisconnector.newBuilder()).build()
fun EnergyConsumer.toPb(): PBEnergyConsumer = toPb(this, PBEnergyConsumer.newBuilder()).build()
fun EnergyConsumerPhase.toPb(): PBEnergyConsumerPhase = toPb(this, PBEnergyConsumerPhase.newBuilder()).build()
fun EnergySource.toPb(): PBEnergySource = toPb(this, PBEnergySource.newBuilder()).build()
fun EnergySourcePhase.toPb(): PBEnergySourcePhase = toPb(this, PBEnergySourcePhase.newBuilder()).build()
fun Fuse.toPb(): PBFuse = toPb(this, PBFuse.newBuilder()).build()
fun Ground.toPb(): PBGround = toPb(this, PBGround.newBuilder()).build()
fun GroundDisconnector.toPb(): PBGroundDisconnector = toPb(this, PBGroundDisconnector.newBuilder()).build()
fun GroundingImpedance.toPb(): PBGroundingImpedance = toPb(this, PBGroundingImpedance.newBuilder()).build()
fun PetersenCoil.toPb(): PBPetersenCoil = toPb(this, PBPetersenCoil.newBuilder()).build()
fun Jumper.toPb(): PBJumper = toPb(this, PBJumper.newBuilder()).build()
fun Junction.toPb(): PBJunction = toPb(this, PBJunction.newBuilder()).build()
fun LinearShuntCompensator.toPb(): PBLinearShuntCompensator = toPb(this, PBLinearShuntCompensator.newBuilder()).build()
fun LoadBreakSwitch.toPb(): PBLoadBreakSwitch = toPb(this, PBLoadBreakSwitch.newBuilder()).build()
fun PerLengthSequenceImpedance.toPb(): PBPerLengthSequenceImpedance = toPb(this, PBPerLengthSequenceImpedance.newBuilder()).build()
fun PowerElectronicsConnection.toPb(): PBPowerElectronicsConnection = toPb(this, PBPowerElectronicsConnection.newBuilder()).build()
fun PowerElectronicsConnectionPhase.toPb(): PBPowerElectronicsConnectionPhase = toPb(this, PBPowerElectronicsConnectionPhase.newBuilder()).build()
fun PowerTransformer.toPb(): PBPowerTransformer = toPb(this, PBPowerTransformer.newBuilder()).build()
fun PowerTransformerEnd.toPb(): PBPowerTransformerEnd = toPb(this, PBPowerTransformerEnd.newBuilder()).build()
fun RatioTapChanger.toPb(): PBRatioTapChanger = toPb(this, PBRatioTapChanger.newBuilder()).build()
fun ReactiveCapabilityCurve.toPb(): PBReactiveCapabilityCurve = toPb(this, PBReactiveCapabilityCurve.newBuilder()).build()
fun Recloser.toPb(): PBRecloser = toPb(this, PBRecloser.newBuilder()).build()
fun SeriesCompensator.toPb(): PBSeriesCompensator = toPb(this, PBSeriesCompensator.newBuilder()).build()
fun SynchronousMachine.toPb(): PBSynchronousMachine = toPb(this, PBSynchronousMachine.newBuilder()).build()
fun TapChangerControl.toPb(): PBTapChangerControl = toPb(this, PBTapChangerControl.newBuilder()).build()
fun TransformerStarImpedance.toPb(): PBTransformerStarImpedance = toPb(this, PBTransformerStarImpedance.newBuilder()).build()

/************ IEC61970 InfIEC61970 Feeder ************/

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

fun toPb(cim: LvFeeder, pb: PBLvFeeder.Builder): PBLvFeeder.Builder =
    pb.apply {
        cim.normalHeadTerminal?.let { normalHeadTerminalMRID = it.mRID } ?: clearNormalHeadTerminalMRID()

        clearNormalEnergizingFeederMRIDs()
        cim.normalEnergizingFeeders.forEach { addNormalEnergizingFeederMRIDs(it.mRID) }

        toPb(cim, ecBuilder)
    }

fun Circuit.toPb(): PBCircuit = toPb(this, PBCircuit.newBuilder()).build()
fun Loop.toPb(): PBLoop = toPb(this, PBLoop.newBuilder()).build()
fun LvFeeder.toPb(): PBLvFeeder = toPb(this, PBLvFeeder.newBuilder()).build()

/************ IEC61970 InfIEC61970 Wires.Generation.Production ************/

fun toPb(cim: EvChargingUnit, pb: PBEvChargingUnit.Builder): PBEvChargingUnit.Builder =
    pb.apply {
        toPb(cim, peuBuilder)
    }

fun EvChargingUnit.toPb(): PBEvChargingUnit = toPb(this, PBEvChargingUnit.newBuilder()).build()

/************ Class for Java friendly usage ************/

class NetworkCimToProto : BaseCimToProto() {

    // IEC61968 ASSET INFO
    fun toPb(cim: CableInfo): PBCableInfo = cim.toPb()
    fun toPb(cim: NoLoadTest): PBNoLoadTest = cim.toPb()
    fun toPb(cim: OpenCircuitTest): PBOpenCircuitTest = cim.toPb()
    fun toPb(cim: OverheadWireInfo): PBOverheadWireInfo = cim.toPb()
    fun toPb(cim: PowerTransformerInfo): PBPowerTransformerInfo = cim.toPb()
    fun toPb(cim: ShortCircuitTest): PBShortCircuitTest = cim.toPb()
    fun toPb(cim: ShuntCompensatorInfo): PBShuntCompensatorInfo = cim.toPb()
    fun toPb(cim: SwitchInfo): PBSwitchInfo = cim.toPb()
    fun toPb(cim: TransformerEndInfo): PBTransformerEndInfo = cim.toPb()
    fun toPb(cim: TransformerTankInfo): PBTransformerTankInfo = cim.toPb()

    // IEC61968 ASSETS
    fun toPb(cim: AssetOwner): PBAssetOwner = cim.toPb()
    fun toPb(cim: Pole): PBPole = cim.toPb()
    fun toPb(cim: Streetlight): PBStreetlight = cim.toPb()

    // IEC61968 COMMON
    fun toPb(cim: Location): PBLocation = cim.toPb()

    // IEC61968 infIEC61968 InfAssetInfo
    fun toPb(cim: RelayInfo): PBRelayInfo = cim.toPb()
    fun toPb(cim: CurrentTransformerInfo): PBCurrentTransformerInfo = cim.toPb()
    fun toPb(cim: PotentialTransformerInfo): PBPotentialTransformerInfo = cim.toPb()

    // IEC61968 METERING
    fun toPb(cim: Meter): PBMeter = cim.toPb()
    fun toPb(cim: UsagePoint): PBUsagePoint = cim.toPb()

    // IEC61968 OPERATIONS
    fun toPb(cim: OperationalRestriction): PBOperationalRestriction = cim.toPb()

    // IEC61970 BASE AUXILIARY EQUIPMENT
    fun toPb(cim: CurrentTransformer): PBCurrentTransformer = cim.toPb()
    fun toPb(cim: FaultIndicator): PBFaultIndicator = cim.toPb()
    fun toPb(cim: PotentialTransformer): PBPotentialTransformer = cim.toPb()

    // IEC61970 BASE CORE
    fun toPb(cim: BaseVoltage): PBBaseVoltage = cim.toPb()
    fun toPb(cim: ConnectivityNode): PBConnectivityNode = cim.toPb()
    fun toPb(cim: Feeder): PBFeeder = cim.toPb()
    fun toPb(cim: GeographicalRegion): PBGeographicalRegion = cim.toPb()
    fun toPb(cim: Site): PBSite = cim.toPb()
    fun toPb(cim: SubGeographicalRegion): PBSubGeographicalRegion = cim.toPb()
    fun toPb(cim: Substation): PBSubstation = cim.toPb()
    fun toPb(cim: Terminal): PBTerminal = cim.toPb()

    // IEC61970 BASE EQUIVALENTS
    fun toPb(cim: EquivalentBranch): PBEquivalentBranch = cim.toPb()

    // IEC61970 BASE MEAS
    fun toPb(cim: Accumulator): PBAccumulator = cim.toPb()
    fun toPb(cim: Analog): PBAnalog = cim.toPb()
    fun toPb(cim: Control): PBControl = cim.toPb()
    fun toPb(cim: Discrete): PBDiscrete = cim.toPb()

    // IEC61970 Base Protection
    fun toPb(cim: CurrentRelay): PBCurrentRelay = cim.toPb()
    fun toPb(cim: DistanceRelay): PBDistanceRelay = cim.toPb()
    fun toPb(cim: ProtectionRelayScheme): PBProtectionRelayScheme = cim.toPb()
    fun toPb(cim: ProtectionRelaySystem): PBProtectionRelaySystem = cim.toPb()
    fun toPb(cim: VoltageRelay): PBVoltageRelay = cim.toPb()

    // IEC61970 BASE SCADA
    fun toPb(cim: RemoteControl): PBRemoteControl = cim.toPb()
    fun toPb(cim: RemoteSource): PBRemoteSource = cim.toPb()

    // IEC61970 BASE WIRES GENERATION PRODUCTION
    fun toPb(cim: BatteryUnit): PBBatteryUnit = cim.toPb()
    fun toPb(cim: PhotoVoltaicUnit): PBPhotoVoltaicUnit = cim.toPb()
    fun toPb(cim: PowerElectronicsWindUnit): PBPowerElectronicsWindUnit = cim.toPb()

    // IEC61970 BASE WIRES
    fun toPb(cim: AcLineSegment): PBAcLineSegment = cim.toPb()
    fun toPb(cim: Breaker): PBBreaker = cim.toPb()
    fun toPb(cim: BusbarSection): PBBusbarSection = cim.toPb()
    fun toPb(cim: Disconnector): PBDisconnector = cim.toPb()
    fun toPb(cim: EnergyConsumer): PBEnergyConsumer = cim.toPb()
    fun toPb(cim: EnergyConsumerPhase): PBEnergyConsumerPhase = cim.toPb()
    fun toPb(cim: EnergySource): PBEnergySource = cim.toPb()
    fun toPb(cim: EnergySourcePhase): PBEnergySourcePhase = cim.toPb()
    fun toPb(cim: Fuse): PBFuse = cim.toPb()
    fun toPb(cim: Ground): PBGround = cim.toPb()
    fun toPb(cim: GroundDisconnector): PBGroundDisconnector = cim.toPb()
    fun toPb(cim: Jumper): PBJumper = cim.toPb()
    fun toPb(cim: Junction): PBJunction = cim.toPb()
    fun toPb(cim: LinearShuntCompensator): PBLinearShuntCompensator = cim.toPb()
    fun toPb(cim: LoadBreakSwitch): PBLoadBreakSwitch = cim.toPb()
    fun toPb(cim: PerLengthSequenceImpedance): PBPerLengthSequenceImpedance = cim.toPb()
    fun toPb(cim: PowerElectronicsConnection): PBPowerElectronicsConnection = cim.toPb()
    fun toPb(cim: PowerElectronicsConnectionPhase): PBPowerElectronicsConnectionPhase = cim.toPb()
    fun toPb(cim: PowerTransformer): PBPowerTransformer = cim.toPb()
    fun toPb(cim: PowerTransformerEnd): PBPowerTransformerEnd = cim.toPb()
    fun toPb(cim: RatioTapChanger): PBRatioTapChanger = cim.toPb()
    fun toPb(cim: Recloser): PBRecloser = cim.toPb()
    fun toPb(cim: SeriesCompensator): PBSeriesCompensator = cim.toPb()
    fun toPb(cim: TapChangerControl): PBTapChangerControl = cim.toPb()
    fun toPb(cim: TransformerStarImpedance): PBTransformerStarImpedance = cim.toPb()

    // IEC61970 InfIEC61970 Base Wires Generation Production
    fun toPb(cim: EvChargingUnit): PBEvChargingUnit = cim.toPb()

    // IEC61970 InfIEC61970 Feeder
    fun toPb(cim: Circuit): PBCircuit = cim.toPb()
    fun toPb(cim: Loop): PBLoop = cim.toPb()
    fun toPb(cim: LvFeeder): PBLvFeeder = cim.toPb()

}

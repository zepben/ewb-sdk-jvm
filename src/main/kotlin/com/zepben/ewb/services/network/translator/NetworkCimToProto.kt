/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.network.translator

import com.google.protobuf.NullValue
import com.zepben.ewb.cim.extensions.iec61968.assetinfo.RelayInfo
import com.zepben.ewb.cim.extensions.iec61968.common.ContactDetails
import com.zepben.ewb.cim.extensions.iec61968.metering.PanDemandResponseFunction
import com.zepben.ewb.cim.extensions.iec61970.base.core.HvCustomer
import com.zepben.ewb.cim.extensions.iec61970.base.core.Site
import com.zepben.ewb.cim.extensions.iec61970.base.feeder.Loop
import com.zepben.ewb.cim.extensions.iec61970.base.feeder.LvFeeder
import com.zepben.ewb.cim.extensions.iec61970.base.feeder.LvSubstation
import com.zepben.ewb.cim.extensions.iec61970.base.generation.production.EvChargingUnit
import com.zepben.ewb.cim.extensions.iec61970.base.protection.*
import com.zepben.ewb.cim.extensions.iec61970.base.wires.BatteryControl
import com.zepben.ewb.cim.extensions.iec61970.base.wires.TransformerEndRatedS
import com.zepben.ewb.cim.iec61968.assetinfo.*
import com.zepben.ewb.cim.iec61968.assets.*
import com.zepben.ewb.cim.iec61968.common.*
import com.zepben.ewb.cim.iec61968.infiec61968.infassetinfo.CurrentTransformerInfo
import com.zepben.ewb.cim.iec61968.infiec61968.infassetinfo.PotentialTransformerInfo
import com.zepben.ewb.cim.iec61968.infiec61968.infassets.Pole
import com.zepben.ewb.cim.iec61968.infiec61968.infcommon.Ratio
import com.zepben.ewb.cim.iec61968.metering.EndDevice
import com.zepben.ewb.cim.iec61968.metering.EndDeviceFunction
import com.zepben.ewb.cim.iec61968.metering.Meter
import com.zepben.ewb.cim.iec61968.metering.UsagePoint
import com.zepben.ewb.cim.iec61968.operations.OperationalRestriction
import com.zepben.ewb.cim.iec61970.base.auxiliaryequipment.*
import com.zepben.ewb.cim.iec61970.base.core.*
import com.zepben.ewb.cim.iec61970.base.equivalents.EquivalentBranch
import com.zepben.ewb.cim.iec61970.base.equivalents.EquivalentEquipment
import com.zepben.ewb.cim.iec61970.base.generation.production.BatteryUnit
import com.zepben.ewb.cim.iec61970.base.generation.production.PhotoVoltaicUnit
import com.zepben.ewb.cim.iec61970.base.generation.production.PowerElectronicsUnit
import com.zepben.ewb.cim.iec61970.base.generation.production.PowerElectronicsWindUnit
import com.zepben.ewb.cim.iec61970.base.meas.*
import com.zepben.ewb.cim.iec61970.base.protection.*
import com.zepben.ewb.cim.iec61970.base.scada.RemoteControl
import com.zepben.ewb.cim.iec61970.base.scada.RemotePoint
import com.zepben.ewb.cim.iec61970.base.scada.RemoteSource
import com.zepben.ewb.cim.iec61970.base.wires.*
import com.zepben.ewb.cim.iec61970.infiec61970.feeder.Circuit
import com.zepben.ewb.services.common.translator.*
import com.zepben.ewb.services.network.whenNetworkServiceObject
import com.zepben.protobuf.nc.NetworkIdentifiedObject
import com.zepben.protobuf.cim.extensions.iec61968.assetinfo.RelayInfo as PBRelayInfo
import com.zepben.protobuf.cim.extensions.iec61968.common.ContactDetails as PBContactDetails
import com.zepben.protobuf.cim.extensions.iec61968.metering.PanDemandResponseFunction as PBPanDemandResponseFunction
import com.zepben.protobuf.cim.extensions.iec61970.base.core.HvCustomer as PBHvCustomer
import com.zepben.protobuf.cim.extensions.iec61970.base.core.Site as PBSite
import com.zepben.protobuf.cim.extensions.iec61970.base.feeder.Loop as PBLoop
import com.zepben.protobuf.cim.extensions.iec61970.base.feeder.LvFeeder as PBLvFeeder
import com.zepben.protobuf.cim.extensions.iec61970.base.feeder.LvSubstation as PBLvSubstation
import com.zepben.protobuf.cim.extensions.iec61970.base.generation.production.EvChargingUnit as PBEvChargingUnit
import com.zepben.protobuf.cim.extensions.iec61970.base.protection.DirectionalCurrentRelay as PBDirectionalCurrentRelay
import com.zepben.protobuf.cim.extensions.iec61970.base.protection.DistanceRelay as PBDistanceRelay
import com.zepben.protobuf.cim.extensions.iec61970.base.protection.ProtectionRelayFunction as PBProtectionRelayFunction
import com.zepben.protobuf.cim.extensions.iec61970.base.protection.ProtectionRelayScheme as PBProtectionRelayScheme
import com.zepben.protobuf.cim.extensions.iec61970.base.protection.ProtectionRelaySystem as PBProtectionRelaySystem
import com.zepben.protobuf.cim.extensions.iec61970.base.protection.RelaySetting as PBRelaySetting
import com.zepben.protobuf.cim.extensions.iec61970.base.protection.VoltageRelay as PBVoltageRelay
import com.zepben.protobuf.cim.extensions.iec61970.base.wires.BatteryControl as PBBatteryControl
import com.zepben.protobuf.cim.extensions.iec61970.base.wires.TransformerEndRatedS as PBTransformerEndRatedS
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
import com.zepben.protobuf.cim.iec61968.assets.AssetFunction as PBAssetFunction
import com.zepben.protobuf.cim.iec61968.assets.AssetInfo as PBAssetInfo
import com.zepben.protobuf.cim.iec61968.assets.AssetOrganisationRole as PBAssetOrganisationRole
import com.zepben.protobuf.cim.iec61968.assets.AssetOwner as PBAssetOwner
import com.zepben.protobuf.cim.iec61968.assets.Streetlight as PBStreetlight
import com.zepben.protobuf.cim.iec61968.assets.Structure as PBStructure
import com.zepben.protobuf.cim.iec61968.common.ElectronicAddress as PBElectronicAddress
import com.zepben.protobuf.cim.iec61968.common.Location as PBLocation
import com.zepben.protobuf.cim.iec61968.common.PositionPoint as PBPositionPoint
import com.zepben.protobuf.cim.iec61968.common.StreetAddress as PBStreetAddress
import com.zepben.protobuf.cim.iec61968.common.StreetDetail as PBStreetDetail
import com.zepben.protobuf.cim.iec61968.common.TelephoneNumber as PBTelephoneNumber
import com.zepben.protobuf.cim.iec61968.common.TownDetail as PBTownDetail
import com.zepben.protobuf.cim.iec61968.infiec61968.infassetinfo.CurrentTransformerInfo as PBCurrentTransformerInfo
import com.zepben.protobuf.cim.iec61968.infiec61968.infassetinfo.PotentialTransformerInfo as PBPotentialTransformerInfo
import com.zepben.protobuf.cim.iec61968.infiec61968.infassets.Pole as PBPole
import com.zepben.protobuf.cim.iec61968.infiec61968.infcommon.Ratio as PBRatio
import com.zepben.protobuf.cim.iec61968.metering.EndDevice as PBEndDevice
import com.zepben.protobuf.cim.iec61968.metering.EndDeviceFunction as PBEndDeviceFunction
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
import com.zepben.protobuf.cim.iec61970.base.core.PowerSystemResource as PBPowerSystemResource
import com.zepben.protobuf.cim.iec61970.base.core.SubGeographicalRegion as PBSubGeographicalRegion
import com.zepben.protobuf.cim.iec61970.base.core.Substation as PBSubstation
import com.zepben.protobuf.cim.iec61970.base.core.Terminal as PBTerminal
import com.zepben.protobuf.cim.iec61970.base.equivalents.EquivalentBranch as PBEquivalentBranch
import com.zepben.protobuf.cim.iec61970.base.equivalents.EquivalentEquipment as PBEquivalentEquipment
import com.zepben.protobuf.cim.iec61970.base.generation.production.BatteryUnit as PBBatteryUnit
import com.zepben.protobuf.cim.iec61970.base.generation.production.PhotoVoltaicUnit as PBPhotoVoltaicUnit
import com.zepben.protobuf.cim.iec61970.base.generation.production.PowerElectronicsUnit as PBPowerElectronicsUnit
import com.zepben.protobuf.cim.iec61970.base.generation.production.PowerElectronicsWindUnit as PBPowerElectronicsWindUnit
import com.zepben.protobuf.cim.iec61970.base.meas.Accumulator as PBAccumulator
import com.zepben.protobuf.cim.iec61970.base.meas.Analog as PBAnalog
import com.zepben.protobuf.cim.iec61970.base.meas.Control as PBControl
import com.zepben.protobuf.cim.iec61970.base.meas.Discrete as PBDiscrete
import com.zepben.protobuf.cim.iec61970.base.meas.IoPoint as PBIoPoint
import com.zepben.protobuf.cim.iec61970.base.meas.Measurement as PBMeasurement
import com.zepben.protobuf.cim.iec61970.base.protection.CurrentRelay as PBCurrentRelay
import com.zepben.protobuf.cim.iec61970.base.scada.RemoteControl as PBRemoteControl
import com.zepben.protobuf.cim.iec61970.base.scada.RemotePoint as PBRemotePoint
import com.zepben.protobuf.cim.iec61970.base.scada.RemoteSource as PBRemoteSource
import com.zepben.protobuf.cim.iec61970.base.wires.AcLineSegment as PBAcLineSegment
import com.zepben.protobuf.cim.iec61970.base.wires.AcLineSegmentPhase as PBAcLineSegmentPhase
import com.zepben.protobuf.cim.iec61970.base.wires.Breaker as PBBreaker
import com.zepben.protobuf.cim.iec61970.base.wires.BusbarSection as PBBusbarSection
import com.zepben.protobuf.cim.iec61970.base.wires.Clamp as PBClamp
import com.zepben.protobuf.cim.iec61970.base.wires.Conductor as PBConductor
import com.zepben.protobuf.cim.iec61970.base.wires.Connector as PBConnector
import com.zepben.protobuf.cim.iec61970.base.wires.Cut as PBCut
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
import com.zepben.protobuf.cim.iec61970.base.wires.Jumper as PBJumper
import com.zepben.protobuf.cim.iec61970.base.wires.Junction as PBJunction
import com.zepben.protobuf.cim.iec61970.base.wires.Line as PBLine
import com.zepben.protobuf.cim.iec61970.base.wires.LinearShuntCompensator as PBLinearShuntCompensator
import com.zepben.protobuf.cim.iec61970.base.wires.LoadBreakSwitch as PBLoadBreakSwitch
import com.zepben.protobuf.cim.iec61970.base.wires.PerLengthImpedance as PBPerLengthImpedance
import com.zepben.protobuf.cim.iec61970.base.wires.PerLengthLineParameter as PBPerLengthLineParameter
import com.zepben.protobuf.cim.iec61970.base.wires.PerLengthPhaseImpedance as PBPerLengthPhaseImpedance
import com.zepben.protobuf.cim.iec61970.base.wires.PerLengthSequenceImpedance as PBPerLengthSequenceImpedance
import com.zepben.protobuf.cim.iec61970.base.wires.PetersenCoil as PBPetersenCoil
import com.zepben.protobuf.cim.iec61970.base.wires.PhaseImpedanceData as PBPhaseImpedanceData
import com.zepben.protobuf.cim.iec61970.base.wires.PowerElectronicsConnection as PBPowerElectronicsConnection
import com.zepben.protobuf.cim.iec61970.base.wires.PowerElectronicsConnectionPhase as PBPowerElectronicsConnectionPhase
import com.zepben.protobuf.cim.iec61970.base.wires.PowerTransformer as PBPowerTransformer
import com.zepben.protobuf.cim.iec61970.base.wires.PowerTransformerEnd as PBPowerTransformerEnd
import com.zepben.protobuf.cim.iec61970.base.wires.ProtectedSwitch as PBProtectedSwitch
import com.zepben.protobuf.cim.iec61970.base.wires.RatioTapChanger as PBRatioTapChanger
import com.zepben.protobuf.cim.iec61970.base.wires.ReactiveCapabilityCurve as PBReactiveCapabilityCurve
import com.zepben.protobuf.cim.iec61970.base.wires.Recloser as PBRecloser
import com.zepben.protobuf.cim.iec61970.base.wires.RegulatingCondEq as PBRegulatingCondEq
import com.zepben.protobuf.cim.iec61970.base.wires.RegulatingControl as PBRegulatingControl
import com.zepben.protobuf.cim.iec61970.base.wires.RotatingMachine as PBRotatingMachine
import com.zepben.protobuf.cim.iec61970.base.wires.SeriesCompensator as PBSeriesCompensator
import com.zepben.protobuf.cim.iec61970.base.wires.ShuntCompensator as PBShuntCompensator
import com.zepben.protobuf.cim.iec61970.base.wires.StaticVarCompensator as PBStaticVarCompensator
import com.zepben.protobuf.cim.iec61970.base.wires.Switch as PBSwitch
import com.zepben.protobuf.cim.iec61970.base.wires.SynchronousMachine as PBSynchronousMachine
import com.zepben.protobuf.cim.iec61970.base.wires.TapChanger as PBTapChanger
import com.zepben.protobuf.cim.iec61970.base.wires.TapChangerControl as PBTapChangerControl
import com.zepben.protobuf.cim.iec61970.base.wires.TransformerEnd as PBTransformerEnd
import com.zepben.protobuf.cim.iec61970.base.wires.TransformerStarImpedance as PBTransformerStarImpedance
import com.zepben.protobuf.cim.iec61970.infiec61970.feeder.Circuit as PBCircuit

/**
 * Convert the [IdentifiedObject] to a [NetworkIdentifiedObject] representation.
 */
fun networkIdentifiedObject(identifiedObject: IdentifiedObject): NetworkIdentifiedObject =
    NetworkIdentifiedObject.newBuilder().apply {
        whenNetworkServiceObject(
            identifiedObject,
            isAcLineSegment = { acLineSegment = it.toPb() },
            isAccumulator = { accumulator = it.toPb() },
            isAnalog = { analog = it.toPb() },
            isAssetOwner = { assetOwner = it.toPb() },
            isBreaker = { breaker = it.toPb() },
            isLoadBreakSwitch = { loadBreakSwitch = it.toPb() },
            isBaseVoltage = { baseVoltage = it.toPb() },
            isCableInfo = { cableInfo = it.toPb() },
            isCircuit = { circuit = it.toPb() },
            isConnectivityNode = { connectivityNode = it.toPb() },
            isControl = { control = it.toPb() },
            isDisconnector = { disconnector = it.toPb() },
            isDiscrete = { discrete = it.toPb() },
            isEnergyConsumer = { energyConsumer = it.toPb() },
            isEnergyConsumerPhase = { energyConsumerPhase = it.toPb() },
            isEnergySource = { energySource = it.toPb() },
            isEnergySourcePhase = { energySourcePhase = it.toPb() },
            isFaultIndicator = { faultIndicator = it.toPb() },
            isFeeder = { feeder = it.toPb() },
            isFuse = { fuse = it.toPb() },
            isGeographicalRegion = { geographicalRegion = it.toPb() },
            isJumper = { jumper = it.toPb() },
            isJunction = { junction = it.toPb() },
            isLinearShuntCompensator = { linearShuntCompensator = it.toPb() },
            isLocation = { location = it.toPb() },
            isMeter = { meter = it.toPb() },
            isOperationalRestriction = { operationalRestriction = it.toPb() },
            isOrganisation = { organisation = it.toPb() },
            isOverheadWireInfo = { overheadWireInfo = it.toPb() },
            isPole = { pole = it.toPb() },
            isPowerTransformer = { powerTransformer = it.toPb() },
            isPowerTransformerEnd = { powerTransformerEnd = it.toPb() },
            isPowerTransformerInfo = { powerTransformerInfo = it.toPb() },
            isRatioTapChanger = { ratioTapChanger = it.toPb() },
            isRecloser = { recloser = it.toPb() },
            isRemoteControl = { remoteControl = it.toPb() },
            isRemoteSource = { remoteSource = it.toPb() },
            isSite = { site = it.toPb() },
            isStreetlight = { streetlight = it.toPb() },
            isSubGeographicalRegion = { subGeographicalRegion = it.toPb() },
            isSubstation = { substation = it.toPb() },
            isTerminal = { terminal = it.toPb() },
            isUsagePoint = { usagePoint = it.toPb() },
            isPerLengthSequenceImpedance = { perLengthSequenceImpedance = it.toPb() },
            isLoop = { loop = it.toPb() },
            isLvFeeder = { lvFeeder = it.toPb() },
            isBatteryUnit = { batteryUnit = it.toPb() },
            isPhotoVoltaicUnit = { photoVoltaicUnit = it.toPb() },
            isPowerElectronicsWindUnit = { powerElectronicsWindUnit = it.toPb() },
            isPowerElectronicsConnection = { powerElectronicsConnection = it.toPb() },
            isPowerElectronicsConnectionPhase = { powerElectronicsConnectionPhase = it.toPb() },
            isBusbarSection = { busbarSection = it.toPb() },
            isTransformerEndInfo = { transformerEndInfo = it.toPb() },
            isTransformerTankInfo = { transformerTankInfo = it.toPb() },
            isTransformerStarImpedance = { transformerStarImpedance = it.toPb() },
            isNoLoadTest = { noLoadTest = it.toPb() },
            isOpenCircuitTest = { openCircuitTest = it.toPb() },
            isShortCircuitTest = { shortCircuitTest = it.toPb() },
            isEquivalentBranch = { equivalentBranch = it.toPb() },
            isShuntCompensatorInfo = { shuntCompensatorInfo = it.toPb() },
            isCurrentTransformerInfo = { currentTransformerInfo = it.toPb() },
            isPotentialTransformerInfo = { potentialTransformerInfo = it.toPb() },
            isCurrentTransformer = { currentTransformer = it.toPb() },
            isPotentialTransformer = { potentialTransformer = it.toPb() },
            isSwitchInfo = { switchInfo = it.toPb() },
            isCurrentRelay = { currentRelay = it.toPb() },
            isRelayInfo = { relayInfo = it.toPb() },
            isEvChargingUnit = { evChargingUnit = it.toPb() },
            isTapChangerControl = { tapChangerControl = it.toPb() },
            isSeriesCompensator = { seriesCompensator = it.toPb() },
            isGround = { ground = it.toPb() },
            isGroundDisconnector = { groundDisconnector = it.toPb() },
            isProtectionRelayScheme = { protectionRelayScheme = it.toPb() },
            isProtectionRelaySystem = { protectionRelaySystem = it.toPb() },
            isVoltageRelay = { voltageRelay = it.toPb() },
            isDistanceRelay = { distanceRelay = it.toPb() },
            isSynchronousMachine = { synchronousMachine = it.toPb() },
            isReactiveCapabilityCurve = { reactiveCapabilityCurve = it.toPb() },
            isGroundingImpedance = { groundingImpedance = it.toPb() },
            isPetersenCoil = { petersenCoil = it.toPb() },
            isPanDemandResponseFunction = { panDemandResponseFunction = it.toPb() },
            isBatteryControl = { batteryControl = it.toPb() },
            isStaticVarCompensator = { staticVarCompensator = it.toPb() },
            isPerLengthPhaseImpedance = { perLengthPhaseImpedance = it.toPb() },
            isCut = { cut = it.toPb() },
            isClamp = { clamp = it.toPb() },
            isDirectionalCurrentRelay = { directionalCurrentRelay = it.toPb() },
            isLvSubstation = { lvSubstation = it.toPb() },
            isHvCustomer = { hvCustomer = it.toPb() },
            isAcLineSegmentPhase = { acLineSegmentPhase = it.toPb() },
        )
    }.build()

// ##################################
// # Extensions IEC61968 Asset Info #
// ##################################

/**
 * Convert the [RelayInfo] into its protobuf counterpart.
 *
 * @param cim The [RelayInfo] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: RelayInfo, pb: PBRelayInfo.Builder): PBRelayInfo.Builder =
    pb.apply {
        cim.curveSetting?.also { curveSettingSet = it } ?: run { curveSettingNull = NullValue.NULL_VALUE }
        cim.recloseFast?.also { recloseFastSet = it } ?: run { recloseFastNull = NullValue.NULL_VALUE }
        cim.recloseDelays.forEach { addRecloseDelays(it) }
        toPb(cim, aiBuilder)
    }

/**
 * An extension for converting any [RelayInfo] into its protobuf counterpart.
 */
fun RelayInfo.toPb(): PBRelayInfo = toPb(this, PBRelayInfo.newBuilder()).build()

// ##############################
// # Extensions IEC61968 Common #
// ##############################

/**
 * Convert the [ContactDetails] into its protobuf counterpart.
 *
 * @param cim The [ContactDetails] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: ContactDetails, pb: PBContactDetails.Builder): PBContactDetails.Builder =
    pb.apply {
        id = cim.id
        clearPhoneNumbers()
        cim.phoneNumbers.forEach { addPhoneNumbers(it.toPb()) }
        cim.contactAddress?.also { toPb(it, contactAddressBuilder) } ?: clearContactAddress()
        clearElectronicAddresses()
        cim.electronicAddresses.forEach { addElectronicAddresses(it.toPb()) }
        cim.contactType?.also { contactTypeSet = it } ?: run { contactTypeNull = NullValue.NULL_VALUE }
        cim.firstName?.also { firstNameSet = it } ?: run { firstNameNull = NullValue.NULL_VALUE }
        cim.lastName?.also { lastNameSet = it } ?: run { lastNameNull = NullValue.NULL_VALUE }
        preferredContactMethod = mapContactMethodType.toPb(cim.preferredContactMethod)
        cim.isPrimary?.also { isPrimarySet = it } ?: run { isPrimaryNull = NullValue.NULL_VALUE }
        cim.businessName?.also { businessNameSet = it } ?: run { businessNameNull = NullValue.NULL_VALUE }
    }

/**
 * An extension for converting any [ContactDetails] into its protobuf counterpart.
 */
fun ContactDetails.toPb(): PBContactDetails = toPb(this, PBContactDetails.newBuilder()).build()

// ################################
// # Extensions IEC61968 Metering #
// ################################

/**
 * Convert the [PanDemandResponseFunction] into its protobuf counterpart.
 *
 * @param cim The [PanDemandResponseFunction] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: PanDemandResponseFunction, pb: PBPanDemandResponseFunction.Builder): PBPanDemandResponseFunction.Builder =
    pb.apply {
        kind = mapEndDeviceFunctionKind.toPb(cim.kind)
        cim.applianceBitmask?.also { applianceSet = it } ?: run { applianceNull = NullValue.NULL_VALUE }
        toPb(cim, edfBuilder)
    }

/**
 * An extension for converting any [PanDemandResponseFunction] into its protobuf counterpart.
 */
fun PanDemandResponseFunction.toPb(): PBPanDemandResponseFunction = toPb(this, PBPanDemandResponseFunction.newBuilder()).build()

// #################################
// # Extensions IEC61970 Base Core #
// #################################

/**
 * Convert the [HvCustomer] into its protobuf counterpart.
 *
 * @param cim The [HvCustomer] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: HvCustomer, pb: PBHvCustomer.Builder): PBHvCustomer.Builder =
    pb.apply { toPb(cim, ecBuilder) }

/**
 * Convert the [Site] into its protobuf counterpart.
 *
 * @param cim The [Site] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: Site, pb: PBSite.Builder): PBSite.Builder =
    pb.apply { toPb(cim, ecBuilder) }

/**
 * An extension for converting any [HvCustomer] into its protobuf counterpart.
 */
fun HvCustomer.toPb(): PBHvCustomer = toPb(this, PBHvCustomer.newBuilder()).build()

/**
 * An extension for converting any [Site] into its protobuf counterpart.
 */
fun Site.toPb(): PBSite = toPb(this, PBSite.newBuilder()).build()

// ###################################
// # Extensions IEC61970 Base Feeder #
// ###################################

/**
 * Convert the [Loop] into its protobuf counterpart.
 *
 * @param cim The [Loop] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
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

/**
 * Convert the [LvFeeder] into its protobuf counterpart.
 *
 * @param cim The [LvFeeder] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: LvFeeder, pb: PBLvFeeder.Builder): PBLvFeeder.Builder =
    pb.apply {
        cim.normalHeadTerminal?.also { normalHeadTerminalMRID = it.mRID } ?: clearNormalHeadTerminalMRID()

        clearNormalEnergizingFeederMRIDs()
        cim.normalEnergizingFeeders.forEach { addNormalEnergizingFeederMRIDs(it.mRID) }
        clearCurrentlyEnergizingFeederMRIDs()
        cim.currentEnergizingFeeders.forEach { addCurrentlyEnergizingFeederMRIDs(it.mRID) }
        cim.normalEnergizingLvSubstation?.also { normalEnergizingLvSubstationMRID = it.mRID } ?: clearNormalEnergizingLvSubstationMRID()

        toPb(cim, ecBuilder)
    }

/**
 * Convert the [LvSubstation] into its protobuf counterpart.
 *
 * @param cim The [LvSubstation] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: LvSubstation, pb: PBLvSubstation.Builder): PBLvSubstation.Builder =
    pb.apply {
        clearNormalEnergizingFeederMRIDs()
        cim.normalEnergizingFeeders.forEach { addNormalEnergizingFeederMRIDs(it.mRID) }
        clearCurrentEnergizingFeederMRIDs()
        cim.currentEnergizingFeeders.forEach { addCurrentEnergizingFeederMRIDs(it.mRID) }

        clearNormalEnergizedLvFeederMRIDs()
        cim.normalEnergizedLvFeeders.forEach { addNormalEnergizedLvFeederMRIDs(it.mRID) }

        toPb(cim, ecBuilder)
    }
/**
 * An extension for converting any [Loop] into its protobuf counterpart.
 */
fun Loop.toPb(): PBLoop = toPb(this, PBLoop.newBuilder()).build()

/**
 * An extension for converting any [LvFeeder] into its protobuf counterpart.
 */
fun LvFeeder.toPb(): PBLvFeeder = toPb(this, PBLvFeeder.newBuilder()).build()

/**
 * An extension for converting any [LvSubstation] into its protobuf counterpart.
 */
fun LvSubstation.toPb(): PBLvSubstation = toPb(this, PBLvSubstation.newBuilder()).build()

// ##################################################
// # Extensions IEC61970 Base Generation Production #
// ##################################################

/**
 * Convert the [EvChargingUnit] into its protobuf counterpart.
 *
 * @param cim The [EvChargingUnit] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: EvChargingUnit, pb: PBEvChargingUnit.Builder): PBEvChargingUnit.Builder =
    pb.apply {
        toPb(cim, peuBuilder)
    }

/**
 * An extension for converting any [EvChargingUnit] into its protobuf counterpart.
 */
fun EvChargingUnit.toPb(): PBEvChargingUnit = toPb(this, PBEvChargingUnit.newBuilder()).build()

// #######################################
// # Extensions IEC61970 Base Protection #
// #######################################

/**
 * Convert the [DirectionalCurrentRelay] into its protobuf counterpart.
 *
 * @param cim The [DirectionalCurrentRelay] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: DirectionalCurrentRelay, pb: PBDirectionalCurrentRelay.Builder): PBDirectionalCurrentRelay.Builder =
    pb.apply {
        cim.directionalCharacteristicAngle?.also { directionalCharacteristicAngleSet = it } ?: run { directionalCharacteristicAngleNull = NullValue.NULL_VALUE }
        polarizingQuantityType = mapPolarizingQuantityType.toPb(cim.polarizingQuantityType)
        relayElementPhase = mapPhaseCode.toPb(cim.relayElementPhase)
        cim.minimumPickupCurrent?.also { minimumPickupCurrentSet = it } ?: run { minimumPickupCurrentNull = NullValue.NULL_VALUE }
        cim.currentLimit1?.also { currentLimit1Set = it } ?: run { currentLimit1Null = NullValue.NULL_VALUE }
        cim.inverseTimeFlag?.also { inverseTimeFlagSet = it } ?: run { inverseTimeFlagNull = NullValue.NULL_VALUE }
        cim.timeDelay1?.also { timeDelay1Set = it } ?: run { timeDelay1Null = NullValue.NULL_VALUE }
        toPb(cim, prfBuilder)
    }

/**
 * Convert the [DistanceRelay] into its protobuf counterpart.
 *
 * @param cim The [DistanceRelay] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: DistanceRelay, pb: PBDistanceRelay.Builder): PBDistanceRelay.Builder =
    pb.apply {
        cim.backwardBlind?.also { backwardBlindSet = it } ?: run { backwardBlindNull = NullValue.NULL_VALUE }
        cim.backwardReach?.also { backwardReachSet = it } ?: run { backwardReachNull = NullValue.NULL_VALUE }
        cim.backwardReactance?.also { backwardReactanceSet = it } ?: run { backwardReactanceNull = NullValue.NULL_VALUE }
        cim.forwardBlind?.also { forwardBlindSet = it } ?: run { forwardBlindNull = NullValue.NULL_VALUE }
        cim.forwardReach?.also { forwardReachSet = it } ?: run { forwardReachNull = NullValue.NULL_VALUE }
        cim.forwardReactance?.also { forwardReactanceSet = it } ?: run { forwardReactanceNull = NullValue.NULL_VALUE }
        cim.operationPhaseAngle1?.also { operationPhaseAngle1Set = it } ?: run { operationPhaseAngle1Null = NullValue.NULL_VALUE }
        cim.operationPhaseAngle2?.also { operationPhaseAngle2Set = it } ?: run { operationPhaseAngle2Null = NullValue.NULL_VALUE }
        cim.operationPhaseAngle3?.also { operationPhaseAngle3Set = it } ?: run { operationPhaseAngle3Null = NullValue.NULL_VALUE }
        toPb(cim, prfBuilder)
    }

/**
 * Convert the [ProtectionRelayFunction] into its protobuf counterpart.
 *
 * @param cim The [ProtectionRelayFunction] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: ProtectionRelayFunction, pb: PBProtectionRelayFunction.Builder): PBProtectionRelayFunction.Builder =
    pb.apply {
        cim.model?.also { modelSet = it } ?: run { modelNull = NullValue.NULL_VALUE }
        cim.reclosing?.also { reclosingSet = it } ?: run { reclosingNull = NullValue.NULL_VALUE }
        cim.relayDelayTime?.also { relayDelayTimeSet = it } ?: run { relayDelayTimeNull = NullValue.NULL_VALUE }
        protectionKind = mapProtectionKind.toPb(cim.protectionKind)
        cim.directable?.also { directableSet = it } ?: run { directableNull = NullValue.NULL_VALUE }
        powerDirection = mapPowerDirectionKind.toPb(cim.powerDirection)
        cim.timeLimits.forEach { addTimeLimits(it) }
        cim.thresholds.forEach { addThresholds(toPb(it)) }
        cim.protectedSwitches.forEach { addProtectedSwitchMRIDs(it.mRID) }
        cim.sensors.forEach { addSensorMRIDs(it.mRID) }
        cim.schemes.forEach { addSchemeMRIDs(it.mRID) }
        toPb(cim, psrBuilder)
    }

/**
 * Convert the [ProtectionRelayScheme] into its protobuf counterpart.
 *
 * @param cim The [ProtectionRelayScheme] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: ProtectionRelayScheme, pb: PBProtectionRelayScheme.Builder): PBProtectionRelayScheme.Builder =
    pb.apply {
        cim.system?.also { systemMRID = it.mRID } ?: clearSystemMRID()
        cim.functions.forEach { addFunctionMRIDs(it.mRID) }
        toPb(cim, ioBuilder)
    }

/**
 * Convert the [ProtectionRelaySystem] into its protobuf counterpart.
 *
 * @param cim The [ProtectionRelaySystem] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: ProtectionRelaySystem, pb: PBProtectionRelaySystem.Builder): PBProtectionRelaySystem.Builder =
    pb.apply {
        protectionKind = mapProtectionKind.toPb(cim.protectionKind)
        cim.schemes.forEach { addSchemeMRIDs(it.mRID) }
        toPb(cim, eqBuilder)
    }

/**
 * Convert the [RelaySetting] into its protobuf counterpart.
 *
 * @param cim The [RelaySetting] to convert.
 * @return The protobuf form of [cim].
 */
fun toPb(cim: RelaySetting): PBRelaySetting.Builder =
    PBRelaySetting.newBuilder().apply {
        unitSymbol = mapUnitSymbol.toPb(cim.unitSymbol)
        value = cim.value
        cim.name?.also { nameSet = it } ?: run { nameNull = NullValue.NULL_VALUE }
    }

/**
 * Convert the [VoltageRelay] into its protobuf counterpart.
 *
 * @param cim The [VoltageRelay] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: VoltageRelay, pb: PBVoltageRelay.Builder): PBVoltageRelay.Builder =
    pb.apply {
        toPb(cim, prfBuilder)
    }

/**
 * An extension for converting any [DirectionalCurrentRelay] into its protobuf counterpart.
 */
fun DirectionalCurrentRelay.toPb(): PBDirectionalCurrentRelay = toPb(this, PBDirectionalCurrentRelay.newBuilder()).build()

/**
 * An extension for converting any [DistanceRelay] into its protobuf counterpart.
 */
fun DistanceRelay.toPb(): PBDistanceRelay = toPb(this, PBDistanceRelay.newBuilder()).build()

/**
 * An extension for converting any [ProtectionRelayScheme] into its protobuf counterpart.
 */
fun ProtectionRelayScheme.toPb(): PBProtectionRelayScheme = toPb(this, PBProtectionRelayScheme.newBuilder()).build()

/**
 * An extension for converting any [ProtectionRelaySystem] into its protobuf counterpart.
 */
fun ProtectionRelaySystem.toPb(): PBProtectionRelaySystem = toPb(this, PBProtectionRelaySystem.newBuilder()).build()

/**
 * An extension for converting any [VoltageRelay] into its protobuf counterpart.
 */
fun VoltageRelay.toPb(): PBVoltageRelay = toPb(this, PBVoltageRelay.newBuilder()).build()

// ##################################
// # Extensions IEC61970 Base Wires #
// ##################################

/**
 * Convert the [BatteryControl] into its protobuf counterpart.
 *
 * @param cim The [BatteryControl] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: BatteryControl, pb: PBBatteryControl.Builder): PBBatteryControl.Builder =
    pb.apply {
        cim.chargingRate?.also { chargingRateSet = it } ?: run { chargingRateNull = NullValue.NULL_VALUE }
        cim.dischargingRate?.also { dischargingRateSet = it } ?: run { dischargingRateNull = NullValue.NULL_VALUE }
        cim.reservePercent?.also { reservePercentSet = it } ?: run { reservePercentNull = NullValue.NULL_VALUE }
        controlMode = mapBatteryControlMode.toPb(cim.controlMode)
        toPb(cim, rcBuilder)
    }

/**
 * An extension for converting any [BatteryControl] into its protobuf counterpart.
 */
fun BatteryControl.toPb(): PBBatteryControl = toPb(this, PBBatteryControl.newBuilder()).build()

// #######################
// # IEC61968 Asset Info #
// #######################

/**
 * Convert the [CableInfo] into its protobuf counterpart.
 *
 * @param cim The [CableInfo] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: CableInfo, pb: PBCableInfo.Builder): PBCableInfo.Builder =
    pb.apply { toPb(cim, wiBuilder) }

/**
 * Convert the [NoLoadTest] into its protobuf counterpart.
 *
 * @param cim The [NoLoadTest] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: NoLoadTest, pb: PBNoLoadTest.Builder): PBNoLoadTest.Builder =
    pb.apply {
        cim.energisedEndVoltage?.also { energisedEndVoltageSet = it } ?: run { energisedEndVoltageNull = NullValue.NULL_VALUE }
        cim.excitingCurrent?.also { excitingCurrentSet = it } ?: run { excitingCurrentNull = NullValue.NULL_VALUE }
        cim.excitingCurrentZero?.also { excitingCurrentZeroSet = it } ?: run { excitingCurrentZeroNull = NullValue.NULL_VALUE }
        cim.loss?.also { lossSet = it } ?: run { lossNull = NullValue.NULL_VALUE }
        cim.lossZero?.also { lossZeroSet = it } ?: run { lossZeroNull = NullValue.NULL_VALUE }
        toPb(cim, ttBuilder)
    }

/**
 * Convert the [OpenCircuitTest] into its protobuf counterpart.
 *
 * @param cim The [OpenCircuitTest] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: OpenCircuitTest, pb: PBOpenCircuitTest.Builder): PBOpenCircuitTest.Builder =
    pb.apply {
        cim.energisedEndStep?.also { energisedEndStepSet = it } ?: run { energisedEndStepNull = NullValue.NULL_VALUE }
        cim.energisedEndVoltage?.also { energisedEndVoltageSet = it } ?: run { energisedEndVoltageNull = NullValue.NULL_VALUE }
        cim.openEndStep?.also { openEndStepSet = it } ?: run { openEndStepNull = NullValue.NULL_VALUE }
        cim.openEndVoltage?.also { openEndVoltageSet = it } ?: run { openEndVoltageNull = NullValue.NULL_VALUE }
        cim.phaseShift?.also { phaseShiftSet = it } ?: run { phaseShiftNull = NullValue.NULL_VALUE }
        toPb(cim, ttBuilder)
    }

/**
 * Convert the [OverheadWireInfo] into its protobuf counterpart.
 *
 * @param cim The [OverheadWireInfo] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: OverheadWireInfo, pb: PBOverheadWireInfo.Builder): PBOverheadWireInfo.Builder =
    pb.apply { toPb(cim, wiBuilder) }

/**
 * Convert the [PowerTransformerInfo] into its protobuf counterpart.
 *
 * @param cim The [PowerTransformerInfo] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: PowerTransformerInfo, pb: PBPowerTransformerInfo.Builder): PBPowerTransformerInfo.Builder =
    pb.apply {
        clearTransformerTankInfoMRIDs()
        cim.transformerTankInfos.forEach { addTransformerTankInfoMRIDs(it.mRID) }
        toPb(cim, aiBuilder)
    }

/**
 * Convert the [ShortCircuitTest] into its protobuf counterpart.
 *
 * @param cim The [ShortCircuitTest] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: ShortCircuitTest, pb: PBShortCircuitTest.Builder): PBShortCircuitTest.Builder =
    pb.apply {
        cim.current?.also { currentSet = it } ?: run { currentNull = NullValue.NULL_VALUE }
        cim.energisedEndStep?.also { energisedEndStepSet = it } ?: run { energisedEndStepNull = NullValue.NULL_VALUE }
        cim.groundedEndStep?.also { groundedEndStepSet = it } ?: run { groundedEndStepNull = NullValue.NULL_VALUE }
        cim.leakageImpedance?.also { leakageImpedanceSet = it } ?: run { leakageImpedanceNull = NullValue.NULL_VALUE }
        cim.leakageImpedanceZero?.also { leakageImpedanceZeroSet = it } ?: run { leakageImpedanceZeroNull = NullValue.NULL_VALUE }
        cim.loss?.also { lossSet = it } ?: run { lossNull = NullValue.NULL_VALUE }
        cim.lossZero?.also { lossZeroSet = it } ?: run { lossZeroNull = NullValue.NULL_VALUE }
        cim.power?.also { powerSet = it } ?: run { powerNull = NullValue.NULL_VALUE }
        cim.voltage?.also { voltageSet = it } ?: run { voltageNull = NullValue.NULL_VALUE }
        cim.voltageOhmicPart?.also { voltageOhmicPartSet = it } ?: run { voltageOhmicPartNull = NullValue.NULL_VALUE }
        toPb(cim, ttBuilder)
    }

/**
 * Convert the [ShuntCompensatorInfo] into its protobuf counterpart.
 *
 * @param cim The [ShuntCompensatorInfo] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: ShuntCompensatorInfo, pb: PBShuntCompensatorInfo.Builder): PBShuntCompensatorInfo.Builder =
    pb.apply {
        cim.maxPowerLoss?.also { maxPowerLossSet = it } ?: run { maxPowerLossNull = NullValue.NULL_VALUE }
        cim.ratedCurrent?.also { ratedCurrentSet = it } ?: run { ratedCurrentNull = NullValue.NULL_VALUE }
        cim.ratedReactivePower?.also { ratedReactivePowerSet = it } ?: run { ratedReactivePowerNull = NullValue.NULL_VALUE }
        cim.ratedVoltage?.also { ratedVoltageSet = it } ?: run { ratedVoltageNull = NullValue.NULL_VALUE }

        toPb(cim, aiBuilder)
    }

/**
 * Convert the [SwitchInfo] into its protobuf counterpart.
 *
 * @param cim The [SwitchInfo] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: SwitchInfo, pb: PBSwitchInfo.Builder): PBSwitchInfo.Builder =
    pb.apply {
        cim.ratedInterruptingTime?.also { ratedInterruptingTimeSet = it } ?: run { ratedInterruptingTimeNull = NullValue.NULL_VALUE }
        toPb(cim, aiBuilder)
    }

/**
 * Convert the [TransformerEndInfo] into its protobuf counterpart.
 *
 * @param cim The [TransformerEndInfo] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: TransformerEndInfo, pb: PBTransformerEndInfo.Builder): PBTransformerEndInfo.Builder =
    pb.apply {
        connectionKind = mapWindingConnection.toPb(cim.connectionKind)
        cim.emergencyS?.also { emergencySSet = it } ?: run { emergencySNull = NullValue.NULL_VALUE }
        endNumber = cim.endNumber
        cim.insulationU?.also { insulationUSet = it } ?: run { insulationUNull = NullValue.NULL_VALUE }
        cim.phaseAngleClock?.also { phaseAngleClockSet = it } ?: run { phaseAngleClockNull = NullValue.NULL_VALUE }
        cim.r?.also { rSet = it } ?: run { rNull = NullValue.NULL_VALUE }
        cim.ratedS?.also { ratedSSet = it } ?: run { ratedSNull = NullValue.NULL_VALUE }
        cim.ratedU?.also { ratedUSet = it } ?: run { ratedUNull = NullValue.NULL_VALUE }
        cim.shortTermS?.also { shortTermSSet = it } ?: run { shortTermSNull = NullValue.NULL_VALUE }

        cim.transformerTankInfo?.also { transformerTankInfoMRID = it.mRID } ?: clearTransformerTankInfoMRID()
        cim.transformerStarImpedance?.also { transformerStarImpedanceMRID = it.mRID } ?: clearTransformerStarImpedanceMRID()
        cim.energisedEndNoLoadTests?.also { energisedEndNoLoadTestsMRID = it.mRID } ?: clearEnergisedEndNoLoadTestsMRID()
        cim.energisedEndShortCircuitTests?.also { energisedEndShortCircuitTestsMRID = it.mRID } ?: clearEnergisedEndShortCircuitTestsMRID()
        cim.groundedEndShortCircuitTests?.also { groundedEndShortCircuitTestsMRID = it.mRID } ?: clearGroundedEndShortCircuitTestsMRID()
        cim.openEndOpenCircuitTests?.also { openEndOpenCircuitTestsMRID = it.mRID } ?: clearOpenEndOpenCircuitTestsMRID()
        cim.energisedEndOpenCircuitTests?.also { energisedEndOpenCircuitTestsMRID = it.mRID } ?: clearEnergisedEndOpenCircuitTestsMRID()

        toPb(cim, aiBuilder)
    }

/**
 * Convert the [TransformerTankInfo] into its protobuf counterpart.
 *
 * @param cim The [TransformerTankInfo] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: TransformerTankInfo, pb: PBTransformerTankInfo.Builder): PBTransformerTankInfo.Builder =
    pb.apply {
        cim.powerTransformerInfo?.also { powerTransformerInfoMRID = it.mRID } ?: clearPowerTransformerInfoMRID()
        clearTransformerEndInfoMRIDs()
        cim.transformerEndInfos.forEach { addTransformerEndInfoMRIDs(it.mRID) }
        toPb(cim, aiBuilder)
    }

/**
 * Convert the [TransformerTest] into its protobuf counterpart.
 *
 * @param cim The [TransformerTest] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: TransformerTest, pb: PBTransformerTest.Builder): PBTransformerTest.Builder =
    pb.apply {
        cim.basePower?.also { basePowerSet = it } ?: run { basePowerNull = NullValue.NULL_VALUE }
        cim.temperature?.also { temperatureSet = it } ?: run { temperatureNull = NullValue.NULL_VALUE }
        toPb(cim, ioBuilder)
    }

/**
 * Convert the [WireInfo] into its protobuf counterpart.
 *
 * @param cim The [WireInfo] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: WireInfo, pb: PBWireInfo.Builder): PBWireInfo.Builder =
    pb.apply {
        cim.ratedCurrent?.also { ratedCurrentSet = it } ?: run { ratedCurrentNull = NullValue.NULL_VALUE }
        material = mapWireMaterialKind.toPb(cim.material)
        cim.sizeDescription?.also { sizeDescriptionSet = it } ?: run { sizeDescriptionNull = NullValue.NULL_VALUE }
        cim.strandCount?.also { strandCountSet = it } ?: run { strandCountNull = NullValue.NULL_VALUE }
        cim.coreStrandCount?.also { coreStrandCountSet = it } ?: run { coreStrandCountNull = NullValue.NULL_VALUE }
        cim.insulated?.also { insulatedSet = it } ?: run { insulatedNull = NullValue.NULL_VALUE }
        insulationMaterial = mapWireInsulationKind.toPb(cim.insulationMaterial)
        cim.insulationThickness?.also { insulationThicknessSet = it } ?: run { insulationThicknessNull = NullValue.NULL_VALUE }

        toPb(cim, aiBuilder)
    }

/**
 * An extension for converting any [CableInfo] into its protobuf counterpart.
 */
fun CableInfo.toPb(): PBCableInfo = toPb(this, PBCableInfo.newBuilder()).build()

/**
 * An extension for converting any [NoLoadTest] into its protobuf counterpart.
 */
fun NoLoadTest.toPb(): PBNoLoadTest = toPb(this, PBNoLoadTest.newBuilder()).build()

/**
 * An extension for converting any [OpenCircuitTest] into its protobuf counterpart.
 */
fun OpenCircuitTest.toPb(): PBOpenCircuitTest = toPb(this, PBOpenCircuitTest.newBuilder()).build()

/**
 * An extension for converting any [OverheadWireInfo] into its protobuf counterpart.
 */
fun OverheadWireInfo.toPb(): PBOverheadWireInfo = toPb(this, PBOverheadWireInfo.newBuilder()).build()

/**
 * An extension for converting any [PowerTransformerInfo] into its protobuf counterpart.
 */
fun PowerTransformerInfo.toPb(): PBPowerTransformerInfo = toPb(this, PBPowerTransformerInfo.newBuilder()).build()

/**
 * An extension for converting any [ShortCircuitTest] into its protobuf counterpart.
 */
fun ShortCircuitTest.toPb(): PBShortCircuitTest = toPb(this, PBShortCircuitTest.newBuilder()).build()

/**
 * An extension for converting any [ShuntCompensatorInfo] into its protobuf counterpart.
 */
fun ShuntCompensatorInfo.toPb(): PBShuntCompensatorInfo = toPb(this, PBShuntCompensatorInfo.newBuilder()).build()

/**
 * An extension for converting any [SwitchInfo] into its protobuf counterpart.
 */
fun SwitchInfo.toPb(): PBSwitchInfo = toPb(this, PBSwitchInfo.newBuilder()).build()

/**
 * An extension for converting any [TransformerEndInfo] into its protobuf counterpart.
 */
fun TransformerEndInfo.toPb(): PBTransformerEndInfo = toPb(this, PBTransformerEndInfo.newBuilder()).build()

/**
 * An extension for converting any [TransformerTankInfo] into its protobuf counterpart.
 */
fun TransformerTankInfo.toPb(): PBTransformerTankInfo = toPb(this, PBTransformerTankInfo.newBuilder()).build()

// ###################
// # IEC61968 Assets #
// ###################

/**
 * Convert the [Asset] into its protobuf counterpart.
 *
 * @param cim The [Asset] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: Asset, pb: PBAsset.Builder): PBAsset.Builder =
    pb.apply {
        cim.location?.also { locationMRID = it.mRID } ?: clearLocationMRID()
        clearOrganisationRoleMRIDs()
        cim.organisationRoles.forEach { addOrganisationRoleMRIDs(it.mRID) }
        cim.powerSystemResources.forEach { addPowerSystemResourceMRIDs(it.mRID) }
        toPb(cim, ioBuilder)
    }

/**
 * Convert the [AssetContainer] into its protobuf counterpart.
 *
 * @param cim The [AssetContainer] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: AssetContainer, pb: PBAssetContainer.Builder): PBAssetContainer.Builder =
    pb.apply { toPb(cim, atBuilder) }

/**
 * Convert the [AssetFunction] into its protobuf counterpart.
 *
 * @param cim The [AssetFunction] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: AssetFunction, pb: PBAssetFunction.Builder): PBAssetFunction.Builder =
    pb.apply { toPb(cim, ioBuilder) }

/**
 * Convert the [AssetInfo] into its protobuf counterpart.
 *
 * @param cim The [AssetInfo] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: AssetInfo, pb: PBAssetInfo.Builder): PBAssetInfo.Builder =
    pb.apply { toPb(cim, ioBuilder) }

/**
 * Convert the [AssetOrganisationRole] into its protobuf counterpart.
 *
 * @param cim The [AssetOrganisationRole] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: AssetOrganisationRole, pb: PBAssetOrganisationRole.Builder): PBAssetOrganisationRole.Builder =
    pb.apply { toPb(cim, orBuilder) }

/**
 * Convert the [AssetOwner] into its protobuf counterpart.
 *
 * @param cim The [AssetOwner] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: AssetOwner, pb: PBAssetOwner.Builder): PBAssetOwner.Builder =
    pb.apply { toPb(cim, aorBuilder) }

/**
 * Convert the [Streetlight] into its protobuf counterpart.
 *
 * @param cim The [Streetlight] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: Streetlight, pb: PBStreetlight.Builder): PBStreetlight.Builder =
    pb.apply {
        cim.lightRating?.also { lightRatingSet = it } ?: run { lightRatingNull = NullValue.NULL_VALUE }
        lampKind = mapStreetlightLampKind.toPb(cim.lampKind)
        cim.pole?.also { poleMRID = it.mRID } ?: clearPoleMRID()
        toPb(cim, atBuilder)
    }

/**
 * Convert the [Structure] into its protobuf counterpart.
 *
 * @param cim The [Structure] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: Structure, pb: PBStructure.Builder): PBStructure.Builder =
    pb.apply { toPb(cim, acBuilder) }

/**
 * An extension for converting any [AssetOwner] into its protobuf counterpart.
 */
fun AssetOwner.toPb(): PBAssetOwner = toPb(this, PBAssetOwner.newBuilder()).build()

/**
 * An extension for converting any [Streetlight] into its protobuf counterpart.
 */
fun Streetlight.toPb(): PBStreetlight = toPb(this, PBStreetlight.newBuilder()).build()

// ###################
// # IEC61968 Common #
// ###################

/**
 * Convert the [ElectronicAddress] into its protobuf counterpart.
 *
 * @param cim The [ElectronicAddress] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: ElectronicAddress, pb: PBElectronicAddress.Builder): PBElectronicAddress.Builder =
    pb.apply {
        cim.email1?.also { email1Set = it } ?: run { email1Null = NullValue.NULL_VALUE }
        cim.isPrimary?.also { isPrimarySet = it } ?: run { isPrimaryNull = NullValue.NULL_VALUE }
        cim.description?.also { descriptionSet = it } ?: run { descriptionNull = NullValue.NULL_VALUE }
    }

/**
 * Convert the [Location] into its protobuf counterpart.
 *
 * @param cim The [Location] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: Location, pb: PBLocation.Builder): PBLocation.Builder =
    pb.apply {
        cim.mainAddress?.also { toPb(it, mainAddressBuilder) } ?: clearMainAddress()
        clearPositionPoints()
        cim.points.forEachIndexed { i, point -> addPositionPointsBuilder(i).apply { toPb(point, this) } }
        toPb(cim, ioBuilder)
    }

/**
 * Convert the [PositionPoint] into its protobuf counterpart.
 *
 * @param cim The [PositionPoint] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: PositionPoint, pb: PBPositionPoint.Builder): PBPositionPoint.Builder =
    pb.apply {
        xPosition = cim.xPosition
        yPosition = cim.yPosition
    }

/**
 * Convert the [StreetAddress] into its protobuf counterpart.
 *
 * @param cim The [StreetAddress] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: StreetAddress, pb: PBStreetAddress.Builder): PBStreetAddress.Builder =
    pb.apply {
        cim.postalCode?.also { postalCodeSet = it } ?: run { postalCodeNull = NullValue.NULL_VALUE }
        cim.townDetail?.also { toPb(it, townDetailBuilder) } ?: clearTownDetail()
        cim.poBox?.also { poBoxSet = it } ?: run { poBoxNull = NullValue.NULL_VALUE }
        cim.streetDetail?.also { toPb(it, streetDetailBuilder) } ?: clearStreetDetail()
    }

/**
 * Convert the [StreetDetail] into its protobuf counterpart.
 *
 * @param cim The [StreetDetail] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: StreetDetail, pb: PBStreetDetail.Builder): PBStreetDetail.Builder =
    pb.apply {
        cim.buildingName?.also { buildingNameSet = it } ?: run { buildingNameNull = NullValue.NULL_VALUE }
        cim.floorIdentification?.also { floorIdentificationSet = it } ?: run { floorIdentificationNull = NullValue.NULL_VALUE }
        cim.name?.also { nameSet = it } ?: run { nameNull = NullValue.NULL_VALUE }
        cim.number?.also { numberSet = it } ?: run { numberNull = NullValue.NULL_VALUE }
        cim.suiteNumber?.also { suiteNumberSet = it } ?: run { suiteNumberNull = NullValue.NULL_VALUE }
        cim.type?.also { typeSet = it } ?: run { typeNull = NullValue.NULL_VALUE }
        cim.displayAddress?.also { displayAddressSet = it } ?: run { displayAddressNull = NullValue.NULL_VALUE }
        cim.buildingNumber?.also { buildingNumberSet = it } ?: run { buildingNumberNull = NullValue.NULL_VALUE }
    }

/**
 * Convert the [TelephoneNumber] into its protobuf counterpart.
 *
 * @param cim The [TelephoneNumber] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: TelephoneNumber, pb: PBTelephoneNumber.Builder): PBTelephoneNumber.Builder =
    pb.apply {
        cim.areaCode?.also { areaCodeSet = it } ?: run { areaCodeNull = NullValue.NULL_VALUE }
        cim.cityCode?.also { cityCodeSet = it } ?: run { cityCodeNull = NullValue.NULL_VALUE }
        cim.countryCode?.also { countryCodeSet = it } ?: run { countryCodeNull = NullValue.NULL_VALUE }
        cim.dialOut?.also { dialOutSet = it } ?: run { dialOutNull = NullValue.NULL_VALUE }
        cim.extension?.also { extensionSet = it } ?: run { extensionNull = NullValue.NULL_VALUE }
        cim.internationalPrefix?.also { internationalPrefixSet = it } ?: run { internationalPrefixNull = NullValue.NULL_VALUE }
        cim.localNumber?.also { localNumberSet = it } ?: run { localNumberNull = NullValue.NULL_VALUE }
        cim.isPrimary?.also { isPrimarySet = it } ?: run { isPrimaryNull = NullValue.NULL_VALUE }
        cim.description?.also { descriptionSet = it } ?: run { descriptionNull = NullValue.NULL_VALUE }
    }

/**
 * Convert the [TownDetail] into its protobuf counterpart.
 *
 * @param cim The [TownDetail] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: TownDetail, pb: PBTownDetail.Builder): PBTownDetail.Builder =
    pb.apply {
        cim.name?.also { nameSet = it } ?: run { nameNull = NullValue.NULL_VALUE }
        cim.stateOrProvince?.also { stateOrProvinceSet = it } ?: run { stateOrProvinceNull = NullValue.NULL_VALUE }
        cim.country?.also { countrySet = it } ?: run { countryNull = NullValue.NULL_VALUE }
    }

/**
 * An extension for converting any [ElectronicAddress] into its protobuf counterpart.
 */
fun ElectronicAddress.toPb(): PBElectronicAddress = toPb(this, PBElectronicAddress.newBuilder()).build()

/**
 * An extension for converting any [Location] into its protobuf counterpart.
 */
fun Location.toPb(): PBLocation = toPb(this, PBLocation.newBuilder()).build()

/**
 * An extension for converting any [TelephoneNumber] into its protobuf counterpart.
 */
fun TelephoneNumber.toPb(): PBTelephoneNumber = toPb(this, PBTelephoneNumber.newBuilder()).build()

// #####################################
// # IEC61968 infIEC61968 InfAssetInfo #
// #####################################

/**
 * Convert the [CurrentTransformerInfo] into its protobuf counterpart.
 *
 * @param cim The [CurrentTransformerInfo] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: CurrentTransformerInfo, pb: PBCurrentTransformerInfo.Builder): PBCurrentTransformerInfo.Builder =
    pb.apply {
        cim.accuracyClass?.also { accuracyClassSet = it } ?: run { accuracyClassNull = NullValue.NULL_VALUE }
        cim.accuracyLimit?.also { accuracyLimitSet = it } ?: run { accuracyLimitNull = NullValue.NULL_VALUE }
        cim.coreCount?.also { coreCountSet = it } ?: run { coreCountNull = NullValue.NULL_VALUE }
        cim.ctClass?.also { ctClassSet = it } ?: run { ctClassNull = NullValue.NULL_VALUE }
        cim.kneePointVoltage?.also { kneePointVoltageSet = it } ?: run { kneePointVoltageNull = NullValue.NULL_VALUE }
        cim.maxRatio?.also { toPb(it, maxRatioBuilder) } ?: clearMaxRatio()
        cim.nominalRatio?.also { toPb(it, nominalRatioBuilder) } ?: clearNominalRatio()
        cim.primaryRatio?.also { primaryRatioSet = it } ?: run { primaryRatioNull = NullValue.NULL_VALUE }
        cim.ratedCurrent?.also { ratedCurrentSet = it } ?: run { ratedCurrentNull = NullValue.NULL_VALUE }
        cim.secondaryFlsRating?.also { secondaryFlsRatingSet = it } ?: run { secondaryFlsRatingNull = NullValue.NULL_VALUE }
        cim.secondaryRatio?.also { secondaryRatioSet = it } ?: run { secondaryRatioNull = NullValue.NULL_VALUE }
        cim.usage?.also { usageSet = it } ?: run { usageNull = NullValue.NULL_VALUE }
        toPb(cim, aiBuilder)
    }

/**
 * Convert the [PotentialTransformerInfo] into its protobuf counterpart.
 *
 * @param cim The [PotentialTransformerInfo] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: PotentialTransformerInfo, pb: PBPotentialTransformerInfo.Builder): PBPotentialTransformerInfo.Builder =
    pb.apply {
        cim.accuracyClass?.also { accuracyClassSet = it } ?: run { accuracyClassNull = NullValue.NULL_VALUE }
        cim.nominalRatio?.also { toPb(it, nominalRatioBuilder) } ?: clearNominalRatio()
        cim.primaryRatio?.also { primaryRatioSet = it } ?: run { primaryRatioNull = NullValue.NULL_VALUE }
        cim.ptClass?.also { ptClassSet = it } ?: run { ptClassNull = NullValue.NULL_VALUE }
        cim.ratedVoltage?.also { ratedVoltageSet = it } ?: run { ratedVoltageNull = NullValue.NULL_VALUE }
        cim.secondaryRatio?.also { secondaryRatioSet = it } ?: run { secondaryRatioNull = NullValue.NULL_VALUE }
        toPb(cim, aiBuilder)
    }

/**
 * An extension for converting any [CurrentTransformerInfo] into its protobuf counterpart.
 */
fun CurrentTransformerInfo.toPb(): PBCurrentTransformerInfo = toPb(this, PBCurrentTransformerInfo.newBuilder()).build()

/**
 * An extension for converting any [PotentialTransformerInfo] into its protobuf counterpart.
 */
fun PotentialTransformerInfo.toPb(): PBPotentialTransformerInfo = toPb(this, PBPotentialTransformerInfo.newBuilder()).build()

// ##################################
// # IEC61968 infIEC61968 InfAssets #
// ##################################

/**
 * Convert the [Pole] into its protobuf counterpart.
 *
 * @param cim The [Pole] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: Pole, pb: PBPole.Builder): PBPole.Builder =
    pb.apply {
        cim.classification?.also { classificationSet = it } ?: run { classificationNull = NullValue.NULL_VALUE }
        clearStreetlightMRIDs()
        cim.streetlights.forEach { addStreetlightMRIDs(it.mRID) }
        toPb(cim, stBuilder)
    }

/**
 * An extension for converting any [Pole] into its protobuf counterpart.
 */
fun Pole.toPb(): PBPole = toPb(this, PBPole.newBuilder()).build()

// ##################################
// # IEC61968 infIEC61968 InfCommon #
// ##################################

/**
 * Convert the [Ratio] into its protobuf counterpart.
 *
 * @param cim The [Ratio] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: Ratio, pb: PBRatio.Builder): PBRatio.Builder =
    pb.apply {
        denominator = cim.denominator
        numerator = cim.numerator
    }

// #####################
// # IEC61968 Metering #
// #####################

/**
 * Convert the [EndDevice] into its protobuf counterpart.
 *
 * @param cim The [EndDevice] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: EndDevice, pb: PBEndDevice.Builder): PBEndDevice.Builder =
    pb.apply {
        clearUsagePointMRIDs()
        cim.usagePoints.forEach { addUsagePointMRIDs(it.mRID) }
        cim.customerMRID?.also { customerMRID = it } ?: clearCustomerMRID()
        cim.serviceLocation?.also { serviceLocationMRID = it.mRID } ?: clearServiceLocationMRID()
        cim.functions.forEach { addEndDeviceFunctionMRIDs(it.mRID) }
        toPb(cim, acBuilder)
    }

/**
 * Convert the [EndDeviceFunction] into its protobuf counterpart.
 *
 * @param cim The [EndDeviceFunction] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: EndDeviceFunction, pb: PBEndDeviceFunction.Builder): PBEndDeviceFunction.Builder =
    pb.apply {
        cim.enabled?.also { enabledSet = it } ?: run { enabledNull = NullValue.NULL_VALUE }
        toPb(cim, afBuilder)
    }

/**
 * Convert the [Meter] into its protobuf counterpart.
 *
 * @param cim The [Meter] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: Meter, pb: PBMeter.Builder): PBMeter.Builder =
    pb.apply {
        toPb(cim, edBuilder)
    }

/**
 * Convert the [UsagePoint] into its protobuf counterpart.
 *
 * @param cim The [UsagePoint] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: UsagePoint, pb: PBUsagePoint.Builder): PBUsagePoint.Builder =
    pb.apply {
        cim.usagePointLocation?.also { usagePointLocationMRID = it.mRID } ?: clearUsagePointLocationMRID()
        cim.isVirtual?.also { isVirtualSet = it } ?: run { isVirtualNull = NullValue.NULL_VALUE }
        cim.ratedPower?.also { ratedPowerSet = it } ?: run { ratedPowerNull = NullValue.NULL_VALUE }
        cim.approvedInverterCapacity?.also { approvedInverterCapacitySet = it } ?: run { approvedInverterCapacityNull = NullValue.NULL_VALUE }
        phaseCode = mapPhaseCode.toPb(cim.phaseCode)
        cim.connectionCategory?.also { connectionCategorySet = it } ?: run { connectionCategoryNull = NullValue.NULL_VALUE }
        clearEquipmentMRIDs()
        cim.equipment.forEach { addEquipmentMRIDs(it.mRID) }
        clearEndDeviceMRIDs()
        cim.endDevices.forEach { addEndDeviceMRIDs(it.mRID) }
        clearContacts()
        cim.contacts.forEach { addContacts(it.toPb()) }
        toPb(cim, ioBuilder)
    }

/**
 * An extension for converting any [Meter] into its protobuf counterpart.
 */
fun Meter.toPb(): PBMeter = toPb(this, PBMeter.newBuilder()).build()

/**
 * An extension for converting any [UsagePoint] into its protobuf counterpart.
 */
fun UsagePoint.toPb(): PBUsagePoint = toPb(this, PBUsagePoint.newBuilder()).build()

// #######################
// # IEC61968 Operations #
// #######################

/**
 * Convert the [OperationalRestriction] into its protobuf counterpart.
 *
 * @param cim The [OperationalRestriction] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: OperationalRestriction, pb: PBOperationalRestriction.Builder): PBOperationalRestriction.Builder =
    pb.apply { toPb(cim, docBuilder) }

/**
 * An extension for converting any [OperationalRestriction] into its protobuf counterpart.
 */
fun OperationalRestriction.toPb(): PBOperationalRestriction = toPb(this, PBOperationalRestriction.newBuilder()).build()

// #####################################
// # IEC61970 Base Auxiliary Equipment #
// #####################################

/**
 * Convert the [AuxiliaryEquipment] into its protobuf counterpart.
 *
 * @param cim The [AuxiliaryEquipment] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: AuxiliaryEquipment, pb: PBAuxiliaryEquipment.Builder): PBAuxiliaryEquipment.Builder =
    pb.apply {
        cim.terminal?.also { terminalMRID = it.mRID } ?: clearTerminalMRID()
        toPb(cim, eqBuilder)
    }

/**
 * Convert the [CurrentTransformer] into its protobuf counterpart.
 *
 * @param cim The [CurrentTransformer] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: CurrentTransformer, pb: PBCurrentTransformer.Builder): PBCurrentTransformer.Builder =
    pb.apply {
        cim.coreBurden?.also { coreBurdenSet = it } ?: run { coreBurdenNull = NullValue.NULL_VALUE }
        toPb(cim, snBuilder)
    }

/**
 * Convert the [FaultIndicator] into its protobuf counterpart.
 *
 * @param cim The [FaultIndicator] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: FaultIndicator, pb: PBFaultIndicator.Builder): PBFaultIndicator.Builder =
    pb.apply { toPb(cim, aeBuilder) }

/**
 * Convert the [PotentialTransformer] into its protobuf counterpart.
 *
 * @param cim The [PotentialTransformer] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: PotentialTransformer, pb: PBPotentialTransformer.Builder): PBPotentialTransformer.Builder =
    pb.apply {
        type = mapPotentialTransformerKind.toPb(cim.type)
        toPb(cim, snBuilder)
    }

/**
 * Convert the [Sensor] into its protobuf counterpart.
 *
 * @param cim The [Sensor] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: Sensor, pb: PBSensor.Builder): PBSensor.Builder =
    pb.apply {
        cim.relayFunctions.forEach { addRelayFunctionMRIDs(it.mRID) }
        toPb(cim, aeBuilder)
    }

/**
 * An extension for converting any [CurrentTransformer] into its protobuf counterpart.
 */
fun CurrentTransformer.toPb(): PBCurrentTransformer = toPb(this, PBCurrentTransformer.newBuilder()).build()

/**
 * An extension for converting any [FaultIndicator] into its protobuf counterpart.
 */
fun FaultIndicator.toPb(): PBFaultIndicator = toPb(this, PBFaultIndicator.newBuilder()).build()

/**
 * An extension for converting any [PotentialTransformer] into its protobuf counterpart.
 */
fun PotentialTransformer.toPb(): PBPotentialTransformer = toPb(this, PBPotentialTransformer.newBuilder()).build()

// ######################
// # IEC61970 Base Core #
// ######################

/**
 * Convert the [AcDcTerminal] into its protobuf counterpart.
 *
 * @param cim The [AcDcTerminal] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: AcDcTerminal, pb: PBAcDcTerminal.Builder): PBAcDcTerminal.Builder =
    pb.apply { toPb(cim, ioBuilder) }

/**
 * Convert the [BaseVoltage] into its protobuf counterpart.
 *
 * @param cim The [BaseVoltage] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: BaseVoltage, pb: PBBaseVoltage.Builder): PBBaseVoltage.Builder =
    pb.apply {
        nominalVoltage = cim.nominalVoltage
        toPb(cim, ioBuilder)
    }

/**
 * Convert the [ConductingEquipment] into its protobuf counterpart.
 *
 * @param cim The [ConductingEquipment] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: ConductingEquipment, pb: PBConductingEquipment.Builder): PBConductingEquipment.Builder =
    pb.apply {
        cim.baseVoltage?.also { baseVoltageMRID = it.mRID } ?: clearBaseVoltageMRID()
        clearTerminalMRIDs()
        cim.terminals.forEach { addTerminalMRIDs(it.mRID) }
        toPb(cim, eqBuilder)
    }

/**
 * Convert the [ConnectivityNode] into its protobuf counterpart.
 *
 * @param cim The [ConnectivityNode] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: ConnectivityNode, pb: PBConnectivityNode.Builder): PBConnectivityNode.Builder =
    pb.apply { toPb(cim, ioBuilder) }

/**
 * Convert the [ConnectivityNodeContainer] into its protobuf counterpart.
 *
 * @param cim The [ConnectivityNodeContainer] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: ConnectivityNodeContainer, pb: PBConnectivityNodeContainer.Builder): PBConnectivityNodeContainer.Builder =
    pb.apply { toPb(cim, psrBuilder) }

/**
 * Convert the [Curve] into its protobuf counterpart.
 *
 * @param cim The [Curve] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: Curve, pb: PBCurve.Builder): PBCurve.Builder =
    pb.apply {
        cim.data.forEachIndexed { i, data -> addCurveDataBuilder(i).apply { toPb(data, this) } }
        toPb(cim, ioBuilder)
    }

/**
 * Convert the [CurveData] into its protobuf counterpart.
 *
 * @param cim The [CurveData] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: CurveData, pb: PBCurveData.Builder): PBCurveData.Builder =
    pb.apply {
        xValue = cim.xValue
        y1Value = cim.y1Value
        cim.y2Value?.also { y2ValueSet = it } ?: run { y2ValueNull = NullValue.NULL_VALUE }
        cim.y3Value?.also { y3ValueSet = it } ?: run { y3ValueNull = NullValue.NULL_VALUE }
    }

/**
 * Convert the [Equipment] into its protobuf counterpart.
 *
 * @param cim The [Equipment] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: Equipment, pb: PBEquipment.Builder): PBEquipment.Builder =
    pb.apply {
        inService = cim.inService
        normallyInService = cim.normallyInService

        cim.commissionedDate?.also { commissionedDateSet = it.toTimestamp() } ?: run { commissionedDateNull = NullValue.NULL_VALUE }

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

/**
 * Convert the [EquipmentContainer] into its protobuf counterpart.
 *
 * @param cim The [EquipmentContainer] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: EquipmentContainer, pb: PBEquipmentContainer.Builder): PBEquipmentContainer.Builder =
    pb.apply {
        toPb(cim, cncBuilder)
    }

/**
 * Convert the [Feeder] into its protobuf counterpart.
 *
 * @param cim The [Feeder] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: Feeder, pb: PBFeeder.Builder): PBFeeder.Builder =
    pb.apply {
        cim.normalHeadTerminal?.also { normalHeadTerminalMRID = it.mRID } ?: clearNormalHeadTerminalMRID()
        cim.normalEnergizingSubstation?.also { normalEnergizingSubstationMRID = it.mRID } ?: clearNormalEnergizingSubstationMRID()

        clearNormalEnergizedLvFeederMRIDs()
        cim.normalEnergizedLvFeeders.forEach { addNormalEnergizedLvFeederMRIDs(it.mRID) }
        clearCurrentlyEnergizedLvFeedersMRIDs()
        cim.currentEnergizedLvFeeders.forEach { addCurrentlyEnergizedLvFeedersMRIDs(it.mRID) }

        clearNormalEnergizedLvSubstationMRIDs()
        cim.normalEnergizedLvSubstations.forEach { addNormalEnergizedLvSubstationMRIDs(it.mRID) }
        clearCurrentlyEnergizedLvSubstationMRIDs()
        cim.currentEnergizedLvSubstations.forEach { addCurrentlyEnergizedLvSubstationMRIDs(it.mRID) }

        toPb(cim, ecBuilder)
    }

/**
 * Convert the [GeographicalRegion] into its protobuf counterpart.
 *
 * @param cim The [GeographicalRegion] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: GeographicalRegion, pb: PBGeographicalRegion.Builder): PBGeographicalRegion.Builder =
    pb.apply {
        clearSubGeographicalRegionMRIDs()
        cim.subGeographicalRegions.forEach { addSubGeographicalRegionMRIDs(it.mRID) }
        toPb(cim, ioBuilder)
    }

/**
 * Convert the [PowerSystemResource] into its protobuf counterpart.
 *
 * @param cim The [PowerSystemResource] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: PowerSystemResource, pb: PBPowerSystemResource.Builder): PBPowerSystemResource.Builder =
    pb.apply {
        cim.location?.also { locationMRID = it.mRID } ?: clearLocationMRID()
        cim.assetInfo?.also { assetInfoMRID = it.mRID } ?: clearAssetInfoMRID()
        cim.assets.forEach { addAssetMRIDs(it.mRID) }

        cim.numControls?.also { numControlsSet = it } ?: run { numControlsNull = NullValue.NULL_VALUE }
        toPb(cim, ioBuilder)
    }

/**
 * Convert the [SubGeographicalRegion] into its protobuf counterpart.
 *
 * @param cim The [SubGeographicalRegion] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: SubGeographicalRegion, pb: PBSubGeographicalRegion.Builder): PBSubGeographicalRegion.Builder =
    pb.apply {
        cim.geographicalRegion?.also { geographicalRegionMRID = it.mRID } ?: clearGeographicalRegionMRID()
        clearSubstationMRIDs()
        cim.substations.forEach { addSubstationMRIDs(it.mRID) }
        toPb(cim, ioBuilder)
    }

/**
 * Convert the [Substation] into its protobuf counterpart.
 *
 * @param cim The [Substation] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: Substation, pb: PBSubstation.Builder): PBSubstation.Builder =
    pb.apply {
        cim.subGeographicalRegion?.also { subGeographicalRegionMRID = it.mRID } ?: clearSubGeographicalRegionMRID()
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

/**
 * Convert the [Terminal] into its protobuf counterpart.
 *
 * @param cim The [Terminal] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: Terminal, pb: PBTerminal.Builder): PBTerminal.Builder =
    pb.apply {
        cim.conductingEquipment?.also { conductingEquipmentMRID = it.mRID } ?: clearConductingEquipmentMRID()
        cim.connectivityNodeId?.also { connectivityNodeMRID = it } ?: clearConnectivityNodeMRID()
        phases = mapPhaseCode.toPb(cim.phases)
        sequenceNumber = cim.sequenceNumber
        normalFeederDirection = mapFeederDirection.toPb(cim.normalFeederDirection)
        currentFeederDirection = mapFeederDirection.toPb(cim.currentFeederDirection)
        tracedPhases = (cim.currentPhases.phaseStatusInternal.toInt() shl 16) + cim.normalPhases.phaseStatusInternal.toInt()
        toPb(cim, adBuilder)
    }

/**
 * An extension for converting any [BaseVoltage] into its protobuf counterpart.
 */
fun BaseVoltage.toPb(): PBBaseVoltage = toPb(this, PBBaseVoltage.newBuilder()).build()

/**
 * An extension for converting any [ConnectivityNode] into its protobuf counterpart.
 */
fun ConnectivityNode.toPb(): PBConnectivityNode = toPb(this, PBConnectivityNode.newBuilder()).build()

/**
 * An extension for converting any [CurveData] into its protobuf counterpart.
 */
fun CurveData.toPb(): PBCurveData = toPb(this, PBCurveData.newBuilder()).build()

/**
 * An extension for converting any [Feeder] into its protobuf counterpart.
 */
fun Feeder.toPb(): PBFeeder = toPb(this, PBFeeder.newBuilder()).build()

/**
 * An extension for converting any [GeographicalRegion] into its protobuf counterpart.
 */
fun GeographicalRegion.toPb(): PBGeographicalRegion = toPb(this, PBGeographicalRegion.newBuilder()).build()

/**
 * An extension for converting any [SubGeographicalRegion] into its protobuf counterpart.
 */
fun SubGeographicalRegion.toPb(): PBSubGeographicalRegion = toPb(this, PBSubGeographicalRegion.newBuilder()).build()

/**
 * An extension for converting any [Substation] into its protobuf counterpart.
 */
fun Substation.toPb(): PBSubstation = toPb(this, PBSubstation.newBuilder()).build()

/**
 * An extension for converting any [Terminal] into its protobuf counterpart.
 */
fun Terminal.toPb(): PBTerminal = toPb(this, PBTerminal.newBuilder()).build()

// #############################
// # IEC61970 Base Equivalents #
// #############################

/**
 * Convert the [EquivalentBranch] into its protobuf counterpart.
 *
 * @param cim The [EquivalentBranch] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: EquivalentBranch, pb: PBEquivalentBranch.Builder): PBEquivalentBranch.Builder =
    pb.apply {
        cim.negativeR12?.also { negativeR12Set = it } ?: run { negativeR12Null = NullValue.NULL_VALUE }
        cim.negativeR21?.also { negativeR21Set = it } ?: run { negativeR21Null = NullValue.NULL_VALUE }
        cim.negativeX12?.also { negativeX12Set = it } ?: run { negativeX12Null = NullValue.NULL_VALUE }
        cim.negativeX21?.also { negativeX21Set = it } ?: run { negativeX21Null = NullValue.NULL_VALUE }
        cim.positiveR12?.also { positiveR12Set = it } ?: run { positiveR12Null = NullValue.NULL_VALUE }
        cim.positiveR21?.also { positiveR21Set = it } ?: run { positiveR21Null = NullValue.NULL_VALUE }
        cim.positiveX12?.also { positiveX12Set = it } ?: run { positiveX12Null = NullValue.NULL_VALUE }
        cim.positiveX21?.also { positiveX21Set = it } ?: run { positiveX21Null = NullValue.NULL_VALUE }
        cim.r?.also { rSet = it } ?: run { rNull = NullValue.NULL_VALUE }
        cim.r21?.also { r21Set = it } ?: run { r21Null = NullValue.NULL_VALUE }
        cim.x?.also { xSet = it } ?: run { xNull = NullValue.NULL_VALUE }
        cim.x21?.also { x21Set = it } ?: run { x21Null = NullValue.NULL_VALUE }
        cim.zeroR12?.also { zeroR12Set = it } ?: run { zeroR12Null = NullValue.NULL_VALUE }
        cim.zeroR21?.also { zeroR21Set = it } ?: run { zeroR21Null = NullValue.NULL_VALUE }
        cim.zeroX12?.also { zeroX12Set = it } ?: run { zeroX12Null = NullValue.NULL_VALUE }
        cim.zeroX21?.also { zeroX21Set = it } ?: run { zeroX21Null = NullValue.NULL_VALUE }
        toPb(cim, eeBuilder)
    }

/**
 * Convert the [EquivalentEquipment] into its protobuf counterpart.
 *
 * @param cim The [EquivalentEquipment] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: EquivalentEquipment, pb: PBEquivalentEquipment.Builder): PBEquivalentEquipment.Builder =
    pb.apply { toPb(cim, ceBuilder) }

/**
 * An extension for converting any [EquivalentBranch] into its protobuf counterpart.
 */
fun EquivalentBranch.toPb(): PBEquivalentBranch = toPb(this, PBEquivalentBranch.newBuilder()).build()

// #######################################
// # IEC61970 Base Generation Production #
// #######################################

/**
 * Convert the [BatteryUnit] into its protobuf counterpart.
 *
 * @param cim The [BatteryUnit] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: BatteryUnit, pb: PBBatteryUnit.Builder): PBBatteryUnit.Builder =
    pb.apply {
        batteryState = mapBatteryStateKind.toPb(cim.batteryState)
        cim.ratedE?.also { ratedESet = it } ?: run { ratedENull = NullValue.NULL_VALUE }
        cim.storedE?.also { storedESet = it } ?: run { storedENull = NullValue.NULL_VALUE }
        cim.controls.forEach { addBatteryControlMRIDs(it.mRID) }
        toPb(cim, peuBuilder)
    }

/**
 * Convert the [PhotoVoltaicUnit] into its protobuf counterpart.
 *
 * @param cim The [PhotoVoltaicUnit] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: PhotoVoltaicUnit, pb: PBPhotoVoltaicUnit.Builder): PBPhotoVoltaicUnit.Builder =
    pb.apply {
        toPb(cim, peuBuilder)
    }

/**
 * Convert the [PowerElectronicsUnit] into its protobuf counterpart.
 *
 * @param cim The [PowerElectronicsUnit] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: PowerElectronicsUnit, pb: PBPowerElectronicsUnit.Builder): PBPowerElectronicsUnit.Builder =
    pb.apply {
        cim.powerElectronicsConnection?.also { powerElectronicsConnectionMRID = it.mRID } ?: clearPowerElectronicsConnectionMRID()
        cim.maxP?.also { maxPSet = it } ?: run { maxPNull = NullValue.NULL_VALUE }
        cim.minP?.also { minPSet = it } ?: run { minPNull = NullValue.NULL_VALUE }
        toPb(cim, eqBuilder)
    }

/**
 * Convert the [PowerElectronicsWindUnit] into its protobuf counterpart.
 *
 * @param cim The [PowerElectronicsWindUnit] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: PowerElectronicsWindUnit, pb: PBPowerElectronicsWindUnit.Builder): PBPowerElectronicsWindUnit.Builder =
    pb.apply {
        toPb(cim, peuBuilder)
    }

/**
 * An extension for converting any [BatteryUnit] into its protobuf counterpart.
 */
fun BatteryUnit.toPb(): PBBatteryUnit = toPb(this, PBBatteryUnit.newBuilder()).build()

/**
 * An extension for converting any [PhotoVoltaicUnit] into its protobuf counterpart.
 */
fun PhotoVoltaicUnit.toPb(): PBPhotoVoltaicUnit = toPb(this, PBPhotoVoltaicUnit.newBuilder()).build()

/**
 * An extension for converting any [PowerElectronicsWindUnit] into its protobuf counterpart.
 */
fun PowerElectronicsWindUnit.toPb(): PBPowerElectronicsWindUnit = toPb(this, PBPowerElectronicsWindUnit.newBuilder()).build()

// ######################
// # IEC61970 Base Meas #
// ######################

/**
 * Convert the [Accumulator] into its protobuf counterpart.
 *
 * @param cim The [Accumulator] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: Accumulator, pb: PBAccumulator.Builder): PBAccumulator.Builder = pb.apply { toPb(cim, measurementBuilder) }

/**
 * Convert the [Analog] into its protobuf counterpart.
 *
 * @param cim The [Analog] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: Analog, pb: PBAnalog.Builder): PBAnalog.Builder =
    pb.apply {
        cim.positiveFlowIn?.also { positiveFlowInSet = it } ?: run { positiveFlowInNull = NullValue.NULL_VALUE }
        toPb(cim, measurementBuilder)
    }

/**
 * Convert the [Control] into its protobuf counterpart.
 *
 * @param cim The [Control] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: Control, pb: PBControl.Builder): PBControl.Builder =
    pb.apply {
        cim.remoteControl?.also { remoteControlMRID = it.mRID } ?: clearRemoteControlMRID()
        cim.powerSystemResourceMRID?.also { powerSystemResourceMRID = it } ?: clearPowerSystemResourceMRID()
        toPb(cim, ipBuilder)
    }

/**
 * Convert the [Discrete] into its protobuf counterpart.
 *
 * @param cim The [Discrete] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: Discrete, pb: PBDiscrete.Builder): PBDiscrete.Builder = pb.apply { toPb(cim, measurementBuilder) }

/**
 * Convert the [IoPoint] into its protobuf counterpart.
 *
 * @param cim The [IoPoint] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: IoPoint, pb: PBIoPoint.Builder): PBIoPoint.Builder = pb.apply { toPb(cim, ioBuilder) }

/**
 * Convert the [Measurement] into its protobuf counterpart.
 *
 * @param cim The [Measurement] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: Measurement, pb: PBMeasurement.Builder): PBMeasurement.Builder =
    pb.apply {
        cim.remoteSource?.also { remoteSourceMRID = it.mRID } ?: clearRemoteSourceMRID()
        cim.powerSystemResourceMRID?.also { powerSystemResourceMRID = it } ?: clearPowerSystemResourceMRID()
        toPb(cim, ioBuilder)
        cim.terminalMRID?.also { terminalMRID = it } ?: clearTerminalMRID()
        phases = mapPhaseCode.toPb(cim.phases)
        unitSymbol = mapUnitSymbol.toPb(cim.unitSymbol)
    }

/**
 * An extension for converting any [Accumulator] into its protobuf counterpart.
 */
fun Accumulator.toPb(): PBAccumulator = toPb(this, PBAccumulator.newBuilder()).build()

/**
 * An extension for converting any [Analog] into its protobuf counterpart.
 */
fun Analog.toPb(): PBAnalog = toPb(this, PBAnalog.newBuilder()).build()

/**
 * An extension for converting any [Control] into its protobuf counterpart.
 */
fun Control.toPb(): PBControl = toPb(this, PBControl.newBuilder()).build()

/**
 * An extension for converting any [Discrete] into its protobuf counterpart.
 */
fun Discrete.toPb(): PBDiscrete = toPb(this, PBDiscrete.newBuilder()).build()

// ############################
// # IEC61970 Base Protection #
// ############################

/**
 * Convert the [CurrentRelay] into its protobuf counterpart.
 *
 * @param cim The [CurrentRelay] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: CurrentRelay, pb: PBCurrentRelay.Builder): PBCurrentRelay.Builder =
    pb.apply {
        cim.currentLimit1?.also { currentLimit1Set = it } ?: run { currentLimit1Null = NullValue.NULL_VALUE }
        cim.inverseTimeFlag?.also { inverseTimeFlagSet = it } ?: run { inverseTimeFlagNull = NullValue.NULL_VALUE }
        cim.timeDelay1?.also { timeDelay1Set = it } ?: run { timeDelay1Null = NullValue.NULL_VALUE }
        toPb(cim, prfBuilder)
    }

/**
 * An extension for converting any [CurrentRelay] into its protobuf counterpart.
 */
fun CurrentRelay.toPb(): PBCurrentRelay = toPb(this, PBCurrentRelay.newBuilder()).build()

// #######################
// # IEC61970 Base Scada #
// #######################

/**
 * Convert the [RemoteControl] into its protobuf counterpart.
 *
 * @param cim The [RemoteControl] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: RemoteControl, pb: PBRemoteControl.Builder): PBRemoteControl.Builder =
    pb.apply {
        cim.control?.also { controlMRID = it.mRID } ?: clearControlMRID()
        toPb(cim, rpBuilder)
    }

/**
 * Convert the [RemotePoint] into its protobuf counterpart.
 *
 * @param cim The [RemotePoint] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: RemotePoint, pb: PBRemotePoint.Builder): PBRemotePoint.Builder =
    pb.apply { toPb(cim, ioBuilder) }

/**
 * Convert the [RemoteSource] into its protobuf counterpart.
 *
 * @param cim The [RemoteSource] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: RemoteSource, pb: PBRemoteSource.Builder): PBRemoteSource.Builder =
    pb.apply {
        cim.measurement?.also { measurementMRID = it.mRID } ?: clearMeasurementMRID()
        toPb(cim, rpBuilder)
    }

/**
 * An extension for converting any [RemoteControl] into its protobuf counterpart.
 */
fun RemoteControl.toPb(): PBRemoteControl = toPb(this, PBRemoteControl.newBuilder()).build()

/**
 * An extension for converting any [RemoteSource] into its protobuf counterpart.
 */
fun RemoteSource.toPb(): PBRemoteSource = toPb(this, PBRemoteSource.newBuilder()).build()

// #######################
// # IEC61970 Base Wires #
// #######################

/**
 * Convert the [AcLineSegment] into its protobuf counterpart.
 *
 * @param cim The [AcLineSegment] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: AcLineSegment, pb: PBAcLineSegment.Builder): PBAcLineSegment.Builder =
    pb.apply {
        cim.perLengthImpedance?.also { perLengthImpedanceMRID = it.mRID } ?: clearPerLengthImpedanceMRID()
        clearCutMRIDs()
        cim.cuts.forEach { addCutMRIDs(it.mRID) }
        clearClampMRIDs()
        cim.clamps.forEach { addClampMRIDs(it.mRID) }
        clearPhaseMRIDs()
        cim.phases.forEach { addPhaseMRIDs(it.mRID) }
        toPb(cim, cdBuilder)
    }

/**
 * Convert the [AcLineSegmentPhase] into its protobuf counterpart.
 *
 * @param cim The [AcLineSegmentPhase] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: AcLineSegmentPhase, pb: PBAcLineSegmentPhase.Builder): PBAcLineSegmentPhase.Builder =
    pb.apply {
        cim.acLineSegment?.also { acLineSegmentMRID = it.mRID } ?: clearAcLineSegmentMRID()
        phase = mapSinglePhaseKind.toPb(cim.phase)
        cim.sequenceNumber?.also { sequenceNumberSet = it } ?: run { sequenceNumberNull = NullValue.NULL_VALUE }

        toPb(cim, psrBuilder)
    }

/**
 * Convert the [Breaker] into its protobuf counterpart.
 *
 * @param cim The [Breaker] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: Breaker, pb: PBBreaker.Builder): PBBreaker.Builder =
    pb.apply {
        cim.inTransitTime?.also { inTransitTimeSet = it } ?: run { inTransitTimeNull = NullValue.NULL_VALUE }
        toPb(cim, swBuilder)
    }

/**
 * Convert the [BusbarSection] into its protobuf counterpart.
 *
 * @param cim The [BusbarSection] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: BusbarSection, pb: PBBusbarSection.Builder): PBBusbarSection.Builder =
    pb.apply { toPb(cim, cnBuilder) }

/**
 * Convert the [Clamp] into its protobuf counterpart.
 *
 * @param cim The [Clamp] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: Clamp, pb: PBClamp.Builder): PBClamp.Builder =
    pb.apply {
        cim.lengthFromTerminal1?.also { lengthFromTerminal1Set = it } ?: run { lengthFromTerminal1Null = NullValue.NULL_VALUE }
        cim.acLineSegment?.also { acLineSegmentMRID = it.mRID } ?: clearAcLineSegmentMRID()
        toPb(cim, ceBuilder)
    }

/**
 * Convert the [Conductor] into its protobuf counterpart.
 *
 * @param cim The [Conductor] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: Conductor, pb: PBConductor.Builder): PBConductor.Builder =
    pb.apply {
        cim.length?.also { lengthSet = it } ?: run { lengthNull = NullValue.NULL_VALUE }
        cim.designTemperature?.also { designTemperatureSet = it } ?: run { designTemperatureNull = NullValue.NULL_VALUE }
        cim.designRating?.also { designRatingSet = it } ?: run { designRatingNull = NullValue.NULL_VALUE }
        toPb(cim, ceBuilder)
    }

/**
 * Convert the [Connector] into its protobuf counterpart.
 *
 * @param cim The [Connector] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: Connector, pb: PBConnector.Builder): PBConnector.Builder =
    pb.apply { toPb(cim, ceBuilder) }

/**
 * Convert the [Cut] into its protobuf counterpart.
 *
 * @param cim The [Cut] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: Cut, pb: PBCut.Builder): PBCut.Builder =
    pb.apply {
        cim.lengthFromTerminal1?.also { lengthFromTerminal1Set = it } ?: run { lengthFromTerminal1Null = NullValue.NULL_VALUE }
        cim.acLineSegment?.also { acLineSegmentMRID = it.mRID } ?: clearAcLineSegmentMRID()
        toPb(cim, swBuilder)
    }

/**
 * Convert the [Disconnector] into its protobuf counterpart.
 *
 * @param cim The [Disconnector] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: Disconnector, pb: PBDisconnector.Builder): PBDisconnector.Builder =
    pb.apply { toPb(cim, swBuilder) }

/**
 * Convert the [EarthFaultCompensator] into its protobuf counterpart.
 *
 * @param cim The [EarthFaultCompensator] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: EarthFaultCompensator, pb: PBEarthFaultCompensator.Builder): PBEarthFaultCompensator.Builder =
    pb.apply {
        cim.r?.also { rSet = it } ?: run { rNull = NullValue.NULL_VALUE }
        toPb(cim, ceBuilder)
    }

/**
 * Convert the [EnergyConnection] into its protobuf counterpart.
 *
 * @param cim The [EnergyConnection] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: EnergyConnection, pb: PBEnergyConnection.Builder): PBEnergyConnection.Builder =
    pb.apply { toPb(cim, ceBuilder) }

/**
 * Convert the [EnergyConsumer] into its protobuf counterpart.
 *
 * @param cim The [EnergyConsumer] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: EnergyConsumer, pb: PBEnergyConsumer.Builder): PBEnergyConsumer.Builder =
    pb.apply {
        clearEnergyConsumerPhasesMRIDs()
        cim.phases.forEach { addEnergyConsumerPhasesMRIDs(it.mRID) }
        cim.customerCount?.also { customerCountSet = it } ?: run { customerCountNull = NullValue.NULL_VALUE }
        cim.grounded?.also { groundedSet = it } ?: run { groundedNull = NullValue.NULL_VALUE }
        cim.p?.also { pSet = it } ?: run { pNull = NullValue.NULL_VALUE }
        cim.pFixed?.also { pFixedSet = it } ?: run { pFixedNull = NullValue.NULL_VALUE }
        phaseConnection = mapPhaseShuntConnectionKind.toPb(cim.phaseConnection)
        cim.q?.also { qSet = it } ?: run { qNull = NullValue.NULL_VALUE }
        cim.qFixed?.also { qFixedSet = it } ?: run { qFixedNull = NullValue.NULL_VALUE }
        toPb(cim, ecBuilder)
    }

/**
 * Convert the [EnergyConsumerPhase] into its protobuf counterpart.
 *
 * @param cim The [EnergyConsumerPhase] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: EnergyConsumerPhase, pb: PBEnergyConsumerPhase.Builder): PBEnergyConsumerPhase.Builder =
    pb.apply {
        cim.energyConsumer?.also { energyConsumerMRID = it.mRID } ?: clearEnergyConsumerMRID()
        phase = mapSinglePhaseKind.toPb(cim.phase)
        cim.p?.also { pSet = it } ?: run { pNull = NullValue.NULL_VALUE }
        cim.pFixed?.also { pFixedSet = it } ?: run { pFixedNull = NullValue.NULL_VALUE }
        cim.q?.also { qSet = it } ?: run { qNull = NullValue.NULL_VALUE }
        cim.qFixed?.also { qFixedSet = it } ?: run { qFixedNull = NullValue.NULL_VALUE }
        toPb(cim, psrBuilder)
    }

/**
 * Convert the [EnergySource] into its protobuf counterpart.
 *
 * @param cim The [EnergySource] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: EnergySource, pb: PBEnergySource.Builder): PBEnergySource.Builder =
    pb.apply {
        clearEnergySourcePhasesMRIDs()
        cim.phases.forEach { addEnergySourcePhasesMRIDs(it.mRID) }

        cim.activePower?.also { activePowerSet = it } ?: run { activePowerNull = NullValue.NULL_VALUE }
        cim.reactivePower?.also { reactivePowerSet = it } ?: run { reactivePowerNull = NullValue.NULL_VALUE }
        cim.voltageAngle?.also { voltageAngleSet = it } ?: run { voltageAngleNull = NullValue.NULL_VALUE }
        cim.voltageMagnitude?.also { voltageMagnitudeSet = it } ?: run { voltageMagnitudeNull = NullValue.NULL_VALUE }
        cim.r?.also { rSet = it } ?: run { rNull = NullValue.NULL_VALUE }
        cim.x?.also { xSet = it } ?: run { xNull = NullValue.NULL_VALUE }
        cim.pMax?.also { pMaxSet = it } ?: run { pMaxNull = NullValue.NULL_VALUE }
        cim.pMin?.also { pMinSet = it } ?: run { pMinNull = NullValue.NULL_VALUE }
        cim.r0?.also { r0Set = it } ?: run { r0Null = NullValue.NULL_VALUE }
        cim.rn?.also { rnSet = it } ?: run { rnNull = NullValue.NULL_VALUE }
        cim.x0?.also { x0Set = it } ?: run { x0Null = NullValue.NULL_VALUE }
        cim.xn?.also { xnSet = it } ?: run { xnNull = NullValue.NULL_VALUE }
        cim.isExternalGrid?.also { isExternalGridSet = it } ?: run { isExternalGridNull = NullValue.NULL_VALUE }
        cim.rMin?.also { rMinSet = it } ?: run { rMinNull = NullValue.NULL_VALUE }
        cim.rnMin?.also { rnMinSet = it } ?: run { rnMinNull = NullValue.NULL_VALUE }
        cim.r0Min?.also { r0MinSet = it } ?: run { r0MinNull = NullValue.NULL_VALUE }
        cim.xMin?.also { xMinSet = it } ?: run { xMinNull = NullValue.NULL_VALUE }
        cim.xnMin?.also { xnMinSet = it } ?: run { xnMinNull = NullValue.NULL_VALUE }
        cim.x0Min?.also { x0MinSet = it } ?: run { x0MinNull = NullValue.NULL_VALUE }
        cim.rMax?.also { rMaxSet = it } ?: run { rMaxNull = NullValue.NULL_VALUE }
        cim.rnMax?.also { rnMaxSet = it } ?: run { rnMaxNull = NullValue.NULL_VALUE }
        cim.r0Max?.also { r0MaxSet = it } ?: run { r0MaxNull = NullValue.NULL_VALUE }
        cim.xMax?.also { xMaxSet = it } ?: run { xMaxNull = NullValue.NULL_VALUE }
        cim.xnMax?.also { xnMaxSet = it } ?: run { xnMaxNull = NullValue.NULL_VALUE }
        cim.x0Max?.also { x0MaxSet = it } ?: run { x0MaxNull = NullValue.NULL_VALUE }

        toPb(cim, ecBuilder)
    }

/**
 * Convert the [EnergySourcePhase] into its protobuf counterpart.
 *
 * @param cim The [EnergySourcePhase] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: EnergySourcePhase, pb: PBEnergySourcePhase.Builder): PBEnergySourcePhase.Builder =
    pb.apply {
        cim.energySource?.also { energySourceMRID = it.mRID } ?: clearEnergySourceMRID()
        phase = mapSinglePhaseKind.toPb(cim.phase)
        toPb(cim, psrBuilder)
    }

/**
 * Convert the [Fuse] into its protobuf counterpart.
 *
 * @param cim The [Fuse] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: Fuse, pb: PBFuse.Builder): PBFuse.Builder =
    pb.apply {
        cim.function?.also { functionMRID = it.mRID } ?: clearFunctionMRID()
        toPb(cim, swBuilder)
    }

/**
 * Convert the [Ground] into its protobuf counterpart.
 *
 * @param cim The [Ground] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: Ground, pb: PBGround.Builder): PBGround.Builder =
    pb.apply { toPb(cim, ceBuilder) }

/**
 * Convert the [GroundDisconnector] into its protobuf counterpart.
 *
 * @param cim The [GroundDisconnector] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: GroundDisconnector, pb: PBGroundDisconnector.Builder): PBGroundDisconnector.Builder =
    pb.apply { toPb(cim, swBuilder) }

/**
 * Convert the [GroundingImpedance] into its protobuf counterpart.
 *
 * @param cim The [GroundingImpedance] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: GroundingImpedance, pb: PBGroundingImpedance.Builder): PBGroundingImpedance.Builder =
    pb.apply {
        cim.x?.also { xSet = it } ?: run { xNull = NullValue.NULL_VALUE }
        toPb(cim, efcBuilder)
    }

/**
 * Convert the [Jumper] into its protobuf counterpart.
 *
 * @param cim The [Jumper] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: Jumper, pb: PBJumper.Builder): PBJumper.Builder =
    pb.apply { toPb(cim, swBuilder) }

/**
 * Convert the [Junction] into its protobuf counterpart.
 *
 * @param cim The [Junction] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: Junction, pb: PBJunction.Builder): PBJunction.Builder =
    pb.apply { toPb(cim, cnBuilder) }

/**
 * Convert the [Line] into its protobuf counterpart.
 *
 * @param cim The [Line] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: Line, pb: PBLine.Builder): PBLine.Builder =
    pb.apply { toPb(cim, ecBuilder) }

/**
 * Convert the [LinearShuntCompensator] into its protobuf counterpart.
 *
 * @param cim The [LinearShuntCompensator] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: LinearShuntCompensator, pb: PBLinearShuntCompensator.Builder): PBLinearShuntCompensator.Builder =
    pb.apply {
        cim.b0PerSection?.also { b0PerSectionSet = it } ?: run { b0PerSectionNull = NullValue.NULL_VALUE }
        cim.bPerSection?.also { bPerSectionSet = it } ?: run { bPerSectionNull = NullValue.NULL_VALUE }
        cim.g0PerSection?.also { g0PerSectionSet = it } ?: run { g0PerSectionNull = NullValue.NULL_VALUE }
        cim.gPerSection?.also { gPerSectionSet = it } ?: run { gPerSectionNull = NullValue.NULL_VALUE }
        toPb(cim, scBuilder)
    }

/**
 * Convert the [LoadBreakSwitch] into its protobuf counterpart.
 *
 * @param cim The [LoadBreakSwitch] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: LoadBreakSwitch, pb: PBLoadBreakSwitch.Builder): PBLoadBreakSwitch.Builder =
    pb.apply { toPb(cim, psBuilder) }

/**
 * Convert the [PerLengthImpedance] into its protobuf counterpart.
 *
 * @param cim The [PerLengthImpedance] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: PerLengthImpedance, pb: PBPerLengthImpedance.Builder): PBPerLengthImpedance.Builder =
    pb.apply { toPb(cim, lpBuilder) }

/**
 * Convert the [PerLengthLineParameter] into its protobuf counterpart.
 *
 * @param cim The [PerLengthLineParameter] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: PerLengthLineParameter, pb: PBPerLengthLineParameter.Builder): PBPerLengthLineParameter.Builder =
    pb.apply { toPb(cim, ioBuilder) }

/**
 * Convert the [PerLengthPhaseImpedance] into its protobuf counterpart.
 *
 * @param cim The [PerLengthPhaseImpedance] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: PerLengthPhaseImpedance, pb: PBPerLengthPhaseImpedance.Builder): PBPerLengthPhaseImpedance.Builder =
    pb.apply {
        cim.data.forEachIndexed { i, data -> addPhaseImpedanceDataBuilder(i).apply { toPb(data, this) } }
        toPb(cim, pliBuilder)
    }

/**
 * Convert the [PerLengthSequenceImpedance] into its protobuf counterpart.
 *
 * @param cim The [PerLengthSequenceImpedance] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: PerLengthSequenceImpedance, pb: PBPerLengthSequenceImpedance.Builder): PBPerLengthSequenceImpedance.Builder =
    pb.apply {
        cim.r?.also { rSet = it } ?: run { rNull = NullValue.NULL_VALUE }
        cim.x?.also { xSet = it } ?: run { xNull = NullValue.NULL_VALUE }
        cim.r0?.also { r0Set = it } ?: run { r0Null = NullValue.NULL_VALUE }
        cim.x0?.also { x0Set = it } ?: run { x0Null = NullValue.NULL_VALUE }
        cim.bch?.also { bchSet = it } ?: run { bchNull = NullValue.NULL_VALUE }
        cim.gch?.also { gchSet = it } ?: run { gchNull = NullValue.NULL_VALUE }
        cim.b0ch?.also { b0ChSet = it } ?: run { b0ChNull = NullValue.NULL_VALUE }
        cim.g0ch?.also { g0ChSet = it } ?: run { g0ChNull = NullValue.NULL_VALUE }
        toPb(cim, pliBuilder)
    }

/**
 * Convert the [PetersenCoil] into its protobuf counterpart.
 *
 * @param cim The [PetersenCoil] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: PetersenCoil, pb: PBPetersenCoil.Builder): PBPetersenCoil.Builder =
    pb.apply {
        cim.xGroundNominal?.also { xGroundNominalSet = it } ?: run { xGroundNominalNull = NullValue.NULL_VALUE }
        toPb(cim, efcBuilder)
    }

/**
 * Convert the [PhaseImpedanceData] into its protobuf counterpart.
 *
 * @param cim The [PhaseImpedanceData] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: PhaseImpedanceData, pb: PBPhaseImpedanceData.Builder): PBPhaseImpedanceData.Builder =
    pb.apply {
        fromPhase = mapSinglePhaseKind.toPb(cim.fromPhase)
        toPhase = mapSinglePhaseKind.toPb(cim.toPhase)
        cim.b?.also { bSet = it } ?: run { bNull = NullValue.NULL_VALUE }
        cim.g?.also { gSet = it } ?: run { gNull = NullValue.NULL_VALUE }
        cim.r?.also { rSet = it } ?: run { rNull = NullValue.NULL_VALUE }
        cim.x?.also { xSet = it } ?: run { xNull = NullValue.NULL_VALUE }
    }

/**
 * Convert the [PowerElectronicsConnection] into its protobuf counterpart.
 *
 * @param cim The [PowerElectronicsConnection] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: PowerElectronicsConnection, pb: PBPowerElectronicsConnection.Builder): PBPowerElectronicsConnection.Builder =
    pb.apply {
        clearPowerElectronicsUnitMRIDs()
        cim.units.forEach { addPowerElectronicsUnitMRIDs(it.mRID) }

        clearPowerElectronicsConnectionPhaseMRIDs()
        cim.phases.forEach { addPowerElectronicsConnectionPhaseMRIDs(it.mRID) }

        cim.maxIFault?.also { maxIFaultSet = it } ?: run { maxIFaultNull = NullValue.NULL_VALUE }
        cim.maxQ?.also { maxQSet = it } ?: run { maxQNull = NullValue.NULL_VALUE }
        cim.minQ?.also { minQSet = it } ?: run { minQNull = NullValue.NULL_VALUE }
        cim.p?.also { pSet = it } ?: run { pNull = NullValue.NULL_VALUE }
        cim.q?.also { qSet = it } ?: run { qNull = NullValue.NULL_VALUE }
        cim.ratedS?.also { ratedSSet = it } ?: run { ratedSNull = NullValue.NULL_VALUE }
        cim.ratedU?.also { ratedUSet = it } ?: run { ratedUNull = NullValue.NULL_VALUE }
        cim.inverterStandard?.also { inverterStandardSet = it } ?: run { inverterStandardNull = NullValue.NULL_VALUE }
        cim.sustainOpOvervoltLimit?.also { sustainOpOvervoltLimitSet = it } ?: run { sustainOpOvervoltLimitNull = NullValue.NULL_VALUE }
        cim.stopAtOverFreq?.also { stopAtOverFreqSet = it } ?: run { stopAtOverFreqNull = NullValue.NULL_VALUE }
        cim.stopAtUnderFreq?.also { stopAtUnderFreqSet = it } ?: run { stopAtUnderFreqNull = NullValue.NULL_VALUE }
        cim.invVoltWattRespMode?.also { invVoltWattRespModeSet = it } ?: run { invVoltWattRespModeNull = NullValue.NULL_VALUE }
        cim.invWattRespV1?.also { invWattRespV1Set = it } ?: run { invWattRespV1Null = NullValue.NULL_VALUE }
        cim.invWattRespV2?.also { invWattRespV2Set = it } ?: run { invWattRespV2Null = NullValue.NULL_VALUE }
        cim.invWattRespV3?.also { invWattRespV3Set = it } ?: run { invWattRespV3Null = NullValue.NULL_VALUE }
        cim.invWattRespV4?.also { invWattRespV4Set = it } ?: run { invWattRespV4Null = NullValue.NULL_VALUE }
        cim.invWattRespPAtV1?.also { invWattRespPAtV1Set = it } ?: run { invWattRespPAtV1Null = NullValue.NULL_VALUE }
        cim.invWattRespPAtV2?.also { invWattRespPAtV2Set = it } ?: run { invWattRespPAtV2Null = NullValue.NULL_VALUE }
        cim.invWattRespPAtV3?.also { invWattRespPAtV3Set = it } ?: run { invWattRespPAtV3Null = NullValue.NULL_VALUE }
        cim.invWattRespPAtV4?.also { invWattRespPAtV4Set = it } ?: run { invWattRespPAtV4Null = NullValue.NULL_VALUE }
        cim.invVoltVarRespMode?.also { invVoltVarRespModeSet = it } ?: run { invVoltVarRespModeNull = NullValue.NULL_VALUE }
        cim.invVarRespV1?.also { invVarRespV1Set = it } ?: run { invVarRespV1Null = NullValue.NULL_VALUE }
        cim.invVarRespV2?.also { invVarRespV2Set = it } ?: run { invVarRespV2Null = NullValue.NULL_VALUE }
        cim.invVarRespV3?.also { invVarRespV3Set = it } ?: run { invVarRespV3Null = NullValue.NULL_VALUE }
        cim.invVarRespV4?.also { invVarRespV4Set = it } ?: run { invVarRespV4Null = NullValue.NULL_VALUE }
        cim.invVarRespQAtV1?.also { invVarRespQAtV1Set = it } ?: run { invVarRespQAtV1Null = NullValue.NULL_VALUE }
        cim.invVarRespQAtV2?.also { invVarRespQAtV2Set = it } ?: run { invVarRespQAtV2Null = NullValue.NULL_VALUE }
        cim.invVarRespQAtV3?.also { invVarRespQAtV3Set = it } ?: run { invVarRespQAtV3Null = NullValue.NULL_VALUE }
        cim.invVarRespQAtV4?.also { invVarRespQAtV4Set = it } ?: run { invVarRespQAtV4Null = NullValue.NULL_VALUE }
        cim.invReactivePowerMode?.also { invReactivePowerModeSet = it } ?: run { invReactivePowerModeNull = NullValue.NULL_VALUE }
        cim.invFixReactivePower?.also { invFixReactivePowerSet = it } ?: run { invFixReactivePowerNull = NullValue.NULL_VALUE }
        toPb(cim, rceBuilder)
    }

/**
 * Convert the [PowerElectronicsConnectionPhase] into its protobuf counterpart.
 *
 * @param cim The [PowerElectronicsConnectionPhase] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: PowerElectronicsConnectionPhase, pb: PBPowerElectronicsConnectionPhase.Builder): PBPowerElectronicsConnectionPhase.Builder =
    pb.apply {
        cim.powerElectronicsConnection?.also { powerElectronicsConnectionMRID = it.mRID } ?: clearPowerElectronicsConnectionMRID()
        cim.p?.also { pSet = it } ?: run { pNull = NullValue.NULL_VALUE }
        phase = mapSinglePhaseKind.toPb(cim.phase)
        cim.q?.also { qSet = it } ?: run { qNull = NullValue.NULL_VALUE }
        toPb(cim, psrBuilder)
    }

/**
 * Convert the [PowerTransformer] into its protobuf counterpart.
 *
 * @param cim The [PowerTransformer] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: PowerTransformer, pb: PBPowerTransformer.Builder): PBPowerTransformer.Builder =
    pb.apply {
        clearPowerTransformerEndMRIDs()
        cim.ends.forEach { addPowerTransformerEndMRIDs(it.mRID) }
        vectorGroup = mapVectorGroup.toPb(cim.vectorGroup)
        cim.transformerUtilisation?.also { transformerUtilisationSet = it } ?: run { transformerUtilisationNull = NullValue.NULL_VALUE }
        constructionKind = mapTransformerConstructionKind.toPb(cim.constructionKind)
        function = mapTransformerFunctionKind.toPb(cim.function)
        toPb(cim, ceBuilder)
    }

/**
 * Convert the [PowerTransformerEnd] into its protobuf counterpart.
 *
 * @param cim The [PowerTransformerEnd] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: PowerTransformerEnd, pb: PBPowerTransformerEnd.Builder): PBPowerTransformerEnd.Builder =
    pb.apply {
        cim.powerTransformer?.also { powerTransformerMRID = it.mRID } ?: clearPowerTransformerMRID()
        cim.sRatings.forEach { addRatings(toPb(it)) }
        cim.ratedU?.also { ratedUSet = it } ?: run { ratedUNull = NullValue.NULL_VALUE }
        cim.r?.also { rSet = it } ?: run { rNull = NullValue.NULL_VALUE }
        cim.r0?.also { r0Set = it } ?: run { r0Null = NullValue.NULL_VALUE }
        cim.x?.also { xSet = it } ?: run { xNull = NullValue.NULL_VALUE }
        cim.x0?.also { x0Set = it } ?: run { x0Null = NullValue.NULL_VALUE }
        connectionKind = mapWindingConnection.toPb(cim.connectionKind)
        cim.b?.also { bSet = it } ?: run { bNull = NullValue.NULL_VALUE }
        cim.b0?.also { b0Set = it } ?: run { b0Null = NullValue.NULL_VALUE }
        cim.g?.also { gSet = it } ?: run { gNull = NullValue.NULL_VALUE }
        cim.g0?.also { g0Set = it } ?: run { g0Null = NullValue.NULL_VALUE }
        cim.phaseAngleClock?.also { phaseAngleClockSet = it } ?: run { phaseAngleClockNull = NullValue.NULL_VALUE }
        toPb(cim, teBuilder)
    }

/**
 * Convert the [ProtectedSwitch] into its protobuf counterpart.
 *
 * @param cim The [ProtectedSwitch] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: ProtectedSwitch, pb: PBProtectedSwitch.Builder): PBProtectedSwitch.Builder =
    pb.apply {
        cim.relayFunctions.forEach { addRelayFunctionMRIDs(it.mRID) }
        cim.breakingCapacity?.also { breakingCapacitySet = it } ?: run { breakingCapacityNull = NullValue.NULL_VALUE }
        toPb(cim, swBuilder)
    }

/**
 * Convert the [RatioTapChanger] into its protobuf counterpart.
 *
 * @param cim The [RatioTapChanger] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: RatioTapChanger, pb: PBRatioTapChanger.Builder): PBRatioTapChanger.Builder =
    pb.apply {
        cim.transformerEnd?.also { transformerEndMRID = it.mRID } ?: clearTransformerEndMRID()
        cim.stepVoltageIncrement?.also { stepVoltageIncrementSet = it } ?: run { stepVoltageIncrementNull = NullValue.NULL_VALUE }
        toPb(cim, tcBuilder)
    }

/**
 * Convert the [ReactiveCapabilityCurve] into its protobuf counterpart.
 *
 * @param cim The [ReactiveCapabilityCurve] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: ReactiveCapabilityCurve, pb: PBReactiveCapabilityCurve.Builder): PBReactiveCapabilityCurve.Builder =
    pb.apply {
        toPb(cim, cBuilder)
    }

/**
 * Convert the [Recloser] into its protobuf counterpart.
 *
 * @param cim The [Recloser] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: Recloser, pb: PBRecloser.Builder): PBRecloser.Builder =
    pb.apply { toPb(cim, swBuilder) }

/**
 * Convert the [RegulatingCondEq] into its protobuf counterpart.
 *
 * @param cim The [RegulatingCondEq] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: RegulatingCondEq, pb: PBRegulatingCondEq.Builder): PBRegulatingCondEq.Builder =
    pb.apply {
        cim.controlEnabled?.also { controlEnabledSet = it } ?: run { controlEnabledNull = NullValue.NULL_VALUE }
        cim.regulatingControl?.also { regulatingControlMRID = it.mRID } ?: clearRegulatingControlMRID()
        toPb(cim, ecBuilder)
    }

/**
 * Convert the [RegulatingControl] into its protobuf counterpart.
 *
 * @param cim The [RegulatingControl] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: RegulatingControl, pb: PBRegulatingControl.Builder): PBRegulatingControl.Builder =
    pb.apply {
        cim.discrete?.also { discreteSet = it } ?: run { discreteNull = NullValue.NULL_VALUE }
        mode = mapRegulatingControlModeKind.toPb(cim.mode)
        monitoredPhase = mapPhaseCode.toPb(cim.monitoredPhase)
        cim.targetDeadband?.also { targetDeadbandSet = it } ?: run { targetDeadbandNull = NullValue.NULL_VALUE }
        cim.targetValue?.also { targetValueSet = it } ?: run { targetValueNull = NullValue.NULL_VALUE }
        cim.enabled?.also { enabledSet = it } ?: run { enabledNull = NullValue.NULL_VALUE }
        cim.maxAllowedTargetValue?.also { maxAllowedTargetValueSet = it } ?: run { maxAllowedTargetValueNull = NullValue.NULL_VALUE }
        cim.minAllowedTargetValue?.also { minAllowedTargetValueSet = it } ?: run { minAllowedTargetValueNull = NullValue.NULL_VALUE }
        cim.ratedCurrent?.also { ratedCurrentSet = it } ?: run { ratedCurrentNull = NullValue.NULL_VALUE }
        cim.terminal?.also { terminalMRID = it.mRID } ?: clearTerminalMRID()
        clearRegulatingCondEqMRIDs()
        cim.regulatingCondEqs.forEach { addRegulatingCondEqMRIDs(it.mRID) }
        cim.ctPrimary?.also { ctPrimarySet = it } ?: run { ctPrimaryNull = NullValue.NULL_VALUE }
        cim.minTargetDeadband?.also { minTargetDeadbandSet = it } ?: run { minTargetDeadbandNull = NullValue.NULL_VALUE }

        toPb(cim, psrBuilder)
    }

/**
 * Convert the [RotatingMachine] into its protobuf counterpart.
 *
 * @param cim The [RotatingMachine] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: RotatingMachine, pb: PBRotatingMachine.Builder): PBRotatingMachine.Builder =
    pb.apply {
        cim.ratedPowerFactor?.also { ratedPowerFactorSet = it } ?: run { ratedPowerFactorNull = NullValue.NULL_VALUE }
        cim.ratedS?.also { ratedSSet = it } ?: run { ratedSNull = NullValue.NULL_VALUE }
        cim.ratedU?.also { ratedUSet = it } ?: run { ratedUNull = NullValue.NULL_VALUE }
        cim.p?.also { pSet = it } ?: run { pNull = NullValue.NULL_VALUE }
        cim.q?.also { qSet = it } ?: run { qNull = NullValue.NULL_VALUE }
        toPb(cim, rceBuilder)
    }

/**
 * Convert the [SeriesCompensator] into its protobuf counterpart.
 *
 * @param cim The [SeriesCompensator] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: SeriesCompensator, pb: PBSeriesCompensator.Builder): PBSeriesCompensator.Builder =
    pb.apply {
        cim.r?.also { rSet = it } ?: run { rNull = NullValue.NULL_VALUE }
        cim.r0?.also { r0Set = it } ?: run { r0Null = NullValue.NULL_VALUE }
        cim.x?.also { xSet = it } ?: run { xNull = NullValue.NULL_VALUE }
        cim.x0?.also { x0Set = it } ?: run { x0Null = NullValue.NULL_VALUE }
        cim.varistorRatedCurrent?.also { varistorRatedCurrentSet = it } ?: run { varistorRatedCurrentNull = NullValue.NULL_VALUE }
        cim.varistorVoltageThreshold?.also { varistorVoltageThresholdSet = it } ?: run { varistorVoltageThresholdNull = NullValue.NULL_VALUE }
        toPb(cim, ceBuilder)
    }

/**
 * Convert the [ShuntCompensator] into its protobuf counterpart.
 *
 * @param cim The [ShuntCompensator] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: ShuntCompensator, pb: PBShuntCompensator.Builder): PBShuntCompensator.Builder =
    pb.apply {
        cim.sections?.also { sectionsSet = it } ?: run { sectionsNull = NullValue.NULL_VALUE }
        cim.grounded?.also { groundedSet = it } ?: run { groundedNull = NullValue.NULL_VALUE }
        cim.nomU?.also { nomUSet = it } ?: run { nomUNull = NullValue.NULL_VALUE }
        phaseConnection = mapPhaseShuntConnectionKind.toPb(cim.phaseConnection)
        cim.groundingTerminal?.also { groundingTerminalMRID = it.mRID } ?: clearGroundingTerminalMRID()

        toPb(cim, rceBuilder)
    }

/**
 * Convert the [StaticVarCompensator] into its protobuf counterpart.
 *
 * @param cim The [StaticVarCompensator] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: StaticVarCompensator, pb: PBStaticVarCompensator.Builder): PBStaticVarCompensator.Builder =
    pb.apply {
        cim.capacitiveRating?.also { capacitiveRatingSet = it } ?: run { capacitiveRatingNull = NullValue.NULL_VALUE }
        cim.inductiveRating?.also { inductiveRatingSet = it } ?: run { inductiveRatingNull = NullValue.NULL_VALUE }
        cim.q?.also { qSet = it } ?: run { qNull = NullValue.NULL_VALUE }
        svcControlMode = mapSVCControlMode.toPb(cim.svcControlMode)
        cim.voltageSetPoint?.also { voltageSetPointSet = it } ?: run { voltageSetPointNull = NullValue.NULL_VALUE }
        toPb(cim, rceBuilder)
    }

/**
 * Convert the [Switch] into its protobuf counterpart.
 *
 * @param cim The [Switch] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: Switch, pb: PBSwitch.Builder): PBSwitch.Builder =
    pb.apply {
        cim.ratedCurrent?.also { ratedCurrentSet = it } ?: run { ratedCurrentNull = NullValue.NULL_VALUE }
        normalOpen = cim.isNormallyOpen()
        open = cim.isOpen()
        // when unganged support is added to protobuf
        // normalOpen = cim.normalOpen
        // open = cim.open
        toPb(cim, ceBuilder)
    }

/**
 * Convert the [SynchronousMachine] into its protobuf counterpart.
 *
 * @param cim The [SynchronousMachine] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: SynchronousMachine, pb: PBSynchronousMachine.Builder): PBSynchronousMachine.Builder =
    pb.apply {
        clearReactiveCapabilityCurveMRIDs()
        cim.curves.forEach { addReactiveCapabilityCurveMRIDs(it.mRID) }
        cim.baseQ?.also { baseQSet = it } ?: run { baseQNull = NullValue.NULL_VALUE }
        cim.condenserP?.also { condenserPSet = it } ?: run { condenserPNull = NullValue.NULL_VALUE }
        cim.earthing?.also { earthingSet = it } ?: run { earthingNull = NullValue.NULL_VALUE }
        cim.earthingStarPointR?.also { earthingStarPointRSet = it } ?: run { earthingStarPointRNull = NullValue.NULL_VALUE }
        cim.earthingStarPointX?.also { earthingStarPointXSet = it } ?: run { earthingStarPointXNull = NullValue.NULL_VALUE }
        cim.ikk?.also { ikkSet = it } ?: run { ikkNull = NullValue.NULL_VALUE }
        cim.maxQ?.also { maxQSet = it } ?: run { maxQNull = NullValue.NULL_VALUE }
        cim.maxU?.also { maxUSet = it } ?: run { maxUNull = NullValue.NULL_VALUE }
        cim.minQ?.also { minQSet = it } ?: run { minQNull = NullValue.NULL_VALUE }
        cim.minU?.also { minUSet = it } ?: run { minUNull = NullValue.NULL_VALUE }
        cim.mu?.also { muSet = it } ?: run { muNull = NullValue.NULL_VALUE }
        cim.r?.also { rSet = it } ?: run { rNull = NullValue.NULL_VALUE }
        cim.r0?.also { r0Set = it } ?: run { r0Null = NullValue.NULL_VALUE }
        cim.r2?.also { r2Set = it } ?: run { r2Null = NullValue.NULL_VALUE }
        cim.satDirectSubtransX?.also { satDirectSubtransXSet = it } ?: run { satDirectSubtransXNull = NullValue.NULL_VALUE }
        cim.satDirectSyncX?.also { satDirectSyncXSet = it } ?: run { satDirectSyncXNull = NullValue.NULL_VALUE }
        cim.satDirectTransX?.also { satDirectTransXSet = it } ?: run { satDirectTransXNull = NullValue.NULL_VALUE }
        cim.x0?.also { x0Set = it } ?: run { x0Null = NullValue.NULL_VALUE }
        cim.x2?.also { x2Set = it } ?: run { x2Null = NullValue.NULL_VALUE }
        type = mapSynchronousMachineKind.toPb(cim.type)
        operatingMode = mapSynchronousMachineKind.toPb(cim.operatingMode)

        toPb(cim, rmBuilder)
    }

/**
 * Convert the [TapChanger] into its protobuf counterpart.
 *
 * @param cim The [TapChanger] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: TapChanger, pb: PBTapChanger.Builder): PBTapChanger.Builder =
    pb.apply {
        cim.highStep?.also { highStepSet = it } ?: run { highStepNull = NullValue.NULL_VALUE }
        cim.lowStep?.also { lowStepSet = it } ?: run { lowStepNull = NullValue.NULL_VALUE }
        cim.step?.also { stepSet = it } ?: run { stepNull = NullValue.NULL_VALUE }
        cim.neutralStep?.also { neutralStepSet = it } ?: run { neutralStepNull = NullValue.NULL_VALUE }
        cim.neutralU?.also { neutralUSet = it } ?: run { neutralUNull = NullValue.NULL_VALUE }
        cim.normalStep?.also { normalStepSet = it } ?: run { normalStepNull = NullValue.NULL_VALUE }
        cim.controlEnabled?.also { controlEnabledSet = it } ?: run { controlEnabledNull = NullValue.NULL_VALUE }
        cim.tapChangerControl?.also { tapChangerControlMRID = it.mRID } ?: clearTapChangerControlMRID()

        toPb(cim, psrBuilder)
    }

/**
 * Convert the [TapChangerControl] into its protobuf counterpart.
 *
 * @param cim The [TapChangerControl] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: TapChangerControl, pb: PBTapChangerControl.Builder): PBTapChangerControl.Builder =
    pb.apply {
        cim.limitVoltage?.also { limitVoltageSet = it } ?: run { limitVoltageNull = NullValue.NULL_VALUE }
        cim.lineDropCompensation?.also { lineDropCompensationSet = it } ?: run { lineDropCompensationNull = NullValue.NULL_VALUE }
        cim.lineDropR?.also { lineDropRSet = it } ?: run { lineDropRNull = NullValue.NULL_VALUE }
        cim.lineDropX?.also { lineDropXSet = it } ?: run { lineDropXNull = NullValue.NULL_VALUE }
        cim.reverseLineDropR?.also { reverseLineDropRSet = it } ?: run { reverseLineDropRNull = NullValue.NULL_VALUE }
        cim.reverseLineDropX?.also { reverseLineDropXSet = it } ?: run { reverseLineDropXNull = NullValue.NULL_VALUE }

        cim.forwardLDCBlocking?.also { forwardLDCBlockingSet = it } ?: run { forwardLDCBlockingNull = NullValue.NULL_VALUE }

        cim.timeDelay?.also { timeDelaySet = it } ?: run { timeDelayNull = NullValue.NULL_VALUE }

        cim.coGenerationEnabled?.also { coGenerationEnabledSet = it } ?: run { coGenerationEnabledNull = NullValue.NULL_VALUE }

        toPb(cim, rcBuilder)
    }

/**
 * Convert the [TransformerEnd] into its protobuf counterpart.
 *
 * @param cim The [TransformerEnd] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: TransformerEnd, pb: PBTransformerEnd.Builder): PBTransformerEnd.Builder =
    pb.apply {
        cim.terminal?.also { terminalMRID = it.mRID } ?: clearTerminalMRID()
        cim.baseVoltage?.also { baseVoltageMRID = it.mRID } ?: clearBaseVoltageMRID()
        cim.ratioTapChanger?.also { ratioTapChangerMRID = it.mRID } ?: clearRatioTapChangerMRID()
        cim.starImpedance?.also { starImpedanceMRID = it.mRID } ?: clearStarImpedanceMRID()
        endNumber = cim.endNumber
        cim.grounded?.also { groundedSet = it } ?: run { groundedNull = NullValue.NULL_VALUE }
        cim.rGround?.also { rGroundSet = it } ?: run { rGroundNull = NullValue.NULL_VALUE }
        cim.xGround?.also { xGroundSet = it } ?: run { xGroundNull = NullValue.NULL_VALUE }
        toPb(cim, ioBuilder)
    }

/**
 * Convert the [TransformerEndRatedS] into its protobuf counterpart.
 *
 * @param cim The [TransformerEndRatedS] to convert.
 * @return The protobuf form of [cim].
 */
fun toPb(cim: TransformerEndRatedS): PBTransformerEndRatedS.Builder =
    PBTransformerEndRatedS.newBuilder().apply {
        ratedS = cim.ratedS
        coolingType = mapTransformerCoolingType.toPb(cim.coolingType)
    }

/**
 * Convert the [TransformerStarImpedance] into its protobuf counterpart.
 *
 * @param cim The [TransformerStarImpedance] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: TransformerStarImpedance, pb: PBTransformerStarImpedance.Builder): PBTransformerStarImpedance.Builder =
    pb.apply {
        cim.transformerEndInfo?.also { transformerEndInfoMRID = it.mRID } ?: clearTransformerEndInfoMRID()
        cim.r?.also { rSet = it } ?: run { rNull = NullValue.NULL_VALUE }
        cim.r0?.also { r0Set = it } ?: run { r0Null = NullValue.NULL_VALUE }
        cim.x?.also { xSet = it } ?: run { xNull = NullValue.NULL_VALUE }
        cim.x0?.also { x0Set = it } ?: run { x0Null = NullValue.NULL_VALUE }
        toPb(cim, ioBuilder)
    }

/**
 * An extension for converting any [AcLineSegment] into its protobuf counterpart.
 */
fun AcLineSegment.toPb(): PBAcLineSegment = toPb(this, PBAcLineSegment.newBuilder()).build()

/**
 * An extension for converting any [AcLineSegmentPhase] into its protobuf counterpart.
 */
fun AcLineSegmentPhase.toPb(): PBAcLineSegmentPhase = toPb(this, PBAcLineSegmentPhase.newBuilder()).build()

/**
 * An extension for converting any [Breaker] into its protobuf counterpart.
 */
fun Breaker.toPb(): PBBreaker = toPb(this, PBBreaker.newBuilder()).build()

/**
 * An extension for converting any [BusbarSection] into its protobuf counterpart.
 */
fun BusbarSection.toPb(): PBBusbarSection = toPb(this, PBBusbarSection.newBuilder()).build()

/**
 * An extension for converting any [Clamp] into its protobuf counterpart.
 */
fun Clamp.toPb(): PBClamp = toPb(this, PBClamp.newBuilder()).build()

/**
 * An extension for converting any [Cut] into its protobuf counterpart.
 */
fun Cut.toPb(): PBCut = toPb(this, PBCut.newBuilder()).build()

/**
 * An extension for converting any [Disconnector] into its protobuf counterpart.
 */
fun Disconnector.toPb(): PBDisconnector = toPb(this, PBDisconnector.newBuilder()).build()

/**
 * An extension for converting any [EnergyConsumer] into its protobuf counterpart.
 */
fun EnergyConsumer.toPb(): PBEnergyConsumer = toPb(this, PBEnergyConsumer.newBuilder()).build()

/**
 * An extension for converting any [EnergyConsumerPhase] into its protobuf counterpart.
 */
fun EnergyConsumerPhase.toPb(): PBEnergyConsumerPhase = toPb(this, PBEnergyConsumerPhase.newBuilder()).build()

/**
 * An extension for converting any [EnergySource] into its protobuf counterpart.
 */
fun EnergySource.toPb(): PBEnergySource = toPb(this, PBEnergySource.newBuilder()).build()

/**
 * An extension for converting any [EnergySourcePhase] into its protobuf counterpart.
 */
fun EnergySourcePhase.toPb(): PBEnergySourcePhase = toPb(this, PBEnergySourcePhase.newBuilder()).build()

/**
 * An extension for converting any [Fuse] into its protobuf counterpart.
 */
fun Fuse.toPb(): PBFuse = toPb(this, PBFuse.newBuilder()).build()

/**
 * An extension for converting any [Ground] into its protobuf counterpart.
 */
fun Ground.toPb(): PBGround = toPb(this, PBGround.newBuilder()).build()

/**
 * An extension for converting any [GroundDisconnector] into its protobuf counterpart.
 */
fun GroundDisconnector.toPb(): PBGroundDisconnector = toPb(this, PBGroundDisconnector.newBuilder()).build()

/**
 * An extension for converting any [GroundingImpedance] into its protobuf counterpart.
 */
fun GroundingImpedance.toPb(): PBGroundingImpedance = toPb(this, PBGroundingImpedance.newBuilder()).build()

/**
 * An extension for converting any [Jumper] into its protobuf counterpart.
 */
fun Jumper.toPb(): PBJumper = toPb(this, PBJumper.newBuilder()).build()

/**
 * An extension for converting any [Junction] into its protobuf counterpart.
 */
fun Junction.toPb(): PBJunction = toPb(this, PBJunction.newBuilder()).build()

/**
 * An extension for converting any [LinearShuntCompensator] into its protobuf counterpart.
 */
fun LinearShuntCompensator.toPb(): PBLinearShuntCompensator = toPb(this, PBLinearShuntCompensator.newBuilder()).build()

/**
 * An extension for converting any [LoadBreakSwitch] into its protobuf counterpart.
 */
fun LoadBreakSwitch.toPb(): PBLoadBreakSwitch = toPb(this, PBLoadBreakSwitch.newBuilder()).build()

/**
 * An extension for converting any [PerLengthPhaseImpedance] into its protobuf counterpart.
 */
fun PerLengthPhaseImpedance.toPb(): PBPerLengthPhaseImpedance = toPb(this, PBPerLengthPhaseImpedance.newBuilder()).build()

/**
 * An extension for converting any [PerLengthSequenceImpedance] into its protobuf counterpart.
 */
fun PerLengthSequenceImpedance.toPb(): PBPerLengthSequenceImpedance = toPb(this, PBPerLengthSequenceImpedance.newBuilder()).build()

/**
 * An extension for converting any [PetersenCoil] into its protobuf counterpart.
 */
fun PetersenCoil.toPb(): PBPetersenCoil = toPb(this, PBPetersenCoil.newBuilder()).build()

/**
 * An extension for converting any [PowerElectronicsConnection] into its protobuf counterpart.
 */
fun PowerElectronicsConnection.toPb(): PBPowerElectronicsConnection = toPb(this, PBPowerElectronicsConnection.newBuilder()).build()

/**
 * An extension for converting any [PowerElectronicsConnectionPhase] into its protobuf counterpart.
 */
fun PowerElectronicsConnectionPhase.toPb(): PBPowerElectronicsConnectionPhase = toPb(this, PBPowerElectronicsConnectionPhase.newBuilder()).build()

/**
 * An extension for converting any [PowerTransformer] into its protobuf counterpart.
 */
fun PowerTransformer.toPb(): PBPowerTransformer = toPb(this, PBPowerTransformer.newBuilder()).build()

/**
 * An extension for converting any [PowerTransformerEnd] into its protobuf counterpart.
 */
fun PowerTransformerEnd.toPb(): PBPowerTransformerEnd = toPb(this, PBPowerTransformerEnd.newBuilder()).build()

/**
 * An extension for converting any [RatioTapChanger] into its protobuf counterpart.
 */
fun RatioTapChanger.toPb(): PBRatioTapChanger = toPb(this, PBRatioTapChanger.newBuilder()).build()

/**
 * An extension for converting any [ReactiveCapabilityCurve] into its protobuf counterpart.
 */
fun ReactiveCapabilityCurve.toPb(): PBReactiveCapabilityCurve = toPb(this, PBReactiveCapabilityCurve.newBuilder()).build()

/**
 * An extension for converting any [Recloser] into its protobuf counterpart.
 */
fun Recloser.toPb(): PBRecloser = toPb(this, PBRecloser.newBuilder()).build()

/**
 * An extension for converting any [SeriesCompensator] into its protobuf counterpart.
 */
fun SeriesCompensator.toPb(): PBSeriesCompensator = toPb(this, PBSeriesCompensator.newBuilder()).build()

/**
 * An extension for converting any [StaticVarCompensator] into its protobuf counterpart.
 */
fun StaticVarCompensator.toPb(): PBStaticVarCompensator = toPb(this, PBStaticVarCompensator.newBuilder()).build()

/**
 * An extension for converting any [SynchronousMachine] into its protobuf counterpart.
 */
fun SynchronousMachine.toPb(): PBSynchronousMachine = toPb(this, PBSynchronousMachine.newBuilder()).build()

/**
 * An extension for converting any [TapChangerControl] into its protobuf counterpart.
 */
fun TapChangerControl.toPb(): PBTapChangerControl = toPb(this, PBTapChangerControl.newBuilder()).build()

/**
 * An extension for converting any [TransformerStarImpedance] into its protobuf counterpart.
 */
fun TransformerStarImpedance.toPb(): PBTransformerStarImpedance = toPb(this, PBTransformerStarImpedance.newBuilder()).build()

// ###############################
// # IEC61970 InfIEC61970 Feeder #
// ###############################

/**
 * Convert the [Circuit] into its protobuf counterpart.
 *
 * @param cim The [Circuit] to convert.
 * @param pb The protobuf builder to populate.
 * @return [pb] for fluent use.
 */
fun toPb(cim: Circuit, pb: PBCircuit.Builder): PBCircuit.Builder =
    pb.apply {
        cim.loop?.also { loopMRID = it.mRID } ?: clearLoopMRID()

        clearEndTerminalMRIDs()
        cim.endTerminals.forEach { addEndTerminalMRIDs(it.mRID) }

        clearEndSubstationMRIDs()
        cim.endSubstations.forEach { addEndSubstationMRIDs(it.mRID) }

        toPb(cim, lBuilder)
    }

/**
 * An extension for converting any [Circuit] into its protobuf counterpart.
 */
fun Circuit.toPb(): PBCircuit = toPb(this, PBCircuit.newBuilder()).build()

// #################################
// # Class for Java friendly usage #
// #################################

/**
 * A helper class for Java friendly convertion from CIM objects to their protobuf counterparts.
 */
class NetworkCimToProto : BaseCimToProto() {

    // ##################################
    // # Extensions IEC61968 Asset Info #
    // ##################################

    /**
     * Convert the [RelayInfo] into its protobuf counterpart.
     *
     * @param cim The [RelayInfo] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: RelayInfo): PBRelayInfo = cim.toPb()

    // ##############################
    // # Extensions IEC61968 Common #
    // ##############################

    /**
     * Convert the [ContactDetails] into its protobuf counterpart.
     *
     * @param cim The [ContactDetails] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: ContactDetails): PBContactDetails = cim.toPb()

    // ################################
    // # Extensions IEC61968 Metering #
    // ################################

    /**
     * Convert the [PanDemandResponseFunction] into its protobuf counterpart.
     *
     * @param cim The [PanDemandResponseFunction] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: PanDemandResponseFunction): PBPanDemandResponseFunction = cim.toPb()

    // #################################
    // # Extensions IEC61970 Base Core #
    // #################################

    /**
     * Convert the [Site] into its protobuf counterpart.
     *
     * @param cim The [Site] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: Site): PBSite = cim.toPb()

    /**
     * Convert the [HvCustomer] into its protobuf counterpart.
     *
     * @param cim The [HvCustomer] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: HvCustomer): PBHvCustomer = cim.toPb()

    // ###################################
    // # Extensions IEC61970 Base Feeder #
    // ###################################

    /**
     * Convert the [Loop] into its protobuf counterpart.
     *
     * @param cim The [Loop] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: Loop): PBLoop = cim.toPb()

    /**
     * Convert the [LvFeeder] into its protobuf counterpart.
     *
     * @param cim The [LvFeeder] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: LvFeeder): PBLvFeeder = cim.toPb()

    /**
     * Convert the [LvSubstation] into its protobuf counterpart.
     *
     * @param cim The [LvSubstation] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: LvSubstation): PBLvSubstation = cim.toPb()

    // ##################################################
    // # Extensions IEC61970 Base Generation Production #
    // ##################################################

    /**
     * Convert the [EvChargingUnit] into its protobuf counterpart.
     *
     * @param cim The [EvChargingUnit] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: EvChargingUnit): PBEvChargingUnit = cim.toPb()

    // #######################################
    // # Extensions IEC61970 Base Protection #
    // #######################################

    /**
     * Convert the [DirectionalCurrentRelay] into its protobuf counterpart.
     *
     * @param cim The [DirectionalCurrentRelay] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: DirectionalCurrentRelay): PBDirectionalCurrentRelay = cim.toPb()

    /**
     * Convert the [DistanceRelay] into its protobuf counterpart.
     *
     * @param cim The [DistanceRelay] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: DistanceRelay): PBDistanceRelay = cim.toPb()

    /**
     * Convert the [ProtectionRelayScheme] into its protobuf counterpart.
     *
     * @param cim The [ProtectionRelayScheme] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: ProtectionRelayScheme): PBProtectionRelayScheme = cim.toPb()

    /**
     * Convert the [ProtectionRelaySystem] into its protobuf counterpart.
     *
     * @param cim The [ProtectionRelaySystem] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: ProtectionRelaySystem): PBProtectionRelaySystem = cim.toPb()

    /**
     * Convert the [VoltageRelay] into its protobuf counterpart.
     *
     * @param cim The [VoltageRelay] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: VoltageRelay): PBVoltageRelay = cim.toPb()

    // ##################################
    // # Extensions IEC61970 Base Wires #
    // ##################################

    /**
     * Convert the [BatteryControl] into its protobuf counterpart.
     *
     * @param cim The [BatteryControl] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: BatteryControl): PBBatteryControl = cim.toPb()

    // #######################
    // # IEC61968 Asset Info #
    // #######################

    /**
     * Convert the [CableInfo] into its protobuf counterpart.
     *
     * @param cim The [CableInfo] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: CableInfo): PBCableInfo = cim.toPb()

    /**
     * Convert the [NoLoadTest] into its protobuf counterpart.
     *
     * @param cim The [NoLoadTest] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: NoLoadTest): PBNoLoadTest = cim.toPb()

    /**
     * Convert the [OpenCircuitTest] into its protobuf counterpart.
     *
     * @param cim The [OpenCircuitTest] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: OpenCircuitTest): PBOpenCircuitTest = cim.toPb()

    /**
     * Convert the [OverheadWireInfo] into its protobuf counterpart.
     *
     * @param cim The [OverheadWireInfo] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: OverheadWireInfo): PBOverheadWireInfo = cim.toPb()

    /**
     * Convert the [PowerTransformerInfo] into its protobuf counterpart.
     *
     * @param cim The [PowerTransformerInfo] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: PowerTransformerInfo): PBPowerTransformerInfo = cim.toPb()

    /**
     * Convert the [ShortCircuitTest] into its protobuf counterpart.
     *
     * @param cim The [ShortCircuitTest] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: ShortCircuitTest): PBShortCircuitTest = cim.toPb()

    /**
     * Convert the [ShuntCompensatorInfo] into its protobuf counterpart.
     *
     * @param cim The [ShuntCompensatorInfo] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: ShuntCompensatorInfo): PBShuntCompensatorInfo = cim.toPb()

    /**
     * Convert the [SwitchInfo] into its protobuf counterpart.
     *
     * @param cim The [SwitchInfo] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: SwitchInfo): PBSwitchInfo = cim.toPb()

    /**
     * Convert the [TransformerEndInfo] into its protobuf counterpart.
     *
     * @param cim The [TransformerEndInfo] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: TransformerEndInfo): PBTransformerEndInfo = cim.toPb()

    /**
     * Convert the [TransformerTankInfo] into its protobuf counterpart.
     *
     * @param cim The [TransformerTankInfo] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: TransformerTankInfo): PBTransformerTankInfo = cim.toPb()

    // ###################
    // # IEC61968 Assets #
    // ###################

    /**
     * Convert the [AssetOwner] into its protobuf counterpart.
     *
     * @param cim The [AssetOwner] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: AssetOwner): PBAssetOwner = cim.toPb()

    /**
     * Convert the [Streetlight] into its protobuf counterpart.
     *
     * @param cim The [Streetlight] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: Streetlight): PBStreetlight = cim.toPb()

    // ###################
    // # IEC61968 Common #
    // ###################

    /**
     * Convert the [Location] into its protobuf counterpart.
     *
     * @param cim The [Location] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: Location): PBLocation = cim.toPb()

    // #####################################
    // # IEC61968 infIEC61968 InfAssetInfo #
    // #####################################

    /**
     * Convert the [CurrentTransformerInfo] into its protobuf counterpart.
     *
     * @param cim The [CurrentTransformerInfo] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: CurrentTransformerInfo): PBCurrentTransformerInfo = cim.toPb()

    /**
     * Convert the [PotentialTransformerInfo] into its protobuf counterpart.
     *
     * @param cim The [PotentialTransformerInfo] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: PotentialTransformerInfo): PBPotentialTransformerInfo = cim.toPb()

    // ##################################
    // # IEC61968 infIEC61968 InfAssets #
    // ##################################

    /**
     * Convert the [Pole] into its protobuf counterpart.
     *
     * @param cim The [Pole] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: Pole): PBPole = cim.toPb()

    // #####################
    // # IEC61968 Metering #
    // #####################

    /**
     * Convert the [Meter] into its protobuf counterpart.
     *
     * @param cim The [Meter] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: Meter): PBMeter = cim.toPb()

    /**
     * Convert the [UsagePoint] into its protobuf counterpart.
     *
     * @param cim The [UsagePoint] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: UsagePoint): PBUsagePoint = cim.toPb()

    // #######################
    // # IEC61968 Operations #
    // #######################

    /**
     * Convert the [OperationalRestriction] into its protobuf counterpart.
     *
     * @param cim The [OperationalRestriction] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: OperationalRestriction): PBOperationalRestriction = cim.toPb()

    // #####################################
    // # IEC61970 Base Auxiliary Equipment #
    // #####################################

    /**
     * Convert the [CurrentTransformer] into its protobuf counterpart.
     *
     * @param cim The [CurrentTransformer] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: CurrentTransformer): PBCurrentTransformer = cim.toPb()

    /**
     * Convert the [FaultIndicator] into its protobuf counterpart.
     *
     * @param cim The [FaultIndicator] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: FaultIndicator): PBFaultIndicator = cim.toPb()

    /**
     * Convert the [PotentialTransformer] into its protobuf counterpart.
     *
     * @param cim The [PotentialTransformer] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: PotentialTransformer): PBPotentialTransformer = cim.toPb()

    // ######################
    // # IEC61970 Base Core #
    // ######################

    /**
     * Convert the [BaseVoltage] into its protobuf counterpart.
     *
     * @param cim The [BaseVoltage] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: BaseVoltage): PBBaseVoltage = cim.toPb()

    /**
     * Convert the [ConnectivityNode] into its protobuf counterpart.
     *
     * @param cim The [ConnectivityNode] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: ConnectivityNode): PBConnectivityNode = cim.toPb()

    /**
     * Convert the [CurveData] into its protobuf counterpart.
     *
     * @param cim The [CurveData] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: CurveData): PBCurveData = cim.toPb()

    /**
     * Convert the [Feeder] into its protobuf counterpart.
     *
     * @param cim The [Feeder] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: Feeder): PBFeeder = cim.toPb()

    /**
     * Convert the [GeographicalRegion] into its protobuf counterpart.
     *
     * @param cim The [GeographicalRegion] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: GeographicalRegion): PBGeographicalRegion = cim.toPb()

    /**
     * Convert the [SubGeographicalRegion] into its protobuf counterpart.
     *
     * @param cim The [SubGeographicalRegion] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: SubGeographicalRegion): PBSubGeographicalRegion = cim.toPb()

    /**
     * Convert the [Substation] into its protobuf counterpart.
     *
     * @param cim The [Substation] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: Substation): PBSubstation = cim.toPb()

    /**
     * Convert the [Terminal] into its protobuf counterpart.
     *
     * @param cim The [Terminal] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: Terminal): PBTerminal = cim.toPb()

    // #############################
    // # IEC61970 Base Equivalents #
    // #############################

    /**
     * Convert the [EquivalentBranch] into its protobuf counterpart.
     *
     * @param cim The [EquivalentBranch] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: EquivalentBranch): PBEquivalentBranch = cim.toPb()

    // #######################################
    // # IEC61970 Base Generation Production #
    // #######################################

    /**
     * Convert the [BatteryUnit] into its protobuf counterpart.
     *
     * @param cim The [BatteryUnit] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: BatteryUnit): PBBatteryUnit = cim.toPb()

    /**
     * Convert the [PhotoVoltaicUnit] into its protobuf counterpart.
     *
     * @param cim The [PhotoVoltaicUnit] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: PhotoVoltaicUnit): PBPhotoVoltaicUnit = cim.toPb()

    /**
     * Convert the [PowerElectronicsWindUnit] into its protobuf counterpart.
     *
     * @param cim The [PowerElectronicsWindUnit] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: PowerElectronicsWindUnit): PBPowerElectronicsWindUnit = cim.toPb()

    // ######################
    // # IEC61970 Base Meas #
    // ######################

    /**
     * Convert the [Accumulator] into its protobuf counterpart.
     *
     * @param cim The [Accumulator] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: Accumulator): PBAccumulator = cim.toPb()

    /**
     * Convert the [Analog] into its protobuf counterpart.
     *
     * @param cim The [Analog] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: Analog): PBAnalog = cim.toPb()

    /**
     * Convert the [Control] into its protobuf counterpart.
     *
     * @param cim The [Control] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: Control): PBControl = cim.toPb()

    /**
     * Convert the [Discrete] into its protobuf counterpart.
     *
     * @param cim The [Discrete] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: Discrete): PBDiscrete = cim.toPb()

    // ############################
    // # IEC61970 Base Protection #
    // ############################

    /**
     * Convert the [CurrentRelay] into its protobuf counterpart.
     *
     * @param cim The [CurrentRelay] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: CurrentRelay): PBCurrentRelay = cim.toPb()

    // #######################
    // # IEC61970 Base Scada #
    // #######################

    /**
     * Convert the [RemoteControl] into its protobuf counterpart.
     *
     * @param cim The [RemoteControl] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: RemoteControl): PBRemoteControl = cim.toPb()

    /**
     * Convert the [RemoteSource] into its protobuf counterpart.
     *
     * @param cim The [RemoteSource] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: RemoteSource): PBRemoteSource = cim.toPb()

    // #######################
    // # IEC61970 Base Wires #
    // #######################

    /**
     * Convert the [AcLineSegment] into its protobuf counterpart.
     *
     * @param cim The [AcLineSegment] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: AcLineSegment): PBAcLineSegment = cim.toPb()

    /**
     * Convert the [AcLineSegmentPhase] into its protobuf counterpart.
     *
     * @param cim The [AcLineSegmentPhase] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: AcLineSegmentPhase): PBAcLineSegmentPhase = cim.toPb()

    /**
     * Convert the [Breaker] into its protobuf counterpart.
     *
     * @param cim The [Breaker] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: Breaker): PBBreaker = cim.toPb()

    /**
     * Convert the [BusbarSection] into its protobuf counterpart.
     *
     * @param cim The [BusbarSection] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: BusbarSection): PBBusbarSection = cim.toPb()

    /**
     * Convert the [Clamp] into its protobuf counterpart.
     *
     * @param cim The [Clamp] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: Clamp): PBClamp = cim.toPb()

    /**
     * Convert the [Cut] into its protobuf counterpart.
     *
     * @param cim The [Cut] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: Cut): PBCut = cim.toPb()

    /**
     * Convert the [Disconnector] into its protobuf counterpart.
     *
     * @param cim The [Disconnector] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: Disconnector): PBDisconnector = cim.toPb()

    /**
     * Convert the [EnergyConsumer] into its protobuf counterpart.
     *
     * @param cim The [EnergyConsumer] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: EnergyConsumer): PBEnergyConsumer = cim.toPb()

    /**
     * Convert the [EnergyConsumerPhase] into its protobuf counterpart.
     *
     * @param cim The [EnergyConsumerPhase] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: EnergyConsumerPhase): PBEnergyConsumerPhase = cim.toPb()

    /**
     * Convert the [EnergySource] into its protobuf counterpart.
     *
     * @param cim The [EnergySource] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: EnergySource): PBEnergySource = cim.toPb()

    /**
     * Convert the [EnergySourcePhase] into its protobuf counterpart.
     *
     * @param cim The [EnergySourcePhase] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: EnergySourcePhase): PBEnergySourcePhase = cim.toPb()

    /**
     * Convert the [Fuse] into its protobuf counterpart.
     *
     * @param cim The [Fuse] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: Fuse): PBFuse = cim.toPb()

    /**
     * Convert the [Ground] into its protobuf counterpart.
     *
     * @param cim The [Ground] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: Ground): PBGround = cim.toPb()

    /**
     * Convert the [GroundDisconnector] into its protobuf counterpart.
     *
     * @param cim The [GroundDisconnector] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: GroundDisconnector): PBGroundDisconnector = cim.toPb()

    /**
     * Convert the [GroundingImpedance] into its protobuf counterpart.
     *
     * @param cim The [GroundingImpedance] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: GroundingImpedance): PBGroundingImpedance = cim.toPb()

    /**
     * Convert the [Jumper] into its protobuf counterpart.
     *
     * @param cim The [Jumper] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: Jumper): PBJumper = cim.toPb()

    /**
     * Convert the [Junction] into its protobuf counterpart.
     *
     * @param cim The [Junction] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: Junction): PBJunction = cim.toPb()

    /**
     * Convert the [LinearShuntCompensator] into its protobuf counterpart.
     *
     * @param cim The [LinearShuntCompensator] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: LinearShuntCompensator): PBLinearShuntCompensator = cim.toPb()

    /**
     * Convert the [LoadBreakSwitch] into its protobuf counterpart.
     *
     * @param cim The [LoadBreakSwitch] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: LoadBreakSwitch): PBLoadBreakSwitch = cim.toPb()

    /**
     * Convert the [PerLengthPhaseImpedance] into its protobuf counterpart.
     *
     * @param cim The [PerLengthPhaseImpedance] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: PerLengthPhaseImpedance): PBPerLengthPhaseImpedance = cim.toPb()

    /**
     * Convert the [PerLengthSequenceImpedance] into its protobuf counterpart.
     *
     * @param cim The [PerLengthSequenceImpedance] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: PerLengthSequenceImpedance): PBPerLengthSequenceImpedance = cim.toPb()

    /**
     * Convert the [PetersenCoil] into its protobuf counterpart.
     *
     * @param cim The [PetersenCoil] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: PetersenCoil): PBPetersenCoil = cim.toPb()

    /**
     * Convert the [PowerElectronicsConnection] into its protobuf counterpart.
     *
     * @param cim The [PowerElectronicsConnection] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: PowerElectronicsConnection): PBPowerElectronicsConnection = cim.toPb()

    /**
     * Convert the [PowerElectronicsConnectionPhase] into its protobuf counterpart.
     *
     * @param cim The [PowerElectronicsConnectionPhase] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: PowerElectronicsConnectionPhase): PBPowerElectronicsConnectionPhase = cim.toPb()

    /**
     * Convert the [PowerTransformer] into its protobuf counterpart.
     *
     * @param cim The [PowerTransformer] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: PowerTransformer): PBPowerTransformer = cim.toPb()

    /**
     * Convert the [PowerTransformerEnd] into its protobuf counterpart.
     *
     * @param cim The [PowerTransformerEnd] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: PowerTransformerEnd): PBPowerTransformerEnd = cim.toPb()

    /**
     * Convert the [RatioTapChanger] into its protobuf counterpart.
     *
     * @param cim The [RatioTapChanger] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: RatioTapChanger): PBRatioTapChanger = cim.toPb()

    /**
     * Convert the [ReactiveCapabilityCurve] into its protobuf counterpart.
     *
     * @param cim The [ReactiveCapabilityCurve] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: ReactiveCapabilityCurve): PBReactiveCapabilityCurve = cim.toPb()

    /**
     * Convert the [Recloser] into its protobuf counterpart.
     *
     * @param cim The [Recloser] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: Recloser): PBRecloser = cim.toPb()

    /**
     * Convert the [SeriesCompensator] into its protobuf counterpart.
     *
     * @param cim The [SeriesCompensator] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: SeriesCompensator): PBSeriesCompensator = cim.toPb()

    /**
     * Convert the [StaticVarCompensator] into its protobuf counterpart.
     *
     * @param cim The [StaticVarCompensator] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: StaticVarCompensator): PBStaticVarCompensator = cim.toPb()

    /**
     * Convert the [SynchronousMachine] into its protobuf counterpart.
     *
     * @param cim The [SynchronousMachine] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: SynchronousMachine): PBSynchronousMachine = cim.toPb()

    /**
     * Convert the [TapChangerControl] into its protobuf counterpart.
     *
     * @param cim The [TapChangerControl] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: TapChangerControl): PBTapChangerControl = cim.toPb()

    /**
     * Convert the [TransformerStarImpedance] into its protobuf counterpart.
     *
     * @param cim The [TransformerStarImpedance] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: TransformerStarImpedance): PBTransformerStarImpedance = cim.toPb()

    // ###############################
    // # IEC61970 InfIEC61970 Feeder #
    // ###############################

    /**
     * Convert the [Circuit] into its protobuf counterpart.
     *
     * @param cim The [Circuit] to convert.
     * @return The protobuf form of [cim].
     */
    fun toPb(cim: Circuit): PBCircuit = cim.toPb()

}

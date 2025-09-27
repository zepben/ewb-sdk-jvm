/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.network.translator

import com.zepben.ewb.cim.extensions.iec61968.assetinfo.RelayInfo
import com.zepben.ewb.cim.extensions.iec61968.common.ContactDetails
import com.zepben.ewb.cim.extensions.iec61968.metering.PanDemandResponseFunction
import com.zepben.ewb.cim.extensions.iec61970.base.core.Site
import com.zepben.ewb.cim.extensions.iec61970.base.feeder.Loop
import com.zepben.ewb.cim.extensions.iec61970.base.feeder.LvFeeder
import com.zepben.ewb.cim.extensions.iec61970.base.generation.production.EvChargingUnit
import com.zepben.ewb.cim.extensions.iec61970.base.protection.*
import com.zepben.ewb.cim.extensions.iec61970.base.wires.*
import com.zepben.ewb.cim.iec61968.assetinfo.*
import com.zepben.ewb.cim.iec61968.assets.*
import com.zepben.ewb.cim.iec61968.common.*
import com.zepben.ewb.cim.iec61968.infiec61968.infassetinfo.*
import com.zepben.ewb.cim.iec61968.infiec61968.infassets.Pole
import com.zepben.ewb.cim.iec61968.infiec61968.infcommon.Ratio
import com.zepben.ewb.cim.iec61968.metering.*
import com.zepben.ewb.cim.iec61968.operations.OperationalRestriction
import com.zepben.ewb.cim.iec61970.base.auxiliaryequipment.*
import com.zepben.ewb.cim.iec61970.base.core.*
import com.zepben.ewb.cim.iec61970.base.equivalents.EquivalentBranch
import com.zepben.ewb.cim.iec61970.base.equivalents.EquivalentEquipment
import com.zepben.ewb.cim.iec61970.base.generation.production.*
import com.zepben.ewb.cim.iec61970.base.meas.*
import com.zepben.ewb.cim.iec61970.base.protection.*
import com.zepben.ewb.cim.iec61970.base.scada.RemoteControl
import com.zepben.ewb.cim.iec61970.base.scada.RemotePoint
import com.zepben.ewb.cim.iec61970.base.scada.RemoteSource
import com.zepben.ewb.cim.iec61970.base.wires.*
import com.zepben.ewb.cim.iec61970.infiec61970.feeder.Circuit
import com.zepben.ewb.services.common.Resolvers
import com.zepben.ewb.services.common.translator.BaseProtoToCim
import com.zepben.ewb.services.common.translator.toCim
import com.zepben.ewb.services.common.translator.toInstant
import com.zepben.ewb.services.network.NetworkService
import com.zepben.protobuf.cim.extensions.iec61968.assetinfo.RelayInfo as PBRelayInfo
import com.zepben.protobuf.cim.extensions.iec61968.common.ContactDetails as PBContactDetails
import com.zepben.protobuf.cim.extensions.iec61968.metering.PanDemandResponseFunction as PBPanDemandResponseFunction
import com.zepben.protobuf.cim.extensions.iec61970.base.core.Site as PBSite
import com.zepben.protobuf.cim.extensions.iec61970.base.feeder.Loop as PBLoop
import com.zepben.protobuf.cim.extensions.iec61970.base.feeder.LvFeeder as PBLvFeeder
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
import com.zepben.protobuf.cim.iec61968.common.Organisation as PBOrganisation
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
import com.zepben.protobuf.cim.iec61970.base.core.NameType as PBNameType
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

// ##################################
// # Extensions IEC61968 Asset Info #
// ##################################

/**
 * Convert the protobuf [PBRelayInfo] into its CIM counterpart.
 *
 * @param pb The protobuf [PBRelayInfo] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [RelayInfo].
 */
fun toCim(pb: PBRelayInfo, networkService: NetworkService): RelayInfo =
    RelayInfo(pb.mRID()).apply {
        curveSetting = pb.curveSettingSet.takeUnless { pb.hasCurveSettingNull() }
        recloseFast = pb.recloseFastSet.takeUnless { pb.hasRecloseFastNull() }
        pb.recloseDelaysList.forEach {
            addDelay(it)
        }
        toCim(pb.ai, this, networkService)
    }

/**
 * An extension to add a converted copy of the protobuf [PBRelayInfo] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBRelayInfo): RelayInfo? = tryAddOrNull(toCim(pb, this))

// ##############################
// # Extensions IEC61968 Common #
// ##############################

/**
 * Convert the protobuf [PBContactDetails] into its CIM counterpart.
 *
 * @param pb The protobuf [PBContactDetails] to convert.
 * @return The converted [pb] as a CIM [ContactDetails].
 */
fun toCim(pb: PBContactDetails): ContactDetails =
    ContactDetails(pb.id).apply {
        pb.phoneNumbersList.forEach { addPhoneNumber(toCim(it)) }
        contactAddress = if (pb.hasContactAddress()) toCim(pb.contactAddress) else null
        pb.electronicAddressesList.forEach { addElectronicAddress(toCim(it)) }
        contactType = pb.contactTypeSet.takeUnless { pb.hasContactTypeNull() }
        firstName = pb.firstNameSet.takeUnless { pb.hasFirstNameNull() }
        lastName = pb.lastNameSet.takeUnless { pb.hasLastNameNull() }
        preferredContactMethod = mapContactMethodType.toCim(pb.preferredContactMethod)
        isPrimary = pb.isPrimarySet.takeUnless { pb.hasIsPrimaryNull() }
        businessName = pb.businessNameSet.takeUnless { pb.hasBusinessNameNull() }
    }

// ################################
// # Extensions IEC61968 Metering #
// ################################

/**
 * Convert the protobuf [PBPanDemandResponseFunction] into its CIM counterpart.
 *
 * @param pb The protobuf [PBPanDemandResponseFunction] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [PanDemandResponseFunction].
 */
fun toCim(pb: PBPanDemandResponseFunction, networkService: NetworkService): PanDemandResponseFunction =
    PanDemandResponseFunction(pb.mRID()).apply {
        kind = pb.kind?.let { k -> mapEndDeviceFunctionKind.toCim(k) } ?: EndDeviceFunctionKind.UNKNOWN
        applianceBitmask = pb.applianceSet.takeUnless { pb.hasApplianceNull() }
    }.also {
        toCim(pb.edf, it, networkService)
    }

/**
 * An extension to add a converted copy of the protobuf [PBPanDemandResponseFunction] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBPanDemandResponseFunction): PanDemandResponseFunction? = tryAddOrNull(toCim(pb, this))

// #################################
// # Extensions IEC61970 Base Core #
// #################################

/**
 * Convert the protobuf [PBSite] into its CIM counterpart.
 *
 * @param pb The protobuf [PBSite] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [Site].
 */
fun toCim(pb: PBSite, networkService: NetworkService): Site =
    Site(pb.mRID()).apply {
        toCim(pb.ec, this, networkService)
    }

/**
 * An extension to add a converted copy of the protobuf [PBSite] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBSite): Site? = tryAddOrNull(toCim(pb, this))

// ###################################
// # Extensions IEC61970 Base Feeder #
// ###################################

/**
 * Convert the protobuf [PBLoop] into its CIM counterpart.
 *
 * @param pb The protobuf [PBLoop] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [Loop].
 */
fun toCim(pb: PBLoop, networkService: NetworkService): Loop =
    Loop(pb.mRID()).apply {
        pb.circuitMRIDsList.forEach { circuitMRID ->
            networkService.resolveOrDeferReference(Resolvers.circuits(this), circuitMRID)
        }
        pb.substationMRIDsList.forEach { substationMRID ->
            networkService.resolveOrDeferReference(Resolvers.substations(this), substationMRID)
        }
        pb.normalEnergizingSubstationMRIDsList.forEach { normalEnergizingSubstationMRID ->
            networkService.resolveOrDeferReference(Resolvers.normalEnergizingSubstations(this), normalEnergizingSubstationMRID)
        }

        toCim(pb.io, this, networkService)
    }

/**
 * Convert the protobuf [PBLvFeeder] into its CIM counterpart.
 *
 * @param pb The protobuf [PBLvFeeder] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [LvFeeder].
 */
fun toCim(pb: PBLvFeeder, networkService: NetworkService): LvFeeder =
    LvFeeder(pb.mRID()).apply {
        networkService.resolveOrDeferReference(Resolvers.normalHeadTerminal(this), pb.normalHeadTerminalMRID)
        pb.normalEnergizingFeederMRIDsList.forEach { normalEnergizingFeederMRID ->
            networkService.resolveOrDeferReference(Resolvers.normalEnergizingFeeders(this), normalEnergizingFeederMRID)
        }

        pb.currentlyEnergizingFeederMRIDsList.forEach { currentEnergizingFeederMRID ->
            networkService.resolveOrDeferReference(Resolvers.currentEnergizingFeeders(this), currentEnergizingFeederMRID)
        }
        toCim(pb.ec, this, networkService)
    }

/**
 * An extension to add a converted copy of the protobuf [PBLoop] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBLoop): Loop? = tryAddOrNull(toCim(pb, this))

/**
 * An extension to add a converted copy of the protobuf [PBLvFeeder] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBLvFeeder): LvFeeder? = tryAddOrNull(toCim(pb, this))

// ##################################################
// # Extensions IEC61970 Base Generation Production #
// ##################################################

/**
 * Convert the protobuf [PBEvChargingUnit] into its CIM counterpart.
 *
 * @param pb The protobuf [PBEvChargingUnit] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [EvChargingUnit].
 */
fun toCim(pb: PBEvChargingUnit, networkService: NetworkService): EvChargingUnit =
    EvChargingUnit(pb.mRID()).apply {
        toCim(pb.peu, this, networkService)
    }

/**
 * An extension to add a converted copy of the protobuf [PBEvChargingUnit] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBEvChargingUnit): EvChargingUnit? = tryAddOrNull(toCim(pb, this))

// #######################################
// # Extensions IEC61970 Base Protection #
// #######################################

/**
 * Convert the protobuf [PBDirectionalCurrentRelay] into its CIM counterpart.
 *
 * @param pb The protobuf [PBDirectionalCurrentRelay] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [DirectionalCurrentRelay].
 */
fun toCim(pb: PBDirectionalCurrentRelay, networkService: NetworkService): DirectionalCurrentRelay =
    DirectionalCurrentRelay(pb.mRID()).apply {
        directionalCharacteristicAngle = pb.directionalCharacteristicAngleSet.takeUnless { pb.hasDirectionalCharacteristicAngleNull() }
        polarizingQuantityType = mapPolarizingQuantityType.toCim(pb.polarizingQuantityType)
        relayElementPhase = mapPhaseCode.toCim(pb.relayElementPhase)
        minimumPickupCurrent = pb.minimumPickupCurrentSet.takeUnless { pb.hasMinimumPickupCurrentNull() }
        currentLimit1 = pb.currentLimit1Set.takeUnless { pb.hasCurrentLimit1Null() }
        inverseTimeFlag = pb.inverseTimeFlagSet.takeUnless { pb.hasInverseTimeFlagNull() }
        timeDelay1 = pb.timeDelay1Set.takeUnless { pb.hasTimeDelay1Null() }
    }.also {
        toCim(pb.prf, it, networkService)
    }

/**
 * Convert the protobuf [PBDistanceRelay] into its CIM counterpart.
 *
 * @param pb The protobuf [PBDistanceRelay] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [DistanceRelay].
 */
fun toCim(pb: PBDistanceRelay, networkService: NetworkService): DistanceRelay =
    DistanceRelay(pb.mRID()).apply {
        backwardBlind = pb.backwardBlindSet.takeUnless { pb.hasBackwardBlindNull() }
        backwardReach = pb.backwardReachSet.takeUnless { pb.hasBackwardReachNull() }
        backwardReactance = pb.backwardReactanceSet.takeUnless { pb.hasBackwardReactanceNull() }
        forwardBlind = pb.forwardBlindSet.takeUnless { pb.hasForwardBlindNull() }
        forwardReach = pb.forwardReachSet.takeUnless { pb.hasForwardReachNull() }
        forwardReactance = pb.forwardReactanceSet.takeUnless { pb.hasForwardReactanceNull() }
        operationPhaseAngle1 = pb.operationPhaseAngle1Set.takeUnless { pb.hasOperationPhaseAngle1Null() }
        operationPhaseAngle2 = pb.operationPhaseAngle2Set.takeUnless { pb.hasOperationPhaseAngle2Null() }
        operationPhaseAngle3 = pb.operationPhaseAngle3Set.takeUnless { pb.hasOperationPhaseAngle3Null() }
        toCim(pb.prf, this, networkService)
    }

/**
 * Convert the protobuf [PBProtectionRelayFunction] into its CIM counterpart.
 *
 * @param pb The protobuf [PBProtectionRelayFunction] to convert.
 * @param cim The CIM [ProtectionRelayFunction] to populate.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [ProtectionRelayFunction].
 */
fun toCim(pb: PBProtectionRelayFunction, cim: ProtectionRelayFunction, networkService: NetworkService): ProtectionRelayFunction =
    cim.apply {
        pb.protectedSwitchMRIDsList.forEach { protectedSwitchMRID ->
            networkService.resolveOrDeferReference(Resolvers.protectedSwitches(this), protectedSwitchMRID)
        }
        pb.sensorMRIDsList.forEach { sensorMRID ->
            networkService.resolveOrDeferReference(Resolvers.sensors(this), sensorMRID)
        }
        pb.schemeMRIDsList.forEach { schemeMRID ->
            networkService.resolveOrDeferReference(Resolvers.schemes(this), schemeMRID)
        }
        model = pb.modelSet.takeUnless { pb.hasModelNull() }
        reclosing = pb.reclosingSet.takeUnless { pb.hasReclosingNull() }
        relayDelayTime = pb.relayDelayTimeSet.takeUnless { pb.hasRelayDelayTimeNull() }
        protectionKind = mapProtectionKind.toCim(pb.protectionKind)
        directable = pb.directableSet.takeUnless { pb.hasDirectableNull() }
        powerDirection = mapPowerDirectionKind.toCim(pb.powerDirection)
        networkService.resolveOrDeferReference(Resolvers.assetInfo(this), pb.assetInfoMRID())

        pb.timeLimitsList.forEach { addTimeLimit(it) }
        pb.thresholdsList.forEach { addThreshold(toCim(it)) }

        toCim(pb.psr, this, networkService)
    }

/**
 * Convert the protobuf [PBProtectionRelayScheme] into its CIM counterpart.
 *
 * @param pb The protobuf [PBProtectionRelayScheme] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [ProtectionRelayScheme].
 */
fun toCim(pb: PBProtectionRelayScheme, networkService: NetworkService): ProtectionRelayScheme =
    ProtectionRelayScheme(pb.mRID()).apply {
        networkService.resolveOrDeferReference(Resolvers.system(this), pb.systemMRID)
        pb.functionMRIDsList.forEach { functionMRID ->
            networkService.resolveOrDeferReference(Resolvers.functions(this), functionMRID)
        }
        toCim(pb.io, this, networkService)
    }

/**
 * Convert the protobuf [PBProtectionRelaySystem] into its CIM counterpart.
 *
 * @param pb The protobuf [PBProtectionRelaySystem] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [ProtectionRelaySystem].
 */
fun toCim(pb: PBProtectionRelaySystem, networkService: NetworkService): ProtectionRelaySystem =
    ProtectionRelaySystem(pb.mRID()).apply {
        pb.schemeMRIDsList.forEach { schemeMRID ->
            networkService.resolveOrDeferReference(Resolvers.schemes(this), schemeMRID)
        }
        protectionKind = mapProtectionKind.toCim(pb.protectionKind)
        toCim(pb.eq, this, networkService)
    }

/**
 * Convert the protobuf [PBRelaySetting] into its CIM counterpart.
 *
 * @param pb The protobuf [PBRelaySetting] to convert.
 * @return The converted [pb] as a CIM [RelaySetting].
 */
fun toCim(pb: PBRelaySetting): RelaySetting =
    RelaySetting(mapUnitSymbol.toCim(pb.unitSymbol), pb.value, pb.nameSet.takeUnless { pb.hasNameNull() })

/**
 * Convert the protobuf [PBVoltageRelay] into its CIM counterpart.
 *
 * @param pb The protobuf [PBVoltageRelay] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [VoltageRelay].
 */
fun toCim(pb: PBVoltageRelay, networkService: NetworkService): VoltageRelay =
    VoltageRelay(pb.mRID()).apply {
        toCim(pb.prf, this, networkService)
    }

/**
 * An extension to add a converted copy of the protobuf [PBDirectionalCurrentRelay] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBDirectionalCurrentRelay): DirectionalCurrentRelay? = tryAddOrNull(toCim(pb, this))

/**
 * An extension to add a converted copy of the protobuf [PBDistanceRelay] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBDistanceRelay): DistanceRelay? = tryAddOrNull(toCim(pb, this))

/**
 * An extension to add a converted copy of the protobuf [PBProtectionRelayScheme] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBProtectionRelayScheme): ProtectionRelayScheme? = tryAddOrNull(toCim(pb, this))

/**
 * An extension to add a converted copy of the protobuf [PBProtectionRelaySystem] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBProtectionRelaySystem): ProtectionRelaySystem? = tryAddOrNull(toCim(pb, this))

/**
 * An extension to add a converted copy of the protobuf [PBVoltageRelay] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBVoltageRelay): VoltageRelay? = tryAddOrNull(toCim(pb, this))

// ##################################
// # Extensions IEC61970 Base Wires #
// ##################################

/**
 * Convert the protobuf [PBBatteryControl] into its CIM counterpart.
 *
 * @param pb The protobuf [PBBatteryControl] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [BatteryControl].
 */
fun toCim(pb: PBBatteryControl, networkService: NetworkService): BatteryControl =
    BatteryControl(pb.mRID()).apply {
        chargingRate = pb.chargingRateSet.takeUnless { pb.hasChargingRateNull() }
        dischargingRate = pb.dischargingRateSet.takeUnless { pb.hasDischargingRateNull() }
        reservePercent = pb.reservePercentSet.takeUnless { pb.hasReservePercentNull() }
        pb.controlMode?.let { controlMode = mapBatteryControlMode.toCim(it) }
        toCim(pb.rc, this, networkService)
    }

/**
 * An extension to add a converted copy of the protobuf [PBBatteryControl] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBBatteryControl): BatteryControl? = tryAddOrNull(toCim(pb, this))

// #######################
// # IEC61968 Asset Info #
// #######################

/**
 * Convert the protobuf [PBCableInfo] into its CIM counterpart.
 *
 * @param pb The protobuf [PBCableInfo] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [CableInfo].
 */
fun toCim(pb: PBCableInfo, networkService: NetworkService): CableInfo =
    CableInfo(pb.mRID()).apply {
        toCim(pb.wi, this, networkService)
    }

/**
 * Convert the protobuf [PBOverheadWireInfo] into its CIM counterpart.
 *
 * @param pb The protobuf [PBOverheadWireInfo] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [OverheadWireInfo].
 */
fun toCim(pb: PBOverheadWireInfo, networkService: NetworkService): OverheadWireInfo =
    OverheadWireInfo(pb.mRID()).apply {
        toCim(pb.wi, this, networkService)
    }

/**
 * Convert the protobuf [PBNoLoadTest] into its CIM counterpart.
 *
 * @param pb The protobuf [PBNoLoadTest] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [NoLoadTest].
 */
fun toCim(pb: PBNoLoadTest, networkService: NetworkService): NoLoadTest =
    NoLoadTest(pb.mRID()).apply {
        energisedEndVoltage = pb.energisedEndVoltageSet.takeUnless { pb.hasEnergisedEndVoltageNull() }
        excitingCurrent = pb.excitingCurrentSet.takeUnless { pb.hasExcitingCurrentNull() }
        excitingCurrentZero = pb.excitingCurrentZeroSet.takeUnless { pb.hasExcitingCurrentZeroNull() }
        loss = pb.lossSet.takeUnless { pb.hasLossNull() }
        lossZero = pb.lossZeroSet.takeUnless { pb.hasLossZeroNull() }
        toCim(pb.tt, this, networkService)
    }

/**
 * Convert the protobuf [PBOpenCircuitTest] into its CIM counterpart.
 *
 * @param pb The protobuf [PBOpenCircuitTest] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [OpenCircuitTest].
 */
fun toCim(pb: PBOpenCircuitTest, networkService: NetworkService): OpenCircuitTest =
    OpenCircuitTest(pb.mRID()).apply {
        energisedEndStep = pb.energisedEndStepSet.takeUnless { pb.hasEnergisedEndStepNull() }
        energisedEndVoltage = pb.energisedEndVoltageSet.takeUnless { pb.hasEnergisedEndVoltageNull() }
        openEndStep = pb.openEndStepSet.takeUnless { pb.hasOpenEndStepNull() }
        openEndVoltage = pb.openEndVoltageSet.takeUnless { pb.hasOpenEndVoltageNull() }
        phaseShift = pb.phaseShiftSet.takeUnless { pb.hasPhaseShiftNull() }
        toCim(pb.tt, this, networkService)
    }

/**
 * Convert the protobuf [PBPowerTransformerInfo] into its CIM counterpart.
 *
 * @param pb The protobuf [PBPowerTransformerInfo] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [PowerTransformerInfo].
 */
fun toCim(pb: PBPowerTransformerInfo, networkService: NetworkService): PowerTransformerInfo =
    PowerTransformerInfo(pb.mRID()).apply {
        pb.transformerTankInfoMRIDsList.forEach {
            networkService.resolveOrDeferReference(Resolvers.transformerTankInfo(this), it)
        }
        toCim(pb.ai, this, networkService)
    }

/**
 * Convert the protobuf [PBShortCircuitTest] into its CIM counterpart.
 *
 * @param pb The protobuf [PBShortCircuitTest] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [ShortCircuitTest].
 */
fun toCim(pb: PBShortCircuitTest, networkService: NetworkService): ShortCircuitTest =
    ShortCircuitTest(pb.mRID()).apply {
        current = pb.currentSet.takeUnless { pb.hasCurrentNull() }
        energisedEndStep = pb.energisedEndStepSet.takeUnless { pb.hasEnergisedEndStepNull() }
        groundedEndStep = pb.groundedEndStepSet.takeUnless { pb.hasGroundedEndStepNull() }
        leakageImpedance = pb.leakageImpedanceSet.takeUnless { pb.hasLeakageImpedanceNull() }
        leakageImpedanceZero = pb.leakageImpedanceZeroSet.takeUnless { pb.hasLeakageImpedanceZeroNull() }
        loss = pb.lossSet.takeUnless { pb.hasLossNull() }
        lossZero = pb.lossZeroSet.takeUnless { pb.hasLossZeroNull() }
        power = pb.powerSet.takeUnless { pb.hasPowerNull() }
        voltage = pb.voltageSet.takeUnless { pb.hasVoltageNull() }
        voltageOhmicPart = pb.voltageOhmicPartSet.takeUnless { pb.hasVoltageOhmicPartNull() }
        toCim(pb.tt, this, networkService)
    }

/**
 * Convert the protobuf [PBShuntCompensatorInfo] into its CIM counterpart.
 *
 * @param pb The protobuf [PBShuntCompensatorInfo] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [ShuntCompensatorInfo].
 */
fun toCim(pb: PBShuntCompensatorInfo, networkService: NetworkService): ShuntCompensatorInfo =
    ShuntCompensatorInfo(pb.mRID()).apply {
        maxPowerLoss = pb.maxPowerLossSet.takeUnless { pb.hasMaxPowerLossNull() }
        ratedCurrent = pb.ratedCurrentSet.takeUnless { pb.hasRatedCurrentNull() }
        ratedReactivePower = pb.ratedReactivePowerSet.takeUnless { pb.hasRatedReactivePowerNull() }
        ratedVoltage = pb.ratedVoltageSet.takeUnless { pb.hasRatedVoltageNull() }

        toCim(pb.ai, this, networkService)
    }

/**
 * Convert the protobuf [PBSwitchInfo] into its CIM counterpart.
 *
 * @param pb The protobuf [PBSwitchInfo] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [SwitchInfo].
 */
fun toCim(pb: PBSwitchInfo, networkService: NetworkService): SwitchInfo =
    SwitchInfo(pb.mRID()).apply {
        ratedInterruptingTime = pb.ratedInterruptingTimeSet.takeUnless { pb.hasRatedInterruptingTimeNull() }
        toCim(pb.ai, this, networkService)
    }

/**
 * Convert the protobuf [PBTransformerEndInfo] into its CIM counterpart.
 *
 * @param pb The protobuf [PBTransformerEndInfo] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [TransformerEndInfo].
 */
fun toCim(pb: PBTransformerEndInfo, networkService: NetworkService): TransformerEndInfo =
    TransformerEndInfo(pb.mRID()).apply {
        connectionKind = mapWindingConnection.toCim(pb.connectionKind)
        emergencyS = pb.emergencySSet.takeUnless { pb.hasEmergencySNull() }
        endNumber = pb.endNumber
        insulationU = pb.insulationUSet.takeUnless { pb.hasInsulationUNull() }
        phaseAngleClock = pb.phaseAngleClockSet.takeUnless { pb.hasPhaseAngleClockNull() }
        r = pb.rSet.takeUnless { pb.hasRNull() }
        ratedS = pb.ratedSSet.takeUnless { pb.hasRatedSNull() }
        ratedU = pb.ratedUSet.takeUnless { pb.hasRatedUNull() }
        shortTermS = pb.shortTermSSet.takeUnless { pb.hasShortTermSNull() }

        networkService.resolveOrDeferReference(Resolvers.transformerTankInfo(this), pb.transformerTankInfoMRID)
        networkService.resolveOrDeferReference(Resolvers.transformerStarImpedance(this), pb.transformerStarImpedanceMRID)
        networkService.resolveOrDeferReference(Resolvers.energisedEndNoLoadTests(this), pb.energisedEndNoLoadTestsMRID)
        networkService.resolveOrDeferReference(Resolvers.energisedEndShortCircuitTests(this), pb.energisedEndShortCircuitTestsMRID)
        networkService.resolveOrDeferReference(Resolvers.groundedEndShortCircuitTests(this), pb.groundedEndShortCircuitTestsMRID)
        networkService.resolveOrDeferReference(Resolvers.openEndOpenCircuitTests(this), pb.openEndOpenCircuitTestsMRID)
        networkService.resolveOrDeferReference(Resolvers.energisedEndOpenCircuitTests(this), pb.energisedEndOpenCircuitTestsMRID)

        toCim(pb.ai, this, networkService)
    }

/**
 * Convert the protobuf [PBTransformerTankInfo] into its CIM counterpart.
 *
 * @param pb The protobuf [PBTransformerTankInfo] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [TransformerTankInfo].
 */
fun toCim(pb: PBTransformerTankInfo, networkService: NetworkService): TransformerTankInfo =
    TransformerTankInfo(pb.mRID()).apply {
        networkService.resolveOrDeferReference(Resolvers.powerTransformerInfo(this), pb.powerTransformerInfoMRID)
        pb.transformerEndInfoMRIDsList.forEach {
            networkService.resolveOrDeferReference(Resolvers.transformerEndInfo(this), it)
        }
        toCim(pb.ai, this, networkService)
    }

/**
 * Convert the protobuf [PBTransformerTest] into its CIM counterpart.
 *
 * @param pb The protobuf [PBTransformerTest] to convert.
 * @param cim The CIM [TransformerTest] to populate.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [TransformerTest].
 */
fun toCim(pb: PBTransformerTest, cim: TransformerTest, networkService: NetworkService): TransformerTest =
    cim.apply {
        basePower = pb.basePowerSet.takeUnless { pb.hasBasePowerNull() }
        temperature = pb.temperatureSet.takeUnless { pb.hasTemperatureNull() }
        toCim(pb.io, this, networkService)
    }

/**
 * Convert the protobuf [PBWireInfo] into its CIM counterpart.
 *
 * @param pb The protobuf [PBWireInfo] to convert.
 * @param cim The CIM [WireInfo] to populate.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [WireInfo].
 */
fun toCim(pb: PBWireInfo, cim: WireInfo, networkService: NetworkService): WireInfo =
    cim.apply {
        ratedCurrent = pb.ratedCurrentSet.takeUnless { pb.hasRatedCurrentNull() }
        material = mapWireMaterialKind.toCim(pb.material)
        toCim(pb.ai, this, networkService)
    }

/**
 * An extension to add a converted copy of the protobuf [PBCableInfo] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBCableInfo): CableInfo? = tryAddOrNull(toCim(pb, this))

/**
 * An extension to add a converted copy of the protobuf [PBNoLoadTest] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBNoLoadTest): NoLoadTest? = tryAddOrNull(toCim(pb, this))

/**
 * An extension to add a converted copy of the protobuf [PBOpenCircuitTest] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBOpenCircuitTest): OpenCircuitTest? = tryAddOrNull(toCim(pb, this))

/**
 * An extension to add a converted copy of the protobuf [PBOverheadWireInfo] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBOverheadWireInfo): OverheadWireInfo? = tryAddOrNull(toCim(pb, this))

/**
 * An extension to add a converted copy of the protobuf [PBPowerTransformerInfo] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBPowerTransformerInfo): PowerTransformerInfo? = tryAddOrNull(toCim(pb, this))

/**
 * An extension to add a converted copy of the protobuf [PBShortCircuitTest] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBShortCircuitTest): ShortCircuitTest? = tryAddOrNull(toCim(pb, this))

/**
 * An extension to add a converted copy of the protobuf [PBShuntCompensatorInfo] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBShuntCompensatorInfo): ShuntCompensatorInfo? = tryAddOrNull(toCim(pb, this))

/**
 * An extension to add a converted copy of the protobuf [PBSwitchInfo] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBSwitchInfo): SwitchInfo? = tryAddOrNull(toCim(pb, this))

/**
 * An extension to add a converted copy of the protobuf [PBTransformerEndInfo] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBTransformerEndInfo): TransformerEndInfo? = tryAddOrNull(toCim(pb, this))

/**
 * An extension to add a converted copy of the protobuf [PBTransformerTankInfo] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBTransformerTankInfo): TransformerTankInfo? = tryAddOrNull(toCim(pb, this))

// ###################
// # IEC61968 Assets #
// ###################

/**
 * Convert the protobuf [PBAsset] into its CIM counterpart.
 *
 * @param pb The protobuf [PBAsset] to convert.
 * @param cim The CIM [Asset] to populate.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [Asset].
 */
fun toCim(pb: PBAsset, cim: Asset, networkService: NetworkService): Asset =
    cim.apply {
        networkService.resolveOrDeferReference(Resolvers.location(this), pb.locationMRID)
        pb.organisationRoleMRIDsList.forEach {
            networkService.resolveOrDeferReference(Resolvers.organisationRoles(this), it)
        }
        pb.powerSystemResourceMRIDsList.forEach {
            networkService.resolveOrDeferReference(Resolvers.powerSystemResources(this), it)
        }

        toCim(pb.io, this, networkService)
    }

/**
 * Convert the protobuf [PBAssetContainer] into its CIM counterpart.
 *
 * @param pb The protobuf [PBAssetContainer] to convert.
 * @param cim The CIM [AssetContainer] to populate.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [AssetContainer].
 */
fun toCim(pb: PBAssetContainer, cim: AssetContainer, networkService: NetworkService): AssetContainer =
    cim.apply { toCim(pb.at, this, networkService) }

/**
 * Convert the protobuf [PBAssetFunction] into its CIM counterpart.
 *
 * @param pb The protobuf [PBAssetFunction] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [AssetFunction].
 */
fun toCim(pb: PBAssetFunction, cim: AssetFunction, networkService: NetworkService): AssetFunction =
    cim.apply {
        toCim(pb.io, this, networkService)
    }

/**
 * Convert the protobuf [PBAssetInfo] into its CIM counterpart.
 *
 * @param pb The protobuf [PBAssetInfo] to convert.
 * @param cim The CIM [AssetInfo] to populate.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [AssetInfo].
 */
fun toCim(pb: PBAssetInfo, cim: AssetInfo, networkService: NetworkService): AssetInfo =
    cim.apply { toCim(pb.io, this, networkService) }

/**
 * Convert the protobuf [PBAssetOrganisationRole] into its CIM counterpart.
 *
 * @param pb The protobuf [PBAssetOrganisationRole] to convert.
 * @param cim The CIM [AssetOrganisationRole] to populate.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [AssetOrganisationRole].
 */
fun toCim(pb: PBAssetOrganisationRole, cim: AssetOrganisationRole, networkService: NetworkService): AssetOrganisationRole =
    cim.apply { toCim(pb.or, this, networkService) }

/**
 * Convert the protobuf [PBAssetOwner] into its CIM counterpart.
 *
 * @param pb The protobuf [PBAssetOwner] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [AssetOwner].
 */
fun toCim(pb: PBAssetOwner, networkService: NetworkService): AssetOwner =
    AssetOwner(pb.mRID()).apply {
        toCim(pb.aor, this, networkService)
    }

/**
 * Convert the protobuf [PBStreetlight] into its CIM counterpart.
 *
 * @param pb The protobuf [PBStreetlight] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [Streetlight].
 */
fun toCim(pb: PBStreetlight, networkService: NetworkService): Streetlight =
    Streetlight(pb.mRID()).apply {
        lampKind = mapStreetlightLampKind.toCim(pb.lampKind)
        lightRating = pb.lightRatingSet.takeUnless { pb.hasLightRatingNull() }
        networkService.resolveOrDeferReference(Resolvers.pole(this), pb.poleMRID)
        toCim(pb.at, this, networkService)
    }

/**
 * Convert the protobuf [PBStructure] into its CIM counterpart.
 *
 * @param pb The protobuf [PBStructure] to convert.
 * @param cim The CIM [Structure] to populate.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [Structure].
 */
fun toCim(pb: PBStructure, cim: Structure, networkService: NetworkService): Structure =
    cim.apply { toCim(pb.ac, this, networkService) }

/**
 * An extension to add a converted copy of the protobuf [PBAssetOwner] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBAssetOwner): AssetOwner? = tryAddOrNull(toCim(pb, this))

/**
 * An extension to add a converted copy of the protobuf [PBStreetlight] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBStreetlight): Streetlight? = tryAddOrNull(toCim(pb, this))

// ###################
// # IEC61968 Common #
// ###################

private fun toCim(pb: PBElectronicAddress): ElectronicAddress =
    ElectronicAddress(
        pb.email1Set.takeUnless { pb.hasEmail1Null() },
        pb.isPrimarySet.takeUnless { pb.hasIsPrimaryNull() },
        pb.descriptionSet.takeUnless { pb.hasDescriptionNull() },
    )

/**
 * Convert the protobuf [PBLocation] into its CIM counterpart.
 *
 * @param pb The protobuf [PBLocation] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [Location].
 */
fun toCim(pb: PBLocation, networkService: NetworkService): Location =
    Location(pb.mRID()).apply {
        mainAddress = if (pb.hasMainAddress()) toCim(pb.mainAddress) else null
        pb.positionPointsList.forEach { addPoint(toCim(it)) }
        toCim(pb.io, this, networkService)
    }

/**
 * Convert the protobuf [PBPositionPoint] into its CIM counterpart.
 *
 * @param pb The protobuf [PBPositionPoint] to convert.
 * @return The converted [pb] as a CIM [PositionPoint].
 */
fun toCim(pb: PBPositionPoint): PositionPoint =
    PositionPoint(pb.xPosition, pb.yPosition)

/**
 * Convert the protobuf [PBStreetAddress] into its CIM counterpart.
 *
 * @param pb The protobuf [PBStreetAddress] to convert.
 * @return The converted [pb] as a CIM [StreetAddress].
 */
fun toCim(pb: PBStreetAddress): StreetAddress =
    StreetAddress(
        pb.postalCodeSet.takeUnless { pb.hasPostalCodeNull() },
        if (pb.hasTownDetail()) toCim(pb.townDetail) else null,
        pb.poBoxSet.takeUnless { pb.hasPoBoxNull() },
        if (pb.hasStreetDetail()) toCim(pb.streetDetail) else null
    )

/**
 * Convert the protobuf [PBStreetDetail] into its CIM counterpart.
 *
 * @param pb The protobuf [PBStreetDetail] to convert.
 * @return The converted [pb] as a CIM [StreetDetail].
 */
fun toCim(pb: PBStreetDetail): StreetDetail =
    StreetDetail(
        pb.buildingNameSet.takeUnless { pb.hasBuildingNameNull() },
        pb.floorIdentificationSet.takeUnless { pb.hasFloorIdentificationNull() },
        pb.nameSet.takeUnless { pb.hasNameNull() },
        pb.numberSet.takeUnless { pb.hasNumberNull() },
        pb.suiteNumberSet.takeUnless { pb.hasSuiteNumberNull() },
        pb.typeSet.takeUnless { pb.hasTypeNull() },
        pb.displayAddressSet.takeUnless { pb.hasDisplayAddressNull() },
        pb.buildingNumberSet.takeUnless { pb.hasBuildingNumberNull() },
    )

private fun toCim(pb: PBTelephoneNumber): TelephoneNumber =
    TelephoneNumber(
        pb.areaCodeSet.takeUnless { pb.hasAreaCodeNull() },
        pb.cityCodeSet.takeUnless { pb.hasCityCodeNull() },
        pb.countryCodeSet.takeUnless { pb.hasCountryCodeNull() },
        pb.dialOutSet.takeUnless { pb.hasDialOutNull() },
        pb.extensionSet.takeUnless { pb.hasExtensionNull() },
        pb.internationalPrefixSet.takeUnless { pb.hasInternationalPrefixNull() },
        pb.localNumberSet.takeUnless { pb.hasLocalNumberNull() },
        pb.isPrimarySet.takeUnless { pb.hasIsPrimaryNull() },
        pb.descriptionSet.takeUnless { pb.hasDescriptionNull() },
    )

/**
 * Convert the protobuf [PBPositionPoint] into its CIM counterpart.
 *
 * @param pb The protobuf [PBPositionPoint] to convert.
 * @return The converted [pb] as a CIM [PositionPoint].
 */
fun toCim(pb: PBTownDetail): TownDetail =
    TownDetail(
        pb.nameSet.takeUnless { pb.hasNameNull() },
        pb.stateOrProvinceSet.takeUnless { pb.hasStateOrProvinceNull() },
        pb.countrySet.takeUnless { pb.hasCountryNull() },
    )

/**
 * An extension to add a converted copy of the protobuf [PBOrganisation] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBOrganisation): Organisation? = tryAddOrNull(toCim(pb, this))

/**
 * An extension to add a converted copy of the protobuf [PBLocation] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBLocation): Location? = tryAddOrNull(toCim(pb, this))

// #####################################
// # IEC61968 infIEC61968 InfAssetInfo #
// #####################################

/**
 * Convert the protobuf [PBCurrentTransformerInfo] into its CIM counterpart.
 *
 * @param pb The protobuf [PBCurrentTransformerInfo] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [CurrentTransformerInfo].
 */
fun toCim(pb: PBCurrentTransformerInfo, networkService: NetworkService): CurrentTransformerInfo =
    CurrentTransformerInfo(pb.mRID()).apply {
        accuracyClass = pb.accuracyClassSet.takeUnless { pb.hasAccuracyClassNull() }
        accuracyLimit = pb.accuracyLimitSet.takeUnless { pb.hasAccuracyLimitNull() }
        coreCount = pb.coreCountSet.takeUnless { pb.hasCoreCountNull() }
        ctClass = pb.ctClassSet.takeUnless { pb.hasCtClassNull() }
        kneePointVoltage = pb.kneePointVoltageSet.takeUnless { pb.hasKneePointVoltageNull() }
        maxRatio = if (pb.hasMaxRatio()) toCim(pb.maxRatio) else null
        nominalRatio = if (pb.hasNominalRatio()) toCim(pb.nominalRatio) else null
        primaryRatio = pb.primaryRatioSet.takeUnless { pb.hasPrimaryRatioNull() }
        ratedCurrent = pb.ratedCurrentSet.takeUnless { pb.hasRatedCurrentNull() }
        secondaryFlsRating = pb.secondaryFlsRatingSet.takeUnless { pb.hasSecondaryFlsRatingNull() }
        secondaryRatio = pb.secondaryRatioSet.takeUnless { pb.hasSecondaryRatioNull() }
        usage = pb.usageSet.takeUnless { pb.hasUsageNull() }
        toCim(pb.ai, this, networkService)
    }

/**
 * Convert the protobuf [PBPotentialTransformerInfo] into its CIM counterpart.
 *
 * @param pb The protobuf [PBPotentialTransformerInfo] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [PotentialTransformerInfo].
 */
fun toCim(pb: PBPotentialTransformerInfo, networkService: NetworkService): PotentialTransformerInfo =
    PotentialTransformerInfo(pb.mRID()).apply {
        accuracyClass = pb.accuracyClassSet.takeUnless { pb.hasAccuracyClassNull() }
        nominalRatio = if (pb.hasNominalRatio()) toCim(pb.nominalRatio) else null
        primaryRatio = pb.primaryRatioSet.takeUnless { pb.hasPrimaryRatioNull() }
        ptClass = pb.ptClassSet.takeUnless { pb.hasPtClassNull() }
        ratedVoltage = pb.ratedVoltageSet.takeUnless { pb.hasRatedVoltageNull() }
        secondaryRatio = pb.secondaryRatioSet.takeUnless { pb.hasSecondaryRatioNull() }
        toCim(pb.ai, this, networkService)
    }

/**
 * An extension to add a converted copy of the protobuf [PBCurrentTransformerInfo] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBCurrentTransformerInfo): CurrentTransformerInfo? = tryAddOrNull(toCim(pb, this))

/**
 * An extension to add a converted copy of the protobuf [PBPotentialTransformerInfo] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBPotentialTransformerInfo): PotentialTransformerInfo? = tryAddOrNull(toCim(pb, this))

// ##################################
// # IEC61968 infIEC61968 InfAssets #
// ##################################

/**
 * Convert the protobuf [PBPole] into its CIM counterpart.
 *
 * @param pb The protobuf [PBPole] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [Pole].
 */
fun toCim(pb: PBPole, networkService: NetworkService): Pole =
    Pole(pb.mRID()).apply {
        classification = pb.classificationSet.takeUnless { pb.hasClassificationNull() }
        pb.streetlightMRIDsList.forEach {
            networkService.resolveOrDeferReference(Resolvers.streetlights(this), it)
        }
        toCim(pb.st, this, networkService)
    }

/**
 * An extension to add a converted copy of the protobuf [PBPole] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBPole): Pole? = tryAddOrNull(toCim(pb, this))

// ##################################
// # IEC61968 infIEC61968 InfCommon #
// ##################################

/**
 * Convert the protobuf [PBRatio] into its CIM counterpart.
 *
 * @param pb The protobuf [PBRatio] to convert.
 * @return The converted [pb] as a CIM [Ratio].
 */
fun toCim(pb: PBRatio): Ratio =
    Ratio(pb.numerator, pb.denominator)

// #####################
// # IEC61968 Metering #
// #####################

/**
 * Convert the protobuf [PBEndDevice] into its CIM counterpart.
 *
 * @param pb The protobuf [PBEndDevice] to convert.
 * @param cim The CIM [EndDevice] to populate.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [EndDevice].
 */
fun toCim(pb: PBEndDevice, cim: EndDevice, networkService: NetworkService): EndDevice =
    cim.apply {
        pb.usagePointMRIDsList.forEach { usagePointMRID ->
            networkService.resolveOrDeferReference(Resolvers.usagePoints(this), usagePointMRID)
        }
        pb.endDeviceFunctionMRIDsList.forEach { endDeviceFunctionMRID ->
            networkService.resolveOrDeferReference(Resolvers.endDeviceFunctions(this), endDeviceFunctionMRID)
        }
        customerMRID = pb.customerMRID.takeIf { !it.isNullOrBlank() }
        networkService.resolveOrDeferReference(Resolvers.serviceLocation(this), pb.serviceLocationMRID)
        toCim(pb.ac, this, networkService)
    }

/**
 * Convert the protobuf [PBEndDeviceFunction] into its CIM counterpart.
 *
 * @param pb The protobuf [PBEndDeviceFunction] to convert.
 * @param cim The CIM [EndDeviceFunction] to populate.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [EndDeviceFunction].
 */
fun toCim(pb: PBEndDeviceFunction, cim: EndDeviceFunction, networkService: NetworkService): EndDeviceFunction =
    cim.apply {
        enabled = pb.enabledSet.takeUnless { pb.hasEnabledNull() }
        toCim(pb.af, this, networkService)
    }

/**
 * Convert the protobuf [PBMeter] into its CIM counterpart.
 *
 * @param pb The protobuf [PBMeter] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [Meter].
 */
fun toCim(pb: PBMeter, networkService: NetworkService): Meter =
    Meter(pb.mRID()).apply {
        toCim(pb.ed, this, networkService)
    }

/**
 * Convert the protobuf [PBUsagePoint] into its CIM counterpart.
 *
 * @param pb The protobuf [PBUsagePoint] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [UsagePoint].
 */
fun toCim(pb: PBUsagePoint, networkService: NetworkService): UsagePoint =
    UsagePoint(pb.mRID()).apply {
        networkService.resolveOrDeferReference(Resolvers.usagePointLocation(this), pb.usagePointLocationMRID)
        isVirtual = pb.isVirtualSet.takeUnless { pb.hasIsVirtualNull() }
        connectionCategory = pb.connectionCategorySet.takeUnless { pb.hasConnectionCategoryNull() }
        ratedPower = pb.ratedPowerSet.takeUnless { pb.hasRatedPowerNull() }
        approvedInverterCapacity = pb.approvedInverterCapacitySet.takeUnless { pb.hasApprovedInverterCapacityNull() }
        phaseCode = mapPhaseCode.toCim(pb.phaseCode)

        pb.equipmentMRIDsList.forEach { equipmentMRID ->
            networkService.resolveOrDeferReference(Resolvers.equipment(this), equipmentMRID)
        }

        pb.endDeviceMRIDsList.forEach {
            networkService.resolveOrDeferReference(Resolvers.endDevices(this), it)
        }

        pb.contactsList.forEach { addContact(toCim(it)) }
    }.also {
        toCim(pb.io, it, networkService)
    }

/**
 * An extension to add a converted copy of the protobuf [PBMeter] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBMeter): Meter? = tryAddOrNull(toCim(pb, this))

/**
 * An extension to add a converted copy of the protobuf [PBUsagePoint] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBUsagePoint): UsagePoint? = tryAddOrNull(toCim(pb, this))

// #######################
// # IEC61968 Operations #
// #######################

/**
 * Convert the protobuf [PBOperationalRestriction] into its CIM counterpart.
 *
 * @param pb The protobuf [PBOperationalRestriction] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [OperationalRestriction].
 */
fun toCim(pb: PBOperationalRestriction, networkService: NetworkService): OperationalRestriction =
    OperationalRestriction(pb.mRID()).apply {
        toCim(pb.doc, this, networkService)
    }

/**
 * An extension to add a converted copy of the protobuf [PBOperationalRestriction] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBOperationalRestriction): OperationalRestriction? = tryAddOrNull(toCim(pb, this))

// #####################################
// # IEC61970 Base Auxiliary Equipment #
// #####################################

/**
 * Convert the protobuf [PBAuxiliaryEquipment] into its CIM counterpart.
 *
 * @param pb The protobuf [PBAuxiliaryEquipment] to convert.
 * @param cim The CIM [AuxiliaryEquipment] to populate.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [AuxiliaryEquipment].
 */
fun toCim(pb: PBAuxiliaryEquipment, cim: AuxiliaryEquipment, networkService: NetworkService): AuxiliaryEquipment =
    cim.apply {
        networkService.resolveOrDeferReference(Resolvers.terminal(this), pb.terminalMRID)
        toCim(pb.eq, this, networkService)
    }

/**
 * Convert the protobuf [PBCurrentTransformer] into its CIM counterpart.
 *
 * @param pb The protobuf [PBCurrentTransformer] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [CurrentTransformer].
 */
fun toCim(pb: PBCurrentTransformer, networkService: NetworkService): CurrentTransformer =
    CurrentTransformer(pb.mRID()).apply {
        networkService.resolveOrDeferReference(Resolvers.assetInfo(this), pb.assetInfoMRID())
        coreBurden = pb.coreBurdenSet.takeUnless { pb.hasCoreBurdenNull() }
        toCim(pb.sn, this, networkService)
    }

/**
 * Convert the protobuf [PBFaultIndicator] into its CIM counterpart.
 *
 * @param pb The protobuf [PBFaultIndicator] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [FaultIndicator].
 */
fun toCim(pb: PBFaultIndicator, networkService: NetworkService): FaultIndicator =
    FaultIndicator(pb.mRID()).apply {
        toCim(pb.ae, this, networkService)
    }

/**
 * Convert the protobuf [PBPotentialTransformer] into its CIM counterpart.
 *
 * @param pb The protobuf [PBPotentialTransformer] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [PotentialTransformer].
 */
fun toCim(pb: PBPotentialTransformer, networkService: NetworkService): PotentialTransformer =
    PotentialTransformer(pb.mRID()).apply {
        networkService.resolveOrDeferReference(Resolvers.assetInfo(this), pb.assetInfoMRID())
        type = mapPotentialTransformerKind.toCim(pb.type)
        toCim(pb.sn, this, networkService)
    }

/**
 * Convert the protobuf [PBSensor] into its CIM counterpart.
 *
 * @param pb The protobuf [PBSensor] to convert.
 * @param cim The CIM [Sensor] to populate.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [Sensor].
 */
fun toCim(pb: PBSensor, cim: Sensor, networkService: NetworkService): Sensor =
    cim.apply {
        pb.relayFunctionMRIDsList.forEach { relayFunctionMRID ->
            networkService.resolveOrDeferReference(Resolvers.relayFunctions(this), relayFunctionMRID)
        }
        toCim(pb.ae, this, networkService)
    }

/**
 * An extension to add a converted copy of the protobuf [PBCurrentTransformer] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBCurrentTransformer): CurrentTransformer? = tryAddOrNull(toCim(pb, this))

/**
 * An extension to add a converted copy of the protobuf [PBFaultIndicator] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBFaultIndicator): FaultIndicator? = tryAddOrNull(toCim(pb, this))

/**
 * An extension to add a converted copy of the protobuf [PBPotentialTransformer] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBPotentialTransformer): PotentialTransformer? = tryAddOrNull(toCim(pb, this))

// ######################
// # IEC61970 Base Core #
// ######################

/**
 * Convert the protobuf [PBAcDcTerminal] into its CIM counterpart.
 *
 * @param pb The protobuf [PBAcDcTerminal] to convert.
 * @param cim The CIM [AcDcTerminal] to populate.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [AcDcTerminal].
 */
fun toCim(pb: PBAcDcTerminal, cim: AcDcTerminal, networkService: NetworkService): AcDcTerminal =
    cim.apply { toCim(pb.io, this, networkService) }

/**
 * Convert the protobuf [PBBaseVoltage] into its CIM counterpart.
 *
 * @param pb The protobuf [PBBaseVoltage] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [BaseVoltage].
 */
fun toCim(pb: PBBaseVoltage, networkService: NetworkService): BaseVoltage =
    BaseVoltage(pb.mRID()).apply {
        nominalVoltage = pb.nominalVoltage
        toCim(pb.io, this, networkService)
    }

/**
 * Convert the protobuf [PBConductingEquipment] into its CIM counterpart.
 *
 * @param pb The protobuf [PBConductingEquipment] to convert.
 * @param cim The CIM [ConductingEquipment] to populate.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [ConductingEquipment].
 */
fun toCim(pb: PBConductingEquipment, cim: ConductingEquipment, networkService: NetworkService): ConductingEquipment =
    cim.apply {
        networkService.resolveOrDeferReference(Resolvers.baseVoltage(this), pb.baseVoltageMRID)
        pb.terminalMRIDsList.forEach { terminalMRID ->
            networkService.resolveOrDeferReference(Resolvers.terminals(this), terminalMRID)
        }
        toCim(pb.eq, this, networkService)
    }

/**
 * Convert the protobuf [PBConnectivityNode] into its CIM counterpart.
 *
 * @param pb The protobuf [PBConnectivityNode] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [ConnectivityNode].
 */
fun toCim(pb: PBConnectivityNode, networkService: NetworkService): ConnectivityNode =
    ConnectivityNode(pb.mRID()).apply {
        toCim(pb.io, this, networkService)
    }

/**
 * Convert the protobuf [PBConnectivityNodeContainer] into its CIM counterpart.
 *
 * @param pb The protobuf [PBConnectivityNodeContainer] to convert.
 * @param cim The CIM [ConnectivityNodeContainer] to populate.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [ConnectivityNodeContainer].
 */
fun toCim(pb: PBConnectivityNodeContainer, cim: ConnectivityNodeContainer, networkService: NetworkService): ConnectivityNodeContainer =
    cim.apply { toCim(pb.psr, this, networkService) }

/**
 * Convert the protobuf [PBCurve] into its CIM counterpart.
 *
 * @param pb The protobuf [PBCurve] to convert.
 * @param cim The CIM [Curve] to populate.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [Curve].
 */
fun toCim(pb: PBCurve, cim: Curve, networkService: NetworkService): Curve =
    cim.apply {
        pb.curveDataList.forEach { addData(toCim(it)) }
        toCim(pb.io, this, networkService)
    }

/**
 * Convert the protobuf [PBCurveData] into its CIM counterpart.
 *
 * @param pb The protobuf [PBCurveData] to convert.
 * @return The converted [pb] as a CIM [CurveData].
 */
fun toCim(pb: PBCurveData): CurveData =
    CurveData(
        pb.xValue,
        pb.y1Value,
        pb.y2ValueSet.takeUnless { pb.hasY2ValueNull() },
        pb.y3ValueSet.takeUnless { pb.hasY3ValueNull() }
    )

/**
 * Convert the protobuf [PBEquipment] into its CIM counterpart.
 *
 * @param pb The protobuf [PBEquipment] to convert.
 * @param cim The CIM [Equipment] to populate.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [Equipment].
 */
fun toCim(pb: PBEquipment, cim: Equipment, networkService: NetworkService): Equipment =
    cim.apply {
        inService = pb.inService
        normallyInService = pb.normallyInService
        commissionedDate = pb.commissionedDateSet.takeUnless { pb.hasCommissionedDateNull() }?.toInstant()

        pb.equipmentContainerMRIDsList.forEach { equipmentContainerMRID ->
            networkService.resolveOrDeferReference(Resolvers.containers(this), equipmentContainerMRID)
        }

        pb.usagePointMRIDsList.forEach { usagePointMRID ->
            networkService.resolveOrDeferReference(Resolvers.usagePoints(this), usagePointMRID)
        }

        pb.operationalRestrictionMRIDsList.forEach { operationalRestrictionMRID ->
            networkService.resolveOrDeferReference(Resolvers.operationalRestrictions(this), operationalRestrictionMRID)
        }

        pb.currentContainerMRIDsList.forEach { currentContainerMRID ->
            networkService.resolveOrDeferReference(Resolvers.currentContainers(this), currentContainerMRID)
        }

        toCim(pb.psr, this, networkService)
    }

/**
 * Convert the protobuf [PBEquipmentContainer] into its CIM counterpart.
 *
 * @param pb The protobuf [PBEquipmentContainer] to convert.
 * @param cim The CIM [EquipmentContainer] to populate.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [EquipmentContainer].
 */
fun toCim(pb: PBEquipmentContainer, cim: EquipmentContainer, networkService: NetworkService): EquipmentContainer =
    cim.apply {
        toCim(pb.cnc, this, networkService)
    }

/**
 * Convert the protobuf [PBFeeder] into its CIM counterpart.
 *
 * @param pb The protobuf [PBFeeder] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [Feeder].
 */
fun toCim(pb: PBFeeder, networkService: NetworkService): Feeder =
    Feeder(pb.mRID()).apply {
        networkService.resolveOrDeferReference(Resolvers.normalHeadTerminal(this), pb.normalHeadTerminalMRID)
        networkService.resolveOrDeferReference(Resolvers.normalEnergizingSubstation(this), pb.normalEnergizingSubstationMRID)

        pb.normalEnergizedLvFeederMRIDsList.forEach { normalEnergizedLvFeederMRID ->
            networkService.resolveOrDeferReference(Resolvers.normalEnergizedLvFeeders(this), normalEnergizedLvFeederMRID)
        }

        pb.currentlyEnergizedLvFeedersMRIDsList.forEach { currentEnergizedLvFeederMRID ->
            networkService.resolveOrDeferReference(Resolvers.currentEnergizedLvFeeders(this), currentEnergizedLvFeederMRID)
        }

        toCim(pb.ec, this, networkService)
    }

/**
 * Convert the protobuf [PBGeographicalRegion] into its CIM counterpart.
 *
 * @param pb The protobuf [PBGeographicalRegion] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [GeographicalRegion].
 */
fun toCim(pb: PBGeographicalRegion, networkService: NetworkService): GeographicalRegion =
    GeographicalRegion(pb.mRID()).apply {
        pb.subGeographicalRegionMRIDsList.forEach { subGeographicalRegionMRID ->
            networkService.resolveOrDeferReference(Resolvers.subGeographicalRegions(this), subGeographicalRegionMRID)
        }
        toCim(pb.io, this, networkService)
    }

/**
 * Convert the protobuf [PBPowerSystemResource] into its CIM counterpart.
 *
 * @param pb The protobuf [PBPowerSystemResource] to convert.
 * @param cim The CIM [PowerSystemResource] to populate.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [PowerSystemResource].
 */
fun toCim(pb: PBPowerSystemResource, cim: PowerSystemResource, networkService: NetworkService): PowerSystemResource =
    cim.apply {
        // NOTE: assetInfoMRID will be handled by classes that use it with specific types.

        networkService.resolveOrDeferReference(Resolvers.location(this), pb.locationMRID)
        pb.assetMRIDsList.forEach { assetMRID ->
            networkService.resolveOrDeferReference(Resolvers.assets(this), assetMRID)
        }
        numControls = pb.numControlsSet.takeUnless { pb.hasNumControlsNull() }
        toCim(pb.io, this, networkService)
    }

/**
 * Convert the protobuf [PBSubGeographicalRegion] into its CIM counterpart.
 *
 * @param pb The protobuf [PBSubGeographicalRegion] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [SubGeographicalRegion].
 */
fun toCim(pb: PBSubGeographicalRegion, networkService: NetworkService): SubGeographicalRegion =
    SubGeographicalRegion(pb.mRID()).apply {
        networkService.resolveOrDeferReference(Resolvers.geographicalRegion(this), pb.geographicalRegionMRID)

        pb.substationMRIDsList.forEach { substationMRID ->
            networkService.resolveOrDeferReference(Resolvers.substations(this), substationMRID)
        }
        toCim(pb.io, this, networkService)
    }

/**
 * Convert the protobuf [PBSubstation] into its CIM counterpart.
 *
 * @param pb The protobuf [PBSubstation] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [Substation].
 */
fun toCim(pb: PBSubstation, networkService: NetworkService): Substation =
    Substation(pb.mRID()).apply {
        networkService.resolveOrDeferReference(Resolvers.subGeographicalRegion(this), pb.subGeographicalRegionMRID)
        pb.normalEnergizedFeederMRIDsList.forEach { normalEnergizedFeederMRID ->
            networkService.resolveOrDeferReference(Resolvers.normalEnergizedFeeders(this), normalEnergizedFeederMRID)
        }
        pb.loopMRIDsList.forEach { loopMRID ->
            networkService.resolveOrDeferReference(Resolvers.loops(this), loopMRID)
        }
        pb.normalEnergizedLoopMRIDsList.forEach { normalEnergizedLoopMRID ->
            networkService.resolveOrDeferReference(Resolvers.normalEnergizedLoops(this), normalEnergizedLoopMRID)
        }
        pb.circuitMRIDsList.forEach { circuitMRID ->
            networkService.resolveOrDeferReference(Resolvers.circuits(this), circuitMRID)
        }
        toCim(pb.ec, this, networkService)
    }

/**
 * Convert the protobuf [PBTerminal] into its CIM counterpart.
 *
 * @param pb The protobuf [PBTerminal] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [Terminal].
 */
fun toCim(pb: PBTerminal, networkService: NetworkService): Terminal =
    Terminal(pb.mRID()).apply {

        phases = mapPhaseCode.toCim(pb.phases)
        sequenceNumber = pb.sequenceNumber
        normalFeederDirection = mapFeederDirection.toCim(pb.normalFeederDirection)
        currentFeederDirection = mapFeederDirection.toCim(pb.currentFeederDirection)
        normalPhases.phaseStatusInternal = (pb.tracedPhases and 0xFFFF).toUShort()
        currentPhases.phaseStatusInternal = ((pb.tracedPhases shr 16) and 0xFFFF).toUShort()

        // Sequence number must be set before adding the terminal to the conducting equipment to prevent it from being auto set
        networkService.resolveOrDeferReference(Resolvers.conductingEquipment(this), pb.conductingEquipmentMRID)
        networkService.resolveOrDeferReference(Resolvers.connectivityNode(this), pb.connectivityNodeMRID)

        toCim(pb.ad, this, networkService)
    }

/**
 * An extension to add a converted copy of the protobuf [PBBaseVoltage] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBBaseVoltage): BaseVoltage? = tryAddOrNull(toCim(pb, this))

/**
 * An extension to add a converted copy of the protobuf [PBConnectivityNode] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBConnectivityNode): ConnectivityNode? = tryAddOrNull(toCim(pb, this))

/**
 * An extension to add a converted copy of the protobuf [PBFeeder] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBFeeder): Feeder? = tryAddOrNull(toCim(pb, this))

/**
 * An extension to add a converted copy of the protobuf [PBGeographicalRegion] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBGeographicalRegion): GeographicalRegion? = tryAddOrNull(toCim(pb, this))

/**
 * An extension to add a converted copy of the protobuf [PBNameType] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBNameType): NameType = toCim(pb, this) // Special case

/**
 * An extension to add a converted copy of the protobuf [PBSubGeographicalRegion] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBSubGeographicalRegion): SubGeographicalRegion? = tryAddOrNull(toCim(pb, this))

/**
 * An extension to add a converted copy of the protobuf [PBSubstation] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBSubstation): Substation? = tryAddOrNull(toCim(pb, this))

/**
 * An extension to add a converted copy of the protobuf [PBTerminal] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBTerminal): Terminal? = tryAddOrNull(toCim(pb, this))

// #############################
// # IEC61970 Base Equivalents #
// #############################

/**
 * Convert the protobuf [PBEquivalentBranch] into its CIM counterpart.
 *
 * @param pb The protobuf [PBEquivalentBranch] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [EquivalentBranch].
 */
fun toCim(pb: PBEquivalentBranch, networkService: NetworkService): EquivalentBranch =
    EquivalentBranch(pb.mRID()).apply {
        negativeR12 = pb.negativeR12Set.takeUnless { pb.hasNegativeR12Null() }
        negativeR21 = pb.negativeR21Set.takeUnless { pb.hasNegativeR21Null() }
        negativeX12 = pb.negativeX12Set.takeUnless { pb.hasNegativeX12Null() }
        negativeX21 = pb.negativeX21Set.takeUnless { pb.hasNegativeX21Null() }
        positiveR12 = pb.positiveR12Set.takeUnless { pb.hasPositiveR12Null() }
        positiveR21 = pb.positiveR21Set.takeUnless { pb.hasPositiveR21Null() }
        positiveX12 = pb.positiveX12Set.takeUnless { pb.hasPositiveX12Null() }
        positiveX21 = pb.positiveX21Set.takeUnless { pb.hasPositiveX21Null() }
        r = pb.rSet.takeUnless { pb.hasRNull() }
        r21 = pb.r21Set.takeUnless { pb.hasR21Null() }
        x = pb.xSet.takeUnless { pb.hasXNull() }
        x21 = pb.x21Set.takeUnless { pb.hasX21Null() }
        zeroR12 = pb.zeroR12Set.takeUnless { pb.hasZeroR12Null() }
        zeroR21 = pb.zeroR21Set.takeUnless { pb.hasZeroR21Null() }
        zeroX12 = pb.zeroX12Set.takeUnless { pb.hasZeroX12Null() }
        zeroX21 = pb.zeroX21Set.takeUnless { pb.hasZeroX21Null() }
        toCim(pb.ee, this, networkService)
    }

/**
 * Convert the protobuf [PBEquivalentEquipment] into its CIM counterpart.
 *
 * @param pb The protobuf [PBEquivalentEquipment] to convert.
 * @param cim The CIM [EquivalentEquipment] to populate.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [EquivalentEquipment].
 */
fun toCim(pb: PBEquivalentEquipment, cim: EquivalentEquipment, networkService: NetworkService): EquivalentEquipment =
    cim.apply { toCim(pb.ce, this, networkService) }

/**
 * An extension to add a converted copy of the protobuf [PBEquivalentBranch] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBEquivalentBranch): EquivalentBranch? = tryAddOrNull(toCim(pb, this))

// #######################################
// # IEC61970 Base Generation Production #
// #######################################

/**
 * Convert the protobuf [PBBatteryUnit] into its CIM counterpart.
 *
 * @param pb The protobuf [PBBatteryUnit] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [BatteryUnit].
 */
fun toCim(pb: PBBatteryUnit, networkService: NetworkService): BatteryUnit =
    BatteryUnit(pb.mRID()).apply {
        batteryState = mapBatteryStateKind.toCim(pb.batteryState)
        ratedE = pb.ratedESet.takeUnless { pb.hasRatedENull() }
        storedE = pb.storedESet.takeUnless { pb.hasStoredENull() }
        pb.batteryControlMRIDsList.forEach {
            networkService.resolveOrDeferReference(Resolvers.batteryControls(this), it)
        }
        toCim(pb.peu, this, networkService)
    }

/**
 * Convert the protobuf [PBPhotoVoltaicUnit] into its CIM counterpart.
 *
 * @param pb The protobuf [PBPhotoVoltaicUnit] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [PhotoVoltaicUnit].
 */
fun toCim(pb: PBPhotoVoltaicUnit, networkService: NetworkService): PhotoVoltaicUnit =
    PhotoVoltaicUnit(pb.mRID()).apply {
        toCim(pb.peu, this, networkService)
    }

/**
 * Convert the protobuf [PBPowerElectronicsUnit] into its CIM counterpart.
 *
 * @param pb The protobuf [PBPowerElectronicsUnit] to convert.
 * @param cim The CIM [PowerElectronicsUnit] to populate.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [PowerElectronicsUnit].
 */
fun toCim(pb: PBPowerElectronicsUnit, cim: PowerElectronicsUnit, networkService: NetworkService): PowerElectronicsUnit =
    cim.apply {
        networkService.resolveOrDeferReference(Resolvers.powerElectronicsConnection(this), pb.powerElectronicsConnectionMRID)
        maxP = pb.maxPSet.takeUnless { pb.hasMaxPNull() }
        minP = pb.minPSet.takeUnless { pb.hasMinPNull() }
        toCim(pb.eq, this, networkService)
    }

/**
 * Convert the protobuf [PBPowerElectronicsWindUnit] into its CIM counterpart.
 *
 * @param pb The protobuf [PBPowerElectronicsWindUnit] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [PowerElectronicsWindUnit].
 */
fun toCim(pb: PBPowerElectronicsWindUnit, networkService: NetworkService): PowerElectronicsWindUnit =
    PowerElectronicsWindUnit(pb.mRID()).apply {
        toCim(pb.peu, this, networkService)
    }

/**
 * An extension to add a converted copy of the protobuf [PBBatteryUnit] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBBatteryUnit): BatteryUnit? = tryAddOrNull(toCim(pb, this))

/**
 * An extension to add a converted copy of the protobuf [PBPhotoVoltaicUnit] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBPhotoVoltaicUnit): PhotoVoltaicUnit? = tryAddOrNull(toCim(pb, this))

/**
 * An extension to add a converted copy of the protobuf [PBPowerElectronicsWindUnit] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBPowerElectronicsWindUnit): PowerElectronicsWindUnit? = tryAddOrNull(toCim(pb, this))

// ######################
// # IEC61970 Base Meas #
// ######################

/**
 * Convert the protobuf [PBAccumulator] into its CIM counterpart.
 *
 * @param pb The protobuf [PBAccumulator] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [Accumulator].
 */
fun toCim(pb: PBAccumulator, networkService: NetworkService): Accumulator =
    Accumulator(pb.mRID()).apply {
        toCim(pb.measurement, this, networkService)
    }

/**
 * Convert the protobuf [PBAnalog] into its CIM counterpart.
 *
 * @param pb The protobuf [PBAnalog] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [Analog].
 */
fun toCim(pb: PBAnalog, networkService: NetworkService): Analog =
    Analog(pb.mRID()).apply {
        toCim(pb.measurement, this, networkService)
        positiveFlowIn = pb.positiveFlowInSet.takeUnless { pb.hasPositiveFlowInNull() }
    }

/**
 * Convert the protobuf [PBControl] into its CIM counterpart.
 *
 * @param pb The protobuf [PBControl] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [Control].
 */
fun toCim(pb: PBControl, networkService: NetworkService): Control =
    Control(pb.mRID()).apply {
        powerSystemResourceMRID = pb.powerSystemResourceMRID.takeIf { it.isNotBlank() }
        networkService.resolveOrDeferReference(Resolvers.remoteControl(this), pb.remoteControlMRID)
        toCim(pb.ip, this, networkService)
    }

/**
 * Convert the protobuf [PBDiscrete] into its CIM counterpart.
 *
 * @param pb The protobuf [PBDiscrete] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [Discrete].
 */
fun toCim(pb: PBDiscrete, networkService: NetworkService): Discrete =
    Discrete(pb.mRID()).apply {
        toCim(pb.measurement, this, networkService)
    }

/**
 * Convert the protobuf [PBIoPoint] into its CIM counterpart.
 *
 * @param pb The protobuf [PBIoPoint] to convert.
 * @param cim The CIM [IoPoint] to populate.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [IoPoint].
 */
fun toCim(pb: PBIoPoint, cim: IoPoint, networkService: NetworkService): IoPoint =
    cim.apply { toCim(pb.io, this, networkService) }

/**
 * Convert the protobuf [PBMeasurement] into its CIM counterpart.
 *
 * @param pb The protobuf [PBMeasurement] to convert.
 * @param cim The [NetworkService] the converted CIM object will be added too.
 * @param networkService The converted [pb] as a CIM [Measurement].
 */
fun toCim(pb: PBMeasurement, cim: Measurement, networkService: NetworkService) {
    cim.apply {
        powerSystemResourceMRID = pb.powerSystemResourceMRID.takeIf { it.isNotBlank() }
        networkService.resolveOrDeferReference(Resolvers.remoteSource(this), pb.remoteSourceMRID)
        terminalMRID = pb.terminalMRID.takeIf { it.isNotBlank() }
        phases = mapPhaseCode.toCim(pb.phases)
        unitSymbol = mapUnitSymbol.toCim(pb.unitSymbol)
        toCim(pb.io, this, networkService)
    }
}

/**
 * An extension to add a converted copy of the protobuf [PBAccumulator] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBAccumulator): Accumulator? = tryAddOrNull(toCim(pb, this))

/**
 * An extension to add a converted copy of the protobuf [PBAnalog] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBAnalog): Analog? = tryAddOrNull(toCim(pb, this))

/**
 * An extension to add a converted copy of the protobuf [PBControl] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBControl): Control? = tryAddOrNull(toCim(pb, this))

/**
 * An extension to add a converted copy of the protobuf [PBDiscrete] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBDiscrete): Discrete? = tryAddOrNull(toCim(pb, this))

// ############################
// # IEC61970 Base Protection #
// ############################

/**
 * Convert the protobuf [PBCurrentRelay] into its CIM counterpart.
 *
 * @param pb The protobuf [PBCurrentRelay] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [CurrentRelay].
 */
fun toCim(pb: PBCurrentRelay, networkService: NetworkService): CurrentRelay =
    CurrentRelay(pb.mRID()).apply {
        currentLimit1 = pb.currentLimit1Set.takeUnless { pb.hasCurrentLimit1Null() }
        inverseTimeFlag = pb.inverseTimeFlagSet.takeUnless { pb.hasInverseTimeFlagNull() }
        timeDelay1 = pb.timeDelay1Set.takeUnless { pb.hasTimeDelay1Null() }
        toCim(pb.prf, this, networkService)
    }

/**
 * An extension to add a converted copy of the protobuf [PBCurrentRelay] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBCurrentRelay): CurrentRelay? = tryAddOrNull(toCim(pb, this))

// #######################
// # IEC61970 Base Scada #
// #######################

/**
 * Convert the protobuf [PBRemoteControl] into its CIM counterpart.
 *
 * @param pb The protobuf [PBRemoteControl] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [RemoteControl].
 */
fun toCim(pb: PBRemoteControl, networkService: NetworkService): RemoteControl =
    RemoteControl(pb.mRID()).apply {
        networkService.resolveOrDeferReference(Resolvers.control(this), pb.controlMRID)
        toCim(pb.rp, this, networkService)
    }

/**
 * Convert the protobuf [PBRemotePoint] into its CIM counterpart.
 *
 * @param pb The protobuf [PBRemotePoint] to convert.
 * @param cim The CIM [RemotePoint] to populate.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [RemotePoint].
 */
fun toCim(pb: PBRemotePoint, cim: RemotePoint, networkService: NetworkService): RemotePoint =
    cim.apply { toCim(pb.io, this, networkService) }

/**
 * Convert the protobuf [PBRemoteSource] into its CIM counterpart.
 *
 * @param pb The protobuf [PBRemoteSource] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [RemoteSource].
 */
fun toCim(pb: PBRemoteSource, networkService: NetworkService): RemoteSource =
    RemoteSource(pb.mRID()).apply {
        networkService.resolveOrDeferReference(Resolvers.measurement(this), pb.measurementMRID)
        toCim(pb.rp, this, networkService)
    }

/**
 * An extension to add a converted copy of the protobuf [PBRemoteControl] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBRemoteControl): RemoteControl? = tryAddOrNull(toCim(pb, this))

/**
 * An extension to add a converted copy of the protobuf [PBRemoteSource] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBRemoteSource): RemoteSource? = tryAddOrNull(toCim(pb, this))

// #######################
// # IEC61970 Base Wires #
// #######################

/**
 * Convert the protobuf [PBAcLineSegment] into its CIM counterpart.
 *
 * @param pb The protobuf [PBAcLineSegment] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [AcLineSegment].
 */
fun toCim(pb: PBAcLineSegment, networkService: NetworkService): AcLineSegment =
    AcLineSegment(pb.mRID()).apply {
        networkService.resolveOrDeferReference(Resolvers.perLengthImpedance(this), pb.perLengthImpedanceMRID)
        pb.cutMRIDsList.forEach { cutMRID ->
            networkService.resolveOrDeferReference(Resolvers.cuts(this), cutMRID)
        }
        pb.clampMRIDsList.forEach { clampMRID ->
            networkService.resolveOrDeferReference(Resolvers.clamps(this), clampMRID)
        }
        toCim(pb.cd, this, networkService)
    }

/**
 * Convert the protobuf [PBBreaker] into its CIM counterpart.
 *
 * @param pb The protobuf [PBBreaker] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [Breaker].
 */
fun toCim(pb: PBBreaker, networkService: NetworkService): Breaker =
    Breaker(pb.mRID()).apply {
        inTransitTime = pb.inTransitTimeSet.takeUnless { pb.hasInTransitTimeNull() }
        toCim(pb.sw, this, networkService)
    }

/**
 * Convert the protobuf [PBBusbarSection] into its CIM counterpart.
 *
 * @param pb The protobuf [PBBusbarSection] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [BusbarSection].
 */
fun toCim(pb: PBBusbarSection, networkService: NetworkService): BusbarSection =
    BusbarSection(pb.mRID()).apply {
        toCim(pb.cn, this, networkService)
    }

/**
 * Convert the protobuf [PBClamp] into its CIM counterpart.
 *
 * @param pb The protobuf [PBClamp] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [Clamp].
 */
fun toCim(pb: PBClamp, networkService: NetworkService): Clamp =
    Clamp(pb.mRID()).apply {
        lengthFromTerminal1 = pb.lengthFromTerminal1Set.takeUnless { pb.hasLengthFromTerminal1Null() }
        networkService.resolveOrDeferReference(Resolvers.acLineSegment(this), pb.acLineSegmentMRID)
        toCim(pb.ce, this, networkService)
    }

/**
 * Convert the protobuf [PBConductor] into its CIM counterpart.
 *
 * @param pb The protobuf [PBConductor] to convert.
 * @param cim The CIM [Conductor] to populate.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [Conductor].
 */
fun toCim(pb: PBConductor, cim: Conductor, networkService: NetworkService): Conductor =
    cim.apply {
        length = pb.lengthSet.takeUnless { pb.hasLengthNull() }
        designTemperature = pb.designTemperatureSet.takeUnless { pb.hasDesignTemperatureNull() }
        designRating = pb.designRatingSet.takeUnless { pb.hasDesignRatingNull() }
        networkService.resolveOrDeferReference(Resolvers.assetInfo(this), pb.assetInfoMRID())
        toCim(pb.ce, this, networkService)
    }

/**
 * Convert the protobuf [PBConnector] into its CIM counterpart.
 *
 * @param pb The protobuf [PBConnector] to convert.
 * @param cim The CIM [Connector] to populate.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [Connector].
 */
fun toCim(pb: PBConnector, cim: Connector, networkService: NetworkService): Connector =
    cim.apply { toCim(pb.ce, this, networkService) }

/**
 * Convert the protobuf [PBCut] into its CIM counterpart.
 *
 * @param pb The protobuf [PBCut] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [Cut].
 */
fun toCim(pb: PBCut, networkService: NetworkService): Cut =
    Cut(pb.mRID()).apply {
        lengthFromTerminal1 = pb.lengthFromTerminal1Set.takeUnless { pb.hasLengthFromTerminal1Null() }
        networkService.resolveOrDeferReference(Resolvers.acLineSegment(this), pb.acLineSegmentMRID)
        toCim(pb.sw, this, networkService)
    }

/**
 * Convert the protobuf [PBDisconnector] into its CIM counterpart.
 *
 * @param pb The protobuf [PBDisconnector] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [Disconnector].
 */
fun toCim(pb: PBDisconnector, networkService: NetworkService): Disconnector =
    Disconnector(pb.mRID()).apply {
        toCim(pb.sw, this, networkService)
    }

/**
 * Convert the protobuf [PBEarthFaultCompensator] into its CIM counterpart.
 *
 * @param pb The protobuf [PBEarthFaultCompensator] to convert.
 * @param cim The CIM [EarthFaultCompensator] to populate.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [EarthFaultCompensator].
 */
fun toCim(pb: PBEarthFaultCompensator, cim: EarthFaultCompensator, networkService: NetworkService): EarthFaultCompensator =
    cim.apply {
        r = pb.rSet.takeUnless { pb.hasRNull() }
        toCim(pb.ce, this, networkService)
    }

/**
 * Convert the protobuf [PBEnergyConnection] into its CIM counterpart.
 *
 * @param pb The protobuf [PBEnergyConnection] to convert.
 * @param cim The CIM [EnergyConnection] to populate.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [EnergyConnection].
 */
fun toCim(pb: PBEnergyConnection, cim: EnergyConnection, networkService: NetworkService): EnergyConnection =
    cim.apply { toCim(pb.ce, this, networkService) }

/**
 * Convert the protobuf [PBEnergyConsumer] into its CIM counterpart.
 *
 * @param pb The protobuf [PBEnergyConsumer] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [EnergyConsumer].
 */
fun toCim(pb: PBEnergyConsumer, networkService: NetworkService): EnergyConsumer =
    EnergyConsumer(pb.mRID()).apply {

        pb.energyConsumerPhasesMRIDsList.forEach { energyConsumerPhasesMRID ->
            networkService.resolveOrDeferReference(Resolvers.phases(this), energyConsumerPhasesMRID)
        }
        customerCount = pb.customerCountSet.takeUnless { pb.hasCustomerCountNull() }
        grounded = pb.groundedSet.takeUnless { pb.hasGroundedNull() }
        p = pb.pSet.takeUnless { pb.hasPNull() }
        pFixed = pb.pFixedSet.takeUnless { pb.hasPFixedNull() }
        phaseConnection = mapPhaseShuntConnectionKind.toCim(pb.phaseConnection)
        q = pb.qSet.takeUnless { pb.hasQNull() }
        qFixed = pb.qFixedSet.takeUnless { pb.hasQFixedNull() }
        toCim(pb.ec, this, networkService)
    }

/**
 * Convert the protobuf [PBEnergyConsumerPhase] into its CIM counterpart.
 *
 * @param pb The protobuf [PBEnergyConsumerPhase] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [EnergyConsumerPhase].
 */
fun toCim(pb: PBEnergyConsumerPhase, networkService: NetworkService): EnergyConsumerPhase =
    EnergyConsumerPhase(pb.mRID()).apply {
        networkService.resolveOrDeferReference(Resolvers.energyConsumer(this), pb.energyConsumerMRID)
        phase = mapSinglePhaseKind.toCim(pb.phase)
        p = pb.pSet.takeUnless { pb.hasPNull() }
        pFixed = pb.pFixedSet.takeUnless { pb.hasPFixedNull() }
        q = pb.qSet.takeUnless { pb.hasQNull() }
        qFixed = pb.qFixedSet.takeUnless { pb.hasQFixedNull() }
        toCim(pb.psr, this, networkService)
    }

/**
 * Convert the protobuf [PBEnergySource] into its CIM counterpart.
 *
 * @param pb The protobuf [PBEnergySource] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [EnergySource].
 */
fun toCim(pb: PBEnergySource, networkService: NetworkService): EnergySource =
    EnergySource(pb.mRID()).apply {
        pb.energySourcePhasesMRIDsList.forEach { energySourcePhasesMRID ->
            networkService.resolveOrDeferReference(Resolvers.phases(this), energySourcePhasesMRID)
        }

        activePower = pb.activePowerSet.takeUnless { pb.hasActivePowerNull() }
        reactivePower = pb.reactivePowerSet.takeUnless { pb.hasReactivePowerNull() }
        voltageAngle = pb.voltageAngleSet.takeUnless { pb.hasVoltageAngleNull() }
        voltageMagnitude = pb.voltageMagnitudeSet.takeUnless { pb.hasVoltageMagnitudeNull() }
        r = pb.rSet.takeUnless { pb.hasRNull() }
        x = pb.xSet.takeUnless { pb.hasXNull() }
        pMax = pb.pMaxSet.takeUnless { pb.hasPMaxNull() }
        pMin = pb.pMinSet.takeUnless { pb.hasPMinNull() }
        r0 = pb.r0Set.takeUnless { pb.hasR0Null() }
        rn = pb.rnSet.takeUnless { pb.hasRnNull() }
        x0 = pb.x0Set.takeUnless { pb.hasX0Null() }
        xn = pb.xnSet.takeUnless { pb.hasXnNull() }
        isExternalGrid = pb.isExternalGridSet.takeUnless { pb.hasIsExternalGridNull() }
        rMin = pb.rMinSet.takeUnless { pb.hasRMinNull() }
        rnMin = pb.rnMinSet.takeUnless { pb.hasRnMinNull() }
        r0Min = pb.r0MinSet.takeUnless { pb.hasR0MinNull() }
        xMin = pb.xMinSet.takeUnless { pb.hasXMinNull() }
        xnMin = pb.xnMinSet.takeUnless { pb.hasXnMinNull() }
        x0Min = pb.x0MinSet.takeUnless { pb.hasX0MinNull() }
        rMax = pb.rMaxSet.takeUnless { pb.hasRMaxNull() }
        rnMax = pb.rnMaxSet.takeUnless { pb.hasRnMaxNull() }
        r0Max = pb.r0MaxSet.takeUnless { pb.hasR0MaxNull() }
        xMax = pb.xMaxSet.takeUnless { pb.hasXMaxNull() }
        xnMax = pb.xnMaxSet.takeUnless { pb.hasXnMaxNull() }
        x0Max = pb.x0MaxSet.takeUnless { pb.hasX0MaxNull() }

        toCim(pb.ec, this, networkService)
    }

/**
 * Convert the protobuf [PBEnergySourcePhase] into its CIM counterpart.
 *
 * @param pb The protobuf [PBEnergySourcePhase] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [EnergySourcePhase].
 */
fun toCim(pb: PBEnergySourcePhase, networkService: NetworkService): EnergySourcePhase =
    EnergySourcePhase(pb.mRID()).apply {
        networkService.resolveOrDeferReference(Resolvers.energySource(this), pb.energySourceMRID)
        phase = mapSinglePhaseKind.toCim(pb.phase)
        toCim(pb.psr, this, networkService)
    }

/**
 * Convert the protobuf [PBFuse] into its CIM counterpart.
 *
 * @param pb The protobuf [PBFuse] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [Fuse].
 */
fun toCim(pb: PBFuse, networkService: NetworkService): Fuse =
    Fuse(pb.mRID()).apply {
        networkService.resolveOrDeferReference(Resolvers.function(this), pb.functionMRID)
        toCim(pb.sw, this, networkService)
    }

/**
 * Convert the protobuf [PBGround] into its CIM counterpart.
 *
 * @param pb The protobuf [PBGround] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [Ground].
 */
fun toCim(pb: PBGround, networkService: NetworkService): Ground =
    Ground(pb.mRID()).apply {
        toCim(pb.ce, this, networkService)
    }

/**
 * Convert the protobuf [PBGroundDisconnector] into its CIM counterpart.
 *
 * @param pb The protobuf [PBGroundDisconnector] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [GroundDisconnector].
 */
fun toCim(pb: PBGroundDisconnector, networkService: NetworkService): GroundDisconnector =
    GroundDisconnector(pb.mRID()).apply {
        toCim(pb.sw, this, networkService)
    }

/**
 * Convert the protobuf [PBGroundingImpedance] into its CIM counterpart.
 *
 * @param pb The protobuf [PBGroundingImpedance] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [GroundingImpedance].
 */
fun toCim(pb: PBGroundingImpedance, networkService: NetworkService): GroundingImpedance =
    GroundingImpedance(pb.mRID()).apply {
        x = pb.xSet.takeUnless { pb.hasXNull() }
        toCim(pb.efc, this, networkService)
    }

/**
 * Convert the protobuf [PBJumper] into its CIM counterpart.
 *
 * @param pb The protobuf [PBJumper] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [Jumper].
 */
fun toCim(pb: PBJumper, networkService: NetworkService): Jumper =
    Jumper(pb.mRID()).apply {
        toCim(pb.sw, this, networkService)
    }

/**
 * Convert the protobuf [PBJunction] into its CIM counterpart.
 *
 * @param pb The protobuf [PBJunction] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [Junction].
 */
fun toCim(pb: PBJunction, networkService: NetworkService): Junction =
    Junction(pb.mRID()).apply {
        toCim(pb.cn, this, networkService)
    }

/**
 * Convert the protobuf [PBLine] into its CIM counterpart.
 *
 * @param pb The protobuf [PBLine] to convert.
 * @param cim The CIM [Line] to populate.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [Line].
 */
fun toCim(pb: PBLine, cim: Line, networkService: NetworkService): Line =
    cim.apply { toCim(pb.ec, this, networkService) }

/**
 * Convert the protobuf [PBLinearShuntCompensator] into its CIM counterpart.
 *
 * @param pb The protobuf [PBLinearShuntCompensator] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [LinearShuntCompensator].
 */
fun toCim(pb: PBLinearShuntCompensator, networkService: NetworkService): LinearShuntCompensator =
    LinearShuntCompensator(pb.mRID()).apply {
        b0PerSection = pb.b0PerSectionSet.takeUnless { pb.hasB0PerSectionNull() }
        bPerSection = pb.bPerSectionSet.takeUnless { pb.hasBPerSectionNull() }
        g0PerSection = pb.g0PerSectionSet.takeUnless { pb.hasG0PerSectionNull() }
        gPerSection = pb.gPerSectionSet.takeUnless { pb.hasGPerSectionNull() }
        toCim(pb.sc, this, networkService)
    }

/**
 * Convert the protobuf [PBLoadBreakSwitch] into its CIM counterpart.
 *
 * @param pb The protobuf [PBLoadBreakSwitch] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [LoadBreakSwitch].
 */
fun toCim(pb: PBLoadBreakSwitch, networkService: NetworkService): LoadBreakSwitch =
    LoadBreakSwitch(pb.mRID()).apply {
        toCim(pb.ps, this, networkService)
    }

/**
 * Convert the protobuf [PBPerLengthImpedance] into its CIM counterpart.
 *
 * @param pb The protobuf [PBPerLengthImpedance] to convert.
 * @param cim The CIM [PerLengthImpedance] to populate.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [PerLengthImpedance].
 */
fun toCim(pb: PBPerLengthImpedance, cim: PerLengthImpedance, networkService: NetworkService): PerLengthImpedance =
    cim.apply { toCim(pb.lp, cim, networkService) }

/**
 * Convert the protobuf [PBPerLengthLineParameter] into its CIM counterpart.
 *
 * @param pb The protobuf [PBPerLengthLineParameter] to convert.
 * @param cim The CIM [PerLengthLineParameter] to populate.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [PerLengthLineParameter].
 */
fun toCim(pb: PBPerLengthLineParameter, cim: PerLengthLineParameter, networkService: NetworkService): PerLengthLineParameter =
    cim.apply { toCim(pb.io, this, networkService) }

/**
 * Convert the protobuf [PBPerLengthPhaseImpedance] into its CIM counterpart.
 *
 * @param pb The protobuf [PBPerLengthPhaseImpedance] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [PerLengthPhaseImpedance].
 */
fun toCim(pb: PBPerLengthPhaseImpedance, networkService: NetworkService): PerLengthPhaseImpedance =
    PerLengthPhaseImpedance(pb.mRID()).apply {
        pb.phaseImpedanceDataList.forEach { addData(toCim(it)) }
        toCim(pb.pli, this, networkService)
    }

/**
 * Convert the protobuf [PBPerLengthSequenceImpedance] into its CIM counterpart.
 *
 * @param pb The protobuf [PBPerLengthSequenceImpedance] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [PerLengthSequenceImpedance].
 */
fun toCim(pb: PBPerLengthSequenceImpedance, networkService: NetworkService): PerLengthSequenceImpedance =
    PerLengthSequenceImpedance(pb.mRID()).apply {
        r = pb.rSet.takeUnless { pb.hasRNull() }
        x = pb.xSet.takeUnless { pb.hasXNull() }
        r0 = pb.r0Set.takeUnless { pb.hasR0Null() }
        x0 = pb.x0Set.takeUnless { pb.hasX0Null() }
        bch = pb.bchSet.takeUnless { pb.hasBchNull() }
        gch = pb.gchSet.takeUnless { pb.hasGchNull() }
        b0ch = pb.b0ChSet.takeUnless { pb.hasB0ChNull() }
        g0ch = pb.g0ChSet.takeUnless { pb.hasG0ChNull() }
        toCim(pb.pli, this, networkService)
    }

/**
 * Convert the protobuf [PBPetersenCoil] into its CIM counterpart.
 *
 * @param pb The protobuf [PBPetersenCoil] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [PetersenCoil].
 */
fun toCim(pb: PBPetersenCoil, networkService: NetworkService): PetersenCoil =
    PetersenCoil(pb.mRID()).apply {
        xGroundNominal = pb.xGroundNominalSet.takeUnless { pb.hasXGroundNominalNull() }
        toCim(pb.efc, this, networkService)
    }

/**
 * Convert the protobuf [PBPhaseImpedanceData] into its CIM counterpart.
 *
 * @param pb The protobuf [PBPhaseImpedanceData] to convert.
 * @return The converted [pb] as a CIM [PhaseImpedanceData].
 */
fun toCim(pb: PBPhaseImpedanceData): PhaseImpedanceData =
    PhaseImpedanceData(
        mapSinglePhaseKind.toCim(pb.fromPhase),
        mapSinglePhaseKind.toCim(pb.toPhase),
        pb.bSet.takeUnless { pb.hasBNull() },
        pb.gSet.takeUnless { pb.hasGNull() },
        pb.rSet.takeUnless { pb.hasRNull() },
        pb.xSet.takeUnless { pb.hasXNull() }
    )

/**
 * Convert the protobuf [PBPowerElectronicsConnection] into its CIM counterpart.
 *
 * @param pb The protobuf [PBPowerElectronicsConnection] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [PowerElectronicsConnection].
 */
fun toCim(pb: PBPowerElectronicsConnection, networkService: NetworkService): PowerElectronicsConnection =
    PowerElectronicsConnection(pb.mRID()).apply {
        pb.powerElectronicsUnitMRIDsList.forEach { powerElectronicsUnitMRID ->
            networkService.resolveOrDeferReference(Resolvers.powerElectronicsUnit(this), powerElectronicsUnitMRID)
        }
        pb.powerElectronicsConnectionPhaseMRIDsList.forEach { powerElectronicsConnectionPhaseMRID ->
            networkService.resolveOrDeferReference(Resolvers.powerElectronicsConnectionPhase(this), powerElectronicsConnectionPhaseMRID)
        }
        maxIFault = pb.maxIFaultSet.takeUnless { pb.hasMaxIFaultNull() }
        maxQ = pb.maxQSet.takeUnless { pb.hasMaxQNull() }
        minQ = pb.minQSet.takeUnless { pb.hasMinQNull() }
        p = pb.pSet.takeUnless { pb.hasPNull() }
        q = pb.qSet.takeUnless { pb.hasQNull() }
        ratedS = pb.ratedSSet.takeUnless { pb.hasRatedSNull() }
        ratedU = pb.ratedUSet.takeUnless { pb.hasRatedUNull() }
        inverterStandard = pb.inverterStandardSet.takeUnless { pb.hasInverterStandardNull() }
        sustainOpOvervoltLimit = pb.sustainOpOvervoltLimitSet.takeUnless { pb.hasSustainOpOvervoltLimitNull() }
        stopAtOverFreq = pb.stopAtOverFreqSet.takeUnless { pb.hasStopAtOverFreqNull() }
        stopAtUnderFreq = pb.stopAtUnderFreqSet.takeUnless { pb.hasStopAtUnderFreqNull() }
        invVoltWattRespMode = pb.invVoltWattRespModeSet.takeUnless { pb.hasInvVoltWattRespModeNull() }
        invWattRespV1 = pb.invWattRespV1Set.takeUnless { pb.hasInvWattRespV1Null() }
        invWattRespV2 = pb.invWattRespV2Set.takeUnless { pb.hasInvWattRespV2Null() }
        invWattRespV3 = pb.invWattRespV3Set.takeUnless { pb.hasInvWattRespV3Null() }
        invWattRespV4 = pb.invWattRespV4Set.takeUnless { pb.hasInvWattRespV4Null() }
        invWattRespPAtV1 = pb.invWattRespPAtV1Set.takeUnless { pb.hasInvWattRespPAtV1Null() }
        invWattRespPAtV2 = pb.invWattRespPAtV2Set.takeUnless { pb.hasInvWattRespPAtV2Null() }
        invWattRespPAtV3 = pb.invWattRespPAtV3Set.takeUnless { pb.hasInvWattRespPAtV3Null() }
        invWattRespPAtV4 = pb.invWattRespPAtV4Set.takeUnless { pb.hasInvWattRespPAtV4Null() }
        invVoltVarRespMode = pb.invVoltVarRespModeSet.takeUnless { pb.hasInvVoltVarRespModeNull() }
        invVarRespV1 = pb.invVarRespV1Set.takeUnless { pb.hasInvVarRespV1Null() }
        invVarRespV2 = pb.invVarRespV2Set.takeUnless { pb.hasInvVarRespV2Null() }
        invVarRespV3 = pb.invVarRespV3Set.takeUnless { pb.hasInvVarRespV3Null() }
        invVarRespV4 = pb.invVarRespV4Set.takeUnless { pb.hasInvVarRespV4Null() }
        invVarRespQAtV1 = pb.invVarRespQAtV1Set.takeUnless { pb.hasInvVarRespQAtV1Null() }
        invVarRespQAtV2 = pb.invVarRespQAtV2Set.takeUnless { pb.hasInvVarRespQAtV2Null() }
        invVarRespQAtV3 = pb.invVarRespQAtV3Set.takeUnless { pb.hasInvVarRespQAtV3Null() }
        invVarRespQAtV4 = pb.invVarRespQAtV4Set.takeUnless { pb.hasInvVarRespQAtV4Null() }
        invReactivePowerMode = pb.invReactivePowerModeSet.takeUnless { pb.hasInvReactivePowerModeNull() }
        invFixReactivePower = pb.invFixReactivePowerSet.takeUnless { pb.hasInvFixReactivePowerNull() }
        toCim(pb.rce, this, networkService)
    }

/**
 * Convert the protobuf [PBPowerElectronicsConnectionPhase] into its CIM counterpart.
 *
 * @param pb The protobuf [PBPowerElectronicsConnectionPhase] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [PowerElectronicsConnectionPhase].
 */
fun toCim(pb: PBPowerElectronicsConnectionPhase, networkService: NetworkService): PowerElectronicsConnectionPhase =
    PowerElectronicsConnectionPhase(pb.mRID()).apply {
        networkService.resolveOrDeferReference(Resolvers.powerElectronicsConnection(this), pb.powerElectronicsConnectionMRID)
        p = pb.pSet.takeUnless { pb.hasPNull() }
        phase = mapSinglePhaseKind.toCim(pb.phase)
        q = pb.qSet.takeUnless { pb.hasQNull() }
        toCim(pb.psr, this, networkService)
    }

/**
 * Convert the protobuf [PBPowerTransformer] into its CIM counterpart.
 *
 * @param pb The protobuf [PBPowerTransformer] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [PowerTransformer].
 */
fun toCim(pb: PBPowerTransformer, networkService: NetworkService): PowerTransformer =
    PowerTransformer(pb.mRID()).apply {
        pb.powerTransformerEndMRIDsList.forEach { endMRID ->
            networkService.resolveOrDeferReference(Resolvers.ends(this), endMRID)
        }
        vectorGroup = mapVectorGroup.toCim(pb.vectorGroup)
        transformerUtilisation = pb.transformerUtilisationSet.takeUnless { pb.hasTransformerUtilisationNull() }
        constructionKind = mapTransformerConstructionKind.toCim(pb.constructionKind)
        function = mapTransformerFunctionKind.toCim(pb.function)
        networkService.resolveOrDeferReference(Resolvers.assetInfo(this), pb.assetInfoMRID())
        toCim(pb.ce, this, networkService)
    }

/**
 * Convert the protobuf [PBPowerTransformerEnd] into its CIM counterpart.
 *
 * @param pb The protobuf [PBPowerTransformerEnd] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [PowerTransformerEnd].
 */
fun toCim(pb: PBPowerTransformerEnd, networkService: NetworkService): PowerTransformerEnd =
    PowerTransformerEnd(pb.mRID()).apply {
        networkService.resolveOrDeferReference(Resolvers.powerTransformer(this), pb.powerTransformerMRID)
        ratedU = pb.ratedUSet.takeUnless { pb.hasRatedUNull() }
        r = pb.rSet.takeUnless { pb.hasRNull() }
        r0 = pb.r0Set.takeUnless { pb.hasR0Null() }
        x = pb.xSet.takeUnless { pb.hasXNull() }
        x0 = pb.x0Set.takeUnless { pb.hasX0Null() }
        connectionKind = mapWindingConnection.toCim(pb.connectionKind)
        b = pb.bSet.takeUnless { pb.hasBNull() }
        b0 = pb.b0Set.takeUnless { pb.hasB0Null() }
        g = pb.gSet.takeUnless { pb.hasGNull() }
        g0 = pb.g0Set.takeUnless { pb.hasG0Null() }
        phaseAngleClock = pb.phaseAngleClockSet.takeUnless { pb.hasPhaseAngleClockNull() }

        pb.ratingsList.forEach {
            addRating(toCim(it))
        }
        toCim(pb.te, this, networkService)
    }

/**
 * Convert the protobuf [PBProtectedSwitch] into its CIM counterpart.
 *
 * @param pb The protobuf [PBProtectedSwitch] to convert.
 * @param cim The CIM [ProtectedSwitch] to populate.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [ProtectedSwitch].
 */
fun toCim(pb: PBProtectedSwitch, cim: ProtectedSwitch, networkService: NetworkService): ProtectedSwitch =
    cim.apply {
        pb.relayFunctionMRIDsList.forEach { relayFunctionMRID ->
            networkService.resolveOrDeferReference(Resolvers.relayFunctions(this), relayFunctionMRID)
        }
        breakingCapacity = pb.breakingCapacitySet.takeUnless { pb.hasBreakingCapacityNull() }
        toCim(pb.sw, this, networkService)
    }

/**
 * Convert the protobuf [PBRatioTapChanger] into its CIM counterpart.
 *
 * @param pb The protobuf [PBRatioTapChanger] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [RatioTapChanger].
 */
fun toCim(pb: PBRatioTapChanger, networkService: NetworkService): RatioTapChanger =
    RatioTapChanger(pb.mRID()).apply {
        networkService.resolveOrDeferReference(Resolvers.transformerEnd(this), pb.transformerEndMRID)
        stepVoltageIncrement = pb.stepVoltageIncrementSet.takeUnless { pb.hasStepVoltageIncrementNull() }
        toCim(pb.tc, this, networkService)
    }

/**
 * Convert the protobuf [PBReactiveCapabilityCurve] into its CIM counterpart.
 *
 * @param pb The protobuf [PBReactiveCapabilityCurve] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [ReactiveCapabilityCurve].
 */
fun toCim(pb: PBReactiveCapabilityCurve, networkService: NetworkService): ReactiveCapabilityCurve =
    ReactiveCapabilityCurve(pb.mRID()).apply {
        toCim(pb.c, this, networkService)
    }

/**
 * Convert the protobuf [PBRecloser] into its CIM counterpart.
 *
 * @param pb The protobuf [PBRecloser] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [Recloser].
 */
fun toCim(pb: PBRecloser, networkService: NetworkService): Recloser =
    Recloser(pb.mRID()).apply {
        toCim(pb.sw, this, networkService)
    }

/**
 * Convert the protobuf [PBRegulatingCondEq] into its CIM counterpart.
 *
 * @param pb The protobuf [PBRegulatingCondEq] to convert.
 * @param cim The CIM [RegulatingCondEq] to populate.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [RegulatingCondEq].
 */
fun toCim(pb: PBRegulatingCondEq, cim: RegulatingCondEq, networkService: NetworkService): RegulatingCondEq =
    cim.apply {
        controlEnabled = pb.controlEnabledSet.takeUnless { pb.hasControlEnabledNull() }
        networkService.resolveOrDeferReference(Resolvers.regulatingControl(this), pb.regulatingControlMRID)
        toCim(pb.ec, this, networkService)
    }

/**
 * Convert the protobuf [PBRegulatingControl] into its CIM counterpart.
 *
 * @param pb The protobuf [PBRegulatingControl] to convert.
 * @param cim The CIM [RegulatingControl] to populate.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [RegulatingControl].
 */
fun toCim(pb: PBRegulatingControl, cim: RegulatingControl, networkService: NetworkService): RegulatingControl =
    cim.apply {
        discrete = pb.discreteSet.takeUnless { pb.hasDiscreteNull() }
        mode = mapRegulatingControlModeKind.toCim(pb.mode)
        monitoredPhase = mapPhaseCode.toCim(pb.monitoredPhase)
        targetDeadband = pb.targetDeadbandSet.takeUnless { pb.hasTargetDeadbandNull() }
        targetValue = pb.targetValueSet.takeUnless { pb.hasTargetValueNull() }
        enabled = pb.enabledSet.takeUnless { pb.hasEnabledNull() }
        maxAllowedTargetValue = pb.maxAllowedTargetValueSet.takeUnless { pb.hasMaxAllowedTargetValueNull() }
        minAllowedTargetValue = pb.minAllowedTargetValueSet.takeUnless { pb.hasMinAllowedTargetValueNull() }
        ratedCurrent = pb.ratedCurrentSet.takeUnless { pb.hasRatedCurrentNull() }
        networkService.resolveOrDeferReference(Resolvers.terminal(this), pb.terminalMRID)
        pb.regulatingCondEqMRIDsList.forEach {
            networkService.resolveOrDeferReference(Resolvers.regulatingCondEq(this), it)
        }
        ctPrimary = pb.ctPrimarySet.takeUnless { pb.hasCtPrimaryNull() }
        minTargetDeadband = pb.minTargetDeadbandSet.takeUnless { pb.hasMinTargetDeadbandNull() }

        toCim(pb.psr, this, networkService)
    }

/**
 * Convert the protobuf [PBRotatingMachine] into its CIM counterpart.
 *
 * @param pb The protobuf [PBRotatingMachine] to convert.
 * @param cim The CIM [RotatingMachine] to populate.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [RotatingMachine].
 */
fun toCim(pb: PBRotatingMachine, cim: RotatingMachine, networkService: NetworkService): RotatingMachine =
    cim.apply {
        ratedPowerFactor = pb.ratedPowerFactorSet.takeUnless { pb.hasRatedPowerFactorNull() }
        ratedS = pb.ratedSSet.takeUnless { pb.hasRatedSNull() }
        ratedU = pb.ratedUSet.takeUnless { pb.hasRatedUNull() }
        p = pb.pSet.takeUnless { pb.hasPNull() }
        q = pb.qSet.takeUnless { pb.hasQNull() }
        toCim(pb.rce, this, networkService)
    }

/**
 * Convert the protobuf [PBSeriesCompensator] into its CIM counterpart.
 *
 * @param pb The protobuf [PBSeriesCompensator] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [SeriesCompensator].
 */
fun toCim(pb: PBSeriesCompensator, networkService: NetworkService): SeriesCompensator =
    SeriesCompensator(pb.mRID()).apply {
        r = pb.rSet.takeUnless { pb.hasRNull() }
        r0 = pb.r0Set.takeUnless { pb.hasR0Null() }
        x = pb.xSet.takeUnless { pb.hasXNull() }
        x0 = pb.x0Set.takeUnless { pb.hasX0Null() }
        varistorRatedCurrent = pb.varistorRatedCurrentSet.takeUnless { pb.hasVaristorRatedCurrentNull() }
        varistorVoltageThreshold = pb.varistorVoltageThresholdSet.takeUnless { pb.hasVaristorVoltageThresholdNull() }
        toCim(pb.ce, this, networkService)
    }

/**
 * Convert the protobuf [PBShuntCompensator] into its CIM counterpart.
 *
 * @param pb The protobuf [PBShuntCompensator] to convert.
 * @param cim The CIM [ShuntCompensator] to populate.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [ShuntCompensator].
 */
fun toCim(pb: PBShuntCompensator, cim: ShuntCompensator, networkService: NetworkService): ShuntCompensator =
    cim.apply {
        networkService.resolveOrDeferReference(Resolvers.assetInfo(this), pb.assetInfoMRID())
        sections = pb.sectionsSet.takeUnless { pb.hasSectionsNull() }
        grounded = pb.groundedSet.takeUnless { pb.hasGroundedNull() }
        nomU = pb.nomUSet.takeUnless { pb.hasNomUNull() }
        phaseConnection = mapPhaseShuntConnectionKind.toCim(pb.phaseConnection)
        toCim(pb.rce, this, networkService)
    }

/**
 * Convert the protobuf [PBStaticVarCompensator] into its CIM counterpart.
 *
 * @param pb The protobuf [PBStaticVarCompensator] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [StaticVarCompensator].
 */
fun toCim(pb: PBStaticVarCompensator, networkService: NetworkService): StaticVarCompensator =
    StaticVarCompensator(pb.mRID()).apply {
        capacitiveRating = pb.capacitiveRatingSet.takeUnless { pb.hasCapacitiveRatingNull() }
        inductiveRating = pb.inductiveRatingSet.takeUnless { pb.hasInductiveRatingNull() }
        q = pb.qSet.takeUnless { pb.hasQNull() }
        pb.svcControlMode?.also { svcControlMode = mapSVCControlMode.toCim(it) }
        voltageSetPoint = pb.voltageSetPointSet.takeUnless { pb.hasVoltageSetPointNull() }
        toCim(pb.rce, this, networkService)
    }

/**
 * Convert the protobuf [PBSwitch] into its CIM counterpart.
 *
 * @param pb The protobuf [PBSwitch] to convert.
 * @param cim The CIM [Switch] to populate.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [Switch].
 */
fun toCim(pb: PBSwitch, cim: Switch, networkService: NetworkService): Switch =
    cim.apply {
        networkService.resolveOrDeferReference(Resolvers.assetInfo(this), pb.assetInfoMRID())
        ratedCurrent = pb.ratedCurrentSet.takeUnless { pb.hasRatedCurrentNull() }
        setNormallyOpen(pb.normalOpen)
        setOpen(pb.open)
        // when unganged support is added to protobuf
        // normalOpen = pb.normalOpen
        // open = pb.open
        toCim(pb.ce, this, networkService)
    }

/**
 * Convert the protobuf [PBSynchronousMachine] into its CIM counterpart.
 *
 * @param pb The protobuf [PBSynchronousMachine] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [SynchronousMachine].
 */
fun toCim(pb: PBSynchronousMachine, networkService: NetworkService): SynchronousMachine =
    SynchronousMachine(pb.mRID()).apply {
        pb.reactiveCapabilityCurveMRIDsList.forEach { reactiveCapabilityCurveMRID ->
            networkService.resolveOrDeferReference(Resolvers.reactiveCapabilityCurve(this), reactiveCapabilityCurveMRID)
        }
        baseQ = pb.baseQSet.takeUnless { pb.hasBaseQNull() }
        condenserP = pb.condenserPSet.takeUnless { pb.hasCondenserPNull() }
        earthing = pb.earthingSet.takeUnless { pb.hasEarthingNull() }
        earthingStarPointR = pb.earthingStarPointRSet.takeUnless { pb.hasEarthingStarPointRNull() }
        earthingStarPointX = pb.earthingStarPointXSet.takeUnless { pb.hasEarthingStarPointXNull() }
        ikk = pb.ikkSet.takeUnless { pb.hasIkkNull() }
        maxQ = pb.maxQSet.takeUnless { pb.hasMaxQNull() }
        maxU = pb.maxUSet.takeUnless { pb.hasMaxUNull() }
        minQ = pb.minQSet.takeUnless { pb.hasMinQNull() }
        minU = pb.minUSet.takeUnless { pb.hasMinUNull() }
        mu = pb.muSet.takeUnless { pb.hasMuNull() }
        r = pb.rSet.takeUnless { pb.hasRNull() }
        r0 = pb.r0Set.takeUnless { pb.hasR0Null() }
        r2 = pb.r2Set.takeUnless { pb.hasR2Null() }
        satDirectSubtransX = pb.satDirectSubtransXSet.takeUnless { pb.hasSatDirectSubtransXNull() }
        satDirectSyncX = pb.satDirectSyncXSet.takeUnless { pb.hasSatDirectSyncXNull() }
        satDirectTransX = pb.satDirectTransXSet.takeUnless { pb.hasSatDirectTransXNull() }
        x0 = pb.x0Set.takeUnless { pb.hasX0Null() }
        x2 = pb.x2Set.takeUnless { pb.hasX2Null() }

        type = mapSynchronousMachineKind.toCim(pb.type)
        operatingMode = mapSynchronousMachineKind.toCim(pb.operatingMode)
        toCim(pb.rm, this, networkService)
    }

/**
 * Convert the protobuf [PBTapChanger] into its CIM counterpart.
 *
 * @param pb The protobuf [PBTapChanger] to convert.
 * @param cim The CIM [TapChanger] to populate.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [TapChanger].
 */
fun toCim(pb: PBTapChanger, cim: TapChanger, networkService: NetworkService): TapChanger =
    cim.apply {
        highStep = pb.highStepSet.takeUnless { pb.hasHighStepNull() }
        lowStep = pb.lowStepSet.takeUnless { pb.hasLowStepNull() }
        step = pb.stepSet.takeUnless { pb.hasStepNull() }
        neutralStep = pb.neutralStepSet.takeUnless { pb.hasNeutralStepNull() }
        neutralU = pb.neutralUSet.takeUnless { pb.hasNeutralUNull() }
        normalStep = pb.normalStepSet.takeUnless { pb.hasNormalStepNull() }
        controlEnabled = pb.controlEnabledSet.takeUnless { pb.hasControlEnabledNull() }
        networkService.resolveOrDeferReference(Resolvers.tapChangerControl(this), pb.tapChangerControlMRID)
        toCim(pb.psr, this, networkService)
    }

/**
 * Convert the protobuf [PBTapChangerControl] into its CIM counterpart.
 *
 * @param pb The protobuf [PBTapChangerControl] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [TapChangerControl].
 */
fun toCim(pb: PBTapChangerControl, networkService: NetworkService): TapChangerControl =
    TapChangerControl(pb.mRID()).apply {
        limitVoltage = pb.limitVoltageSet.takeUnless { pb.hasLimitVoltageNull() }
        lineDropCompensation = pb.lineDropCompensationSet.takeUnless { pb.hasLineDropCompensationNull() }
        lineDropR = pb.lineDropRSet.takeUnless { pb.hasLineDropRNull() }
        lineDropX = pb.lineDropXSet.takeUnless { pb.hasLineDropXNull() }
        reverseLineDropR = pb.reverseLineDropRSet.takeUnless { pb.hasReverseLineDropRNull() }
        reverseLineDropX = pb.reverseLineDropXSet.takeUnless { pb.hasReverseLineDropXNull() }

        forwardLDCBlocking = pb.forwardLDCBlockingSet.takeUnless { pb.hasForwardLDCBlockingNull() }

        timeDelay = pb.timeDelaySet.takeUnless { pb.hasTimeDelayNull() }

        coGenerationEnabled = pb.coGenerationEnabledSet.takeUnless { pb.hasCoGenerationEnabledNull() }

        toCim(pb.rc, this, networkService)
    }

/**
 * Convert the protobuf [PBTransformerEnd] into its CIM counterpart.
 *
 * @param pb The protobuf [PBTransformerEnd] to convert.
 * @param cim The CIM [TransformerEnd] to populate.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [TransformerEnd].
 */
fun toCim(pb: PBTransformerEnd, cim: TransformerEnd, networkService: NetworkService): TransformerEnd =
    cim.apply {
        networkService.resolveOrDeferReference(Resolvers.terminal(this), pb.terminalMRID)
        networkService.resolveOrDeferReference(Resolvers.baseVoltage(this), pb.baseVoltageMRID)
        networkService.resolveOrDeferReference(Resolvers.ratioTapChanger(this), pb.ratioTapChangerMRID)
        networkService.resolveOrDeferReference(Resolvers.starImpedance(this), pb.starImpedanceMRID)
        endNumber = pb.endNumber
        grounded = pb.groundedSet.takeUnless { pb.hasGroundedNull() }
        rGround = pb.rGroundSet.takeUnless { pb.hasRGroundNull() }
        xGround = pb.xGroundSet.takeUnless { pb.hasXGroundNull() }
        toCim(pb.io, this, networkService)
    }

/**
 * Convert the protobuf [PBTransformerEndRatedS] into its CIM counterpart.
 *
 * @param pb The protobuf [PBTransformerEndRatedS] to convert.
 * @return The converted [pb] as a CIM [TransformerEndRatedS].
 */
fun toCim(pb: PBTransformerEndRatedS): TransformerEndRatedS =
    TransformerEndRatedS(mapTransformerCoolingType.toCim(pb.coolingType), pb.ratedS)

/**
 * Convert the protobuf [PBTransformerStarImpedance] into its CIM counterpart.
 *
 * @param pb The protobuf [PBTransformerStarImpedance] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [TransformerStarImpedance].
 */
fun toCim(pb: PBTransformerStarImpedance, networkService: NetworkService): TransformerStarImpedance =
    TransformerStarImpedance(pb.mRID()).apply {
        networkService.resolveOrDeferReference(Resolvers.transformerEndInfo(this), pb.transformerEndInfoMRID)
        r = pb.rSet.takeUnless { pb.hasRNull() }
        r0 = pb.r0Set.takeUnless { pb.hasR0Null() }
        x = pb.xSet.takeUnless { pb.hasXNull() }
        x0 = pb.x0Set.takeUnless { pb.hasXNull() }
        toCim(pb.io, this, networkService)
    }

/**
 * An extension to add a converted copy of the protobuf [PBAcLineSegment] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBAcLineSegment): AcLineSegment? = tryAddOrNull(toCim(pb, this))

/**
 * An extension to add a converted copy of the protobuf [PBBreaker] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBBreaker): Breaker? = tryAddOrNull(toCim(pb, this))

/**
 * An extension to add a converted copy of the protobuf [PBBusbarSection] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBBusbarSection): BusbarSection? = tryAddOrNull(toCim(pb, this))

/**
 * An extension to add a converted copy of the protobuf [PBClamp] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBClamp): Clamp? = tryAddOrNull(toCim(pb, this))

/**
 * An extension to add a converted copy of the protobuf [PBCut] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBCut): Cut? = tryAddOrNull(toCim(pb, this))

/**
 * An extension to add a converted copy of the protobuf [PBDisconnector] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBDisconnector): Disconnector? = tryAddOrNull(toCim(pb, this))

/**
 * An extension to add a converted copy of the protobuf [PBEnergyConsumer] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBEnergyConsumer): EnergyConsumer? = tryAddOrNull(toCim(pb, this))

/**
 * An extension to add a converted copy of the protobuf [PBEnergyConsumerPhase] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBEnergyConsumerPhase): EnergyConsumerPhase? = tryAddOrNull(toCim(pb, this))

/**
 * An extension to add a converted copy of the protobuf [PBEnergySource] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBEnergySource): EnergySource? = tryAddOrNull(toCim(pb, this))

/**
 * An extension to add a converted copy of the protobuf [PBEnergySourcePhase] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBEnergySourcePhase): EnergySourcePhase? = tryAddOrNull(toCim(pb, this))

/**
 * An extension to add a converted copy of the protobuf [PBFuse] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBFuse): Fuse? = tryAddOrNull(toCim(pb, this))

/**
 * An extension to add a converted copy of the protobuf [PBGround] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBGround): Ground? = tryAddOrNull(toCim(pb, this))

/**
 * An extension to add a converted copy of the protobuf [PBGroundDisconnector] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBGroundDisconnector): GroundDisconnector? = tryAddOrNull(toCim(pb, this))

/**
 * An extension to add a converted copy of the protobuf [PBGroundingImpedance] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBGroundingImpedance): GroundingImpedance? = tryAddOrNull(toCim(pb, this))

/**
 * An extension to add a converted copy of the protobuf [PBJumper] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBJumper): Jumper? = tryAddOrNull(toCim(pb, this))

/**
 * An extension to add a converted copy of the protobuf [PBJunction] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBJunction): Junction? = tryAddOrNull(toCim(pb, this))

/**
 * An extension to add a converted copy of the protobuf [PBLinearShuntCompensator] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBLinearShuntCompensator): LinearShuntCompensator? = tryAddOrNull(toCim(pb, this))

/**
 * An extension to add a converted copy of the protobuf [PBLoadBreakSwitch] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBLoadBreakSwitch): LoadBreakSwitch? = tryAddOrNull(toCim(pb, this))

/**
 * An extension to add a converted copy of the protobuf [PBPerLengthPhaseImpedance] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBPerLengthPhaseImpedance): PerLengthPhaseImpedance? = tryAddOrNull(toCim(pb, this))

/**
 * An extension to add a converted copy of the protobuf [PBPerLengthSequenceImpedance] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBPerLengthSequenceImpedance): PerLengthSequenceImpedance? = tryAddOrNull(toCim(pb, this))

/**
 * An extension to add a converted copy of the protobuf [PBPetersenCoil] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBPetersenCoil): PetersenCoil? = tryAddOrNull(toCim(pb, this))

/**
 * An extension to add a converted copy of the protobuf [PBPowerElectronicsConnection] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBPowerElectronicsConnection): PowerElectronicsConnection? = tryAddOrNull(toCim(pb, this))

/**
 * An extension to add a converted copy of the protobuf [PBPowerElectronicsConnectionPhase] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBPowerElectronicsConnectionPhase): PowerElectronicsConnectionPhase? = tryAddOrNull(toCim(pb, this))

/**
 * An extension to add a converted copy of the protobuf [PBPowerTransformer] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBPowerTransformer): PowerTransformer? = tryAddOrNull(toCim(pb, this))

/**
 * An extension to add a converted copy of the protobuf [PBPowerTransformerEnd] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBPowerTransformerEnd): PowerTransformerEnd? = tryAddOrNull(toCim(pb, this))

/**
 * An extension to add a converted copy of the protobuf [PBRatioTapChanger] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBRatioTapChanger): RatioTapChanger? = tryAddOrNull(toCim(pb, this))

/**
 * An extension to add a converted copy of the protobuf [PBReactiveCapabilityCurve] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBReactiveCapabilityCurve): ReactiveCapabilityCurve? = tryAddOrNull(toCim(pb, this))

/**
 * An extension to add a converted copy of the protobuf [PBRecloser] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBRecloser): Recloser? = tryAddOrNull(toCim(pb, this))

/**
 * An extension to add a converted copy of the protobuf [PBSeriesCompensator] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBSeriesCompensator): SeriesCompensator? = tryAddOrNull(toCim(pb, this))

/**
 * An extension to add a converted copy of the protobuf [PBStaticVarCompensator] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBStaticVarCompensator): StaticVarCompensator? = tryAddOrNull(toCim(pb, this))

/**
 * An extension to add a converted copy of the protobuf [PBSynchronousMachine] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBSynchronousMachine): SynchronousMachine? = tryAddOrNull(toCim(pb, this))

/**
 * An extension to add a converted copy of the protobuf [PBTapChangerControl] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBTapChangerControl): TapChangerControl? = tryAddOrNull(toCim(pb, this))

/**
 * An extension to add a converted copy of the protobuf [PBTransformerStarImpedance] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBTransformerStarImpedance): TransformerStarImpedance? = tryAddOrNull(toCim(pb, this))

// ################################
// # IEC61970 InfIEC61970 Feeder #
// ################################

/**
 * Convert the protobuf [PBCircuit] into its CIM counterpart.
 *
 * @param pb The protobuf [PBCircuit] to convert.
 * @param networkService The [NetworkService] the converted CIM object will be added too.
 * @return The converted [pb] as a CIM [Circuit].
 */
fun toCim(pb: PBCircuit, networkService: NetworkService): Circuit =
    Circuit(pb.mRID()).apply {
        networkService.resolveOrDeferReference(Resolvers.loop(this), pb.loopMRID)
        pb.endTerminalMRIDsList.forEach { endTerminalMRID ->
            networkService.resolveOrDeferReference(Resolvers.endTerminal(this), endTerminalMRID)
        }
        pb.endSubstationMRIDsList.forEach { endSubstationMRID ->
            networkService.resolveOrDeferReference(Resolvers.endSubstation(this), endSubstationMRID)
        }

        toCim(pb.l, this, networkService)
    }

/**
 * An extension to add a converted copy of the protobuf [PBCircuit] to the [NetworkService].
 */
fun NetworkService.addFromPb(pb: PBCircuit): Circuit? = tryAddOrNull(toCim(pb, this))

// #################################
// # Class for Java friendly usage #
// #################################

/**
 * A helper class for Java friendly convertion from protobuf objects to their CIM counterparts.
 *
 * @property networkService The [NetworkService] all converted objects should be added to.
 */
class NetworkProtoToCim(val networkService: NetworkService) : BaseProtoToCim() {

    // ##################################
    // # Extensions IEC61968 Asset Info #
    // ##################################

    /**
     * Add a converted copy of the protobuf [PBRelayInfo] to the [NetworkService].
     *
     * @param pb The [PBRelayInfo] to convert.
     * @return The converted [RelayInfo]
     */
    fun addFromPb(pb: PBRelayInfo): RelayInfo? = networkService.addFromPb(pb)

    // ################################
    // # Extensions IEC61968 Metering #
    // ################################

    /**
     * Add a converted copy of the protobuf [PBPanDemandResponseFunction] to the [NetworkService].
     *
     * @param pb The [PBPanDemandResponseFunction] to convert.
     * @return The converted [PanDemandResponseFunction]
     */
    fun addFromPb(pb: PBPanDemandResponseFunction): PanDemandResponseFunction? = networkService.addFromPb(pb)

    // #################################
    // # Extensions IEC61970 Base Core #
    // #################################

    /**
     * Add a converted copy of the protobuf [PBSite] to the [NetworkService].
     *
     * @param pb The [PBSite] to convert.
     * @return The converted [Site]
     */
    fun addFromPb(pb: PBSite): Site? = networkService.addFromPb(pb)

    // ###################################
    // # Extensions IEC61970 Base Feeder #
    // ###################################

    /**
     * Add a converted copy of the protobuf [PBLoop] to the [NetworkService].
     *
     * @param pb The [PBLoop] to convert.
     * @return The converted [Loop]
     */
    fun addFromPb(pb: PBLoop): Loop? = networkService.addFromPb(pb)

    /**
     * Add a converted copy of the protobuf [PBLvFeeder] to the [NetworkService].
     *
     * @param pb The [PBLvFeeder] to convert.
     * @return The converted [LvFeeder]
     */
    fun addFromPb(pb: PBLvFeeder): LvFeeder? = networkService.addFromPb(pb)

    // ##################################################
    // # Extensions IEC61970 Base Generation Production #
    // ##################################################

    /**
     * Add a converted copy of the protobuf [PBEvChargingUnit] to the [NetworkService].
     *
     * @param pb The [PBEvChargingUnit] to convert.
     * @return The converted [EvChargingUnit]
     */
    fun addFromPb(pb: PBEvChargingUnit): EvChargingUnit? = networkService.addFromPb(pb)

    // #######################################
    // # Extensions IEC61970 Base Protection #
    // #######################################

    /**
     * Add a converted copy of the protobuf [PBDirectionalCurrentRelay] to the [NetworkService].
     *
     * @param pb The [PBDirectionalCurrentRelay] to convert.
     * @return The converted [DirectionalCurrentRelay]
     */
    fun addFromPb(pb: PBDirectionalCurrentRelay): DirectionalCurrentRelay? = networkService.addFromPb(pb)

    /**
     * Add a converted copy of the protobuf [PBDistanceRelay] to the [NetworkService].
     *
     * @param pb The [PBDistanceRelay] to convert.
     * @return The converted [DistanceRelay]
     */
    fun addFromPb(pb: PBDistanceRelay): DistanceRelay? = networkService.addFromPb(pb)

    /**
     * Add a converted copy of the protobuf [PBProtectionRelayScheme] to the [NetworkService].
     *
     * @param pb The [PBProtectionRelayScheme] to convert.
     * @return The converted [ProtectionRelayScheme]
     */
    fun addFromPb(pb: PBProtectionRelayScheme): ProtectionRelayScheme? = networkService.addFromPb(pb)

    /**
     * Add a converted copy of the protobuf [PBProtectionRelaySystem] to the [NetworkService].
     *
     * @param pb The [PBProtectionRelaySystem] to convert.
     * @return The converted [ProtectionRelaySystem]
     */
    fun addFromPb(pb: PBProtectionRelaySystem): ProtectionRelaySystem? = networkService.addFromPb(pb)

    /**
     * Add a converted copy of the protobuf [PBVoltageRelay] to the [NetworkService].
     *
     * @param pb The [PBVoltageRelay] to convert.
     * @return The converted [VoltageRelay]
     */
    fun addFromPb(pb: PBVoltageRelay): VoltageRelay? = networkService.addFromPb(pb)

    // ##################################
    // # Extensions IEC61970 Base Wires #
    // ##################################

    /**
     * Add a converted copy of the protobuf [PBBatteryControl] to the [NetworkService].
     *
     * @param pb The [PBBatteryControl] to convert.
     * @return The converted [BatteryControl]
     */
    fun addFromPb(pb: PBBatteryControl): BatteryControl? = networkService.addFromPb(pb)

    // #######################
    // # IEC61968 Asset Info #
    // #######################

    /**
     * Add a converted copy of the protobuf [PBCableInfo] to the [NetworkService].
     *
     * @param pb The [PBCableInfo] to convert.
     * @return The converted [CableInfo]
     */
    fun addFromPb(pb: PBCableInfo): CableInfo? = networkService.addFromPb(pb)

    /**
     * Add a converted copy of the protobuf [PBNoLoadTest] to the [NetworkService].
     *
     * @param pb The [PBNoLoadTest] to convert.
     * @return The converted [NoLoadTest]
     */
    fun addFromPb(pb: PBNoLoadTest): NoLoadTest? = networkService.addFromPb(pb)

    /**
     * Add a converted copy of the protobuf [PBOpenCircuitTest] to the [NetworkService].
     *
     * @param pb The [PBOpenCircuitTest] to convert.
     * @return The converted [OpenCircuitTest]
     */
    fun addFromPb(pb: PBOpenCircuitTest): OpenCircuitTest? = networkService.addFromPb(pb)

    /**
     * Add a converted copy of the protobuf [PBOverheadWireInfo] to the [NetworkService].
     *
     * @param pb The [PBOverheadWireInfo] to convert.
     * @return The converted [OverheadWireInfo]
     */
    fun addFromPb(pb: PBOverheadWireInfo): OverheadWireInfo? = networkService.addFromPb(pb)

    /**
     * Add a converted copy of the protobuf [PBPowerTransformerInfo] to the [NetworkService].
     *
     * @param pb The [PBPowerTransformerInfo] to convert.
     * @return The converted [PowerTransformerInfo]
     */
    fun addFromPb(pb: PBPowerTransformerInfo): PowerTransformerInfo? = networkService.addFromPb(pb)

    /**
     * Add a converted copy of the protobuf [PBShortCircuitTest] to the [NetworkService].
     *
     * @param pb The [PBShortCircuitTest] to convert.
     * @return The converted [ShortCircuitTest]
     */
    fun addFromPb(pb: PBShortCircuitTest): ShortCircuitTest? = networkService.addFromPb(pb)

    /**
     * Add a converted copy of the protobuf [PBShuntCompensatorInfo] to the [NetworkService].
     *
     * @param pb The [PBShuntCompensatorInfo] to convert.
     * @return The converted [ShuntCompensatorInfo]
     */
    fun addFromPb(pb: PBShuntCompensatorInfo): ShuntCompensatorInfo? = networkService.addFromPb(pb)

    /**
     * Add a converted copy of the protobuf [PBSwitchInfo] to the [NetworkService].
     *
     * @param pb The [PBSwitchInfo] to convert.
     * @return The converted [SwitchInfo]
     */
    fun addFromPb(pb: PBSwitchInfo): SwitchInfo? = networkService.addFromPb(pb)

    /**
     * Add a converted copy of the protobuf [PBTransformerEndInfo] to the [NetworkService].
     *
     * @param pb The [PBTransformerEndInfo] to convert.
     * @return The converted [TransformerEndInfo]
     */
    fun addFromPb(pb: PBTransformerEndInfo): TransformerEndInfo? = networkService.addFromPb(pb)

    /**
     * Add a converted copy of the protobuf [PBTransformerTankInfo] to the [NetworkService].
     *
     * @param pb The [PBTransformerTankInfo] to convert.
     * @return The converted [TransformerTankInfo]
     */
    fun addFromPb(pb: PBTransformerTankInfo): TransformerTankInfo? = networkService.addFromPb(pb)

    // ###################
    // # IEC61968 Assets #
    // ###################

    /**
     * Add a converted copy of the protobuf [PBAssetOwner] to the [NetworkService].
     *
     * @param pb The [PBAssetOwner] to convert.
     * @return The converted [AssetOwner]
     */
    fun addFromPb(pb: PBAssetOwner): AssetOwner? = networkService.addFromPb(pb)

    /**
     * Add a converted copy of the protobuf [PBStreetlight] to the [NetworkService].
     *
     * @param pb The [PBStreetlight] to convert.
     * @return The converted [Streetlight]
     */
    fun addFromPb(pb: PBStreetlight): Streetlight? = networkService.addFromPb(pb)

    // ###################
    // # IEC61968 Common #
    // ###################

    /**
     * Add a converted copy of the protobuf [PBOrganisation] to the [NetworkService].
     *
     * @param pb The [PBOrganisation] to convert.
     * @return The converted [Organisation]
     */
    fun addFromPb(pb: PBOrganisation): Organisation? = networkService.addFromPb(pb)

    /**
     * Add a converted copy of the protobuf [PBLocation] to the [NetworkService].
     *
     * @param pb The [PBLocation] to convert.
     * @return The converted [Location]
     */
    fun addFromPb(pb: PBLocation): Location? = networkService.addFromPb(pb)

    // #####################################
    // # IEC61968 infIEC61968 InfAssetInfo #
    // #####################################

    /**
     * Add a converted copy of the protobuf [PBCurrentTransformerInfo] to the [NetworkService].
     *
     * @param pb The [PBCurrentTransformerInfo] to convert.
     * @return The converted [CurrentTransformerInfo]
     */
    fun addFromPb(pb: PBCurrentTransformerInfo): CurrentTransformerInfo? = networkService.addFromPb(pb)

    /**
     * Add a converted copy of the protobuf [PBPotentialTransformerInfo] to the [NetworkService].
     *
     * @param pb The [PBPotentialTransformerInfo] to convert.
     * @return The converted [PotentialTransformerInfo]
     */
    fun addFromPb(pb: PBPotentialTransformerInfo): PotentialTransformerInfo? = networkService.addFromPb(pb)

    // ##################################
    // # IEC61968 infIEC61968 InfAssets #
    // ##################################

    /**
     * Add a converted copy of the protobuf [PBPole] to the [NetworkService].
     *
     * @param pb The [PBPole] to convert.
     * @return The converted [Pole]
     */
    fun addFromPb(pb: PBPole): Pole? = networkService.addFromPb(pb)

    // #####################
    // # IEC61968 Metering #
    // #####################

    /**
     * Add a converted copy of the protobuf [PBMeter] to the [NetworkService].
     *
     * @param pb The [PBMeter] to convert.
     * @return The converted [Meter]
     */
    fun addFromPb(pb: PBMeter): Meter? = networkService.addFromPb(pb)

    /**
     * Add a converted copy of the protobuf [PBUsagePoint] to the [NetworkService].
     *
     * @param pb The [PBUsagePoint] to convert.
     * @return The converted [UsagePoint]
     */
    fun addFromPb(pb: PBUsagePoint): UsagePoint? = networkService.addFromPb(pb)

    // #######################
    // # IEC61968 Operations #
    // #######################

    /**
     * Add a converted copy of the protobuf [PBOperationalRestriction] to the [NetworkService].
     *
     * @param pb The [PBOperationalRestriction] to convert.
     * @return The converted [OperationalRestriction]
     */
    fun addFromPb(pb: PBOperationalRestriction): OperationalRestriction? = networkService.addFromPb(pb)

    // #####################################
    // # IEC61970 Base Auxiliary Equipment #
    // #####################################

    /**
     * Add a converted copy of the protobuf [PBCurrentTransformer] to the [NetworkService].
     *
     * @param pb The [PBCurrentTransformer] to convert.
     * @return The converted [CurrentTransformer]
     */
    fun addFromPb(pb: PBCurrentTransformer): CurrentTransformer? = networkService.addFromPb(pb)

    /**
     * Add a converted copy of the protobuf [PBFaultIndicator] to the [NetworkService].
     *
     * @param pb The [PBFaultIndicator] to convert.
     * @return The converted [FaultIndicator]
     */
    fun addFromPb(pb: PBFaultIndicator): FaultIndicator? = networkService.addFromPb(pb)

    /**
     * Add a converted copy of the protobuf [PBPotentialTransformer] to the [NetworkService].
     *
     * @param pb The [PBPotentialTransformer] to convert.
     * @return The converted [PotentialTransformer]
     */
    fun addFromPb(pb: PBPotentialTransformer): PotentialTransformer? = networkService.addFromPb(pb)

    // ######################
    // # IEC61970 Base Core #
    // ######################

    /**
     * Add a converted copy of the protobuf [PBBaseVoltage] to the [NetworkService].
     *
     * @param pb The [PBBaseVoltage] to convert.
     * @return The converted [BaseVoltage]
     */
    fun addFromPb(pb: PBBaseVoltage): BaseVoltage? = networkService.addFromPb(pb)

    /**
     * Add a converted copy of the protobuf [PBConnectivityNode] to the [NetworkService].
     *
     * @param pb The [PBConnectivityNode] to convert.
     * @return The converted [ConnectivityNode]
     */
    fun addFromPb(pb: PBConnectivityNode): ConnectivityNode? = networkService.addFromPb(pb)

    /**
     * Add a converted copy of the protobuf [PBFeeder] to the [NetworkService].
     *
     * @param pb The [PBFeeder] to convert.
     * @return The converted [Feeder]
     */
    fun addFromPb(pb: PBFeeder): Feeder? = networkService.addFromPb(pb)

    /**
     * Add a converted copy of the protobuf [PBGeographicalRegion] to the [NetworkService].
     *
     * @param pb The [PBGeographicalRegion] to convert.
     * @return The converted [GeographicalRegion]
     */
    fun addFromPb(pb: PBGeographicalRegion): GeographicalRegion? = networkService.addFromPb(pb)

    /**
     * Add a converted copy of the protobuf [PBNameType] to the [NetworkService].
     *
     * @param pb The [PBNameType] to convert.
     * @return The converted [NameType]
     */
    fun addFromPb(pb: PBNameType): NameType = networkService.addFromPb(pb)

    /**
     * Add a converted copy of the protobuf [PBSubGeographicalRegion] to the [NetworkService].
     *
     * @param pb The [PBSubGeographicalRegion] to convert.
     * @return The converted [SubGeographicalRegion]
     */
    fun addFromPb(pb: PBSubGeographicalRegion): SubGeographicalRegion? = networkService.addFromPb(pb)

    /**
     * Add a converted copy of the protobuf [PBSubstation] to the [NetworkService].
     *
     * @param pb The [PBSubstation] to convert.
     * @return The converted [Substation]
     */
    fun addFromPb(pb: PBSubstation): Substation? = networkService.addFromPb(pb)

    /**
     * Add a converted copy of the protobuf [PBTerminal] to the [NetworkService].
     *
     * @param pb The [PBTerminal] to convert.
     * @return The converted [Terminal]
     */
    fun addFromPb(pb: PBTerminal): Terminal? = networkService.addFromPb(pb)

    // #############################
    // # IEC61970 Base Equivalents #
    // #############################

    /**
     * Add a converted copy of the protobuf [PBEquivalentBranch] to the [NetworkService].
     *
     * @param pb The [PBEquivalentBranch] to convert.
     * @return The converted [EquivalentBranch]
     */
    fun addFromPb(pb: PBEquivalentBranch): EquivalentBranch? = networkService.addFromPb(pb)

    // #######################################
    // # IEC61970 Base Generation Production #
    // #######################################

    /**
     * Add a converted copy of the protobuf [PBBatteryUnit] to the [NetworkService].
     *
     * @param pb The [PBBatteryUnit] to convert.
     * @return The converted [BatteryUnit]
     */
    fun addFromPb(pb: PBBatteryUnit): BatteryUnit? = networkService.addFromPb(pb)

    /**
     * Add a converted copy of the protobuf [PBPhotoVoltaicUnit] to the [NetworkService].
     *
     * @param pb The [PBPhotoVoltaicUnit] to convert.
     * @return The converted [PhotoVoltaicUnit]
     */
    fun addFromPb(pb: PBPhotoVoltaicUnit): PhotoVoltaicUnit? = networkService.addFromPb(pb)

    /**
     * Add a converted copy of the protobuf [PBPowerElectronicsWindUnit] to the [NetworkService].
     *
     * @param pb The [PBPowerElectronicsWindUnit] to convert.
     * @return The converted [PowerElectronicsWindUnit]
     */
    fun addFromPb(pb: PBPowerElectronicsWindUnit): PowerElectronicsWindUnit? = networkService.addFromPb(pb)

    // ######################
    // # IEC61970 Base Meas #
    // ######################

    /**
     * Add a converted copy of the protobuf [PBAccumulator] to the [NetworkService].
     *
     * @param pb The [PBAccumulator] to convert.
     * @return The converted [Accumulator]
     */
    fun addFromPb(pb: PBAccumulator): Accumulator? = networkService.addFromPb(pb)

    /**
     * Add a converted copy of the protobuf [PBAnalog] to the [NetworkService].
     *
     * @param pb The [PBAnalog] to convert.
     * @return The converted [Analog]
     */
    fun addFromPb(pb: PBAnalog): Analog? = networkService.addFromPb(pb)

    /**
     * Add a converted copy of the protobuf [PBControl] to the [NetworkService].
     *
     * @param pb The [PBControl] to convert.
     * @return The converted [Control]
     */
    fun addFromPb(pb: PBControl): Control? = networkService.addFromPb(pb)

    /**
     * Add a converted copy of the protobuf [PBDiscrete] to the [NetworkService].
     *
     * @param pb The [PBDiscrete] to convert.
     * @return The converted [Discrete]
     */
    fun addFromPb(pb: PBDiscrete): Discrete? = networkService.addFromPb(pb)

    // ############################
    // # IEC61970 Base Protection #
    // ############################

    /**
     * Add a converted copy of the protobuf [PBCurrentRelay] to the [NetworkService].
     *
     * @param pb The [PBCurrentRelay] to convert.
     * @return The converted [CurrentRelay]
     */
    fun addFromPb(pb: PBCurrentRelay): CurrentRelay? = networkService.addFromPb(pb)

    // #######################
    // # IEC61970 Base Scada #
    // #######################

    /**
     * Add a converted copy of the protobuf [PBRemoteControl] to the [NetworkService].
     *
     * @param pb The [PBRemoteControl] to convert.
     * @return The converted [RemoteControl]
     */
    fun addFromPb(pb: PBRemoteControl): RemoteControl? = networkService.addFromPb(pb)

    /**
     * Add a converted copy of the protobuf [PBRemoteSource] to the [NetworkService].
     *
     * @param pb The [PBRemoteSource] to convert.
     * @return The converted [RemoteSource]
     */
    fun addFromPb(pb: PBRemoteSource): RemoteSource? = networkService.addFromPb(pb)

    // #######################
    // # IEC61970 Base Wires #
    // #######################

    /**
     * Add a converted copy of the protobuf [PBAcLineSegment] to the [NetworkService].
     *
     * @param pb The [PBAcLineSegment] to convert.
     * @return The converted [AcLineSegment]
     */
    fun addFromPb(pb: PBAcLineSegment): AcLineSegment? = networkService.addFromPb(pb)

    /**
     * Add a converted copy of the protobuf [PBBreaker] to the [NetworkService].
     *
     * @param pb The [PBBreaker] to convert.
     * @return The converted [Breaker]
     */
    fun addFromPb(pb: PBBreaker): Breaker? = networkService.addFromPb(pb)

    /**
     * Add a converted copy of the protobuf [PBBusbarSection] to the [NetworkService].
     *
     * @param pb The [PBBusbarSection] to convert.
     * @return The converted [BusbarSection]
     */
    fun addFromPb(pb: PBBusbarSection): BusbarSection? = networkService.addFromPb(pb)

    /**
     * Add a converted copy of the protobuf [PBClamp] to the [NetworkService].
     *
     * @param pb The [PBClamp] to convert.
     * @return The converted [Clamp]
     */
    fun addFromPb(pb: PBClamp): Clamp? = networkService.addFromPb(pb)

    /**
     * Add a converted copy of the protobuf [PBCut] to the [NetworkService].
     *
     * @param pb The [PBCut] to convert.
     * @return The converted [Cut]
     */
    fun addFromPb(pb: PBCut): Cut? = networkService.addFromPb(pb)

    /**
     * Add a converted copy of the protobuf [PBDisconnector] to the [NetworkService].
     *
     * @param pb The [PBDisconnector] to convert.
     * @return The converted [Disconnector]
     */
    fun addFromPb(pb: PBDisconnector): Disconnector? = networkService.addFromPb(pb)

    /**
     * Add a converted copy of the protobuf [PBEnergyConsumer] to the [NetworkService].
     *
     * @param pb The [PBEnergyConsumer] to convert.
     * @return The converted [EnergyConsumer]
     */
    fun addFromPb(pb: PBEnergyConsumer): EnergyConsumer? = networkService.addFromPb(pb)

    /**
     * Add a converted copy of the protobuf [PBEnergyConsumerPhase] to the [NetworkService].
     *
     * @param pb The [PBEnergyConsumerPhase] to convert.
     * @return The converted [EnergyConsumerPhase]
     */
    fun addFromPb(pb: PBEnergyConsumerPhase): EnergyConsumerPhase? = networkService.addFromPb(pb)

    /**
     * Add a converted copy of the protobuf [PBEnergySource] to the [NetworkService].
     *
     * @param pb The [PBEnergySource] to convert.
     * @return The converted [EnergySource]
     */
    fun addFromPb(pb: PBEnergySource): EnergySource? = networkService.addFromPb(pb)

    /**
     * Add a converted copy of the protobuf [PBEnergySourcePhase] to the [NetworkService].
     *
     * @param pb The [PBEnergySourcePhase] to convert.
     * @return The converted [EnergySourcePhase]
     */
    fun addFromPb(pb: PBEnergySourcePhase): EnergySourcePhase? = networkService.addFromPb(pb)

    /**
     * Add a converted copy of the protobuf [PBFuse] to the [NetworkService].
     *
     * @param pb The [PBFuse] to convert.
     * @return The converted [Fuse]
     */
    fun addFromPb(pb: PBFuse): Fuse? = networkService.addFromPb(pb)

    /**
     * Add a converted copy of the protobuf [PBGround] to the [NetworkService].
     *
     * @param pb The [PBGround] to convert.
     * @return The converted [Ground]
     */
    fun addFromPb(pb: PBGround): Ground? = networkService.addFromPb(pb)

    /**
     * Add a converted copy of the protobuf [PBGroundDisconnector] to the [NetworkService].
     *
     * @param pb The [PBGroundDisconnector] to convert.
     * @return The converted [GroundDisconnector]
     */
    fun addFromPb(pb: PBGroundDisconnector): GroundDisconnector? = networkService.addFromPb(pb)

    /**
     * Add a converted copy of the protobuf [PBGroundingImpedance] to the [NetworkService].
     *
     * @param pb The [PBGroundingImpedance] to convert.
     * @return The converted [GroundingImpedance]
     */
    fun addFromPb(pb: PBGroundingImpedance): GroundingImpedance? = networkService.addFromPb(pb)

    /**
     * Add a converted copy of the protobuf [PBJumper] to the [NetworkService].
     *
     * @param pb The [PBJumper] to convert.
     * @return The converted [Jumper]
     */
    fun addFromPb(pb: PBJumper): Jumper? = networkService.addFromPb(pb)

    /**
     * Add a converted copy of the protobuf [PBJunction] to the [NetworkService].
     *
     * @param pb The [PBJunction] to convert.
     * @return The converted [Junction]
     */
    fun addFromPb(pb: PBJunction): Junction? = networkService.addFromPb(pb)

    /**
     * Add a converted copy of the protobuf [PBLinearShuntCompensator] to the [NetworkService].
     *
     * @param pb The [PBLinearShuntCompensator] to convert.
     * @return The converted [LinearShuntCompensator]
     */
    fun addFromPb(pb: PBLinearShuntCompensator): LinearShuntCompensator? = networkService.addFromPb(pb)

    /**
     * Add a converted copy of the protobuf [PBLoadBreakSwitch] to the [NetworkService].
     *
     * @param pb The [PBLoadBreakSwitch] to convert.
     * @return The converted [LoadBreakSwitch]
     */
    fun addFromPb(pb: PBLoadBreakSwitch): LoadBreakSwitch? = networkService.addFromPb(pb)

    /**
     * Add a converted copy of the protobuf [PBPerLengthPhaseImpedance] to the [NetworkService].
     *
     * @param pb The [PBPerLengthPhaseImpedance] to convert.
     * @return The converted [PerLengthPhaseImpedance]
     */
    fun addFromPb(pb: PBPerLengthPhaseImpedance): PerLengthPhaseImpedance? = networkService.addFromPb(pb)

    /**
     * Add a converted copy of the protobuf [PBPerLengthSequenceImpedance] to the [NetworkService].
     *
     * @param pb The [PBPerLengthSequenceImpedance] to convert.
     * @return The converted [PerLengthSequenceImpedance]
     */
    fun addFromPb(pb: PBPerLengthSequenceImpedance): PerLengthSequenceImpedance? = networkService.addFromPb(pb)

    /**
     * Add a converted copy of the protobuf [PBPetersenCoil] to the [NetworkService].
     *
     * @param pb The [PBPetersenCoil] to convert.
     * @return The converted [PetersenCoil]
     */
    fun addFromPb(pb: PBPetersenCoil): PetersenCoil? = networkService.addFromPb(pb)

    /**
     * Add a converted copy of the protobuf [PBPowerElectronicsConnection] to the [NetworkService].
     *
     * @param pb The [PBPowerElectronicsConnection] to convert.
     * @return The converted [PowerElectronicsConnection]
     */
    fun addFromPb(pb: PBPowerElectronicsConnection): PowerElectronicsConnection? = networkService.addFromPb(pb)

    /**
     * Add a converted copy of the protobuf [PBPowerElectronicsConnectionPhase] to the [NetworkService].
     *
     * @param pb The [PBPowerElectronicsConnectionPhase] to convert.
     * @return The converted [PowerElectronicsConnectionPhase]
     */
    fun addFromPb(pb: PBPowerElectronicsConnectionPhase): PowerElectronicsConnectionPhase? = networkService.addFromPb(pb)

    /**
     * Add a converted copy of the protobuf [PBPowerTransformer] to the [NetworkService].
     *
     * @param pb The [PBPowerTransformer] to convert.
     * @return The converted [PowerTransformer]
     */
    fun addFromPb(pb: PBPowerTransformer): PowerTransformer? = networkService.addFromPb(pb)

    /**
     * Add a converted copy of the protobuf [PBPowerTransformerEnd] to the [NetworkService].
     *
     * @param pb The [PBPowerTransformerEnd] to convert.
     * @return The converted [PowerTransformerEnd]
     */
    fun addFromPb(pb: PBPowerTransformerEnd): PowerTransformerEnd? = networkService.addFromPb(pb)

    /**
     * Add a converted copy of the protobuf [PBRatioTapChanger] to the [NetworkService].
     *
     * @param pb The [PBRatioTapChanger] to convert.
     * @return The converted [RatioTapChanger]
     */
    fun addFromPb(pb: PBRatioTapChanger): RatioTapChanger? = networkService.addFromPb(pb)

    /**
     * Add a converted copy of the protobuf [PBReactiveCapabilityCurve] to the [NetworkService].
     *
     * @param pb The [PBReactiveCapabilityCurve] to convert.
     * @return The converted [ReactiveCapabilityCurve]
     */
    fun addFromPb(pb: PBReactiveCapabilityCurve): ReactiveCapabilityCurve? = networkService.addFromPb(pb)

    /**
     * Add a converted copy of the protobuf [PBRecloser] to the [NetworkService].
     *
     * @param pb The [PBRecloser] to convert.
     * @return The converted [Recloser]
     */
    fun addFromPb(pb: PBRecloser): Recloser? = networkService.addFromPb(pb)

    /**
     * Add a converted copy of the protobuf [PBSeriesCompensator] to the [NetworkService].
     *
     * @param pb The [PBSeriesCompensator] to convert.
     * @return The converted [SeriesCompensator]
     */
    fun addFromPb(pb: PBSeriesCompensator): SeriesCompensator? = networkService.addFromPb(pb)

    /**
     * Add a converted copy of the protobuf [PBStaticVarCompensator] to the [NetworkService].
     *
     * @param pb The [PBStaticVarCompensator] to convert.
     * @return The converted [StaticVarCompensator]
     */
    fun addFromPb(pb: PBStaticVarCompensator): StaticVarCompensator? = networkService.addFromPb(pb)

    /**
     * Add a converted copy of the protobuf [PBSynchronousMachine] to the [NetworkService].
     *
     * @param pb The [PBSynchronousMachine] to convert.
     * @return The converted [SynchronousMachine]
     */
    fun addFromPb(pb: PBSynchronousMachine): SynchronousMachine? = networkService.addFromPb(pb)

    /**
     * Add a converted copy of the protobuf [PBTapChangerControl] to the [NetworkService].
     *
     * @param pb The [PBTapChangerControl] to convert.
     * @return The converted [TapChangerControl]
     */
    fun addFromPb(pb: PBTapChangerControl): TapChangerControl? = networkService.addFromPb(pb)

    /**
     * Add a converted copy of the protobuf [PBTransformerStarImpedance] to the [NetworkService].
     *
     * @param pb The [PBTransformerStarImpedance] to convert.
     * @return The converted [TransformerStarImpedance]
     */
    fun addFromPb(pb: PBTransformerStarImpedance): TransformerStarImpedance? = networkService.addFromPb(pb)

    // ###############################
    // # IEC61970 InfIEC61970 Feeder #
    // ###############################

    /**
     * Add a converted copy of the protobuf [PBCircuit] to the [NetworkService].
     *
     * @param pb The [PBCircuit] to convert.
     * @return The converted [Circuit]
     */
    fun addFromPb(pb: PBCircuit): Circuit? = networkService.addFromPb(pb)

}

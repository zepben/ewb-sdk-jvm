/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.network.translator

import com.zepben.ewb.services.common.translator.mRID
import com.zepben.protobuf.cim.extensions.iec61968.assetinfo.RelayInfo
import com.zepben.protobuf.cim.extensions.iec61968.metering.PanDemandResponseFunction
import com.zepben.protobuf.cim.extensions.iec61970.base.core.Site
import com.zepben.protobuf.cim.extensions.iec61970.base.feeder.Loop
import com.zepben.protobuf.cim.extensions.iec61970.base.feeder.LvFeeder
import com.zepben.protobuf.cim.extensions.iec61970.base.generation.production.EvChargingUnit
import com.zepben.protobuf.cim.extensions.iec61970.base.protection.*
import com.zepben.protobuf.cim.extensions.iec61970.base.wires.BatteryControl
import com.zepben.protobuf.cim.iec61968.assetinfo.*
import com.zepben.protobuf.cim.iec61968.assets.*
import com.zepben.protobuf.cim.iec61968.common.Location
import com.zepben.protobuf.cim.iec61968.infiec61968.infassetinfo.CurrentTransformerInfo
import com.zepben.protobuf.cim.iec61968.infiec61968.infassetinfo.PotentialTransformerInfo
import com.zepben.protobuf.cim.iec61968.infiec61968.infassets.Pole
import com.zepben.protobuf.cim.iec61968.metering.EndDevice
import com.zepben.protobuf.cim.iec61968.metering.EndDeviceFunction
import com.zepben.protobuf.cim.iec61968.metering.Meter
import com.zepben.protobuf.cim.iec61968.metering.UsagePoint
import com.zepben.protobuf.cim.iec61968.operations.OperationalRestriction
import com.zepben.protobuf.cim.iec61970.base.auxiliaryequipment.*
import com.zepben.protobuf.cim.iec61970.base.core.*
import com.zepben.protobuf.cim.iec61970.base.equivalents.EquivalentBranch
import com.zepben.protobuf.cim.iec61970.base.equivalents.EquivalentEquipment
import com.zepben.protobuf.cim.iec61970.base.generation.production.BatteryUnit
import com.zepben.protobuf.cim.iec61970.base.generation.production.PhotoVoltaicUnit
import com.zepben.protobuf.cim.iec61970.base.generation.production.PowerElectronicsUnit
import com.zepben.protobuf.cim.iec61970.base.generation.production.PowerElectronicsWindUnit
import com.zepben.protobuf.cim.iec61970.base.meas.*
import com.zepben.protobuf.cim.iec61970.base.protection.CurrentRelay
import com.zepben.protobuf.cim.iec61970.base.scada.RemoteControl
import com.zepben.protobuf.cim.iec61970.base.scada.RemotePoint
import com.zepben.protobuf.cim.iec61970.base.scada.RemoteSource
import com.zepben.protobuf.cim.iec61970.base.wires.*
import com.zepben.protobuf.cim.iec61970.infiec61970.feeder.Circuit

// ##################################
// # Extensions IEC61968 Asset Info #
// ##################################

/**
 * Extract the mRID from the composed classes.
 */
fun RelayInfo.mRID(): String = ai.mRID()

// ################################
// # Extensions IEC61968 Metering #
// ################################

/**
 * Extract the mRID from the composed classes.
 */
fun PanDemandResponseFunction.mRID(): String = edf.mRID()

// #################################
// # Extensions IEC61970 Base Core #
// #################################

/**
 * Extract the mRID from the composed classes.
 */
fun Site.mRID(): String = ec.mRID()

// ###################################
// # Extensions IEC61970 Base Feeder #
// ###################################

/**
 * Extract the mRID from the composed classes.
 */
fun Loop.mRID(): String = io.mrid

/**
 * Extract the mRID from the composed classes.
 */
fun LvFeeder.mRID(): String = ec.mRID()

// ##################################################
// # Extensions IEC61970 Base Generation Production #
// ##################################################

/**
 * Extract the mRID from the composed classes.
 */
fun EvChargingUnit.mRID(): String = peu.mRID()

// #######################################
// # Extensions IEC61970 Base Protection #
// #######################################

/**
 * Extract the mRID from the composed classes.
 */
fun DirectionalCurrentRelay.mRID(): String = prf.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun DistanceRelay.mRID(): String = prf.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun ProtectionRelayFunction.mRID(): String = psr.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun ProtectionRelayScheme.mRID(): String = io.mrid

/**
 * Extract the mRID from the composed classes.
 */
fun ProtectionRelaySystem.mRID(): String = eq.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun VoltageRelay.mRID(): String = prf.mRID()

/**
 * Extract the asset info mRID from the composed classes.
 */
fun ProtectionRelayFunction.assetInfoMRID(): String = psr.assetInfoMRID

// ##################################
// # Extensions IEC61970 Base Wires #
// ##################################

/**
 * Extract the mRID from the composed classes.
 */
fun BatteryControl.mRID(): String = rc.mRID()

// #######################
// # IEC61968 Asset Info #
// #######################

/**
 * Extract the mRID from the composed classes.
 */
fun CableInfo.mRID(): String = wi.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun OverheadWireInfo.mRID(): String = wi.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun NoLoadTest.mRID(): String = tt.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun OpenCircuitTest.mRID(): String = tt.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun PowerTransformerInfo.mRID(): String = ai.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun ShortCircuitTest.mRID(): String = tt.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun ShuntCompensatorInfo.mRID(): String = ai.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun SwitchInfo.mRID(): String = ai.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun TransformerEndInfo.mRID(): String = ai.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun TransformerTankInfo.mRID(): String = ai.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun TransformerTest.mRID(): String = io.mrid

/**
 * Extract the mRID from the composed classes.
 */
fun WireInfo.mRID(): String = ai.mRID()

// ###################
// # IEC61968 Assets #
// ###################

/**
 * Extract the mRID from the composed classes.
 */
fun Asset.mRID(): String = io.mrid

/**
 * Extract the mRID from the composed classes.
 */
fun AssetContainer.mRID(): String = at.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun AssetFunction.mRID(): String = io.mrid

/**
 * Extract the mRID from the composed classes.
 */
fun AssetInfo.mRID(): String = io.mrid

/**
 * Extract the mRID from the composed classes.
 */
fun AssetOrganisationRole.mRID(): String = or.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun AssetOwner.mRID(): String = aor.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun Streetlight.mRID(): String = at.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun Structure.mRID(): String = ac.mRID()

// ###################
// # IEC61968 Common #
// ###################

/**
 * Extract the mRID from the composed classes.
 */
fun Location.mRID(): String = io.mrid

// #####################################
// # IEC61968 infIEC61968 InfAssetInfo #
// #####################################

/**
 * Extract the mRID from the composed classes.
 */
fun CurrentTransformerInfo.mRID(): String = ai.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun PotentialTransformerInfo.mRID(): String = ai.mRID()

// ##################################
// # IEC61968 infIEC61968 InfAssets #
// ##################################

/**
 * Extract the mRID from the composed classes.
 */
fun Pole.mRID(): String = st.mRID()

// #####################
// # IEC61968 Metering #
// #####################

/**
 * Extract the mRID from the composed classes.
 */
fun EndDevice.mRID(): String = ac.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun EndDeviceFunction.mRID(): String = af.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun Meter.mRID(): String = ed.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun UsagePoint.mRID(): String = io.mrid

// #######################
// # IEC61968 Operations #
// #######################

/**
 * Extract the mRID from the composed classes.
 */
fun OperationalRestriction.mRID(): String = doc.mRID()

// #####################################
// # IEC61970 Base Auxiliary Equipment #
// #####################################

/**
 * Extract the mRID from the composed classes.
 */
fun AuxiliaryEquipment.mRID(): String = eq.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun CurrentTransformer.mRID(): String = sn.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun FaultIndicator.mRID(): String = ae.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun PotentialTransformer.mRID(): String = sn.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun Sensor.mRID(): String = ae.mRID()

/**
 * Extract the asset info mRID from the composed classes.
 */
fun CurrentTransformer.assetInfoMRID(): String = sn.ae.eq.assetInfoMRID()

/**
 * Extract the asset info mRID from the composed classes.
 */
fun PotentialTransformer.assetInfoMRID(): String = sn.ae.eq.assetInfoMRID()

// ######################
// # IEC61970 Base Core #
// ######################

/**
 * Extract the mRID from the composed classes.
 */
fun AcDcTerminal.mRID(): String = io.mrid

/**
 * Extract the mRID from the composed classes.
 */
fun BaseVoltage.mRID(): String = io.mrid

/**
 * Extract the mRID from the composed classes.
 */
fun ConductingEquipment.mRID(): String = eq.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun ConnectivityNode.mRID(): String = io.mrid

/**
 * Extract the mRID from the composed classes.
 */
fun ConnectivityNodeContainer.mRID(): String = psr.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun Curve.mRID(): String = io.mrid

/**
 * Extract the mRID from the composed classes.
 */
fun Equipment.mRID(): String = psr.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun EquipmentContainer.mRID(): String = cnc.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun Feeder.mRID(): String = ec.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun GeographicalRegion.mRID(): String = io.mrid

/**
 * Extract the mRID from the composed classes.
 */
fun PowerSystemResource.mRID(): String = io.mrid

/**
 * Extract the mRID from the composed classes.
 */
fun SubGeographicalRegion.mRID(): String = io.mrid

/**
 * Extract the mRID from the composed classes.
 */
fun Substation.mRID(): String = ec.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun Terminal.mRID(): String = ad.mRID()

/**
 * Extract the asset info mRID from the composed classes.
 */
fun ConductingEquipment.assetInfoMRID(): String = eq.assetInfoMRID()

/**
 * Extract the asset info mRID from the composed classes.
 */
fun Equipment.assetInfoMRID(): String = psr.assetInfoMRID

// #############################
// # IEC61970 Base Equivalents #
// #############################

/**
 * Extract the mRID from the composed classes.
 */
fun EquivalentBranch.mRID(): String = ee.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun EquivalentEquipment.mRID(): String = ce.mRID()

// #######################################
// # IEC61970 Base Generation Production #
// #######################################

/**
 * Extract the mRID from the composed classes.
 */
fun BatteryUnit.mRID(): String = peu.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun PhotoVoltaicUnit.mRID(): String = peu.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun PowerElectronicsUnit.mRID(): String = eq.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun PowerElectronicsWindUnit.mRID(): String = peu.mRID()

// ######################
// # IEC61970 Base Meas #
// ######################

/**
 * Extract the mRID from the composed classes.
 */
fun Accumulator.mRID(): String = measurement.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun Analog.mRID(): String = measurement.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun Control.mRID(): String = ip.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun Discrete.mRID(): String = measurement.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun IoPoint.mRID(): String = io.mrid

/**
 * Extract the mRID from the composed classes.
 */
fun Measurement.mRID(): String = io.mrid

// ############################
// # IEC61970 Base Protection #
// ############################

/**
 * Extract the mRID from the composed classes.
 */
fun CurrentRelay.mRID(): String = prf.mRID()

// #######################
// # IEC61970 Base Scada #
// #######################

/**
 * Extract the mRID from the composed classes.
 */
fun RemoteControl.mRID(): String = rp.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun RemotePoint.mRID(): String = io.mrid

/**
 * Extract the mRID from the composed classes.
 */
fun RemoteSource.mRID(): String = rp.mRID()

// #######################
// # IEC61970 Base Wires #
// #######################

/**
 * Extract the mRID from the composed classes.
 */
fun AcLineSegment.mRID(): String = cd.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun Breaker.mRID(): String = sw.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun BusbarSection.mRID(): String = cn.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun Clamp.mRID(): String = ce.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun Conductor.mRID(): String = ce.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun Connector.mRID(): String = ce.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun Cut.mRID(): String = sw.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun Disconnector.mRID(): String = sw.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun EarthFaultCompensator.mRID(): String = ce.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun EnergyConnection.mRID(): String = ce.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun EnergyConsumer.mRID(): String = ec.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun EnergyConsumerPhase.mRID(): String = psr.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun EnergySource.mRID(): String = ec.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun EnergySourcePhase.mRID(): String = psr.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun Fuse.mRID(): String = sw.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun Ground.mRID(): String = ce.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun GroundingImpedance.mRID(): String = efc.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun GroundDisconnector.mRID(): String = sw.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun Jumper.mRID(): String = sw.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun Junction.mRID(): String = cn.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun Line.mRID(): String = ec.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun LinearShuntCompensator.mRID(): String = sc.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun LoadBreakSwitch.mRID(): String = ps.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun PerLengthImpedance.mRID(): String = lp.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun PerLengthLineParameter.mRID(): String = io.mrid

/**
 * Extract the mRID from the composed classes.
 */
fun PerLengthPhaseImpedance.mRID(): String = pli.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun PerLengthSequenceImpedance.mRID(): String = pli.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun PetersenCoil.mRID(): String = efc.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun PowerElectronicsConnection.mRID(): String = rce.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun PowerElectronicsConnectionPhase.mRID(): String = psr.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun PowerTransformer.mRID(): String = ce.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun PowerTransformerEnd.mRID(): String = te.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun ProtectedSwitch.mRID(): String = sw.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun RatioTapChanger.mRID(): String = tc.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun ReactiveCapabilityCurve.mRID(): String = c.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun Recloser.mRID(): String = sw.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun RegulatingCondEq.mRID(): String = ec.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun RegulatingControl.mRID(): String = psr.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun RotatingMachine.mRID(): String = rce.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun SeriesCompensator.mRID(): String = ce.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun ShuntCompensator.mRID(): String = rce.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun StaticVarCompensator.mRID(): String = rce.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun Switch.mRID(): String = ce.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun SynchronousMachine.mRID(): String = rm.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun TapChanger.mRID(): String = psr.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun TapChangerControl.mRID(): String = rc.mRID()

/**
 * Extract the mRID from the composed classes.
 */
fun TransformerEnd.mRID(): String = io.mrid

/**
 * Extract the mRID from the composed classes.
 */
fun TransformerStarImpedance.mRID(): String = io.mrid

/**
 * Extract the asset info mRID from the composed classes.
 */
fun Conductor.assetInfoMRID(): String = ce.assetInfoMRID()

/**
 * Extract the asset info mRID from the composed classes.
 */
fun PowerTransformer.assetInfoMRID(): String = ce.assetInfoMRID()

/**
 * Extract the asset info mRID from the composed classes.
 */
fun ShuntCompensator.assetInfoMRID(): String = rce.ec.ce.assetInfoMRID()

/**
 * Extract the asset info mRID from the composed classes.
 */
fun Switch.assetInfoMRID(): String = ce.assetInfoMRID()

// ###############################
// # IEC61970 InfIEC61970 Feeder #
// ###############################

/**
 * Extract the mRID from the composed classes.
 */
fun Circuit.mRID(): String = l.mRID()

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

fun RelayInfo.mRID(): String = ai.mRID()

// ################################
// # Extensions IEC61968 Metering #
// ################################

fun PanDemandResponseFunction.mRID(): String = edf.mRID()

// #################################
// # Extensions IEC61970 Base Core #
// #################################

fun Site.mRID(): String = ec.mRID()

// ###################################
// # Extensions IEC61970 Base Feeder #
// ###################################

fun Loop.mRID(): String = io.mrid
fun LvFeeder.mRID(): String = ec.mRID()

// ##################################################
// # Extensions IEC61970 Base Generation Production #
// ##################################################

fun EvChargingUnit.mRID(): String = peu.mRID()

// #######################################
// # Extensions IEC61970 Base Protection #
// #######################################

fun DistanceRelay.mRID(): String = prf.mRID()
fun ProtectionRelayFunction.mRID(): String = psr.mRID()
fun ProtectionRelayScheme.mRID(): String = io.mrid
fun ProtectionRelaySystem.mRID(): String = eq.mRID()
fun VoltageRelay.mRID(): String = prf.mRID()

fun ProtectionRelayFunction.assetInfoMRID(): String = psr.assetInfoMRID

// ##################################
// # Extensions IEC61970 Base Wires #
// ##################################

fun BatteryControl.mRID(): String = rc.mRID()

// #######################
// # IEC61968 Asset Info #
// #######################

fun CableInfo.mRID(): String = wi.mRID()
fun OverheadWireInfo.mRID(): String = wi.mRID()
fun NoLoadTest.mRID(): String = tt.mRID()
fun OpenCircuitTest.mRID(): String = tt.mRID()
fun PowerTransformerInfo.mRID(): String = ai.mRID()
fun ShortCircuitTest.mRID(): String = tt.mRID()
fun ShuntCompensatorInfo.mRID(): String = ai.mRID()
fun SwitchInfo.mRID(): String = ai.mRID()
fun TransformerEndInfo.mRID(): String = ai.mRID()
fun TransformerTankInfo.mRID(): String = ai.mRID()
fun TransformerTest.mRID(): String = io.mrid
fun WireInfo.mRID(): String = ai.mRID()

// ###################
// # IEC61968 Assets #
// ###################

fun Asset.mRID(): String = io.mrid
fun AssetContainer.mRID(): String = at.mRID()
fun AssetFunction.mRID(): String = io.mrid
fun AssetInfo.mRID(): String = io.mrid
fun AssetOrganisationRole.mRID(): String = or.mRID()
fun AssetOwner.mRID(): String = aor.mRID()
fun Streetlight.mRID(): String = at.mRID()
fun Structure.mRID(): String = ac.mRID()

// ###################
// # IEC61968 Common #
// ###################

fun Location.mRID(): String = io.mrid

// #####################################
// # IEC61968 infIEC61968 InfAssetInfo #
// #####################################

fun CurrentTransformerInfo.mRID(): String = ai.mRID()
fun PotentialTransformerInfo.mRID(): String = ai.mRID()

// ##################################
// # IEC61968 infIEC61968 InfAssets #
// ##################################

fun Pole.mRID(): String = st.mRID()

// #####################
// # IEC61968 Metering #
// #####################

fun EndDevice.mRID(): String = ac.mRID()
fun EndDeviceFunction.mRID(): String = af.mRID()
fun Meter.mRID(): String = ed.mRID()
fun UsagePoint.mRID(): String = io.mrid

// #######################
// # IEC61968 Operations #
// #######################

fun OperationalRestriction.mRID(): String = doc.mRID()

// #####################################
// # IEC61970 Base Auxiliary Equipment #
// #####################################

fun AuxiliaryEquipment.mRID(): String = eq.mRID()
fun CurrentTransformer.mRID(): String = sn.mRID()
fun FaultIndicator.mRID(): String = ae.mRID()
fun PotentialTransformer.mRID(): String = sn.mRID()
fun Sensor.mRID(): String = ae.mRID()

fun CurrentTransformer.assetInfoMRID(): String = sn.ae.eq.assetInfoMRID()
fun PotentialTransformer.assetInfoMRID(): String = sn.ae.eq.assetInfoMRID()

// ######################
// # IEC61970 Base Core #
// ######################

fun AcDcTerminal.mRID(): String = io.mrid
fun BaseVoltage.mRID(): String = io.mrid
fun ConductingEquipment.mRID(): String = eq.mRID()
fun ConnectivityNode.mRID(): String = io.mrid
fun ConnectivityNodeContainer.mRID(): String = psr.mRID()
fun Curve.mRID(): String = io.mrid
fun Equipment.mRID(): String = psr.mRID()
fun EquipmentContainer.mRID(): String = cnc.mRID()
fun Feeder.mRID(): String = ec.mRID()
fun GeographicalRegion.mRID(): String = io.mrid
fun PowerSystemResource.mRID(): String = io.mrid
fun SubGeographicalRegion.mRID(): String = io.mrid
fun Substation.mRID(): String = ec.mRID()
fun Terminal.mRID(): String = ad.mRID()

fun ConductingEquipment.assetInfoMRID(): String = eq.assetInfoMRID()
fun Equipment.assetInfoMRID(): String = psr.assetInfoMRID

// #############################
// # IEC61970 Base Equivalents #
// #############################

fun EquivalentBranch.mRID(): String = ee.mRID()
fun EquivalentEquipment.mRID(): String = ce.mRID()

// #######################################
// # IEC61970 Base Generation Production #
// #######################################

fun BatteryUnit.mRID(): String = peu.mRID()
fun PhotoVoltaicUnit.mRID(): String = peu.mRID()
fun PowerElectronicsUnit.mRID(): String = eq.mRID()
fun PowerElectronicsWindUnit.mRID(): String = peu.mRID()

// ######################
// # IEC61970 Base Meas #
// ######################

fun Accumulator.mRID(): String = measurement.mRID()
fun Analog.mRID(): String = measurement.mRID()
fun Control.mRID(): String = ip.mRID()
fun Discrete.mRID(): String = measurement.mRID()
fun IoPoint.mRID(): String = io.mrid
fun Measurement.mRID(): String = io.mrid

// ############################
// # IEC61970 Base Protection #
// ############################

fun CurrentRelay.mRID(): String = prf.mRID()

// #######################
// # IEC61970 Base Scada #
// #######################

fun RemoteControl.mRID(): String = rp.mRID()
fun RemotePoint.mRID(): String = io.mrid
fun RemoteSource.mRID(): String = rp.mRID()

// #######################
// # IEC61970 Base Wires #
// #######################

fun AcLineSegment.mRID(): String = cd.mRID()
fun Breaker.mRID(): String = sw.mRID()
fun BusbarSection.mRID(): String = cn.mRID()
fun Clamp.mRID(): String = ce.mRID()
fun Conductor.mRID(): String = ce.mRID()
fun Connector.mRID(): String = ce.mRID()
fun Cut.mRID(): String = sw.mRID()
fun Disconnector.mRID(): String = sw.mRID()
fun EarthFaultCompensator.mRID(): String = ce.mRID()
fun EnergyConnection.mRID(): String = ce.mRID()
fun EnergyConsumer.mRID(): String = ec.mRID()
fun EnergyConsumerPhase.mRID(): String = psr.mRID()
fun EnergySource.mRID(): String = ec.mRID()
fun EnergySourcePhase.mRID(): String = psr.mRID()
fun Fuse.mRID(): String = sw.mRID()
fun Ground.mRID(): String = ce.mRID()
fun GroundingImpedance.mRID(): String = efc.mRID()
fun GroundDisconnector.mRID(): String = sw.mRID()
fun Jumper.mRID(): String = sw.mRID()
fun Junction.mRID(): String = cn.mRID()
fun Line.mRID(): String = ec.mRID()
fun LinearShuntCompensator.mRID(): String = sc.mRID()
fun LoadBreakSwitch.mRID(): String = ps.mRID()
fun PerLengthImpedance.mRID(): String = lp.mRID()
fun PerLengthLineParameter.mRID(): String = io.mrid
fun PerLengthPhaseImpedance.mRID(): String = pli.mRID()
fun PerLengthSequenceImpedance.mRID(): String = pli.mRID()
fun PetersenCoil.mRID(): String = efc.mRID()
fun PowerElectronicsConnection.mRID(): String = rce.mRID()
fun PowerElectronicsConnectionPhase.mRID(): String = psr.mRID()
fun PowerTransformer.mRID(): String = ce.mRID()
fun PowerTransformerEnd.mRID(): String = te.mRID()
fun ProtectedSwitch.mRID(): String = sw.mRID()
fun RatioTapChanger.mRID(): String = tc.mRID()
fun ReactiveCapabilityCurve.mRID(): String = c.mRID()
fun Recloser.mRID(): String = sw.mRID()
fun RegulatingCondEq.mRID(): String = ec.mRID()
fun RegulatingControl.mRID(): String = psr.mRID()
fun RotatingMachine.mRID(): String = rce.mRID()
fun SeriesCompensator.mRID(): String = ce.mRID()
fun ShuntCompensator.mRID(): String = rce.mRID()
fun StaticVarCompensator.mRID(): String = rce.mRID()
fun Switch.mRID(): String = ce.mRID()
fun SynchronousMachine.mRID(): String = rm.mRID()
fun TapChanger.mRID(): String = psr.mRID()
fun TapChangerControl.mRID(): String = rc.mRID()
fun TransformerEnd.mRID(): String = io.mrid
fun TransformerStarImpedance.mRID(): String = io.mrid

fun Conductor.assetInfoMRID(): String = ce.assetInfoMRID()
fun PowerTransformer.assetInfoMRID(): String = ce.assetInfoMRID()
fun ShuntCompensator.assetInfoMRID(): String = rce.ec.ce.assetInfoMRID()
fun Switch.assetInfoMRID(): String = ce.assetInfoMRID()

// ###############################
// # IEC61970 InfIEC61970 Feeder #
// ###############################

fun Circuit.mRID(): String = l.mRID()

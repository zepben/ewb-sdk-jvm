/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network.translator

import com.zepben.evolve.services.common.translator.mRID
import com.zepben.evolve.services.common.translator.nameAndMRID
import com.zepben.protobuf.cim.iec61968.assetinfo.CableInfo
import com.zepben.protobuf.cim.iec61968.assetinfo.OverheadWireInfo
import com.zepben.protobuf.cim.iec61968.assetinfo.PowerTransformerInfo
import com.zepben.protobuf.cim.iec61968.assetinfo.WireInfo
import com.zepben.protobuf.cim.iec61968.assets.*
import com.zepben.protobuf.cim.iec61968.common.Location
import com.zepben.protobuf.cim.iec61968.metering.EndDevice
import com.zepben.protobuf.cim.iec61968.metering.Meter
import com.zepben.protobuf.cim.iec61968.metering.UsagePoint
import com.zepben.protobuf.cim.iec61968.operations.OperationalRestriction
import com.zepben.protobuf.cim.iec61970.base.auxiliaryequipment.AuxiliaryEquipment
import com.zepben.protobuf.cim.iec61970.base.auxiliaryequipment.FaultIndicator
import com.zepben.protobuf.cim.iec61970.base.core.*
import com.zepben.protobuf.cim.iec61970.base.meas.Control
import com.zepben.protobuf.cim.iec61970.base.meas.IoPoint
import com.zepben.protobuf.cim.iec61970.base.meas.Measurement
import com.zepben.protobuf.cim.iec61970.base.scada.RemoteControl
import com.zepben.protobuf.cim.iec61970.base.scada.RemotePoint
import com.zepben.protobuf.cim.iec61970.base.scada.RemoteSource
import com.zepben.protobuf.cim.iec61970.base.wires.*
import com.zepben.protobuf.cim.iec61970.base.wires.generation.production.BatteryUnit
import com.zepben.protobuf.cim.iec61970.base.wires.generation.production.PhotoVoltaicUnit
import com.zepben.protobuf.cim.iec61970.base.wires.generation.production.PowerElectronicsUnit
import com.zepben.protobuf.cim.iec61970.base.wires.generation.production.PowerElectronicsWindUnit
import com.zepben.protobuf.cim.iec61970.infiec61970.feeder.Circuit
import com.zepben.protobuf.cim.iec61970.infiec61970.feeder.Loop

fun CableInfo.mRID(): String = wi.mRID()
fun OverheadWireInfo.mRID(): String = wi.mRID()
fun PowerTransformerInfo.mRID(): String = ai.mRID()
fun WireInfo.mRID(): String = ai.mRID()
fun Asset.mRID(): String = io.mrid
fun AssetContainer.mRID(): String = at.mRID()
fun AssetInfo.mRID(): String = io.mrid
fun AssetOrganisationRole.mRID(): String = or.mRID()
fun AssetOwner.mRID(): String = aor.mRID()
fun Pole.mRID(): String = st.mRID()
fun Streetlight.mRID(): String = at.mRID()
fun Structure.mRID(): String = ac.mRID()
fun Location.mRID(): String = io.mrid
fun EndDevice.mRID(): String = ac.mRID()
fun Meter.mRID(): String = ed.mRID()
fun UsagePoint.mRID(): String = io.mrid
fun OperationalRestriction.mRID(): String = doc.mRID()
fun AuxiliaryEquipment.mRID(): String = eq.mRID()
fun FaultIndicator.mRID(): String = ae.mRID()
fun AcDcTerminal.mRID(): String = io.mrid
fun BaseVoltage.mRID(): String = io.mrid
fun ConductingEquipment.mRID(): String = eq.mRID()
fun ConnectivityNode.mRID(): String = io.mrid
fun ConnectivityNodeContainer.mRID(): String = psr.mRID()
fun Equipment.mRID(): String = psr.mRID()
fun EquipmentContainer.mRID(): String = cnc.mRID()
fun Feeder.mRID(): String = ec.mRID()
fun GeographicalRegion.mRID(): String = io.mrid
fun PowerSystemResource.mRID(): String = io.mrid
fun Site.mRID(): String = ec.mRID()
fun SubGeographicalRegion.mRID(): String = io.mrid
fun Substation.mRID(): String = ec.mRID()
fun Terminal.mRID(): String = ad.mRID()
fun Control.mRID(): String = ip.mRID()
fun IoPoint.mRID(): String = io.mrid
fun Measurement.mRID(): String = io.mrid
fun RemoteControl.mRID(): String = rp.mRID()
fun RemotePoint.mRID(): String = io.mrid
fun RemoteSource.mRID(): String = rp.mRID()
fun PowerElectronicsUnit.mRID(): String = eq.mRID()
fun BatteryUnit.mRID(): String = peu.mRID()
fun PhotoVoltaicUnit.mRID(): String = peu.mRID()
fun PowerElectronicsWindUnit.mRID(): String = peu.mRID()
fun AcLineSegment.mRID(): String = cd.mRID()
fun Breaker.mRID(): String = sw.mRID()
fun BusbarSection.mRID(): String = cn.mRID()
fun Conductor.mRID(): String = ce.mRID()
fun Connector.mRID(): String = ce.mRID()
fun Disconnector.mRID(): String = sw.mRID()
fun EnergyConnection.mRID(): String = ce.mRID()
fun EnergyConsumer.mRID(): String = ec.mRID()
fun EnergyConsumerPhase.mRID(): String = psr.mRID()
fun EnergySource.mRID(): String = ec.mRID()
fun EnergySourcePhase.mRID(): String = psr.mRID()
fun Fuse.mRID(): String = sw.mRID()
fun Jumper.mRID(): String = sw.mRID()
fun Junction.mRID(): String = cn.mRID()
fun Line.mRID(): String = ec.mRID()
fun LinearShuntCompensator.mRID(): String = sc.mRID()
fun PerLengthImpedance.mRID(): String = lp.mRID()
fun PerLengthLineParameter.mRID(): String = io.mrid
fun PerLengthSequenceImpedance.mRID(): String = pli.mRID()
fun PowerElectronicsConnection.mRID(): String = rce.mRID()
fun PowerElectronicsConnectionPhase.mRID(): String = psr.mRID()
fun PowerTransformer.mRID(): String = ce.mRID()
fun PowerTransformerEnd.mRID(): String = te.mRID()
fun ProtectedSwitch.mRID(): String = sw.mRID()
fun RatioTapChanger.mRID(): String = tc.mRID()
fun Recloser.mRID(): String = sw.mRID()
fun RegulatingCondEq.mRID(): String = ec.mRID()
fun ShuntCompensator.mRID(): String = rce.mRID()
fun Switch.mRID(): String = ce.mRID()
fun TapChanger.mRID(): String = psr.mRID()
fun TransformerEnd.mRID(): String = io.mrid
fun Circuit.mRID(): String = l.mRID()
fun Loop.mRID(): String = io.mrid

fun AcDcTerminal.nameAndMRID(): String = io.nameAndMRID()
fun ConnectivityNodeContainer.nameAndMRID(): String = psr.nameAndMRID()
fun EquipmentContainer.nameAndMRID(): String = cnc.nameAndMRID()
fun Feeder.nameAndMRID(): String = ec.nameAndMRID()
fun PowerSystemResource.nameAndMRID(): String = io.nameAndMRID()
fun Terminal.nameAndMRID(): String = ad.nameAndMRID()
fun EnergyConsumerPhase.nameAndMRID(): String = psr.nameAndMRID()
fun EnergySourcePhase.nameAndMRID(): String = psr.nameAndMRID()
fun PowerTransformerEnd.nameAndMRID(): String = te.nameAndMRID()
fun TransformerEnd.nameAndMRID(): String = io.nameAndMRID()

fun ConductingEquipment.assetInfoMRID(): String = eq.assetInfoMRID()
fun Equipment.assetInfoMRID(): String = psr.assetInfoMRID
fun Conductor.assetInfoMRID(): String = ce.assetInfoMRID()
fun PowerTransformer.assetInfoMRID(): String = ce.assetInfoMRID()

/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.network.translator

import com.zepben.ewb.cim.extensions.iec61968.common.ContactMethodType
import com.zepben.ewb.cim.extensions.iec61970.base.protection.PolarizingQuantityType
import com.zepben.ewb.cim.extensions.iec61970.base.protection.PowerDirectionKind
import com.zepben.ewb.cim.extensions.iec61970.base.protection.ProtectionKind
import com.zepben.ewb.cim.extensions.iec61970.base.wires.BatteryControlMode
import com.zepben.ewb.cim.extensions.iec61970.base.wires.TransformerCoolingType
import com.zepben.ewb.cim.extensions.iec61970.base.wires.VectorGroup
import com.zepben.ewb.cim.iec61968.assetinfo.WireInsulationKind
import com.zepben.ewb.cim.iec61968.assetinfo.WireMaterialKind
import com.zepben.ewb.cim.iec61968.infiec61968.infassetinfo.TransformerConstructionKind
import com.zepben.ewb.cim.iec61968.infiec61968.infassetinfo.TransformerFunctionKind
import com.zepben.ewb.cim.iec61968.infiec61968.infassets.StreetlightLampKind
import com.zepben.ewb.cim.iec61968.metering.EndDeviceFunctionKind
import com.zepben.ewb.cim.iec61970.base.auxiliaryequipment.PotentialTransformerKind
import com.zepben.ewb.cim.iec61970.base.core.PhaseCode
import com.zepben.ewb.cim.iec61970.base.domain.UnitSymbol
import com.zepben.ewb.cim.iec61970.base.generation.production.BatteryStateKind
import com.zepben.ewb.cim.iec61970.base.wires.*
import com.zepben.ewb.services.common.translator.EnumMapper
import com.zepben.ewb.services.network.tracing.feeder.FeederDirection
import com.zepben.protobuf.cim.extensions.iec61968.common.ContactMethodType as PBContactMethodType
import com.zepben.protobuf.cim.extensions.iec61970.base.protection.PolarizingQuantityType as PBPolarizingQuantityType
import com.zepben.protobuf.cim.extensions.iec61970.base.protection.PowerDirectionKind as PBPowerDirectionKind
import com.zepben.protobuf.cim.extensions.iec61970.base.protection.ProtectionKind as PBProtectionKind
import com.zepben.protobuf.cim.extensions.iec61970.base.wires.BatteryControlMode as PBBatteryControlMode
import com.zepben.protobuf.cim.extensions.iec61970.base.wires.TransformerCoolingType as PBTransformerCoolingType
import com.zepben.protobuf.cim.extensions.iec61970.base.wires.VectorGroup as PBVectorGroup
import com.zepben.protobuf.cim.iec61968.assetinfo.WireInsulationKind as PBWireInsulationKind
import com.zepben.protobuf.cim.iec61968.assetinfo.WireMaterialKind as PBWireMaterialKind
import com.zepben.protobuf.cim.iec61968.infiec61968.infassetinfo.TransformerConstructionKind as PBTransformerConstructionKind
import com.zepben.protobuf.cim.iec61968.infiec61968.infassetinfo.TransformerFunctionKind as PBTransformerFunctionKind
import com.zepben.protobuf.cim.iec61968.infiec61968.infassets.StreetlightLampKind as PBStreetlightLampKind
import com.zepben.protobuf.cim.iec61968.metering.EndDeviceFunctionKind as PBEndDeviceFunctionKind
import com.zepben.protobuf.cim.iec61970.base.auxiliaryequipment.PotentialTransformerKind as PBPotentialTransformerKind
import com.zepben.protobuf.cim.iec61970.base.core.PhaseCode as PBPhaseCode
import com.zepben.protobuf.cim.iec61970.base.domain.UnitSymbol as PBUnitSymbol
import com.zepben.protobuf.cim.iec61970.base.generation.production.BatteryStateKind as PBBatteryStateKind
import com.zepben.protobuf.cim.iec61970.base.wires.PhaseShuntConnectionKind as PBPhaseShuntConnectionKind
import com.zepben.protobuf.cim.iec61970.base.wires.RegulatingControlModeKind as PBRegulatingControlModeKind
import com.zepben.protobuf.cim.iec61970.base.wires.SVCControlMode as PBSVCControlMode
import com.zepben.protobuf.cim.iec61970.base.wires.SinglePhaseKind as PBSinglePhaseKind
import com.zepben.protobuf.cim.iec61970.base.wires.SynchronousMachineKind as PBSynchronousMachineKind
import com.zepben.protobuf.cim.iec61970.base.wires.WindingConnection as PBWindingConnection
import com.zepben.protobuf.network.model.FeederDirection as PBFeederDirection

internal val mapBatteryControlMode = EnumMapper(BatteryControlMode.entries, PBBatteryControlMode.entries)
internal val mapBatteryStateKind = EnumMapper(BatteryStateKind.entries, PBBatteryStateKind.entries)
internal val mapContactMethodType = EnumMapper(ContactMethodType.entries, PBContactMethodType.entries)
internal val mapEndDeviceFunctionKind = EnumMapper(EndDeviceFunctionKind.entries, PBEndDeviceFunctionKind.entries)
internal val mapFeederDirection = EnumMapper(FeederDirection.entries, PBFeederDirection.entries)
internal val mapPhaseCode = EnumMapper(PhaseCode.entries, PBPhaseCode.entries)
internal val mapPhaseShuntConnectionKind = EnumMapper(PhaseShuntConnectionKind.entries, PBPhaseShuntConnectionKind.entries)
internal val mapPolarizingQuantityType = EnumMapper(PolarizingQuantityType.entries, PBPolarizingQuantityType.entries)
internal val mapPotentialTransformerKind = EnumMapper(PotentialTransformerKind.entries, PBPotentialTransformerKind.entries)
internal val mapPowerDirectionKind = EnumMapper(PowerDirectionKind.entries, PBPowerDirectionKind.entries)
internal val mapProtectionKind = EnumMapper(ProtectionKind.entries, PBProtectionKind.entries)
internal val mapRegulatingControlModeKind = EnumMapper(RegulatingControlModeKind.entries, PBRegulatingControlModeKind.entries)
internal val mapSinglePhaseKind = EnumMapper(SinglePhaseKind.entries, PBSinglePhaseKind.entries)
internal val mapStreetlightLampKind = EnumMapper(StreetlightLampKind.entries, PBStreetlightLampKind.entries)
internal val mapSVCControlMode = EnumMapper(SVCControlMode.entries, PBSVCControlMode.entries)
internal val mapSynchronousMachineKind = EnumMapper(SynchronousMachineKind.entries, PBSynchronousMachineKind.entries)
internal val mapTransformerConstructionKind = EnumMapper(TransformerConstructionKind.entries, PBTransformerConstructionKind.entries)
internal val mapTransformerCoolingType = EnumMapper(TransformerCoolingType.entries, PBTransformerCoolingType.entries)
internal val mapTransformerFunctionKind = EnumMapper(TransformerFunctionKind.entries, PBTransformerFunctionKind.entries)
internal val mapUnitSymbol = EnumMapper(UnitSymbol.entries, PBUnitSymbol.entries)
internal val mapVectorGroup = EnumMapper(VectorGroup.entries, PBVectorGroup.entries)
internal val mapWindingConnection = EnumMapper(WindingConnection.entries, PBWindingConnection.entries)
internal val mapWireMaterialKind = EnumMapper(WireMaterialKind.entries, PBWireMaterialKind.entries)
internal val mapWireInsulationKind = EnumMapper(WireInsulationKind.entries, PBWireInsulationKind.entries)

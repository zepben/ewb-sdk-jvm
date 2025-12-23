/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.database.sqlite.cim.network

import com.zepben.ewb.cim.extensions.iec61968.assetinfo.RelayInfo
import com.zepben.ewb.cim.extensions.iec61968.metering.PanDemandResponseFunction
import com.zepben.ewb.cim.extensions.iec61970.base.core.HvCustomer
import com.zepben.ewb.cim.extensions.iec61970.base.core.Site
import com.zepben.ewb.cim.extensions.iec61970.base.feeder.Loop
import com.zepben.ewb.cim.extensions.iec61970.base.feeder.LvFeeder
import com.zepben.ewb.cim.extensions.iec61970.base.feeder.LvSubstation
import com.zepben.ewb.cim.extensions.iec61970.base.generation.production.EvChargingUnit
import com.zepben.ewb.cim.extensions.iec61970.base.protection.*
import com.zepben.ewb.cim.extensions.iec61970.base.wires.BatteryControl
import com.zepben.ewb.cim.iec61968.assetinfo.*
import com.zepben.ewb.cim.iec61968.assets.AssetOwner
import com.zepben.ewb.cim.iec61968.assets.Streetlight
import com.zepben.ewb.cim.iec61968.common.Location
import com.zepben.ewb.cim.iec61968.common.Organisation
import com.zepben.ewb.cim.iec61968.infiec61968.infassetinfo.CurrentTransformerInfo
import com.zepben.ewb.cim.iec61968.infiec61968.infassetinfo.PotentialTransformerInfo
import com.zepben.ewb.cim.iec61968.infiec61968.infassets.Pole
import com.zepben.ewb.cim.iec61968.metering.Meter
import com.zepben.ewb.cim.iec61968.metering.UsagePoint
import com.zepben.ewb.cim.iec61968.operations.OperationalRestriction
import com.zepben.ewb.cim.iec61970.base.auxiliaryequipment.CurrentTransformer
import com.zepben.ewb.cim.iec61970.base.auxiliaryequipment.FaultIndicator
import com.zepben.ewb.cim.iec61970.base.auxiliaryequipment.PotentialTransformer
import com.zepben.ewb.cim.iec61970.base.core.*
import com.zepben.ewb.cim.iec61970.base.equivalents.EquivalentBranch
import com.zepben.ewb.cim.iec61970.base.generation.production.BatteryUnit
import com.zepben.ewb.cim.iec61970.base.generation.production.PhotoVoltaicUnit
import com.zepben.ewb.cim.iec61970.base.generation.production.PowerElectronicsWindUnit
import com.zepben.ewb.cim.iec61970.base.meas.Accumulator
import com.zepben.ewb.cim.iec61970.base.meas.Analog
import com.zepben.ewb.cim.iec61970.base.meas.Control
import com.zepben.ewb.cim.iec61970.base.meas.Discrete
import com.zepben.ewb.cim.iec61970.base.protection.CurrentRelay
import com.zepben.ewb.cim.iec61970.base.scada.RemoteControl
import com.zepben.ewb.cim.iec61970.base.scada.RemoteSource
import com.zepben.ewb.cim.iec61970.base.wires.*
import com.zepben.ewb.cim.iec61970.infiec61970.feeder.Circuit
import com.zepben.ewb.database.sqlite.cim.BaseServiceWriter
import com.zepben.ewb.services.network.NetworkService

/**
 * A class for writing a [NetworkService] into the database.
 *
 * @param databaseTables The [NetworkDatabaseTables] to add to the database.
 */
internal class NetworkServiceWriter(
    databaseTables: NetworkDatabaseTables,
    override val writer: NetworkCimWriter = NetworkCimWriter(databaseTables)
) : BaseServiceWriter<NetworkService>(writer) {

    override fun NetworkService.writeService(): Boolean =
        writeEach<CableInfo>(writer::write) and
            writeEach<OverheadWireInfo>(writer::write) and
            writeEach<PowerTransformerInfo>(writer::write) and
            writeEach<TransformerTankInfo>(writer::write) and
            writeEach<NoLoadTest>(writer::write) and
            writeEach<OpenCircuitTest>(writer::write) and
            writeEach<ShortCircuitTest>(writer::write) and
            writeEach<ShuntCompensatorInfo>(writer::write) and
            writeEach<SwitchInfo>(writer::write) and
            writeEach<TransformerEndInfo>(writer::write) and
            writeEach<AssetOwner>(writer::write) and
            writeEach<Pole>(writer::write) and
            writeEach<Streetlight>(writer::write) and
            writeEach<Location>(writer::write) and
            writeEach<Organisation>(writer::write) and
            writeEach<Meter>(writer::write) and
            writeEach<UsagePoint>(writer::write) and
            writeEach<OperationalRestriction>(writer::write) and
            writeEach<FaultIndicator>(writer::write) and
            writeEach<BaseVoltage>(writer::write) and
            writeEach<ConnectivityNode>(writer::write) and
            writeEach<Feeder>(writer::write) and
            writeEach<GeographicalRegion>(writer::write) and
            writeEach<Site>(writer::write) and
            writeEach<HvCustomer>(writer::write) and
            writeEach<SubGeographicalRegion>(writer::write) and
            writeEach<Substation>(writer::write) and
            writeEach<Terminal>(writer::write) and
            writeEach<EquivalentBranch>(writer::write) and
            writeEach<BatteryUnit>(writer::write) and
            writeEach<PhotoVoltaicUnit>(writer::write) and
            writeEach<PowerElectronicsWindUnit>(writer::write) and
            writeEach<AcLineSegment>(writer::write) and
            writeEach<AcLineSegmentPhase>(writer::write) and
            writeEach<Breaker>(writer::write) and
            writeEach<LoadBreakSwitch>(writer::write) and
            writeEach<BusbarSection>(writer::write) and
            writeEach<Clamp>(writer::write) and
            writeEach<Cut>(writer::write) and
            writeEach<Disconnector>(writer::write) and
            writeEach<EnergyConsumer>(writer::write) and
            writeEach<EnergyConsumerPhase>(writer::write) and
            writeEach<EnergySource>(writer::write) and
            writeEach<EnergySourcePhase>(writer::write) and
            writeEach<Fuse>(writer::write) and
            writeEach<Jumper>(writer::write) and
            writeEach<Junction>(writer::write) and
            writeEach<LinearShuntCompensator>(writer::write) and
            writeEach<PerLengthPhaseImpedance>(writer::write) and
            writeEach<PerLengthSequenceImpedance>(writer::write) and
            writeEach<PowerElectronicsConnection>(writer::write) and
            writeEach<PowerElectronicsConnectionPhase>(writer::write) and
            writeEach<PowerTransformer>(writer::write) and
            writeEach<PowerTransformerEnd>(writer::write) and
            writeEach<RatioTapChanger>(writer::write) and
            writeEach<Recloser>(writer::write) and
            writeEach<TransformerStarImpedance>(writer::write) and
            writeEach<Circuit>(writer::write) and
            writeEach<Loop>(writer::write) and
            writeEach<LvFeeder>(writer::write) and
            writeEach<LvSubstation>(writer::write) and
            writeEach<Analog>(writer::write) and
            writeEach<Accumulator>(writer::write) and
            writeEach<Discrete>(writer::write) and
            writeEach<Control>(writer::write) and
            writeEach<RemoteControl>(writer::write) and
            writeEach<RemoteSource>(writer::write) and
            writeEach<CurrentTransformerInfo>(writer::write) and
            writeEach<PotentialTransformerInfo>(writer::write) and
            writeEach<CurrentTransformer>(writer::write) and
            writeEach<PotentialTransformer>(writer::write) and
            writeEach<RelayInfo>(writer::write) and
            writeEach<CurrentRelay>(writer::write) and
            writeEach<TapChangerControl>(writer::write) and
            writeEach<EvChargingUnit>(writer::write) and
            writeEach<DirectionalCurrentRelay>(writer::write) and
            writeEach<DistanceRelay>(writer::write) and
            writeEach<ProtectionRelayScheme>(writer::write) and
            writeEach<ProtectionRelaySystem>(writer::write) and
            writeEach<VoltageRelay>(writer::write) and
            writeEach<Ground>(writer::write) and
            writeEach<GroundDisconnector>(writer::write) and
            writeEach<SeriesCompensator>(writer::write) and
            writeEach<SynchronousMachine>(writer::write) and
            writeEach<PetersenCoil>(writer::write) and
            writeEach<GroundingImpedance>(writer::write) and
            writeEach<ReactiveCapabilityCurve>(writer::write) and
            writeEach<PanDemandResponseFunction>(writer::write) and
            writeEach<BatteryControl>(writer::write) and
            writeEach<StaticVarCompensator>(writer::write)

}

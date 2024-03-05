/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.database.sqlite.network

import com.zepben.evolve.cim.iec61968.assetinfo.*
import com.zepben.evolve.cim.iec61968.assets.AssetOwner
import com.zepben.evolve.cim.iec61968.assets.Pole
import com.zepben.evolve.cim.iec61968.assets.Streetlight
import com.zepben.evolve.cim.iec61968.common.Location
import com.zepben.evolve.cim.iec61968.common.Organisation
import com.zepben.evolve.cim.iec61968.infiec61968.infassetinfo.CurrentTransformerInfo
import com.zepben.evolve.cim.iec61968.infiec61968.infassetinfo.PotentialTransformerInfo
import com.zepben.evolve.cim.iec61968.infiec61968.infassetinfo.RelayInfo
import com.zepben.evolve.cim.iec61968.metering.Meter
import com.zepben.evolve.cim.iec61968.metering.UsagePoint
import com.zepben.evolve.cim.iec61968.operations.OperationalRestriction
import com.zepben.evolve.cim.iec61970.base.auxiliaryequipment.CurrentTransformer
import com.zepben.evolve.cim.iec61970.base.auxiliaryequipment.FaultIndicator
import com.zepben.evolve.cim.iec61970.base.auxiliaryequipment.PotentialTransformer
import com.zepben.evolve.cim.iec61970.base.core.*
import com.zepben.evolve.cim.iec61970.base.equivalents.EquivalentBranch
import com.zepben.evolve.cim.iec61970.base.meas.Accumulator
import com.zepben.evolve.cim.iec61970.base.meas.Analog
import com.zepben.evolve.cim.iec61970.base.meas.Control
import com.zepben.evolve.cim.iec61970.base.meas.Discrete
import com.zepben.evolve.cim.iec61970.base.protection.*
import com.zepben.evolve.cim.iec61970.base.scada.RemoteControl
import com.zepben.evolve.cim.iec61970.base.scada.RemoteSource
import com.zepben.evolve.cim.iec61970.base.wires.*
import com.zepben.evolve.cim.iec61970.base.wires.generation.production.BatteryUnit
import com.zepben.evolve.cim.iec61970.base.wires.generation.production.PhotoVoltaicUnit
import com.zepben.evolve.cim.iec61970.base.wires.generation.production.PowerElectronicsWindUnit
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.Circuit
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.Loop
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.LvFeeder
import com.zepben.evolve.cim.iec61970.infiec61970.wires.generation.production.EvChargingUnit
import com.zepben.evolve.database.sqlite.common.BaseServiceWriter
import com.zepben.evolve.services.network.NetworkService

class NetworkServiceWriter(
    service: NetworkService,
    writer: NetworkCIMWriter,
    hasCommon: (String) -> Boolean,
    addCommon: (String) -> Boolean
) : BaseServiceWriter<NetworkService, NetworkCIMWriter>(service, writer, hasCommon, addCommon) {

    override fun doSave(): Boolean {
        var status = true

        status = status and saveEach<CableInfo>(writer::save)
        status = status and saveEach<OverheadWireInfo>(writer::save)
        status = status and saveEach<PowerTransformerInfo>(writer::save)
        status = status and saveEach<TransformerTankInfo>(writer::save)
        status = status and saveEach<NoLoadTest>(writer::save)
        status = status and saveEach<OpenCircuitTest>(writer::save)
        status = status and saveEach<ShortCircuitTest>(writer::save)
        status = status and saveEach<ShuntCompensatorInfo>(writer::save)
        status = status and saveEach<SwitchInfo>(writer::save)
        status = status and saveEach<TransformerEndInfo>(writer::save)
        status = status and saveEach<AssetOwner>(writer::save)
        status = status and saveEach<Pole>(writer::save)
        status = status and saveEach<Streetlight>(writer::save)
        status = status and saveEach<Location>(writer::save)
        status = status and saveEach<Organisation> { trySaveCommon(writer::save, it) }
        status = status and saveEach<Meter>(writer::save)
        status = status and saveEach<UsagePoint>(writer::save)
        status = status and saveEach<OperationalRestriction>(writer::save)
        status = status and saveEach<FaultIndicator>(writer::save)
        status = status and saveEach<BaseVoltage>(writer::save)
        status = status and saveEach<ConnectivityNode>(writer::save)
        status = status and saveEach<Feeder>(writer::save)
        status = status and saveEach<GeographicalRegion>(writer::save)
        status = status and saveEach<Site>(writer::save)
        status = status and saveEach<SubGeographicalRegion>(writer::save)
        status = status and saveEach<Substation>(writer::save)
        status = status and saveEach<Terminal>(writer::save)
        status = status and saveEach<EquivalentBranch>(writer::save)
        status = status and saveEach<BatteryUnit>(writer::save)
        status = status and saveEach<PhotoVoltaicUnit>(writer::save)
        status = status and saveEach<PowerElectronicsWindUnit>(writer::save)
        status = status and saveEach<AcLineSegment>(writer::save)
        status = status and saveEach<Breaker>(writer::save)
        status = status and saveEach<LoadBreakSwitch>(writer::save)
        status = status and saveEach<BusbarSection>(writer::save)
        status = status and saveEach<Disconnector>(writer::save)
        status = status and saveEach<EnergyConsumer>(writer::save)
        status = status and saveEach<EnergyConsumerPhase>(writer::save)
        status = status and saveEach<EnergySource>(writer::save)
        status = status and saveEach<EnergySourcePhase>(writer::save)
        status = status and saveEach<Fuse>(writer::save)
        status = status and saveEach<Jumper>(writer::save)
        status = status and saveEach<Junction>(writer::save)
        status = status and saveEach<LinearShuntCompensator>(writer::save)
        status = status and saveEach<PerLengthSequenceImpedance>(writer::save)
        status = status and saveEach<PowerElectronicsConnection>(writer::save)
        status = status and saveEach<PowerElectronicsConnectionPhase>(writer::save)
        status = status and saveEach<PowerTransformer>(writer::save)
        status = status and saveEach<PowerTransformerEnd>(writer::save)
        status = status and saveEach<RatioTapChanger>(writer::save)
        status = status and saveEach<Recloser>(writer::save)
        status = status and saveEach<TransformerStarImpedance>(writer::save)
        status = status and saveEach<Circuit>(writer::save)
        status = status and saveEach<Loop>(writer::save)
        status = status and saveEach<LvFeeder>(writer::save)
        status = status and saveEach<Analog>(writer::save)
        status = status and saveEach<Accumulator>(writer::save)
        status = status and saveEach<Discrete>(writer::save)
        status = status and saveEach<Control>(writer::save)
        status = status and saveEach<RemoteControl>(writer::save)
        status = status and saveEach<RemoteSource>(writer::save)
        status = status and saveEach<CurrentTransformerInfo>(writer::save)
        status = status and saveEach<PotentialTransformerInfo>(writer::save)
        status = status and saveEach<CurrentTransformer>(writer::save)
        status = status and saveEach<PotentialTransformer>(writer::save)
        status = status and saveEach<RelayInfo>(writer::save)
        status = status and saveEach<CurrentRelay>(writer::save)
        status = status and saveEach<TapChangerControl>(writer::save)
        status = status and saveEach<EvChargingUnit>(writer::save)
        status = status and saveEach<DistanceRelay>(writer::save)
        status = status and saveEach<ProtectionRelayScheme>(writer::save)
        status = status and saveEach<ProtectionRelaySystem>(writer::save)
        status = status and saveEach<VoltageRelay>(writer::save)
        status = status and saveEach<Ground>(writer::save)
        status = status and saveEach<GroundDisconnector>(writer::save)
        status = status and saveEach<SeriesCompensator>(writer::save)

        return status
    }

}

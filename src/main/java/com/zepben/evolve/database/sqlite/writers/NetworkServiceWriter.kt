/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.database.sqlite.writers

import com.zepben.evolve.cim.iec61968.assetinfo.*
import com.zepben.evolve.cim.iec61968.assets.AssetOwner
import com.zepben.evolve.cim.iec61968.assets.Pole
import com.zepben.evolve.cim.iec61968.assets.Streetlight
import com.zepben.evolve.cim.iec61968.common.Location
import com.zepben.evolve.cim.iec61968.common.Organisation
import com.zepben.evolve.cim.iec61968.metering.Meter
import com.zepben.evolve.cim.iec61968.metering.UsagePoint
import com.zepben.evolve.cim.iec61968.operations.OperationalRestriction
import com.zepben.evolve.cim.iec61970.base.auxiliaryequipment.FaultIndicator
import com.zepben.evolve.cim.iec61970.base.core.*
import com.zepben.evolve.cim.iec61970.base.meas.Accumulator
import com.zepben.evolve.cim.iec61970.base.meas.Analog
import com.zepben.evolve.cim.iec61970.base.meas.Control
import com.zepben.evolve.cim.iec61970.base.meas.Discrete
import com.zepben.evolve.cim.iec61970.base.scada.RemoteControl
import com.zepben.evolve.cim.iec61970.base.scada.RemoteSource
import com.zepben.evolve.cim.iec61970.base.wires.*
import com.zepben.evolve.cim.iec61970.base.wires.generation.production.BatteryUnit
import com.zepben.evolve.cim.iec61970.base.wires.generation.production.PhotoVoltaicUnit
import com.zepben.evolve.cim.iec61970.base.wires.generation.production.PowerElectronicsWindUnit
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.Circuit
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.Loop
import com.zepben.evolve.services.network.NetworkService

class NetworkServiceWriter(hasCommon: (String) -> Boolean, addCommon: (String) -> Boolean) :
    BaseServiceWriter<NetworkService, NetworkCIMWriter>(hasCommon, addCommon) {

    override fun save(service: NetworkService, writer: NetworkCIMWriter): Boolean {
        var status = true

        service.sequenceOf<CableInfo>().forEach { status = status and validateSave(it, writer::save) }
        service.sequenceOf<OverheadWireInfo>().forEach { status = status and validateSave(it, writer::save) }
        service.sequenceOf<PowerTransformerInfo>().forEach { status = status and validateSave(it, writer::save) }
        service.sequenceOf<TransformerTankInfo>().forEach { status = status and validateSave(it, writer::save) }
        service.sequenceOf<TransformerEndInfo>().forEach { status = status and validateSave(it, writer::save) }
        service.sequenceOf<AssetOwner>().forEach { status = status and validateSave(it, writer::save) }
        service.sequenceOf<Pole>().forEach { status = status and validateSave(it, writer::save) }
        service.sequenceOf<Streetlight>().forEach { status = status and validateSave(it, writer::save) }
        service.sequenceOf<Location>().forEach { status = status and validateSave(it, writer::save) }
        service.sequenceOf<Organisation>().forEach { status = status and trySaveCommon(writer::save, it) }
        service.sequenceOf<Meter>().forEach { status = status and validateSave(it, writer::save) }
        service.sequenceOf<UsagePoint>().forEach { status = status and validateSave(it, writer::save) }
        service.sequenceOf<OperationalRestriction>().forEach { status = status and validateSave(it, writer::save) }
        service.sequenceOf<FaultIndicator>().forEach { status = status and validateSave(it, writer::save) }
        service.sequenceOf<BaseVoltage>().forEach { status = status and validateSave(it, writer::save) }
        service.sequenceOf<ConnectivityNode>().forEach { status = status and validateSave(it, writer::save) }
        service.sequenceOf<Feeder>().forEach { status = status and validateSave(it, writer::save) }
        service.sequenceOf<GeographicalRegion>().forEach { status = status and validateSave(it, writer::save) }
        service.sequenceOf<Site>().forEach { status = status and validateSave(it, writer::save) }
        service.sequenceOf<SubGeographicalRegion>().forEach { status = status and validateSave(it, writer::save) }
        service.sequenceOf<Substation>().forEach { status = status and validateSave(it, writer::save) }
        service.sequenceOf<Terminal>().forEach { status = status and validateSave(it, writer::save) }
        service.sequenceOf<BatteryUnit>().forEach { status = status and validateSave(it, writer::save) }
        service.sequenceOf<PhotoVoltaicUnit>().forEach { status = status and validateSave(it, writer::save) }
        service.sequenceOf<PowerElectronicsWindUnit>().forEach { status = status and validateSave(it, writer::save) }
        service.sequenceOf<AcLineSegment>().forEach { status = status and validateSave(it, writer::save) }
        service.sequenceOf<Breaker>().forEach { status = status and validateSave(it, writer::save) }
        service.sequenceOf<LoadBreakSwitch>().forEach { status = status and validateSave(it, writer::save) }
        service.sequenceOf<BusbarSection>().forEach { status = status and validateSave(it, writer::save) }
        service.sequenceOf<Disconnector>().forEach { status = status and validateSave(it, writer::save) }
        service.sequenceOf<EnergyConsumer>().forEach { status = status and validateSave(it, writer::save) }
        service.sequenceOf<EnergyConsumerPhase>().forEach { status = status and validateSave(it, writer::save) }
        service.sequenceOf<EnergySource>().forEach { status = status and validateSave(it, writer::save) }
        service.sequenceOf<EnergySourcePhase>().forEach { status = status and validateSave(it, writer::save) }
        service.sequenceOf<Fuse>().forEach { status = status and validateSave(it, writer::save) }
        service.sequenceOf<Jumper>().forEach { status = status and validateSave(it, writer::save) }
        service.sequenceOf<Junction>().forEach { status = status and validateSave(it, writer::save) }
        service.sequenceOf<LinearShuntCompensator>().forEach { status = status and validateSave(it, writer::save) }
        service.sequenceOf<PerLengthSequenceImpedance>().forEach { status = status and validateSave(it, writer::save) }
        service.sequenceOf<PowerElectronicsConnection>().forEach { status = status and validateSave(it, writer::save) }
        service.sequenceOf<PowerElectronicsConnectionPhase>().forEach { status = status and validateSave(it, writer::save) }
        service.sequenceOf<PowerTransformer>().forEach { status = status and validateSave(it, writer::save) }
        service.sequenceOf<PowerTransformerEnd>().forEach { status = status and validateSave(it, writer::save) }
        service.sequenceOf<RatioTapChanger>().forEach { status = status and validateSave(it, writer::save) }
        service.sequenceOf<Recloser>().forEach { status = status and validateSave(it, writer::save) }
        service.sequenceOf<TransformerStarImpedance>().forEach { status = status and validateSave(it, writer::save) }
        service.sequenceOf<Circuit>().forEach { status = status and validateSave(it, writer::save) }
        service.sequenceOf<Loop>().forEach { status = status and validateSave(it, writer::save) }
        service.sequenceOf<Analog>().forEach { status = status and validateSave(it, writer::save) }
        service.sequenceOf<Accumulator>().forEach { status = status and validateSave(it, writer::save) }
        service.sequenceOf<Discrete>().forEach { status = status and validateSave(it, writer::save) }
        service.sequenceOf<Control>().forEach { status = status and validateSave(it, writer::save) }
        service.sequenceOf<RemoteControl>().forEach { status = status and validateSave(it, writer::save) }
        service.sequenceOf<RemoteSource>().forEach { status = status and validateSave(it, writer::save) }

        return status
    }
}

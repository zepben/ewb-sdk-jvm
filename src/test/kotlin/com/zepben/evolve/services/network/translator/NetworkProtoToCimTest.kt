/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network.translator

import com.zepben.evolve.services.network.NetworkService
import com.zepben.protobuf.cim.iec61968.assetinfo.PowerTransformerInfo
import com.zepben.protobuf.cim.iec61968.assetinfo.TransformerEndInfo
import com.zepben.protobuf.cim.iec61968.assetinfo.TransformerTankInfo
import com.zepben.protobuf.cim.iec61968.assets.Pole
import com.zepben.protobuf.cim.iec61968.metering.Meter
import com.zepben.protobuf.cim.iec61970.base.core.Substation
import com.zepben.protobuf.cim.iec61970.base.meas.Accumulator
import com.zepben.protobuf.cim.iec61970.base.meas.Analog
import com.zepben.protobuf.cim.iec61970.base.meas.Discrete
import com.zepben.protobuf.cim.iec61970.base.wires.PowerTransformerEnd
import com.zepben.protobuf.cim.iec61970.base.wires.TransformerStarImpedance
import com.zepben.protobuf.cim.iec61970.infiec61970.feeder.Circuit
import com.zepben.protobuf.cim.iec61970.infiec61970.feeder.Loop
import org.junit.jupiter.api.Test
import com.zepben.protobuf.cim.iec61970.base.wires.BusbarSection as PBBusbarSection
import com.zepben.protobuf.cim.iec61970.base.wires.LoadBreakSwitch as PBLoadBreakSwitch
import com.zepben.protobuf.cim.iec61970.base.wires.PowerTransformer as PBPowerTransformer

class NetworkProtoToCimTest {
    private val network = NetworkService()
    private val translator = NetworkProtoToCim(network)
    private val validator = NetworkProtoToCimTestValidator(network)

    @Test
    internal fun convertsBusbarSection() {
        val pb = PBBusbarSection.newBuilder()
        validator.validate(pb) { translator.addFromPb(pb.build())!! }
    }

    @Test
    internal fun convertsPowerTransformer() {
        val pb = PBPowerTransformer.newBuilder()
        validator.validate(pb) { translator.addFromPb(pb.build())!! }
    }

    @Test
    internal fun convertsMeter() {
        val pb = Meter.newBuilder()
        validator.validate(pb) { translator.addFromPb(pb.build())!! }
    }

    @Test
    internal fun convertsSubstation() {
        val pb = Substation.newBuilder()
        validator.validate(pb) { translator.addFromPb(pb.build())!! }
    }

    @Test
    internal fun convertsCircuit() {
        val pb = Circuit.newBuilder()
        validator.validate(pb) { translator.addFromPb(pb.build())!! }
    }

    @Test
    internal fun convertsLoop() {
        val pb = Loop.newBuilder()
        validator.validate(pb) { translator.addFromPb(pb.build())!! }
    }

    @Test
    internal fun convertsPole() {
        val pb = Pole.newBuilder()
        validator.validate(pb) { translator.addFromPb(pb.build())!! }
    }

    @Test
    internal fun convertsAnalog() {
        val pb = Analog.newBuilder()
        validator.validate(pb) { translator.addFromPb(pb.build())!! }
    }

    @Test
    internal fun convertsAccumulator() {
        val pb = Accumulator.newBuilder()
        validator.validate(pb) { translator.addFromPb(pb.build())!! }
    }

    @Test
    internal fun convertsDiscrete() {
        val pb = Discrete.newBuilder()
        validator.validate(pb) { translator.addFromPb(pb.build())!! }
    }

    @Test
    internal fun convertsLoadBreakSwitch() {
        val pb = PBLoadBreakSwitch.newBuilder()
        validator.validate(pb) { translator.addFromPb(pb.build())!! }
    }

    @Test
    internal fun convertsPowerTransformerInfo() {
        val pb = PowerTransformerInfo.newBuilder()
        validator.validate(pb) { translator.addFromPb(pb.build())!! }
    }

    @Test
    internal fun convertsTransformerTankInfo() {
        val pb = TransformerTankInfo.newBuilder()
        validator.validate(pb) { translator.addFromPb(pb.build())!! }
    }

    @Test
    internal fun convertsTransformerEndInfo() {
        val pb = TransformerEndInfo.newBuilder()
        validator.validate(pb) { translator.addFromPb(pb.build())!! }
    }

    @Test
    internal fun convertsTransformerStarImpedance() {
        val pb = TransformerStarImpedance.newBuilder()
        validator.validate(pb) { translator.addFromPb(pb.build())!! }
    }

    @Test
    internal fun convertsPowerTransformerEnd() {
        val pb = PowerTransformerEnd.newBuilder()
        validator.validate(pb) { translator.addFromPb(pb.build())!! }
    }

}

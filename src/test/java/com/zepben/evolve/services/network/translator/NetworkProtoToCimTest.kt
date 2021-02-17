/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network.translator

import com.zepben.evolve.services.network.NetworkService
import org.junit.jupiter.api.Test
import com.zepben.protobuf.cim.iec61968.assets.Pole as PBPole
import com.zepben.protobuf.cim.iec61968.metering.Meter as PBMeter
import com.zepben.protobuf.cim.iec61970.base.core.Substation as PBSubstation
import com.zepben.protobuf.cim.iec61970.base.meas.Accumulator as PBAccumulator
import com.zepben.protobuf.cim.iec61970.base.meas.Analog as PBAnalog
import com.zepben.protobuf.cim.iec61970.base.meas.Discrete as PBDiscrete
import com.zepben.protobuf.cim.iec61970.base.wires.BusbarSection as PBBusbarSection
import com.zepben.protobuf.cim.iec61970.base.wires.PowerTransformer as PBPowerTransformer
import com.zepben.protobuf.cim.iec61970.base.wires.LoadBreakSwitch as PBLoadBreakSwitch
import com.zepben.protobuf.cim.iec61970.infiec61970.feeder.Circuit as PBCircuit
import com.zepben.protobuf.cim.iec61970.infiec61970.feeder.Loop as PBLoop

class NetworkProtoToCimTest {
    private val network = NetworkService()
    private val translator = NetworkProtoToCim(network)
    private val validator = NetworkProtoToCimTestValidator(network)

    @Test
    internal fun testFromPbBusbarSection() {
        val pbBusbarSection = PBBusbarSection.newBuilder()
        validator.validate(pbBusbarSection) { translator.addFromPb(pbBusbarSection.build())!! }
    }

    @Test
    internal fun testFromPbPowerTransformer() {
        val pbPowerTransformer = PBPowerTransformer.newBuilder()
        validator.validate(pbPowerTransformer) { translator.addFromPb(pbPowerTransformer.build())!! }
    }

    @Test
    internal fun testFromPbMeter() {
        val pbMeter = PBMeter.newBuilder()
        validator.validate(pbMeter) { translator.addFromPb(pbMeter.build())!! }
    }

    @Test
    internal fun testFromPbSubstation() {
        val pbSubstation = PBSubstation.newBuilder()
        validator.validate(pbSubstation) { translator.addFromPb(pbSubstation.build())!! }
    }

    @Test
    internal fun testFromPbCircuit() {
        val pbCircuit = PBCircuit.newBuilder()
        validator.validate(pbCircuit) { translator.addFromPb(pbCircuit.build())!! }
    }

    @Test
    internal fun testFromPbLoop() {
        val pbLoop = PBLoop.newBuilder()
        validator.validate(pbLoop) { translator.addFromPb(pbLoop.build())!! }
    }

    @Test
    internal fun testFromPbPole() {
        val pbPole = PBPole.newBuilder()
        validator.validate(pbPole) { translator.addFromPb(pbPole.build())!! }
    }

    @Test
    internal fun testFromPbAnalog() {
        val pb = PBAnalog.newBuilder()
        validator.validate(pb) { translator.addFromPb(pb.build())!! }
    }

    @Test
    internal fun testFromPbAccumulator() {
        val pb = PBAccumulator.newBuilder()
        validator.validate(pb) { translator.addFromPb(pb.build())!! }
    }

    @Test
    internal fun testFromPbDiscrete() {
        val pb = PBDiscrete.newBuilder()
        validator.validate(pb) { translator.addFromPb(pb.build())!! }
    }

    @Test
    internal fun testFromPbLoadBreakSwitch() {
        val pb = PBLoadBreakSwitch.newBuilder()
        validator.validate(pb) { translator.addFromPb(pb.build())!! }
    }

}

/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 * This file is part of evolve-sdk-jvm.
 *
 * evolve-sdk-jvm is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * evolve-sdk-jvm is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with evolve-sdk-jvm.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.zepben.cimbend.network.model

import com.zepben.cimbend.network.NetworkService
import org.junit.jupiter.api.Test
import com.zepben.protobuf.cim.iec61968.assets.Pole as PBPole
import com.zepben.protobuf.cim.iec61968.metering.Meter as PBMeter
import com.zepben.protobuf.cim.iec61970.base.core.Substation as PBSubstation
import com.zepben.protobuf.cim.iec61970.base.meas.Accumulator as PBAccumulator
import com.zepben.protobuf.cim.iec61970.base.meas.Analog as PBAnalog
import com.zepben.protobuf.cim.iec61970.base.meas.Discrete as PBDiscrete
import com.zepben.protobuf.cim.iec61970.infiec61970.feeder.Circuit as PBCircuit
import com.zepben.protobuf.cim.iec61970.infiec61970.feeder.Loop as PBLoop

class NetworkProtoToCimTest {
    private val network = NetworkService()
    private val translator = NetworkProtoToCim(network)
    private val validator = NetworkProtoToCimTestValidator(network)

    @Test
    internal fun testFromPbMeter() {
        val pbMeter = PBMeter.newBuilder()
        validator.validate(pbMeter) { translator.addFromPb(pbMeter.build()) }
    }

    @Test
    internal fun testFromPbSubstation() {
        val pbSubstation = PBSubstation.newBuilder()
        validator.validate(pbSubstation) { translator.addFromPb(pbSubstation.build()) }
    }

    @Test
    internal fun testFromPbCircuit() {
        val pbCircuit = PBCircuit.newBuilder()
        validator.validate(pbCircuit) { translator.addFromPb(pbCircuit.build()) }
    }

    @Test
    internal fun testFromPbLoop() {
        val pbLoop = PBLoop.newBuilder()
        validator.validate(pbLoop) { translator.addFromPb(pbLoop.build()) }
    }

    @Test
    internal fun testFromPbPole() {
        val pbPole = PBPole.newBuilder()
        validator.validate(pbPole) { translator.addFromPb(pbPole.build()) }
    }

    @Test
    internal fun testFromPbAnalog() {
        val pb = PBAnalog.newBuilder()
        validator.validate(pb) { translator.addFromPb(pb.build()) }
    }

    @Test
    internal fun testFromPbAccumulator() {
        val pb = PBAccumulator.newBuilder()
        validator.validate(pb) { translator.addFromPb(pb.build()) }
    }

    @Test
    internal fun testFromPbDiscrete() {
        val pb = PBDiscrete.newBuilder()
        validator.validate(pb) { translator.addFromPb(pb.build()) }
    }
}

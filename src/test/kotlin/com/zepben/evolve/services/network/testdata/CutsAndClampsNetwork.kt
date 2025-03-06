/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.testdata

import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.cim.iec61970.base.wires.AcLineSegment
import com.zepben.evolve.cim.iec61970.base.wires.Clamp
import com.zepben.evolve.cim.iec61970.base.wires.Cut
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.testing.TestNetworkBuilder

object CutsAndClampsNetwork {

    fun mulitiCutAndClampNetwork(): TestNetworkBuilder {
        //
        //          2                     2
        //          c3          2         c7          2
        //          1           c5        1           c9
        //          1 clamp1    1         1 clamp3    1
        //          |           |         |           |
        // 1 b0 21--*--*1 cut1 2*--*--c1--*--*1 cut2 2*--*--21 b2 2
        //             |           |         |           |
        //             1           1 clamp2  1           1 clamp4
        //             c4          1         c8          1
        //             2           c6        2           c10
        //                         2                     2
        //
        val builder = TestNetworkBuilder()
            .fromBreaker() // b0
            .toAcls() // c1
            .toBreaker() // b2
            .fromAcls() // c3
            .fromAcls() // c4
            .fromAcls() // c5
            .fromAcls() // c6
            .fromAcls() // c7
            .fromAcls() // c8
            .fromAcls() // c9
            .fromAcls() // c10

        val network = builder.network

        val segment: AcLineSegment = network["c1"]!!

        val clamp1 = segment.withClamp(network, 1.0)
        val cut1 = segment.withCut(network, 2.0)
        val clamp2 = segment.withClamp(network, 3.0)
        val clamp3 = segment.withClamp(network, 4.0)
        val cut2 = segment.withCut(network, 5.0)
        val clamp4 = segment.withClamp(network, 6.0)

        network.connect(clamp1.t1, network.get<ConductingEquipment>("c3")!!.t1)
        network.connect(cut1.t1, network.get<ConductingEquipment>("c4")!!.t1)
        network.connect(cut1.t2, network.get<ConductingEquipment>("c5")!!.t1)
        network.connect(clamp2.t1, network.get<ConductingEquipment>("c6")!!.t1)
        network.connect(clamp3.t1, network.get<ConductingEquipment>("c7")!!.t1)
        network.connect(cut2.t1, network.get<ConductingEquipment>("c8")!!.t1)
        network.connect(cut2.t2, network.get<ConductingEquipment>("c9")!!.t1)
        network.connect(clamp4.t1, network.get<ConductingEquipment>("c10")!!.t1)

        return builder
    }

    fun AcLineSegment.withClamp(network: NetworkService, lengthFromTerminal1: Double?): Clamp {
        val clamp = Clamp("clamp${numClamps() + 1}").apply {
            addTerminal(Terminal("$mRID-t1"))
            this.lengthFromTerminal1 = lengthFromTerminal1
        }

        addClamp(clamp)
        network.add(clamp)

        return clamp
    }

    fun AcLineSegment.withCut(network: NetworkService, lengthFromTerminal1: Double?): Cut {
        val cut = Cut("cut${numCuts() + 1}").apply {
            addTerminal(Terminal("$mRID-t1"))
            addTerminal(Terminal("$mRID-t2"))
            this.lengthFromTerminal1 = lengthFromTerminal1
        }

        addCut(cut)
        network.add(cut)
        return cut
    }

}

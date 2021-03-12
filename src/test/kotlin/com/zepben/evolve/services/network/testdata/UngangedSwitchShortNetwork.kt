/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network.testdata

import com.zepben.evolve.cim.iec61970.base.core.PhaseCode
import com.zepben.evolve.services.network.NetworkService

object UngangedSwitchShortNetwork {

    //     n0     c0     n1     c1     n2
    // A  ----00======10-/ -10======10---- A
    // B  ----00======10----10======10---- B
    // C  ----00======10-/ -10======10---- C
    // N  ----00======10----10======10---- N
    //
    fun create() = NetworkService().also { network ->
        val node0 = createSourceForConnecting(network, "node0", 1, PhaseCode.ABCN)
        val node1 = createSwitchForConnecting(network, "node1", 2, PhaseCode.ABCN, true, false, true, false)
        val node2 = createSourceForConnecting(network, "node2", 1, PhaseCode.ABCN)

        val acLineSegment0 = createAcLineSegmentForConnecting(network, "acLineSegment0", PhaseCode.ABCN)
        val acLineSegment1 = createAcLineSegmentForConnecting(network, "acLineSegment1", PhaseCode.ABCN)

        // Connect up a network so we can check connectivity.
        network.connect(node0.getTerminal(1)!!, acLineSegment0.getTerminal(1)!!)
        network.connect(node1.getTerminal(1)!!, acLineSegment0.getTerminal(2)!!)
        network.connect(node1.getTerminal(2)!!, acLineSegment1.getTerminal(1)!!)
        network.connect(node2.getTerminal(1)!!, acLineSegment1.getTerminal(2)!!)
    }

}

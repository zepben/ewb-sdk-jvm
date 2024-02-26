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

object LoopingNetwork {

    //
    // n0   ac0   n1   ac1   n2   ac2   n3
    // *11------21*21------21*21------21*
    //            3                     2
    //            1                     1
    //        ac3 |                     | ac4
    //            2                     2
    //            1  ac5                1
    //         n4 *21------21* n5       * n6 (open)
    //            3                     2
    //            1                     1
    //        ac6 |                     | ac7
    //            2                     2
    //            1    ac8   n8   ac9   2
    //         n7 *21------21*21------21* n9
    //            3
    //            1    ac11
    //            |      /--21* n11
    //       ac10 |     /     2
    //            |    /      1
    //            2   /       | ac13
    //            1  /        2
    //        n10 *21 ac12    2   ac14
    //            31--------21*31------21* n13
    //             1        2 n12
    //              \       |
    //               \      1 ac16
    //                \     2
    //                 \--21* n14
    //               ac15
    //
    fun create(): NetworkService = NetworkService().also { network ->
        val node0 = createSourceForConnecting(network, "node0", 1, PhaseCode.ABCN)
        val node1 = createNodeForConnecting(network, "node1", 3, PhaseCode.ABCN)
        val node2 = createNodeForConnecting(network, "node2", 2, PhaseCode.ABCN)
        val node3 = createNodeForConnecting(network, "node3", 2, PhaseCode.ABCN)
        val node4 = createNodeForConnecting(network, "node4", 3, PhaseCode.ABCN)
        val node5 = createNodeForConnecting(network, "node5", 1, PhaseCode.ABCN)
        val node6 = createSwitchForConnecting(network, "node6", 2, true, true, true, true, nominalPhases = PhaseCode.ABCN)
        val node7 = createNodeForConnecting(network, "node7", 3, PhaseCode.ABCN)
        val node8 = createNodeForConnecting(network, "node8", 2, PhaseCode.ABCN)
        val node9 = createNodeForConnecting(network, "node9", 2, PhaseCode.ABCN)
        val node10 = createNodeForConnecting(network, "node10", 3, PhaseCode.ABCN)
        val node11 = createNodeForConnecting(network, "node11", 2, PhaseCode.ABCN)
        val node12 = createNodeForConnecting(network, "node12", 3, PhaseCode.ABCN)
        val node13 = createNodeForConnecting(network, "node13", 1, PhaseCode.ABCN)
        val node14 = createNodeForConnecting(network, "node14", 2, PhaseCode.ABCN)

        val acLineSegment0 = createAcLineSegmentForConnecting(network, "acLineSegment0", PhaseCode.ABCN, 10000.0, "abc")
        val acLineSegment1 = createAcLineSegmentForConnecting(network, "acLineSegment1", PhaseCode.ABCN, 2000.0, "abc")
        val acLineSegment2 = createAcLineSegmentForConnecting(network, "acLineSegment2", PhaseCode.ABCN, 500.0, "abc")
        val acLineSegment3 = createAcLineSegmentForConnecting(network, "acLineSegment3", PhaseCode.ABCN, 100.0, "abc")
        val acLineSegment4 = createAcLineSegmentForConnecting(network, "acLineSegment4", PhaseCode.ABCN, 10.0, "abc")
        val acLineSegment5 = createAcLineSegmentForConnecting(network, "acLineSegment5", PhaseCode.ABCN, 1.0, "abc")
        val acLineSegment6 = createAcLineSegmentForConnecting(network, "acLineSegment6", PhaseCode.ABCN, 1000.0, "abc")
        val acLineSegment7 = createAcLineSegmentForConnecting(network, "acLineSegment7", PhaseCode.ABCN, 10.0, "abc")
        val acLineSegment8 = createAcLineSegmentForConnecting(network, "acLineSegment8", PhaseCode.ABCN, 750.0, "abc")
        val acLineSegment9 = createAcLineSegmentForConnecting(network, "acLineSegment9", PhaseCode.ABCN, 3025.0, "abc")
        val acLineSegment10 = createAcLineSegmentForConnecting(network, "acLineSegment10", PhaseCode.ABCN, 100.0, "abc")
        val acLineSegment11 = createAcLineSegmentForConnecting(network, "acLineSegment11", PhaseCode.ABCN, 100.0, "abc")
        val acLineSegment12 = createAcLineSegmentForConnecting(network, "acLineSegment12", PhaseCode.ABCN, 100.0, "abc")
        val acLineSegment13 = createAcLineSegmentForConnecting(network, "acLineSegment13", PhaseCode.ABCN, 100.0, "abc")
        val acLineSegment14 = createAcLineSegmentForConnecting(network, "acLineSegment14", PhaseCode.ABCN, 100.0, "abc")
        val acLineSegment15 = createAcLineSegmentForConnecting(network, "acLineSegment15", PhaseCode.ABCN, 100.0, "abc")
        val acLineSegment16 = createAcLineSegmentForConnecting(network, "acLineSegment16", PhaseCode.ABCN, 100.0, "abc")

        // Connect up a network so we can check connectivity.
        network.connect(node0.getTerminal(1)!!, acLineSegment0.getTerminal(1)!!)
        network.connect(node1.getTerminal(1)!!, acLineSegment0.getTerminal(2)!!)
        network.connect(node1.getTerminal(2)!!, acLineSegment1.getTerminal(1)!!)
        network.connect(node1.getTerminal(3)!!, acLineSegment3.getTerminal(1)!!)
        network.connect(node2.getTerminal(1)!!, acLineSegment1.getTerminal(2)!!)
        network.connect(node2.getTerminal(2)!!, acLineSegment2.getTerminal(1)!!)
        network.connect(node3.getTerminal(1)!!, acLineSegment2.getTerminal(2)!!)
        network.connect(node3.getTerminal(2)!!, acLineSegment4.getTerminal(1)!!)
        network.connect(node4.getTerminal(1)!!, acLineSegment3.getTerminal(2)!!)
        network.connect(node4.getTerminal(2)!!, acLineSegment5.getTerminal(1)!!)
        network.connect(node4.getTerminal(3)!!, acLineSegment6.getTerminal(1)!!)
        network.connect(node5.getTerminal(1)!!, acLineSegment5.getTerminal(2)!!)
        network.connect(node6.getTerminal(1)!!, acLineSegment4.getTerminal(2)!!)
        network.connect(node6.getTerminal(2)!!, acLineSegment7.getTerminal(1)!!)
        network.connect(node7.getTerminal(1)!!, acLineSegment6.getTerminal(2)!!)
        network.connect(node7.getTerminal(2)!!, acLineSegment8.getTerminal(1)!!)
        network.connect(node7.getTerminal(3)!!, acLineSegment10.getTerminal(1)!!)
        network.connect(node8.getTerminal(1)!!, acLineSegment8.getTerminal(2)!!)
        network.connect(node8.getTerminal(2)!!, acLineSegment9.getTerminal(1)!!)
        network.connect(node9.getTerminal(1)!!, acLineSegment9.getTerminal(2)!!)
        network.connect(node9.getTerminal(2)!!, acLineSegment7.getTerminal(2)!!)
        network.connect(node10.getTerminal(1)!!, acLineSegment10.getTerminal(2)!!)
        network.connect(node10.getTerminal(2)!!, acLineSegment11.getTerminal(1)!!)
        network.connect(node10.getTerminal(3)!!, acLineSegment12.getTerminal(1)!!)
        network.connect(node10.getTerminal(3)!!, acLineSegment15.getTerminal(1)!!)
        network.connect(node11.getTerminal(1)!!, acLineSegment11.getTerminal(2)!!)
        network.connect(node11.getTerminal(2)!!, acLineSegment13.getTerminal(1)!!)
        network.connect(node12.getTerminal(1)!!, acLineSegment12.getTerminal(2)!!)
        network.connect(node12.getTerminal(1)!!, acLineSegment16.getTerminal(2)!!)
        network.connect(node12.getTerminal(2)!!, acLineSegment13.getTerminal(2)!!)
        network.connect(node12.getTerminal(3)!!, acLineSegment14.getTerminal(1)!!)
        network.connect(node13.getTerminal(1)!!, acLineSegment14.getTerminal(2)!!)
        network.connect(node14.getTerminal(1)!!, acLineSegment15.getTerminal(2)!!)
        network.connect(node14.getTerminal(2)!!, acLineSegment16.getTerminal(1)!!)
    }

}

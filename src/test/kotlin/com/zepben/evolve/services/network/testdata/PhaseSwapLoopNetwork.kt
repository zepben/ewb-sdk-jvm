/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network.testdata

import com.zepben.evolve.cim.iec61970.base.core.PhaseCode
import com.zepben.evolve.services.network.NetworkService

object PhaseSwapLoopNetwork {

    //
    //  n0 ac0        ac1     n1   ac2        ac3   n2
    //  *==ABCN==+====ABCN====*====ABCN====+==ABCN==*
    //           |                         |
    //       ac4 AB                        BC ac9
    //           |                         |
    //        n3 *                         * n7
    //           |                         |
    //       ac5 XY                        XY ac8
    //           |            n5           |
    //        n4 *-----XY-----*-----XY-----* n6 (open)
    //           |     ac6    |     ac7
    //      ac10 X            Y ac11
    //           |            |
    //        n8 *            * n9
    //
    fun create() = NetworkService().also { network ->
        val node0 = createSourceForConnecting(network, "node0", 1, PhaseCode.ABCN)
        val node1 = createNodeForConnecting(network, "node1", 2, PhaseCode.ABCN)
        val node2 = createNodeForConnecting(network, "node2", 1, PhaseCode.ABCN)
        val node3 = createNodeForConnecting(network, "node3", 2, PhaseCode.AB)
        val node4 = createNodeForConnecting(network, "node4", 3, PhaseCode.XY)
        val node5 = createNodeForConnecting(network, "node5", 3, PhaseCode.XY)
        val node6 = createSwitchForConnecting(network, "node6", 2, true, true, nominalPhases = PhaseCode.XY)
        val node7 = createNodeForConnecting(network, "node7", 2, PhaseCode.BC)
        val node8 = createNodeForConnecting(network, "node8", 1, PhaseCode.X)
        val node9 = createNodeForConnecting(network, "node9", 1, PhaseCode.Y)

        val acLineSegment0 = createAcLineSegmentForConnecting(network, "acLineSegment0", PhaseCode.ABCN)
        val acLineSegment1 = createAcLineSegmentForConnecting(network, "acLineSegment1", PhaseCode.ABCN)
        val acLineSegment2 = createAcLineSegmentForConnecting(network, "acLineSegment2", PhaseCode.ABCN)
        val acLineSegment3 = createAcLineSegmentForConnecting(network, "acLineSegment3", PhaseCode.ABCN)
        val acLineSegment4 = createAcLineSegmentForConnecting(network, "acLineSegment4", PhaseCode.AB)
        val acLineSegment5 = createAcLineSegmentForConnecting(network, "acLineSegment5", PhaseCode.XY)
        val acLineSegment6 = createAcLineSegmentForConnecting(network, "acLineSegment6", PhaseCode.XY)
        val acLineSegment7 = createAcLineSegmentForConnecting(network, "acLineSegment7", PhaseCode.XY)
        val acLineSegment8 = createAcLineSegmentForConnecting(network, "acLineSegment8", PhaseCode.XY)
        val acLineSegment9 = createAcLineSegmentForConnecting(network, "acLineSegment9", PhaseCode.BC)
        val acLineSegment10 = createAcLineSegmentForConnecting(network, "acLineSegment10", PhaseCode.X)
        val acLineSegment11 = createAcLineSegmentForConnecting(network, "acLineSegment11", PhaseCode.Y)

        // Connect up a network so we can check connectivity.
        network.connect(node0.getTerminal(1)!!, "cn_0")
        network.connect(acLineSegment0.getTerminal(1)!!, "cn_0")
        network.connect(acLineSegment0.getTerminal(2)!!, "cn_1")
        network.connect(acLineSegment1.getTerminal(1)!!, "cn_1")
        network.connect(acLineSegment1.getTerminal(2)!!, "cn_2")
        network.connect(node1.getTerminal(1)!!, "cn_2")
        network.connect(node1.getTerminal(2)!!, "cn_3")
        network.connect(acLineSegment2.getTerminal(1)!!, "cn_3")
        network.connect(acLineSegment2.getTerminal(2)!!, "cn_4")
        network.connect(acLineSegment3.getTerminal(1)!!, "cn_4")
        network.connect(acLineSegment3.getTerminal(2)!!, "cn_5")
        network.connect(node2.getTerminal(1)!!, "cn_5")
        network.connect(acLineSegment4.getTerminal(1)!!, "cn_1")
        network.connect(acLineSegment4.getTerminal(2)!!, "cn_6")
        network.connect(node3.getTerminal(1)!!, "cn_6")
        network.connect(node3.getTerminal(2)!!, "cn_7")
        network.connect(acLineSegment5.getTerminal(1)!!, "cn_7")
        network.connect(acLineSegment5.getTerminal(2)!!, "cn_8")
        network.connect(node4.getTerminal(1)!!, "cn_8")
        network.connect(node4.getTerminal(2)!!, "cn_9")
        network.connect(node4.getTerminal(3)!!, "cn_16")
        network.connect(acLineSegment6.getTerminal(1)!!, "cn_9")
        network.connect(acLineSegment6.getTerminal(2)!!, "cn_10")
        network.connect(node5.getTerminal(1)!!, "cn_10")
        network.connect(node5.getTerminal(2)!!, "cn_11")
        network.connect(node5.getTerminal(3)!!, "cn_18")
        network.connect(acLineSegment7.getTerminal(1)!!, "cn_11")
        network.connect(acLineSegment7.getTerminal(2)!!, "cn_12")
        network.connect(node6.getTerminal(1)!!, "cn_12")
        network.connect(node6.getTerminal(2)!!, "cn_13")
        network.connect(acLineSegment8.getTerminal(1)!!, "cn_13")
        network.connect(acLineSegment8.getTerminal(2)!!, "cn_14")
        network.connect(node7.getTerminal(1)!!, "cn_14")
        network.connect(node7.getTerminal(2)!!, "cn_15")
        network.connect(acLineSegment9.getTerminal(1)!!, "cn_15")
        network.connect(acLineSegment9.getTerminal(2)!!, "cn_4")
        network.connect(acLineSegment10.getTerminal(1)!!, "cn_16")
        network.connect(acLineSegment10.getTerminal(2)!!, "cn_17")
        network.connect(node8.getTerminal(1)!!, "cn_17")
        network.connect(acLineSegment11.getTerminal(1)!!, "cn_18")
        network.connect(acLineSegment11.getTerminal(2)!!, "cn_19")
        network.connect(node9.getTerminal(1)!!, "cn_19")
    }

}

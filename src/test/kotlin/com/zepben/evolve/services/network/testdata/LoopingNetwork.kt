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
    // j0   ac0   j1   ac1   j2   ac2   j3
    // *11------21*21------21*21------21*
    //            3                     2
    //            1                     1
    //        ac3 |                     | ac4
    //            2                     2
    //            1  ac5                1
    //         j4 *21------21* j5       * j6 (open)
    //            3                     2
    //            1                     1
    //        ac6 |                     | ac7
    //            2                     2
    //            1    ac8   j8   ac9   2
    //         j7 *21------21*21------21* j9
    //            3
    //            1    ac11
    //            |      /--21* j11
    //       ac10 |     /     2
    //            |    /      1
    //            2   /       | ac13
    //            1  /        2
    //        j10 *21 ac12    2   ac14
    //            31--------21*31------21* j13
    //             1        2 j12
    //              \       |
    //               \      1 ac16
    //                \     2
    //                 \--21* j14
    //               ac15
    //
    fun create(): NetworkService = NetworkService().also { network ->
        val j0 = createSourceForConnecting(network, "j0", 1, PhaseCode.ABCN)
        val j1 = createJunctionForConnecting(network, "j1", 3, PhaseCode.ABCN)
        val j2 = createJunctionForConnecting(network, "j2", 2, PhaseCode.ABCN)
        val j3 = createJunctionForConnecting(network, "j3", 2, PhaseCode.ABCN)
        val j4 = createJunctionForConnecting(network, "j4", 3, PhaseCode.ABCN)
        val j5 = createJunctionForConnecting(network, "j5", 1, PhaseCode.ABCN)
        val j6 = createSwitchForConnecting(network, "j6", 2, true, true, true, true, nominalPhases = PhaseCode.ABCN)
        val j7 = createJunctionForConnecting(network, "j7", 3, PhaseCode.ABCN)
        val j8 = createJunctionForConnecting(network, "j8", 2, PhaseCode.ABCN)
        val j9 = createJunctionForConnecting(network, "j9", 2, PhaseCode.ABCN)
        val j10 = createJunctionForConnecting(network, "j10", 3, PhaseCode.ABCN)
        val j11 = createJunctionForConnecting(network, "j11", 2, PhaseCode.ABCN)
        val j12 = createJunctionForConnecting(network, "j12", 3, PhaseCode.ABCN)
        val j13 = createJunctionForConnecting(network, "j13", 1, PhaseCode.ABCN)
        val j14 = createJunctionForConnecting(network, "j14", 2, PhaseCode.ABCN)

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
        network.connect(j0.t1, acLineSegment0.t1)
        network.connect(j1.t1, acLineSegment0.t2)
        network.connect(j1.t2, acLineSegment1.t1)
        network.connect(j1.t3, acLineSegment3.t1)
        network.connect(j2.t1, acLineSegment1.t2)
        network.connect(j2.t2, acLineSegment2.t1)
        network.connect(j3.t1, acLineSegment2.t2)
        network.connect(j3.t2, acLineSegment4.t1)
        network.connect(j4.t1, acLineSegment3.t2)
        network.connect(j4.t2, acLineSegment5.t1)
        network.connect(j4.t3, acLineSegment6.t1)
        network.connect(j5.t1, acLineSegment5.t2)
        network.connect(j6.t1, acLineSegment4.t2)
        network.connect(j6.t2, acLineSegment7.t1)
        network.connect(j7.t1, acLineSegment6.t2)
        network.connect(j7.t2, acLineSegment8.t1)
        network.connect(j7.t3, acLineSegment10.t1)
        network.connect(j8.t1, acLineSegment8.t2)
        network.connect(j8.t2, acLineSegment9.t1)
        network.connect(j9.t1, acLineSegment9.t2)
        network.connect(j9.t2, acLineSegment7.t2)
        network.connect(j10.t1, acLineSegment10.t2)
        network.connect(j10.t2, acLineSegment11.t1)
        network.connect(j10.t3, acLineSegment12.t1)
        network.connect(j10.t3, acLineSegment15.t1)
        network.connect(j11.t1, acLineSegment11.t2)
        network.connect(j11.t2, acLineSegment13.t1)
        network.connect(j12.t1, acLineSegment12.t2)
        network.connect(j12.t1, acLineSegment16.t2)
        network.connect(j12.t2, acLineSegment13.t2)
        network.connect(j12.t3, acLineSegment14.t1)
        network.connect(j13.t1, acLineSegment14.t2)
        network.connect(j14.t1, acLineSegment15.t2)
        network.connect(j14.t2, acLineSegment16.t1)
    }

}

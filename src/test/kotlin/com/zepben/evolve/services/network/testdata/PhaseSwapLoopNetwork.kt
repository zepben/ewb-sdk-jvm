/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.testdata

import com.zepben.evolve.cim.iec61970.base.core.Feeder
import com.zepben.evolve.cim.iec61970.base.core.PhaseCode
import com.zepben.evolve.services.network.NetworkService

object PhaseSwapLoopNetwork {

    //
    //  j0 ac0        ac1     j1   ac2        ac3   j2
    //  *==ABCN==+====ABCN====*====ABCN====+==ABCN==*
    //           |                         |
    //       ac4 AB                        BC ac9
    //           |                         |
    //        j3 *                         * j7
    //           |                         |
    //       ac5 XY                        XY ac8
    //           |            j5           |
    //        j4 *-----XY-----*-----XY-----* j6 (open)
    //           |     ac6    |     ac7
    //      ac10 X            Y ac11
    //           |            |
    //        j8 *            * j9
    //
    fun create(): NetworkService = NetworkService().also { network ->
        val j0 = createSourceForConnecting(network, "j0", 1, PhaseCode.ABCN)
        val j1 = createJunctionForConnecting(network, "j1", 2, PhaseCode.ABCN)
        val j2 = createJunctionForConnecting(network, "j2", 1, PhaseCode.ABCN)
        val j3 = createJunctionForConnecting(network, "j3", 2, PhaseCode.AB)
        val j4 = createJunctionForConnecting(network, "j4", 3, PhaseCode.XY)
        val j5 = createJunctionForConnecting(network, "j5", 3, PhaseCode.XY)
        val j6 = createSwitchForConnecting(network, "j6", 2, true, true, nominalPhases = PhaseCode.XY)
        val j7 = createJunctionForConnecting(network, "j7", 2, PhaseCode.BC)
        val j8 = createJunctionForConnecting(network, "j8", 1, PhaseCode.X)
        val j9 = createJunctionForConnecting(network, "j9", 1, PhaseCode.Y)

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

        Feeder("fdr").apply { normalHeadTerminal = j0.t1 }.also { network.add(it) }

        // Connect up a network so we can check connectivity.
        network.connect(j0.t1, "cn_0")
        network.connect(acLineSegment0.t1, "cn_0")
        network.connect(acLineSegment0.t2, "cn_1")
        network.connect(acLineSegment1.t1, "cn_1")
        network.connect(acLineSegment1.t2, "cn_2")
        network.connect(j1.t1, "cn_2")
        network.connect(j1.t2, "cn_3")
        network.connect(acLineSegment2.t1, "cn_3")
        network.connect(acLineSegment2.t2, "cn_4")
        network.connect(acLineSegment3.t1, "cn_4")
        network.connect(acLineSegment3.t2, "cn_5")
        network.connect(j2.t1, "cn_5")
        network.connect(acLineSegment4.t1, "cn_1")
        network.connect(acLineSegment4.t2, "cn_6")
        network.connect(j3.t1, "cn_6")
        network.connect(j3.t2, "cn_7")
        network.connect(acLineSegment5.t1, "cn_7")
        network.connect(acLineSegment5.t2, "cn_8")
        network.connect(j4.t1, "cn_8")
        network.connect(j4.t2, "cn_9")
        network.connect(j4.t3, "cn_16")
        network.connect(acLineSegment6.t1, "cn_9")
        network.connect(acLineSegment6.t2, "cn_10")
        network.connect(j5.t1, "cn_10")
        network.connect(j5.t2, "cn_11")
        network.connect(j5.t3, "cn_18")
        network.connect(acLineSegment7.t1, "cn_11")
        network.connect(acLineSegment7.t2, "cn_12")
        network.connect(j6.t1, "cn_12")
        network.connect(j6.t2, "cn_13")
        network.connect(acLineSegment8.t1, "cn_13")
        network.connect(acLineSegment8.t2, "cn_14")
        network.connect(j7.t1, "cn_14")
        network.connect(j7.t2, "cn_15")
        network.connect(acLineSegment9.t1, "cn_15")
        network.connect(acLineSegment9.t2, "cn_4")
        network.connect(acLineSegment10.t1, "cn_16")
        network.connect(acLineSegment10.t2, "cn_17")
        network.connect(j8.t1, "cn_17")
        network.connect(acLineSegment11.t1, "cn_18")
        network.connect(acLineSegment11.t2, "cn_19")
        network.connect(j9.t1, "cn_19")
    }

}

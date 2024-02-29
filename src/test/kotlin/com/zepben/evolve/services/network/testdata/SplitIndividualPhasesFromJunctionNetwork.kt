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

object SplitIndividualPhasesFromJunctionNetwork {

    //
    //          | ac2
    //          A
    //  ac1     |/---B--- ac3
    //  ==ABCN==*----N--- ac4
    //          | j1
    //          CN
    //          | ac5
    //
    fun create(): NetworkService = NetworkService().also { network ->
        val j1 = createJunctionForConnecting(network, "j1", 5, PhaseCode.ABCN)
        val acLineSegment1 = createAcLineSegmentForConnecting(network, "acLineSegment1", PhaseCode.ABCN)
        val acLineSegment2 = createAcLineSegmentForConnecting(network, "acLineSegment2", PhaseCode.A)
        val acLineSegment3 = createAcLineSegmentForConnecting(network, "acLineSegment3", PhaseCode.B)
        val acLineSegment4 = createAcLineSegmentForConnecting(network, "acLineSegment4", PhaseCode.N)
        val acLineSegment5 = createAcLineSegmentForConnecting(network, "acLineSegment5", PhaseCode.CN)

        network.connect(j1.t1, "cn_1")
        network.connect(j1.t2, "cn_2")
        network.connect(j1.t3, "cn_3")
        network.connect(j1.getTerminal(4)!!, "cn_4")
        network.connect(j1.getTerminal(5)!!, "cn_5")

        network.connect(acLineSegment1.t1, "cn_1")
        network.connect(acLineSegment2.t1, "cn_2")
        network.connect(acLineSegment3.t1, "cn_3")
        network.connect(acLineSegment4.t1, "cn_4")
        network.connect(acLineSegment5.t1, "cn_5")
    }

}

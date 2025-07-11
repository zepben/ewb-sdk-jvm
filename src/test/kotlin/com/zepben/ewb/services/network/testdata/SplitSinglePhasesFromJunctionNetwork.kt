/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.network.testdata

import com.zepben.ewb.cim.iec61970.base.core.PhaseCode
import com.zepben.ewb.services.network.NetworkService

object SplitSinglePhasesFromJunctionNetwork {

    //
    //          | ac2
    //          AB
    //  ac1     |     ac3
    //  ==ABCN==+---AC---
    //          |
    //          BC
    //          | ac4
    //
    fun create(): NetworkService = NetworkService().also { network ->
        val acLineSegment1 = createAcLineSegmentForConnecting(network, "acLineSegment1", PhaseCode.ABCN)
        val acLineSegment2 = createAcLineSegmentForConnecting(network, "acLineSegment2", PhaseCode.AB)
        val acLineSegment3 = createAcLineSegmentForConnecting(network, "acLineSegment3", PhaseCode.AC)
        val acLineSegment4 = createAcLineSegmentForConnecting(network, "acLineSegment4", PhaseCode.BC)

        network.connect(acLineSegment1.t1, "cn_1")
        network.connect(acLineSegment2.t1, "cn_1")
        network.connect(acLineSegment3.t1, "cn_1")
        network.connect(acLineSegment4.t1, "cn_1")
    }

}

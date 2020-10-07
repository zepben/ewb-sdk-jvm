/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.cimbend.testdata

import com.zepben.cimbend.cim.iec61970.base.core.PhaseCode
import com.zepben.cimbend.network.NetworkService
import com.zepben.cimbend.testdata.TestDataCreators.*

object SingleLineSwerNetwork {

    fun create(): NetworkService {
        val networkService = NetworkService()

        val source = createSourceForConnecting(networkService, "source", 1, PhaseCode.ABC)
        val line1 = createAcLineSegmentForConnecting(networkService, "line1", PhaseCode.ABC)
        val isoTx = createPowerTransformerForConnecting(networkService, "isoTx", 2, PhaseCode.ABC, 0, 0)
        val line2 = createAcLineSegmentForConnecting(networkService, "line2", PhaseCode.X)

        networkService.connect(source.getTerminal(1)!!, line1.getTerminal(1)!!)
        networkService.connect(isoTx.getTerminal(1)!!, line1.getTerminal(2)!!)
        networkService.connect(isoTx.getTerminal(2)!!, line2.getTerminal(1)!!)

        return networkService
    }

}

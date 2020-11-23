/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.cimbend.testdata

import com.zepben.cimbend.cim.iec61970.base.core.PhaseCode
import com.zepben.cimbend.cim.iec61970.base.core.Substation
import com.zepben.cimbend.network.NetworkService
import com.zepben.cimbend.testdata.TestDataCreators.*

object FeederToSubstationTransformerNetwork {

    //
    //      c1      c2
    // fsp ---- tz ----
    //
    fun create(): NetworkService {
        val networkService = NetworkService()

        val substation = Substation().also { networkService.add(it) }

        val fsp = createNodeForConnecting(networkService, "fsp", 1)
        val c1 = createAcLineSegmentForConnecting(networkService, "c1", PhaseCode.A)
        val tz = createPowerTransformerForConnecting(networkService, "tz", 2, PhaseCode.A, 0, 0)
        val c2 = createAcLineSegmentForConnecting(networkService, "c2", PhaseCode.A)

        networkService.connect(c1.getTerminal(1)!!, fsp.getTerminal(1)!!)
        networkService.connect(c1.getTerminal(2)!!, tz.getTerminal(1)!!)
        networkService.connect(c2.getTerminal(1)!!, tz.getTerminal(2)!!)

        tz.addContainer(substation)
        createFeeder(networkService, "f", "f", substation, fsp, fsp.getTerminal(1))
        return networkService
    }
}

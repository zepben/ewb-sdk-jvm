/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.testdata

import com.zepben.evolve.cim.iec61970.base.core.PhaseCode
import com.zepben.evolve.cim.iec61970.base.core.Substation
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.testdata.TestDataCreators.*

object DownstreamFeederStartPointNetwork {

    //
    //  c1        c2       c3
    // ---- fsp1 ---- fsp2 ----
    //
    fun create(fsp2Terminal: Int): NetworkService {
        val networkService = NetworkService()

        val substation = Substation().also { networkService.add(it) }

        val c1 = createAcLineSegmentForConnecting(networkService, "c1", PhaseCode.A)
        val fsp1 = createNodeForConnecting(networkService, "fsp1", 2)
        val c2 = createAcLineSegmentForConnecting(networkService, "c2", PhaseCode.A)
        val fsp2 = createNodeForConnecting(networkService, "fsp2", 2)
        val c3 = createAcLineSegmentForConnecting(networkService, "c3", PhaseCode.A)

        networkService.connect(c1.getTerminal(2)!!, fsp1.getTerminal(1)!!)
        networkService.connect(c2.getTerminal(1)!!, fsp1.getTerminal(2)!!)
        networkService.connect(c2.getTerminal(2)!!, fsp2.getTerminal(1)!!)
        networkService.connect(c3.getTerminal(1)!!, fsp2.getTerminal(2)!!)

        createFeeder(networkService, "f1", "f1", substation, fsp1, fsp1.getTerminal(2))
        createFeeder(networkService, "f2", "f2", substation, fsp2, fsp2.getTerminal(fsp2Terminal))

        return networkService
    }
}

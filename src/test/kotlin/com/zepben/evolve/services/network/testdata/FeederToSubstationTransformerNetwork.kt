/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.testdata

import com.zepben.evolve.cim.iec61970.base.core.Substation
import com.zepben.evolve.services.network.NetworkService

object FeederToSubstationTransformerNetwork {

    //
    //      c1      c2
    // fsp ---- tz ----
    //
    fun create(): NetworkService = NetworkService().also { networkService ->
        val substation = Substation().also { networkService.add(it) }

        val fsp = createNodeForConnecting(networkService, "fsp", 1)
        val c1 = createAcLineSegmentForConnecting(networkService, "c1")
        val tz = createPowerTransformerForConnecting(networkService, "tz", 2, 0, 0)
        val c2 = createAcLineSegmentForConnecting(networkService, "c2")

        networkService.connect(c1.getTerminal(1)!!, fsp.getTerminal(1)!!)
        networkService.connect(c1.getTerminal(2)!!, tz.getTerminal(1)!!)
        networkService.connect(c2.getTerminal(1)!!, tz.getTerminal(2)!!)

        tz.addContainer(substation)
        createFeeder(networkService, "f", "f", substation, fsp, fsp.getTerminal(1))
    }

}

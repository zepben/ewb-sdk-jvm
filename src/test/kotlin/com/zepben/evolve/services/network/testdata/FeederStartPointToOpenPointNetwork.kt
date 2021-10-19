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

object FeederStartPointToOpenPointNetwork {

    //
    //      c1      c2
    // fsp ---- op ----
    //
    fun create(normallyOpen: Boolean, currentlyOpen: Boolean) = NetworkService().also { networkService ->
        val substation = Substation().also { networkService.add(it) }

        val fsp = createNodeForConnecting(networkService, "fsp", 1)
        val c1 = createAcLineSegmentForConnecting(networkService, "c1")
        val op = createSwitchForConnecting(networkService, "op", 2)
        val c2 = createAcLineSegmentForConnecting(networkService, "c2")

        op.setNormallyOpen(normallyOpen)
        op.setOpen(currentlyOpen)

        networkService.connect(c1.getTerminal(1)!!, fsp.getTerminal(1)!!)
        networkService.connect(c1.getTerminal(2)!!, op.getTerminal(1)!!)
        networkService.connect(c2.getTerminal(1)!!, op.getTerminal(2)!!)

        createFeeder(networkService, "f", "f", substation, fsp, fsp.getTerminal(1))
    }

}

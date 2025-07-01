/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.network.testdata

import com.zepben.ewb.cim.extensions.iec61970.base.feeder.LvFeeder
import com.zepben.ewb.cim.iec61970.base.core.PhaseCode
import com.zepben.ewb.cim.iec61970.base.core.Substation
import com.zepben.ewb.services.network.NetworkService

object FeederStartPointBetweenConductorsNetwork {

    //
    //  c1       c2
    // ---- fsp ----
    //
    fun create(makeFeederLv: Boolean = false): NetworkService = NetworkService().also { networkService ->
        val c1 = createAcLineSegmentForConnecting(networkService, "c1", PhaseCode.A)
        val fsp = createJunctionForConnecting(networkService, "fsp", 2)
        val c2 = createAcLineSegmentForConnecting(networkService, "c2", PhaseCode.A)

        networkService.connect(c1.t2, fsp.t1)
        networkService.connect(c2.t1, fsp.t2)

        if (makeFeederLv) {
            LvFeeder("f").apply { normalHeadTerminal = fsp.getTerminal(2) }.also { networkService.add(it) }
        } else {
            val substation = Substation().also { networkService.add(it) }
            createFeeder(networkService, "f", "f", substation, fsp, fsp.getTerminal(2))
        }
    }

}

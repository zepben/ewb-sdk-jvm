/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.network.testdata

import com.zepben.ewb.cim.iec61970.base.core.Substation
import com.zepben.ewb.services.common.testdata.generateId
import com.zepben.ewb.services.network.NetworkService

object FeederToSubstationTransformerNetwork {

    //
    //      c1      c2
    // fsp ---- tz ----
    //
    fun create(): NetworkService = NetworkService().also { networkService ->
        val substation = Substation(generateId()).also { networkService.add(it) }

        val fsp = createJunctionForConnecting(networkService, "fsp", 1)
        val c1 = createAcLineSegmentForConnecting(networkService, "c1")
        val tz = createPowerTransformerForConnecting(networkService, "tz", 2, 0, 0)
        val c2 = createAcLineSegmentForConnecting(networkService, "c2")

        networkService.connect(c1.t1, fsp.t1)
        networkService.connect(c1.t2, tz.t1)
        networkService.connect(c2.t1, tz.t2)

        tz.addContainer(substation)
        createFeeder(networkService, "f", "f", substation, fsp, fsp.getTerminal(1))
    }

}

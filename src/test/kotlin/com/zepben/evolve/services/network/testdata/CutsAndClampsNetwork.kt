/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.testdata

import com.zepben.evolve.testing.TestNetworkBuilder

object CutsAndClampsNetwork {

    fun multiCutAndClampNetwork(): TestNetworkBuilder {
        //
        //          2                     2
        //          c3          2         c7          2
        //          1           c5        1           c9
        //          1 clamp1    1         1 clamp3    1
        //          |           |         |           |
        // 1 b0 21--*--*1 cut1 2*--*--c1--*--*1 cut2 2*--*--21 b2 2
        //             |           |         |           |
        //             1           1 clamp2  1           1 clamp4
        //             c4          1         c8          1
        //             2           c6        2           c10
        //                         2                     2
        //
        val builder = TestNetworkBuilder()
            .fromBreaker() // b0
            .toAcls() // c1
            .withClamp(lengthFromTerminal1 = 1.0) //c1-clamp1
            .withCut(lengthFromTerminal1 = 2.0, isNormallyOpen = false) //c1-cut1
            .withClamp(lengthFromTerminal1 = 3.0) //c1-clamp2
            .withClamp(lengthFromTerminal1 = 4.0) //c1-clamp3
            .withCut(lengthFromTerminal1 = 5.0, isNormallyOpen = false) //c1-cut2
            .withClamp(lengthFromTerminal1 = 6.0) //c1-clamp4
            .toBreaker() // b2
            .fromAcls() // c3
            .connectTo("c1-clamp1", fromTerminal = 1)
            .fromAcls() // c4
            .connectTo("c1-cut1", fromTerminal = 1)
            .fromAcls() // c5
            .connectTo("c1-cut1", toTerminal = 2, fromTerminal = 1)
            .fromAcls() // c6
            .connectTo("c1-clamp2", fromTerminal = 1)
            .fromAcls() // c7
            .connectTo("c1-clamp3", fromTerminal = 1)
            .fromAcls() // c8
            .connectTo("c1-cut2", fromTerminal = 1)
            .fromAcls() // c9
            .connectTo("c1-cut2", toTerminal = 2, fromTerminal = 1)
            .fromAcls() // c10
            .connectTo("c1-clamp4", fromTerminal = 1)

        return builder
    }

}

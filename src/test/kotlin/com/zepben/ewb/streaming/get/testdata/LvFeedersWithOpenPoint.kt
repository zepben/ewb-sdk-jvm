/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.streaming.get.testdata

import com.zepben.ewb.services.network.NetworkService
import com.zepben.ewb.testing.TestNetworkBuilder

object LvFeedersWithOpenPoint {

    //    lvf5:[     c1  {  ]  c3     }:lvf6
    //    lvf5:[tx0------{b2]------tx4}:lvf6
    fun create(): NetworkService =
        TestNetworkBuilder()
            .fromPowerTransformer() // tx0
            .toAcls() // c1
            .toBreaker(isNormallyOpen = true) // b2
            .toAcls() // c3
            .toPowerTransformer() // tx4
            .addLvFeeder("tx0", 2) // lvf5
            .addLvFeeder("tx4", 1) // lvf6
            .build()

}

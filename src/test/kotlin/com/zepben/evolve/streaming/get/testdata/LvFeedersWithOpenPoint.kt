/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.streaming.get.testdata

import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.testing.TestNetworkBuilder

object LvFeedersWithOpenPoint {

    //    lv1:[     c1  {  ]  c2     }:lv2
    //    lv1:[tx1------{sw]------tx2}:lv2
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

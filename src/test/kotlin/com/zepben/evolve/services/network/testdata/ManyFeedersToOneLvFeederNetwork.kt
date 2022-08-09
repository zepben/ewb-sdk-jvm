/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.testdata

import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.testing.TestNetworkBuilder

object ManyFeedersToOneLvFeederNetwork {

    //
    // b0
    //
    // fdr1, fdr2, and lvf3 head terminals are all b0-t2
    //
    fun create(): NetworkService = TestNetworkBuilder()
        .fromBreaker() // b0
        .addFeeder("b0") // fdr1
        .addFeeder("b0") // fdr2
        .addLvFeeder("b0") // lvf3
        .network
}

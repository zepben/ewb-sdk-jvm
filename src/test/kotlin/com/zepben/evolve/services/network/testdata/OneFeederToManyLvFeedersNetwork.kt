/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.testdata

import com.zepben.evolve.cim.iec61970.base.core.BaseVoltage
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.testing.TestNetworkBuilder

object OneFeederToManyLvFeedersNetwork {

    //
    // - or |: LV line
    // = or #: HV line
    //
    //      c1     c2         c4
    // b0 ======+====== tx3 ------
    //          #
    //       c5 #
    //          #
    //         tx6 ------+
    //               c7
    //
    // fdr8 head terminal is b0-t2
    // lvf9 head terminal is tx3-t2
    // lvf10 head terminal is tx6-t2
    fun create(): NetworkService {
        val hvBaseVoltage = BaseVoltage().apply { nominalVoltage = 1000 }
        val lvBaseVoltage = BaseVoltage().apply { nominalVoltage = 999 }

        return TestNetworkBuilder()
            .fromBreaker { baseVoltage = hvBaseVoltage }
            .toAcls { baseVoltage = hvBaseVoltage }
            .toAcls { baseVoltage = hvBaseVoltage }
            .toPowerTransformer(endActions = listOf({ baseVoltage = hvBaseVoltage }, { baseVoltage = lvBaseVoltage }))
            .toAcls { baseVoltage = lvBaseVoltage }
            .branchFrom("c1")
            .toAcls { baseVoltage = hvBaseVoltage }
            .toPowerTransformer(endActions = listOf({ baseVoltage = hvBaseVoltage }, { baseVoltage = lvBaseVoltage }))
            .toAcls { baseVoltage = lvBaseVoltage }
            .addFeeder("b0")
            .addLvFeeder("tx3")
            .addLvFeeder("tx6")
            .network
    }
}

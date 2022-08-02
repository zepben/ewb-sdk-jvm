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

object HvLvNetwork {

    //
    // - or |: LV line
    // = or #: HV line
    //
    //  b0   c1   j2   c4   j5   c9  tx10  c11
    // 1*21=====21*21=====21*21=====21*21------2
    //   ^        3         3          ^
    // fdr12      1         1        lvf14
    //            |         #
    //         c3 |      c6 #
    //            |         #
    //            2         2
    //                      1    c8
    //                  tx7 *21-----2
    //                       ^
    //                     lvf13
    //
    fun create(): NetworkService {
        val hvBaseVoltage = BaseVoltage().apply { nominalVoltage = 1000 }
        val lvBaseVoltage = BaseVoltage().apply { nominalVoltage = 999 }

        val network = TestNetworkBuilder()
            .fromBreaker { baseVoltage = hvBaseVoltage}
            .toAcls { baseVoltage = hvBaseVoltage }
            .toJunction(numTerminals = 3) { baseVoltage = hvBaseVoltage }
            .toAcls { baseVoltage = lvBaseVoltage }
            .branchFrom("j2", 2)
            .toAcls { baseVoltage = hvBaseVoltage }
            .toJunction(numTerminals = 3) { baseVoltage = hvBaseVoltage }
            .toAcls { baseVoltage = hvBaseVoltage }
            .toPowerTransformer(endActions = listOf({ baseVoltage = hvBaseVoltage }, { baseVoltage = lvBaseVoltage }))
            .toAcls { baseVoltage = lvBaseVoltage }
            .branchFrom("j5", 2)
            .toAcls { baseVoltage = hvBaseVoltage }
            .toPowerTransformer(endActions = listOf({ ratedU = 1000 }, { ratedU = 999 }))
            .toAcls { baseVoltage = lvBaseVoltage }
            .addFeeder("b0")
            .addLvFeeder("tx7")
            .addLvFeeder("tx10")
            .build()

        network.add(hvBaseVoltage)
        network.add(lvBaseVoltage)

        return network
    }
}

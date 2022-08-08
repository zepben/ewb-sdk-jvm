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

object LvEquipmentBelowFeederHeadNetwork {

    //
    // - or |: LV line
    // = or #: HV line
    //
    //      c1     c3     c7         c9     c10
    // b0 ======+======+====== tx8 ------+======
    //          |      #
    //       c2 |   c4 #
    //          |      #
    //                tx5
    //                 |
    //              c6 |
    //                 |
    //
    // fdr11 head terminal is b0-t2
    //
    fun create(): NetworkService {
        val hvBaseVoltage = BaseVoltage().apply { nominalVoltage = 1000 }
        val lvBaseVoltage = BaseVoltage().apply { nominalVoltage = 999 }

        return TestNetworkBuilder()
            .fromBreaker { baseVoltage = hvBaseVoltage}
            .toAcls { baseVoltage = hvBaseVoltage }
            .toAcls { baseVoltage = lvBaseVoltage }
            .branchFrom("c1")
            .toAcls { baseVoltage = hvBaseVoltage }
            .toAcls { baseVoltage = hvBaseVoltage }
            .toPowerTransformer(endActions = listOf({ baseVoltage = hvBaseVoltage }, { baseVoltage = lvBaseVoltage }))
            .toAcls { baseVoltage = lvBaseVoltage }
            .branchFrom("c3")
            .toAcls { baseVoltage = hvBaseVoltage }
            .toPowerTransformer(endActions = listOf({ ratedU = 1000 }, { ratedU = 999 }))
            .toAcls { baseVoltage = lvBaseVoltage }
            .toAcls { baseVoltage = hvBaseVoltage }
            .addFeeder("b0")
            .network
            .apply {
                add(hvBaseVoltage)
                add(lvBaseVoltage)
            }
    }
}

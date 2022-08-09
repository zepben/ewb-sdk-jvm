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

object FeederHeadToTxToLvEquipmentNetwork {

    //
    // - or |: LV line
    // = or #: HV line
    //
    //      c1         c3
    // b0 ====== tx2 ------
    //
    // fdr4 head terminal is b0-t2
    //
    fun create(): NetworkService {
        val hvBaseVoltage = BaseVoltage().apply { nominalVoltage = 11000 }
        val lvBaseVoltage = BaseVoltage().apply { nominalVoltage = 400 }

        return TestNetworkBuilder()
            .fromBreaker { baseVoltage = hvBaseVoltage} // b0
            .toAcls { baseVoltage = hvBaseVoltage } // c1
            .toPowerTransformer(endActions = listOf({ baseVoltage = hvBaseVoltage }, { baseVoltage = lvBaseVoltage })) // tx2
            .toAcls { baseVoltage = lvBaseVoltage } // c3
            .addFeeder("b0")
            .network
            .apply {
                add(hvBaseVoltage)
                add(lvBaseVoltage)
            }
    }
}

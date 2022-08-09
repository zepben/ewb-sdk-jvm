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

object HvEquipmentBelowLvFeederHeadNetwork {

    //
    // - or |: LV line
    // = or #: HV line
    //
    //      c1     c2
    // b0 ------+======
    //
    // lvf3 head terminal is b0-t2
    //
    fun create(): NetworkService {
        val hvBaseVoltage = BaseVoltage().apply { nominalVoltage = 11000 }
        val lvBaseVoltage = BaseVoltage().apply { nominalVoltage = 400 }

        return TestNetworkBuilder()
            .fromBreaker { baseVoltage = lvBaseVoltage} // b0
            .toAcls { baseVoltage = lvBaseVoltage } // c1
            .toAcls { baseVoltage = hvBaseVoltage } // c2
            .addLvFeeder("b0")
            .network
            .apply {
                add(hvBaseVoltage)
                add(lvBaseVoltage)
            }
    }

}

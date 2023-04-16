/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.streaming.get.testdata

import com.zepben.evolve.cim.iec61970.base.core.PhaseCode
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.testdata.*
import com.zepben.evolve.services.network.tracing.Tracing

object LvFeedersWithOpenPoint {

    //    lv1:[     c1  {  ]  c2     }:lv2
    //    lv1:[tx1------{sw]------tx2}:lv2
    fun create(): NetworkService {
        val networkService = NetworkService()

        val sw = createSwitchForConnecting(networkService, "sw", 2, nominalPhases = PhaseCode.AB).apply {
            setOpen(true)
            setNormallyOpen(true)
        }

        val tx1 = createPowerTransformerForConnecting(networkService, "tx1", 2, 0, 0, PhaseCode.AB)
        val tx2 = createPowerTransformerForConnecting(networkService, "tx2", 2, 0, 0, PhaseCode.AB)

        val c1 = createAcLineSegmentForConnecting(networkService, "c1", PhaseCode.AB)
        val c2 = createAcLineSegmentForConnecting(networkService, "c2", PhaseCode.AB)

        createEnd(networkService, tx1, 22000, 1)
        createEnd(networkService, tx1, 415, 2)
        createEnd(networkService, tx2, 22000, 1)
        createEnd(networkService, tx2, 415, 2)

        createLvFeeder(networkService, "lvf001", "lvf001", headTerminal = tx1.getTerminal(2))
        createLvFeeder(networkService, "lvf002", "lvf002", headTerminal = tx2.getTerminal(2))

        networkService.connect(c1.getTerminal(1)!!, tx1.getTerminal(2)!!)
        networkService.connect(c2.getTerminal(1)!!, tx2.getTerminal(2)!!)
        networkService.connect(c1.getTerminal(2)!!, sw.getTerminal(2)!!)
        networkService.connect(c2.getTerminal(2)!!, sw.getTerminal(1)!!)

        Tracing.setPhases().run(networkService)
        Tracing.assignEquipmentToLvFeeders().run(networkService)

        return networkService
    }

}

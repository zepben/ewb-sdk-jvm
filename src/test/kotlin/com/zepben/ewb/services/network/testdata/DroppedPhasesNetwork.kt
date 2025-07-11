/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.network.testdata

import com.zepben.ewb.cim.extensions.iec61970.base.feeder.LvFeeder
import com.zepben.ewb.cim.iec61970.base.core.ConductingEquipment
import com.zepben.ewb.cim.iec61970.base.core.Feeder
import com.zepben.ewb.cim.iec61970.base.core.PhaseCode
import com.zepben.ewb.cim.iec61970.base.core.Terminal
import com.zepben.ewb.cim.iec61970.base.wires.Breaker
import com.zepben.ewb.cim.iec61970.base.wires.PowerTransformer
import com.zepben.ewb.services.network.NetworkService

object DroppedPhasesNetwork {

    //
    //     A ----- A  ----- A
    // fcb B ----- B  ----- B  ----- B ISO         TX
    //     C ----- C           ----- C     ----- C
    //
    fun create(makeFeederLv: Boolean = false): NetworkService = NetworkService().also { networkService ->
        val fcb = Breaker("fcb").also { addTerminals(networkService, it, PhaseCode.ABC); networkService.add(it) }
        val iso = PowerTransformer("iso").also { addTerminals(networkService, it, PhaseCode.BC); networkService.add(it) }
        val tx = PowerTransformer("tx").also { addTerminals(networkService, it, PhaseCode.C); networkService.add(it) }

        val acls1 = PowerTransformer("acls1").also { addTerminals(networkService, it, PhaseCode.ABC); networkService.add(it) }
        val acls2 = PowerTransformer("acls2").also { addTerminals(networkService, it, PhaseCode.AB); networkService.add(it) }
        val acls3 = PowerTransformer("acls3").also { addTerminals(networkService, it, PhaseCode.BC); networkService.add(it) }
        val acls4 = PowerTransformer("acls4").also { addTerminals(networkService, it, PhaseCode.C); networkService.add(it) }

        if (makeFeederLv) {
            LvFeeder("f").apply { normalHeadTerminal = fcb.getTerminal(2) }.also { networkService.add(it) }
        } else {
            Feeder("f").apply { normalHeadTerminal = fcb.getTerminal(2) }.also { networkService.add(it) }
        }

        networkService.connect(fcb.t2, acls1.t1)
        networkService.connect(acls1.t2, acls2.t1)
        networkService.connect(acls2.t2, acls3.t1)
        networkService.connect(acls3.t2, iso.t1)
        networkService.connect(iso.t2, acls4.t1)
        networkService.connect(acls4.t2, tx.t1)
    }

    private fun addTerminals(networkService: NetworkService, conductingEquipment: ConductingEquipment, phaseCode: PhaseCode) {
        for (i in 1..2) {
            Terminal().apply {
                this.conductingEquipment = conductingEquipment
                phases = phaseCode
            }.also {
                conductingEquipment.addTerminal(it)
                networkService.add(it)
            }
        }
    }

}

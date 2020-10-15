/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.testdata

import com.zepben.cimbend.cim.iec61970.base.core.BaseVoltage
import com.zepben.cimbend.cim.iec61970.base.core.PhaseCode
import com.zepben.cimbend.cim.iec61970.base.core.Substation
import com.zepben.cimbend.cim.iec61970.base.wires.PowerTransformer
import com.zepben.cimbend.network.NetworkService
import com.zepben.cimbend.testdata.TestDataCreators.*

object PowerTransformersWithEndsNetwork {

    private val bv66000 = BaseVoltage().apply { nominalVoltage = 66000 }
    private val bv22000 = BaseVoltage().apply { nominalVoltage = 22000 }
    private val bv12700 = BaseVoltage().apply { nominalVoltage = 12700 }
    private val bv415 = BaseVoltage().apply { nominalVoltage = 415 }

    @JvmStatic
    fun createWithBaseVoltage(): NetworkService {
        val networkService = createBase()

        networkService.get<PowerTransformer>("ztx")!!.apply {
            createEnd(networkService, this, bv66000, 1)
            createEnd(networkService, this, bv22000, 2)
        }

        networkService.get<PowerTransformer>("reg")!!.apply {
            createEnd(networkService, this, bv22000, 1)
            createEnd(networkService, this, bv22000, 2)
        }

        networkService.get<PowerTransformer>("iso")!!.apply {
            createEnd(networkService, this, bv22000, 1)
            createEnd(networkService, this, bv12700, 2)
        }

        networkService.get<PowerTransformer>("tx")!!.apply {
            createEnd(networkService, this, bv12700, 1)
            createEnd(networkService, this, bv415, 2)
        }

        return networkService
    }

    @JvmStatic
    fun createWithRatedVoltage(): NetworkService {
        val networkService = createBase()

        networkService.get<PowerTransformer>("ztx")!!.apply {
            createEnd(networkService, this, 66000, 1)
            createEnd(networkService, this, 22000, 2)
        }

        networkService.get<PowerTransformer>("reg")!!.apply {
            createEnd(networkService, this, 22000, 1)
            createEnd(networkService, this, 22000, 2)
        }

        networkService.get<PowerTransformer>("iso")!!.apply {
            createEnd(networkService, this, 22000, 1)
            createEnd(networkService, this, 12700, 2)
        }

        networkService.get<PowerTransformer>("tx")!!.apply {
            createEnd(networkService, this, 12700, 1)
            createEnd(networkService, this, 415, 2)
        }

        return networkService
    }

    //
    //   c0       c1       c2       c3       c4      c5
    //  ---- ztx ---- fsp ---- reg ---- iso ---- tx ----
    //
    private fun createBase(): NetworkService {
        val networkService = NetworkService()

        val substation = Substation().also { networkService.add(it) }

        val c0 = createAcLineSegmentForConnecting(networkService, "c0", PhaseCode.A).apply { baseVoltage = bv66000 }
        val ztx = createPowerTransformerForConnecting(networkService, "ztx", 2, PhaseCode.A, 0, 0)
        val c1 = createAcLineSegmentForConnecting(networkService, "c1", PhaseCode.A).apply { baseVoltage = bv22000 }
        val fsp = createNodeForConnecting(networkService, "fsp", 1).apply { baseVoltage = bv22000 }
        val c2 = createAcLineSegmentForConnecting(networkService, "c2", PhaseCode.A).apply { baseVoltage = bv22000 }
        val reg = createPowerTransformerForConnecting(networkService, "reg", 2, PhaseCode.A, 0, 0)
        val c3 = createAcLineSegmentForConnecting(networkService, "c3", PhaseCode.A).apply { baseVoltage = bv22000 }
        val iso = createPowerTransformerForConnecting(networkService, "iso", 2, PhaseCode.A, 0, 0)
        val c4 = createAcLineSegmentForConnecting(networkService, "c4", PhaseCode.A).apply { baseVoltage = bv12700 }
        val tx = createPowerTransformerForConnecting(networkService, "tx", 2, PhaseCode.A, 0, 0)
        val c5 = createAcLineSegmentForConnecting(networkService, "c5", PhaseCode.A).apply { baseVoltage = bv415 }

        sequenceOf(c0, ztx, c1).forEach {
            it.addContainer(substation)
            substation.addEquipment(it)
        }

        networkService.connect(c0.getTerminal(2)!!, ztx.getTerminal(1)!!)
        networkService.connect(c1.getTerminal(1)!!, ztx.getTerminal(2)!!)
        networkService.connect(c1.getTerminal(2)!!, fsp.getTerminal(1)!!)
        networkService.connect(c2.getTerminal(1)!!, fsp.getTerminal(1)!!)
        networkService.connect(c2.getTerminal(2)!!, reg.getTerminal(1)!!)
        networkService.connect(c3.getTerminal(1)!!, reg.getTerminal(2)!!)
        networkService.connect(c3.getTerminal(2)!!, iso.getTerminal(1)!!)
        networkService.connect(c4.getTerminal(1)!!, iso.getTerminal(2)!!)
        networkService.connect(c4.getTerminal(2)!!, tx.getTerminal(1)!!)
        networkService.connect(c5.getTerminal(1)!!, tx.getTerminal(2)!!)

        createFeeder(networkService, "f", "f", substation, fsp)
        return networkService
    }
}

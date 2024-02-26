/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network.testdata

import com.zepben.evolve.cim.iec61968.metering.UsagePoint
import com.zepben.evolve.cim.iec61970.base.core.BaseVoltage
import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.cim.iec61970.base.core.NameType
import com.zepben.evolve.cim.iec61970.base.wires.AcLineSegment
import com.zepben.evolve.cim.iec61970.base.wires.EnergyConsumer
import com.zepben.evolve.cim.iec61970.base.wires.PowerTransformer
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.tracing.Tracing
import java.util.*
import kotlin.reflect.full.createInstance

object WithUsagePointsNetwork {

    //
    //      c0   c1   c2             c3   c4   c5
    //  es ----+----+----+- sw1 iso ----+----+---- tx1
    //         |    |    |              |    |
    //         |c6  |c7  |c8            |c9  |c10
    //         |    |    |              |    |
    //        tx2  tx3   |             tx4  tx5
    //                   |
    //                   |       c11  c12  c13
    //                   +- sw2 ----+----+---- tx6
    //                              |    |
    //                              |c14 |c15
    //                              |    |
    //                             tx7  tx8
    //
    // sw1: normally close, currently open
    // sw2: normally open, currently closed
    //
    fun createLarge(): NetworkService = NetworkService().also { network ->
        val es = createSourceForConnecting(network, "es", 1)
        val iso = createPowerTransformerForConnecting(network, "iso", 2, 1, 2)
        val sw1 = createSwitchForConnecting(network, "sw1", 2)
        val sw2 = createSwitchForConnecting(network, "sw2", 2, true)
        val tx1 = createPowerTransformerForConnecting(network, "tx1", 1, 3, 3)
        val tx2 = createPowerTransformerForConnecting(network, "tx2", 1, 3, 1)
        val tx3 = createPowerTransformerForConnecting(network, "tx3", 1, 3, 1)
        val tx4 = createPowerTransformerForConnecting(network, "tx4", 1, 3, 1)
        val tx5 = createPowerTransformerForConnecting(network, "tx5", 1, 1, 7)
        val tx6 = createPowerTransformerForConnecting(network, "tx6", 1, 3, 1)
        val tx7 = createPowerTransformerForConnecting(network, "tx7", 1, 2, 1)
        val tx8 = createPowerTransformerForConnecting(network, "tx8", 1, 1, 1)

        val c0 = createAcLineSegmentForConnecting(network, "c0")
        val c1 = createAcLineSegmentForConnecting(network, "c1")
        val c2 = createAcLineSegmentForConnecting(network, "c2")
        val c3 = createAcLineSegmentForConnecting(network, "c3")
        val c4 = createAcLineSegmentForConnecting(network, "c4")
        val c5 = createAcLineSegmentForConnecting(network, "c5")
        val c6 = createAcLineSegmentForConnecting(network, "c6")
        val c7 = createAcLineSegmentForConnecting(network, "c7")
        val c8 = createAcLineSegmentForConnecting(network, "c8")
        val c9 = createAcLineSegmentForConnecting(network, "c9")
        val c10 = createAcLineSegmentForConnecting(network, "c10")
        val c11 = createAcLineSegmentForConnecting(network, "c11")
        val c12 = createAcLineSegmentForConnecting(network, "c12")
        val c13 = createAcLineSegmentForConnecting(network, "c13")
        val c14 = createAcLineSegmentForConnecting(network, "c14")
        val c15 = createAcLineSegmentForConnecting(network, "c15")

        sw1.setOpen(true, SinglePhaseKind.A)
        sw2.setOpen(false, SinglePhaseKind.A)

        network.connect(es.getTerminal(1)!!, c0.getTerminal(1)!!)
        network.connect(c0.getTerminal(2)!!, c1.getTerminal(1)!!)
        network.connect(c1.getTerminal(2)!!, c2.getTerminal(1)!!)
        network.connect(c2.getTerminal(2)!!, sw1.getTerminal(1)!!)
        network.connect(sw1.getTerminal(2)!!, iso.getTerminal(1)!!)
        network.connect(iso.getTerminal(2)!!, c3.getTerminal(1)!!)
        network.connect(c3.getTerminal(2)!!, c4.getTerminal(1)!!)
        network.connect(c4.getTerminal(2)!!, c5.getTerminal(1)!!)
        network.connect(c5.getTerminal(2)!!, tx1.getTerminal(1)!!)
        network.connect(c6.getTerminal(2)!!, tx2.getTerminal(1)!!)
        network.connect(c7.getTerminal(2)!!, tx3.getTerminal(1)!!)
        network.connect(c8.getTerminal(2)!!, sw2.getTerminal(1)!!)
        network.connect(c9.getTerminal(2)!!, tx4.getTerminal(1)!!)
        network.connect(c10.getTerminal(2)!!, tx5.getTerminal(1)!!)
        network.connect(sw2.getTerminal(2)!!, c11.getTerminal(1)!!)
        network.connect(c11.getTerminal(2)!!, c12.getTerminal(1)!!)
        network.connect(c12.getTerminal(2)!!, c13.getTerminal(1)!!)
        network.connect(c13.getTerminal(2)!!, tx6.getTerminal(1)!!)
        network.connect(c14.getTerminal(2)!!, tx7.getTerminal(1)!!)
        network.connect(c15.getTerminal(2)!!, tx8.getTerminal(1)!!)

        network.connect(c6.getTerminal(1)!!, Objects.requireNonNull(c0.getTerminal(2)!!.connectivityNodeId))
        network.connect(c7.getTerminal(1)!!, Objects.requireNonNull(c1.getTerminal(2)!!.connectivityNodeId))
        network.connect(c8.getTerminal(1)!!, Objects.requireNonNull(c2.getTerminal(2)!!.connectivityNodeId))
        network.connect(c9.getTerminal(1)!!, Objects.requireNonNull(c3.getTerminal(2)!!.connectivityNodeId))
        network.connect(c10.getTerminal(1)!!, Objects.requireNonNull(c4.getTerminal(2)!!.connectivityNodeId))
        network.connect(c14.getTerminal(1)!!, Objects.requireNonNull(c11.getTerminal(2)!!.connectivityNodeId))
        network.connect(c15.getTerminal(1)!!, Objects.requireNonNull(c12.getTerminal(2)!!.connectivityNodeId))

        Tracing.setPhases().run(network)
        es.addFeederDirections()
    }

    //
    //                 LV
    // es - tx [vcpi] ---- ec1 [cpi2]
    //       |
    //       | HV
    //       |
    //      ec2 [cpi1]
    //
    inline fun <reified T : ConductingEquipment> createTxWithVirtual(virtualConnectionCategory: String): NetworkService = NetworkService().also { network ->
        val nameType = NameType("CPI").also { network.addNameType(it) }
        val es = createSourceForConnecting(network, "es", 1)
        val tx = PowerTransformer("tx").apply {
            createTerminals(network, this, 3)
            addUsagePoint(
                UsagePoint().apply {
                    isVirtual = true
                    connectionCategory = virtualConnectionCategory
                    addName(nameType, "vcpi")
                }.also { network.add(it) }
            )
        }.also { network.add(it) }
        val ec1 = EnergyConsumer("ec1").apply {
            createTerminals(network, this, 1)
            addUsagePoint(UsagePoint().apply { addName(nameType, "cpi1") }.also { network.add(it) })
        }.also { network.add(it) }
        val ec2 = EnergyConsumer("ec2").apply {
            createTerminals(network, this, 1)
            addUsagePoint(UsagePoint().apply { addName(nameType, "cpi2") }.also { network.add(it) })
        }.also { network.add(it) }

        val lv = AcLineSegment().apply {
            baseVoltage = BaseVoltage().apply { nominalVoltage = 415 }.also { network.add(it) }
            createTerminals(network, this, 2)
        }.also { network.add(it) }
        val hv = T::class.createInstance().apply {
            baseVoltage = BaseVoltage().apply { nominalVoltage = 11000 }.also { network.add(it) }
            createTerminals(network, this, 2)
        }.also { network.tryAdd(it) }

        network.connect(es.terminals[0], tx.terminals[0])
        network.connect(tx.terminals[1], lv.terminals[0])
        network.connect(tx.terminals[2], hv.terminals[0])
        network.connect(lv.terminals[1], ec1.terminals[0])
        network.connect(hv.terminals[1], ec2.terminals[0])

        Tracing.setPhases().run(network)
        es.addFeederDirections()
    }

    //
    //                 LV
    // es - tx [cpi1] ---- ec [cpi2]
    //
    fun createTxWithRealAndLv(connectionCategory: String? = null): NetworkService = NetworkService().also { network ->
        val nameType = NameType("CPI").also { network.addNameType(it) }
        val es = createSourceForConnecting(network, "es", 1)
        val tx = PowerTransformer("tx").apply {
            createTerminals(network, this, 2)
            addUsagePoint(
                UsagePoint().apply {
                    this.connectionCategory = connectionCategory
                    addName(nameType, "vcpi")
                }.also { network.add(it) }
            )
        }.also { network.add(it) }
        val ec = EnergyConsumer("ec").apply {
            createTerminals(network, this, 1)
            addUsagePoint(UsagePoint().apply { addName(nameType, "cpi1") }.also { network.add(it) })
        }.also { network.add(it) }

        val lv = AcLineSegment().apply {
            baseVoltage = BaseVoltage().apply { nominalVoltage = 415 }.also { network.add(it) }
            createTerminals(network, this, 2)
        }.also { network.add(it) }

        network.connect(es.terminals[0], tx.terminals[0])
        network.connect(tx.terminals[1], lv.terminals[0])
        network.connect(lv.terminals[1], ec.terminals[0])

        Tracing.setPhases().run(network)
        es.addFeederDirections()
    }

}

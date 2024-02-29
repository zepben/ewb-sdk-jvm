/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.streaming.get.testdata

import com.zepben.evolve.cim.iec61970.base.core.BaseVoltage
import com.zepben.evolve.cim.iec61970.base.core.Feeder
import com.zepben.evolve.cim.iec61970.base.core.Substation
import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.cim.iec61970.base.wires.Junction
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.Circuit
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.Loop
import com.zepben.evolve.services.network.NetworkService

object LoopNetwork {

    //
    //   /-- TG --\
    //  |          |
    //  |/--------\|
    // BTS        ZTS
    // ||\__ ACT _/
    // |\________
    // |         |
    // ZEP     BEN
    //   \--.--/
    //      |
    //     CBR
    //
    fun create(): NetworkService = NetworkService().apply {
        val v330 = BaseVoltage().apply { nominalVoltage = 330000 }.also { add(it) }
        val v132 = BaseVoltage().apply { nominalVoltage = 132000 }.also { add(it) }
        val v66 = BaseVoltage().apply { nominalVoltage = 66000 }.also { add(it) }
        val v11 = BaseVoltage().apply { nominalVoltage = 11000 }.also { add(it) }

        val tg = createSubstation("TG", v330, v132)
        val zts = createSubstation("ZTS", v132, v66)
        val bts = createSubstation("BTS", v132, v66)
        val zep = createSubstation("ZEP", v66, v11)
        val ben = createSubstation("BEN", v66, v11)
        val cbr = createSubstation("CBR", v66, v11)
        val act = createSubstation("ACT", v66, v11)

        val tgZts = createCircuit("TGZTS", v132, tg, zts)
        val tgBts = createCircuit("TGBTS", v132, tg, bts)
        val ztsBts = createCircuit("ZTSBTS", v132, zts, bts)
        val btsZep = createCircuit("BTSZEP", v66, bts, zep)
        val btsBen = createCircuit("BTSBEN", v66, bts, ben)
        val zepBenCbr = createCircuit("ZEPBENCBR", v66, zep, ben, cbr)
        val btsAct = createCircuit("BTSACT", v66, bts, act)
        val ztsAct = createCircuit("ZTSACT", v66, zts, act)

        createLoop("TG-ZTS-BTS-TG", listOf(tg), listOf(bts, zts), listOf(tgBts, ztsBts, tgZts))
        createLoop("BTS-ZEP-BEN-BTS-CBR", listOf(bts), listOf(zep, ben, cbr), listOf(btsZep, zepBenCbr, btsBen))
        createLoop("ZTS-ACT-BTS", listOf(bts, zts), listOf(act), listOf(ztsAct, btsAct))
    }

    private fun NetworkService.createSubstation(id: String, vararg voltages: BaseVoltage) =
        Substation(id).apply {
            name = id
            voltages.forEach {
                addEquipment(createJunction("$id-j-${it.nominalVoltage}", it))
                if (it.nominalVoltage == 11000)
                    createFeeder("${id}001", this, it)
            }
        }.also {
            add(it)
        }

    private fun NetworkService.createJunction(id: String, voltage: BaseVoltage) =
        Junction(id).apply {
            name = id
            baseVoltage = voltage
        }.also {
            it.addTerminal(Terminal("$id-t").apply { conductingEquipment = it; add(this) })
            add(it)
        }

    private fun NetworkService.createFeeder(id: String, substation: Substation, voltage: BaseVoltage) =
        Feeder(id).apply {
            name = id
            addEquipment(createJunction("$id-j", voltage))
        }.also {
            it.normalEnergizingSubstation = substation
            substation.addFeeder(it)
            add(it)
        }

    private fun NetworkService.createCircuit(id: String, voltage: BaseVoltage, vararg endSubstations: Substation) =
        Circuit(id).apply {
            name = id
            addEquipment(createJunction("$id-j", voltage))
        }.also {
            endSubstations.forEach { substation ->
                it.addEndSubstation(substation)
                substation.addCircuit(it)
            }
            add(it)
        }

    private fun NetworkService.createLoop(id: String, energisingSubstations: List<Substation>, substations: List<Substation>, circuits: List<Circuit>) =
        Loop(id).apply {
            name = id
        }.also {
            energisingSubstations.forEach { substation ->
                it.addEnergizingSubstation(substation)
                substation.addEnergizedLoop(it)
            }
            substations.forEach { substation ->
                it.addSubstation(substation)
                substation.addLoop(it)
            }
            circuits.forEach { circuit ->
                it.addCircuit(circuit)
                circuit.loop = it
            }
            add(it)
        }

}

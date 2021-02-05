/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.examples.networks


import com.zepben.evolve.cim.iec61968.assetinfo.OverheadWireInfo
import com.zepben.evolve.cim.iec61968.assetinfo.PowerTransformerInfo
import com.zepben.evolve.cim.iec61968.assetinfo.WireInfo
import com.zepben.evolve.cim.iec61968.assets.AssetInfo
import com.zepben.evolve.cim.iec61970.base.core.BaseVoltage
import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.cim.iec61970.base.core.ConnectivityNode
import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.cim.iec61970.base.wires.*
import com.zepben.evolve.services.network.NetworkService


fun NetworkService.createBus(bv:  BaseVoltage, init: Junction.() -> Unit): Junction {
    // TODO: Figure out how to add Voltage to Buses - Looks like we need to add topologicalNode to support the relationship to BaseVoltage. Meanwhile using Junction.
    val bus = Junction().apply{baseVoltage=bv}.apply(init)
    val t = Terminal()
    this.tryAdd(t)
    this.tryAdd(bus)
    t.conductingEquipment = bus
    bus.addTerminal(t)
    return bus
}

fun NetworkService.createEnergySource(bus: Junction, init: EnergySource.() -> Unit): EnergySource = create(::EnergySource, bus, numTerminals = 1,  init)
fun NetworkService.createLoad(bus: Junction, init: EnergyConsumer.() -> Unit): EnergyConsumer = create(::EnergyConsumer, bus, numTerminals = 1, init)
fun NetworkService.createConnectivityNode(init: ConnectivityNode.()-> Unit): ConnectivityNode{
    val cn = ConnectivityNode().apply(init)
    this.add(cn)
    return cn
}
fun NetworkService.createTransformer(bus1: Junction, bus2: Junction, numEnds: Int = 2, ptInfo: PowerTransformerInfo?, init: PowerTransformer.() -> Unit): PowerTransformer{
    val pt = PowerTransformer().apply(init)
    this.add(pt)
    pt.createTerminals(numEnds, this)
    pt.connectBuses(bus1, bus2, this)
    for (i in 1..numEnds) {
        val end = PowerTransformerEnd().apply {powerTransformer = pt}
        this.tryAdd(end)
        pt.addEnd(end)
        end.terminal = pt.getTerminal(i)
        // TODO: How to associated PowerTrandformerEndInfo to a PowerTranformerInfo?
    }
    pt.apply{assetInfo = ptInfo }
    return pt
}

fun NetworkService.createLine(bus1:  Junction, bus2: Junction, std_type: String = "N2XS(FL)2Y 1x300 RM/35 64/110 kV",
                              init: AcLineSegment.() -> Unit): AcLineSegment{
    val acls = AcLineSegment().apply(init)
    acls.createTerminals(2,this)
    this.tryAdd(acls)
    acls.connectBuses(bus1, bus2, this)
    return acls.apply{assetInfo = getAvailableLineStdTypes(std_type)}
}

fun NetworkService.createBreaker(bus1: Junction, bus2: Junction, init: Breaker.() -> Unit): Breaker {
    val breaker = Breaker().apply(init)
    breaker.createTerminals(2,this)
    this.tryAdd(breaker)
    breaker.connectBuses(bus1, bus2, this)
    return breaker
}

fun NetworkService.createBreaker(bus: Junction, line: AcLineSegment, init: Breaker.() -> Unit): Breaker {
    val breaker = Breaker().apply(init)
    breaker.createTerminals(2,this)
    this.tryAdd(breaker)
    breaker.connectBusToLine(bus,line, this)
    return breaker
}

fun Breaker.connectBusToLine(bus: Junction, line: AcLineSegment, net: NetworkService) {
    net.connect(this.getTerminal(1)!!, bus.getTerminal(1)!!)
    net.connect(this.getTerminal(2)!!, line.getTerminal(1)!!)
}

private fun ConductingEquipment.connectBuses(bus1: Junction, bus2: Junction, net: NetworkService){
    net.connect(this.getTerminal(1)!!, bus1.getTerminal(1)!!)
    net.connect(this.getTerminal(2)!!, bus2.getTerminal(1)!!)
}



private fun getAvailableLineStdTypes(id: String): WireInfo
{
    /* linetypes = {
        # Cables, all from S.744, Heuck: Elektrische Energieversorgung - Vierweg+Teubner 2013
        # additional MV cables from Werth: Netzberechnung mit Erzeugungsporfilen (Dreiecksverlegung)
              # High Voltage
        "N2XS(FL)2Y 1x300 RM/35 64/110 kV":
        {"c_nf_per_km": 144,
            "r_ohm_per_km": 0.060,
            "x_ohm_per_km": 0.144,
            "max_i_ka": 0.588,
            "type": "cs",
            "q_mm2": 300,
            "alpha": alpha_cu}, */
    val list = mutableListOf<WireInfo>()
    list.add(OverheadWireInfo("N2XS(FL)2Y 1x300 RM/35 64/110 kV").apply {ratedCurrent = 366})
    /*
    # Medium Voltage
    "NA2XS2Y 1x240 RM/25 12/20 kV":
        {"c_nf_per_km": 304,
            "r_ohm_per_km": 0.122,
            "x_ohm_per_km": 0.112,
            "max_i_ka": 0.421,
            "type": "cs",
            "q_mm2": 240,
            "alpha": alpha_al},*/
    list.add(OverheadWireInfo("NA2XS2Y 1x240 RM/25 12/20 kV").apply {ratedCurrent = 421})
    /*  "48-AL1/8-ST1A 20.0":
        {"c_nf_per_km": 9.5,
            "r_ohm_per_km": 0.5939,
            "x_ohm_per_km": 0.372,
            "max_i_ka": 0.210,
            "type": "ol",
            "q_mm2": 48,
            "alpha": alpha_al},*/
    list.add(OverheadWireInfo("48-AL1/8-ST1A 20.0").apply {ratedCurrent = 210})
    //TODO:  Add all the line parameters
    return list.find {it.mRID == id}!!
}



private fun <T : ConductingEquipment> NetworkService.create(creator: () -> T, bus: Junction, numTerminals: Int = 1, init: T.() -> Unit): T {
    val obj = creator().apply { createTerminals(numTerminals, NetworkService()) }.apply(init)
    this.tryAdd(obj)
    this.connect(obj.getTerminal(1)!!, bus.getTerminal(1)!!)
    return obj
}

private fun ConductingEquipment.createTerminals(num: Int, net: NetworkService) {
    for (i in 1..num) {
        val terminal = Terminal()
        net.tryAdd(terminal)
        terminal.conductingEquipment = this
        addTerminal(terminal)
    }
}


fun NetworkService.getAvailablePowerTransformerInfo(mrid: String): PowerTransformerInfo? {

    // TODO: Consider when the instance is already in the NetworkService and it is matching with the Type. Check: <is> keyword
    var info  = this.get<PowerTransformerInfo>(mrid)
    if(info == null) {
        info = when (mrid) {
        //TODO: Add all the parameters to the PowerTranformerEndInfo. See std_type.py in pandapower for input data.
        "0.4 MVA 20/0.4 kV" -> PowerTransformerInfo(mrid).apply {}
        "25 MVA 110/20 kV" -> PowerTransformerInfo(mrid).apply {}
        "0.63 MVA 10/0.4 kV" -> PowerTransformerInfo(mrid).apply {}
        else -> null }
        this.add(info!!)
    }
    return info
}





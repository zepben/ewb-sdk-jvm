/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.streaming.get.testdata

import com.zepben.ewb.cim.iec61968.common.Location
import com.zepben.ewb.cim.iec61968.common.PositionPoint
import com.zepben.ewb.cim.iec61970.base.core.PhaseCode
import com.zepben.ewb.cim.iec61970.base.core.PowerSystemResource
import com.zepben.ewb.services.network.NetworkService
import com.zepben.ewb.services.network.testdata.*

object FeederNetworkWithCurrent {
    //                c1       c2      c3       c4
    //    source-fcb------fsp------tx------sw--------tx2
    //                                   (open)
    fun create(): NetworkService {
        val networkService = NetworkService()

        val source = createSourceForConnecting(networkService, "source", 1, PhaseCode.AB)
        val fcb = createSwitchForConnecting(networkService, "fcb", 2, nominalPhases = PhaseCode.AB)
        val fsp = createJunctionForConnecting(networkService, "fsp", 2, PhaseCode.AB)
        val tx = createPowerTransformerForConnecting(networkService, "tx", 2, 0, 0, PhaseCode.AB)
        val tx2 = createPowerTransformerForConnecting(networkService, "tx2", 2, 0, 0, PhaseCode.AB)
        val sw = createSwitchForConnecting(networkService, "sw", 2, nominalPhases = PhaseCode.AB)
        sw.setOpen(true)

        val c1 = createAcLineSegmentForConnecting(networkService, "c1", PhaseCode.AB)
        val c2 = createAcLineSegmentForConnecting(networkService, "c2", PhaseCode.AB)
        val c3 = createAcLineSegmentForConnecting(networkService, "c3", PhaseCode.AB)
        val c4 = createAcLineSegmentForConnecting(networkService, "c4", PhaseCode.AB)

        val substation = createSubstation(networkService, "f", "f", null)
        createFeeder(networkService, "f001", "f001", substation, fsp, fsp.getTerminal(2))

        createEnd(networkService, tx, 22000, 1)
        createEnd(networkService, tx, 415, 2)

        addLocation(networkService, source, listOf(1.0, 1.0))
        addLocation(networkService, fcb, listOf(1.0, 1.0))
        addLocation(networkService, fsp, listOf(5.0, 1.0))
        addLocation(networkService, tx, listOf(10.0, 2.0))
        addLocation(networkService, sw, listOf(15.0, 3.0))
        addLocation(networkService, tx2, listOf(20.0, 4.0))
        addLocation(networkService, c1, listOf(1.0, 1.0, 5.0, 1.0))
        addLocation(networkService, c2, listOf(5.0, 1.0, 10.0, 2.0))
        addLocation(networkService, c3, listOf(10.0, 1.0, 15.0, 3.0))
        addLocation(networkService, c4, listOf(15.0, 1.0, 20.0, 4.0))

        networkService.connect(source.t1, fcb.t1)
        networkService.connect(fcb.t2, c1.t1)
        networkService.connect(c1.t2, fsp.t1)
        networkService.connect(fsp.t2, c2.t1)
        networkService.connect(c2.t2, tx.t1)
        networkService.connect(tx.t2, c3.t1)
        networkService.connect(c3.t2, sw.t1)
        networkService.connect(sw.t2, c4.t1)
        networkService.connect(c4.t2, tx2.t1)

        networkService.setPhases()
        networkService.assignEquipmentToFeeders()

        return networkService
    }

    private fun addLocation(networkService: NetworkService, psr: PowerSystemResource, coords: List<Double>) {
        Location().apply {
            for (i in coords.indices step 2)
                addPoint(PositionPoint(coords[i], coords[i + 1]))
        }.also {
            psr.location = it
            networkService.add(it)
        }
    }

}

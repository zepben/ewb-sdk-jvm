/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.streaming.get.testdata

import com.zepben.evolve.cim.iec61968.common.Location
import com.zepben.evolve.cim.iec61968.common.PositionPoint
import com.zepben.evolve.cim.iec61970.base.core.PhaseCode
import com.zepben.evolve.cim.iec61970.base.core.PowerSystemResource
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.testdata.*
import com.zepben.evolve.services.network.tracing.Tracing

object FeederNetwork {
    //             c1       c2
    // source-fcb------fsp------tx
    //
    fun create(): NetworkService {
        val networkService = NetworkService()

        val source = createSourceForConnecting(networkService, "source", 1, PhaseCode.AB)
        val fcb = createSwitchForConnecting(networkService, "fcb", 2, nominalPhases = PhaseCode.AB)
        val fsp = createNodeForConnecting(networkService, "fsp", 2, PhaseCode.AB)
        val tx = createPowerTransformerForConnecting(networkService, "tx", 2, 0, 0, PhaseCode.AB)

        val c1 = createAcLineSegmentForConnecting(networkService, "c1", PhaseCode.AB)
        val c2 = createAcLineSegmentForConnecting(networkService, "c2", PhaseCode.AB)

        val substation = createSubstation(networkService, "sub1", "sub 1", null)
        createFeeder(networkService, "f001", "f001", substation, fsp, fsp.getTerminal(2))

        createEnd(networkService, tx, 22000, 1)
        createEnd(networkService, tx, 415, 2)

        addLocation(networkService, source, listOf(1.0, 1.0))
        addLocation(networkService, fcb, listOf(1.0, 1.0))
        addLocation(networkService, fsp, listOf(5.0, 1.0))
        addLocation(networkService, tx, listOf(10.0, 2.0))
        addLocation(networkService, c1, listOf(1.0, 1.0, 5.0, 1.0))
        addLocation(networkService, c2, listOf(5.0, 1.0, 10.0, 2.0))

        networkService.connect(source.getTerminal(1)!!, fcb.getTerminal(1)!!)
        networkService.connect(fcb.getTerminal(2)!!, c1.getTerminal(1)!!)
        networkService.connect(c1.getTerminal(2)!!, fsp.getTerminal(1)!!)
        networkService.connect(fsp.getTerminal(2)!!, c2.getTerminal(1)!!)
        networkService.connect(c2.getTerminal(2)!!, tx.getTerminal(1)!!)

        Tracing.setPhases().run(networkService)
        Tracing.assignEquipmentContainersToFeeders().run(networkService)

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

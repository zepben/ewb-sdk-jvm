/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network.testdata

import com.zepben.evolve.cim.iec61970.base.core.PhaseCode
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.tracing.Tracing

object SinglePhaseJunctionNetwork {

    //
    //        n3
    //        |
    //        A c3
    //        |
    // n1--AB-+--AB-n2
    //    c1  |  c2
    //        |
    //        AB c4
    //        |
    //        n4
    //
    fun create() = NetworkService().also { network ->
        val n1 = createSourceForConnecting(network, "n1", 1, PhaseCode.AB)
        val n2 = createSourceForConnecting(network, "n2", 1, PhaseCode.AB)
        val n3 = createSourceForConnecting(network, "n3", 1, PhaseCode.A)
        val n4 = createNodeForConnecting(network, "n4", 2, PhaseCode.AB)

        val c1 = createAcLineSegmentForConnecting(network, "c1", PhaseCode.AB)
        val c2 = createAcLineSegmentForConnecting(network, "c2", PhaseCode.AB)
        val c3 = createAcLineSegmentForConnecting(network, "c3", PhaseCode.A)
        val c4 = createAcLineSegmentForConnecting(network, "c4", PhaseCode.AB)

        network.connect(n1.getTerminal(1)!!, c1.getTerminal(1)!!)
        network.connect(c2.getTerminal(1)!!, c1.getTerminal(2)!!)
        network.connect(n2.getTerminal(1)!!, c2.getTerminal(2)!!)
        network.connect(n3.getTerminal(1)!!, c3.getTerminal(1)!!)
        network.connect(c1.getTerminal(2)!!, c3.getTerminal(2)!!)
        network.connect(c1.getTerminal(2)!!, c4.getTerminal(1)!!)
        network.connect(n4.getTerminal(1)!!, c4.getTerminal(2)!!)

        Tracing.setPhases().run(network)
    }

}

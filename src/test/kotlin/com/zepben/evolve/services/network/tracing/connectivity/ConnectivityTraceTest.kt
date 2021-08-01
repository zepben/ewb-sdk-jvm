/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.connectivity


import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.cim.iec61970.base.wires.AcLineSegment
import com.zepben.evolve.cim.iec61970.base.wires.BusbarSection
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.testdata.ConnectedEquipmentNetwork
import com.zepben.evolve.services.network.tracing.Tracing
import com.zepben.evolve.services.network.tracing.traversals.BasicTraversal
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class ConnectivityTraceTest {

    @JvmField
    @RegisterExtension
    var systemOut: SystemLogExtension = SystemLogExtension.SYSTEM_OUT.captureLog().muteOnSuccess()

    private val visited = mutableSetOf<String>()

    @Test
    internal fun connectivityTraceIgnoresOpenState() {
        validateRun(Tracing.connectivityTrace(), "s1", "s2", "n1", "s3", "s4", "n2")
        validateRun(Tracing.connectivityBreadthTrace(), "s1", "s2", "n1", "s3", "s4", "n2")
    }

    @Test
    internal fun normalConnectedEquipmentTraceUsesOpenState() {
        validateRun(Tracing.normalConnectivityTrace(), "s1", "s3", "s4")
    }

    @Test
    internal fun currentConnectivityTraceUsesOpenState() {
        validateRun(Tracing.currentConnectivityTrace(), "s1", "s2", "s3")
    }

    @Test
    internal fun doesntBackTraceBusbars() {
        //
        // ---- | ---- ----
        //  c1 bb1 c2   c3
        //
        val c1 = AcLineSegment("c1").apply { addTerminal(Terminal()) }
        val c2 = AcLineSegment("c2").apply { addTerminal(Terminal()); addTerminal(Terminal()) }
        val c3 = AcLineSegment("c3").apply { addTerminal(Terminal()); addTerminal(Terminal()) }
        val bb1 = BusbarSection("bb1").apply { addTerminal(Terminal()) }

        NetworkService().apply {
            connect(bb1.terminals[0], c1.terminals[0])
            connect(bb1.terminals[0], c2.terminals[0])
            connect(c2.terminals[1], c3.terminals[0])
        }

        val t = Tracing.connectivityTrace()
        t.queue.add(ConnectivityResult.between(c1.terminals[0], c2.terminals[0], emptyList()))
        t.addStepAction { cr, _ -> visited.add(cr.to!!.mRID) }
            .run()

        assertThat(visited, containsInAnyOrder(c2.mRID, c3.mRID))
    }

    private fun validateRun(t: BasicTraversal<ConnectivityResult>, vararg expected: String) {
        NetworkService.connectedEquipment(ConnectedEquipmentNetwork.create()["start"]!!).forEach { t.queue.add(it) }

        t.addStepAction { cr, _ -> visited.add(cr.to!!.mRID) }
            .run()

        assertThat(visited, containsInAnyOrder(*expected))
    }

}

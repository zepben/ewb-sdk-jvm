/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.connectivity

import com.zepben.evolve.cim.iec61970.base.core.ConnectivityNode
import com.zepben.evolve.cim.iec61970.base.core.PhaseCode
import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.cim.iec61970.base.wires.AcLineSegment
import com.zepben.evolve.services.network.NetworkService
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind as SPK

internal class TerminalConnectivityConnectedTest {

    @JvmField
    @RegisterExtension
    var systemOut: SystemLogExtension = SystemLogExtension.SYSTEM_OUT.captureLog().muteOnSuccess()

    val networkService = NetworkService()
    val connectivity = TerminalConnectivityConnected

    @Test
    internal fun straightConnections() {
        createConnectedTerminals(PhaseCode.ABCN, PhaseCode.ABCN).also { (t1, t2) ->
            validateConnection(t1, SPK.A, SPK.B, SPK.C, SPK.N)
            validateConnection(t2, SPK.A, SPK.B, SPK.C, SPK.N)
        }
        createConnectedTerminals(PhaseCode.ABCN, PhaseCode.AN).also { (t1, t2) ->
            validateConnection(t1, SPK.A, SPK.NONE, SPK.NONE, SPK.N)
            validateConnection(t2, SPK.A, SPK.N)
        }
        createConnectedTerminals(PhaseCode.ABN, PhaseCode.BCN).also { (t1, t2) ->
            validateConnection(t1, SPK.NONE, SPK.B, SPK.N)
            validateConnection(t2, SPK.B, SPK.NONE, SPK.N)
        }
        createConnectedTerminals(PhaseCode.XYN, PhaseCode.YN).also { (t1, t2) ->
            validateConnection(t1, SPK.NONE, SPK.Y, SPK.N)
            validateConnection(t2, SPK.Y, SPK.N)
        }
    }

    @Test
    internal fun xynConnectivity() {
        createConnectedTerminals(PhaseCode.XYN, PhaseCode.AN).also { (t1, t2) ->
            validateConnection(t1, SPK.A, SPK.NONE, SPK.N)
            validateConnection(t2, SPK.X, SPK.N)

            t1.replaceNormalPhases(PhaseCode.BCN)

            validateConnection(t1, SPK.NONE, SPK.NONE, SPK.N)
            validateConnection(t2, SPK.NONE, SPK.N)
        }

        createConnectedTerminals(PhaseCode.XYN, PhaseCode.BN).also { (t1, t2) ->
            validateConnection(t1, SPK.B, SPK.NONE, SPK.N)
            validateConnection(t2, SPK.X, SPK.N)

            t1.replaceNormalPhases(PhaseCode.ABN)

            validateConnection(t1, SPK.NONE, SPK.B, SPK.N)
            validateConnection(t2, SPK.Y, SPK.N)
        }

        createConnectedTerminals(PhaseCode.XYN, PhaseCode.CN).also { (t1, t2) ->
            validateConnection(t1, SPK.C, SPK.NONE, SPK.N)
            validateConnection(t2, SPK.X, SPK.N)
        }

        createConnectedTerminals(PhaseCode.XYN, PhaseCode.BCN, PhaseCode.ABCN).also { (t1, t2, t3) ->
            validateConnection(t1, listOf(t2 to listOf(SPK.B, SPK.C, SPK.N), t3 to listOf(SPK.B, SPK.C, SPK.N)))
            validateConnection(t2, listOf(t1 to listOf(SPK.X, SPK.Y, SPK.N), t3 to listOf(SPK.B, SPK.C, SPK.N)))
            validateConnection(t3, listOf(t1 to listOf(SPK.NONE, SPK.X, SPK.Y, SPK.N), t2 to listOf(SPK.NONE, SPK.B, SPK.C, SPK.N)))
        }

        createConnectedTerminals(PhaseCode.XYN, PhaseCode.YN, PhaseCode.ABCN).also { (t1, t2, t3) ->
            validateConnection(t1, listOf(t2 to listOf(SPK.NONE, SPK.Y, SPK.N), t3 to listOf(SPK.A, SPK.C, SPK.N)))
            validateConnection(t2, listOf(t1 to listOf(SPK.Y, SPK.N), t3 to listOf(SPK.C, SPK.N)))
            validateConnection(t3, listOf(t1 to listOf(SPK.X, SPK.NONE, SPK.Y, SPK.N), t2 to listOf(SPK.NONE, SPK.NONE, SPK.Y, SPK.N)))
        }
    }

    @Test
    internal fun xnConnectivity() {
        createConnectedTerminals(PhaseCode.XN, PhaseCode.ABCN).also { (t1, t2) ->
            validateConnection(t1, SPK.A, SPK.N)
            validateConnection(t2, SPK.X, SPK.NONE, SPK.NONE, SPK.N)

            t1.replaceNormalPhases(PhaseCode.AN)

            validateConnection(t1, SPK.A, SPK.N)
            validateConnection(t2, SPK.X, SPK.NONE, SPK.NONE, SPK.N)

            t1.replaceNormalPhases(PhaseCode.BN)

            validateConnection(t1, SPK.B, SPK.N)
            validateConnection(t2, SPK.NONE, SPK.X, SPK.NONE, SPK.N)

            t1.replaceNormalPhases(PhaseCode.CN)

            validateConnection(t1, SPK.C, SPK.N)
            validateConnection(t2, SPK.NONE, SPK.NONE, SPK.X, SPK.N)
        }

        createConnectedTerminals(PhaseCode.XN, PhaseCode.BN, PhaseCode.ABCN).also { (t1, t2, t3) ->
            validateConnection(t1, listOf(t2 to listOf(SPK.B, SPK.N), t3 to listOf(SPK.B, SPK.N)))
            validateConnection(t2, listOf(t1 to listOf(SPK.X, SPK.N), t3 to listOf(SPK.B, SPK.N)))
            validateConnection(t3, listOf(t1 to listOf(SPK.NONE, SPK.X, SPK.NONE, SPK.N), t2 to listOf(SPK.NONE, SPK.B, SPK.NONE, SPK.N)))
        }
    }

    @Test
    internal fun ynConnectivity() {
        createConnectedTerminals(PhaseCode.YN, PhaseCode.ABCN).also { (t1, t2) ->
            validateConnection(t1, SPK.C, SPK.N)
            validateConnection(t2, SPK.NONE, SPK.NONE, SPK.Y, SPK.N)

            t1.replaceNormalPhases(PhaseCode.BN)

            validateConnection(t1, SPK.B, SPK.N)
            validateConnection(t2, SPK.NONE, SPK.Y, SPK.NONE, SPK.N)

            // Y can be forced onto phase A with traced phases (will not happen in practice).
            t1.replaceNormalPhases(PhaseCode.AN)

            validateConnection(t1, SPK.A, SPK.N)
            validateConnection(t2, SPK.Y, SPK.NONE, SPK.NONE, SPK.N)
        }

        createConnectedTerminals(PhaseCode.YN, PhaseCode.AN, PhaseCode.ABCN).also { (t1, t2, t3) ->
            validateConnection(t1, listOf(t2 to listOf(SPK.NONE, SPK.N), t3 to listOf(SPK.C, SPK.N)))
            validateConnection(t2, listOf(t1 to listOf(SPK.NONE, SPK.N), t3 to listOf(SPK.A, SPK.N)))
            validateConnection(t3, listOf(t1 to listOf(SPK.NONE, SPK.NONE, SPK.Y, SPK.N), t2 to listOf(SPK.A, SPK.NONE, SPK.NONE, SPK.N)))
        }
    }

    @Test
    internal fun singlePhaseXyPriorityConnectivity() {
        createConnectedTerminals(PhaseCode.X, PhaseCode.Y, PhaseCode.A).also { (t1, t2, t3) ->
            validateConnection(t1, listOf(t3 to listOf(SPK.A)))
            validateConnection(t2)
            validateConnection(t3, listOf(t1 to listOf(SPK.X)))
        }

        createConnectedTerminals(PhaseCode.X, PhaseCode.Y, PhaseCode.B).also { (t1, t2, t3) ->
            validateConnection(t1, listOf(t3 to listOf(SPK.B)))
            validateConnection(t2)
            validateConnection(t3, listOf(t1 to listOf(SPK.X)))
        }

        createConnectedTerminals(PhaseCode.X, PhaseCode.Y, PhaseCode.C).also { (t1, t2, t3) ->
            validateConnection(t1, listOf(t3 to listOf(SPK.C)))
            validateConnection(t2)
            validateConnection(t3, listOf(t1 to listOf(SPK.X)))
        }
    }

    @Test
    internal fun straightXynChainConnectivity() {
        createXyChainedTerminals(listOf(PhaseCode.ABN), listOf(PhaseCode.ABCN)).also { (ts1, ts2) ->
            val (t11, t12) = ts1
            val (t21, t22) = ts2

            validateConnection(t11, SPK.A, SPK.B, SPK.N)
            validateConnection(t12, SPK.X, SPK.Y, SPK.N)

            validateConnection(t21, SPK.A, SPK.B, SPK.N)
            validateConnection(t22, SPK.X, SPK.Y, SPK.NONE, SPK.N)
        }

        createXyChainedTerminals(listOf(PhaseCode.ACN), listOf(PhaseCode.ABCN)).also { (ts1, ts2) ->
            val (t11, t12) = ts1
            val (t21, t22) = ts2

            validateConnection(t11, SPK.A, SPK.C, SPK.N)
            validateConnection(t12, SPK.X, SPK.Y, SPK.N)

            validateConnection(t21, SPK.A, SPK.C, SPK.N)
            validateConnection(t22, SPK.X, SPK.NONE, SPK.Y, SPK.N)
        }

        createXyChainedTerminals(listOf(PhaseCode.BCN), listOf(PhaseCode.ABCN)).also { (ts1, ts2) ->
            val (t11, t12) = ts1
            val (t21, t22) = ts2

            validateConnection(t11, SPK.B, SPK.C, SPK.N)
            validateConnection(t12, SPK.X, SPK.Y, SPK.N)

            validateConnection(t21, SPK.B, SPK.C, SPK.N)
            validateConnection(t22, SPK.NONE, SPK.X, SPK.Y, SPK.N)
        }

        createXyChainedTerminals(listOf(PhaseCode.BCN), listOf(PhaseCode.ABCN, PhaseCode.ABN)).also { (ts1, ts2) ->
            val (t11, t12) = ts1
            val (t21, t22, t23) = ts2

            validateConnection(t11, SPK.B, SPK.C, SPK.N)
            validateConnection(t12, SPK.X, SPK.Y, SPK.N)

            validateConnection(t21, listOf(t22 to listOf(SPK.B, SPK.C, SPK.N), t23 to listOf(SPK.B, SPK.NONE, SPK.N)))
            validateConnection(t22, listOf(t21 to listOf(SPK.NONE, SPK.X, SPK.Y, SPK.N), t23 to listOf(SPK.A, SPK.B, SPK.NONE, SPK.N)))
            validateConnection(t23, listOf(t21 to listOf(SPK.NONE, SPK.X, SPK.N), t22 to listOf(SPK.A, SPK.B, SPK.N)))
        }
    }

    @Test
    internal fun xyToSplitConnectivity() {
        createConnectedTerminals(PhaseCode.XY, PhaseCode.A, PhaseCode.B).also { (t1, t2, t3) ->
            validateConnection(t1, listOf(t2 to listOf(SPK.A, SPK.NONE), t3 to listOf(SPK.NONE, SPK.B)))
            validateConnection(t2, listOf(t1 to listOf(SPK.X)))
            validateConnection(t3, listOf(t1 to listOf(SPK.Y)))
        }

        createConnectedTerminals(PhaseCode.XY, PhaseCode.A, PhaseCode.C).also { (t1, t2, t3) ->
            validateConnection(t1, listOf(t2 to listOf(SPK.A, SPK.NONE), t3 to listOf(SPK.NONE, SPK.C)))
            validateConnection(t2, listOf(t1 to listOf(SPK.X)))
            validateConnection(t3, listOf(t1 to listOf(SPK.Y)))
        }

        createConnectedTerminals(PhaseCode.XY, PhaseCode.B, PhaseCode.C).also { (t1, t2, t3) ->
            validateConnection(t1, listOf(t2 to listOf(SPK.B, SPK.NONE), t3 to listOf(SPK.NONE, SPK.C)))
            validateConnection(t2, listOf(t1 to listOf(SPK.X)))
            validateConnection(t3, listOf(t1 to listOf(SPK.Y)))
        }
    }

    @Test
    internal fun secondaryPhasesAreNotConnected() {
        createConnectedTerminals(PhaseCode.s1, PhaseCode.s1).also { (t1, t2) ->
            validateConnection(t1)
            validateConnection(t2)
        }
    }

    private fun createConnectedTerminals(vararg phaseCodes: PhaseCode): List<Terminal> {
        val cn = networkService.getNextConnectivityNode()
        return phaseCodes.map { phaseCode ->
            Terminal().apply { phases = phaseCode }.also { networkService.connect(it, cn.mRID) }
        }
    }

    private fun createXyChainedTerminals(phaseCodes1: List<PhaseCode>, phaseCodes2: List<PhaseCode>): List<List<Terminal>> {
        val terminals1 = createConnectedTerminals(PhaseCode.XYN, *phaseCodes1.toTypedArray())
        val terminals2 = createConnectedTerminals(PhaseCode.XYN, *phaseCodes2.toTypedArray())

        createConnectedTerminals(PhaseCode.XYN, PhaseCode.XYN).also { midPointTerminals ->
            AcLineSegment("acls1").apply { addTerminal(terminals1[0]); addTerminal(midPointTerminals[0]) }
            AcLineSegment("acls2").apply { addTerminal(midPointTerminals[1]); addTerminal(terminals2[0]) }
        }

        return listOf(terminals1, terminals2)
    }

    private fun validateConnection(t: Terminal, vararg expectedPhases: SPK) {
        val expected = expectedPhases
            .mapIndexed { index, phase -> NominalPhasePath(t.phases.singlePhases[index], phase) }
            .filter { it.to != SPK.NONE }

        if (expected.isNotEmpty())
            assertThat(connectivity.connectedTerminals(t)[0].nominalPhasePaths, containsInAnyOrder(*expected.toTypedArray()))
        else
            assertThat(connectivity.connectedTerminals(t), empty())
    }

    private fun validateConnection(t: Terminal, expectedPhases: List<Pair<Terminal, List<SPK>>>) {
        val connected = connectivity.connectedTerminals(t).associateBy { it.toTerminal }

        assertThat(connected, aMapWithSize(expectedPhases.size))
        expectedPhases.forEach { (toTerminal, phases) ->
            assertThat(
                connected[toTerminal]!!.nominalPhasePaths,
                containsInAnyOrder(*phases
                    .mapIndexed { index, phase -> NominalPhasePath(t.phases.singlePhases[index], phase) }
                    .filter { it.to != SPK.NONE }
                    .toTypedArray())
            )
        }
    }

    private fun Terminal.replaceNormalPhases(normalPhases: PhaseCode) {
        phases.singlePhases.filterIndexed { index, phase ->
            this.normalPhases[phase] = SPK.NONE
            this.normalPhases.set(phase, normalPhases.singlePhases[index])
        }
    }

    private fun NetworkService.getNextConnectivityNode(): ConnectivityNode = getOrPutConnectivityNode("cn${num<ConnectivityNode>()}")

}

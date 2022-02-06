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
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind.*
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.tracing.phases.FeederDirection
import com.zepben.evolve.services.network.tracing.phases.NominalPhasePath
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.Matchers.empty
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class TerminalConnectivityTest {

    @JvmField
    @RegisterExtension
    var systemOut: SystemLogExtension = SystemLogExtension.SYSTEM_OUT.captureLog().muteOnSuccess()

    val networkService = NetworkService()

    @Test
    internal fun straightConnections() {
        createConnectedTerminals(PhaseCode.ABCN, PhaseCode.ABCN).also { (t1, t2) ->
            validateConnection(t1, A, B, C, N)
            validateConnection(t2, A, B, C, N)
        }
        createConnectedTerminals(PhaseCode.ABCN, PhaseCode.AN).also { (t1, t2) ->
            validateConnection(t1, A, NONE, NONE, N)
            validateConnection(t2, A, N)
        }
        createConnectedTerminals(PhaseCode.ABN, PhaseCode.BCN).also { (t1, t2) ->
            validateConnection(t1, NONE, B, N)
            validateConnection(t2, B, NONE, N)
        }
        createConnectedTerminals(PhaseCode.XYN, PhaseCode.YN).also { (t1, t2) ->
            validateConnection(t1, NONE, Y, N)
            validateConnection(t2, Y, N)
        }
    }

    @Test
    internal fun xynConnectivity() {
        createConnectedTerminals(PhaseCode.XYN, PhaseCode.AN).also { (t1, t2) ->
            validateConnection(t1, A, NONE, N)
            validateConnection(t2, X, N)

            t1.setNormalPhases(PhaseCode.BCN)

            validateConnection(t1, NONE, NONE, N)
            validateConnection(t2, NONE, N)
        }

        createConnectedTerminals(PhaseCode.XYN, PhaseCode.BN).also { (t1, t2) ->
            validateConnection(t1, B, NONE, N)
            validateConnection(t2, X, N)

            t1.setNormalPhases(PhaseCode.ABN)

            validateConnection(t1, NONE, B, N)
            validateConnection(t2, Y, N)
        }

        createConnectedTerminals(PhaseCode.XYN, PhaseCode.CN).also { (t1, t2) ->
            validateConnection(t1, C, NONE, N)
            validateConnection(t2, X, N)
        }

        createConnectedTerminals(PhaseCode.XYN, PhaseCode.BCN, PhaseCode.ABCN).also { (t1, t2, t3) ->
            validateConnection(t1, listOf(listOf(B, C, N), listOf(B, C, N)))
            validateConnection(t2, listOf(listOf(X, Y, N), listOf(B, C, N)))
            validateConnection(t3, listOf(listOf(NONE, X, Y, N), listOf(NONE, B, C, N)))
        }

        createConnectedTerminals(PhaseCode.XYN, PhaseCode.YN, PhaseCode.ABCN).also { (t1, t2, t3) ->
            validateConnection(t1, listOf(listOf(NONE, Y, N), listOf(A, C, N)))
            validateConnection(t2, listOf(listOf(Y, N), listOf(C, N)))
            validateConnection(t3, listOf(listOf(X, NONE, Y, N), listOf(NONE, NONE, Y, N)))
        }
    }

    @Test
    internal fun xnConnectivity() {
        createConnectedTerminals(PhaseCode.XN, PhaseCode.ABCN).also { (t1, t2) ->
            validateConnection(t1, A, N)
            validateConnection(t2, X, NONE, NONE, N)

            t1.setNormalPhases(PhaseCode.AN)

            validateConnection(t1, A, N)
            validateConnection(t2, X, NONE, NONE, N)

            t1.setNormalPhases(PhaseCode.BN)

            validateConnection(t1, B, N)
            validateConnection(t2, NONE, X, NONE, N)

            t1.setNormalPhases(PhaseCode.CN)

            validateConnection(t1, C, N)
            validateConnection(t2, NONE, NONE, X, N)
        }

        createConnectedTerminals(PhaseCode.XN, PhaseCode.BN, PhaseCode.ABCN).also { (t1, t2, t3) ->
            validateConnection(t1, listOf(listOf(B, N), listOf(B, N)))
            validateConnection(t2, listOf(listOf(X, N), listOf(B, N)))
            validateConnection(t3, listOf(listOf(NONE, X, NONE, N), listOf(NONE, B, NONE, N)))
        }
    }

    @Test
    internal fun ynConnectivity() {
        createConnectedTerminals(PhaseCode.YN, PhaseCode.ABCN).also { (t1, t2) ->
            validateConnection(t1, C, N)
            validateConnection(t2, NONE, NONE, Y, N)

            t1.setNormalPhases(PhaseCode.BN)

            validateConnection(t1, B, N)
            validateConnection(t2, NONE, Y, NONE, N)

            // Y can be forced onto phase A with traced phases (will not happen in practice).
            t1.setNormalPhases(PhaseCode.AN)

            validateConnection(t1, A, N)
            validateConnection(t2, Y, NONE, NONE, N)
        }

        createConnectedTerminals(PhaseCode.YN, PhaseCode.AN, PhaseCode.ABCN).also { (t1, t2, t3) ->
            validateConnection(t1, listOf(listOf(NONE, N), listOf(C, N)))
            validateConnection(t2, listOf(listOf(NONE, N), listOf(A, N)))
            validateConnection(t3, listOf(listOf(NONE, NONE, Y, N), listOf(A, NONE, NONE, N)))
        }
    }

    @Test
    internal fun singlePhaseXyPriorityConnectivity() {
        createConnectedTerminals(PhaseCode.X, PhaseCode.Y, PhaseCode.A).also { (t1, t2, t3) ->
            validateConnection(t1, listOf(listOf(NONE), listOf(A)))
            validateConnection(t2, listOf(listOf(NONE), listOf(NONE)))
            validateConnection(t3, listOf(listOf(X), listOf(NONE)))
        }

        createConnectedTerminals(PhaseCode.X, PhaseCode.Y, PhaseCode.B).also { (t1, t2, t3) ->
            validateConnection(t1, listOf(listOf(NONE), listOf(B)))
            validateConnection(t2, listOf(listOf(NONE), listOf(NONE)))
            validateConnection(t3, listOf(listOf(X), listOf(NONE)))
        }

        createConnectedTerminals(PhaseCode.X, PhaseCode.Y, PhaseCode.C).also { (t1, t2, t3) ->
            validateConnection(t1, listOf(listOf(NONE), listOf(C)))
            validateConnection(t2, listOf(listOf(NONE), listOf(NONE)))
            validateConnection(t3, listOf(listOf(X), listOf(NONE)))
        }
    }

    @Test
    internal fun straightXynChainConnectivity() {
        createXyChainedTerminals(listOf(PhaseCode.ABN), listOf(PhaseCode.ABCN)).also { (ts1, ts2) ->
            val (t11, t12) = ts1
            val (t21, t22) = ts2

            validateConnection(t11, A, B, N)
            validateConnection(t12, X, Y, N)
            validateConnection(t21, A, B, N)
            validateConnection(t22, X, Y, NONE, N)
        }

        createXyChainedTerminals(listOf(PhaseCode.ACN), listOf(PhaseCode.ABCN)).also { (ts1, ts2) ->
            val (t11, t12) = ts1
            val (t21, t22) = ts2

            validateConnection(t11, A, C, N)
            validateConnection(t12, X, Y, N)
            validateConnection(t21, A, C, N)
            validateConnection(t22, X, NONE, Y, N)
        }

        createXyChainedTerminals(listOf(PhaseCode.BCN), listOf(PhaseCode.ABCN)).also { (ts1, ts2) ->
            val (t11, t12) = ts1
            val (t21, t22) = ts2

            validateConnection(t11, B, C, N)
            validateConnection(t12, X, Y, N)
            validateConnection(t21, B, C, N)
            validateConnection(t22, NONE, X, Y, N)
        }

        createXyChainedTerminals(listOf(PhaseCode.BCN), listOf(PhaseCode.ABCN, PhaseCode.ABN)).also { (ts1, ts2) ->
            val (t11, t12) = ts1
            val (t21, t22, t23) = ts2

            validateConnection(t11, B, C, N)
            validateConnection(t12, X, Y, N)
            validateConnection(t21, listOf(listOf(B, C, N), listOf(B, NONE, N)))
            validateConnection(t22, listOf(listOf(NONE, X, Y, N), listOf(A, B, NONE, N)))
            validateConnection(t23, listOf(listOf(NONE, X, N), listOf(A, B, N)))
        }
    }

    @Test
    internal fun xyToSplitConnectivity() {
        createConnectedTerminals(PhaseCode.XY, PhaseCode.A, PhaseCode.B).also { (t1, t2, t3) ->
            validateConnection(t1, listOf(listOf(A, NONE), listOf(NONE, B)))
            validateConnection(t2, listOf(listOf(X), listOf(NONE)))
            validateConnection(t3, listOf(listOf(NONE), listOf(Y)))
        }

        createConnectedTerminals(PhaseCode.XY, PhaseCode.A, PhaseCode.C).also { (t1, t2, t3) ->
            validateConnection(t1, listOf(listOf(A, NONE), listOf(NONE, C)))
            validateConnection(t2, listOf(listOf(X), listOf(NONE)))
            validateConnection(t3, listOf(listOf(NONE), listOf(Y)))
        }

        createConnectedTerminals(PhaseCode.XY, PhaseCode.B, PhaseCode.C).also { (t1, t2, t3) ->
            validateConnection(t1, listOf(listOf(B, NONE), listOf(NONE, C)))
            validateConnection(t2, listOf(listOf(X), listOf(NONE)))
            validateConnection(t3, listOf(listOf(NONE), listOf(Y)))
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

    private fun validateConnection(t: Terminal, vararg expectedPhases: SinglePhaseKind) {
        val expected = expectedPhases
            .mapIndexed { index, phase -> NominalPhasePath(t.phases.singlePhases[index], phase) }
            .filter { it.to != NONE }

        if (expected.isNotEmpty())
            assertThat(NetworkService.connectedTerminals(t)[0].nominalPhasePaths, containsInAnyOrder(*expected.toTypedArray()))
        else
            assertThat(NetworkService.connectedTerminals(t), empty())
    }

    private fun validateConnection(t: Terminal, expectedPhases: List<List<SinglePhaseKind>>) {
        val expected = expectedPhases.map { phases ->
            phases
                .mapIndexed { index, phase -> NominalPhasePath(t.phases.singlePhases[index], phase) }
                .filter { it.to != NONE }
        }.filter { it.isNotEmpty() }

        expected.forEachIndexed { crIndex, phases ->
            if (phases.isNotEmpty())
                assertThat(NetworkService.connectedTerminals(t)[crIndex].nominalPhasePaths, containsInAnyOrder(*phases.toTypedArray()))
            else
                assertThat(NetworkService.connectedTerminals(t), empty())
        }
    }

    private fun Terminal.setNormalPhases(normalPhases: PhaseCode) {
        normalPhases.singlePhases.filterIndexed { index, phase ->
            tracedPhases.setNormal(phase, FeederDirection.DOWNSTREAM, phases.singlePhases[index])
        }
    }

    private fun NetworkService.getNextConnectivityNode(): ConnectivityNode = getOrPutConnectivityNode("cn${num<ConnectivityNode>()}")

}

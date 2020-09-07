/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 * This file is part of evolve-sdk-jvm.
 *
 * evolve-sdk-jvm is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * evolve-sdk-jvm is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with evolve-sdk-jvm.  If not, see <https://www.gnu.org/licenses/>.
 */


@file:Suppress("PropertyName")

package com.zepben.cimbend.network.tracing

import com.zepben.cimbend.cim.iec61968.assetinfo.OverheadWireInfo
import com.zepben.cimbend.cim.iec61970.base.core.ConductingEquipment
import com.zepben.cimbend.cim.iec61970.base.core.PhaseCode
import com.zepben.cimbend.cim.iec61970.base.core.Substation
import com.zepben.cimbend.cim.iec61970.base.core.Terminal
import com.zepben.cimbend.cim.iec61970.base.wires.*
import com.zepben.cimbend.cim.iec61970.base.wires.SinglePhaseKind.*
import com.zepben.cimbend.network.NetworkService
import com.zepben.cimbend.network.model.PhaseDirection
import com.zepben.cimbend.network.model.PhaseDirection.*
import com.zepben.cimbend.network.model.PhaseDirection.NONE
import com.zepben.cimbend.testdata.TestDataCreators.createSourceForConnecting
import com.zepben.cimbend.testdata.TestDataCreators.createTerminal
import com.zepben.cimbend.testdata.TestNetworks
import com.zepben.test.util.ExpectException.expect
import com.zepben.test.util.junit.SystemLogExtension
import org.hamcrest.Matchers.equalTo
import org.junit.Assert.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.util.function.Function

class SetPhasesTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Suppress("PrivatePropertyName")
    private val P_NONE = SinglePhaseKind.NONE

    @Test
    internal fun setPhasesTest() {
        val n = TestNetworks.getNetwork(1)

        val sw = n.get<Breaker>("node6")!!
        assertNotNull(sw)
        assertTrue(sw.isOpen(X))
        assertTrue(sw.isOpen(Y))
        doSetPhasesTrace(n)

        // Check various points to make sure phases have been applied during the trace.
        checkExpectedCurrentPhases(getT(n, "acLineSegment0", 1), arrayOf(A, B, C, N), arrayOf(IN, IN, IN, IN))
        checkExpectedCurrentPhases(getT(n, "acLineSegment0", 2), arrayOf(A, B, C, N), arrayOf(OUT, OUT, OUT, OUT))
        checkExpectedCurrentPhases(getT(n, "acLineSegment1", 1), arrayOf(A, B, C, N), arrayOf(IN, IN, IN, IN))
        checkExpectedCurrentPhases(getT(n, "acLineSegment4", 1), arrayOf(A, B), arrayOf(IN, IN))
        checkExpectedCurrentPhases(getT(n, "node4", 1), arrayOf(A, B), arrayOf(IN, IN))
        checkExpectedCurrentPhases(getT(n, "node4", 2), arrayOf(A, B), arrayOf(OUT, OUT))
        checkExpectedCurrentPhases(getT(n, "node4", 3), arrayOf(A, B), arrayOf(OUT, OUT))
        checkExpectedCurrentPhases(getT(n, "node8", 1), arrayOf(A), arrayOf(IN))
        checkExpectedCurrentPhases(getT(n, "node5", 1), arrayOf(A, B), arrayOf(IN, IN))
        checkExpectedCurrentPhases(getT(n, "node5", 2), arrayOf(A, B), arrayOf(OUT, OUT))
        checkExpectedCurrentPhases(getT(n, "node5", 3), arrayOf(A, B), arrayOf(OUT, OUT))
        checkExpectedCurrentPhases(getT(n, "node9", 1), arrayOf(B), arrayOf(IN))
        checkExpectedCurrentPhases(getT(n, "node6", 1), arrayOf(A, B), arrayOf(IN, IN))
        checkExpectedCurrentPhases(getT(n, "node6", 2), arrayOf(B, C), arrayOf(IN, IN))
        checkExpectedCurrentPhases(getT(n, "acLineSegment2", 2), arrayOf(A, B, C, N), arrayOf(OUT, OUT, OUT, OUT))
        checkExpectedCurrentPhases(getT(n, "acLineSegment3", 1), arrayOf(A, B, C, N), arrayOf(IN, IN, IN, IN))
        checkExpectedCurrentPhases(getT(n, "acLineSegment9", 2), arrayOf(B, C), arrayOf(IN, IN))
        checkExpectedCurrentPhases(getT(n, "node2", 1), arrayOf(A, B, C, N), arrayOf(IN, IN, IN, IN))
    }

    @Test
    internal fun setPhasesMultiSourceTest() {
        val n = TestNetworks.getNetwork(5)

        val sw = n.get<Breaker>("node1")!!
        assertNotNull(sw)
        assertTrue(sw.isOpen(A))
        assertFalse(sw.isOpen(B))
        assertTrue(sw.isOpen(C))
        assertFalse(sw.isOpen(N))

        doSetPhasesTrace(n)

        // Check various points to make sure phases have been applied during the trace.
        checkExpectedCurrentPhases(getT(n, "node0", 1), arrayOf(A, B, C, N), arrayOf(OUT, BOTH, OUT, BOTH))
        checkExpectedCurrentPhases(getT(n, "acLineSegment0", 1), arrayOf(A, B, C, N), arrayOf(IN, BOTH, IN, BOTH))
        checkExpectedCurrentPhases(getT(n, "acLineSegment0", 2), arrayOf(A, B, C, N), arrayOf(OUT, BOTH, OUT, BOTH))
        checkExpectedCurrentPhases(getT(n, "node1", 1), arrayOf(A, B, C, N), arrayOf(IN, BOTH, IN, BOTH))
        checkExpectedCurrentPhases(getT(n, "node1", 2), arrayOf(A, B, C, N), arrayOf(IN, BOTH, IN, BOTH))
        checkExpectedCurrentPhases(getT(n, "acLineSegment1", 1), arrayOf(A, B, C, N), arrayOf(OUT, BOTH, OUT, BOTH))
        checkExpectedCurrentPhases(getT(n, "acLineSegment1", 2), arrayOf(A, B, C, N), arrayOf(IN, BOTH, IN, BOTH))
        checkExpectedCurrentPhases(getT(n, "node2", 1), arrayOf(A, B, C, N), arrayOf(OUT, BOTH, OUT, BOTH))
    }

    @Test
    internal fun setPhasesTestCrossPhases() {
        val n = TestNetworks.getNetwork(1)

        val sw = n.get<Breaker>("node6")!!
        assertNotNull(sw)
        assertTrue(sw.isOpen(X))
        sw.setOpen(false, Y)

        expect { doSetPhasesTrace(n) }
            .toThrow(IllegalStateException::class.java)
    }

    // Build a small network with a closed loop and test tracing phases both ways
    @Test
    internal fun setPhasesInClosedLoopTest() {
        val n = NetworkService()

        val n0 = createSourceForConnecting(n, "n0", 1, PhaseCode.A)
        for (i in 0..4) {
            val c = AcLineSegment("c$i").apply { name = "c"; addTerminals(2, PhaseCode.A) }
            n.add(c)
        }

        for (i in 1..3) {
            val bn = Junction("n$i").apply { addTerminals(2, PhaseCode.A) }
            n.add(bn)
        }

        val n4 = Junction("n4").apply { addTerminal(createTerminal(n, this, PhaseCode.A, 1)) }
        n.add(n4)

        assertTrue(n.connect(getT(n, "n0", 1), getT(n, "c0", 1)))
        assertTrue(n.connect(getT(n, "c0", 2), getT(n, "n1", 1)))
        assertTrue(n.connect(getT(n, "n1", 2), getT(n, "c1", 1)))
        assertTrue(n.connect(getT(n, "c1", 2), getT(n, "n2", 1)))
        assertTrue(n.connect(getT(n, "n2", 2), getT(n, "c2", 1)))
        assertTrue(n.connect(getT(n, "c2", 2), getT(n, "n3", 1)))
        assertTrue(n.connect(getT(n, "n3", 2), getT(n, "c3", 1)))
        assertTrue(n.connect(getT(n, "c3", 2), getT(n, "n1", 2)))
        assertTrue(n.connect(getT(n, "c4", 1), getT(n, "n3", 2)))
        assertTrue(n.connect(getT(n, "n4", 1), getT(n, "c4", 2)))

        n0.terminals.forEach { t ->
            for (phase in t.phases.singlePhases()) {
                t.normalPhases(phase).add(A, OUT)
                t.currentPhases(phase).add(A, OUT)
            }
            checkExpectedCurrentPhases(t, arrayOf(A), arrayOf(OUT))
        }

        doSetPhasesTrace(n0)
        checkExpectedCurrentPhases(getT(n, "n0", 1), arrayOf(A), arrayOf(OUT))
        checkExpectedCurrentPhases(getT(n, "c0", 1), arrayOf(A), arrayOf(IN))
        checkExpectedCurrentPhases(getT(n, "c0", 2), arrayOf(A), arrayOf(OUT))
        checkExpectedCurrentPhases(getT(n, "n1", 1), arrayOf(A), arrayOf(IN))
        checkExpectedCurrentPhases(getT(n, "n1", 2), arrayOf(A), arrayOf(BOTH))
        checkExpectedCurrentPhases(getT(n, "c1", 1), arrayOf(A), arrayOf(BOTH))
        checkExpectedCurrentPhases(getT(n, "c1", 2), arrayOf(A), arrayOf(BOTH))
        checkExpectedCurrentPhases(getT(n, "n2", 1), arrayOf(A), arrayOf(BOTH))
        checkExpectedCurrentPhases(getT(n, "n2", 2), arrayOf(A), arrayOf(BOTH))
        checkExpectedCurrentPhases(getT(n, "c2", 1), arrayOf(A), arrayOf(BOTH))
        checkExpectedCurrentPhases(getT(n, "c2", 2), arrayOf(A), arrayOf(BOTH))
        checkExpectedCurrentPhases(getT(n, "n3", 1), arrayOf(A), arrayOf(BOTH))
        checkExpectedCurrentPhases(getT(n, "n3", 2), arrayOf(A), arrayOf(BOTH))
        checkExpectedCurrentPhases(getT(n, "c3", 1), arrayOf(A), arrayOf(BOTH))
        checkExpectedCurrentPhases(getT(n, "c3", 2), arrayOf(A), arrayOf(BOTH))
        checkExpectedCurrentPhases(getT(n, "c4", 1), arrayOf(A), arrayOf(IN))
        checkExpectedCurrentPhases(getT(n, "c4", 2), arrayOf(A), arrayOf(OUT))
        checkExpectedCurrentPhases(getT(n, "n4", 1), arrayOf(A), arrayOf(IN))
    }

    @Test
    internal fun setPhasesThroughFeederCbs() {
        val n = NetworkService()

        createSourceForConnecting(n, "n0", 1, PhaseCode.ABCN)
        createSourceForConnecting(n, "n1", 1, PhaseCode.ABCN)

        fun createBreaker(id: String, open: Boolean, normallyOpen: Array<Boolean>): Breaker {
            return Breaker(id).apply {
                name = id
                addTerminals(2, PhaseCode.ABCN)
                setOpen(isOpen = open)
                for (i in normallyOpen.indices)
                    setNormallyOpen(normallyOpen[i], PhaseCode.ABCN.singlePhases()[i])
                addContainer(Substation())
                assertTrue(this.isSubstationBreaker)
            }
        }

        n.add(createBreaker("f0", true, arrayOf(false, true, false, true)))
        n.add(createBreaker("f1", true, arrayOf(false, false, false, false)))
        n.add(createBreaker("f2", true, arrayOf(false, false, false, false)))
        n.add(createBreaker("f3", true, arrayOf(true, false, false, true)))
        n.add(createBreaker("f4", false, arrayOf(false, false, false, false)))

        val overheadWireInfo = OverheadWireInfo()
        for (i in 0..5) {
            n.add(AcLineSegment("c$i").apply {
                name = "c$i"
                assetInfo = overheadWireInfo
                addTerminals(2, PhaseCode.ABCN)
            })
        }

        assertTrue(n.connect(getT(n, "n0", 1), getT(n, "c0", 1)))
        assertTrue(n.connect(getT(n, "c0", 2), getT(n, "f0", 1)))
        assertTrue(n.connect(getT(n, "f0", 2), getT(n, "c1", 1)))
        assertTrue(n.connect(getT(n, "c1", 2), getT(n, "f1", 1)))
        assertTrue(n.connect(getT(n, "f1", 2), getT(n, "c2", 1)))
        assertTrue(n.connect(getT(n, "c2", 2), getT(n, "f2", 1)))
        assertTrue(n.connect(getT(n, "f2", 2), getT(n, "c3", 1)))
        assertTrue(n.connect(getT(n, "c3", 2), getT(n, "f3", 1)))
        assertTrue(n.connect(getT(n, "f3", 2), getT(n, "c4", 1)))
        assertTrue(n.connect(getT(n, "c4", 2), getT(n, "n1", 1)))

        doSetPhasesTrace(n)

        checkExpectedNormalPhases(getT(n, "n0", 1), arrayOf(A, B, C, N), arrayOf(OUT, OUT, OUT, OUT))
        checkExpectedNormalPhases(getT(n, "c0", 1), arrayOf(A, B, C, N), arrayOf(IN, IN, IN, IN))
        checkExpectedNormalPhases(getT(n, "c0", 2), arrayOf(A, B, C, N), arrayOf(OUT, OUT, OUT, OUT))
        checkExpectedNormalPhases(getT(n, "f0", 1), arrayOf(A, B, C, N), arrayOf(IN, IN, IN, IN))
        checkExpectedNormalPhases(getT(n, "f0", 2), arrayOf(A, B, C, P_NONE), arrayOf(OUT, IN, OUT, NONE))
        checkExpectedNormalPhases(getT(n, "c1", 1), arrayOf(A, B, C, P_NONE), arrayOf(IN, OUT, IN, NONE))
        checkExpectedNormalPhases(getT(n, "c1", 2), arrayOf(A, B, C, P_NONE), arrayOf(OUT, IN, OUT, NONE))
        checkExpectedNormalPhases(getT(n, "f1", 1), arrayOf(A, B, C, P_NONE), arrayOf(IN, OUT, IN, NONE))
        checkExpectedNormalPhases(getT(n, "f1", 2), arrayOf(A, B, C, P_NONE), arrayOf(OUT, IN, BOTH, NONE))
        checkExpectedNormalPhases(getT(n, "c2", 1), arrayOf(A, B, C, P_NONE), arrayOf(IN, OUT, BOTH, NONE))
        checkExpectedNormalPhases(getT(n, "c2", 2), arrayOf(A, B, C, P_NONE), arrayOf(OUT, IN, BOTH, NONE))
        checkExpectedNormalPhases(getT(n, "f2", 1), arrayOf(A, B, C, P_NONE), arrayOf(IN, OUT, BOTH, NONE))
        checkExpectedNormalPhases(getT(n, "f2", 2), arrayOf(A, B, C, P_NONE), arrayOf(OUT, IN, IN, NONE))
        checkExpectedNormalPhases(getT(n, "c3", 1), arrayOf(A, B, C, P_NONE), arrayOf(IN, OUT, OUT, NONE))
        checkExpectedNormalPhases(getT(n, "c3", 2), arrayOf(A, B, C, P_NONE), arrayOf(OUT, IN, IN, NONE))
        checkExpectedNormalPhases(getT(n, "f3", 1), arrayOf(A, B, C, P_NONE), arrayOf(IN, OUT, OUT, NONE))
        checkExpectedNormalPhases(getT(n, "f3", 2), arrayOf(A, B, C, N), arrayOf(IN, IN, IN, IN))
        checkExpectedNormalPhases(getT(n, "c4", 1), arrayOf(A, B, C, N), arrayOf(OUT, OUT, OUT, OUT))
        checkExpectedNormalPhases(getT(n, "c4", 2), arrayOf(A, B, C, N), arrayOf(IN, IN, IN, IN))
        checkExpectedNormalPhases(getT(n, "n1", 1), arrayOf(A, B, C, N), arrayOf(OUT, OUT, OUT, OUT))
        checkExpectedNormalPhases(getT(n, "f4", 1), arrayOf(P_NONE, P_NONE, P_NONE, P_NONE), arrayOf(NONE, NONE, NONE, NONE))
        checkExpectedNormalPhases(getT(n, "f4", 2), arrayOf(P_NONE, P_NONE, P_NONE, P_NONE), arrayOf(NONE, NONE, NONE, NONE))

        checkExpectedCurrentPhases(getT(n, "n0", 1), arrayOf(A, B, C, N), arrayOf(OUT, OUT, OUT, OUT))
        checkExpectedCurrentPhases(getT(n, "c0", 1), arrayOf(A, B, C, N), arrayOf(IN, IN, IN, IN))
        checkExpectedCurrentPhases(getT(n, "c0", 2), arrayOf(A, B, C, N), arrayOf(OUT, OUT, OUT, OUT))
        checkExpectedCurrentPhases(getT(n, "f0", 1), arrayOf(A, B, C, N), arrayOf(IN, IN, IN, IN))
        checkExpectedCurrentPhases(getT(n, "f0", 2), arrayOf(P_NONE, P_NONE, P_NONE, P_NONE), arrayOf(NONE, NONE, NONE, NONE))
        checkExpectedCurrentPhases(getT(n, "c1", 1), arrayOf(P_NONE, P_NONE, P_NONE, P_NONE), arrayOf(NONE, NONE, NONE, NONE))
        checkExpectedCurrentPhases(getT(n, "c1", 2), arrayOf(P_NONE, P_NONE, P_NONE, P_NONE), arrayOf(NONE, NONE, NONE, NONE))
        checkExpectedCurrentPhases(getT(n, "f1", 1), arrayOf(P_NONE, P_NONE, P_NONE, P_NONE), arrayOf(NONE, NONE, NONE, NONE))
        checkExpectedCurrentPhases(getT(n, "f1", 2), arrayOf(P_NONE, P_NONE, P_NONE, P_NONE), arrayOf(NONE, NONE, NONE, NONE))
        checkExpectedCurrentPhases(getT(n, "c2", 1), arrayOf(P_NONE, P_NONE, P_NONE, P_NONE), arrayOf(NONE, NONE, NONE, NONE))
        checkExpectedCurrentPhases(getT(n, "c2", 2), arrayOf(P_NONE, P_NONE, P_NONE, P_NONE), arrayOf(NONE, NONE, NONE, NONE))
        checkExpectedCurrentPhases(getT(n, "f2", 1), arrayOf(P_NONE, P_NONE, P_NONE, P_NONE), arrayOf(NONE, NONE, NONE, NONE))
        checkExpectedCurrentPhases(getT(n, "f2", 2), arrayOf(P_NONE, P_NONE, P_NONE, P_NONE), arrayOf(NONE, NONE, NONE, NONE))
        checkExpectedCurrentPhases(getT(n, "c3", 1), arrayOf(P_NONE, P_NONE, P_NONE, P_NONE), arrayOf(NONE, NONE, NONE, NONE))
        checkExpectedCurrentPhases(getT(n, "c3", 2), arrayOf(P_NONE, P_NONE, P_NONE, P_NONE), arrayOf(NONE, NONE, NONE, NONE))
        checkExpectedCurrentPhases(getT(n, "f3", 1), arrayOf(P_NONE, P_NONE, P_NONE, P_NONE), arrayOf(NONE, NONE, NONE, NONE))
        checkExpectedCurrentPhases(getT(n, "f3", 2), arrayOf(A, B, C, N), arrayOf(IN, IN, IN, IN))
        checkExpectedCurrentPhases(getT(n, "c4", 1), arrayOf(A, B, C, N), arrayOf(OUT, OUT, OUT, OUT))
        checkExpectedCurrentPhases(getT(n, "c4", 2), arrayOf(A, B, C, N), arrayOf(IN, IN, IN, IN))
        checkExpectedCurrentPhases(getT(n, "n1", 1), arrayOf(A, B, C, N), arrayOf(OUT, OUT, OUT, OUT))
        checkExpectedCurrentPhases(getT(n, "f4", 1), arrayOf(P_NONE, P_NONE, P_NONE, P_NONE), arrayOf(NONE, NONE, NONE, NONE))
        checkExpectedCurrentPhases(getT(n, "f4", 2), arrayOf(P_NONE, P_NONE, P_NONE, P_NONE), arrayOf(NONE, NONE, NONE, NONE))
    }

    @Test
    internal fun canStartFromSingleTerminalFeederCbWithAnySequence() {
        val n = NetworkService()

        val substation = Substation()
        fun createBreaker(id: String, sequenceNumber: Int): Breaker {
            return Breaker(id).apply {
                name = id
                addTerminal(Terminal().also { it.conductingEquipment = this; it.sequenceNumber = sequenceNumber })
                addContainer(substation)
                substation.addEquipment(this)
            }
        }

        val fcb1: Breaker = createBreaker("f1", 1)
        val fcb2: Breaker = createBreaker("f2", 2)
        val energySource1 = createSourceForConnecting(n, "energySource1", 1, PhaseCode.ABC)
        val energySource2 = createSourceForConnecting(n, "energySource2", 1, PhaseCode.ABC)

        n.connect(fcb1.getTerminal(1)!!, energySource1.getTerminal(1)!!)
        n.connect(fcb2.getTerminal(2)!!, energySource1.getTerminal(1)!!)

        n.add(fcb1)
        n.add(fcb2)
        n.add(energySource1)
        n.add(energySource2)

        doSetPhasesTrace(n)
    }

    private fun doSetPhasesTrace(n: NetworkService) {
        Tracing.setPhases().run(n)
        n.sequenceOf<EnergySource>().forEach { PhaseLogger.trace(it) }
    }

    private fun doSetPhasesTrace(start: ConductingEquipment) {
        assertEquals(1, start.numTerminals())

        Tracing.setPhases().run(start.getTerminal(1)!!, emptyList())
        PhaseLogger.trace(start)
    }

    // Get a terminal from an asset in the network
    private fun getT(n: NetworkService, id: String, terminalId: Int) =
        n.get<ConductingEquipment>(id)!!.getTerminal(terminalId)!!

    // TODO: These should be combined to force doing something with normalPhases and currentPhases, not just one.
    private fun checkExpectedNormalPhases(t: Terminal, singlePhaseKinds: Array<SinglePhaseKind>, directions: Array<PhaseDirection>) {
        checkExpectedPhases(t, singlePhaseKinds, directions, Function { phase: SinglePhaseKind? -> t.normalPhases(phase!!) })
    }

    private fun checkExpectedCurrentPhases(t: Terminal?, singlePhaseKinds: Array<SinglePhaseKind>, directions: Array<PhaseDirection>) {
        checkExpectedPhases(t, singlePhaseKinds, directions, Function { phase: SinglePhaseKind? -> t!!.currentPhases(phase!!) })
    }

    private fun checkExpectedPhases(
        t: Terminal?,
        singlePhaseKinds: Array<SinglePhaseKind>,
        directions: Array<PhaseDirection>,
        phaseStatusSelector: Function<SinglePhaseKind, PhaseStatus>
    ) {
        assertNotNull(t)
        assertEquals(singlePhaseKinds.size, directions.size)
        assertEquals(singlePhaseKinds.size, t!!.phases.singlePhases().size)
        for (i in singlePhaseKinds.indices) {
            val ps = phaseStatusSelector.apply(t.phases.singlePhases()[i])
            assertEquals(singlePhaseKinds[i], ps.phase())
            assertEquals(directions[i], ps.direction())
        }
    }

    private fun ConductingEquipment.addTerminals(count: Int, phases: PhaseCode = PhaseCode.ABC) {
        for (i in 1..count)
            addTerminal(Terminal().also { it.conductingEquipment = this; it.phases = phases })
    }
}


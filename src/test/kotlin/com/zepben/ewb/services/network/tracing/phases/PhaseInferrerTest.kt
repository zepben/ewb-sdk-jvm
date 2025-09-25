/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.network.tracing.phases

import com.zepben.ewb.cim.iec61970.base.core.ConductingEquipment
import com.zepben.ewb.cim.iec61970.base.core.PhaseCode
import com.zepben.ewb.cim.iec61970.base.core.Terminal
import com.zepben.ewb.services.network.NetworkService
import com.zepben.ewb.services.network.tracing.networktrace.operators.NetworkStateOperators
import com.zepben.ewb.services.network.tracing.phases.PhaseValidator.validatePhases
import com.zepben.ewb.testing.TestNetworkBuilder
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.spy
import com.zepben.ewb.cim.iec61970.base.wires.SinglePhaseKind as SPK

@Suppress("SameParameterValue")
internal class PhaseInferrerTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    private val phaseInferrer = PhaseInferrer(debugLogger = null)

    //
    // nominal
    // AB -> BC -> XY -> ABC
    // traced
    // AB -> B? -> B? -> ?B?
    //
    // infer nominal
    // AB -> BC -> BC -> ABC
    //
    @Test
    internal fun testABtoBCtoXYtoABC() {
        val network = TestNetworkBuilder()
            .fromSource(PhaseCode.AB)
            .toAcls(PhaseCode.BC)
            .toAcls(PhaseCode.XY)
            .toAcls(PhaseCode.ABC)
            .build()

        validatePhases(network, "c1", listOf(SPK.B, SPK.NONE))
        validatePhases(network, "c2", listOf(SPK.B, SPK.NONE))
        validatePhases(network, "c3", listOf(SPK.NONE, SPK.B, SPK.NONE))

        val normalInferred = phaseInferrer.run(network, NetworkStateOperators.NORMAL)
        val currentInferred = phaseInferrer.run(network, NetworkStateOperators.CURRENT)

        validateReturnedPhases(normalInferred, network, correct = listOf("c1", "c3"))
        validateReturnedPhases(currentInferred, network, correct = listOf("c1", "c3"))

        validatePhases(network, "c1", PhaseCode.BC)
        validatePhases(network, "c2", PhaseCode.BC)
        validatePhases(network, "c3", PhaseCode.ABC)
    }

    //
    // nominal
    // ABN -> BCN -> XYN -> ABCN
    // traced
    // ABN -> B?N -> B?N -> ?B?N
    //
    // infer nominal
    // ABN -> BCN -> BCN -> ABCN
    //
    @Test
    internal fun testABNtoBCNtoXYNtoABCN() {
        val network = TestNetworkBuilder()
            .fromSource(PhaseCode.ABN)
            .toAcls(PhaseCode.BCN)
            .toAcls(PhaseCode.XYN)
            .toAcls(PhaseCode.ABCN)
            .build()

        validatePhases(network, "c1", listOf(SPK.B, SPK.NONE, SPK.N))
        validatePhases(network, "c2", listOf(SPK.B, SPK.NONE, SPK.N))
        validatePhases(network, "c3", listOf(SPK.NONE, SPK.B, SPK.NONE, SPK.N))

        val normalInferred = phaseInferrer.run(network, NetworkStateOperators.NORMAL)
        val currentInferred = phaseInferrer.run(network, NetworkStateOperators.CURRENT)

        validateReturnedPhases(normalInferred, network, correct = listOf("c1", "c3"))
        validateReturnedPhases(currentInferred, network, correct = listOf("c1", "c3"))

        validatePhases(network, "c1", PhaseCode.BCN)
        validatePhases(network, "c2", PhaseCode.BCN)
        validatePhases(network, "c3", PhaseCode.ABCN)
    }

    //
    // nominal
    // BC -> AC -> XY -> ABC
    // traced
    // BC -> ?C -> ?C -> ??C
    //
    // infer nominal
    // BC -> AC -> AC -> ABC
    //
    @Test
    internal fun testBCtoACtoXYtoABC() {
        val network = TestNetworkBuilder()
            .fromSource(PhaseCode.BC)
            .toAcls(PhaseCode.AC)
            .toAcls(PhaseCode.XY)
            .toAcls(PhaseCode.ABC)
            .build()

        validatePhases(network, "c1", listOf(SPK.NONE, SPK.C))
        validatePhases(network, "c2", listOf(SPK.NONE, SPK.C))
        validatePhases(network, "c3", listOf(SPK.NONE, SPK.NONE, SPK.C))

        val normalInferred = phaseInferrer.run(network, NetworkStateOperators.NORMAL)
        val currentInferred = phaseInferrer.run(network, NetworkStateOperators.CURRENT)

        validateReturnedPhases(normalInferred, network, correct = listOf("c1", "c3"))
        validateReturnedPhases(currentInferred, network, correct = listOf("c1", "c3"))

        validatePhases(network, "c1", PhaseCode.AC)
        validatePhases(network, "c2", PhaseCode.AC)
        validatePhases(network, "c3", PhaseCode.ABC)
    }

    //
    // nominal
    // ABC -> XYN -> XY -> BC
    // traced
    // ABC -> BC? -> BC -> BC
    //
    // infer nominal
    // ABC -> BCN -> BC -> BC
    //
    @Test
    internal fun testABCtoXYNtoXYtoBC() {
        val network = TestNetworkBuilder()
            .fromSource(PhaseCode.ABC)
            .toAcls(PhaseCode.XYN)
            .toAcls(PhaseCode.XY)
            .toAcls(PhaseCode.BC)
            .build()

        validatePhases(network, "c1", listOf(SPK.B, SPK.C, SPK.NONE))
        validatePhases(network, "c2", PhaseCode.BC)
        validatePhases(network, "c3", PhaseCode.BC)

        val normalInferred = phaseInferrer.run(network, NetworkStateOperators.NORMAL)
        val currentInferred = phaseInferrer.run(network, NetworkStateOperators.CURRENT)

        validateReturnedPhases(normalInferred, network, correct = listOf("c1"))
        validateReturnedPhases(currentInferred, network, correct = listOf("c1"))

        validatePhases(network, "c1", PhaseCode.BCN)
        validatePhases(network, "c2", PhaseCode.BC)
        validatePhases(network, "c3", PhaseCode.BC)
    }

    //
    // nominal
    // ABC -> XY -> XYN -> BC
    // traced
    // ABC -> BC -> BC? -> BC
    //
    // infer nominal
    // ABC -> BC -> BCN -> BC
    //
    @Test
    internal fun testABCtoXYtoXYNtoBC() {
        val network = TestNetworkBuilder()
            .fromSource(PhaseCode.ABC)
            .toAcls(PhaseCode.XY)
            .toAcls(PhaseCode.XYN)
            .toAcls(PhaseCode.BC)
            .build()

        validatePhases(network, "c1", PhaseCode.BC)
        validatePhases(network, "c2", listOf(SPK.B, SPK.C, SPK.NONE))
        validatePhases(network, "c3", PhaseCode.BC)

        val normalInferred = phaseInferrer.run(network, NetworkStateOperators.NORMAL)
        val currentInferred = phaseInferrer.run(network, NetworkStateOperators.CURRENT)

        validateReturnedPhases(normalInferred, network, correct = listOf("c2"))
        validateReturnedPhases(currentInferred, network, correct = listOf("c2"))

        validatePhases(network, "c1", PhaseCode.BC)
        validatePhases(network, "c2", PhaseCode.BCN)
        validatePhases(network, "c3", PhaseCode.BC)
    }

    //
    // nominal
    // ABC -> ABC -> N -> ABCN
    // traced
    // ABC -> ABC -> ? -> ????
    //
    // infer nominal
    // ABC -> ABC -> N -> ABCN
    //
    @Test
    internal fun testABCtoNtoABCN() {
        val network = TestNetworkBuilder()
            .fromSource(PhaseCode.ABC)
            .toAcls(PhaseCode.ABC)
            .toAcls(PhaseCode.N)
            .toAcls(PhaseCode.ABCN)
            .build()

        validatePhases(network, "c1", PhaseCode.ABC)
        validatePhases(network, "c2", PhaseCode.NONE)
        validatePhases(network, "c3", PhaseCode.NONE)

        val normalInferred = phaseInferrer.run(network, NetworkStateOperators.NORMAL)
        val currentInferred = phaseInferrer.run(network, NetworkStateOperators.CURRENT)

        validateReturnedPhases(normalInferred, network, correct = listOf("c2", "c3"))
        validateReturnedPhases(currentInferred, network, correct = listOf("c2", "c3"))

        validatePhases(network, "c1", PhaseCode.ABC)
        validatePhases(network, "c2", PhaseCode.N)
        validatePhases(network, "c3", PhaseCode.ABCN)
    }

    //
    // nominal
    // ABC -> ABC -> B -> XYN
    // traced
    // ABC -> ABC -> B -> B??
    //
    // infer nominal
    // ABC -> ABC -> B -> B?N
    // infer xy
    // ABC -> ABC -> B -> BCN
    //
    @Test
    internal fun testABCtoBtoXYN() {
        val network = TestNetworkBuilder()
            .fromSource(PhaseCode.ABC)
            .toAcls(PhaseCode.ABC)
            .toAcls(PhaseCode.B)
            .toAcls(PhaseCode.XYN)
            .build()

        validatePhases(network, "c1", PhaseCode.ABC)
        validatePhases(network, "c2", PhaseCode.B)
        validatePhases(network, "c3", listOf(SPK.B, SPK.NONE, SPK.NONE))

        val normalInferred = phaseInferrer.run(network, NetworkStateOperators.NORMAL)
        val currentInferred = phaseInferrer.run(network, NetworkStateOperators.CURRENT)

        validateReturnedPhases(normalInferred, network, suspect = listOf("c3"))
        validateReturnedPhases(currentInferred, network, suspect = listOf("c3"))

        validatePhases(network, "c1", PhaseCode.ABC)
        validatePhases(network, "c2", PhaseCode.B)
        validatePhases(network, "c3", PhaseCode.BCN)
    }

    //
    // nominal
    // ABC -> ABC -> C -> XYN
    // traced
    // ABC -> ABC -> C -> C??
    //
    // infer nominal
    // ABC -> ABC -> C -> C?N
    // infer xy
    // ABC -> ABC -> C -> C?N
    //
    @Test
    internal fun testABCtoCtoXYN() {
        val network = TestNetworkBuilder()
            .fromSource(PhaseCode.ABC)
            .toAcls(PhaseCode.ABC)
            .toAcls(PhaseCode.C)
            .toAcls(PhaseCode.XYN)
            .build()

        validatePhases(network, "c1", PhaseCode.ABC)
        validatePhases(network, "c2", PhaseCode.C)
        validatePhases(network, "c3", listOf(SPK.C, SPK.NONE, SPK.NONE))

        val normalInferred = phaseInferrer.run(network, NetworkStateOperators.NORMAL)
        val currentInferred = phaseInferrer.run(network, NetworkStateOperators.CURRENT)

        validateReturnedPhases(normalInferred, network, suspect = listOf("c3"))
        validateReturnedPhases(currentInferred, network, suspect = listOf("c3"))

        validatePhases(network, "c1", PhaseCode.ABC)
        validatePhases(network, "c2", PhaseCode.C)
        validatePhases(network, "c3", listOf(SPK.C, SPK.NONE, SPK.N))
    }

    //
    // nominal
    // ABC -> ABC -> A -> XN
    // traced
    // ABC -> ABC -> A -> A?
    //
    // infer nominal
    // ABC -> ABC -> A -> AN
    //
    @Test
    internal fun testABCtoAtoXN() {
        val network = TestNetworkBuilder()
            .fromSource(PhaseCode.ABC)
            .toAcls(PhaseCode.ABC)
            .toAcls(PhaseCode.A)
            .toAcls(PhaseCode.XN)
            .build()

        validatePhases(network, "c1", PhaseCode.ABC)
        validatePhases(network, "c2", PhaseCode.A)
        validatePhases(network, "c3", listOf(SPK.A, SPK.NONE))

        val normalInferred = phaseInferrer.run(network, NetworkStateOperators.NORMAL)
        val currentInferred = phaseInferrer.run(network, NetworkStateOperators.CURRENT)

        validateReturnedPhases(normalInferred, network, correct = listOf("c3"))
        validateReturnedPhases(currentInferred, network, correct = listOf("c3"))

        validatePhases(network, "c1", PhaseCode.ABC)
        validatePhases(network, "c2", PhaseCode.A)
        validatePhases(network, "c3", PhaseCode.AN)
    }

    //
    // nominal
    // AN <-> ABCN <-> AN
    // traced
    // AN <-> A??N <-> AN
    //
    // infer nominal
    // AN <-> ABCN <-> AN
    //
    @Test
    internal fun testDualFeedANtoABCN() {
        val network = TestNetworkBuilder()
            .fromSource(PhaseCode.AN)
            .toAcls(PhaseCode.ABCN)
            .toSource(PhaseCode.AN)
            .build()

        validatePhases(network, "s0", PhaseCode.AN)
        validatePhases(network, "c1", listOf(SPK.A, SPK.NONE, SPK.NONE, SPK.N))
        validatePhases(network, "s2", PhaseCode.AN)

        val normalInferred = phaseInferrer.run(network, NetworkStateOperators.NORMAL)
        val currentInferred = phaseInferrer.run(network, NetworkStateOperators.CURRENT)

        validateReturnedPhases(normalInferred, network, correct = listOf("c1"))
        validateReturnedPhases(currentInferred, network, correct = listOf("c1"))

        validatePhases(network, "s0", PhaseCode.AN)
        validatePhases(network, "c1", PhaseCode.ABCN)
        validatePhases(network, "s2", PhaseCode.AN)
    }

    //
    // nominal
    // ABCN -> ABCN -> N -> AB -> XY
    // traced
    // ABCN -> ABCN -> N -> ?? -> ??
    //
    // infer nominal
    // ABCN -> ABCN -> N -> AB -> AB
    //
    @Test
    internal fun testABCNtoNtoABtoXY() {
        val network = TestNetworkBuilder()
            .fromSource(PhaseCode.ABCN)
            .toAcls(PhaseCode.ABCN)
            .toAcls(PhaseCode.N)
            .toAcls(PhaseCode.AB)
            .toAcls(PhaseCode.XY)
            .build()

        validatePhases(network, "c1", PhaseCode.ABCN)
        validatePhases(network, "c2", PhaseCode.N)
        validatePhases(network, "c3", PhaseCode.NONE)
        validatePhases(network, "c4", PhaseCode.NONE)

        val normalInferred = phaseInferrer.run(network, NetworkStateOperators.NORMAL)
        val currentInferred = phaseInferrer.run(network, NetworkStateOperators.CURRENT)

        validateReturnedPhases(normalInferred, network, correct = listOf("c3"))
        validateReturnedPhases(currentInferred, network, correct = listOf("c3"))

        validatePhases(network, "c1", PhaseCode.ABCN)
        validatePhases(network, "c2", PhaseCode.N)
        validatePhases(network, "c3", PhaseCode.AB)
        validatePhases(network, "c4", PhaseCode.AB)
    }

    //
    // nominal
    // ABC -> ABC -> ABC OPEN SWICH -> ABC
    // traced
    // ABC -> ABC -> ABC/??? -> ???
    //
    // infer nominal
    // ABC -> ABC -> ABC/??? -> ???
    //
    @Test
    internal fun testWithOpenSwitch() {
        val network = TestNetworkBuilder()
            .fromSource(PhaseCode.ABC)
            .toAcls(PhaseCode.ABC)
            .toBreaker(PhaseCode.ABC, isNormallyOpen = true)
            .toAcls(PhaseCode.ABC)
            .build()

        validatePhases(network, "c1", PhaseCode.ABC)
        validatePhases(network, "b2", PhaseCode.ABC, PhaseCode.NONE)
        validatePhases(network, "c3", PhaseCode.NONE)

        val normalInferred = phaseInferrer.run(network, NetworkStateOperators.NORMAL)
        val currentInferred = phaseInferrer.run(network, NetworkStateOperators.CURRENT)

        validateReturnedPhases(normalInferred, network)
        validateReturnedPhases(currentInferred, network)

        validatePhases(network, "c1", PhaseCode.ABC)
        validatePhases(network, "b2", PhaseCode.ABC, PhaseCode.NONE)
        validatePhases(network, "c3", PhaseCode.NONE)
    }

    //
    // s0 * 1----2 * 1----2 * 1----2 * 1----2 * 1----2 * 1----2 *
    //        c1       c2       c3       c4   2   c5   1   c9
    // ABC    ABC      XY       ABC      ABC  |   ABC  |   ABC
    //                                     c8 |     c6 |
    //                                     ABC|     ABC|
    //                                        1        2
    //                                        * 2----1 *
    //                                            c7
    //                                            XY
    //
    @Test
    internal fun validateDirectionsWithDroppedDirectionLoop() {
        val network = TestNetworkBuilder()
            .fromSource(PhaseCode.ABC) // s0
            .toAcls(PhaseCode.ABC) // c1
            .toAcls(PhaseCode.XY) // c2
            .toAcls(PhaseCode.ABC) // c3
            .toAcls(PhaseCode.ABC) // c4
            .toAcls(PhaseCode.ABC) // c5
            .toAcls(PhaseCode.ABC) // c6
            .toAcls(PhaseCode.XY) // c7
            .toAcls(PhaseCode.ABC) // c8
            .connect("c8", "c4", 2, 2)
            .branchFrom("c5")
            .toAcls(PhaseCode.ABC) // c9
            .addFeeder("s0")
            .build()

        // We need to make sure "c6-t2" is returned first to replicate the production data bug that this test is designed to replicate.
        val networkSpy = spy(network).also { ns ->
            doReturn(listOf(network["c6-t2"]!!, *network.listOf<Terminal> { it.mRID != "c6-t2" }.toTypedArray()))
                .`when`(ns)
                .listOf(Terminal::class.java)
        }
        phaseInferrer.run(networkSpy, NetworkStateOperators.NORMAL)
        phaseInferrer.run(networkSpy, NetworkStateOperators.CURRENT)

        validatePhases(network, "c2", PhaseCode.AC, PhaseCode.AC)
        validatePhases(network, "c3", PhaseCode.ABC, PhaseCode.ABC)
        validatePhases(network, "c4", PhaseCode.ABC, PhaseCode.ABC)
        validatePhases(network, "c5", PhaseCode.ABC, PhaseCode.ABC)
        validatePhases(network, "c6", PhaseCode.ABC, PhaseCode.ABC)
        validatePhases(network, "c7", PhaseCode.AC, PhaseCode.AC)
        validatePhases(network, "c8", PhaseCode.ABC, PhaseCode.ABC)
        validatePhases(network, "c9", PhaseCode.ABC, PhaseCode.ABC)
    }

    private fun validateReturnedPhases(
        inferredPhases: Collection<PhaseInferrer.InferredPhase>,
        network: NetworkService,
        correct: List<String> = emptyList(),
        suspect: List<String> = emptyList()
    ) {
        val expectedInferred = correct.map { PhaseInferrer.InferredPhase(network.get<ConductingEquipment>(it)!!, false) } +
            suspect.map { PhaseInferrer.InferredPhase(network.get<ConductingEquipment>(it)!!, true) }

        assertThat(inferredPhases, containsInAnyOrder(*expectedInferred.toTypedArray()))
    }

}

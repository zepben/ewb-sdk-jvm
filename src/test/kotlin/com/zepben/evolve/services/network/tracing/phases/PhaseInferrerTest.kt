/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network.tracing.phases

import com.zepben.evolve.cim.iec61970.base.core.PhaseCode
import com.zepben.evolve.cim.iec61970.base.core.Terminal
import com.zepben.evolve.services.network.tracing.Tracing
import com.zepben.evolve.services.network.tracing.phases.PhaseValidator.validatePhases
import com.zepben.evolve.testing.TestNetworkBuilder
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.spy
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind as SPK

@Suppress("SameParameterValue")
class PhaseInferrerTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    private val phaseInferrer = PhaseInferrer()

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

        phaseInferrer.run(network)

        validatePhases(network, "c1", PhaseCode.BC)
        validatePhases(network, "c2", PhaseCode.BC)
        validatePhases(network, "c3", PhaseCode.ABC)

        validateLog(correct = listOf("c1", "c3"))
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

        phaseInferrer.run(network)

        validatePhases(network, "c1", PhaseCode.BCN)
        validatePhases(network, "c2", PhaseCode.BCN)
        validatePhases(network, "c3", PhaseCode.ABCN)

        validateLog(correct = listOf("c1", "c3"))
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

        phaseInferrer.run(network)

        validatePhases(network, "c1", PhaseCode.AC)
        validatePhases(network, "c2", PhaseCode.AC)
        validatePhases(network, "c3", PhaseCode.ABC)

        validateLog(correct = listOf("c1", "c3"))
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
        Tracing.normalPhaseTrace()
        val network = TestNetworkBuilder()
            .fromSource(PhaseCode.ABC)
            .toAcls(PhaseCode.XYN)
            .toAcls(PhaseCode.XY)
            .toAcls(PhaseCode.BC)
            .build()

        validatePhases(network, "c1", listOf(SPK.B, SPK.C, SPK.NONE))
        validatePhases(network, "c2", PhaseCode.BC)
        validatePhases(network, "c3", PhaseCode.BC)

        phaseInferrer.run(network)

        validatePhases(network, "c1", PhaseCode.BCN)
        validatePhases(network, "c2", PhaseCode.BC)
        validatePhases(network, "c3", PhaseCode.BC)

        validateLog(correct = listOf("c1"))
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
        Tracing.normalPhaseTrace()
        val network = TestNetworkBuilder()
            .fromSource(PhaseCode.ABC)
            .toAcls(PhaseCode.XY)
            .toAcls(PhaseCode.XYN)
            .toAcls(PhaseCode.BC)
            .build()

        validatePhases(network, "c1", PhaseCode.BC)
        validatePhases(network, "c2", listOf(SPK.B, SPK.C, SPK.NONE))
        validatePhases(network, "c3", PhaseCode.BC)

        phaseInferrer.run(network)

        validatePhases(network, "c1", PhaseCode.BC)
        validatePhases(network, "c2", PhaseCode.BCN)
        validatePhases(network, "c3", PhaseCode.BC)

        validateLog(correct = listOf("c2"))
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

        phaseInferrer.run(network)

        validatePhases(network, "c1", PhaseCode.ABC)
        validatePhases(network, "c2", PhaseCode.N)
        validatePhases(network, "c3", PhaseCode.ABCN)

        validateLog(correct = listOf("c2", "c3"))
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

        phaseInferrer.run(network)

        validatePhases(network, "c1", PhaseCode.ABC)
        validatePhases(network, "c2", PhaseCode.B)
        validatePhases(network, "c3", PhaseCode.BCN)

        validateLog(suspect = listOf("c3"))
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

        phaseInferrer.run(network)

        validatePhases(network, "c1", PhaseCode.ABC)
        validatePhases(network, "c2", PhaseCode.C)
        validatePhases(network, "c3", listOf(SPK.C, SPK.NONE, SPK.N))

        validateLog(suspect = listOf("c3"))
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

        phaseInferrer.run(network)

        validatePhases(network, "c1", PhaseCode.ABC)
        validatePhases(network, "c2", PhaseCode.A)
        validatePhases(network, "c3", PhaseCode.AN)

        validateLog(correct = listOf("c3"))
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
    fun testDualFeedANtoABCN() {
        val network = TestNetworkBuilder()
            .fromSource(PhaseCode.AN)
            .toAcls(PhaseCode.ABCN)
            .toSource(PhaseCode.AN)
            .build()

        validatePhases(network, "s0", PhaseCode.AN)
        validatePhases(network, "c1", listOf(SPK.A, SPK.NONE, SPK.NONE, SPK.N))
        validatePhases(network, "s2", PhaseCode.AN)

        phaseInferrer.run(network)

        validatePhases(network, "s0", PhaseCode.AN)
        validatePhases(network, "c1", PhaseCode.ABCN)
        validatePhases(network, "s2", PhaseCode.AN)

        validateLog(correct = listOf("c1"))
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

        phaseInferrer.run(network)

        validatePhases(network, "c1", PhaseCode.ABCN)
        validatePhases(network, "c2", PhaseCode.N)
        validatePhases(network, "c3", PhaseCode.AB)
        validatePhases(network, "c4", PhaseCode.AB)

        validateLog(correct = listOf("c3"))
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

        phaseInferrer.run(network)

        validatePhases(network, "c1", PhaseCode.ABC)
        validatePhases(network, "b2", PhaseCode.ABC, PhaseCode.NONE)
        validatePhases(network, "c3", PhaseCode.NONE)

        validateLog()
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
        phaseInferrer.run(spy(network).also { ns ->
            doReturn(listOf(network["c6-t2"]!!, *network.listOf<Terminal> { it.mRID != "c6-t2" }.toTypedArray()))
                .`when`(ns)
                .listOf(Terminal::class.java)
        })

        validatePhases(network, "c2", PhaseCode.AC, PhaseCode.AC)
        validatePhases(network, "c3", PhaseCode.ABC, PhaseCode.ABC)
        validatePhases(network, "c4", PhaseCode.ABC, PhaseCode.ABC)
        validatePhases(network, "c5", PhaseCode.ABC, PhaseCode.ABC)
        validatePhases(network, "c6", PhaseCode.ABC, PhaseCode.ABC)
        validatePhases(network, "c7", PhaseCode.AC, PhaseCode.AC)
        validatePhases(network, "c8", PhaseCode.ABC, PhaseCode.ABC)
        validatePhases(network, "c9", PhaseCode.ABC, PhaseCode.ABC)
    }

    private fun validateLog(correct: List<String> = emptyList(), suspect: List<String> = emptyList()) {
        assertThat(
            listOf(*systemErr.logLines),
            containsInAnyOrder(
                *(correct.map { correctMessage(it) } +
                    suspect.map { suspectMessage(it) })
                    .map { containsString(it) }
                    .toTypedArray()
            )
        )
    }

    private fun correctMessage(id: String) =
        "*** Action Required *** Inferred missing phase for '' [$id] which should be correct. The phase was inferred due to a disconnected nominal phase because of an upstream error in the source data. Phasing information for the upstream equipment should be fixed in the source system."

    private fun suspectMessage(id: String) =
        "*** Action Required *** Inferred missing phases for '' [$id] which may not be correct. The phases were inferred due to a disconnected nominal phase because of an upstream error in the source data. Phasing information for the upstream equipment should be fixed in the source system."

}

/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network.tracing.phases

import com.zepben.evolve.cim.iec61970.base.core.PhaseCode
import com.zepben.evolve.services.network.testdata.PhasesTestNetwork
import com.zepben.evolve.services.network.tracing.Tracing
import com.zepben.evolve.services.network.tracing.phases.FeederDirection.*
import com.zepben.evolve.services.network.tracing.phases.PhaseValidator.validatePhaseDirections
import com.zepben.evolve.services.network.tracing.phases.PhaseValidator.validatePhases
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind as SPK

@Suppress("SameParameterValue")
class PhaseInferrerTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    private val phaseInferrer = Tracing.phaseInferrer()

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
        val network = PhasesTestNetwork
            .from(PhaseCode.AB)
            .to(PhaseCode.BC)
            .to(PhaseCode.XY)
            .to(PhaseCode.ABC)
            .build()

        validatePhases(network, "c1", SPK.B, SPK.NONE)
        validatePhases(network, "c2", SPK.B, SPK.NONE)
        validatePhases(network, "c3", SPK.NONE, SPK.B, SPK.NONE)

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
        val network = PhasesTestNetwork
            .from(PhaseCode.ABN)
            .to(PhaseCode.BCN)
            .to(PhaseCode.XYN)
            .to(PhaseCode.ABCN)
            .build()

        validatePhases(network, "c1", SPK.B, SPK.NONE, SPK.N)
        validatePhases(network, "c2", SPK.B, SPK.NONE, SPK.N)
        validatePhases(network, "c3", SPK.NONE, SPK.B, SPK.NONE, SPK.N)

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
        val network = PhasesTestNetwork
            .from(PhaseCode.BC)
            .to(PhaseCode.AC)
            .to(PhaseCode.XY)
            .to(PhaseCode.ABC)
            .build()

        validatePhases(network, "c1", SPK.NONE, SPK.C)
        validatePhases(network, "c2", SPK.NONE, SPK.C)
        validatePhases(network, "c3", SPK.NONE, SPK.NONE, SPK.C)

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
        val network = PhasesTestNetwork
            .from(PhaseCode.ABC)
            .to(PhaseCode.XYN)
            .to(PhaseCode.XY)
            .to(PhaseCode.BC)
            .build()

        validatePhases(network, "c1", SPK.B, SPK.C, SPK.NONE)
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
        val network = PhasesTestNetwork
            .from(PhaseCode.ABC)
            .to(PhaseCode.XY)
            .to(PhaseCode.XYN)
            .to(PhaseCode.BC)
            .build()

        validatePhases(network, "c1", PhaseCode.BC)
        validatePhases(network, "c2", SPK.B, SPK.C, SPK.NONE)
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
        val network = PhasesTestNetwork
            .from(PhaseCode.ABC)
            .to(PhaseCode.ABC)
            .to(PhaseCode.N)
            .to(PhaseCode.ABCN)
            .build()

        validatePhases(network, "c1", PhaseCode.ABC)
        validatePhases(network, "c2", SPK.NONE)
        validatePhases(network, "c3", SPK.NONE, SPK.NONE, SPK.NONE, SPK.NONE)

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
    // ABC -> ABC -> B -> BAN
    // (warning with may not be correct)
    //
    @Test
    internal fun testABCtoBtoXYN() {
        val network = PhasesTestNetwork
            .from(PhaseCode.ABC)
            .to(PhaseCode.ABC)
            .to(PhaseCode.B)
            .to(PhaseCode.XYN)
            .build()

        validatePhases(network, "c1", PhaseCode.ABC)
        validatePhases(network, "c2", PhaseCode.B)
        validatePhases(network, "c3", SPK.B, SPK.NONE, SPK.NONE)

        phaseInferrer.run(network)

        validatePhases(network, "c1", PhaseCode.ABC)
        validatePhases(network, "c2", PhaseCode.B)
        validatePhases(network, "c3", SPK.B, SPK.A, SPK.N)

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
        val network = PhasesTestNetwork
            .from(PhaseCode.ABC)
            .to(PhaseCode.ABC)
            .to(PhaseCode.A)
            .to(PhaseCode.XN)
            .build()

        validatePhases(network, "c1", PhaseCode.ABC)
        validatePhases(network, "c2", PhaseCode.A)
        validatePhases(network, "c3", SPK.A, SPK.NONE)

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
        val network = PhasesTestNetwork
            .from(PhaseCode.AN)
            .to(PhaseCode.ABCN)
            .toSource(PhaseCode.AN)
            .build()

        validatePhases(network, "source0", PhaseCode.AN)
        validatePhases(network, "c1", SPK.A, SPK.NONE, SPK.NONE, SPK.N)
        validatePhases(network, "source2", PhaseCode.AN)

        validatePhaseDirections(network, "source0", listOf(BOTH, BOTH))
        validatePhaseDirections(network, "c1", listOf(BOTH, NONE, NONE, BOTH), listOf(BOTH, NONE, NONE, BOTH))
        validatePhaseDirections(network, "source2", listOf(BOTH, BOTH), listOf(DOWNSTREAM, DOWNSTREAM))

        phaseInferrer.run(network)

        validatePhases(network, "source0", PhaseCode.AN)
        validatePhases(network, "c1", PhaseCode.ABCN)
        validatePhases(network, "source2", PhaseCode.AN)

        validatePhaseDirections(network, "source0", listOf(BOTH, BOTH))
        validatePhaseDirections(network, "c1", listOf(BOTH, BOTH, BOTH, BOTH), listOf(BOTH, BOTH, BOTH, BOTH))
        validatePhaseDirections(network, "source2", listOf(BOTH, BOTH), listOf(DOWNSTREAM, DOWNSTREAM))

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
        val network = PhasesTestNetwork
            .from(PhaseCode.ABCN)
            .to(PhaseCode.ABCN)
            .to(PhaseCode.N)
            .to(PhaseCode.AB)
            .to(PhaseCode.XY)
            .build()

        validatePhases(network, "c1", PhaseCode.ABCN)
        validatePhases(network, "c2", PhaseCode.N)
        validatePhases(network, "c3", SPK.NONE, SPK.NONE)
        validatePhases(network, "c4", SPK.NONE, SPK.NONE)

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
        val network = PhasesTestNetwork
            .from(PhaseCode.ABC)
            .to(PhaseCode.ABC)
            .toSwitch(PhaseCode.ABC, true)
            .to(PhaseCode.ABC)
            .build()

        validatePhases(network, "c1", PhaseCode.ABC)
        validatePhases(network, "s2", PhaseCode.ABC, PhaseCode.NONE)
        validatePhases(network, "c3", SPK.NONE, SPK.NONE, SPK.NONE)

        phaseInferrer.run(network)

        validatePhases(network, "c1", PhaseCode.ABC)
        validatePhases(network, "s2", PhaseCode.ABC, PhaseCode.NONE)
        validatePhases(network, "c3", SPK.NONE, SPK.NONE, SPK.NONE)

        validateLog()
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
        "*** Action Required *** Inferred missing phase for '$id name' [$id] which should be correct. The phase was inferred due to a disconnected nominal phase because of an upstream error in the source data. Phasing information for the upstream equipment should be fixed in the source system."

    private fun suspectMessage(id: String) =
        "*** Action Required *** Inferred missing phases for '$id name' [$id] which may not be correct. The phases were inferred due to a disconnected nominal phase because of an upstream error in the source data. Phasing information for the upstream equipment should be fixed in the source system."

}

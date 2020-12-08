/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network.tracing.phases

import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind
import com.zepben.evolve.services.network.testdata.TestNetworks
import com.zepben.evolve.services.network.tracing.Tracing
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class PhaseInferrerTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    //
    // nominal
    // ABC -> N -> ABCN
    // traced
    // ABC -> NONE -> NONE NONE NONE NONE
    //
    // infer nominal
    // ABC -> N -> ABCN
    // (warning with should be correct)
    //
    @Test
    internal fun testABCtoNtoABCN() {
        val phaseInferrer = Tracing.phaseInferrer()
        systemErr.clearCapturedLog()

        val network = PhasesTestNetwork
            .from(PhaseCode.ABC)
            .to(PhaseCode.ABC)
            .to(PhaseCode.N)
            .to(PhaseCode.ABCN)
            .build()

        validatePhases(network, "c0", SinglePhaseKind.A, SinglePhaseKind.B, SinglePhaseKind.C)
        validatePhases(network, "c1", SinglePhaseKind.NONE)
        validatePhases(network, "c2", SinglePhaseKind.NONE, SinglePhaseKind.NONE, SinglePhaseKind.NONE, SinglePhaseKind.NONE)

        phaseInferrer.run(network)

        validatePhases(network, "c0", SinglePhaseKind.A, SinglePhaseKind.B, SinglePhaseKind.C)
        validatePhases(network, "c1", SinglePhaseKind.N)
        validatePhases(network, "c2", SinglePhaseKind.A, SinglePhaseKind.B, SinglePhaseKind.C, SinglePhaseKind.N)

        assertThat(
            listOf(*systemErr.logLines),
            Matchers.containsInAnyOrder(
                Matchers.containsString("*** Action Required *** Inferred missing phase for 'c1 name' [c1] which should be correct. The phase was inferred due to a disconnected nominal phase because of an upstream error in the source data. Phasing information for the upstream equipment should be fixed in the source system."),
                Matchers.containsString("*** Action Required *** Inferred missing phase for 'c2 name' [c2] which should be correct. The phase was inferred due to a disconnected nominal phase because of an upstream error in the source data. Phasing information for the upstream equipment should be fixed in the source system.")
            )
        )
    }

    //
    // nominal
    // ABC -> B -> XYN
    // traced
    // ABC -> B -> B NONE NONE
    //
    // infer nominal
    // ABC -> B -> B NONE N
    // infer xy
    // ABC -> B -> B A N
    // (warning with may not be correct)
    //
    @Test
    internal fun testABCtoBtoXYN() {
        val phaseInferrer = Tracing.phaseInferrer()
        systemErr.clearCapturedLog()

        val network = PhasesTestNetwork
            .from(PhaseCode.ABC)
            .to(PhaseCode.ABC)
            .to(PhaseCode.B)
            .to(PhaseCode.XYN)
            .build()

        validatePhases(network, "c0", SinglePhaseKind.A, SinglePhaseKind.B, SinglePhaseKind.C)
        validatePhases(network, "c1", SinglePhaseKind.B)
        validatePhases(network, "c2", SinglePhaseKind.B, SinglePhaseKind.NONE, SinglePhaseKind.NONE)

        phaseInferrer.run(network)

        validatePhases(network, "c0", SinglePhaseKind.A, SinglePhaseKind.B, SinglePhaseKind.C)
        validatePhases(network, "c1", SinglePhaseKind.B)
        validatePhases(network, "c2", SinglePhaseKind.B, SinglePhaseKind.A, SinglePhaseKind.N)

        assertThat(
            listOf(*systemErr.logLines),
            Matchers.containsInAnyOrder(
                Matchers.containsString("*** Action Required *** Inferred missing phases for 'c2 name' [c2] which may not be correct. The phases were inferred due to a disconnected nominal phase because of an upstream error in the source data. Phasing information for the upstream equipment should be fixed in the source system.")
            )
        )
    }

    //
    // nominal
    // ABC -> A -> XN
    // traced
    // ABC -> A -> A NONE
    //
    // infer nominal
    // ABC -> A -> AN
    // (warning with should be correct)
    //
    @Test
    internal fun testABCtoAtoXN() {
        val phaseInferrer = Tracing.phaseInferrer()
        systemErr.clearCapturedLog()

        val network = PhasesTestNetwork
            .from(PhaseCode.ABC)
            .to(PhaseCode.ABC)
            .to(PhaseCode.A)
            .to(PhaseCode.XN)
            .build()

        validatePhases(network, "c0", SinglePhaseKind.A, SinglePhaseKind.B, SinglePhaseKind.C)
        validatePhases(network, "c1", SinglePhaseKind.A)
        validatePhases(network, "c2", SinglePhaseKind.A, SinglePhaseKind.NONE)

        phaseInferrer.run(network)

        validatePhases(network, "c0", SinglePhaseKind.A, SinglePhaseKind.B, SinglePhaseKind.C)
        validatePhases(network, "c1", SinglePhaseKind.A)
        validatePhases(network, "c2", SinglePhaseKind.A, SinglePhaseKind.N)

        assertThat(
            listOf(*systemErr.logLines),
            Matchers.containsInAnyOrder(
                Matchers.containsString("*** Action Required *** Inferred missing phase for 'c2 name' [c2] which should be correct. The phase was inferred due to a disconnected nominal phase because of an upstream error in the source data. Phasing information for the upstream equipment should be fixed in the source system.")
            )
        )
    }


    //
    // nominal
    // AN <-> ABCN <-> AN
    // traced
    // AN <-> A NONE NONE N <-> AN
    //
    // infer nominal
    // AN <-> ABCN <-> AN
    // (warning with should be correct)
    //
    @Test
    internal fun testDualFeedANtoABCN() {
        val phaseInferrer = Tracing.phaseInferrer()
        systemErr.clearCapturedLog()

        val network = PhasesTestNetwork
            .from(PhaseCode.AN)
            .to(PhaseCode.ABCN)
            .toSource(PhaseCode.AN)
            .build()

        validatePhases(network, "source", SinglePhaseKind.A, SinglePhaseKind.N)
        validatePhases(network, "c0", SinglePhaseKind.A, SinglePhaseKind.NONE, SinglePhaseKind.NONE, SinglePhaseKind.N)
        validatePhases(network, "source1", SinglePhaseKind.A, SinglePhaseKind.N)

        phaseInferrer.run(network)

        validatePhases(network, "source", SinglePhaseKind.A, SinglePhaseKind.N)
        validatePhases(network, "c0", SinglePhaseKind.A, SinglePhaseKind.B, SinglePhaseKind.C, SinglePhaseKind.N)
        validatePhases(network, "source1", SinglePhaseKind.A, SinglePhaseKind.N)

        assertThat(
            listOf(*systemErr.logLines),
            Matchers.containsInAnyOrder(
                Matchers.containsString("*** Action Required *** Inferred missing phase for 'c0 name' [c0] which should be correct. The phase was inferred due to a disconnected nominal phase because of an upstream error in the source data. Phasing information for the upstream equipment should be fixed in the source system.")
            )
        )
    }

    //
    // nominal
    // ABCN -> N -> AB -> XY
    // traced
    // ABCN -> N -> NONE NONE -> NONE NONE
    //
    // infer nominal
    // ABC -> N -> AB -> AB
    // (warning with should be correct)
    //
    @Test
    internal fun testABCtoNtoABtoXY() {
        val phaseInferrer = Tracing.phaseInferrer()
        systemErr.clearCapturedLog()

        val network = PhasesTestNetwork
            .from(PhaseCode.ABCN)
            .to(PhaseCode.ABCN)
            .to(PhaseCode.N)
            .to(PhaseCode.AB)
            .to(PhaseCode.XY)
            .build()

        validatePhases(network, "c0", SinglePhaseKind.A, SinglePhaseKind.B, SinglePhaseKind.C, SinglePhaseKind.N)
        validatePhases(network, "c1", SinglePhaseKind.N)
        validatePhases(network, "c2", SinglePhaseKind.NONE, SinglePhaseKind.NONE)
        validatePhases(network, "c3", SinglePhaseKind.NONE, SinglePhaseKind.NONE)

        phaseInferrer.run(network)

        validatePhases(network, "c0", SinglePhaseKind.A, SinglePhaseKind.B, SinglePhaseKind.C)
        validatePhases(network, "c1", SinglePhaseKind.N)
        validatePhases(network, "c2", SinglePhaseKind.A, SinglePhaseKind.B)
        validatePhases(network, "c3", SinglePhaseKind.A, SinglePhaseKind.B)

        assertThat(
            listOf(*systemErr.logLines),
            Matchers.containsInAnyOrder(
                Matchers.containsString("*** Action Required *** Inferred missing phase for 'c2 name' [c2] which should be correct. The phase was inferred due to a disconnected nominal phase because of an upstream error in the source data. Phasing information for the upstream equipment should be fixed in the source system."),
            )
        )
    }

    //
    // nominal
    // ABC -> ABC OPEN SWICH -> ABC
    // traced
    // ABC -> ABC/NONE NONE NONE -> NONE NONE NONE
    //
    // infer nominal
    // ABC -> ABC/NONE NONE NONE -> NONE NONE NONE
    //
    @Test
    internal fun testABCtoOpenSwitchABC() {
        val phaseInferrer = Tracing.phaseInferrer()
        systemErr.clearCapturedLog()

        val network = PhasesTestNetwork
            .from(PhaseCode.ABC)
            .to(PhaseCode.ABC)
            .toSwitch(PhaseCode.ABC, true)
            .to(PhaseCode.ABC)
            .build()

        validatePhases(network, "c0", SinglePhaseKind.A, SinglePhaseKind.B, SinglePhaseKind.C)
        validatePhasesInTerminals(
            network,
            "s1",
            listOf(SinglePhaseKind.A, SinglePhaseKind.B, SinglePhaseKind.C),
            listOf(SinglePhaseKind.NONE, SinglePhaseKind.NONE, SinglePhaseKind.NONE),
        )
        validatePhases(network, "c2", SinglePhaseKind.NONE, SinglePhaseKind.NONE, SinglePhaseKind.NONE)

        phaseInferrer.run(network)

        validatePhases(network, "c0", SinglePhaseKind.A, SinglePhaseKind.B, SinglePhaseKind.C)
        validatePhasesInTerminals(
            network,
            "s1",
            listOf(SinglePhaseKind.A, SinglePhaseKind.B, SinglePhaseKind.C),
            listOf(SinglePhaseKind.NONE, SinglePhaseKind.NONE, SinglePhaseKind.NONE),
        )
        validatePhases(network, "c2", SinglePhaseKind.NONE, SinglePhaseKind.NONE, SinglePhaseKind.NONE)

        assertThat(listOf(*systemErr.logLines), hasSize(0))
    }


    private fun validatePhases(network: NetworkService, id: String, vararg expectedPhases: SinglePhaseKind) {
        val asset = network.get<ConductingEquipment>(id)!!
        for (index in expectedPhases.indices) {
            asset.terminals.forEach { terminal ->
                val nominalPhase = terminal.phases.singlePhases()[index]
                assertThat(terminal.normalPhases(nominalPhase).phase(), equalTo(expectedPhases[index]))
            }
        }
    }

    private fun validatePhasesInTerminals(
        network: NetworkService,
        @Suppress("SameParameterValue") id: String,
        expectedPhasesT1: List<SinglePhaseKind>,
        expectedPhasesT2: List<SinglePhaseKind>
    ) {
        val asset = network.get<ConductingEquipment>(id)!!
        val t1 = asset.terminals[0]
        val t2 = asset.terminals[1]

        for (index in expectedPhasesT1.indices) {
            val nominalPhase = t1.phases.singlePhases()[index]
            assertThat(t1.normalPhases(nominalPhase).phase(), equalTo(expectedPhasesT1[index]))
        }

        for (index in expectedPhasesT2.indices) {
            val nominalPhase = t2.phases.singlePhases()[index]
            assertThat(t2.normalPhases(nominalPhase).phase(), equalTo(expectedPhasesT2[index]))
        }
    }

}

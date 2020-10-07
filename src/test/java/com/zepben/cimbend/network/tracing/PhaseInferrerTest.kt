/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.network.tracing

import com.zepben.cimbend.cim.iec61970.base.core.ConductingEquipment
import com.zepben.cimbend.cim.iec61970.base.wires.SinglePhaseKind
import com.zepben.cimbend.testdata.TestNetworks
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class PhaseInferrerTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    private val network = TestNetworks.getNetwork(9)

    @Test
    internal fun infersMissingPhases() {
        val phaseInferrer = Tracing.phaseInferrer()
        systemErr.clearCapturedLog()
        validatePhases("c0", SinglePhaseKind.A, SinglePhaseKind.B, SinglePhaseKind.C)
        validatePhases("c1", SinglePhaseKind.A, SinglePhaseKind.B)
        validatePhases("c2", SinglePhaseKind.A, SinglePhaseKind.B, SinglePhaseKind.NONE)
        validatePhases("c3", SinglePhaseKind.A, SinglePhaseKind.B, SinglePhaseKind.NONE)
        validatePhases("c4", SinglePhaseKind.A, SinglePhaseKind.NONE)
        validatePhases("c5", SinglePhaseKind.A)
        validatePhases("c6", SinglePhaseKind.A, SinglePhaseKind.NONE)
        validatePhases("c7", SinglePhaseKind.A)
        validatePhases("c8", SinglePhaseKind.A, SinglePhaseKind.NONE, SinglePhaseKind.NONE)
        phaseInferrer.run(network)
        validatePhases("c0", SinglePhaseKind.A, SinglePhaseKind.B, SinglePhaseKind.C)
        validatePhases("c1", SinglePhaseKind.A, SinglePhaseKind.B)
        validatePhases("c2", SinglePhaseKind.A, SinglePhaseKind.B, SinglePhaseKind.C)
        validatePhases("c3", SinglePhaseKind.A, SinglePhaseKind.B, SinglePhaseKind.C)
        validatePhases("c4", SinglePhaseKind.A, SinglePhaseKind.C)
        validatePhases("c5", SinglePhaseKind.A)
        validatePhases("c6", SinglePhaseKind.A, SinglePhaseKind.B)
        validatePhases("c7", SinglePhaseKind.A)
        validatePhases("c8", SinglePhaseKind.A, SinglePhaseKind.B, SinglePhaseKind.C)
        assertThat(
            listOf(*systemErr.logLines),
            Matchers.containsInAnyOrder(
                Matchers.containsString("*** Action Required *** Inferred missing phase for 'c2 name' [c2] which should be correct. The phase was inferred due to a disconnected nominal phase because of an upstream error in the source data. Phasing information for the upstream equipment should be fixed in the source system."),
                Matchers.containsString("*** Action Required *** Inferred missing phases for 'c6 name' [c6] which may not be correct. The phases were inferred due to a disconnected nominal phase because of an upstream error in the source data. Phasing information for the upstream equipment should be fixed in the source system."),
                Matchers.containsString("*** Action Required *** Inferred missing phases for 'c8 name' [c8] which may not be correct. The phases were inferred due to a disconnected nominal phase because of an upstream error in the source data. Phasing information for the upstream equipment should be fixed in the source system.")
            )
        )
    }

    private fun validatePhases(id: String, vararg singlePhaseKinds: SinglePhaseKind) {
        val asset = network.get<ConductingEquipment>(id)!!
        for (index in singlePhaseKinds.indices) {
            asset.terminals.forEach { terminal ->
                assertThat(terminal.normalPhases(terminal.phases.singlePhases()[index]).phase(), equalTo(singlePhaseKinds[index]))
            }
        }
    }
}

/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.cim.iec61970.base.core

import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind
import com.zepben.evolve.cim.validateEnum
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import com.zepben.protobuf.cim.iec61970.base.core.PhaseCode as PBPhaseCode

internal class PhaseCodeTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun validateVsPb() {
        validateEnum(PhaseCode.values(), PBPhaseCode.values())
    }

    @Test
    internal fun singlePhases() {
        PhaseCode.values()
            .asSequence()
            .filter { it !== PhaseCode.NONE }
            .forEach { phaseCode ->
                // We need to strip the 's' off secondary phases for the following checks to work correctly.
                assertThat(phaseCode.singlePhases().size, equalTo(phaseCode.name.trimStart('s').length))

                val singlePhases = phaseCode.singlePhases()
                    .asSequence()
                    .map { it.name.trimStart('s') }
                    .toSet()

                val namePhases = phaseCode.name.trimStart('s').toCharArray().map { it.toString() }.toSet()

                assertThat(singlePhases.containsAll(namePhases), equalTo(true))
            }
    }

    @Test
    internal fun withoutNeutral() {
        assertThat(PhaseCode.ABCN.withoutNeutral(), equalTo(PhaseCode.ABC))
        assertThat(PhaseCode.ABC.withoutNeutral(), equalTo(PhaseCode.ABC))
        assertThat(PhaseCode.BCN.withoutNeutral(), equalTo(PhaseCode.BC))
        assertThat(PhaseCode.XYN.withoutNeutral(), equalTo(PhaseCode.XY))
        assertThat(PhaseCode.NONE.withoutNeutral(), equalTo(PhaseCode.NONE))
    }

    @Test
    internal fun fromSinglePhases() {
        PhaseCode.values()
            .asSequence()
            .forEach {
                assertThat(PhaseCode.fromSinglePhases(it.singlePhases()), equalTo(it))
            }

        assertThat(PhaseCode.fromSinglePhases(listOf(SinglePhaseKind.A, SinglePhaseKind.B)), equalTo(PhaseCode.AB))
        assertThat(PhaseCode.fromSinglePhases(setOf(SinglePhaseKind.B, SinglePhaseKind.C)), equalTo(PhaseCode.BC))
    }

}

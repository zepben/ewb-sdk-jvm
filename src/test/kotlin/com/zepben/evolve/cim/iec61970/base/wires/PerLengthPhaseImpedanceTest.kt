/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.wires

import com.zepben.evolve.utils.PrivateCollectionValidator
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class PerLengthPhaseImpedanceTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(PerLengthPhaseImpedance().mRID, not(equalTo("")))
        assertThat(PerLengthPhaseImpedance("id").mRID, equalTo("id"))
    }

    @Test
    internal fun phaseImpedanceData() {
        PrivateCollectionValidator.validateUnordered(
            ::PerLengthPhaseImpedance,
            { it: Int -> PhaseImpedanceData(SinglePhaseKind.get(it), SinglePhaseKind.get(it), it.toDouble(), it.toDouble(), it.toDouble(), it.toDouble()) },
            PerLengthPhaseImpedance::phaseImpedanceData,
            PerLengthPhaseImpedance::numPhaseImpedanceData,
            { i: PerLengthPhaseImpedance, t: SinglePhaseKind -> i.getData(t, t) },
            PerLengthPhaseImpedance::addPhaseImpedanceData,
            PerLengthPhaseImpedance::removePhaseImpedanceData,
            PerLengthPhaseImpedance::clearPhaseImpedanceData,
            { it.fromPhase }
        )
    }

    @Test
    internal fun `diagonals returns only diagonals`() {
        val pi1 = PhaseImpedanceData(SinglePhaseKind.A, SinglePhaseKind.B)
        val pi2 = PhaseImpedanceData(SinglePhaseKind.A, SinglePhaseKind.C)
        val pi3 = PhaseImpedanceData(SinglePhaseKind.B, SinglePhaseKind.C)

        val pid1 = PhaseImpedanceData(SinglePhaseKind.A, SinglePhaseKind.A)
        val pid2 = PhaseImpedanceData(SinglePhaseKind.B, SinglePhaseKind.B)
        val pid3 = PhaseImpedanceData(SinglePhaseKind.C, SinglePhaseKind.C)

        PerLengthPhaseImpedance().apply {
            addPhaseImpedanceData(pi1)
            addPhaseImpedanceData(pi2)
            addPhaseImpedanceData(pi3)
            addPhaseImpedanceData(pid1)
            addPhaseImpedanceData(pid2)
            addPhaseImpedanceData(pid3)

            assertThat(diagonal(), containsInAnyOrder(pid1, pid2, pid3))
            assertThat(diagonal(), not(containsInAnyOrder(pi1, pi2, pi3)))
        }
    }

}

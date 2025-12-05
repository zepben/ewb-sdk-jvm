/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.wires

import com.zepben.ewb.services.common.testdata.generateId
import com.zepben.ewb.utils.PrivateCollectionValidator
import com.zepben.ewb.utils.PrivateCollectionValidator.DuplicateBehaviour
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class PerLengthPhaseImpedanceTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(PerLengthPhaseImpedance("id").mRID, equalTo("id"))
    }

    @Test
    internal fun phaseImpedanceData() {
        // We need to change the formatting of the key's string to match what we log.
        data class Key(val fromPhase: SinglePhaseKind, val toPhase: SinglePhaseKind) {
            override fun toString(): String = "fromPhase $fromPhase and toPhase $toPhase"
        }

        PrivateCollectionValidator.validateUnordered(
            ::PerLengthPhaseImpedance,
            { PhaseImpedanceData(SinglePhaseKind[it], SinglePhaseKind[it + 1], it.toDouble()) },
            PerLengthPhaseImpedance::data,
            PerLengthPhaseImpedance::numData,
            { it, (from, to) -> it.getData(from, to) },
            PerLengthPhaseImpedance::addData,
            PerLengthPhaseImpedance::removeData,
            PerLengthPhaseImpedance::clearData,
            { Key(it.fromPhase, it.toPhase) },
            DuplicateBehaviour.THROWS,
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

        PerLengthPhaseImpedance(generateId()).apply {
            addData(pi1)
            addData(pi2)
            addData(pi3)
            addData(pid1)
            addData(pid2)
            addData(pid3)

            assertThat(diagonal(), containsInAnyOrder(pid1, pid2, pid3))
            assertThat(diagonal(), not(containsInAnyOrder(pi1, pi2, pi3)))
        }
    }

}

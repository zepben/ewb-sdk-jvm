/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.boilerplate.relations

import com.zepben.ewb.cim.iec61970.base.wires.PhaseImpedanceData
import com.zepben.ewb.cim.iec61970.base.wires.SinglePhaseKind
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class PhaseImpedanceDataListTest {

    companion object {
        @JvmField
        @RegisterExtension
        val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()
    }

    @Test
    internal fun `get returns the matching phase pair or null`() {
        val differentFrom = PhaseImpedanceData(SinglePhaseKind.B, SinglePhaseKind.C)
        val differentTo = PhaseImpedanceData(SinglePhaseKind.A, SinglePhaseKind.C)
        val match = PhaseImpedanceData(SinglePhaseKind.A, SinglePhaseKind.B)
        var backing: MutableList<PhaseImpedanceData>? = mutableListOf(differentFrom, differentTo, match)
        val data = PhaseImpedanceDataList({ backing }, { backing = it })

        assertThat(data.get(SinglePhaseKind.A, SinglePhaseKind.B), sameInstance(match))
        assertThat(data.get(SinglePhaseKind.C, SinglePhaseKind.A), nullValue())
    }

    @Test
    internal fun `diagonal returns only entries with matching phases`() {
        val phaseA = PhaseImpedanceData(SinglePhaseKind.A, SinglePhaseKind.A)
        val phaseB = PhaseImpedanceData(SinglePhaseKind.B, SinglePhaseKind.B)
        val offDiagonal = PhaseImpedanceData(SinglePhaseKind.A, SinglePhaseKind.B)
        var backing: MutableList<PhaseImpedanceData>? = mutableListOf(phaseA, offDiagonal, phaseB)
        val data = PhaseImpedanceDataList({ backing }, { backing = it })

        assertThat(data.diagonal(), containsInAnyOrder(phaseA, phaseB))
    }

}

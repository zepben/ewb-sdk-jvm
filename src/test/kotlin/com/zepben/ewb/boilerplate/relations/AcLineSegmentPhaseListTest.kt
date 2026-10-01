/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.boilerplate.relations

import com.zepben.ewb.cim.iec61970.base.wires.AcLineSegment
import com.zepben.ewb.cim.iec61970.base.wires.AcLineSegmentPhase
import com.zepben.ewb.cim.iec61970.base.wires.SinglePhaseKind
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.nullValue
import org.hamcrest.Matchers.sameInstance
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class AcLineSegmentPhaseListTest {

    companion object {
        @JvmField
        @RegisterExtension
        val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()
    }

    @Test
    internal fun `getByPhase returns the matching phase or null`() {
        val phaseA = AcLineSegmentPhase("a").apply { phase = SinglePhaseKind.A }
        val phaseB = AcLineSegmentPhase("b").apply { phase = SinglePhaseKind.B }
        var backing: MutableList<AcLineSegmentPhase>? = mutableListOf(phaseA, phaseB)
        val phases = AcLineSegmentPhaseList(
            { backing },
            { backing = it },
            AcLineSegment("line"),
            "An AcLineSegmentPhase",
        )

        assertThat(phases.get(SinglePhaseKind.B), sameInstance(phaseB))
        assertThat(phases.get(SinglePhaseKind.C), nullValue())
    }

}

/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.wires

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test

class PhaseImpedanceDataTest {
    @Test
    internal fun constructorCoverage() {
        assertThat(com.zepben.ewb.cim.iec61970.base.wires.PhaseImpedanceData(SinglePhaseKind.A, SinglePhaseKind.B, 0.0, 0.0, 0.0, 0.0), notNullValue())
    }

    @Test
    internal fun accessorCoverage() {
        var data = com.zepben.ewb.cim.iec61970.base.wires.PhaseImpedanceData(SinglePhaseKind.A, SinglePhaseKind.B)

        assertThat(data.fromPhase, equalTo(SinglePhaseKind.A))
        assertThat(data.toPhase, equalTo(SinglePhaseKind.B))
        assertThat(data.b, nullValue())
        assertThat(data.g, nullValue())
        assertThat(data.r, nullValue())
        assertThat(data.x, nullValue())

        data = com.zepben.ewb.cim.iec61970.base.wires.PhaseImpedanceData(SinglePhaseKind.NONE, SinglePhaseKind.NONE, 0.1, 0.2, 0.3, 0.4)

        assertThat(data.toPhase, equalTo(SinglePhaseKind.NONE))
        assertThat(data.fromPhase, equalTo(SinglePhaseKind.NONE))
        assertThat(data.b, equalTo(0.1))
        assertThat(data.g, equalTo(0.2))
        assertThat(data.r, equalTo(0.3))
        assertThat(data.x, equalTo(0.4))
    }

}

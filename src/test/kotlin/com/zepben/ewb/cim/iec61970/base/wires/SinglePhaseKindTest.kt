/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.wires

import com.zepben.ewb.cim.iec61970.base.core.PhaseCode
import com.zepben.ewb.cim.validateEnum
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import com.zepben.protobuf.cim.iec61970.base.wires.SinglePhaseKind as PBSinglePhaseKind

internal class SinglePhaseKindTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun validateVsPb() {
        validateEnum(SinglePhaseKind.entries, PBSinglePhaseKind.entries)
    }

    @Test
    internal fun accessors() {
        // These tests are just to provide coverage and satisfy the IDE visibility checks.
        assertThat(SinglePhaseKind.A.value, equalTo(1))
        assertThat(SinglePhaseKind.A.maskIndex, equalTo(0))
        assertThat(SinglePhaseKind.A.bitMask, equalTo(1))
    }

    @Test
    internal fun plus() {
        assertThat(SinglePhaseKind.A + SinglePhaseKind.B, equalTo(PhaseCode.AB))
        assertThat(SinglePhaseKind.A + SinglePhaseKind.A, equalTo(PhaseCode.A))
        assertThat(SinglePhaseKind.A + PhaseCode.BC, equalTo(PhaseCode.ABC))
    }

    @Test
    internal fun minus() {
        assertThat(SinglePhaseKind.B - SinglePhaseKind.A, equalTo(PhaseCode.B))
        assertThat(SinglePhaseKind.A - SinglePhaseKind.A, equalTo(PhaseCode.NONE))
        assertThat(SinglePhaseKind.A - PhaseCode.BC, equalTo(PhaseCode.A))
        assertThat(SinglePhaseKind.A - PhaseCode.ABC, equalTo(PhaseCode.NONE))
    }

}

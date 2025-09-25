/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.network.tracing.phases

import com.zepben.ewb.cim.iec61970.base.wires.SinglePhaseKind
import com.zepben.ewb.services.network.tracing.connectivity.NominalPhasePath
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class NominalPhasePathTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    private val nominalPhasePath1 = NominalPhasePath(SinglePhaseKind.A, SinglePhaseKind.B)
    private val nominalPhasePath2 = NominalPhasePath(SinglePhaseKind.C, SinglePhaseKind.N)

    @Test
    internal fun accessors() {
        assertThat(nominalPhasePath1.from, equalTo(SinglePhaseKind.A))
        assertThat(nominalPhasePath1.to, equalTo(SinglePhaseKind.B))
        assertThat(nominalPhasePath2.from, equalTo(SinglePhaseKind.C))
        assertThat(nominalPhasePath2.to, equalTo(SinglePhaseKind.N))
    }

    @Test
    internal fun coverage() {
        assertThat(nominalPhasePath1, equalTo(nominalPhasePath1))
        assertThat(nominalPhasePath1, equalTo(NominalPhasePath(SinglePhaseKind.A, SinglePhaseKind.B)))
        assertThat(nominalPhasePath1, not(equalTo(nominalPhasePath2)))
        assertThat(nominalPhasePath1, not(equalTo(null)))
        assertThat(nominalPhasePath1, not(equalTo("test")))
        assertThat(nominalPhasePath1, not(equalTo(NominalPhasePath(SinglePhaseKind.A, SinglePhaseKind.C))))
        assertThat(nominalPhasePath1, not(equalTo(NominalPhasePath(SinglePhaseKind.B, SinglePhaseKind.B))))

        assertThat(nominalPhasePath1.toString(), not(emptyString()))

        assertThat(nominalPhasePath1.hashCode(), not(equalTo(NominalPhasePath(SinglePhaseKind.B, SinglePhaseKind.A))))
    }

}

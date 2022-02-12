/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.phases

import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind as SPK

internal class TracedPhasesBitManipulationTest {

    @JvmField
    @RegisterExtension
    var systemOut: SystemLogExtension = SystemLogExtension.SYSTEM_OUT.captureLog().muteOnSuccess()

    @Test
    internal fun get() {
        assertThat(TracedPhasesBitManipulation.get(0x0001.toUInt(), SPK.A), equalTo(SPK.A))
        assertThat(TracedPhasesBitManipulation.get(0x0002.toUInt(), SPK.A), equalTo(SPK.B))
        assertThat(TracedPhasesBitManipulation.get(0x0004.toUInt(), SPK.A), equalTo(SPK.C))
        assertThat(TracedPhasesBitManipulation.get(0x0008.toUInt(), SPK.A), equalTo(SPK.N))

        assertThat(TracedPhasesBitManipulation.get(0x0010.toUInt(), SPK.B), equalTo(SPK.A))
        assertThat(TracedPhasesBitManipulation.get(0x0020.toUInt(), SPK.B), equalTo(SPK.B))
        assertThat(TracedPhasesBitManipulation.get(0x0040.toUInt(), SPK.B), equalTo(SPK.C))
        assertThat(TracedPhasesBitManipulation.get(0x0080.toUInt(), SPK.B), equalTo(SPK.N))

        assertThat(TracedPhasesBitManipulation.get(0x0100.toUInt(), SPK.C), equalTo(SPK.A))
        assertThat(TracedPhasesBitManipulation.get(0x0200.toUInt(), SPK.C), equalTo(SPK.B))
        assertThat(TracedPhasesBitManipulation.get(0x0400.toUInt(), SPK.C), equalTo(SPK.C))
        assertThat(TracedPhasesBitManipulation.get(0x0800.toUInt(), SPK.C), equalTo(SPK.N))

        assertThat(TracedPhasesBitManipulation.get(0x1000.toUInt(), SPK.N), equalTo(SPK.A))
        assertThat(TracedPhasesBitManipulation.get(0x2000.toUInt(), SPK.N), equalTo(SPK.B))
        assertThat(TracedPhasesBitManipulation.get(0x4000.toUInt(), SPK.N), equalTo(SPK.C))
        assertThat(TracedPhasesBitManipulation.get(0x8000.toUInt(), SPK.N), equalTo(SPK.N))
    }

    @Test
    internal fun set() {
        assertThat(TracedPhasesBitManipulation.set(0x0000.toUInt(), SPK.A, SPK.A), equalTo(0x0001.toUInt()))
        assertThat(TracedPhasesBitManipulation.set(0x0000.toUInt(), SPK.A, SPK.B), equalTo(0x0002.toUInt()))
        assertThat(TracedPhasesBitManipulation.set(0x0000.toUInt(), SPK.A, SPK.C), equalTo(0x0004.toUInt()))
        assertThat(TracedPhasesBitManipulation.set(0x0000.toUInt(), SPK.A, SPK.N), equalTo(0x0008.toUInt()))
        assertThat(TracedPhasesBitManipulation.set(0xffff.toUInt(), SPK.A, SPK.NONE), equalTo(0xfff0.toUInt()))

        assertThat(TracedPhasesBitManipulation.set(0x0000.toUInt(), SPK.B, SPK.A), equalTo(0x0010.toUInt()))
        assertThat(TracedPhasesBitManipulation.set(0x0000.toUInt(), SPK.B, SPK.B), equalTo(0x0020.toUInt()))
        assertThat(TracedPhasesBitManipulation.set(0x0000.toUInt(), SPK.B, SPK.C), equalTo(0x0040.toUInt()))
        assertThat(TracedPhasesBitManipulation.set(0x0000.toUInt(), SPK.B, SPK.N), equalTo(0x0080.toUInt()))
        assertThat(TracedPhasesBitManipulation.set(0xffff.toUInt(), SPK.B, SPK.NONE), equalTo(0xff0f.toUInt()))

        assertThat(TracedPhasesBitManipulation.set(0x0000.toUInt(), SPK.C, SPK.A), equalTo(0x0100.toUInt()))
        assertThat(TracedPhasesBitManipulation.set(0x0000.toUInt(), SPK.C, SPK.B), equalTo(0x0200.toUInt()))
        assertThat(TracedPhasesBitManipulation.set(0x0000.toUInt(), SPK.C, SPK.C), equalTo(0x0400.toUInt()))
        assertThat(TracedPhasesBitManipulation.set(0x0000.toUInt(), SPK.C, SPK.N), equalTo(0x0800.toUInt()))
        assertThat(TracedPhasesBitManipulation.set(0xffff.toUInt(), SPK.C, SPK.NONE), equalTo(0xf0ff.toUInt()))

        assertThat(TracedPhasesBitManipulation.set(0x0000.toUInt(), SPK.N, SPK.A), equalTo(0x1000.toUInt()))
        assertThat(TracedPhasesBitManipulation.set(0x0000.toUInt(), SPK.N, SPK.B), equalTo(0x2000.toUInt()))
        assertThat(TracedPhasesBitManipulation.set(0x0000.toUInt(), SPK.N, SPK.C), equalTo(0x4000.toUInt()))
        assertThat(TracedPhasesBitManipulation.set(0x0000.toUInt(), SPK.N, SPK.N), equalTo(0x8000.toUInt()))
        assertThat(TracedPhasesBitManipulation.set(0xffff.toUInt(), SPK.N, SPK.NONE), equalTo(0x0fff.toUInt()))
    }

}

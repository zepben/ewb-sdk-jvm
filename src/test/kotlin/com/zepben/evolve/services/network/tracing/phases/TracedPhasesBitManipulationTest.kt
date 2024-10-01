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
        assertThat(TracedPhasesBitManipulation.get(0x0001.toUShort(), SPK.A), equalTo(SPK.A))
        assertThat(TracedPhasesBitManipulation.get(0x0002.toUShort(), SPK.A), equalTo(SPK.B))
        assertThat(TracedPhasesBitManipulation.get(0x0004.toUShort(), SPK.A), equalTo(SPK.C))
        assertThat(TracedPhasesBitManipulation.get(0x0008.toUShort(), SPK.A), equalTo(SPK.N))

        assertThat(TracedPhasesBitManipulation.get(0x0010.toUShort(), SPK.B), equalTo(SPK.A))
        assertThat(TracedPhasesBitManipulation.get(0x0020.toUShort(), SPK.B), equalTo(SPK.B))
        assertThat(TracedPhasesBitManipulation.get(0x0040.toUShort(), SPK.B), equalTo(SPK.C))
        assertThat(TracedPhasesBitManipulation.get(0x0080.toUShort(), SPK.B), equalTo(SPK.N))

        assertThat(TracedPhasesBitManipulation.get(0x0100.toUShort(), SPK.C), equalTo(SPK.A))
        assertThat(TracedPhasesBitManipulation.get(0x0200.toUShort(), SPK.C), equalTo(SPK.B))
        assertThat(TracedPhasesBitManipulation.get(0x0400.toUShort(), SPK.C), equalTo(SPK.C))
        assertThat(TracedPhasesBitManipulation.get(0x0800.toUShort(), SPK.C), equalTo(SPK.N))

        assertThat(TracedPhasesBitManipulation.get(0x1000.toUShort(), SPK.N), equalTo(SPK.A))
        assertThat(TracedPhasesBitManipulation.get(0x2000.toUShort(), SPK.N), equalTo(SPK.B))
        assertThat(TracedPhasesBitManipulation.get(0x4000.toUShort(), SPK.N), equalTo(SPK.C))
        assertThat(TracedPhasesBitManipulation.get(0x8000.toUShort(), SPK.N), equalTo(SPK.N))
    }

    @Test
    internal fun set() {
        assertThat(TracedPhasesBitManipulation.set(0x0000.toUShort(), SPK.A, SPK.A), equalTo(0x0001.toUShort()))
        assertThat(TracedPhasesBitManipulation.set(0x0000.toUShort(), SPK.A, SPK.B), equalTo(0x0002.toUShort()))
        assertThat(TracedPhasesBitManipulation.set(0x0000.toUShort(), SPK.A, SPK.C), equalTo(0x0004.toUShort()))
        assertThat(TracedPhasesBitManipulation.set(0x0000.toUShort(), SPK.A, SPK.N), equalTo(0x0008.toUShort()))
        assertThat(TracedPhasesBitManipulation.set(0xffff.toUShort(), SPK.A, SPK.NONE), equalTo(0xfff0.toUShort()))

        assertThat(TracedPhasesBitManipulation.set(0x0000.toUShort(), SPK.B, SPK.A), equalTo(0x0010.toUShort()))
        assertThat(TracedPhasesBitManipulation.set(0x0000.toUShort(), SPK.B, SPK.B), equalTo(0x0020.toUShort()))
        assertThat(TracedPhasesBitManipulation.set(0x0000.toUShort(), SPK.B, SPK.C), equalTo(0x0040.toUShort()))
        assertThat(TracedPhasesBitManipulation.set(0x0000.toUShort(), SPK.B, SPK.N), equalTo(0x0080.toUShort()))
        assertThat(TracedPhasesBitManipulation.set(0xffff.toUShort(), SPK.B, SPK.NONE), equalTo(0xff0f.toUShort()))

        assertThat(TracedPhasesBitManipulation.set(0x0000.toUShort(), SPK.C, SPK.A), equalTo(0x0100.toUShort()))
        assertThat(TracedPhasesBitManipulation.set(0x0000.toUShort(), SPK.C, SPK.B), equalTo(0x0200.toUShort()))
        assertThat(TracedPhasesBitManipulation.set(0x0000.toUShort(), SPK.C, SPK.C), equalTo(0x0400.toUShort()))
        assertThat(TracedPhasesBitManipulation.set(0x0000.toUShort(), SPK.C, SPK.N), equalTo(0x0800.toUShort()))
        assertThat(TracedPhasesBitManipulation.set(0xffff.toUShort(), SPK.C, SPK.NONE), equalTo(0xf0ff.toUShort()))

        assertThat(TracedPhasesBitManipulation.set(0x0000.toUShort(), SPK.N, SPK.A), equalTo(0x1000.toUShort()))
        assertThat(TracedPhasesBitManipulation.set(0x0000.toUShort(), SPK.N, SPK.B), equalTo(0x2000.toUShort()))
        assertThat(TracedPhasesBitManipulation.set(0x0000.toUShort(), SPK.N, SPK.C), equalTo(0x4000.toUShort()))
        assertThat(TracedPhasesBitManipulation.set(0x0000.toUShort(), SPK.N, SPK.N), equalTo(0x8000.toUShort()))
        assertThat(TracedPhasesBitManipulation.set(0xffff.toUShort(), SPK.N, SPK.NONE), equalTo(0x0fff.toUShort()))
    }

}

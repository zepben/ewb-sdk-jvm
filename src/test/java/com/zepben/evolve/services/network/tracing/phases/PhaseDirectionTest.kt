/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
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

internal class PhaseDirectionTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun valueCoverage() {
        PhaseDirection.values().forEach {
            assertThat(PhaseDirection.valueOf(it.name), equalTo(it))
        }
    }

    @Test
    internal fun testPhaseDirection() {
        assertThat(PhaseDirection.NONE.has(PhaseDirection.NONE), equalTo(true))
        assertThat(PhaseDirection.NONE.has(PhaseDirection.IN), equalTo(false))
        assertThat(PhaseDirection.NONE.has(PhaseDirection.OUT), equalTo(false))
        assertThat(PhaseDirection.NONE.has(PhaseDirection.BOTH), equalTo(false))

        assertThat(PhaseDirection.IN.has(PhaseDirection.NONE), equalTo(false))
        assertThat(PhaseDirection.IN.has(PhaseDirection.IN), equalTo(true))
        assertThat(PhaseDirection.IN.has(PhaseDirection.OUT), equalTo(false))
        assertThat(PhaseDirection.IN.has(PhaseDirection.BOTH), equalTo(false))

        assertThat(PhaseDirection.OUT.has(PhaseDirection.NONE), equalTo(false))
        assertThat(PhaseDirection.OUT.has(PhaseDirection.IN), equalTo(false))
        assertThat(PhaseDirection.OUT.has(PhaseDirection.OUT), equalTo(true))
        assertThat(PhaseDirection.OUT.has(PhaseDirection.BOTH), equalTo(false))

        assertThat(PhaseDirection.BOTH.has(PhaseDirection.NONE), equalTo(false))
        assertThat(PhaseDirection.BOTH.has(PhaseDirection.IN), equalTo(true))
        assertThat(PhaseDirection.BOTH.has(PhaseDirection.OUT), equalTo(true))
        assertThat(PhaseDirection.BOTH.has(PhaseDirection.BOTH), equalTo(true))

        var direction: PhaseDirection = PhaseDirection.NONE
        assertThat(direction, equalTo(PhaseDirection.NONE))
        direction += PhaseDirection.IN
        assertThat(direction, equalTo(PhaseDirection.IN))
        direction += PhaseDirection.OUT
        assertThat(direction, equalTo(PhaseDirection.BOTH))
        direction -= PhaseDirection.IN
        assertThat(direction, equalTo(PhaseDirection.OUT))
        direction += PhaseDirection.BOTH
        assertThat(direction, equalTo(PhaseDirection.BOTH))
        direction -= PhaseDirection.BOTH
        assertThat(direction, equalTo(PhaseDirection.NONE))
        direction += PhaseDirection.BOTH
        assertThat(direction, equalTo(PhaseDirection.BOTH))
        direction -= PhaseDirection.OUT
        assertThat(direction, equalTo(PhaseDirection.IN))
        direction -= PhaseDirection.NONE
        assertThat(direction, equalTo(PhaseDirection.IN))
        direction += PhaseDirection.NONE
        assertThat(direction, equalTo(PhaseDirection.IN))
    }
}

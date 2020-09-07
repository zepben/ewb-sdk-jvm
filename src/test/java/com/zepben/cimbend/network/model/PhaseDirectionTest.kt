/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 * This file is part of evolve-sdk-jvm.
 *
 * evolve-sdk-jvm is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * evolve-sdk-jvm is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with evolve-sdk-jvm.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.zepben.cimbend.network.model

import com.zepben.test.util.junit.SystemLogExtension
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

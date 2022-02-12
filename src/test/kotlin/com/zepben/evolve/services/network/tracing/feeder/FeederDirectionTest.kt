/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network.tracing.feeder

import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class FeederDirectionTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun valueCoverage() {
        FeederDirection.values().forEach {
            assertThat(FeederDirection.valueOf(it.name), equalTo(it))
        }
    }

    @Test
    internal fun testPhaseDirection() {
        assertThat(FeederDirection.NONE.has(FeederDirection.NONE), equalTo(true))
        assertThat(FeederDirection.NONE.has(FeederDirection.UPSTREAM), equalTo(false))
        assertThat(FeederDirection.NONE.has(FeederDirection.DOWNSTREAM), equalTo(false))
        assertThat(FeederDirection.NONE.has(FeederDirection.BOTH), equalTo(false))

        assertThat(FeederDirection.UPSTREAM.has(FeederDirection.NONE), equalTo(false))
        assertThat(FeederDirection.UPSTREAM.has(FeederDirection.UPSTREAM), equalTo(true))
        assertThat(FeederDirection.UPSTREAM.has(FeederDirection.DOWNSTREAM), equalTo(false))
        assertThat(FeederDirection.UPSTREAM.has(FeederDirection.BOTH), equalTo(false))

        assertThat(FeederDirection.DOWNSTREAM.has(FeederDirection.NONE), equalTo(false))
        assertThat(FeederDirection.DOWNSTREAM.has(FeederDirection.UPSTREAM), equalTo(false))
        assertThat(FeederDirection.DOWNSTREAM.has(FeederDirection.DOWNSTREAM), equalTo(true))
        assertThat(FeederDirection.DOWNSTREAM.has(FeederDirection.BOTH), equalTo(false))

        assertThat(FeederDirection.BOTH.has(FeederDirection.NONE), equalTo(false))
        assertThat(FeederDirection.BOTH.has(FeederDirection.UPSTREAM), equalTo(true))
        assertThat(FeederDirection.BOTH.has(FeederDirection.DOWNSTREAM), equalTo(true))
        assertThat(FeederDirection.BOTH.has(FeederDirection.BOTH), equalTo(true))

        var direction: FeederDirection = FeederDirection.NONE
        assertThat(direction, equalTo(FeederDirection.NONE))
        direction += FeederDirection.UPSTREAM
        assertThat(direction, equalTo(FeederDirection.UPSTREAM))
        direction += FeederDirection.DOWNSTREAM
        assertThat(direction, equalTo(FeederDirection.BOTH))
        direction -= FeederDirection.UPSTREAM
        assertThat(direction, equalTo(FeederDirection.DOWNSTREAM))
        direction += FeederDirection.BOTH
        assertThat(direction, equalTo(FeederDirection.BOTH))
        direction -= FeederDirection.BOTH
        assertThat(direction, equalTo(FeederDirection.NONE))
        direction += FeederDirection.BOTH
        assertThat(direction, equalTo(FeederDirection.BOTH))
        direction -= FeederDirection.DOWNSTREAM
        assertThat(direction, equalTo(FeederDirection.UPSTREAM))
        direction -= FeederDirection.NONE
        assertThat(direction, equalTo(FeederDirection.UPSTREAM))
        direction += FeederDirection.NONE
        assertThat(direction, equalTo(FeederDirection.UPSTREAM))
    }
}

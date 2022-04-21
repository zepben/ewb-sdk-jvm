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
    internal fun testHas() {
        assertThat("NONE has NONE", FeederDirection.NONE.has(FeederDirection.NONE))
        assertThat("NONE does not have UPSTREAM", !FeederDirection.NONE.has(FeederDirection.UPSTREAM))
        assertThat("NONE does not have DOWNSTREAM", !FeederDirection.NONE.has(FeederDirection.DOWNSTREAM))
        assertThat("NONE does not have BOTH", !FeederDirection.NONE.has(FeederDirection.BOTH))

        assertThat("UPSTREAM does not have NONE", !FeederDirection.UPSTREAM.has(FeederDirection.NONE))
        assertThat("UPSTREAM has UPSTREAM", FeederDirection.UPSTREAM.has(FeederDirection.UPSTREAM))
        assertThat("UPSTREAM does not have DOWNSTREAM", !FeederDirection.UPSTREAM.has(FeederDirection.DOWNSTREAM))
        assertThat("UPSTREAM does not have BOTH", !FeederDirection.UPSTREAM.has(FeederDirection.BOTH))

        assertThat("DOWNSTREAM does not have NONE", !FeederDirection.DOWNSTREAM.has(FeederDirection.NONE))
        assertThat("DOWNSTREAM does not have UPSTREAM", !FeederDirection.DOWNSTREAM.has(FeederDirection.UPSTREAM))
        assertThat("DOWNSTREAM has DOWNSTREAM", FeederDirection.DOWNSTREAM.has(FeederDirection.DOWNSTREAM))
        assertThat("DOWNSTREAM does not have BOTH", !FeederDirection.DOWNSTREAM.has(FeederDirection.BOTH))

        assertThat("BOTH does not have NONE", !FeederDirection.BOTH.has(FeederDirection.NONE))
        assertThat("BOTH has UPSTREAM", FeederDirection.BOTH.has(FeederDirection.UPSTREAM))
        assertThat("BOTH has DOWNSTREAM", FeederDirection.BOTH.has(FeederDirection.DOWNSTREAM))
        assertThat("BOTH has BOTH", FeederDirection.BOTH.has(FeederDirection.BOTH))
    }

    @Test
    internal fun testPlus() {
        assertThat(FeederDirection.NONE + FeederDirection.NONE, equalTo(FeederDirection.NONE))
        assertThat(FeederDirection.NONE + FeederDirection.UPSTREAM, equalTo(FeederDirection.UPSTREAM))
        assertThat(FeederDirection.NONE + FeederDirection.DOWNSTREAM, equalTo(FeederDirection.DOWNSTREAM))
        assertThat(FeederDirection.NONE + FeederDirection.BOTH, equalTo(FeederDirection.BOTH))

        assertThat(FeederDirection.UPSTREAM + FeederDirection.NONE, equalTo(FeederDirection.UPSTREAM))
        assertThat(FeederDirection.UPSTREAM + FeederDirection.UPSTREAM, equalTo(FeederDirection.UPSTREAM))
        assertThat(FeederDirection.UPSTREAM + FeederDirection.DOWNSTREAM, equalTo(FeederDirection.BOTH))
        assertThat(FeederDirection.UPSTREAM + FeederDirection.BOTH, equalTo(FeederDirection.BOTH))

        assertThat(FeederDirection.DOWNSTREAM + FeederDirection.NONE, equalTo(FeederDirection.DOWNSTREAM))
        assertThat(FeederDirection.DOWNSTREAM + FeederDirection.UPSTREAM, equalTo(FeederDirection.BOTH))
        assertThat(FeederDirection.DOWNSTREAM + FeederDirection.DOWNSTREAM, equalTo(FeederDirection.DOWNSTREAM))
        assertThat(FeederDirection.DOWNSTREAM + FeederDirection.BOTH, equalTo(FeederDirection.BOTH))

        assertThat(FeederDirection.BOTH + FeederDirection.NONE, equalTo(FeederDirection.BOTH))
        assertThat(FeederDirection.BOTH + FeederDirection.UPSTREAM, equalTo(FeederDirection.BOTH))
        assertThat(FeederDirection.BOTH + FeederDirection.DOWNSTREAM, equalTo(FeederDirection.BOTH))
        assertThat(FeederDirection.BOTH + FeederDirection.BOTH, equalTo(FeederDirection.BOTH))
    }

    @Test
    internal fun testMinus() {
        assertThat(FeederDirection.NONE - FeederDirection.NONE, equalTo(FeederDirection.NONE))
        assertThat(FeederDirection.NONE - FeederDirection.UPSTREAM, equalTo(FeederDirection.NONE))
        assertThat(FeederDirection.NONE - FeederDirection.DOWNSTREAM, equalTo(FeederDirection.NONE))
        assertThat(FeederDirection.NONE - FeederDirection.BOTH, equalTo(FeederDirection.NONE))

        assertThat(FeederDirection.UPSTREAM - FeederDirection.NONE, equalTo(FeederDirection.UPSTREAM))
        assertThat(FeederDirection.UPSTREAM - FeederDirection.UPSTREAM, equalTo(FeederDirection.NONE))
        assertThat(FeederDirection.UPSTREAM - FeederDirection.DOWNSTREAM, equalTo(FeederDirection.UPSTREAM))
        assertThat(FeederDirection.UPSTREAM - FeederDirection.BOTH, equalTo(FeederDirection.NONE))

        assertThat(FeederDirection.DOWNSTREAM - FeederDirection.NONE, equalTo(FeederDirection.DOWNSTREAM))
        assertThat(FeederDirection.DOWNSTREAM - FeederDirection.UPSTREAM, equalTo(FeederDirection.DOWNSTREAM))
        assertThat(FeederDirection.DOWNSTREAM - FeederDirection.DOWNSTREAM, equalTo(FeederDirection.NONE))
        assertThat(FeederDirection.DOWNSTREAM - FeederDirection.BOTH, equalTo(FeederDirection.NONE))

        assertThat(FeederDirection.BOTH - FeederDirection.NONE, equalTo(FeederDirection.BOTH))
        assertThat(FeederDirection.BOTH - FeederDirection.UPSTREAM, equalTo(FeederDirection.DOWNSTREAM))
        assertThat(FeederDirection.BOTH - FeederDirection.DOWNSTREAM, equalTo(FeederDirection.UPSTREAM))
        assertThat(FeederDirection.BOTH - FeederDirection.BOTH, equalTo(FeederDirection.NONE))

    }

}

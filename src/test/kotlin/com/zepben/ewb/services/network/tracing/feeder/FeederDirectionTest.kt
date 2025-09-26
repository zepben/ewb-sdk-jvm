/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.network.tracing.feeder

import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class FeederDirectionTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun valueCoverage() {
        FeederDirection.entries.forEach {
            assertThat(FeederDirection.valueOf(it.name), equalTo(it))
        }
    }

    @Test
    internal fun testContains() {
        assertThat("NONE has NONE", FeederDirection.NONE in FeederDirection.NONE)
        assertThat("NONE does not have UPSTREAM", FeederDirection.UPSTREAM !in FeederDirection.NONE)
        assertThat("NONE does not have DOWNSTREAM", FeederDirection.DOWNSTREAM !in FeederDirection.NONE)
        assertThat("NONE does not have BOTH", FeederDirection.BOTH !in FeederDirection.NONE)

        assertThat("UPSTREAM does not have NONE", FeederDirection.NONE !in FeederDirection.UPSTREAM)
        assertThat("UPSTREAM has UPSTREAM", FeederDirection.UPSTREAM in FeederDirection.UPSTREAM)
        assertThat("UPSTREAM does not have DOWNSTREAM", FeederDirection.DOWNSTREAM !in FeederDirection.UPSTREAM)
        assertThat("UPSTREAM does not have BOTH", FeederDirection.BOTH !in FeederDirection.UPSTREAM)

        assertThat("DOWNSTREAM does not have NONE", FeederDirection.NONE !in FeederDirection.DOWNSTREAM)
        assertThat("DOWNSTREAM does not have UPSTREAM", FeederDirection.UPSTREAM !in FeederDirection.DOWNSTREAM)
        assertThat("DOWNSTREAM has DOWNSTREAM", FeederDirection.DOWNSTREAM in FeederDirection.DOWNSTREAM)
        assertThat("DOWNSTREAM does not have BOTH", FeederDirection.BOTH !in FeederDirection.DOWNSTREAM)

        assertThat("BOTH does not have NONE", FeederDirection.NONE !in FeederDirection.BOTH)
        assertThat("BOTH has UPSTREAM", FeederDirection.UPSTREAM in FeederDirection.BOTH)
        assertThat("BOTH has DOWNSTREAM", FeederDirection.DOWNSTREAM in FeederDirection.BOTH)
        assertThat("BOTH has BOTH", FeederDirection.BOTH in FeederDirection.BOTH)
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

    @Test
    internal fun testNot() {
        assertThat(!FeederDirection.NONE, equalTo(FeederDirection.BOTH))
        assertThat(!FeederDirection.UPSTREAM, equalTo(FeederDirection.DOWNSTREAM))
        assertThat(!FeederDirection.DOWNSTREAM, equalTo(FeederDirection.UPSTREAM))
        assertThat(!FeederDirection.BOTH, equalTo(FeederDirection.NONE))
    }

}

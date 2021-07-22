/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network

import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class ResistanceReactanceTest {

    @JvmField
    @RegisterExtension
    var systemOut: SystemLogExtension = SystemLogExtension.SYSTEM_OUT.captureLog().muteOnSuccess()

    @Test
    internal fun isEmpty() {
        assertThat(ResistanceReactance(null, null, null, null).isEmpty(), equalTo(true))
        assertThat(ResistanceReactance(1.1, null, null, null).isEmpty(), equalTo(false))
        assertThat(ResistanceReactance(null, 2.2, null, null).isEmpty(), equalTo(false))
        assertThat(ResistanceReactance(null, null, 3.3, null).isEmpty(), equalTo(false))
        assertThat(ResistanceReactance(null, null, null, 4.4).isEmpty(), equalTo(false))
    }

    @Test
    internal fun isComplete() {
        assertThat(ResistanceReactance(1.1, 1.2, 1.3, 1.4).isComplete(), equalTo(true))
        assertThat(ResistanceReactance(null, 2.2, 2.3, 2.4).isComplete(), equalTo(false))
        assertThat(ResistanceReactance(3.1, null, 3.3, 3.4).isComplete(), equalTo(false))
        assertThat(ResistanceReactance(4.1, 4.2, null, 4.4).isComplete(), equalTo(false))
        assertThat(ResistanceReactance(5.1, 5.2, 5.3, null).isComplete(), equalTo(false))
    }

    companion object {
        fun validateResistanceReactance(rr: ResistanceReactance, r: Double?, x: Double?, r0: Double?, x0: Double?) {
            assertThat(rr.r, equalTo(r))
            assertThat(rr.x, equalTo(x))
            assertThat(rr.r0, equalTo(r0))
            assertThat(rr.x0, equalTo(x0))
        }
    }

}

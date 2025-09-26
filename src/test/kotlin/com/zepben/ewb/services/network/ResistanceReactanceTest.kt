/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.network

import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class ResistanceReactanceTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun isEmpty() {
        assertThat("ResistanceReactance with all nulls is empty", ResistanceReactance().isEmpty())
        assertThat("ResistanceReactance with nonnull r is not empty", !ResistanceReactance(r = 1.1).isEmpty())
        assertThat("ResistanceReactance with nonnull x is not empty", !ResistanceReactance(x = 2.2).isEmpty())
        assertThat("ResistanceReactance with nonnull r0 is not empty", !ResistanceReactance(r0 = 3.3).isEmpty())
        assertThat("ResistanceReactance with nonnull x0 is not empty", !ResistanceReactance(x0 = 4.4).isEmpty())
    }

    @Test
    internal fun isComplete() {
        assertThat("ResistanceReactance without nulls is complete", ResistanceReactance(1.1, 1.2, 1.3, 1.4).isComplete())
        assertThat("ResistanceReactance with null r is not complete", !ResistanceReactance(x = 2.2, r0 = 2.3, x0 = 2.4).isComplete())
        assertThat("ResistanceReactance with null x is not complete", !ResistanceReactance(r = 3.1, r0 = 3.3, x0 = 3.4).isComplete())
        assertThat("ResistanceReactance with null r0 is not complete", !ResistanceReactance(r = 4.1, x = 4.2, x0 = 4.4).isComplete())
        assertThat("ResistanceReactance with null x0 is not complete", !ResistanceReactance(r = 5.1, x = 5.2, r0 = 5.3).isComplete())
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

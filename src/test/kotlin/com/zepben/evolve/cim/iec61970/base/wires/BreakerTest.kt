/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.cim.iec61970.base.wires

import com.zepben.evolve.cim.iec61970.base.core.Substation
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class BreakerTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(Breaker().mRID, not(equalTo("")))
        assertThat(Breaker("id").mRID, equalTo("id"))
    }

    @Test
    internal fun `is substation breaker when associated with a substation equipment container`() {
        val breaker = Breaker()

        assertThat(breaker.isSubstationBreaker, equalTo(false))

        breaker.addContainer(Substation())

        assertThat(breaker.isSubstationBreaker, equalTo(true))
    }
}

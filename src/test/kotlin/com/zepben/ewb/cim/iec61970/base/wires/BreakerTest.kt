/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.wires

import com.zepben.ewb.cim.iec61970.base.core.Feeder
import com.zepben.ewb.cim.iec61970.base.core.Substation
import com.zepben.ewb.cim.iec61970.base.core.Terminal
import com.zepben.ewb.services.network.NetworkService
import com.zepben.ewb.services.network.testdata.fillFields
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
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
    internal fun accessorCoverage() {
        val breaker = Breaker()

        assertThat(breaker.inTransitTime, nullValue())

        breaker.fillFields(NetworkService())

        assertThat(breaker.inTransitTime, equalTo(1.1))
    }

    @Test
    internal fun `is substation breaker when associated with a substation equipment container`() {
        val breaker = Breaker()

        assertThat(breaker.isSubstationBreaker, equalTo(false))

        breaker.addContainer(Substation())

        assertThat(breaker.isSubstationBreaker, equalTo(true))
    }

    @Test
    internal fun `is feeder head breaker when a terminal is a feeder head terminal`() {
        val breaker = Breaker().apply { addTerminal(Terminal()); addTerminal(Terminal()) }
        val feeder = Feeder().apply { normalHeadTerminal = Terminal() }

        assertThat(breaker.isFeederHeadBreaker, equalTo(false))

        breaker.addContainer(feeder)
        assertThat(breaker.isFeederHeadBreaker, equalTo(false))

        breaker.addTerminal(feeder.normalHeadTerminal!!)
        assertThat(breaker.isFeederHeadBreaker, equalTo(true))
    }

}

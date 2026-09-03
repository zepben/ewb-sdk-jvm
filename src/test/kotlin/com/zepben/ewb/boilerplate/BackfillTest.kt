/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.boilerplate

import com.zepben.ewb.cim.iec61970.base.core.Feeder
import com.zepben.ewb.cim.iec61970.base.core.Substation
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.RegisterExtension

internal class BackfillTest {

    companion object {
        @JvmField
        @RegisterExtension
        val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()
    }

    private val backfill = Backfill(
        Feeder::normalEnergizingSubstation,
        { feeder, substation -> feeder.normalEnergizingSubstation = substation },
        Feeder::normalEnergizingSubstation
    )

    @Test
    internal fun `exposes the configured getter and setter`() {
        val owner = Substation("owner")
        val feeder = Feeder("feeder")

        assertThat(backfill.getter(feeder), nullValue())
        backfill.setter(feeder, owner)
        assertThat(backfill.getter(feeder), sameInstance(owner))
    }

    @Test
    internal fun `apply sets a missing back reference`() {
        val owner = Substation("owner")
        val feeder = Feeder("feeder")

        backfill.set(feeder, owner)

        assertThat(feeder.normalEnergizingSubstation, sameInstance(owner))
        assertThat(backfill.backfillProp, equalTo(Feeder::normalEnergizingSubstation))
    }

    @Test
    internal fun `apply accepts the expected back reference and rejects a different owner`() {
        val owner = Substation("owner")
        val feeder = Feeder("feeder").apply { normalEnergizingSubstation = owner }

        backfill.set(feeder, owner)
        assertThrows<IllegalArgumentException> { backfill.set(feeder, Substation("other")) }
        assertThat(feeder.normalEnergizingSubstation, sameInstance(owner))
    }

    @Test
    internal fun `apply rejects an element when its back reference cannot be set`() {
        val owner = Substation("owner")
        val feeder = Feeder("feeder")
        val ineffectiveBackfill = Backfill(
            Feeder::normalEnergizingSubstation,
            { _, _ -> },
            Feeder::normalEnergizingSubstation
        )

        assertThrows<IllegalArgumentException> { ineffectiveBackfill.set(feeder, owner) }
        assertThat(feeder.normalEnergizingSubstation, nullValue())
    }

    @Test
    internal fun `clear removes the back reference`() {
        val feeder = Feeder("feeder").apply { normalEnergizingSubstation = Substation("owner") }

        backfill.clear(feeder)

        assertThat(feeder.normalEnergizingSubstation, nullValue())
    }
}

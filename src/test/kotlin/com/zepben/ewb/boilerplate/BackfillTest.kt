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
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class BackfillTest {

    private val backfill = Backfill<Feeder, Substation>(
        Feeder::normalEnergizingSubstation,
        { feeder, substation -> feeder.normalEnergizingSubstation = substation },
        Feeder::normalEnergizingSubstation
    )

    @Test
    internal fun `apply sets a missing back reference`() {
        val owner = Substation("owner")
        val feeder = Feeder("feeder")

        backfill.apply(owner, feeder)

        assertThat(feeder.normalEnergizingSubstation, sameInstance(owner))
        assertThat(backfill.backfillProp, equalTo(Feeder::normalEnergizingSubstation))
    }

    @Test
    internal fun `apply accepts the expected back reference and rejects a different owner`() {
        val owner = Substation("owner")
        val feeder = Feeder("feeder").apply { normalEnergizingSubstation = owner }

        backfill.apply(owner, feeder)
        assertThrows<IllegalArgumentException> { backfill.apply(Substation("other"), feeder) }
        assertThat(feeder.normalEnergizingSubstation, sameInstance(owner))
    }

    @Test
    internal fun `apply rejects an element when its back reference cannot be set`() {
        val owner = Substation("owner")
        val feeder = Feeder("feeder")
        val ineffectiveBackfill = Backfill<Feeder, Substation>(
            Feeder::normalEnergizingSubstation,
            { _, _ -> },
            Feeder::normalEnergizingSubstation
        )

        assertThrows<IllegalArgumentException> { ineffectiveBackfill.apply(owner, feeder) }
        assertThat(feeder.normalEnergizingSubstation, nullValue())
    }

    @Test
    internal fun `clear removes the back reference`() {
        val feeder = Feeder("feeder").apply { normalEnergizingSubstation = Substation("owner") }

        backfill.clear(feeder)

        assertThat(feeder.normalEnergizingSubstation, nullValue())
    }
}

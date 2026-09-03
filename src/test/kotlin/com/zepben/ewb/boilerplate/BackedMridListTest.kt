/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.boilerplate

import com.zepben.ewb.boilerplate.collections.BackedMridList
import com.zepben.ewb.cim.iec61970.base.core.Feeder
import com.zepben.ewb.cim.iec61970.base.core.Substation
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.RegisterExtension

internal class BackedMridListTest {

    companion object {
        @JvmField
        @RegisterExtension
        val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()
    }

    private fun backfill() = Backfill<Feeder, Substation>(
        Feeder::normalEnergizingSubstation,
        { feeder, owner -> feeder.normalEnergizingSubstation = owner },
        Feeder::normalEnergizingSubstation
    )

    @Test
    internal fun `existing backing items are exposed and can be found by mrid`() {
        val feeder = Feeder("a")
        val backing = mutableListOf(feeder)
        val list = BackedMridList(backing, Feeder("owner"), "A Feeder")

        assertThat(list.getByMrid("a"), sameInstance(feeder))
        assertThat(list.getByMrid("missing"), nullValue())
        assertThat(list["a"], sameInstance(feeder))
        assertThat(list["missing"], nullValue())
        assertThat(list[0], sameInstance(feeder))
        assertThat(list.toList(), contains(feeder))
    }

    @Test
    internal fun `add stores the item and applies backfill`() {
        val owner = Substation("owner")
        val feeder = Feeder("a")
        val list = BackedMridList(owner = owner, elementDescription = "A Feeder", backfill = backfill())

        assertThat(list.add(feeder), equalTo(true))
        assertThat(list.toList(), contains(feeder))
        assertThat(feeder.normalEnergizingSubstation, sameInstance(owner))
    }

    @Test
    internal fun `backfill and validation failures do not append`() {
        val otherOwner = Substation("other")
        val wrongOwner = Feeder("wrong").apply { normalEnergizingSubstation = otherOwner }
        val owner = Substation("owner")
        val list = BackedMridList(owner = owner, elementDescription = "A Feeder", backfill = backfill())

        assertThrows<IllegalArgumentException> { list.add(wrongOwner) }
        assertThat(list, empty())

        val rejected = Feeder("rejected")
        val validating = BackedMridList<Feeder, Substation>(owner = owner, elementDescription = "A Feeder", validate = { require(it !== rejected) })
        assertThrows<IllegalArgumentException> { validating.add(rejected) }
        assertThat(validating, empty())
    }

    @Test
    internal fun `backfill runs before validation`() {
        val owner = Substation("owner")
        val feeder = Feeder("a")
        var backfilledDuringValidation = false
        val list = BackedMridList(
            owner = owner,
            elementDescription = "A Feeder",
            backfill = backfill(),
            validate = { backfilledDuringValidation = it.normalEnergizingSubstation === owner }
        )

        list.add(feeder)

        assertThat(backfilledDuringValidation, equalTo(true))
    }

    @Test
    internal fun `mrid collision is rejected before backfill`() {
        val owner = Substation("owner")
        val stored = Feeder("a").apply { normalEnergizingSubstation = owner }
        val collision = Feeder("a")
        val list = BackedMridList(mutableListOf(stored), owner, "A Feeder", backfill())

        assertThrows<IllegalArgumentException> { list.add(collision) }

        assertThat(collision.normalEnergizingSubstation, nullValue())
        assertThat(list, contains(sameInstance(stored)))
    }

    @Test
    internal fun `add sorts and remove and clear release back references`() {
        val owner = Substation("owner")
        val a = Feeder("a")
        val b = Feeder("b")
        val list = BackedMridList(owner = owner, elementDescription = "A Feeder", backfill = backfill(), sortBy = { it.mRID })

        list.add(b)
        list.add(a)
        assertThat(list.toList(), contains(a, b))
        assertThat(list.toString(), equalTo(listOf(a, b).toString()))

        assertThat(list.remove(a), equalTo(true))
        assertThat(a.normalEnergizingSubstation, nullValue())
        list.clear()
        assertThat(list, empty())
        assertThat(b.normalEnergizingSubstation, nullValue())
    }

    @Test
    internal fun `failed remove leaves the list and back reference unchanged`() {
        val owner = Substation("owner")
        val stored = Feeder("stored").apply { normalEnergizingSubstation = owner }
        val missing = Feeder("missing").apply { normalEnergizingSubstation = owner }
        val list = BackedMridList(mutableListOf(stored), owner, "A Feeder", backfill())

        assertThat(list.remove(missing), equalTo(false))

        assertThat(list, contains(stored))
        assertThat(stored.normalEnergizingSubstation, sameInstance(owner))
        assertThat(missing.normalEnergizingSubstation, sameInstance(owner))
    }
}

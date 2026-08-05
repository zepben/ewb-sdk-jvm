/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.boilerplate

import com.zepben.ewb.boilerplate.collections.LazyMridList
import com.zepben.ewb.cim.iec61970.base.core.Feeder
import com.zepben.ewb.cim.iec61970.base.core.Substation
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class LazyMridListTest {

    private fun backfill() = Backfill<Feeder, Substation>(
        Feeder::normalEnergizingSubstation,
        { feeder, owner -> feeder.normalEnergizingSubstation = owner },
        Feeder::normalEnergizingSubstation
    )

    @Test
    internal fun `getByMrid searches the backing list and treats null as empty`() {
        val feeder = Feeder("a")
        var backing: MutableList<Feeder>? = mutableListOf(feeder)
        val list = LazyMridList({ backing }, { backing = it }, Feeder("owner"), "A Feeder")

        assertThat(list.getByMrid("a"), sameInstance(feeder))
        backing = null
        assertThat(list.getByMrid("a"), nullValue())
    }

    @Test
    internal fun `add applies backfill before superclass validation`() {
        val owner = Substation("owner")
        val feeder = Feeder("a")
        var backing: MutableList<Feeder>? = null
        val list = LazyMridList(
            { backing }, { backing = it }, owner, "A Feeder", backfill(),
            validate = { check(it.normalEnergizingSubstation === owner) }
        )

        list.add(feeder)

        assertThat(feeder.normalEnergizingSubstation, sameInstance(owner))
        assertThat(backing, contains(feeder))
    }

    @Test
    internal fun `backfill failure does not call superclass add`() {
        val owner = Substation("owner")
        val other = Substation("other")
        var backing: MutableList<Feeder>? = null
        val failingBackfill = Backfill<Feeder, Substation>(
            getter = { other },
            setter = { _, _ -> },
            backfillProp = Feeder::normalEnergizingSubstation
        )
        val list = LazyMridList({ backing }, { backing = it }, owner, "A Feeder", failingBackfill)

        assertThrows<IllegalArgumentException> { list.add(Feeder("a")) }
        assertThat(backing, nullValue())
    }

    @Test
    internal fun `validation failure does not create the backing list`() {
        val owner = Substation("owner")
        val feeder = Feeder("a")
        var backing: MutableList<Feeder>? = null
        val list = LazyMridList(
            { backing }, { backing = it }, owner, "A Feeder", backfill(),
            validate = { require(false) }
        )

        assertThrows<IllegalArgumentException> { list.add(feeder) }

        assertThat(backing, nullValue())
        assertThat(feeder.normalEnergizingSubstation, sameInstance(owner))
    }

    @Test
    internal fun `successful additions sort the backing list`() {
        val a = Feeder("a")
        val b = Feeder("b")
        var backing: MutableList<Feeder>? = null
        val list = LazyMridList(
            { backing }, { backing = it }, Feeder("owner"), "A Feeder",
            sortBy = Feeder::mRID
        )

        list.add(b)
        list.add(a)

        assertThat(backing, contains(a, b))
    }

    @Test
    internal fun `remove releases the back reference and nulls empty backing`() {
        val owner = Substation("owner")
        val feeder = Feeder("a").apply { normalEnergizingSubstation = owner }
        var backing: MutableList<Feeder>? = mutableListOf(feeder)
        val list = LazyMridList({ backing }, { backing = it }, owner, "A Feeder", backfill())

        assertThat(list.remove(feeder), equalTo(true))

        assertThat(backing, nullValue())
        assertThat(feeder.normalEnergizingSubstation, nullValue())
    }

    @Test
    internal fun `failed remove leaves an existing back reference unchanged`() {
        val owner = Substation("owner")
        val feeder = Feeder("a").apply { normalEnergizingSubstation = owner }
        val missing = Feeder("missing").apply { normalEnergizingSubstation = owner }
        var backing: MutableList<Feeder>? = mutableListOf(feeder)
        val list = LazyMridList({ backing }, { backing = it }, owner, "A Feeder", backfill())

        assertThat(list.remove(missing), equalTo(false))

        assertThat(backing, contains(feeder))
        assertThat(feeder.normalEnergizingSubstation, sameInstance(owner))
        assertThat(missing.normalEnergizingSubstation, sameInstance(owner))
    }

    @Test
    internal fun `clear releases all back references and nulls the backing list`() {
        val owner = Substation("owner")
        val a = Feeder("a").apply { normalEnergizingSubstation = owner }
        val b = Feeder("b").apply { normalEnergizingSubstation = owner }
        var backing: MutableList<Feeder>? = mutableListOf(a, b)
        val list = LazyMridList({ backing }, { backing = it }, owner, "A Feeder", backfill())

        list.clear()

        assertThat(backing, nullValue())
        assertThat(a.normalEnergizingSubstation, nullValue())
        assertThat(b.normalEnergizingSubstation, nullValue())
    }
}

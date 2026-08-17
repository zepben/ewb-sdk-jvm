/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.boilerplate

import com.zepben.ewb.boilerplate.collections.LazyMridMap
import com.zepben.ewb.boilerplate.collections.MridCollection
import com.zepben.ewb.cim.iec61970.base.core.Feeder
import com.zepben.ewb.cim.iec61970.base.core.Substation
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.RegisterExtension

internal class LazyMridMapTest {

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
    internal fun `null backing is exposed as an empty collection`() {
        var backing: MutableMap<String, Feeder>? = null
        val map: MridCollection<Feeder> = LazyMridMap(
            { backing }, { backing = it }, Feeder("owner"), "A Feeder"
        )

        assertThat(map.size, equalTo(0))
        assertThat(map.isEmpty(), equalTo(true))
        assertThat(map.getByMrid("missing"), nullValue())
        assertThat(map, empty())
        assertThat(backing, nullValue())
    }

    @Test
    internal fun `add creates a map keyed by mrid and adds to an existing map`() {
        var backing: MutableMap<String, Feeder>? = null
        val map: MridCollection<Feeder> = LazyMridMap(
            { backing }, { backing = it }, Feeder("owner"), "A Feeder"
        )
        val a = Feeder("a")
        val b = Feeder("b")

        assertThat(map.add(a), equalTo(true))
        assertThat(map.add(b), equalTo(true))

        assertThat(backing, equalTo(mapOf("a" to a, "b" to b)))
        assertThat(map.getByMrid("a"), sameInstance(a))
        assertThat(map.toList(), containsInAnyOrder(a, b))
        assertThat(map.contains(a), equalTo(true))
        assertThat(map.contains(Feeder("a")), equalTo(false))
    }

    @Test
    internal fun `backfill runs before validation`() {
        val owner = Substation("owner")
        var backing: MutableMap<String, Feeder>? = null
        val map = LazyMridMap(
            { backing }, { backing = it }, owner, "A Feeder", backfill(),
            validate = { check(it.normalEnergizingSubstation === owner) }
        )

        map.add(Feeder("a"))

        assertThat(backing, aMapWithSize(1))
    }

    @Test
    internal fun `backfill and validation failures do not create a map`() {
        var backing: MutableMap<String, Feeder>? = null
        val owner = Substation("owner")
        val other = Substation("other")
        val badBackfill = Backfill<Feeder, Substation>(
            { other }, { _, _ -> }, Feeder::normalEnergizingSubstation
        )

        assertThrows<IllegalArgumentException> {
            LazyMridMap({ backing }, { backing = it }, owner, "A Feeder", badBackfill).add(Feeder("a"))
        }
        assertThat(backing, nullValue())

        assertThrows<IllegalArgumentException> {
            LazyMridMap({ backing }, { backing = it }, owner, "A Feeder", validate = { require(false) }).add(Feeder("b"))
        }
        assertThat(backing, nullValue())
    }

    @Test
    internal fun `remove uses mrid identity and nulls the last backing map`() {
        val a = Feeder("a")
        val b = Feeder("b")
        var backing: MutableMap<String, Feeder>? = mutableMapOf("a" to a, "b" to b)
        val map: MridCollection<Feeder> = LazyMridMap(
            { backing }, { backing = it }, Feeder("owner"), "A Feeder"
        )

        assertThat(map.remove(Feeder("a")), equalTo(false))
        assertThat(map.remove(a), equalTo(true))
        assertThat(backing, equalTo(mapOf("b" to b)))
        assertThat(map.remove(b), equalTo(true))
        assertThat(backing, nullValue())
    }

    @Test
    internal fun `remove releases backfill only for an item that was removed`() {
        val owner = Substation("owner")
        val stored = Feeder("a").apply { normalEnergizingSubstation = owner }
        val collision = Feeder("a").apply { normalEnergizingSubstation = owner }
        var backing: MutableMap<String, Feeder>? = mutableMapOf("a" to stored)
        val map: MridCollection<Feeder> = LazyMridMap(
            { backing }, { backing = it }, owner, "A Feeder", backfill()
        )

        assertThat(map.remove(collision), equalTo(false))
        assertThat(collision.normalEnergizingSubstation, sameInstance(owner))
        assertThat(stored.normalEnergizingSubstation, sameInstance(owner))

        assertThat(map.remove(stored), equalTo(true))
        assertThat(stored.normalEnergizingSubstation, nullValue())
    }

    @Test
    internal fun `clear nulls the backing map and representation follows collection values`() {
        val feeder = Feeder("a")
        var backing: MutableMap<String, Feeder>? = mutableMapOf("a" to feeder)
        val map = LazyMridMap({ backing }, { backing = it }, Feeder("owner"), "A Feeder")

        assertThat(map.toString(), equalTo(listOf(feeder).toString()))
        map.clear()

        assertThat(backing, nullValue())
        assertThat(map.toString(), equalTo("[]"))

        backing = mutableMapOf()
        map.clear()
        assertThat(backing, nullValue())
    }

    @Test
    internal fun `clear releases every back reference`() {
        val owner = Substation("owner")
        val a = Feeder("a").apply { normalEnergizingSubstation = owner }
        val b = Feeder("b").apply { normalEnergizingSubstation = owner }
        var backing: MutableMap<String, Feeder>? = mutableMapOf("a" to a, "b" to b)
        val map = LazyMridMap({ backing }, { backing = it }, owner, "A Feeder", backfill())

        map.clear()

        assertThat(backing, nullValue())
        assertThat(a.normalEnergizingSubstation, nullValue())
        assertThat(b.normalEnergizingSubstation, nullValue())
    }
}

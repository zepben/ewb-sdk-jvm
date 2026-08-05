/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.boilerplate

import com.zepben.ewb.boilerplate.collections.AbstractBackedCollection
import com.zepben.ewb.cim.iec61970.base.core.Feeder
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test

internal class AbstractBackedCollectionTest {

    private class TestCollection(
        val backing: MutableList<Feeder> = mutableListOf(),
        private val addAction: ((Feeder) -> Boolean)? = null,
        private val removeAction: ((Feeder) -> Boolean)? = null
    ) : AbstractBackedCollection<Feeder>() {
        override fun getCollection(): Collection<Feeder> = backing
        override fun add(element: Feeder): Boolean = addAction?.invoke(element) ?: backing.add(element)
        override fun remove(element: Feeder): Boolean = removeAction?.invoke(element) ?: backing.remove(element)
        override fun clear() = backing.clear()
    }

    @Test
    internal fun `addAll adds every element in order`() {
        val collection = TestCollection()
        val a = Feeder("a")
        val b = Feeder("b")

        assertThat(collection.addAll(listOf(a, b)), equalTo(true))
        assertThat(collection.toList(), contains(a, b))
    }

    @Test
    internal fun `addAll accepts an empty collection`() {
        val collection = TestCollection()

        assertThat(collection.addAll(emptyList()), equalTo(true))
        assertThat(collection, empty())
    }

    @Test
    internal fun `addAll stops and reports false when an add fails`() {
        val attempted = mutableListOf<Feeder>()
        val collection = TestCollection(addAction = {
            attempted.add(it)
            it.mRID != "b"
        })
        val a = Feeder("a")
        val b = Feeder("b")
        val c = Feeder("c")

        assertThat(collection.addAll(listOf(a, b, c)), equalTo(false))
        assertThat(attempted, contains(a, b))
    }

    @Test
    internal fun `removeAll removes every element in order`() {
        val a = Feeder("a")
        val b = Feeder("b")
        val c = Feeder("c")
        val collection = TestCollection(mutableListOf(a, b, c))

        assertThat(collection.removeAll(listOf(a, c)), equalTo(true))
        assertThat(collection, contains(b))
        assertThat(collection.removeAll(emptyList()), equalTo(true))
    }

    @Test
    internal fun `removeAll stops and reports false when a remove fails`() {
        val attempted = mutableListOf<Feeder>()
        val collection = TestCollection(removeAction = {
            attempted.add(it)
            it.mRID != "b"
        })
        val a = Feeder("a")
        val b = Feeder("b")
        val c = Feeder("c")

        assertThat(collection.removeAll(listOf(a, b, c)), equalTo(false))
        assertThat(attempted, contains(a, b))
    }

    @Test
    internal fun `size and isEmpty reflect the backing collection`() {
        val empty = TestCollection()
        val populated = TestCollection(mutableListOf(Feeder("a"), Feeder("b")))

        assertThat(empty.size, equalTo(0))
        assertThat(empty.isEmpty(), equalTo(true))
        assertThat(populated.size, equalTo(2))
        assertThat(populated.isEmpty(), equalTo(false))
    }

    @Test
    internal fun `iteration preserves backing collection order`() {
        val a = Feeder("a")
        val b = Feeder("b")

        assertThat(TestCollection(mutableListOf(a, b)).toList(), contains(a, b))
        assertThat(TestCollection().toList(), empty())
    }

    @Test
    internal fun `contains and containsAll delegate to the backing collection`() {
        val a = Feeder("a")
        val b = Feeder("b")
        val collection = TestCollection(mutableListOf(a, b))

        assertThat(collection.contains(a), equalTo(true))
        assertThat(collection.contains(Feeder("missing")), equalTo(false))
        assertThat(collection.containsAll(listOf(a, b)), equalTo(true))
        assertThat(collection.containsAll(listOf(a, Feeder("missing"))), equalTo(false))
        assertThat(collection.containsAll(emptyList()), equalTo(true))
    }

    @Test
    internal fun `clear empties the backing collection`() {
        val collection = TestCollection(mutableListOf(Feeder("a")))

        collection.clear()

        assertThat(collection.backing, empty())
        assertThat(collection, empty())
    }
}

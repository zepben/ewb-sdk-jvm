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
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.RegisterExtension

internal class AbstractBackedCollectionTest {

    companion object {
        @JvmField
        @RegisterExtension
        val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()
    }

    private class TestCollection(
        val backing: MutableList<Feeder> = mutableListOf(),
        private val addAction: ((Feeder) -> Boolean)? = null,
        validate: ((Feeder) -> Unit)? = null,
    ) : AbstractBackedCollection<Feeder>(validate) {
        val removed = mutableListOf<Feeder>()
        val presentAfterRemove = mutableListOf<Boolean>()

        override fun getCollection(): MutableCollection<Feeder> = backing
        override fun addRaw(element: Feeder): Boolean = addAction?.invoke(element) ?: backing.add(element)
        override fun postRemove(element: Feeder) {
            removed.add(element)
            presentAfterRemove.add(backing.contains(element))
        }
    }

    @Test
    internal fun `validation runs before storage insertion`() {
        val rejected = Feeder("rejected")
        var insertionAttempted = false
        val collection = TestCollection(
            addAction = {
                insertionAttempted = true
                true
            },
            validate = { require(it !== rejected) },
        )

        assertThrows<IllegalArgumentException> { collection.add(rejected) }

        assertThat(insertionAttempted, equalTo(false))
        assertThat(collection, empty())
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

        assertThat(collection.addAll(emptyList()), equalTo(false))
        assertThat(collection, empty())
    }

    @Test
    internal fun `addAll continues after an add reports false`() {
        val attempted = mutableListOf<Feeder>()
        val collection = TestCollection(addAction = {
            attempted.add(it)
            it.mRID != "b"
        })
        val a = Feeder("a")
        val b = Feeder("b")
        val c = Feeder("c")

        assertThat(collection.addAll(listOf(a, b, c)), equalTo(true))
        assertThat(attempted, contains(a, b, c))
    }

    @Test
    internal fun `removeAll removes every element in order`() {
        val a = Feeder("a")
        val b = Feeder("b")
        val c = Feeder("c")
        val collection = TestCollection(mutableListOf(a, b, c))

        assertThat(collection.removeAll(listOf(a, c)), equalTo(true))
        assertThat(collection, contains(b))
        assertThat(collection.removed, contains(a, c))
        assertThat(collection.presentAfterRemove, contains(false, false))
        assertThat(collection.removeAll(emptyList()), equalTo(false))
    }

    @Test
    internal fun `retainAll removes unmatched elements through the cleanup callback`() {
        val a = Feeder("a")
        val b = Feeder("b")
        val c = Feeder("c")
        val collection = TestCollection(mutableListOf(a, b, c))

        assertThat(collection.retainAll(listOf(b)), equalTo(true))

        assertThat(collection, contains(b))
        assertThat(collection.removed, contains(a, c))
    }

    @Test
    internal fun `failed remove does not invoke removal cleanup`() {
        val collection = TestCollection(mutableListOf(Feeder("a")))

        assertThat(collection.remove(Feeder("missing")), equalTo(false))
        assertThat(collection.removed, empty())
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
        val feeder = Feeder("a")
        val collection = TestCollection(mutableListOf(feeder))

        collection.clear()

        assertThat(collection.backing, empty())
        assertThat(collection, empty())
        assertThat(collection.removed, empty())
    }

    @Test
    internal fun `mutable iterator removes from backing and invokes cleanup`() {
        val a = Feeder("a")
        val b = Feeder("b")
        val collection = TestCollection(mutableListOf(a, b))
        val iterator = collection.iterator()

        assertThat(iterator.next(), sameInstance(a))
        iterator.remove()

        assertThat(collection, contains(b))
        assertThat(collection.removed, contains(a))
        assertThat(collection.presentAfterRemove, contains(false))
    }
}

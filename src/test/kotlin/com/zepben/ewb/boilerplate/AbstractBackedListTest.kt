/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.boilerplate

import com.zepben.ewb.boilerplate.collections.AbstractBackedList
import com.zepben.ewb.cim.iec61970.base.core.Feeder
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.RegisterExtension

internal class AbstractBackedListTest {

    companion object {
        @JvmField
        @RegisterExtension
        val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()
    }

    private class TestList(
        private val backing: MutableList<Feeder>,
        sortBy: ((Feeder) -> Comparable<*>?)? = null,
    ) : AbstractBackedList<Feeder>(sortBy = sortBy) {
        override fun getCollection(): MutableList<Feeder> = backing
        override fun remove(element: Feeder): Boolean = backing.remove(element)
    }

    @Test
    internal fun `successful additions sort the backing list`() {
        val a = Feeder("a")
        val b = Feeder("b")
        val list = TestList(mutableListOf(), Feeder::mRID)

        list.add(b)
        list.add(a)

        assertThat(list, contains(a, b))
    }

    @Suppress("KotlinConstantConditions")
    @Test
    internal fun `get returns item at index and rejects an out of range index`() {
        val a = Feeder("a")
        val b = Feeder("b")
        val list = TestList(mutableListOf(a, b))

        assertThat(list[1], sameInstance(b))
        assertThrows<IndexOutOfBoundsException> { list[-1] }
        assertThrows<IndexOutOfBoundsException> { list[2] }
    }

    @Test
    internal fun `index lookup delegates to the backing list`() {
        val a = Feeder("a")
        val b = Feeder("b")
        val list = TestList(mutableListOf(a, b, a))

        assertThat(list.indexOf(a), equalTo(0))
        assertThat(list.lastIndexOf(a), equalTo(2))
        assertThat(list.indexOf(Feeder("missing")), equalTo(-1))
    }

    @Test
    internal fun `list iterators expose the backing list in both directions`() {
        val a = Feeder("a")
        val b = Feeder("b")
        val list = TestList(mutableListOf(a, b))

        assertThat(list.listIterator().asSequence().toList(), contains(a, b))

        val iterator = list.listIterator(1)
        assertThat(iterator.hasPrevious(), equalTo(true))
        assertThat(iterator.previous(), sameInstance(a))
        assertThat(iterator.next(), sameInstance(a))
        assertThat(iterator.next(), sameInstance(b))
    }

    @Test
    internal fun `subList returns the requested section of the backing list`() {
        val a = Feeder("a")
        val b = Feeder("b")
        val c = Feeder("c")
        val d = Feeder("d")
        val list = TestList(mutableListOf(a, b, c, d))

        assertThat(list.subList(1, 3), contains(b, c))
        assertThat(list.subList(2, 2), empty())
    }
}

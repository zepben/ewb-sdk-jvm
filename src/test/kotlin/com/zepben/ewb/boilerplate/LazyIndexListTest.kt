/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.boilerplate

import com.zepben.ewb.boilerplate.collections.LazyIndexList
import com.zepben.ewb.cim.iec61970.base.core.Feeder
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.RegisterExtension

internal class LazyIndexListTest {

    companion object {
        @JvmField
        @RegisterExtension
        val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()
    }

    @Test
    internal fun `add to an empty list creates the backing list`() {
        var backing: MutableList<Feeder>? = null
        val list = LazyIndexList({ backing }, { backing = it }, Feeder("owner"), "a Feeder")
        val feeder = Feeder("a")

        list.add(0, feeder)

        assertThat(backing, contains(feeder))
    }

    @Test
    internal fun `add places an item at the requested index`() {
        val a = Feeder("a")
        val c = Feeder("c")
        val b = Feeder("b")
        var backing: MutableList<Feeder>? = mutableListOf(a, c)
        val list = LazyIndexList({ backing }, { backing = it }, Feeder("owner"), "a Feeder")

        list.add(1, b)

        assertThat(backing, contains(a, b, c))
    }

    @Test
    internal fun `add rejects indexes outside the valid insertion range without creating backing`() {
        var backing: MutableList<Feeder>? = null
        val list = LazyIndexList({ backing }, { backing = it }, Feeder("owner"), "a Feeder")

        assertThrows<IllegalArgumentException> { list.add(-1, Feeder("a")) }
        assertThrows<IllegalArgumentException> { list.add(1, Feeder("a")) }
        assertThat(backing, nullValue())
    }

    @Test
    internal fun `add without an index appends at the end`() {
        val a = Feeder("a")
        val b = Feeder("b")
        var backing: MutableList<Feeder>? = mutableListOf(a)
        val list = LazyIndexList({ backing }, { backing = it }, Feeder("owner"), "a Feeder")

        assertThat(list.add(b), equalTo(true))

        assertThat(backing, contains(a, b))
    }

    @Test
    internal fun `removeAt returns and removes the requested item`() {
        val a = Feeder("a")
        val b = Feeder("b")
        var backing: MutableList<Feeder>? = mutableListOf(a, b)
        val list = LazyIndexList({ backing }, { backing = it }, Feeder("owner"), "a Feeder")

        assertThat(list.removeAt(0), sameInstance(a))
        assertThat(backing, contains(b))
    }

    @Test
    internal fun `removeAt rejects an invalid index`() {
        var backing: MutableList<Feeder>? = null
        val list = LazyIndexList({ backing }, { backing = it }, Feeder("owner"), "a Feeder")

        assertThrows<IndexOutOfBoundsException> { list.removeAt(0) }
        assertThrows<IndexOutOfBoundsException> { list.removeAt(-1) }
        assertThat(list.removeAtOrNull(0), nullValue())
        assertThat(list.removeAtOrNull(-1), nullValue())

        backing = mutableListOf(Feeder("a"))
        assertThrows<IndexOutOfBoundsException> { list.removeAt(1) }
        assertThat(list.removeAtOrNull(1), nullValue())
    }

    @Test
    internal fun `removeAt nulls the backing list after removing its last item`() {
        val feeder = Feeder("a")
        var backing: MutableList<Feeder>? = mutableListOf(feeder)
        val list = LazyIndexList({ backing }, { backing = it }, Feeder("owner"), "a Feeder")

        assertThat(list.removeAt(0), sameInstance(feeder))
        assertThat(backing, nullValue())
    }

    @Test
    internal fun `indexed addAll mutates the backing list`() {
        val a = Feeder("a")
        val c = Feeder("c")
        var backing: MutableList<Feeder>? = null
        val list = LazyIndexList({ backing }, { backing = it }, Feeder("owner"), "a Feeder")

        assertThat(list.addAll(0, listOf(a, c)), equalTo(true))

        assertThat(backing, contains(a, c))
    }

    @Test
    internal fun `exposes a read only list iterator`() {
        val a = Feeder("a")
        var backing: MutableList<Feeder>? = mutableListOf(a)
        val list = LazyIndexList({ backing }, { backing = it }, Feeder("owner"), "a Feeder")
        val iterator: ListIterator<Feeder> = list.listIterator()

        assertThat(iterator.next(), sameInstance(a))
    }

    @Test
    internal fun `subList returns a read only view of the backing list`() {
        val a = Feeder("a")
        val b = Feeder("b")
        val replacement = Feeder("replacement")
        var backing: MutableList<Feeder>? = mutableListOf(a, b)
        val list = LazyIndexList({ backing }, { backing = it }, Feeder("owner"), "a Feeder")
        val subList: List<Feeder> = list.subList(0, 1)

        assertThat(subList, contains(a))

        backing!![0] = replacement

        assertThat(subList, contains(replacement))
    }
}

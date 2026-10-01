/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.boilerplate

import com.zepben.ewb.boilerplate.collections.LazyList
import com.zepben.ewb.cim.iec61970.base.core.Feeder
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.RegisterExtension

internal class LazyListTest {

    companion object {
        @JvmField
        @RegisterExtension
        val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()
    }

    @Test
    internal fun `null backing is exposed as an empty list without being created`() {
        var backing: MutableList<Feeder>? = null
        val list = LazyList({ backing }, { backing = it })

        assertThat(list.size, equalTo(0))
        assertThat(list.isEmpty(), equalTo(true))
        assertThat(list.toList(), empty())
        assertThat(backing, nullValue())
    }

    @Test
    internal fun `add creates a missing backing list and appends to an existing list`() {
        var backing: MutableList<Feeder>? = null
        val list = LazyList({ backing }, { backing = it })
        val a = Feeder("a")
        val b = Feeder("b")

        assertThat(list.add(a), equalTo(true))
        assertThat(list.add(b), equalTo(true))

        assertThat(backing, contains(a, b))
    }

    @Test
    internal fun `validation runs before the backing list is mutated`() {
        var backing: MutableList<Feeder>? = null
        val rejected = Feeder("rejected")
        var validationCalls = 0
        val list = LazyList({ backing }, { backing = it }, validate = {
            ++validationCalls
            require(it !== rejected)
        })

        assertThrows<IllegalArgumentException> { list.add(rejected) }
        assertThat(backing, nullValue())

        val accepted = Feeder("accepted")
        backing = mutableListOf(accepted)
        assertThrows<IllegalArgumentException> { list.add(rejected) }
        assertThat(backing, contains(accepted))
        assertThat(validationCalls, equalTo(2))
    }

    @Test
    internal fun `successful additions sort the backing list`() {
        var backing: MutableList<Feeder>? = null
        val list = LazyList({ backing }, { backing = it }, sortBy = Feeder::mRID)
        val a = Feeder("a")
        val b = Feeder("b")

        list.add(b)
        list.add(a)

        assertThat(backing, contains(a, b))
    }

    @Test
    internal fun `remove mutates the backing list and nulls it after the last item`() {
        val a = Feeder("a")
        val b = Feeder("b")
        var backing: MutableList<Feeder>? = mutableListOf(a, b)
        val list = LazyList({ backing }, { backing = it })

        assertThat(list.remove(a), equalTo(true))
        assertThat(backing, contains(b))
        assertThat(list.remove(b), equalTo(true))
        assertThat(backing, nullValue())
    }

    @Test
    internal fun `remove returns false without changing null or populated backing`() {
        var backing: MutableList<Feeder>? = null
        val list = LazyList({ backing }, { backing = it })

        assertThat(list.remove(Feeder("missing")), equalTo(false))
        assertThat(backing, nullValue())

        val existing = Feeder("existing")
        backing = mutableListOf(existing)
        assertThat(list.remove(Feeder("missing")), equalTo(false))
        assertThat(backing, contains(existing))
    }

    @Test
    internal fun `clear nulls the backing list and string representation matches a list`() {
        val feeder = Feeder("a")
        var backing: MutableList<Feeder>? = mutableListOf(feeder)
        val list = LazyList({ backing }, { backing = it })

        assertThat(list.toString(), equalTo(listOf(feeder).toString()))
        list.clear()

        assertThat(backing, nullValue())
        assertThat(list.toString(), equalTo("[]"))

        backing = mutableListOf()
        list.clear()
        assertThat(backing, nullValue())
    }
}

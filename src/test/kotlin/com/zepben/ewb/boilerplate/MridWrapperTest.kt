/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.boilerplate


import com.zepben.ewb.cim.iec61970.base.wires.Clamp
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.RegisterExtension

internal class MridWrapperTest {

    companion object {
        @JvmField
        @RegisterExtension
        val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()
    }

    @Test
    fun `nullable list wrapper starts empty when backing list is null`() {
        var backing: MutableList<Clamp>? = null

        val wrapper = LazyMridList(
            getter = { backing },
            setter = { backing = it },
            owner = Clamp("owner"),
            elementDescription = "A Clamp"
        )

        assertThat(wrapper.size, `is`(0))
        assertThat(wrapper.isEmpty(), `is`(true))
        assertThat(wrapper.getByMrid("a"), nullValue())
        assertThat(wrapper.toList(), empty())
        assertThat(backing, nullValue())
    }

    @Test
    fun `nullable list wrapper add creates backing list and stores element`() {
        var backing: MutableList<Clamp>? = null

        val wrapper = LazyMridList(
            getter = { backing },
            setter = { backing = it },
            owner = Clamp("owner"),
            elementDescription = "A Clamp"
        )

        val clamp = Clamp("a")

        assertThat(wrapper.add(clamp), `is`(true))

        assertThat(backing!!.toList(), contains(clamp))
        assertThat(wrapper.size, `is`(1))
        assertThat(wrapper.getByMrid("a"), sameInstance(clamp))
        assertThat(wrapper.toList(), contains(clamp))
    }

    @Test
    fun `nullable list wrapper adding same instance twice is ignored`() {
        var backing: MutableList<Clamp>? = null

        val wrapper = LazyMridList(
            getter = { backing },
            setter = { backing = it },
            owner = Clamp("owner"),
            elementDescription = "A Clamp"
        )

        val clamp = Clamp("a")

        assertThat(wrapper.add(clamp), `is`(true))
        assertThat(wrapper.add(clamp), `is`(false))

        assertThat(backing!!.toList(), contains(clamp))
        assertThat(wrapper.size, `is`(1))
        assertThat(wrapper.getByMrid("a"), sameInstance(clamp))
    }

    @Test
    fun `nullable list wrapper rejects mrid collision before mutating backing list`() {
        var backing: MutableList<Clamp>? = null

        val wrapper = LazyMridList(
            getter = { backing },
            setter = { backing = it },
            owner = Clamp("owner"),
            elementDescription = "A Clamp"
        )

        val existing = Clamp("a")
        val collision = Clamp("a")

        assertThat(wrapper.add(existing), `is`(true))

        assertThrows<IllegalArgumentException> {
            wrapper.add(collision)
        }

        assertThat(backing!!.toList(), contains(existing))
        assertThat(wrapper.size, `is`(1))
        assertThat(wrapper.getByMrid("a"), sameInstance(existing))
    }

    @Test
    fun `nullable list wrapper validation runs before backing list mutation`() {
        var backing: MutableList<Clamp>? = null
        val rejected = Clamp("a")

        val wrapper = LazyMridList(
            getter = { backing },
            setter = { backing = it },
            owner = Clamp("owner"),
            elementDescription = "A Clamp",
            validate = {
                if (it === rejected)
                    error("blocked")
            }
        )

        assertThrows<IllegalStateException> {
            wrapper.add(rejected)
        }

        assertThat(backing, nullValue())
        assertThat(wrapper.size, `is`(0))
        assertThat(wrapper.getByMrid("a"), nullValue())
    }

    @Test
    fun `nullable list wrapper sorts after successful addition`() {
        var backing: MutableList<Clamp>? = null

        val wrapper = LazyMridList(
            getter = { backing },
            setter = { backing = it },
            owner = Clamp("owner"),
            elementDescription = "A Clamp",
            sortBy = { it.mRID }
        )

        val b = Clamp("b")
        val a = Clamp("a")
        val c = Clamp("c")

        assertThat(wrapper.add(b), `is`(true))
        assertThat(wrapper.add(a), `is`(true))
        assertThat(wrapper.add(c), `is`(true))

        assertThat(backing!!.toList(), contains(a, b, c))
        assertThat(wrapper.toList(), contains(a, b, c))
    }

    @Test
    fun `nullable list wrapper remove clears backing list when last element is removed`() {
        val clamp = Clamp("a")
        var backing: MutableList<Clamp>? = mutableListOf(clamp)

        val wrapper = LazyMridList(
            getter = { backing },
            setter = { backing = it },
            owner = Clamp("owner"),
            elementDescription = "A Clamp"
        )

        assertThat(wrapper.remove(clamp), `is`(true))

        assertThat(backing, nullValue())
        assertThat(wrapper.size, `is`(0))
    }

    @Test
    fun `nullable list wrapper remove mutates backing list when elements remain`() {
        val a = Clamp("a")
        val b = Clamp("b")
        var backing: MutableList<Clamp>? = mutableListOf(a, b)

        val wrapper = LazyMridList(
            getter = { backing },
            setter = { backing = it },
            owner = Clamp("owner"),
            elementDescription = "A Clamp"
        )

        assertThat(wrapper.remove(a), `is`(true))

        assertThat(backing!!.toList(), contains(b))
        assertThat(wrapper.size, `is`(1))
        assertThat(wrapper.getByMrid("a"), nullValue())
        assertThat(wrapper.getByMrid("b"), sameInstance(b))
    }

    @Test
    fun `nullable list wrapper iterator remove clears backing list when last element is removed`() {
        val clamp = Clamp("a")
        var backing: MutableList<Clamp>? = mutableListOf(clamp)

        val wrapper = LazyMridList(
            getter = { backing },
            setter = { backing = it },
            owner = Clamp("owner"),
            elementDescription = "A Clamp"
        )

        val iterator = wrapper.iterator()

        assertThat(iterator.hasNext(), `is`(true))
        assertThat(iterator.next(), sameInstance(clamp))

        iterator.remove()

        assertThat(backing, nullValue())
        assertThat(wrapper.size, `is`(0))
    }

    @Test
    fun `nullable list wrapper clear sets backing list to null`() {
        var backing: MutableList<Clamp>? = mutableListOf(Clamp("a"))

        val wrapper = LazyMridList(
            getter = { backing },
            setter = { backing = it },
            owner = Clamp("owner"),
            elementDescription = "A Clamp"
        )

        wrapper.clear()

        assertThat(backing, nullValue())
        assertThat(wrapper.size, `is`(0))
    }

    @Test
    fun `dict wrapper starts empty when backing map is null`() {
        var backing: MutableMap<String, Clamp>? = null

        val wrapper = LazyMridMap<Clamp>(
            getter = { backing },
            setter = { backing = it },
            owner = Clamp("owner"),
            elementDescription = "A Clamp"
        )

        assertThat(wrapper.size, `is`(0))
        assertThat(wrapper.isEmpty(), `is`(true))
        assertThat(wrapper.getByMrid("a"), nullValue())
        assertThat(wrapper.toList(), empty())
        assertThat(backing, nullValue())
    }

    @Test
    fun `dict wrapper add creates backing map and stores element by mrid`() {
        var backing: MutableMap<String, Clamp>? = null

        val wrapper = LazyMridMap<Clamp>(
            getter = { backing },
            setter = { backing = it },
            owner = Clamp("owner"),
            elementDescription = "A Clamp"
        )

        val clamp = Clamp("a")

        assertThat(wrapper.add(clamp), `is`(true))

        assertThat(backing, `is`(mapOf("a" to clamp)))
        assertThat(wrapper.size, `is`(1))
        assertThat(wrapper.getByMrid("a"), sameInstance(clamp))
        assertThat(wrapper.toList(), contains(clamp))
    }

    @Test
    fun `dict wrapper adding same instance twice is ignored`() {
        var backing: MutableMap<String, Clamp>? = null

        val wrapper = LazyMridMap<Clamp>(
            getter = { backing },
            setter = { backing = it },
            owner = Clamp("owner"),
            elementDescription = "A Clamp"
        )

        val clamp = Clamp("a")

        assertThat(wrapper.add(clamp), `is`(true))
        assertThat(wrapper.add(clamp), `is`(false))

        assertThat(backing, `is`(mapOf("a" to clamp)))
        assertThat(wrapper.size, `is`(1))
    }

    @Test
    fun `dict wrapper rejects mrid collision before mutating backing map`() {
        var backing: MutableMap<String, Clamp>? = null

        val wrapper = LazyMridMap<Clamp>(
            getter = { backing },
            setter = { backing = it },
            owner = Clamp("owner"),
            elementDescription = "A Clamp"
        )

        val existing = Clamp("a")
        val collision = Clamp("a")

        assertThat(wrapper.add(existing), `is`(true))

        assertThrows<IllegalArgumentException> {
            wrapper.add(collision)
        }

        assertThat(backing, `is`(mapOf("a" to existing)))
        assertThat(wrapper.size, `is`(1))
        assertThat(wrapper.getByMrid("a"), sameInstance(existing))
    }

    @Test
    fun `dict wrapper validation runs before backing map mutation`() {
        var backing: MutableMap<String, Clamp>? = null
        val rejected = Clamp("a")

        val wrapper = LazyMridMap<Clamp>(
            getter = { backing },
            setter = { backing = it },
            owner = Clamp("owner"),
            elementDescription = "A Clamp",
            validate = {
                if (it === rejected)
                    error("blocked")
            }
        )

        assertThrows<IllegalStateException> {
            wrapper.add(rejected)
        }

        assertThat(backing, nullValue())
        assertThat(wrapper.size, `is`(0))
        assertThat(wrapper.getByMrid("a"), nullValue())
    }

    @Test
    fun `dict wrapper remove clears nullable backing map when last element is removed`() {
        val clamp = Clamp("a")
        var backing: MutableMap<String, Clamp>? = hashMapOf("a" to clamp)

        val wrapper = LazyMridMap<Clamp>(
            getter = { backing },
            setter = { backing = it },
            owner = Clamp("owner"),
            elementDescription = "A Clamp"
        )

        assertThat(wrapper.remove(clamp), `is`(true))

        assertThat(backing, nullValue())
        assertThat(wrapper.size, `is`(0))
    }

    @Test
    fun `dict wrapper iterator remove clears nullable backing map when last element is removed`() {
        val clamp = Clamp("a")
        var backing: MutableMap<String, Clamp>? = hashMapOf("a" to clamp)

        val wrapper = LazyMridMap<Clamp>(
            getter = { backing },
            setter = { backing = it },
            owner = Clamp("owner"),
            elementDescription = "A Clamp"
        )

        val iterator = wrapper.iterator()

        assertThat(iterator.hasNext(), `is`(true))
        assertThat(iterator.next(), sameInstance(clamp))

        iterator.remove()

        assertThat(backing, nullValue())
        assertThat(wrapper.size, `is`(0))
    }

    @Test
    fun `dict wrapper clear sets nullable backing map to null`() {
        var backing: MutableMap<String, Clamp>? = hashMapOf("a" to Clamp("a"))

        val wrapper = LazyMridMap<Clamp>(
            getter = { backing },
            setter = { backing = it },
            owner = Clamp("owner"),
            elementDescription = "A Clamp"
        )

        wrapper.clear()

        assertThat(backing, nullValue())
        assertThat(wrapper.size, `is`(0))
    }

    @Test
    fun `not null list wrapper starts empty without external backing field`() {
        val wrapper = MridList<Clamp>(
            owner = Clamp("owner"),
            elementDescription = "A Clamp"
        )

        assertThat(wrapper.size, `is`(0))
        assertThat(wrapper.isEmpty(), `is`(true))
        assertThat(wrapper.getByMrid("a"), nullValue())
        assertThat(wrapper.toList(), empty())
    }

    @Test
    fun `not null list wrapper add stores element in internal list`() {
        val wrapper = MridList<Clamp>(
            owner = Clamp("owner"),
            elementDescription = "A Clamp"
        )

        val clamp = Clamp("a")

        assertThat(wrapper.add(clamp), `is`(true))

        assertThat(wrapper.size, `is`(1))
        assertThat(wrapper.getByMrid("a"), sameInstance(clamp))
        assertThat(wrapper.toList(), contains(clamp))
    }

    @Test
    fun `not null list wrapper adding same instance twice is ignored`() {
        val wrapper = MridList<Clamp>(
            owner = Clamp("owner"),
            elementDescription = "A Clamp"
        )

        val clamp = Clamp("a")

        assertThat(wrapper.add(clamp), `is`(true))
        assertThat(wrapper.add(clamp), `is`(false))

        assertThat(wrapper.size, `is`(1))
        assertThat(wrapper.getByMrid("a"), sameInstance(clamp))
        assertThat(wrapper.toList(), contains(clamp))
    }

    @Test
    fun `not null list wrapper rejects mrid collision before mutating internal list`() {
        val wrapper = MridList<Clamp>(
            owner = Clamp("owner"),
            elementDescription = "A Clamp"
        )

        val existing = Clamp("a")
        val collision = Clamp("a")

        assertThat(wrapper.add(existing), `is`(true))

        assertThrows<IllegalArgumentException> {
            wrapper.add(collision)
        }

        assertThat(wrapper.size, `is`(1))
        assertThat(wrapper.getByMrid("a"), sameInstance(existing))
        assertThat(wrapper.toList(), contains(existing))
    }

    @Test
    fun `not null list wrapper validation runs before internal list mutation`() {
        val rejected = Clamp("a")

        val wrapper = MridList<Clamp>(
            owner = Clamp("owner"),
            elementDescription = "A Clamp",
            validate = {
                if (it === rejected)
                    error("blocked")
            }
        )

        assertThrows<IllegalStateException> {
            wrapper.add(rejected)
        }

        assertThat(wrapper.size, `is`(0))
        assertThat(wrapper.getByMrid("a"), nullValue())
        assertThat(wrapper.toList(), empty())
    }

    @Test
    fun `not null list wrapper sorts after successful addition`() {
        val wrapper = MridList<Clamp>(
            owner = Clamp("owner"),
            elementDescription = "A Clamp",
            sortBy = { it.mRID }
        )

        val b = Clamp("b")
        val a = Clamp("a")
        val c = Clamp("c")

        assertThat(wrapper.add(b), `is`(true))
        assertThat(wrapper.add(a), `is`(true))
        assertThat(wrapper.add(c), `is`(true))

        assertThat(wrapper.toList(), contains(a, b, c))
    }

    @Test
    fun `not null list wrapper remove mutates internal list`() {
        val wrapper = MridList<Clamp>(
            owner = Clamp("owner"),
            elementDescription = "A Clamp"
        )

        val a = Clamp("a")
        val b = Clamp("b")

        wrapper.add(a)
        wrapper.add(b)

        assertThat(wrapper.remove(a), `is`(true))

        assertThat(wrapper.size, `is`(1))
        assertThat(wrapper.getByMrid("a"), nullValue())
        assertThat(wrapper.getByMrid("b"), sameInstance(b))
        assertThat(wrapper.toList(), contains(b))
    }

    @Test
    fun `not null list wrapper clear mutates internal list`() {
        val wrapper = MridList<Clamp>(
            owner = Clamp("owner"),
            elementDescription = "A Clamp"
        )

        wrapper.add(Clamp("a"))
        wrapper.add(Clamp("b"))

        wrapper.clear()

        assertThat(wrapper.size, `is`(0))
        assertThat(wrapper.toList(), empty())
        assertThat(wrapper.getByMrid("a"), nullValue())
        assertThat(wrapper.getByMrid("b"), nullValue())
    }
}

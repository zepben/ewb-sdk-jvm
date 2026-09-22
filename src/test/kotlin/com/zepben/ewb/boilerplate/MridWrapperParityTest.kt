/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.boilerplate

import com.zepben.testutils.junit.SystemLogExtension
import org.junit.jupiter.api.extension.RegisterExtension

internal class MridWrapperParityTest {

    companion object {
        @JvmField
        @RegisterExtension
        val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()
    }

//    private data class WrapperCase(
//        val name: String,
//        val ordered: Boolean = true,
//        val collection: MutableCollection<Clamp>,
//        val getByMrid: (String) -> Clamp?
//    )
//
//    private fun wrappers(
//        validate: ((Clamp) -> Unit)? = null,
//        sortBy: ((Clamp) -> Comparable<*>?)? = null
//    ): List<WrapperCase> {
//        var nullableListBacking: MutableList<Clamp>? = null
//        var nullableMapBacking: MutableMap<String, Clamp>? = null
//
//        val nullableListWrapper = LazyMridList(
//            getter = { nullableListBacking },
//            setter = { nullableListBacking = it },
//            owner = Clamp("owner"),
//            elementDescription = "A Clamp",
//            validate = validate,
//            sortBy = sortBy
//        )
//
//        val nullableDictWrapper = LazyMridMap(
//            getter = { nullableMapBacking },
//            setter = { nullableMapBacking = it },
//            owner = Clamp("owner"),
//            elementDescription = "A Clamp",
//            validate = validate
//        )
//
//        val notNullListWrapper = RefMridList(
//            owner = Clamp("owner"),
//            elementDescription = "A Clamp",
//            validate = validate,
//            sortBy = sortBy
//        )
//
//        return listOf(
//            WrapperCase(
//                name = "nullable list",
//                collection = nullableListWrapper,
//                getByMrid = nullableListWrapper::getByMrid
//            ),
//            WrapperCase(
//                name = "nullable dict",
//                ordered = false,
//                collection = nullableDictWrapper,
//                getByMrid = nullableDictWrapper::getByMrid
//            ),
//            WrapperCase(
//                name = "not-null list",
//                collection = notNullListWrapper,
//                getByMrid = notNullListWrapper::getByMrid
//            )
//        )
//    }
//
//    @Test
//    fun `all wrappers expose the same empty collection behaviour`() {
//        for (case in wrappers()) {
//            assertThat(case.name, case.collection.size, `is`(0))
//            assertThat(case.name, case.collection.isEmpty(), `is`(true))
//            assertThat(case.name, case.collection.toList(), empty())
//            assertThat(case.name, case.getByMrid("a"), nullValue())
//        }
//    }
//
//    @Test
//    fun `all wrappers expose the same add and getByMrid behaviour`() {
//        val a = Clamp("a")
//        val b = Clamp("b")
//
//        for (case in wrappers()) {
//            assertThat(case.name, case.collection.add(a), `is`(true))
//            assertThat(case.name, case.collection.add(b), `is`(true))
//
//            assertThat(case.name, case.collection.size, `is`(2))
//            assertThat(case.name, case.collection.isEmpty(), `is`(false))
//            assertThat(case.name, case.getByMrid("a"), sameInstance(a))
//            assertThat(case.name, case.getByMrid("b"), sameInstance(b))
//            assertThat(case.name, case.getByMrid("c"), nullValue())
//
//            if (case.ordered)
//                assertThat(case.name, case.collection.toList(), contains(a, b))
//            else
//                assertThat(case.name, case.collection.toList(), containsInAnyOrder(a, b))
//        }
//    }
//
//    @Test
//    fun `all wrappers ignore adding the same instance twice`() {
//        val a = Clamp("a")
//
//        for (case in wrappers()) {
//            assertThat(case.name, case.collection.add(a), `is`(true))
//            assertThat(case.name, case.collection.add(a), `is`(false))
//
//            assertThat(case.name, case.collection.size, `is`(1))
//            assertThat(case.name, case.getByMrid("a"), sameInstance(a))
//            assertThat(case.name, case.collection.toList(), contains(a))
//        }
//    }
//
//    @Test
//    fun `all wrappers reject mrid collision with the same user-facing state`() {
//        val existing = Clamp("a")
//        val collision = Clamp("a")
//
//        for (case in wrappers()) {
//            assertThat(case.name, case.collection.add(existing), `is`(true))
//
//            val exception = assertThrows<IllegalArgumentException>(case.name) {
//                case.collection.add(collision)
//            }
//
//            assertThat(
//                case.name,
//                exception.message,
//                `is`("A Clamp with mRID a already exists in Clamp owner.")
//            )
//
//            assertThat(case.name, case.collection.size, `is`(1))
//            assertThat(case.name, case.getByMrid("a"), sameInstance(existing))
//            assertThat(case.name, case.collection.toList(), contains(existing))
//        }
//    }
//
//    @Test
//    fun `all wrappers run validation before adding the element`() {
//        val rejected = Clamp("a")
//
//        for (case in wrappers(
//            validate = {
//                if (it === rejected)
//                    error("blocked")
//            }
//        )) {
//            val exception = assertThrows<IllegalStateException>(case.name) {
//                case.collection.add(rejected)
//            }
//
//            assertThat(case.name, exception.message, `is`("blocked"))
//            assertThat(case.name, case.collection.size, `is`(0))
//            assertThat(case.name, case.collection.isEmpty(), `is`(true))
//            assertThat(case.name, case.getByMrid("a"), nullValue())
//            assertThat(case.name, case.collection.toList(), empty())
//        }
//    }
//
//    @Test
//    fun `all wrappers expose the same remove behaviour`() {
//        val a = Clamp("a")
//        val b = Clamp("b")
//        val missing = Clamp("missing")
//
//        for (case in wrappers()) {
//            case.collection.add(a)
//            case.collection.add(b)
//
//            assertThat(case.name, case.collection.remove(missing), `is`(false))
//            assertThat(case.name, case.collection.remove(a), `is`(true))
//
//            assertThat(case.name, case.collection.size, `is`(1))
//            assertThat(case.name, case.getByMrid("a"), nullValue())
//            assertThat(case.name, case.getByMrid("b"), sameInstance(b))
//            assertThat(case.name, case.collection.toList(), contains(b))
//
//            assertThat(case.name, case.collection.remove(b), `is`(true))
//            assertThat(case.name, case.collection.size, `is`(0))
//            assertThat(case.name, case.collection.isEmpty(), `is`(true))
//            assertThat(case.name, case.getByMrid("b"), nullValue())
//            assertThat(case.name, case.collection.toList(), empty())
//        }
//    }
//
//    @Test
//    fun `all wrappers expose the same iterator remove behaviour`() {
//        val a = Clamp("a")
//        val b = Clamp("b")
//
//        for (case in wrappers()) {
//            case.collection.add(a)
//            case.collection.add(b)
//
//            val iterator = case.collection.iterator()
//            val first = iterator.next()
//
//            iterator.remove()
//
//            assertThat(case.name, case.collection.size, `is`(1))
//            assertThat(case.name, case.getByMrid(first.mRID), nullValue())
//
//            val remaining = if (first === a) b else a
//            assertThat(case.name, case.getByMrid(remaining.mRID), sameInstance(remaining))
//            assertThat(case.name, case.collection.toList(), contains(remaining))
//        }
//    }
//
//    @Test
//    fun `all wrappers expose the same clear behaviour`() {
//        for (case in wrappers()) {
//            case.collection.add(Clamp("a"))
//            case.collection.add(Clamp("b"))
//
//            case.collection.clear()
//
//            assertThat(case.name, case.collection.size, `is`(0))
//            assertThat(case.name, case.collection.isEmpty(), `is`(true))
//            assertThat(case.name, case.getByMrid("a"), nullValue())
//            assertThat(case.name, case.getByMrid("b"), nullValue())
//            assertThat(case.name, case.collection.toList(), empty())
//        }
//    }
//
//    @Test
//    fun `list wrappers expose matching sorted order while dict wrapper exposes matching membership`() {
//        val b = Clamp("b")
//        val a = Clamp("a")
//        val c = Clamp("c")
//
//        for (case in wrappers(sortBy = { it.mRID })) {
//            case.collection.add(b)
//            case.collection.add(a)
//            case.collection.add(c)
//
//            if (case.ordered)
//                assertThat(case.name, case.collection.toList(), contains(a, b, c))
//            else
////                assertThat(case.name, case.collection.toList(), containsInAnyOrder(a, b, c))
////
////            assertThat(case.name, case.getByMrid("a"), sameInstance(a))
////            assertThat(case.name, case.getByMrid("b"), sameInstance(b))
////            assertThat(case.name, case.getByMrid("c"), sameInstance(c))
////        }
////    }
}
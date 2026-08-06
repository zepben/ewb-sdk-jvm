/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.boilerplate

import com.zepben.ewb.boilerplate.collections.LazyMridList
import com.zepben.ewb.boilerplate.collections.LazyMridMap
import com.zepben.ewb.boilerplate.collections.MridCollection
import com.zepben.ewb.boilerplate.collections.MridList
import com.zepben.ewb.cim.iec61970.base.core.Feeder
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.RegisterExtension

internal class MridCollectionsParityTest {

    companion object {
        @JvmField
        @RegisterExtension
        val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()
    }

    private fun collections(): List<MridCollection<Feeder>> {
        var listBacking: MutableList<Feeder>? = null
        var mapBacking: MutableMap<String, Feeder>? = null
        return listOf(
            LazyMridList({ listBacking }, { listBacking = it }, Feeder("list-owner"), "A Feeder"),
            LazyMridMap({ mapBacking }, { mapBacking = it }, Feeder("map-owner"), "A Feeder"),
            MridList(owner = Feeder("eager-owner"), elementDescription = "A Feeder")
        )
    }

    @Test
    internal fun `mrid collections have the same public effects`() {
        collections().forEach { collection ->
            val a = Feeder("a")
            val b = Feeder("b")

            assertThat(collection.isEmpty(), equalTo(true))
            assertThat(collection.add(a), equalTo(true))
            assertThat(collection.add(b), equalTo(true))
            assertThat(collection.add(a), equalTo(false))
            assertThat(collection.getByMrid("a"), sameInstance(a))
            assertThat(collection["a"], sameInstance(a))
            assertThat(collection.getByMrid("missing"), nullValue())
            assertThat(collection["missing"], nullValue())
            assertThrows<IllegalArgumentException> { collection.add(Feeder("a")) }
            assertThat(collection.size, equalTo(2))

            assertThat(collection.remove(a), equalTo(true))
            assertThat(collection.getByMrid("a"), nullValue())
            collection.clear()
            assertThat(collection, empty())
        }
    }
}

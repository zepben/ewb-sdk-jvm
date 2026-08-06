/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.boilerplate

import com.zepben.ewb.boilerplate.collections.MridCollection
import com.zepben.ewb.cim.iec61970.base.core.Feeder
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.RegisterExtension

internal class MridCollectionTest {

    companion object {
        @JvmField
        @RegisterExtension
        val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()
    }

    private class TestMridCollection : MridCollection<Feeder>() {
        private val backing = mutableListOf<Feeder>()

        override val owner = Feeder("owner")
        override val elementDescription = "A Feeder"
        override fun getCollection(): MutableCollection<Feeder> = backing
        override fun getByMrid(mRID: String): Feeder? = backing.firstOrNull { it.mRID == mRID }
        override fun add(element: Feeder): Boolean = if (canAddByMrid(element)) backing.add(element) else false
    }

    @Test
    internal fun `getByMrid returns the matching item and null when missing`() {
        val feeder = Feeder("a")
        val collection = TestMridCollection().apply { add(feeder) }

        assertThat(collection.getByMrid("a"), sameInstance(feeder))
        assertThat(collection.getByMrid("missing"), nullValue())
    }

    @Test
    internal fun `add accepts a new mrid and ignores the same instance`() {
        val feeder = Feeder("a")
        val collection = TestMridCollection()

        assertThat(collection.add(feeder), equalTo(true))
        assertThat(collection.add(feeder), equalTo(false))
        assertThat(collection.size, equalTo(1))
    }

    @Test
    internal fun `add rejects a different instance with the same mrid`() {
        val existing = Feeder("a")
        val collection = TestMridCollection().apply { add(existing) }

        val exception = assertThrows<IllegalArgumentException> { collection.add(Feeder("a")) }

        assertThat(exception.message, equalTo("A Feeder with mRID a already exists in Feeder owner."))
        assertThat(collection.single(), sameInstance(existing))
    }
}

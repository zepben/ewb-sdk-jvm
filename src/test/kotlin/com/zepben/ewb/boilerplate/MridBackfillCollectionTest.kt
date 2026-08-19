/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.boilerplate

import com.zepben.ewb.boilerplate.collections.MridBackfillCollection
import com.zepben.ewb.cim.iec61970.base.core.Feeder
import com.zepben.ewb.cim.iec61970.base.core.Substation
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class MridBackfillCollectionTest {

    companion object {
        @JvmField
        @RegisterExtension
        val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()
    }

    private class TestCollection : MridBackfillCollection<Feeder, Substation>() {
        private val backing = mutableListOf<Feeder>()

        override val owner = Substation("owner")
        override val elementDescription = "A Feeder"

        override fun getCollection(): MutableCollection<Feeder> = backing

        override fun getByMrid(mRID: String): Feeder? =
            backing.firstOrNull { it.mRID == mRID }
    }

    @Test
    internal fun `defaults to no backfill`() {
        val collection = TestCollection()
        val feeder = Feeder("feeder")

        assertThat(collection.backfill, nullValue())
        assertThat(collection.add(feeder), equalTo(true))
        assertThat(collection, contains(feeder))

        collection.clear()
        assertThat(collection.isEmpty(), equalTo(true))
    }

}

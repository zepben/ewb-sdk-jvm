/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.common.translator

import com.zepben.ewb.cim.iec61970.base.wires.Junction
import com.zepben.ewb.services.network.NetworkService
import com.zepben.testutils.junit.SystemLogExtension
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifySequence
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.sameInstance
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class BaseProtoToCimTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.Companion.SYSTEM_ERR.captureLog().muteOnSuccess()

    private val service = NetworkService()
    private val junction = Junction("mRID1").also { service.add(it) }
    private val addFromPb = mockk<() -> Junction>()

    @Test
    internal fun `getOrAddFromPb reuses existing items if possible`() {
        service.getOrAddFromPb("mRID1", addFromPb).apply {
            assertThat(mRID, equalTo("mRID1"))
            assertThat(identifiedObject, sameInstance(junction))
            assertThat("Should have found the object in the service", reusedExisting)
        }
        confirmVerified(addFromPb)
    }

    @Test
    internal fun `getOrAddFromPb creates new items via addFromPb if required`() {
        val newJunction = Junction("mRID2")
        every { addFromPb() } returns newJunction

        service.getOrAddFromPb("mRID2", addFromPb).apply {
            assertThat(mRID, equalTo("mRID2"))
            assertThat(identifiedObject, sameInstance(newJunction))
            assertThat("Should have created a new object", !reusedExisting)
        }

        verifySequence {
            addFromPb()
        }
    }

}

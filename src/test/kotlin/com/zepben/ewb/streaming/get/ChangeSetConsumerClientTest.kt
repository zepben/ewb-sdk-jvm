/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.streaming.get

import com.zepben.ewb.streaming.grpc.GrpcChannel
import com.zepben.testutils.junit.SystemLogExtension
import io.grpc.Channel
import io.mockk.mockk
import io.mockk.verifySequence
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension


class ChangeSetConsumerClientTest {

    companion object {
        @JvmField
        @RegisterExtension
        val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()
    }

    @Test
    fun `constructor coverage`() {
        ChangeSetConsumerClient(
            variantChannel = mockk<GrpcChannel>(relaxed = true),
            variantService = mockk(relaxed = true),
            networkChannel = mockk(relaxed = true),
            diagramChannel = mockk(relaxed = true),
            customerChannel = mockk(relaxed = true),
            variantCallCredentials = mockk(),
            networkCallCredentials = mockk(),
            diagramCallCredentials = mockk(),
            customerCallCredentials = mockk(),
        )

        ChangeSetConsumerClient(
            variantChannel = mockk<Channel>(),
            variantService = mockk(),
            networkChannel = mockk(),
            diagramChannel = mockk(),
            customerChannel = mockk(),
            variantCallCredentials = mockk(),
            networkCallCredentials = mockk(),
            diagramCallCredentials = mockk(),
            customerCallCredentials = mockk(),
        )

        ChangeSetConsumerClient(
            channel = mockk<GrpcChannel>(relaxed = true),
            variantService = mockk(),
            callCredentials = mockk(),
        )
    }

    @Test
    fun `closes underlying client`() {
        val underlying = mockk<VariantConsumerClient>(relaxed = true)
        val client = ChangeSetConsumerClient(
            underlying,
            networkChannel = mockk(),
            diagramChannel = mockk(),
            customerChannel = mockk(),
            networkCallCredentials = mockk(),
            diagramCallCredentials = mockk(),
            customerCallCredentials = mockk(),
        )
        client.close()

        verifySequence { underlying.close() }
    }

}

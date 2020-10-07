/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.put.grpc

import com.zepben.cimbend.grpc.ConnectionConfig
import com.zepben.cimbend.grpc.GrpcChannelFactory
import org.junit.jupiter.api.Test

internal class GrpcChannelFactoryTest {

    @Test
    fun createsChannel() {
        // TODO How do we actually test the channel is configured correctly?
        val config = ConnectionConfig("localhost", 80)
        val channel = GrpcChannelFactory.create(config)
        channel.shutdownNow()
    }
}

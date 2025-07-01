/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.streaming.grpc

import io.grpc.Channel
import io.grpc.ManagedChannel
import java.util.concurrent.TimeUnit

/**
 * Wrapper class for gRPC channels that closes the channel at the end of a try-with-resources block.
 */
class GrpcChannel(val channel: Channel) : AutoCloseable {
    override fun close() {
        if (channel is ManagedChannel) {
            if (!channel.isShutdown)
                channel.shutdown()
            channel.awaitTermination(100, TimeUnit.MILLISECONDS)
            if (!channel.isTerminated)
                channel.shutdownNow()
        }
    }
}

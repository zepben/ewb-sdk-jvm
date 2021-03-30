/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.streaming.grpc

import io.grpc.Channel
import io.grpc.ManagedChannel
import java.util.concurrent.TimeUnit


class GrpcChannel(val channel: Channel) : AutoCloseable {
    override fun close() {
        if (channel is ManagedChannel) {
            if (!channel.isShutdown)
                channel.shutdown()
            channel.awaitTermination(100, TimeUnit.MILLISECONDS)
        }
    }
}
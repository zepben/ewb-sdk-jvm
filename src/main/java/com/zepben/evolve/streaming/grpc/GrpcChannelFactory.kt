/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.streaming.grpc

import com.zepben.evolve.streaming.get.CimConsumerClient
import com.zepben.evolve.streaming.put.CimProducerClient
import io.grpc.ManagedChannel
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder
import java.io.File

data class BadConfigException(val msg: String) : Exception(msg)

object GrpcChannelFactory {

    /**
     * Create a gRPC Channel with the specified [config] that can be passed to a gRPC based [CimProducerClient] or
     * [CimConsumerClient] implementation.
     */
    @JvmStatic
    fun create(config: ConnectionConfig): ManagedChannel {
        val channelBuilder = NettyChannelBuilder.forAddress(config.host, config.port)

        if (config.enableTls) {
            val sslContextBuilder = GrpcSslContexts.forClient()

            if (!config.trustCertPath.isNullOrBlank())
                sslContextBuilder.trustManager(File(config.trustCertPath))

            if (!config.authCertPath.isNullOrBlank()) {
                if (!config.authKeyPath.isNullOrBlank())
                    sslContextBuilder.keyManager(File(config.authCertPath), File(config.authKeyPath))
                else
                    throw BadConfigException("If TLS auth is enabled you must specify a key and cert")
            }

            channelBuilder.sslContext(GrpcSslContexts.configure(sslContextBuilder).build())
        } else
            channelBuilder.usePlaintext()

        return channelBuilder.build()
    }

}

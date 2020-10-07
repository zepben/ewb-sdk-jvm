/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.grpc

import io.grpc.ManagedChannel
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder
import java.io.File

data class BadConfigException(val msg: String): Exception(msg)

object GrpcChannelFactory {

    /**
     * Create a gRPC Channel with the specified [config] that can be passed to a gRPC based [com.zepben.cimbend.put.CimProducerClient] or
     * [com.zepben.cimbend.get.CimConsumerClient] implementation.
     */
    @JvmStatic
    fun create(config: ConnectionConfig): ManagedChannel {
        val channelBuilder = NettyChannelBuilder.forAddress(config.host, config.port)

        if (config.enableTls) {
            if (!config.privateKeyFilePath.isNullOrBlank() && !config.certChainFilePath.isNullOrBlank()) {
                val sslContextBuilder = GrpcSslContexts.forClient()

                sslContextBuilder.keyManager(File(config.certChainFilePath), File(config.privateKeyFilePath))

                if (!config.trustCertCollectionFilePath.isNullOrBlank()) {
                    sslContextBuilder.trustManager(File(config.trustCertCollectionFilePath))
                }

                val sslContext = GrpcSslContexts.configure(sslContextBuilder).build()
                channelBuilder.sslContext(sslContext)
            } else {
               throw BadConfigException("If TLS is enabled you must specify at least a key and cert")
            }
        } else
            channelBuilder.usePlaintext()

        return channelBuilder.build()
    }

}

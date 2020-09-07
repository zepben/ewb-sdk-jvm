/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 * This file is part of evolve-sdk-jvm.
 *
 * evolve-sdk-jvm is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * evolve-sdk-jvm is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with evolve-sdk-jvm.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.zepben.cimbend.put.grpc

import com.zepben.cimbend.put.ConnectionConfig
import io.grpc.ManagedChannel
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder
import java.io.File

object GrpcChannelFactory {

    /**
     * Create a gRPC Channel with the specified [config] that can be passed to a gRPC based [com.zepben.cimbendput.CimProducerClient] implementation.
     */
    @JvmStatic
    fun create(config: ConnectionConfig): ManagedChannel {
        val channelBuilder = NettyChannelBuilder.forAddress(config.host, config.port)

        if (!config.privateKeyFilePath.isNullOrBlank() && !config.certChainFilePath.isNullOrBlank()) {
            val sslContextBuilder = GrpcSslContexts.forClient()

            sslContextBuilder.keyManager(File(config.certChainFilePath), File(config.privateKeyFilePath))

            if (!config.trustCertCollectionFilePath.isNullOrBlank()) {
                sslContextBuilder.trustManager(File(config.trustCertCollectionFilePath))
            }

            val sslContext = GrpcSslContexts.configure(sslContextBuilder).build()
            channelBuilder.sslContext(sslContext)
        } else {
            channelBuilder.usePlaintext()
        }

        return channelBuilder.build()
    }

}

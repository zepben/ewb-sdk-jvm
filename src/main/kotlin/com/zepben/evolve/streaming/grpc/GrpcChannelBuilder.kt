/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.streaming.grpc

import com.zepben.auth.client.ZepbenTokenFetcher
import io.grpc.CallCredentials
import io.grpc.ChannelCredentials
import io.grpc.TlsChannelCredentials
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder
import java.io.File

/**
 * Builder class for GrpcChannel. Allows easy specification of channel credentials via SSL/TLS
 * and call credentials via a ZepbenTokenFetcher.
 */
class GrpcChannelBuilder {

    private var _host: String = "localhost"
    private var _port: Int = 50051
    private var _channelCredentials: ChannelCredentials? = null
    private var _callCredentials: CallCredentials? = null

    fun build(): GrpcChannel = GrpcChannel(
        _channelCredentials?.let { channelCreds ->
            val channelBuilder = NettyChannelBuilder.forAddress(_host, _port, channelCreds)
            _callCredentials?.let { callCreds ->
                channelBuilder.intercept(CallCredentialApplier(callCreds)).build()
            } ?: channelBuilder.build()
        } ?: NettyChannelBuilder.forAddress(_host, _port).usePlaintext().build()
    )

    fun forAddress(
        host: String,
        port: Int
    ): GrpcChannelBuilder {
        _host = host
        _port = port
        return this
    }

    fun makeSecure(
        rootCertificates: File? = null
    ): GrpcChannelBuilder {
        var channelCredentialsBuilder = TlsChannelCredentials.newBuilder()
        if (rootCertificates != null) {
            channelCredentialsBuilder = channelCredentialsBuilder.trustManager(rootCertificates)
        }
        _channelCredentials = channelCredentialsBuilder.build()
        return this
    }

    fun makeSecure(
        rootCertificates: File? = null,
        certificateChain: File,
        privateKey: File
    ): GrpcChannelBuilder {
        var channelCredentialsBuilder = TlsChannelCredentials.newBuilder().keyManager(certificateChain, privateKey)
        if (rootCertificates != null) {
            channelCredentialsBuilder = channelCredentialsBuilder.trustManager(rootCertificates)
        }
        _channelCredentials = channelCredentialsBuilder.build()
        return this
    }

    fun withTokenFetcher(tokenFetcher: ZepbenTokenFetcher): GrpcChannelBuilder {
        _callCredentials = TokenCallCredentials(tokenFetcher::fetchToken)
        return this
    }

}

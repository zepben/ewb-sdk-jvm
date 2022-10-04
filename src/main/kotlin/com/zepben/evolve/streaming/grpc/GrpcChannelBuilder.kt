/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.streaming.grpc

import io.grpc.CallCredentials
import io.grpc.ChannelCredentials
import io.grpc.TlsChannelCredentials
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

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
        } ?: NettyChannelBuilder.forAddress(_host, _port).build()
    )

    fun socketAddress(
        host: String,
        port: Int
    ): GrpcChannelBuilder {
        _host = host
        _port = port
        return this
    }

    fun makeSecure(
        rootCertificates: File,
        privateKey: File,
        certificateChain: File
    ): GrpcChannelBuilder =
        makeSecure(
            rootCertificates = FileInputStream(rootCertificates),
            privateKey = FileInputStream(privateKey),
            certificateChain = FileInputStream(certificateChain)
        )

    fun makeSecure(
        rootCertificates: InputStream,
        privateKey: InputStream,
        certificateChain: InputStream
    ): GrpcChannelBuilder {
        _channelCredentials = TlsChannelCredentials
            .newBuilder()
            .trustManager(rootCertificates)
            .keyManager(certificateChain, privateKey)
            .build()
        return this
    }

}
/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.streaming.grpc

import com.zepben.auth.client.ZepbenTokenFetcher
import com.zepben.evolve.streaming.get.CustomerConsumerClient
import com.zepben.evolve.streaming.get.DiagramConsumerClient
import com.zepben.evolve.streaming.get.NetworkConsumerClient
import com.zepben.protobuf.cc.CustomerConsumerGrpc
import com.zepben.protobuf.dc.DiagramConsumerGrpc
import com.zepben.protobuf.nc.NetworkConsumerGrpc
import io.grpc.*
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.util.concurrent.TimeUnit

private const val TWENTY_MEGABYTES = 1024 * 1024 * 20

data class GrpcBuildArgs(
    val skipConnectionTest: Boolean,
    val debugConnectionTest: Boolean,
    val connectionTestTimeoutMs: Long,
    val maxInboundMessageSize: Int
)

val DEFAULT_BUILD_ARGS = GrpcBuildArgs(skipConnectionTest = false, debugConnectionTest = true, connectionTestTimeoutMs = 5000, maxInboundMessageSize = TWENTY_MEGABYTES)


/**
 * Builder class for GrpcChannel. Allows easy specification of channel credentials via SSL/TLS
 * and call credentials via a ZepbenTokenFetcher.
 */
class GrpcChannelBuilder {

    private var _host: String = "localhost"
    private var _port: Int = 50051
    private var _channelCredentials: ChannelCredentials? = null
    private var _callCredentials: CallCredentials? = null

    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    fun build(buildArgs: GrpcBuildArgs = DEFAULT_BUILD_ARGS): GrpcChannel = GrpcChannel(
        _channelCredentials?.let { channelCreds ->
            val channelBuilder = NettyChannelBuilder.forAddress(_host, _port, channelCreds).maxInboundMessageSize(buildArgs.maxInboundMessageSize)
            _callCredentials?.let { callCreds ->
                channelBuilder.intercept(CallCredentialApplier(callCreds)).build()
            } ?: channelBuilder.build()
        } ?: NettyChannelBuilder.forAddress(_host, _port).usePlaintext().maxInboundMessageSize(buildArgs.maxInboundMessageSize).build()
    ).also {
        if (!buildArgs.skipConnectionTest)
            testConnection(it, buildArgs.debugConnectionTest, buildArgs.connectionTestTimeoutMs)
    }

    internal fun testConnection(grpcChannel: GrpcChannel, debug: Boolean, timeoutMs: Long) {
        //withDeadLineAfter() sets the deadline based on now() when it is called not when the request is made, so we hold the clients in these lambda's until we are ready to make the getMetadata() request.
        val clients = mapOf(
            "NetworkConsumerClient" to { NetworkConsumerClient(NetworkConsumerGrpc.newStub(grpcChannel.channel).withDeadlineAfter(timeoutMs, TimeUnit.MILLISECONDS)) },
            "CustomerConsumerClient" to { CustomerConsumerClient(CustomerConsumerGrpc.newStub(grpcChannel.channel).withDeadlineAfter(timeoutMs, TimeUnit.MILLISECONDS)) },
            "DiagramConsumerClient" to { DiagramConsumerClient(DiagramConsumerGrpc.newStub(grpcChannel.channel).withDeadlineAfter(timeoutMs, TimeUnit.MILLISECONDS)) },
        )
        val debugErrors = mutableMapOf<String, StatusRuntimeException>()

        clients.forEach {
            val result = it.value().getMetadata()

            if (result.wasSuccessful) {
                logger.debug("Initial connection test with ${it.key} succeeded. Returning GrpcChannel to client.")
                return
            }
            val t = result.thrown
            if (t is StatusRuntimeException) {
                logger.debug("Initial connection test failed for ${it.key} with $t")
                if (debug)
                    debugErrors[it.key] = t
            } else
                throw t
        }
        var debugInfo = ""
        debugErrors.forEach{ debugInfo +=  "\n[DEBUG] ${it.key}: ${it.value}" }
        throw GrpcConnectionException("Couldn't establish gRPC connection to any service on $_host:$_port.${debugInfo}")
    }

    fun forAddress(
        host: String,
        port: Int
    ): GrpcChannelBuilder = apply {
        _host = host
        _port = port
    }

    fun makeSecure(
        rootCertificates: File
    ): GrpcChannelBuilder = apply {
        _channelCredentials = TlsChannelCredentials.newBuilder().trustManager(rootCertificates).build()
    }

    fun makeSecure(rootCertificates: String? = null): GrpcChannelBuilder = rootCertificates?.let {
        makeSecure(File(rootCertificates))
    } ?: apply {
        _channelCredentials = TlsChannelCredentials.create()
    }

    fun makeSecure(
        rootCertificates: File? = null,
        certificateChain: File,
        privateKey: File
    ): GrpcChannelBuilder = apply {
        var channelCredentialsBuilder = TlsChannelCredentials.newBuilder().keyManager(certificateChain, privateKey)
        if (rootCertificates != null) {
            channelCredentialsBuilder = channelCredentialsBuilder.trustManager(rootCertificates)
        }
        _channelCredentials = channelCredentialsBuilder.build()
    }

    fun makeSecure(
        rootCertificates: String? = null,
        certificateChain: String,
        privateKey: String
    ): GrpcChannelBuilder = makeSecure(rootCertificates?.let { File(it) }, File(certificateChain), File(privateKey))

    fun makeInsecure( ): GrpcChannelBuilder = apply {
        _channelCredentials = InsecureChannelCredentials.create()
    }

    fun withTokenFetcher(tokenFetcher: ZepbenTokenFetcher): GrpcChannelBuilder = apply {
        if (_callCredentials != null)
            throw IllegalArgumentException("Call credential already set in connection builder.")
        _callCredentials = TokenCallCredentials(tokenFetcher::fetchToken)
    }

    fun withAccessToken(token: String, prefix: String = "Bearer"): GrpcChannelBuilder = apply {
        if (_callCredentials != null)
            throw IllegalArgumentException("Call credential already set in connection builder.")
        _callCredentials = TokenCallCredentials { "$prefix $token" }
    }

}

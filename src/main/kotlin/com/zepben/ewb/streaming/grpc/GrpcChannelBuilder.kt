/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.streaming.grpc

import com.google.protobuf.Empty
import com.zepben.auth.client.ZepbenTokenFetcher
import com.zepben.protobuf.cc.CustomerConsumerGrpc
import com.zepben.protobuf.connection.CheckConnectionRequest
import com.zepben.protobuf.dc.DiagramConsumerGrpc
import com.zepben.protobuf.nc.NetworkConsumerGrpc
import com.zepben.protobuf.ns.QueryNetworkStateServiceGrpc
import com.zepben.protobuf.ns.UpdateNetworkStateServiceGrpc
import io.grpc.*
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder
import io.grpc.stub.ClientCalls
import io.netty.handler.ssl.util.InsecureTrustManagerFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.util.concurrent.TimeUnit

private const val TWENTY_MEGABYTES = 1024 * 1024 * 20

/**
 * @param skipConnectionTest Whether to skip the connection test when establishing the channel.
 * @param debugConnectionTest Set to true to save and log all errors from the connection test. Note this will be verbose and is for advanced use only.
 * @param connectionTestTimeoutMs The amount of time to wait for the connection test to complete.
 * @param maxInboundMessageSize The amount of data that can be received in a single message over the gRPC connection.
 */
data class GrpcBuildArgs(
    val skipConnectionTest: Boolean = false,
    val debugConnectionTest: Boolean = false,
    val connectionTestTimeoutMs: Long = 10000,
    val maxInboundMessageSize: Int = TWENTY_MEGABYTES
)

val DEFAULT_BUILD_ARGS =
    GrpcBuildArgs(skipConnectionTest = false, debugConnectionTest = true, connectionTestTimeoutMs = 5000, maxInboundMessageSize = TWENTY_MEGABYTES)


// This needs to be updated for every new client that gets added to the SDK.
internal val GRPC_CLIENT_CONNECTION_TESTS: Map<String, MethodDescriptor<CheckConnectionRequest, Empty>> = mapOf(
    "NetworkConsumerClient" to NetworkConsumerGrpc.getCheckConnectionMethod(),
    "CustomerConsumerClient" to CustomerConsumerGrpc.getCheckConnectionMethod(),
    "DiagramConsumerClient" to DiagramConsumerGrpc.getCheckConnectionMethod(),
    "UpdateNetworkStateClient" to UpdateNetworkStateServiceGrpc.getCheckConnectionMethod(),
    "QueryNetworkStateClient" to QueryNetworkStateServiceGrpc.getCheckConnectionMethod(),
)

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
            testConnection(it, GRPC_CLIENT_CONNECTION_TESTS, buildArgs.debugConnectionTest, buildArgs.connectionTestTimeoutMs)
    }

    internal fun testConnection(
        grpcChannel: GrpcChannel,
        clients: Map<String, MethodDescriptor<CheckConnectionRequest, Empty>>,
        debug: Boolean,
        timeoutMs: Long
    ) {

        val debugErrors = mutableMapOf<String, StatusRuntimeException>()

        // Grabbing the callOptions from one of our stubs, hoping that they are all the same.
        val callOptions = NetworkConsumerGrpc.newStub(grpcChannel.channel).callOptions
        val request = CheckConnectionRequest.newBuilder().build()
        clients.forEach { (desc, methodDescriptor) ->
            runCatching {
                // Doing this by hand so we can set the deadline for each call/turning it into a request timeout
                // Future improvement might be to make this async one day, but it's only an improvement if
                // we are in a scenario where a user only has permissions for one service and it's not the first one that is checked.
                // TODO: this could probably just use client stubs because deadlines don't really work
                ClientCalls.blockingUnaryCall(
                    grpcChannel.channel.newCall(methodDescriptor, callOptions.withDeadlineAfter(timeoutMs, TimeUnit.MILLISECONDS)),
                    request
                )
                logger.debug("Initial connection test with $desc succeeded. Returning GrpcChannel to client.")
                return
            }.onFailure { t ->
                if (t is StatusRuntimeException) {
                    logger.debug("Initial connection test failed for $desc with $t")
                    if (debug)
                        debugErrors[desc] = t
                } else
                    throw t
            }
        }
        var debugInfo = ""
        debugErrors.forEach { debugInfo += "\n[DEBUG] ${it.key}: ${it.value}" }
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
        rootCertificates: File,
        verifyCertificates: Boolean = true
    ): GrpcChannelBuilder = apply {
        _channelCredentials = if (!verifyCertificates) {
            TlsChannelCredentials.newBuilder().trustManager(*InsecureTrustManagerFactory.INSTANCE.trustManagers).build()
        } else {
            TlsChannelCredentials.newBuilder().trustManager(rootCertificates).build()
        }
    }

    fun makeSecure(rootCertificates: String? = null, verifyCertificates: Boolean = true): GrpcChannelBuilder = rootCertificates?.let {
        makeSecure(File(rootCertificates), verifyCertificates)
    } ?: apply {
        _channelCredentials = if (verifyCertificates) {
            TlsChannelCredentials.create()
        } else {
            TlsChannelCredentials.newBuilder().trustManager(*InsecureTrustManagerFactory.INSTANCE.trustManagers).build()
        }
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

    fun makeInsecure(): GrpcChannelBuilder = apply {
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

/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.streaming.grpc

import com.google.common.reflect.ClassPath
import com.google.protobuf.Empty
import com.zepben.auth.client.ZepbenTokenFetcher
import com.zepben.auth.common.AuthMethod
import com.zepben.evolve.streaming.get.*
import com.zepben.evolve.streaming.mutations.UpdateNetworkStateClient
import com.zepben.protobuf.connection.CheckConnectionRequest
import com.zepben.testutils.exception.ExpectException.Companion.expect
import io.grpc.*
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder
import io.grpc.stub.ClientCalls
import io.mockk.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import java.io.File
import java.lang.reflect.Modifier
import kotlin.reflect.KClass

internal class GrpcChannelBuilderTest {

    @AfterEach
    internal fun teardownMockks() {
        unmockkAll()
    }

    @Test
    internal fun `test max inbound message size is set`() {
        val insecureChannel = mockk<ManagedChannel>()

        mockkStatic(NettyChannelBuilder::class)
        every { NettyChannelBuilder.forAddress("hostname", 1234).usePlaintext().maxInboundMessageSize(any()).build() } returns insecureChannel

        var grpcChannel = GrpcChannelBuilder().forAddress("hostname", 1234)
            .build(GrpcBuildArgs(skipConnectionTest = true, debugConnectionTest = false, connectionTestTimeoutMs = 5000, maxInboundMessageSize = 2000))

        verify {
            NettyChannelBuilder.forAddress("hostname", 1234).usePlaintext().maxInboundMessageSize(2000)
        }
        assertThat(grpcChannel.channel, equalTo(insecureChannel))

        grpcChannel = GrpcChannelBuilder().forAddress("hostname", 1234)
            .build(GrpcBuildArgs(skipConnectionTest = true, debugConnectionTest = false, connectionTestTimeoutMs = 5000, maxInboundMessageSize = DEFAULT_BUILD_ARGS.maxInboundMessageSize))

        verify {
            NettyChannelBuilder.forAddress("hostname", 1234).usePlaintext().maxInboundMessageSize(20971520)
        }
        assertThat(grpcChannel.channel, equalTo(insecureChannel))

        grpcChannel = GrpcChannelBuilder().forAddress("hostname", 1234)
            .build(GrpcBuildArgs(skipConnectionTest = true, debugConnectionTest = false, connectionTestTimeoutMs = 5000, maxInboundMessageSize = 2000))

        verify {
            NettyChannelBuilder.forAddress("hostname", 1234).usePlaintext().maxInboundMessageSize(2000)
        }
        assertThat(grpcChannel.channel, equalTo(insecureChannel))
    }

    @Test
    internal fun defaultBuildArgs() {
        val insecureChannel = mockk<ManagedChannel>()

        mockkStatic(NettyChannelBuilder::class) {
            every {
                NettyChannelBuilder.forAddress("localhost", 50051).usePlaintext().maxInboundMessageSize(DEFAULT_BUILD_ARGS.maxInboundMessageSize).build()
            } returns insecureChannel

            val builderSpy = spyk(GrpcChannelBuilder())
            every { builderSpy.testConnection(any(), GRPC_CLIENT_CONNECTION_TESTS, any(), any()) } just runs

            val grpcChannel = builderSpy.build()

            verifySequence {
                builderSpy.build(DEFAULT_BUILD_ARGS)
                builderSpy.testConnection(grpcChannel, GRPC_CLIENT_CONNECTION_TESTS, true, 5000)
            }
        }
    }

    @Test
    internal fun skipTestConnection() {
        val insecureChannel = mockk<ManagedChannel>()

        mockkStatic(NettyChannelBuilder::class)
        every { NettyChannelBuilder.forAddress("localhost", 50051).usePlaintext().maxInboundMessageSize(7).build() } returns insecureChannel

        val builderSpy = spyk(GrpcChannelBuilder())

        val grpcChannel = builderSpy.build(GrpcBuildArgs(skipConnectionTest = true, debugConnectionTest = false, connectionTestTimeoutMs = 5000, maxInboundMessageSize = 7))

        verifySequence {
            builderSpy.build(GrpcBuildArgs(skipConnectionTest = true, debugConnectionTest = false, connectionTestTimeoutMs = 5000, maxInboundMessageSize = 7))
        }
        assertThat(grpcChannel.channel, equalTo(insecureChannel))
    }

    @Test
    internal fun passDebugFlagAndTimeoutMsToTestConnection() {
        val insecureChannel = mockk<ManagedChannel>()

        mockkStatic(NettyChannelBuilder::class)
        every { NettyChannelBuilder.forAddress("localhost", 50051).usePlaintext().maxInboundMessageSize(12).build() } returns insecureChannel

        val builderSpy = spyk(GrpcChannelBuilder())
        every { builderSpy.testConnection(any(), GRPC_CLIENT_CONNECTION_TESTS, any(), any()) } just runs

        val grpcChannel = builderSpy.build(GrpcBuildArgs(skipConnectionTest = false, debugConnectionTest = true, connectionTestTimeoutMs = 1234, maxInboundMessageSize = 12))

        verifySequence {
            builderSpy.build(GrpcBuildArgs(skipConnectionTest = false, debugConnectionTest = true, connectionTestTimeoutMs = 1234, maxInboundMessageSize = 12))
            builderSpy.testConnection(grpcChannel, GRPC_CLIENT_CONNECTION_TESTS, true, 1234)
        }
    }

    @Test
    internal fun `testConnection returns on first successful call`() {
        val responses = mapOf<KClass<out GrpcClient>, Exception?>(
            NetworkConsumerClient::class to StatusRuntimeException(Status.DATA_LOSS),
            CustomerConsumerClient::class to StatusRuntimeException(Status.INVALID_ARGUMENT),
            DiagramConsumerClient::class to null, //null returns GrpcResult mockk with wasSuccessful = true
            UpdateNetworkStateClient::class to null,
            QueryNetworkStateClient::class to null,
        )

        runTestConnection(responses)
    }

    @Test
    internal fun `testConnection continues past grpc status UNAVAILABLE exceptions`() {
        val responses = mapOf<KClass<out GrpcClient>, Exception?>(
            NetworkConsumerClient::class to StatusRuntimeException(Status.UNAVAILABLE),
            CustomerConsumerClient::class to null, //null returns GrpcResult mockk with wasSuccessful = true
            DiagramConsumerClient::class to StatusRuntimeException(Status.DATA_LOSS),
        )

        runTestConnection(responses)
    }

    @Test
    internal fun `testConnection continues past grpc status UNAUTHENTICATED exceptions`() {
        val responses = mapOf<KClass<out GrpcClient>, Exception?>(
            NetworkConsumerClient::class to StatusRuntimeException(Status.DATA_LOSS),
            CustomerConsumerClient::class to StatusRuntimeException(Status.UNAUTHENTICATED),
            DiagramConsumerClient::class to null, //null returns GrpcResult mockk with wasSuccessful = true
        )

        runTestConnection(responses)
    }

    @Test
    internal fun `testConnection continues past grpc status UNKNOWN exceptions`() {
        val responses = mapOf<KClass<out GrpcClient>, Exception?>(
            NetworkConsumerClient::class to StatusRuntimeException(Status.DATA_LOSS),
            CustomerConsumerClient::class to StatusRuntimeException(Status.UNKNOWN),
            DiagramConsumerClient::class to null, //null returns GrpcResult mockk with wasSuccessful = true
        )

        runTestConnection(responses)
    }

    @Test
    internal fun `testConnection rethrows unexpected exceptions`() {
        val responses = mapOf<KClass<out GrpcClient>, Exception?>(
            NetworkConsumerClient::class to StatusRuntimeException(Status.DATA_LOSS),
            CustomerConsumerClient::class to Exception("Unexpected"),
            DiagramConsumerClient::class to StatusRuntimeException(Status.UNAVAILABLE),
        )

        expect {
            runTestConnection(responses)
        }.toThrow<Exception>().withMessage("Unexpected")
    }

    @Test
    internal fun `testConnection collects other exceptions for debug output`() {
        val responses = mapOf<KClass<out GrpcClient>, Exception?>(
            NetworkConsumerClient::class to StatusRuntimeException(Status.DATA_LOSS.withDescription("Data loss message for testing")),
            CustomerConsumerClient::class to StatusRuntimeException(Status.INVALID_ARGUMENT.withDescription("Invalid argument message for testing")),
            DiagramConsumerClient::class to StatusRuntimeException(Status.UNIMPLEMENTED.withDescription("Method not found: zepben.protobuf.dc.DiagramConsumer/checkConnection")),
            UpdateNetworkStateClient::class to StatusRuntimeException(Status.UNIMPLEMENTED.withDescription("Method not found: zepben.protobuf.ns.UpdateNetworkStateClient/checkConnection")),
            QueryNetworkStateClient::class to StatusRuntimeException(Status.UNIMPLEMENTED.withDescription("Method not found: zepben.protobuf.ns.QueryNetworkStateClient/checkConnection"))
            )

        expect {
            runTestConnection(responses, debug = true)
        }.toThrow<GrpcConnectionException>().withMessage("Couldn't establish gRPC connection to any service on localhost:50051.\n" +
            "[DEBUG] NetworkConsumerClient: io.grpc.StatusRuntimeException: DATA_LOSS: Data loss message for testing\n" +
            "[DEBUG] CustomerConsumerClient: io.grpc.StatusRuntimeException: INVALID_ARGUMENT: Invalid argument message for testing\n" +
            "[DEBUG] DiagramConsumerClient: io.grpc.StatusRuntimeException: UNIMPLEMENTED: Method not found: zepben.protobuf.dc.DiagramConsumer/checkConnection\n" +
            "[DEBUG] UpdateNetworkStateClient: io.grpc.StatusRuntimeException: UNIMPLEMENTED: Method not found: zepben.protobuf.ns.UpdateNetworkStateClient/checkConnection\n" +
            "[DEBUG] QueryNetworkStateClient: io.grpc.StatusRuntimeException: UNIMPLEMENTED: Method not found: zepben.protobuf.ns.QueryNetworkStateClient/checkConnection"
        )
    }

    @Test
    internal fun `testConnection throws GrpcConnectionException if no successful responses received`() {
        val responses = mapOf<KClass<out GrpcClient>, Exception?>(
            NetworkConsumerClient::class to StatusRuntimeException(Status.DATA_LOSS.withDescription("Data loss message for testing")),
            CustomerConsumerClient::class to StatusRuntimeException(Status.INVALID_ARGUMENT.withDescription("Invalid argument message for testing")),
            DiagramConsumerClient::class to StatusRuntimeException(Status.UNIMPLEMENTED.withDescription("Method not found: zepben.protobuf.dc.DiagramConsumer/checkConnection")),
            UpdateNetworkStateClient::class to StatusRuntimeException(Status.UNIMPLEMENTED.withDescription("Method not found: zepben.protobuf.ns.UpdateNetworkStateClient/checkConnection")),
            QueryNetworkStateClient::class to StatusRuntimeException(Status.UNIMPLEMENTED.withDescription("Method not found: zepben.protobuf.ns.QueryNetworkStateClient/checkConnection")),
        )

        expect {
            runTestConnection(responses, debug = false)
        }.toThrow<GrpcConnectionException>().withMessage("Couldn't establish gRPC connection to any service on localhost:50051.")
    }

    @Test
    internal fun forAddress() {
        val insecureChannel = mockk<ManagedChannel>()

        mockkStatic(NettyChannelBuilder::class)
        every { NettyChannelBuilder.forAddress("hostname", 1234).usePlaintext().maxInboundMessageSize(any()).build() } returns insecureChannel

        val grpcChannel = GrpcChannelBuilder().forAddress("hostname", 1234)
            .build(GrpcBuildArgs(skipConnectionTest = true, debugConnectionTest = false, connectionTestTimeoutMs = 5000, maxInboundMessageSize = 28))
        assertThat(grpcChannel.channel, equalTo(insecureChannel))
    }

    @Test
    internal fun makeSecure() {
        val caFile = mockk<File>()
        val channelCredentials = mockk<ChannelCredentials>()
        val secureChannel = mockk<ManagedChannel>()

        mockkStatic(TlsChannelCredentials::class)
        every { TlsChannelCredentials.newBuilder().trustManager(caFile).build() } returns channelCredentials

        mockkStatic(NettyChannelBuilder::class)
        every { NettyChannelBuilder.forAddress("hostname", 1234, channelCredentials).maxInboundMessageSize(any()).build() } returns secureChannel

        val grpcChannel = GrpcChannelBuilder().forAddress("hostname", 1234).makeSecure(caFile).build(
            GrpcBuildArgs(skipConnectionTest = true, debugConnectionTest = false, connectionTestTimeoutMs = 5000, maxInboundMessageSize = 34))
        assertThat(grpcChannel.channel, equalTo(secureChannel))
    }

    @Test
    internal fun makeSecureWithDefaultTrust() {
        val channelCredentials = mockk<ChannelCredentials>()
        val secureChannel = mockk<ManagedChannel>()

        mockkStatic(TlsChannelCredentials::class)
        every { TlsChannelCredentials.create() } returns channelCredentials

        mockkStatic(NettyChannelBuilder::class)
        every { NettyChannelBuilder.forAddress("hostname", 1234, channelCredentials).maxInboundMessageSize(any()).build() } returns secureChannel

        val grpcChannel = GrpcChannelBuilder().forAddress("hostname", 1234).makeSecure()
            .build(GrpcBuildArgs(skipConnectionTest = true, debugConnectionTest = false, connectionTestTimeoutMs = 5000, maxInboundMessageSize = 42))
        assertThat(grpcChannel.channel, equalTo(secureChannel))
    }

    @Test
    internal fun makeSecureWithClientAuthentication() {
        val caFile = mockk<File>()
        val pkFile = mockk<File>()
        val certChainFile = mockk<File>()
        val channelCredentials = mockk<ChannelCredentials>()
        val secureChannel = mockk<ManagedChannel>()

        mockkStatic(TlsChannelCredentials::class)
        every { TlsChannelCredentials.newBuilder().keyManager(certChainFile, pkFile).trustManager(caFile).build() } returns channelCredentials
        every { TlsChannelCredentials.newBuilder().keyManager(certChainFile, pkFile).build() } returns channelCredentials

        mockkStatic(NettyChannelBuilder::class)
        every { NettyChannelBuilder.forAddress("hostname", 1234, channelCredentials).maxInboundMessageSize(any()).build() } returns secureChannel

        assertThat(
            GrpcChannelBuilder().forAddress("hostname", 1234).makeSecure(caFile, certChainFile, pkFile)
                .build(GrpcBuildArgs(skipConnectionTest = true, debugConnectionTest = false, connectionTestTimeoutMs = 5000, maxInboundMessageSize = 53)).channel, equalTo(secureChannel)
        )
        assertThat(
            GrpcChannelBuilder().forAddress("hostname", 1234).makeSecure(certificateChain = certChainFile, privateKey = pkFile)
                .build(GrpcBuildArgs(skipConnectionTest = true, debugConnectionTest = false, connectionTestTimeoutMs = 5000, maxInboundMessageSize = 62)).channel, equalTo(secureChannel)
        )
    }

    @Test
    internal fun makeSecureWithFilenamesCoverage() {
        // There's no way of stubbing constructors themselves AFAIK, which would be useful in ensuring that the filename overload calls the original with
        // the File(...) of each filename.
        val secureGrpcChannel = mockk<GrpcChannel>()

        mockkConstructor(GrpcChannelBuilder::class)
        every {
            constructedWith<GrpcChannelBuilder>().makeSecure(null, "certChainFilename", "pkFilename")
                .build(GrpcBuildArgs(skipConnectionTest = true, debugConnectionTest = false, connectionTestTimeoutMs = 5000, maxInboundMessageSize = 1))
        } returns secureGrpcChannel

        assertThat(
            GrpcChannelBuilder().makeSecure(null, "certChainFilename", "pkFilename")
                .build(GrpcBuildArgs(skipConnectionTest = true, debugConnectionTest = false, connectionTestTimeoutMs = 5000, maxInboundMessageSize = 1)), equalTo(secureGrpcChannel)
        )
        assertThat(
            GrpcChannelBuilder().makeSecure(certificateChain = "certChainFilename", privateKey = "pkFilename")
                .build(GrpcBuildArgs(skipConnectionTest = true, debugConnectionTest = false, connectionTestTimeoutMs = 5000, maxInboundMessageSize = 1)), equalTo(secureGrpcChannel)
        )
    }

    @Test
    internal fun withTokenFetcher() {
        val authenticatedChannel = mockk<ManagedChannel>()

        mockkStatic(NettyChannelBuilder::class)
        every { NettyChannelBuilder.forAddress("hostname", 1234, any()).maxInboundMessageSize(any()).intercept(any<CallCredentialApplier>()).build() } returns authenticatedChannel

        val tokenFetcher = ZepbenTokenFetcher("audience", "domain", AuthMethod.AUTH0)
        val grpcChannel = GrpcChannelBuilder().forAddress("hostname", 1234).makeSecure().withTokenFetcher(tokenFetcher)
            .build(GrpcBuildArgs(skipConnectionTest = true, debugConnectionTest = false, connectionTestTimeoutMs = 5000, maxInboundMessageSize = 1))
        assertThat(grpcChannel.channel, equalTo(authenticatedChannel))
    }

    @Test
    internal fun withAccessToken() {
        val authenticatedChannel = mockk<ManagedChannel>()

        mockkStatic(NettyChannelBuilder::class)
        every { NettyChannelBuilder.forAddress("hostname", 1234, any()).maxInboundMessageSize(any()).intercept(any<CallCredentialApplier>()).build() } returns authenticatedChannel

        val grpcChannel = GrpcChannelBuilder().forAddress("hostname", 1234).makeInsecure().withAccessToken("token")
            .build(GrpcBuildArgs(skipConnectionTest = true, debugConnectionTest = false, connectionTestTimeoutMs = 5000, maxInboundMessageSize = 1))
        assertThat(grpcChannel.channel, equalTo(authenticatedChannel))
    }

    @Test
    internal fun `will throw exception if attempting to override already set call credentials`() {
        val tokenFetcher = ZepbenTokenFetcher("audience", "domain", AuthMethod.AUTH0)
        val grpcChannelBuilder = GrpcChannelBuilder().forAddress("hostname", 1234).makeInsecure().withTokenFetcher(tokenFetcher)

        expect {
            grpcChannelBuilder.withAccessToken("token")
        }.toThrow<IllegalArgumentException>().withMessage("Call credential already set in connection builder.")
        expect {
            grpcChannelBuilder.withTokenFetcher(tokenFetcher)
        }.toThrow<IllegalArgumentException>().withMessage("Call credential already set in connection builder.")
    }

    private fun runTestConnection(responses: Map<KClass<out GrpcClient>, Exception?>, debug: Boolean = false, timeoutMs: Long = 5000) {
        val builder = GrpcChannelBuilder()
        val channel = mockk<Channel>()
        val grpcChannel = mockk<GrpcChannel> {
            every { this@mockk.channel } returns channel
        }

        mockkStatic(ClientCalls::class) {

            val networkConsumerGrpcMethodDesc = mockk<MethodDescriptor<CheckConnectionRequest, Empty>>()
            val customerConsumerGrpcMethodDesc = mockk<MethodDescriptor<CheckConnectionRequest, Empty>>()
            val diagramConsumerGrpcMethodDesc = mockk<MethodDescriptor<CheckConnectionRequest, Empty>>()
            val updateNetworkStateServiceGrpcMethodDesc = mockk<MethodDescriptor<CheckConnectionRequest, Empty>>()
            val queryNetworkStateServiceGrpcMethodDesc = mockk<MethodDescriptor<CheckConnectionRequest, Empty>>()

            val mockkedGrpcClientConnectionTests: Map<String, MethodDescriptor<CheckConnectionRequest, Empty>> = mapOf(
                "NetworkConsumerClient" to networkConsumerGrpcMethodDesc,
                "CustomerConsumerClient" to customerConsumerGrpcMethodDesc,
                "DiagramConsumerClient" to diagramConsumerGrpcMethodDesc,
                "UpdateNetworkStateClient" to updateNetworkStateServiceGrpcMethodDesc,
                "QueryNetworkStateClient" to queryNetworkStateServiceGrpcMethodDesc,
            )

            val networkConsumerClientCall = mockk<ClientCall<CheckConnectionRequest, Empty>>()
            val customerConsumerClientCall = mockk<ClientCall<CheckConnectionRequest, Empty>>()
            val diagramConsumerClientCall = mockk<ClientCall<CheckConnectionRequest, Empty>>()
            val updateNetworkStateServiceClientCall = mockk<ClientCall<CheckConnectionRequest, Empty>>()
            val queryNetworkStateServiceClientCall = mockk<ClientCall<CheckConnectionRequest, Empty>>()

            every { channel.newCall(networkConsumerGrpcMethodDesc, any()) } returns networkConsumerClientCall
            every { channel.newCall(customerConsumerGrpcMethodDesc, any()) } returns customerConsumerClientCall
            every { channel.newCall(diagramConsumerGrpcMethodDesc, any()) } returns diagramConsumerClientCall
            every { channel.newCall(updateNetworkStateServiceGrpcMethodDesc, any()) } returns updateNetworkStateServiceClientCall
            every { channel.newCall(queryNetworkStateServiceGrpcMethodDesc, any()) } returns queryNetworkStateServiceClientCall

            val networkConsumerResult = mockk<Empty>()
            val customerConsumerResult = mockk<Empty>()
            val diagramConsumerResult = mockk<Empty>()
            val updateNetworkStateServiceResult = mockk<Empty>()
            val queryNetworkStateServiceResult = mockk<Empty>()

            every { ClientCalls.blockingUnaryCall(networkConsumerClientCall, any<CheckConnectionRequest>()) } returns networkConsumerResult
            every { ClientCalls.blockingUnaryCall(customerConsumerClientCall, any<CheckConnectionRequest>()) } returns customerConsumerResult
            every { ClientCalls.blockingUnaryCall(diagramConsumerClientCall, any<CheckConnectionRequest>()) } returns diagramConsumerResult
            every { ClientCalls.blockingUnaryCall(updateNetworkStateServiceClientCall, any<CheckConnectionRequest>()) } returns updateNetworkStateServiceResult
            every { ClientCalls.blockingUnaryCall(queryNetworkStateServiceClientCall, any<CheckConnectionRequest>()) } returns queryNetworkStateServiceResult

            responses[NetworkConsumerClient::class]?.let {
                every { ClientCalls.blockingUnaryCall(networkConsumerClientCall, any<CheckConnectionRequest>()) } throws it
            } ?: (every { ClientCalls.blockingUnaryCall(networkConsumerClientCall, any<CheckConnectionRequest>()) } returns networkConsumerResult)

            responses[CustomerConsumerClient::class]?.let {
                every { ClientCalls.blockingUnaryCall(customerConsumerClientCall, any<CheckConnectionRequest>()) } throws it
            } ?: (every { ClientCalls.blockingUnaryCall(customerConsumerClientCall, any<CheckConnectionRequest>()) } returns customerConsumerResult)

            responses[DiagramConsumerClient::class]?.let {
                every { ClientCalls.blockingUnaryCall(diagramConsumerClientCall, any<CheckConnectionRequest>()) } throws it
            } ?: (every { ClientCalls.blockingUnaryCall(diagramConsumerClientCall, any<CheckConnectionRequest>()) } returns diagramConsumerResult)

            responses[UpdateNetworkStateClient::class]?.let {
                every { ClientCalls.blockingUnaryCall(updateNetworkStateServiceClientCall, any<CheckConnectionRequest>()) } throws it
            } ?: (every { ClientCalls.blockingUnaryCall(updateNetworkStateServiceClientCall, any<CheckConnectionRequest>()) } returns updateNetworkStateServiceResult)

            responses[QueryNetworkStateClient::class]?.let {
                every { ClientCalls.blockingUnaryCall(queryNetworkStateServiceClientCall, any<CheckConnectionRequest>()) } throws it
            } ?: (every { ClientCalls.blockingUnaryCall(queryNetworkStateServiceClientCall, any<CheckConnectionRequest>()) } returns queryNetworkStateServiceResult)

            builder.testConnection(grpcChannel, mockkedGrpcClientConnectionTests, debug, timeoutMs)
        }
    }

    @Test
    fun testGrpcClientConnectionTests() {
        //Doesn't actually check that they are covering all the stubs etc. but it's a start.
        val grpcClients = ClassPath.from(ClassLoader.getSystemClassLoader())
            .getTopLevelClassesRecursive("com.zepben.evolve")
            .asSequence()
            .map { it.load() }
            .filter { !Modifier.isAbstract(it.modifiers) }
            .filter { GrpcClient::class.java.isAssignableFrom(it) }
            .toSet()

        val toIgnore = listOf("ThrowingGrpcClient")

        assertThat(grpcClients.map { it.simpleName }.subtract(toIgnore), equalTo(GRPC_CLIENT_CONNECTION_TESTS.keys))
    }
}

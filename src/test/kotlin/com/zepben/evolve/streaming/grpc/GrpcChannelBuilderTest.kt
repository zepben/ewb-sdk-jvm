/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.streaming.grpc

import com.zepben.auth.client.ZepbenTokenFetcher
import com.zepben.auth.common.AuthMethod
import io.grpc.ChannelCredentials
import io.grpc.ManagedChannel
import io.grpc.TlsChannelCredentials
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder
import io.mockk.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import java.io.File

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

        var grpcChannel = GrpcChannelBuilder().forAddress("hostname", 1234).build(2000)

        verify {
            NettyChannelBuilder.forAddress("hostname", 1234).usePlaintext().maxInboundMessageSize(2000)
        }
        assertThat(grpcChannel.channel, equalTo(insecureChannel))

        grpcChannel = GrpcChannelBuilder().forAddress("hostname", 1234).build()

        verify {
            NettyChannelBuilder.forAddress("hostname", 1234).usePlaintext().maxInboundMessageSize(20971520)
        }
        assertThat(grpcChannel.channel, equalTo(insecureChannel))

        grpcChannel = GrpcChannelBuilder().forAddress("hostname", 1234).build(0)

        verify {
            NettyChannelBuilder.forAddress("hostname", 1234).usePlaintext().maxInboundMessageSize(0)
        }
        assertThat(grpcChannel.channel, equalTo(insecureChannel))
    }

    @Test
    internal fun forAddress() {
        val insecureChannel = mockk<ManagedChannel>()

        mockkStatic(NettyChannelBuilder::class)
        every { NettyChannelBuilder.forAddress("hostname", 1234).usePlaintext().maxInboundMessageSize(any()).build() } returns insecureChannel

        val grpcChannel = GrpcChannelBuilder().forAddress("hostname", 1234).build()
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

        val grpcChannel = GrpcChannelBuilder().forAddress("hostname", 1234).makeSecure(caFile).build()
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

        val grpcChannel = GrpcChannelBuilder().forAddress("hostname", 1234).makeSecure().build()
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

        assertThat(GrpcChannelBuilder().forAddress("hostname", 1234).makeSecure(caFile, certChainFile, pkFile).build().channel, equalTo(secureChannel))
        assertThat(GrpcChannelBuilder().forAddress("hostname", 1234).makeSecure(certificateChain = certChainFile, privateKey = pkFile).build().channel, equalTo(secureChannel))
    }

    @Test
    internal fun makeSecureWithFilenamesCoverage() {
        // There's no way of stubbing constructors themselves AFAIK, which would be useful in ensuring that the filename overload calls the original with
        // the File(...) of each filename.
        val secureGrpcChannel = mockk<GrpcChannel>()

        mockkConstructor(GrpcChannelBuilder::class)
        every { constructedWith<GrpcChannelBuilder>().makeSecure(null, "certChainFilename", "pkFilename").build() } returns secureGrpcChannel

        assertThat(GrpcChannelBuilder().makeSecure(null, "certChainFilename", "pkFilename").build(), equalTo(secureGrpcChannel))
        assertThat(GrpcChannelBuilder().makeSecure(certificateChain = "certChainFilename", privateKey = "pkFilename").build(), equalTo(secureGrpcChannel))
    }

    @Test
    internal fun withTokenFetcher() {
        val authenticatedChannel = mockk<ManagedChannel>()

        mockkStatic(NettyChannelBuilder::class)
        every { NettyChannelBuilder.forAddress("hostname", 1234, any()).maxInboundMessageSize(any()).intercept(any<CallCredentialApplier>()).build() } returns authenticatedChannel

        val tokenFetcher = ZepbenTokenFetcher("audience", "domain", AuthMethod.AUTH0)
        val grpcChannel = GrpcChannelBuilder().forAddress("hostname", 1234).makeSecure().withTokenFetcher(tokenFetcher).build()
        assertThat(grpcChannel.channel, equalTo(authenticatedChannel))
    }

}

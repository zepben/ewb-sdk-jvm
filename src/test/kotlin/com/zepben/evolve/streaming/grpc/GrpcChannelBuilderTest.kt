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
import io.grpc.ManagedChannel
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mockStatic
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock

internal class GrpcChannelBuilderTest {

    @Test
    internal fun forAddress() {
        mockStatic(NettyChannelBuilder::class.java).use { mockedStatic ->
            val insecureChannelBuilder = mock<NettyChannelBuilder>()
            val insecureChannel = mock<ManagedChannel>()

            mockedStatic
                .`when`<NettyChannelBuilder> { NettyChannelBuilder.forAddress(eq("hostname"), eq(1234)) }
                .doReturn(insecureChannelBuilder)
            doReturn(insecureChannel).`when`(insecureChannelBuilder).build()

            val grpcChannel = GrpcChannelBuilder().forAddress("hostname", 1234).build()
            assertThat(grpcChannel.channel, equalTo(insecureChannel))
        }
    }

    @Test
    internal fun makeSecure() {
        mockStatic(NettyChannelBuilder::class.java).use { mockedStatic ->
            val secureChannelBuilder = mock<NettyChannelBuilder>()
            val secureChannel = mock<ManagedChannel>()

            mockedStatic
                .`when`<NettyChannelBuilder> { NettyChannelBuilder.forAddress(eq("hostname"), eq(1234), any()) }
                .thenReturn(secureChannelBuilder)
            doReturn(secureChannel).`when`(secureChannelBuilder).build()

            val grpcChannel = GrpcChannelBuilder().forAddress("hostname", 1234).makeSecure().build()
            assertThat(grpcChannel.channel, equalTo(secureChannel))
        }
    }

    @Test
    internal fun withTokenFetcher() {
        mockStatic(NettyChannelBuilder::class.java).use { mockedStatic ->
            val secureChannelBuilder = mock<NettyChannelBuilder>()
            val authenticatedChannelBuilder = mock<NettyChannelBuilder>()
            val authenticatedChannel = mock<ManagedChannel>()

            mockedStatic
                .`when`<NettyChannelBuilder> { NettyChannelBuilder.forAddress(eq("hostname"), eq(1234), any()) }
                .thenReturn(secureChannelBuilder)
            doReturn(authenticatedChannelBuilder).`when`(secureChannelBuilder).intercept(any<CallCredentialApplier>())
            doReturn(authenticatedChannel).`when`(authenticatedChannelBuilder).build()

            val tokenFetcher = ZepbenTokenFetcher("audience", "domain", AuthMethod.AUTH0)
            val grpcChannel = GrpcChannelBuilder().forAddress("hostname", 1234).makeSecure().withTokenFetcher(tokenFetcher).build()
            assertThat(grpcChannel.channel, equalTo(authenticatedChannel))
        }
    }

}
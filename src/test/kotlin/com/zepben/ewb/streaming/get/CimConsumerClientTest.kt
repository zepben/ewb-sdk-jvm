/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.streaming.get

import com.zepben.ewb.services.common.BaseService
import com.zepben.ewb.services.common.meta.ServiceInfo
import com.zepben.ewb.services.common.meta.fromPb
import com.zepben.ewb.services.common.translator.BaseProtoToCim
import com.zepben.ewb.streaming.grpc.GrpcResult
import com.zepben.protobuf.metadata.GetMetadataRequest
import com.zepben.protobuf.metadata.GetMetadataResponse
import com.zepben.protobuf.nc.NetworkConsumerGrpc.NetworkConsumerStub
import io.mockk.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import com.zepben.protobuf.metadata.ServiceInfo as PBServiceInfo

internal class TestCimConsumerClient {

    @Test
    internal fun `getMetadata returns non-cached response, and caches it`() {
        val metadataResponse = GetMetadataResponse.newBuilder().apply {
            serviceInfoBuilder.apply {
                title = "test title"
                version = "test version"
                addDataSourcesBuilder().apply {
                    source = "source one"
                    version = "source version"
                    timestamp = timestampBuilder.apply {
                        seconds = 123L
                        nanos = 456
                    }.build()
                }
            }.build()
        }.build()

        val returnedServiceInfo = metadataResponse.serviceInfo.fromPb()
        val baseConsumerClient = spyk(createClient(metadataResponse))

        mockkStatic(PBServiceInfo::fromPb) {
            every { any<PBServiceInfo>().fromPb() } returns returnedServiceInfo
            val result = baseConsumerClient.getMetadata()

            //
            // NOTE: Property (i.e. `serviceInfo`) calls inside classes are no longer captured by Mockk, so there are no
            //       recorded interactions with `baseConsumerClient.serviceInfo`.
            //
            verifySequence {
                baseConsumerClient.getMetadata()
                baseConsumerClient.tryRpc<GrpcResult<ServiceInfo>>(any())
                baseConsumerClient.runGetMetadata(GetMetadataRequest.newBuilder().build(), any())
                metadataResponse.serviceInfo.fromPb()
            }

            assertThat(result.value, equalTo(returnedServiceInfo))
            assertThat(baseConsumerClient.serviceInfo, equalTo(returnedServiceInfo))
        }
    }

    @Test
    internal fun `getMetadata returns cached response`() {
        val cachedResponse = PBServiceInfo.newBuilder().apply {
            title = "cached title"
            version = "cached version"
            addDataSourcesBuilder().apply {
                source = "source cached"
                version = "source cached"
                timestamp = timestampBuilder.apply {
                    seconds = 123L
                    nanos = 456
                }.build()
            }
        }.build().fromPb()

        // We create this with a mockk to prove it isn't used, as it should use the cached version.
        val baseConsumerClient = spyk(createClient(mockk()).apply { serviceInfo = cachedResponse })

        mockkStatic(PBServiceInfo::fromPb) {
            every { any<PBServiceInfo>().fromPb() } throws AssertionError("shouldn't have been called")
            val result = baseConsumerClient.getMetadata()

            //
            // NOTE: Property (i.e. `serviceInfo`) calls inside classes are no longer captured by Mockk, so there are no
            //       recorded interactions with `baseConsumerClient.serviceInfo`.
            //
            verifySequence {
                baseConsumerClient.getMetadata()
                baseConsumerClient.tryRpc<GrpcResult<ServiceInfo>>(any())
            }

            assertThat(result.value, equalTo(cachedResponse))
        }
    }

    private fun createClient(response: GetMetadataResponse) = object : CimConsumerClient<BaseService, BaseProtoToCim, NetworkConsumerStub>() {
        override val service: BaseService get() = assertNotCalled()
        override val protoToCim: BaseProtoToCim get() = assertNotCalled()

        override fun processIdentifiables(mRIDs: Sequence<String>): Sequence<ExtractResult> = assertNotCalled()

        override fun runGetMetadata(
            getMetadataRequest: GetMetadataRequest,
            streamObserver: AwaitableStreamObserver<GetMetadataResponse>
        ) {
            streamObserver.onNext(response)
            streamObserver.onCompleted()
        }

        override val stub: NetworkConsumerStub get() = assertNotCalled()

        private fun assertNotCalled(): Nothing =
            throw AssertionError("shouldn't have been called")

    }

}

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
import io.mockk.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import com.zepben.protobuf.metadata.ServiceInfo as PBServiceInfo

internal class TestCimConsumerClient {

    private val baseConsumerClient = mockk<CimConsumerClient<BaseService, BaseProtoToCim>>()
    
    // @Test
    // Test disabled as the mocking in this test no longer works with Mockk
    // as it is confused between mocking a field and the getter
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
                addDataSourcesBuilder().apply {
                    source = "source two"
                    version = "source version two"
                    timestamp = timestampBuilder.apply {
                        seconds = 654L
                        nanos = 321
                    }.build()
                }
            }.build()
        }.build()

        val returnedServiceInfo = metadataResponse.serviceInfo.fromPb()

        every { baseConsumerClient.runGetMetadata(ofType(GetMetadataRequest::class), any()) } answers {
            secondArg<AwaitableStreamObserver<GetMetadataResponse>>().onNext(metadataResponse)
            secondArg<AwaitableStreamObserver<GetMetadataResponse>>().onCompleted()
        }

        every { baseConsumerClient.getMetadata() } answers { callOriginal() }
        every { baseConsumerClient.tryRpc(any<() -> ServiceInfo>()) } answers {
            GrpcResult.of(firstArg<() -> ServiceInfo>().invoke())
        }

        //Because serviceInfo was made internal for other testing, everyone else has to mockk it themselves...
        every { baseConsumerClient.serviceInfo } returns null
        every { baseConsumerClient.serviceInfo = any() } answers {
            every { baseConsumerClient.serviceInfo } returns firstArg()
        }

        mockkStatic(PBServiceInfo::fromPb) {
            every { any<PBServiceInfo>().fromPb() } returns returnedServiceInfo
            val result = baseConsumerClient.getMetadata()

            verifySequence {
                baseConsumerClient.getMetadata()
                baseConsumerClient.tryRpc<GrpcResult<ServiceInfo>>(any())
                baseConsumerClient.serviceInfo
                baseConsumerClient.runGetMetadata(GetMetadataRequest.newBuilder().build(), any())
                metadataResponse.serviceInfo.fromPb()
                baseConsumerClient.serviceInfo = returnedServiceInfo //verify response cached
                baseConsumerClient.serviceInfo
            }
            assertThat(result.value, equalTo(returnedServiceInfo))
            assertThat(baseConsumerClient.serviceInfo, equalTo(returnedServiceInfo))
        }
    }

    // @Test
    // Test disabled as the mocking in this test no longer works with Mockk
    // as it is confused between mocking a field and the getter
    internal fun `getMetadata returns cached response`() {
        val uncachedServiceInfo = mockk<ServiceInfo>()

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

        every { baseConsumerClient.serviceInfo } returns cachedResponse

        every { baseConsumerClient.getMetadata() } answers { callOriginal() }
        every { baseConsumerClient.tryRpc(any<() -> ServiceInfo>()) } answers {
            GrpcResult.of(firstArg<() -> ServiceInfo>().invoke())
        }
        mockkStatic(PBServiceInfo::fromPb) {
            every { any<PBServiceInfo>().fromPb() } returns uncachedServiceInfo
            val result = baseConsumerClient.getMetadata()

            verifySequence {
                baseConsumerClient.getMetadata()
                baseConsumerClient.tryRpc<GrpcResult<ServiceInfo>>(any())
                baseConsumerClient.serviceInfo
                baseConsumerClient.serviceInfo
            }
            verify {
                any<PBServiceInfo>().fromPb() wasNot called
                uncachedServiceInfo wasNot called
            }
            assertThat(result.value, equalTo(cachedResponse))
        }
    }
}

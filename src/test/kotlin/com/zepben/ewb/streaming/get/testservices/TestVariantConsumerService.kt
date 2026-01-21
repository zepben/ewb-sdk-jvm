/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.streaming.get.testservices

import com.zepben.protobuf.dc.*
import com.zepben.protobuf.metadata.GetMetadataRequest
import com.zepben.protobuf.metadata.GetMetadataResponse
import com.zepben.protobuf.vc.GetChangeSetsRequest
import com.zepben.protobuf.vc.GetChangeSetsResponse
import com.zepben.protobuf.vc.GetNetworkModelProjectsRequest
import com.zepben.protobuf.vc.GetNetworkModelProjectsResponse
import com.zepben.protobuf.vc.VariantConsumerGrpc
import io.grpc.Status
import io.grpc.stub.StreamObserver

internal class TestVariantConsumerService : VariantConsumerGrpc.VariantConsumerImplBase() {

    lateinit var onGetNetworkModelProjects: (request: GetNetworkModelProjectsRequest, response: StreamObserver<GetNetworkModelProjectsResponse>) -> Unit
    lateinit var onGetChangeSets: (request: GetChangeSetsRequest, response: StreamObserver<GetChangeSetsResponse>) -> Unit
    lateinit var onGetMetadataRequest: (request: GetMetadataRequest, response: StreamObserver<GetMetadataResponse>) -> Unit

    override fun getNetworkModelProjects(response: StreamObserver<GetNetworkModelProjectsResponse>): StreamObserver<GetNetworkModelProjectsRequest> =
        TestStreamObserver(response, onGetNetworkModelProjects)

    override fun getChangeSets(response: StreamObserver<GetChangeSetsResponse>): StreamObserver<GetChangeSetsRequest> =
        TestStreamObserver(response, onGetChangeSets)

    override fun getMetadata(request: GetMetadataRequest, responseObserver: StreamObserver<GetMetadataResponse>) =
        runGrpc(request, responseObserver, onGetMetadataRequest)

    private fun <T, U : StreamObserver<*>> runGrpc(request: T, response: U, handler: (T, U) -> Unit) {
        try {
            handler(request, response)
            response.onCompleted()
        } catch (t: Throwable) {
            response.onError(Status.ABORTED.withDescription(t.message).asRuntimeException())
        }
    }
}

/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.streaming.get.testservices

import com.zepben.protobuf.cc.*
import com.zepben.protobuf.metadata.GetMetadataRequest
import com.zepben.protobuf.metadata.GetMetadataResponse
import io.grpc.Status
import io.grpc.stub.StreamObserver

internal class TestCustomerConsumerService : CustomerConsumerGrpc.CustomerConsumerImplBase() {

    lateinit var onGetIdentifiedObjects: (request: GetIdentifiedObjectsRequest, response: StreamObserver<GetIdentifiedObjectsResponse>) -> Unit
    lateinit var onGetCustomersForContainer: (request: GetCustomersForContainerRequest, response: StreamObserver<GetCustomersForContainerResponse>) -> Unit
    lateinit var onGetMetadataRequest: (request: GetMetadataRequest, response: StreamObserver<GetMetadataResponse>) -> Unit

    override fun getIdentifiedObjects(response: StreamObserver<GetIdentifiedObjectsResponse>): StreamObserver<GetIdentifiedObjectsRequest> =
        TestStreamObserver(response, onGetIdentifiedObjects)

    override fun getCustomersForContainer(responseObserver: StreamObserver<GetCustomersForContainerResponse>?): StreamObserver<GetCustomersForContainerRequest> =
        TestStreamObserver(responseObserver!!, onGetCustomersForContainer)

    override fun getMetadata(request: GetMetadataRequest, responseObserver: StreamObserver<GetMetadataResponse>) {
        runGrpc(request, responseObserver, onGetMetadataRequest)
    }

    private fun <T, U : StreamObserver<*>> runGrpc(request: T, response: U, handler: (T, U) -> Unit) {
        try {
            handler(request, response)
            response.onCompleted()
        } catch (t: Throwable) {
            response.onError(Status.ABORTED.withDescription(t.message).asRuntimeException())
        }
    }
}

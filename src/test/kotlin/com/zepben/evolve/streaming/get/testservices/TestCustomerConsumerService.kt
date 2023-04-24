/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.streaming.get.testservices

import com.zepben.protobuf.cc.*
import io.grpc.stub.StreamObserver

class TestCustomerConsumerService : CustomerConsumerGrpc.CustomerConsumerImplBase() {

    lateinit var onGetIdentifiedObjects: (request: GetIdentifiedObjectsRequest, response: StreamObserver<GetIdentifiedObjectsResponse>) -> Unit

    lateinit var onGetCustomersForContainer: (request: GetCustomersForContainerRequest, response: StreamObserver<GetCustomersForContainerResponse>) -> Unit
    override fun getIdentifiedObjects(response: StreamObserver<GetIdentifiedObjectsResponse>): StreamObserver<GetIdentifiedObjectsRequest> =
        TestStreamObserver(response, onGetIdentifiedObjects)

    override fun getCustomersForContainer(responseObserver: StreamObserver<GetCustomersForContainerResponse>?): StreamObserver<GetCustomersForContainerRequest> =
        TestStreamObserver(responseObserver!!, onGetCustomersForContainer)

}

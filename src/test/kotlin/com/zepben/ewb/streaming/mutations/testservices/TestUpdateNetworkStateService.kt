/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.streaming.mutations.testservices

import com.zepben.ewb.streaming.get.testservices.TestStreamObserver
import com.zepben.protobuf.ns.SetCurrentStatesRequest
import com.zepben.protobuf.ns.SetCurrentStatesResponse
import com.zepben.protobuf.ns.UpdateNetworkStateServiceGrpc
import io.grpc.stub.StreamObserver

internal class TestUpdateNetworkStateService : UpdateNetworkStateServiceGrpc.UpdateNetworkStateServiceImplBase() {

    lateinit var onSetCurrentStates: (request: SetCurrentStatesRequest, response: StreamObserver<SetCurrentStatesResponse>) -> Unit
    override fun setCurrentStates(responseObserver: StreamObserver<SetCurrentStatesResponse>): StreamObserver<SetCurrentStatesRequest> =
        TestStreamObserver(responseObserver, onSetCurrentStates)
}

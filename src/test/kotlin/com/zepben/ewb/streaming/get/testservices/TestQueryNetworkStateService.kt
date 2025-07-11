/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.streaming.get.testservices

import com.google.protobuf.Empty
import com.zepben.ewb.services.common.translator.toLocalDateTime
import com.zepben.ewb.streaming.data.CurrentStateEventBatch
import com.zepben.ewb.streaming.data.SetCurrentStatesStatus
import com.zepben.protobuf.ns.GetCurrentStatesRequest
import com.zepben.protobuf.ns.GetCurrentStatesResponse
import com.zepben.protobuf.ns.QueryNetworkStateServiceGrpc
import com.zepben.protobuf.ns.SetCurrentStatesResponse
import io.grpc.stub.StreamObserver
import java.time.LocalDateTime

internal class TestQueryNetworkStateService : QueryNetworkStateServiceGrpc.QueryNetworkStateServiceImplBase() {

    lateinit var onGetCurrentStates: (from: LocalDateTime?, to: LocalDateTime?) -> Sequence<CurrentStateEventBatch>
    lateinit var onBatchStatus: (status: SetCurrentStatesStatus) -> Unit

    override fun getCurrentStates(request: GetCurrentStatesRequest, responseObserver: StreamObserver<GetCurrentStatesResponse>) {
        onGetCurrentStates(request.fromTimestamp.toLocalDateTime(), request.toTimestamp.toLocalDateTime()).forEach { batch ->
            responseObserver.onNext(
                GetCurrentStatesResponse.newBuilder()
                    .setMessageId(batch.batchId)
                    .addAllEvent(batch.events.map { it.toPb() })
                    .build()
            )
        }

        responseObserver.onCompleted()
    }

    // NOTE: This is a crummy implementation that just passes the decoded status to a callback so you can make sure it was sent...
    override fun reportBatchStatus(responseObserver: StreamObserver<Empty>): StreamObserver<SetCurrentStatesResponse> {
        return object : StreamObserver<SetCurrentStatesResponse> {
            override fun onNext(value: SetCurrentStatesResponse) {
                onBatchStatus(SetCurrentStatesStatus.fromPb(value)!!)
                responseObserver.onNext(Empty.newBuilder().build())
            }

            override fun onError(t: Throwable) = throw NotImplementedError("If you want to test it, you better implement it.")

            override fun onCompleted() {
                responseObserver.onCompleted()
            }
        }
    }

}

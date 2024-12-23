/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.streaming.get.testservices

import com.zepben.evolve.services.common.translator.toLocalDateTime
import com.zepben.evolve.streaming.data.CurrentStateEventBatch
import com.zepben.protobuf.ns.GetCurrentStatesRequest
import com.zepben.protobuf.ns.GetCurrentStatesResponse
import com.zepben.protobuf.ns.QueryNetworkStateServiceGrpc
import io.grpc.stub.StreamObserver
import java.time.LocalDateTime

internal class TestQueryNetworkStateService : QueryNetworkStateServiceGrpc.QueryNetworkStateServiceImplBase() {

    lateinit var onGetCurrentStates: (from: LocalDateTime?, to: LocalDateTime?) -> Sequence<CurrentStateEventBatch>

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

}

/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.streaming.mutations

import com.zepben.evolve.services.common.translator.toLocalDateTime
import com.zepben.evolve.services.common.translator.toTimestamp
import com.zepben.protobuf.ns.*
import io.grpc.stub.StreamObserver
import java.time.LocalDateTime
import java.util.stream.Stream
import com.zepben.protobuf.ns.data.CurrentStateEvent as PBCurrentStateEvent
import com.zepben.protobuf.ns.data.SwitchStateEvent as PBSwitchStateEvent
import com.zepben.protobuf.ns.data.SwitchAction as PBSwitchAction
import com.zepben.protobuf.cim.iec61970.base.core.PhaseCode as PBPhaseCode

class QueryNetworkStateService @JvmOverloads constructor(
    private val onGetCurrentStates: ((from: LocalDateTime, to: LocalDateTime) -> Sequence<List<CurrentStateEvent>>)? = null,
    private val onGetCurrentStatesJava: ((from: LocalDateTime, to: LocalDateTime) -> Stream<List<CurrentStateEvent>>)? = null
) : QueryNetworkStateServiceGrpc.QueryNetworkStateServiceImplBase() {
    init {
        require((onGetCurrentStates != null) xor (onGetCurrentStatesJava != null)) {
            "Either 'onGetCurrentStates' or 'onGetCurrentStatesJava' must be provided, but not both"
        }
    }

    override fun getCurrentStates(request: GetCurrentStatesRequest, responseObserver: StreamObserver<GetCurrentStatesResponse>) {
        val from = request.from.toLocalDateTime()
        val to = request.to.toLocalDateTime()

        require(from != null) { "GetCurrentStatesRequest.from is not valid" }
        require(to != null) { "GetCurrentStatesRequest.to is not valid" }

        onGetCurrentStates?.let { it(from, to) }?.forEach { sendResponse(it, request.messageId, responseObserver) }
            ?: onGetCurrentStatesJava?.let{ it(from, to) }?.forEach { sendResponse(it, request.messageId, responseObserver) }

        responseObserver.onCompleted()
    }

    private fun sendResponse(currentStateEvents: List<CurrentStateEvent>, messageId: Long, responseObserver: StreamObserver<GetCurrentStatesResponse>){
        val builder = GetCurrentStatesResponse.newBuilder()
        builder.setMessageId(messageId)

        currentStateEvents.forEach { currentStateEvent: CurrentStateEvent ->
            builder.addEvent(PBCurrentStateEvent.newBuilder().apply {
                eventId = currentStateEvent.eventId
                timestamp = currentStateEvent.timestamp.toTimestamp()
                when (currentStateEvent) {
                    is SwitchStateEvent -> switch = PBSwitchStateEvent.newBuilder().apply {
                        mrid = currentStateEvent.mRID
                        action = PBSwitchAction.valueOf(currentStateEvent.action.name)
                        phases = PBPhaseCode.valueOf(currentStateEvent.phases.name)
                    }.build()

                    else -> throw NotImplementedError("There is currently no implementation of ${currentStateEvent::class.simpleName}.")
                }
            }.build())
        }

        responseObserver.onNext(builder.build())
    }
}

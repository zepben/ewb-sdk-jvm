/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.streaming.mutations

import com.zepben.evolve.cim.iec61970.base.core.PhaseCode
import com.zepben.evolve.conn.grpc.GrpcException
import com.zepben.evolve.services.common.translator.toLocalDateTime
import com.zepben.evolve.services.common.translator.toTimestamp
import com.zepben.evolve.streaming.data.*
import com.zepben.protobuf.ns.SetCurrentStatesRequest
import com.zepben.protobuf.ns.SetCurrentStatesResponse
import com.zepben.protobuf.ns.SetCurrentStatesResponse.StatusCase
import com.zepben.protobuf.ns.UpdateNetworkStateServiceGrpc
import io.grpc.stub.StreamObserver
import com.zepben.protobuf.ns.data.CurrentStateEvent.EventCase
import com.zepben.protobuf.ns.data.BatchSuccessful as PBBatchSuccessful
import com.zepben.protobuf.ns.data.ProcessingPaused as PBProcessingPaused
import com.zepben.protobuf.ns.data.BatchFailure as PBBatchFailure
import com.zepben.protobuf.ns.data.StateEventFailure as PBStateEventFailure
import com.zepben.protobuf.ns.data.StateEventUnknownMrid as PBStateEventUnknownMrid
import com.zepben.protobuf.ns.data.StateEventDuplicateMrid as PBStateEventDuplicateMrid
import com.zepben.protobuf.ns.data.StateEventInvalidMrid as PBStateEventInvalidMrid
import com.zepben.protobuf.ns.data.StateEventUnsupportedPhasing as PBStateEventUnsupportedPhasing

class UpdateNetworkStateService(
    private val onSetCurrentStates: (events: List<CurrentStateEvent>) -> SetCurrentStatesStatus
) : UpdateNetworkStateServiceGrpc.UpdateNetworkStateServiceImplBase() {

    override fun setCurrentStates(responseObserver: StreamObserver<SetCurrentStatesResponse>): StreamObserver<SetCurrentStatesRequest> =
        object : StreamObserver<SetCurrentStatesRequest> {
            override fun onNext(request: SetCurrentStatesRequest) {
                val builder = SetCurrentStatesResponse.newBuilder()
                builder.setMessageId(request.messageId)
                onSetCurrentStates(request.eventList.map {
                    val timeStamp = it.timestamp.toLocalDateTime()
                    require(timeStamp != null) { "SetCurrentStatesRequest.timestamp is not valid" }
                    when (it.eventCase) {
                        EventCase.SWITCH -> SwitchStateEvent(
                            it.eventId,
                            timeStamp,
                            it.switch.mrid,
                            SwitchAction.valueOf(it.switch.action.name),
                            PhaseCode.valueOf(it.switch.phases.name)
                        )

                        else -> throw UnsupportedOperationException("There is currently no implementation of ${it.eventCase}.")
                    }
                }).also {
                    when (it) {
                        is BatchSuccessful -> builder.apply { success = PBBatchSuccessful.newBuilder().build() }
                        is ProcessingPaused -> builder.apply {
                            paused = PBProcessingPaused.newBuilder().setSince(it.since.toTimestamp()).build()
                        }

                        is BatchFailure -> builder.apply { failure = it.toPb() }
                    }

                    if (builder.statusCase != StatusCase.STATUS_NOT_SET)
                        responseObserver.onNext(builder.build())
                }
            }

            override fun onError(e: Throwable) {
                throw GrpcException(
                    when (e) {
                        is NoSuchMethodError -> "Failed to serialise - are you using the correct version of evolve-grpc? Error was: NoSuchMethodError: ${e.localizedMessage}"
                        else -> "Failed to serialise Error was: ${e.javaClass.name} ${e.localizedMessage}. See server logs for more details."
                    },
                    e
                )
            }

            override fun onCompleted() {
                responseObserver.onCompleted()
            }
        }

    private fun BatchFailure.toPb(): PBBatchFailure {
        val builder = PBBatchFailure.newBuilder()
        builder.partialFailure = partialFailure
        failures.forEach {
            val eventFailureBuilder = PBStateEventFailure.newBuilder()
            eventFailureBuilder.eventId = it.eventId
            when (it) {
                is StateEventUnknownMrid -> eventFailureBuilder.unknownMrid = PBStateEventUnknownMrid.newBuilder().build()
                is StateEventDuplicateMrid -> eventFailureBuilder.duplicateMrid = PBStateEventDuplicateMrid.newBuilder().build()
                is StateEventInvalidMrid -> eventFailureBuilder.invalidMrid = PBStateEventInvalidMrid.newBuilder().build()
                is StateEventUnsupportedPhasing -> eventFailureBuilder.unsupportedPhasing = PBStateEventUnsupportedPhasing.newBuilder().build()
            }
            builder.addFailed(eventFailureBuilder.build())
        }
        return builder.build()
    }
}


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
import com.zepben.evolve.streaming.data.CurrentStateEvent
import com.zepben.evolve.streaming.data.SwitchStateEvent
import com.zepben.protobuf.ns.*
import io.grpc.stub.StreamObserver
import java.time.LocalDateTime
import java.util.stream.Stream
import kotlin.streams.asSequence
import com.zepben.protobuf.ns.data.CurrentStateEvent as PBCurrentStateEvent
import com.zepben.protobuf.ns.data.SwitchStateEvent as PBSwitchStateEvent
import com.zepben.protobuf.ns.data.SwitchAction as PBSwitchAction
import com.zepben.protobuf.cim.iec61970.base.core.PhaseCode as PBPhaseCode

/**
 * A service class that provides a simplified interface for retrieving current state events
 * via gRPC without exposing the underlying complexity of gRPC mechanisms.
 *
 * This class serves as a wrapper around the gRPC-generated service implementation,
 * allowing users to interact with it using a more convenient function type.
 *
 * @property onGetCurrentStates A function that retrieves a sequence of lists of
 * [CurrentStateEvent] objects within the specified time range.
 */
class QueryNetworkStateService(
    private val onGetCurrentStates: ((from: LocalDateTime, to: LocalDateTime) -> Sequence<List<CurrentStateEvent>>)
) : QueryNetworkStateServiceGrpc.QueryNetworkStateServiceImplBase() {

    /**
     * Secondary constructor for Java compatibility that initializes the service using
     * a method that returns Stream for retrieving current states.
     *
     * @param onGetCurrentStates A [GetCurrentStates] functional interface that provides
     * a method to fetch current state events.
     *
     * Examples:
     * ```
     * //Lambda expression
     * QueryNetworkStateService service = new QueryNetworkStateService((QueryNetworkStateService.GetCurrentStates) (from, to) -> Stream.of(List.of()));
     *
     * //Method reference
     * public class QueryNetworkStateServiceImpl {
     *     QueryNetworkStateService service = new QueryNetworkStateService(this::getCurrentStates);
     *
     *     Stream<List<CurrentStateEvent>> getCurrentStates(LocalDateTime from, LocalDateTime to){
     *         // implementation here
     *         return Stream.of(List.of());
     *     }
     * }
     * ```
     */
    constructor(onGetCurrentStates: GetCurrentStates) : this({from, to -> onGetCurrentStates.get(from, to).asSequence() })

    /**
     * Handles the incoming request for retrieving current state events.
     *
     * This method processes the provided [GetCurrentStatesRequest], retrieves the
     * corresponding current state events using the callback function passed in
     * the constructor, and sends the response back through the provided [StreamObserver].
     * It acts as a bridge between the gRPC request and the business logic that fetches the current state events.
     *
     * @param request The request object containing parameters for fetching current
     * state events, including the time range for the query. This parameter must not be null.
     * @param responseObserver The observer used to send the response back to the
     * client. This parameter must not be null.
     *
     * @throws IllegalArgumentException if the request contains invalid parameters,
     * such as when provided time is 0 or an end time that is before the start time.
     * @throws NotImplementedError if the callback function returns an implementation of [CurrentStateEvent] that is not supported.
     */
    override fun getCurrentStates(request: GetCurrentStatesRequest, responseObserver: StreamObserver<GetCurrentStatesResponse>) {
        val from = request.from.toLocalDateTime()
        val to = request.to.toLocalDateTime()

        require(from != null) { "GetCurrentStatesRequest.from is not valid" }
        require(to != null) { "GetCurrentStatesRequest.to is not valid" }
        require(to >= from) { "End time (GetCurrentStatesRequest.to) must not be before start time (GetCurrentStatesRequest.from)" }

        onGetCurrentStates(from, to).forEach { sendResponse(it, request.messageId, responseObserver) }

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

    /**
     * A functional interface that defines a contract for retrieving current state events
     * within a specified time range. This interface can be used as a lambda expression
     * or method reference, providing a concise way to implement the retrieval logic.
     *
     * Implementations of this interface are expected to provide a mechanism
     * to fetch current state events based on the provided start and end
     * timestamps.
     */
    interface GetCurrentStates{
        /**
         * Retrieves a stream of lists of current state events that occur
         * between the specified time range.
         *
         * @param from The starting timestamp of the time range from which to
         * retrieve current state events.
         * @param to The ending timestamp of the time range until which to
         * retrieve current state events.
         * @return A [Stream] of lists of [CurrentStateEvent] objects that
         * represent the current states within the specified time range.
         * If no events are found, an empty stream is returned.
         */
        fun get (from: LocalDateTime, to: LocalDateTime) : Stream<List<CurrentStateEvent>>
    }

}


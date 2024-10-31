/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.streaming.get

import com.zepben.evolve.services.common.translator.toLocalDateTime
import com.zepben.evolve.streaming.data.CurrentStateEvent
import com.zepben.protobuf.ns.GetCurrentStatesRequest
import com.zepben.protobuf.ns.GetCurrentStatesResponse
import com.zepben.protobuf.ns.QueryNetworkStateServiceGrpc
import io.grpc.Status
import io.grpc.stub.StreamObserver
import java.time.LocalDateTime
import java.util.stream.Stream
import kotlin.streams.asSequence

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
    private val onGetCurrentStates: (from: LocalDateTime?, to: LocalDateTime?) -> Sequence<List<CurrentStateEvent>>
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
    constructor(onGetCurrentStates: GetCurrentStates) : this({ from, to -> onGetCurrentStates.get(from, to).asSequence() })

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
     */
    override fun getCurrentStates(request: GetCurrentStatesRequest, responseObserver: StreamObserver<GetCurrentStatesResponse>) {
        try {
            val from = request.fromTimestamp.toLocalDateTime()
            val to = request.toTimestamp.toLocalDateTime()

            onGetCurrentStates(from, to).forEach { sendResponse(it, request.messageId, responseObserver) }

            responseObserver.onCompleted()
        } catch (e: Throwable) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.localizedMessage).asRuntimeException())
        }
    }

    private fun sendResponse(currentStateEvents: List<CurrentStateEvent>, messageId: Long, responseObserver: StreamObserver<GetCurrentStatesResponse>) {
        val responseBuilder = GetCurrentStatesResponse.newBuilder()
        responseBuilder.setMessageId(messageId)
        responseBuilder.addAllEvent(currentStateEvents.map { it.toPb() })
        responseObserver.onNext(responseBuilder.build())
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
    interface GetCurrentStates {
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
        fun get(from: LocalDateTime?, to: LocalDateTime?): Stream<List<CurrentStateEvent>>
    }

}

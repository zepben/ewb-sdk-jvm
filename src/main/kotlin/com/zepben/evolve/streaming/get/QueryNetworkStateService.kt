/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.streaming.get

import com.google.protobuf.Empty
import com.zepben.evolve.conn.grpc.GrpcException
import com.zepben.evolve.services.common.translator.toLocalDateTime
import com.zepben.evolve.streaming.data.CurrentStateEvent
import com.zepben.evolve.streaming.data.CurrentStateEventBatch
import com.zepben.evolve.streaming.data.SetCurrentStatesStatus
import com.zepben.protobuf.checkConnection.CheckConnectionRequest
import com.zepben.protobuf.ns.GetCurrentStatesRequest
import com.zepben.protobuf.ns.GetCurrentStatesResponse
import com.zepben.protobuf.ns.QueryNetworkStateServiceGrpc
import com.zepben.protobuf.ns.SetCurrentStatesResponse
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
 * @property onCurrentStatesStatus A callback triggered when the response status
 * of an event returned via [onGetCurrentStates] is received from the client.
 * @property onProcessingError A function that takes a [GrpcException] object. Called when [onCurrentStatesStatus]
 * throws an exception, or the [SetCurrentStatesResponse] is for an unknown event status.
 */
class QueryNetworkStateService(
    private val onGetCurrentStates: (from: LocalDateTime?, to: LocalDateTime?) -> Sequence<CurrentStateEventBatch>,
    private val onCurrentStatesStatus: (SetCurrentStatesStatus) -> Unit,
    private val onProcessingError: (GrpcException) -> Unit = {},
) : QueryNetworkStateServiceGrpc.QueryNetworkStateServiceImplBase() {

    /**
     * Secondary constructor for Java compatibility.
     *
     * @param onGetCurrentStates A [GetCurrentStates] functional interface that provides
     * a method to fetch current state events.
     * @param onCurrentStatesStatus A [CurrentStatesStatusHandler] functional interface that provides a method which
     * is called when the response status of an event returned via [onGetCurrentStates] is received from the client.
     * @param onProcessingError A [ProcessingErrorHandler] functional interface that provides a method which is
     * called when there is an error handling a response status of an event.
     *
     * Examples:
     * ```java
     * // Lambda expression
     * QueryNetworkStateService service = new QueryNetworkStateService(
     *     (QueryNetworkStateService.GetCurrentStates) (from, to) -> Stream.of(List.of()),
     *     (QueryNetworkStateService.CurrentStatesStatusHandler) (eventStatus) -> System.out.println(eventStatus.getBatchId()),
     *     (QueryNetworkStateService.ProcessingErrorHandler) (error) -> error.printStackTrace(System.out)
     * );
     *
     * // Method reference
     * public class QueryNetworkStateServiceImpl {
     *
     *     QueryNetworkStateService service = new QueryNetworkStateService(
     *         this::getCurrentStates,
     *         this::onBatchStatus,
     *         this::onProcessorError
     *     );
     *
     *     Stream<List<CurrentStateEvent>> getCurrentStates(LocalDateTime from, LocalDateTime to) {
     *         // implementation here
     *         return Stream.of(List.of());
     *     }
     *
     *     void onBatchStatus(SetCurrentStatesStatus eventStatus) {
     *         // implementation here
     *         System.out.println(eventStatus.getBatchId());
     *     }
     *
     *     void onProcessorError(GrpcException error) {
     *         // implementation here
     *         error.printStackTrace(System.out);
     *     }
     *
     * }
     * ```
     */
    @JvmOverloads
    constructor(
        onGetCurrentStates: GetCurrentStates,
        onCurrentStatesStatus: CurrentStatesStatusHandler,
        onProcessingError: ProcessingErrorHandler = ProcessingErrorHandler {},
    ) :
        this(
            { from, to -> onGetCurrentStates.get(from, to).asSequence() },
            { eventStatus -> onCurrentStatesStatus.handle(eventStatus) },
            { error -> onProcessingError.handle(error) }
        )

    /**
     * Handles the incoming request for retrieving current state events.
     *
     * You shouldn't be calling this method directly, it will be invoked automatically via the gRPC engine. Each
     * [GetCurrentStatesRequest] retrieves the corresponding current state events using the [onGetCurrentStates]
     * callback function passed in the constructor, and sends the response back through the provided [StreamObserver].
     *
     * It acts as a bridge between the gRPC request and the business logic that fetches the current state events.
     *
     * @param request The request object containing parameters for fetching current state events, including the time
     * range for the query.
     * @param responseObserver The observer used to send responses back to the client.
     */
    override fun getCurrentStates(request: GetCurrentStatesRequest, responseObserver: StreamObserver<GetCurrentStatesResponse>) {
        try {
            val from = request.fromTimestamp.toLocalDateTime()
            val to = request.toTimestamp.toLocalDateTime()

            onGetCurrentStates(from, to).forEach { batch ->
                val response = GetCurrentStatesResponse.newBuilder()
                    .setMessageId(batch.batchId)
                    .addAllEvent(batch.events.map { it.toPb() })
                    .build()

                responseObserver.onNext(response)
            }

            responseObserver.onCompleted()
        } catch (e: Throwable) {
            responseObserver.onError(Status.fromThrowable(e).asRuntimeException())
        }
    }

    /**
     * Handles incoming status reports in response to an event batch returned via [getCurrentStates].
     *
     * You shouldn't be calling this method directly, it will be invoked automatically via the gRPC engine. Each
     * [SetCurrentStatesResponse] will trigger the [onCurrentStatesStatus] callback function passed in the constructor.
     *
     * It acts as a bridge between the gRPC request and the business logic that validates/logs the status of event
     * handling. Any errors in this handling, or unexpected status message will trigger the [onProcessingError]
     * callback function passed in the constructor.
     *
     * @param responseObserver The observer used to a completion back to the client.
     * @return An observer the gRPC engine can use on each request it receives.
     */
    override fun reportBatchStatus(responseObserver: StreamObserver<Empty>): StreamObserver<SetCurrentStatesResponse> =
        object : StreamObserver<SetCurrentStatesResponse> {
            override fun onNext(statusResponse: SetCurrentStatesResponse) {
                val status = SetCurrentStatesStatus.fromPb(statusResponse)
                if (status != null) {
                    try {
                        onCurrentStatesStatus(status)
                    } catch (e: Throwable) {
                        val message = "Exception thrown in status response handler for batch `${statusResponse.messageId}`: ${e.localizedMessage}"
                        onProcessingError(GrpcException(message, e))
                    }
                } else
                    onProcessingError(GrpcException("Failed to decode status response for batch `${statusResponse.messageId}`: Unsupported type ${statusResponse.statusCase}"))
            }

            override fun onError(e: Throwable) = throw e

            override fun onCompleted() = responseObserver.onCompleted()

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
    fun interface GetCurrentStates {
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
        fun get(from: LocalDateTime?, to: LocalDateTime?): Stream<CurrentStateEventBatch>
    }

    /**
     * A functional interface that defines a contract for processing responses to current
     * state events returned from the client. This interface can be used as a lambda expression
     * or method reference, providing a concise way to implement the handling logic.
     */
    fun interface CurrentStatesStatusHandler {
        /**
         * Handle the status of an event.
         *
         * @param eventStatus The status of an event.
         */
        fun handle(eventStatus: SetCurrentStatesStatus)
    }

    /**
     * A functional interface that defines a contract for handling a [GrpcException] object raised when [onCurrentStatesStatus]
     * throws an exception, or the [SetCurrentStatesResponse] is for an unknown event status.
     */
    fun interface ProcessingErrorHandler {
        /**
         * Handle an error raised in the processing of event status responses.
         *
         * @param error A [GrpcException] indicating what went wrong in the processing.
         */
        fun handle(error: GrpcException)
    }

    override fun checkConnection(request: CheckConnectionRequest?, responseObserver: StreamObserver<Empty>) {
        responseObserver.onNext(Empty.getDefaultInstance())
        responseObserver.onCompleted()
    }
}

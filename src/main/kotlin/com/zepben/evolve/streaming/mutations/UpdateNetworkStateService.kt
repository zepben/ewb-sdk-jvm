/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.streaming.mutations

import com.google.protobuf.Empty
import com.zepben.evolve.conn.grpc.GrpcException
import com.zepben.evolve.streaming.data.BatchFailure
import com.zepben.evolve.streaming.data.CurrentStateEvent
import com.zepben.evolve.streaming.data.SetCurrentStatesStatus
import com.zepben.protobuf.connection.CheckConnectionRequest
import com.zepben.protobuf.ns.SetCurrentStatesRequest
import com.zepben.protobuf.ns.SetCurrentStatesResponse
import com.zepben.protobuf.ns.UpdateNetworkStateServiceGrpc
import io.grpc.stub.StreamObserver
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

/**
 * A service class that provides a simplified interface for updating network state events
 * via gRPC without exposing the underlying complexity of gRPC mechanisms.
 *
 * This class serves as a wrapper around the gRPC-generated service implementation,
 * allowing users to update the network state using a more convenient function type.
 *
 * @property onSetCurrentStates A function that takes a list of [CurrentStateEvent] objects
 * and returns a [CompletableFuture] of [SetCurrentStatesStatus], which reflects the success or failure of
 * the update process.
 * @property onProcessingError A function that takes a [Throwable] object. Called when onSetCurrentStates
 * throws an exception or the [CompletableFuture] is completed with exception.
 * @property timeout Duration (in seconds) for the future to complete. It ensures that the future will complete and
 * avoid blocking the gRPC connection thread. Note that this overrides the timeout (if set) of the future returned
 * by onSetCurrentStates.
 */
class UpdateNetworkStateService(
    private val onSetCurrentStates: (batchId: Long, events: List<CurrentStateEvent>) -> CompletableFuture<SetCurrentStatesStatus>,
    private val onProcessingError: (Throwable) -> Unit = {},
    private val timeout: Long = 60
) : UpdateNetworkStateServiceGrpc.UpdateNetworkStateServiceImplBase() {

    init {
        require(timeout > 0) { "Property timeout must be an integer value greater than 0" }
    }

    /**
     * Handles streaming requests for setting current state events and responds with the result of the operation.
     *
     * This method is a bidirectional streaming gRPC implementation that processes incoming
     * [SetCurrentStatesRequest] objects from the client, applies the provided state events using the callback function
     * passed in the constructor, and sends back a [SetCurrentStatesResponse] indicating the outcome of the update operation.
     *
     * It allows clients to stream multiple state events for asynchronous processing. As each event is processed,
     * the service responds with the status of the operation in real-time, without waiting for all events to be received.
     *
     * @param responseObserver The observer used to send the response back to the client. This parameter must not be null.
     *
     * @return An observer that listens for incoming [SetCurrentStatesRequest] messages. The incoming requests are processed
     * by invoking the callback function passed via the constructor. Once the state events are applied, a response is sent
     * back through the [responseObserver].
     */
    override fun setCurrentStates(responseObserver: StreamObserver<SetCurrentStatesResponse>): StreamObserver<SetCurrentStatesRequest> =

        object : StreamObserver<SetCurrentStatesRequest> {

            /**
             * An atomic int used for tracking when to send the `onCompleted` response.
             *
             * Since the callback used to handle requests can run on a separate thread, there are two possible places we may need to send the
             * `onCompleted` response message:
             * 1. In direct response to the `onCompleted` request if there are no outstanding callbacks.
             * 2. When the last callback completes if a previous `onCompleted` request has been received.
             *
             * In order to prevent race conditions, this should be handled by a single variable. The race condition exists because the completable
             * returned from `onSetCurrentStates` could be running on any thread.
             *
             * gRPC guarantees we will not receive the `onCompleted` request until after all `onNext` calls have been made. We guarantee that we
             * increment this counter before we return from `onNext`, and decrement it when a callback is completed.
             *
             * By treating the `onCompleted` request to be the completion of an initial task (hence initialised to 1), it allows us to track when
             * to send the `onCompleted` response, as both the completion of the last callback, and the handling of the `onCompleted` will decrement
             * our counter.
             *
             * Given the only way of this number being zero is after all callbacks have been completed, and the `onCompleted` request being received,
             * we can just check in the two places for a zero value as an indicator the `onCompleted` response needs to be sent.
             */
            private val completeWhenZero = AtomicInteger(1)

            override fun onNext(request: SetCurrentStatesRequest) {
                try {
                    onSetCurrentStates(request.messageId, request.eventList.map { CurrentStateEvent.fromPb(it) })
                        .orTimeout(timeout, TimeUnit.SECONDS)
                        .also { completable ->
                            completeWhenZero.incrementAndGet()

                            completable.whenComplete { result, e ->
                                if (result != null)
                                    responseObserver.onNext(result.toPb())
                                else if (e != null)
                                    handleError(request, "Error raised by the state updater", e)

                                if (completeWhenZero.decrementAndGet() == 0)
                                    responseObserver.onCompleted()
                            }
                        }

                } catch (e: Throwable) {
                    handleError(request, "onSetCurrentStates has error", e)
                }
            }

            override fun onError(e: Throwable) {
                throw e
            }

            override fun onCompleted() {
                if (completeWhenZero.decrementAndGet() == 0)
                    responseObserver.onCompleted()
            }

            private fun handleError(request: SetCurrentStatesRequest, message: String, e: Throwable) {
                onProcessingError(GrpcException("$message for batch `${request.messageId}`: ${e.localizedMessage}", e))
                responseObserver.onNext(BatchFailure(request.messageId, false, listOf()).toPb())
            }

        }

    override fun checkConnection(request: CheckConnectionRequest?, responseObserver: StreamObserver<Empty>) {
        responseObserver.onNext(Empty.getDefaultInstance())
        responseObserver.onCompleted()
    }

}

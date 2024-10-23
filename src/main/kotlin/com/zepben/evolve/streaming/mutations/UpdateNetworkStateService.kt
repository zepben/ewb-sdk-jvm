/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.streaming.mutations

import com.zepben.evolve.conn.grpc.GrpcException
import com.zepben.evolve.streaming.data.*
import com.zepben.protobuf.ns.SetCurrentStatesRequest
import com.zepben.protobuf.ns.SetCurrentStatesResponse
import com.zepben.protobuf.ns.UpdateNetworkStateServiceGrpc
import io.grpc.Status
import io.grpc.stub.StreamObserver

/**
 * A service class that provides a simplified interface for updating network state events
 * via gRPC without exposing the underlying complexity of gRPC mechanisms.
 *
 * This class serves as a wrapper around the gRPC-generated service implementation,
 * allowing users to update the network state using a more convenient function type.
 *
 * @property onSetCurrentStates A function that takes a list of [CurrentStateEvent] objects
 * and returns a [SetCurrentStatesStatus], which reflects the success or failure of the update process.
 */
class UpdateNetworkStateService(
    private val onSetCurrentStates: (events: List<CurrentStateEvent>) -> SetCurrentStatesStatus
) : UpdateNetworkStateServiceGrpc.UpdateNetworkStateServiceImplBase() {

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
            override fun onNext(request: SetCurrentStatesRequest) {
                try {
                    onSetCurrentStates(request.eventList.map { CurrentStateEvent.fromPb(it) })
                        .also {
                            val responseBuilder = SetCurrentStatesResponse.newBuilder()
                            responseBuilder.setMessageId(request.messageId)
                            when (it) {
                                is BatchSuccessful -> responseBuilder.success = it.toPb()
                                is ProcessingPaused -> responseBuilder.paused = it.toPb()
                                is BatchFailure -> responseBuilder.failure = it.toPb()
                            }
                            responseObserver.onNext(responseBuilder.build())
                        }
                } catch (e: Throwable) {
                    responseObserver.onError(Status.INTERNAL.withDescription(e.localizedMessage).asRuntimeException())
                }
            }

            override fun onError(e: Throwable) {
                throw GrpcException("Serialization failed due to: ${e.localizedMessage}", e)
            }

            override fun onCompleted() {
                responseObserver.onCompleted()
            }
        }
}

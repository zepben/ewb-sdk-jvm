/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.streaming.get

import com.zepben.evolve.streaming.data.*
import com.zepben.evolve.streaming.grpc.GrpcChannel
import com.zepben.evolve.streaming.grpc.GrpcClient
import com.zepben.protobuf.ns.SetCurrentStatesRequest
import com.zepben.protobuf.ns.SetCurrentStatesResponse
import com.zepben.protobuf.ns.UpdateNetworkStateServiceGrpc
import io.grpc.CallCredentials
import io.grpc.Channel
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.stream.Stream
import kotlin.streams.asSequence
import kotlin.streams.asStream

/**
 * A client class that provides functionality to interact with the gRPC service for updating network states.
 *
 * @property stub The gRPC stub to be used to communicate with the server
 * @param executor An optional [ExecutorService] to use with the stub. If provided, it will be cleaned up when this client is closed.
 */
class UpdateNetworkStateClient(
    private val stub: UpdateNetworkStateServiceGrpc.UpdateNetworkStateServiceStub,
    executor: ExecutorService?
) : GrpcClient(executor) {

    /**
     * Create a [UpdateNetworkStateClient]
     *
     * @param channel [Channel] to build a stub from.
     * @param callCredentials [CallCredentials] to be attached to the stub.
     */
    @JvmOverloads
    constructor(channel: Channel, callCredentials: CallCredentials? = null) :
        this(
            UpdateNetworkStateServiceGrpc.newStub(channel).apply { callCredentials?.let { withCallCredentials(it) } },
            executor = Executors.newSingleThreadExecutor()
        )

    /**
     * Create a [UpdateNetworkStateClient]
     *
     * @param channel [GrpcChannel] to build a stub from.
     * @param callCredentials [CallCredentials] to be attached to the stub.
     */
    @JvmOverloads
    constructor(channel: GrpcChannel, callCredentials: CallCredentials? = null) :
        this(
            UpdateNetworkStateServiceGrpc.newStub(channel.channel).apply { callCredentials?.let { withCallCredentials(it) } },
            executor = Executors.newSingleThreadExecutor()
        )

    /**
     * Sends a single batch of current state events to the gRPC service for processing.
     *
     * This method allows for sending a single batch of current state events to the
     * `UpdateNetworkStateServiceGrpc` service using the gRPC stub provided in the constructor.
     *
     * @param batch A list of [CurrentStateEvent] objects representing a single batch of events
     * to be processed by the gRPC service.
     *
     * @return A [SetCurrentStatesStatus] object representing the status of the batch after
     * being processed by the service.
     */
    fun setCurrentStates(batch: List<CurrentStateEvent>): SetCurrentStatesStatus =
        setCurrentStates(sequenceOf(batch)).first()

    /**
     * Sends batches of current state events to the gRPC service for processing.
     *
     * This method is responsible for streaming a sequence of current state event batches to the
     * `UpdateNetworkStateServiceGrpc` service using the gRPC stub provided in the constructor.
     *
     * @param batches A sequence of lists, where each list contains multiple [CurrentStateEvent] objects
     * representing a batch of events to be processed by the gRPC service.
     *
     * @return A sequence of [SetCurrentStatesStatus] objects representing the status of each batch
     * after being processed by the service.
     */
    fun setCurrentStates(batches: Sequence<List<CurrentStateEvent>>): Sequence<SetCurrentStatesStatus> {
        val results = mutableListOf<SetCurrentStatesStatus>()
        val responseObserver = AwaitableStreamObserver<SetCurrentStatesResponse> { response ->
            val status = when (response.statusCase) {
                SetCurrentStatesResponse.StatusCase.SUCCESS -> BatchSuccessful.fromPb(response.success)
                SetCurrentStatesResponse.StatusCase.PAUSED -> ProcessingPaused.fromPb(response.paused)
                SetCurrentStatesResponse.StatusCase.FAILURE -> BatchFailure.fromPb(response.failure)
                else -> null
            }

            if (status != null)
                results.add(status)
        }

        val request = stub.setCurrentStates(responseObserver)
        val requestBuilder = SetCurrentStatesRequest.newBuilder()
        batches.forEach { batch ->
            requestBuilder.addAllEvent(batch.map { it.toPb() })
            request.onNext(requestBuilder.build())
            requestBuilder.clearEvent()
        }

        request.onCompleted()
        responseObserver.await()

        return results.asSequence()
    }

    /**
     * Sends batches of current state events to the gRPC service for processing using a Java [Stream].
     *
     * This method streams batches of current state events to the `UpdateNetworkStateServiceGrpc` service
     * using the gRPC stub provided in the constructor.
     *
     * @param batches A Java [Stream] of lists, where each list contains multiple [CurrentStateEvent] objects
     * representing a batch of events to be processed by the gRPC service.
     *
     * @return A Java [Stream] of [SetCurrentStatesStatus] objects representing the status of each batch
     * after being processed by the service.
     */
    fun setCurrentStates(batches: Stream<List<CurrentStateEvent>>): Stream<SetCurrentStatesStatus> =
        setCurrentStates(batches.asSequence()).asStream()

}

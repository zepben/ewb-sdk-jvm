/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.streaming.get

import com.zepben.evolve.services.common.translator.toTimestamp
import com.zepben.evolve.streaming.data.CurrentStateEvent
import com.zepben.evolve.streaming.data.CurrentStateEventBatch
import com.zepben.evolve.streaming.grpc.GrpcChannel
import com.zepben.evolve.streaming.grpc.GrpcClient
import com.zepben.protobuf.ns.GetCurrentStatesRequest
import com.zepben.protobuf.ns.GetCurrentStatesResponse
import com.zepben.protobuf.ns.QueryNetworkStateServiceGrpc
import io.grpc.CallCredentials
import io.grpc.Channel
import java.time.LocalDateTime
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.stream.Stream
import kotlin.streams.asStream

/**
 * A client class that provides functionality to interact with the gRPC service for querying network states.
 *
 * @property stub The gRPC stub to be used to communicate with the server
 * @param executor An optional [ExecutorService] to use with the stub. If provided, it will be cleaned up when this client is closed.
 */
class QueryNetworkStateClient(
    private val stub: QueryNetworkStateServiceGrpc.QueryNetworkStateServiceStub,
    executor: ExecutorService?
) : GrpcClient(executor) {

    /**
     * Create a [QueryNetworkStateClient]
     *
     * @param channel [Channel] to build a stub from.
     * @param callCredentials [CallCredentials] to be attached to the stub.
     */
    @JvmOverloads
    constructor(channel: Channel, callCredentials: CallCredentials? = null) :
        this(
            QueryNetworkStateServiceGrpc.newStub(channel).apply { callCredentials?.let { withCallCredentials(it) } },
            executor = Executors.newSingleThreadExecutor()
        )

    /**
     * Create a [QueryNetworkStateClient]
     *
     * @param channel [GrpcChannel] to build a stub from.
     * @param callCredentials [CallCredentials] to be attached to the stub.
     */
    @JvmOverloads
    constructor(channel: GrpcChannel, callCredentials: CallCredentials? = null) :
        this(
            QueryNetworkStateServiceGrpc.newStub(channel.channel).apply { callCredentials?.let { withCallCredentials(it) } },
            executor = Executors.newSingleThreadExecutor()
        )

    /**
     * Retrieves a sequence of lists containing [CurrentStateEvent] objects, representing the network states
     * within a specified time range.
     *
     * @param queryId A unique identifier for the query being processed.
     * @param from The start time, as a [LocalDateTime], for the query range.
     * @param to The end time, as a [LocalDateTime], for the query range.
     *
     * @return A [Sequence] of [List]s of [CurrentStateEvent] objects, where each list represents a
     * collection of network state events in the specified time range.
     */
    fun getCurrentStates(queryId: Long, from: LocalDateTime, to: LocalDateTime): Sequence<CurrentStateEventBatch> {
        val results = mutableListOf<CurrentStateEventBatch>()
        val responseObserver = AwaitableStreamObserver<GetCurrentStatesResponse>{ response ->
            results.add(CurrentStateEventBatch(response.messageId, response.eventList.map { CurrentStateEvent.fromPb(it) }))
        }
        val request = GetCurrentStatesRequest.newBuilder().also {
            it.messageId = queryId
            it.fromTimestamp = from.toTimestamp()
            it.toTimestamp = to.toTimestamp()
        }.build()

        stub.getCurrentStates(request, responseObserver)

        responseObserver.await()

        return results.asSequence()
    }

    /**
     * Retrieves a stream of lists containing [CurrentStateEvent] objects, representing the network states
     * within a specified time range.
     *
     * @param queryId A unique identifier for the query being processed.
     * @param from The start time, as a [LocalDateTime], for the query range.
     * @param to The end time, as a [LocalDateTime], for the query range.
     *
     * @return A [Stream] of [List]s of [CurrentStateEvent] objects, where each list represents a collection
     * of network state events in the specified time range.
     */
    fun getCurrentStatesStream(queryId: Long, from: LocalDateTime, to: LocalDateTime): Stream<CurrentStateEventBatch> =
        getCurrentStates(queryId, from, to).asStream()

}

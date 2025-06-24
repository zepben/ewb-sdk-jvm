/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.streaming.get

import com.google.protobuf.Empty
import com.zepben.ewb.services.common.translator.toTimestamp
import com.zepben.ewb.streaming.data.CurrentStateEvent
import com.zepben.ewb.streaming.data.CurrentStateEventBatch
import com.zepben.ewb.streaming.data.SetCurrentStatesStatus
import com.zepben.ewb.streaming.grpc.GrpcChannel
import com.zepben.ewb.streaming.grpc.GrpcClient
import com.zepben.protobuf.ns.GetCurrentStatesRequest
import com.zepben.protobuf.ns.GetCurrentStatesResponse
import com.zepben.protobuf.ns.QueryNetworkStateServiceGrpc
import io.grpc.CallCredentials
import io.grpc.Channel
import org.slf4j.LoggerFactory
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
    private val networkStateIssues: NetworkStateIssues,
    executor: ExecutorService?
) : GrpcClient(executor) {

    private val logger = LoggerFactory.getLogger("QueryNetworkStateClient")
    /**
     * Create a [QueryNetworkStateClient]
     *
     * @param channel [Channel] to build a stub from.
     * @param callCredentials [CallCredentials] to be attached to the stub.
     */
    @JvmOverloads
    constructor(channel: Channel, networkStateIssues: NetworkStateIssues, callCredentials: CallCredentials? = null) :
        this(
            QueryNetworkStateServiceGrpc.newStub(channel).apply { callCredentials?.let { withCallCredentials(it) } },
            networkStateIssues,
            executor = Executors.newSingleThreadExecutor()
        )

    /**
     * Create a [QueryNetworkStateClient]
     *
     * @param channel [GrpcChannel] to build a stub from.
     * @param callCredentials [CallCredentials] to be attached to the stub.
     */
    @JvmOverloads
    constructor(channel: GrpcChannel, networkStateIssues: NetworkStateIssues, callCredentials: CallCredentials? = null) :
        this(
            QueryNetworkStateServiceGrpc.newStub(channel.channel).apply { callCredentials?.let { withCallCredentials(it) } },
            networkStateIssues,
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
        val responseObserver = AwaitableStreamObserver<GetCurrentStatesResponse> { response ->
            logger.info("Retrieved event: ${response.messageId}")
            try {
                results.add(CurrentStateEventBatch(response.messageId, response.eventList.map { CurrentStateEvent.fromPb(it) }))
            } catch (ex: Exception) {
                logger.error("${response.messageId} could not be deserialised:", ex)
                networkStateIssues.invalidBacklogEvent.track("${response.messageId} could not be deserialised ${ex.message}")
            }
        }

        try {
            logger.info("Retrieving states from: $from until $to")
            val request = GetCurrentStatesRequest.newBuilder().also {
                it.messageId = queryId
                it.fromTimestamp = from.toTimestamp()
                it.toTimestamp = to.toTimestamp()
            }.build()

            stub.getCurrentStates(request, responseObserver)

            responseObserver.await()

        } catch (ex: Exception) {
            logger.error("Failed to convert current state event: ${ex.message}", ex)
        }
        return results.asSequence()
    }

    /**
     * Send a response to a previous [getCurrentStates] request to let the server know how we handled its response.
     *
     * @param status The batch status to report.
     */
    fun reportBatchStatus(status: SetCurrentStatesStatus) {
        //
        // NOTE: We could make this reuse the same observers and close them when the client is shut down, but we expect
        //       this will only be called once, so we have just inlined it for code simplicity.
        //
        val statusResponseObserver = AwaitableStreamObserver<Empty> {}
        val statusRequestObserver = stub.reportBatchStatus(statusResponseObserver)

        statusRequestObserver.onNext(status.toPb())
        statusRequestObserver.onCompleted()

        statusResponseObserver.await()
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

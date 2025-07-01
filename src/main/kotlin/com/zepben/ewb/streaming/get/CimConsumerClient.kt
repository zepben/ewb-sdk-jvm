/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.streaming.get

import com.zepben.ewb.cim.iec61970.base.core.IdentifiedObject
import com.zepben.ewb.services.common.BaseService
import com.zepben.ewb.services.common.meta.ServiceInfo
import com.zepben.ewb.services.common.meta.fromPb
import com.zepben.ewb.services.common.translator.BaseProtoToCim
import com.zepben.ewb.streaming.grpc.GrpcClient
import com.zepben.ewb.streaming.grpc.GrpcResult
import com.zepben.protobuf.metadata.GetMetadataRequest
import com.zepben.protobuf.metadata.GetMetadataResponse
import java.io.IOException
import java.util.concurrent.ExecutorService

/**
 * A result to use when multiple objects are requested.
 * @property objects A Map of mRID to its IdentifiedObject that were processed.
 * @property failed The set of mRIDs that failed to be processed.
 */
data class MultiObjectResult(val objects: MutableMap<String, IdentifiedObject> = mutableMapOf(), val failed: MutableSet<String> = mutableSetOf())

/**
 * Represents the result of deserialising a protobuf message and adding it to a service.
 * @property identifiedObject The [IdentifiedObject] that was deserialised, or null if it couldn't be added to the service
 * @property mRID The corresponding mRID of [identifiedObject]. Typically only used if [identifiedObject] is null.
 */
data class ExtractResult(val identifiedObject: IdentifiedObject?, val mRID: String)

/**
 * Base class that defines some helpful functions when producer clients are sending to the server.
 *
 * WARNING: The [MultiObjectResult] operations below are not atomic upon a [BaseService], and thus if processing fails partway through, any previously
 * successful additions will have been processed by the service, and thus you may have an incomplete service. Also note that adding to the service may not
 * occur for an object if another object with the same mRID is already present in service. [MultiObjectResult.failed] can be used to check for mRIDs that
 * were not found or retrieved but not added to service (this should not be the case unless you are processing things concurrently).
 *
 * @param T The type of service used by this client.
 * @property service The service to store fetched objects in. Descendant of [BaseService] defined by [T]
 */
abstract class CimConsumerClient<T : BaseService, U : BaseProtoToCim>(executor: ExecutorService?) : GrpcClient(executor) {

    abstract val service: T

    /**
     * The protobuf to cim converter.
     */
    protected abstract val protoToCim: U

    internal var serviceInfo: ServiceInfo? = null

    /**
     * Retrieve the object with the given [mRID] and store the result in the [service].
     *
     * Exceptions that occur during sending will be caught and passed to all error handlers that have been registered by [addErrorHandler].
     *
     * @param mRID The mRID to retrieve.
     *
     * @return A [GrpcResult] with a result of one of the following:
     * - When [GrpcResult.wasSuccessful], the item found, accessible via [GrpcResult.value].
     * - When [GrpcResult.wasFailure], the error that occurred retrieving or processing the object, accessible via [GrpcResult.thrown]. One of:
     *    - [NoSuchElementException] if the object could not be found.
     *    - The gRPC error that occurred while retrieving the object
     */
    fun getIdentifiedObject(mRID: String): GrpcResult<IdentifiedObject> = tryRpc {
        processIdentifiedObjects(sequenceOf(mRID)).firstOrNull()?.identifiedObject
            ?: throw NoSuchElementException("No object with mRID $mRID could be found.")
    }

    /**
     * Retrieve the objects with the given [mRIDs] and store the results in the [service].
     *
     * Exceptions that occur during processing will be caught and passed to all error handlers that have been registered by [addErrorHandler].
     *
     * @param mRIDs The mRIDs to retrieve.
     *
     * @return A [GrpcResult] with a result of one of the following:
     * - When [GrpcResult.wasSuccessful], a map containing the retrieved objects keyed by mRID, accessible via [GrpcResult.value]. If an item was not found, or
     * couldn't be added to [service], it will be excluded from the map and its mRID will be present in [MultiObjectResult.failed] (see [BaseService.add]).
     * - When [GrpcResult.wasFailure], the error that occurred retrieving or processing the object, accessible via [GrpcResult.thrown].
     * Note the [CimConsumerClient] warning in this case.
     */
    fun getIdentifiedObjects(mRIDs: Iterable<String>): GrpcResult<MultiObjectResult> = getIdentifiedObjects(mRIDs.asSequence())

    /**
     * Retrieve the objects with the given [mRIDs] and store the results in the [service].
     *
     * Exceptions that occur during processing will be caught and passed to all error handlers that have been registered by [addErrorHandler].
     *
     * @param mRIDs The mRIDs to retrieve.
     *
     * @return A [GrpcResult] with a result of one of the following:
     * - When [GrpcResult.wasSuccessful], a map containing the retrieved objects keyed by mRID, accessible via [GrpcResult.value]. If an item was not found, or
     * couldn't be added to [service], it will be excluded from the map and its mRID will be present in [MultiObjectResult.failed] (see [BaseService.add]).
     * - When [GrpcResult.wasFailure], the error that occurred retrieving or processing the object, accessible via [GrpcResult.thrown].
     * Note the [CimConsumerClient] warning in this case.
     */
    fun getIdentifiedObjects(mRIDs: Sequence<String>): GrpcResult<MultiObjectResult> = handleMultiObjectRPC(mRIDs) {
        processIdentifiedObjects(mRIDs)
    }

    /**
     * Process the requested identified objects.
     *
     * @param mRIDs The mRIDs of the requested identified objects.
     * @return A sequence of [ExtractResult] containing the identified objects if found, null otherwise.
     */
    protected abstract fun processIdentifiedObjects(mRIDs: Sequence<String>): Sequence<ExtractResult>

    /**
     * A helper function to split responses into batches of 1000.
     *
     * @param mRIDs The mRIDs to send.
     * @param addMrid A callback to add the mRID to the message.
     * @param sendBatch A callback to send the batch when required.
     */
    protected fun <T> batchSend(mRIDs: Sequence<T>, addMrid: (T) -> Unit, sendBatch: () -> Unit) {
        var count = 0
        mRIDs.forEach {
            if (++count % 1000 == 0)
                sendBatch()
            addMrid(it)
        }
        sendBatch()
    }

    /**
     * A helper function for adding items received from protobuf to the service.
     *
     * @param CIM The type of CIM object being added.
     * @param mRID The mRID of the CIM object.
     * @param addFromPb A callback to convert and add the protobuf object, scoped to the [protoToCim] object.
     * @return A result object containing either an existing object with the same mRID, or the result of the [addFromPb] call.
     */
    protected inline fun <reified CIM : IdentifiedObject> extractResult(mRID: String, addFromPb: U.() -> CIM?): ExtractResult =
        ExtractResult(getOrAdd(mRID, addFromPb), mRID)

    /**
     * A helper function for check if an item already exists before creating it.
     *
     * @param CIM The type of CIM object being added.
     * @param mRID The mRID of the CIM object.
     * @param addFromPb A callback to convert and add the protobuf object, scoped to the [protoToCim] object.
     * @return Either an existing object with the same mRID, or the result of the [addFromPb] call.
     */
    protected inline fun <reified CIM : IdentifiedObject> getOrAdd(mRID: String, addFromPb: U.() -> CIM?): CIM? =
        service.get(CIM::class, mRID) ?: protoToCim.addFromPb()

    /**
     * @param processor The function which returns results to be handled.
     * @param mRIDs The mRIDs expected to be retrieved from a call to [processor]
     */
    protected fun handleMultiObjectRPC(mRIDs: Sequence<String>? = null, processor: () -> Sequence<ExtractResult>): GrpcResult<MultiObjectResult> =
        tryRpc {
            val results = mutableMapOf<String, IdentifiedObject>()
            val failed = mRIDs?.toMutableSet() ?: mutableSetOf()
            processor().forEach { (identifiedObject, mRID) ->
                identifiedObject?.let {
                    results[it.mRID] = it
                    failed.remove(it.mRID)
                } ?: failed.add(mRID)
            }
            MultiObjectResult(results, failed)
        }

    internal abstract fun runGetMetadata(getMetadataRequest: GetMetadataRequest, streamObserver: AwaitableStreamObserver<GetMetadataResponse>)

    /**
     * Request the metadata for the service.
     */
    fun getMetadata(): GrpcResult<ServiceInfo> =
        tryRpc {
            if (serviceInfo == null) {
                val streamObserver = AwaitableStreamObserver<GetMetadataResponse> { response ->
                    serviceInfo = response.serviceInfo.fromPb()
                }

                runGetMetadata(GetMetadataRequest.newBuilder().build(), streamObserver)
                streamObserver.await()
            }
            serviceInfo
                ?: throw IOException("No metadata was received before GRPC channel was closed.")// Other exceptions should be raised before serviceInfo is found to be null
        }
}

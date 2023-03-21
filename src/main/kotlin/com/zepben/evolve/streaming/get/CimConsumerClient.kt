/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.streaming.get

import com.zepben.evolve.cim.iec61970.base.core.IdentifiedObject
import com.zepben.evolve.services.common.BaseService
import com.zepben.evolve.services.common.translator.BaseProtoToCim
import com.zepben.evolve.streaming.grpc.GrpcClient
import com.zepben.evolve.streaming.grpc.GrpcResult
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
    protected abstract val protoToCim: U

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
    fun getIdentifiedObjects(mRIDs: Sequence<String>): GrpcResult<MultiObjectResult> = tryRpc {
        val results = mutableMapOf<String, IdentifiedObject>()
        val failed = mRIDs.toMutableSet()
        processIdentifiedObjects(mRIDs).forEach { result ->
            result.identifiedObject?.let {
                results[it.mRID] = it
                failed.remove(it.mRID)
            }
        }
        MultiObjectResult(results, failed)
    }

    protected abstract fun processIdentifiedObjects(mRIDs: Sequence<String>): Sequence<ExtractResult>

    protected fun <T> batchSend(mRIDs: Sequence<T>, addMrid: (T) -> Unit, sendBatch: () -> Unit) {
        var count = 0
        mRIDs.forEach {
            if (++count % 1000 == 0)
                sendBatch()
            addMrid(it)
        }
        sendBatch()
    }

    protected inline fun <reified CIM : IdentifiedObject> extractResult(mRID: String, addFromPb: U.() -> CIM?): ExtractResult =
        ExtractResult(getOrAdd(mRID, addFromPb), mRID)

    protected inline fun <reified CIM : IdentifiedObject> getOrAdd(mRID: String, addFromPb: U.() -> CIM?): CIM? =
        service.get(CIM::class, mRID) ?: protoToCim.addFromPb()

}

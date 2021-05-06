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
import com.zepben.evolve.streaming.grpc.GrpcClient
import com.zepben.evolve.streaming.grpc.GrpcResult


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
internal data class ExtractResult(val identifiedObject: IdentifiedObject?, val mRID: String)

/**
 * Base class that defines some helpful functions when producer clients are sending to the server.
 *
 * WARNING: The [MultiObjectResult] operations below are not atomic upon a [BaseService], and thus if processing fails partway through, any previously
 * successful additions will have been processed by the service, and thus you may have an incomplete service. Also note that adding to the service may not
 * occur for an object if another object with the same mRID is already present in service. [MultiObjectResult.failed] can be used to check for mRIDs that
 * were retrieved but not added to service. This should not be the case unless you are processing things concurrently.
 *
 * @property T The base service to send objects from.
 */
abstract class CimConsumerClient<T : BaseService> : GrpcClient() {

    /**
     * Retrieve the object with the given [mRID] and store the result in the [service].
     *
     * Exceptions that occur during sending will be caught and passed to all error handlers that have been registered by [addErrorHandler].
     *
     * @return A [GrpcResult] with a result of one of the following:
     * - When [GrpcResult.wasSuccessful], the item found, accessible via [GrpcResult.value].
     * - When [GrpcResult.wasFailure], the error that occurred retrieving or processing the the object, accessible via [GrpcResult.thrown]. One of:
     *    - [NoSuchElementException] if the object could not be found.
     *    - The gRPC error that occurred while retrieving the object
     */
    abstract fun getIdentifiedObject(service: T, mRID: String): GrpcResult<IdentifiedObject>

    /**
     * Retrieve the objects with the given [mRIDs] and store the results in the [service].
     *
     * Exceptions that occur during processing will be caught and passed to all error handlers that have been registered by [addErrorHandler].
     *
     * @return A [GrpcResult] with a result of one of the following:
     * - When [GrpcResult.wasSuccessful], a map containing the retrieved objects keyed by mRID, accessible via [GrpcResult.value]. If an item was not found, or
     * couldn't be added to [service], it will be excluded from the map and its mRID will be present in [MultiObjectResult.failed] (see [BaseService.add]).
     * - When [GrpcResult.wasFailure], the error that occurred retrieving or processing the the object, accessible via [GrpcResult.thrown].
     * Note the [CimConsumerClient] warning in this case.
     */
    abstract fun getIdentifiedObjects(service: T, mRIDs: Iterable<String>): GrpcResult<MultiObjectResult>

    internal fun processExtractResults(mRIDs: Iterable<String>, extracted: Sequence<ExtractResult>): MultiObjectResult {
        val results = mutableMapOf<String, IdentifiedObject>()
        val failed = mRIDs.toMutableSet()
        extracted.forEach { result ->
            result.identifiedObject?.let {
                results[it.mRID] = it
                failed.remove(it.mRID)
            }
        }
        return MultiObjectResult(results, failed)
    }

}

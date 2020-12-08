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
data class MultiObjectResult(val objects: Map<String, IdentifiedObject>, val failed: Set<String>)

/**
 * Represents the result of deserialising a protobuf message and adding it to a service.
 * @property identifiedObject The [IdentifiedObject] that was deserialised, or null if it couldn't be added to the service
 * @property mRID The corresponding mRID of [identifiedObject]. Typically only used if [identifiedObject] is null.
 */
internal data class ExtractResult(val identifiedObject: IdentifiedObject?, val mRID: String)

/**
 * Base class that defines some helpful functions when producer clients are sending to the server.
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
     * - The item if found
     * - null if an object could not be found or it was found but not added to [service] (see [BaseService.add]).
     * - A [Throwable] if an error occurred while retrieving or processing the object, in which case, [GrpcResult.wasSuccessful] will return false.
     */
    abstract fun getIdentifiedObject(service: T, mRID: String): GrpcResult<IdentifiedObject?>

    /**
     * Retrieve the objects with the given [mRIDs] and store the results in the [service].
     *
     * Exceptions that occur during processing will be caught and passed to all error handlers that have been registered by [addErrorHandler].
     *
     * WARNING: This operation is not atomic upon [service], and thus if processing fails partway through [mRIDs], any previously successful mRID will have been
     * added to the service, and thus you may have an incomplete [BaseService]. Also note that adding to the [service] may not occur for an object if another
     * object with the same mRID is already present in [service]. [MultiObjectResult.failed] can be used to check for mRIDs that were retrieved but not
     * added to [service].
     *
     * @return A [GrpcResult] with a result of one of the following:
     * - A [MultiObjectResult] containing a map of the retrieved objects keyed by mRID. If an item is not found it will be excluded from the map.
     *   If an item couldn't be added to [service] its mRID will be present in [MultiObjectResult.failed] (see [BaseService.add]).
     * - A [Throwable] if an error occurred while retrieving or processing the objects, in which case, [GrpcResult.wasSuccessful] will return false.
     * Note the warning above in this case.
     */
    abstract fun getIdentifiedObjects(service: T, mRIDs: Iterable<String>): GrpcResult<MultiObjectResult>

}

/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 * This file is part of evolve-sdk-jvm.
 *
 * evolve-sdk-jvm is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * evolve-sdk-jvm is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with evolve-sdk-jvm.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.zepben.cimbend.get

import com.zepben.cimbend.cim.iec61970.base.core.IdentifiedObject
import com.zepben.cimbend.common.BaseService
import com.zepben.cimbend.grpc.GrpcClient
import com.zepben.cimbend.grpc.GrpcResult

/**
 * Base class that defines some helpful functions when producer clients are sending to the server.
 *
 * @property T The base service to send objects from.
 */
abstract class CimConsumerClient<T : BaseService> : GrpcClient() {

    /**
     * Retrieve the object with the given [mRID] and store the result in the [service].
     *
     * Exceptions that occur during sending will be caught and passed to all error handlers that have been registered by
     * [addErrorHandler]. If none of the registered error handlers return true to indicate the error has been handled,
     * the exception will be rethrown.
     *
     * @return The item if found, otherwise null.
     */
    abstract fun getIdentifiedObject(service: T, mRID: String): GrpcResult<IdentifiedObject>

    /**
     * Retrieve the objects with the given [mRIDs] and store the results in the [service].
     *
     * Exceptions that occur during sending will be caught and passed to all error handlers that have been registered by
     * [addErrorHandler]. If none of the registered error handlers return true to indicate the error has been handled,
     * the exception will be rethrown.
     *
     * @return A [Map] containing the retrieved objects keyed by mRID. If an item is not found it will be excluded from the map.
     */
    abstract fun getIdentifiedObjects(service: T, mRIDs: Iterable<String>): GrpcResult<Map<String, IdentifiedObject>>

}

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
package com.zepben.cimbend.grpc

import com.zepben.cimbend.put.RpcErrorHandler

/**
 * Base class that defines some helpful functions for gRPC clients to communicate with the server.
 */
abstract class GrpcClient {

    private val errorHandlers: MutableList<RpcErrorHandler> = mutableListOf()

    /**
     * Registers an error handler that will be called if any exception are thrown when sending a service.
     */
    fun addErrorHandler(handler: RpcErrorHandler) {
        errorHandlers.add(handler)
    }

    /**
     * Removes the handler if registered.
     */
    fun removeErrorHandler(handler: RpcErrorHandler) {
        errorHandlers.remove(handler)
    }

    /**
     * Use to pass the throwable to all registered error handlers. Returns true if and error handler returns true.
     */
    protected fun tryHandleError(t: Throwable): Boolean =
        errorHandlers.fold(false) { handled, handler -> handler.onError(t) or handled }

    /**
     * Allows a safe RPC call to be made to the server by wrapping [rpcCall] in a try/catch block. Any [Throwable] caught will
     * be passed to all registered error handlers. If no handler returns true to indicate it has been handled the exception
     * will be rethrown.
     */
    protected fun <T> tryRpc(rpcCall: () -> GrpcResult<T>): GrpcResult<T> {
        return try {
            rpcCall()
        } catch (t: Throwable) {
            if (!tryHandleError(t)) {
                throw t
            }
            GrpcResult.ofError(t)
        }
    }

}

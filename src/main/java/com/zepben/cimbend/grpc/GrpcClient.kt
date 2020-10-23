/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
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
     * be passed to all registered error handlers.
     */
    protected fun <T> tryRpc(rpcCall: () -> T): GrpcResult<T> {
        return try {
            GrpcResult.of(rpcCall())
        } catch (t: Throwable) {
            GrpcResult.ofError(t, tryHandleError(t))
        }
    }

}

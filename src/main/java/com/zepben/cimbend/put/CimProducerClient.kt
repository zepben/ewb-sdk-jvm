/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.put

import com.zepben.cimbend.common.BaseService

/**
 * Base class that defines some helpful functions when producer clients are sending to the server.
 *
 * @property T The base service to send objects from.
 */
abstract class CimProducerClient<T : BaseService> {

    private val errorHandlers: MutableList<RpcErrorHandler> = mutableListOf()

    /**
     * Sends objects within the given [service] to the producer server.
     *
     * Exceptions that occur during sending will be caught and passed to all error handlers that have been registered by
     * [addErrorHandler]. If none of the registered error handlers return true to indicate the error has been handled,
     * the exception will be rethrown.
     */
    abstract fun send(service: T)

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
    protected fun tryRpc(rpcCall: () -> Unit) {
        try {
            rpcCall()
        } catch (t: Throwable) {
            if (!tryHandleError(t)) {
                throw t
            }
        }
    }
}

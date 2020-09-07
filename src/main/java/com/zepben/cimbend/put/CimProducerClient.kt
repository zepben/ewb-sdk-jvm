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
package com.zepben.cimbend.put

import com.zepben.cimbend.common.BaseService
import io.grpc.StatusRuntimeException

/**
 * Base class that defines some helpful functions when producer clients are sending to the server.
 *
 * The property [onRpcError] can be used to get a callback anytime GRPc throws a [StatusRuntimeException] as long as
 * gRPC calls are made within the [tryRpc] function. E.g.
 * ```
 * tryRpc {
 *     stub.sendToServer(...)
 * }
 * ```
 *
 * @property T The base service to send objects from.
 */
abstract class CimProducerClient<T : BaseService>(
    internal val onRpcError: RpcErrorHandler
) {
    /**
     * Sends objects within the given [service] to the producer server.
     */
    abstract fun send(service: T)

    /**
     * Allows a safe RPC call to be made to the server by wrapping [rpcCall] in a try/catch block. Any [Throwable] caught will check if
     * [onRpcError].handles returns true and passes it into the onError method if so. Otherwise rethrows the exception.
     */
    protected fun tryRpc(rpcCall: () -> Unit) {
        try {
            rpcCall()
        } catch (t: Throwable) {
            if (onRpcError.handles(t))
                onRpcError.onError(t)
            else
                throw t
        }
    }
}

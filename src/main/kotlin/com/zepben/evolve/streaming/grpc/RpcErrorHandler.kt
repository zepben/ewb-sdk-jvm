/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.streaming.grpc

/**
 * Interface that allows you to react to RPC errors when sending items using a [GrpcClient].
 */
interface RpcErrorHandler {

    /**
     * Handle the given [t]. This should return true if the error was handled, otherwise false.
     */
    fun onError(t: Throwable): Boolean

}


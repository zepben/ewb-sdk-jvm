/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.streaming.put

import com.zepben.evolve.services.common.BaseService
import com.zepben.evolve.streaming.grpc.GrpcClient

/**
 * Base class that defines some helpful functions when producer clients are sending to the server.
 *
 * @property T The base service to send objects from.
 */
abstract class CimProducerClient<T : BaseService> : GrpcClient() {

    /**
     * Sends objects within the given [service] to the producer server.
     *
     * Exceptions that occur during sending will be caught and passed to all error handlers that have been registered by
     * [addErrorHandler]. If none of the registered error handlers return true to indicate the error has been handled,
     * the exception will be rethrown.
     */
    abstract fun send(service: T)

}

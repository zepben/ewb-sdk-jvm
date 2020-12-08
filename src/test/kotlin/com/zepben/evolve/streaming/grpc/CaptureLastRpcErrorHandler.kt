/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.streaming.grpc

import kotlin.reflect.KClass

/**
 * @property filter Only capture exceptions that are subclasses of the specified class.
 */
class CaptureLastRpcErrorHandler(val filter: KClass<out Throwable> = Throwable::class) : RpcErrorHandler {
    var lastError: Throwable? = null
    var count = 0

    override fun onError(t: Throwable): Boolean {
        ++count
        if (filter.isInstance(t))
            lastError = t

        return lastError != null
    }

}

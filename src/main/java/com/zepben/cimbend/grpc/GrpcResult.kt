/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.cimbend.grpc

/**
 * The result of a gRPC call.
 * @property wasSuccessful Indicates if the call was resolved without error
 * @property result The result of the call if [wasSuccessful] is true, otherwise null. The result may still be null even if successful.
 * @property thrown The exception that was caught if [wasSuccessful] is false, otherwise null.
 */
data class GrpcResult<T>(
    val result: T?,
    val thrown: Throwable?
) {
    val wasSuccessful: Boolean get() = thrown == null

    inline fun onSuccess(handler: (result: T?) -> Unit): GrpcResult<T> {
        if (wasSuccessful)
            handler.invoke(result)
        return this
    }

    inline fun onError(handler: (thrown: Throwable) -> Unit): GrpcResult<T> {
        thrown?.let(handler)
        return this
    }

    companion object {

        @JvmStatic
        fun <T> of(result: T?): GrpcResult<T> {
            return GrpcResult(result, null)
        }

        @JvmStatic
        fun <T> ofError(thrown: Throwable): GrpcResult<T> {
            return GrpcResult(null, thrown)
        }

    }

}

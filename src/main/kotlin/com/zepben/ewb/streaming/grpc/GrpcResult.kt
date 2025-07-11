/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.streaming.grpc

/**
 * The result of a gRPC call.
 * @property wasSuccessful Indicates if the call was resolved without error
 * @property value The value of the call if [wasSuccessful] is true, otherwise throws [ClassCastException].
 * @property thrown The exception that was caught if [wasSuccessful] is false, otherwise throws [ClassCastException].
 * @property wasErrorHandled Indicates if the error has already been handled
 */
data class GrpcResult<T>(
    private val result: Any?,
    val wasErrorHandled: Boolean = false
) {
    val wasSuccessful: Boolean get() = result !is Throwable
    val wasFailure: Boolean get() = result is Throwable
    val wasErrorUnhandled: Boolean get() = !wasErrorHandled

    @Suppress("UNCHECKED_CAST")
    val value: T
        get() = result as T

    val thrown: Throwable
        get() = result as Throwable

    /**
     * Calls the [handler] with the [value] if [wasSuccessful].
     */
    inline fun onSuccess(handler: (value: T) -> Unit): GrpcResult<T> {
        if (wasSuccessful)
            handler.invoke(value)
        return this
    }

    /**
     * Calls the [handler] with the [thrown] exception and [wasErrorHandled] if [wasFailure].
     */
    inline fun onError(handler: (thrown: Throwable, wasErrorHandled: Boolean) -> Unit): GrpcResult<T> {
        if (wasFailure)
            handler.invoke(thrown, wasErrorHandled)
        return this
    }

    /**
     * Calls the [handler] with the [thrown] exception if [wasFailure] only if [wasErrorHandled].
     */
    inline fun onHandledError(handler: (thrown: Throwable) -> Unit): GrpcResult<T> {
        if (wasFailure && wasErrorHandled)
            handler.invoke(thrown)
        return this
    }

    /**
     * Calls the [handler] with the [thrown] exception if [wasFailure] only if [wasErrorUnhandled].
     */
    inline fun onUnhandledError(handler: (thrown: Throwable) -> Unit): GrpcResult<T> {
        if (wasFailure && wasErrorUnhandled)
            handler.invoke(thrown)
        return this
    }

    /**
     * Throws the [thrown] exception and if [wasFailure].
     */
    fun throwOnError(): GrpcResult<T> {
        if (wasFailure)
            throw thrown
        return this
    }

    /**
     * Throws the [thrown] exception and if [wasFailure] only if [wasErrorUnhandled].
     */
    fun throwOnUnhandledError(): GrpcResult<T> {
        if (wasFailure && wasErrorUnhandled)
            throw thrown
        return this
    }

    /**
     * Returns a new [GrpcResult] with the result of the [transform] function if [wasSuccessful] or
     * the original [thrown] exception and [wasErrorHandled] if [wasFailure].
     */
    fun <R> map(transform: (T) -> R): GrpcResult<R> {
        return if (wasSuccessful)
            of(transform(value))
        else
            ofError(thrown, wasErrorHandled)
    }

    companion object {

        /**
         * Create a new successful [GrpcResult].
         */
        @JvmStatic
        fun <T> of(result: T): GrpcResult<T> {
            return GrpcResult(result)
        }

        /**
         * Create a new failed [GrpcResult].
         */
        @JvmStatic
        fun <T> ofError(thrown: Throwable, wasErrorHandled: Boolean): GrpcResult<T> {
            return GrpcResult(thrown, wasErrorHandled)
        }

    }

}

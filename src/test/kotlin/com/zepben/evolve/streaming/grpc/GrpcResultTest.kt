/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.streaming.grpc

import com.zepben.testutils.exception.ExpectException.expect
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class GrpcResultTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun canCreateResult() {
        val result: GrpcResult<Int> = GrpcResult.of(1)

        assertThat(result.wasSuccessful, equalTo(true))
        assertThat(result.wasFailure, equalTo(false))
        assertThat(result.value, equalTo(1))
        assertThat(result.wasErrorHandled, equalTo(false))
        assertThat(result.wasErrorUnhandled, equalTo(true))
    }

    @Test
    internal fun canCreateNullResult() {
        val result: GrpcResult<Int?> = GrpcResult.of(null)

        assertThat(result.wasSuccessful, equalTo(true))
        assertThat(result.value, nullValue())
        assertThat(result.wasErrorUnhandled, equalTo(true))
    }

    @Test
    internal fun canCreateHandledErrorResult() {
        val exception = RuntimeException()
        val result: GrpcResult<Int> = GrpcResult.ofError(exception, true)

        assertThat(result.wasFailure, equalTo(true))
        assertThat(result.wasSuccessful, equalTo(false))
        assertThat(result.thrown, equalTo(exception))
        assertThat(result.wasErrorHandled, equalTo(true))
        assertThat(result.wasErrorUnhandled, equalTo(false))
    }

    @Test
    internal fun canCreateUnhandledErrorResult() {
        val exception = RuntimeException()
        val result: GrpcResult<Int> = GrpcResult.ofError(exception, false)

        assertThat(result.wasFailure, equalTo(true))
        assertThat(result.thrown, equalTo(exception))
        assertThat(result.wasErrorUnhandled, equalTo(true))
    }

    @Test
    internal fun callsOnSuccessOnlyWhenSuccessful() {
        var calledSuccessful = false
        var calledHandled = false
        var calledUnhandled = false

        GrpcResult.of(1)
            .onSuccess { calledSuccessful = true }

        GrpcResult.ofError<Any>(IllegalStateException(), true)
            .onSuccess { calledHandled = true }

        GrpcResult.ofError<Any>(IllegalArgumentException(), false)
            .onSuccess { calledUnhandled = true }

        assertThat(calledSuccessful, equalTo(true))
        assertThat(calledHandled, equalTo(false))
        assertThat(calledUnhandled, equalTo(false))
    }

    @Test
    internal fun callsOnErrorOnlyWhenFailure() {
        var calledSuccessful = false
        var calledHandled = false
        var calledUnhandled = false

        GrpcResult.of(1)
            .onError { _, _ -> calledSuccessful = true }

        GrpcResult.ofError<Any>(IllegalStateException(), true)
            .onError { _, _ -> calledHandled = true }

        GrpcResult.ofError<Any>(IllegalArgumentException(), false)
            .onError { _, _ -> calledUnhandled = true }

        assertThat(calledSuccessful, equalTo(false))
        assertThat(calledHandled, equalTo(true))
        assertThat(calledUnhandled, equalTo(true))
    }

    @Test
    internal fun callsOnHandledErrorOnlyWhenFailureHandled() {
        var calledSuccessful = false
        var calledHandled = false
        var calledUnhandled = false

        GrpcResult.of(1)
            .onHandledError { calledSuccessful = true }

        GrpcResult.ofError<Any>(IllegalStateException(), true)
            .onHandledError { calledHandled = true }

        GrpcResult.ofError<Any>(IllegalArgumentException(), false)
            .onHandledError { calledUnhandled = true }

        assertThat(calledSuccessful, equalTo(false))
        assertThat(calledHandled, equalTo(true))
        assertThat(calledUnhandled, equalTo(false))
    }

    @Test
    internal fun callsOnUnhandledErrorOnlyWhenFailureUnhandled() {
        var calledSuccessful = false
        var calledHandled = false
        var calledUnhandled = false

        GrpcResult.of(1)
            .onUnhandledError { calledSuccessful = true }

        GrpcResult.ofError<Any>(IllegalStateException(), true)
            .onUnhandledError { calledHandled = true }

        GrpcResult.ofError<Any>(IllegalArgumentException(), false)
            .onUnhandledError { calledUnhandled = true }

        assertThat(calledSuccessful, equalTo(false))
        assertThat(calledHandled, equalTo(false))
        assertThat(calledUnhandled, equalTo(true))
    }

    @Test
    internal fun throwOnErrorOnlyThrowsWhenFailure() {
        GrpcResult.of(1)
            .throwOnError()

        expect {
            GrpcResult.ofError<Any>(IllegalStateException(), true)
                .throwOnError()
        }.toThrow(RuntimeException::class.java)

        expect {
            GrpcResult.ofError<Any>(IllegalArgumentException(), false)
                .throwOnError()
        }.toThrow(IllegalArgumentException::class.java)
    }

    @Test
    internal fun throwOnUnhandledErrorOnlyThrowsWhenFailureUnhandled() {
        GrpcResult.of(1)
            .throwOnError()

        GrpcResult.ofError<Any>(IllegalStateException(), true)
            .throwOnUnhandledError()

        expect {
            GrpcResult.ofError<Any>(IllegalArgumentException(), false)
                .throwOnUnhandledError()
        }.toThrow(IllegalArgumentException::class.java)
    }

    @Test
    internal fun canMapResult() {
        assertThat(GrpcResult.of(1).map { it.toString() }.value, equalTo("1"))
    }

    @Test
    internal fun mapResultRetainsErrors() {
        val ex = IllegalStateException()

        val result = GrpcResult.ofError<Any>(ex, true)
            .map { it.toString() }

        assertThat(result.thrown, equalTo(ex))
    }

}

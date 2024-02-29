/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.streaming.grpc

import com.zepben.testutils.exception.ExpectException.Companion.expect
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

        assertThat("Result of 1 should count as successful", result.wasSuccessful)
        assertThat("Result of 1 should not count as a failure", !result.wasFailure)
        assertThat(result.value, equalTo(1))
        assertThat("wasErrorHandled should be false, as no error was handled", !result.wasErrorHandled)
        assertThat("wasErrorUnhandled should be true, as no error was handled", result.wasErrorUnhandled)
    }

    @Test
    internal fun canCreateNullResult() {
        val result: GrpcResult<Int?> = GrpcResult.of(null)

        assertThat("null result should count as success", result.wasSuccessful)
        assertThat(result.value, nullValue())
        assertThat("wasErrorUnhandled should be true, as no error was handled", result.wasErrorUnhandled)
    }

    @Test
    internal fun canCreateHandledErrorResult() {
        val exception = RuntimeException()
        val result: GrpcResult<Int> = GrpcResult.ofError(exception, true)

        assertThat("Result of a handled error should count as a failure", result.wasFailure)
        assertThat("Result of a handled error should not count as a success", !result.wasSuccessful)
        assertThat(result.thrown, equalTo(exception))
        assertThat("wasErrorHandled should be true for a result of a handled error", result.wasErrorHandled)
        assertThat("wasErrorUnhandled should be false for the result of a handled error", !result.wasErrorUnhandled)
    }

    @Test
    internal fun canCreateUnhandledErrorResult() {
        val exception = RuntimeException()
        val result: GrpcResult<Int> = GrpcResult.ofError(exception, false)

        assertThat("Result of an unhandled error should count as a failure", result.wasFailure)
        assertThat(result.thrown, equalTo(exception))
        assertThat("wasErrorUnhandled should be true for a result of an unhandled error", result.wasErrorUnhandled)
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

        assertThat("onSuccess should be called on successful gRPC result", calledSuccessful)
        assertThat("onSuccess should not be called on failed gRPC result", !calledHandled)
        assertThat("onSuccess should not be called on failed gRPC result", !calledUnhandled)
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

        assertThat("onError should not be called for successful gRPC result", !calledSuccessful)
        assertThat("onError should be called for failed gRPC result with handled error", calledHandled)
        assertThat("onError should be called for failed gRPC result with unhandled error", calledUnhandled)
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

        assertThat("onHandledError should not be called for successful gRPC result", !calledSuccessful)
        assertThat("onHandledError should be called for failed gRPC result with handled error", calledHandled)
        assertThat("onHandledError should not called for failed gRPC result with unhandled error", !calledUnhandled)
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

        assertThat("onUnhandledError should not be called for successful gRPC result", !calledSuccessful)
        assertThat("onUnhandledError should not be called for failed gRPC result with handled error", !calledHandled)
        assertThat("onUnhandledError should be called for failed gRPC result with unhandled error", calledUnhandled)
    }

    @Test
    internal fun throwOnErrorOnlyThrowsWhenFailure() {
        GrpcResult.of(1)
            .throwOnError()

        expect {
            GrpcResult.ofError<Any>(IllegalStateException(), true)
                .throwOnError()
        }.toThrow<RuntimeException>()

        expect {
            GrpcResult.ofError<Any>(IllegalArgumentException(), false)
                .throwOnError()
        }.toThrow<IllegalArgumentException>()
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
        }.toThrow<IllegalArgumentException>()
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

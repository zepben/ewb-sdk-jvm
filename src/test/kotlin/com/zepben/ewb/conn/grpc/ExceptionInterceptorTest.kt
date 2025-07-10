/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.conn.grpc

import io.grpc.Status
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.Test

class ExceptionInterceptorTest {

    @Test
    fun `test intercepts exceptions on close and propagates to status`() {
        // ExceptionInterceptor.close() is what we're testing
        val interceptor = ExceptionInterceptor()

        runServerCall(
            interceptor,
            { status, _ ->
                assertThat(status!!.code, Matchers.equalTo(Status.INTERNAL.code))
                assertThat(status.description, Matchers.equalTo("Received no status, this is a bug in the server."))
            },
            { call, metadata -> call.close(null, metadata) }
        )

        runServerCall(
            interceptor,
            { status, _ ->
                assertThat(status!!.code, Matchers.equalTo(Status.OK.code))
                assertThat(status.description, nullValue())
            },
            { call, metadata -> call.close(Status.OK, metadata) }
        )

        runServerCall(
            interceptor,
            { status, _ ->
                assertThat(status!!.code, Matchers.equalTo(Status.INVALID_ARGUMENT.code))
                assertThat(status.description, Matchers.equalTo("some description"))
                assertThat(status.cause, Matchers.instanceOf(IllegalArgumentException::class.java))
            },
            { call, metadata -> call.close(Status.UNKNOWN.withCause(IllegalArgumentException("some description")), metadata) }
        )

        runServerCall(
            interceptor,
            { status, _ ->
                assertThat(status!!.code, Matchers.equalTo(Status.FAILED_PRECONDITION.code))
                assertThat(status.description, Matchers.equalTo("some description"))
                assertThat(status.cause, Matchers.instanceOf(IllegalStateException::class.java))
            },
            { call, metadata -> call.close(Status.UNKNOWN.withCause(IllegalStateException("some description")), metadata) }
        )

        runServerCall(
            interceptor,
            { status, _ ->
                assertThat(status!!.code, Matchers.equalTo(Status.UNKNOWN.code))
                assertThat(status.description, Matchers.equalTo("some description"))
                assertThat(status.cause, Matchers.instanceOf(NoSuchMethodError::class.java))
            },
            { call, metadata -> call.close(Status.UNKNOWN.withCause(NoSuchMethodError("some description")), metadata) }
        )
    }
}

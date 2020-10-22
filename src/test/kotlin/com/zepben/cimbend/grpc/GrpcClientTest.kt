/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.cimbend.grpc

import com.zepben.testutils.exception.ExpectException.expect
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class GrpcClientTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    private val client = ThrowingGrpcClient()

    @Test
    internal fun `safeTryRpc calls all handlers`() {
        val handler1 = CaptureLastRpcErrorHandler()
        val handler2 = CaptureLastRpcErrorHandler()
        client.apply {
            addErrorHandler(handler1)
            addErrorHandler(handler2)
        }

        validateFailure(true)

        assertThat(handler1.lastError, equalTo(client.ex))
        assertThat(handler2.lastError, equalTo(client.ex))
    }

    @Test
    internal fun `safeTryRpc calls all handlers even if unhandled`() {
        val handler1 = CaptureLastRpcErrorHandler(IllegalArgumentException::class)
        val handler2 = CaptureLastRpcErrorHandler(RuntimeException::class)
        client.apply {
            addErrorHandler(handler1)
            addErrorHandler(handler2)
        }

        validateFailure(true)

        assertThat(handler1.lastError, nullValue())
        assertThat(handler2.lastError, equalTo(client.ex))
    }

    @Test
    internal fun `safeTryRpc captures unhandled exception`() {
        validateFailure(false)
    }

    @Test
    internal fun `tryRpc calls all handlers`() {
        val handler1 = CaptureLastRpcErrorHandler()
        val handler2 = CaptureLastRpcErrorHandler()
        client.apply {
            addErrorHandler(handler1)
            addErrorHandler(handler2)
        }

        client.throwViaTryRpc()

        assertThat(handler1.lastError, equalTo(client.ex))
        assertThat(handler2.lastError, equalTo(client.ex))
    }

    @Test
    internal fun `tryRpc calls all handlers even if unhandled`() {
        val handler1 = CaptureLastRpcErrorHandler(IllegalArgumentException::class)
        val handler2 = CaptureLastRpcErrorHandler(RuntimeException::class)
        client.apply {
            addErrorHandler(handler1)
            addErrorHandler(handler2)
        }

        client.throwViaTryRpc()

        assertThat(handler1.lastError, nullValue())
        assertThat(handler2.lastError, equalTo(client.ex))
    }

    @Test
    internal fun `tryRpc propagates unhandled exception`() {
        expect { client.throwViaTryRpc() }
            .toThrow(RuntimeException::class.java)
    }

    @Test
    internal fun `can remove error handler`() {
        val handler = CaptureLastRpcErrorHandler()

        client.apply { addErrorHandler(handler) }

        validateFailure(true)

        client.removeErrorHandler(handler)

        validateFailure(false)

        assertThat(handler.count, equalTo(1))
    }

    private fun validateFailure(expectHandled: Boolean) {
        val result = client.throwViaSafeTryRpc()

        assertThat(result.wasSuccessful, equalTo(false))
        assertThat(result.thrown, equalTo(client.ex))
        assertThat(result.wasHandled, equalTo(expectHandled))
    }

}

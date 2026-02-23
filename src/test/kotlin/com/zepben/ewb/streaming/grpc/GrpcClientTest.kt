/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.streaming.grpc

import com.zepben.protobuf.nc.NetworkConsumerGrpc
import com.zepben.protobuf.nc.NetworkConsumerGrpc.NetworkConsumerStub
import com.zepben.testutils.junit.SystemLogExtension
import io.grpc.CallOptions
import io.mockk.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.util.concurrent.ExecutorService
import java.util.concurrent.TimeUnit

internal class GrpcClientTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    private val client = ThrowingGrpcClient(stub = mockk())

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
    internal fun `can remove error handler`() {
        val handler = CaptureLastRpcErrorHandler()

        client.apply { addErrorHandler(handler) }

        validateFailure(true)

        client.removeErrorHandler(handler)

        validateFailure(false)

        assertThat(handler.count, equalTo(1))
    }

    @Test
    internal fun `cleans up executor if provided`() {
        val executor = mockk<ExecutorService>().also {
            justRun { it.shutdown() }
            every { it.awaitTermination(any(), any()) } returns true
        }

        val client1 = object : GrpcClient<NetworkConsumerStub>() {
            override val stub: NetworkConsumerStub
                get() = NetworkConsumerGrpc.newStub(spyk()).withExecutor(executor)
        }

        client1.close()

        verify(exactly = 1) { executor.shutdown() }
        verify(exactly = 1) { executor.awaitTermination(1000, TimeUnit.MILLISECONDS) }
        confirmVerified(executor)
    }

    @Test
    internal fun `tries more heavy handed shutdown if the first attempt fails`() {
        val executor = mockk<ExecutorService>().also {
            justRun { it.shutdown() }
            every { it.awaitTermination(any(), any()) } returns false
            every { it.shutdownNow() } returns emptyList()
        }

        val client1 = object : GrpcClient<NetworkConsumerStub>() {
            override val stub: NetworkConsumerStub
                get() = NetworkConsumerGrpc.newStub(spyk()).withExecutor(executor)
        }

        client1.close()

        verify(exactly = 1) { executor.shutdown() }
        verify(exactly = 1) { executor.awaitTermination(1000, TimeUnit.MILLISECONDS) }
        verify(exactly = 1) { executor.shutdownNow() }
        confirmVerified(executor)
    }

    @Test
    internal fun `supports null executor`() {
        val callOpts = mockk<CallOptions> {
            every { executor } returns null
        }
        val stub = mockk<NetworkConsumerStub> {
            every { callOptions } returns callOpts
        }

        val client1 = object : GrpcClient<NetworkConsumerStub>() {
            override val stub: NetworkConsumerStub = stub
        }

        client1.close()
    }

    @Test
    internal fun `supports null callOpts`() {
        val stub = mockk<NetworkConsumerStub> {
            every { callOptions } returns null
        }

        val client1 = object : GrpcClient<NetworkConsumerStub>() {
            override val stub: NetworkConsumerStub = stub
        }

        client1.close()
    }

    private fun validateFailure(expectHandled: Boolean) {
        val result = client.throwViaSafeTryRpc()

        assertThat("Result should be failure", result.wasFailure)
        assertThat(result.thrown, equalTo(client.ex))
        assertThat(result.wasErrorHandled, equalTo(expectHandled))
    }

}

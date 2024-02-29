/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.streaming.mutations

import com.zepben.evolve.streaming.grpc.CaptureLastRpcErrorHandler
import com.zepben.evolve.streaming.grpc.GrpcChannel
import com.zepben.protobuf.nm.SetCurrentSwitchStatesRequest
import com.zepben.protobuf.nm.SwitchStateServiceGrpc
import com.zepben.testutils.junit.SystemLogExtension
import io.grpc.Channel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class SwitchStateClientTest {
    @JvmField
    @RegisterExtension
    var systemOut: SystemLogExtension = SystemLogExtension.SYSTEM_OUT.captureLog().muteOnSuccess()

    private val stub = mockk<SwitchStateServiceGrpc.SwitchStateServiceBlockingStub>(relaxed = true)
    private val onErrorHandler = CaptureLastRpcErrorHandler()
    private val client = SwitchStateClient(stub).apply { addErrorHandler(onErrorHandler) }

    @Test
    internal fun `update current switch state`() {
        val switchToUpdate = SwitchStateUpdate("id", true)

        val result = client.setCurrentSwitchState(switchToUpdate)

        assertThat("Updating switch state should have succeeded", result.wasSuccessful)
        verify {
            stub.setCurrentSwitchStates(
                SetCurrentSwitchStatesRequest.newBuilder()
                    .addSwitchesToUpdate(switchToUpdate.toPb())
                    .build()
            )
        }
    }

    @Test
    internal fun `update multiple switch states`() {
        val update1 = SwitchStateUpdate("id1", true)
        val update2 = SwitchStateUpdate("id2", false)

        val result = client.setCurrentSwitchStates(listOf(update1, update2))

        assertThat("Updating multiple switch states should have succeeded", result.wasSuccessful)
        verify {
            stub.setCurrentSwitchStates(
                SetCurrentSwitchStatesRequest.newBuilder()
                    .addAllSwitchesToUpdate(listOf(update1.toPb(), update2.toPb()))
                    .build()
            )
        }
    }

    @Test
    internal fun `calls error handler when setCurrentSwitchState throws`() {
        val switchToUpdate = SwitchStateUpdate("id", true)

        val expectedEx = StatusRuntimeException(Status.UNAVAILABLE)
        every { stub.setCurrentSwitchStates(any()) } throws expectedEx

        val result = client.setCurrentSwitchState(switchToUpdate)

        assertThat("Updating switch states should have failed due to $expectedEx", result.wasFailure)
        assertThat(onErrorHandler.lastError, equalTo(expectedEx))
        verify {
            stub.setCurrentSwitchStates(
                SetCurrentSwitchStatesRequest.newBuilder()
                    .addSwitchesToUpdate(switchToUpdate.toPb())
                    .build()
            )
        }
    }

    @Test
    internal fun `construct via Channel`() {
        val channel = mockk<Channel>()
        mockkStatic(SwitchStateServiceGrpc::class)
        every { SwitchStateServiceGrpc.newBlockingStub(channel) } returns stub

        val switchToUpdate = SwitchStateUpdate("id", true)
        val result = SwitchStateClient(channel).setCurrentSwitchState(switchToUpdate)

        assertThat("Updating switch state should have succeeded", result.wasSuccessful)
        verify {
            stub.setCurrentSwitchStates(
                SetCurrentSwitchStatesRequest.newBuilder()
                    .addSwitchesToUpdate(switchToUpdate.toPb())
                    .build()
            )
        }
    }

    @Test
    internal fun `construct via GrpcChannel`() {
        val channel = mockk<Channel>()
        val grpcChannel = GrpcChannel(channel)
        mockkStatic(SwitchStateServiceGrpc::class)
        every { SwitchStateServiceGrpc.newBlockingStub(channel) } returns stub

        val switchToUpdate = SwitchStateUpdate("id", true)
        val result = SwitchStateClient(grpcChannel).setCurrentSwitchState(switchToUpdate)

        assertThat("Updating switch state should have succeeded", result.wasSuccessful)
        verify {
            stub.setCurrentSwitchStates(
                SetCurrentSwitchStatesRequest.newBuilder()
                    .addSwitchesToUpdate(switchToUpdate.toPb())
                    .build()
            )
        }
    }
}

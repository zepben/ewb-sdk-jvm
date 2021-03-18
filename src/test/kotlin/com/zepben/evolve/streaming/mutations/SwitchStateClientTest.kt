/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.streaming.mutations

import com.zepben.evolve.streaming.grpc.CaptureLastRpcErrorHandler
import com.zepben.protobuf.nm.SetCurrentSwitchStatesRequest
import com.zepben.protobuf.nm.SwitchStateServiceGrpc
import com.zepben.testutils.junit.SystemLogExtension
import io.grpc.Status
import io.grpc.StatusRuntimeException
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

internal class SwitchStateClientTest {
    @JvmField
    @RegisterExtension
    var systemOut: SystemLogExtension = SystemLogExtension.SYSTEM_OUT.captureLog().muteOnSuccess()

    private val stub = mock(SwitchStateServiceGrpc.SwitchStateServiceBlockingStub::class.java)
    private val onErrorHandler = CaptureLastRpcErrorHandler()
    private val client: SwitchStateClient = SwitchStateClient(stub).apply { addErrorHandler(onErrorHandler) }

    @Test
    fun `update current switch state`() {
        val switchToUpdate = SwitchStateUpdate("id", true)

        val result = client.setCurrentSwitchState(switchToUpdate)

        assertThat(result.wasSuccessful, equalTo(true))
        verify(stub).setCurrentSwitchStates(
            SetCurrentSwitchStatesRequest.newBuilder()
                .addSwitchesToUpdate(switchToUpdate.toPb())
                .build()
        )
    }

    @Test
    fun `calls error handler when setCurrentSwitchState throws`() {
        val switchToUpdate = SwitchStateUpdate("id", true)

        val expectedEx = StatusRuntimeException(Status.UNAVAILABLE)
        Mockito.doAnswer { throw expectedEx }.`when`(stub).setCurrentSwitchStates(Mockito.any())

        val result = client.setCurrentSwitchState(switchToUpdate)

        assertThat(result.wasFailure, equalTo(true))
        assertThat(onErrorHandler.lastError, equalTo(expectedEx))
        verify(stub).setCurrentSwitchStates(
            SetCurrentSwitchStatesRequest.newBuilder()
                .addSwitchesToUpdate(switchToUpdate.toPb())
                .build()
        )
    }
}

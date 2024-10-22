/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.streaming.mutations

import com.google.protobuf.Timestamp
import com.zepben.evolve.cim.iec61970.base.core.PhaseCode
import com.zepben.evolve.services.common.translator.toTimestamp
import com.zepben.evolve.streaming.data.CurrentStateEvent
import com.zepben.evolve.streaming.data.SwitchAction
import com.zepben.evolve.streaming.data.SwitchStateEvent
import com.zepben.protobuf.ns.GetCurrentStatesRequest
import com.zepben.protobuf.ns.GetCurrentStatesResponse
import io.grpc.stub.StreamObserver
import io.mockk.*
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.contains
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime

class QueryNetworkStateServiceTest {
    private val currentStateEvents = listOf<CurrentStateEvent>(
        SwitchStateEvent("event1", LocalDateTime.now(), "mrid1", SwitchAction.OPEN, PhaseCode.ABC),
        SwitchStateEvent("event2", LocalDateTime.now(), "mrid1", SwitchAction.CLOSE, PhaseCode.ABN),
        SwitchStateEvent("event3", LocalDateTime.now(), "mrid2", SwitchAction.CLOSE, PhaseCode.A)
    )
    private val switchStateEvents = currentStateEvents.filterIsInstance<SwitchStateEvent>()

    private val responseCurrentStateEvents = listOf(currentStateEvents.take(2), currentStateEvents.drop(2))

    private val onGetCurrentStatesSequence = mockk<(from: LocalDateTime, to: LocalDateTime) -> Sequence<List<CurrentStateEvent>>>().also {
        every { it(any(), any()) } returns responseCurrentStateEvents.asSequence()
    }

    private val onGetCurrentStatesStream = mockk<QueryNetworkStateService.GetCurrentStates>().also {
        every { it.get(any(), any()) } returns responseCurrentStateEvents.stream()
    }

    private val responseSlot = mutableListOf<GetCurrentStatesResponse>()
    private val responseObserver = mockk<StreamObserver<GetCurrentStatesResponse>>().also {
        justRun { it.onNext(capture(responseSlot)) }
        justRun { it.onCompleted() }
    }

    private val serviceSequence = QueryNetworkStateService(onGetCurrentStatesSequence)
    private val serviceStream = QueryNetworkStateService(onGetCurrentStatesStream)

    private val request = GetCurrentStatesRequest.newBuilder().apply {
        messageId = 1
        from = Timestamp.newBuilder().apply { nanos = 1 }.build()
        to = Timestamp.newBuilder().apply { seconds = 1 }.build()
    }.build()

    @Test
    fun getCurrentStates() {
        serviceSequence.getCurrentStates(request, responseObserver)
        assertResponse()

        //reset captured slot
        responseSlot.clear()
        serviceStream.getCurrentStates(request, responseObserver)
        assertResponse()
    }

    @Test
    fun `getCurrentStates throws NotImpelemntedError when processing unsupported CurrentStateEvent`() {
        every { onGetCurrentStatesSequence(any(), any()) } returns listOf(listOf(mockk<CurrentStateEvent>())).asSequence()

        assertThrows<NotImplementedError> { serviceSequence.getCurrentStates(request, responseObserver) }
    }

    @Test
    fun `getCurrentStates throws IllegalArgumentException when GetCurrentStatesRequest properties 'from' and 'to' is invalid`() {
        // from is 0
        val requestBuilder = GetCurrentStatesRequest.newBuilder().apply {
            messageId = 1
            to = Timestamp.newBuilder().apply { nanos = 1 }.build()
        }
        assertIllegalArgument(
            requestBuilder.build(),
            "'GetCurrentStatesRequest.from' is not valid"
        )
        // to is 0
        requestBuilder.apply {
            from = Timestamp.newBuilder().apply { seconds = 1 }.build()
            clearTo()
        }
        assertIllegalArgument(
            requestBuilder.build(),
            "'GetCurrentStatesRequest.to' is not valid"
        )
        // to is less than from
        requestBuilder.apply {
            to = Timestamp.newBuilder().apply { nanos = 1 }.build()
        }
        assertIllegalArgument(
            requestBuilder.build(),
            "End time 'GetCurrentStatesRequest.to' must not be before start time 'GetCurrentStatesRequest.from'"
        )
    }

    private fun assertIllegalArgument(request: GetCurrentStatesRequest, message: String) =
        assertThat(
            assertThrows<IllegalArgumentException> { serviceSequence.getCurrentStates(request, responseObserver) }.message,
            equalTo(message)
        )

    private fun assertResponse() {
        assertThat(responseSlot.map { it.messageId }, contains(1, 1))
        responseSlot.flatMap { it.eventList }.let {
            assertThat(it.map { it.eventId }, contains(*currentStateEvents.map { it.eventId }.toTypedArray()))
            assertThat(it.map { it.timestamp }, contains(*currentStateEvents.map { it.timestamp.toTimestamp() }.toTypedArray()))
            assertThat(it.map { it.switch.mrid }, contains(*switchStateEvents.map { it.mRID }.toTypedArray()))
            assertThat(it.map { it.switch.action.name }, contains(*switchStateEvents.map { it.action.name }.toTypedArray()))
            assertThat(it.map { it.switch.phases.name }, contains(*switchStateEvents.map { it.phases.name }.toTypedArray()))
        }
    }
}

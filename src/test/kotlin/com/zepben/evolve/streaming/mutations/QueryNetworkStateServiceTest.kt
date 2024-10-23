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
import com.zepben.evolve.services.common.translator.toLocalDateTime
import com.zepben.evolve.services.common.translator.toTimestamp
import com.zepben.evolve.streaming.data.CurrentStateEvent
import com.zepben.evolve.streaming.data.SwitchAction
import com.zepben.evolve.streaming.data.SwitchStateEvent
import com.zepben.protobuf.ns.GetCurrentStatesRequest
import com.zepben.protobuf.ns.GetCurrentStatesResponse
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.stub.StreamObserver
import io.mockk.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class QueryNetworkStateServiceTest {
    private val currentStateEvents = listOf<CurrentStateEvent>(
        SwitchStateEvent("event1", LocalDateTime.now(), "mrid1", SwitchAction.OPEN, PhaseCode.ABC),
        SwitchStateEvent("event2", LocalDateTime.now(), "mrid1", SwitchAction.CLOSE, PhaseCode.ABN),
        SwitchStateEvent("event3", LocalDateTime.now(), "mrid2", SwitchAction.CLOSE, PhaseCode.A)
    )
    private val switchStateEvents = currentStateEvents.filterIsInstance<SwitchStateEvent>()
    private val responseCurrentStateEvents = listOf(currentStateEvents.take(2), currentStateEvents.drop(2))
    private val fromSlot = slot<LocalDateTime>()
    private val toSlot = slot<LocalDateTime>()
    private val onGetCurrentStatesSequence = mockk<(from: LocalDateTime?, to: LocalDateTime?) -> Sequence<List<CurrentStateEvent>>>().also {
        every { it(capture(fromSlot), capture(toSlot)) } returns responseCurrentStateEvents.asSequence()
    }
    private val onGetCurrentStatesStream = mockk<QueryNetworkStateService.GetCurrentStates>().also {
        every { it.get(capture(fromSlot), capture(toSlot)) } returns responseCurrentStateEvents.stream()
    }
    private val responseSlot = mutableListOf<GetCurrentStatesResponse>()
    private val responseErrorSlot = slot<Throwable>()
    private val responseObserver = mockk<StreamObserver<GetCurrentStatesResponse>>().also {
        justRun { it.onNext(capture(responseSlot)) }
        justRun { it.onCompleted() }
        justRun { it.onError(capture(responseErrorSlot)) }
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
        assertGetCurrentStates {
            verify { onGetCurrentStatesSequence(fromSlot.captured, toSlot.captured) }
        }

        clearMocks(responseObserver, onGetCurrentStatesSequence, onGetCurrentStatesStream, answers = false)
        responseSlot.clear()

        serviceStream.getCurrentStates(request, responseObserver)
        assertGetCurrentStates {
            verify { onGetCurrentStatesStream.get(fromSlot.captured, toSlot.captured) }
        }
    }

    @Test
    fun `getCurrentStates handles error`(){
        every { onGetCurrentStatesSequence(any(), any()) } throws Error("TEST ERROR!")

        serviceSequence.getCurrentStates(request, responseObserver)

        assertThat(responseErrorSlot.captured, instanceOf(StatusRuntimeException::class.java))
        (responseErrorSlot.captured as StatusRuntimeException).status.let {
            assertThat(it.code, equalTo(Status.INTERNAL.code))
            assertThat(it.description, equalTo("TEST ERROR!"))
        }

        verify {
            responseObserver.onError(responseErrorSlot.captured)
        }
    }

    private fun assertGetCurrentStates(onAdditionalVerification: (() -> Unit)? = null) {
        assertThat(fromSlot.captured, equalTo(request.from.toLocalDateTime()))
        assertThat(toSlot.captured, equalTo(request.to.toLocalDateTime()))

        assertThat(responseSlot.map { it.messageId }, contains(1, 1))
        responseSlot.flatMap { it.eventList }.let {
            assertThat(it.map { it.eventId }, contains(*currentStateEvents.map { it.eventId }.toTypedArray()))
            assertThat(it.map { it.timestamp }, contains(*currentStateEvents.map { it.timestamp.toTimestamp() }.toTypedArray()))
            assertThat(it.map { it.switch.mrid }, contains(*switchStateEvents.map { it.mRID }.toTypedArray()))
            assertThat(it.map { it.switch.action.name }, contains(*switchStateEvents.map { it.action.name }.toTypedArray()))
            assertThat(it.map { it.switch.phases.name }, contains(*switchStateEvents.map { it.phases.name }.toTypedArray()))
        }

        onAdditionalVerification?.let { it() }

        verifySequence {
            responseObserver.onNext(responseSlot[0])
            responseObserver.onNext(responseSlot[1])
            responseObserver.onCompleted()
        }
    }
}

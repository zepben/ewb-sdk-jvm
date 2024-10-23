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
import com.zepben.evolve.conn.grpc.GrpcException
import com.zepben.evolve.services.common.translator.toLocalDateTime
import com.zepben.evolve.services.common.translator.toTimestamp
import com.zepben.evolve.streaming.data.*
import com.zepben.protobuf.ns.SetCurrentStatesRequest
import com.zepben.protobuf.ns.SetCurrentStatesResponse
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.stub.StreamObserver
import io.mockk.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import com.zepben.protobuf.ns.data.SwitchAction as PBSwitchAction
import com.zepben.protobuf.cim.iec61970.base.core.PhaseCode as PBPhaseCode
import com.zepben.protobuf.ns.data.SwitchStateEvent as PBSwitchStateEvent
import com.zepben.protobuf.ns.data.CurrentStateEvent as PBCurrentStateEvent
import com.zepben.protobuf.ns.data.StateEventFailure as PBStateEventFailure

class UpdateNetworkStateServiceTest {
    private val eventsSlot = slot<List<CurrentStateEvent>>()
    private val setCurrentStatesReturns = listOf(
        BatchSuccessful(),
        ProcessingPaused(LocalDateTime.now()),
        BatchFailure(true, listOf(
            StateEventUnknownMrid("event id"),
            StateEventDuplicateMrid("event id"),
            StateEventInvalidMrid("event id"),
            StateEventUnsupportedPhasing("event id")
        ))
    )
    private val onSetCurrentStates = mockk<(events: List<CurrentStateEvent>) -> SetCurrentStatesStatus>().also {
        every { it(capture(eventsSlot)) } returnsMany setCurrentStatesReturns
    }
    private val responseSlot = slot<SetCurrentStatesResponse>()
    private val responseErrorSlot = slot<Throwable>()
    private val responseObserver = mockk<StreamObserver<SetCurrentStatesResponse>>().also {
        justRun { it.onNext(capture(responseSlot)) }
        justRun { it.onCompleted() }
        justRun { it.onError(capture(responseErrorSlot)) }
    }
    private val service = UpdateNetworkStateService(onSetCurrentStates)
    private val requestObserver = service.setCurrentStates(responseObserver)
    private val timeStamp = Timestamp.newBuilder().apply { seconds = 1 }.build()
    private val request = SetCurrentStatesRequest.newBuilder().apply {
        messageId = 1
        addEvent(PBCurrentStateEvent.newBuilder().apply {
            eventId = "event id"
            timestamp = timeStamp
            switch = PBSwitchStateEvent.newBuilder().apply {
                mrid = "mr id"
                action = PBSwitchAction.OPEN
                phases = PBPhaseCode.ABCN
            }.build()
        }.build())
    }.build()

    @Test
    fun setCurrentStates(){
        setCurrentStatesTest(SetCurrentStatesResponse.StatusCase.SUCCESS)
        setCurrentStatesTest(SetCurrentStatesResponse.StatusCase.PAUSED){
            assertThat(it.paused.since, equalTo((setCurrentStatesReturns[1] as ProcessingPaused).since.toTimestamp()))
        }
        setCurrentStatesTest(SetCurrentStatesResponse.StatusCase.FAILURE){ response ->
            response.failure.let { failure ->
                assertThat(failure.partialFailure, equalTo(true))
                assertThat(failure.failedList.map { it.reasonCase }, contains(
                    PBStateEventFailure.ReasonCase.UNKNOWNMRID,
                    PBStateEventFailure.ReasonCase.DUPLICATEMRID,
                    PBStateEventFailure.ReasonCase.INVALIDMRID,
                    PBStateEventFailure.ReasonCase.UNSUPPORTEDPHASING))
            }

        }
    }

    @Test
    fun `setCurrentStates onNext handles error`(){
        every { onSetCurrentStates(any()) } throws Error("TEST ERROR!")

        requestObserver.onNext(request)

        assertThat(responseErrorSlot.captured, instanceOf(StatusRuntimeException::class.java))
        (responseErrorSlot.captured as StatusRuntimeException).status.let {
            assertThat(it.code, equalTo(Status.INTERNAL.code))
            assertThat(it.description, equalTo("TEST ERROR!"))
        }

        verify {
            responseObserver.onError(responseErrorSlot.captured)
        }
    }

    @Test
    fun `setCurrentStates onError throws GrpcException`(){
        val throwable = Status.INTERNAL.withDescription("TEST ERROR!").asRuntimeException()
        val exception = assertThrows<GrpcException> { requestObserver.onError(throwable) }

        assertThat(exception.cause, equalTo(throwable))
        assertThat(exception.message, equalTo("Serialization failed due to: ${throwable.localizedMessage}"))
    }

    @Test
    fun `setCurrentStates onCompleted`(){
        requestObserver.onCompleted()

        verify {
            responseObserver.onCompleted()
        }
    }

    private fun setCurrentStatesTest(statusCase: SetCurrentStatesResponse.StatusCase, onResponseAssertion: ((SetCurrentStatesResponse) -> Unit)? = null){
        requestObserver.onNext(request)

        eventsSlot.captured.let{
            assertThat(it.size, equalTo(1))
            assertThat(it[0], instanceOf(SwitchStateEvent::class.java))
            (it[0] as SwitchStateEvent).let {
                assertThat(it.eventId, equalTo("event id"))
                assertThat(it.timestamp, equalTo(timeStamp.toLocalDateTime()))
                assertThat(it.mRID, equalTo("mr id"))
                assertThat(it.action, equalTo(SwitchAction.OPEN))
                assertThat(it.phases, equalTo(PhaseCode.ABCN))
            }

        }
        responseSlot.captured.let {
            assertThat(it.messageId, equalTo(1))
            assertThat(it.statusCase, equalTo(statusCase))
            onResponseAssertion?.let { fn -> fn(it) }
        }

        verify {
            onSetCurrentStates(eventsSlot.captured)
            responseObserver.onNext(responseSlot.captured)
        }
    }
}

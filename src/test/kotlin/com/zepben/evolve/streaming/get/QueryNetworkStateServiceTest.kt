/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.streaming.get

import com.google.protobuf.Empty
import com.google.protobuf.Timestamp
import com.zepben.evolve.cim.iec61970.base.core.PhaseCode
import com.zepben.evolve.conn.grpc.GrpcException
import com.zepben.evolve.services.common.translator.toLocalDateTime
import com.zepben.evolve.services.common.translator.toTimestamp
import com.zepben.evolve.streaming.data.*
import com.zepben.protobuf.ns.GetCurrentStatesRequest
import com.zepben.protobuf.ns.GetCurrentStatesResponse
import com.zepben.protobuf.ns.SetCurrentStatesResponse
import com.zepben.testutils.exception.ExpectException.Companion.expect
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.stub.StreamObserver
import io.grpc.testing.GrpcCleanupRule
import io.mockk.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Rule
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

internal class QueryNetworkStateServiceTest {

    @JvmField
    @Rule
    val grpcCleanup: GrpcCleanupRule = GrpcCleanupRule()

    private val currentStateEvents = listOf<CurrentStateEvent>(
        SwitchStateEvent("event1", LocalDateTime.now(), "mrid1", SwitchAction.OPEN, PhaseCode.ABC),
        SwitchStateEvent("event2", LocalDateTime.now(), "mrid1", SwitchAction.CLOSE, PhaseCode.ABN),
        SwitchStateEvent("event3", LocalDateTime.now(), "mrid2", SwitchAction.CLOSE, PhaseCode.A)
    )
    private val currentStateEventBatches = listOf(
        CurrentStateEventBatch(1, currentStateEvents.take(2)),
        CurrentStateEventBatch(2, currentStateEvents.drop(2))
    )

    // Callbacks for the Kotlin/lambda constructor.
    private val onGetCurrentStates = mockk<(LocalDateTime?, LocalDateTime?) -> Sequence<CurrentStateEventBatch>>().also {
        every { it(any(), any()) } returns currentStateEventBatches.asSequence()
    }
    private val onCurrentStatesStatus = mockk<(SetCurrentStatesStatus) -> Unit>().also { justRun { it(any()) } }
    private val onProcessingError = mockk<(GrpcException) -> Unit>().also { justRun { it(any()) } }

    // Callbacks for the Java/interface constructor.
    private val onGetCurrentStatesInterface = mockk<QueryNetworkStateService.GetCurrentStates> {
        every { get(any(), any()) } returns currentStateEventBatches.stream()
    }
    private val onCurrentStatesStatusInterface = mockk<QueryNetworkStateService.CurrentStatesStatusHandler> { justRun { handle(any()) } }
    private val onProcessingErrorInterface = mockk<QueryNetworkStateService.ProcessingErrorHandler> { justRun { handle(any()) } }

    private val service = QueryNetworkStateService(onGetCurrentStates, onCurrentStatesStatus, onProcessingError)
    private val serviceJava = QueryNetworkStateService(onGetCurrentStatesInterface, onCurrentStatesStatusInterface, onProcessingErrorInterface)

    @Test
    internal fun getCurrentStates() {
        service.assertGetCurrentStates { request ->
            verify { onGetCurrentStates(request.fromTimestamp.toLocalDateTime(), request.toTimestamp.toLocalDateTime()) }
        }
    }

    @Test
    internal fun `getCurrentStates using Java`() {
        serviceJava.assertGetCurrentStates { request ->
            verify { onGetCurrentStatesInterface.get(request.fromTimestamp.toLocalDateTime(), request.toTimestamp.toLocalDateTime()) }
        }
    }

    @Test
    internal fun `getCurrentStates handles error`() {
        val responseObserver = mockk<StreamObserver<GetCurrentStatesResponse>> { justRun { onError(any()) } }
        val error = Error("TEST ERROR!")
        every { onGetCurrentStates(any(), any()) } throws error

        service.getCurrentStates(GetCurrentStatesRequest.newBuilder().build(), responseObserver)

        val responseError = slot<Throwable>()
        verifySequence { responseObserver.onError(capture(responseError)) }

        assertThat(responseError.captured, instanceOf(StatusRuntimeException::class.java))
        (responseError.captured as StatusRuntimeException).status.let {
            assertThat(it.code, equalTo(Status.UNKNOWN.code))
            assertThat(it.cause, equalTo(error))
        }
    }

    @Test
    internal fun `can receive status responses`() {
        val responseObserver = mockk<StreamObserver<Empty>> { justRun { onCompleted() } }
        val requestObserver = service.reportBatchStatus(responseObserver)

        requestObserver.onNext(BatchNotProcessed(1L).toPb())
        requestObserver.onNext(BatchSuccessful(2L).toPb())
        requestObserver.onCompleted()

        val exception = Exception("Test")
        expect { requestObserver.onError(exception) }.toThrow<Exception>().withMessage("Test")

        val statuses = mutableListOf<SetCurrentStatesStatus>()
        verifySequence {
            onCurrentStatesStatus(capture(statuses))
            onCurrentStatesStatus(capture(statuses))
            responseObserver.onCompleted()
        }

        assertThat(statuses.map { it.batchId }, contains(1L, 2L))
        assertThat(statuses.map { it::class }, contains(BatchNotProcessed::class, BatchSuccessful::class))
    }

    @Test
    internal fun `calls process error handler with unknown status responses`() {
        val responseObserver = mockk<StreamObserver<Empty>> { justRun { onCompleted() } }
        val requestObserver = service.reportBatchStatus(responseObserver)

        // Not sure if/how we can set this to something not supported like you would get from a future version, so just leave it blank.
        requestObserver.onNext(SetCurrentStatesResponse.newBuilder().setMessageId(1).build())
        requestObserver.onCompleted()

        val error = slot<GrpcException>()
        verifySequence {
            onProcessingError(capture(error))
            responseObserver.onCompleted()
        }

        assertThat(
            error.captured.message,
            equalTo("Failed to decode status response for batch `1`: Unsupported type STATUS_NOT_SET")
        )
    }

    @Test
    internal fun `calls process error handler with exception in status handler`() {
        val responseObserver = mockk<StreamObserver<Empty>> { justRun { onCompleted() } }
        val requestObserver = service.reportBatchStatus(responseObserver)

        // Not sure if/how we can set this to something not supported like you would get from a future version, so just leave it blank.
        requestObserver.onNext(SetCurrentStatesResponse.newBuilder().setMessageId(1).build())
        requestObserver.onCompleted()

        val error = slot<GrpcException>()
        verifySequence {
            onProcessingError(capture(error))
            responseObserver.onCompleted()
        }

        assertThat(
            error.captured.message,
            equalTo("Failed to decode status response for batch `1`: Unsupported type STATUS_NOT_SET")
        )
    }

    private fun QueryNetworkStateService.assertGetCurrentStates(onAdditionalVerification: (GetCurrentStatesRequest) -> Unit = {}) {
        val request = GetCurrentStatesRequest.newBuilder().apply {
            messageId = 1
            fromTimestamp = Timestamp.newBuilder().apply { nanos = 1 }.build()
            toTimestamp = Timestamp.newBuilder().apply { seconds = 1 }.build()
        }.build()

        val responseObserver = mockk<StreamObserver<GetCurrentStatesResponse>> {
            justRun { onNext(any()) }
            justRun { onCompleted() }
        }

        // This is called on the receiver.
        getCurrentStates(request, responseObserver)

        val response1 = slot<GetCurrentStatesResponse>()
        val response2 = slot<GetCurrentStatesResponse>()
        verifySequence {
            responseObserver.onNext(capture(response1))
            responseObserver.onNext(capture(response2))
            responseObserver.onCompleted()
        }

        onAdditionalVerification(request)

        fun GetCurrentStatesResponse.validate(expectedBatch: CurrentStateEventBatch) {
            assertThat(messageId, equalTo(expectedBatch.batchId))

            assertThat(eventList.map { it.eventId }, contains(*expectedBatch.events.map { it.eventId }.toTypedArray()))
            assertThat(eventList.map { it.timestamp }, contains(*expectedBatch.events.map { it.timestamp.toTimestamp() }.toTypedArray()))

            val expectedSwitchEvents = expectedBatch.events.filterIsInstance<SwitchStateEvent>()
            assertThat(eventList.map { it.switch.mrid }, contains(*expectedSwitchEvents.map { it.mRID }.toTypedArray()))
            assertThat(eventList.map { it.switch.action.name }, contains(*expectedSwitchEvents.map { it.action.name }.toTypedArray()))
            assertThat(eventList.map { it.switch.phases.name }, contains(*expectedSwitchEvents.map { it.phases.name }.toTypedArray()))
        }

        response1.captured.validate(currentStateEventBatches[0])
        response2.captured.validate(currentStateEventBatches[1])
    }

}

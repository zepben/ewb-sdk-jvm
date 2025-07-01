/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.streaming.mutations

import com.zepben.ewb.cim.iec61970.base.core.PhaseCode
import com.zepben.ewb.services.common.translator.toTimestamp
import com.zepben.ewb.streaming.data.*
import com.zepben.ewb.streaming.grpc.GrpcChannel
import com.zepben.ewb.streaming.grpc.TokenCallCredentials
import com.zepben.ewb.streaming.mutations.testservices.TestUpdateNetworkStateService
import com.zepben.protobuf.ns.SetCurrentStatesRequest
import com.zepben.protobuf.ns.SetCurrentStatesResponse
import com.zepben.protobuf.ns.UpdateNetworkStateServiceGrpc
import io.grpc.inprocess.InProcessChannelBuilder
import io.grpc.inprocess.InProcessServerBuilder
import io.grpc.testing.GrpcCleanupRule
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Rule
import org.junit.jupiter.api.Test
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.atLeastOnce
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify
import java.time.LocalDateTime
import kotlin.streams.toList
import com.zepben.protobuf.ns.data.BatchFailure as PBBatchFailure
import com.zepben.protobuf.ns.data.BatchNotProcessed as PBBatchNotProcessed
import com.zepben.protobuf.ns.data.BatchSuccessful as PBBatchSuccessful
import com.zepben.protobuf.ns.data.StateEventDuplicateMrid as PBStateEventDuplicateMrid
import com.zepben.protobuf.ns.data.StateEventFailure as PBStateEventFailure
import com.zepben.protobuf.ns.data.StateEventInvalidMrid as PBStateEventInvalidMrid
import com.zepben.protobuf.ns.data.StateEventUnknownMrid as PBStateEventUnknownMrid
import com.zepben.protobuf.ns.data.StateEventUnsupportedPhasing as PBStateEventUnsupportedPhasing
import org.mockito.kotlin.any as mockitoAny

internal class UpdateNetworkStateClientTest {

    @JvmField
    @Rule
    val grpcCleanup: GrpcCleanupRule = GrpcCleanupRule()

    private val serverName = InProcessServerBuilder.generateName()
    private val channel = grpcCleanup.register(InProcessChannelBuilder.forName(serverName).directExecutor().build())
    private val stub = spy(UpdateNetworkStateServiceGrpc.newStub(channel))
    private val client = UpdateNetworkStateClient(stub, null)
    private val service = TestUpdateNetworkStateService()

    private val currentStateEvents = listOf<CurrentStateEvent>(
        SwitchStateEvent("event1", LocalDateTime.now(), "mrid1", SwitchAction.OPEN, PhaseCode.ABC),
        SwitchStateEvent("event2", LocalDateTime.now(), "mrid1", SwitchAction.CLOSE, PhaseCode.ABN),
        SwitchStateEvent("event3", LocalDateTime.now(), "mrid2", SwitchAction.CLOSE),
        SwitchStateEvent("event4", LocalDateTime.now(), "mrid2", SwitchAction.OPEN),
    )
    private val switchStateEvents = currentStateEvents.filterIsInstance<SwitchStateEvent>()
    private val batches = currentStateEvents.mapIndexed { index, item ->
        CurrentStateEventBatch(index.toLong(), listOf(item))
    }

    init {
        grpcCleanup.register(InProcessServerBuilder.forName(serverName).directExecutor().addService(service).build().start())
    }

    @Test
    internal fun `constructor coverage`() {
        UpdateNetworkStateClient(GrpcChannel(channel), TokenCallCredentials { "auth-token" })
        UpdateNetworkStateClient(channel, TokenCallCredentials { "auth-token" })
    }

    @Test
    internal fun `setCurrentStates in batches using Kotlin Sequence`() {
        testSetCurrentStates {
            assertBatchedCurrentStatesResponse(client.setCurrentStates(batches.asSequence()).toList())

            //verify request arguments
            val requests = argumentCaptor<SetCurrentStatesRequest>()
            verify(service.onSetCurrentStates, atLeastOnce()).invoke(requests.capture(), mockitoAny())

            requests.allValues.flatMap { it.eventList }.apply {
                assertThat(map { it.eventId }, contains(*switchStateEvents.map { it.eventId }.toTypedArray()))
                assertThat(map { it.timestamp }, contains(*switchStateEvents.map { it.timestamp.toTimestamp() }.toTypedArray()))
                assertThat(map { it.switch.action.name }, contains(*switchStateEvents.map { endsWith(it.action.name) }.toTypedArray()))
                assertThat(map { it.switch.phases.name }, contains(*switchStateEvents.map { endsWith(it.phases.name) }.toTypedArray()))
            }
        }
    }

    @Test
    internal fun `setCurrentStates in batches using Java Stream`() {
        testSetCurrentStates { assertBatchedCurrentStatesResponse(client.setCurrentStates(batches.stream()).toList()) }
    }

    @Test
    internal fun `setCurrentStates in single batch`() {
        testSetCurrentStates {
            batches.first().apply {
                listOf(client.setCurrentStates(batchId, events)).assert<BatchSuccessful>(0)
            }
        }
    }

    private fun testSetCurrentStates(act: () -> Unit) {
        val responses = ArrayDeque(
            listOf(
                SetCurrentStatesResponse.newBuilder().apply { success = PBBatchSuccessful.newBuilder().build() },
                SetCurrentStatesResponse.newBuilder().apply {
                    failure = PBBatchFailure.newBuilder().apply {
                        partialFailure = true
                        addAllFailed(
                            listOf(
                                PBStateEventFailure.newBuilder().apply {
                                    eventId = "event1"
                                    unknownMrid = PBStateEventUnknownMrid.newBuilder().build()
                                }.build(),
                                PBStateEventFailure.newBuilder().apply {
                                    eventId = "event2"
                                    duplicateMrid = PBStateEventDuplicateMrid.newBuilder().build()
                                }.build(),
                                PBStateEventFailure.newBuilder().apply {
                                    eventId = "event3"
                                    invalidMrid = PBStateEventInvalidMrid.newBuilder().build()
                                }.build(),
                                PBStateEventFailure.newBuilder().apply {
                                    eventId = "event4"
                                    unsupportedPhasing = PBStateEventUnsupportedPhasing.newBuilder().build()
                                }.build()
                            )
                        )
                    }.build()
                },
                SetCurrentStatesResponse.newBuilder().apply { notProcessed = PBBatchNotProcessed.newBuilder().build() },
                // Test for unknown message types. Could also be `null`, but the intentions are not as clear.
                SetCurrentStatesResponse.newBuilder()
            )
        )

        // We must have the same number of responses as batches to prevent errors about ArrayDeque having no elements.
        assertThat(responses, hasSize(batches.size))

        service.onSetCurrentStates = spy { request, response ->
            response.onNext(responses.removeFirst()?.apply { messageId = request.messageId }?.build())
        }

        act()
    }

    private fun assertBatchedCurrentStatesResponse(currentStatesStatus: List<SetCurrentStatesStatus>) {
        // The unknown message type shouldn't have been included in the results.
        assertThat(currentStatesStatus, hasSize(batches.size - 1))

        currentStatesStatus.assert<BatchSuccessful>(0)
        currentStatesStatus.assert<BatchFailure>(1) {
            assertThat("Should have been a partial failure", it.partialFailure)
            assertThat(
                it.failures.map { f -> f::class },
                contains(
                    StateEventUnknownMrid::class,
                    StateEventDuplicateMrid::class,
                    StateEventInvalidMrid::class,
                    StateEventUnsupportedPhasing::class
                )
            )
            it.failures.forEachIndexed { index, f ->
                assertThat(f.eventId, equalTo("event${index + 1}"))
            }
        }
        currentStatesStatus.assert<BatchNotProcessed>(2)
    }

    private inline fun <reified T> List<SetCurrentStatesStatus>.assert(index: Int, additionalAssertions: (T) -> Unit = {}) {
        this[index].also {
            assertThat(it.batchId, equalTo(index.toLong()))
            assertThat(it, instanceOf(T::class.java))
            additionalAssertions(it as T)
        }
    }

}

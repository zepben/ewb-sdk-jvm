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
import com.zepben.evolve.streaming.data.*
import com.zepben.evolve.streaming.grpc.GrpcChannel
import com.zepben.evolve.streaming.grpc.TokenCallCredentials
import com.zepben.evolve.streaming.mutations.testservices.TestUpdateNetworkStateService
import com.zepben.protobuf.ns.SetCurrentStatesRequest
import com.zepben.protobuf.ns.SetCurrentStatesResponse
import com.zepben.protobuf.ns.UpdateNetworkStateServiceGrpc
import io.grpc.inprocess.InProcessChannelBuilder
import io.grpc.inprocess.InProcessServerBuilder
import io.grpc.testing.GrpcCleanupRule
import org.mockito.kotlin.any as mockitoAny
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Rule
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import java.time.LocalDateTime
import kotlin.streams.asStream
import kotlin.streams.toList
import com.zepben.protobuf.ns.data.BatchFailure as PBBatchFailure
import com.zepben.protobuf.ns.data.BatchSuccessful as PBBatchSuccessful
import com.zepben.protobuf.ns.data.ProcessingPaused as PBProcessingPaused
import com.zepben.protobuf.ns.data.StateEventDuplicateMrid as PBStateEventDuplicateMrid
import com.zepben.protobuf.ns.data.StateEventFailure as PBStateEventFailure
import com.zepben.protobuf.ns.data.StateEventInvalidMrid as PBStateEventInvalidMrid
import com.zepben.protobuf.ns.data.StateEventUnknownMrid as PBStateEventUnknownMrid
import com.zepben.protobuf.ns.data.StateEventUnsupportedPhasing as PBStateEventUnsupportedPhasing

class UpdateNetworkStateClientTest {
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
    private val timestampOf1Second = Timestamp.newBuilder().apply { seconds = 1 }.build()
    private val switchStateEvents = currentStateEvents.filterIsInstance<SwitchStateEvent>()
    private val batches = currentStateEvents.mapIndexed { index, item ->
        UpdateNetworkStateClient.SetCurrentStatesRequest(index.toLong(), listOf(item))
    }.asSequence()

    init {
        grpcCleanup.register(InProcessServerBuilder.forName(serverName).directExecutor().addService(service).build().start())
    }

    @Test
    fun `setCurrentStates in batches using Kotlin Sequence`() {
        testSetCurrentStates {
            assertBatchedCurrentStatesResponse(client.setCurrentStates(batches).toList())

            //verify request arguments
            val requests = argumentCaptor<SetCurrentStatesRequest>()
            verify(service.onSetCurrentStates, atLeastOnce()).invoke(requests.capture(), mockitoAny())

            requests.allValues.flatMap { it.eventList }.apply {
                assertThat(map { it.eventId }, contains(*switchStateEvents.map { it.eventId }.toTypedArray()))
                assertThat(map { it.timestamp }, contains(*switchStateEvents.map { it.timestamp.toTimestamp() }.toTypedArray()))
                assertThat(map { it.switch.action.name }, contains(*switchStateEvents.map { it.action.name }.toTypedArray()))
                assertThat(map { it.switch.phases.name }, contains(*switchStateEvents.map { it.phases.name }.toTypedArray()))
            }
        }
    }

    @Test
    fun `setCurrentStates in batches using Java Stream`() {
        testSetCurrentStates { assertBatchedCurrentStatesResponse(client.setCurrentStates(batches.asStream()).toList()) }
    }

    @Test
    fun `setCurrentStates in single batch`() {
        testSetCurrentStates {
            batches.first().apply {
                listOf(client.setCurrentStates(batchId, events)).assert<BatchSuccessful>(0)
            }
        }
    }

    @Test
    fun `constructor coverage`() {
        testSetCurrentStates {
            assertBatchedCurrentStatesResponse(
                UpdateNetworkStateClient(GrpcChannel(channel), TokenCallCredentials({ "auth-token" })).setCurrentStates(batches).toList()
            )
        }

        testSetCurrentStates {
            assertBatchedCurrentStatesResponse(
                UpdateNetworkStateClient(channel, TokenCallCredentials({ "auth-token" })).setCurrentStates(batches).toList()
            )
        }
    }

    private fun testSetCurrentStates(act: () -> Unit) {
        val responses = ArrayDeque(
            listOf(
                SetCurrentStatesResponse.newBuilder().apply { success = PBBatchSuccessful.newBuilder().build() },
                SetCurrentStatesResponse.newBuilder().apply { paused = PBProcessingPaused.newBuilder().apply { since = timestampOf1Second }.build() },
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
                null
            )
        )
        service.onSetCurrentStates = spy { request, response ->
            response.onNext(responses.removeFirst()?.apply { messageId = request.messageId }?.build())
        }

        act()
    }

    private fun assertBatchedCurrentStatesResponse(currentStatesStatus: List<SetCurrentStatesStatus>) {
        assertThat(currentStatesStatus.size, equalTo(3))
        currentStatesStatus.assert<BatchSuccessful>(0)
        currentStatesStatus.assert<ProcessingPaused>(1) {
            assertThat(it.since, equalTo(timestampOf1Second.toLocalDateTime()))
        }
        currentStatesStatus.assert<BatchFailure>(2) {
            assertThat(it.partialFailure, equalTo(true))
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
    }

    private inline fun <reified T> List<SetCurrentStatesStatus>.assert(index: Int, additionalAssertions: (T) -> Unit = {}) {
        this[index].also {
            assertThat(it.batchId, equalTo(index.toLong()))
            assertThat(it, instanceOf(T::class.java))
            additionalAssertions(it as T)
        }
    }
}

/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.streaming.get

import com.google.protobuf.Timestamp
import com.zepben.evolve.cim.iec61970.base.core.PhaseCode
import com.zepben.evolve.services.common.translator.toLocalDateTime
import com.zepben.evolve.services.common.translator.toTimestamp
import com.zepben.evolve.streaming.data.*
import com.zepben.evolve.streaming.get.testservices.TestUpdateNetworkStateService
import com.zepben.protobuf.ns.SetCurrentStatesResponse
import com.zepben.protobuf.ns.UpdateNetworkStateServiceGrpc
import com.zepben.protobuf.ns.data.BatchSuccessful as PBBatchSuccessful
import com.zepben.protobuf.ns.data.ProcessingPaused as PBProcessingPaused
import com.zepben.protobuf.ns.data.BatchFailure as PBBatchFailure
import com.zepben.protobuf.ns.data.StateEventFailure as PBStateEventFailure
import com.zepben.protobuf.ns.data.StateEventUnknownMrid as PBStateEventUnknownMrid
import com.zepben.protobuf.ns.data.StateEventDuplicateMrid as PBStateEventDuplicateMrid
import com.zepben.protobuf.ns.data.StateEventInvalidMrid as PBStateEventInvalidMrid
import com.zepben.protobuf.ns.data.StateEventUnsupportedPhasing as PBStateEventUnsupportedPhasing
import io.grpc.inprocess.InProcessChannelBuilder
import io.grpc.inprocess.InProcessServerBuilder
import io.grpc.testing.GrpcCleanupRule
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Rule
import org.junit.jupiter.api.Test
import org.mockito.kotlin.spy
import java.time.LocalDateTime
import java.util.concurrent.Executors
import kotlin.streams.asStream
import kotlin.streams.toList

class UpdateNetworkStateClientTest {
    @JvmField
    @Rule
    val grpcCleanup: GrpcCleanupRule = GrpcCleanupRule()

    private val serverName = InProcessServerBuilder.generateName()
    private val channel = grpcCleanup.register(InProcessChannelBuilder.forName(serverName).directExecutor().build())
    private val stub = spy(UpdateNetworkStateServiceGrpc.newStub(channel).withExecutor(Executors.newSingleThreadExecutor()))
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
    private val batches = currentStateEvents.map { listOf(it) }.asSequence()

    init {
        grpcCleanup.register(InProcessServerBuilder.forName(serverName).directExecutor().addService(service).build().start())
    }

    @Test
    fun `setCurrentStates in batches using Kotlin Sequence`(){
        testSetCurrentStates { assertBatchedCurrentStatesStatus(client.setCurrentStates(batches).toList()) }
    }

    @Test
    fun `setCurrentStates in batches using Java Stream`(){
        testSetCurrentStates { assertBatchedCurrentStatesStatus(client.setCurrentStates(batches.asStream()).toList()) }
    }

    @Test
    fun `setCurrentStates in single batch`(){
        testSetCurrentStates {
            val status = client.setCurrentStates(batches.first())
            assertThat(status, instanceOf(BatchSuccessful::class.java))
        }
    }

    private fun testSetCurrentStates(act: () -> Unit){
        val responses = ArrayDeque(listOf(
            SetCurrentStatesResponse.newBuilder().apply { success = PBBatchSuccessful.newBuilder().build() }.build(),
            SetCurrentStatesResponse.newBuilder().apply { paused = PBProcessingPaused.newBuilder().apply { since = timestampOf1Second }.build() }.build(),
            SetCurrentStatesResponse.newBuilder().apply { failure = PBBatchFailure.newBuilder().apply {
                partialFailure = true
                addAllFailed(listOf(
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
                    }.build()))
            }.build() }.build(),
            null
        ))
        service.onSetCurrentStates = spy { request, response ->
            request.eventList.let {
                assertThat(it.map { it.eventId }, anyOf(switchStateEvents.map { hasItem(it.eventId) }))
                assertThat(it.map { it.timestamp }, anyOf(switchStateEvents.map { hasItem(it.timestamp.toTimestamp()) }))
                assertThat(it.map { it.switch.mrid }, anyOf(switchStateEvents.map { hasItem(it.mRID) }))
                assertThat(it.map { it.switch.action.name }, anyOf(switchStateEvents.map { hasItem(it.action.name) }))
                assertThat(it.map { it.switch.phases.name }, anyOf(switchStateEvents.map { hasItem(it.phases.name) }))
            }
            response.onNext(responses.removeFirst())
        }

        act()
    }

    private fun assertBatchedCurrentStatesStatus(currentStatesStatus: List<SetCurrentStatesStatus>){
        assertThat(currentStatesStatus.size, equalTo(3))
        assertThat(currentStatesStatus[0], instanceOf(BatchSuccessful::class.java))
        (currentStatesStatus[1] as ProcessingPaused).let {
            assertThat(it.since, equalTo(timestampOf1Second.toLocalDateTime()))
        }
        (currentStatesStatus[2] as BatchFailure).let {
            assertThat(it.partialFailure, equalTo(true))
            assertThat(it.failures.size, equalTo(4))
            it.failures.assertFailure(0, StateEventUnknownMrid::class.java)
            it.failures.assertFailure(1, StateEventDuplicateMrid::class.java)
            it.failures.assertFailure(2, StateEventInvalidMrid::class.java)
            it.failures.assertFailure(3, StateEventUnsupportedPhasing::class.java)
        }
    }

    private fun List<StateEventFailure>.assertFailure(index: Int, clazz: Class<out StateEventFailure>){
        this[index].let {
            assertThat(it.eventId, equalTo("event${index + 1}"))
            assertThat(it, instanceOf(clazz))
        }
    }
}

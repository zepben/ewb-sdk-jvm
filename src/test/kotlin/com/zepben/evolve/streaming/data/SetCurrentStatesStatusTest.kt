/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.streaming.data

import com.google.protobuf.Timestamp
import com.zepben.evolve.services.common.translator.toLocalDateTime
import com.zepben.protobuf.ns.SetCurrentStatesResponse as PBSetCurrentStatesResponse
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import com.zepben.protobuf.ns.data.BatchSuccessful as PBBatchSuccessful
import com.zepben.protobuf.ns.data.ProcessingPaused as PBProcessingPaused
import com.zepben.protobuf.ns.data.BatchFailure as PBBatchFailure
import com.zepben.protobuf.ns.data.StateEventFailure as PBStateEventFailure
import com.zepben.protobuf.ns.data.StateEventUnknownMrid as PBStateEventUnknownMrid
import com.zepben.protobuf.ns.data.StateEventDuplicateMrid as PBStateEventDuplicateMrid
import com.zepben.protobuf.ns.data.StateEventInvalidMrid as PBStateEventInvalidMrid
import com.zepben.protobuf.ns.data.StateEventUnsupportedPhasing as PBStateEventUnsupportedPhasing

class SetCurrentStatesStatusTest {
    private val invalidMridPb = PBStateEventFailure.newBuilder().apply {
        eventId = "event2"
        invalidMrid = PBStateEventInvalidMrid.newBuilder().build()
    }.build()

    @Test
    fun `BatchSuccessful from protobuf and then back to protobuf`() {
        val pb = createSetCurrentStatesResponse { success = PBBatchSuccessful.newBuilder().build() }

        val status = SetCurrentStatesStatus.fromPb(pb)
        assertThat(status, instanceOf(BatchSuccessful::class.java))
        assertThat(status?.batchId, equalTo(1))

        status?.toPb()?.also {
            assertThat(it.messageId, equalTo(1))
            assertThat(it.statusCase, equalTo(PBSetCurrentStatesResponse.StatusCase.SUCCESS))
        }
    }

    @Test
    fun `ProcessingPaused from protobuf and then back to protobuf`() {
        val pb = createSetCurrentStatesResponse {
            paused = PBProcessingPaused.newBuilder().apply { since = Timestamp.newBuilder().apply { seconds = 1 }.build() }.build()
        }

        val status = SetCurrentStatesStatus.fromPb(pb) as ProcessingPaused
        assertThat(status.since, equalTo(pb.paused.since.toLocalDateTime()))
        assertThat(status.batchId, equalTo(1))

        (status as SetCurrentStatesStatus).toPb().also {
            assertThat(it.messageId, equalTo(1))
            assertThat(it.paused.since, equalTo(pb.paused.since))
        }
    }

    @Test
    fun `BatchFailure from protobuf and then back to protobuf`() {
        val pb = createSetCurrentStatesResponse {
            failure = PBBatchFailure.newBuilder().apply {
                partialFailure = true
                addAllFailed(listOf(invalidMridPb))
            }.build()
        }


        val status = SetCurrentStatesStatus.fromPb(pb) as BatchFailure
        assertThat(status.partialFailure, equalTo(pb.failure.partialFailure))
        assertThat(status.failures.size, equalTo(1))
        assertThat(status.failures.first(), instanceOf(StateEventInvalidMrid::class.java))

        (status as SetCurrentStatesStatus).toPb().failure.also {
            assertThat(it.partialFailure, equalTo(pb.failure.partialFailure))
            assertThat(it.failedList.size, equalTo(1))
            assertThat(it.failedList.first().reasonCase, equalTo(PBStateEventFailure.ReasonCase.INVALIDMRID))
        }
    }

    @Test
    fun `StateEventFailure from protobuf and then back to protobuf`() {
        val unknowMridPb = PBStateEventFailure.newBuilder().apply {
            eventId = "event1"
            unknownMrid = PBStateEventUnknownMrid.newBuilder().build()
        }.build()

        val duplicateMridPb = PBStateEventFailure.newBuilder().apply {
            eventId = "event3"
            duplicateMrid = PBStateEventDuplicateMrid.newBuilder().build()
        }.build()

        val unsupportedPhasingPb = PBStateEventFailure.newBuilder().apply {
            eventId = "event4"
            unsupportedPhasing = PBStateEventUnsupportedPhasing.newBuilder().build()
        }.build()

        testStateEventFailure(unknowMridPb, StateEventUnknownMrid::class.java)
        testStateEventFailure(duplicateMridPb, StateEventDuplicateMrid::class.java)
        testStateEventFailure(unsupportedPhasingPb, StateEventUnsupportedPhasing::class.java)
        testStateEventFailure(invalidMridPb, StateEventInvalidMrid::class.java)
        assertThat(StateEventFailure.fromPb(PBStateEventFailure.newBuilder().build()), nullValue())
    }

    private fun testStateEventFailure(pb: PBStateEventFailure, clazz: Class<out StateEventFailure>) {
        val state = StateEventFailure.fromPb(pb)
        assertThat(state?.eventId, equalTo(pb.eventId))
        assertThat(state, instanceOf(clazz))

        state?.toPb()?.let {
            assertThat(it.eventId, equalTo(pb.eventId))
            assertThat(it.reasonCase, equalTo(pb.reasonCase))
        }
    }

    private fun createSetCurrentStatesResponse(block: PBSetCurrentStatesResponse.Builder.() -> Unit): PBSetCurrentStatesResponse =
        PBSetCurrentStatesResponse.newBuilder().also { it.messageId = 1 }.apply(block).build()
}

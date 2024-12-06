/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.streaming.data

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import com.zepben.protobuf.ns.data.StateEventFailure as PBStateEventFailure

internal class SetCurrentStatesStatusTest {

    //
    // NOTE: We don;t bother to check that the correct thing was put into the protobuf variants directly because it is
    //       assumed that if the resulting object coming out the other side is correct then the intermediate one must
    //       have been correct (good enough fo us anyway).
    //

    @Test
    internal fun `Batch successful to and from protobuf`() {
        val original = BatchSuccessful(1)
        val converted = SetCurrentStatesStatus.fromPb(original.toPb())

        assertThat(converted, instanceOf(original.javaClass))
        assertThat(converted?.batchId, equalTo(original.batchId))
    }

    @Test
    internal fun `Batch failure to and from protobuf`() {
        val original = BatchFailure(1, true, listOf(StateEventInvalidMrid("event1", "message1"), StateEventUnsupportedMrid("event2", "message2")))
        val converted = SetCurrentStatesStatus.fromPb(original.toPb())

        assertThat(converted, instanceOf(original.javaClass))
        with(converted as BatchFailure) {
            assertThat(batchId, equalTo(original.batchId))
            assertThat(partialFailure, equalTo(original.partialFailure))

            assertThat(failures.map { it.javaClass }, contains(StateEventInvalidMrid::class.java, StateEventUnsupportedMrid::class.java))
            assertThat(failures.map { it.eventId }, contains("event1", "event2"))
            assertThat(failures.map { it.message }, contains("message1", "message2"))
        }
    }

    @Test
    internal fun `Batch not processed to and from protobuf`() {
        val original = BatchNotProcessed(1)
        val converted = SetCurrentStatesStatus.fromPb(original.toPb())

        assertThat(converted, instanceOf(original.javaClass))
        assertThat(converted?.batchId, equalTo(original.batchId))
    }

    @Test
    internal fun `State event failures to and from protobuf`() {
        testStateEventFailure(StateEventUnknownMrid("event1", "unknown mrid message"))
        testStateEventFailure(StateEventInvalidMrid("event2", "invalid mrid message"))
        testStateEventFailure(StateEventDuplicateMrid("event3", "duplicate mrid message"))
        testStateEventFailure(StateEventUnsupportedPhasing("event4", "unsupported phasing message"))
        testStateEventFailure(StateEventUnsupportedMrid("event5", "unsupported mrid message"))

        // Unknown protobuf types return null.
        assertThat(StateEventFailure.fromPb(PBStateEventFailure.newBuilder().build()), nullValue())
    }

    private fun testStateEventFailure(original: StateEventFailure) {
        val converted = StateEventFailure.fromPb(original.toPb())

        assertThat(converted, instanceOf(original.javaClass))
        assertThat(converted?.eventId, equalTo(original.eventId))
        assertThat(converted?.message, equalTo(original.message))
    }

}

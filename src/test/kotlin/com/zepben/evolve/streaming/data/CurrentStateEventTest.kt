/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.streaming.data

import com.google.protobuf.Timestamp
import com.zepben.evolve.cim.iec61970.base.core.PhaseCode
import com.zepben.evolve.services.common.translator.toLocalDateTime
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import com.zepben.protobuf.cim.iec61970.base.core.PhaseCode as PBPhaseCode
import com.zepben.protobuf.ns.data.AddCutEvent as PBAddCutEvent
import com.zepben.protobuf.ns.data.AddJumperEvent as PBAddJumperEvent
import com.zepben.protobuf.ns.data.CurrentStateEvent as PBCurrentStateEvent
import com.zepben.protobuf.ns.data.RemoveCutEvent as PBRemoveCutEvent
import com.zepben.protobuf.ns.data.RemoveJumperEvent as PBRemoveJumperEvent
import com.zepben.protobuf.ns.data.SwitchAction as PBSwitchAction
import com.zepben.protobuf.ns.data.SwitchStateEvent as PBSwitchStateEvent

internal class CurrentStateEventTest {

    @Test
    internal fun `CurrentStateEvent from protobuf`() {
        val stateEvent = CurrentStateEvent.fromPb(PBCurrentStateEvent.newBuilder().apply {
            switch = PBSwitchStateEvent.newBuilder().build()
        }.build())

        assertThat(stateEvent, instanceOf(SwitchStateEvent::class.java))
    }

    @Test
    internal fun `CurrentStateEvent from protobuf throws UnsupportedException for classes other than switch`() {
        notSupportedState { addCut = PBAddCutEvent.newBuilder().build() }

        notSupportedState { removeCut = PBRemoveCutEvent.newBuilder().build() }

        notSupportedState { addJumper = PBAddJumperEvent.newBuilder().build() }

        notSupportedState { removeJumper = PBRemoveJumperEvent.newBuilder().build() }
    }

    @Test
    internal fun `SwitchStateEvent from protobuf and then back to protobuf`() {
        val time = Timestamp.newBuilder().apply { nanos = 1 }.build()
        val event = PBCurrentStateEvent.newBuilder().apply {
            eventId = "event id"
            timestamp = time
            switch = PBSwitchStateEvent.newBuilder().apply {
                mrid = "MRID"
                action = PBSwitchAction.OPEN
                phases = PBPhaseCode.N
            }.build()
        }.build()

        val currentStateEvent = CurrentStateEvent.fromPb(event)
        (currentStateEvent as SwitchStateEvent).apply {
            assertThat(eventId, equalTo(event.eventId))
            assertThat(timestamp, equalTo(event.timestamp.toLocalDateTime()))
            assertThat(mRID, equalTo(event.switch.mrid))
            assertThat(action, equalTo(SwitchAction.OPEN))
            assertThat(phases, equalTo(PhaseCode.N))
        }

        currentStateEvent.toPb().apply {
            assertThat(eventId, equalTo(event.eventId))
            assertThat(timestamp, equalTo(event.timestamp))
            assertThat(switch.mrid, equalTo(event.switch.mrid))
            assertThat(switch.action, equalTo(event.switch.action))
            assertThat(switch.phases, equalTo(event.switch.phases))
        }
    }

    private fun notSupportedState(block: PBCurrentStateEvent.Builder.() -> Unit) {
        assertThrows<UnsupportedOperationException> {
            CurrentStateEvent.fromPb(PBCurrentStateEvent.newBuilder().apply(block).build())
        }
    }

}

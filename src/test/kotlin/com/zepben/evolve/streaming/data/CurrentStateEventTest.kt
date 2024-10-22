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
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import com.zepben.protobuf.ns.data.CurrentStateEvent as PBCurrentStateEvent
import com.zepben.protobuf.ns.data.SwitchStateEvent as PBSwitchStateEvent

class CurrentStateEventTest {
    @Test
    fun `SwitchStateEvent from protobuf`(){
        // eventId
        val pbBuilder = PBCurrentStateEvent.newBuilder()
        assertSwitchStateEventIllegalArgument(pbBuilder.build(), "eventId is required")
        // timestamp
        pbBuilder.eventId = "event id"
        assertSwitchStateEventIllegalArgument(pbBuilder.build(), "timestamp is invalid")
        pbBuilder.timestamp = Timestamp.newBuilder().build()
        assertSwitchStateEventIllegalArgument(pbBuilder.build(), "timestamp is invalid")
        // mrid
        pbBuilder.timestamp = Timestamp.newBuilder().apply { nanos = 1 }.build()
        assertSwitchStateEventIllegalArgument(pbBuilder.build(), "mrid is required")
        pbBuilder.switch = PBSwitchStateEvent.newBuilder().apply { mrid = "MRID" }.build()

        val switchState = SwitchStateEvent.fromPb(pbBuilder.build())
        assertThat(switchState.eventId, equalTo("event id"))
        assertThat(switchState.timestamp, equalTo(Timestamp.newBuilder().apply { nanos = 1 }.build().toLocalDateTime()))
        assertThat(switchState.mRID, equalTo("MRID"))
        assertThat(switchState.action, equalTo(SwitchAction.UNKNOWN))
        assertThat(switchState.phases, equalTo(PhaseCode.NONE))
    }

    fun assertSwitchStateEventIllegalArgument(pb: PBCurrentStateEvent, message: String){
        assertThat(
            assertThrows<IllegalArgumentException> { SwitchStateEvent.fromPb(pb) }.message,
            equalTo(message)
        )
    }
}

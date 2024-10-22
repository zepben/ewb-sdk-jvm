/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.streaming.data

import com.zepben.evolve.cim.iec61970.base.core.PhaseCode
import com.zepben.evolve.services.common.translator.toLocalDateTime
import com.zepben.evolve.services.common.translator.toTimestamp
import java.time.LocalDateTime
import com.zepben.protobuf.ns.data.CurrentStateEvent as PBCurrentStateEvent
import com.zepben.protobuf.ns.data.SwitchStateEvent as PBSwitchStateEvent
import com.zepben.protobuf.ns.data.SwitchAction as PBSwitchAction
import com.zepben.protobuf.cim.iec61970.base.core.PhaseCode as PBPhaseCode

/**
 * An event to apply to the current state of the network.
 *
 * @property eventId An identifier of this event. This must be unique across requests to allow detection of
 * duplicates when requesting events via dates vs those streamed via live updates.
 * @property timestamp The timestamp when the event occurred.
 */
abstract class CurrentStateEvent(val eventId: String, val timestamp: LocalDateTime){
    internal abstract fun toPb(): PBCurrentStateEvent

    /**
     * Creates a [PBCurrentStateEvent.Builder] builder with [eventId] and [timestamp] assigned.
     */
    protected fun toPbBuilder(): PBCurrentStateEvent.Builder =
        PBCurrentStateEvent.newBuilder().also {
            it.eventId = eventId
            it.timestamp = timestamp.toTimestamp()
        }
}

/**
 * An event to update the state of a switch.
 *
 * @property eventId An identifier of this event. This must be unique across requests to allow detection of
 * duplicates when requesting events via dates vs those streamed via live updates.
 * @property timestamp The timestamp when the event occurred.
 * @property mRID The mRID of the switch affected by this event.
 * @property action The action to take on the switch for the specified phases.
 * @property phases The phases affected by this event. If not specified, all phases will be affected.
 */
class SwitchStateEvent(eventId: String, timestamp: LocalDateTime, val mRID: String, val action: SwitchAction, val phases: PhaseCode) : CurrentStateEvent(eventId, timestamp){
    companion object {
        /**
         * Creates a [SwitchStateEvent] object from protobuf [PBCurrentStateEvent]
         */
        internal fun fromPb(event: PBCurrentStateEvent): SwitchStateEvent{
            require(event.eventId.isNotEmpty()) { "eventId is required" }
            val timestamp = event.timestamp?.toLocalDateTime()
            require(timestamp != null) { "timestamp is invalid" }
            require(event.switch.mrid.isNotEmpty()) { "mrid is required" }

            return SwitchStateEvent(
                event.eventId,
                timestamp,
                event.switch.mrid,
                SwitchAction.valueOf(event.switch.action.name),
                PhaseCode.valueOf(event.switch.phases.name)
            )
        }
    }

    /**
     * Creates a protobuf [PBCurrentStateEvent] object with switch from [SwitchStateEvent]
     */
    override fun toPb(): PBCurrentStateEvent = toPbBuilder().also {
        it.switch = PBSwitchStateEvent.newBuilder().also {
            it.mrid = mRID
            it.action = PBSwitchAction.valueOf(action.name)
            it.phases = PBPhaseCode.valueOf(phases.name)
        }.build()
    }.build()
}


/**
 * Enum representing possible actions for a switch.
 */
enum class SwitchAction {
    /**
     * The specified action was unknown, or was not set.
     */
    UNKNOWN,
    /**
     * A request to open a switch.
     */
    OPEN,
    /**
     * A request to close a switch.
     */
    CLOSE
}

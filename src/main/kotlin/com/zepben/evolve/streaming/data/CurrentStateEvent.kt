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
import com.zepben.protobuf.ns.data.CurrentStateEvent.EventCase

/**
 * An event to apply to the current state of the network.
 *
 * @property eventId An identifier of this event. This must be unique across requests to allow detection of
 * duplicates when requesting events via dates vs those streamed via live updates.
 * @property timestamp The timestamp when the event occurred.
 */
sealed class CurrentStateEvent(
    val eventId: String,
    val timestamp: LocalDateTime?
) {

    companion object {
        /**
         * Creates a [CurrentStateEvent] object from protobuf [PBCurrentStateEvent]
         */
        internal fun fromPb(event: PBCurrentStateEvent): CurrentStateEvent =
            when (event.eventCase) {
                EventCase.SWITCH -> SwitchStateEvent.fromPb(event)
                else -> throw UnsupportedOperationException("'${event.eventCase}' is currently unsupported.")
            }
    }

    internal abstract fun toPb(): PBCurrentStateEvent

    /**
     * Creates a [PBCurrentStateEvent.Builder] builder with [eventId] and [timestamp] assigned.
     */
    protected fun toPbBuilder(): PBCurrentStateEvent.Builder =
        PBCurrentStateEvent.newBuilder().also { pb ->
            pb.eventId = eventId
            timestamp.toTimestamp()?.let { pb.timestamp = it} ?: pb.clearTimestamp()
            pb.timestamp = timestamp.toTimestamp()
        }

}

/**
 * An event to update the state of a switch.
 *
 * @property eventId An identifier of this event. This must be unique across requests to allow detection of
 * duplicates when requesting events via dates vs those streamed via live updates.
 * @property timestamp The timestamp when the event occurred. This is always handled as UTC (Coordinated Universal Time).
 * @property mRID The mRID of the switch affected by this event.
 * @property action The action to take on the switch for the specified phases.
 * @property phases The phases affected by this event. Defaults to NONE.
 */
class SwitchStateEvent @JvmOverloads constructor(
    eventId: String,
    timestamp: LocalDateTime?,
    val mRID: String,
    val action: SwitchAction,
    val phases: PhaseCode = PhaseCode.NONE
) : CurrentStateEvent(eventId, timestamp) {

    companion object {
        /**
         * Creates a [SwitchStateEvent] object from protobuf [PBCurrentStateEvent]
         */
        internal fun fromPb(event: PBCurrentStateEvent): CurrentStateEvent =
            SwitchStateEvent(
                event.eventId,
                event.timestamp.toLocalDateTime(),
                event.switch.mrid,
                SwitchAction.valueOf(event.switch.action.name),
                PhaseCode.valueOf(event.switch.phases.name)
            )
    }

    /**
     * Creates a protobuf [PBCurrentStateEvent] object with switch from [SwitchStateEvent]
     */
    override fun toPb(): PBCurrentStateEvent = toPbBuilder().also { event ->
        event.switch = PBSwitchStateEvent.newBuilder().also {
            it.mrid = mRID
            it.action = PBSwitchAction.valueOf(action.name)
            it.phases = PBPhaseCode.valueOf(phases.name)
        }.build()
    }.build()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SwitchStateEvent

        if (eventId != other.eventId) return false
        if (timestamp != other.timestamp) return false
        if (mRID != other.mRID) return false
        if (action != other.action) return false
        if (phases != other.phases) return false

        return true
    }

    override fun hashCode(): Int = listOf(eventId, timestamp, mRID, action, phases).hashCode()

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

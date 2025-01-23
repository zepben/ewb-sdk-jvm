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
import com.zepben.protobuf.ns.data.CurrentStateEvent.EventCase
import java.time.LocalDateTime
import com.zepben.protobuf.cim.iec61970.base.core.PhaseCode as PBPhaseCode
import com.zepben.protobuf.ns.data.AddCutEvent as PBAddCutEvent
import com.zepben.protobuf.ns.data.AddJumperEvent as PBAddJumperEvent
import com.zepben.protobuf.ns.data.CurrentStateEvent as PBCurrentStateEvent
import com.zepben.protobuf.ns.data.JumperConnection as PBJumperConnection
import com.zepben.protobuf.ns.data.RemoveCutEvent as PBRemoveCutEvent
import com.zepben.protobuf.ns.data.RemoveJumperEvent as PBRemoveJumperEvent
import com.zepben.protobuf.ns.data.SwitchAction as PBSwitchAction
import com.zepben.protobuf.ns.data.SwitchStateEvent as PBSwitchStateEvent

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
                EventCase.ADDCUT -> AddCutEvent.fromPb(event)
                EventCase.REMOVECUT -> RemoveCutEvent.fromPb(event)
                EventCase.ADDJUMPER -> AddJumperEvent.fromPb(event)
                EventCase.REMOVEJUMPER -> RemoveJumperEvent.fromPb(event)
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
            timestamp.toTimestamp()?.let { pb.timestamp = it } ?: pb.clearTimestamp()
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
        internal fun fromPb(event: PBCurrentStateEvent): SwitchStateEvent =
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
 * An event to add a cut to the network.
 *
 * @property eventId An identifier of this event. This must be unique across requests to allow detection of
 * duplicates when requesting events via dates vs those streamed via live updates.
 * @property timestamp The timestamp when the event occurred. This is always handled as UTC (Coordinated Universal Time).
 * @property mRID The mRID of the cut defined by this event. This should match any future remove instructions.
 * @property aclsMRID The mRID of the AC line segment that was cut.
 */
class AddCutEvent(
    eventId: String,
    timestamp: LocalDateTime?,
    val mRID: String,
    val aclsMRID: String
) : CurrentStateEvent(eventId, timestamp) {

    companion object {
        /**
         * Creates a [AddCutEvent] object from protobuf [PBCurrentStateEvent]
         */
        internal fun fromPb(event: PBCurrentStateEvent): AddCutEvent =
            AddCutEvent(
                event.eventId,
                event.timestamp.toLocalDateTime(),
                event.addCut.mrid,
                event.addCut.aclsMRID
            )
    }

    /**
     * Creates a protobuf [PBCurrentStateEvent] object with switch from [AddCutEvent]
     */
    override fun toPb(): PBCurrentStateEvent = toPbBuilder().also { event ->
        event.addCut = PBAddCutEvent.newBuilder().also {
            it.mrid = mRID
            it.aclsMRID = aclsMRID
        }.build()
    }.build()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AddCutEvent

        if (eventId != other.eventId) return false
        if (timestamp != other.timestamp) return false
        if (mRID != other.mRID) return false
        if (aclsMRID != other.aclsMRID) return false

        return true
    }

    override fun hashCode(): Int = listOf(eventId, timestamp, mRID, aclsMRID).hashCode()

}

/**
 * An event to remove a cut from the network.
 *
 * @property eventId An identifier of this event. This must be unique across requests to allow detection of
 * duplicates when requesting events via dates vs those streamed via live updates.
 * @property timestamp The timestamp when the event occurred. This is always handled as UTC (Coordinated Universal Time).
 * @property mRID The mRID of the cut to remove. This should match a previously added cut.
 */
class RemoveCutEvent(
    eventId: String,
    timestamp: LocalDateTime?,
    val mRID: String
) : CurrentStateEvent(eventId, timestamp) {

    companion object {
        /**
         * Creates a [RemoveCutEvent] object from protobuf [PBCurrentStateEvent]
         */
        internal fun fromPb(event: PBCurrentStateEvent): RemoveCutEvent =
            RemoveCutEvent(
                event.eventId,
                event.timestamp.toLocalDateTime(),
                event.removeCut.mrid
            )
    }

    /**
     * Creates a protobuf [PBCurrentStateEvent] object with switch from [RemoveCutEvent]
     */
    override fun toPb(): PBCurrentStateEvent = toPbBuilder().also { event ->
        event.removeCut = PBRemoveCutEvent.newBuilder().also {
            it.mrid = mRID
        }.build()
    }.build()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RemoveCutEvent

        if (eventId != other.eventId) return false
        if (timestamp != other.timestamp) return false
        if (mRID != other.mRID) return false

        return true
    }

    override fun hashCode(): Int = listOf(eventId, timestamp, mRID).hashCode()

}

/**
 * An event to add a jumper to the network.
 *
 * @property eventId An identifier of this event. This must be unique across requests to allow detection of
 * duplicates when requesting events via dates vs those streamed via live updates.
 * @property timestamp The timestamp when the event occurred. This is always handled as UTC (Coordinated Universal Time).
 * @property mRID The mRID of the jumper affected by this event.
 * @property from Information on how this jumper is connected at one end of the jumper.
 * @property to Information on how this jumper is connected at the other end of the jumper.
 */
class AddJumperEvent(
    eventId: String,
    timestamp: LocalDateTime?,
    val mRID: String,
    val from: JumperConnection,
    val to: JumperConnection
) : CurrentStateEvent(eventId, timestamp) {

    companion object {
        /**
         * Creates a [AddJumperEvent] object from protobuf [PBCurrentStateEvent]
         */
        internal fun fromPb(event: PBCurrentStateEvent): AddJumperEvent =
            AddJumperEvent(
                event.eventId,
                event.timestamp.toLocalDateTime(),
                event.addJumper.mrid,
                JumperConnection.fromPb(event.addJumper.fromConnection),
                JumperConnection.fromPb(event.addJumper.toConnection)
            )
    }

    /**
     * Creates a protobuf [PBCurrentStateEvent] object with switch from [AddJumperEvent]
     */
    override fun toPb(): PBCurrentStateEvent = toPbBuilder().also { event ->
        event.addJumper = PBAddJumperEvent.newBuilder().also {
            it.mrid = mRID
            it.fromConnection = from.toPb()
            it.toConnection = to.toPb()
        }.build()
    }.build()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AddJumperEvent

        if (eventId != other.eventId) return false
        if (timestamp != other.timestamp) return false
        if (mRID != other.mRID) return false
        if (from != other.from) return false
        if (to != other.to) return false

        return true
    }

    override fun hashCode(): Int = listOf(eventId, timestamp, mRID, from, to).hashCode()

}

/**
 * An event to remove a jumper from the network.
 *
 * @property eventId An identifier of this event. This must be unique across requests to allow detection of
 * duplicates when requesting events via dates vs those streamed via live updates.
 * @property timestamp The timestamp when the event occurred. This is always handled as UTC (Coordinated Universal Time).
 * @property mRID The mRID of the jumper affected by this event.
 */
class RemoveJumperEvent(
    eventId: String,
    timestamp: LocalDateTime?,
    val mRID: String
) : CurrentStateEvent(eventId, timestamp) {

    companion object {
        /**
         * Creates a [RemoveJumperEvent] object from protobuf [PBCurrentStateEvent]
         */
        internal fun fromPb(event: PBCurrentStateEvent): RemoveJumperEvent =
            RemoveJumperEvent(
                event.eventId,
                event.timestamp.toLocalDateTime(),
                event.removeJumper.mrid
            )
    }

    /**
     * Creates a protobuf [PBCurrentStateEvent] object with switch from [RemoveJumperEvent]
     */
    override fun toPb(): PBCurrentStateEvent = toPbBuilder().also { event ->
        event.removeJumper = PBRemoveJumperEvent.newBuilder().also {
            it.mrid = mRID
        }.build()
    }.build()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RemoveJumperEvent

        if (eventId != other.eventId) return false
        if (timestamp != other.timestamp) return false
        if (mRID != other.mRID) return false

        return true
    }

    override fun hashCode(): Int = listOf(eventId, timestamp, mRID).hashCode()

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

/**
 * Information about how a jumper is connected to the network.
 *
 * @property connectedMRID The mRID of the conducting equipment (or terminal) connected to this end of the jumper.
 */
class JumperConnection(
    val connectedMRID: String
) {

    companion object {
        /**
         * Creates a [JumperConnection] object from protobuf [PBJumperConnection]
         */
        internal fun fromPb(connection: PBJumperConnection): JumperConnection =
            JumperConnection(
                connection.connectedMRID
            )
    }

    /**
     * Creates a protobuf [PBCurrentStateEvent] object with switch from [AddJumperEvent]
     */
    fun toPb(): PBJumperConnection = PBJumperConnection.newBuilder().also {
        it.connectedMRID = connectedMRID
    }.build()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as JumperConnection

        return connectedMRID == other.connectedMRID
    }

    override fun hashCode(): Int = listOf(connectedMRID).hashCode()

}

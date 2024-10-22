/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.streaming.data

import com.zepben.evolve.cim.iec61970.base.core.PhaseCode
import java.time.LocalDateTime

/**
 * An event to apply to the current state of the network.
 *
 * @property eventId An identifier of this event. This must be unique across requests to allow detection of
 * duplicates when requesting events via dates vs those streamed via live updates.
 * @property timestamp The timestamp when the event occurred.
 */
abstract class CurrentStateEvent(val eventId: String, val timestamp: LocalDateTime)

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
class SwitchStateEvent(eventId: String, timestamp: LocalDateTime, val mRID: String, val action: SwitchAction, val phases: PhaseCode) : CurrentStateEvent(eventId, timestamp)


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

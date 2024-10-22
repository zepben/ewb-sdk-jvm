/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.streaming.mutations

import com.zepben.evolve.cim.iec61970.base.core.PhaseCode
import java.time.LocalDateTime

abstract class CurrentStateEvent(val eventId: String, val timestamp: LocalDateTime)

class SwitchStateEvent(eventId: String, timestamp: LocalDateTime, val mRID: String, val action: SwitchAction, val phases: PhaseCode) : CurrentStateEvent(eventId, timestamp)

enum class SwitchAction {
    UNKNOWN,
    OPEN,
    CLOSE
}

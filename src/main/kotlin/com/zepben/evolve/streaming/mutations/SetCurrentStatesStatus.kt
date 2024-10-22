/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.streaming.mutations

import java.time.LocalDateTime

interface SetCurrentStatesStatus
class BatchSuccessful : SetCurrentStatesStatus
data class ProcessingPaused(val since: LocalDateTime) : SetCurrentStatesStatus
data class BatchFailure(val partialFailure: Boolean, val failures: List<StateEventFailure>) : SetCurrentStatesStatus

abstract class StateEventFailure(val eventId: String)

class StateEventUnknownMrid(eventId: String) : StateEventFailure(eventId)
class StateEventDuplicateMrid(eventId: String) : StateEventFailure(eventId)
class StateEventInvalidMrid(eventId: String) : StateEventFailure(eventId)
class StateEventUnsupportedPhasing(eventId: String) : StateEventFailure(eventId)

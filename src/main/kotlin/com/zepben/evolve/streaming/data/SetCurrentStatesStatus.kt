/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.streaming.data

import java.time.LocalDateTime

/**
 * The outcome of processing this batch of updates.
 */
interface SetCurrentStatesStatus

/**
 * A response indicating all items in the batch were applied successfully.
 */
class BatchSuccessful : SetCurrentStatesStatus

/**
 * A response indicating that current state events are not currently being processed. There is no need to retry these,
 * the missed events will be requested when processing resumes.
 *
 * @property since The timestamp when the processing was paused.
 */
data class ProcessingPaused(val since: LocalDateTime) : SetCurrentStatesStatus

/**
 * A response indicating one or more items in the batch couldn't be applied.
 *
 * @property partialFailure Indicates if only come of the batch failed (true), or all entries in the batch failed (false).
 * @property failures The status of each item processed in the batch that failed.
 */
data class BatchFailure(val partialFailure: Boolean, val failures: List<StateEventFailure>) : SetCurrentStatesStatus

/**
 * A wrapper class for allowing a one-of to be repeated.
 *
 * @property eventId The eventId of the state event that failed.
 */
abstract class StateEventFailure(val eventId: String)

/**
 * The requested mRID was not found in the network.
 */
class StateEventUnknownMrid(eventId: String) : StateEventFailure(eventId)

/**
 * The requested mRID already existed in the network and can't be used.
 */
class StateEventDuplicateMrid(eventId: String) : StateEventFailure(eventId)

/**
 * The requested mRID was found in the network model, but was of an invalid type.
 */
class StateEventInvalidMrid(eventId: String) : StateEventFailure(eventId)

/**
 * The requested phasing was not available for the given operation. e.g. An open state request was made with
 * unsupported phases.
 */
class StateEventUnsupportedPhasing(eventId: String) : StateEventFailure(eventId)

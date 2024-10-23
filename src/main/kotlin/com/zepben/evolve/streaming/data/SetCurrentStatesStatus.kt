/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.streaming.data

import com.zepben.evolve.services.common.translator.toTimestamp
import java.time.LocalDateTime
import com.zepben.protobuf.ns.data.BatchSuccessful as PBBatchSuccessful
import com.zepben.protobuf.ns.data.ProcessingPaused as PBProcessingPaused
import com.zepben.protobuf.ns.data.BatchFailure as PBBatchFailure
import com.zepben.protobuf.ns.data.StateEventFailure as PBStateEventFailure
import com.zepben.protobuf.ns.data.StateEventUnknownMrid as PBStateEventUnknownMrid
import com.zepben.protobuf.ns.data.StateEventDuplicateMrid as PBStateEventDuplicateMrid
import com.zepben.protobuf.ns.data.StateEventInvalidMrid as PBStateEventInvalidMrid
import com.zepben.protobuf.ns.data.StateEventUnsupportedPhasing as PBStateEventUnsupportedPhasing

/**
 * The outcome of processing this batch of updates.
 */
sealed interface SetCurrentStatesStatus

/**
 * A response indicating all items in the batch were applied successfully.
 */
class BatchSuccessful : SetCurrentStatesStatus{
    internal fun toPb(): PBBatchSuccessful =
        PBBatchSuccessful.newBuilder().build()
}

/**
 * A response indicating that current state events are not currently being processed. There is no need to retry these,
 * the missed events will be requested when processing resumes.
 *
 * @property since The timestamp when the processing was paused.
 */
class ProcessingPaused(val since: LocalDateTime) : SetCurrentStatesStatus{
    internal fun toPb(): PBProcessingPaused =
        PBProcessingPaused.newBuilder().also { it.since = since.toTimestamp() }.build()
}

/**
 * A response indicating one or more items in the batch couldn't be applied.
 *
 * @property partialFailure Indicates if only come of the batch failed (true), or all entries in the batch failed (false).
 * @property failures The status of each item processed in the batch that failed.
 */
class BatchFailure(val partialFailure: Boolean, val failures: List<StateEventFailure>) : SetCurrentStatesStatus{
    internal fun toPb(): PBBatchFailure =
        PBBatchFailure.newBuilder().also {
            it.partialFailure = partialFailure
            it.addAllFailed(failures.map { it.toPb() })
        }.build()
}

/**
 * A wrapper class for allowing a one-of to be repeated.
 *
 * @property eventId The eventId of the state event that failed.
 */
sealed class StateEventFailure(val eventId: String){
    internal abstract fun toPb(): PBStateEventFailure

    /**
     * Creates a [PBStateEventFailure.Builder] builder with [eventId] assigned.
     */
    protected fun toPbBuilder(): PBStateEventFailure.Builder =
        PBStateEventFailure.newBuilder().also { it.eventId = eventId }
}

/**
 * The requested mRID was not found in the network.
 */
class StateEventUnknownMrid(eventId: String) : StateEventFailure(eventId){
    override fun toPb(): PBStateEventFailure =
        toPbBuilder().apply {
            unknownMrid = PBStateEventUnknownMrid.newBuilder().build()
        }.build()
}

/**
 * The requested mRID already existed in the network and can't be used.
 */
class StateEventDuplicateMrid(eventId: String) : StateEventFailure(eventId){
    override fun toPb(): PBStateEventFailure =
        toPbBuilder().apply {
            duplicateMrid = PBStateEventDuplicateMrid.newBuilder().build()
        }.build()
}

/**
 * The requested mRID was found in the network model, but was of an invalid type.
 */
class StateEventInvalidMrid(eventId: String) : StateEventFailure(eventId){
    override fun toPb(): PBStateEventFailure =
        toPbBuilder().apply {
            invalidMrid = PBStateEventInvalidMrid.newBuilder().build()
        }.build()
}

/**
 * The requested phasing was not available for the given operation. e.g. An open state request was made with
 * unsupported phases.
 */
class StateEventUnsupportedPhasing(eventId: String) : StateEventFailure(eventId){
    override fun toPb(): PBStateEventFailure =
        toPbBuilder().apply {
            unsupportedPhasing = PBStateEventUnsupportedPhasing.newBuilder().build()
        }.build()
}

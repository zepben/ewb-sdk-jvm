/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.streaming.data

import com.zepben.evolve.services.common.translator.toLocalDateTime
import com.zepben.evolve.services.common.translator.toTimestamp
import java.time.LocalDateTime
import com.zepben.protobuf.ns.data.BatchFailure as PBBatchFailure
import com.zepben.protobuf.ns.data.BatchSuccessful as PBBatchSuccessful
import com.zepben.protobuf.ns.data.ProcessingPaused as PBProcessingPaused
import com.zepben.protobuf.ns.data.StateEventDuplicateMrid as PBStateEventDuplicateMrid
import com.zepben.protobuf.ns.data.StateEventFailure as PBStateEventFailure
import com.zepben.protobuf.ns.data.StateEventInvalidMrid as PBStateEventInvalidMrid
import com.zepben.protobuf.ns.data.StateEventUnknownMrid as PBStateEventUnknownMrid
import com.zepben.protobuf.ns.data.StateEventUnsupportedPhasing as PBStateEventUnsupportedPhasing

/**
 * The outcome of processing this batch of updates.
 */
sealed interface SetCurrentStatesStatus

/**
 * A response indicating all items in the batch were applied successfully.
 */
class BatchSuccessful : SetCurrentStatesStatus {

    companion object {
        @Suppress("UNUSED_PARAMETER")
        internal fun fromPb(pb: PBBatchSuccessful): SetCurrentStatesStatus =
            BatchSuccessful()
    }

    internal fun toPb(): PBBatchSuccessful =
        PBBatchSuccessful.newBuilder().build()

}

/**
 * A response indicating that current state events are not currently being processed. There is no need to retry these,
 * the missed events will be requested when processing resumes.
 *
 * @property since The timestamp when the processing was paused.
 */
class ProcessingPaused(val since: LocalDateTime?) : SetCurrentStatesStatus {

    companion object {
        internal fun fromPb(pb: PBProcessingPaused): SetCurrentStatesStatus =
            ProcessingPaused(pb.since.toLocalDateTime())
    }

    internal fun toPb(): PBProcessingPaused =
        PBProcessingPaused.newBuilder().also { it.since = since.toTimestamp() }.build()

}

/**
 * A response indicating one or more items in the batch couldn't be applied.
 *
 * @property partialFailure Indicates if only come of the batch failed (true), or all entries in the batch failed (false).
 * @property failures The status of each item processed in the batch that failed.
 */
class BatchFailure(val partialFailure: Boolean, val failures: List<StateEventFailure>) : SetCurrentStatesStatus {

    companion object {
        internal fun fromPb(pb: PBBatchFailure): SetCurrentStatesStatus =
            BatchFailure(pb.partialFailure, pb.failedList.mapNotNull { StateEventFailure.fromPb(it) })
    }

    internal fun toPb(): PBBatchFailure =
        PBBatchFailure.newBuilder().also { batchFailure ->
            batchFailure.partialFailure = partialFailure
            batchFailure.addAllFailed(failures.map { it.toPb() })
        }.build()

}

/**
 * A wrapper class for allowing a one-of to be repeated.
 *
 * @property eventId The eventId of the state event that failed.
 */
sealed class StateEventFailure(val eventId: String) {

    companion object {
        internal fun fromPb(pb: PBStateEventFailure): StateEventFailure? =
            when (pb.reasonCase) {
                PBStateEventFailure.ReasonCase.UNKNOWNMRID -> StateEventUnknownMrid.fromPb(pb)
                PBStateEventFailure.ReasonCase.DUPLICATEMRID -> StateEventDuplicateMrid.fromPb(pb)
                PBStateEventFailure.ReasonCase.INVALIDMRID -> StateEventInvalidMrid.fromPb(pb)
                PBStateEventFailure.ReasonCase.UNSUPPORTEDPHASING -> StateEventUnsupportedPhasing.fromPb(pb)
                else -> null
            }
    }

    internal abstract fun toPb(): PBStateEventFailure

    /**
     * Creates a [PBStateEventFailure] with [eventId] assigned along with the specified [block].
     */
    protected fun toPb(block: PBStateEventFailure.Builder.() -> Unit): PBStateEventFailure =
        PBStateEventFailure.newBuilder().also { it.eventId = eventId }.apply(block).build()

}

/**
 * The requested mRID was not found in the network.
 */
class StateEventUnknownMrid(eventId: String) : StateEventFailure(eventId) {

    companion object {
        internal fun fromPb(pb: PBStateEventFailure): StateEventFailure =
            StateEventUnknownMrid(pb.eventId)
    }

    override fun toPb(): PBStateEventFailure = toPb {
        unknownMrid = PBStateEventUnknownMrid.newBuilder().build()
    }

}

/**
 * The requested mRID already existed in the network and can't be used.
 */
class StateEventDuplicateMrid(eventId: String) : StateEventFailure(eventId) {

    companion object {
        internal fun fromPb(pb: PBStateEventFailure): StateEventFailure =
            StateEventDuplicateMrid(pb.eventId)
    }

    override fun toPb(): PBStateEventFailure = toPb {
        duplicateMrid = PBStateEventDuplicateMrid.newBuilder().build()
    }

}

/**
 * The requested mRID was found in the network model, but was of an invalid type.
 */
class StateEventInvalidMrid(eventId: String) : StateEventFailure(eventId) {

    companion object {
        internal fun fromPb(pb: PBStateEventFailure): StateEventFailure =
            StateEventInvalidMrid(pb.eventId)
    }

    override fun toPb(): PBStateEventFailure = toPb {
        invalidMrid = PBStateEventInvalidMrid.newBuilder().build()
    }

}

/**
 * The requested phasing was not available for the given operation. e.g. An open state request was made with
 * unsupported phases.
 */
class StateEventUnsupportedPhasing(eventId: String) : StateEventFailure(eventId) {

    companion object {
        internal fun fromPb(pb: PBStateEventFailure): StateEventFailure =
            StateEventUnsupportedPhasing(pb.eventId)
    }

    override fun toPb(): PBStateEventFailure = toPb {
        unsupportedPhasing = PBStateEventUnsupportedPhasing.newBuilder().build()
    }

}

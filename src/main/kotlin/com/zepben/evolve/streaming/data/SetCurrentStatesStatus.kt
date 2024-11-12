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
import com.zepben.protobuf.ns.SetCurrentStatesResponse as PBSetCurrentStatesResponse

/**
 * The outcome of processing this batch of updates.
 *
 * @property batchId The unique identifier of the batch that was processed. This matches the batch ID from the original request to allow correlation between
 * request and response.
 */
sealed class SetCurrentStatesStatus(val batchId: Long) {

    companion object {
        internal fun fromPb(pb: PBSetCurrentStatesResponse): SetCurrentStatesStatus? =
            when (pb.statusCase) {
                PBSetCurrentStatesResponse.StatusCase.SUCCESS -> BatchSuccessful.fromPb(pb)
                PBSetCurrentStatesResponse.StatusCase.PAUSED -> ProcessingPaused.fromPb(pb)
                PBSetCurrentStatesResponse.StatusCase.FAILURE -> BatchFailure.fromPb(pb)
                else -> null
            }
    }

    internal abstract fun toPb(): PBSetCurrentStatesResponse

    /**
     * Creates a [PBSetCurrentStatesResponse] with messageId assigned along with the specified [block].
     */
    protected fun toPb(block: PBSetCurrentStatesResponse.Builder.() -> Unit): PBSetCurrentStatesResponse =
        PBSetCurrentStatesResponse.newBuilder().also { it.messageId = batchId }.apply(block).build()
}

/**
 * A response indicating all items in the batch were applied successfully.
 */
class BatchSuccessful(batchId: Long) : SetCurrentStatesStatus(batchId) {

    companion object {
        internal fun fromPb(pb: PBSetCurrentStatesResponse): SetCurrentStatesStatus =
            BatchSuccessful(pb.messageId)
    }

    override fun toPb(): PBSetCurrentStatesResponse =
        toPb { success = PBBatchSuccessful.newBuilder().build() }

}

/**
 * A response indicating that current state events are not currently being processed. There is no need to retry these,
 * the missed events will be requested when processing resumes.
 *
 * @property since The timestamp when the processing was paused.
 */
class ProcessingPaused(batchId: Long, val since: LocalDateTime?) : SetCurrentStatesStatus(batchId) {

    companion object {
        internal fun fromPb(pb: PBSetCurrentStatesResponse): SetCurrentStatesStatus =
            ProcessingPaused(pb.messageId, pb.paused.since.toLocalDateTime())
    }

    override fun toPb(): PBSetCurrentStatesResponse =
        toPb { paused = PBProcessingPaused.newBuilder().also { it.since = since.toTimestamp() }.build() }

}

/**
 * A response indicating one or more items in the batch couldn't be applied.
 *
 * @property partialFailure Indicates if only come of the batch failed (true), or all entries in the batch failed (false).
 * @property failures The status of each item processed in the batch that failed.
 */
class BatchFailure(batchId: Long, val partialFailure: Boolean, val failures: List<StateEventFailure>) : SetCurrentStatesStatus(batchId) {

    companion object {
        internal fun fromPb(pb: PBSetCurrentStatesResponse): SetCurrentStatesStatus =
            BatchFailure(pb.messageId, pb.failure.partialFailure, pb.failure.failedList.mapNotNull { StateEventFailure.fromPb(it) })
    }

    override fun toPb(): PBSetCurrentStatesResponse =
        toPb {
            failure = PBBatchFailure.newBuilder().also { batchFailure ->
                batchFailure.partialFailure = partialFailure
                batchFailure.addAllFailed(failures.map { it.toPb() })
            }.build()
        }

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

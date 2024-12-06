/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.streaming.data

import com.zepben.protobuf.ns.SetCurrentStatesResponse as PBSetCurrentStatesResponse
import com.zepben.protobuf.ns.data.BatchFailure as PBBatchFailure
import com.zepben.protobuf.ns.data.BatchNotProcessed as PBBatchNotProcessed
import com.zepben.protobuf.ns.data.BatchSuccessful as PBBatchSuccessful
import com.zepben.protobuf.ns.data.StateEventDuplicateMrid as PBStateEventDuplicateMrid
import com.zepben.protobuf.ns.data.StateEventFailure as PBStateEventFailure
import com.zepben.protobuf.ns.data.StateEventInvalidMrid as PBStateEventInvalidMrid
import com.zepben.protobuf.ns.data.StateEventUnknownMrid as PBStateEventUnknownMrid
import com.zepben.protobuf.ns.data.StateEventUnsupportedMrid as PBStateEventUnsupportedMrid
import com.zepben.protobuf.ns.data.StateEventUnsupportedPhasing as PBStateEventUnsupportedPhasing

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
                PBSetCurrentStatesResponse.StatusCase.FAILURE -> BatchFailure.fromPb(pb)
                PBSetCurrentStatesResponse.StatusCase.NOTPROCESSED -> BatchNotProcessed.fromPb(pb)
                else -> null
            }
    }

    internal abstract fun toPb(): PBSetCurrentStatesResponse

    /**
     * Creates a [PBSetCurrentStatesResponse] with messageId assigned along with the specified [block].
     */
    protected fun toPb(block: PBSetCurrentStatesResponse.Builder.() -> Unit): PBSetCurrentStatesResponse =
        PBSetCurrentStatesResponse.newBuilder().setMessageId(batchId).apply(block).build()

}

/**
 * A response indicating all items in the batch were applied successfully.
 */
class BatchSuccessful(batchId: Long) : SetCurrentStatesStatus(batchId) {

    companion object {
        internal fun fromPb(pb: PBSetCurrentStatesResponse): BatchSuccessful =
            BatchSuccessful(pb.messageId)
    }

    override fun toPb(): PBSetCurrentStatesResponse =
        toPb { success = PBBatchSuccessful.newBuilder().build() }

}

/**
 * A response indicating one or more items in the batch couldn't be applied.
 *
 * @property partialFailure Indicates if only come of the batch failed (true), or all entries in the batch failed (false).
 * @property failures The status of each item processed in the batch that failed.
 */
class BatchFailure(batchId: Long, val partialFailure: Boolean, val failures: List<StateEventFailure>) : SetCurrentStatesStatus(batchId) {

    companion object {
        internal fun fromPb(pb: PBSetCurrentStatesResponse): BatchFailure =
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
 * A response indicating all items in the batch were ignored because the message ID of the batch was prior to the
 * last processed batch. This is expected when starting the service if the same item is sent to the current state
 * processing queue and is also included in the backlog processing response.
 */
class BatchNotProcessed(batchId: Long) : SetCurrentStatesStatus(batchId) {

    companion object {
        internal fun fromPb(pb: PBSetCurrentStatesResponse): BatchNotProcessed =
            BatchNotProcessed(pb.messageId)
    }

    override fun toPb(): PBSetCurrentStatesResponse =
        toPb { notProcessed = PBBatchNotProcessed.newBuilder().build() }

}

/**
 * A wrapper class for allowing a one-of to be repeated.
 *
 * @property eventId The eventId of the state event that failed.
 * @property message A message describing why the event failed.
 */
sealed class StateEventFailure(val eventId: String, val message: String) {

    companion object {
        internal fun fromPb(pb: PBStateEventFailure): StateEventFailure? =
            when (pb.reasonCase) {
                PBStateEventFailure.ReasonCase.UNKNOWNMRID -> StateEventUnknownMrid.fromPb(pb)
                PBStateEventFailure.ReasonCase.DUPLICATEMRID -> StateEventDuplicateMrid.fromPb(pb)
                PBStateEventFailure.ReasonCase.INVALIDMRID -> StateEventInvalidMrid.fromPb(pb)
                PBStateEventFailure.ReasonCase.UNSUPPORTEDPHASING -> StateEventUnsupportedPhasing.fromPb(pb)
                PBStateEventFailure.ReasonCase.UNSUPPORTEDMRID -> StateEventUnsupportedMrid.fromPb(pb)
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
class StateEventUnknownMrid(eventId: String, message: String) : StateEventFailure(eventId, message) {

    companion object {
        internal fun fromPb(pb: PBStateEventFailure): StateEventFailure =
            StateEventUnknownMrid(pb.eventId, pb.message)
    }

    override fun toPb(): PBStateEventFailure = toPb {
        unknownMrid = PBStateEventUnknownMrid.newBuilder().build()
    }

}

/**
 * The requested mRID already existed in the network and can't be used.
 */
class StateEventDuplicateMrid(eventId: String, message: String) : StateEventFailure(eventId, message) {

    companion object {
        internal fun fromPb(pb: PBStateEventFailure): StateEventFailure =
            StateEventDuplicateMrid(pb.eventId, pb.message)
    }

    override fun toPb(): PBStateEventFailure = toPb {
        duplicateMrid = PBStateEventDuplicateMrid.newBuilder().build()
    }

}

/**
 * The requested mRID was found in the network model, but was of an invalid type.
 */
class StateEventInvalidMrid(eventId: String, message: String) : StateEventFailure(eventId, message) {

    companion object {
        internal fun fromPb(pb: PBStateEventFailure): StateEventFailure =
            StateEventInvalidMrid(pb.eventId, pb.message)
    }

    override fun toPb(): PBStateEventFailure = toPb {
        invalidMrid = PBStateEventInvalidMrid.newBuilder().build()
    }

}

/**
 * The requested phasing was not available for the given operation. e.g. An open state request was made with
 * unsupported phases.
 */
class StateEventUnsupportedPhasing(eventId: String, message: String) : StateEventFailure(eventId, message) {

    companion object {
        internal fun fromPb(pb: PBStateEventFailure): StateEventFailure =
            StateEventUnsupportedPhasing(pb.eventId, pb.message)
    }

    override fun toPb(): PBStateEventFailure = toPb {
        unsupportedPhasing = PBStateEventUnsupportedPhasing.newBuilder().build()
    }

}

/**
 * The mRID provided can't be used to perform the given action even though it is of the correct type. e.g. Trying to
 * open/close a switch in a voltage level that hasn't been implemented in the server.
 */
class StateEventUnsupportedMrid(eventId: String, message: String) : StateEventFailure(eventId, message) {

    companion object {
        internal fun fromPb(pb: PBStateEventFailure): StateEventFailure =
            StateEventUnsupportedMrid(pb.eventId, pb.message)
    }

    override fun toPb(): PBStateEventFailure = toPb {
        unsupportedMrid = PBStateEventUnsupportedMrid.newBuilder().build()
    }

}

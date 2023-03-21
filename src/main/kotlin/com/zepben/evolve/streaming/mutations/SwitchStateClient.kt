/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.streaming.mutations

import com.zepben.evolve.services.common.translator.toTimestamp
import com.zepben.evolve.streaming.grpc.GrpcChannel
import com.zepben.evolve.streaming.grpc.GrpcClient
import com.zepben.evolve.streaming.grpc.GrpcResult
import com.zepben.protobuf.nm.SetCurrentSwitchStatesRequest
import com.zepben.protobuf.nm.SwitchStateServiceGrpc
import io.grpc.Channel
import com.zepben.protobuf.nm.SwitchStateUpdate as PBSwitchStateUpdate

class SwitchStateClient(
    private val stub: SwitchStateServiceGrpc.SwitchStateServiceBlockingStub
) : GrpcClient(null) {

    constructor(channel: Channel) : this(SwitchStateServiceGrpc.newBlockingStub(channel))
    constructor(channel: GrpcChannel) : this(SwitchStateServiceGrpc.newBlockingStub(channel.channel))

    /**
     * Send a request to the server to update the current state of a switch.
     *
     * @param switchToUpdate The switch and its state to be updated.
     * @return a GrpcResult that indicates success or failure of the remote call.
     */
    fun setCurrentSwitchState(switchToUpdate: SwitchStateUpdate): GrpcResult<Unit> =
        setCurrentSwitchStates(listOf(switchToUpdate))

    /**
     * Send a request to the server to update the current state of a group of switches as a batch.
     *
     * All switches included in the request will be treated as a batch. That is, the resulting network state will
     * only be calculated once all the new switch states have been applied. This can then avoid
     * where the model may be in an 'inconsistent' state if you know a set of switch operations should for some reason
     * be applied all together.
     *
     * Note that this should not be used to send large amounts of switch updates for unrelated groups of switching
     * as excessively large state updates can starve consumers / readers getting access to the model.
     *
     * @param switchesToUpdate A set of switches and their states to be updated.
     * @return a GrpcResult that indicates success or failure of the remote call.
     */
    fun setCurrentSwitchStates(switchesToUpdate: List<SwitchStateUpdate>): GrpcResult<Unit> = tryRpc {
        stub.setCurrentSwitchStates(
            SetCurrentSwitchStatesRequest.newBuilder()
                .addAllSwitchesToUpdate(switchesToUpdate.map { it.toPb() })
                .build()
        )
    }
}

internal fun SwitchStateUpdate.toPb(): PBSwitchStateUpdate =
    PBSwitchStateUpdate.newBuilder()
        .setMRID(mRID)
        .setSetOpen(setOpen)
        .setTimestamp(timestamp.toTimestamp())
        .build()

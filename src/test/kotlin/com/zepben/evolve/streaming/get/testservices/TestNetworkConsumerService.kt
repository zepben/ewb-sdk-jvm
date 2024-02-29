/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.streaming.get.testservices

import com.zepben.protobuf.metadata.GetMetadataRequest
import com.zepben.protobuf.metadata.GetMetadataResponse
import com.zepben.protobuf.nc.*
import io.grpc.Status
import io.grpc.stub.StreamObserver

internal class TestNetworkConsumerService : NetworkConsumerGrpc.NetworkConsumerImplBase() {

    lateinit var onGetIdentifiedObjects: (request: GetIdentifiedObjectsRequest, response: StreamObserver<GetIdentifiedObjectsResponse>) -> Unit
    lateinit var onGetNetworkHierarchy: (request: GetNetworkHierarchyRequest, response: StreamObserver<GetNetworkHierarchyResponse>) -> Unit
    lateinit var onGetEquipmentForContainers: (request: GetEquipmentForContainersRequest, response: StreamObserver<GetEquipmentForContainersResponse>) -> Unit
    lateinit var onGetCurrentEquipmentForFeeder: (request: GetCurrentEquipmentForFeederRequest, response: StreamObserver<GetCurrentEquipmentForFeederResponse>) -> Unit
    lateinit var onGetEquipmentForRestriction: (request: GetEquipmentForRestrictionRequest, response: StreamObserver<GetEquipmentForRestrictionResponse>) -> Unit
    lateinit var onGetTerminalsForNode: (request: GetTerminalsForNodeRequest, response: StreamObserver<GetTerminalsForNodeResponse>) -> Unit
    lateinit var onGetMetadataRequest: (request: GetMetadataRequest, response: StreamObserver<GetMetadataResponse>) -> Unit

    override fun getIdentifiedObjects(response: StreamObserver<GetIdentifiedObjectsResponse>): StreamObserver<GetIdentifiedObjectsRequest> =
        TestStreamObserver(response, onGetIdentifiedObjects)

    override fun getNetworkHierarchy(request: GetNetworkHierarchyRequest, response: StreamObserver<GetNetworkHierarchyResponse>) {
        runGrpc(request, response, onGetNetworkHierarchy)
    }

    override fun getMetadata(request: GetMetadataRequest, response: StreamObserver<GetMetadataResponse>) {
        runGrpc(request, response, onGetMetadataRequest)
    }

    override fun getEquipmentForContainers(response: StreamObserver<GetEquipmentForContainersResponse>): StreamObserver<GetEquipmentForContainersRequest> =
        TestStreamObserver(response, onGetEquipmentForContainers)

    override fun getCurrentEquipmentForFeeder(request: GetCurrentEquipmentForFeederRequest, response: StreamObserver<GetCurrentEquipmentForFeederResponse>) {
        runGrpc(request, response, onGetCurrentEquipmentForFeeder)
    }

    override fun getEquipmentForRestriction(request: GetEquipmentForRestrictionRequest, response: StreamObserver<GetEquipmentForRestrictionResponse>) {
        runGrpc(request, response, onGetEquipmentForRestriction)
    }

    override fun getTerminalsForNode(request: GetTerminalsForNodeRequest, response: StreamObserver<GetTerminalsForNodeResponse>) {
        runGrpc(request, response, onGetTerminalsForNode)
    }

    private fun <T, U : StreamObserver<*>> runGrpc(request: T, response: U, handler: (T, U) -> Unit) {
        try {
            handler(request, response)
            response.onCompleted()
        } catch (t: Throwable) {
            response.onError(Status.ABORTED.withDescription(t.message).asRuntimeException())
        }
    }

}

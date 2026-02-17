/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.streaming.get

import com.zepben.ewb.cim.iec61970.base.core.*
import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.ChangeSet
import com.zepben.ewb.services.network.NetworkService
import com.zepben.ewb.services.variant.ChangeSetServices
import com.zepben.ewb.streaming.grpc.GrpcChannel
import com.zepben.ewb.streaming.grpc.GrpcResult
import com.zepben.protobuf.nc.*
import io.grpc.CallCredentials
import io.grpc.Channel

/**
 * Helper client for querying multiple gRPC services for a [ChangeSet] and its contents.
 *
 * @property services The [ChangeSetServices] to store fetched objects in.
 */
class ChangeSetConsumerClient(
    val services: ChangeSetServices,
    val variantConsumerClient: VariantConsumerClient,
    val networkConsumerClient: NetworkConsumerClient?,
    val diagramConsumerClient: DiagramConsumerClient?,
    val customerConsumerClient: CustomerConsumerClient?,
) {


    /**
     * Create a [ChangeSetConsumerClient]
     *
     * @param channel [Channel] to build stubs from.
     * @param callCredentials [CallCredentials] to be attached to the stubs.
     */
    @JvmOverloads
    constructor(channel: Channel, callCredentials: CallCredentials? = null) :
        this(
            ChangeSetServices(),
            VariantConsumerClient(channel, callCredentials),
            NetworkConsumerClient(channel, callCredentials),
            DiagramConsumerClient(channel, callCredentials),
            CustomerConsumerClient(channel, callCredentials),
        )

    constructor(
        variantChannel: Channel,
        networkChannel: Channel? = null,
        diagramChannel: Channel? = null,
        customerChannel: Channel? = null,
        callCredentials: CallCredentials? = null
    ) :
        this(
            ChangeSetServices(),
            VariantConsumerClient(variantChannel, callCredentials),
            networkChannel?.let { NetworkConsumerClient(it, callCredentials) },
            diagramChannel?.let { DiagramConsumerClient(it, callCredentials) },
            customerChannel?.let { CustomerConsumerClient(it, callCredentials) },
        )


    /**
     * Create a [ChangeSetConsumerClient]
     *
     * @param channel [GrpcChannel] to build stubs from. This channel will be used for all clients, so will only work if your EWB is hosting all clients.
     *   If your services are separated, use the constructor with multiple [GrpcChannel]s.
     * @param callCredentials [CallCredentials] to be attached to the stub.
     */
    @JvmOverloads
    constructor(channel: GrpcChannel, callCredentials: CallCredentials? = null) : this(channel.channel, callCredentials)

    constructor(
        variantChannel: GrpcChannel,
        networkChannel: GrpcChannel? = null,
        diagramChannel: GrpcChannel? = null,
        customerChannel: GrpcChannel? = null,
        callCredentials: CallCredentials? = null
    ) :
        this(
            ChangeSetServices(),
            VariantConsumerClient(variantChannel.channel, callCredentials),
            networkChannel?.channel?.let { NetworkConsumerClient(it, callCredentials) },
            diagramChannel?.channel?.let { DiagramConsumerClient(it, callCredentials) },
            customerChannel?.channel?.let { CustomerConsumerClient(it, callCredentials) },
        )

    /**
     * Retrieve a [ChangeSet] and all its contents. The returned [ChangeSetServices] will contain a populated [VariantService], and a [NetworkService], [DiagramService], and [CustomerService] if
     * they are available for the requested change set.
     *
     * Exceptions that occur during retrieval will be caught and passed to all error handlers that have been registered against this client.
     *
     * @param mRID The mRID of the [ChangeSet].
     * @return A [GrpcResult] containing a [ChangeSetServices].
     */
    fun getChangeSet(mRID: String): GrpcResult<ChangeSetServices> {
        variantConsumerClient.getChangeSet(mRID).throwOnError()
        val nMor = networkConsumerClient?.getChangeSetObjects(mRID)?.throwOnError()
        val dMor = diagramConsumerClient?.getChangeSetObjects(mRID)?.throwOnError()
        val cMor = customerConsumerClient?.getChangeSetObjects(mRID)?.throwOnError()

        return GrpcResult.of(
            ChangeSetServices(
                variantConsumerClient.service.get<ChangeSet>(mRID),
                variantService = variantConsumerClient.service,
                networkService = nMor?.value,
                diagramService = dMor?.value,
                customerService = cMor?.value,
            )
        )
    }


}

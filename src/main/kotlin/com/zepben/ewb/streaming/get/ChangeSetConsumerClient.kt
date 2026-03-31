/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.streaming.get

import com.zepben.ewb.cim.iec61970.infiec61970.part303.genericdataset.ChangeSet
import com.zepben.ewb.database.paths.VariantContents
import com.zepben.ewb.services.customer.CustomerService
import com.zepben.ewb.services.diagram.DiagramService
import com.zepben.ewb.services.network.NetworkService
import com.zepben.ewb.services.variant.ChangeSetServices
import com.zepben.ewb.services.variant.VariantService
import com.zepben.ewb.streaming.grpc.GrpcChannel
import com.zepben.ewb.streaming.grpc.GrpcResult
import io.grpc.CallCredentials
import io.grpc.Channel
import java.time.LocalDate

/**
 * Helper client for querying multiple gRPC services for a [ChangeSet] and its contents.
 *
 * @property services The [ChangeSetServices] to store fetched objects in.
 */
class ChangeSetConsumerClient(
    val variantConsumerClient: VariantConsumerClient,
    val networkChannel: Channel?,
    val diagramChannel: Channel?,
    val customerChannel: Channel?,
    val networkCallCredentials: CallCredentials?,
    val diagramCallCredentials: CallCredentials?,
    val customerCallCredentials: CallCredentials?,
) {

    init {
        require(networkChannel != null || diagramChannel != null || customerChannel != null) {
            "At least one of networkChannel, diagramChannel, or customerChannel must be provided."
        }
    }

    /**
     * Create a [ChangeSetConsumerClient]
     *
     * @param channel [Channel] to build stubs from.
     * @param callCredentials [CallCredentials] to be attached to the stubs.
     */
    @JvmOverloads
    constructor(channel: Channel, variantService: VariantService, callCredentials: CallCredentials? = null) :
        this(
            VariantConsumerClient(channel, variantService, callCredentials),
            channel,
            channel,
            channel,
            callCredentials,
            callCredentials,
            callCredentials
        )

    constructor(
        variantChannel: Channel,
        variantService: VariantService,
        networkChannel: Channel? = null,
        diagramChannel: Channel? = null,
        customerChannel: Channel? = null,
        variantCallCredentials: CallCredentials? = null,
        networkCallCredentials: CallCredentials? = null,
        diagramCallCredentials: CallCredentials? = null,
        customerCallCredentials: CallCredentials? = null,
    ) :
        this(
            VariantConsumerClient(variantChannel, variantService, variantCallCredentials),
            networkChannel,
            diagramChannel,
            customerChannel,
            networkCallCredentials,
            diagramCallCredentials,
            customerCallCredentials
        )


    /**
     * Create a [ChangeSetConsumerClient]
     *
     * @param channel [GrpcChannel] to build stubs from. This channel will be used for all clients, so will only work if your EWB is hosting all clients.
     *   If your services are separated, use the constructor with multiple [GrpcChannel]s.
     * @param callCredentials [CallCredentials] to be attached to the stub.
     */
    @JvmOverloads
    constructor(channel: GrpcChannel, variantService: VariantService, callCredentials: CallCredentials? = null) : this(
        channel.channel,
        variantService,
        callCredentials
    )

    constructor(
        variantChannel: GrpcChannel,
        variantService: VariantService,
        networkChannel: GrpcChannel,
        diagramChannel: GrpcChannel,
        customerChannel: GrpcChannel,
        variantCallCredentials: CallCredentials? = null,
        networkCallCredentials: CallCredentials? = null,
        diagramCallCredentials: CallCredentials? = null,
        customerCallCredentials: CallCredentials? = null,
    ) :
        this(
            VariantConsumerClient(variantChannel.channel, variantService, variantCallCredentials),
            networkChannel.channel,
            diagramChannel.channel,
            customerChannel.channel,
            networkCallCredentials,
            diagramCallCredentials,
            customerCallCredentials
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
    fun getChangeSet(mRID: String, baseModelVersion: LocalDate? = null): GrpcResult<ChangeSetServices> {
        variantConsumerClient.getChangeSet(mRID, baseModelVersion).throwOnError()
        return variantConsumerClient.service.get<ChangeSet>(mRID)?.let { changeSet ->
            val nMor = networkChannel?.let {
                NetworkConsumerClient(it, networkCallCredentials).getChangeSetObjects(mRID, VariantContents.CREATIONS_MODIFICATIONS, baseModelVersion)
                    .throwOnError()
            }
            val nMorOriginal = networkChannel?.let {
                NetworkConsumerClient(it, networkCallCredentials).getChangeSetObjects(mRID, VariantContents.DELETIONS_REVERSEMODIFICATIONS, baseModelVersion)
                    .throwOnError()
            }
            val dMor = diagramChannel?.let {
                DiagramConsumerClient(it, diagramCallCredentials).getChangeSetObjects(mRID, VariantContents.CREATIONS_MODIFICATIONS, baseModelVersion)
                    .throwOnError()
            }
            val dMorOriginal = diagramChannel?.let {
                DiagramConsumerClient(it, diagramCallCredentials).getChangeSetObjects(mRID, VariantContents.DELETIONS_REVERSEMODIFICATIONS, baseModelVersion)
                    .throwOnError()
            }
            val cMor = customerChannel?.let {
                CustomerConsumerClient(it, customerCallCredentials).getChangeSetObjects(mRID, VariantContents.CREATIONS_MODIFICATIONS, baseModelVersion)
                    .throwOnError()
            }
            val cMorOriginal = customerChannel?.let {
                CustomerConsumerClient(it, customerCallCredentials).getChangeSetObjects(mRID, VariantContents.DELETIONS_REVERSEMODIFICATIONS, baseModelVersion)
                    .throwOnError()
            }

            GrpcResult.of(
                ChangeSetServices(
                    changeSet,
                    newNetworkService = nMor?.value ?: NetworkService(),
                    originalNetworkService = nMorOriginal?.value ?: NetworkService(),
                    newDiagramService = dMor?.value ?: DiagramService(),
                    originalDiagramService = dMorOriginal?.value ?: DiagramService(),
                    newCustomerService = cMor?.value ?: CustomerService(),
                    originalCustomerService = cMorOriginal?.value ?: CustomerService(),
                )
            )
        }
            ?: throw IllegalStateException("ChangeSet $mRID was missing from the service after retrieval - this shouldn't have occurred, contact Zepben for support.")

    }


}

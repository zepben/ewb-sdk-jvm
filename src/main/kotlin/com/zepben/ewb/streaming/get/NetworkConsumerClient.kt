/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.streaming.get

import com.zepben.ewb.cim.extensions.iec61970.base.feeder.Loop
import com.zepben.ewb.cim.iec61968.operations.OperationalRestriction
import com.zepben.ewb.cim.iec61970.base.core.*
import com.zepben.ewb.services.common.BaseService
import com.zepben.ewb.services.common.translator.EnumMapper
import com.zepben.ewb.services.common.translator.mRID
import com.zepben.ewb.services.network.NetworkService
import com.zepben.ewb.services.network.NetworkState
import com.zepben.ewb.services.network.translator.NetworkProtoToCim
import com.zepben.ewb.services.network.translator.addFromPb
import com.zepben.ewb.services.network.translator.mRID
import com.zepben.ewb.streaming.get.hierarchy.NetworkHierarchy
import com.zepben.ewb.streaming.grpc.GrpcChannel
import com.zepben.ewb.streaming.grpc.GrpcResult
import com.zepben.protobuf.metadata.GetMetadataRequest
import com.zepben.protobuf.metadata.GetMetadataResponse
import com.zepben.protobuf.nc.*
import io.grpc.CallCredentials
import io.grpc.Channel
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import com.zepben.protobuf.nc.IncludedEnergizedContainers as PBIncludedEnergizedContainers
import com.zepben.protobuf.nc.IncludedEnergizingContainers as PBIncludedEnergizingContainers
import com.zepben.protobuf.nc.NetworkState as PBNetworkState

/**
 * Consumer client for a [NetworkService].
 *
 * WARNING: The [MultiObjectResult] operations below are not atomic upon a [NetworkService], and thus if processing fails partway through, any previously
 * successful additions will have been processed by the service, and thus you may have an incomplete service. Also note that adding to the service may not
 * occur for an object if another object with the same mRID is already present in service. [MultiObjectResult.failed] can be used to check for mRIDs that
 * were retrieved but not added to service. This should not be the case unless you are processing things concurrently.
 *
 * @property stub The gRPC stub to be used to communicate with the server
 * @property service The [NetworkService] to store fetched objects in.
 * @param executor An optional [ExecutorService] to use with the stub. If provided, it will be cleaned up when this client is closed.
 */
class NetworkConsumerClient(
    private val stub: NetworkConsumerGrpc.NetworkConsumerStub,
    override val service: NetworkService = NetworkService(),
    override val protoToCim: NetworkProtoToCim = NetworkProtoToCim(service),
    executor: ExecutorService? = null
) : CimConsumerClient<NetworkService, NetworkProtoToCim>(executor) {

    private var networkHierarchy: NetworkHierarchy? = null

    private val mapIncludeEnergizingContainers = EnumMapper(IncludedEnergizingContainers.entries, PBIncludedEnergizingContainers.entries)
    private val mapIncludeEnergizedContainers = EnumMapper(IncludedEnergizedContainers.entries, PBIncludedEnergizedContainers.entries)
    private val mapNetworkState = EnumMapper(NetworkState.entries, PBNetworkState.entries)

    /**
     * Create a [NetworkConsumerClient]
     *
     * @param channel [Channel] to build a stub from.
     * @param callCredentials [CallCredentials] to be attached to the stub.
     */
    @JvmOverloads
    constructor(channel: Channel, callCredentials: CallCredentials? = null) :
        this(
            NetworkConsumerGrpc.newStub(channel).apply { callCredentials?.let { withCallCredentials(it) } },
            executor = Executors.newSingleThreadExecutor()
        )

    /**
     * Create a [NetworkConsumerClient]
     *
     * @param channel [GrpcChannel] to build a stub from.
     * @param callCredentials [CallCredentials] to be attached to the stub.
     */
    @JvmOverloads
    constructor(channel: GrpcChannel, callCredentials: CallCredentials? = null) : this(channel.channel, callCredentials)

    override fun runGetMetadata(getMetadataRequest: GetMetadataRequest, streamObserver: AwaitableStreamObserver<GetMetadataResponse>) {
        stub.getMetadata(getMetadataRequest, streamObserver)
    }

    /**
     * Retrieve the [Equipment] for [equipmentContainer]
     *
     * Exceptions that occur during retrieval will be caught and passed to all error handlers that have been registered against this client.
     *
     * @param equipmentContainer The [EquipmentContainer] to fetch equipment for.
     * @param includeEnergizingContainers The level of energizing containers to include equipment from.
     * @param includeEnergizedContainers The level of energized containers to include equipment from.
     * @param networkState The network state of the equipment.
     *
     * @return A [GrpcResult] with a result of one of the following:
     * - When [GrpcResult.wasSuccessful], a map containing the retrieved objects keyed by mRID, accessible via [GrpcResult.value]. If an item was not found, or
     * couldn't be added to [service], it will be excluded from the map and its mRID will be present in [MultiObjectResult.failed] (see [BaseService.add]).
     * - When [GrpcResult.wasFailure], the error that occurred retrieving or processing the object, accessible via [GrpcResult.thrown].
     * Note the [NetworkConsumerClient] warning in this case.
     */
    @JvmOverloads
    fun getEquipmentForContainer(
        equipmentContainer: EquipmentContainer,
        includeEnergizingContainers: IncludedEnergizingContainers = IncludedEnergizingContainers.NONE,
        includeEnergizedContainers: IncludedEnergizedContainers = IncludedEnergizedContainers.NONE,
        networkState: NetworkState = NetworkState.NORMAL
    ): GrpcResult<MultiObjectResult> =
        getEquipmentForContainer(equipmentContainer.mRID, includeEnergizingContainers, includeEnergizedContainers, networkState)

    /**
     * Retrieve the [Equipment] for the [EquipmentContainer] represented by [mRID]
     *
     * Exceptions that occur during retrieval will be caught and passed to all error handlers that have been registered against this client.
     *
     * @param mRID The mRID of the [EquipmentContainer] to fetch equipment for.
     * @param includeEnergizingContainers The level of energizing containers to include equipment from.
     * @param includeEnergizedContainers The level of energized containers to include equipment from.
     * @param networkState The network state of the equipment.
     *
     * @return A [GrpcResult] with a result of one of the following:
     * - When [GrpcResult.wasSuccessful], a map containing the retrieved objects keyed by mRID, accessible via [GrpcResult.value]. If an item was not found, or
     * couldn't be added to [service], it will be excluded from the map and its mRID will be present in [MultiObjectResult.failed] (see [BaseService.add]).
     * - When [GrpcResult.wasFailure], the error that occurred retrieving or processing the object, accessible via [GrpcResult.thrown].
     * Note the [NetworkConsumerClient] warning in this case.
     */
    @JvmOverloads
    fun getEquipmentForContainer(
        mRID: String,
        includeEnergizingContainers: IncludedEnergizingContainers = IncludedEnergizingContainers.NONE,
        includeEnergizedContainers: IncludedEnergizedContainers = IncludedEnergizedContainers.NONE,
        networkState: NetworkState = NetworkState.NORMAL
    ): GrpcResult<MultiObjectResult> =
        getEquipmentForContainers(sequenceOf(mRID), includeEnergizingContainers, includeEnergizedContainers, networkState)

    /**
     * Retrieve the [Equipment] for all [EquipmentContainer]s represented by [mRIDs]
     *
     * Exceptions that occur during retrieval will be caught and passed to all error handlers that have been registered against this client.
     *
     * @param mRIDs The mRIDs of the [EquipmentContainer]s to fetch equipment for.
     * @param includeEnergizingContainers The level of energizing containers to include equipment from.
     * @param includeEnergizedContainers The level of energized containers to include equipment from.
     * @param networkState The network state of the equipment.
     *
     * @return A [GrpcResult] with a result of one of the following:
     * - When [GrpcResult.wasSuccessful], a map containing the retrieved objects keyed by mRID, accessible via [GrpcResult.value]. If an item was not found, or
     * couldn't be added to [service], it will be excluded from the map and its mRID will be present in [MultiObjectResult.failed] (see [BaseService.add]).
     * - When [GrpcResult.wasFailure], the error that occurred retrieving or processing the object, accessible via [GrpcResult.thrown].
     * Note the [NetworkConsumerClient] warning in this case.
     */
    @JvmOverloads
    fun getEquipmentForContainers(
        mRIDs: Iterable<String>,
        includeEnergizingContainers: IncludedEnergizingContainers = IncludedEnergizingContainers.NONE,
        includeEnergizedContainers: IncludedEnergizedContainers = IncludedEnergizedContainers.NONE,
        networkState: NetworkState = NetworkState.NORMAL
    ): GrpcResult<MultiObjectResult> =
        getEquipmentForContainers(mRIDs.asSequence(), includeEnergizingContainers, includeEnergizedContainers, networkState)

    /**
     * Retrieve the [Equipment] for all [EquipmentContainer]s represented by [mRIDs]
     *
     * Exceptions that occur during retrieval will be caught and passed to all error handlers that have been registered against this client.
     *
     * @param mRIDs The mRIDs of the [EquipmentContainer]s to fetch equipment for.
     * @param includeEnergizingContainers The level of energizing containers to include equipment from.
     * @param includeEnergizedContainers The level of energized containers to include equipment from.
     * @param networkState The network state of the equipment.
     *
     * @return A [GrpcResult] with a result of one of the following:
     * - When [GrpcResult.wasSuccessful], a map containing the retrieved objects keyed by mRID, accessible via [GrpcResult.value]. If an item was not found, or
     * couldn't be added to [service], it will be excluded from the map and its mRID will be present in [MultiObjectResult.failed] (see [BaseService.add]).
     * - When [GrpcResult.wasFailure], the error that occurred retrieving or processing the object, accessible via [GrpcResult.thrown].
     * Note the [NetworkConsumerClient] warning in this case.
     */
    @JvmOverloads
    fun getEquipmentForContainers(
        mRIDs: Sequence<String>,
        includeEnergizingContainers: IncludedEnergizingContainers = IncludedEnergizingContainers.NONE,
        includeEnergizedContainers: IncludedEnergizedContainers = IncludedEnergizedContainers.NONE,
        networkState: NetworkState = NetworkState.NORMAL
    ): GrpcResult<MultiObjectResult> =
        handleMultiObjectRPC { processEquipmentForContainers(mRIDs, includeEnergizingContainers, includeEnergizedContainers, networkState) }

    /**
     * Retrieve the [Equipment] for [operationalRestriction].
     *
     * Exceptions that occur during retrieval will be caught and passed to all error handlers that have been registered against this client.
     *
     * @param operationalRestriction The [OperationalRestriction] to fetch equipment for.
     * @return A [GrpcResult] with a result of one of the following:
     * - When [GrpcResult.wasSuccessful], a map containing the retrieved objects keyed by mRID, accessible via [GrpcResult.value]. If an item was not found, or
     * couldn't be added to [service], it will be excluded from the map and its mRID will be present in [MultiObjectResult.failed] (see [BaseService.add]).
     * - When [GrpcResult.wasFailure], the error that occurred retrieving or processing the object, accessible via [GrpcResult.thrown].
     * Note the [NetworkConsumerClient] warning in this case.
     */
    fun getEquipmentForRestriction(operationalRestriction: OperationalRestriction): GrpcResult<MultiObjectResult> =
        getEquipmentForRestriction(operationalRestriction.mRID)

    /**
     * Retrieve the [Equipment] for the [OperationalRestriction] represented by [mRID].
     *
     * Exceptions that occur during retrieval will be caught and passed to all error handlers that have been registered against this client.
     *
     * @param mRID The mRID of the [OperationalRestriction] to fetch equipment for.
     * @return A [GrpcResult] with a result of one of the following:
     * - When [GrpcResult.wasSuccessful], a map containing the retrieved objects keyed by mRID, accessible via [GrpcResult.value]. If an item was not found, or
     * couldn't be added to [service], it will be excluded from the map and its mRID will be present in [MultiObjectResult.failed] (see [BaseService.add]).
     * - When [GrpcResult.wasFailure], the error that occurred retrieving or processing the object, accessible via [GrpcResult.thrown].
     * Note the [NetworkConsumerClient] warning in this case.
     */
    fun getEquipmentForRestriction(mRID: String): GrpcResult<MultiObjectResult> =
        handleMultiObjectRPC { processRestriction(mRID) }

    /**
     * Retrieve the [Terminal]s for [connectivityNode].
     *
     * Exceptions that occur during retrieval will be caught and passed to all error handlers that have been registered against this client.
     *
     * @param connectivityNode The [ConnectivityNode] to fetch terminals for.
     * @return A [GrpcResult] with a result of one of the following:
     * - When [GrpcResult.wasSuccessful], a map containing the retrieved objects keyed by mRID, accessible via [GrpcResult.value]. If an item was not found, or
     * couldn't be added to [service], it will be excluded from the map and its mRID will be present in [MultiObjectResult.failed] (see [BaseService.add]).
     * - When [GrpcResult.wasFailure], the error that occurred retrieving or processing the object, accessible via [GrpcResult.thrown].
     * Note the [NetworkConsumerClient] warning in this case.
     */
    fun getTerminalsForConnectivityNode(connectivityNode: ConnectivityNode): GrpcResult<MultiObjectResult> =
        getTerminalsForConnectivityNode(connectivityNode.mRID)

    /**
     * Retrieve the [Terminal]s for the [ConnectivityNode] represented by [mRID].
     *
     * Exceptions that occur during retrieval will be caught and passed to all error handlers that have been registered against this client.
     *
     * @param mRID The mRID of the [ConnectivityNode] to fetch terminals for.
     * @return A [GrpcResult] with a result of one of the following:
     * - When [GrpcResult.wasSuccessful], a map containing the retrieved objects keyed by mRID, accessible via [GrpcResult.value]. If an item was not found, or
     * couldn't be added to [service], it will be excluded from the map and its mRID will be present in [MultiObjectResult.failed] (see [BaseService.add]).
     * - When [GrpcResult.wasFailure], the error that occurred retrieving or processing the object, accessible via [GrpcResult.thrown].
     * Note the [NetworkConsumerClient] warning in this case.
     */
    fun getTerminalsForConnectivityNode(mRID: String): GrpcResult<MultiObjectResult> =
        handleMultiObjectRPC { processConnectivityNode(mRID) }

    /**
     * Retrieve the network hierarchy.
     *
     * @return A simplified version of the network hierarchy that can be used to make further in-depth requests.
     */
    fun getNetworkHierarchy(
        includeGeographicalRegions: Boolean = true,
        includeSubgeographicalRegions: Boolean = true,
        includeSubstations: Boolean = true,
        includeFeeders: Boolean = true,
        includeCircuits: Boolean = true,
        includeLoops: Boolean = true,
        includeLvSubstations: Boolean = false,
        includeLvFeeders: Boolean = false,
    ): GrpcResult<NetworkHierarchy> =
        tryRpc {
            if (networkHierarchy == null) {
                val streamObserver = AwaitableStreamObserver<GetNetworkHierarchyResponse> { response ->
                    networkHierarchy = NetworkHierarchy(
                        if (includeGeographicalRegions) toMap(response.geographicalRegionsList) { getOrAdd(it.mRID()) { addFromPb(it) } } else emptyMap(),
                        if (includeSubgeographicalRegions) toMap(response.subGeographicalRegionsList) { getOrAdd(it.mRID()) { addFromPb(it) } } else emptyMap(),
                        if (includeSubstations) toMap(response.substationsList) { getOrAdd(it.mRID()) { addFromPb(it) } } else emptyMap(),
                        if (includeFeeders) toMap(response.feedersList) { getOrAdd(it.mRID()) { addFromPb(it) } } else emptyMap(),
                        if (includeCircuits) toMap(response.circuitsList) { getOrAdd(it.mRID()) { addFromPb(it) } } else emptyMap(),
                        if (includeLoops) toMap(response.loopsList) { getOrAdd(it.mRID()) { addFromPb(it) } } else emptyMap(),
                        if (includeLvSubstations) toMap(response.lvSubstationsList) { getOrAdd(it.mRID()) { addFromPb(it) } } else emptyMap(),
                        if (includeLvFeeders) toMap(response.lvFeedersList) { getOrAdd(it.mRID()) { addFromPb(it) } } else emptyMap(),
                    )
                }

                val request = GetNetworkHierarchyRequest.newBuilder().also {
                    it.includeGeographicalRegions = includeGeographicalRegions
                    it.includeSubgeographicalRegions = includeSubgeographicalRegions
                    it.includeSubstations = includeSubstations
                    it.includeFeeders = includeFeeders
                    it.includeCircuits = includeCircuits
                    it.includeLoops = includeLoops
                    it.includeLvSubstations = includeLvSubstations
                    it.includeLvFeeders = includeLvFeeders
                }
                stub.getNetworkHierarchy(request.build(), streamObserver)

                streamObserver.await()
            }
            networkHierarchy ?: throw IOException("No network hierarchy was received before GRPC channel was closed.")
        }

    /***
     * Retrieve the equipment container network for the specified [mRID] and store the results in the [service].
     *
     * This is a convenience method that will fetch the container object and all the equipment contained, along with all subsequent
     * references. This should entail a complete connectivity model for the container, however not the connectivity between multiple containers.
     *
     * @param mRID The mRID of the [EquipmentContainer] to fetch.
     * @param expectedClass The expected type of the fetched container.
     * @param includeEnergizingContainers The level of energizing containers to include equipment from.
     * @param includeEnergizedContainers The level of energized containers to include equipment from.
     * @param networkState The network state of the equipment.
     *
     * @return A [GrpcResult] of a [MultiObjectResult]. If successful, containing a map keyed by mRID of all the objects retrieved. If an item couldn't be added to
     * [service], its mRID will be present in [MultiObjectResult.failed].
     *
     * In addition to normal gRPC errors, you may also receive an unsuccessful [GrpcResult] with the following errors:
     * - [NoSuchElementException] if the requested object was not found.
     * - [ClassCastException] if the requested object was of the wrong type.
     */
    @JvmOverloads
    fun getEquipmentContainer(
        mRID: String,
        expectedClass: Class<out EquipmentContainer> = EquipmentContainer::class.java,
        includeEnergizingContainers: IncludedEnergizingContainers = IncludedEnergizingContainers.NONE,
        includeEnergizedContainers: IncludedEnergizedContainers = IncludedEnergizedContainers.NONE,
        networkState: NetworkState = NetworkState.NORMAL
    ): GrpcResult<MultiObjectResult> =
        tryRpc {
            val result = getEquipmentContainers(sequenceOf(mRID), expectedClass, includeEnergizingContainers, includeEnergizedContainers, networkState)
            if (result.wasFailure)
                throw result.thrown

            if (result.value.objects.isEmpty())
                throw NoSuchElementException("No object with mRID $mRID could be found.")

            result.value
        }

    /***
     * Retrieve the equipment container networks for the specified [mRID]s and store the results in the [service].
     *
     * This is a convenience method that will fetch the container objects and all the equipment contained, along with all subsequent
     * references. This should entail a complete connectivity model for the containers, however not the connectivity between multiple containers.
     *
     * @param mRIDs The mRIDs of the [EquipmentContainer]s to fetch.
     * @param expectedClass The expected type of the fetched containers.
     * @param includeEnergizingContainers The level of energizing containers to include equipment from.
     * @param includeEnergizedContainers The level of energized containers to include equipment from.
     * @param networkState The network state of the equipment.
     *
     * @return A [GrpcResult] of a [MultiObjectResult]. If successful, containing a map keyed by mRID of all the objects retrieved. If an item was not found, or
     * couldn't be added to [service], it will be excluded from the map and its mRID will be present in [MultiObjectResult.failed]
     *
     * In addition to normal gRPC errors, you may also receive an unsuccessful [GrpcResult] with the following errors:
     * - [ClassCastException] if the requested object was of the wrong type.
     */
    @JvmOverloads
    fun getEquipmentContainers(
        mRIDs: Iterable<String>,
        expectedClass: Class<out EquipmentContainer> = EquipmentContainer::class.java,
        includeEnergizingContainers: IncludedEnergizingContainers = IncludedEnergizingContainers.NONE,
        includeEnergizedContainers: IncludedEnergizedContainers = IncludedEnergizedContainers.NONE,
        networkState: NetworkState = NetworkState.NORMAL
    ): GrpcResult<MultiObjectResult> =
        getEquipmentContainers(mRIDs.asSequence(), expectedClass, includeEnergizingContainers, includeEnergizedContainers, networkState)

    /***
     * Retrieve the equipment container networks for the specified [mRID]s and store the results in the [service].
     *
     * This is a convenience method that will fetch the container objects and all the equipment contained, along with all subsequent
     * references. This should entail a complete connectivity model for the containers, however not the connectivity between multiple containers.
     *
     * @param mRIDs The mRIDs of the [EquipmentContainer]s to fetch.
     * @param expectedClass The expected type of the fetched containers.
     * @param includeEnergizingContainers The level of energizing containers to include equipment from.
     * @param includeEnergizedContainers The level of energized containers to include equipment from.
     * @param networkState The network state of the equipment.
     *
     * @return A [GrpcResult] of a [MultiObjectResult]. If successful, containing a map keyed by mRID of all the objects retrieved. If an item was not found, or
     * couldn't be added to [service], it will be excluded from the map and its mRID will be present in [MultiObjectResult.failed]
     *
     * In addition to normal gRPC errors, you may also receive an unsuccessful [GrpcResult] with the following errors:
     * - [ClassCastException] if the requested object was of the wrong type.
     */
    @JvmOverloads
    fun getEquipmentContainers(
        mRIDs: Sequence<String>,
        expectedClass: Class<out EquipmentContainer> = EquipmentContainer::class.java,
        includeEnergizingContainers: IncludedEnergizingContainers = IncludedEnergizingContainers.NONE,
        includeEnergizedContainers: IncludedEnergizedContainers = IncludedEnergizedContainers.NONE,
        networkState: NetworkState = NetworkState.NORMAL
    ): GrpcResult<MultiObjectResult> =
        getWithReferences(mRIDs, expectedClass) { it, (objects, _) ->
            objects.putAll(
                getEquipmentForContainers(it.map { eq -> eq.mRID }, includeEnergizingContainers, includeEnergizedContainers, networkState)
                    .onError { thrown, wasHandled -> return@getWithReferences GrpcResult.ofError(thrown, wasHandled) }
                    .value.objects
            )
            null
        }

    /**
     * Retrieve the [Equipment] for the [loop]
     *
     * Exceptions that occur during retrieval will be caught and passed to all error handlers that have been registered against this client.
     *
     * @param loop The [Loop] to fetch equipment for.
     * @param networkState The network state of the equipment.
     *
     * @return A [GrpcResult] with a result of one of the following:
     * - When [GrpcResult.wasSuccessful], a map containing the retrieved objects keyed by mRID, accessible via [GrpcResult.value]. If an item was not found, or
     * couldn't be added to [service], it will be excluded from the map and its mRID will be present in [MultiObjectResult.failed] (see [BaseService.add]).
     * - When [GrpcResult.wasFailure], the error that occurred retrieving or processing the object, accessible via [GrpcResult.thrown].
     * Note the [NetworkConsumerClient] warning in this case.
     */
    fun getEquipmentForLoop(loop: Loop, networkState: NetworkState = NetworkState.NORMAL): GrpcResult<MultiObjectResult> =
        getEquipmentForLoop(loop.mRID, networkState)

    /**
     * Retrieve the [Equipment] for the [Loop] represented by [mRID]
     *
     * Exceptions that occur during retrieval will be caught and passed to all error handlers that have been registered against this client.
     *
     * @param mRID The mRID of the [Loop] to fetch equipment for.
     * @param networkState The network state of the equipment.
     *
     * @return A [GrpcResult] with a result of one of the following:
     * - When [GrpcResult.wasSuccessful], a map containing the retrieved objects keyed by mRID, accessible via [GrpcResult.value]. If an item was not found, or
     * couldn't be added to [service], it will be excluded from the map and its mRID will be present in [MultiObjectResult.failed] (see [BaseService.add]).
     * - When [GrpcResult.wasFailure], the error that occurred retrieving or processing the object, accessible via [GrpcResult.thrown].
     * Note the [NetworkConsumerClient] warning in this case.
     */
    fun getEquipmentForLoop(mRID: String, networkState: NetworkState = NetworkState.NORMAL): GrpcResult<MultiObjectResult> =
        getWithReferences(mRID, Loop::class.java) { loop, (objects, _) ->
            objects.putAll(loop.circuits.associateBy { it.mRID })
            objects.putAll(loop.substations.associateBy { it.mRID })
            objects.putAll(loop.energizingSubstations.associateBy { it.mRID })

            val containers = loop.circuits.asSequence() + loop.substations.asSequence() + loop.energizingSubstations.asSequence()
            objects.putAll(
                getEquipmentForContainers(containers.map { it.mRID }, networkState = networkState)
                    .onError { thrown, wasHandled -> return@getWithReferences GrpcResult.ofError(thrown, wasHandled) }
                    .value.objects)
            null
        }

    /**
     * Retrieve the [Equipment] for all [Loop]s
     *
     * Exceptions that occur during retrieval will be caught and passed to all error handlers that have been registered against this client.
     *
     * @param networkState The network state of the equipment.
     *
     * @return A [GrpcResult] with a result of one of the following:
     * - When [GrpcResult.wasSuccessful], a map containing the retrieved objects keyed by mRID, accessible via [GrpcResult.value]. If an item was not found, or
     * couldn't be added to [service], it will be excluded from the map and its mRID will be present in [MultiObjectResult.failed] (see [BaseService.add]).
     * - When [GrpcResult.wasFailure], the error that occurred retrieving or processing the object, accessible via [GrpcResult.thrown].
     * Note the [NetworkConsumerClient] warning in this case.
     */
    fun getAllLoops(networkState: NetworkState = NetworkState.NORMAL): GrpcResult<MultiObjectResult> {
        val response = getNetworkHierarchy()
        val hierarchy = response.onError { thrown, wasHandled -> return GrpcResult.ofError(thrown, wasHandled) }.value

        val mor = MultiObjectResult()
        mor.objects.putAll(hierarchy.geographicalRegions)
        mor.objects.putAll(hierarchy.subGeographicalRegions)
        mor.objects.putAll(hierarchy.substations)
        mor.objects.putAll(hierarchy.feeders)
        mor.objects.putAll(hierarchy.circuits)
        mor.objects.putAll(hierarchy.loops)

        val mrids = hierarchy.loops.values
            .asSequence()
            .flatMap { it.circuits.asSequence() + it.substations.asSequence() + it.energizingSubstations.asSequence() }
            .distinct()
            .map { it.mRID }

        mor.objects.putAll(
            getEquipmentForContainers(mrids, networkState = networkState)
                .onError { thrown, wasHandled -> return GrpcResult.ofError(thrown, wasHandled) }
                .value.objects
        )

        resolveReferences(mor)?.let { return it }

        return GrpcResult(mor)
    }

    override fun processIdentifiedObjects(mRIDs: Sequence<String>): Sequence<ExtractResult> {
        val extractResults = mutableListOf<ExtractResult>()
        val streamObserver = AwaitableStreamObserver<GetIdentifiedObjectsResponse> { response ->
            response.identifiedObjectsList.forEach {
                extractResults.add(extractIdentifiedObject(it))
            }
        }

        val request = stub.getIdentifiedObjects(streamObserver)
        val builder = GetIdentifiedObjectsRequest.newBuilder()

        batchSend(mRIDs, builder::addMrids) {
            if (builder.mridsList.isNotEmpty())
                request.onNext(builder.build())
            builder.clearMrids()
        }

        request.onCompleted()
        streamObserver.await()

        return extractResults.asSequence()
    }

    private fun processEquipmentForContainers(
        mRIDs: Sequence<String>,
        includeEnergizingContainers: IncludedEnergizingContainers,
        includeEnergizedContainers: IncludedEnergizedContainers,
        networkState: NetworkState
    ): Sequence<ExtractResult> {
        val extractResults = mutableListOf<ExtractResult>()
        val streamObserver = AwaitableStreamObserver<GetEquipmentForContainersResponse> { response ->
            response.identifiedObjectsList.forEach {
                extractResults.add(extractIdentifiedObject(it))
            }
        }

        val request = stub.getEquipmentForContainers(streamObserver)
        val builder = GetEquipmentForContainersRequest.newBuilder()

        builder.includeEnergizingContainers = mapIncludeEnergizingContainers.toPb(includeEnergizingContainers)
        builder.includeEnergizedContainers = mapIncludeEnergizedContainers.toPb(includeEnergizedContainers)
        builder.networkState = mapNetworkState.toPb(networkState)

        batchSend(mRIDs, builder::addMrids) {
            if (builder.mridsList.isNotEmpty())
                request.onNext(builder.build())
            builder.clearMrids()
        }

        request.onCompleted()
        streamObserver.await()

        return extractResults.asSequence()
    }

    private fun processRestriction(mRID: String): Sequence<ExtractResult> {
        val extractResults = mutableListOf<ExtractResult>()
        val streamObserver = AwaitableStreamObserver<GetEquipmentForRestrictionResponse> { response ->
            response.identifiedObjectsList.forEach {
                extractResults.add(extractIdentifiedObject(it))
            }
        }

        stub.getEquipmentForRestriction(GetEquipmentForRestrictionRequest.newBuilder().setMrid(mRID).build(), streamObserver)
        streamObserver.await()

        return extractResults.asSequence()
    }

    private fun processConnectivityNode(mRID: String): Sequence<ExtractResult> {
        val extractResults = mutableListOf<ExtractResult>()
        val streamObserver = AwaitableStreamObserver<GetTerminalsForNodeResponse> { response ->
            extractResults.add(extractResult(response.terminal.mRID()) { addFromPb(response.terminal) })
        }

        stub.getTerminalsForNode(GetTerminalsForNodeRequest.newBuilder().setMrid(mRID).build(), streamObserver)
        streamObserver.await()

        return extractResults.asSequence()
    }

    private fun extractIdentifiedObject(io: NetworkIdentifiedObject): ExtractResult =
        protoToCim.networkService.addFromPb(io).let {
            ExtractResult(it.identifiedObject, it.mRID)
        }

    private fun <T, U : IdentifiedObject> toMap(objects: Iterable<T>, mapper: (T) -> U?): Map<String, U> =
        objects
            .mapNotNull(mapper)
            .associateBy { it.mRID }

    private inline fun <reified T> getWithReferences(
        mRID: String,
        expectedClass: Class<out T>,
        getAdditional: (T, MultiObjectResult) -> GrpcResult<MultiObjectResult>?
    ): GrpcResult<MultiObjectResult> =
        getWithReferences(sequenceOf(mRID), expectedClass) { it, mor -> getAdditional(it.elementAt(0), mor) }

    private inline fun <reified T> getWithReferences(
        mRIDs: Sequence<String>,
        expectedClass: Class<out T>,
        getAdditional: (Sequence<T>, MultiObjectResult) -> GrpcResult<MultiObjectResult>?
    ): GrpcResult<MultiObjectResult> {
        val mor = MultiObjectResult()

        networkHierarchy ?: getNetworkHierarchy().onError { thrown, wasHandled -> return@getWithReferences GrpcResult.ofError(thrown, wasHandled) }

        val toFetch = mutableListOf<String>()
        mRIDs.forEach { mRID ->  // Only process mRIDs not already present in service
            service.get<IdentifiedObject>(mRID)?.let { mor.objects[it.mRID] = it } ?: toFetch.add(mRID)
        }

        val response = getIdentifiedObjects(toFetch.asSequence())
        val result = response.onError { thrown, wasHandled -> return@getWithReferences GrpcResult.ofError(thrown, wasHandled) }.value

        val invalid = result.objects.values.filter { !expectedClass.isInstance(it) }.toMutableSet()
        if (invalid.isNotEmpty()) {
            val e = ClassCastException("Unable to extract ${expectedClass.simpleName} networks from ${invalid.map { it.typeNameAndMRID() }}.")
            return GrpcResult.ofError(e, tryHandleError(e))
        }

        mor.objects.putAll(result.objects)
        mor.failed.addAll(result.failed)

        getAdditional(mor.objects.values.asSequence().filterIsInstance(expectedClass), mor)?.let { return@getWithReferences it }
        resolveReferences(mor)?.let { return@getWithReferences it }

        return GrpcResult(mor)
    }

    internal fun resolveReferences(mor: MultiObjectResult): GrpcResult<MultiObjectResult>? {
        var res = mor
        var subsequent = false
        do {
            // Skip any reference trying to resolve from an EquipmentContainer on subsequent passes - e.g a PowerTransformer trying to pull in its LvFeeder.
            // EquipmentContainers should be retrieved explicitly or via a hierarchy call.
            val toResolve = res.objects.keys
                .flatMap { service.getUnresolvedReferencesFrom(it) }
                .filterNot { subsequent && EquipmentContainer::class.java.isAssignableFrom(it.resolver.fromClass) }
                .map { it.toMrid }
                .distinct()
                .toList()

            res = getIdentifiedObjects(toResolve).onError { thrown, wasHandled ->
                return GrpcResult.ofError(thrown, wasHandled)
            }.value
            mor.objects.putAll(res.objects)

            subsequent = true
        } while (res.objects.isNotEmpty())
        return null
    }

}

/***
 * Retrieve the equipment container network for the specified [mRID] and store the results in the [NetworkConsumerClient.service].
 *
 * This is a Kotlin's generic convenience method that will fetch the container object and all the equipment contained, along with all subsequent
 * references. This should entail a complete connectivity model for the container, however not the connectivity between multiple containers.
 *
 * @param mRID The mRID of the [EquipmentContainer] to fetch.
 * @param includeEnergizingContainers The level of energizing containers to include equipment from.
 * @param includeEnergizedContainers The level of energized containers to include equipment from.
 * @param networkState The network state of the equipment.
 *
 * @return A [GrpcResult] of a [MultiObjectResult]. If successful, containing a map keyed by mRID of all the objects retrieved. If an item couldn't be added to
 * [NetworkConsumerClient.service], its mRID will be present in [MultiObjectResult.failed].
 *
 * In addition to normal gRPC errors, you may also receive an unsuccessful [GrpcResult] with the following errors:
 * - [NoSuchElementException] if the requested object was not found.
 * - [ClassCastException] if the requested object was of the wrong type.
 */
inline fun <reified T : EquipmentContainer> NetworkConsumerClient.getEquipmentContainer(
    mRID: String,
    includeEnergizingContainers: IncludedEnergizingContainers = IncludedEnergizingContainers.NONE,
    includeEnergizedContainers: IncludedEnergizedContainers = IncludedEnergizedContainers.NONE,
    networkState: NetworkState = NetworkState.NORMAL
): GrpcResult<MultiObjectResult> {
    return getEquipmentContainer(mRID, T::class.java, includeEnergizingContainers, includeEnergizedContainers, networkState)
}

/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.streaming.get

import com.zepben.evolve.cim.iec61968.operations.OperationalRestriction
import com.zepben.evolve.cim.iec61970.base.core.*
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.Loop
import com.zepben.evolve.services.common.BaseService
import com.zepben.evolve.services.common.extensions.typeNameAndMRID
import com.zepben.evolve.services.common.translator.mRID
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.translator.NetworkProtoToCim
import com.zepben.evolve.services.network.translator.mRID
import com.zepben.evolve.streaming.get.hierarchy.NetworkHierarchy
import com.zepben.evolve.streaming.grpc.GrpcChannel
import com.zepben.evolve.streaming.grpc.GrpcResult
import com.zepben.protobuf.nc.*
import com.zepben.protobuf.nc.NetworkIdentifiedObject.IdentifiedObjectCase.*
import io.grpc.CallCredentials
import io.grpc.ManagedChannel
import java.io.IOException
import java.util.concurrent.Executors

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
 */
class NetworkConsumerClient(
    private val stub: NetworkConsumerGrpc.NetworkConsumerStub,
    override val service: NetworkService = NetworkService(),
    override val protoToCim: NetworkProtoToCim = NetworkProtoToCim(service)
) : CimConsumerClient<NetworkService, NetworkProtoToCim>() {

    private var networkHierarchy: NetworkHierarchy? = null

    /**
     * Create a [NetworkConsumerClient]
     *
     * @param channel [ManagedChannel] to build a stub from.
     * @param callCredentials [CallCredentials] to be attached to the stub.
     */
    @JvmOverloads
    constructor(channel: ManagedChannel, callCredentials: CallCredentials? = null) :
        this(NetworkConsumerGrpc.newStub(channel).withExecutor(Executors.newSingleThreadExecutor())
            .apply { callCredentials?.let { withCallCredentials(it) } })

    /**
     * Create a [NetworkConsumerClient]
     *
     * @param channel [GrpcChannel] to build a stub from.
     * @param callCredentials [CallCredentials] to be attached to the stub.
     */
    @JvmOverloads
    constructor(channel: GrpcChannel, callCredentials: CallCredentials? = null) :
        this(NetworkConsumerGrpc.newStub(channel.channel).withExecutor(Executors.newSingleThreadExecutor())
            .apply { callCredentials?.let { withCallCredentials(it) } })

    /**
     * Retrieve the [Equipment] for [equipmentContainer]
     *
     * Exceptions that occur during retrieval will be caught and passed to all error handlers that have been registered against this client.
     *
     * @param equipmentContainer The [EquipmentContainer] to fetch equipment for.
     *
     * @return A [GrpcResult] with a result of one of the following:
     * - When [GrpcResult.wasSuccessful], a map containing the retrieved objects keyed by mRID, accessible via [GrpcResult.value]. If an item was not found, or
     * couldn't be added to [service], it will be excluded from the map and its mRID will be present in [MultiObjectResult.failed] (see [BaseService.add]).
     * - When [GrpcResult.wasFailure], the error that occurred retrieving or processing the the object, accessible via [GrpcResult.thrown].
     * Note the [NetworkConsumerClient] warning in this case.
     */
    fun getEquipmentForContainer(equipmentContainer: EquipmentContainer): GrpcResult<MultiObjectResult> =
        getEquipmentForContainer(equipmentContainer.mRID)

    /**
     * Retrieve the [Equipment] for the [EquipmentContainer] represented by [mRID]
     *
     * Exceptions that occur during retrieval will be caught and passed to all error handlers that have been registered against this client.
     *
     * @param mRID The mRID of the [EquipmentContainer] to fetch equipment for.
     *
     * @return A [GrpcResult] with a result of one of the following:
     * - When [GrpcResult.wasSuccessful], a map containing the retrieved objects keyed by mRID, accessible via [GrpcResult.value]. If an item was not found, or
     * couldn't be added to [service], it will be excluded from the map and its mRID will be present in [MultiObjectResult.failed] (see [BaseService.add]).
     * - When [GrpcResult.wasFailure], the error that occurred retrieving or processing the the object, accessible via [GrpcResult.thrown].
     * Note the [NetworkConsumerClient] warning in this case.
     */
    fun getEquipmentForContainer(mRID: String): GrpcResult<MultiObjectResult> =
        getEquipmentForContainers(sequenceOf(mRID))

    /**
     * Retrieve the [Equipment] for all [EquipmentContainer]s represented by [mRIDs]
     *
     * Exceptions that occur during retrieval will be caught and passed to all error handlers that have been registered against this client.
     *
     * @param mRIDs The mRIDs of the [EquipmentContainer]s to fetch equipment for.
     *
     * @return A [GrpcResult] with a result of one of the following:
     * - When [GrpcResult.wasSuccessful], a map containing the retrieved objects keyed by mRID, accessible via [GrpcResult.value]. If an item was not found, or
     * couldn't be added to [service], it will be excluded from the map and its mRID will be present in [MultiObjectResult.failed] (see [BaseService.add]).
     * - When [GrpcResult.wasFailure], the error that occurred retrieving or processing the the object, accessible via [GrpcResult.thrown].
     * Note the [NetworkConsumerClient] warning in this case.
     */
    fun getEquipmentForContainers(mRIDs: Iterable<String>): GrpcResult<MultiObjectResult> = getEquipmentForContainers(mRIDs.asSequence())

    /**
     * Retrieve the [Equipment] for all [EquipmentContainer]s represented by [mRIDs]
     *
     * Exceptions that occur during retrieval will be caught and passed to all error handlers that have been registered against this client.
     *
     * @param mRIDs The mRIDs of the [EquipmentContainer]s to fetch equipment for.
     *
     * @return A [GrpcResult] with a result of one of the following:
     * - When [GrpcResult.wasSuccessful], a map containing the retrieved objects keyed by mRID, accessible via [GrpcResult.value]. If an item was not found, or
     * couldn't be added to [service], it will be excluded from the map and its mRID will be present in [MultiObjectResult.failed] (see [BaseService.add]).
     * - When [GrpcResult.wasFailure], the error that occurred retrieving or processing the the object, accessible via [GrpcResult.thrown].
     * Note the [NetworkConsumerClient] warning in this case.
     */
    fun getEquipmentForContainers(mRIDs: Sequence<String>): GrpcResult<MultiObjectResult> = handleMultiObjectRPC { processEquipmentForContainers(mRIDs) }

    /**
     * Retrieve the [Equipment] for [operationalRestriction].
     *
     * Exceptions that occur during retrieval will be caught and passed to all error handlers that have been registered against this client.
     *
     * @param operationalRestriction The [OperationalRestriction] to fetch equipment for.
     * @return A [GrpcResult] with a result of one of the following:
     * - When [GrpcResult.wasSuccessful], a map containing the retrieved objects keyed by mRID, accessible via [GrpcResult.value]. If an item was not found, or
     * couldn't be added to [service], it will be excluded from the map and its mRID will be present in [MultiObjectResult.failed] (see [BaseService.add]).
     * - When [GrpcResult.wasFailure], the error that occurred retrieving or processing the the object, accessible via [GrpcResult.thrown].
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
     * - When [GrpcResult.wasFailure], the error that occurred retrieving or processing the the object, accessible via [GrpcResult.thrown].
     * Note the [NetworkConsumerClient] warning in this case.
     */
    fun getEquipmentForRestriction(mRID: String): GrpcResult<MultiObjectResult> =
        handleMultiObjectRPC { processRestriction(mRID) }

    /**
     * Retrieve the current [Equipment] for [feeder]. The current equipment is the equipment connected to the Feeder based on
     * the current phasing and switching of the network.
     *
     * Exceptions that occur during retrieval will be caught and passed to all error handlers that have been registered against this client.
     *
     * @param feeder The [Feeder] to fetch current equipment for.
     * @return A [GrpcResult] with a result of one of the following:
     * - When [GrpcResult.wasSuccessful], a map containing the retrieved objects keyed by mRID, accessible via [GrpcResult.value]. If an item was not found, or
     * couldn't be added to [service], it will be excluded from the map and its mRID will be present in [MultiObjectResult.failed] (see [BaseService.add]).
     * - When [GrpcResult.wasFailure], the error that occurred retrieving or processing the the object, accessible via [GrpcResult.thrown].
     * Note the [NetworkConsumerClient] warning in this case.
     */
    fun getCurrentEquipmentForFeeder(feeder: Feeder): GrpcResult<MultiObjectResult> =
        getCurrentEquipmentForFeeder(feeder.mRID)

    /**
     * Retrieve the current [Equipment] for the [Feeder] represented by [mRID]. The current equipment is the equipment connected to the Feeder based on
     * the current phasing and switching of the network.
     *
     * Exceptions that occur during retrieval will be caught and passed to all error handlers that have been registered against this client.
     *
     * @param mRID The mRID of the [Feeder] to fetch current equipment for.
     * @return A [GrpcResult] with a result of one of the following:
     * - When [GrpcResult.wasSuccessful], a map containing the retrieved objects keyed by mRID, accessible via [GrpcResult.value]. If an item was not found, or
     * couldn't be added to [service], it will be excluded from the map and its mRID will be present in [MultiObjectResult.failed] (see [BaseService.add]).
     * - When [GrpcResult.wasFailure], the error that occurred retrieving or processing the the object, accessible via [GrpcResult.thrown].
     * Note the [NetworkConsumerClient] warning in this case.
     */
    fun getCurrentEquipmentForFeeder(mRID: String): GrpcResult<MultiObjectResult> =
        handleMultiObjectRPC { processFeeder(mRID) }

    /**
     * Retrieve the [Terminal]s for [connectivityNode].
     *
     * Exceptions that occur during retrieval will be caught and passed to all error handlers that have been registered against this client.
     *
     * @param connectivityNode The [ConnectivityNode] to fetch terminals for.
     * @return A [GrpcResult] with a result of one of the following:
     * - When [GrpcResult.wasSuccessful], a map containing the retrieved objects keyed by mRID, accessible via [GrpcResult.value]. If an item was not found, or
     * couldn't be added to [service], it will be excluded from the map and its mRID will be present in [MultiObjectResult.failed] (see [BaseService.add]).
     * - When [GrpcResult.wasFailure], the error that occurred retrieving or processing the the object, accessible via [GrpcResult.thrown].
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
     * - When [GrpcResult.wasFailure], the error that occurred retrieving or processing the the object, accessible via [GrpcResult.thrown].
     * Note the [NetworkConsumerClient] warning in this case.
     */
    fun getTerminalsForConnectivityNode(mRID: String): GrpcResult<MultiObjectResult> =
        handleMultiObjectRPC { processConnectivityNode(mRID) }

    /***
     * Retrieve the network hierarchy.
     *
     *
     * @return A simplified version of the network hierarchy that can be used to make further in-depth requests.
     */
    fun getNetworkHierarchy(): GrpcResult<NetworkHierarchy> = tryRpc {
        if (networkHierarchy == null) {
            val streamObserver = AwaitableStreamObserver<GetNetworkHierarchyResponse> { response ->
                networkHierarchy = NetworkHierarchy(
                    toMap(response.geographicalRegionsList) { getOrAdd(it.mRID()) { addFromPb(it) } },
                    toMap(response.subGeographicalRegionsList) { getOrAdd(it.mRID()) { addFromPb(it) } },
                    toMap(response.substationsList) { getOrAdd(it.mRID()) { addFromPb(it) } },
                    toMap(response.feedersList) { getOrAdd(it.mRID()) { addFromPb(it) } },
                    toMap(response.circuitsList) { getOrAdd(it.mRID()) { addFromPb(it) } },
                    toMap(response.loopsList) { getOrAdd(it.mRID()) { addFromPb(it) } }
                )
            }

            stub.getNetworkHierarchy(GetNetworkHierarchyRequest.newBuilder().build(), streamObserver)

            streamObserver.await()
        }
        networkHierarchy ?: throw IOException("No network hierarchy was received before GRPC channel was closed.")
    }

    /***
     * Retrieve the feeder network for the specified [mRID] and store the results in the [service].
     *
     * This is a convenience method that will fetch the feeder object and all of the equipment referenced by the feeder (normal state), along with
     * all references. This should entail a complete connectivity model for the feeder, however not the connectivity between multiple feeders.
     *
     * @param mRID The mRID of the [Feeder] to fetch equipment for.
     *
     * @return A [GrpcResult] of a [MultiObjectResult]. If successful, containing a map keyed by mRID of all the objects retrieved. If an item couldn't be added to
     * [service], its mRID will be present in [MultiObjectResult.failed].
     *
     * In addition to normal gRPC errors, you may also receive an unsuccessful [GrpcResult] with the following errors:
     * - [NoSuchElementException] if the requested object was not found.
     * - [ClassCastException] if the requested object was of the wrong type.
     */
    @Deprecated(
        "prefer the more generic getEquipmentContainer",
        replaceWith = ReplaceWith(
            "this.getEquipmentContainer<Feeder>(mRID)",
            "com.zepben.evolve.cim.iec61970.base.core.Feeder"
        )
    )
    fun getFeeder(mRID: String): GrpcResult<MultiObjectResult> = getEquipmentContainer(mRID, Feeder::class.java)

    /***
     * Retrieve the equipment container network for the specified [mRID] and store the results in the [service].
     *
     * This is a convenience method that will fetch the container object and all of the equipment contained, along with all subsequent
     * references. This should entail a complete connectivity model for the container, however not the connectivity between multiple containers.
     *
     * @param mRID The mRID of the [EquipmentContainer] to fetch.
     * @param expectedClass The expected type of the fetched container.
     *
     * @return A [GrpcResult] of a [MultiObjectResult]. If successful, containing a map keyed by mRID of all the objects retrieved. If an item couldn't be added to
     * [service], its mRID will be present in [MultiObjectResult.failed].
     *
     * In addition to normal gRPC errors, you may also receive an unsuccessful [GrpcResult] with the following errors:
     * - [NoSuchElementException] if the requested object was not found.
     * - [ClassCastException] if the requested object was of the wrong type.
     */
    @JvmOverloads
    fun getEquipmentContainer(mRID: String, expectedClass: Class<out EquipmentContainer> = EquipmentContainer::class.java): GrpcResult<MultiObjectResult> =
        tryRpc {
            val result = getEquipmentContainers(sequenceOf(mRID), expectedClass)
            if (result.wasFailure)
                throw result.thrown

            if (result.value.objects.isEmpty())
                throw NoSuchElementException("No object with mRID $mRID could be found.")

            result.value
        }

    /***
     * Retrieve the equipment container networks for the specified [mRID]s and store the results in the [service].
     *
     * This is a convenience method that will fetch the container objects and all of the equipment contained, along with all subsequent
     * references. This should entail a complete connectivity model for the containers, however not the connectivity between multiple containers.
     *
     * @param mRIDs The mRIDs of the [EquipmentContainer]s to fetch.
     * @param expectedClass The expected type of the fetched containers.
     *
     * @return A [GrpcResult] of a [MultiObjectResult]. If successful, containing a map keyed by mRID of all the objects retrieved. If an item was not found, or
     * couldn't be added to [service], it will be excluded from the map and its mRID will be present in [MultiObjectResult.failed]
     *
     * In addition to normal gRPC errors, you may also receive an unsuccessful [GrpcResult] with the following errors:
     * - [ClassCastException] if the requested object was of the wrong type.
     */
    fun getEquipmentContainers(
        mRIDs: Iterable<String>,
        expectedClass: Class<out EquipmentContainer> = EquipmentContainer::class.java
    ): GrpcResult<MultiObjectResult> = getEquipmentContainers(mRIDs.asSequence(), expectedClass)

    /***
     * Retrieve the equipment container networks for the specified [mRID]s and store the results in the [service].
     *
     * This is a convenience method that will fetch the container objects and all of the equipment contained, along with all subsequent
     * references. This should entail a complete connectivity model for the containers, however not the connectivity between multiple containers.
     *
     * @param mRIDs The mRIDs of the [EquipmentContainer]s to fetch.
     * @param expectedClass The expected type of the fetched containers.
     *
     * @return A [GrpcResult] of a [MultiObjectResult]. If successful, containing a map keyed by mRID of all the objects retrieved. If an item was not found, or
     * couldn't be added to [service], it will be excluded from the map and its mRID will be present in [MultiObjectResult.failed]
     *
     * In addition to normal gRPC errors, you may also receive an unsuccessful [GrpcResult] with the following errors:
     * - [ClassCastException] if the requested object was of the wrong type.
     */
    fun getEquipmentContainers(
        mRIDs: Sequence<String>,
        expectedClass: Class<out EquipmentContainer> = EquipmentContainer::class.java
    ): GrpcResult<MultiObjectResult> = getWithReferences(mRIDs, expectedClass) { it, mor ->
        mor.objects.putAll(getEquipmentForContainers(it.map { eq -> eq.mRID })
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
     *
     * @return A [GrpcResult] with a result of one of the following:
     * - When [GrpcResult.wasSuccessful], a map containing the retrieved objects keyed by mRID, accessible via [GrpcResult.value]. If an item was not found, or
     * couldn't be added to [service], it will be excluded from the map and its mRID will be present in [MultiObjectResult.failed] (see [BaseService.add]).
     * - When [GrpcResult.wasFailure], the error that occurred retrieving or processing the the object, accessible via [GrpcResult.thrown].
     * Note the [NetworkConsumerClient] warning in this case.
     */
    fun getEquipmentForLoop(loop: Loop): GrpcResult<MultiObjectResult> =
        getEquipmentForLoop(loop.mRID)

    /**
     * Retrieve the [Equipment] for the [Loop] represented by [mRID]
     *
     * Exceptions that occur during retrieval will be caught and passed to all error handlers that have been registered against this client.
     *
     * @param mRID The mRID of the [Loop] to fetch equipment for.
     *
     * @return A [GrpcResult] with a result of one of the following:
     * - When [GrpcResult.wasSuccessful], a map containing the retrieved objects keyed by mRID, accessible via [GrpcResult.value]. If an item was not found, or
     * couldn't be added to [service], it will be excluded from the map and its mRID will be present in [MultiObjectResult.failed] (see [BaseService.add]).
     * - When [GrpcResult.wasFailure], the error that occurred retrieving or processing the the object, accessible via [GrpcResult.thrown].
     * Note the [NetworkConsumerClient] warning in this case.
     */
    fun getEquipmentForLoop(mRID: String): GrpcResult<MultiObjectResult> =
        getWithReferences(mRID, Loop::class.java) { loop, mor ->
            mor.objects.putAll(loop.circuits.associateBy { it.mRID })
            mor.objects.putAll(loop.substations.associateBy { it.mRID })
            mor.objects.putAll(loop.energizingSubstations.associateBy { it.mRID })

            val containers = loop.circuits.asSequence() + loop.substations.asSequence() + loop.energizingSubstations.asSequence()
            mor.objects.putAll(getEquipmentForContainers(containers.map { it.mRID })
                .onError { thrown, wasHandled -> return@getWithReferences GrpcResult.ofError(thrown, wasHandled) }
                .value.objects)
            null
        }

    /**
     * Retrieve the [Equipment] for all [Loop]s
     *
     * Exceptions that occur during retrieval will be caught and passed to all error handlers that have been registered against this client.
     *
     *
     * @return A [GrpcResult] with a result of one of the following:
     * - When [GrpcResult.wasSuccessful], a map containing the retrieved objects keyed by mRID, accessible via [GrpcResult.value]. If an item was not found, or
     * couldn't be added to [service], it will be excluded from the map and its mRID will be present in [MultiObjectResult.failed] (see [BaseService.add]).
     * - When [GrpcResult.wasFailure], the error that occurred retrieving or processing the the object, accessible via [GrpcResult.thrown].
     * Note the [NetworkConsumerClient] warning in this case.
     */
    fun getAllLoops(): GrpcResult<MultiObjectResult> {
        val response = getNetworkHierarchy()
        val hierarchy = response.onError { thrown, wasHandled -> return GrpcResult.ofError(thrown, wasHandled) }.value

        val mor = MultiObjectResult()
        mor.objects.putAll(hierarchy.geographicalRegions)
        mor.objects.putAll(hierarchy.subGeographicalRegions)
        mor.objects.putAll(hierarchy.substations)
        mor.objects.putAll(hierarchy.feeders)
        mor.objects.putAll(hierarchy.circuits)
        mor.objects.putAll(hierarchy.loops)

        mor.objects.putAll(getEquipmentForContainers(hierarchy.loops.values
            .asSequence()
            .flatMap { it.circuits.asSequence() + it.substations.asSequence() + it.energizingSubstations.asSequence() }
            .distinct()
            .map { it.mRID })
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

    private fun processEquipmentForContainers(mRIDs: Sequence<String>): Sequence<ExtractResult> {
        val extractResults = mutableListOf<ExtractResult>()
        val streamObserver = AwaitableStreamObserver<GetEquipmentForContainersResponse> { response ->
            response.identifiedObjectsList.forEach {
                extractResults.add(extractIdentifiedObject(it))
            }
        }

        val request = stub.getEquipmentForContainers(streamObserver)
        val builder = GetEquipmentForContainersRequest.newBuilder()

        batchSend(mRIDs, builder::addMrids) {
            if (builder.mridsList.isNotEmpty())
                request.onNext(builder.build())
            builder.clearMrids()
        }

        request.onCompleted()
        streamObserver.await()

        return extractResults.asSequence()
    }

    private fun processFeeder(mRID: String): Sequence<ExtractResult> {
        val extractResults = mutableListOf<ExtractResult>()
        val streamObserver = AwaitableStreamObserver<GetCurrentEquipmentForFeederResponse> { response ->
            response.identifiedObjectsList.forEach {
                extractResults.add(extractIdentifiedObject(it))
            }
        }

        stub.getCurrentEquipmentForFeeder(GetCurrentEquipmentForFeederRequest.newBuilder().setMrid(mRID).build(), streamObserver)
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

    private fun extractIdentifiedObject(io: NetworkIdentifiedObject): ExtractResult {
        return when (io.identifiedObjectCase) {
            BATTERYUNIT -> extractResult(io.batteryUnit.mRID()) { addFromPb(io.batteryUnit) }
            PHOTOVOLTAICUNIT -> extractResult(io.photoVoltaicUnit.mRID()) { addFromPb(io.photoVoltaicUnit) }
            POWERELECTRONICSWINDUNIT -> extractResult(io.powerElectronicsWindUnit.mRID()) { addFromPb(io.powerElectronicsWindUnit) }
            CABLEINFO -> extractResult(io.cableInfo.mRID()) { addFromPb(io.cableInfo) }
            OVERHEADWIREINFO -> extractResult(io.overheadWireInfo.mRID()) { addFromPb(io.overheadWireInfo) }
            POWERTRANSFORMERINFO -> extractResult(io.powerTransformerInfo.mRID()) { addFromPb(io.powerTransformerInfo) }
            ASSETOWNER -> extractResult(io.assetOwner.mRID()) { addFromPb(io.assetOwner) }
            ORGANISATION -> extractResult(io.organisation.mRID()) { addFromPb(io.organisation) }
            LOCATION -> extractResult(io.location.mRID()) { addFromPb(io.location) }
            METER -> extractResult(io.meter.mRID()) { addFromPb(io.meter) }
            USAGEPOINT -> extractResult(io.usagePoint.mRID()) { addFromPb(io.usagePoint) }
            OPERATIONALRESTRICTION -> extractResult(io.operationalRestriction.mRID()) { addFromPb(io.operationalRestriction) }
            FAULTINDICATOR -> extractResult(io.faultIndicator.mRID()) { addFromPb(io.faultIndicator) }
            BASEVOLTAGE -> extractResult(io.baseVoltage.mRID()) { addFromPb(io.baseVoltage) }
            CONNECTIVITYNODE -> extractResult(io.connectivityNode.mRID()) { addFromPb(io.connectivityNode) }
            FEEDER -> extractResult(io.feeder.mRID()) { addFromPb(io.feeder) }
            GEOGRAPHICALREGION -> extractResult(io.geographicalRegion.mRID()) { addFromPb(io.geographicalRegion) }
            SITE -> extractResult(io.site.mRID()) { addFromPb(io.site) }
            SUBGEOGRAPHICALREGION -> extractResult(io.subGeographicalRegion.mRID()) { addFromPb(io.subGeographicalRegion) }
            SUBSTATION -> extractResult(io.substation.mRID()) { addFromPb(io.substation) }
            TERMINAL -> extractResult(io.terminal.mRID()) { addFromPb(io.terminal) }
            ACLINESEGMENT -> extractResult(io.acLineSegment.mRID()) { addFromPb(io.acLineSegment) }
            BREAKER -> extractResult(io.breaker.mRID()) { addFromPb(io.breaker) }
            LOADBREAKSWITCH -> extractResult(io.loadBreakSwitch.mRID()) { addFromPb(io.loadBreakSwitch) }
            DISCONNECTOR -> extractResult(io.disconnector.mRID()) { addFromPb(io.disconnector) }
            ENERGYCONSUMER -> extractResult(io.energyConsumer.mRID()) { addFromPb(io.energyConsumer) }
            ENERGYCONSUMERPHASE -> extractResult(io.energyConsumerPhase.mRID()) { addFromPb(io.energyConsumerPhase) }
            ENERGYSOURCE -> extractResult(io.energySource.mRID()) { addFromPb(io.energySource) }
            ENERGYSOURCEPHASE -> extractResult(io.energySourcePhase.mRID()) { addFromPb(io.energySourcePhase) }
            FUSE -> extractResult(io.fuse.mRID()) { addFromPb(io.fuse) }
            JUMPER -> extractResult(io.jumper.mRID()) { addFromPb(io.jumper) }
            JUNCTION -> extractResult(io.junction.mRID()) { addFromPb(io.junction) }
            LINEARSHUNTCOMPENSATOR -> extractResult(io.linearShuntCompensator.mRID()) { addFromPb(io.linearShuntCompensator) }
            PERLENGTHSEQUENCEIMPEDANCE -> extractResult(io.perLengthSequenceImpedance.mRID()) { addFromPb(io.perLengthSequenceImpedance) }
            POWERELECTRONICSCONNECTION -> extractResult(io.powerElectronicsConnection.mRID()) { addFromPb(io.powerElectronicsConnection) }
            POWERELECTRONICSCONNECTIONPHASE -> extractResult(io.powerElectronicsConnectionPhase.mRID()) { addFromPb(io.powerElectronicsConnectionPhase) }
            POWERTRANSFORMER -> extractResult(io.powerTransformer.mRID()) { addFromPb(io.powerTransformer) }
            POWERTRANSFORMEREND -> extractResult(io.powerTransformerEnd.mRID()) { addFromPb(io.powerTransformerEnd) }
            RATIOTAPCHANGER -> extractResult(io.ratioTapChanger.mRID()) { addFromPb(io.ratioTapChanger) }
            RECLOSER -> extractResult(io.recloser.mRID()) { addFromPb(io.recloser) }
            BUSBARSECTION -> extractResult(io.busbarSection.mRID()) { addFromPb(io.busbarSection) }
            CIRCUIT -> extractResult(io.circuit.mRID()) { addFromPb(io.circuit) }
            LOOP -> extractResult(io.loop.mRID()) { addFromPb(io.loop) }
            POLE -> extractResult(io.pole.mRID()) { addFromPb(io.pole) }
            STREETLIGHT -> extractResult(io.streetlight.mRID()) { addFromPb(io.streetlight) }
            ACCUMULATOR -> extractResult(io.accumulator.measurement.mRID()) { addFromPb(io.accumulator) }
            ANALOG -> extractResult(io.analog.measurement.mRID()) { addFromPb(io.analog) }
            DISCRETE -> extractResult(io.discrete.measurement.mRID()) { addFromPb(io.discrete) }
            CONTROL -> extractResult(io.control.mRID()) { addFromPb(io.control) }
            REMOTECONTROL -> extractResult(io.remoteControl.mRID()) { addFromPb(io.remoteControl) }
            REMOTESOURCE -> extractResult(io.remoteSource.mRID()) { addFromPb(io.remoteSource) }
            TRANSFORMERSTARIMPEDANCE -> extractResult(io.transformerStarImpedance.mRID()) { addFromPb(io.transformerStarImpedance) }
            TRANSFORMERENDINFO -> extractResult(io.transformerEndInfo.mRID()) { addFromPb(io.transformerEndInfo) }
            TRANSFORMERTANKINFO -> extractResult(io.transformerTankInfo.mRID()) { addFromPb(io.transformerTankInfo) }
            NOLOADTEST -> extractResult(io.noLoadTest.mRID()) { addFromPb(io.noLoadTest) }
            OPENCIRCUITTEST -> extractResult(io.openCircuitTest.mRID()) { addFromPb(io.openCircuitTest) }
            SHORTCIRCUITTEST -> extractResult(io.shortCircuitTest.mRID()) { addFromPb(io.shortCircuitTest) }
            EQUIVALENTBRANCH -> extractResult(io.equivalentBranch.mRID()) { addFromPb(io.equivalentBranch) }
            SHUNTCOMPENSATORINFO -> extractResult(io.shuntCompensatorInfo.mRID()) { addFromPb(io.shuntCompensatorInfo) }
            LVFEEDER -> extractResult(io.lvFeeder.mRID()) { addFromPb(io.lvFeeder) }
            OTHER, IDENTIFIEDOBJECT_NOT_SET, null -> throw UnsupportedOperationException(
                "Identified object type ${io.identifiedObjectCase} is not supported by the network service"
            )
        }
    }

    private fun <T, U : IdentifiedObject> toMap(objects: Iterable<T>, mapper: (T) -> U?): Map<String, U> = objects
        .mapNotNull(mapper)
        .associateBy { it.mRID }

    private fun handleMultiObjectRPC(processor: () -> Sequence<ExtractResult>): GrpcResult<MultiObjectResult> =
        tryRpc {
            val results = mutableMapOf<String, IdentifiedObject>()
            val failed = mutableSetOf<String>()
            processor().forEach { result ->
                result.identifiedObject?.let { results[it.mRID] = it } ?: failed.add(result.mRID)
            }
            MultiObjectResult(results, failed)
        }

    private inline fun <reified T> getWithReferences(
        mRID: String,
        expectedClass: Class<out T>,
        getAdditional: (T, MultiObjectResult) -> GrpcResult<MultiObjectResult>?
    ): GrpcResult<MultiObjectResult> = getWithReferences(sequenceOf(mRID), expectedClass) { it, mor -> getAdditional(it.elementAt(0), mor) }

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

    private fun resolveReferences(mor: MultiObjectResult): GrpcResult<MultiObjectResult>? {
        var res = mor
        do {
            val toResolve = res.objects.keys
                .asSequence()
                .flatMap { service.getUnresolvedReferencesFrom(it) }
                .map { it.toMrid }
                .distinct()

            res = getIdentifiedObjects(toResolve).onError { thrown, wasHandled ->
                return GrpcResult.ofError(thrown, wasHandled)
            }.value
            mor.objects.putAll(res.objects)
        } while (res.objects.isNotEmpty())
        return null
    }

}

inline fun <reified T : EquipmentContainer> NetworkConsumerClient.getEquipmentContainer(mRID: String): GrpcResult<MultiObjectResult> {
    return getEquipmentContainer(mRID, T::class.java)
}

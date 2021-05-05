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

/**
 * Consumer client for a [NetworkService].
 *
 * WARNING: The [MultiObjectResult] operations below are not atomic upon a [NetworkService], and thus if processing fails partway through, any previously
 * successful additions will have been processed by the service, and thus you may have an incomplete service. Also note that adding to the service may not
 * occur for an object if another object with the same mRID is already present in service. [MultiObjectResult.failed] can be used to check for mRIDs that
 * were retrieved but not added to service. This should not be the case unless you are processing things concurrently.
 *
 * @property stub The gRPC stub to be used to communicate with the server
 */
class NetworkConsumerClient(
    private val stub: NetworkConsumerGrpc.NetworkConsumerBlockingStub,
    private val protoToCimProvider: (NetworkService) -> NetworkProtoToCim = { NetworkProtoToCim(it) },
) : CimConsumerClient<NetworkService>() {

    /**
     * Create a [NetworkConsumerClient]
     *
     * @param channel [ManagedChannel] to build a blocking stub from.
     * @param callCredentials [CallCredentials] to be attached to the stub.
     */
    @JvmOverloads
    constructor(channel: ManagedChannel, callCredentials: CallCredentials? = null) :
        this(callCredentials?.let { NetworkConsumerGrpc.newBlockingStub(channel).withCallCredentials(callCredentials) }
            ?: NetworkConsumerGrpc.newBlockingStub(channel))

    /**
     * Create a [NetworkConsumerClient]
     *
     * @param channel [GrpcChannel] to build a blocking stub from.
     * @param callCredentials [CallCredentials] to be attached to the stub.
     */
    @JvmOverloads
    constructor(channel: GrpcChannel, callCredentials: CallCredentials? = null) :
        this(callCredentials?.let { NetworkConsumerGrpc.newBlockingStub(channel.channel).withCallCredentials(callCredentials) }
            ?: NetworkConsumerGrpc.newBlockingStub(channel.channel))

    /**
     * Retrieve the object with the given [mRID] and store the result in the [service].
     *
     * Exceptions that occur during sending will be caught and passed to all error handlers that have been registered by [addErrorHandler].
     *
     * @return A [GrpcResult] with a result of one of the following:
     * - When [GrpcResult.wasSuccessful], the item found, accessible via [GrpcResult.value].
     * - When [GrpcResult.wasFailure], the error that occurred retrieving or processing the the object, accessible via [GrpcResult.thrown]. One of:
     *    - [NoSuchElementException] if the object could not be found.
     *    - The gRPC error that occurred while retrieving the object
     */
    override fun getIdentifiedObject(service: NetworkService, mRID: String): GrpcResult<IdentifiedObject> = tryRpc {
        processIdentifiedObjects(service, setOf(mRID)).firstOrNull()?.identifiedObject
            ?: throw NoSuchElementException("No object with mRID $mRID could be found.")
    }

    /**
     * Retrieve the objects with the given [mRIDs] and store the results in the [service].
     *
     * Exceptions that occur during processing will be caught and passed to all error handlers that have been registered by [addErrorHandler].
     *
     * @return A [GrpcResult] with a result of one of the following:
     * - When [GrpcResult.wasSuccessful], a map containing the retrieved objects keyed by mRID, accessible via [GrpcResult.value]. If an item was not found, or
     * couldn't be added to [service], it will be excluded from the map and its mRID will be present in [MultiObjectResult.failed] (see [BaseService.add]).
     * - When [GrpcResult.wasFailure], the error that occurred retrieving or processing the the object, accessible via [GrpcResult.thrown].
     * Note the [NetworkConsumerClient] warning in this case.
     */
    override fun getIdentifiedObjects(service: NetworkService, mRIDs: Iterable<String>): GrpcResult<MultiObjectResult> = tryRpc {
        processExtractResults(mRIDs, processIdentifiedObjects(service, mRIDs.toSet()))
    }

    /**
     * Retrieve the [Equipment] for [equipmentContainer]
     *
     * Exceptions that occur during retrieval will be caught and passed to all error handlers that have been registered against this client.
     *
     * @param equipmentContainer The [EquipmentContainer] to fetch equipment for.
     * @return A [GrpcResult] with a result of one of the following:
     * - When [GrpcResult.wasSuccessful], a map containing the retrieved objects keyed by mRID, accessible via [GrpcResult.value]. If an item was not found, or
     * couldn't be added to [service], it will be excluded from the map and its mRID will be present in [MultiObjectResult.failed] (see [BaseService.add]).
     * - When [GrpcResult.wasFailure], the error that occurred retrieving or processing the the object, accessible via [GrpcResult.thrown].
     * Note the [NetworkConsumerClient] warning in this case.
     */
    fun getEquipmentForContainer(service: NetworkService, equipmentContainer: EquipmentContainer): GrpcResult<MultiObjectResult> =
        getEquipmentForContainer(service, equipmentContainer.mRID)

    /**
     * Retrieve the [Equipment] for the [EquipmentContainer] represented by [mRID]
     *
     * Exceptions that occur during retrieval will be caught and passed to all error handlers that have been registered against this client.
     *
     * @param mRID The mRID of the [EquipmentContainer] to fetch equipment for.
     * @return A [GrpcResult] with a result of one of the following:
     * - When [GrpcResult.wasSuccessful], a map containing the retrieved objects keyed by mRID, accessible via [GrpcResult.value]. If an item was not found, or
     * couldn't be added to [service], it will be excluded from the map and its mRID will be present in [MultiObjectResult.failed] (see [BaseService.add]).
     * - When [GrpcResult.wasFailure], the error that occurred retrieving or processing the the object, accessible via [GrpcResult.thrown].
     * Note the [NetworkConsumerClient] warning in this case.
     */
    fun getEquipmentForContainer(service: NetworkService, mRID: String): GrpcResult<MultiObjectResult> =
        tryRpc { handleMultiObjectRPC(service, mRID, ::processEquipmentContainer) }

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
    fun getEquipmentForRestriction(service: NetworkService, operationalRestriction: OperationalRestriction): GrpcResult<MultiObjectResult> =
        getEquipmentForRestriction(service, operationalRestriction.mRID)

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
    fun getEquipmentForRestriction(service: NetworkService, mRID: String): GrpcResult<MultiObjectResult> =
        tryRpc { handleMultiObjectRPC(service, mRID, ::processRestriction) }

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
    fun getCurrentEquipmentForFeeder(service: NetworkService, feeder: Feeder): GrpcResult<MultiObjectResult> =
        getCurrentEquipmentForFeeder(service, feeder.mRID)

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
    fun getCurrentEquipmentForFeeder(service: NetworkService, mRID: String): GrpcResult<MultiObjectResult> =
        tryRpc { handleMultiObjectRPC(service, mRID, ::processFeeder) }

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
    fun getTerminalsForConnectivityNode(service: NetworkService, connectivityNode: ConnectivityNode): GrpcResult<MultiObjectResult> =
        getTerminalsForConnectivityNode(service, connectivityNode.mRID)

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
    fun getTerminalsForConnectivityNode(service: NetworkService, mRID: String): GrpcResult<MultiObjectResult> =
        tryRpc { handleMultiObjectRPC(service, mRID, ::processConnectivityNode) }

    /***
     * Retrieve the network hierarchy.
     *
     * @return A simplified version of the network hierarchy that can be used to make further in-depth requests.
     */
    fun getNetworkHierarchy(service: NetworkService): GrpcResult<NetworkHierarchy> = tryRpc {
        val response = stub.getNetworkHierarchy(GetNetworkHierarchyRequest.newBuilder().build())

        NetworkHierarchy(
            toMap(response.geographicalRegionsList) { service[it.mRID()] ?: protoToCimProvider(service).addFromPb(it) },
            toMap(response.subGeographicalRegionsList) { service[it.mRID()] ?: protoToCimProvider(service).addFromPb(it) },
            toMap(response.substationsList) { service[it.mRID()] ?: protoToCimProvider(service).addFromPb(it) },
            toMap(response.feedersList) { service[it.mRID()] ?: protoToCimProvider(service).addFromPb(it) },
            toMap(response.circuitsList) { service[it.mRID()] ?: protoToCimProvider(service).addFromPb(it) },
            toMap(response.loopsList) { service[it.mRID()] ?: protoToCimProvider(service).addFromPb(it) }
        )
    }

    /***
     * Retrieve the feeder network for the specified [mRID] and store the results in the [service].
     *
     * This is a convenience method that will fetch the feeder object and all of the equipment referenced by the feeder (normal state), along with
     * all references. This should entail a complete connectivity model for the feeder, however not the connectivity between multiple feeders.
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
            "this.getEquipmentContainer<Feeder>(service, mRID)",
            "com.zepben.evolve.cim.iec61970.base.core.Feeder"
        )
    )
    fun getFeeder(service: NetworkService, mRID: String): GrpcResult<MultiObjectResult> = getEquipmentContainer(service, mRID, Feeder::class.java)

    /***
     * Retrieve the equipment container network for the specified [mRID] and store the results in the [service].
     *
     * This is a convenience method that will fetch the container object and all of the equipment contained, along with all subsequent
     * references. This should entail a complete connectivity model for the container, however not the connectivity between multiple containers.
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
        service: NetworkService,
        mRID: String,
        expectedClass: Class<out EquipmentContainer> = EquipmentContainer::class.java
    ): GrpcResult<MultiObjectResult> = getWithReferences(service, mRID, expectedClass) { it, mor ->
        mor.objects.putAll(getEquipmentForContainer(service, it)
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
     * @return A [GrpcResult] with a result of one of the following:
     * - When [GrpcResult.wasSuccessful], a map containing the retrieved objects keyed by mRID, accessible via [GrpcResult.value]. If an item was not found, or
     * couldn't be added to [service], it will be excluded from the map and its mRID will be present in [MultiObjectResult.failed] (see [BaseService.add]).
     * - When [GrpcResult.wasFailure], the error that occurred retrieving or processing the the object, accessible via [GrpcResult.thrown].
     * Note the [NetworkConsumerClient] warning in this case.
     */
    fun getEquipmentForLoop(service: NetworkService, loop: Loop): GrpcResult<MultiObjectResult> =
        getEquipmentForLoop(service, loop.mRID)

    /**
     * Retrieve the [Equipment] for the [Loop] represented by [mRID]
     *
     * Exceptions that occur during retrieval will be caught and passed to all error handlers that have been registered against this client.
     *
     * @param mRID The mRID of the [Loop] to fetch equipment for.
     * @return A [GrpcResult] with a result of one of the following:
     * - When [GrpcResult.wasSuccessful], a map containing the retrieved objects keyed by mRID, accessible via [GrpcResult.value]. If an item was not found, or
     * couldn't be added to [service], it will be excluded from the map and its mRID will be present in [MultiObjectResult.failed] (see [BaseService.add]).
     * - When [GrpcResult.wasFailure], the error that occurred retrieving or processing the the object, accessible via [GrpcResult.thrown].
     * Note the [NetworkConsumerClient] warning in this case.
     */
    fun getEquipmentForLoop(service: NetworkService, mRID: String): GrpcResult<MultiObjectResult> =
        getWithReferences(service, mRID, Loop::class.java) { loop, mor ->
            resolveReferences(service, mor)?.let { return@getWithReferences it }
            (loop.circuits + loop.substations + loop.energizingSubstations).forEach {
                mor.objects.putAll(getEquipmentForContainer(service, it.mRID)
                    .onError { thrown, wasHandled -> return@getWithReferences GrpcResult.ofError(thrown, wasHandled) }
                    .value.objects
                )
            }
            null
        }

    /**
     * Retrieve the [Equipment] for all [Loop]s
     *
     * Exceptions that occur during retrieval will be caught and passed to all error handlers that have been registered against this client.
     *
     * @return A [GrpcResult] with a result of one of the following:
     * - When [GrpcResult.wasSuccessful], a map containing the retrieved objects keyed by mRID, accessible via [GrpcResult.value]. If an item was not found, or
     * couldn't be added to [service], it will be excluded from the map and its mRID will be present in [MultiObjectResult.failed] (see [BaseService.add]).
     * - When [GrpcResult.wasFailure], the error that occurred retrieving or processing the the object, accessible via [GrpcResult.thrown].
     * Note the [NetworkConsumerClient] warning in this case.
     */
    fun getAllLoops(service: NetworkService): GrpcResult<MultiObjectResult> {
        val response = getNetworkHierarchy(service)
        val hierarchy = response.onError { thrown, wasHandled -> return GrpcResult.ofError(thrown, wasHandled) }.value

        val mor = MultiObjectResult()
        mor.objects.putAll(hierarchy.geographicalRegions)
        mor.objects.putAll(hierarchy.subGeographicalRegions)
        mor.objects.putAll(hierarchy.substations)
        mor.objects.putAll(hierarchy.feeders)
        mor.objects.putAll(hierarchy.circuits)
        mor.objects.putAll(hierarchy.loops)

        resolveReferences(service, mor)?.let { return it }

        hierarchy.loops.values
            .flatMap { it.circuits + it.substations + it.energizingSubstations }
            .toSet()
            .forEach {
                mor.objects.putAll(getEquipmentForContainer(service, it.mRID)
                    .onError { thrown, wasHandled -> return GrpcResult.ofError(thrown, wasHandled) }
                    .value.objects
                )
            }

        resolveReferences(service, mor)?.let { return it }

        return GrpcResult(mor)
    }

    private fun getUnresolvedMRIDs(service: NetworkService, mRIDs: Set<String>): Set<String> =
        mRIDs.flatMap { service.getUnresolvedReferencesFrom(it) }
            .map { it.toMrid }
            .toSet()

    private fun processEquipmentContainer(service: NetworkService, mRID: String): Sequence<ExtractResult> =
        stub.getEquipmentForContainer(GetEquipmentForContainerRequest.newBuilder().setMrid(mRID).build())
            .asSequence()
            .map { extractIdentifiedObject(service, it.identifiedObject) }

    private fun processFeeder(service: NetworkService, mRID: String): Sequence<ExtractResult> =
        stub.getCurrentEquipmentForFeeder(GetCurrentEquipmentForFeederRequest.newBuilder().setMrid(mRID).build())
            .asSequence()
            .map { extractIdentifiedObject(service, it.identifiedObject) }

    private fun processRestriction(service: NetworkService, mRID: String): Sequence<ExtractResult> =
        stub.getEquipmentForRestriction(GetEquipmentForRestrictionRequest.newBuilder().setMrid(mRID).build())
            .asSequence()
            .map { extractIdentifiedObject(service, it.identifiedObject) }

    private fun processConnectivityNode(service: NetworkService, mRID: String): Sequence<ExtractResult> =
        stub.getTerminalsForNode(GetTerminalsForNodeRequest.newBuilder().setMrid(mRID).build())
            .asSequence()
            .map { response -> extractResult(service, response.terminal.mRID()) { it.addFromPb(response.terminal) } }

    private fun processIdentifiedObjects(service: NetworkService, mRIDs: Set<String>): Sequence<ExtractResult> {
        if (mRIDs.isEmpty())
            return emptySequence()

        val toFetch = mutableSetOf<String>()
        val existing = mutableSetOf<ExtractResult>()
        mRIDs.forEach { mRID ->  // Only process mRIDs not already present in service
            service.get<IdentifiedObject>(mRID)?.let { existing.add(ExtractResult(it, it.mRID)) } ?: toFetch.add(mRID)
        }

        if (toFetch.isEmpty())
            return existing.asSequence()

        return stub.getIdentifiedObjects(GetIdentifiedObjectsRequest.newBuilder().addAllMrids(toFetch).build())
            .asSequence()
            .map { extractIdentifiedObject(service, it.identifiedObject) } + existing
    }

    private fun extractIdentifiedObject(service: NetworkService, io: NetworkIdentifiedObject): ExtractResult {
        return when (io.identifiedObjectCase) {
            BATTERYUNIT -> extractResult(service, io.batteryUnit.mRID()) { it.addFromPb(io.batteryUnit) }
            PHOTOVOLTAICUNIT -> extractResult(service, io.photoVoltaicUnit.mRID()) { it.addFromPb(io.photoVoltaicUnit) }
            POWERELECTRONICSWINDUNIT -> extractResult(service, io.powerElectronicsWindUnit.mRID()) { it.addFromPb(io.powerElectronicsWindUnit) }
            CABLEINFO -> extractResult(service, io.cableInfo.mRID()) { it.addFromPb(io.cableInfo) }
            OVERHEADWIREINFO -> extractResult(service, io.overheadWireInfo.mRID()) { it.addFromPb(io.overheadWireInfo) }
            POWERTRANSFORMERINFO -> extractResult(service, io.powerTransformerInfo.mRID()) { it.addFromPb(io.powerTransformerInfo) }
            ASSETOWNER -> extractResult(service, io.assetOwner.mRID()) { it.addFromPb(io.assetOwner) }
            ORGANISATION -> extractResult(service, io.organisation.mRID()) { it.addFromPb(io.organisation) }
            LOCATION -> extractResult(service, io.location.mRID()) { it.addFromPb(io.location) }
            METER -> extractResult(service, io.meter.mRID()) { it.addFromPb(io.meter) }
            USAGEPOINT -> extractResult(service, io.usagePoint.mRID()) { it.addFromPb(io.usagePoint) }
            OPERATIONALRESTRICTION -> extractResult(service, io.operationalRestriction.mRID()) { it.addFromPb(io.operationalRestriction) }
            FAULTINDICATOR -> extractResult(service, io.faultIndicator.mRID()) { it.addFromPb(io.faultIndicator) }
            BASEVOLTAGE -> extractResult(service, io.baseVoltage.mRID()) { it.addFromPb(io.baseVoltage) }
            CONNECTIVITYNODE -> extractResult(service, io.connectivityNode.mRID()) { it.addFromPb(io.connectivityNode) }
            FEEDER -> extractResult(service, io.feeder.mRID()) { it.addFromPb(io.feeder) }
            GEOGRAPHICALREGION -> extractResult(service, io.geographicalRegion.mRID()) { it.addFromPb(io.geographicalRegion) }
            SITE -> extractResult(service, io.site.mRID()) { it.addFromPb(io.site) }
            SUBGEOGRAPHICALREGION -> extractResult(service, io.subGeographicalRegion.mRID()) { it.addFromPb(io.subGeographicalRegion) }
            SUBSTATION -> extractResult(service, io.substation.mRID()) { it.addFromPb(io.substation) }
            TERMINAL -> extractResult(service, io.terminal.mRID()) { it.addFromPb(io.terminal) }
            ACLINESEGMENT -> extractResult(service, io.acLineSegment.mRID()) { it.addFromPb(io.acLineSegment) }
            BREAKER -> extractResult(service, io.breaker.mRID()) { it.addFromPb(io.breaker) }
            LOADBREAKSWITCH -> extractResult(service, io.loadBreakSwitch.mRID()) { it.addFromPb(io.loadBreakSwitch) }
            DISCONNECTOR -> extractResult(service, io.disconnector.mRID()) { it.addFromPb(io.disconnector) }
            ENERGYCONSUMER -> extractResult(service, io.energyConsumer.mRID()) { it.addFromPb(io.energyConsumer) }
            ENERGYCONSUMERPHASE -> extractResult(service, io.energyConsumerPhase.mRID()) { it.addFromPb(io.energyConsumerPhase) }
            ENERGYSOURCE -> extractResult(service, io.energySource.mRID()) { it.addFromPb(io.energySource) }
            ENERGYSOURCEPHASE -> extractResult(service, io.energySourcePhase.mRID()) { it.addFromPb(io.energySourcePhase) }
            FUSE -> extractResult(service, io.fuse.mRID()) { it.addFromPb(io.fuse) }
            JUMPER -> extractResult(service, io.jumper.mRID()) { it.addFromPb(io.jumper) }
            JUNCTION -> extractResult(service, io.junction.mRID()) { it.addFromPb(io.junction) }
            LINEARSHUNTCOMPENSATOR -> extractResult(service, io.linearShuntCompensator.mRID()) { it.addFromPb(io.linearShuntCompensator) }
            PERLENGTHSEQUENCEIMPEDANCE -> extractResult(service, io.perLengthSequenceImpedance.mRID()) { it.addFromPb(io.perLengthSequenceImpedance) }
            POWERELECTRONICSCONNECTION -> extractResult(service, io.powerElectronicsConnection.mRID()) { it.addFromPb(io.powerElectronicsConnection) }
            POWERELECTRONICSCONNECTIONPHASE -> extractResult(
                service,
                io.powerElectronicsConnectionPhase.mRID()
            ) { it.addFromPb(io.powerElectronicsConnectionPhase) }
            POWERTRANSFORMER -> extractResult(service, io.powerTransformer.mRID()) { it.addFromPb(io.powerTransformer) }
            POWERTRANSFORMEREND -> extractResult(service, io.powerTransformerEnd.mRID()) { it.addFromPb(io.powerTransformerEnd) }
            RATIOTAPCHANGER -> extractResult(service, io.ratioTapChanger.mRID()) { it.addFromPb(io.ratioTapChanger) }
            RECLOSER -> extractResult(service, io.recloser.mRID()) { it.addFromPb(io.recloser) }
            BUSBARSECTION -> extractResult(service, io.busbarSection.mRID()) { it.addFromPb(io.busbarSection) }
            CIRCUIT -> extractResult(service, io.circuit.mRID()) { it.addFromPb(io.circuit) }
            LOOP -> extractResult(service, io.loop.mRID()) { it.addFromPb(io.loop) }
            POLE -> extractResult(service, io.pole.mRID()) { it.addFromPb(io.pole) }
            STREETLIGHT -> extractResult(service, io.streetlight.mRID()) { it.addFromPb(io.streetlight) }
            ACCUMULATOR -> extractResult(service, io.accumulator.measurement.mRID()) { it.addFromPb(io.accumulator) }
            ANALOG -> extractResult(service, io.analog.measurement.mRID()) { it.addFromPb(io.analog) }
            DISCRETE -> extractResult(service, io.discrete.measurement.mRID()) { it.addFromPb(io.discrete) }
            CONTROL -> extractResult(service, io.control.mRID()) { it.addFromPb(io.control) }
            REMOTECONTROL -> extractResult(service, io.remoteControl.mRID()) { it.addFromPb(io.remoteControl) }
            REMOTESOURCE -> extractResult(service, io.remoteSource.mRID()) { it.addFromPb(io.remoteSource) }
            TRANSFORMERSTARIMPEDANCE -> extractResult(service, io.transformerStarImpedance.mRID()) { it.addFromPb(io.transformerStarImpedance) }
            TRANSFORMERENDINFO -> extractResult(service, io.transformerEndInfo.mRID()) { it.addFromPb(io.transformerEndInfo) }
            TRANSFORMERTANKINFO -> extractResult(service, io.transformerTankInfo.mRID()) { it.addFromPb(io.transformerTankInfo) }
            OTHER, IDENTIFIEDOBJECT_NOT_SET, null -> throw UnsupportedOperationException(
                "Identified object type ${io.identifiedObjectCase} is not supported by the network service"
            )
        }
    }

    private inline fun <reified CIM : IdentifiedObject> extractResult(
        service: NetworkService,
        mRID: String,
        addFromPb: (NetworkProtoToCim) -> CIM?
    ): ExtractResult =
        ExtractResult(service[mRID] ?: addFromPb(protoToCimProvider(service)), mRID)

    private fun <T, U : IdentifiedObject> toMap(objects: Iterable<T>, mapper: (T) -> U?): Map<String, U> = objects
        .mapNotNull(mapper)
        .associateBy { it.mRID }

    private fun handleMultiObjectRPC(service: NetworkService, mRID: String, rpc: (NetworkService, String) -> Sequence<ExtractResult>): MultiObjectResult =
        rpc(service, mRID).let { extracted ->
            val results = mutableMapOf<String, IdentifiedObject>()
            val failed = mutableSetOf<String>()
            extracted.forEach {
                if (it.identifiedObject == null) failed.add(it.mRID) else results[it.identifiedObject.mRID] = it.identifiedObject
            }
            MultiObjectResult(results, failed)
        }

    private inline fun <reified T : IdentifiedObject> getWithReferences(
        service: NetworkService,
        mRID: String,
        expectedClass: Class<out T>,
        getAdditional: (T, MultiObjectResult) -> GrpcResult<MultiObjectResult>?
    ): GrpcResult<MultiObjectResult> {
        val response = getIdentifiedObject(service, mRID)
        val identifiedObject = response.onError { thrown, wasHandled -> return@getWithReferences GrpcResult.ofError(thrown, wasHandled) }.value

        if (!expectedClass.isInstance(identifiedObject)) {
            val e = ClassCastException("Unable to extract ${expectedClass.simpleName} network from ${identifiedObject.typeNameAndMRID()}.")
            return GrpcResult.ofError(e, tryHandleError(e))
        }

        val mor = MultiObjectResult()
        mor.objects[identifiedObject.mRID] = identifiedObject

        getAdditional(expectedClass.cast(identifiedObject), mor)?.let { return@getWithReferences it }
        resolveReferences(service, mor)?.let { return@getWithReferences it }

        return GrpcResult(mor)
    }

    private fun resolveReferences(service: NetworkService, mor: MultiObjectResult): GrpcResult<MultiObjectResult>? {
        var res = mor
        do {
            val toResolve = getUnresolvedMRIDs(service, res.objects.keys)
            res = getIdentifiedObjects(service, toResolve).onError { thrown, wasHandled ->
                return GrpcResult.ofError(thrown, wasHandled)
            }.value
            mor.objects.putAll(res.objects)
        } while (res.objects.isNotEmpty())
        return null
    }

}

inline fun <reified T : EquipmentContainer> NetworkConsumerClient.getEquipmentContainer(service: NetworkService, mRID: String): GrpcResult<MultiObjectResult> {
    return getEquipmentContainer(service, mRID, T::class.java)
}

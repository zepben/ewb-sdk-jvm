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
import com.zepben.evolve.services.network.translator.addFromPb
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
     * - The item if found
     * - null if an object could not be found or it was found but not added to [service] (see [BaseService.add]).
     * - A [Throwable] if an error occurred while retrieving or processing the object, in which case, [GrpcResult.wasSuccessful] will return false.
     */
    override fun getIdentifiedObject(service: NetworkService, mRID: String): GrpcResult<IdentifiedObject?> = tryRpc {
        processIdentifiedObjects(service, setOf(mRID)).firstOrNull()?.identifiedObject
    }

    /**
     * Retrieve the objects with the given [mRIDs] and store the results in the [service].
     *
     * Exceptions that occur during processing will be caught and passed to all error handlers that have been registered by [addErrorHandler].
     *
     * WARNING: This operation is not atomic upon [service], and thus if processing fails partway through [mRIDs], any previously successful mRID will have been
     * added to the service, and thus you may have an incomplete [NetworkService]. Also note that adding to the [service] may not occur for an object if another
     * object with the same mRID is already present in [service]. [MultiObjectResult.failed] can be used to check for mRIDs that were retrieved but not
     * added to [service].
     *
     * @return A [GrpcResult] with a result of one of the following:
     * - A [MultiObjectResult] containing a map of the retrieved objects keyed by mRID. If an item is not found it will be excluded from the map.
     *   If an item couldn't be added to [service], its mRID will be present in [MultiObjectResult.failed] (see [BaseService.add]).
     * - A [Throwable] if an error occurred while retrieving or processing the objects, in which case, [GrpcResult.wasSuccessful] will return false.
     *   Note the warning above in this case.
     */
    override fun getIdentifiedObjects(service: NetworkService, mRIDs: Iterable<String>): GrpcResult<MultiObjectResult> = tryRpc {
        processIdentifiedObjects(service, mRIDs.toSet()).let { extracted ->
            val objects = mutableMapOf<String, IdentifiedObject>()
            val failed = mutableSetOf<String>()
            extracted.forEach { result -> result.identifiedObject?.let { objects[it.mRID] = it } ?: failed.add(result.mRID) }
            MultiObjectResult(objects, failed)
        }
    }

    /**
     * Retrieve the [Equipment] for [equipmentContainer]
     *
     * Exceptions that occur during retrieval will be caught and passed to all error handlers that have been registered against this client.
     *
     * @param equipmentContainer The [EquipmentContainer] to fetch equipment for.
     * @return A [GrpcResult] with a result of one of the following:
     * - A [MultiObjectResult] containing a map of the retrieved objects keyed by mRID. If an item is not found it will be excluded from the map.
     *   If an item couldn't be added to [service] its mRID will be present in [MultiObjectResult.failed] (see [BaseService.add]).
     * - An [Exception] if an error occurred while retrieving or processing the objects, in which case, [GrpcResult.wasSuccessful] will return false.
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
     * - A [MultiObjectResult] containing a map of the retrieved objects keyed by mRID. If an item is not found it will be excluded from the map.
     *   If an item couldn't be added to [service] its mRID will be present in [MultiObjectResult.failed] (see [BaseService.add]).
     * - An [Exception] if an error occurred while retrieving or processing the objects, in which case, [GrpcResult.wasSuccessful] will return false.
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
     * - A [MultiObjectResult] containing a map of the retrieved objects keyed by mRID. If an item is not found it will be excluded from the map.
     *   If an item couldn't be added to [service] its mRID will be present in [MultiObjectResult.failed] (see [BaseService.add]).
     * - An [Exception] if an error occurred while retrieving or processing the objects, in which case, [GrpcResult.wasSuccessful] will return false.
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
     * - A [MultiObjectResult] containing a map of the retrieved objects keyed by mRID. If an item is not found it will be excluded from the map.
     *   If an item couldn't be added to [service] its mRID will be present in [MultiObjectResult.failed] (see [BaseService.add]).
     * - An [Exception] if an error occurred while retrieving or processing the objects, in which case, [GrpcResult.wasSuccessful] will return false.
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
     * - A [MultiObjectResult] containing a map of the retrieved objects keyed by mRID. If an item is not found it will be excluded from the map.
     *   If an item couldn't be added to [service] its mRID will be present in [MultiObjectResult.failed] (see [BaseService.add]).
     * - An [Exception] if an error occurred while retrieving or processing the objects, in which case, [GrpcResult.wasSuccessful] will return false.
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
     * - A [MultiObjectResult] containing a map of the retrieved objects keyed by mRID. If an item is not found it will be excluded from the map.
     *   If an item couldn't be added to [service] its mRID will be present in [MultiObjectResult.failed] (see [BaseService.add]).
     * - An [Exception] if an error occurred while retrieving or processing the objects, in which case, [GrpcResult.wasSuccessful] will return false.
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
     * - A [MultiObjectResult] containing a map of the retrieved objects keyed by mRID. If an item is not found it will be excluded from the map.
     *   If an item couldn't be added to [service] its mRID will be present in [MultiObjectResult.failed] (see [BaseService.add]).
     * - An [Exception] if an error occurred while retrieving or processing the objects, in which case, [GrpcResult.wasSuccessful] will return false.
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
     * - A [MultiObjectResult] containing a map of the retrieved objects keyed by mRID. If an item is not found it will be excluded from the map.
     *   If an item couldn't be added to [service] its mRID will be present in [MultiObjectResult.failed] (see [BaseService.add]).
     * - An [Exception] if an error occurred while retrieving or processing the objects, in which case, [GrpcResult.wasSuccessful] will return false.
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
            toMap(response.geographicalRegionsList) { service.addFromPb(it) ?: service[it.mRID()] },
            toMap(response.subGeographicalRegionsList) { service.addFromPb(it) ?: service[it.mRID()] },
            toMap(response.substationsList) { service.addFromPb(it) ?: service[it.mRID()] },
            toMap(response.feedersList) { service.addFromPb(it) ?: service[it.mRID()] },
            toMap(response.circuitsList) { service.addFromPb(it) ?: service[it.mRID()] },
            toMap(response.loopsList) { service.addFromPb(it) ?: service[it.mRID()] }
        )
    }

    /***
     * Retrieve the feeder network for the specified [mRID] and store the results in the [service].
     *
     * This is a convenience method that will fetch the feeder object and all of the equipment referenced by the feeder (normal state), along with
     * all references. This should entail a complete connectivity model for the feeder, however not the connectivity between multiple feeders.
     *
     * @return A GrpcResult of a [MultiObjectResult], containing a map keyed by mRID of all the objects retrieved as part of retrieving the [Feeder] and the
     * [Feeder] itself. If an item couldn't be added to [service], its mRID will be present in [MultiObjectResult.failed].
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
     * @return A GrpcResult of a [MultiObjectResult], containing a map keyed by mRID of all the objects retrieved. If an item couldn't be added to
     * [service], its mRID will be present in [MultiObjectResult.failed].
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
     * - A [MultiObjectResult] containing a map of the retrieved objects keyed by mRID. If an item is not found it will be excluded from the map.
     *   If an item couldn't be added to [service] its mRID will be present in [MultiObjectResult.failed] (see [BaseService.add]).
     * - An [Exception] if an error occurred while retrieving or processing the objects, in which case, [GrpcResult.wasSuccessful] will return false.
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
     * - A [MultiObjectResult] containing a map of the retrieved objects keyed by mRID. If an item is not found it will be excluded from the map.
     *   If an item couldn't be added to [service] its mRID will be present in [MultiObjectResult.failed] (see [BaseService.add]).
     * - An [Exception] if an error occurred while retrieving or processing the objects, in which case, [GrpcResult.wasSuccessful] will return false.
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
     * - A [MultiObjectResult] containing a map of the retrieved objects keyed by mRID. If an item is not found it will be excluded from the map.
     *   If an item couldn't be added to [service] its mRID will be present in [MultiObjectResult.failed] (see [BaseService.add]).
     * - An [Exception] if an error occurred while retrieving or processing the objects, in which case, [GrpcResult.wasSuccessful] will return false.
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
            .map { ExtractResult(protoToCimProvider(service).addFromPb(it.terminal), it.terminal.mRID()) }

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

    private fun extractIdentifiedObject(service: NetworkService, it: NetworkIdentifiedObject): ExtractResult {
        return when (it.identifiedObjectCase) {
            BATTERYUNIT -> ExtractResult(protoToCimProvider(service).addFromPb(it.batteryUnit), it.batteryUnit.mRID())
            PHOTOVOLTAICUNIT -> ExtractResult(protoToCimProvider(service).addFromPb(it.photoVoltaicUnit), it.photoVoltaicUnit.mRID())
            POWERELECTRONICSWINDUNIT -> ExtractResult(protoToCimProvider(service).addFromPb(it.powerElectronicsWindUnit), it.powerElectronicsWindUnit.mRID())
            CABLEINFO -> ExtractResult(protoToCimProvider(service).addFromPb(it.cableInfo), it.cableInfo.mRID())
            OVERHEADWIREINFO -> ExtractResult(protoToCimProvider(service).addFromPb(it.overheadWireInfo), it.overheadWireInfo.mRID())
            POWERTRANSFORMERINFO -> ExtractResult(protoToCimProvider(service).addFromPb(it.powerTransformerInfo), it.powerTransformerInfo.mRID())
            ASSETOWNER -> ExtractResult(protoToCimProvider(service).addFromPb(it.assetOwner), it.assetOwner.mRID())
            ORGANISATION -> ExtractResult(protoToCimProvider(service).addFromPb(it.organisation), it.organisation.mRID())
            LOCATION -> ExtractResult(protoToCimProvider(service).addFromPb(it.location), it.location.mRID())
            METER -> ExtractResult(protoToCimProvider(service).addFromPb(it.meter), it.meter.mRID())
            USAGEPOINT -> ExtractResult(protoToCimProvider(service).addFromPb(it.usagePoint), it.usagePoint.mRID())
            OPERATIONALRESTRICTION -> ExtractResult(protoToCimProvider(service).addFromPb(it.operationalRestriction), it.operationalRestriction.mRID())
            FAULTINDICATOR -> ExtractResult(protoToCimProvider(service).addFromPb(it.faultIndicator), it.faultIndicator.mRID())
            BASEVOLTAGE -> ExtractResult(protoToCimProvider(service).addFromPb(it.baseVoltage), it.baseVoltage.mRID())
            CONNECTIVITYNODE -> ExtractResult(protoToCimProvider(service).addFromPb(it.connectivityNode), it.connectivityNode.mRID())
            FEEDER -> ExtractResult(protoToCimProvider(service).addFromPb(it.feeder), it.feeder.mRID())
            GEOGRAPHICALREGION -> ExtractResult(protoToCimProvider(service).addFromPb(it.geographicalRegion), it.geographicalRegion.mRID())
            SITE -> ExtractResult(protoToCimProvider(service).addFromPb(it.site), it.site.mRID())
            SUBGEOGRAPHICALREGION -> ExtractResult(protoToCimProvider(service).addFromPb(it.subGeographicalRegion), it.subGeographicalRegion.mRID())
            SUBSTATION -> ExtractResult(protoToCimProvider(service).addFromPb(it.substation), it.substation.mRID())
            TERMINAL -> ExtractResult(protoToCimProvider(service).addFromPb(it.terminal), it.terminal.mRID())
            ACLINESEGMENT -> ExtractResult(protoToCimProvider(service).addFromPb(it.acLineSegment), it.acLineSegment.mRID())
            BREAKER -> ExtractResult(protoToCimProvider(service).addFromPb(it.breaker), it.breaker.mRID())
            LOADBREAKSWITCH -> ExtractResult(protoToCimProvider(service).addFromPb(it.loadBreakSwitch), it.loadBreakSwitch.mRID())
            DISCONNECTOR -> ExtractResult(protoToCimProvider(service).addFromPb(it.disconnector), it.disconnector.mRID())
            ENERGYCONSUMER -> ExtractResult(protoToCimProvider(service).addFromPb(it.energyConsumer), it.energyConsumer.mRID())
            ENERGYCONSUMERPHASE -> ExtractResult(protoToCimProvider(service).addFromPb(it.energyConsumerPhase), it.energyConsumerPhase.mRID())
            ENERGYSOURCE -> ExtractResult(protoToCimProvider(service).addFromPb(it.energySource), it.energySource.mRID())
            ENERGYSOURCEPHASE -> ExtractResult(protoToCimProvider(service).addFromPb(it.energySourcePhase), it.energySourcePhase.mRID())
            FUSE -> ExtractResult(protoToCimProvider(service).addFromPb(it.fuse), it.fuse.mRID())
            JUMPER -> ExtractResult(protoToCimProvider(service).addFromPb(it.jumper), it.jumper.mRID())
            JUNCTION -> ExtractResult(protoToCimProvider(service).addFromPb(it.junction), it.junction.mRID())
            LINEARSHUNTCOMPENSATOR -> ExtractResult(protoToCimProvider(service).addFromPb(it.linearShuntCompensator), it.linearShuntCompensator.mRID())
            PERLENGTHSEQUENCEIMPEDANCE -> ExtractResult(
                protoToCimProvider(service).addFromPb(it.perLengthSequenceImpedance),
                it.perLengthSequenceImpedance.mRID()
            )
            POWERELECTRONICSCONNECTION -> ExtractResult(
                protoToCimProvider(service).addFromPb(it.powerElectronicsConnection),
                it.powerElectronicsConnection.mRID()
            )
            POWERELECTRONICSCONNECTIONPHASE -> ExtractResult(
                protoToCimProvider(service).addFromPb(it.powerElectronicsConnectionPhase),
                it.powerElectronicsConnectionPhase.mRID()
            )
            POWERTRANSFORMER -> ExtractResult(protoToCimProvider(service).addFromPb(it.powerTransformer), it.powerTransformer.mRID())
            POWERTRANSFORMEREND -> ExtractResult(protoToCimProvider(service).addFromPb(it.powerTransformerEnd), it.powerTransformerEnd.mRID())
            RATIOTAPCHANGER -> ExtractResult(protoToCimProvider(service).addFromPb(it.ratioTapChanger), it.ratioTapChanger.mRID())
            RECLOSER -> ExtractResult(protoToCimProvider(service).addFromPb(it.recloser), it.recloser.mRID())
            BUSBARSECTION -> ExtractResult(protoToCimProvider(service).addFromPb(it.busbarSection), it.busbarSection.mRID())
            CIRCUIT -> ExtractResult(protoToCimProvider(service).addFromPb(it.circuit), it.circuit.mRID())
            LOOP -> ExtractResult(protoToCimProvider(service).addFromPb(it.loop), it.loop.mRID())
            POLE -> ExtractResult(protoToCimProvider(service).addFromPb(it.pole), it.pole.mRID())
            STREETLIGHT -> ExtractResult(protoToCimProvider(service).addFromPb(it.streetlight), it.streetlight.mRID())
            ACCUMULATOR -> ExtractResult(protoToCimProvider(service).addFromPb(it.accumulator), it.accumulator.measurement.mRID())
            ANALOG -> ExtractResult(protoToCimProvider(service).addFromPb(it.analog), it.analog.measurement.mRID())
            DISCRETE -> ExtractResult(protoToCimProvider(service).addFromPb(it.discrete), it.discrete.measurement.mRID())
            CONTROL -> ExtractResult(protoToCimProvider(service).addFromPb(it.control), it.control.mRID())
            REMOTECONTROL -> ExtractResult(protoToCimProvider(service).addFromPb(it.remoteControl), it.remoteControl.mRID())
            REMOTESOURCE -> ExtractResult(protoToCimProvider(service).addFromPb(it.remoteSource), it.remoteSource.mRID())
            TRANSFORMERSTARIMPEDANCE -> ExtractResult(protoToCimProvider(service).addFromPb(it.transformerStarImpedance), it.transformerStarImpedance.mRID())
            TRANSFORMERENDINFO -> ExtractResult(protoToCimProvider(service).addFromPb(it.transformerEndInfo), it.transformerEndInfo.mRID())
            TRANSFORMERTANKINFO -> ExtractResult(protoToCimProvider(service).addFromPb(it.transformerTankInfo), it.transformerTankInfo.mRID())
            OTHER, IDENTIFIEDOBJECT_NOT_SET, null -> throw UnsupportedOperationException(
                "Identified object type ${it.identifiedObjectCase} is not supported by the network service"
            )
        }
    }

    private fun <T, U : IdentifiedObject> toMap(objects: Iterable<T>, mapper: (T) -> U?): Map<String, U> = objects
        .mapNotNull(mapper)
        .associateBy { it.mRID }

    private fun handleMultiObjectRPC(
        service: NetworkService,
        mRID: String,
        rpc: (NetworkService, String) -> Sequence<ExtractResult>,
    ): MultiObjectResult {

        return rpc(service, mRID).let { extracted ->
            val results = mutableMapOf<String, IdentifiedObject>()
            val failed = mutableSetOf<String>()
            extracted.forEach {
                if (it.identifiedObject == null) failed.add(it.mRID) else results[it.identifiedObject.mRID] = it.identifiedObject
            }
            MultiObjectResult(results, failed)
        }
    }

    private inline fun <reified T : IdentifiedObject> getWithReferences(
        service: NetworkService,
        mRID: String,
        expectedClass: Class<out T>,
        getAdditional: (T, MultiObjectResult) -> GrpcResult<MultiObjectResult>?
    ): GrpcResult<MultiObjectResult> {
        val response = getIdentifiedObject(service, mRID)
        val identifiedObject = response.onError { thrown, wasHandled -> return@getWithReferences GrpcResult.ofError(thrown, wasHandled) }.value

        if (identifiedObject == null)
            return GrpcResult.of(null)
        else if (identifiedObject !is T)
            return GrpcResult.ofError(
                ClassCastException("Unable to extract ${T::class.simpleName} network from ${identifiedObject.typeNameAndMRID()}."),
                false
            )

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
                return GrpcResult.ofError(
                    thrown,
                    wasHandled
                )
            }.value
            mor.objects.putAll(res.objects)
        } while (res.objects.isNotEmpty())
        return null
    }

}

inline fun <reified T : EquipmentContainer> NetworkConsumerClient.getEquipmentContainer(service: NetworkService, mRID: String): GrpcResult<MultiObjectResult> {
    return getEquipmentContainer(service, mRID, T::class.java)
}

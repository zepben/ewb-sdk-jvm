/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.get

import com.zepben.cimbend.cim.iec61970.base.core.ConductingEquipment
import com.zepben.cimbend.cim.iec61970.base.core.Feeder
import com.zepben.cimbend.cim.iec61970.base.core.IdentifiedObject
import com.zepben.cimbend.cim.iec61970.base.wires.AcLineSegment
import com.zepben.cimbend.cim.iec61970.base.wires.Conductor
import com.zepben.cimbend.common.Resolvers
import com.zepben.cimbend.common.extensions.typeNameAndMRID
import com.zepben.cimbend.get.hierarchy.*
import com.zepben.cimbend.grpc.GrpcResult
import com.zepben.cimbend.network.NetworkService
import com.zepben.cimbend.network.model.NetworkProtoToCim
import com.zepben.protobuf.nc.GetIdentifiedObjectsRequest
import com.zepben.protobuf.nc.GetNetworkHierarchyRequest
import com.zepben.protobuf.nc.NetworkConsumerGrpc
import com.zepben.protobuf.nc.NetworkIdentifiedObject
import com.zepben.protobuf.nc.NetworkIdentifiedObject.IdentifiedObjectCase.*
import io.grpc.Channel

/**
 * Consumer client for a [NetworkService].
 *
 * @property stub The gRPC stub to be used to communicate with the server
 */
class NetworkConsumerClient(
    private val stub: NetworkConsumerGrpc.NetworkConsumerBlockingStub,
    private val protoToCimProvider: (NetworkService) -> NetworkProtoToCim = { NetworkProtoToCim(it) }
) : CimConsumerClient<NetworkService>() {

    constructor(channel: Channel) : this(NetworkConsumerGrpc.newBlockingStub(channel))

    /**
     * Retrieve the object with the given [mRID] and store the result in the [service].
     *
     * Exceptions that occur during sending will be caught and passed to all error handlers that have been registered by
     * [addErrorHandler]. If none of the registered error handlers return true to indicate the error has been handled,
     * the exception will be rethrown.
     *
     * @return The item if found, otherwise null.
     */
    override fun getIdentifiedObject(service: NetworkService, mRID: String): GrpcResult<IdentifiedObject> {
        return safeTryRpc {
            GrpcResult.of(
                processIdentifiedObjects(service, GetIdentifiedObjectsRequest.newBuilder().addMrids(mRID).build())
                    .firstOrNull()
            )
        }
    }

    /**
     * Retrieve the objects with the given [mRIDs] and store the results in the [service].
     *
     * Exceptions that occur during sending will be caught and passed to all error handlers that have been registered by
     * [addErrorHandler]. If none of the registered error handlers return true to indicate the error has been handled,
     * the exception will be rethrown.
     *
     * @return A [Map] containing the retrieved objects keyed by mRID. If an item is not found it will be excluded from the map.
     */
    override fun getIdentifiedObjects(service: NetworkService, mRIDs: Iterable<String>): GrpcResult<Map<String, IdentifiedObject>> {
        return safeTryRpc {
            GrpcResult.of(
                processIdentifiedObjects(service, GetIdentifiedObjectsRequest.newBuilder().addAllMrids(mRIDs).build())
                    .filterNotNull()
                    .associateBy({ it.mRID }, { it })
            )
        }
    }

    /***
     * Retrieve the network hierarchy.
     *
     * @return A simplified version of the network hierarchy that can be used to make further in-depth requests.
     */
    fun getNetworkHierarchy(): GrpcResult<NetworkHierarchy> {
        return safeTryRpc {
            val response = stub.getNetworkHierarchy(GetNetworkHierarchyRequest.newBuilder().build())

            val feeders = toMap(response.feedersList) { NetworkHierarchyFeeder(it.mrid, it.name) }
            val substations = toMap(response.substationsList) { NetworkHierarchySubstation(it.mrid, it.name, lookup(it.feederMridsList, feeders)) }
            val subGeographicalRegions = toMap(response.subGeographicalRegionsList) { NetworkHierarchySubGeographicalRegion(it.mrid, it.name, lookup(it.substationMridsList, substations)) }
            val geographicalRegions = toMap(response.geographicalRegionsList) { NetworkHierarchyGeographicalRegion(it.mrid, it.name, lookup(it.subGeographicalRegionMridsList, subGeographicalRegions)) }

            finaliseLinks(geographicalRegions, subGeographicalRegions, substations)
            GrpcResult.of(NetworkHierarchy(geographicalRegions, subGeographicalRegions, substations, feeders))
        }
    }

    /***
     * Retrieve the feeder network for the specified [mRID] and store the results in the [service].
     *
     * This is a convenience method that will fetch the feeder object, all of the equipment referenced by the feeder (normal state),
     * the terminals of all elements, the connectivity between terminals, the locations of all elements, the ends of all transformers
     * and the wire info for all conductors.
     *
     * @return The [Feeder], or null if it was not found.
     */
    fun getFeeder(service: NetworkService, mRID: String): GrpcResult<Feeder> {
        val feederResponse = getIdentifiedObject(service, mRID)
        val feeder = feederResponse
            .onError { thrown, wasHandled -> return@getFeeder GrpcResult.ofError(thrown, wasHandled) }
            .result

        if (feeder == null)
            return GrpcResult.of(null)
        else if (feeder !is Feeder)
            return GrpcResult.ofError(ClassCastException("Unable to extract feeder network from ${feeder.typeNameAndMRID()}."), false)

        getIdentifiedObjects(service, service.getUnresolvedReferenceMrids(Resolvers.equipment(feeder)))
            .onError { thrown, wasHandled -> return@getFeeder GrpcResult.ofError(thrown, wasHandled) }

        val mRIDs = service.getUnresolvedReferenceMrids(Resolvers.normalEnergizingSubstation(feeder)).toMutableSet()

        feeder.equipment.forEach {
            if (it is ConductingEquipment) {
                it.terminals.forEach { terminal ->
                    mRIDs.addAll(service.getUnresolvedReferenceMrids(Resolvers.connectivityNode(terminal)))
                }
            }

            if (it is Conductor) {
                if (it is AcLineSegment)
                    mRIDs.addAll(service.getUnresolvedReferenceMrids(Resolvers.perLengthSequenceImpedance(it)))
                mRIDs.addAll(service.getUnresolvedReferenceMrids(Resolvers.assetInfo(it)))
            }

            mRIDs.addAll(service.getUnresolvedReferenceMrids(Resolvers.location(it)))
        }

        getIdentifiedObjects(service, mRIDs)
            .onError { thrown, wasHandled -> return@getFeeder GrpcResult.ofError(thrown, wasHandled) }

        return GrpcResult.of(feeder)
    }

    private fun processIdentifiedObjects(service: NetworkService, request: GetIdentifiedObjectsRequest): Sequence<IdentifiedObject?> {
        return stub.getIdentifiedObjects(request)
            .asSequence()
            .map { it.objectGroup }
            .flatMap {
                sequenceOf(extractIdentifiedObject(service, it.identifiedObject)) +
                    it.ownedIdentifiedObjectList.map { owned -> extractIdentifiedObject(service, owned) }
            }
    }

    private fun extractIdentifiedObject(service: NetworkService, it: NetworkIdentifiedObject): IdentifiedObject {
        return when (it.identifiedObjectCase) {
            CABLEINFO -> protoToCimProvider(service).addFromPb(it.cableInfo)
            OVERHEADWIREINFO -> protoToCimProvider(service).addFromPb(it.overheadWireInfo)
            ASSETOWNER -> protoToCimProvider(service).addFromPb(it.assetOwner)
            ORGANISATION -> protoToCimProvider(service).addFromPb(it.organisation)
            LOCATION -> protoToCimProvider(service).addFromPb(it.location)
            METER -> protoToCimProvider(service).addFromPb(it.meter)
            USAGEPOINT -> protoToCimProvider(service).addFromPb(it.usagePoint)
            OPERATIONALRESTRICTION -> protoToCimProvider(service).addFromPb(it.operationalRestriction)
            FAULTINDICATOR -> protoToCimProvider(service).addFromPb(it.faultIndicator)
            BASEVOLTAGE -> protoToCimProvider(service).addFromPb(it.baseVoltage)
            CONNECTIVITYNODE -> protoToCimProvider(service).addFromPb(it.connectivityNode)
            FEEDER -> protoToCimProvider(service).addFromPb(it.feeder)
            GEOGRAPHICALREGION -> protoToCimProvider(service).addFromPb(it.geographicalRegion)
            SITE -> protoToCimProvider(service).addFromPb(it.site)
            SUBGEOGRAPHICALREGION -> protoToCimProvider(service).addFromPb(it.subGeographicalRegion)
            SUBSTATION -> protoToCimProvider(service).addFromPb(it.substation)
            TERMINAL -> protoToCimProvider(service).addFromPb(it.terminal)
            ACLINESEGMENT -> protoToCimProvider(service).addFromPb(it.acLineSegment)
            BREAKER -> protoToCimProvider(service).addFromPb(it.breaker)
            DISCONNECTOR -> protoToCimProvider(service).addFromPb(it.disconnector)
            ENERGYCONSUMER -> protoToCimProvider(service).addFromPb(it.energyConsumer)
            ENERGYCONSUMERPHASE -> protoToCimProvider(service).addFromPb(it.energyConsumerPhase)
            ENERGYSOURCE -> protoToCimProvider(service).addFromPb(it.energySource)
            ENERGYSOURCEPHASE -> protoToCimProvider(service).addFromPb(it.energySourcePhase)
            FUSE -> protoToCimProvider(service).addFromPb(it.fuse)
            JUMPER -> protoToCimProvider(service).addFromPb(it.jumper)
            JUNCTION -> protoToCimProvider(service).addFromPb(it.junction)
            LINEARSHUNTCOMPENSATOR -> protoToCimProvider(service).addFromPb(it.linearShuntCompensator)
            PERLENGTHSEQUENCEIMPEDANCE -> protoToCimProvider(service).addFromPb(it.perLengthSequenceImpedance)
            POWERTRANSFORMER -> protoToCimProvider(service).addFromPb(it.powerTransformer)
            POWERTRANSFORMEREND -> protoToCimProvider(service).addFromPb(it.powerTransformerEnd)
            RATIOTAPCHANGER -> protoToCimProvider(service).addFromPb(it.ratioTapChanger)
            RECLOSER -> protoToCimProvider(service).addFromPb(it.recloser)
            CIRCUIT -> protoToCimProvider(service).addFromPb(it.circuit)
            LOOP -> protoToCimProvider(service).addFromPb(it.loop)
            POLE -> protoToCimProvider(service).addFromPb(it.pole)
            STREETLIGHT -> protoToCimProvider(service).addFromPb(it.streetlight)
            ACCUMULATOR -> protoToCimProvider(service).addFromPb(it.accumulator)
            ANALOG -> protoToCimProvider(service).addFromPb(it.analog)
            DISCRETE -> protoToCimProvider(service).addFromPb(it.discrete)
            CONTROL -> protoToCimProvider(service).addFromPb(it.control)
            REMOTECONTROL -> protoToCimProvider(service).addFromPb(it.remoteControl)
            REMOTESOURCE -> protoToCimProvider(service).addFromPb(it.remoteSource)
            OTHER, IDENTIFIEDOBJECT_NOT_SET, null -> throw UnsupportedOperationException("Identified object type ${it.identifiedObjectCase} is not supported by the network service")
        }
    }

    private fun <T, U : NetworkHierarchyIdentifiedObject> toMap(objects: Iterable<T>, mapper: (T) -> U): Map<String, U> = objects
        .map(mapper)
        .associateBy { it.mRID }

    private fun <T : NetworkHierarchyIdentifiedObject> lookup(mRIDs: Iterable<String>, lookup: Map<String, T>): Map<String, T> =
        mRIDs.mapNotNull { mRID -> lookup[mRID] }
            .associateBy { it.mRID }

    private fun finaliseLinks(
        geographicalRegions: Map<String, NetworkHierarchyGeographicalRegion>,
        subGeographicalRegions: Map<String, NetworkHierarchySubGeographicalRegion>,
        substations: Map<String, NetworkHierarchySubstation>
    ) {
        geographicalRegions.values.forEach { it.subGeographicalRegions.values.forEach { other -> other.geographicalRegion = it } }
        subGeographicalRegions.values.forEach { it.substations.values.forEach { other -> other.subGeographicalRegion = it } }
        substations.values.forEach { it.feeders.values.forEach { other -> other.substation = it } }
    }

}

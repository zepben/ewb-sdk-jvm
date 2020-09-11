/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 * This file is part of evolve-sdk-jvm.
 *
 * evolve-sdk-jvm is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * evolve-sdk-jvm is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with evolve-sdk-jvm.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.zepben.cimbend.put

import com.zepben.cimbend.cim.iec61970.base.core.IdentifiedObject
import com.zepben.cimbend.common.translator.toPb
import com.zepben.cimbend.network.NetworkService
import com.zepben.cimbend.network.model.toPb
import com.zepben.cimbend.network.whenNetworkServiceObject
import com.zepben.protobuf.np.*
import io.grpc.Channel

/**
 * Producer client for a [NetworkService].
 *
 * @property stub The gRPC stub to be used to communicate with the server
 */
class NetworkProducerClient(
    private val stub: NetworkProducerGrpc.NetworkProducerBlockingStub
) : CimProducerClient<NetworkService>() {

    constructor(channel: Channel) : this(NetworkProducerGrpc.newBlockingStub(channel))

    override fun send(service: NetworkService) {
        tryRpc { stub.createNetwork(CreateNetworkRequest.newBuilder().build()) }

        service.sequenceOf<IdentifiedObject>().forEach { sendToServer(it) }

        tryRpc { stub.completeNetwork(CompleteNetworkRequest.newBuilder().build()) }
    }

    private fun sendToServer(identifiedObject: IdentifiedObject) = tryRpc {
        whenNetworkServiceObject(
            identifiedObject,
            isAcLineSegment = {
                val builder = CreateAcLineSegmentRequest.newBuilder().setAcLineSegment(it.toPb()).build()
                stub.createAcLineSegment(builder)
            },
            isAssetOwner = {
                val builder = CreateAssetOwnerRequest.newBuilder().setAssetOwner(it.toPb()).build()
                stub.createAssetOwner(builder)
            },
            isBaseVoltage = {
                val builder = CreateBaseVoltageRequest.newBuilder().setBaseVoltage(it.toPb()).build()
                stub.createBaseVoltage(builder)
            },
            isBreaker = {
                val builder = CreateBreakerRequest.newBuilder().setBreaker(it.toPb()).build()
                stub.createBreaker(builder)
            },
            isCableInfo = {
                val builder = CreateCableInfoRequest.newBuilder().setCableInfo(it.toPb()).build()
                stub.createCableInfo(builder)
            },
            isCircuit = {
                val builder = CreateCircuitRequest.newBuilder().setCircuit(it.toPb()).build()
                stub.createCircuit(builder)
            },
            isConnectivityNode = {
                val builder = CreateConnectivityNodeRequest.newBuilder().setConnectivityNode(it.toPb()).build()
                stub.createConnectivityNode(builder)
            },
            isDisconnector = {
                val builder = CreateDisconnectorRequest.newBuilder().setDisconnector(it.toPb()).build()
                stub.createDisconnector(builder)
            },
            isEnergyConsumer = {
                val builder = CreateEnergyConsumerRequest.newBuilder().setEnergyConsumer(it.toPb()).build()
                stub.createEnergyConsumer(builder)
            },
            isEnergyConsumerPhase = {
                val builder = CreateEnergyConsumerPhaseRequest.newBuilder().setEnergyConsumerPhase(it.toPb()).build()
                stub.createEnergyConsumerPhase(builder)
            },
            isEnergySource = {
                val builder = CreateEnergySourceRequest.newBuilder().setEnergySource(it.toPb()).build()
                stub.createEnergySource(builder)
            },
            isEnergySourcePhase = {
                val builder = CreateEnergySourcePhaseRequest.newBuilder().setEnergySourcePhase(it.toPb()).build()
                stub.createEnergySourcePhase(builder)
            },
            isFaultIndicator = {
                val builder = CreateFaultIndicatorRequest.newBuilder().setFaultIndicator(it.toPb()).build()
                stub.createFaultIndicator(builder)
            },
            isFeeder = {
                val builder = CreateFeederRequest.newBuilder().setFeeder(it.toPb()).build()
                stub.createFeeder(builder)
            },
            isFuse = {
                val builder = CreateFuseRequest.newBuilder().setFuse(it.toPb()).build()
                stub.createFuse(builder)
            },
            isGeographicalRegion = {
                val builder = CreateGeographicalRegionRequest.newBuilder().setGeographicalRegion(it.toPb()).build()
                stub.createGeographicalRegion(builder)
            },
            isJumper = {
                val builder = CreateJumperRequest.newBuilder().setJumper(it.toPb()).build()
                stub.createJumper(builder)
            },
            isJunction = {
                val builder = CreateJunctionRequest.newBuilder().setJunction(it.toPb()).build()
                stub.createJunction(builder)
            },
            isLinearShuntCompensator = {
                val builder = CreateLinearShuntCompensatorRequest.newBuilder().setLinearShuntCompensator(it.toPb()).build()
                stub.createLinearShuntCompensator(builder)
            },
            isLocation = {
                val builder = CreateLocationRequest.newBuilder().setLocation(it.toPb()).build()
                stub.createLocation(builder)
            },
            isLoop = {
                val builder = CreateLoopRequest.newBuilder().setLoop(it.toPb()).build()
                stub.createLoop(builder)
            },
            isMeter = {
                val builder = CreateMeterRequest.newBuilder().setMeter(it.toPb()).build()
                stub.createMeter(builder)
            },
            isOperationalRestriction = {
                val builder = CreateOperationalRestrictionRequest.newBuilder().setOperationalRestriction(it.toPb()).build()
                stub.createOperationalRestriction(builder)
            },
            isOrganisation = {
                val builder = CreateOrganisationRequest.newBuilder().setOrganisation(it.toPb()).build()
                stub.createOrganisation(builder)
            },
            isOverheadWireInfo = {
                val builder = CreateOverheadWireInfoRequest.newBuilder().setOverheadWireInfo(it.toPb()).build()
                stub.createOverheadWireInfo(builder)
            },
            isPerLengthSequenceImpedance = {
                val builder = CreatePerLengthSequenceImpedanceRequest.newBuilder().setPerLengthSequenceImpedance(it.toPb()).build()
                stub.createPerLengthSequenceImpedance(builder)
            },
            isPole = {
                val builder = CreatePoleRequest.newBuilder().setPole(it.toPb()).build()
                stub.createPole(builder)
            },
            isPowerTransformer = {
                val builder = CreatePowerTransformerRequest.newBuilder().setPowerTransformer(it.toPb()).build()
                stub.createPowerTransformer(builder)
            },
            isPowerTransformerEnd = {
                val builder = CreatePowerTransformerEndRequest.newBuilder().setPowerTransformerEnd(it.toPb()).build()
                stub.createPowerTransformerEnd(builder)
            },
            isRatioTapChanger = {
                val builder = CreateRatioTapChangerRequest.newBuilder().setRatioTapChanger(it.toPb()).build()
                stub.createRatioTapChanger(builder)
            },
            isRecloser = {
                val builder = CreateRecloserRequest.newBuilder().setRecloser(it.toPb()).build()
                stub.createRecloser(builder)
            },
            isSite = {
                val builder = CreateSiteRequest.newBuilder().setSite(it.toPb()).build()
                stub.createSite(builder)
            },
            isStreetlight = {
                val builder = CreateStreetlightRequest.newBuilder().setStreetlight(it.toPb()).build()
                stub.createStreetlight(builder)
            },
            isSubGeographicalRegion = {
                val builder = CreateSubGeographicalRegionRequest.newBuilder().setSubGeographicalRegion(it.toPb()).build()
                stub.createSubGeographicalRegion(builder)
            },
            isSubstation = {
                val builder = CreateSubstationRequest.newBuilder().setSubstation(it.toPb()).build()
                stub.createSubstation(builder)
            },
            isTerminal = {
                val builder = CreateTerminalRequest.newBuilder().setTerminal(it.toPb()).build()
                stub.createTerminal(builder)
            },
            isUsagePoint = {
                val builder = CreateUsagePointRequest.newBuilder().setUsagePoint(it.toPb()).build()
                stub.createUsagePoint(builder)
            },
            isControl = {
            },
            isAnalog = {},
            isAccumulator = {},
            isDiscrete = {},
            isRemoteControl = {},
            isRemoteSource = {}
        )
    }
}

/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.streaming.put

import com.zepben.evolve.cim.iec61970.base.core.IdentifiedObject
import com.zepben.evolve.services.common.translator.toPb
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.translator.toPb
import com.zepben.evolve.services.network.whenNetworkServiceObject
import com.zepben.evolve.streaming.grpc.GrpcChannel
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
    constructor(channel: GrpcChannel) : this(NetworkProducerGrpc.newBlockingStub(channel.channel))

    override fun send(service: NetworkService) {
        tryRpc { stub.createNetwork(CreateNetworkRequest.newBuilder().build()) }
            .throwOnUnhandledError()

        service.sequenceOf<IdentifiedObject>().forEach { sendToServer(it) }

        tryRpc { stub.completeNetwork(CompleteNetworkRequest.newBuilder().build()) }
            .throwOnUnhandledError()
    }

    private fun sendToServer(identifiedObject: IdentifiedObject) = tryRpc {
        whenNetworkServiceObject(
            identifiedObject,
            isBatteryUnit = {
                val builder = CreateBatteryUnitRequest.newBuilder().setBatteryUnit(it.toPb()).build()
                stub.createBatteryUnit(builder)
            },
            isPhotoVoltaicUnit = {
                val builder = CreatePhotoVoltaicRequest.newBuilder().setPhotoVoltaicUnit(it.toPb()).build()
                stub.createPhotoVoltaicUnit(builder)
            },
            isPowerElectronicsWindUnit = {
                val builder = CreatePowerElectronicsWindUnitRequest.newBuilder().setPowerElectronicsWindUnits(it.toPb()).build()
                stub.createPowerElectronicsWindUnit(builder)
            },
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
            isBusbarSection = {
                val builder = CreateBusbarSectionRequest.newBuilder().setBusbarSection(it.toPb()).build()
                stub.createBusbarSection(builder)
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
            isPowerElectronicsConnection = {
                val builder = CreatePowerElectronicsConnectionRequest.newBuilder().setPowerElectronicsConnection(it.toPb()).build()
                stub.createPowerElectronicsConnection(builder)
            },
            isPowerElectronicsConnectionPhase = {
                val builder = CreatePowerElectronicsConnectionPhaseRequest.newBuilder().setPowerElectronicsConnectionPhase(it.toPb()).build()
                stub.createPowerElectronicsConnectionPhase(builder)
            },
            isPowerTransformer = {
                val builder = CreatePowerTransformerRequest.newBuilder().setPowerTransformer(it.toPb()).build()
                stub.createPowerTransformer(builder)
            },
            isPowerTransformerEnd = {
                val builder = CreatePowerTransformerEndRequest.newBuilder().setPowerTransformerEnd(it.toPb()).build()
                stub.createPowerTransformerEnd(builder)
            },
            isPowerTransformerInfo = {
                val builder = CreatePowerTransformerInfoRequest.newBuilder().setPowerTransformerInfo(it.toPb()).build()
                stub.createPowerTransformerInfo(builder)
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
    }.throwOnUnhandledError()
}

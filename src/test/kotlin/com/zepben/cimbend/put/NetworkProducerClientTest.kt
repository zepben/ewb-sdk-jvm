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

import com.zepben.cimbend.cim.iec61968.assetinfo.CableInfo
import com.zepben.cimbend.cim.iec61968.assetinfo.OverheadWireInfo
import com.zepben.cimbend.cim.iec61968.assets.AssetOwner
import com.zepben.cimbend.cim.iec61968.assets.Pole
import com.zepben.cimbend.cim.iec61968.assets.Streetlight
import com.zepben.cimbend.cim.iec61968.common.Location
import com.zepben.cimbend.cim.iec61968.common.Organisation
import com.zepben.cimbend.cim.iec61968.metering.Meter
import com.zepben.cimbend.cim.iec61968.metering.UsagePoint
import com.zepben.cimbend.cim.iec61968.operations.OperationalRestriction
import com.zepben.cimbend.cim.iec61970.base.auxiliaryequipment.FaultIndicator
import com.zepben.cimbend.cim.iec61970.base.core.*
import com.zepben.cimbend.cim.iec61970.base.wires.*
import com.zepben.cimbend.cim.iec61970.infiec61970.feeder.Circuit
import com.zepben.cimbend.cim.iec61970.infiec61970.feeder.Loop
import com.zepben.cimbend.common.translator.toPb
import com.zepben.cimbend.network.NetworkService
import com.zepben.cimbend.network.model.toPb
import com.zepben.protobuf.np.*
import io.grpc.Channel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.instanceOf
import org.junit.Assert.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*

internal class NetworkProducerClientTest {

    private val stub = mock(NetworkProducerGrpc.NetworkProducerBlockingStub::class.java)
    private val onErrorHandler = CaptureLastRpcErrorHandler()
    private val producerClient: NetworkProducerClient = NetworkProducerClient(stub, onErrorHandler)
    private val service: NetworkService = NetworkService()

    @Test
    internal fun `onRpcError defaults to logging handler`() {
        val client = NetworkProducerClient(mock(Channel::class.java))
        assertThat(client.onRpcError, instanceOf(RpcErrorLogger::class.java))
    }

    @Test
    internal fun `sends CableInfo`() {
        val cableInfo = CableInfo("id1").also { service.add(it) }

        producerClient.send(service)

        val stubInOrder = inOrder(stub)
        stubInOrder.verify(stub).createNetwork(CreateNetworkRequest.newBuilder().build())
        stubInOrder.verify(stub).createCableInfo(CreateCableInfoRequest.newBuilder().setCableInfo(cableInfo.toPb()).build())
        stubInOrder.verify(stub).completeNetwork(CompleteNetworkRequest.newBuilder().build())
    }

    @Test
    internal fun `calls error handler when sending CableInfo throws`() {
        val cableInfo = CableInfo("id1").also { service.add(it) }

        val expectedEx = StatusRuntimeException(Status.UNAVAILABLE)
        doAnswer { throw expectedEx }.`when`(stub).createCableInfo(any())
        producerClient.send(service)

        verify(stub).createCableInfo(CreateCableInfoRequest.newBuilder().setCableInfo(cableInfo.toPb()).build())
        assertThat(onErrorHandler.lastError, equalTo(expectedEx))
    }

    @Test
    internal fun `sends OverheadWireInfo`() {
        val overheadWireInfo = OverheadWireInfo("id1").also { service.add(it) }

        producerClient.send(service)

        val stubInOrder = inOrder(stub)
        stubInOrder.verify(stub).createNetwork(CreateNetworkRequest.newBuilder().build())
        stubInOrder.verify(stub).createOverheadWireInfo(CreateOverheadWireInfoRequest.newBuilder().setOverheadWireInfo(overheadWireInfo.toPb()).build())
        stubInOrder.verify(stub).completeNetwork(CompleteNetworkRequest.newBuilder().build())
    }

    @Test
    internal fun `calls error handler when sending OverheadWireInfo throws`() {
        val overheadWireInfo = OverheadWireInfo("id1").also { service.add(it) }

        val expectedEx = StatusRuntimeException(Status.UNAVAILABLE)
        doAnswer { throw expectedEx }.`when`(stub).createOverheadWireInfo(any())
        producerClient.send(service)

        verify(stub).createOverheadWireInfo(CreateOverheadWireInfoRequest.newBuilder().setOverheadWireInfo(overheadWireInfo.toPb()).build())
        assertThat(onErrorHandler.lastError, equalTo(expectedEx))
    }

    @Test
    internal fun `sends AssetOwner`() {
        val assetOwner = AssetOwner("id1").also { service.add(it) }

        producerClient.send(service)

        val stubInOrder = inOrder(stub)
        stubInOrder.verify(stub).createNetwork(CreateNetworkRequest.newBuilder().build())
        stubInOrder.verify(stub).createAssetOwner(CreateAssetOwnerRequest.newBuilder().setAssetOwner(assetOwner.toPb()).build())
        stubInOrder.verify(stub).completeNetwork(CompleteNetworkRequest.newBuilder().build())
    }

    @Test
    internal fun `calls error handler when sending AssetOwner throws`() {
        val assetOwner = AssetOwner("id1").also { service.add(it) }

        val expectedEx = StatusRuntimeException(Status.UNAVAILABLE)
        doAnswer { throw expectedEx }.`when`(stub).createAssetOwner(any())
        producerClient.send(service)

        verify(stub).createAssetOwner(CreateAssetOwnerRequest.newBuilder().setAssetOwner(assetOwner.toPb()).build())
        assertThat(onErrorHandler.lastError, equalTo(expectedEx))
    }

    @Test
    internal fun `sends Pole`() {
        val pole = Pole("id1").also { service.add(it) }

        producerClient.send(service)

        val stubInOrder = inOrder(stub)
        stubInOrder.verify(stub).createNetwork(CreateNetworkRequest.newBuilder().build())
        stubInOrder.verify(stub).createPole(CreatePoleRequest.newBuilder().setPole(pole.toPb()).build())
        stubInOrder.verify(stub).completeNetwork(CompleteNetworkRequest.newBuilder().build())
    }

    @Test
    internal fun `calls error handler when sending Pole throws`() {
        val pole = Pole("id1").also { service.add(it) }

        val expectedEx = StatusRuntimeException(Status.UNAVAILABLE)
        doAnswer { throw expectedEx }.`when`(stub).createPole(any())
        producerClient.send(service)

        verify(stub).createPole(CreatePoleRequest.newBuilder().setPole(pole.toPb()).build())
        assertThat(onErrorHandler.lastError, equalTo(expectedEx))
    }

    @Test
    internal fun `sends Streetlight`() {
        val streetlight = Streetlight("id1").also { service.add(it) }

        producerClient.send(service)

        val stubInOrder = inOrder(stub)
        stubInOrder.verify(stub).createNetwork(CreateNetworkRequest.newBuilder().build())
        stubInOrder.verify(stub).createStreetlight(CreateStreetlightRequest.newBuilder().setStreetlight(streetlight.toPb()).build())
        stubInOrder.verify(stub).completeNetwork(CompleteNetworkRequest.newBuilder().build())
    }

    @Test
    internal fun `calls error handler when sending Streetlight throws`() {
        val streetlight = Streetlight("id1").also { service.add(it) }

        val expectedEx = StatusRuntimeException(Status.UNAVAILABLE)
        doAnswer { throw expectedEx }.`when`(stub).createStreetlight(any())
        producerClient.send(service)

        verify(stub).createStreetlight(CreateStreetlightRequest.newBuilder().setStreetlight(streetlight.toPb()).build())
        assertThat(onErrorHandler.lastError, equalTo(expectedEx))
    }

    @Test
    internal fun `sends Location`() {
        val location = Location("id1").also { service.add(it) }

        producerClient.send(service)

        val stubInOrder = inOrder(stub)
        stubInOrder.verify(stub).createNetwork(CreateNetworkRequest.newBuilder().build())
        stubInOrder.verify(stub).createLocation(CreateLocationRequest.newBuilder().setLocation(location.toPb()).build())
        stubInOrder.verify(stub).completeNetwork(CompleteNetworkRequest.newBuilder().build())
    }

    @Test
    internal fun `calls error handler when sending Location throws`() {
        val location = Location("id1").also { service.add(it) }

        val expectedEx = StatusRuntimeException(Status.UNAVAILABLE)
        doAnswer { throw expectedEx }.`when`(stub).createLocation(any())
        producerClient.send(service)

        verify(stub).createLocation(CreateLocationRequest.newBuilder().setLocation(location.toPb()).build())
        assertThat(onErrorHandler.lastError, equalTo(expectedEx))
    }

    @Test
    internal fun `sends Organisation`() {
        val organisation = Organisation("id1").also { service.add(it) }

        producerClient.send(service)

        val stubInOrder = inOrder(stub)
        stubInOrder.verify(stub).createNetwork(CreateNetworkRequest.newBuilder().build())
        stubInOrder.verify(stub).createOrganisation(CreateOrganisationRequest.newBuilder().setOrganisation(organisation.toPb()).build())
        stubInOrder.verify(stub).completeNetwork(CompleteNetworkRequest.newBuilder().build())
    }

    @Test
    internal fun `calls error handler when sending Organisation throws`() {
        val organisation = Organisation("id1").also { service.add(it) }

        val expectedEx = StatusRuntimeException(Status.UNAVAILABLE)
        doAnswer { throw expectedEx }.`when`(stub).createOrganisation(any())
        producerClient.send(service)

        verify(stub).createOrganisation(CreateOrganisationRequest.newBuilder().setOrganisation(organisation.toPb()).build())
        assertThat(onErrorHandler.lastError, equalTo(expectedEx))
    }

    @Test
    internal fun `sends Meter`() {
        val meter = Meter("id1").also { service.add(it) }

        producerClient.send(service)

        val stubInOrder = inOrder(stub)
        stubInOrder.verify(stub).createNetwork(CreateNetworkRequest.newBuilder().build())
        stubInOrder.verify(stub).createMeter(CreateMeterRequest.newBuilder().setMeter(meter.toPb()).build())
        stubInOrder.verify(stub).completeNetwork(CompleteNetworkRequest.newBuilder().build())
    }

    @Test
    internal fun `calls error handler when sending Meter throws`() {
        val meter = Meter("id1").also { service.add(it) }

        val expectedEx = StatusRuntimeException(Status.UNAVAILABLE)
        doAnswer { throw expectedEx }.`when`(stub).createMeter(any())
        producerClient.send(service)

        verify(stub).createMeter(CreateMeterRequest.newBuilder().setMeter(meter.toPb()).build())
        assertThat(onErrorHandler.lastError, equalTo(expectedEx))
    }

    @Test
    internal fun `sends UsagePoint`() {
        val usagePoint = UsagePoint("id1").also { service.add(it) }

        producerClient.send(service)

        val stubInOrder = inOrder(stub)
        stubInOrder.verify(stub).createNetwork(CreateNetworkRequest.newBuilder().build())
        stubInOrder.verify(stub).createUsagePoint(CreateUsagePointRequest.newBuilder().setUsagePoint(usagePoint.toPb()).build())
        stubInOrder.verify(stub).completeNetwork(CompleteNetworkRequest.newBuilder().build())
    }

    @Test
    internal fun `calls error handler when sending UsagePoint throws`() {
        val usagePoint = UsagePoint("id1").also { service.add(it) }

        val expectedEx = StatusRuntimeException(Status.UNAVAILABLE)
        doAnswer { throw expectedEx }.`when`(stub).createUsagePoint(any())
        producerClient.send(service)

        verify(stub).createUsagePoint(CreateUsagePointRequest.newBuilder().setUsagePoint(usagePoint.toPb()).build())
        assertThat(onErrorHandler.lastError, equalTo(expectedEx))
    }

    @Test
    internal fun `sends OperationalRestriction`() {
        val operationalRestriction = OperationalRestriction("id1").also { service.add(it) }

        producerClient.send(service)

        val stubInOrder = inOrder(stub)
        stubInOrder.verify(stub).createNetwork(CreateNetworkRequest.newBuilder().build())
        stubInOrder.verify(stub).createOperationalRestriction(CreateOperationalRestrictionRequest.newBuilder().setOperationalRestriction(operationalRestriction.toPb()).build())
        stubInOrder.verify(stub).completeNetwork(CompleteNetworkRequest.newBuilder().build())
    }

    @Test
    internal fun `calls error handler when sending OperationalRestriction throws`() {
        val operationalRestriction = OperationalRestriction("id1").also { service.add(it) }

        val expectedEx = StatusRuntimeException(Status.UNAVAILABLE)
        doAnswer { throw expectedEx }.`when`(stub).createOperationalRestriction(any())
        producerClient.send(service)

        verify(stub).createOperationalRestriction(CreateOperationalRestrictionRequest.newBuilder().setOperationalRestriction(operationalRestriction.toPb()).build())
        assertThat(onErrorHandler.lastError, equalTo(expectedEx))
    }

    @Test
    internal fun `sends FaultIndicator`() {
        val faultIndicator = FaultIndicator("id1").also { service.add(it) }

        producerClient.send(service)

        val stubInOrder = inOrder(stub)
        stubInOrder.verify(stub).createNetwork(CreateNetworkRequest.newBuilder().build())
        stubInOrder.verify(stub).createFaultIndicator(CreateFaultIndicatorRequest.newBuilder().setFaultIndicator(faultIndicator.toPb()).build())
        stubInOrder.verify(stub).completeNetwork(CompleteNetworkRequest.newBuilder().build())
    }

    @Test
    internal fun `calls error handler when sending FaultIndicator throws`() {
        val faultIndicator = FaultIndicator("id1").also { service.add(it) }

        val expectedEx = StatusRuntimeException(Status.UNAVAILABLE)
        doAnswer { throw expectedEx }.`when`(stub).createFaultIndicator(any())
        producerClient.send(service)

        verify(stub).createFaultIndicator(CreateFaultIndicatorRequest.newBuilder().setFaultIndicator(faultIndicator.toPb()).build())
        assertThat(onErrorHandler.lastError, equalTo(expectedEx))
    }

    @Test
    internal fun `sends BaseVoltage`() {
        val baseVoltage = BaseVoltage("id1").also { service.add(it) }

        producerClient.send(service)

        val stubInOrder = inOrder(stub)
        stubInOrder.verify(stub).createNetwork(CreateNetworkRequest.newBuilder().build())
        stubInOrder.verify(stub).createBaseVoltage(CreateBaseVoltageRequest.newBuilder().setBaseVoltage(baseVoltage.toPb()).build())
        stubInOrder.verify(stub).completeNetwork(CompleteNetworkRequest.newBuilder().build())
    }

    @Test
    internal fun `calls error handler when sending BaseVoltage throws`() {
        val baseVoltage = BaseVoltage("id1").also { service.add(it) }

        val expectedEx = StatusRuntimeException(Status.UNAVAILABLE)
        doAnswer { throw expectedEx }.`when`(stub).createBaseVoltage(any())
        producerClient.send(service)

        verify(stub).createBaseVoltage(CreateBaseVoltageRequest.newBuilder().setBaseVoltage(baseVoltage.toPb()).build())
        assertThat(onErrorHandler.lastError, equalTo(expectedEx))
    }

    @Test
    internal fun `sends ConnectivityNode`() {
        val connectivityNode = ConnectivityNode("id1").also { service.add(it) }

        producerClient.send(service)

        val stubInOrder = inOrder(stub)
        stubInOrder.verify(stub).createNetwork(CreateNetworkRequest.newBuilder().build())
        stubInOrder.verify(stub).createConnectivityNode(CreateConnectivityNodeRequest.newBuilder().setConnectivityNode(connectivityNode.toPb()).build())
        stubInOrder.verify(stub).completeNetwork(CompleteNetworkRequest.newBuilder().build())
    }

    @Test
    internal fun `calls error handler when sending ConnectivityNode throws`() {
        val connectivityNode = ConnectivityNode("id1").also { service.add(it) }

        val expectedEx = StatusRuntimeException(Status.UNAVAILABLE)
        doAnswer { throw expectedEx }.`when`(stub).createConnectivityNode(any())
        producerClient.send(service)

        verify(stub).createConnectivityNode(CreateConnectivityNodeRequest.newBuilder().setConnectivityNode(connectivityNode.toPb()).build())
        assertThat(onErrorHandler.lastError, equalTo(expectedEx))
    }

    @Test
    internal fun `sends Feeder`() {
        val feeder = Feeder("id1").also { service.add(it) }

        producerClient.send(service)

        val stubInOrder = inOrder(stub)
        stubInOrder.verify(stub).createNetwork(CreateNetworkRequest.newBuilder().build())
        stubInOrder.verify(stub).createFeeder(CreateFeederRequest.newBuilder().setFeeder(feeder.toPb()).build())
        stubInOrder.verify(stub).completeNetwork(CompleteNetworkRequest.newBuilder().build())
    }

    @Test
    internal fun `calls error handler when sending Feeder throws`() {
        val feeder = Feeder("id1").also { service.add(it) }

        val expectedEx = StatusRuntimeException(Status.UNAVAILABLE)
        doAnswer { throw expectedEx }.`when`(stub).createFeeder(any())
        producerClient.send(service)

        verify(stub).createFeeder(CreateFeederRequest.newBuilder().setFeeder(feeder.toPb()).build())
        assertThat(onErrorHandler.lastError, equalTo(expectedEx))
    }

    @Test
    internal fun `sends GeographicalRegion`() {
        val geographicalRegion = GeographicalRegion("id1").also { service.add(it) }

        producerClient.send(service)

        val stubInOrder = inOrder(stub)
        stubInOrder.verify(stub).createNetwork(CreateNetworkRequest.newBuilder().build())
        stubInOrder.verify(stub).createGeographicalRegion(CreateGeographicalRegionRequest.newBuilder().setGeographicalRegion(geographicalRegion.toPb()).build())
        stubInOrder.verify(stub).completeNetwork(CompleteNetworkRequest.newBuilder().build())
    }

    @Test
    internal fun `calls error handler when sending GeographicalRegion throws`() {
        val geographicalRegion = GeographicalRegion("id1").also { service.add(it) }

        val expectedEx = StatusRuntimeException(Status.UNAVAILABLE)
        doAnswer { throw expectedEx }.`when`(stub).createGeographicalRegion(any())
        producerClient.send(service)

        verify(stub).createGeographicalRegion(CreateGeographicalRegionRequest.newBuilder().setGeographicalRegion(geographicalRegion.toPb()).build())
        assertThat(onErrorHandler.lastError, equalTo(expectedEx))
    }

    @Test
    internal fun `sends Site`() {
        val site = Site("id1").also { service.add(it) }

        producerClient.send(service)

        val stubInOrder = inOrder(stub)
        stubInOrder.verify(stub).createNetwork(CreateNetworkRequest.newBuilder().build())
        stubInOrder.verify(stub).createSite(CreateSiteRequest.newBuilder().setSite(site.toPb()).build())
        stubInOrder.verify(stub).completeNetwork(CompleteNetworkRequest.newBuilder().build())
    }

    @Test
    internal fun `calls error handler when sending Site throws`() {
        val site = Site("id1").also { service.add(it) }

        val expectedEx = StatusRuntimeException(Status.UNAVAILABLE)
        doAnswer { throw expectedEx }.`when`(stub).createSite(any())
        producerClient.send(service)

        verify(stub).createSite(CreateSiteRequest.newBuilder().setSite(site.toPb()).build())
        assertThat(onErrorHandler.lastError, equalTo(expectedEx))
    }

    @Test
    internal fun `sends SubGeographicalRegion`() {
        val subGeographicalRegion = SubGeographicalRegion("id1").also { service.add(it) }

        producerClient.send(service)

        val stubInOrder = inOrder(stub)
        stubInOrder.verify(stub).createNetwork(CreateNetworkRequest.newBuilder().build())
        stubInOrder.verify(stub).createSubGeographicalRegion(CreateSubGeographicalRegionRequest.newBuilder().setSubGeographicalRegion(subGeographicalRegion.toPb()).build())
        stubInOrder.verify(stub).completeNetwork(CompleteNetworkRequest.newBuilder().build())
    }

    @Test
    internal fun `calls error handler when sending SubGeographicalRegion throws`() {
        val subGeographicalRegion = SubGeographicalRegion("id1").also { service.add(it) }

        val expectedEx = StatusRuntimeException(Status.UNAVAILABLE)
        doAnswer { throw expectedEx }.`when`(stub).createSubGeographicalRegion(any())
        producerClient.send(service)

        verify(stub).createSubGeographicalRegion(CreateSubGeographicalRegionRequest.newBuilder().setSubGeographicalRegion(subGeographicalRegion.toPb()).build())
        assertThat(onErrorHandler.lastError, equalTo(expectedEx))
    }

    @Test
    internal fun `sends Substation`() {
        val substation = Substation("id1").also { service.add(it) }

        producerClient.send(service)

        val stubInOrder = inOrder(stub)
        stubInOrder.verify(stub).createNetwork(CreateNetworkRequest.newBuilder().build())
        stubInOrder.verify(stub).createSubstation(CreateSubstationRequest.newBuilder().setSubstation(substation.toPb()).build())
        stubInOrder.verify(stub).completeNetwork(CompleteNetworkRequest.newBuilder().build())
    }

    @Test
    internal fun `calls error handler when sending Substation throws`() {
        val substation = Substation("id1").also { service.add(it) }

        val expectedEx = StatusRuntimeException(Status.UNAVAILABLE)
        doAnswer { throw expectedEx }.`when`(stub).createSubstation(any())
        producerClient.send(service)

        verify(stub).createSubstation(CreateSubstationRequest.newBuilder().setSubstation(substation.toPb()).build())
        assertThat(onErrorHandler.lastError, equalTo(expectedEx))
    }

    @Test
    internal fun `sends Terminal`() {
        val terminal = Terminal("id1").also { service.add(it) }

        producerClient.send(service)

        val stubInOrder = inOrder(stub)
        stubInOrder.verify(stub).createNetwork(CreateNetworkRequest.newBuilder().build())
        stubInOrder.verify(stub).createTerminal(CreateTerminalRequest.newBuilder().setTerminal(terminal.toPb()).build())
        stubInOrder.verify(stub).completeNetwork(CompleteNetworkRequest.newBuilder().build())
    }

    @Test
    internal fun `calls error handler when sending Terminal throws`() {
        val terminal = Terminal("id1").also { service.add(it) }

        val expectedEx = StatusRuntimeException(Status.UNAVAILABLE)
        doAnswer { throw expectedEx }.`when`(stub).createTerminal(any())
        producerClient.send(service)

        verify(stub).createTerminal(CreateTerminalRequest.newBuilder().setTerminal(terminal.toPb()).build())
        assertThat(onErrorHandler.lastError, equalTo(expectedEx))
    }

    @Test
    internal fun `sends AcLineSegment`() {
        val acLineSegment = AcLineSegment("id1").also { service.add(it) }

        producerClient.send(service)

        val stubInOrder = inOrder(stub)
        stubInOrder.verify(stub).createNetwork(CreateNetworkRequest.newBuilder().build())
        stubInOrder.verify(stub).createAcLineSegment(CreateAcLineSegmentRequest.newBuilder().setAcLineSegment(acLineSegment.toPb()).build())
        stubInOrder.verify(stub).completeNetwork(CompleteNetworkRequest.newBuilder().build())
    }

    @Test
    internal fun `calls error handler when sending AcLineSegment throws`() {
        val acLineSegment = AcLineSegment("id1").also { service.add(it) }

        val expectedEx = StatusRuntimeException(Status.UNAVAILABLE)
        doAnswer { throw expectedEx }.`when`(stub).createAcLineSegment(any())
        producerClient.send(service)

        verify(stub).createAcLineSegment(CreateAcLineSegmentRequest.newBuilder().setAcLineSegment(acLineSegment.toPb()).build())
        assertThat(onErrorHandler.lastError, equalTo(expectedEx))
    }

    @Test
    internal fun `sends Breaker`() {
        val breaker = Breaker("id1").also { service.add(it) }

        producerClient.send(service)

        val stubInOrder = inOrder(stub)
        stubInOrder.verify(stub).createNetwork(CreateNetworkRequest.newBuilder().build())
        stubInOrder.verify(stub).createBreaker(CreateBreakerRequest.newBuilder().setBreaker(breaker.toPb()).build())
        stubInOrder.verify(stub).completeNetwork(CompleteNetworkRequest.newBuilder().build())
    }

    @Test
    internal fun `calls error handler when sending Breaker throws`() {
        val breaker = Breaker("id1").also { service.add(it) }

        val expectedEx = StatusRuntimeException(Status.UNAVAILABLE)
        doAnswer { throw expectedEx }.`when`(stub).createBreaker(any())
        producerClient.send(service)

        verify(stub).createBreaker(CreateBreakerRequest.newBuilder().setBreaker(breaker.toPb()).build())
        assertThat(onErrorHandler.lastError, equalTo(expectedEx))
    }

    @Test
    internal fun `sends Disconnector`() {
        val disconnector = Disconnector("id1").also { service.add(it) }

        producerClient.send(service)

        val stubInOrder = inOrder(stub)
        stubInOrder.verify(stub).createNetwork(CreateNetworkRequest.newBuilder().build())
        stubInOrder.verify(stub).createDisconnector(CreateDisconnectorRequest.newBuilder().setDisconnector(disconnector.toPb()).build())
        stubInOrder.verify(stub).completeNetwork(CompleteNetworkRequest.newBuilder().build())
    }

    @Test
    internal fun `calls error handler when sending Disconnector throws`() {
        val disconnector = Disconnector("id1").also { service.add(it) }

        val expectedEx = StatusRuntimeException(Status.UNAVAILABLE)
        doAnswer { throw expectedEx }.`when`(stub).createDisconnector(any())
        producerClient.send(service)

        verify(stub).createDisconnector(CreateDisconnectorRequest.newBuilder().setDisconnector(disconnector.toPb()).build())
        assertThat(onErrorHandler.lastError, equalTo(expectedEx))
    }

    @Test
    internal fun `sends EnergyConsumer`() {
        val energyConsumer = EnergyConsumer("id1").also { service.add(it) }

        producerClient.send(service)

        val stubInOrder = inOrder(stub)
        stubInOrder.verify(stub).createNetwork(CreateNetworkRequest.newBuilder().build())
        stubInOrder.verify(stub).createEnergyConsumer(CreateEnergyConsumerRequest.newBuilder().setEnergyConsumer(energyConsumer.toPb()).build())
        stubInOrder.verify(stub).completeNetwork(CompleteNetworkRequest.newBuilder().build())
    }

    @Test
    internal fun `calls error handler when sending EnergyConsumer throws`() {
        val energyConsumer = EnergyConsumer("id1").also { service.add(it) }

        val expectedEx = StatusRuntimeException(Status.UNAVAILABLE)
        doAnswer { throw expectedEx }.`when`(stub).createEnergyConsumer(any())
        producerClient.send(service)

        verify(stub).createEnergyConsumer(CreateEnergyConsumerRequest.newBuilder().setEnergyConsumer(energyConsumer.toPb()).build())
        assertThat(onErrorHandler.lastError, equalTo(expectedEx))
    }

    @Test
    internal fun `sends EnergyConsumerPhase`() {
        val energyConsumerPhase = EnergyConsumerPhase("id1").also { service.add(it) }

        producerClient.send(service)

        val stubInOrder = inOrder(stub)
        stubInOrder.verify(stub).createNetwork(CreateNetworkRequest.newBuilder().build())
        stubInOrder.verify(stub).createEnergyConsumerPhase(CreateEnergyConsumerPhaseRequest.newBuilder().setEnergyConsumerPhase(energyConsumerPhase.toPb()).build())
        stubInOrder.verify(stub).completeNetwork(CompleteNetworkRequest.newBuilder().build())
    }

    @Test
    internal fun `calls error handler when sending EnergyConsumerPhase throws`() {
        val energyConsumerPhase = EnergyConsumerPhase("id1").also { service.add(it) }

        val expectedEx = StatusRuntimeException(Status.UNAVAILABLE)
        doAnswer { throw expectedEx }.`when`(stub).createEnergyConsumerPhase(any())
        producerClient.send(service)

        verify(stub).createEnergyConsumerPhase(CreateEnergyConsumerPhaseRequest.newBuilder().setEnergyConsumerPhase(energyConsumerPhase.toPb()).build())
        assertThat(onErrorHandler.lastError, equalTo(expectedEx))
    }

    @Test
    internal fun `sends EnergySource`() {
        val energySource = EnergySource("id1").also { service.add(it) }

        producerClient.send(service)

        val stubInOrder = inOrder(stub)
        stubInOrder.verify(stub).createNetwork(CreateNetworkRequest.newBuilder().build())
        stubInOrder.verify(stub).createEnergySource(CreateEnergySourceRequest.newBuilder().setEnergySource(energySource.toPb()).build())
        stubInOrder.verify(stub).completeNetwork(CompleteNetworkRequest.newBuilder().build())
    }

    @Test
    internal fun `calls error handler when sending EnergySource throws`() {
        val energySource = EnergySource("id1").also { service.add(it) }

        val expectedEx = StatusRuntimeException(Status.UNAVAILABLE)
        doAnswer { throw expectedEx }.`when`(stub).createEnergySource(any())
        producerClient.send(service)

        verify(stub).createEnergySource(CreateEnergySourceRequest.newBuilder().setEnergySource(energySource.toPb()).build())
        assertThat(onErrorHandler.lastError, equalTo(expectedEx))
    }

    @Test
    internal fun `sends EnergySourcePhase`() {
        val energySourcePhase = EnergySourcePhase("id1").also { service.add(it) }

        producerClient.send(service)

        val stubInOrder = inOrder(stub)
        stubInOrder.verify(stub).createNetwork(CreateNetworkRequest.newBuilder().build())
        stubInOrder.verify(stub).createEnergySourcePhase(CreateEnergySourcePhaseRequest.newBuilder().setEnergySourcePhase(energySourcePhase.toPb()).build())
        stubInOrder.verify(stub).completeNetwork(CompleteNetworkRequest.newBuilder().build())
    }

    @Test
    internal fun `calls error handler when sending EnergySourcePhase throws`() {
        val energySourcePhase = EnergySourcePhase("id1").also { service.add(it) }

        val expectedEx = StatusRuntimeException(Status.UNAVAILABLE)
        doAnswer { throw expectedEx }.`when`(stub).createEnergySourcePhase(any())
        producerClient.send(service)

        verify(stub).createEnergySourcePhase(CreateEnergySourcePhaseRequest.newBuilder().setEnergySourcePhase(energySourcePhase.toPb()).build())
        assertThat(onErrorHandler.lastError, equalTo(expectedEx))
    }

    @Test
    internal fun `sends Fuse`() {
        val fuse = Fuse("id1").also { service.add(it) }

        producerClient.send(service)

        val stubInOrder = inOrder(stub)
        stubInOrder.verify(stub).createNetwork(CreateNetworkRequest.newBuilder().build())
        stubInOrder.verify(stub).createFuse(CreateFuseRequest.newBuilder().setFuse(fuse.toPb()).build())
        stubInOrder.verify(stub).completeNetwork(CompleteNetworkRequest.newBuilder().build())
    }

    @Test
    internal fun `calls error handler when sending Fuse throws`() {
        val fuse = Fuse("id1").also { service.add(it) }

        val expectedEx = StatusRuntimeException(Status.UNAVAILABLE)
        doAnswer { throw expectedEx }.`when`(stub).createFuse(any())
        producerClient.send(service)

        verify(stub).createFuse(CreateFuseRequest.newBuilder().setFuse(fuse.toPb()).build())
        assertThat(onErrorHandler.lastError, equalTo(expectedEx))
    }

    @Test
    internal fun `sends Jumper`() {
        val jumper = Jumper("id1").also { service.add(it) }

        producerClient.send(service)

        val stubInOrder = inOrder(stub)
        stubInOrder.verify(stub).createNetwork(CreateNetworkRequest.newBuilder().build())
        stubInOrder.verify(stub).createJumper(CreateJumperRequest.newBuilder().setJumper(jumper.toPb()).build())
        stubInOrder.verify(stub).completeNetwork(CompleteNetworkRequest.newBuilder().build())
    }

    @Test
    internal fun `calls error handler when sending Jumper throws`() {
        val jumper = Jumper("id1").also { service.add(it) }

        val expectedEx = StatusRuntimeException(Status.UNAVAILABLE)
        doAnswer { throw expectedEx }.`when`(stub).createJumper(any())
        producerClient.send(service)

        verify(stub).createJumper(CreateJumperRequest.newBuilder().setJumper(jumper.toPb()).build())
        assertThat(onErrorHandler.lastError, equalTo(expectedEx))
    }

    @Test
    internal fun `sends Junction`() {
        val junction = Junction("id1").also { service.add(it) }

        producerClient.send(service)

        val stubInOrder = inOrder(stub)
        stubInOrder.verify(stub).createNetwork(CreateNetworkRequest.newBuilder().build())
        stubInOrder.verify(stub).createJunction(CreateJunctionRequest.newBuilder().setJunction(junction.toPb()).build())
        stubInOrder.verify(stub).completeNetwork(CompleteNetworkRequest.newBuilder().build())
    }

    @Test
    internal fun `calls error handler when sending Junction throws`() {
        val junction = Junction("id1").also { service.add(it) }

        val expectedEx = StatusRuntimeException(Status.UNAVAILABLE)
        doAnswer { throw expectedEx }.`when`(stub).createJunction(any())
        producerClient.send(service)

        verify(stub).createJunction(CreateJunctionRequest.newBuilder().setJunction(junction.toPb()).build())
        assertThat(onErrorHandler.lastError, equalTo(expectedEx))
    }

    @Test
    internal fun `sends LinearShuntCompensator`() {
        val linearShuntCompensator = LinearShuntCompensator("id1").also { service.add(it) }

        producerClient.send(service)

        val stubInOrder = inOrder(stub)
        stubInOrder.verify(stub).createNetwork(CreateNetworkRequest.newBuilder().build())
        stubInOrder.verify(stub).createLinearShuntCompensator(CreateLinearShuntCompensatorRequest.newBuilder().setLinearShuntCompensator(linearShuntCompensator.toPb()).build())
        stubInOrder.verify(stub).completeNetwork(CompleteNetworkRequest.newBuilder().build())
    }

    @Test
    internal fun `calls error handler when sending LinearShuntCompensator throws`() {
        val linearShuntCompensator = LinearShuntCompensator("id1").also { service.add(it) }

        val expectedEx = StatusRuntimeException(Status.UNAVAILABLE)
        doAnswer { throw expectedEx }.`when`(stub).createLinearShuntCompensator(any())
        producerClient.send(service)

        verify(stub).createLinearShuntCompensator(CreateLinearShuntCompensatorRequest.newBuilder().setLinearShuntCompensator(linearShuntCompensator.toPb()).build())
        assertThat(onErrorHandler.lastError, equalTo(expectedEx))
    }

    @Test
    internal fun `sends PerLengthSequenceImpedance`() {
        val perLengthSequenceImpedance = PerLengthSequenceImpedance("id1").also { service.add(it) }

        producerClient.send(service)

        val stubInOrder = inOrder(stub)
        stubInOrder.verify(stub).createNetwork(CreateNetworkRequest.newBuilder().build())
        stubInOrder.verify(stub).createPerLengthSequenceImpedance(CreatePerLengthSequenceImpedanceRequest.newBuilder().setPerLengthSequenceImpedance(perLengthSequenceImpedance.toPb()).build())
        stubInOrder.verify(stub).completeNetwork(CompleteNetworkRequest.newBuilder().build())
    }

    @Test
    internal fun `calls error handler when sending PerLengthSequenceImpedance throws`() {
        val perLengthSequenceImpedance = PerLengthSequenceImpedance("id1").also { service.add(it) }

        val expectedEx = StatusRuntimeException(Status.UNAVAILABLE)
        doAnswer { throw expectedEx }.`when`(stub).createPerLengthSequenceImpedance(any())
        producerClient.send(service)

        verify(stub).createPerLengthSequenceImpedance(CreatePerLengthSequenceImpedanceRequest.newBuilder().setPerLengthSequenceImpedance(perLengthSequenceImpedance.toPb()).build())
        assertThat(onErrorHandler.lastError, equalTo(expectedEx))
    }

    @Test
    internal fun `sends PowerTransformer`() {
        val powerTransformer = PowerTransformer("id1").also { service.add(it) }

        producerClient.send(service)

        val stubInOrder = inOrder(stub)
        stubInOrder.verify(stub).createNetwork(CreateNetworkRequest.newBuilder().build())
        stubInOrder.verify(stub).createPowerTransformer(CreatePowerTransformerRequest.newBuilder().setPowerTransformer(powerTransformer.toPb()).build())
        stubInOrder.verify(stub).completeNetwork(CompleteNetworkRequest.newBuilder().build())
    }

    @Test
    internal fun `calls error handler when sending PowerTransformer throws`() {
        val powerTransformer = PowerTransformer("id1").also { service.add(it) }

        val expectedEx = StatusRuntimeException(Status.UNAVAILABLE)
        doAnswer { throw expectedEx }.`when`(stub).createPowerTransformer(any())
        producerClient.send(service)

        verify(stub).createPowerTransformer(CreatePowerTransformerRequest.newBuilder().setPowerTransformer(powerTransformer.toPb()).build())
        assertThat(onErrorHandler.lastError, equalTo(expectedEx))
    }

    @Test
    internal fun `sends PowerTransformerEnd`() {
        val powerTransformerEnd = PowerTransformerEnd("id1").also { service.add(it) }

        producerClient.send(service)

        val stubInOrder = inOrder(stub)
        stubInOrder.verify(stub).createNetwork(CreateNetworkRequest.newBuilder().build())
        stubInOrder.verify(stub).createPowerTransformerEnd(CreatePowerTransformerEndRequest.newBuilder().setPowerTransformerEnd(powerTransformerEnd.toPb()).build())
        stubInOrder.verify(stub).completeNetwork(CompleteNetworkRequest.newBuilder().build())
    }

    @Test
    internal fun `calls error handler when sending PowerTransformerEnd throws`() {
        val powerTransformerEnd = PowerTransformerEnd("id1").also { service.add(it) }

        val expectedEx = StatusRuntimeException(Status.UNAVAILABLE)
        doAnswer { throw expectedEx }.`when`(stub).createPowerTransformerEnd(any())
        producerClient.send(service)

        verify(stub).createPowerTransformerEnd(CreatePowerTransformerEndRequest.newBuilder().setPowerTransformerEnd(powerTransformerEnd.toPb()).build())
        assertThat(onErrorHandler.lastError, equalTo(expectedEx))
    }

    @Test
    internal fun `sends RatioTapChanger`() {
        val ratioTapChanger = RatioTapChanger("id1").also { service.add(it) }

        producerClient.send(service)

        val stubInOrder = inOrder(stub)
        stubInOrder.verify(stub).createNetwork(CreateNetworkRequest.newBuilder().build())
        stubInOrder.verify(stub).createRatioTapChanger(CreateRatioTapChangerRequest.newBuilder().setRatioTapChanger(ratioTapChanger.toPb()).build())
        stubInOrder.verify(stub).completeNetwork(CompleteNetworkRequest.newBuilder().build())
    }

    @Test
    internal fun `calls error handler when sending RatioTapChanger throws`() {
        val ratioTapChanger = RatioTapChanger("id1").also { service.add(it) }

        val expectedEx = StatusRuntimeException(Status.UNAVAILABLE)
        doAnswer { throw expectedEx }.`when`(stub).createRatioTapChanger(any())
        producerClient.send(service)

        verify(stub).createRatioTapChanger(CreateRatioTapChangerRequest.newBuilder().setRatioTapChanger(ratioTapChanger.toPb()).build())
        assertThat(onErrorHandler.lastError, equalTo(expectedEx))
    }

    @Test
    internal fun `sends Recloser`() {
        val recloser = Recloser("id1").also { service.add(it) }

        producerClient.send(service)

        val stubInOrder = inOrder(stub)
        stubInOrder.verify(stub).createNetwork(CreateNetworkRequest.newBuilder().build())
        stubInOrder.verify(stub).createRecloser(CreateRecloserRequest.newBuilder().setRecloser(recloser.toPb()).build())
        stubInOrder.verify(stub).completeNetwork(CompleteNetworkRequest.newBuilder().build())
    }

    @Test
    internal fun `calls error handler when sending Recloser throws`() {
        val recloser = Recloser("id1").also { service.add(it) }

        val expectedEx = StatusRuntimeException(Status.UNAVAILABLE)
        doAnswer { throw expectedEx }.`when`(stub).createRecloser(any())
        producerClient.send(service)

        verify(stub).createRecloser(CreateRecloserRequest.newBuilder().setRecloser(recloser.toPb()).build())
        assertThat(onErrorHandler.lastError, equalTo(expectedEx))
    }

    @Test
    internal fun `sends Circuit`() {
        val circuit = Circuit("id1").also { service.add(it) }

        producerClient.send(service)

        val stubInOrder = inOrder(stub)
        stubInOrder.verify(stub).createNetwork(CreateNetworkRequest.newBuilder().build())
        stubInOrder.verify(stub).createCircuit(CreateCircuitRequest.newBuilder().setCircuit(circuit.toPb()).build())
        stubInOrder.verify(stub).completeNetwork(CompleteNetworkRequest.newBuilder().build())
    }

    @Test
    internal fun `calls error handler when sending Circuit throws`() {
        val circuit = Circuit("id1").also { service.add(it) }

        val expectedEx = StatusRuntimeException(Status.UNAVAILABLE)
        doAnswer { throw expectedEx }.`when`(stub).createCircuit(any())
        producerClient.send(service)

        verify(stub).createCircuit(CreateCircuitRequest.newBuilder().setCircuit(circuit.toPb()).build())
        assertThat(onErrorHandler.lastError, equalTo(expectedEx))
    }

    @Test
    internal fun `sends Loop`() {
        val loop = Loop("id1").also { service.add(it) }

        producerClient.send(service)

        val stubInOrder = inOrder(stub)
        stubInOrder.verify(stub).createNetwork(CreateNetworkRequest.newBuilder().build())
        stubInOrder.verify(stub).createLoop(CreateLoopRequest.newBuilder().setLoop(loop.toPb()).build())
        stubInOrder.verify(stub).completeNetwork(CompleteNetworkRequest.newBuilder().build())
    }

    @Test
    internal fun `calls error handler when sending Loop throws`() {
        val loop = Loop("id1").also { service.add(it) }

        val expectedEx = StatusRuntimeException(Status.UNAVAILABLE)
        doAnswer { throw expectedEx }.`when`(stub).createLoop(any())
        producerClient.send(service)

        verify(stub).createLoop(CreateLoopRequest.newBuilder().setLoop(loop.toPb()).build())
        assertThat(onErrorHandler.lastError, equalTo(expectedEx))
    }
}

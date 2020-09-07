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
package com.zepben.cimbend.common.translator

import com.zepben.cimbend.cim.iec61968.assetinfo.CableInfo
import com.zepben.cimbend.cim.iec61968.assetinfo.OverheadWireInfo
import com.zepben.cimbend.cim.iec61968.assets.AssetOwner
import com.zepben.cimbend.cim.iec61968.common.Location
import com.zepben.cimbend.cim.iec61968.common.Organisation
import com.zepben.cimbend.cim.iec61968.customers.Customer
import com.zepben.cimbend.cim.iec61968.customers.CustomerAgreement
import com.zepben.cimbend.cim.iec61968.customers.PricingStructure
import com.zepben.cimbend.cim.iec61968.customers.Tariff
import com.zepben.cimbend.cim.iec61968.metering.Meter
import com.zepben.cimbend.cim.iec61968.metering.UsagePoint
import com.zepben.cimbend.cim.iec61968.operations.OperationalRestriction
import com.zepben.cimbend.cim.iec61970.base.auxiliaryequipment.FaultIndicator
import com.zepben.cimbend.cim.iec61970.base.core.*
import com.zepben.cimbend.cim.iec61970.base.diagramlayout.Diagram
import com.zepben.cimbend.cim.iec61970.base.diagramlayout.DiagramObject
import com.zepben.cimbend.cim.iec61970.base.diagramlayout.DiagramObjectPoint
import com.zepben.cimbend.cim.iec61970.base.diagramlayout.DiagramObjectStyle
import com.zepben.cimbend.cim.iec61970.base.meas.Control
import com.zepben.cimbend.cim.iec61970.base.meas.*
import com.zepben.cimbend.cim.iec61970.base.scada.RemoteControl
import com.zepben.cimbend.cim.iec61970.base.scada.RemoteSource
import com.zepben.cimbend.cim.iec61970.base.wires.*
import com.zepben.cimbend.common.BaseService
import com.zepben.cimbend.common.BaseServiceComparator
import com.zepben.cimbend.customer.CustomerService
import com.zepben.cimbend.customer.translator.addFromPb
import com.zepben.cimbend.customer.translator.toPb
import com.zepben.cimbend.diagram.DiagramService
import com.zepben.cimbend.diagram.DiagramServiceComparator
import com.zepben.cimbend.diagram.addFromPb
import com.zepben.cimbend.diagram.toPb
import com.zepben.cimbend.measurement.MeasurementService
import com.zepben.cimbend.measurement.addFromPb
import com.zepben.cimbend.measurement.toPb
import com.zepben.cimbend.network.NetworkService
import com.zepben.cimbend.network.NetworkServiceComparator
import com.zepben.cimbend.network.SchemaTestNetwork
import com.zepben.cimbend.network.model.addFromPb
import com.zepben.cimbend.network.model.toPb
import com.zepben.cimbend.testdata.TestDataCreators.createTerminal
import com.zepben.test.util.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class CimPbTest {
    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()


    @Test
    fun cimToPbToCim() {
        val (sourceNetworkService, sourceDiagramService, sourceCustomer, sourceMeasurement) = SchemaTestNetwork.createStupidlyLargeServices()

        testNetworkService(sourceNetworkService)
        testDiagramService(sourceDiagramService)
        testCustomerService(sourceCustomer)
        testMeasurementService(sourceMeasurement)
    }

    @Test
    fun `test terminals retain their order`() {
        val networkService = NetworkService()
        val jc = Junction("j1")
        val acls= AcLineSegment("acls1")
        val firstTerm = createTerminal(networkService, jc, PhaseCode.ABC, 1)
        val secondTerm= createTerminal(networkService, jc, PhaseCode.ABC, 2)
        networkService.add(jc)
        networkService.add(acls)
        jc.addTerminal(firstTerm)
        jc.addTerminal(secondTerm)
        // Order should be maintained (no differences) regardless of the order they are resolved
        // e.g resolved after ConductingEquipment is added or before.
        var targetNetwork = NetworkService()
        networkService.sequenceOf<AcLineSegment>().forEach { targetNetwork.addFromPb(it.toPb()) }
        networkService.sequenceOf<Junction>().forEach { targetNetwork.addFromPb(it.toPb()) }
        networkService.sequenceOf<Terminal>().forEach { targetNetwork.addFromPb(it.toPb()) }
        validateService(networkService, targetNetwork) { NetworkServiceComparator() }

        targetNetwork = NetworkService()
        networkService.sequenceOf<Terminal>().forEach { targetNetwork.addFromPb(it.toPb()) }
        networkService.sequenceOf<AcLineSegment>().forEach { targetNetwork.addFromPb(it.toPb()) }
        networkService.sequenceOf<Junction>().forEach { targetNetwork.addFromPb(it.toPb()) }
        validateService(networkService, targetNetwork) { NetworkServiceComparator() }
    }

    @Test
    fun `test diagram service resolution`(){
        val diagramService = DiagramService()
        val diagram1 = Diagram("diagram1")
        val diagramObject1 = DiagramObject("do1").apply {
            this.diagram = diagram1
            identifiedObjectMRID = "fakeObj"
            style = DiagramObjectStyle.CB
            rotation = 90.0
            addPoint(DiagramObjectPoint(1.0, 1.0))
            addPoint(DiagramObjectPoint(2.0, 2.0))
        }
        val diagramObject2 = DiagramObject("do2").apply {
            this.diagram = diagram1
            identifiedObjectMRID = "fakeObj2"
            style = DiagramObjectStyle.CB
            rotation = 90.0
            addPoint(DiagramObjectPoint(1.0, 1.0))
            addPoint(DiagramObjectPoint(2.0, 2.0))
        }
        diagram1.addDiagramObject(diagramObject1)
        diagram1.addDiagramObject(diagramObject2)

        diagramService.add(diagram1)
        diagramService.add(diagramObject1)
        diagramService.add(diagramObject2)

        // Test both orders of addition
        var targetService = DiagramService()
        diagramService.sequenceOf<Diagram>().forEach { targetService.addFromPb(it.toPb()) }
        diagramService.sequenceOf<DiagramObject>().forEach { targetService.addFromPb(it.toPb()) }
        validateService(diagramService, targetService) { DiagramServiceComparator() }

        targetService = DiagramService()
        diagramService.sequenceOf<DiagramObject>().forEach { targetService.addFromPb(it.toPb()) }
        diagramService.sequenceOf<Diagram>().forEach { targetService.addFromPb(it.toPb()) }
        validateService(diagramService, targetService) { DiagramServiceComparator() }
    }

    private fun testNetworkService(sourceNetworkService: NetworkService) {
        val targetNetwork = NetworkService()

        sourceNetworkService.sequenceOf<IdentifiedObject>().forEach {
            when (it) {
                is CableInfo -> targetNetwork.addFromPb(it.toPb())
                is OverheadWireInfo -> targetNetwork.addFromPb(it.toPb())
                is Meter -> targetNetwork.addFromPb(it.toPb())
                is OperationalRestriction -> targetNetwork.addFromPb(it.toPb())
                is AssetOwner -> targetNetwork.addFromPb(it.toPb())
                is Location -> targetNetwork.addFromPb(it.toPb())
                is UsagePoint -> targetNetwork.addFromPb(it.toPb())
                is ConnectivityNode -> targetNetwork.addFromPb(it.toPb())
                is BaseVoltage -> targetNetwork.addFromPb(it.toPb())
                is Junction -> targetNetwork.addFromPb(it.toPb())
                is AcLineSegment -> targetNetwork.addFromPb(it.toPb())
                is LinearShuntCompensator -> targetNetwork.addFromPb(it.toPb())
                is EnergyConsumer -> targetNetwork.addFromPb(it.toPb())
                is EnergySource -> targetNetwork.addFromPb(it.toPb())
                is PowerTransformer -> targetNetwork.addFromPb(it.toPb())
                is Disconnector -> targetNetwork.addFromPb(it.toPb())
                is Fuse -> targetNetwork.addFromPb(it.toPb())
                is Jumper -> targetNetwork.addFromPb(it.toPb())
                is Recloser -> targetNetwork.addFromPb(it.toPb())
                is Breaker -> targetNetwork.addFromPb(it.toPb())
                is FaultIndicator -> targetNetwork.addFromPb(it.toPb())
                is Feeder -> targetNetwork.addFromPb(it.toPb())
                is Site -> targetNetwork.addFromPb(it.toPb())
                is Substation -> targetNetwork.addFromPb(it.toPb())
                is EnergySourcePhase -> targetNetwork.addFromPb(it.toPb())
                is EnergyConsumerPhase -> targetNetwork.addFromPb(it.toPb())
                is RatioTapChanger -> targetNetwork.addFromPb(it.toPb())
                is GeographicalRegion -> targetNetwork.addFromPb(it.toPb())
                is SubGeographicalRegion -> targetNetwork.addFromPb(it.toPb())
                is Terminal -> targetNetwork.addFromPb(it.toPb())
                is PerLengthSequenceImpedance -> targetNetwork.addFromPb(it.toPb())
                is PowerTransformerEnd -> targetNetwork.addFromPb(it.toPb())
                is Organisation -> targetNetwork.addFromPb(it.toPb())
                is Control -> targetNetwork.addFromPb(it.toPb())
                is RemoteControl -> targetNetwork.addFromPb(it.toPb())
                is RemoteSource -> targetNetwork.addFromPb(it.toPb())
                is Analog -> targetNetwork.addFromPb(it.toPb())
                is Accumulator -> targetNetwork.addFromPb(it.toPb())
                is Discrete -> targetNetwork.addFromPb(it.toPb())
                else -> throw AssertionError("unknown type ${it::class}")
            }
        }

        validateService(sourceNetworkService, targetNetwork) { NetworkServiceComparator() }
    }

    private fun testDiagramService(sourceDiagramService: DiagramService) {
        val targetDiagramService = DiagramService()

        sourceDiagramService.sequenceOf<IdentifiedObject>().forEach {
            when (it) {
                is Diagram -> targetDiagramService.addFromPb(it.toPb())
                is DiagramObject -> targetDiagramService.addFromPb(it.toPb())
                else -> throw AssertionError("unknown type ${it::class}")
            }
        }

        validateService(sourceDiagramService, targetDiagramService) { DiagramServiceComparator() }
    }

    private fun testCustomerService(sourceCustomerService: CustomerService) {
        val targetCustomerService = CustomerService()

        sourceCustomerService.sequenceOf<IdentifiedObject>().forEach {
            when (it) {
                is Customer -> targetCustomerService.addFromPb(it.toPb())
                is CustomerAgreement -> targetCustomerService.addFromPb(it.toPb())
                is PricingStructure -> targetCustomerService.addFromPb(it.toPb())
                is Tariff -> targetCustomerService.addFromPb(it.toPb())
                is Organisation -> targetCustomerService.addFromPb(it.toPb())
                else -> throw AssertionError("unknown type ${it::class}")
            }
        }

        validateService(sourceCustomerService, targetCustomerService) { DiagramServiceComparator() }

    }

    private fun testMeasurementService(sourceMeasurementService: MeasurementService) {
        val targetMeasurementService = MeasurementService()

    }

    private fun validateService(expectedService: BaseService, service: BaseService, getComparator: () -> BaseServiceComparator) {
        val differences = getComparator().compare(expectedService, service)

        System.err.println(differences.toString())

        assertThat("objects missing from actual ${service.name} service", differences.missingFromTarget(), Matchers.empty())
        assertThat("unexpected modifications for ${service.name} service", differences.modifications(), Matchers.anEmptyMap())
        assertThat("unexpected objects found in actual ${service.name} service", differences.missingFromSource(), Matchers.empty())
    }
}

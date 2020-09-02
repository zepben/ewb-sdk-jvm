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

package com.zepben.cimbendput

import com.zepben.cimbend.cim.iec61968.assetinfo.CableInfo
import com.zepben.cimbend.customer.CustomerService
import com.zepben.cimbend.diagram.DiagramService
import com.zepben.cimbend.network.NetworkService
import com.zepben.cimbendput.grpc.GrpcChannelFactory
import org.junit.Ignore
import org.junit.Test
import java.util.concurrent.TimeUnit

@Ignore
internal class EndToEndTest {

    val channel = GrpcChannelFactory.create(ConnectionConfig("localhost", 50051))

    @Test
    internal fun `connects and sends to a cimcap server`() {
        try {
            val networkService = NetworkService()
            val customerService = CustomerService()
            val diagramService = DiagramService()
            populateNetworkObjects(networkService)

            NetworkProducerClient(channel).send(networkService)
            CustomerProducerClient(channel).send(customerService)
            DiagramProducerClient(channel).send(diagramService)
        } finally {
            channel.shutdown()
            channel.awaitTermination(5, TimeUnit.SECONDS)
        }
    }

    private fun populateNetworkObjects(networkService: NetworkService) {
        networkService.add(CableInfo("CableInfo1"))
//        networkService.add(OverheadWireInfo("OverheadWireInfo1"))
//        networkService.add(AssetOwner("AssetOwner1"))
//        networkService.add(Location("Location1"))
//        networkService.add(Organisation("Organisation1"))
//        networkService.add(Meter("Meter1"))
//        networkService.add(UsagePoint("UsagePoint1"))
//        networkService.add(OperationalRestriction("OperationalRestriction1"))
//        networkService.add(FaultIndicator("FaultIndicator1"))
//        networkService.add(BaseVoltage("BaseVoltage1"))
//        networkService.add(ConnectivityNode("ConnectivityNode1"))
//        networkService.add(Feeder("Feeder1"))
//        networkService.add(GeographicalRegion("GeographicalRegion1"))
//        networkService.add(Site("Site1"))
//        networkService.add(SubGeographicalRegion("SubGeographicalRegion1"))
//        networkService.add(Substation("Substation1"))
//        networkService.add(Terminal("Terminal1"))
//        networkService.add(AcLineSegment("AcLineSegment1"))
//        networkService.add(Breaker("Breaker1"))
//        networkService.add(Disconnector("Disconnector1"))
//        networkService.add(EnergyConsumer("EnergyConsumer1"))
//        networkService.add(EnergyConsumerPhase("EnergyConsumerPhase1"))
//        networkService.add(EnergySource("EnergySource1"))
//        networkService.add(EnergySourcePhase("EnergySourcePhase1"))
//        networkService.add(Fuse("Fuse1"))
//        networkService.add(Jumper("Jumper1"))
//        networkService.add(Junction("Junction1"))
//        networkService.add(LinearShuntCompensator("LinearShuntCompensator1"))
//        networkService.add(PerLengthSequenceImpedance("PerLengthSequenceImpedance1"))
//        networkService.add(PowerTransformer("PowerTransformer1"))
//        networkService.add(PowerTransformerEnd("PowerTransformerEnd1"))
//        networkService.add(RatioTapChanger("RatioTapChanger1"))
//        networkService.add(Recloser("Recloser1"))
    }
}

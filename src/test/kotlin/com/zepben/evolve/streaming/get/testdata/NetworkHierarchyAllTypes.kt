/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.streaming.get.testdata

import com.zepben.evolve.cim.iec61970.base.core.*
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.Circuit
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.Loop
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.streaming.get.ConsumerUtils.buildFromBuilder
import com.zepben.evolve.streaming.get.hierarchy.NetworkHierarchy
import com.zepben.protobuf.nc.GetNetworkHierarchyResponse

object NetworkHierarchyAllTypes {

    fun createResponse(): GetNetworkHierarchyResponse {
        val response = GetNetworkHierarchyResponse.newBuilder()

        buildFromBuilder(
            response.addGeographicalRegionsBuilder(),
            "setMRID" to "gr1",
            "setName" to "gr 1",
            "addAllSubGeographicalRegionMRIDs" to listOf("sgr1", "sgr2")
        )
        buildFromBuilder(response.addGeographicalRegionsBuilder(), "setMRID" to "gr2", "setName" to "gr 2")

        buildFromBuilder(
            response.addSubGeographicalRegionsBuilder(),
            "setMRID" to "sgr1",
            "setName" to "sgr 1",
            "addAllSubstationMRIDs" to listOf("sub1", "sub2")
        )
        buildFromBuilder(response.addSubGeographicalRegionsBuilder(), "setMRID" to "sgr2", "setName" to "sgr 2")

        buildFromBuilder(
            response.addSubstationsBuilder(),
            "setMRID" to "sub1",
            "setName" to "sub 1",
            "addAllNormalEnergizedFeederMRIDs" to listOf("fdr1", "fdr2")
        )
        buildFromBuilder(response.addSubstationsBuilder(), "setMRID" to "sub2", "setName" to "sub 2")

        buildFromBuilder(response.addFeedersBuilder(), "setMRID" to "fdr1", "setName" to "fdr 1")
        buildFromBuilder(response.addFeedersBuilder(), "setMRID" to "fdr2", "setName" to "fdr 2")

        buildFromBuilder(response.addCircuitsBuilder(), "setMRID" to "cir1", "setName" to "cir 1", "addAllEndSubstationMRIDs" to listOf("sub1", "sub2"))
        buildFromBuilder(response.addCircuitsBuilder(), "setMRID" to "cir2", "setName" to "cir 2")

        buildFromBuilder(
            response.addLoopsBuilder(),
            "setMRID" to "loop1",
            "setName" to "loop 1",
            "addAllCircuitMRIDs" to listOf("cir1", "cir2"),
            "addAllSubstationMRIDs" to listOf("sub1", "sub2")
        )
        buildFromBuilder(response.addLoopsBuilder(), "setMRID" to "loop2", "setName" to "loop 2")

        return response.build()
    }

    fun createService() = NetworkService().also { service ->
        val loop1 = Loop("loop1").apply { name = "loop 1" }.also { service.add(it) }
        Loop("loop2").apply { name = "loop 2" }.also { service.add(it) }

        val cir1 = Circuit("cir1").apply { name = "cir 1"; loop = loop1 }.also { loop1.addCircuit(it); service.add(it) }
        Circuit("cir2").apply { name = "cir 2"; loop = loop1 }.also { loop1.addCircuit(it); service.add(it) }

        val gr1 = GeographicalRegion("gr1").apply { name = "gr 1" }.also { service.add(it) }
        GeographicalRegion("gr2").apply { name = "gr 2" }.also { service.add(it) }

        val sgr1 = SubGeographicalRegion("sgr1").apply { name = "sgr 1"; geographicalRegion = gr1 }.also { gr1.addSubGeographicalRegion(it); service.add(it) }
        SubGeographicalRegion("sgr2").apply { name = "sgr 2"; geographicalRegion = gr1 }.also { gr1.addSubGeographicalRegion(it); service.add(it) }

        val sub1 = Substation("sub1").apply { name = "sub 1"; subGeographicalRegion = sgr1; addCircuit(cir1); addLoop(loop1) }.also {
            sgr1.addSubstation(it)
            cir1.addEndSubstation(it)
            loop1.addSubstation(it)
            service.add(it)
        }
        Substation("sub2").apply { name = "sub 2"; subGeographicalRegion = sgr1; addCircuit(cir1); addLoop(loop1) }.also {
            sgr1.addSubstation(it)
            cir1.addEndSubstation(it)
            loop1.addSubstation(it)
            service.add(it)
        }

        Feeder("fdr1").apply { name = "fdr 1"; normalEnergizingSubstation = sub1 }.also { sub1.addFeeder(it); service.add(it) }
        Feeder("fdr2").apply { name = "fdr 2"; normalEnergizingSubstation = sub1 }.also { sub1.addFeeder(it); service.add(it) }
    }

    fun createNetworkHierarchy() = createService().let {
        NetworkHierarchy(it.mapOf(), it.mapOf(), it.mapOf(), it.mapOf(), it.mapOf(), it.mapOf())
    }

    private fun <T : IdentifiedObject> mapOf(vararg items: T): Map<String, T> = items.associateBy { it.mRID }

}

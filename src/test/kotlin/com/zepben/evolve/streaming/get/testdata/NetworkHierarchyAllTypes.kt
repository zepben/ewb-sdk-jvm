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

        buildFromBuilder(response.addSubGeographicalRegionsBuilder(), "setMRID" to "sgr1", "setName" to "sgr 1", "addAllSubstationMRIDs" to listOf("s1", "s2"))
        buildFromBuilder(response.addSubGeographicalRegionsBuilder(), "setMRID" to "sgr2", "setName" to "sgr 2")

        buildFromBuilder(response.addSubstationsBuilder(), "setMRID" to "s1", "setName" to "s 1", "addAllNormalEnergizedFeederMRIDs" to listOf("f1", "f2"))
        buildFromBuilder(response.addSubstationsBuilder(), "setMRID" to "s2", "setName" to "s 2")

        buildFromBuilder(response.addFeedersBuilder(), "setMRID" to "f1", "setName" to "f 1")
        buildFromBuilder(response.addFeedersBuilder(), "setMRID" to "f2", "setName" to "f 2")

        buildFromBuilder(response.addCircuitsBuilder(), "setMRID" to "c1", "setName" to "c 1", "addAllEndSubstationMRIDs" to listOf("s1", "s2"))
        buildFromBuilder(response.addCircuitsBuilder(), "setMRID" to "c2", "setName" to "c 2")

        buildFromBuilder(
            response.addLoopsBuilder(),
            "setMRID" to "loop1",
            "setName" to "loop 1",
            "addAllCircuitMRIDs" to listOf("c1", "c2"),
            "addAllSubstationMRIDs" to listOf("s1", "s2")
        )
        buildFromBuilder(response.addLoopsBuilder(), "setMRID" to "loop2", "setName" to "loop 2")

        return response.build()
    }

    fun createNetworkHierarchy(): NetworkHierarchy {
        val loop1 = Loop("loop1").apply { name = "loop 1" }
        val loop2 = Loop("loop2").apply { name = "loop 2" }

        val c1 = Circuit("c1").apply { name = "c 1"; loop = loop1 }.also { loop1.addCircuit(it) }
        val c2 = Circuit("c2").apply { name = "c 2"; loop = loop1 }.also { loop1.addCircuit(it) }

        val gr1 = GeographicalRegion("gr1").apply { name = "gr 1" }
        val gr2 = GeographicalRegion("gr2").apply { name = "gr 2" }

        val sgr1 = SubGeographicalRegion("sgr1").apply { name = "sgr 1"; geographicalRegion = gr1 }.also { gr1.addSubGeographicalRegion(it) }
        val sgr2 = SubGeographicalRegion("sgr2").apply { name = "sgr 2"; geographicalRegion = gr1 }.also { gr1.addSubGeographicalRegion(it) }

        val s1 = Substation("s1").apply { name = "s 1"; subGeographicalRegion = sgr1; addCircuit(c1); addLoop(loop1) }.also {
            sgr1.addSubstation(it)
            c1.addEndSubstation(it)
            loop1.addSubstation(it)
        }
        val s2 = Substation("s2").apply { name = "s 2"; subGeographicalRegion = sgr1; addCircuit(c1); addLoop(loop1) }.also {
            sgr1.addSubstation(it)
            c1.addEndSubstation(it)
            loop1.addSubstation(it)
        }

        val f1 = Feeder("f1").apply { name = "f 1"; normalEnergizingSubstation = s1 }.also { s1.addFeeder(it) }
        val f2 = Feeder("f2").apply { name = "f 2"; normalEnergizingSubstation = s1 }.also { s1.addFeeder(it) }


        return NetworkHierarchy(mapOf(gr1, gr2), mapOf(sgr1, sgr2), mapOf(s1, s2), mapOf(f1, f2), mapOf(c1, c2), mapOf(loop1, loop2))
    }

    private fun <T : IdentifiedObject> mapOf(vararg items: T): Map<String, T> = items.associateBy { it.mRID }

}

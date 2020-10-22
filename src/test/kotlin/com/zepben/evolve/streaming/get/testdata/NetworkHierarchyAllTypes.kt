/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.streaming.get.testdata

import com.zepben.evolve.streaming.get.hierarchy.*
import com.zepben.protobuf.nc.GetNetworkHierarchyResponse

object NetworkHierarchyAllTypes {

    fun createResponse(): GetNetworkHierarchyResponse {
        val response = GetNetworkHierarchyResponse.newBuilder()

        response.addFeedersBuilder().setMRID("f1").setName("f 1").build()
        response.addFeedersBuilder().setMRID("f2").setName("f 2").build()

        response.addSubstationsBuilder().setMRID("s1").setName("s 1").addAllFeederMrids(listOf("f1", "f2")).build()
        response.addSubstationsBuilder().setMRID("s2").setName("s 2").build()

        response.addSubGeographicalRegionsBuilder().setMRID("sgr1").setName("sgr 1").addAllSubstationMrids(listOf("s1", "s2")).build()
        response.addSubGeographicalRegionsBuilder().setMRID("sgr2").setName("sgr 2").build()

        response.addGeographicalRegionsBuilder().setMRID("gr1").setName("gr 1").addAllSubGeographicalRegionMrids(listOf("sgr1", "sgr2")).build()
        response.addGeographicalRegionsBuilder().setMRID("gr2").setName("gr 2").build()

        return response.build()
    }

    fun createNetworkHierarchy(): NetworkHierarchy {
        val f1 = NetworkHierarchyFeeder("f1", "f 1")
        val f2 = NetworkHierarchyFeeder("f2", "f 2")

        val s1 = NetworkHierarchySubstation("s1", "s 1", mapOf(f1, f2))
        val s2 = NetworkHierarchySubstation("s2", "s 2", emptyMap())

        val sgr1 = NetworkHierarchySubGeographicalRegion("sgr1", "sgr 1", mapOf(s1, s2))
        val sgr2 = NetworkHierarchySubGeographicalRegion("sgr2", "sgr 2", emptyMap())

        val gr1 = NetworkHierarchyGeographicalRegion("gr1", "gr 1", mapOf(sgr1, sgr2))
        val gr2 = NetworkHierarchyGeographicalRegion("gr2", "gr 2", emptyMap())

        f1.substation = s1
        f2.substation = s1

        s1.subGeographicalRegion = sgr1
        s2.subGeographicalRegion = sgr1

        sgr1.geographicalRegion = gr1
        sgr2.geographicalRegion = gr1

        return NetworkHierarchy(mapOf(gr1, gr2), mapOf(sgr1, sgr2), mapOf(s1, s2), mapOf(f1, f2))
    }

    private fun <T : NetworkHierarchyIdentifiedObject> mapOf(vararg items: T): Map<String, T> = items.associateBy { it.mRID }

}
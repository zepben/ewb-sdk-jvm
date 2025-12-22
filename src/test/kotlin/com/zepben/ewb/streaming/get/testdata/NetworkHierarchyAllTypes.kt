/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.streaming.get.testdata

import com.google.protobuf.NullValue
import com.zepben.ewb.cim.extensions.iec61970.base.feeder.Loop
import com.zepben.ewb.cim.extensions.iec61970.base.feeder.LvFeeder
import com.zepben.ewb.cim.extensions.iec61970.base.feeder.LvSubstation
import com.zepben.ewb.cim.iec61970.base.core.Feeder
import com.zepben.ewb.cim.iec61970.base.core.GeographicalRegion
import com.zepben.ewb.cim.iec61970.base.core.SubGeographicalRegion
import com.zepben.ewb.cim.iec61970.base.core.Substation
import com.zepben.ewb.cim.iec61970.infiec61970.feeder.Circuit
import com.zepben.ewb.services.network.NetworkService
import com.zepben.ewb.streaming.get.ConsumerUtils.buildFromBuilder
import com.zepben.ewb.streaming.get.hierarchy.NetworkHierarchy
import com.zepben.protobuf.nc.GetNetworkHierarchyResponse

object NetworkHierarchyAllTypes {

    fun createResponse(
        includeGeographicalRegions: Boolean = false,
        includeSubgeographicalRegions: Boolean = false,
        includeSubstations: Boolean = false,
        includeFeeders: Boolean = false,
        includeCircuits: Boolean = false,
        includeLoops: Boolean = false,
        includeLvSubstations: Boolean = false,
        includeLvFeeders: Boolean = false,
    ): GetNetworkHierarchyResponse {
        val response = GetNetworkHierarchyResponse.newBuilder()

        if (includeGeographicalRegions) {
            val geoRegionsBuilder = response.addGeographicalRegionsBuilder()
            buildFromBuilder(
                geoRegionsBuilder,
                "setMRID" to "gr1",
                "setNameSet" to "gr 1",
                "setDescriptionNull" to NullValue.NULL_VALUE,
                "setNumDiagramObjectsNull" to NullValue.NULL_VALUE,
            )

            if (includeSubgeographicalRegions) {
                buildFromBuilder(geoRegionsBuilder, "addAllSubGeographicalRegionMRIDs" to listOf("sgr1", "sgr2"))
            }

            buildFromBuilder(
                response.addGeographicalRegionsBuilder(),
                "setMRID" to "gr2",
                "setNameSet" to "gr 2",
                "setDescriptionNull" to NullValue.NULL_VALUE,
                "setNumDiagramObjectsNull" to NullValue.NULL_VALUE
            )
        }

        if (includeSubgeographicalRegions) {
            val subgeoBuilder = response.addSubGeographicalRegionsBuilder()
            buildFromBuilder(
                subgeoBuilder,
                "setMRID" to "sgr1",
                "setNameSet" to "sgr 1",
                "setDescriptionNull" to NullValue.NULL_VALUE,
                "setNumDiagramObjectsNull" to NullValue.NULL_VALUE,
            )
            if (includeSubstations) {
                buildFromBuilder(subgeoBuilder, "addAllSubstationMRIDs" to listOf("sub1", "sub2"))
            }

            buildFromBuilder(
                response.addSubGeographicalRegionsBuilder(),
                "setMRID" to "sgr2",
                "setNameSet" to "sgr 2",
                "setDescriptionNull" to NullValue.NULL_VALUE,
                "setNumDiagramObjectsNull" to NullValue.NULL_VALUE
            )
        }

        if (includeSubstations) {
            val substationBuilder = response.addSubstationsBuilder()
            buildFromBuilder(
                substationBuilder,
                "setMRID" to "sub1",
                "setNameSet" to "sub 1",
                "setDescriptionNull" to NullValue.NULL_VALUE,
                "setNumDiagramObjectsNull" to NullValue.NULL_VALUE,
                "setNumControlsNull" to NullValue.NULL_VALUE,
                "addAllNormalEnergizedFeederMRIDs" to listOf("fdr1", "fdr2")
            )
            if (includeFeeders)
                buildFromBuilder(substationBuilder, "addAllNormalEnergizedFeederMRIDs" to listOf("fdr1", "fdr2"))

            buildFromBuilder(
                response.addSubstationsBuilder(),
                "setMRID" to "sub2",
                "setNameSet" to "sub 2",
                "setDescriptionNull" to NullValue.NULL_VALUE,
                "setNumDiagramObjectsNull" to NullValue.NULL_VALUE,
                "setNumControlsNull" to NullValue.NULL_VALUE,
            )
        }

        if (includeFeeders) {
            buildFromBuilder(
                response.addFeedersBuilder(),
                "setMRID" to "fdr1",
                "setNameSet" to "fdr 1",
                "setDescriptionNull" to NullValue.NULL_VALUE,
                "setNumDiagramObjectsNull" to NullValue.NULL_VALUE,
                "setNumControlsNull" to NullValue.NULL_VALUE,
            )
            buildFromBuilder(
                response.addFeedersBuilder(),
                "setMRID" to "fdr2",
                "setNameSet" to "fdr 2",
                "setDescriptionNull" to NullValue.NULL_VALUE,
                "setNumDiagramObjectsNull" to NullValue.NULL_VALUE,
                "setNumControlsNull" to NullValue.NULL_VALUE,
            )
        }

        if (includeCircuits) {
            val circuitsBuilder = response.addCircuitsBuilder()
            buildFromBuilder(
                circuitsBuilder,
                "setMRID" to "cir1",
                "setNameSet" to "cir 1",
                "setDescriptionNull" to NullValue.NULL_VALUE,
                "setNumDiagramObjectsNull" to NullValue.NULL_VALUE,
                "setNumControlsNull" to NullValue.NULL_VALUE,
            )
            if (includeSubstations) {
                buildFromBuilder(circuitsBuilder, "addAllEndSubstationMRIDs" to listOf("sub1", "sub2"))
            }
            buildFromBuilder(
                response.addCircuitsBuilder(),
                "setMRID" to "cir2",
                "setNameSet" to "cir 2",
                "setDescriptionNull" to NullValue.NULL_VALUE,
                "setNumDiagramObjectsNull" to NullValue.NULL_VALUE,
                "setNumControlsNull" to NullValue.NULL_VALUE,
            )
        }

        if (includeLoops) {
            val loopBuilder = response.addLoopsBuilder()
            buildFromBuilder(
                loopBuilder,
                "setMRID" to "loop1",
                "setNameSet" to "loop 1",
                "setDescriptionNull" to NullValue.NULL_VALUE,
                "setNumDiagramObjectsNull" to NullValue.NULL_VALUE,
                "setNumControlsNull" to NullValue.NULL_VALUE
            )
            if (includeSubstations)
                buildFromBuilder(loopBuilder, "addAllSubstationMRIDs" to listOf("sub1", "sub2"))
            if (includeCircuits)
                buildFromBuilder(loopBuilder, "addAllCircuitMRIDs" to listOf("cir1", "cir2"))

            buildFromBuilder(
                response.addLoopsBuilder(),
                "setMRID" to "loop2",
                "setNameSet" to "loop 2",
                "setDescriptionNull" to NullValue.NULL_VALUE,
                "setNumDiagramObjectsNull" to NullValue.NULL_VALUE,
                "setNumControlsNull" to NullValue.NULL_VALUE,
            )
        }

        if (includeLvSubstations) {
            buildFromBuilder(
                response.addLvSubstationsBuilder(),
                "setMRID" to "lvsub1",
                "setNameSet" to "lvsub 1",
                "setDescriptionNull" to NullValue.NULL_VALUE,
                "setNumDiagramObjectsNull" to NullValue.NULL_VALUE,
                "setNumControlsNull" to NullValue.NULL_VALUE
            )
            buildFromBuilder(
                response.addLvSubstationsBuilder(),
                "setMRID" to "lvsub2",
                "setNameSet" to "lvsub2 2",
                "setDescriptionNull" to NullValue.NULL_VALUE,
                "setNumDiagramObjectsNull" to NullValue.NULL_VALUE,
                "setNumControlsNull" to NullValue.NULL_VALUE,
            )
        }

        if (includeLvFeeders) {
            buildFromBuilder(
                response.addLvFeedersBuilder(),
                "setMRID" to "lvfdr1",
                "setNameSet" to "lvfdr 1",
                "setDescriptionNull" to NullValue.NULL_VALUE,
                "setNumDiagramObjectsNull" to NullValue.NULL_VALUE,
                "setNumControlsNull" to NullValue.NULL_VALUE,
            )
            buildFromBuilder(
                response.addLvFeedersBuilder(),
                "setMRID" to "lvfdr2",
                "setNameSet" to "lvfdr 2",
                "setDescriptionNull" to NullValue.NULL_VALUE,
                "setNumDiagramObjectsNull" to NullValue.NULL_VALUE,
                "setNumControlsNull" to NullValue.NULL_VALUE,
            )
        }
        return response.build()
    }

    fun createService(
        includeGeographicalRegions: Boolean = false,
        includeSubgeographicalRegions: Boolean = false,
        includeSubstations: Boolean = false,
        includeFeeders: Boolean = false,
        includeCircuits: Boolean = false,
        includeLoops: Boolean = false,
        includeLvSubstations: Boolean = false,
        includeLvFeeders: Boolean = false,
    ): NetworkService = NetworkService().also { service ->
        val loop1 = Loop("loop1").apply { name = "loop 1" }.also { service.add(it) }
        Loop("loop2").apply { name = "loop 2" }.also { service.add(it) }

        val cir1 = Circuit("cir1").apply { name = "cir 1"
            if (includeLoops) {
                loop = loop1
                loop1.addCircuit(this)
            }
            service.add(this)
        }

        Circuit("cir2").apply {
            name = "cir 2"
            if (includeLoops) {
                loop = loop1
                loop1.addCircuit(this)
            }
            service.add(this)
        }

        val gr1 = GeographicalRegion("gr1").apply { name = "gr 1" }.also { service.add(it) }
        GeographicalRegion("gr2").apply { name = "gr 2" }.also { service.add(it) }

        val sgr1 = SubGeographicalRegion("sgr1").apply {
            name = "sgr 1";
            if (includeGeographicalRegions)
                geographicalRegion = gr1
        }.also {
            if (includeSubgeographicalRegions)
                gr1.addSubGeographicalRegion(it)
            service.add(it)
        }

        SubGeographicalRegion("sgr2").apply {
            name = "sgr 2";
            if (includeGeographicalRegions)
                geographicalRegion = gr1
        }.also {
            if (includeSubgeographicalRegions)
                gr1.addSubGeographicalRegion(it)
            service.add(it)
        }

        val sub1 = Substation("sub1").apply {
            name = "sub 1";
            if (includeSubgeographicalRegions) {
                subGeographicalRegion = sgr1
                sgr1.addSubstation(this)
            }
            if (includeCircuits) {
                cir1.addEndSubstation(this)
                addCircuit(cir1)
            }
            if (includeLoops) {
                addLoop(loop1)
                loop1.addSubstation(this)
            }
            service.add(this)
        }

        Substation("sub2").apply {
            name = "sub 2";
            if (includeSubgeographicalRegions) {
                subGeographicalRegion = sgr1
                sgr1.addSubstation(this)
            }
            if (includeCircuits) {
                cir1.addEndSubstation(this)
                addCircuit(cir1)
            }
            if (includeLoops) {
                addLoop(loop1)
                loop1.addSubstation(this)
            }
            service.add(this)
        }

        val fdr1 = Feeder("fdr1").apply {
            name = "fdr 1"
            if (includeSubstations) {
                normalEnergizingSubstation = sub1
                sub1.addFeeder(this)
            }
            service.add(this)
        }

        val fdr2 = Feeder("fdr2").apply {
            name = "fdr 2";
            if (includeSubstations) {
                normalEnergizingSubstation = sub1
                sub1.addFeeder(this);
            }
            service.add(this)
        }

        val lvf1 = LvFeeder("lvfdr1").apply {
            name = "lvfdr 1"
            if (includeFeeders) {
                fdr1.addNormalEnergizedLvFeeder(this);
                this.addNormalEnergizingFeeder(fdr1)
            }
            service.add(this)
        }
        LvFeeder("lvfdr2").apply {
            name = "lvfdr 2"
            if (includeFeeders) {
                fdr1.addNormalEnergizedLvFeeder(this);
                this.addNormalEnergizingFeeder(fdr1)
            }
            service.add(this)
        }

        LvSubstation("lvsub1").apply {
            name = "lvsub 1"
            if (includeFeeders) {
                fdr1.addNormalEnergizedLvSubstation(this);
                this.addNormalEnergizingFeeder(fdr1)
            }
            if (includeLvFeeders) {
                lvf1.normalEnergizingLvSubstation = this
                this.addNormalEnergizedLvFeeder(lvf1)
            }
            service.add(this)
        }

        LvSubstation("lvsub2").apply {
            name = "lvsub 2"
            if (includeFeeders) {
                fdr1.addNormalEnergizedLvSubstation(this);
                this.addNormalEnergizingFeeder(fdr1)
            }
            service.add(this)
        }
    }

    fun createNetworkHierarchy(
        includeGeographicalRegions: Boolean = false,
        includeSubgeographicalRegions: Boolean = false,
        includeSubstations: Boolean = false,
        includeFeeders: Boolean = false,
        includeCircuits: Boolean = false,
        includeLoops: Boolean = false,
        includeLvSubstations: Boolean = false,
        includeLvFeeders: Boolean = false,
    ): NetworkHierarchy = createService().let {
        NetworkHierarchy(
            if (includeGeographicalRegions) it.mapOf() else emptyMap(),
            if (includeSubgeographicalRegions) it.mapOf() else emptyMap(),
            if (includeSubstations) it.mapOf() else emptyMap(),
            if (includeFeeders) it.mapOf() else emptyMap(),
            if (includeCircuits) it.mapOf() else emptyMap(),
            if (includeLoops) it.mapOf() else emptyMap(),
            if (includeLvSubstations) it.mapOf() else emptyMap(),
            if (includeLvFeeders) it.mapOf() else emptyMap()
        )
    }

}

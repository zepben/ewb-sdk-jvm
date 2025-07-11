/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.metrics

import com.zepben.ewb.cim.extensions.iec61970.base.feeder.LvFeeder
import com.zepben.ewb.cim.iec61970.base.core.Feeder
import com.zepben.ewb.cim.iec61970.base.core.GeographicalRegion
import com.zepben.ewb.cim.iec61970.base.core.SubGeographicalRegion
import com.zepben.ewb.cim.iec61970.base.core.Substation
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class NetworkContainerTest {

    @Test
    internal fun `extension functions`() {
        val fromGeoRegion = GeographicalRegion("GR").apply { name = "geoRegion" }.toNetworkContainer()
        val fromSubGeoRegion = SubGeographicalRegion("SGR").apply { name = "subGeoRegion" }.toNetworkContainer()
        val fromSubstation = Substation("SS").apply { name = "substation" }.toNetworkContainer()
        val fromSubstationTotal = Substation("SS2").apply { name = "substation2" }.toNetworkContainer(includeDownstream = true)
        val fromFeeder = Feeder("FDR").apply { name = "feeder" }.toNetworkContainer()
        val fromFeederTotal = Feeder("FDR2").apply { name = "feeder2" }.toNetworkContainer(includeDownstream = true)
        val fromLvFeeder = LvFeeder("LVF").apply { name = "lvFeeder" }.toNetworkContainer()

        assertThat(fromGeoRegion, equalTo(PartialNetworkContainer(NetworkLevel.GeographicalRegion, "GR", "geoRegion")))
        assertThat(fromSubGeoRegion, equalTo(PartialNetworkContainer(NetworkLevel.SubGeographicalRegion, "SGR", "subGeoRegion")))
        assertThat(fromSubstation, equalTo(PartialNetworkContainer(NetworkLevel.Substation, "SS", "substation")))
        assertThat(fromSubstationTotal, equalTo(PartialNetworkContainer(NetworkLevel.SubstationTotal, "SS2", "substation2")))
        assertThat(fromFeeder, equalTo(PartialNetworkContainer(NetworkLevel.Feeder, "FDR", "feeder")))
        assertThat(fromFeederTotal, equalTo(PartialNetworkContainer(NetworkLevel.FeederTotal, "FDR2", "feeder2")))
        assertThat(fromLvFeeder, equalTo(PartialNetworkContainer(NetworkLevel.LvFeeder, "LVF", "lvFeeder")))
    }

    @Test
    internal fun `accessor coverage`() {
        val partialNetworkContainer = PartialNetworkContainer(NetworkLevel.GeographicalRegion, "GR", "geoRegion")
        assertThat(partialNetworkContainer.level, equalTo(NetworkLevel.GeographicalRegion))
        assertThat(partialNetworkContainer.mRID, equalTo("GR"))
        assertThat(partialNetworkContainer.name, equalTo("geoRegion"))
    }

    @Test
    internal fun `java interop coverage`() {
        val fromGeoRegion = networkContainer(GeographicalRegion("GR").apply { name = "geoRegion" })
        val fromSubGeoRegion = networkContainer(SubGeographicalRegion("SGR").apply { name = "subGeoRegion" })
        val fromSubstation = networkContainer(Substation("SS").apply { name = "substation" })
        val fromSubstationTotal = networkContainer(Substation("SS2").apply { name = "substation2" }, true)
        val fromFeeder = networkContainer(Feeder("FDR").apply { name = "feeder" })
        val fromFeederTotal = networkContainer(Feeder("FDR2").apply { name = "feeder2" }, true)
        val fromLvFeeder = networkContainer(LvFeeder("LVF").apply { name = "lvFeeder" })

        assertThat(fromGeoRegion, equalTo(PartialNetworkContainer(NetworkLevel.GeographicalRegion, "GR", "geoRegion")))
        assertThat(fromSubGeoRegion, equalTo(PartialNetworkContainer(NetworkLevel.SubGeographicalRegion, "SGR", "subGeoRegion")))
        assertThat(fromSubstation, equalTo(PartialNetworkContainer(NetworkLevel.Substation, "SS", "substation")))
        assertThat(fromSubstationTotal, equalTo(PartialNetworkContainer(NetworkLevel.SubstationTotal, "SS2", "substation2")))
        assertThat(fromFeeder, equalTo(PartialNetworkContainer(NetworkLevel.Feeder, "FDR", "feeder")))
        assertThat(fromFeederTotal, equalTo(PartialNetworkContainer(NetworkLevel.FeederTotal, "FDR2", "feeder2")))
        assertThat(fromLvFeeder, equalTo(PartialNetworkContainer(NetworkLevel.LvFeeder, "LVF", "lvFeeder")))
    }

}

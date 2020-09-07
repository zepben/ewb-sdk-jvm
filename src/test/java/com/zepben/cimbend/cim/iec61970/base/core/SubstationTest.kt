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
package com.zepben.cimbend.cim.iec61970.base.core

import com.zepben.cimbend.cim.iec61970.infiec61970.feeder.Circuit
import com.zepben.cimbend.cim.iec61970.infiec61970.feeder.Loop
import com.zepben.cimbend.utils.PrivateCollectionValidator
import com.zepben.test.util.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class SubstationTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(Substation().mRID, not(equalTo("")))
        assertThat(Substation("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val substation = Substation()
        val subGeographicalRegion = SubGeographicalRegion()

        assertThat(substation.subGeographicalRegion, Matchers.nullValue())

        substation.subGeographicalRegion = subGeographicalRegion

        assertThat(substation.subGeographicalRegion, equalTo(subGeographicalRegion))
    }

    @Test
    internal fun feederAssociations() {
        PrivateCollectionValidator.validate(
            { Substation() },
            { id, _ -> Feeder(id) },
            Substation::numFeeders,
            Substation::getFeeder,
            Substation::feeders,
            Substation::addFeeder,
            Substation::removeFeeder,
            Substation::clearFeeders
        )
    }

    @Test
    internal fun circuitAssociations() {
        PrivateCollectionValidator.validate(
            { Substation() },
            { id, _ -> Circuit(id) },
            Substation::numCircuits,
            Substation::getCircuit,
            Substation::circuits,
            Substation::addCircuit,
            Substation::removeCircuit,
            Substation::clearCircuits
        )
    }

    @Test
    internal fun loopAssociations() {
        PrivateCollectionValidator.validate(
            { Substation() },
            { id, _ -> Loop(id) },
            Substation::numLoops,
            Substation::getLoop,
            Substation::loops,
            Substation::addLoop,
            Substation::removeLoop,
            Substation::clearLoops
        )
    }

    @Test
    internal fun energizedLoopAssociations() {
        PrivateCollectionValidator.validate(
            { Substation() },
            { id, _ -> Loop(id) },
            Substation::numEnergizedLoops,
            Substation::getEnergizedLoop,
            Substation::energizedLoops,
            Substation::addEnergizedLoop,
            Substation::removeEnergizedLoop,
            Substation::clearEnergizedLoops
        )
    }
}

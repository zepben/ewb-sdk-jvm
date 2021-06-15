/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.cim.iec61970.base.core

import com.zepben.evolve.cim.iec61970.infiec61970.feeder.Circuit
import com.zepben.evolve.cim.iec61970.infiec61970.feeder.Loop
import com.zepben.evolve.utils.PrivateCollectionValidator
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
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

        assertThat(substation.subGeographicalRegion, nullValue())

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

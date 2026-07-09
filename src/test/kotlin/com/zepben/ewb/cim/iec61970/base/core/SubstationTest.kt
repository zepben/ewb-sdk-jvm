/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.core

import com.zepben.ewb.cim.extensions.iec61970.base.feeder.Loop
import com.zepben.ewb.cim.iec61970.infiec61970.feeder.Circuit
import com.zepben.ewb.services.common.testdata.generateId
import com.zepben.ewb.utils.PrivateCollectionValidator
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class SubstationTest {

    companion object {
        @JvmField
        @RegisterExtension
        val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()
    }

    @Test
    internal fun constructorCoverage() {
        assertThat(Substation("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val substation = Substation(generateId())
        val subGeographicalRegion = SubGeographicalRegion(generateId())

        assertThat(substation.subGeographicalRegion, nullValue())

        substation.subGeographicalRegion = subGeographicalRegion

        assertThat(substation.subGeographicalRegion, equalTo(subGeographicalRegion))
    }

    @Test
    internal fun feederAssociations() {
        PrivateCollectionValidator.validateUnordered(
            ::Substation,
            ::Feeder,
            Substation::feeders,
            Substation::numFeeders,
            Substation::getFeeder,
            Substation::addFeeder,
            Substation::removeFeeder,
            Substation::clearFeeders
        )
    }

    @Test
    internal fun feederAssociationsBackfill() {
        PrivateCollectionValidator.validateBackfill(
            ::Substation,
            ::Feeder,
            "normalEnergizingSubstation",
            { it, other -> other.normalEnergizingSubstation = it },
            { other -> other.normalEnergizingSubstation },
            Substation::numFeeders,
            Substation::addFeeder,
        )
    }

    @Test
    internal fun circuitAssociations() {
        PrivateCollectionValidator.validateUnordered(
            ::Substation,
            ::Circuit,
            Substation::circuits,
            Substation::numCircuits,
            Substation::getCircuit,
            Substation::addCircuit,
            Substation::removeCircuit,
            Substation::clearCircuits
        )
    }

    @Test
    internal fun loopAssociations() {
        PrivateCollectionValidator.validateUnordered(
            ::Substation,
            ::Loop,
            Substation::loops,
            Substation::numLoops,
            Substation::getLoop,
            Substation::addLoop,
            Substation::removeLoop,
            Substation::clearLoops
        )
    }

    @Test
    internal fun energizedLoopAssociations() {
        PrivateCollectionValidator.validateUnordered(
            ::Substation,
            ::Loop,
            Substation::energizedLoops,
            Substation::numEnergizedLoops,
            Substation::getEnergizedLoop,
            Substation::addEnergizedLoop,
            Substation::removeEnergizedLoop,
            Substation::clearEnergizedLoops
        )
    }

}

/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.core

import com.zepben.evolve.services.common.extensions.typeNameAndMRID
import com.zepben.evolve.utils.PrivateCollectionValidator
import com.zepben.testutils.exception.ExpectException
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class SubGeographicalRegionTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(SubGeographicalRegion().mRID, not(equalTo("")))
        assertThat(SubGeographicalRegion("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val subGeographicalRegion = SubGeographicalRegion()
        val geographicalRegion = GeographicalRegion()

        assertThat(subGeographicalRegion.geographicalRegion, nullValue())

        subGeographicalRegion.geographicalRegion = geographicalRegion

        assertThat(subGeographicalRegion.geographicalRegion, equalTo(geographicalRegion))
    }

    @Test
    internal fun assignsSubGeographicalRegionToSubstationIfMissing() {
        val subGeographicalRegion = SubGeographicalRegion()
        val substation = Substation()

        subGeographicalRegion.addSubstation(substation)
        assertThat(substation.subGeographicalRegion, equalTo(subGeographicalRegion))
    }

    @Test
    internal fun rejectsSubstationWithWrongSubGeographicalRegion() {
        val subGeographicalRegion1 = SubGeographicalRegion()
        val subGeographicalRegion2 = SubGeographicalRegion()
        val substation = Substation().apply { subGeographicalRegion = subGeographicalRegion2 }

        ExpectException.expect { subGeographicalRegion1.addSubstation(substation) }
            .toThrow<IllegalArgumentException>()
            .withMessage("${substation.typeNameAndMRID()} `subGeographicalRegion` property references ${subGeographicalRegion2.typeNameAndMRID()}, expected ${subGeographicalRegion1.typeNameAndMRID()}.")
    }

    @Test
    internal fun substations() {
        PrivateCollectionValidator.validate(
            { SubGeographicalRegion() },
            { id, _ -> Substation(id) },
            SubGeographicalRegion::numSubstations,
            SubGeographicalRegion::getSubstation,
            SubGeographicalRegion::substations,
            SubGeographicalRegion::addSubstation,
            SubGeographicalRegion::removeSubstation,
            SubGeographicalRegion::clearSubstations
        )
    }
}

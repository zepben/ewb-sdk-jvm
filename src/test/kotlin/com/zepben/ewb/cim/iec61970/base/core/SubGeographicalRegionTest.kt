/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.core

import com.zepben.ewb.services.common.extensions.typeNameAndMRID
import com.zepben.ewb.services.common.testdata.generateId
import com.zepben.ewb.utils.PrivateCollectionValidator
import com.zepben.testutils.exception.ExpectException
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class SubGeographicalRegionTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(SubGeographicalRegion("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val subGeographicalRegion = SubGeographicalRegion(generateId())
        val geographicalRegion = GeographicalRegion(generateId())

        assertThat(subGeographicalRegion.geographicalRegion, nullValue())

        subGeographicalRegion.geographicalRegion = geographicalRegion

        assertThat(subGeographicalRegion.geographicalRegion, equalTo(geographicalRegion))
    }

    @Test
    internal fun assignsSubGeographicalRegionToSubstationIfMissing() {
        val subGeographicalRegion = SubGeographicalRegion(generateId())
        val substation = Substation(generateId())

        subGeographicalRegion.addSubstation(substation)
        assertThat(substation.subGeographicalRegion, equalTo(subGeographicalRegion))
    }

    @Test
    internal fun rejectsSubstationWithWrongSubGeographicalRegion() {
        val subGeographicalRegion1 = SubGeographicalRegion(generateId())
        val subGeographicalRegion2 = SubGeographicalRegion(generateId())
        val substation = Substation(generateId()).apply { subGeographicalRegion = subGeographicalRegion2 }

        ExpectException.expect { subGeographicalRegion1.addSubstation(substation) }
            .toThrow<IllegalArgumentException>()
            .withMessage("${substation.typeNameAndMRID()} `subGeographicalRegion` property references ${subGeographicalRegion2.typeNameAndMRID()}, expected ${subGeographicalRegion1.typeNameAndMRID()}.")
    }

    @Test
    internal fun substations() {
        PrivateCollectionValidator.validateUnordered(
            ::SubGeographicalRegion,
            ::Substation,
            SubGeographicalRegion::substations,
            SubGeographicalRegion::numSubstations,
            SubGeographicalRegion::getSubstation,
            SubGeographicalRegion::addSubstation,
            SubGeographicalRegion::removeSubstation,
            SubGeographicalRegion::clearSubstations
        )
    }

}

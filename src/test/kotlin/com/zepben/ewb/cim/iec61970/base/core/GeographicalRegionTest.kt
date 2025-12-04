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
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class GeographicalRegionTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(GeographicalRegion("id").mRID, equalTo("id"))
    }

    @Test
    internal fun assignsGeographicalRegionToSubGeographicalRegionIfMissing() {
        val geographicalRegion = GeographicalRegion(generateId())
        val subGeographicalRegion = SubGeographicalRegion(generateId())

        geographicalRegion.addSubGeographicalRegion(subGeographicalRegion)
        assertThat(subGeographicalRegion.geographicalRegion, equalTo(geographicalRegion))
    }

    @Test
    internal fun rejectsSubGeographicalRegionWithWrongGeographicalRegion() {
        val geographicalRegion1 = GeographicalRegion(generateId())
        val geographicalRegion2 = GeographicalRegion(generateId())
        val subGeographicalRegion = SubGeographicalRegion(generateId()).apply { geographicalRegion = geographicalRegion2 }

        ExpectException.expect { geographicalRegion1.addSubGeographicalRegion(subGeographicalRegion) }
            .toThrow<IllegalArgumentException>()
            .withMessage("${subGeographicalRegion.typeNameAndMRID()} `geographicalRegion` property references ${geographicalRegion2.typeNameAndMRID()}, expected ${geographicalRegion1.typeNameAndMRID()}.")
    }

    @Test
    internal fun subGeographicalRegions() {
        PrivateCollectionValidator.validateUnordered(
            ::GeographicalRegion,
            ::SubGeographicalRegion,
            GeographicalRegion::subGeographicalRegions,
            GeographicalRegion::numSubGeographicalRegions,
            GeographicalRegion::getSubGeographicalRegion,
            GeographicalRegion::addSubGeographicalRegion,
            GeographicalRegion::removeSubGeographicalRegion,
            GeographicalRegion::clearSubGeographicalRegions
        )
    }

}

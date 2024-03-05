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
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class GeographicalRegionTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(GeographicalRegion().mRID, not(equalTo("")))
        assertThat(GeographicalRegion("id").mRID, equalTo("id"))
    }

    @Test
    internal fun assignsGeographicalRegionToSubGeographicalRegionIfMissing() {
        val geographicalRegion = GeographicalRegion()
        val subGeographicalRegion = SubGeographicalRegion()

        geographicalRegion.addSubGeographicalRegion(subGeographicalRegion)
        assertThat(subGeographicalRegion.geographicalRegion, equalTo(geographicalRegion))
    }

    @Test
    internal fun rejectsSubGeographicalRegionWithWrongGeographicalRegion() {
        val geographicalRegion1 = GeographicalRegion()
        val geographicalRegion2 = GeographicalRegion()
        val subGeographicalRegion = SubGeographicalRegion().apply { geographicalRegion = geographicalRegion2 }

        ExpectException.expect { geographicalRegion1.addSubGeographicalRegion(subGeographicalRegion) }
            .toThrow<IllegalArgumentException>()
            .withMessage("${subGeographicalRegion.typeNameAndMRID()} `geographicalRegion` property references ${geographicalRegion2.typeNameAndMRID()}, expected ${geographicalRegion1.typeNameAndMRID()}.")
    }

    @Test
    internal fun subGeographicalRegions() {
        PrivateCollectionValidator.validate(
            { GeographicalRegion() },
            { id, _ -> SubGeographicalRegion(id) },
            GeographicalRegion::numSubGeographicalRegions,
            GeographicalRegion::getSubGeographicalRegion,
            GeographicalRegion::subGeographicalRegions,
            GeographicalRegion::addSubGeographicalRegion,
            GeographicalRegion::removeSubGeographicalRegion,
            GeographicalRegion::clearSubGeographicalRegions
        )
    }
}

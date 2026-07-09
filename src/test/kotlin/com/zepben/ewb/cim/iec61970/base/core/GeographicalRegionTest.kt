/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.core

import com.zepben.ewb.utils.PrivateCollectionValidator
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class GeographicalRegionTest {

    companion object {
        @JvmField
        @RegisterExtension
        val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()
    }

    @Test
    internal fun constructorCoverage() {
        assertThat(GeographicalRegion("id").mRID, equalTo("id"))
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

    @Test
    internal fun subGeographicalRegionsBackfill() {
        PrivateCollectionValidator.validateBackfill(
            ::GeographicalRegion,
            ::SubGeographicalRegion,
            "geographicalRegion",
            { it, other -> other.geographicalRegion = it },
            { other -> other.geographicalRegion },
            GeographicalRegion::numSubGeographicalRegions,
            GeographicalRegion::addSubGeographicalRegion,
        )
    }

}

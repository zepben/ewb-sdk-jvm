/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.cimbend.cim.iec61970.base.core

import com.zepben.cimbend.utils.PrivateCollectionValidator
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

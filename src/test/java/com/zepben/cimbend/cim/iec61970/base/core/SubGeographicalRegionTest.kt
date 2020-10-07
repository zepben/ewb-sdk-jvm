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

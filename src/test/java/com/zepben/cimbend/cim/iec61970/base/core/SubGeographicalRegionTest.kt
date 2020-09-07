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

import com.zepben.cimbend.utils.PrivateCollectionValidator
import com.zepben.test.util.junit.SystemLogExtension
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

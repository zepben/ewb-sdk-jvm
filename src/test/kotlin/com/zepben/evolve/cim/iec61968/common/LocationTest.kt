/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61968.common

import com.zepben.evolve.utils.PrivateCollectionValidator
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class LocationTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(Location().mRID, not(equalTo("")))
        assertThat(Location("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val location = Location()
        val mainAddress = StreetAddress()

        assertThat(location.mainAddress, nullValue())

        location.mainAddress = mainAddress
        assertThat(location.mainAddress, equalTo(mainAddress))
    }

    @Test
    internal fun positionPoints() {
        PrivateCollectionValidator.validateOrdered(
            ::Location,
            { PositionPoint(it.toDouble(), it.toDouble()) },
            Location::points,
            Location::numPoints,
            Location::getPoint,
            Location::forEachPoint,
            Location::addPoint,
            Location::addPoint,
            Location::removePoint,
            Location::removePoint,
            Location::clearPoints
        )
    }
}

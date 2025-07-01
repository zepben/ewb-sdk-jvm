/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61968.infiec61968.infassets

import com.zepben.ewb.cim.iec61968.assets.Streetlight
import com.zepben.ewb.utils.PrivateCollectionValidator
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class PoleTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(Pole().mRID, not(equalTo("")))
        assertThat(Pole("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val pole = Pole("id")

        assertThat(pole.classification, equalTo(""))

        pole.classification = "classification"

        assertThat(pole.classification, equalTo("classification"))
    }

    @Test
    internal fun streetlights() {
        PrivateCollectionValidator.validateUnordered(
            ::Pole,
            ::Streetlight,
            Pole::streetlights,
            Pole::numStreetlights,
            Pole::getStreetlight,
            Pole::addStreetlight,
            Pole::removeStreetlight,
            Pole::clearStreetlights
        )
    }
}

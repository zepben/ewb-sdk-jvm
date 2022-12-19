/*
 * Copyright 2022 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.protection

import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.testdata.fillFields
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.Test

internal class RecloseSequenceTest {

    @Test
    internal fun constructorCoverage() {
        assertThat(RecloseSequence().mRID, Matchers.not(equalTo("")))
        assertThat(RecloseSequence("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val recloseSequence = RecloseSequence()

        assertThat(recloseSequence.recloseDelay, nullValue())
        assertThat(recloseSequence.recloseStep, nullValue())

        recloseSequence.fillFields(NetworkService())

        assertThat(recloseSequence.recloseDelay, equalTo(1.1))
        assertThat(recloseSequence.recloseStep, equalTo(2))
    }

}

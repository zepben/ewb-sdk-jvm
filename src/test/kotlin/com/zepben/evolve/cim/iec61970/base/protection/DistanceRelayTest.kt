/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.protection

import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.testdata.fillFields
import org.hamcrest.Matchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test

internal class DistanceRelayTest {

    @Test
    internal fun constructorCoverage() {
        assertThat(DistanceRelay().mRID, not(equalTo("")))
        assertThat(DistanceRelay("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val distanceRelay = DistanceRelay()

        assertThat(distanceRelay.backwardBlind, nullValue())
        assertThat(distanceRelay.backwardReach, nullValue())
        assertThat(distanceRelay.backwardReactance, nullValue())
        assertThat(distanceRelay.forwardBlind, nullValue())
        assertThat(distanceRelay.forwardReach, nullValue())
        assertThat(distanceRelay.forwardReactance, nullValue())
        assertThat(distanceRelay.operationPhaseAngle1, nullValue())
        assertThat(distanceRelay.operationPhaseAngle2, nullValue())
        assertThat(distanceRelay.operationPhaseAngle3, nullValue())

        distanceRelay.fillFields(NetworkService())

        assertThat(distanceRelay.backwardBlind, equalTo(1.1))
        assertThat(distanceRelay.backwardReach, equalTo(2.2))
        assertThat(distanceRelay.backwardReactance, equalTo(3.3))
        assertThat(distanceRelay.forwardBlind, equalTo(4.4))
        assertThat(distanceRelay.forwardReach, equalTo(5.5))
        assertThat(distanceRelay.forwardReactance, equalTo(6.6))
        assertThat(distanceRelay.operationPhaseAngle1, equalTo(7.7))
        assertThat(distanceRelay.operationPhaseAngle2, equalTo(8.8))
        assertThat(distanceRelay.operationPhaseAngle3, equalTo(9.9))
    }

}

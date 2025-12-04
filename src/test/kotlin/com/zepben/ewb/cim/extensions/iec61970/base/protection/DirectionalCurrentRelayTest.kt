/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.extensions.iec61970.base.protection

import com.zepben.ewb.cim.iec61970.base.core.PhaseCode
import com.zepben.ewb.services.common.testdata.generateId
import com.zepben.ewb.services.network.NetworkService
import com.zepben.ewb.services.network.testdata.fillFields
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class DirectionalCurrentRelayTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.Companion.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(DirectionalCurrentRelay("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val relay = DirectionalCurrentRelay(generateId())

        assertThat(relay.directionalCharacteristicAngle, nullValue())
        assertThat(relay.polarizingQuantityType, equalTo(PolarizingQuantityType.UNKNOWN))
        assertThat(relay.relayElementPhase, equalTo(PhaseCode.NONE))
        assertThat(relay.minimumPickupCurrent, nullValue())
        assertThat(relay.currentLimit1, nullValue())
        assertThat(relay.inverseTimeFlag, nullValue())
        assertThat(relay.timeDelay1, nullValue())

        relay.fillFields(NetworkService())

        assertThat(relay.directionalCharacteristicAngle, equalTo(1.1))
        assertThat(relay.polarizingQuantityType, equalTo(PolarizingQuantityType.NEGATIVE_SEQUENCE_VOLTAGE))
        assertThat(relay.relayElementPhase, equalTo(PhaseCode.ABCN))
        assertThat(relay.minimumPickupCurrent, equalTo(2.2))
        assertThat(relay.currentLimit1, equalTo(3.3))
        assertThat(relay.inverseTimeFlag, equalTo(true))
        assertThat(relay.timeDelay1, equalTo(4.4))
    }

}

/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.cim.iec61970.base.protection

import com.zepben.evolve.cim.iec61970.infiec61970.protection.PowerDirectionKind
import com.zepben.evolve.cim.iec61970.infiec61970.protection.ProtectionKind
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.testdata.fillFields
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.contains
import org.hamcrest.Matchers.emptyIterable
import org.junit.jupiter.api.Test

internal class ProtectionRelayFunctionTest {

    @Test
    internal fun constructorCoverage() {
        assertThat(object : ProtectionRelayFunction() {}.mRID, not(equalTo("")))
        assertThat(object : ProtectionRelayFunction("id") {}.mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val protectionRelayFunction = object : ProtectionRelayFunction() {}

        assertThat(protectionRelayFunction.model, nullValue())
        assertThat(protectionRelayFunction.reclosing, nullValue())
        assertThat(protectionRelayFunction.relayDelayTime, nullValue())
        assertThat(protectionRelayFunction.protectionKind, equalTo(ProtectionKind.UNKNOWN))
        assertThat(protectionRelayFunction.directable, nullValue())
        assertThat(protectionRelayFunction.powerDirection, equalTo(PowerDirectionKind.UNKNOWN_DIRECTION))
        assertThat(protectionRelayFunction.timeLimits, emptyIterable())

        protectionRelayFunction.fillFields(NetworkService())

        assertThat(protectionRelayFunction.model, equalTo("model"))
        assertThat(protectionRelayFunction.reclosing, equalTo(true))
        assertThat(protectionRelayFunction.relayDelayTime, equalTo(1.1))
        assertThat(protectionRelayFunction.protectionKind, equalTo(ProtectionKind.DISTANCE))
        assertThat(protectionRelayFunction.directable, equalTo(true))
        assertThat(protectionRelayFunction.powerDirection, equalTo(PowerDirectionKind.FORWARD))
        assertThat(protectionRelayFunction.timeLimits, contains(1.0, 2.0, 3.0))
    }
}

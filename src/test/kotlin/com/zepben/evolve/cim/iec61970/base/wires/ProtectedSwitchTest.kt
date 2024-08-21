/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.wires

import com.zepben.evolve.cim.iec61970.base.protection.ProtectionRelayFunction
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.testdata.fillFields
import com.zepben.evolve.utils.PrivateCollectionValidator
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class ProtectedSwitchTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(object : ProtectedSwitch() {}.mRID, not(equalTo("")))
        assertThat(object : ProtectedSwitch("id") {}.mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val protectedSwitch = object : ProtectedSwitch() {}

        assertThat(protectedSwitch.breakingCapacity, nullValue())

        protectedSwitch.fillFields(NetworkService())

        assertThat(protectedSwitch.breakingCapacity, equalTo(1))
    }

    @Test
    internal fun relayFunctions() {
        PrivateCollectionValidator.validateUnordered(
            { object : ProtectedSwitch() {} },
            { id -> object : ProtectionRelayFunction(id) {} },
            ProtectedSwitch::relayFunctions,
            ProtectedSwitch::numRelayFunctions,
            ProtectedSwitch::getRelayFunction,
            ProtectedSwitch::addRelayFunction,
            ProtectedSwitch::removeRelayFunction,
            ProtectedSwitch::clearRelayFunctions
        )
    }

}

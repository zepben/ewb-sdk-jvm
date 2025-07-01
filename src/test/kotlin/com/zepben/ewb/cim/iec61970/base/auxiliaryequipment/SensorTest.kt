/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.auxiliaryequipment

import com.zepben.ewb.cim.extensions.iec61970.base.protection.ProtectionRelayFunction
import com.zepben.ewb.utils.PrivateCollectionValidator
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class SensorTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(object : Sensor() {}.mRID, not(equalTo("")))
        assertThat(object : Sensor("id") {}.mRID, equalTo("id"))
    }

    @Test
    internal fun relayFunctions() {
        PrivateCollectionValidator.validateUnordered(
            { object : Sensor() {} },
            { id -> object : ProtectionRelayFunction(id) {} },
            Sensor::relayFunctions,
            Sensor::numRelayFunctions,
            Sensor::getRelayFunction,
            Sensor::addRelayFunction,
            Sensor::removeRelayFunction,
            Sensor::clearRelayFunctions
        )
    }

}

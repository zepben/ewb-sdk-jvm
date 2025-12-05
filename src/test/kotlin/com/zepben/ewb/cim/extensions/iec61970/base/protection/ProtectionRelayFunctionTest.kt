/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.extensions.iec61970.base.protection

import com.zepben.ewb.cim.extensions.iec61968.assetinfo.RelayInfo
import com.zepben.ewb.cim.iec61970.base.auxiliaryequipment.Sensor
import com.zepben.ewb.cim.iec61970.base.domain.UnitSymbol
import com.zepben.ewb.cim.iec61970.base.wires.ProtectedSwitch
import com.zepben.ewb.services.common.testdata.generateId
import com.zepben.ewb.services.network.NetworkService
import com.zepben.ewb.services.network.testdata.fillFields
import com.zepben.ewb.utils.PrivateCollectionValidator
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test

internal class ProtectionRelayFunctionTest {

    @Test
    internal fun constructorCoverage() {
        assertThat(object : ProtectionRelayFunction("id") {}.mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val protectionRelayFunction = object : ProtectionRelayFunction(generateId()) {}

        assertThat(protectionRelayFunction.assetInfo, nullValue())
        assertThat(protectionRelayFunction.model, nullValue())
        assertThat(protectionRelayFunction.reclosing, nullValue())
        assertThat(protectionRelayFunction.relayDelayTime, nullValue())
        assertThat(protectionRelayFunction.protectionKind, equalTo(ProtectionKind.UNKNOWN))
        assertThat(protectionRelayFunction.directable, nullValue())
        assertThat(protectionRelayFunction.powerDirection, equalTo(PowerDirectionKind.UNKNOWN))

        protectionRelayFunction.fillFields(NetworkService())

        assertThat(protectionRelayFunction.assetInfo, instanceOf(RelayInfo::class.java))
        assertThat(protectionRelayFunction.model, equalTo("model"))
        assertThat(protectionRelayFunction.reclosing, equalTo(true))
        assertThat(protectionRelayFunction.relayDelayTime, equalTo(1.1))
        assertThat(protectionRelayFunction.protectionKind, equalTo(ProtectionKind.DISTANCE))
        assertThat(protectionRelayFunction.directable, equalTo(true))
        assertThat(protectionRelayFunction.powerDirection, equalTo(PowerDirectionKind.FORWARD))
    }

    @Test
    internal fun timeLimits() {
        PrivateCollectionValidator.validateOrdered(
            { id -> object : ProtectionRelayFunction(id) {} },
            { it.toDouble() },
            ProtectionRelayFunction::timeLimits,
            ProtectionRelayFunction::numTimeLimits,
            ProtectionRelayFunction::getTimeLimit,
            ProtectionRelayFunction::forEachTimeLimits,
            ProtectionRelayFunction::addTimeLimit,
            ProtectionRelayFunction::addTimeLimit,
            ProtectionRelayFunction::removeTimeLimit,
            ProtectionRelayFunction::removeTimeLimitAt,
            ProtectionRelayFunction::clearTimeLimits
        )

        val protectionRelayFunction = object : ProtectionRelayFunction(generateId()) {}

        protectionRelayFunction.addTimeLimits(1.0, 2.0, 3.0)
        assertThat(protectionRelayFunction.timeLimits, contains(1.0, 2.0, 3.0))
    }

    @Test
    internal fun thresholds() {
        PrivateCollectionValidator.validateOrdered(
            { id -> object : ProtectionRelayFunction(id) {} },
            { RelaySetting(UnitSymbol.W, it.toDouble()) },
            ProtectionRelayFunction::thresholds,
            ProtectionRelayFunction::numThresholds,
            ProtectionRelayFunction::getThreshold,
            ProtectionRelayFunction::forEachThreshold,
            ProtectionRelayFunction::addThreshold,
            ProtectionRelayFunction::addThreshold,
            ProtectionRelayFunction::removeThreshold,
            ProtectionRelayFunction::removeThreshold,
            ProtectionRelayFunction::clearThresholds
        )
    }

    @Test
    internal fun protectedSwitches() {
        PrivateCollectionValidator.validateUnordered(
            { id -> object : ProtectionRelayFunction(id) {} },
            { id -> object : ProtectedSwitch(id) {} },
            ProtectionRelayFunction::protectedSwitches,
            ProtectionRelayFunction::numProtectedSwitches,
            ProtectionRelayFunction::getProtectedSwitch,
            ProtectionRelayFunction::addProtectedSwitch,
            ProtectionRelayFunction::removeProtectedSwitch,
            ProtectionRelayFunction::clearProtectedSwitches
        )
    }

    @Test
    internal fun sensors() {
        PrivateCollectionValidator.validateUnordered(
            { id -> object : ProtectionRelayFunction(id) {} },
            { id -> object : Sensor(id) {} },
            ProtectionRelayFunction::sensors,
            ProtectionRelayFunction::numSensors,
            ProtectionRelayFunction::getSensor,
            ProtectionRelayFunction::addSensor,
            ProtectionRelayFunction::removeSensor,
            ProtectionRelayFunction::clearSensors
        )
    }

    @Test
    internal fun schemes() {
        PrivateCollectionValidator.validateUnordered(
            { id -> object : ProtectionRelayFunction(id) {} },
            ::ProtectionRelayScheme,
            ProtectionRelayFunction::schemes,
            ProtectionRelayFunction::numSchemes,
            ProtectionRelayFunction::getScheme,
            ProtectionRelayFunction::addScheme,
            ProtectionRelayFunction::removeScheme,
            ProtectionRelayFunction::clearSchemes
        )
    }

}

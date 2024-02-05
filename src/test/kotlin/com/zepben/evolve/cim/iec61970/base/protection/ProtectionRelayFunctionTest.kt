/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.cim.iec61970.base.protection

import com.zepben.evolve.cim.iec61968.infiec61968.infassetinfo.RelayInfo
import com.zepben.evolve.cim.iec61970.base.auxiliaryequipment.Sensor
import com.zepben.evolve.cim.iec61970.base.domain.UnitSymbol
import com.zepben.evolve.cim.iec61970.base.wires.ProtectedSwitch
import com.zepben.evolve.cim.iec61970.infiec61970.protection.PowerDirectionKind
import com.zepben.evolve.cim.iec61970.infiec61970.protection.ProtectionKind
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.testdata.fillFields
import com.zepben.evolve.utils.PrivateCollectionValidator
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
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

        assertThat(protectionRelayFunction.assetInfo, nullValue())
        assertThat(protectionRelayFunction.model, nullValue())
        assertThat(protectionRelayFunction.reclosing, nullValue())
        assertThat(protectionRelayFunction.relayDelayTime, nullValue())
        assertThat(protectionRelayFunction.protectionKind, equalTo(ProtectionKind.UNKNOWN))
        assertThat(protectionRelayFunction.directable, nullValue())
        assertThat(protectionRelayFunction.powerDirection, equalTo(PowerDirectionKind.UNKNOWN_DIRECTION))

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
        var timeLimit = 0.0
        PrivateCollectionValidator.validate(
            { object : ProtectionRelayFunction() {} },
            { timeLimit++ },
            ProtectionRelayFunction::numTimeLimits,
            ProtectionRelayFunction::getTimeLimit,
            ProtectionRelayFunction::forEachTimeLimits,
            ProtectionRelayFunction::addTimeLimit,
            ProtectionRelayFunction::addTimeLimit,
            ProtectionRelayFunction::removeTimeLimit,
            ProtectionRelayFunction::removeTimeLimitAt,
            ProtectionRelayFunction::clearTimeLimits
        )
    }

    @Test
    internal fun protectedSwitches() {
        PrivateCollectionValidator.validate(
            { object : ProtectionRelayFunction() {} },
            { id, _ -> object : ProtectedSwitch(id) {} },
            ProtectionRelayFunction::numProtectedSwitches,
            ProtectionRelayFunction::getProtectedSwitch,
            ProtectionRelayFunction::protectedSwitches,
            ProtectionRelayFunction::addProtectedSwitch,
            ProtectionRelayFunction::removeProtectedSwitch,
            ProtectionRelayFunction::clearProtectedSwitches
        )
    }

    @Test
    internal fun sensors() {
        PrivateCollectionValidator.validate(
            { object : ProtectionRelayFunction() {} },
            { id, _ -> object : Sensor(id) {} },
            ProtectionRelayFunction::numSensors,
            ProtectionRelayFunction::getSensor,
            ProtectionRelayFunction::sensors,
            ProtectionRelayFunction::addSensor,
            ProtectionRelayFunction::removeSensor,
            ProtectionRelayFunction::clearSensors
        )
    }

    @Test
    internal fun thresholds() {
        var thresholdNumber = 0.0
        PrivateCollectionValidator.validate(
            { object : ProtectionRelayFunction() {} },
            { RelaySetting(UnitSymbol.W, thresholdNumber++) },
            ProtectionRelayFunction::numThresholds,
            ProtectionRelayFunction::getThreshold,
            ProtectionRelayFunction::forEachThreshold,
            ProtectionRelayFunction::addThreshold,
            ProtectionRelayFunction::addThreshold,
            ProtectionRelayFunction::removeThreshold,
            null,
            ProtectionRelayFunction::clearThresholds
        )
    }

}

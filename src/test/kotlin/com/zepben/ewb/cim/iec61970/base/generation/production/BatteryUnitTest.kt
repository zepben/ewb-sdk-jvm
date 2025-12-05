/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61970.base.generation.production

import com.zepben.ewb.cim.extensions.iec61970.base.wires.BatteryControl
import com.zepben.ewb.cim.extensions.iec61970.base.wires.BatteryControlMode
import com.zepben.ewb.services.common.testdata.generateId
import com.zepben.ewb.utils.PrivateCollectionValidator
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class BatteryUnitTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(BatteryUnit("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val batteryUnit = BatteryUnit(generateId())
        val batteryControl = BatteryControl(generateId())

        assertThat(batteryUnit.batteryState, equalTo(BatteryStateKind.UNKNOWN))
        assertThat(batteryUnit.ratedE, nullValue())
        assertThat(batteryUnit.storedE, nullValue())

        batteryUnit.apply {
            this.batteryState = BatteryStateKind.charging
            ratedE = 1L
            storedE = 2L
        }
        batteryUnit.addControl(batteryControl)

        assertThat(batteryUnit.batteryState, equalTo(BatteryStateKind.charging))
        assertThat(batteryUnit.ratedE, equalTo(1L))
        assertThat(batteryUnit.storedE, equalTo(2L))
    }

    @Test
    internal fun batteryControls() {
        PrivateCollectionValidator.validateUnordered(
            ::BatteryUnit,
            ::BatteryControl,
            BatteryUnit::controls,
            BatteryUnit::numBatteryControls,
            BatteryUnit::getControl,
            BatteryUnit::addControl,
            BatteryUnit::removeControl,
            BatteryUnit::clearControls
        )
    }

    @Test
    internal fun getBatteryControlWithMode() {
        val batteryUnit = BatteryUnit(generateId())
        val batteryControl = BatteryControl(generateId())

        batteryUnit.addControl(batteryControl)

        assertThat(batteryUnit.getControl(BatteryControlMode.UNKNOWN), equalTo(batteryControl))
    }

}

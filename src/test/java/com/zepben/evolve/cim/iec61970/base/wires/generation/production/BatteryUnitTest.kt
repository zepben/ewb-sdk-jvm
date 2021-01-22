/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61970.base.wires.generation.production

import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class BatteryUnitTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
       assertThat(BatteryUnit().mRID, not(equalTo("")))
       assertThat(BatteryUnit("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val batteryUnit = BatteryUnit()

       assertThat(batteryUnit.batteryState, equalTo(BatteryStateKind.UNKNOWN))
       assertThat(batteryUnit.ratedE, equalTo(0.0))
       assertThat(batteryUnit.storedE, equalTo(0.0))

        batteryUnit.apply {
            this.batteryState = BatteryStateKind.charging
            ratedE = 1.0
            storedE = 2.0
        }

       assertThat(batteryUnit.batteryState, equalTo(BatteryStateKind.charging))
       assertThat(batteryUnit.ratedE, equalTo(1.0))
       assertThat(batteryUnit.storedE, equalTo(2.0))
    }
}
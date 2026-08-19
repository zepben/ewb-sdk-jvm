/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.boilerplate.relations

import com.zepben.ewb.cim.extensions.iec61970.base.wires.BatteryControl
import com.zepben.ewb.cim.extensions.iec61970.base.wires.BatteryControlMode
import com.zepben.ewb.cim.iec61970.base.generation.production.BatteryUnit
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.nullValue
import org.hamcrest.Matchers.sameInstance
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class BatteryControlListTest {

    companion object {
        @JvmField
        @RegisterExtension
        val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()
    }

    @Test
    internal fun `getByMode returns the matching control or null`() {
        val schedule = BatteryControl("schedule").apply { controlMode = BatteryControlMode.schedule }
        val time = BatteryControl("time").apply { controlMode = BatteryControlMode.time }
        var backing: MutableList<BatteryControl>? = mutableListOf(schedule, time)
        val controls = BatteryControlList(
            { backing },
            { backing = it },
            BatteryUnit("battery"),
            "A BatteryControl",
        )

        assertThat(controls.getByMode(BatteryControlMode.time), sameInstance(time))
        assertThat(controls.getByMode(BatteryControlMode.UNKNOWN), nullValue())
    }

}

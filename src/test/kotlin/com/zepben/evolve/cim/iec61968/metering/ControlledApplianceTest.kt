/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61968.metering

import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class ControlledApplianceTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        val controlledAppliance = ControlledAppliance()
        assertThat(controlledAppliance, notNullValue())
        assertThat(controlledAppliance.isElectricVehicle, equalTo(false))
        assertThat(controlledAppliance.isExteriorLighting, equalTo(false))
        assertThat(controlledAppliance.isGenerationSystem, equalTo(false))
        assertThat(controlledAppliance.isHvacCompressorOrFurnace, equalTo(false))
        assertThat(controlledAppliance.isInteriorLighting, equalTo(false))
        assertThat(controlledAppliance.isIrrigationPump, equalTo(false))
        assertThat(controlledAppliance.isManagedCommercialIndustrialLoad, equalTo(false))
        assertThat(controlledAppliance.isPoolPumpSpaJacuzzi, equalTo(false))
        assertThat(controlledAppliance.isSimpleMiscLoad, equalTo(false))
        assertThat(controlledAppliance.isSmartAppliance, equalTo(false))
        assertThat(controlledAppliance.isStripAndBaseboardHeater, equalTo(false))
        assertThat(controlledAppliance.isWaterHeater, equalTo(false))
    }

    @Test
    internal fun accessorCoverage() {
        val controlledAppliance = ControlledAppliance(
            isElectricVehicle = true,
            isExteriorLighting = true,
            isGenerationSystem = true,
            isHvacCompressorOrFurnace = true,
            isInteriorLighting = true,
            isIrrigationPump = true,
            isManagedCommercialIndustrialLoad = true,
            isPoolPumpSpaJacuzzi = true,
            isSimpleMiscLoad = true,
            isSmartAppliance = true,
            isStripAndBaseboardHeater = true,
            isWaterHeater = true
        )

        assertThat(controlledAppliance.isElectricVehicle, equalTo(true))
        assertThat(controlledAppliance.isExteriorLighting, equalTo(true))
        assertThat(controlledAppliance.isGenerationSystem, equalTo(true))
        assertThat(controlledAppliance.isHvacCompressorOrFurnace, equalTo(true))
        assertThat(controlledAppliance.isInteriorLighting, equalTo(true))
        assertThat(controlledAppliance.isIrrigationPump, equalTo(true))
        assertThat(controlledAppliance.isManagedCommercialIndustrialLoad, equalTo(true))
        assertThat(controlledAppliance.isPoolPumpSpaJacuzzi, equalTo(true))
        assertThat(controlledAppliance.isSimpleMiscLoad, equalTo(true))
        assertThat(controlledAppliance.isSmartAppliance, equalTo(true))
        assertThat(controlledAppliance.isStripAndBaseboardHeater, equalTo(true))
        assertThat(controlledAppliance.isWaterHeater, equalTo(true))
    }

    @Test
    internal fun createControlledApplianceFromInt() {
        val controlledAppliance = ControlledAppliance.fromInt(1365)

        assertThat(controlledAppliance.isElectricVehicle, equalTo(true))
        assertThat(controlledAppliance.isExteriorLighting, equalTo(false))
        assertThat(controlledAppliance.isGenerationSystem, equalTo(true))
        assertThat(controlledAppliance.isHvacCompressorOrFurnace, equalTo(false))
        assertThat(controlledAppliance.isInteriorLighting, equalTo(true))
        assertThat(controlledAppliance.isIrrigationPump, equalTo(false))
        assertThat(controlledAppliance.isManagedCommercialIndustrialLoad, equalTo(true))
        assertThat(controlledAppliance.isPoolPumpSpaJacuzzi, equalTo(false))
        assertThat(controlledAppliance.isSimpleMiscLoad, equalTo(true))
        assertThat(controlledAppliance.isSmartAppliance, equalTo(false))
        assertThat(controlledAppliance.isStripAndBaseboardHeater, equalTo(true))
        assertThat(controlledAppliance.isWaterHeater, equalTo(false))
    }

    @Test
    internal fun controlledApplianceToInt() {
        val controlledAppliance = ControlledAppliance(
            isElectricVehicle = true,
            isExteriorLighting = false,
            isGenerationSystem = true,
            isHvacCompressorOrFurnace = false,
            isInteriorLighting = true,
            isIrrigationPump = false,
            isManagedCommercialIndustrialLoad = true,
            isPoolPumpSpaJacuzzi = false,
            isSimpleMiscLoad = true,
            isSmartAppliance = false,
            isStripAndBaseboardHeater = true,
            isWaterHeater = false
        )

        assertThat(controlledAppliance.toInt(), equalTo(1365))
    }
}

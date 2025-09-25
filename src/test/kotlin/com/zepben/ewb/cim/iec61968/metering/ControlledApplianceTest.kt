/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.iec61968.metering

import com.zepben.ewb.cim.iec61968.metering.ControlledAppliance.Appliance.*
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class ControlledApplianceTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun `constructor with bitmask`() {
        // Bitmask of zero should exclude all types.
        validateBitmaskConstructor(0)

        // Each bit controls a different field.
        validateBitmaskConstructor(ELECTRIC_VEHICLE.bitmask, expectIsElectricVehicle = true)
        validateBitmaskConstructor(EXTERIOR_LIGHTING.bitmask, expectIsExteriorLighting = true)
        validateBitmaskConstructor(GENERATION_SYSTEM.bitmask, expectIsGenerationSystem = true)
        validateBitmaskConstructor(HVAC_COMPRESSOR_OR_FURNACE.bitmask, expectIsHvacCompressorOrFurnace = true)
        validateBitmaskConstructor(INTERIOR_LIGHTING.bitmask, expectIsInteriorLighting = true)
        validateBitmaskConstructor(IRRIGATION_PUMP.bitmask, expectIsIrrigationPump = true)
        validateBitmaskConstructor(MANAGED_COMMERCIAL_INDUSTRIAL_LOAD.bitmask, expectIsManagedCommercialIndustrialLoad = true)
        validateBitmaskConstructor(POOL_PUMP_SPA_JACUZZI.bitmask, expectIsPoolPumpSpaJacuzzi = true)
        validateBitmaskConstructor(SIMPLE_MISC_LOAD.bitmask, expectIsSimpleMiscLoad = true)
        validateBitmaskConstructor(SMART_APPLIANCE.bitmask, expectIsSmartAppliance = true)
        validateBitmaskConstructor(STRIP_AND_BASEBOARD_HEATER.bitmask, expectIsStripAndBaseboardHeater = true)
        validateBitmaskConstructor(WATER_HEATER.bitmask, expectIsWaterHeater = true)

        // Can combine more than one.
        validateBitmaskConstructor(ELECTRIC_VEHICLE.bitmask or EXTERIOR_LIGHTING.bitmask, expectIsElectricVehicle = true, expectIsExteriorLighting = true)
    }

    @Test
    internal fun `secondary constructors create expected bitmasks`() {
        ControlledAppliance.Appliance.entries.forEach {
            assertThat(ControlledAppliance(it).bitmask, equalTo(it.bitmask))
        }

        assertThat(
            ControlledAppliance(ELECTRIC_VEHICLE, EXTERIOR_LIGHTING, INTERIOR_LIGHTING).bitmask,
            equalTo(ELECTRIC_VEHICLE.bitmask or EXTERIOR_LIGHTING.bitmask or INTERIOR_LIGHTING.bitmask)
        )
    }

    private fun validateBitmaskConstructor(
        bitmask: Int,
        expectIsElectricVehicle: Boolean = false,
        expectIsExteriorLighting: Boolean = false,
        expectIsGenerationSystem: Boolean = false,
        expectIsHvacCompressorOrFurnace: Boolean = false,
        expectIsInteriorLighting: Boolean = false,
        expectIsIrrigationPump: Boolean = false,
        expectIsManagedCommercialIndustrialLoad: Boolean = false,
        expectIsPoolPumpSpaJacuzzi: Boolean = false,
        expectIsSimpleMiscLoad: Boolean = false,
        expectIsSmartAppliance: Boolean = false,
        expectIsStripAndBaseboardHeater: Boolean = false,
        expectIsWaterHeater: Boolean = false,
    ) {
        ControlledAppliance(bitmask).apply {
            assertThat(isElectricVehicle, equalTo(expectIsElectricVehicle))
            assertThat(isExteriorLighting, equalTo(expectIsExteriorLighting))
            assertThat(isGenerationSystem, equalTo(expectIsGenerationSystem))
            assertThat(isHvacCompressorOrFurnace, equalTo(expectIsHvacCompressorOrFurnace))
            assertThat(isInteriorLighting, equalTo(expectIsInteriorLighting))
            assertThat(isIrrigationPump, equalTo(expectIsIrrigationPump))
            assertThat(isManagedCommercialIndustrialLoad, equalTo(expectIsManagedCommercialIndustrialLoad))
            assertThat(isPoolPumpSpaJacuzzi, equalTo(expectIsPoolPumpSpaJacuzzi))
            assertThat(isSimpleMiscLoad, equalTo(expectIsSimpleMiscLoad))
            assertThat(isSmartAppliance, equalTo(expectIsSmartAppliance))
            assertThat(isStripAndBaseboardHeater, equalTo(expectIsStripAndBaseboardHeater))
            assertThat(isWaterHeater, equalTo(expectIsWaterHeater))
        }
    }

}

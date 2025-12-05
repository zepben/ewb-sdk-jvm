/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.cim.extensions.iec61968.metering

import com.zepben.ewb.cim.iec61968.metering.ControlledAppliance
import com.zepben.ewb.cim.iec61968.metering.ControlledAppliance.Appliance.*
import com.zepben.ewb.cim.iec61968.metering.EndDeviceFunctionKind
import com.zepben.ewb.services.common.testdata.generateId
import com.zepben.ewb.services.network.NetworkService
import com.zepben.ewb.services.network.testdata.fillFields
import com.zepben.testutils.exception.ExpectException.Companion.expect
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class PanDemandResponseFunctionTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(PanDemandResponseFunction("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val panDemandResponseFunction = PanDemandResponseFunction(generateId())

        assertThat(panDemandResponseFunction.kind, equalTo(EndDeviceFunctionKind.UNKNOWN))
        assertThat(panDemandResponseFunction.applianceBitmask, nullValue())
        assertThat(panDemandResponseFunction.appliance, nullValue())

        panDemandResponseFunction.fillFields(NetworkService())

        assertThat(panDemandResponseFunction.kind, equalTo(EndDeviceFunctionKind.autonomousDst))
        assertThat(panDemandResponseFunction.applianceBitmask, equalTo(1365))
        assertThat(panDemandResponseFunction.appliance, equalTo(ControlledAppliance(1365)))
    }

    @Test
    internal fun `can add and remove appliances`() {
        val panDemandResponseFunction = PanDemandResponseFunction(generateId())

        // Add an appliance with no previous bitmask.
        assertThat("Should have added", panDemandResponseFunction.addAppliance(WATER_HEATER))
        panDemandResponseFunction.appliance.validate(isWaterHeater = true)

        // Add an appliance with a previous bitmask.
        assertThat("Should have added", panDemandResponseFunction.addAppliance(ELECTRIC_VEHICLE))
        panDemandResponseFunction.appliance.validate(isWaterHeater = true, isElectricVehicle = true)

        // Add a duplicate appliance.
        assertThat("Shouldn't have added duplicate", !panDemandResponseFunction.addAppliance(ELECTRIC_VEHICLE))
        panDemandResponseFunction.appliance.validate(isWaterHeater = true, isElectricVehicle = true)

        // Remove an appliance with a remaining bitmask.
        assertThat("Should have removed", panDemandResponseFunction.removeAppliance(WATER_HEATER))
        panDemandResponseFunction.appliance.validate(isElectricVehicle = true)

        // Remove an appliance that wasn't included.
        assertThat("Shouldn't have removed unused", !panDemandResponseFunction.removeAppliance(WATER_HEATER))
        panDemandResponseFunction.appliance.validate(isElectricVehicle = true)

        // Remove an appliance with no remaining bitmask.
        assertThat("Should have removed", panDemandResponseFunction.removeAppliance(ELECTRIC_VEHICLE))
        panDemandResponseFunction.appliance.validate()
    }

    @Test
    internal fun `can add and remove multiple appliances`() {
        val panDemandResponseFunction = PanDemandResponseFunction(generateId())

        // Must include appliances to add.
        expect { panDemandResponseFunction.addAppliances() }.toThrow<IllegalArgumentException>()

        // Add appliance with no previous bitmask.
        assertThat("Should have added", panDemandResponseFunction.addAppliances(SMART_APPLIANCE, IRRIGATION_PUMP))
        panDemandResponseFunction.appliance.validate(isSmartAppliance = true, isIrrigationPump = true)

        // Add partial duplicate appliance with a previous bitmask.
        assertThat("Should have added", panDemandResponseFunction.addAppliances(ELECTRIC_VEHICLE, IRRIGATION_PUMP))
        panDemandResponseFunction.appliance.validate(
            isSmartAppliance = true,
            isIrrigationPump = true,
            isElectricVehicle = true
        )

        // Add duplicate appliances.
        assertThat("Shouldn't have added", !panDemandResponseFunction.addAppliances(ELECTRIC_VEHICLE, IRRIGATION_PUMP))
        panDemandResponseFunction.appliance.validate(
            isSmartAppliance = true,
            isIrrigationPump = true,
            isElectricVehicle = true
        )

        // Must include appliances to remove.
        expect { panDemandResponseFunction.removeAppliances() }.toThrow<IllegalArgumentException>()

        // Remove appliances with a remaining bitmask.
        assertThat("Should have removed", panDemandResponseFunction.removeAppliances(ELECTRIC_VEHICLE, IRRIGATION_PUMP))
        panDemandResponseFunction.appliance.validate(isSmartAppliance = true)

        // Remove appliances that weren't included.
        assertThat("Shouldn't have removed", !panDemandResponseFunction.removeAppliances(ELECTRIC_VEHICLE, IRRIGATION_PUMP))
        panDemandResponseFunction.appliance.validate(isSmartAppliance = true)

        // Remove partial unused appliances with no remaining bitmask.
        assertThat("Should have removed", panDemandResponseFunction.removeAppliances(SMART_APPLIANCE, IRRIGATION_PUMP))
        panDemandResponseFunction.appliance.validate()
    }

    @Test
    internal fun `removing an appliance initialises the bitmask`() {
        // Removing an appliance with no previous bitmask marks the controlled appliance as used.
        val panDemandResponseFunction = PanDemandResponseFunction(generateId())

        assertThat(panDemandResponseFunction.applianceBitmask, nullValue())
        assertThat(panDemandResponseFunction.appliance, nullValue())

        assertThat("Shouldn't have removed", panDemandResponseFunction.removeAppliance(ELECTRIC_VEHICLE))

        assertThat(panDemandResponseFunction.applianceBitmask, equalTo(0))
        panDemandResponseFunction.appliance.validate()
    }

    @Test
    internal fun `removing appliances initialises the bitmask`() {
        // Removing appliances with no previous bitmask marks the controlled appliance as used.
        val panDemandResponseFunction = PanDemandResponseFunction(generateId())

        assertThat(panDemandResponseFunction.applianceBitmask, nullValue())
        assertThat(panDemandResponseFunction.appliance, nullValue())

        assertThat("Shouldn't have removed", panDemandResponseFunction.removeAppliances(ELECTRIC_VEHICLE, WATER_HEATER))

        assertThat(panDemandResponseFunction.applianceBitmask, equalTo(0))
        panDemandResponseFunction.appliance.validate()
    }

    private fun ControlledAppliance?.validate(
        isElectricVehicle: Boolean = false,
        isExteriorLighting: Boolean = false,
        isGenerationSystem: Boolean = false,
        isHvacCompressorOrFurnace: Boolean = false,
        isInteriorLighting: Boolean = false,
        isIrrigationPump: Boolean = false,
        isManagedCommercialIndustrialLoad: Boolean = false,
        isPoolPumpSpaJacuzzi: Boolean = false,
        isSimpleMiscLoad: Boolean = false,
        isSmartAppliance: Boolean = false,
        isStripAndBaseboardHeater: Boolean = false,
        isWaterHeater: Boolean = false
    ) {
        assertThat(this, notNullValue())
        assertThat(this!!.isElectricVehicle, equalTo(isElectricVehicle))
        assertThat(this.isExteriorLighting, equalTo(isExteriorLighting))
        assertThat(this.isGenerationSystem, equalTo(isGenerationSystem))
        assertThat(this.isHvacCompressorOrFurnace, equalTo(isHvacCompressorOrFurnace))
        assertThat(this.isInteriorLighting, equalTo(isInteriorLighting))
        assertThat(this.isIrrigationPump, equalTo(isIrrigationPump))
        assertThat(this.isManagedCommercialIndustrialLoad, equalTo(isManagedCommercialIndustrialLoad))
        assertThat(this.isPoolPumpSpaJacuzzi, equalTo(isPoolPumpSpaJacuzzi))
        assertThat(this.isSimpleMiscLoad, equalTo(isSimpleMiscLoad))
        assertThat(this.isSmartAppliance, equalTo(isSmartAppliance))
        assertThat(this.isStripAndBaseboardHeater, equalTo(isStripAndBaseboardHeater))
        assertThat(this.isWaterHeater, equalTo(isWaterHeater))
    }

}

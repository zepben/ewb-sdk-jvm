/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.extensions.iec61968.metering

import com.zepben.evolve.cim.extensions.ZBEX
import com.zepben.evolve.cim.iec61968.metering.ControlledAppliance
import com.zepben.evolve.cim.iec61968.metering.EndDeviceFunctionKind
import com.zepben.evolve.services.common.extensions.typeNameAndMRID
import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.testdata.fillFields
import com.zepben.testutils.exception.ExpectException.Companion.expect
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class PanDemandResponseFunctionTest {

    @ZBEX
    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    @Test
    internal fun constructorCoverage() {
        assertThat(PanDemandResponseFunction().mRID, not(equalTo("")))
        assertThat(PanDemandResponseFunction("id").mRID, equalTo("id"))
    }

    @Test
    internal fun accessorCoverage() {
        val panDemandResponseFunction = PanDemandResponseFunction()

        assertThat(panDemandResponseFunction.kind, equalTo(EndDeviceFunctionKind.UNKNOWN))
        assertThat(panDemandResponseFunction.appliance, nullValue())

        panDemandResponseFunction.fillFields(NetworkService())

        assertThat(panDemandResponseFunction.kind, equalTo(EndDeviceFunctionKind.autonomousDst))
        assertThat(panDemandResponseFunction.appliance, equalTo(ControlledAppliance.fromInt(1365)))
    }

    @Test
    internal fun assignApplianceByControlledAppliance() {
        val panDemandResponseFunction = PanDemandResponseFunction()
        val ca = ControlledAppliance.fromInt(300)

        panDemandResponseFunction.assignAppliance(ca)

        assertThat(panDemandResponseFunction.appliance, equalTo(ca))
    }

    @Test
    internal fun assignApplianceByInt() {
        val panDemandResponseFunction = PanDemandResponseFunction()
        val ca = ControlledAppliance.fromInt(300)

        panDemandResponseFunction.assignAppliance(300)

        assertThat(panDemandResponseFunction.appliance, equalTo(ca))
    }

    @Test
    internal fun assignApplianceBySettingEachValueIndividually() {
        val panDemandResponseFunction = PanDemandResponseFunction()
        val ca = ControlledAppliance.fromInt(1365)

        panDemandResponseFunction.assignAppliance(
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

        assertThat(panDemandResponseFunction.appliance, equalTo(ca))
    }

    @Test
    internal fun `assigning controlled appliance to panDemandResponseFunction with existing appliance throws`() {
        val panDemandResponseFunction = PanDemandResponseFunction()

        panDemandResponseFunction.fillFields(NetworkService())

        expect { panDemandResponseFunction.assignAppliance(1234) }
            .toThrow<IllegalArgumentException>()
            .withMessage("Unable to assign this ControlledAppliance to ${panDemandResponseFunction.typeNameAndMRID()}. A ControlledAppliance is already assigned to this PanDemandResponseFunction, try using the updateAppliance function.")

    }

    @Test
    internal fun `updating controlled appliance of panDemandResponseFunction without existing appliance throws`() {
        val panDemandResponseFunction = PanDemandResponseFunction()

        expect { panDemandResponseFunction.updateAppliance(isExteriorLighting = true) }
            .toThrow<IllegalArgumentException>()
            .withMessage("Unable to update ControlledAppliance of ${panDemandResponseFunction.typeNameAndMRID()}. A ControlledAppliance must be assigned to this PanDemandResponseFunction first, try using the assignAppliance function.")

    }

    @Test
    internal fun updateAppliance() {
        val panDemandResponseFunction = PanDemandResponseFunction()

        panDemandResponseFunction.assignAppliance(
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

        panDemandResponseFunction.updateAppliance(
            isStripAndBaseboardHeater = false,
            isWaterHeater = true
        )

        assertThat(
            panDemandResponseFunction.appliance, equalTo(
                ControlledAppliance(
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
                    isStripAndBaseboardHeater = false,
                    isWaterHeater = true
                )
            )
        )
    }

    @Test
    internal fun clearAppliance() {
        val panDemandResponseFunction = PanDemandResponseFunction()

        panDemandResponseFunction.fillFields(NetworkService())
        panDemandResponseFunction.clearAppliance()

        assertThat(panDemandResponseFunction.appliance, nullValue())
    }

}

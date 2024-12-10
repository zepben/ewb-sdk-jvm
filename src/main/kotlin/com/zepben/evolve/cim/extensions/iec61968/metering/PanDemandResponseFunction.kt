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
import com.zepben.evolve.cim.iec61968.metering.EndDeviceFunction
import com.zepben.evolve.cim.iec61968.metering.EndDeviceFunctionKind
import com.zepben.evolve.services.common.extensions.typeNameAndMRID

/**
 * PAN function that an end device supports, distinguished by 'kind'.
 *
 * @property kind Kind of this function.
 * @property appliance Appliance being controlled.
 */
@ZBEX
class PanDemandResponseFunction @JvmOverloads constructor(mRID: String = "") : EndDeviceFunction(mRID) {

    var kind: EndDeviceFunctionKind = EndDeviceFunctionKind.UNKNOWN
    private var _appliance: Int? = null

    /**
     * Assign a [ControlledAppliance] configuration to this [PanDemandResponseFunction].
     *
     * @property ca bitmask (Int) of a [ControlledAppliance]
     *
     * @throws IllegalArgumentException if the [PanDemandResponseFunction] already has a [ControlledAppliance] config.
     * @return Returns true if [ControlledAppliance] config is assigned
     */
    fun assignAppliance(ca: Int): Boolean {
        require(_appliance == null) { "Unable to assign this ControlledAppliance to ${typeNameAndMRID()}. A ControlledAppliance is already assigned to this PanDemandResponseFunction, try using the updateAppliance function." }
        _appliance = ca
        return true
    }

    /**
     * Assign a [ControlledAppliance] configuration to this [PanDemandResponseFunction].
     *
     * @property ca a [ControlledAppliance] object
     *
     * @throws IllegalArgumentException if the [PanDemandResponseFunction] already has a [ControlledAppliance] config.
     * @return Returns true if [ControlledAppliance] config is assigned
     */
    fun assignAppliance(ca: ControlledAppliance): Boolean {
        require(_appliance == null) { "Unable to assign this ControlledAppliance to ${typeNameAndMRID()}. A ControlledAppliance is already assigned to this PanDemandResponseFunction, try using the updateAppliance function." }
        _appliance = ca.toInt()
        return true
    }

    /**
     * Assign a [ControlledAppliance] config for this [PanDemandResponseFunction].
     * Default value will be false for unspecified properties.
     *
     * @property isElectricVehicle True if the appliance is an electric vehicle.
     * @property isExteriorLighting True if the appliance is exterior lighting.
     * @property isGenerationSystem True if the appliance is a generation system.
     * @property isHvacCompressorOrFurnace True if the appliance is HVAC compressor or furnace.
     * @property isInteriorLighting True if the appliance is interior lighting.
     * @property isIrrigationPump True if the appliance is an irrigation pump.
     * @property isManagedCommercialIndustrialLoad True if the appliance is managed commercial or industrial load.
     * @property isPoolPumpSpaJacuzzi True if the appliance is a pool, pump, spa or jacuzzi.
     * @property isSimpleMiscLoad True if the appliance is a simple miscellaneous load.
     * @property isSmartAppliance True if the appliance is a smart appliance.
     * @property isStripAndBaseboardHeater True if the appliance is a strip or baseboard heater.
     * @property isWaterHeater True if the appliance is a water heater.
     *
     * @throws IllegalArgumentException if the [PanDemandResponseFunction] already has a [ControlledAppliance] config.
     * @return Returns true if [ControlledAppliance] config is assigned
     */
    fun assignAppliance(
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
    ): Boolean {
        assignAppliance(
            ControlledAppliance(
                isElectricVehicle = isElectricVehicle,
                isExteriorLighting = isExteriorLighting,
                isGenerationSystem = isGenerationSystem,
                isHvacCompressorOrFurnace = isHvacCompressorOrFurnace,
                isInteriorLighting = isInteriorLighting,
                isIrrigationPump = isIrrigationPump,
                isManagedCommercialIndustrialLoad = isManagedCommercialIndustrialLoad,
                isPoolPumpSpaJacuzzi = isPoolPumpSpaJacuzzi,
                isSimpleMiscLoad = isSimpleMiscLoad,
                isSmartAppliance = isSmartAppliance,
                isStripAndBaseboardHeater = isStripAndBaseboardHeater,
                isWaterHeater = isWaterHeater
            )
        )
        return true
    }

    /**
     * The [ControlledAppliance] for this [PanDemandResponseFunction].
     */
    val appliance: ControlledAppliance? get() = _appliance?.let { ControlledAppliance.fromInt(it) }

    /**
     * Update [ControlledAppliance] config for this [PanDemandResponseFunction].
     *
     * @property isElectricVehicle True if the appliance is an electric vehicle.
     * @property isExteriorLighting True if the appliance is exterior lighting.
     * @property isGenerationSystem True if the appliance is a generation system.
     * @property isHvacCompressorOrFurnace True if the appliance is HVAC compressor or furnace.
     * @property isInteriorLighting True if the appliance is interior lighting.
     * @property isIrrigationPump True if the appliance is an irrigation pump.
     * @property isManagedCommercialIndustrialLoad True if the appliance is managed commercial or industrial load.
     * @property isPoolPumpSpaJacuzzi True if the appliance is a pool, pump, spa or jacuzzi.
     * @property isSimpleMiscLoad True if the appliance is a simple miscellaneous load.
     * @property isSmartAppliance True if the appliance is a smart appliance.
     * @property isStripAndBaseboardHeater True if the appliance is a strip or baseboard heater.
     * @property isWaterHeater True if the appliance is a water heater.
     *
     * @throws IllegalArgumentException if the [PanDemandResponseFunction] does not have a [ControlledAppliance] config.
     * @return Returns true if [ControlledAppliance] config is updated
     */
    fun updateAppliance(
        isElectricVehicle: Boolean? = null,
        isExteriorLighting: Boolean? = null,
        isGenerationSystem: Boolean? = null,
        isHvacCompressorOrFurnace: Boolean? = null,
        isInteriorLighting: Boolean? = null,
        isIrrigationPump: Boolean? = null,
        isManagedCommercialIndustrialLoad: Boolean? = null,
        isPoolPumpSpaJacuzzi: Boolean? = null,
        isSimpleMiscLoad: Boolean? = null,
        isSmartAppliance: Boolean? = null,
        isStripAndBaseboardHeater: Boolean? = null,
        isWaterHeater: Boolean? = null
    ): Boolean {
        require(_appliance != null) { "Unable to update ControlledAppliance of ${typeNameAndMRID()}. A ControlledAppliance must be assigned to this PanDemandResponseFunction first, try using the assignAppliance function." }
        appliance?.also {
            _appliance = ControlledAppliance(
                isElectricVehicle = isElectricVehicle ?: it.isElectricVehicle,
                isExteriorLighting = isExteriorLighting ?: it.isExteriorLighting,
                isGenerationSystem = isGenerationSystem ?: it.isGenerationSystem,
                isHvacCompressorOrFurnace = isHvacCompressorOrFurnace ?: it.isHvacCompressorOrFurnace,
                isInteriorLighting = isInteriorLighting ?: it.isInteriorLighting,
                isIrrigationPump = isIrrigationPump ?: it.isIrrigationPump,
                isManagedCommercialIndustrialLoad = isManagedCommercialIndustrialLoad ?: it.isManagedCommercialIndustrialLoad,
                isPoolPumpSpaJacuzzi = isPoolPumpSpaJacuzzi ?: it.isPoolPumpSpaJacuzzi,
                isSimpleMiscLoad = isSimpleMiscLoad ?: it.isSimpleMiscLoad,
                isSmartAppliance = isSmartAppliance ?: it.isSmartAppliance,
                isStripAndBaseboardHeater = isStripAndBaseboardHeater ?: it.isStripAndBaseboardHeater,
                isWaterHeater = isWaterHeater ?: it.isWaterHeater
            ).toInt()
        }

        return true
    }

    /**
     * Clear [ControlledAppliance] configuration.
     * @return This [PanDemandResponseFunction] for fluent use.
     */
    fun clearAppliance(): PanDemandResponseFunction {
        _appliance = null
        return this
    }
}

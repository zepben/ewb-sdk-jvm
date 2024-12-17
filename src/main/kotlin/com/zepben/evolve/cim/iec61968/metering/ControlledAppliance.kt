/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61968.metering

/**
 * Appliance controlled with a PAN device control.
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
 */
data class ControlledAppliance(internal val bitmask: Int) {

    constructor(appliance: Appliance) : this(appliance.bitmask)
    constructor(appliance: Appliance, vararg appliances: Appliance) : this(appliances.fold(appliance.bitmask) { bitmask, next -> bitmask or next.bitmask })

    val isElectricVehicle: Boolean get() = Appliance.ELECTRIC_VEHICLE in bitmask
    val isExteriorLighting: Boolean get() = Appliance.EXTERIOR_LIGHTING in bitmask
    val isGenerationSystem: Boolean get() = Appliance.GENERATION_SYSTEM in bitmask
    val isHvacCompressorOrFurnace: Boolean get() = Appliance.HVAC_COMPRESSOR_OR_FURNACE in bitmask
    val isInteriorLighting: Boolean get() = Appliance.INTERIOR_LIGHTING in bitmask
    val isIrrigationPump: Boolean get() = Appliance.IRRIGATION_PUMP in bitmask
    val isManagedCommercialIndustrialLoad: Boolean get() = Appliance.MANAGED_COMMERCIAL_INDUSTRIAL_LOAD in bitmask
    val isPoolPumpSpaJacuzzi: Boolean get() = Appliance.POOL_PUMP_SPA_JACUZZI in bitmask
    val isSimpleMiscLoad: Boolean get() = Appliance.SIMPLE_MISC_LOAD in bitmask
    val isSmartAppliance: Boolean get() = Appliance.SMART_APPLIANCE in bitmask
    val isStripAndBaseboardHeater: Boolean get() = Appliance.STRIP_AND_BASEBOARD_HEATER in bitmask
    val isWaterHeater: Boolean get() = Appliance.WATER_HEATER in bitmask

    enum class Appliance(index: Int) {
        ELECTRIC_VEHICLE(0),
        EXTERIOR_LIGHTING(1),
        GENERATION_SYSTEM(2),
        HVAC_COMPRESSOR_OR_FURNACE(3),
        INTERIOR_LIGHTING(4),
        IRRIGATION_PUMP(5),
        MANAGED_COMMERCIAL_INDUSTRIAL_LOAD(6),
        POOL_PUMP_SPA_JACUZZI(7),
        SIMPLE_MISC_LOAD(8),
        SMART_APPLIANCE(9),
        STRIP_AND_BASEBOARD_HEATER(10),
        WATER_HEATER(11);

        val bitmask: Int = 1 shl index
    }

    private operator fun Int.contains(appliance: Appliance): Boolean = (this and appliance.bitmask) != 0

}

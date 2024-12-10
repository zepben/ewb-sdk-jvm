/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.cim.iec61968.metering

import kotlin.reflect.full.declaredMemberProperties

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
data class ControlledAppliance @JvmOverloads constructor(
    val isElectricVehicle: Boolean = false,
    val isExteriorLighting: Boolean = false,
    val isGenerationSystem: Boolean = false,
    val isHvacCompressorOrFurnace: Boolean = false,
    val isInteriorLighting: Boolean = false,
    val isIrrigationPump: Boolean = false,
    val isManagedCommercialIndustrialLoad: Boolean = false,
    val isPoolPumpSpaJacuzzi: Boolean = false,
    val isSimpleMiscLoad: Boolean = false,
    val isSmartAppliance: Boolean = false,
    val isStripAndBaseboardHeater: Boolean = false,
    val isWaterHeater: Boolean = false
) {
    /**
     * Return the int representation of this [ControlledAppliance]
     */
    fun toInt(): Int {
        var result = 0
        ControlledAppliance::class.declaredMemberProperties.forEach { property ->
            result += bitmask(property.name).times(if (property.get(this) == false) 0 else 1)
        }
        return result
    }

    companion object {
        /**
         * Return a [ControlledAppliance]
         *
         * @param int the bitmask of the configuration
         * @return The [ControlledAppliance] with the specified configuration.
         */
        fun fromInt(int: Int): ControlledAppliance {
            return ControlledAppliance::class.declaredMemberProperties.map {
                decode(it.name, int)
            }.let { value ->
                ControlledAppliance(
                    value[0],
                    value[1],
                    value[2],
                    value[3],
                    value[4],
                    value[5],
                    value[6],
                    value[7],
                    value[8],
                    value[9],
                    value[10],
                    value[11]
                )
            }
        }

        // Decode the setting for a variable base on input Int
        private fun decode(variableName: String, int: Int): Boolean {
            return int and bitmask(variableName) > 0
        }

        // Get the bitmask of a property of this class
        private fun bitmask(variableName: String): Int {
            return when (variableName) {
                "isElectricVehicle" -> 1 shl 0
                "isExteriorLighting" -> 1 shl 1
                "isGenerationSystem" -> 1 shl 2
                "isHvacCompressorOrFurnace" -> 1 shl 3
                "isInteriorLighting" -> 1 shl 4
                "isIrrigationPump" -> 1 shl 5
                "isManagedCommercialIndustrialLoad" -> 1 shl 6
                "isPoolPumpSpaJacuzzi" -> 1 shl 7
                "isSimpleMiscLoad" -> 1 shl 8
                "isSmartAppliance" -> 1 shl 9
                "isStripAndBaseboardHeater" -> 1 shl 10
                "isWaterHeater" -> 1 shl 11
                else -> -1
            }
        }
    }

}

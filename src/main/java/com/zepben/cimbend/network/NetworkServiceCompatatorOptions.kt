/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 * This file is part of evolve-sdk-jvm.
 *
 * evolve-sdk-jvm is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * evolve-sdk-jvm is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with evolve-sdk-jvm.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.zepben.cimbend.network

data class NetworkServiceCompatatorOptions(
    val compareTerminals: Boolean,
    val compareTracedPhases: Boolean,
    val compareFeederEquipment: Boolean,
    val compareEquipmentContainers: Boolean,
    val compareLvSimplification: Boolean
) {

    companion object {
        @JvmStatic
        fun all(): NetworkServiceCompatatorOptions =
            NetworkServiceCompatatorOptions(
                compareTerminals = true,
                compareTracedPhases = true,
                compareFeederEquipment = true,
                compareEquipmentContainers = true,
                compareLvSimplification = true
            )

        @JvmStatic
        fun none(): NetworkServiceCompatatorOptions =
            NetworkServiceCompatatorOptions(
                compareTerminals = false,
                compareTracedPhases = false,
                compareFeederEquipment = false,
                compareEquipmentContainers = false,
                compareLvSimplification = false
            )

        @JvmStatic
        fun of(): Builder = Builder()
    }

    class Builder internal constructor() {
        private var compareTerminals = false
        private var comparePhases = false
        private var compareFeederEquipment = false
        private var compareEquipmentContainers = false
        private var compareLvSimplification = false
        fun compareTerminals(): Builder {
            compareTerminals = true
            return this
        }

        fun comparePhases(): Builder {
            comparePhases = true
            return this
        }

        fun compareFeederEquipment(): Builder {
            compareFeederEquipment = true
            return this
        }

        fun compareEquipmentContainers(): Builder {
            compareEquipmentContainers = true
            return this
        }

        fun compareLvSimplification(): Builder {
            compareLvSimplification = true
            return this
        }

        fun build(): NetworkServiceCompatatorOptions {
            return NetworkServiceCompatatorOptions(
                compareTerminals,
                comparePhases,
                compareFeederEquipment,
                compareEquipmentContainers,
                compareLvSimplification
            )
        }
    }
}

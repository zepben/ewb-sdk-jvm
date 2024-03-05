/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network

data class NetworkServiceComparatorOptions(
    val compareTerminals: Boolean,
    val compareTracedPhases: Boolean,
    val compareFeederEquipment: Boolean,
    val compareEquipmentContainers: Boolean,
    val compareLvSimplification: Boolean
) {

    companion object {
        @JvmStatic
        fun all(): NetworkServiceComparatorOptions =
            NetworkServiceComparatorOptions(
                compareTerminals = true,
                compareTracedPhases = true,
                compareFeederEquipment = true,
                compareEquipmentContainers = true,
                compareLvSimplification = true
            )

        @JvmStatic
        fun none(): NetworkServiceComparatorOptions =
            NetworkServiceComparatorOptions(
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

        fun build(): NetworkServiceComparatorOptions {
            return NetworkServiceComparatorOptions(
                compareTerminals,
                comparePhases,
                compareFeederEquipment,
                compareEquipmentContainers,
                compareLvSimplification
            )
        }
    }
}

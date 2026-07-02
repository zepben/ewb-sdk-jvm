/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.network

/**
 * Contains the options that are used to customise how the network service comparison is completed.
 *
 * @param compareTerminals Indicates if terminals should be compared.
 * @param compareTracedPhases Flag that is unused by the comparator and has been deprecated.
 * @param compareFeederEquipment Indicates if the equipment belonging to a Feeder (or an LvFeeder) should be compared.
 * @param compareEquipmentContainers Indicates if equipment containers of any kind should be compared.
 * @param compareLvSimplification Indicates if LV simplification should be compared. This will also apply to LV
 * equipment <-> usage point <-> end device links.
 * @param compareRunTime Indicates if variables/relationships that are calculated at runtime should be compared.
 */
data class NetworkServiceComparatorOptions(
    val compareTerminals: Boolean = true,
    @Deprecated("compareTracedPhases is unused, use compareRunTime instead")
    val compareTracedPhases: Boolean = true,
    val compareFeederEquipment: Boolean = true,
    val compareEquipmentContainers: Boolean = true,
    val compareLvSimplification: Boolean = true,
    val compareRunTime: Boolean = true,
) {

    companion object {

        /**
         * Create a [NetworkServiceComparatorOptions] with all options enabled.
         *
         * @return The [NetworkServiceComparatorOptions] with all options enabled.
         */
        @JvmStatic
        fun all(): NetworkServiceComparatorOptions =
            NetworkServiceComparatorOptions()

        /**
         * Create a [NetworkServiceComparatorOptions] with all options disabled.
         *
         * @return The [NetworkServiceComparatorOptions] with all options disabled.
         */
        @JvmStatic
        fun none(): NetworkServiceComparatorOptions =
            NetworkServiceComparatorOptions(
                compareTerminals = false,
                compareTracedPhases = false,
                compareFeederEquipment = false,
                compareEquipmentContainers = false,
                compareLvSimplification = false,
                compareRunTime = false,
            )

        @JvmStatic
        @Deprecated("The builder has been deprecated. Replace with a direct call to the constructor, passing the appropriate arguments.")
        fun of(): Builder = Builder()
    }

    @Deprecated("The builder has been deprecated. Replace with a direct call to the constructor, passing the appropriate arguments.")
    class Builder internal constructor() {
        private var compareTerminals = false
        private var comparePhases = false
        private var compareFeederEquipment = false
        private var compareEquipmentContainers = false
        private var compareLvSimplification = false

        @Deprecated("The builder has been deprecated. Replace with a direct call to the constructor, passing `true` for the `compareTerminals` argument.")
        fun compareTerminals(): Builder {
            compareTerminals = true
            return this
        }

        @Deprecated("comparePhases is unused in the comparator, so this value has no effect.")
        fun comparePhases(): Builder {
            comparePhases = true
            return this
        }

        @Deprecated("The builder has been deprecated. Replace with a direct call to the constructor, passing `true` for the `compareFeederEquipment` argument.")
        fun compareFeederEquipment(): Builder {
            compareFeederEquipment = true
            return this
        }

        @Deprecated("The builder has been deprecated. Replace with a direct call to the constructor, passing `true` for the `compareEquipmentContainers` argument.")
        fun compareEquipmentContainers(): Builder {
            compareEquipmentContainers = true
            return this
        }

        @Deprecated("The builder has been deprecated. Replace with a direct call to the constructor, passing `true` for the `compareLvSimplification` argument.")
        fun compareLvSimplification(): Builder {
            compareLvSimplification = true
            return this
        }

        @Deprecated("The builder has been deprecated. Replace with a direct call to the constructor, passing the appropriate arguments.")
        fun build(): NetworkServiceComparatorOptions {
            return NetworkServiceComparatorOptions(
                compareTerminals,
                comparePhases,
                compareFeederEquipment,
                compareEquipmentContainers,
                compareLvSimplification,
                // You can't turn on the new `compareRunTime` option via the deprecated builder.
                compareRunTime = false,
            )
        }
    }
}

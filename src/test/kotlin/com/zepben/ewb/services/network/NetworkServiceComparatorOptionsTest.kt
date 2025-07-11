/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.network

import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test

internal class NetworkServiceComparatorOptionsTest {

    @Test
    internal fun all() {
        val options = NetworkServiceComparatorOptions.all()
        assertThat("compareTracedPhases should be true for all()", options.compareTracedPhases)
        assertThat("compareTerminals should be true for all()", options.compareTerminals)
        assertThat("compareFeederEquipment should be true for all()", options.compareFeederEquipment)
        assertThat("compareEquipmentContainers should be true for all()", options.compareEquipmentContainers)
        assertThat("compareLvSimplification should be true for all()", options.compareLvSimplification)
    }

    @Test
    internal fun none() {
        val options = NetworkServiceComparatorOptions.none()
        assertThat("compareTracedPhases should be false for none()", !options.compareTracedPhases)
        assertThat("compareTerminals should be false for none()", !options.compareTerminals)
        assertThat("compareFeederEquipment should be false for none()", !options.compareFeederEquipment)
        assertThat("compareEquipmentContainers should be false for none()", !options.compareEquipmentContainers)
        assertThat("compareLvSimplification should be false for none()", !options.compareLvSimplification)
    }

    @Test
    internal fun builderAll() {
        val options = NetworkServiceComparatorOptions.of()
            .comparePhases()
            .compareTerminals()
            .compareFeederEquipment()
            .compareEquipmentContainers()
            .compareLvSimplification()
            .build()

        assertThat("compareTracedPhases should be true when building with all options enabled", options.compareTracedPhases)
        assertThat("compareTerminals should be true when building with all options enabled", options.compareTerminals)
        assertThat("compareFeederEquipment should be true when building with all options enabled", options.compareFeederEquipment)
        assertThat("compareEquipmentContainers should be true when building with all options enabled", options.compareEquipmentContainers)
        assertThat("compareLvSimplification should be true when building with all options enabled", options.compareLvSimplification)
    }

    @Test
    internal fun builderNone() {
        val options = NetworkServiceComparatorOptions.of().build()

        assertThat("compareTracedPhases should be false when building without any options enabled", !options.compareTracedPhases)
        assertThat("compareTerminals should be false when building without any options enabled", !options.compareTerminals)
        assertThat("compareFeederEquipment should be false when building without any options enabled", !options.compareFeederEquipment)
        assertThat("compareEquipmentContainers should be false when building without any options enabled", !options.compareEquipmentContainers)
        assertThat("compareLvSimplification should be false when building without any options enabled", !options.compareLvSimplification)
    }
}

/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.network

import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class NetworkServiceComparatorOptionsTest {

    companion object {
        @JvmField
        @RegisterExtension
        val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()
    }

    @Test
    internal fun all() {
        val options = NetworkServiceComparatorOptions.all()
        assertThat("compareTracedPhases should be enabled for all()", options.compareTracedPhases)
        assertThat("compareTerminals should be enabled for all()", options.compareTerminals)
        assertThat("compareFeederEquipment should be enabled for all()", options.compareFeederEquipment)
        assertThat("compareEquipmentContainers should be enabled for all()", options.compareEquipmentContainers)
        assertThat("compareLvSimplification should be enabled for all()", options.compareLvSimplification)
        assertThat("compareRunTime should be enabled for all()", options.compareRunTime)
    }

    @Test
    internal fun none() {
        val options = NetworkServiceComparatorOptions.none()
        assertThat("compareTracedPhases should be disabled for none()", !options.compareTracedPhases)
        assertThat("compareTerminals should be disabled for none()", !options.compareTerminals)
        assertThat("compareFeederEquipment should be disabled for none()", !options.compareFeederEquipment)
        assertThat("compareEquipmentContainers should be disabled for none()", !options.compareEquipmentContainers)
        assertThat("compareLvSimplification should be disabled for none()", !options.compareLvSimplification)
        assertThat("compareRunTime should be disabled for none()", !options.compareRunTime)
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

        assertThat("compareTracedPhases should be enabled when building with all options enabled", options.compareTracedPhases)
        assertThat("compareTerminals should be enabled when building with all options enabled", options.compareTerminals)
        assertThat("compareFeederEquipment should be enabled when building with all options enabled", options.compareFeederEquipment)
        assertThat("compareEquipmentContainers should be enabled when building with all options enabled", options.compareEquipmentContainers)
        assertThat("compareLvSimplification should be enabled when building with all options enabled", options.compareLvSimplification)

        // You can't turn on the new `compareRunTime` option via the deprecated builder.
        assertThat("compareRunTime should be disabled in the deprecated builder", !options.compareRunTime)
    }

    @Test
    internal fun builderNone() {
        val options = NetworkServiceComparatorOptions.of().build()

        assertThat("compareTracedPhases should be disabled when building without any options enabled", !options.compareTracedPhases)
        assertThat("compareTerminals should be disabled when building without any options enabled", !options.compareTerminals)
        assertThat("compareFeederEquipment should be disabled when building without any options enabled", !options.compareFeederEquipment)
        assertThat("compareEquipmentContainers should be disabled when building without any options enabled", !options.compareEquipmentContainers)
        assertThat("compareLvSimplification should be disabled when building without any options enabled", !options.compareLvSimplification)
        assertThat("compareRunTime should be disabled in the deprecated builder", !options.compareRunTime)
    }

    @Test
    internal fun constructorCoverage() {
        // Stupid test to give coverage to generated Kotlin code that is guaranteed to work.
        NetworkServiceComparatorOptions()
        NetworkServiceComparatorOptions(compareTerminals = false)
        NetworkServiceComparatorOptions(compareRunTime = false)
    }

}

/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.network.equivalents

import com.zepben.ewb.cim.iec61970.base.core.EquipmentContainer
import com.zepben.ewb.cim.iec61970.base.core.Feeder
import com.zepben.ewb.cim.iec61970.base.core.Substation
import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import kotlin.reflect.KClass

internal class EdgeDetectionDetailsTest {

    @JvmField
    @RegisterExtension
    var systemOut: SystemLogExtension = SystemLogExtension.SYSTEM_OUT.captureLog().muteOnSuccess()

    @Test
    internal fun `factory methods fill in object as expected`() {
        val feeder = Feeder()
        val substation = Substation()

        validate(setOf(Feeder::class, Substation::class), setOf(feeder, substation)) { EdgeDetectionDetails.between(feeder, substation) }
        validate(setOf(Feeder::class, Substation::class), setOf(feeder)) { EdgeDetectionDetails.between(feeder, Substation::class) }
        validate(setOf(Feeder::class, Substation::class), emptySet()) { EdgeDetectionDetails.between(Feeder::class, Substation::class) }
    }

    private fun validate(
        expectedClasses: Set<KClass<out EquipmentContainer>>,
        expectedContainers: Set<EquipmentContainer>,
        creator: () -> EdgeDetectionDetails
    ) {
        creator().apply {
            assertThat(edgeContainerClasses, equalTo(expectedClasses))
            assertThat(containers, equalTo(expectedContainers))
        }
    }

}

/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.network.tracing.networktrace.operators

import com.zepben.ewb.cim.iec61970.base.core.Equipment
import io.mockk.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import kotlin.reflect.KMutableProperty1

class InServiceStateOperatorsTest {

    private val normal = InServiceStateOperators.NORMAL
    private val current = InServiceStateOperators.CURRENT

    @Test
    fun isInService() {
        fun test(operators: InServiceStateOperators, inServiceProp: KMutableProperty1<Equipment, Boolean>) {
            val equipment = mockk<Equipment>()
            every { inServiceProp(equipment) } returns true

            val result = operators.isInService(equipment)

            assertThat(result, equalTo(true))
            verify { inServiceProp(equipment) }
        }

        test(normal, Equipment::normallyInService)
        test(current, Equipment::inService)
    }

    @Test
    fun setInService() {
        fun test(operators: InServiceStateOperators, inServiceProp: KMutableProperty1<Equipment, Boolean>) {
            val equipment = mockk<Equipment>()
            every { inServiceProp.set(equipment, true) } just runs

            operators.setInService(equipment, true)

            verify { inServiceProp.set(equipment, true) }
        }

        test(normal, Equipment::normallyInService)
        test(current, Equipment::inService)
    }
}

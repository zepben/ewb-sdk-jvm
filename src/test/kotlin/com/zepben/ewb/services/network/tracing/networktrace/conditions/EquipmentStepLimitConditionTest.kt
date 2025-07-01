/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.network.tracing.networktrace.conditions

import com.zepben.ewb.services.network.tracing.networktrace.NetworkTraceStep
import io.mockk.mockk
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

class EquipmentStepLimitConditionTest {

    @Test
    fun `should stop when step number is equal to limit`() {
        val step = NetworkTraceStep(mockk(), 0, 2, Unit)
        val result = EquipmentStepLimitCondition<Unit>(2).shouldStop(step, mockk())
        assertThat(result, equalTo(true))
    }

    @Test
    fun `should stop when step number is greater than limit`() {
        val step = NetworkTraceStep(mockk(), 0, 3, Unit)
        val result = EquipmentStepLimitCondition<Unit>(2).shouldStop(step, mockk())
        assertThat(result, equalTo(true))
    }

    @Test
    fun `should not stop when step number is less than limit`() {
        val step = NetworkTraceStep(mockk(), 3, 1, Unit)
        val result = EquipmentStepLimitCondition<Unit>(2).shouldStop(step, mockk())
        assertThat(result, equalTo(false))
    }

}

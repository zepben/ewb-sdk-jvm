/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.networktrace.conditions

import com.zepben.evolve.cim.iec61970.base.wires.Breaker
import com.zepben.evolve.cim.iec61970.base.wires.Junction
import com.zepben.evolve.cim.iec61970.base.wires.Switch
import com.zepben.evolve.services.network.tracing.networktrace.NetworkTraceStep
import com.zepben.evolve.services.network.tracing.traversal.StepContext
import io.mockk.called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

class EquipmentTypeStepLimitConditionTest {


    @Test
    fun `should stop when matched type count is equal to limit`() {
        val condition = EquipmentTypeStepLimitCondition<Unit>(2, Switch::class)

        val context = mockk<StepContext>()
        every { context.getValue<Int>(condition.key) } returns 2

        val result = condition.shouldStop(mockk(), context)
        assertThat(result, equalTo(true))
    }

    @Test
    fun `should stop when matched type count is greater than limit`() {
        val condition = EquipmentTypeStepLimitCondition<Unit>(2, Switch::class)

        val context = mockk<StepContext>()
        every { context.getValue<Int>(condition.key) } returns 3

        val result = condition.shouldStop(mockk(), context)
        assertThat(result, equalTo(true))
    }

    @Test
    fun `should not stop when matched type count is less than limit`() {
        val condition = EquipmentTypeStepLimitCondition<Unit>(2, Switch::class)

        val context = mockk<StepContext>()
        every { context.getValue<Int>(condition.key) } returns 1

        val result = condition.shouldStop(mockk(), context)
        assertThat(result, equalTo(false))
    }

    @Test
    fun `always returns 0 for initial value`() {
        val step = mockk<NetworkTraceStep<Unit>>()
        val result = EquipmentTypeStepLimitCondition<Unit>(2, Switch::class).computeInitialValue(step)

        assertThat(result, equalTo(0))
        verify { step wasNot called }
    }

    @Test
    fun `computes correct next value on internal step`() {
        val condition = EquipmentTypeStepLimitCondition<Unit>(2, Switch::class)

        val currentStep = mockk<NetworkTraceStep<Unit>>()
        val nextPath = mockk<NetworkTraceStep.Path> { every { tracedInternally } returns true }
        val nextStep = mockk<NetworkTraceStep<Unit>> { every { path } returns nextPath }

        val result = condition.computeNextValue(nextStep, currentStep, 1)
        assertThat(result, equalTo(1))
        verify { currentStep wasNot called }
    }

    @Test
    fun `computes correct next value on matching external step`() {
        val condition = EquipmentTypeStepLimitCondition<Unit>(2, Switch::class)

        val currentStep = mockk<NetworkTraceStep<Unit>>()
        val nextPath = mockk<NetworkTraceStep.Path> {
            every { tracedInternally } returns false
            every { toEquipment } returns mockk<Breaker>()
        }
        val nextStep = mockk<NetworkTraceStep<Unit>> { every { path } returns nextPath }

        val result = condition.computeNextValue(nextStep, currentStep, 1)
        assertThat(result, equalTo(2))
        verify { currentStep wasNot called }
    }

    @Test
    fun `computes correct next value on non matching external step`() {
        val condition = EquipmentTypeStepLimitCondition<Unit>(2, Switch::class)

        val currentStep = mockk<NetworkTraceStep<Unit>>()
        val nextPath = mockk<NetworkTraceStep.Path> {
            every { tracedInternally } returns false
            every { toEquipment } returns mockk<Junction>()
        }
        val nextStep = mockk<NetworkTraceStep<Unit>> { every { path } returns nextPath }

        val result = condition.computeNextValue(nextStep, currentStep, 1)
        assertThat(result, equalTo(1))
        verify { currentStep wasNot called }
    }
}

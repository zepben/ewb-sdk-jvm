package com.zepben.evolve.services.network.tracing.networktrace.conditions

import com.zepben.evolve.services.network.tracing.networktrace.NetworkTraceStep
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

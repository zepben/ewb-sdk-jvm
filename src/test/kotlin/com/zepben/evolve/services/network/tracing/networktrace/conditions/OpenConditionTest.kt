package com.zepben.evolve.services.network.tracing.networktrace.conditions

import com.zepben.evolve.cim.iec61970.base.core.ConductingEquipment
import com.zepben.evolve.cim.iec61970.base.wires.SinglePhaseKind
import com.zepben.evolve.cim.iec61970.base.wires.Switch
import com.zepben.evolve.services.network.tracing.networktrace.NetworkTraceStep
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

class OpenConditionTest {

    @Test
    fun `always queues external steps`() {
        val isOpen = mockk<(Switch, SinglePhaseKind?) -> Boolean>()
        val spk = mockk<SinglePhaseKind>()

        val nextStep = mockk<NetworkTraceStep<Unit>> {
            every { type } returns NetworkTraceStep.Type.EXTERNAL
        }

        val result = OpenCondition<Unit>(isOpen, spk).shouldQueue(nextStep, mockk(), mockk(), mockk())
        assertThat(result, equalTo(true))
    }

    @Test
    fun `always queues non switch equipment`() {
        val isOpen = mockk<(Switch, SinglePhaseKind?) -> Boolean>()
        val spk = mockk<SinglePhaseKind>()

        val nextPath = mockk<NetworkTraceStep.Path>() { every { toEquipment } returns mockk<ConductingEquipment>() }
        val nextStep = mockk<NetworkTraceStep<Unit>> {
            every { type } returns NetworkTraceStep.Type.INTERNAL
            every { path } returns nextPath
        }

        val result = OpenCondition<Unit>(isOpen, spk).shouldQueue(nextStep, mockk(), mockk(), mockk())
        assertThat(result, equalTo(true))
    }

    @Test
    fun `queues closed switch equipment`() {
        val switch = mockk<Switch>()
        val spk = mockk<SinglePhaseKind>()

        val isOpen = mockk<(Switch, SinglePhaseKind?) -> Boolean>()
        every { isOpen(switch, spk) } returns false

        val nextPath = mockk<NetworkTraceStep.Path> { every { toEquipment } returns switch }
        val nextStep = mockk<NetworkTraceStep<Unit>> {
            every { type } returns NetworkTraceStep.Type.INTERNAL
            every { path } returns nextPath
        }

        val result = OpenCondition<Unit>(isOpen, spk).shouldQueue(nextStep, mockk(), mockk(), mockk())
        assertThat(result, equalTo(true))
        verify { isOpen(switch, spk) }
    }

    @Test
    fun `does not queue open switch equipment`() {
        val switch = mockk<Switch>()
        val spk = mockk<SinglePhaseKind>()

        val isOpen = mockk<(Switch, SinglePhaseKind?) -> Boolean>()
        every { isOpen(switch, spk) } returns true

        val nextPath = mockk<NetworkTraceStep.Path> { every { toEquipment } returns switch }
        val nextStep = mockk<NetworkTraceStep<Unit>> {
            every { type } returns NetworkTraceStep.Type.INTERNAL
            every { path } returns nextPath
        }

        val result = OpenCondition<Unit>(isOpen, spk).shouldQueue(nextStep, mockk(), mockk(), mockk())
        assertThat(result, equalTo(false))
        verify { isOpen(switch, spk) }
    }
}

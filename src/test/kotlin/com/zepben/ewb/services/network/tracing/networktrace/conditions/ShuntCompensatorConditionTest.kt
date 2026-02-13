/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.network.tracing.networktrace.conditions

import com.zepben.ewb.cim.iec61970.base.core.ConductingEquipment
import com.zepben.ewb.cim.iec61970.base.core.Terminal
import com.zepben.ewb.cim.iec61970.base.wires.LinearShuntCompensator
import com.zepben.ewb.services.common.testdata.generateId
import com.zepben.ewb.services.network.tracing.networktrace.NetworkTraceStep
import io.mockk.every
import io.mockk.mockk
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

class ShuntCompensatorConditionTest {

    private val shuntCompensator = LinearShuntCompensator("sc")
    private val fromTerm = Terminal(mRID = generateId()).also { shuntCompensator.addTerminal(it) }
    private val toTerm = Terminal(mRID = generateId()).also { shuntCompensator.addTerminal(it) }
    private val groundTerm = Terminal(mRID = generateId()).also { shuntCompensator.groundingTerminal = it }

    @Test
    fun `always queues external steps`() {
        validateQueues(mockk<NetworkTraceStep<Unit>> { every { type } returns NetworkTraceStep.Type.EXTERNAL })
    }

    @Test
    fun `always queues non ShuntCompensator equipment`() {
        validateQueues(
            stepOf(
                mockk<NetworkTraceStep.Path> {
                    every { toEquipment } returns mockk<ConductingEquipment>()
                    every { tracedInternally } returns true
                }
            )
        )
    }

    @Test
    fun `queues ShuntCompensator paths that don't use the grounding terminal`() {
        validateQueues(stepOf(NetworkTraceStep.Path(fromTerm, toTerm)))
    }

    @Test
    fun `does not queue from grounding terminal`() {
        validateQueues(stepOf(NetworkTraceStep.Path(groundTerm, toTerm)), shouldQueue = false)
    }

    @Test
    fun `does not queue onto grounding terminal`() {
        validateQueues(stepOf(NetworkTraceStep.Path(fromTerm, groundTerm)), shouldQueue = false)
    }

    private fun stepOf(path: NetworkTraceStep.Path) = NetworkTraceStep<Unit>(path, 0, 0, Unit)

    private fun validateQueues(nextStep: NetworkTraceStep<Unit>, shouldQueue: Boolean = true) {
        val result = ShuntCompensatorCondition.StopOnGround<Unit>().shouldQueue(nextStep, mockk(), mockk(), mockk())
        assertThat(result, equalTo(shouldQueue))
    }

}

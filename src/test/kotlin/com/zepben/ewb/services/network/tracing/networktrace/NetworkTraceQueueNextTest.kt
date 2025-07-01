/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.network.tracing.networktrace

import com.zepben.ewb.services.network.tracing.networktrace.operators.NetworkStateOperators
import com.zepben.ewb.services.network.tracing.traversal.StepContext
import io.mockk.every
import io.mockk.mockk
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test

class NetworkTraceQueueNextTest {

    private val stateOperators = mockk<NetworkStateOperators>()
    private val dataComputer = mockk<ComputeData<String>>()
    private val queuer = TestQueuer<String>()
    private val branchingQueuer = TestQueuer<String>()

    @Test
    fun `queues next basic`() {
        val queueNext = NetworkTraceQueueNext.Basic(stateOperators, dataComputer)

        val seedPath = mockk<NetworkTraceStep.Path>()
        val seedStep = mockk<NetworkTraceStep<String>> {
            every { path } returns seedPath
            every { numEquipmentSteps } returns 1
            every { numTerminalSteps } returns 3
        }
        val seedContext = mockk<StepContext>()

        val nextPath1 = mockk<NetworkTraceStep.Path> { every { tracedExternally } returns true }
        val nextPath2 = mockk<NetworkTraceStep.Path> { every { tracedExternally } returns false }
        every { stateOperators.nextPaths(seedPath) } returns sequenceOf(nextPath1, nextPath2)

        every { dataComputer.computeNext(seedStep, seedContext, nextPath1) } returns "Foo"
        every { dataComputer.computeNext(seedStep, seedContext, nextPath2) } returns "Bar"

        queueNext.accept(seedStep, seedContext, queuer)

        assertThat(queuer.queued, hasSize(2))

        val nextStep1 = queuer.queued[0]
        assertThat(nextStep1.path, equalTo(nextPath1))
        assertThat(nextStep1.data, equalTo("Foo"))
        assertThat(nextStep1.numTerminalSteps, equalTo(4))
        assertThat(nextStep1.numEquipmentSteps, equalTo(2))

        val nextStep2 = queuer.queued[1]
        assertThat(nextStep2.path, equalTo(nextPath2))
        assertThat(nextStep2.data, equalTo("Bar"))
        assertThat(nextStep2.numTerminalSteps, equalTo(4))
        assertThat(nextStep2.numEquipmentSteps, equalTo(1))
    }

    @Test
    fun `calls branching queuer when queuing more than 1 path on branching queue next`() {
        val queueNext = NetworkTraceQueueNext.Branching(stateOperators, dataComputer)

        val seedPath = mockk<NetworkTraceStep.Path>()
        val seedStep = mockk<NetworkTraceStep<String>> {
            every { path } returns seedPath
            every { numEquipmentSteps } returns 1
            every { numTerminalSteps } returns 3
        }
        val seedContext = mockk<StepContext>()

        val nextPath1 = mockk<NetworkTraceStep.Path> { every { tracedExternally } returns true }
        val nextPath2 = mockk<NetworkTraceStep.Path> { every { tracedExternally } returns false }
        every { stateOperators.nextPaths(seedPath) } returns sequenceOf(nextPath1, nextPath2)

        every { dataComputer.computeNext(seedStep, seedContext, nextPath1) } returns "Foo"
        every { dataComputer.computeNext(seedStep, seedContext, nextPath2) } returns "Bar"

        queueNext.accept(seedStep, seedContext, queuer, branchingQueuer)

        assertThat(queuer.queued, hasSize(0))
        assertThat(branchingQueuer.queued, hasSize(2))

        val nextStep1 = branchingQueuer.queued[0]
        assertThat(nextStep1.path, equalTo(nextPath1))
        assertThat(nextStep1.data, equalTo("Foo"))
        assertThat(nextStep1.numTerminalSteps, equalTo(4))
        assertThat(nextStep1.numEquipmentSteps, equalTo(2))

        val nextStep2 = branchingQueuer.queued[1]
        assertThat(nextStep2.path, equalTo(nextPath2))
        assertThat(nextStep2.data, equalTo("Bar"))
        assertThat(nextStep2.numTerminalSteps, equalTo(4))
        assertThat(nextStep2.numEquipmentSteps, equalTo(1))
    }

    @Test
    fun `calls straight queuer when queuing a single path on branching queue next`() {
        val queueNext = NetworkTraceQueueNext.Branching(stateOperators, dataComputer)

        val seedPath = mockk<NetworkTraceStep.Path>()
        val seedStep = mockk<NetworkTraceStep<String>> {
            every { path } returns seedPath
            every { numEquipmentSteps } returns 1
            every { numTerminalSteps } returns 3
        }
        val seedContext = mockk<StepContext>()

        val nextPath1 = mockk<NetworkTraceStep.Path> { every { tracedExternally } returns true }
        every { stateOperators.nextPaths(seedPath) } returns sequenceOf(nextPath1)

        every { dataComputer.computeNext(seedStep, seedContext, nextPath1) } returns "Foo"

        queueNext.accept(seedStep, seedContext, queuer, branchingQueuer)

        assertThat(queuer.queued, hasSize(1))
        assertThat(branchingQueuer.queued, hasSize(0))

        val nextStep1 = queuer.queued[0]
        assertThat(nextStep1.path, equalTo(nextPath1))
        assertThat(nextStep1.data, equalTo("Foo"))
        assertThat(nextStep1.numTerminalSteps, equalTo(4))
        assertThat(nextStep1.numEquipmentSteps, equalTo(2))
    }

    private class TestQueuer<T> : (NetworkTraceStep<T>) -> Boolean {
        val queued = mutableListOf<NetworkTraceStep<T>>()
        override fun invoke(step: NetworkTraceStep<T>): Boolean {
            return queued.add(step)
        }
    }
}

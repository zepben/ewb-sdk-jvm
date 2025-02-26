/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.networktrace

import com.zepben.evolve.annotations.ZepbenExperimental
import com.zepben.evolve.services.network.tracing.networktrace.operators.NetworkStateOperators
import com.zepben.evolve.services.network.tracing.traversal.StepContext
import com.zepben.evolve.services.network.tracing.traversal.Traversal

internal abstract class NetworkTraceQueueNext(private val stateOperators: NetworkStateOperators) {

    internal class Basic<T> : NetworkTraceQueueNext, Traversal.QueueNext<NetworkTraceStep<T>> {

        private val getNextSteps: (item: NetworkTraceStep<T>, context: StepContext) -> Iterator<NetworkTraceStep<T>>

        constructor(stateOperators: NetworkStateOperators, computeData: ComputeData<T>) : super(stateOperators) {
            getNextSteps = { item, context -> nextTraceSteps(item, context, computeData).iterator() }
        }

        @ZepbenExperimental
        constructor(stateOperators: NetworkStateOperators, computeData: ComputeDataWithPaths<T>) : super(stateOperators) {
            getNextSteps = { item, context -> nextTraceSteps(item, context, computeData).iterator() }
        }

        override fun accept(item: NetworkTraceStep<T>, context: StepContext, queueItem: (NetworkTraceStep<T>) -> Boolean) {
            getNextSteps(item, context).forEach { queueItem(it) }
        }
    }

    internal class Branching<T> : NetworkTraceQueueNext, Traversal.BranchingQueueNext<NetworkTraceStep<T>> {

        private val getNextSteps: (item: NetworkTraceStep<T>, context: StepContext) -> List<NetworkTraceStep<T>>

        constructor(stateOperators: NetworkStateOperators, computeData: ComputeData<T>) : super(stateOperators) {
            getNextSteps = { item, context -> nextTraceSteps(item, context, computeData).toList() }
        }

        @ZepbenExperimental
        constructor(stateOperators: NetworkStateOperators, computeData: ComputeDataWithPaths<T>) : super(stateOperators) {
            getNextSteps = { item, context -> nextTraceSteps(item, context, computeData) }
        }

        override fun accept(
            item: NetworkTraceStep<T>,
            context: StepContext,
            queueItem: (NetworkTraceStep<T>) -> Boolean,
            queueBranch: (NetworkTraceStep<T>) -> Boolean
        ) {
            val nextSteps = getNextSteps(item, context)
            if (nextSteps.size == 1) queueItem(nextSteps[0]) else nextSteps.forEach { queueBranch(it) }
        }
    }

    protected fun <T> nextTraceSteps(
        currentStep: NetworkTraceStep<T>,
        currentContext: StepContext,
        computeData: ComputeData<T>,
    ): Sequence<NetworkTraceStep<T>> {
        val nextNumTerminalSteps = currentStep.nextNumTerminalSteps()
        return stateOperators.nextPaths(currentStep.path).map {
            val data = computeData.computeNext(currentStep, currentContext, it)
            NetworkTraceStep(it, nextNumTerminalSteps, it.nextNumEquipmentSteps(currentStep.numEquipmentSteps), data)
        }
    }

    @ZepbenExperimental
    protected fun <T> nextTraceSteps(
        currentStep: NetworkTraceStep<T>,
        currentContext: StepContext,
        computeNextT: ComputeDataWithPaths<T>,
    ): List<NetworkTraceStep<T>> {
        val nextNumTerminalSteps = currentStep.nextNumTerminalSteps()
        val nextPaths = stateOperators.nextPaths(currentStep.path).toList()
        return nextPaths.map {
            val data = computeNextT.computeNext(currentStep, currentContext, it, nextPaths)
            NetworkTraceStep(it, nextNumTerminalSteps, it.nextNumEquipmentSteps(currentStep.numEquipmentSteps), data)
        }
    }

    private fun NetworkTraceStep<*>.nextNumTerminalSteps() = numTerminalSteps + 1
    private fun NetworkTraceStep.Path.nextNumEquipmentSteps(currentNum: Int) = if (tracedExternally) currentNum + 1 else currentNum

}

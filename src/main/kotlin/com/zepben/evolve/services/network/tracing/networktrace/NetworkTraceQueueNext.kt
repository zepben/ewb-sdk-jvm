/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.networktrace

import com.zepben.evolve.annotations.ZepbenExperimental
import com.zepben.evolve.services.network.tracing.traversal.StepContext
import com.zepben.evolve.services.network.tracing.traversal.Traversal

internal abstract class NetworkTraceQueueNext(private val pathProvider: NetworkTraceStepPathProvider) {

    internal class Basic<T> : NetworkTraceQueueNext, Traversal.QueueNext<NetworkTraceStep<T>> {

        private val getNextSteps: (item: NetworkTraceStep<T>, context: StepContext) -> Iterator<NetworkTraceStep<T>>

        constructor(pathProvider: NetworkTraceStepPathProvider, computeData: ComputeData<T>) : super(pathProvider) {
            getNextSteps = { item, context -> nextTraceSteps(item, context, computeData).iterator() }
        }

        @ZepbenExperimental
        constructor(pathProvider: NetworkTraceStepPathProvider, computeData: ComputeDataWithPaths<T>) : super(pathProvider) {
            getNextSteps = { item, context -> nextTraceSteps(item, context, computeData).iterator() }
        }

        override fun accept(item: NetworkTraceStep<T>, context: StepContext, queueItem: (NetworkTraceStep<T>) -> Boolean) {
            getNextSteps(item, context).forEach { queueItem(it) }
        }
    }

    internal class Branching<T> : NetworkTraceQueueNext, Traversal.BranchingQueueNext<NetworkTraceStep<T>> {

        private val getNextSteps: (item: NetworkTraceStep<T>, context: StepContext) -> List<NetworkTraceStep<T>>

        constructor(pathProvider: NetworkTraceStepPathProvider, computeData: ComputeData<T>) : super(pathProvider) {
            getNextSteps = { item, context -> nextTraceSteps(item, context, computeData).toList() }
        }

        @ZepbenExperimental
        constructor(pathProvider: NetworkTraceStepPathProvider, computeData: ComputeDataWithPaths<T>) : super(pathProvider) {
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
        val nextNumEquipmentSteps = currentStep.nextNumEquipmentSteps()
        return pathProvider.nextPaths(currentStep.path).map {
            NetworkTraceStep(it, nextNumTerminalSteps, nextNumEquipmentSteps, computeData.computeNext(currentStep, currentContext, it))
        }
    }

    @ZepbenExperimental
    protected fun <T> nextTraceSteps(
        currentStep: NetworkTraceStep<T>,
        currentContext: StepContext,
        computeNextT: ComputeDataWithPaths<T>,
    ): List<NetworkTraceStep<T>> {
        val nextNumTerminalSteps = currentStep.nextNumTerminalSteps()
        val nextNumEquipmentSteps = currentStep.nextNumEquipmentSteps()
        val nextPaths = pathProvider.nextPaths(currentStep.path).toList()
        return nextPaths.map {
            NetworkTraceStep(it, nextNumTerminalSteps, nextNumEquipmentSteps, computeNextT.computeNext(currentStep, currentContext, it, nextPaths))
        }
    }

    private fun NetworkTraceStep<*>.nextNumTerminalSteps() = numTerminalSteps + 1
    private fun NetworkTraceStep<*>.nextNumEquipmentSteps() = if (path.tracedInternally) numEquipmentSteps + 1 else numEquipmentSteps

}

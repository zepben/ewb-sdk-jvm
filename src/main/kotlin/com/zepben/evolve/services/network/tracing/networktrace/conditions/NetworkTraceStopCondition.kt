/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.networktrace.conditions

import com.zepben.evolve.services.network.tracing.networktrace.NetworkTraceStep
import com.zepben.evolve.services.network.tracing.traversal.StepContext
import com.zepben.evolve.services.network.tracing.traversal.StopCondition

/**
 * A special stop condition implementation that allows only checking `shouldStop` when a [NetworkTraceStep] matches a given
 * [NetworkTraceStep.Type]. When [stepType] is:
 * *[NetworkTraceStep.Type.ALL]: [shouldStop] will be checked for every step.
 * *[NetworkTraceStep.Type.INTERNAL]: [shouldStop] will be checked only when [NetworkTraceStep.type] is [NetworkTraceStep.Type.INTERNAL].
 * *[NetworkTraceStep.Type.EXTERNAL]: [shouldStop] will be checked only when [NetworkTraceStep.type] is [NetworkTraceStep.Type.EXTERNAL].
 *
 * If the step does not match the given step type, `false` will always be returned.
 *
 * @property stepType The step type to match to check `shouldStop`.
 */
abstract class NetworkTraceStopCondition<T>(val stepType: NetworkTraceStep.Type) : StopCondition<NetworkTraceStep<T>> {

    private val shouldStopFunc = when (stepType) {
        NetworkTraceStep.Type.ALL -> ::shouldStopMatchedStep
        NetworkTraceStep.Type.INTERNAL -> ::shouldStopInternalStep
        NetworkTraceStep.Type.EXTERNAL -> ::shouldStopExternalStep
    }

    override fun shouldStop(item: NetworkTraceStep<T>, context: StepContext): Boolean =
        shouldStopFunc(item, context)

    /**
     * The logic you would normally put in [shouldStop]. However, this will only be called when a step matches the [stepType].
     */
    abstract fun shouldStopMatchedStep(
        item: NetworkTraceStep<T>,
        context: StepContext
    ): Boolean

    private fun shouldStopInternalStep(
        item: NetworkTraceStep<T>,
        context: StepContext
    ): Boolean =
        if (item.type == NetworkTraceStep.Type.INTERNAL) shouldStopMatchedStep(item, context) else false

    private fun shouldStopExternalStep(
        item: NetworkTraceStep<T>,
        context: StepContext
    ): Boolean =
        if (item.type == NetworkTraceStep.Type.EXTERNAL) shouldStopMatchedStep(item, context) else false

    companion object {
        /**
         * Creates A [NetworkTraceStopCondition] that delegates to the given [condition] when matching the given [stepType].
         * The [condition] is already a [NetworkTraceStopCondition] this will override the [stepType] of that instance.
         *
         * @param stepType The step type to match to check the queue condition.
         * @param condition The queue conditions to delegate to when matching the step type.
         * @return A new queue condition that only checks the condition when matching the given step type.
         */
        @JvmStatic
        fun <T> delegateTo(stepType: NetworkTraceStep.Type, condition: StopCondition<NetworkTraceStep<T>>): NetworkTraceStopCondition<T> =
            DelegatedNetworkTraceStopCondition(stepType, condition)
    }
}

private class DelegatedNetworkTraceStopCondition<T>(
    stepType: NetworkTraceStep.Type,
    val delegate: StopCondition<NetworkTraceStep<T>>
) : NetworkTraceStopCondition<T>(stepType) {
    override fun shouldStopMatchedStep(
        item: NetworkTraceStep<T>,
        context: StepContext
    ): Boolean = delegate.shouldStop(item, context)
}

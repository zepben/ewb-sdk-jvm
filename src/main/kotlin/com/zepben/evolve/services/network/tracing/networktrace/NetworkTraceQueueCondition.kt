/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.networktrace

import com.zepben.evolve.services.network.tracing.traversal.QueueCondition
import com.zepben.evolve.services.network.tracing.traversal.StepContext

abstract class NetworkTraceQueueCondition<T>(val stepType: NetworkTraceStep.Type) : QueueCondition<NetworkTraceStep<T>> {

    private val shouldQueueFunc = when (stepType) {
        NetworkTraceStep.Type.ALL -> ::shouldQueueMatchedStep
        NetworkTraceStep.Type.INTERNAL -> ::shouldQueueInternalStep
        NetworkTraceStep.Type.EXTERNAL -> ::shouldQueueExternalStep
    }

    override fun shouldQueue(nextItem: NetworkTraceStep<T>, nextContext: StepContext, currentItem: NetworkTraceStep<T>, currentContext: StepContext): Boolean =
        shouldQueueFunc(nextItem, nextContext, currentItem, currentContext)

    abstract fun shouldQueueMatchedStep(
        nextItem: NetworkTraceStep<T>,
        nextContext: StepContext,
        currentItem: NetworkTraceStep<T>,
        currentContext: StepContext
    ): Boolean

    private fun shouldQueueInternalStep(
        nextItem: NetworkTraceStep<T>,
        nextContext: StepContext,
        currentItem: NetworkTraceStep<T>,
        currentContext: StepContext
    ): Boolean =
        if (nextItem.path.tracedInternally) shouldQueueMatchedStep(nextItem, nextContext, currentItem, currentContext) else true

    private fun shouldQueueExternalStep(
        nextItem: NetworkTraceStep<T>,
        nextContext: StepContext,
        currentItem: NetworkTraceStep<T>,
        currentContext: StepContext
    ): Boolean =
        if (!nextItem.path.tracedInternally) shouldQueueMatchedStep(nextItem, nextContext, currentItem, currentContext) else true

    companion object {
        @JvmStatic
        fun <T> delegateTo(stepType: NetworkTraceStep.Type, condition: QueueCondition<NetworkTraceStep<T>>): NetworkTraceQueueCondition<T> =
            DelegatedNetworkTraceQueueCondition(stepType, condition)
    }
}

internal class DelegatedNetworkTraceQueueCondition<T>(
    stepType: NetworkTraceStep.Type,
    val delegate: QueueCondition<NetworkTraceStep<T>>
) : NetworkTraceQueueCondition<T>(stepType) {
    override fun shouldQueueMatchedStep(
        nextItem: NetworkTraceStep<T>,
        nextContext: StepContext,
        currentItem: NetworkTraceStep<T>,
        currentContext: StepContext
    ): Boolean = delegate.shouldQueue(nextItem, nextContext, currentItem, currentContext)

    override fun shouldQueueStartItem(item: NetworkTraceStep<T>): Boolean = delegate.shouldQueueStartItem(item)
}

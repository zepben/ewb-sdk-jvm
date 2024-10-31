/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.traversal

fun interface QueueCondition<T> : TraversalCondition<T> {
    // TODO [Review]: Does this need the current step and context?
    //  Use case would be when queuing item you may have stored state in the current T or context that impacts if you should queue the next item or not.
    fun shouldQueue(nextItem: T, nextContext: StepContext, currentItem: T, currentContext: StepContext): Boolean

    fun shouldQueueStartItem(item: T): Boolean = true
}

interface QueueConditionWithContextValue<T, U> : QueueCondition<T>, TypedContextValueComputer<T, U>

/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.traversal

/**
 * Functional interface representing a condition that determines whether a traversal should queue a next item.
 *
 * @param T The type of items being traversed.
 */
fun interface QueueCondition<T> : TraversalCondition<T> {
    /**
     * Determines whether the [nextItem] should be queued for traversal.
     *
     * @param nextItem The next item to be potentially queued.
     * @param nextContext The context associated with the [nextItem].
     * @param currentItem The current item being processed in the traversal.
     * @param currentContext The context associated with the [currentItem].
     * @return `true` if the [nextItem] should be queued; `false` otherwise.
     */
    fun shouldQueue(nextItem: T, nextContext: StepContext, currentItem: T, currentContext: StepContext): Boolean

    /**
     * Determines whether a traversal startItem should be queued when running a [Traversal].
     *
     * @param item The item to be potentially queued.
     * @return `true` if the [item] should be queued; `false` otherwise. Defaults to `true`.
     */
    fun shouldQueueStartItem(item: T): Boolean = true
}

/**
 * Interface representing a queue condition that requires a value stored in the [StepContext] to determine if an item should be queued.
 *
 * @param T The type of items being traversed.
 * @param U The type of the context value computed and used in the condition.
 */
interface QueueConditionWithContextValue<T, U> : QueueCondition<T>, TypedContextValueComputer<T, U>

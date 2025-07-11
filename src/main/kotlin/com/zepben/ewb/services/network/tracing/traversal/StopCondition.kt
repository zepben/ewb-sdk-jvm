/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.network.tracing.traversal

/**
 * Functional interface representing a condition that determines whether the traversal should stop at a given item.
 *
 * @param T The type of items being traversed.
 */
fun interface StopCondition<in T> : TraversalCondition<T> {
    /**
     * Determines whether the traversal should stop at the specified item.
     *
     * @param item The current item being processed in the traversal.
     * @param context The context associated with the current traversal step.
     * @return `true` if the traversal should stop at this item; `false` otherwise.
     */
    fun shouldStop(item: T, context: StepContext): Boolean
}

/**
 * Interface representing a stop condition that requires a value stored in the [StepContext] to determine if an item should be queued.
 *
 * @param T The type of items being traversed.
 * @param U The type of the context value computed and used in the condition.
 */
interface StopConditionWithContextValue<in T, U> : StopCondition<T>, TypedContextValueComputer<T, U>

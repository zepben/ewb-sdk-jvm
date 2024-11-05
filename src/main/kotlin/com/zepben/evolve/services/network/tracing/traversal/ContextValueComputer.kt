/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.traversal

/**
 * Interface representing a context value computer used to compute and store values in a [StepContext].
 * This interface does not specify a generic return type because the [StepContext] stores its values as `Any?`.
 * Implementations compute initial and subsequent context values during traversal steps.
 *
 * @param T The type of items being traversed.
 */
interface ContextValueComputer<T> {
    /**
     * A unique key identifying the context value computed by this computer.
     */
    val key: String

    /**
     * Computes the initial context value for the given starting item.
     *
     * @param item The starting item for which to compute the initial context value.
     * @return The initial context value associated with the starting item.
     */
    fun computeInitialValue(item: T): Any?

    /**
     * Computes the next context value based on the current item, next item, and the current context value.
     *
     * @param nextItem The next item in the traversal.
     * @param currentItem The current item of the traversal.
     * @param currentValue The current context value associated with the current item.
     * @return The updated context value for the next item.
     */
    fun computeNextValue(nextItem: T, currentItem: T, currentValue: Any?): Any?
}

/**
 * A typed version of [ContextValueComputer] that avoids unchecked casts by specifying the type of context value.
 * This interface allows for type-safe computation of context values in implementations.
 *
 * @param T The type of items being traversed.
 * @param U The type of the context value computed and stored.
 */
interface TypedContextValueComputer<T, U> : ContextValueComputer<T> {
    /**
     * Computes the initial context value of type [U] for the given starting item.
     *
     * @param item The starting item for which to compute the initial context value.
     * @return The initial context value associated with the starting item.
     */
    override fun computeInitialValue(item: T): U

    @Suppress("UNCHECKED_CAST")
    override fun computeNextValue(nextItem: T, currentItem: T, currentValue: Any?): Any? {
        return computeNextValueTyped(nextItem, currentItem, currentValue as U)
    }

    /**
     * Computes the next context value of type [U] based on the current item, next item, and the current context value.
     *
     * @param nextItem The next item in the traversal.
     * @param currentItem The current item being processed.
     * @param currentValue The current context value associated with the current item cast to type [U].
     * @return The updated context value of type for the next item.
     */
    fun computeNextValueTyped(nextItem: T, currentItem: T, currentValue: U): U

    /**
     * Gets the computed value from the context cast to type [U].
     */
    @Suppress("UNCHECKED_CAST")
    val StepContext.value: U get() = this.getValue<Any?>(key) as U
}

/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.network.tracing.traversal

/**
 * Functional interface representing an action to be performed at each step of a traversal.
 * This allows for custom operations to be executed on each item during traversal.
 *
 * @param T The type of items being traversed.
 */
fun interface StepAction<in T> {
    /**
     * Applies the action to the specified [item].
     *
     * @param item The current item in the traversal.
     * @param context The context associated with the current traversal step.
     */
    fun apply(item: T, context: StepContext)
}

/**
 * Interface representing a step action that utilises a value stored in the [StepContext].
 *
 * @param T The type of items being traversed.
 * @param U The type of the context value computed and used in the action.
 */
interface StepActionWithContextValue<T, U> : StepAction<T>, TypedContextValueComputer<T, U>

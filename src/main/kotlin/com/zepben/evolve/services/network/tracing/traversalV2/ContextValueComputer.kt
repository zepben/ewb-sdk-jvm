/*
 * Copyright 2023 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.traversalV2

// NOTE: This doesn't specific a Generic R here, because the [StepContext] stores its values as Any? and these computers are how we compute and place the step values
interface ContextValueComputer<T> {
    val key: String

    fun computeInitialValue(nextItem: T): Any?

    fun computeNextValue(nextItem: T, currentValue: Any?): Any?
}

/**
 * Using this allows you to create a ContextValueComputer without having to handle messy unchecked casts.
 */
interface TypedContextValueComputer<T, U> : ContextValueComputer<T> {
    override fun computeInitialValue(nextItem: T): U

    @Suppress("UNCHECKED_CAST")
    override fun computeNextValue(nextItem: T, currentValue: Any?): Any? {
        return computeNextValueTyped(nextItem, currentValue as U)
    }

    fun computeNextValueTyped(nextItem: T, currentValue: U): U
}

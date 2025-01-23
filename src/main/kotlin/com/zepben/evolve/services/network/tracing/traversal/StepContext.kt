/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.traversal

/**
 * Represents the context of a traversal step, holding information about the traversal state and the ability to store arbitrary values with the context.
 * This context is passed to conditions and actions during a traversal to provide additional information about each step.
 * Any [ContextValueComputer] registered with the traversal will put the computed value into this context with the given [ContextValueComputer.key] which can
 * be retrieved by using [getValue].
 *
 * @property isStartItem Indicates whether the current item is a starting item of the traversal.
 * @property isBranchStartItem Indicates whether the current item is the start of a new branch in a branching traversal.
 * @property stepNumber The number of steps taken in the traversal so far for this traversal path.
 * @property branchDepth The depth of the current branch in a branching traversal.
 * @property isStopping Indicates whether the traversal is stopping at the current item due to a stop condition.
 */
class StepContext(
    val isStartItem: Boolean,
    val isBranchStartItem: Boolean,
    val stepNumber: Int = 0,
    val branchDepth: Int,
    private var values: MutableMap<String, Any?>? = null
) {
    var isStopping: Boolean = false
        // This is internal because it needs to be set after all stop conditions are called, however stop conditions take a context.
        // It's a chicken and egg problem...
        internal set

    var isActionableItem: Boolean = false
        // Once again it's a chicken and egg problem as this because it needs to be set after calling canActionItem, however it takes a context.
        internal set

    /**
     * Sets a context value associated with the specified key.
     *
     * @param key The key identifying the context value.
     * @param value The value to associate with the key.
     */
    fun setValue(key: String, value: Any?) {
        values = values ?: mutableMapOf()
        values!![key] = value
    }

    /**
     * Retrieves a context value associated with the specified key.
     *
     * @param T The expected type of the context value.
     * @param key The key identifying the context value.
     * @return The context value associated with the key, or `null` if not found.
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> getValue(key: String): T? {
        return values?.get(key) as? T?
    }
}

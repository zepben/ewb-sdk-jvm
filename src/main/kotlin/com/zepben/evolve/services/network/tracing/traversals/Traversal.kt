/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network.tracing.traversals

import java.util.function.Consumer

/**
 *
 * Base class that provides some common functionality for traversals. This includes things like registering callbacks
 * to be called at every step in the traversal as well as registering stop conditions that traversals can check for when
 * to stop following a path.
 *
 * This base class does not actually provide any way to traverse the items. It needs to be implemented in
 * subclasses. See [BasicTraversal] for an example.
 *
 * Note this class is not thread safe!
 *
 * @param T Object type to be traversed.
 */
abstract class Traversal<T> {

    /**
     * The item the traversal will start at, or `null` if it has not been set.
     */
    var startItem: T? = null

    abstract val tracker: Tracker<T>

    @Volatile
    private var running = false

    @Volatile
    private var hasRun = false
    private val stopConditions = mutableListOf<(T) -> Boolean>()
    private val stepActions = mutableListOf<StepAction<T>>()

    /**
     *
     * Add a callback to check whether the current item in the traversal is a stop point.
     *
     * If any of the registered stop conditions return true, the traversal will not call the callback to queue more items.
     * Note that a match on a stop condition doesn't necessarily stop the traversal, it just stops
     * traversal of the current branch.
     *
     * @param condition A predicate that if returns true will cause the traversal to stop traversing the branch.
     * @return this traversal instance.
     */
    fun addStopCondition(condition: (T) -> Boolean): Traversal<T> {
        stopConditions.add(condition)
        return this
    }

    /**
     * Clears all of the stop conditions registered on this traversal.
     */
    fun clearStopConditions(): Traversal<T> {
        stopConditions.clear()
        return this
    }

    /**
     * Copies all the stop conditions from another traversal to this traversal
     *
     * @param other The other traversal object to copy from.
     * @return this traversal instance.
     */
    fun copyStopConditions(other: Traversal<T>): Traversal<T> {
        stopConditions.addAll(other.stopConditions)
        return this
    }

    /**
     * Checks all the stop conditions for the passed in item and returns true if any match.
     * This calls all registered stop conditions even if one has already returned true to make sure everything is
     * notified about this item.
     *
     * @param item The item to pass to the stop conditions.
     * @return true if any of the stop conditions return true.
     */
    fun matchesAnyStopCondition(item: T): Boolean = stopConditions.fold(false) { stop, condition -> stop or condition(item) }

    /**
     * Add a callback which is called for every item in the traversal (including the starting item).
     *
     * @param action Action to be called on each item in the traversal, passing if the trace will stop on this step.
     * @return this traversal instance.
     */
    fun addStepAction(action: StepAction<T>): Traversal<T> {
        stepActions.add(action)
        return this
    }

    /**
     * Add a callback which is called for every item in the traversal (including the starting item).
     *
     * @param action Action to be called on each item in the traversal, without passing if the trace will stop on this step.
     * @return this traversal instance.
     */
    fun addStepAction(action: Consumer<T>): Traversal<T> {
        stepActions.add { it, _ -> action.accept(it) }
        return this
    }

    /**
     * Add a callback which is called for every item in the traversal (including the starting item).
     *
     * @param action Action to be called on each item in the traversal, without passing if the trace will stop on this step.
     * @return this traversal instance.
     */
    fun addStepAction(action: (item: T) -> Unit): Traversal<T> {
        stepActions.add { it, _ -> action(it) }
        return this
    }

    /**
     * Add a callback which is called for every item in the traversal that does not match a stop condition (including the starting item).
     *
     * @param action Action to be called on each item in the traversal that is not being stopped on.
     * @return this traversal instance.
     */
    fun ifNotStopping(action: (item: T) -> Unit): Traversal<T> {
        stepActions.add { it, isStopping -> if (!isStopping) action(it) }
        return this
    }

    /**
     * Add a callback which is called for every item in the traversal that matches a stop condition (including the starting item).
     *
     * @param action Action to be called on each item in the traversal that is being stopped on.
     * @return this traversal instance.
     */
    fun ifStopping(action: (item: T) -> Unit): Traversal<T> {
        stepActions.add { it, isStopping -> if (isStopping) action(it) }
        return this
    }

    /**
     * Clears all step actions registered on this traversal.
     */
    fun clearStepActions(): Traversal<T> {
        stepActions.clear()
        return this
    }

    /**
     * Copies all the step actions from the passed in traversal to this traversal.
     *
     * @param other The other traversal object to copy from.
     * @return this traversal instance.
     */
    fun copyStepActions(other: Traversal<T>): Traversal<T> {
        stepActions.addAll(other.stepActions)
        return this
    }

    /**
     * Calls all the step actions with the passed in item.
     *
     * @param item       The item to pass to the step actions.
     * @param isStopping Indicates if the trace will stop on this step.
     */
    fun applyStepActions(item: T, isStopping: Boolean): Traversal<T> {
        stepActions.forEach { it.apply(item, isStopping) }
        return this
    }

    /**
     * Sets the item the traversal will start at.
     *
     * @param item The item to start at.
     * @return this traversal instance.
     */
    fun setStart(item: T): Traversal<T> {
        startItem = item
        return this
    }

    /**
     * Starts the traversal calling [setStart] on the [start] parameter before running.
     *
     * @param start              The item to start at.
     * @param canStopOnStartItem Indicates if the traversal will check the start item for stop conditions.
     */
    @JvmOverloads
    fun run(start: T, canStopOnStartItem: Boolean = true) {
        setStart(start)
        run(canStopOnStartItem)
    }

    /**
     * Starts the traversal. [setStart] should of been called to set the starting item or use the
     * overloaded run method that takes an item to start at.
     *
     * @param canStopOnStartItem Indicates if the traversal will check the start item for stop conditions.
     */
    @JvmOverloads
    fun run(canStopOnStartItem: Boolean = true) {
        check(!running) { "Traversal is already running." }
        check(!hasRun) { "Traversal must be reset before reuse." }

        running = true
        hasRun = true

        doRun(canStopOnStartItem)

        running = false
    }

    protected fun resetRunFlag() {
        check(!running) { "Traversal is currently running." }
        hasRun = false
    }

    abstract fun reset(): Traversal<T>
    protected abstract fun doRun(canStopOnStartItem: Boolean)

    fun interface StepAction<T> {

        fun apply(item: T, isStopping: Boolean)

    }

}

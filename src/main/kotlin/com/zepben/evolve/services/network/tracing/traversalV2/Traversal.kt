/*
 * Copyright 2023 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network.tracing.traversalV2

import com.zepben.evolve.services.network.tracing.traversals.Tracker
import com.zepben.evolve.services.network.tracing.traversals.TraversalQueue
import java.util.*
import kotlin.collections.ArrayDeque

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
abstract class Traversal<T, D : Traversal<T, D>> internal constructor(
    protected val queueNext: QueueNext<T>,
    protected val queueFactory: () -> TraversalQueue<T>,
    protected val branchQueueFactory: () -> TraversalQueue<D>,
    protected val trackerFactory: () -> Tracker<T>,
    protected val parent: D? = null
) {
    /**
     * Represents a consumer that takes the current item of the traversal, and the traversal instance so items can be queued.
     *
     * @param T The type of object being traversed.
     */
    fun interface QueueNext<T> {
        fun accept(item: T, context: StepContext, queueItem: (T) -> Boolean, queueBranch: (T) -> Boolean)
    }

    private val queue = queueFactory()
    private val branchQueue = branchQueueFactory()
    private val tracker: RecursiveTracker<T> = RecursiveTracker(parent?.tracker, trackerFactory())
    private val startItems: ArrayDeque<T> = ArrayDeque()

    @Volatile
    private var running = false

    @Volatile
    private var hasRun = false
    private val stopConditions = mutableListOf<StopCondition<T>>()
    private val queueConditions = mutableListOf<QueueCondition<T>>()
    private val stepActions = mutableListOf<StepAction<T>>()
    private val branchStartActions = mutableListOf<StepAction<T>>()

    private val computeNextContextFuns: MutableMap<String, ContextValueComputer<T>> = mutableMapOf()
    private val contexts: IdentityHashMap<T, StepContext> = IdentityHashMap()

    protected abstract fun getDerivedThis(): D
    protected abstract fun createNewThis(): D

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
    fun addStopCondition(condition: StopCondition<T>): D {
        stopConditions.add(condition)
        if (condition is StopConditionWithContextValue<T, *>) {
            computeNextContextFuns[condition.key] = condition
        }
        return getDerivedThis()
    }


    /**
     * Clears all of the stop conditions registered on this traversal.
     */
    fun clearStopConditions(): D {
        stopConditions.clear()
        computeNextContextFuns.entries.removeIf { it.value is StopCondition<*> }
        return getDerivedThis()
    }

    /**
     * Copies all the stop conditions from another traversal to this traversal
     *
     * @param other The other traversal object to copy from.
     * @return this traversal instance.
     */
    fun copyStopConditions(other: Traversal<T, D>): D {
        stopConditions.addAll(other.stopConditions)
        computeNextContextFuns.putAll(other.computeNextContextFuns.filter { it.value is StopCondition<*> })
        return getDerivedThis()
    }

    /**
     * Checks all the stop conditions for the passed in item and returns true if any match.
     * This calls all registered stop conditions even if one has already returned true to make sure everything is
     * notified about this item.
     *
     * @param item The item to pass to the stop conditions.
     * @return true if any of the stop conditions return true.
     */
    fun matchesAnyStopCondition(item: T, context: StepContext): Boolean =
        stopConditions.fold(false) { stop, condition -> stop or condition.shouldStop(item, context) }


    fun addQueueCondition(condition: QueueCondition<T>): D {
        queueConditions.add(condition)
        if (condition is QueueConditionWithContextValue<T, *>) {
            computeNextContextFuns[condition.key] = condition
        }
        return getDerivedThis()
    }

    fun clearQueueConditions(): D {
        queueConditions.clear()
        computeNextContextFuns.entries.removeIf { it is QueueCondition<*> }
        return getDerivedThis()
    }

    fun copyQueueConditions(other: Traversal<T, D>): D {
        queueConditions.addAll(other.queueConditions)
        computeNextContextFuns.putAll(other.computeNextContextFuns.filter { it is QueueCondition<*> })
        return getDerivedThis()
    }

    private fun matchesAllQueueConditions(item: T, context: StepContext): Boolean = queueConditions.all { it.shouldQueue(item, context) }


    /**
     * Add a callback which is called for every item in the traversal (including the starting item).
     *
     * @param action Action to be called on each item in the traversal, passing if the trace will stop on this step.
     * @return this traversal instance.
     */
    fun addStepAction(action: StepAction<T>): D {
        stepActions.add(action)
        if (action is StepActionWithContextValue<T, *>) {
            computeNextContextFuns[action.key] = action
        }
        return getDerivedThis()
    }

    /**
     * Add a callback which is called for every item in the traversal that does not match a stop condition (including the starting item).
     *
     * @param action Action to be called on each item in the traversal that is not being stopped on.
     * @return this traversal instance.
     */
    fun ifNotStopping(action: StepAction<T>): D {
        stepActions.add { it, context -> if (!context.isStopping) action.apply(it, context) }
        return getDerivedThis()
    }

    /**
     * Add a callback which is called for every item in the traversal that matches a stop condition (including the starting item).
     *
     * @param action Action to be called on each item in the traversal that is being stopped on.
     * @return this traversal instance.
     */
    fun ifStopping(action: StepAction<T>): D {
        stepActions.add { it, context -> if (context.isStopping) action.apply(it, context) }
        return getDerivedThis()
    }

    /**
     * Clears all step actions registered on this traversal.
     */
    fun clearStepActions(): D {
        stepActions.clear()
        computeNextContextFuns.entries.removeIf { it is StepAction<*> }
        return getDerivedThis()
    }

    /**
     * Copies all the step actions from the passed in traversal to this traversal.
     *
     * @param other The other traversal object to copy from.
     * @return this traversal instance.
     */
    fun copyStepActions(other: Traversal<T, D>): D {
        stepActions.addAll(other.stepActions)
        computeNextContextFuns.putAll(other.computeNextContextFuns.filter { it is StepAction<*> })
        return getDerivedThis()
    }

    /**
     * Calls all the step actions with the passed in item.
     *
     * @param item       The item to pass to the step actions.
     * @param isStopping Indicates if the trace will stop on this step.
     */
    private fun applyStepActions(item: T, context: StepContext): D {
        stepActions.forEach { it.apply(item, context) }
        return getDerivedThis()
    }

    fun addContextValueComputer(computer: ContextValueComputer<T>): D {
        computeNextContextFuns[computer.key] = computer
        return getDerivedThis()
    }

    fun clearContextValueComputers(): D {
        computeNextContextFuns.entries.removeIf { it.value.isStandaloneComputer() }
        return getDerivedThis()
    }

    fun copyContextValueComputers(other: Traversal<T, D>): D {
        computeNextContextFuns.putAll(other.computeNextContextFuns.filter { it.value.isStandaloneComputer() })
        return getDerivedThis()
    }

    fun addBranchStartAction(onBranchStart: StepAction<T>): D {
        branchStartActions.add(onBranchStart)
        return getDerivedThis()
    }

    fun clearBranchStartActions(): D {
        branchStartActions.clear()
        return getDerivedThis()
    }

    fun copyBranchStartActions(other: Traversal<T, D>): D {
        branchStartActions.addAll(other.branchStartActions)
        return getDerivedThis()
    }

    private fun applyBranchStartActions(item: T, context: StepContext): D {
        branchStartActions.forEach { it.apply(item, context) }
        return getDerivedThis()
    }

    private fun computeInitialContext(nextStep: T): StepContext {
        var newContextData: MutableMap<String, Any?>? = null

        for ((key, computer) in computeNextContextFuns) {
            newContextData = newContextData ?: mutableMapOf()
            newContextData[key] = computer.computeInitialValue(nextStep)
        }

        return StepContext(true, 0, newContextData)
    }


    private fun computeNextContext(context: StepContext, nextStep: T): StepContext {
        var newContextData: MutableMap<String, Any?>? = null

        for ((key, computer) in computeNextContextFuns) {
            newContextData = newContextData ?: mutableMapOf()
            newContextData[key] = computer.computeNextValue(nextStep, context.getValue(key))
        }

        return StepContext(false, context.stepNumber + 1, newContextData)
    }

    fun addStartItem(item: T): D {
        startItems.add(item)
        return getDerivedThis()
    }

    fun addStartItems(vararg items: T): D {
        startItems.addAll(items)
        return getDerivedThis()
    }

    fun addStartItems(items: Iterable<T>): D {
        startItems.addAll(items)
        return getDerivedThis()
    }

    fun run(startItem: T, canStopOnStartItem: Boolean = true) {
        startItems.add(startItem)
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

    fun reset(): D {
        check(!running) { "Traversal is currently running." }
        hasRun = false

        queue.clear()
        branchQueue.clear()
        tracker.clear()

        return getDerivedThis()
    }

    private fun doRun(canStopOnStartItem: Boolean) {
        while (startItems.isNotEmpty()) {
            val startItem = startItems.removeFirst()
            queue.add(startItem)
            var canStop = canStopOnStartItem

            // This is a getOrPut because if this is a branch traversal it will already have a context
            contexts.getOrPut(startItem) { computeInitialContext(startItem) }

            while (queue.hasNext()) {
                queue.next()?.let { current ->
                    if (tracker.visit(current)) {
                        val context = contexts[current] ?: error { "INTERNAL ERROR: Traversal item should always have a context" }
                        context.isStopping = canStop && matchesAnyStopCondition(current, context)

                        if (parent != null && current === startItem) {
                            applyBranchStartActions(current, context)
                        }

                        applyStepActions(current, context)

                        if (!context.isStopping) {
                            queueNext(current, context)
                        }

                        canStop = true
                    }
                }
            }
        }

        traverseBranches()
    }

    private fun queueNext(current: T, context: StepContext) {
        val queueNextItem = { nextItem: T ->
            if (canQueueItem(nextItem, context) && queue.add(nextItem)) {
                contexts[nextItem] = computeNextContext(context, nextItem)
                true
            } else {
                false
            }
        }

        val queueBranch = { nextItem: T ->
            if (canQueueItem(nextItem, context)) {
                val nextContext = computeNextContext(context, nextItem)
                val branch = createNewThis().also {
                    it.addStartItem(nextItem)
                    it.contexts[nextItem] = nextContext

                    it.copyQueueConditions(this)
                    it.copyStepActions(this)
                    it.copyStopConditions(this)
                    it.copyContextValueComputers(this)
                    it.copyBranchStartActions(this)
                }

                branchQueue.add(branch)
                true
            } else {
                false
            }
        }

        queueNext.accept(current, context, queueNextItem, queueBranch)
    }

    private fun traverseBranches() {
        while (branchQueue.hasNext()) {
            branchQueue.next()?.run()
        }
    }

    private fun canQueueItem(nextItem: T, currentContext: StepContext): Boolean {
        return matchesAllQueueConditions(nextItem, currentContext)
    }

    private fun ContextValueComputer<*>.isStandaloneComputer() =
        this !is StepAction<*> && this !is StopCondition<*> && this !is QueueCondition<*>
}

private class RecursiveTracker<T>(val parent: RecursiveTracker<T>?, val delegate: Tracker<T>) : Tracker<T> {
    override fun hasVisited(item: T): Boolean {
        return delegate.hasVisited(item) || run {
            var current = parent
            while (current != null) {
                if (current.hasVisited(item))
                    return true
                current = current.parent
            }
            return false
        }
    }

    override fun visit(item: T): Boolean {
        var parent = parent
        while (parent != null) {
            if (parent.hasVisited(item))
                return false
            parent = parent.parent
        }

        return delegate.visit(item)
    }

    override fun clear() {
        var parent = parent
        while (parent != null) {
            parent.clear()
            parent = parent.parent
        }

        return delegate.clear()
    }
}

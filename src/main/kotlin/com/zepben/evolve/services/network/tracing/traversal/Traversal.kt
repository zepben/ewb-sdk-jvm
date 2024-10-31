/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network.tracing.traversal

import java.util.*
import kotlin.collections.ArrayDeque

/**
 *
 * Base class for a Traversal. This provides most of the public interface and implementations for a traversal.

 * This cannot be a concrete class because it requires a final type for branching type traversals. It also
 * leaves how to add start items up to the derived class implementer as this can often be cleaner, or a
 * simpler interface if the [T] is a complex object. See [NetworkTrace] for an example.
 *
 * Note this class is not thread safe!
 *
 * @param T Object type to be traversed.
 */

abstract class Traversal<T, D : Traversal<T, D>> internal constructor(
    internal val queueType: QueueType<T, D>,
    trackerFactory: () -> Tracker<T>,
    protected val parent: D? = null
) {

    fun interface QueueNext<T> {
        fun accept(item: T, context: StepContext, queueItem: (T) -> Boolean)
    }

    fun interface BranchingQueueNext<T> {
        fun accept(item: T, context: StepContext, queueItem: (T) -> Boolean, queueBranch: (T) -> Boolean)
    }

    internal sealed interface QueueType<T, D : Traversal<T, D>> {
        val queue: TraversalQueue<T>
        val branchQueue: TraversalQueue<D>?
    }

    internal class BasicQueueType<T, D : Traversal<T, D>>(
        val queueNext: QueueNext<T>,
        override val queue: TraversalQueue<T>,
    ) : QueueType<T, D> {
        override val branchQueue: TraversalQueue<D>? = null
    }

    internal class BranchingQueueType<T, D : Traversal<T, D>>(
        val queueNext: BranchingQueueNext<T>,
        val queueFactory: () -> TraversalQueue<T>,
        val branchQueueFactory: (() -> TraversalQueue<D>),
    ) : QueueType<T, D> {
        override val queue: TraversalQueue<T> get() = queueFactory()
        override val branchQueue: TraversalQueue<D> get() = branchQueueFactory()
    }

    private val queueNext: (T, StepContext) -> Unit = when (queueType) {
        is BasicQueueType -> { current, context -> queueNextNonBranching(current, context, queueType.queueNext) }
        is BranchingQueueType -> { current, context -> queueNextBranching(current, context, queueType.queueNext) }
    }

    private val queue: TraversalQueue<T> = queueType.queue
    private val branchQueue: TraversalQueue<D>? = queueType.branchQueue
    private val tracker: RecursiveTracker<T> = RecursiveTracker(parent?.tracker, trackerFactory())
    private val startItems: ArrayDeque<T> = ArrayDeque()

    @Volatile
    private var running = false

    @Volatile
    private var hasRun = false
    private val stopConditions = mutableListOf<StopCondition<T>>()
    private val queueConditions = mutableListOf<QueueCondition<T>>()
    private val stepActions = mutableListOf<StepAction<T>>()

    private val computeNextContextFuns: MutableMap<String, ContextValueComputer<T>> = mutableMapOf()
    private val contexts: IdentityHashMap<T, StepContext> = IdentityHashMap()

    protected open fun canActionItem(item: T, context: StepContext): Boolean = true
    protected abstract fun getDerivedThis(): D
    protected abstract fun createNewThis(): D

    fun addCondition(condition: TraversalCondition<T>): D {
        when (condition) {
            is QueueCondition -> addQueueCondition(condition)
            is StopCondition -> addStopCondition(condition)
        }
        return getDerivedThis()
    }

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

    private fun computeInitialContext(nextStep: T): StepContext {
        var newContextData: MutableMap<String, Any?>? = null

        for ((key, computer) in computeNextContextFuns) {
            newContextData = newContextData ?: mutableMapOf()
            newContextData[key] = computer.computeInitialValue(nextStep)
        }

        return StepContext(true, false, 0, 0, newContextData)
    }


    private fun computeNextContext(currentItem: T, context: StepContext, nextStep: T, isBranchStart: Boolean): StepContext {
        var newContextData: MutableMap<String, Any?>? = null

        for ((key, computer) in computeNextContextFuns) {
            newContextData = newContextData ?: mutableMapOf()
            newContextData[key] = computer.computeNextValue(nextStep, currentItem, context.getValue(key))
        }

        val branchDepth = if (isBranchStart) context.branchDepth + 1 else context.branchDepth
        return StepContext(false, isBranchStart, context.stepNumber + 1, branchDepth, newContextData)
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

    fun startItems(): Collection<T> = startItems

    protected fun run(startItem: T, canStopOnStartItem: Boolean = true) {
        startItems.add(startItem)
        run(canStopOnStartItem)
    }

    /**
     * Starts the traversal processing items added via [addStartItem] or [addStartItems].
     *
     * @param canStopOnStartItem Indicates if the traversal will check the start item for stop conditions.
     */
    @JvmOverloads
    fun run(canStopOnStartItem: Boolean = true) {
        check(!running) { "Traversal is already running." }
        check(!hasRun) { "Traversal must be reset before reuse." }

        running = true
        hasRun = true

        if (parent == null && queueType is BranchingQueueType && startItems.size > 1) {
            branchStartItems()
        } else {
            traverse(canStopOnStartItem)
        }

        traverseBranches(canStopOnStartItem)

        running = false
    }

    fun reset(): D {
        check(!running) { "Traversal is currently running." }
        hasRun = false

        queue.clear()
        branchQueue?.clear()
        tracker.clear()

        onReset()

        return getDerivedThis()
    }

    protected abstract fun onReset()

    private fun branchStartItems() {
        while (startItems.isNotEmpty()) {
            val startItem = startItems.removeFirst()
            if (canQueueStartItem(startItem)) {
                val branch = createNewBranch(startItem, computeInitialContext(startItem))
                branchQueue?.add(branch) ?: throw IllegalStateException("INTERNAL ERROR: branchQueue should never be null here")
            }
        }
    }

    private fun traverse(canStopOnStartItem: Boolean) {
        while (startItems.isNotEmpty()) {
            val startItem = startItems.removeFirst()

            // If the traversal is not a branch we need to compute an initial context and check if it
            // should even be queued to trace. If the traversal is a branch, the branch creators should
            // have only created the branch if the item was eligible to be queued and added the item
            // context as part of the branch creation.
            if (parent == null) {
                if (canQueueStartItem(startItem)) {
                    contexts[startItem] = computeInitialContext(startItem)
                    queue.add(startItem)
                }
            } else {
                queue.add(startItem)
            }

            var canStop = canStopOnStartItem
            while (queue.hasNext()) {
                queue.next()?.let { current ->
                    if (tracker.visit(current)) {
                        val context = getStepContext(current)
                        val canAction = canActionItem(current, context)

                        if (canAction) {
                            context.isStopping = canStop && matchesAnyStopCondition(current, context)
                        }

                        if (canAction) {
                            applyStepActions(current, context)
                        }

                        if (!context.isStopping) {
                            queueNext(current, context)
                        }

                        canStop = true
                    }
                }
            }
        }
    }

    private fun getStepContext(item: T): StepContext = contexts[item] ?: error { "INTERNAL ERROR: Traversal item should always have a context" }

    private fun createNewBranch(startItem: T, context: StepContext): D {
        return createNewThis().also {
            it.copyQueueConditions(this)
            it.copyStepActions(this)
            it.copyStopConditions(this)
            it.copyContextValueComputers(this)

            it.contexts[startItem] = context
            it.addStartItem(startItem)
        }
    }

    private fun itemQueuer(currentItem: T, currentContext: StepContext): (T) -> Boolean = { nextItem ->
        val nextContext = computeNextContext(currentItem, currentContext, nextItem, false)
        if (canQueueItem(nextItem, nextContext, currentItem, currentContext) && queue.add(nextItem)) {
            contexts[nextItem] = nextContext
            true
        } else {
            false
        }
    }

    private fun queueNextNonBranching(current: T, currentContext: StepContext, queueNext: QueueNext<T>) {
        queueNext.accept(current, currentContext, itemQueuer(current, currentContext))
    }

    private fun queueNextBranching(current: T, currentContext: StepContext, queueNext: BranchingQueueNext<T>) {
        val queueBranch = { nextItem: T ->
            val nextContext = computeNextContext(current, currentContext, nextItem, true)
            if (canQueueItem(nextItem, nextContext, current, currentContext)) {
                val branch = createNewBranch(nextItem, nextContext)
                branchQueue?.add(branch) ?: throw IllegalStateException("INTERNAL ERROR: branchQueue should never be null here")
            } else {
                false
            }
        }

        queueNext.accept(current, currentContext, itemQueuer(current, currentContext), queueBranch)
    }

    private fun traverseBranches(canStopOnStartItem: Boolean) {
        branchQueue ?: return
        while (branchQueue.hasNext()) {
            branchQueue.next()?.run(canStopOnStartItem)
        }
    }

    private fun canQueueItem(nextItem: T, nextContext: StepContext, currentItem: T, currentContext: StepContext): Boolean {
        return queueConditions.all { it.shouldQueue(nextItem, nextContext, currentItem, currentContext) }
    }

    private fun canQueueStartItem(startItem: T): Boolean {
        return queueConditions.all { it.shouldQueueStartItem(startItem) }
    }

    private fun ContextValueComputer<*>.isStandaloneComputer() =
        this !is StepAction<*> && this !is StopCondition<*> && this !is QueueCondition<*>
}

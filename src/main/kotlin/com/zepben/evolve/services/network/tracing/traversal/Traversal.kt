/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network.tracing.traversal

import java.util.*
import kotlin.collections.ArrayDeque

/**
 * A base traversal class allowing items in a connected graph to be traced.
 * It provides the main interface and implementation for traversal logic.
 * This class manages conditions, actions, and context values that guide each traversal step.
 *
 * This class supports a concept of 'branching', whereby when a new branch is created a new child traversal instance is created. The child
 * inherits its parents conditions, actions and what it has tracked. However, it knows nothing about what its siblings have tracked. This
 * allows traversing both ways around loops in the graph.
 *
 * This class is abstract to allow for type-specific implementations for branching traversals and custom start item handling.
 *
 * This class is **not thread safe**.
 *
 * @param T The type of object to be traversed.
 * @param D The specific type of traversal, extending [Traversal].
 */
abstract class Traversal<T, D : Traversal<T, D>> internal constructor(
    internal val queueType: QueueType<T, D>,
    protected val parent: D? = null
) {

    /**
     * Functional interface for queuing items in a non-branching traversal.
     */
    internal fun interface QueueNext<T> {
        fun accept(item: T, context: StepContext, queueItem: (T) -> Boolean)
    }

    /**
     * Functional interface for queuing items in a branching traversal.
     */
    internal fun interface BranchingQueueNext<T> {
        fun accept(item: T, context: StepContext, queueItem: (T) -> Boolean, queueBranch: (T) -> Boolean)
    }

    /**
     * Defines the types of queues used in the traversal.
     */
    internal sealed interface QueueType<T, D : Traversal<T, D>> {
        val queue: TraversalQueue<T>
        val branchQueue: TraversalQueue<D>?
    }

    /**
     * Basic queue type that handles non-branching item queuing.
     *
     * @property queueNext Logic for queueing the next item in the traversal.
     * @property queue The primary queue of items.
     */
    internal class BasicQueueType<T, D : Traversal<T, D>>(
        val queueNext: QueueNext<T>,
        override val queue: TraversalQueue<T>,
    ) : QueueType<T, D> {
        override val branchQueue: TraversalQueue<D>? = null
    }

    /**
     * Branching queue type, supporting operations that may split into separate branches during traversal.
     *
     * @property queueNext Logic for queueing the next item in a branching traversal.
     * @property queueFactory Factory function to create the main queue.
     * @property branchQueueFactory Factory function to create the branch queue.
     */
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

    /**
     * Determines if the traversal can apply step actions and stop conditions on the specified item.
     *
     * @param item The item to check.
     * @param context The context of the current traversal step.
     * @return `true` if the item can be acted upon; `false` otherwise.
     */
    protected open fun canActionItem(item: T, context: StepContext): Boolean = true

    protected abstract fun canVisitItem(item: T, context: StepContext): Boolean

    /**
     * Retrieves the derived instance of this traversal class.
     *
     * @return The derived traversal instance.
     */
    protected abstract fun getDerivedThis(): D

    /**
     * Creates a new instance of the traversal for branching purposes.
     *
     * @return A new traversal instance.
     */
    protected abstract fun createNewThis(): D

    /**
     * Adds a traversal condition to the traversal.
     *
     * @param condition The condition to add.
     * @return this traversal instance.
     */
    fun addCondition(condition: TraversalCondition<T>): D {
        when (condition) {
            is QueueCondition -> addQueueCondition(condition)
            is StopCondition -> addStopCondition(condition)
        }
        return getDerivedThis()
    }

    /**
     * Adds a stop condition to the traversal. If any stop condition returns `true`, the traversal
     * will not call the callback to queue more items from the current item.
     *
     * @param condition The stop condition to add.
     * @return this traversal instance.
     */
    open fun addStopCondition(condition: StopCondition<T>): D {
        stopConditions.add(condition)
        if (condition is StopConditionWithContextValue<T, *>) {
            computeNextContextFuns[condition.key] = condition
        }
        return getDerivedThis()
    }

    /**
     * Copies all the stop conditions from another traversal to this traversal.
     *
     * @param other The other traversal object to copy from.
     * @return The current traversal instance.
     */
    fun copyStopConditions(other: Traversal<T, D>): D {
        other.stopConditions.forEach { addStopCondition(it) }
        return getDerivedThis()
    }

    private fun matchesAnyStopCondition(item: T, context: StepContext): Boolean =
        stopConditions.fold(false) { stop, condition -> stop or condition.shouldStop(item, context) }


    /**
     * Adds a queue condition to the traversal. Queue conditions determine whether an item should be queued for traversal.
     * All registered queue conditions must return true for an item to be queued.
     *
     * @param condition The queue condition to add.
     * @return The current traversal instance.
     */
    open fun addQueueCondition(condition: QueueCondition<T>): D {
        queueConditions.add(condition)
        if (condition is QueueConditionWithContextValue<T, *>) {
            computeNextContextFuns[condition.key] = condition
        }
        return getDerivedThis()
    }

    /**
     * Copies all queue conditions from another traversal to this traversal.
     *
     * @param other The other traversal from which to copy queue conditions.
     * @return The current traversal instance.
     */
    fun copyQueueConditions(other: Traversal<T, D>): D {
        other.queueConditions.forEach { addQueueCondition(it) }
        return getDerivedThis()
    }

    /**
     * Adds an action to be performed on each item in the traversal, including the starting items.
     *
     * @param action The action to perform on each item.
     * @return The current traversal instance.
     */
    fun addStepAction(action: StepAction<T>): D {
        stepActions.add(action)
        if (action is StepActionWithContextValue<T, *>) {
            computeNextContextFuns[action.key] = action
        }
        return getDerivedThis()
    }

    /**
     * Adds an action to be performed on each item that does not match any stop condition.
     *
     * @param action The action to perform on each non-stopping item.
     * @return The current traversal instance.
     */
    fun ifNotStopping(action: StepAction<T>): D {
        stepActions.add { it, context -> if (!context.isStopping) action.apply(it, context) }
        return getDerivedThis()
    }

    /**
     * Adds an action to be performed on each item that matches a stop condition.
     *
     * @param action The action to perform on each stopping item.
     * @return The current traversal instance.
     */
    fun ifStopping(action: StepAction<T>): D {
        stepActions.add { it, context -> if (context.isStopping) action.apply(it, context) }
        return getDerivedThis()
    }

    /**
     * Copies all the step actions from the passed in traversal to this traversal.
     *
     * @param other The other traversal object to copy from.
     * @return The current traversal instance.
     */
    fun copyStepActions(other: Traversal<T, D>): D {
        other.stepActions.forEach { addStepAction(it) }
        return getDerivedThis()
    }

    private fun applyStepActions(item: T, context: StepContext): D {
        stepActions.forEach { it.apply(item, context) }
        return getDerivedThis()
    }

    /**
     * Adds a standalone context value computer to compute additional [StepContext] values during traversal.
     *
     * @param computer The context value computer to add.
     * @return The current traversal instance.
     */
    fun addContextValueComputer(computer: ContextValueComputer<T>): D {
        require(computer !is TraversalCondition<*>) { "`computer` must not be a TraversalCondition. Use `addCondition` to add conditions that also compute context values" }
        computeNextContextFuns[computer.key] = computer
        return getDerivedThis()
    }

    /**
     * Copies all standalone context value computers from another traversal to this traversal.
     * That is, it does not copy any [TraversalCondition] registered that also implements [ContextValueComputer]
     *
     * @param other The other traversal from which to copy context value computers.
     * @return The current traversal instance.
     */
    fun copyContextValueComputers(other: Traversal<T, D>): D {
        other.computeNextContextFuns.values.filter { it.isStandaloneComputer() }.forEach { addContextValueComputer(it) }
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

    /**
     * Adds a starting item to the traversal.
     *
     * @param item The item to add.
     * @return The current traversal instance.
     */
    protected open fun addStartItem(item: T): D {
        startItems.add(item)
        return getDerivedThis()
    }

    /**
     * Retrieves a read only collection of starting items for the traversal.
     *
     * @return A collection of starting items.
     */
    fun startItems(): Collection<T> = startItems

    /**
     * Runs the traversal adding [startItem] to the collection of start items.
     *
     * @param startItem The item from which to start the traversal.
     * @param canStopOnStartItem Indicates if the traversal should check stop conditions on the starting item.
     * @return The current traversal instance.
     */
    protected open fun run(startItem: T, canStopOnStartItem: Boolean = true): D {
        startItems.add(startItem)
        run(canStopOnStartItem)
        return getDerivedThis()
    }

    /**
     * Starts the traversal processing items added via [addStartItem].
     * This wil call [reset] if the traversal has previously been run.
     *
     * @param canStopOnStartItem Indicates if the traversal should check stop conditions on the starting item.
     */
    @JvmOverloads
    fun run(canStopOnStartItem: Boolean = true) {
        check(!running) { "Traversal is already running." }

        if (hasRun) {
            reset()
        }

        running = true
        hasRun = true

        if (parent == null && queueType is BranchingQueueType && startItems.size > 1) {
            branchStartItems()
            // Because we don't traverse anything at the top level parent, we need to pass canStopAtStart item
            // to the child branch only in this case because they are actually start items.
            traverseBranches(canStopOnStartItem)
        } else {
            traverse(canStopOnStartItem)
            // Child branches should never stop at start items because a branch start item is not a whole trace start item.
            traverseBranches(true)
        }

        running = false
    }

    /**
     * Resets the traversal to allow it to be reused.
     *
     * @return The current traversal instance.
     */
    fun reset(): D {
        check(!running) { "Traversal is currently running." }
        hasRun = false

        queue.clear()
        branchQueue?.clear()

        onReset()

        return getDerivedThis()
    }

    /**
     * Called when the traversal is reset. Derived classes can override this to reset additional state.
     */
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
                    val context = getStepContext(current)
                    if (canVisitItem(current, context)) {
                        context.isStopping = canStop && matchesAnyStopCondition(current, context)

                        context.isActionableItem = canActionItem(current, context)
                        if (context.isActionableItem) {
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

    private fun getStepContext(item: T): StepContext = contexts.remove(item) ?: error { "INTERNAL ERROR: Traversal item should always have a context" }

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

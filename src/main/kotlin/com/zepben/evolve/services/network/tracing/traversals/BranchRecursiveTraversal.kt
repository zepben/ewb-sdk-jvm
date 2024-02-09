/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network.tracing.traversals

import java.util.function.Consumer
import java.util.function.Supplier

/**
 * A traversal class that allows you to branch by spawning another traversal instance.
 * I've tried to make this a reusable class, but not sure how useful it is in its current state.
 * It's working for my current use case (set phase tracing) but may need to be rethought to meet requirements of
 * other traversals.
 * Be seriously careful if you ever plan to subclass this class. It would be extremely easy to break it's intended
 * use. Should possibly even be final...
 *
 * @param parent               The traversal that spawned this instance
 * @param queueNext            Function that supplies the next available items to step to from the current item in the traversal.
 * @param processQueueSupplier A supplier of the item queue to be used by the traversal.
 * @param trackerSupplier      A supplier of the item tracker to be used by this traversal.
 * @param branchQueueSupplier  A supplier of the branch queue to be used by this traversal.
 */
@Suppress("MemberVisibilityCanBePrivate")
class BranchRecursiveTraversal<T>(
    private val queueNext: QueueNext<T>,
    private val processQueueSupplier: () -> TraversalQueue<T>,
    private val trackerSupplier: () -> Tracker<T>,
    private val branchQueueSupplier: () -> TraversalQueue<Traversal<T>>,
    val parent: BranchRecursiveTraversal<T>? = null,
    private val onBranchStart: ((T) -> Unit)? = null
) : Traversal<T>() {

    val queue: TraversalQueue<T> = processQueueSupplier()
    val branchQueue: TraversalQueue<Traversal<T>> = branchQueueSupplier()
    override val tracker: Tracker<T> = trackerSupplier()

    var branchSupplier = defaultBranchSupplier()

    /**
     * Java interop constructor.
     */
    @JvmOverloads
    constructor(
        queueNext: QueueNext<T>,
        processQueueSupplier: Supplier<TraversalQueue<T>>,
        trackerSupplier: Supplier<Tracker<T>>,
        branchQueueSupplier: Supplier<TraversalQueue<Traversal<T>>>,
        parent: BranchRecursiveTraversal<T>? = null,
        onBranchStart: Consumer<T>? = null
    ) : this(queueNext, processQueueSupplier::get, trackerSupplier::get, branchQueueSupplier::get, parent, { onBranchStart?.accept(it) })

    /**
     * Represents a consumer that takes the current item of the traversal,
     * a function to add items to the traversal queue,
     * a function to add a branch to recurse on,
     * and a supplier that can generate a new Traversal branch.
     *
     * @param T The type of object being traversed.
     */
    fun interface QueueNext<T> {
        fun accept(item: T, traversal: BranchRecursiveTraversal<T>)
    }

    /**
     * @return Whether the item has been visited before.
     */
    fun hasVisited(item: T): Boolean {
        var parent = parent
        while (parent != null) {
            if (parent.tracker.hasVisited(item)) return true
            parent = parent.parent
        }

        return tracker.hasVisited(item)
    }

    /**
     *
     * The default branch supplier to provide the ability to create traversal branches.
     *
     * This supplier creates a new [BranchRecursiveTraversal] using the suppliers provided to this instance when
     * it was created and setting this as the parent.
     *
     * It then copies all step actions and stop conditions into the new instance.
     * where its creator has already been.
     *
     * @return The default branch supplier used by this class.
     */
    fun defaultBranchSupplier(): () -> Traversal<T> = {
        BranchRecursiveTraversal(
            queueNext,
            processQueueSupplier,
            trackerSupplier,
            branchQueueSupplier,
            this,
            onBranchStart
        )
            .copyStepActions(this)
            .copyStopConditions(this)
    }

    override fun reset(): BranchRecursiveTraversal<T> {
        resetRunFlag()

        queue.clear()
        branchQueue.clear()
        tracker.clear()

        return this
    }

    /**
     * Runs the traversal. All items in the queue will be processed before moving onto processing the branch
     * queue. The results from each of the branch traversals are added to the result of this traversal to give all
     * items that were traversed through all branches.
     */
    override fun doRun(canStopOnStartItem: Boolean) {
        var canStop = true

        startItem?.let {
            queue.add(it)
            canStop = canStopOnStartItem
        }

        while (queue.hasNext()) {
            queue.next()?.let {
                if (visit(it)) {
                    val isStopping = canStop && matchesAnyStopCondition(it)

                    applyStepActions(it, isStopping)

                    if (!isStopping)
                        queueNext.accept(it, this)

                    canStop = true
                }
            }
        }

        traverseBranches()
    }

    private fun visit(item: T): Boolean {
        var parent = parent
        while (parent != null) {
            if (parent.tracker.hasVisited(item))
                return false
            parent = parent.parent
        }

        return tracker.visit(item)
    }

    private fun traverseBranches() {
        while (branchQueue.hasNext()) {
            branchQueue.next()?.let { traversal ->
                traversal.startItem?.let {
                    onBranchStart?.invoke(it)
                    traversal.run()
                }
            }
        }
    }

}

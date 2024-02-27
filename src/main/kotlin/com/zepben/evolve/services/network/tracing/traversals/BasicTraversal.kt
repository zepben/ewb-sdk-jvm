/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network.tracing.traversals

/**
 *
 * A basic traversal implementation that can be used to traverse any type of item.
 *
 * The traversal gets the next items to be traversed to by calling a user provided functional interface
 * [QueueNext], that passes the current item of the traversal, and the
 * traversal instance itself where the process queue can have items added to it. This is registered during construction.
 *
 * The process queue, an instance of [TraversalQueue] is also supplied during construction. This gives the
 * flexibility for this trace to be backed by any type of queue: breadth, depth, priority etc.
 *
 * The traversal also requires a [Tracker] to be supplied on construction. This gives flexibility to track
 * items in unique ways, more than just "has this item been visited" e.g. visiting more than once,
 * visiting under different conditions etc.
 *
 * @param queueNext The consumer that will be called at each step of the traversal to queue "adjacent" items.*
 * @param queue     The item queue to be used by this traversal.
 * @param tracker   The tracker that tracks items during the traversal.
 */
open class BasicTraversal<T>(
    private val queueNext: QueueNext<T>,
    val queue: TraversalQueue<T>,
    override val tracker: Tracker<T>
) : Traversal<T>() {

    /**
     * Represents a consumer that takes the current item of the traversal, and the traversal instance so items can be queued.
     *
     * @param T The type of object being traversed.
     */
    fun interface QueueNext<T> {
        fun accept(item: T, traversal: BasicTraversal<T>)
    }

    override fun reset(): BasicTraversal<T> {
        resetRunFlag()

        queue.clear()
        tracker.clear()

        return this
    }

    override fun doRun(canStopOnStartItem: Boolean) {
        var canStop = true

        startItem?.let {
            queue.add(it)
            canStop = canStopOnStartItem
        }

        while (queue.hasNext()) {
            queue.next()?.let { current ->
                if (tracker.visit(current)) {
                    val isStopping = canStop && matchesAnyStopCondition(current)

                    applyStepActions(current, isStopping)

                    if (!isStopping)
                        queueNext.accept(current, this)

                    canStop = true
                }
            }
        }
    }

}

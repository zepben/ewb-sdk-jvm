/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.traversal

import java.util.*

/**
 * A simple queue interface used for traversals.
 */
interface TraversalQueue<T> {

    /**
     * Check if the queue has more items.
     *
     * @return true if the queue has more items.
     */
    operator fun hasNext(): Boolean

    /**
     * Removes and returns the next item in the queue. Most implementations of this interface will return
     * null is this is called when the queue is empty, however it is not enforced.
     *
     * @return The next item in the queue.
     */
    operator fun next(): T?

    /**
     * Adds an item to the queue.
     * The boolean return is there for queues that may reject an item being added.
     *
     * @param item The item to be added to the queue.
     * @return true if the item was added, false if it could not be.
     */
    fun add(item: T): Boolean

    /**
     * Adds the items to the queue.
     *
     * @param items A collection of items to add to the queue.
     * @return true if the queue was changed as the result of the operation.
     */
    fun addAll(items: Collection<T>): Boolean =
        items.fold(false) { result, item -> add(item) || result }

    /**
     * Adds the items to the queue.
     *
     * @param items The items to be added to the queue.
     * @return true if the queue was changed as the result of the operation.
     */
    fun addAll(vararg items: T): Boolean =
        addAll(items.asList())

    /**
     * Look at the item at the front of the queue without removing it.  Most implementations of this interface will return
     * null is this is called when the queue is empty, however it is not enforced.
     */
    fun peek(): T?

    /**
     * Clears the queue.
     */
    fun clear()

    companion object {

        /**
         * Creates a new instance backed by a breadth first (FIFO) queue.
         *
         * @param T Type to be held by the queue.
         * @return The new instance.
         */
        @JvmStatic
        fun <T> breadthFirst(): TraversalQueue<T> {
            return BasicQueue(ArrayDeque())
        }

        /**
         * Creates a new instance backed by a depth first (LIFO) queue.
         *
         * @param T Type to be held by the queue.
         * @return The new instance.
         */
        @JvmStatic
        fun <T> depthFirst(): TraversalQueue<T> {
            return BasicQueue(Collections.asLifoQueue(ArrayDeque()))
        }

    }
}

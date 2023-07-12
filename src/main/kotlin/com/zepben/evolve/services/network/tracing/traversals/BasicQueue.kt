/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network.tracing.traversals

import com.zepben.annotations.EverythingIsNonnullByDefault
import java.util.*
import java.util.function.Supplier

/**
 * A simple queue implementation for use with traversals.
 */
@EverythingIsNonnullByDefault
open class BasicQueue<T> protected constructor(
    private val queue: Queue<T>
) : TraversalQueue<T> {

    override fun hasNext(): Boolean {
        return queue.peek() != null
    }

    override fun next(): T? {
        return queue.poll()
    }

    override fun add(item: T): Boolean {
        return queue.add(item)
    }

    override fun addAll(items: Collection<T>): Boolean {
        return queue.addAll(items)
    }

    override fun peek(): T? {
        return queue.peek()
    }

    override fun clear() {
        queue.clear()
    }

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
         * Returns a supplier that creates new instances of a breadth first queue.
         * This exists because mainly because a method reference where a supplier was needed was causing type inference
         * related errors in some circumstances.
         *
         * @param T Type to be held by the queue.
         * @return The new instance.
         */
        @JvmStatic
        fun <T> breadthFirstSupplier(): Supplier<TraversalQueue<T>> {
            return Supplier { breadthFirst() }
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

        /**
         * Returns a supplier that creates new instances of a depth first queue.
         * This exists because mainly because a method reference where a supplier was needed was causing type inference
         * related errors in some circumstances.
         *
         * @param T Type to be held by the queue.
         * @return The new instance.
         */
        @JvmStatic
        fun <T> depthFirstSupplier(): Supplier<TraversalQueue<T>> {
            return Supplier { depthFirst() }
        }

    }

}

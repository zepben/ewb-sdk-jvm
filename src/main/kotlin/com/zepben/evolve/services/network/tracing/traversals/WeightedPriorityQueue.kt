/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network.tracing.traversals

import java.util.*

/**
 * A traversal queue which uses a weighted order. The higher the weight, the higher the priority.
 *
 * @param T             The type of objects in the queue.
 * @param queueProvider A queue provider. This allows you to customise the priority of items with the same weight.
 * @param getWeight     A method to extract the weight of an item being added to the queue.
 */
class WeightedPriorityQueue<T>(
    private val queueProvider: () -> Queue<T>,
    private val getWeight: (T) -> Int
) : TraversalQueue<T> {

    private val queue: MutableMap<Int, Queue<T>> = TreeMap(Collections.reverseOrder())

    override fun hasNext(): Boolean = queue.isNotEmpty()

    override fun next(): T? {
        var next: T? = null

        val iterator = queue.entries.iterator()
        while (iterator.hasNext() && next == null) {
            val subQueue = iterator.next().value
            next = subQueue.poll()

            if (subQueue.peek() == null)
                iterator.remove()
        }

        return next
    }

    override fun add(item: T): Boolean {
        val weight = getWeight(item)
        if (weight < 0)
            return false

        queue.compute(weight) { _, q -> (q ?: queueProvider()).also { it.add(item) } }

        return true
    }

    override fun peek(): T? {
        var next: T? = null

        val iterator: Iterator<Map.Entry<Int, Queue<T>>> = queue.entries.iterator()
        while (iterator.hasNext() && next == null)
            next = iterator.next().value.peek()

        return next
    }

    override fun clear() {
        queue.clear()
    }

    companion object {

        /**
         * Special priority queue that queues items with the largest weight as the highest priority.
         */
        @JvmStatic
        fun <T> processQueue(getWeight: (T) -> Int): TraversalQueue<T> = WeightedPriorityQueue({ Collections.asLifoQueue(ArrayDeque()) }, getWeight)

        /**
         * Special priority queue that queues branch items with the largest weight on the starting item as highest priority.
         */
        @JvmStatic
        fun <T> branchQueue(getWeight: (T) -> Int): TraversalQueue<Traversal<T>> = WeightedPriorityQueue(
            { ArrayDeque() },
            { traversal -> traversal.startItem?.let { getWeight(it) } ?: -1 }
        )

    }

}

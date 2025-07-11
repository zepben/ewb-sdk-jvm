/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.network.tracing.traversal

import java.util.*

/**
 * A simple queue implementation for use with traversals.
 */
internal class BasicQueue<T>(
    private val queue: Queue<T>
) : TraversalQueue<T> {

    override fun hasNext(): Boolean {
        return queue.isNotEmpty()
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

}

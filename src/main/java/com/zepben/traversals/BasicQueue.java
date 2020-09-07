/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 * This file is part of evolve-sdk-jvm.
 *
 * evolve-sdk-jvm is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * evolve-sdk-jvm is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with evolve-sdk-jvm.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.zepben.traversals;

import com.zepben.annotations.EverythingIsNonnullByDefault;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Queue;
import java.util.function.Supplier;

/**
 * A simple queue implementation for use with traversals.
 */
@SuppressWarnings("WeakerAccess")
@EverythingIsNonnullByDefault
public class BasicQueue<T> implements TraversalQueue<T> {

    private
    Queue<T> queue;

    /**
     * Creates a new instance backed by a breadth first (FIFO) queue.
     *
     * @param <T> Type to be held by the queue.
     * @return The new instance.
     */
    public static <T> TraversalQueue<T> breadthFirst() {
        return new BasicQueue<>(new ArrayDeque<>());
    }

    /**
     * Returns a supplier that creates new instances of a breadth first queue.
     * This exists because mainly because a method reference where a supplier was needed was causing type inference
     * related errors in some circumstances.
     *
     * @param <T> Type to be held by the queue.
     * @return The new instance.
     */
    public static <T> Supplier<TraversalQueue<T>> breadFirstSupplier() {
        return BasicQueue::breadthFirst;
    }

    /**
     * Creates a new instance backed by a depth first (LIFO) queue.
     *
     * @param <T> Type to be held by the queue.
     * @return The new instance.
     */
    public static <T> TraversalQueue<T> depthFirst() {
        return new BasicQueue<>(Collections.asLifoQueue(new ArrayDeque<>()));
    }

    /**
     * Returns a supplier that creates new instances of a depth first queue.
     * This exists because mainly because a method reference where a supplier was needed was causing type inference
     * related errors in some circumstances.
     *
     * @param <T> Type to be held by the queue.
     * @return The new instance.
     */
    public static <T> Supplier<TraversalQueue<T>> depthFirstSupplier() {
        return BasicQueue::depthFirst;
    }

    protected BasicQueue(Queue<T> queue) {
        this.queue = queue;
    }

    @Override
    public boolean hasNext() {
        return queue.peek() != null;
    }

    @Override
    public T next() {
        return queue.poll();
    }

    @Override
    public boolean add(T item) {
        return queue.add(item);
    }

    @Nullable
    @Override
    public T peek() {
        return queue.peek();
    }

    @Override
    public void clear() {
        queue.clear();
    }

}

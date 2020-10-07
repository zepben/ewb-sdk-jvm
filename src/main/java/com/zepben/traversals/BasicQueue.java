/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
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

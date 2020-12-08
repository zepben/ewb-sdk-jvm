/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network.tracing.traversals;

import com.zepben.annotations.EverythingIsNonnullByDefault;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A traversal queue which uses a weighted order. The higher the weight, the higher the priority.
 *
 * @param <T> The type of objects in the queue.
 */
@EverythingIsNonnullByDefault
public class WeightedPriorityQueue<T> implements TraversalQueue<T> {

    private final TreeMap<Integer, Queue<T>> queue = new TreeMap<>(Collections.reverseOrder());
    private final Supplier<Queue<T>> queueProvider;
    private final Function<T, Integer> getWeight;

    /**
     * Special priority queue that queues items with the largest weight as the highest priority.
     */
    public static <T> TraversalQueue<T> processQueue(Function<T, Integer> getWeight) {
        return new WeightedPriorityQueue<>(() -> Collections.asLifoQueue(new ArrayDeque<>()), getWeight);
    }

    /**
     * Special priority queue that queues branch items with the largest weight on the starting item as highest priority.
     */
    public static <T> TraversalQueue<Traversal<T>> branchQueue(Function<T, Integer> getWeight) {
        return new WeightedPriorityQueue<>(ArrayDeque::new,
            item -> {
                T start = item.startItem();
                return start != null ? getWeight.apply(start) : -1;
            });
    }

    /**
     * @param queueProvider A queue provider. This allows you to customise the priority of items with the same weight.
     * @param getWeight     A method to extract the weight of an item being added to the queue.
     */
    public WeightedPriorityQueue(Supplier<Queue<T>> queueProvider, Function<T, Integer> getWeight) {
        this.queueProvider = queueProvider;
        this.getWeight = getWeight;
    }

    @Override
    public boolean hasNext() {
        return queue.size() > 0;
    }

    @Nullable
    @Override
    public T next() {
        T next = null;
        Iterator<Map.Entry<Integer, Queue<T>>> iterator = queue.entrySet().iterator();
        while (iterator.hasNext() && next == null) {
            Queue<T> subQueue = iterator.next().getValue();
            next = subQueue.poll();
            if (subQueue.peek() == null)
                iterator.remove();
        }

        return next;
    }

    @Override
    public boolean add(T item) {
        int weight = getWeight.apply(item);
        if (weight < 0)
            return false;

        queue.compute(weight, (k, v) -> {
            if (v == null)
                v = queueProvider.get();
            v.add(item);
            return v;
        });
        return true;
    }

    @Nullable
    @Override
    public T peek() {
        T next = null;
        Iterator<Map.Entry<Integer, Queue<T>>> iterator = queue.entrySet().iterator();
        while (iterator.hasNext() && next == null) {
            Queue<T> subQueue = iterator.next().getValue();
            next = subQueue.peek();
        }

        return next;
    }

    @Override
    public void clear() {
        queue.clear();
    }

}

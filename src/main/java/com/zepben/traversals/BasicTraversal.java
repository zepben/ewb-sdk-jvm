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

/**
 * <p>A basic traversal implementation that can be used to traverse any type of item.</p>
 * <p>The traversal gets the next items to be traversed to by calling a user provided functional interface
 * ({@link com.zepben.traversals.BasicTraversal.QueueNext}, that passes the current item of the traversal, and the
 * traversal instance itself where the process queue can have items added to it. This is registered during construction.</p>
 * <p>The process queue, an instance of {@link TraversalQueue} is also supplied during construction. This gives the
 * flexibility for this trace to be backed by any type of queue: breadth, depth, priority etc.</p>
 * <p>The traversal also requires a {@link Tracker} to be supplied on construction. This gives flexibility to track
 * items in unique ways, more than just "has this item been visited" e.g. visiting more than once,
 * visiting under different conditions etc.</p>
 */
@EverythingIsNonnullByDefault
public class BasicTraversal<T> extends Traversal<T> {

    private TraversalQueue<T> processQueue;
    private QueueNext<T> queueNext;
    private Tracker<T> tracker;

    /**
     * Represents a consumer that takes the current item of the traversal, and the traversal instance so items can be queued.
     *
     * @param <T> The type of object being traversed.
     */
    public interface QueueNext<T> {

        void accept(@Nullable T item, BasicTraversal<T> traversal);

    }

    /**
     * Creates a new basic traversal.
     *
     * @param queueNext    The consumer that will be called at each step of the traversal to queue "adjacent" items.*
     * @param processQueue The item queue to be used by this traversal.
     * @param tracker      The tracker that tracks items during the traversal.
     */
    public BasicTraversal(QueueNext<T> queueNext, TraversalQueue<T> processQueue, Tracker<T> tracker) {
        this.queueNext = queueNext;
        this.processQueue = processQueue;
        this.tracker = tracker;
    }

    /**
     * @return The queue of items that this traversal is yet to visit.
     */
    public TraversalQueue<T> queue() {
        return processQueue;
    }

    /**
     * @return The tracker used by this traversal
     */
    @Override
    public Tracker<T> tracker() {
        return tracker;
    }

    @Override
    public BasicTraversal<T> reset() {
        resetRunFlag();

        processQueue.clear();
        tracker.clear();

        return this;
    }

    @Override
    protected void doRun(boolean canStopOnStartItem) {
        boolean canStop = true;

        T startItem = startItem();
        if (startItem != null) {
            processQueue.add(startItem);
            canStop = canStopOnStartItem;
        }

        while (processQueue.hasNext()) {
            T current = processQueue.next();
            if (tracker.visit(current)) {
                boolean isStopping = canStop && matchesAnyStopCondition(current);

                applyStepActions(current, isStopping);

                if (!isStopping)
                    queueNext.accept(current, this);

                canStop = true;
            }
        }
    }

}


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
 * A simple queue interface used for traversals.
 */
@EverythingIsNonnullByDefault
public interface TraversalQueue<T> {

    /**
     * Check if the queue has more items.
     *
     * @return true if the queue has more items.
     */
    boolean hasNext();

    /**
     * Returns the next item in the queue. Most implementations of this interface will return
     * null is this is called when the queue is empty, however it is not enforced.
     *
     * @return The next item in the queue.
     */
    @Nullable
    T next();

    /**
     * Adds an item to the queue.
     * The boolean return is there for queues that may reject an item being added.
     *
     * @param item The item to be added to the queue.
     * @return true if the item was added, false if it could not be.
     */
    boolean add(T item);

    @Nullable
    T peek();

    /**
     * Clears the queue.
     */
    void clear();

}

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

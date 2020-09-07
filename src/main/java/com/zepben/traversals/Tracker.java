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
 * An interface used by Traversals to 'track' items that have been visited.
 */
@EverythingIsNonnullByDefault
public interface Tracker<T> {

    /**
     * Check if the tracker has already seen an item. Note the item passed in is marked as nullable, however
     * implementations may require non-null items.
     *
     * @param item The item to check if it has been visited.
     * @return true if the item has been visited.
     */
    boolean hasVisited(@Nullable T item);

    /**
     * Tells the tracker the supplied item is to be visited. Note the item passed in is marked as nullable, however
     * implementations may require non-null items.
     *
     * @param item The item to be tracked.
     * @return True if this item needs to be visited.
     */
    boolean visit(@Nullable T item);

    /**
     * Clears all tracked items.
     */
    void clear();

}

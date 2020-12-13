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

/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.traversals

/**
 * An interface used by Traversals to 'track' items that have been visited.
 */
interface Tracker<T> {

    /**
     * Check if the tracker has already seen an item.
     *
     * @param item The item to check if it has been visited.
     * @return true if the item has been visited.
     */
    fun hasVisited(item: T): Boolean

    /**
     * Tells the tracker the supplied item is to be visited.
     *
     * @param item The item to be tracked.
     * @return True if this item needs to be visited.
     */
    fun visit(item: T): Boolean

    /**
     * Clears all tracked items.
     */
    fun clear()

}

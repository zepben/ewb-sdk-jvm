/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.traversals

/**
 * Simple tracker for traversals that just tracks the items visited and the order visited.
 */
class BasicTracker<T> : Tracker<T> {

    private val visited = mutableSetOf<T>()

    override fun hasVisited(item: T): Boolean = visited.contains(item)

    override fun visit(item: T): Boolean = visited.add(item)

    override fun clear() {
        visited.clear()
    }

}

/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.traversalV2

import com.zepben.evolve.services.network.tracing.traversals.Tracker

internal class RecursiveTracker<T>(val parent: RecursiveTracker<T>?, val delegate: Tracker<T>) : Tracker<T> {
    override fun hasVisited(item: T): Boolean {
        return delegate.hasVisited(item) || run {
            var current = parent
            while (current != null) {
                if (current.hasVisited(item))
                    return true
                current = current.parent
            }
            return false
        }
    }

    override fun visit(item: T): Boolean {
        var parent = parent
        while (parent != null) {
            if (parent.hasVisited(item))
                return false
            parent = parent.parent
        }

        return delegate.visit(item)
    }

    override fun clear() {
        var parent = parent
        while (parent != null) {
            parent.clear()
            parent = parent.parent
        }

        return delegate.clear()
    }
}

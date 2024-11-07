/*
 * Copyright 2023 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.networktrace

import com.zepben.evolve.services.network.tracing.traversal.Tracker

internal class NetworkTraceTracker<T>(
    private val keySelector: (NetworkTraceStep<T>) -> Any?,
) : Tracker<NetworkTraceStep<T>> {

    // Setting initial capacity greater than default of 16 as we suspect a majority of network traces will step on more than 12 terminals (load factor is 0.75).
    // Not sure what a sensible initial capacity actually is, but 16 just felt too small.
    private val visited = HashSet<Any?>(256)

    override fun hasVisited(item: NetworkTraceStep<T>): Boolean = visited.contains(keySelector(item))

    override fun visit(item: NetworkTraceStep<T>): Boolean = visited.add(keySelector(item))

    override fun clear() {
        visited.clear()
    }

    companion object {
        fun <T> terminalTracker(): NetworkTraceTracker<T> = NetworkTraceTracker { (path) ->
            if (path.nominalPhasePaths.isEmpty())
                path.toTerminal
            else
                path.toTerminal to path.nominalPhasePaths.mapTo(mutableSetOf()) { it.to }
        }

        fun <T> equipmentTracker(): NetworkTraceTracker<T> = NetworkTraceTracker { (path) ->
            if (path.nominalPhasePaths.isEmpty())
                path.toEquipment
            else
                path.toEquipment to path.nominalPhasePaths.mapTo(mutableSetOf()) { it.to }
        }
    }
}

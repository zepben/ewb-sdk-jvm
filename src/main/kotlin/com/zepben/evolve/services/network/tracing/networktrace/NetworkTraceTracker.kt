/*
 * Copyright 2023 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.networktrace

import com.zepben.evolve.services.network.tracing.traversals.Tracker

internal class NetworkTraceTracker<T>(
    private val keySelector: (NetworkTraceStep<T>) -> Any?,
) : Tracker<NetworkTraceStep<T>> {
    private val visited = mutableSetOf<Any?>()

    override fun hasVisited(item: NetworkTraceStep<T>): Boolean = visited.contains(keySelector(item))

    override fun visit(item: NetworkTraceStep<T>): Boolean = visited.add(keySelector(item))

    override fun clear() {
        visited.clear()
    }

    companion object {
        fun <T> terminalTracker(): NetworkTraceTracker<T> = NetworkTraceTracker { it.path.toTerminal }
        fun <T> equipmentTracker(): NetworkTraceTracker<T> = NetworkTraceTracker { it.path.toEquipment }
    }
}

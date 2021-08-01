/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.connectivity

import com.zepben.evolve.services.network.NetworkService
import com.zepben.evolve.services.network.tracing.OpenTest
import com.zepben.evolve.services.network.tracing.traversals.BasicQueue
import com.zepben.evolve.services.network.tracing.traversals.BasicTraversal
import com.zepben.evolve.services.network.tracing.traversals.TraversalQueue

/**
 * A class that creates commonly used connectivity based traces. These ignores phases, they are purely to trace equipment that are connected
 * in any way giving the connectivity between them. You can add custom step actions and stop conditions to the returned traversal.
 */
object ConnectivityTrace {

    /**
     * @return a traversal that traces equipment that are connected, ignoring open status.
     */
    fun newConnectivityTrace(): BasicTraversal<ConnectivityResult> = createTraversal(OpenTest.IGNORE_OPEN, BasicQueue.depthFirst())

    /**
     * @return a traversal that traces equipment that are connected, ignoring open status.
     */
    fun newConnectivityBreadthTrace(): BasicTraversal<ConnectivityResult> = createTraversal(OpenTest.IGNORE_OPEN, BasicQueue.breadthFirst())

    /**
     * @return a traversal that traces equipment that are connected stopping at normally open points.
     */
    fun newNormalConnectivityTrace(): BasicTraversal<ConnectivityResult> = createTraversal(OpenTest.NORMALLY_OPEN, BasicQueue.depthFirst())

    /**
     * @return a traversal that traces equipment that are connected stopping at currently open points.
     */
    fun newCurrentConnectivityTrace(): BasicTraversal<ConnectivityResult> = createTraversal(OpenTest.CURRENTLY_OPEN, BasicQueue.depthFirst())

    private fun createTraversal(openTest: OpenTest, queue: TraversalQueue<ConnectivityResult>) =
        BasicTraversal(queueNext(openTest), queue, ConnectivityTracker())

    private fun queueNext(openTest: OpenTest): BasicTraversal.QueueNext<ConnectivityResult> = BasicTraversal.QueueNext { cr, traversal ->
        val to = cr.to ?: return@QueueNext
        if (openTest.isOpen(to, null))
            return@QueueNext

        NetworkService.connectedEquipment(to)
            .asSequence()
            .filter { it.to != cr.from }
            .forEach { traversal.queue.add(it) }
    }

}

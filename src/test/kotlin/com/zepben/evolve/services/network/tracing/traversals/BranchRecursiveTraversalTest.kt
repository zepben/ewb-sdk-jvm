/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network.tracing.traversals

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.contains
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

/**
 * I'm not entirely sure how to test this class thoroughly...
 * Testing some basic things for now (2015-12-01)
 */
internal class BranchRecursiveTraversalTest {

    private val visitOrder = mutableListOf<Int>()
    private var stopCount = 0

    private val traversal = BranchRecursiveTraversal(
        this::queueNext,
        BasicQueue.depthFirstSupplier(),
        ::BasicTracker,
        BasicQueue.breadthFirstSupplier()
    )
        .addStepAction { i, _ -> visitOrder.add(i) }
        .addStopCondition { ++stopCount; false }

    @Test
    internal fun simpleTest() {
        traversal.run(0)

        assertThat(visitOrder, contains(0, 1, 2, 3, 3, 2, 1))
        assertThat(stopCount, equalTo(visitOrder.size))
    }

    @Test
    internal fun canControlStoppingOnFirstAsset() {
        traversal.addStopCondition { i -> i == 0 }
            .run(0, false)

        assertThat<List<Int>>(visitOrder, contains(0, 1, 2, 3, 3, 2, 1))
        assertThat(stopCount, equalTo(visitOrder.size - 1))

        traversal.reset()
        visitOrder.clear()
        stopCount = 0

        traversal.run(0, true)

        assertThat<List<Int>>(visitOrder, contains(0))
        assertThat(stopCount, equalTo(visitOrder.size))
    }

    private fun queueNext(item: Int, traversal: BranchRecursiveTraversal<Int>) {
        if (item == 0) {
            var branch = traversal.branchSupplier()
            branch.setStart(1)
            traversal.branchQueue.add(branch)

            branch = traversal.branchSupplier()
            branch.setStart(3)
            traversal.branchQueue.add(branch)
        } else if (item == 1 || item == 3) {
            if (traversal.tracker.hasVisited(2))
                traversal.queue.add(0)
            else
                traversal.queue.add(2)
        } else if (item == 2) {
            if (traversal.tracker.hasVisited(1))
                traversal.queue.add(3)
            else if (traversal.tracker.hasVisited(3))
                traversal.queue.add(1)
        }
    }

}

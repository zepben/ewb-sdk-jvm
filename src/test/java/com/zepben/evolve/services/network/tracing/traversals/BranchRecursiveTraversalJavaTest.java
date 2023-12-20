/*
 * Copyright 2021 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.traversals;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;

public class BranchRecursiveTraversalJavaTest {

    private final List<Integer> visitOrder = new ArrayList<>();
    private int stopCount = 0;

    @Test
    void simpleTest() {
        new BranchRecursiveTraversal<>(this::queueNext, BasicQueue.depthFirstSupplier(), BasicTracker::new, BasicQueue.breadthFirstSupplier())
            .addStepAction((i, isStopping) -> visitOrder.add(i))
            .addStopCondition((i) -> {
                ++stopCount;
                return false;
            })
            .run(0);

        assertThat(visitOrder, contains(0, 1, 2, 3, 3, 2, 1));
        assertThat(stopCount, equalTo(visitOrder.size()));
    }

    private void queueNext(Integer item, BranchRecursiveTraversal<Integer> traversal) {
        if (item == 0) {
            var branch = traversal.getBranchSupplier().invoke();
            branch.setStart(1);
            traversal.getBranchQueue().add(branch);

            branch = traversal.getBranchSupplier().invoke();
            branch.setStart(3);
            traversal.getBranchQueue().add(branch);
        } else if (item == 1 || item == 3) {
            if (traversal.getTracker().hasVisited(2))
                traversal.getQueue().add(0);
            else
                traversal.getQueue().add(2);
        } else if (item == 2) {
            if (traversal.getTracker().hasVisited(1))
                traversal.getQueue().add(3);
            else if (traversal.getTracker().hasVisited(3))
                traversal.getQueue().add(1);
        }
    }

}

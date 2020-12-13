/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
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

/**
 * I'm not entirely sure how to test this class thoroughly...
 * Testing some basic things for now (2015-12-01)
 */
public class BranchRecursiveTraversalTest {

    private final List<Integer> visitOrder = new ArrayList<>();
    private int stopCount = 0;

    @Test
    public void simpleTest() {
        BranchRecursiveTraversal<Integer> traversal = new BranchRecursiveTraversal<>(
            this::queueNext,
            BasicQueue.depthFirstSupplier(),
            BasicTracker::new,
            BasicQueue.breadFirstSupplier());
        traversal.addStepAction(this::stepAction);
        traversal.addStopCondition(this::stopCondition);

        traversal.run(0);

        assertThat(visitOrder, contains(0, 1, 2, 3, 3, 2, 1));
        assertThat(stopCount, equalTo(visitOrder.size()));
    }

    @Test
    public void canControlStoppingOnFirstAsset() {
        BranchRecursiveTraversal<Integer> traversal = new BranchRecursiveTraversal<>(
            this::queueNext,
            BasicQueue.depthFirstSupplier(),
            BasicTracker::new,
            BasicQueue.breadFirstSupplier());
        traversal.addStepAction(this::stepAction);
        traversal.addStopCondition(this::stopCondition);
        traversal.addStopCondition(i -> i == 0);

        traversal.run(0, false);

        assertThat(visitOrder, contains(0, 1, 2, 3, 3, 2, 1));
        assertThat(stopCount, equalTo(visitOrder.size() - 1));

        traversal.reset();
        visitOrder.clear();
        stopCount = 0;

        traversal.run(0, true);

        assertThat(visitOrder, contains(0));
        assertThat(stopCount, equalTo(visitOrder.size()));
    }

    private void queueNext(Integer item, BranchRecursiveTraversal<Integer> traversal) {
        if (item.equals(0)) {
            Traversal<Integer> branch = traversal.branchSupplier().get();
            branch.setStart(1);
            traversal.branchQueue().add(branch);

            branch = traversal.branchSupplier().get();
            branch.setStart(3);
            traversal.branchQueue().add(branch);
        } else if (item.equals(1) || item.equals(3)) {
            if (traversal.tracker().hasVisited(2))
                traversal.queue().add(0);
            else
                traversal.queue().add(2);
        } else if (item.equals(2)) {
            if (traversal.tracker().hasVisited(1))
                traversal.queue().add(3);
            else if (traversal.tracker().hasVisited(3))
                traversal.queue().add(1);
        }
    }

    private void stepAction(Integer i, boolean isStopping) {
        visitOrder.add(i);
    }

    private boolean stopCondition(Integer i) {
        ++stopCount;
        return false;
    }

}

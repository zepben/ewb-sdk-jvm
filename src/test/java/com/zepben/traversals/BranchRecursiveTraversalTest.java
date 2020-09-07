/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 * This file is part of evolve-sdk-jvm.
 *
 * evolve-sdk-jvm is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * evolve-sdk-jvm is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with evolve-sdk-jvm.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.zepben.traversals;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * I'm not entirely sure how to test this class thoroughly...
 * Testing some basic things for now (2015-12-01)
 */
public class BranchRecursiveTraversalTest {

    private List<Integer> visitOrder = new ArrayList<>();
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

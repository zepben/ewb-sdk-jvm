/*
 * Copyright 2020 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.zepben.evolve.services.network.tracing.traversals;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class BasicTraversalTest {

    @SuppressWarnings("ConstantConditions")
    private BasicTraversal.QueueNext<Integer> queueNext = (i, t) -> Stream.of(i - 2, i - 1, i + 1, i + 2)
        .filter(n -> n > 0)
        .forEach(t.queue()::add);

    @Test
    public void testBreadthFirst() {
        List<Integer> expectedOrder = Arrays.asList(1, 2, 3, 4, 5, 6, 7);
        List<Integer> visitOrder = new ArrayList<>();

        Traversal<Integer> t = new BasicTraversal<>(queueNext, BasicQueue.breadthFirst(), new BasicTracker<>())
            .addStopCondition((i) -> i >= 6)
            .addStepAction((i, s) -> visitOrder.add(i));

        validateRun(t, true, visitOrder, expectedOrder);
    }

    @Test
    public void testDepthFirst() {
        List<Integer> expectedOrder = Arrays.asList(1, 3, 5, 7, 6, 4, 2);
        List<Integer> visitOrder = new ArrayList<>();

        Traversal<Integer> t = new BasicTraversal<>(queueNext, BasicQueue.depthFirst(), new BasicTracker<>())
            .addStopCondition((i) -> i >= 6)
            .addStepAction((i, s) -> visitOrder.add(i));

        validateRun(t, true, visitOrder, expectedOrder);
    }

    @Test
    public void canControlStoppingOnFirstAsset() {
        validateStoppingOnFirstAsset(new BasicTraversal<>(queueNext, BasicQueue.breadthFirst(), new BasicTracker<>()),
            Arrays.asList(1, 2, 3));

        validateStoppingOnFirstAsset(new BasicTraversal<>(queueNext, BasicQueue.depthFirst(), new BasicTracker<>()),
            Arrays.asList(1, 3, 2));
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void passesStoppingToStep() {
        BasicTraversal.QueueNext<Integer> queueNext = (i, t) -> {
            t.queue().add(i + 1);
            t.queue().add(i + 2);
        };

        Set<Integer> visited = new HashSet<>();
        Set<Integer> stoppingOn = new HashSet<>();

        Traversal<Integer> t = new BasicTraversal<>(queueNext, BasicQueue.depthFirst(), new BasicTracker<>())
            .addStopCondition((i) -> i >= 3)
            .addStepAction((i, s) -> {
                visited.add(i);
                if (s)
                    stoppingOn.add(i);
            });

        t.run(1, true);
        assertThat(visited, containsInAnyOrder(1, 2, 3, 4));
        assertThat(stoppingOn, containsInAnyOrder(3, 4));
    }

    private void validateStoppingOnFirstAsset(BasicTraversal<Integer> t, List<Integer> expectedOrder) {
        t.addStopCondition((i) -> i >= 0);
        t.addStopCondition((i) -> i >= 6);

        final List<Integer> visitOrder = new ArrayList<>();
        t.addStepAction((i, s) -> visitOrder.add(i));

        validateRun(t, false, visitOrder, expectedOrder);

        t.reset();
        visitOrder.clear();

        validateRun(t, true, visitOrder, Collections.singletonList(1));
    }

    private void validateRun(Traversal<Integer> t, boolean canStopOnStart, List<Integer> visitOrder, List<Integer> expectedOrder) {
        t.run(1, canStopOnStart);
        assertThat(visitOrder, contains(expectedOrder.toArray()));
        expectedOrder.forEach(i -> assertThat(t.tracker().hasVisited(i), equalTo(true)));
    }

}

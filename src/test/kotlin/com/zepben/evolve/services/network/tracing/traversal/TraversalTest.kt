///*
// * Copyright 2024 Zeppelin Bend Pty Ltd
// *
// * This Source Code Form is subject to the terms of the Mozilla Public
// * License, v. 2.0. If a copy of the MPL was not distributed with this
// * file, You can obtain one at https://mozilla.org/MPL/2.0/.
// */
//package com.zepben.evolve.services.network.tracing.traversalV2
//
//import com.zepben.evolve.services.network.tracing.traversals.BasicQueue
//import com.zepben.evolve.services.network.tracing.traversals.BasicTracker
//import com.zepben.evolve.services.network.tracing.traversals.Tracker
//import com.zepben.evolve.services.network.tracing.traversals.TraversalQueue
//import org.hamcrest.MatcherAssert.assertThat
//import org.hamcrest.Matchers.*
//import org.junit.jupiter.api.Test
//
//class TraversalTest {
//
//    class TestTraversal(
//        queueNext: QueueNext,
//        queue: TraversalQueue<Int>,
//        tracker: Tracker<Int>
//    ) : Traversal<Int, TestTraversal>(queueNext, queue, tracker) {
//        fun interface QueueNext : Traversal.QueueNext<Int, TestTraversal>
//
//        override fun getDerivedThis(): TestTraversal = this
//    }
//
//    private val queueNext = TestTraversal.QueueNext { i, _, queueNext, _ ->
//        sequenceOf(i - 2, i - 1, i + 1, i + 2)
//            .filter { n -> n > 0 }
//            .forEach { item -> queueNext(item) }
//    }
//
//    @Test
//    fun testBreadthFirst() {
//        val expectedOrder = listOf(1, 2, 3, 4, 5, 6, 7)
//        val visitOrder = mutableListOf<Int>()
//
//        val t = TestTraversal(queueNext, BasicQueue.breadthFirst(), BasicTracker())
//            .addStopCondition { i, _ -> i >= 6 }
//            .addStepAction { i, _ -> visitOrder.add(i) }
//
//        validateRun(t, true, visitOrder, expectedOrder)
//    }
//
//    @Test
//    fun testDepthFirst() {
//        val expectedOrder = listOf(1, 3, 5, 7, 6, 4, 2)
//        val visitOrder = mutableListOf<Int>()
//
//        val t = TestTraversal(queueNext, BasicQueue.depthFirst(), BasicTracker())
//            .addStopCondition { i, _ -> i >= 6 }
//            .addStepAction { i, _ -> visitOrder.add(i) }
//
//        validateRun(t, true, visitOrder, expectedOrder)
//    }
//
//    @Test
//    fun canControlStoppingOnFirstAsset() {
//        validateStoppingOnFirstAsset(TestTraversal(queueNext, BasicQueue.breadthFirst(), BasicTracker()), listOf(1, 2, 3))
//        validateStoppingOnFirstAsset(TestTraversal(queueNext, BasicQueue.depthFirst(), BasicTracker()), listOf(1, 3, 2))
//    }
//
//    @Test
//    fun passesStoppingToStep() {
//        val queueNext = TestTraversal.QueueNext { i, ctx, queue, t ->
//            queue(i + 1)
//            queue(i + 2)
//        }
//
//        val visited = mutableSetOf<Int>()
//        val stoppingOn = mutableSetOf<Int>()
//
//        val t = TestTraversal(queueNext, BasicQueue.depthFirst(), BasicTracker())
//            .addStopCondition { i, _ -> i >= 3 }
//            .addStepAction { i, ctx ->
//                visited.add(i)
//                if (ctx.isStopping)
//                    stoppingOn.add(i)
//            }
//
//        t.run(1, true)
//        assertThat(visited, containsInAnyOrder(1, 2, 3, 4))
//        assertThat(stoppingOn, containsInAnyOrder(3, 4))
//    }
//
//    @Test
//    internal fun runsAllStopChecks() {
//        val stopCalls = mutableListOf(0, 0, 0)
//
//        TestTraversal({ _, _, _, _ -> }, BasicQueue.depthFirst(), BasicTracker())
//            .addStopCondition { i, _ -> stopCalls[0] = i; true }
//            .addStopCondition { i, _ -> stopCalls[1] = i; true }
//            .addStopCondition { i, _ -> stopCalls[2] = i; true }
//            .run(1, true)
//
//        assertThat(stopCalls, contains(1, 1, 1))
//    }
//
//    @Test
//    internal fun runsAllStepActions() {
//        val stopCalls = mutableListOf(0, 0, 0)
//
//        TestTraversal({ _, _, _, _ -> }, BasicQueue.depthFirst(), BasicTracker())
//            .addStepAction { i, _ -> stopCalls[0] = i }
//            .addStepAction { i, _ -> stopCalls[1] = i }
//            .addStepAction { i, _ -> stopCalls[2] = i }
//            .run(1, true)
//
//        assertThat(stopCalls, contains(1, 1, 1))
//    }
//
//    @Test
//    fun `stop checking actions are triggered correctly`() {
//        // We do not bother with the queue next as we will just prime the queue with what we want to test.
//        val queueNext = TestTraversal.QueueNext { _, _, _, _ -> }
//
//        val steppedOn = mutableSetOf<Int>()
//        val notStoppingOn = mutableSetOf<Int>()
//        val stoppingOn = mutableSetOf<Int>()
//
//        TestTraversal(queueNext, BasicQueue.depthFirst(), BasicTracker()).apply {
//            addStopCondition { it, _ -> it >= 3 }
//            addStepAction { it, _ -> steppedOn.add(it) }
//            ifNotStopping { it, _ -> notStoppingOn.add(it) }
//            ifStopping { it, _ -> stoppingOn.add(it) }
//
//            addStartItems(1, 2, 3, 4)
//
//            run()
//        }
//
//        assertThat(steppedOn, containsInAnyOrder(1, 2, 3, 4))
//        assertThat(notStoppingOn, containsInAnyOrder(1, 2))
//        assertThat(stoppingOn, containsInAnyOrder(3, 4))
//    }
//
//    private fun validateStoppingOnFirstAsset(t: TestTraversal, expectedOrder: List<Int>) {
//        t.addStopCondition { i, _ -> i >= 0 }
//        t.addStopCondition { i, _ -> i >= 6 }
//
//        val visitOrder = mutableListOf<Int>()
//        t.addStepAction { i, _ -> visitOrder.add(i) }
//
//        validateRun(t, false, visitOrder, expectedOrder)
//
//        t.reset()
//        visitOrder.clear()
//
//        validateRun(t, true, visitOrder, listOf(1))
//    }
//
//    private fun validateRun(t: TestTraversal, canStopOnStart: Boolean, visitOrder: List<Int>, expectedOrder: List<Int>) {
//        t.run(1, canStopOnStart)
//        assertThat(visitOrder, contains<Any>(*expectedOrder.toTypedArray()))
//        expectedOrder.forEach { assertThat(t.tracker.hasVisited(it), equalTo(true)) }
//    }
//
//}

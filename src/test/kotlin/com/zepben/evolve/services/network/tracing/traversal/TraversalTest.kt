/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.evolve.services.network.tracing.traversal

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.contains
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import kotlin.math.abs

class TraversalTest {

    private class TestTraversal<T>(
        queueType: QueueType<T, TestTraversal<T>>,
        parent: TestTraversal<T>? = null,
        private val canVisitItemImpl: (T, StepContext) -> Boolean,
        private val canActionItemImpl: (T, StepContext) -> Boolean,
        private val onResetImpl: () -> Unit,
    ) : Traversal<T, TestTraversal<T>>(queueType, parent) {
        override fun canVisitItem(item: T, context: StepContext): Boolean = canVisitItemImpl(item, context)
        override fun canActionItem(item: T, context: StepContext): Boolean = canActionItemImpl(item, context)
        override fun onReset() = onResetImpl()
        override fun getDerivedThis(): TestTraversal<T> = this
        override fun createNewThis(): TestTraversal<T> = TestTraversal(queueType, this, canVisitItemImpl, canActionItemImpl, onResetImpl)

        public override fun addStartItem(item: T): TestTraversal<T> = super.addStartItem(item)
        public override fun run(startItem: T, canStopOnStartItem: Boolean): TestTraversal<T> = super.run(startItem, canStopOnStartItem)
    }

    private fun createTraversal(
        canVisitItem: (Int, StepContext) -> Boolean = { _, _ -> true },
        canActionItem: (Int, StepContext) -> Boolean = { _, _ -> true },
        onReset: () -> Unit = {}
    ): TestTraversal<Int> {
        val queueType = Traversal.BasicQueueType<Int, TestTraversal<Int>>(
            queueNext = { item, _, queueItem ->
                if (item < 0)
                    queueItem(item - 1)
                else
                    queueItem(item + 1)
            },
            queue = TraversalQueue.depthFirst()
        )

        return TestTraversal(queueType, null, canVisitItem, canActionItem, onReset)
    }

    private fun createBranchingTraversal(): TestTraversal<Int> {
        val queueType = Traversal.BranchingQueueType<Int, TestTraversal<Int>>(
            // Note: This is pretty simple and probably doesn't capture testing branching thoroughly. E.g it doesn't queue multiple branches at once...
            //  Consider making a more thorough test.
            queueNext = { item, _, queueItem, queueBranch ->
//                if (item == 100) queueBranch(-100)
//                if (item % 10 == 0) queueBranch(item + 1)
//                else queueItem(item + 1)

                  if (item == 0) {
                      queueBranch(-10)
                      queueBranch(10)
                  } else if (item < 0) {
                      queueItem(item + 1)
                  } else {
                      queueItem(item - 1)
                  }

            },
            queueFactory = { TraversalQueue.depthFirst() },
            branchQueueFactory = { TraversalQueue.depthFirst() },
        )

        return TestTraversal(queueType, null, { _, _ -> true }, { _, _ -> true }, {})
    }

    @Test
    fun `addCondition with stop condition`() {
        var lastNum: Int? = null
        createTraversal()
            .addCondition(StopCondition { item, _ -> item == 2 })
            .addStepAction { item, _ -> lastNum = item }
            .run(1)

        assertThat(lastNum, equalTo(2))
    }

    @Test
    fun `addCondition with queue condition`() {
        var lastNum: Int? = null
        createTraversal()
            .addCondition(QueueCondition { item, _, _, _ -> item < 3 })
            .addStepAction { item, _ -> lastNum = item }
            .run(1)

        assertThat(lastNum, equalTo(2))
    }

    @Test
    fun `stop conditions`() {
        val steps = mutableListOf<Pair<Int, StepContext>>()
        createTraversal()
            .addStopCondition { item, _ -> item == 3 }
            .addStepAction { item, ctx -> steps.add(item to ctx) }
            .run(1)

        assertThat(steps[0].first, equalTo(1))
        assertThat(steps[0].second.isStopping, equalTo(false))
        assertThat(steps[1].first, equalTo(2))
        assertThat(steps[1].second.isStopping, equalTo(false))
        assertThat(steps[2].first, equalTo(3))
        assertThat(steps[2].second.isStopping, equalTo(true))
    }

    @Test
    fun `stops when matching any stop condition`() {
        var lastNum: Int? = null
        createTraversal()
            .addStopCondition { item, _ -> item == 3 }
            .addStopCondition { item, _ -> item % 2 == 0 }
            .addStepAction { item, _ -> lastNum = item }
            .run(1)

        assertThat(lastNum, equalTo(2))
    }

    @Test
    fun `can stop on start item true`() {
        var lastNum: Int? = null
        createTraversal()
            .addStopCondition { item, _ -> item == 1 }
            .addStopCondition { item, _ -> item == 2 }
            .addStepAction { item, _ -> lastNum = item }
            .run(1, canStopOnStartItem = true)

        assertThat(lastNum, equalTo(1))
    }

    @Test
    fun `can stop on start item false`() {
        var lastNum: Int? = null
        createTraversal()
            .addStopCondition { item, _ -> item == 1 }
            .addStopCondition { item, _ -> item == 2 }
            .addStepAction { item, _ -> lastNum = item }
            .run(1, canStopOnStartItem = false)

        assertThat(lastNum, equalTo(2))
    }

    @Test
    fun `checks queue condition`() {
        var lastNum: Int? = null
        createTraversal()
            .addQueueCondition { nextItem, _, _, _ -> nextItem < 3 }
            .addStepAction { item, _ -> lastNum = item }
            .run(1)

        assertThat(lastNum, equalTo(2))
    }

    @Test
    fun `queues when matching all queue conditions`() {
        var lastNum: Int? = null
        createTraversal()
            .addQueueCondition { nextItem, _, _, _ -> nextItem < 3 }
            .addQueueCondition { nextItem, _, _, _ -> nextItem > 3 }
            .addStepAction { item, _ -> lastNum = item }
            .run(1)

        assertThat(lastNum, equalTo(1))
    }

    @Test
    fun `calls all registered step actions`() {
        var called1 = false
        var called2 = false
        createTraversal()
            .addStopCondition { item, _ -> item == 2 }
            .addStepAction { _, _ -> called1 = true }
            .addStepAction { _, _ -> called2 = true }
            .run(1)

        assertThat(called1, equalTo(true))
        assertThat(called2, equalTo(true))
    }

    @Test
    fun `ifNotStopping helper only calls when not stopping`() {
        val steps = mutableListOf<Int>()
        createTraversal()
            .addStopCondition { item, _ -> item == 3 }
            .ifNotStopping { item, _ -> steps.add(item) }
            .run(1)

        assertThat(steps, contains(1, 2))
    }

    @Test
    fun `ifStopping helper only calls when stopping`() {
        val steps = mutableListOf<Int>()
        createTraversal()
            .addStopCondition { item, _ -> item == 3 }
            .ifStopping { item, _ -> steps.add(item) }
            .run(1)

        assertThat(steps, contains(3))
    }

    @Test
    fun `contextValueComputer adds value to context`() {
        val dataCapture = mutableMapOf<Int, String?>()
        createTraversal()
            .addContextValueComputer(object : ContextValueComputer<Int> {
                override val key: String
                    get() = "test"

                override fun computeNextValue(nextItem: Int, currentItem: Int, currentValue: Any?): Any = "$currentValue : ${nextItem + currentItem}"

                override fun computeInitialValue(item: Int): Any = "$item"
            })
            .addStepAction { item, ctx -> dataCapture[item] = ctx.getValue("test") }
            .addStopCondition { item, _ -> item == 2 }
            .run(1)

        assertThat(dataCapture[1], equalTo("1"))
        assertThat(dataCapture[2], equalTo("1 : 3"))
    }

    @Test
    fun startItems() {
        val steps = mutableMapOf<Int, StepContext>()
        val traversal = createTraversal()
            .addStartItem(1)
            .addStartItem(-1)
            .addStopCondition { item, _ -> abs(item) == 2 }
            .addStepAction { item, ctx -> steps[item] = ctx }

        assertThat(traversal.startItems(), contains(1, -1))
        traversal.run()

        assertThat(steps[1]?.isStartItem, equalTo(true))
        assertThat(steps[-1]?.isStartItem, equalTo(true))
        assertThat(steps[2]?.isStartItem, equalTo(false))
        assertThat(steps[-2]?.isStartItem, equalTo(false))
    }

    @Test
    fun `only visits items that can be visited`() {
        val steps = mutableListOf<Int>()
        createTraversal(canVisitItem = { item, _ -> item < 0 })
            .addStopCondition { item, _ -> item == -2 }
            .addStepAction { item, _ -> steps.add(item) }
            .addStartItem(1)
            .addStartItem(-1)
            .run()

        assertThat(steps, contains(-1, -2))
    }

    @Test
    fun `only actions items that can be actioned`() {
        val steps = mutableListOf<Int>()
        createTraversal(canActionItem = { item, _ -> item % 2 == 1 })
            .addStopCondition { item, _ -> item == 3 }
            .addStepAction { item, _ -> steps.add(item) }
            .run(1)

        assertThat(steps, contains(1, 3))
    }

    @Test
    fun `can be rerun`() {
        var resetCalled = false
        val stepVisitCount = mutableMapOf<Int, Int>()
        createTraversal(onReset = { resetCalled = true })
            .addStopCondition { item, _ -> item == 2 }
            .addStepAction { item, _ -> stepVisitCount[item] = stepVisitCount.getOrDefault(item, 0) + 1 }
            .run(1)
            .run(2)

        assertThat(stepVisitCount[1], equalTo(1))
        assertThat(stepVisitCount[2], equalTo(2))
        assertThat(resetCalled, equalTo(true))
    }

    @Test
    fun `supports branching traversals`() {
        val steps = mutableMapOf<Int, StepContext>()
        createBranchingTraversal()
            .addQueueCondition { item, ctx, _, _ -> ctx.branchDepth <= 1 && item != 0 }
            .addStepAction { item, ctx -> steps[item] = ctx }
            .run(0, canStopOnStartItem = false)

        assertThat(steps[0]?.isBranchStartItem, equalTo(false))
        assertThat(steps[0]?.isStartItem, equalTo(true))
        assertThat(steps[0]?.branchDepth, equalTo(0))

        assertThat(steps[10]?.isBranchStartItem, equalTo(true))
        assertThat(steps[10]?.branchDepth, equalTo(1))

        assertThat(steps[1]?.isBranchStartItem, equalTo(false))
        assertThat(steps[1]?.isStartItem, equalTo(false))
        assertThat(steps[1]?.branchDepth, equalTo(1))

        assertThat(steps[-10]?.isBranchStartItem, equalTo(true))
        assertThat(steps[-10]?.branchDepth, equalTo(1))

        assertThat(steps[-1]?.isBranchStartItem, equalTo(false))
        assertThat(steps[-1]?.isStartItem, equalTo(false))
        assertThat(steps[-1]?.branchDepth, equalTo(1))
    }

    @Test
    fun `canStopOnStartItem is not assessed on branch start items`() {
        var stopConditionTriggered = false
        createBranchingTraversal()
            .addStopCondition { item, _ ->
                stopConditionTriggered = abs(item) == 10
                stopConditionTriggered
            }
            .addQueueCondition { _, ctx, _, _ -> ctx.branchDepth < 2 }
            .addStartItem(1)
            .addStartItem(-1)
            .run(canStopOnStartItem = false)

        assertThat(stopConditionTriggered, equalTo(true))
    }
}

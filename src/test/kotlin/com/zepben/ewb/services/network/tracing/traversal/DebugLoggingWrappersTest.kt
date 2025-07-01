/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.network.tracing.traversal

import com.zepben.testutils.junit.SystemLogExtension
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verifySequence
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.slf4j.LoggerFactory

internal class DebugLoggingWrappersTest {

    @JvmField
    @RegisterExtension
    var systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    private val logger = LoggerFactory.getLogger(javaClass)

    private val context1 = mockk<StepContext>().also { every { it.toString() } returns "context 1 string" }
    private val context2 = mockk<StepContext>().also { every { it.toString() } returns "context 2 string" }
    private val item1 = 1 to 1.1
    private val item2 = 2 to 2.2

    @Test
    internal fun `can wrap stop conditions`() {
        val condition = mockk<StopCondition<Pair<Int, Double>>> {
            every { shouldStop(item1, any()) } returns true
            every { shouldStop(item2, any()) } returns false
        }
        val wrapped = DebugLoggingWrappers.wrapStopCondition("my desc", condition, logger, 100)

        assertThat("should have stopped on item1", wrapped.shouldStop(item1, context1))
        assertThat("shouldn't have stopped on item2", !wrapped.shouldStop(item2, context1))

        validateLog("my desc: shouldStop(100)=true [item=$item1, context=$context1]")
        validateLog("my desc: shouldStop(100)=false [item=$item2, context=$context1]")

        verifySequence {
            condition.shouldStop(item1, context1)
            condition.shouldStop(item2, context1)
        }
    }

    @Test
    internal fun `can wrap queue conditions`() {
        val condition = mockk<QueueCondition<Pair<Int, Double>>> {
            every { shouldQueue(item1, any(), any(), any()) } returns true
            every { shouldQueue(item2, any(), any(), any()) } returns false

            every { shouldQueueStartItem(item1) } returns false
            every { shouldQueueStartItem(item2) } returns true
        }
        val wrapped = DebugLoggingWrappers.wrapQueueCondition("my desc", condition, logger, 50)

        assertThat("should have queued item1", wrapped.shouldQueue(item1, context1, item2, context2))
        assertThat("shouldn't have queued item2", !wrapped.shouldQueue(item2, context2, item1, context1))

        assertThat("shouldn't have queued start item1", !wrapped.shouldQueueStartItem(item1))
        assertThat("should have queued start item2", wrapped.shouldQueueStartItem(item2))

        validateLog("my desc: shouldQueue(50)=true [nextItem=$item1, nextContext=$context1, currentItem=$item2, currentContext=$context2]")
        validateLog("my desc: shouldQueue(50)=false [nextItem=$item2, nextContext=$context2, currentItem=$item1, currentContext=$context1]")

        validateLog("my desc: shouldQueueStartItem(50)=false [item=$item1]")
        validateLog("my desc: shouldQueueStartItem(50)=true [item=$item2]")

        verifySequence {
            condition.shouldQueue(item1, context1, item2, context2)
            condition.shouldQueue(item2, context2, item1, context1)
            condition.shouldQueueStartItem(item1)
            condition.shouldQueueStartItem(item2)
        }
    }

    @Test
    internal fun `can wrap step actions`() {
        val action = mockk<StepAction<Pair<Int, Double>>> {
            justRun { apply(any(), any()) }
        }
        val wrapped = DebugLoggingWrappers.wrapStepAction("my desc", action, logger, 1)

        wrapped.apply(item1, context1)
        wrapped.apply(item2, context1)

        validateLog("my desc: steppingOn(1) [item=$item1, context=$context1]")
        validateLog("my desc: steppingOn(1) [item=$item2, context=$context1]")

        verifySequence {
            action.apply(item1, context1)
            action.apply(item2, context1)
        }
    }

    private fun validateLog(expectedMessage: String) {
        assertThat(systemErr.log, containsString(expectedMessage))
    }

}

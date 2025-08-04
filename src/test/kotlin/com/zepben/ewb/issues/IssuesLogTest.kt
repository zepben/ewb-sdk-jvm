/*
 * Copyright (c) Zeppelin Bend Pty Ltd (Zepben) 2025 - All Rights Reserved.
 * Unauthorized use, copy, or distribution of this file or its contents, via any medium is strictly prohibited.
 */

package com.zepben.ewb.issues

import com.zepben.testutils.junit.SystemLogExtension
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifySequence
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.slf4j.LoggerFactory

internal class IssuesLogTest {

    @JvmField
    @RegisterExtension
    var systemOut: SystemLogExtension = SystemLogExtension.SYSTEM_OUT.captureLog().muteOnSuccess()

    private val logger = LoggerFactory.getLogger(javaClass)

    @Test
    internal fun `default constructor coverage`() {
        IssuesLog(logger = logger)
    }

    @Test
    internal fun `logs trackers via categories`() {
        val tracker1 = IssueTracker(logger) { "issues 1 summary: $it" }
        val tracker2 = IssueTracker(logger) { "issues 2 summary: $it" }
        val tracker3 = IssueTracker(logger) { "issues 3 summary: $it" }
        val tracker4 = IssueTracker(logger) { "issues 4 summary: $it" }
        val tracker5 = IssueTracker(logger) { "issues 5 summary: $it" }

        val group1 = groupOf(tracker1, tracker2)
        val group2 = groupOf(tracker3, tracker4)
        val group3 = groupOf(tracker5)

        val issuesLog = IssuesLog(detailedLogging = true, logger)

        issuesLog.add("cat1", group1)
        issuesLog.add("cat1", group2)
        issuesLog.add("cat2", group3)

        tracker1.track("message 1")
        tracker4.track("message 2")
        tracker5.track("message 3")

        issuesLog.logIssues()

        verifySequence {
            group1.setLogIssues(true)
            group2.setLogIssues(true)
            group3.setLogIssues(true)

            // For overall totals.
            group1.trackers
            group2.trackers
            group3.trackers

            // For category totals.
            group1.trackers
            group2.trackers

            // For category log.
            group1.trackers
            group1.logSummaries()
            group2.trackers
            group2.logSummaries()

            // For category totals.
            group3.trackers

            // For category log.
            group3.trackers
            group3.logSummaries()
        }
    }

    @Test
    internal fun `only logs category if it tracked something`() {
        val tracker1 = IssueTracker(logger) { "issues 1 summary: $it" }
        val tracker2 = IssueTracker(logger) { "issues 2 summary: $it" }

        val group1 = groupOf(tracker1)
        val group2 = groupOf(tracker2)

        val issuesLog = IssuesLog(detailedLogging = true, logger)

        issuesLog.add("cat1", group1)
        issuesLog.add("cat2", group2)

        tracker1.track("message 1")

        issuesLog.logIssues()

        verifySequence {
            group1.setLogIssues(true)
            group2.setLogIssues(true)

            // For overall totals.
            group1.trackers
            group2.trackers

            // For category totals.
            group1.trackers

            // For category log.
            group1.trackers
            group1.logSummaries()

            // For category totals.
            group2.trackers
        }
    }

    @Test
    internal fun `only logs the group in a category if it tracked something`() {
        val tracker1 = IssueTracker(logger) { "issues 1 summary: $it" }
        val tracker2 = IssueTracker(logger) { "issues 2 summary: $it" }

        val group1 = groupOf(tracker1, tracker2)

        val issuesLog = IssuesLog(detailedLogging = true, logger)

        issuesLog.add("cat1", group1)

        tracker1.track("message 1")

        issuesLog.logIssues()

        verifySequence {
            group1.setLogIssues(true)

            // For overall totals.
            group1.trackers

            // For category totals.
            group1.trackers

            // For category log.
            group1.trackers
            group1.logSummaries()
        }
    }

    private fun groupOf(vararg issueTrackers: IssueTracker): IssueTrackerGroup =
        mockk {
            every { setLogIssues(any()) } returns this
            every { logSummaries() } returns this
            every { trackers } returns issueTrackers.toList()
        }

}

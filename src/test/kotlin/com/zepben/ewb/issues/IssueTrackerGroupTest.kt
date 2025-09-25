/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */


package com.zepben.ewb.issues

import com.zepben.ewb.metrics.NetworkMetrics
import com.zepben.testutils.junit.SystemLogExtension
import io.mockk.mockk
import io.mockk.verify
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.Matchers.sameInstance
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class IssueTrackerGroupTest {

    @JvmField
    @RegisterExtension
    val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()

    private val trackerGroup = TrackerGroup()
    private val subTrackerGroup = SubTrackerGroup()
    private val composedTrackerGroup = ComposedTrackerGroup()

    @Test
    internal fun trackers() {
        assertThat(trackerGroup.trackers, containsInAnyOrder(trackerGroup.tracker1, trackerGroup.tracker2))
        assertThat(subTrackerGroup.trackers, containsInAnyOrder(subTrackerGroup.tracker1, subTrackerGroup.tracker2, subTrackerGroup.tracker3))
        assertThat(
            composedTrackerGroup.trackers,
            containsInAnyOrder(
                composedTrackerGroup.compTracker1,
                composedTrackerGroup.compTracker2,
                composedTrackerGroup.tracker1,
                composedTrackerGroup.tracker2,
                composedTrackerGroup.tracker3
            )
        )
    }

    @Test
    internal fun setLogIssues() {
        trackerGroup.setLogIssues(true)
        verify {
            trackerGroup.tracker1.logIssues = true
            trackerGroup.tracker2.logIssues = true
        }

        subTrackerGroup.setLogIssues(true)
        verify {
            subTrackerGroup.tracker1.logIssues = true
            subTrackerGroup.tracker2.logIssues = true
            subTrackerGroup.tracker3.logIssues = true
        }

        composedTrackerGroup.setLogIssues(true)
        verify {
            composedTrackerGroup.compTracker1.logIssues = true
            composedTrackerGroup.compTracker2.logIssues = true
            composedTrackerGroup.tracker1.logIssues = true
            composedTrackerGroup.tracker2.logIssues = true
            composedTrackerGroup.tracker3.logIssues = true
        }
    }

    @Test
    internal fun logSummaries() {
        trackerGroup.logSummaries()
        verify {
            trackerGroup.tracker1.logSummary()
            trackerGroup.tracker2.logSummary()
        }

        subTrackerGroup.logSummaries()
        verify {
            subTrackerGroup.tracker1.logSummary()
            subTrackerGroup.tracker2.logSummary()
            subTrackerGroup.tracker3.logSummary()
        }

        composedTrackerGroup.logSummaries()
        verify {
            composedTrackerGroup.compTracker1.logSummary()
            composedTrackerGroup.compTracker2.logSummary()
            composedTrackerGroup.tracker1.logSummary()
            composedTrackerGroup.tracker2.logSummary()
            composedTrackerGroup.tracker3.logSummary()
        }
    }

    @Test
    internal fun setNetworkMetrics() {
        val networkMetrics = mockk<NetworkMetrics>()

        assertThat(trackerGroup.setNetworkMetrics(networkMetrics), sameInstance(trackerGroup))
        verify {
            trackerGroup.tracker1.setNetworkMetrics(networkMetrics)
            trackerGroup.tracker2.setNetworkMetrics(networkMetrics)
        }

        assertThat(subTrackerGroup.setNetworkMetrics(networkMetrics), sameInstance(subTrackerGroup))
        verify {
            subTrackerGroup.tracker1.setNetworkMetrics(networkMetrics)
            subTrackerGroup.tracker2.setNetworkMetrics(networkMetrics)
            subTrackerGroup.tracker3.setNetworkMetrics(networkMetrics)
        }

        assertThat(composedTrackerGroup.setNetworkMetrics(networkMetrics), sameInstance(composedTrackerGroup))
        verify {
            composedTrackerGroup.compTracker1.setNetworkMetrics(networkMetrics)
            composedTrackerGroup.compTracker2.setNetworkMetrics(networkMetrics)
            composedTrackerGroup.tracker1.setNetworkMetrics(networkMetrics)
            composedTrackerGroup.tracker2.setNetworkMetrics(networkMetrics)
            composedTrackerGroup.tracker3.setNetworkMetrics(networkMetrics)
        }
    }

    @Test
    internal fun defaultsMetricNames() {
        trackerGroup.trackers // Runs the lazy initialization block, which includes setting default metric names for each tracker
        verify {
            trackerGroup.tracker1.defaultMetricNameTo("tracker1")
            trackerGroup.tracker2.defaultMetricNameTo("tracker2")
        }

        subTrackerGroup.trackers
        verify {
            subTrackerGroup.tracker1.defaultMetricNameTo("tracker1")
            subTrackerGroup.tracker2.defaultMetricNameTo("tracker2")
            subTrackerGroup.tracker3.defaultMetricNameTo("tracker3")
        }

        composedTrackerGroup.trackers
        verify {
            composedTrackerGroup.compTracker1.defaultMetricNameTo("compTracker1")
            composedTrackerGroup.compTracker2.defaultMetricNameTo("compTracker2")
            composedTrackerGroup.tracker1.defaultMetricNameTo("tracker1")
            composedTrackerGroup.tracker2.defaultMetricNameTo("tracker2")
            composedTrackerGroup.tracker3.defaultMetricNameTo("tracker3")
        }
    }

    private open class TrackerGroup : IssueTrackerGroup() {

        val tracker1 = mockk<IssueTracker>(relaxed = true)
        val tracker2 = mockk<IssueTracker>(relaxed = true)

    }

    private class SubTrackerGroup : TrackerGroup() {

        val tracker3 = mockk<IssueTracker>(relaxed = true)

    }

    private interface CompTrackers {
        val compTracker1: IssueTracker
        val compTracker2: IssueTracker
    }

    private class CompTrackersImpl : CompTrackers {
        override val compTracker1 = mockk<IssueTracker>(relaxed = true)
        override val compTracker2 = mockk<IssueTracker>(relaxed = true)
    }

    private class ComposedTrackerGroup : TrackerGroup(), CompTrackers by CompTrackersImpl() {
        val tracker3 = mockk<IssueTracker>(relaxed = true)
    }

}

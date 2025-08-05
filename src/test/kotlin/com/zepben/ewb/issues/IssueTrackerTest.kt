/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */


package com.zepben.ewb.issues

import com.zepben.ewb.metrics.NetworkLevel
import com.zepben.ewb.metrics.NetworkMetrics
import com.zepben.ewb.metrics.PartialNetworkContainer
import com.zepben.ewb.metrics.TotalNetworkContainer
import com.zepben.testutils.junit.SystemLogExtension
import io.mockk.confirmVerified
import io.mockk.mockk
import io.mockk.verifyAll
import io.mockk.verifySequence
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.slf4j.Logger
import org.slf4j.event.Level

internal class IssueTrackerTest {

    @JvmField
    @RegisterExtension
    var systemOut: SystemLogExtension = SystemLogExtension.SYSTEM_OUT.captureLog().muteOnSuccess()

    private val logger = mockk<Logger>(relaxed = true)

    @Test
    internal fun logsBasedOnLogLevel() {
        IssueTracker(logger, Level.INFO, logIssues = true) { "summary $it" }.track("message1")
        IssueTracker(logger, Level.WARN, logIssues = true) { "summary $it" }.track("message2")
        IssueTracker(logger, Level.ERROR, logIssues = true) { "summary $it" }.track("message3")
        IssueTracker(logger, Level.DEBUG, logIssues = true) { "summary $it" }.track("message4")
        IssueTracker(logger, Level.TRACE, logIssues = true) { "summary $it" }.track("message5")

        verifySequence {
            logger.info("message1")
            logger.warn("message2")
            logger.error("message3")
            logger.debug("message4")
            logger.trace("message5")
        }
    }

    @Test
    internal fun onlyLogsTrackedMessageIfRequested() {
        IssueTracker(logger, Level.INFO) { "summary $it" }.track("message1")
        IssueTracker(logger, Level.INFO, logIssues = true) { "summary $it" }.track("message2")

        verifyAll {
            logger.info("message2")
        }
    }

    @Test
    internal fun canChangeLoggingOfTrackedMessage() {
        val tracker = IssueTracker(logger, Level.INFO) { "summary $it" }

        tracker.track("message1")
        tracker.logIssues = true
        tracker.track("message2")
        tracker.logIssues = false
        tracker.track("message3")

        verifyAll {
            logger.info("message2")
        }
    }

    @Test
    internal fun loggingDefaultsToStaticVariable() {
        val previous = IssueTracker.defaultLogIssues

        IssueTracker(logger, Level.INFO) { "summary $it" }.track("message1")
        IssueTracker.defaultLogIssues = !previous
        IssueTracker(logger, Level.INFO) { "summary $it" }.track("message2")
        IssueTracker.defaultLogIssues = previous

        if (previous)
            verifyAll { logger.info("message1") }
        else
            verifyAll { logger.info("message2") }
    }

    @Test
    internal fun canUseFormatParametersForTrackedMessage() {
        IssueTracker(logger, Level.INFO, logIssues = true) { "summary $it" }.track("%s format %s with %d details", "can", "message", 3)

        verifyAll { logger.info("can format message with 3 details") }
    }

    @Test
    internal fun countsIssuesTracked() {
        val tracker = IssueTracker(logger, Level.INFO) { "summary $it" }

        tracker.track("message1")
        tracker.track("message2")

        assertThat(tracker.issueCount, equalTo(2))
    }

    @Test
    internal fun logsSummaryWithCountToCorrectLogLevel() {
        val tracker = IssueTracker(logger, summaryLogLevel = Level.DEBUG) { "summary $it" }

        tracker.track("message1")
        tracker.track("message2")
        tracker.logSummary()

        verifyAll { logger.debug("summary 2") }
    }

    @Test
    internal fun doesntLogSummaryIfNothingTracked() {
        val tracker = IssueTracker(logger, Level.INFO) { "summary $it" }

        tracker.logSummary()

        confirmVerified(logger)
    }

    @Test
    internal fun usesProvidedNetworkMetrics() {
        val networkMetrics = NetworkMetrics()
        val tracker = IssueTracker(logger, Level.INFO, networkMetrics = networkMetrics, metricName = "metricName") { "summary $it" }
        val substationTotalContainer = PartialNetworkContainer(NetworkLevel.SubstationTotal, "STN", "STN")
        val feederContainer1 = PartialNetworkContainer(NetworkLevel.Feeder, "FDR1", "FDR1")
        val feederContainer2 = PartialNetworkContainer(NetworkLevel.Feeder, "FDR2", "FDR2")

        tracker.track("unformatted", listOf(substationTotalContainer, feederContainer1))
        tracker.track("formatted {}", 123, networkContainers = listOf(substationTotalContainer, feederContainer2))

        assertThat(networkMetrics[TotalNetworkContainer]["metricName"], equalTo(2.0))
        assertThat(networkMetrics[substationTotalContainer]["metricName"], equalTo(2.0))
        assertThat(networkMetrics[feederContainer1]["metricName"], equalTo(1.0))
        assertThat(networkMetrics[feederContainer2]["metricName"], equalTo(1.0))
    }

    @Test
    internal fun ignoresDupedNetworkContainers() {
        val networkMetrics = NetworkMetrics()
        val tracker = IssueTracker(logger, Level.INFO, networkMetrics = networkMetrics, metricName = "metricName") { "summary $it" }
        val substationContainer = PartialNetworkContainer(NetworkLevel.Substation, "STN", "STN")

        tracker.track("message", listOf(substationContainer, substationContainer, TotalNetworkContainer))

        assertThat(networkMetrics[TotalNetworkContainer]["metricName"], equalTo(1.0))
        assertThat(networkMetrics[substationContainer]["metricName"], equalTo(1.0))
    }

    @Test
    internal fun incrementsMetricWhenUnnamed() {
        val networkMetrics = NetworkMetrics()
        val tracker = IssueTracker(logger, Level.INFO, networkMetrics = networkMetrics) { "summary $it" }

        tracker.track("message")

        assertThat(networkMetrics[TotalNetworkContainer]["Unnamed issue"], equalTo(1.0))
    }

    @Test
    internal fun usesFirstDefaultMetricName() {
        val networkMetrics = NetworkMetrics()
        val tracker = IssueTracker(logger, Level.INFO, networkMetrics = networkMetrics) { "summary $it" }.apply {
            defaultMetricNameTo("default1")
            defaultMetricNameTo("default2")
        }

        tracker.track("message")

        assertThat(networkMetrics[TotalNetworkContainer]["default1"], equalTo(1.0))
    }

    @Test
    internal fun providedMetricNameTakesPrecedenceOverDefault() {
        val networkMetrics = NetworkMetrics()
        val tracker = IssueTracker(logger, Level.INFO, networkMetrics = networkMetrics, metricName = "metricName") { "summary $it" }.apply {
            defaultMetricNameTo("default")
        }

        tracker.track("message")

        assertThat(networkMetrics[TotalNetworkContainer]["metricName"], equalTo(1.0))
    }

    @Test
    internal fun setNetworkMetrics() {
        val networkMetrics = NetworkMetrics()
        val tracker = IssueTracker(logger, Level.INFO, metricName = "metricName") { "summary $it" }

        tracker.track("message")
        tracker.setNetworkMetrics(networkMetrics)
        tracker.track("message")

        assertThat(networkMetrics[TotalNetworkContainer]["metricName"], equalTo(1.0))
    }

}

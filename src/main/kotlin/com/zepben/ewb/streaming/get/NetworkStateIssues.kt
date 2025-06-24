/*
 * Copyright (c) Zeppelin Bend Pty Ltd (Zepben) 2025 - All Rights Reserved.
 * Unauthorized use, copy, or distribution of this file or its contents, via any medium is strictly prohibited.
 */

package com.zepben.evolve.streaming.get

import com.zepben.ewb.issues.IssueTracker
import com.zepben.ewb.issues.IssueTrackerGroup
import com.zepben.evolve.metrics.NetworkMetrics
import org.slf4j.Logger
import org.slf4j.event.Level


open class NetworkStateIssues(logger: Logger, networkMetrics: NetworkMetrics) : IssueTrackerGroup() {

    open val invalidBacklogEvent: IssueTracker = IssueTracker(
        logger,
        logLevel = Level.ERROR,
        summaryLogLevel = Level.WARN,
        logIssues = true,
        networkMetrics = networkMetrics,
        metricName = "invalid_backlog_events"
    ) {
        "$it backlog events could not be deserialised"
    }

}
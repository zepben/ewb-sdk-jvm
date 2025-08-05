/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.streaming.get

import com.zepben.ewb.issues.IssueTracker
import com.zepben.ewb.issues.IssueTrackerGroup
import com.zepben.ewb.metrics.NetworkMetrics
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
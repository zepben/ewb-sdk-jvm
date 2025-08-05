/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.issues

import org.slf4j.Logger

open class IssuesLog(val detailedLogging: Boolean = false, val logger: Logger) {
    private val issueTrackers: MutableMap<String, MutableList<IssueTrackerGroup>> = linkedMapOf()

    open fun add(category: String, issueTrackerGroup: IssueTrackerGroup) {
        issueTrackerGroup.setLogIssues(detailedLogging)
        issueTrackers.computeIfAbsent(category) { ArrayList() }.add(issueTrackerGroup)
    }

    open fun logIssues() {
        val numOfTotalIssues = issueTrackers.values.flatMap { l -> l.asSequence() }.sumOf { it.trackers.sumOf { t -> t.issueCount } }

        if (numOfTotalIssues > 0) {
            logger.warn("Issues Summary:")
            issueTrackers.forEach { (category, trackerGroups) ->
                val numOfTotalIssuesInTrackerGroup = trackerGroups.sumOf { it.trackers.sumOf { t -> t.issueCount } }
                if (numOfTotalIssuesInTrackerGroup > 0) {
                    logger.warn("$category:")
                    trackerGroups.forEach { trackerGroup ->
                        val numOfIssues = trackerGroup.trackers.sumOf { t -> t.issueCount }
                        if (numOfIssues > 0)
                            trackerGroup.logSummaries()
                    }
                }
            }
        }
    }

}
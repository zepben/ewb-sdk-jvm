/*
 * Copyright (c) Zeppelin Bend Pty Ltd (Zepben) 2024 - All Rights Reserved.
 * Unauthorized use, copy, or distribution of this file or its contents, via any medium is strictly prohibited.
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
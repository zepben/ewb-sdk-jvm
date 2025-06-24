/*
 * Copyright (c) Zeppelin Bend Pty Ltd (Zepben) 2024 - All Rights Reserved.
 * Unauthorized use, copy, or distribution of this file or its contents, via any medium is strictly prohibited.
 */

package com.zepben.ewb.issues

import com.zepben.ewb.metrics.NetworkMetrics
import org.slf4j.LoggerFactory
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSupertypeOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

open class IssueTrackerGroup {

    val trackers: List<IssueTracker> by lazy { populateTrackers() }

    fun setNetworkMetrics(networkMetrics: NetworkMetrics): IssueTrackerGroup = apply {
        trackers.forEach { it.setNetworkMetrics(networkMetrics) }
    }

    fun setLogIssues(logIssues: Boolean): IssueTrackerGroup = apply {
        trackers.forEach { it.logIssues = logIssues }
    }

    fun logSummaries(): IssueTrackerGroup = apply {
        trackers.forEach { it.logSummary() }
    }

    private fun populateTrackers(): List<IssueTracker> {
        val issueTrackerType = IssueTracker::class.createType()

        return javaClass.kotlin.memberProperties
            .filter { it.returnType.isSupertypeOf(issueTrackerType) }
            .mapNotNull { property ->
                val wasAccessible = property.isAccessible
                try {
                    property.isAccessible = true
                    (property.call(this) as IssueTracker).also { tracker ->
                        tracker.defaultMetricNameTo(property.name)
                    }
                } catch (e: IllegalAccessException) {
                    logger.warn("INTERNAL ERROR: Reflection failed to get issue tracker ${property.name} for '${javaClass.name}'", e)
                    null
                } finally {
                    property.isAccessible = wasAccessible
                }
            }
    }

    companion object {

        private val logger = LoggerFactory.getLogger(IssueTrackerGroup::class.java)

    }

}

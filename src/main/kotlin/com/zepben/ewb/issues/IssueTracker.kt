/*
 * Copyright (c) Zeppelin Bend Pty Ltd (Zepben) 2024 - All Rights Reserved.
 * Unauthorized use, copy, or distribution of this file or its contents, via any medium is strictly prohibited.
 */

package com.zepben.ewb.issues

import com.google.errorprone.annotations.FormatMethod
import com.zepben.ewb.metrics.NetworkContainer
import com.zepben.ewb.metrics.NetworkMetrics
import com.zepben.ewb.metrics.TotalNetworkContainer
import org.slf4j.Logger
import org.slf4j.event.Level

class IssueTracker(
    logger: Logger,
    logLevel: Level = Level.WARN,
    var logIssues: Boolean = defaultLogIssues,
    summaryLogLevel: Level = logLevel,
    private var networkMetrics: NetworkMetrics? = null,
    private var metricName: String? = null,
    private val networkContainers: List<NetworkContainer> = emptyList(),
    private val generateSummary: (Long) -> String,
) {

    private val summaryLogger = getLoggingMethod(summaryLogLevel, logger)
    private val issueLogger = getLoggingMethod(logLevel, logger)
    var issueCount = 0L; private set

    fun logSummary(): IssueTracker = apply {
        if (issueCount > 0)
            summaryLogger(generateSummary(issueCount))
    }

    @FormatMethod
    fun track(
        format: String,
        vararg formatArgs: Any?,
        networkContainers: List<NetworkContainer> = emptyList()
    ): IssueTracker = track(String.format(format, *formatArgs), networkContainers)

    /**
     * Track an issue. It will log the message, keep count of the number of calls to this function, and record a metric in [networkMetrics] if it is specified.
     *
     * @param overrideNetworkContainers The containers to use for the corresponding metric for this IssueTracker. The [TotalNetworkContainer] will also be incremented for the metric.
     * When null (default), it will use the containers specified in [networkContainers] plus [TotalNetworkContainer].
     */
    fun track(msg: String, overrideNetworkContainers: List<NetworkContainer>? = null): IssueTracker = apply {
        ++issueCount
        if (logIssues)
            issueLogger(msg)

        networkMetrics?.let { metrics ->
            (((overrideNetworkContainers ?: networkContainers) + TotalNetworkContainer)).distinct().forEach { networkContainer ->
                metrics[networkContainer].inc(metricName ?: "Unnamed issue")
            }
        }
    }

    internal fun setNetworkMetrics(networkMetrics: NetworkMetrics) {
        this.networkMetrics = networkMetrics
    }

    internal fun defaultMetricNameTo(defaultName: String) {
        metricName = metricName ?: defaultName
    }

    private fun getLoggingMethod(level: Level, logger: Logger): (String) -> Unit {
        return when (level) {
            Level.INFO -> { msg -> logger.info(msg) }
            Level.WARN -> { msg -> logger.warn(msg) }
            Level.ERROR -> { msg -> logger.error(msg) }
            Level.DEBUG -> { msg -> logger.debug(msg) }
            Level.TRACE -> { msg -> logger.trace(msg) }
            else -> throw UnsupportedOperationException("Unsupported log level: $level")
        }
    }

    companion object {

        @Volatile
        var defaultLogIssues: Boolean = false

    }

}

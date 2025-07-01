/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.services.network.tracing.traversal

import org.slf4j.Logger

internal object DebugLoggingWrappers {

    internal fun <T> wrapStopCondition(description: String, condition: StopCondition<T>, debugLogger: Logger, count: Int): StopCondition<T> =
        StopCondition { item: T, context: StepContext ->
            val shouldStop = condition.shouldStop(item, context)
            debugLogger.info("$description: shouldStop($count)=$shouldStop [item=$item, context=$context]")
            shouldStop
        }

    internal fun <T> wrapQueueCondition(description: String, condition: QueueCondition<T>, debugLogger: Logger, count: Int): QueueCondition<T> =
        object : QueueCondition<T> {
            override fun shouldQueue(
                nextItem: T,
                nextContext: StepContext,
                currentItem: T,
                currentContext: StepContext
            ): Boolean {
                val shouldQueue = condition.shouldQueue(nextItem, nextContext, currentItem, currentContext)
                debugLogger.info("$description: shouldQueue($count)=$shouldQueue [nextItem=$nextItem, nextContext=$nextContext, currentItem=$currentItem, currentContext=$currentContext]")
                return shouldQueue
            }

            override fun shouldQueueStartItem(item: T): Boolean {
                val shouldQueueStartItem = condition.shouldQueueStartItem(item)
                debugLogger.info("$description: shouldQueueStartItem($count)=$shouldQueueStartItem [item=$item]")
                return shouldQueueStartItem
            }
        }

    internal fun <T> wrapStepAction(description: String, action: StepAction<T>, debugLogger: Logger, count: Int): StepAction<T> =
        StepAction { item: T, context: StepContext ->
            debugLogger.info("$description: steppingOn($count) [item=$item, context=$context]")
            action.apply(item, context)

        }

}

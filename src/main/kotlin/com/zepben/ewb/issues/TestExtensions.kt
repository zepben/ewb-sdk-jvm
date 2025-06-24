/*
 * Copyright (c) Zeppelin Bend Pty Ltd (Zepben) 2024 - All Rights Reserved.
 * Unauthorized use, copy, or distribution of this file or its contents, via any medium is strictly prohibited.
 */

package com.zepben.ewb.issues

/**
 * Extension functions for testing classes in the issues package.
 */
object TestExtensions {

    fun IssueTrackerGroup.validateSummaries() {
        trackers.forEach { it.track("") }
        logSummaries()
    }

}

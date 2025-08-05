/*
 * Copyright 2025 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
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

/*
 * Copyright 2026 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.metrics.dataquality

import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.arrayContaining
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class DataQualityIssueStatusTest {

    companion object {
        @JvmField
        @RegisterExtension
        val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()
    }

    @Test
    internal fun `has expected values`() {
        assertThat(
            DataQualityIssueStatus.entries.toTypedArray(),
            arrayContaining(
                DataQualityIssueStatus.CREATED,
                DataQualityIssueStatus.IN_PROGRESS,
                DataQualityIssueStatus.BLOCKED,
                DataQualityIssueStatus.RESOLVED
            )
        )
    }

    @Test
    internal fun `valueOf resolves each status`() {
        assertThat(DataQualityIssueStatus.valueOf("CREATED"), equalTo(DataQualityIssueStatus.CREATED))
        assertThat(DataQualityIssueStatus.valueOf("IN_PROGRESS"), equalTo(DataQualityIssueStatus.IN_PROGRESS))
        assertThat(DataQualityIssueStatus.valueOf("BLOCKED"), equalTo(DataQualityIssueStatus.BLOCKED))
        assertThat(DataQualityIssueStatus.valueOf("RESOLVED"), equalTo(DataQualityIssueStatus.RESOLVED))
    }
}

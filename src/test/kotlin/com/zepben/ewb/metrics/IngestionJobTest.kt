/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.metrics

import com.zepben.testutils.junit.SystemLogExtension
import io.mockk.mockk
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.empty
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.util.*

internal class IngestionJobTest {

    companion object {
        @JvmField
        @RegisterExtension
        val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()
    }

    private val uuid = UUID.randomUUID()

    @Test
    internal fun constructorCoverage() {
        val metadata = mockk<IngestionMetadata>()
        val job = IngestionJob(uuid, metadata)
        assertThat(job.id, equalTo(uuid))
        assertThat(job.metadata, equalTo(metadata))
        assertThat(job.sources.entries, empty())
        assertThat(job.networkMetrics.entries, empty())
    }

}

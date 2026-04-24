/*
 * Copyright 2024 Zeppelin Bend Pty Ltd
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.zepben.ewb.metrics

import com.zepben.testutils.junit.SystemLogExtension
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.time.Instant

internal class IngestionMetadataTest {

    companion object {
        @JvmField
        @RegisterExtension
        val systemErr: SystemLogExtension = SystemLogExtension.SYSTEM_ERR.captureLog().muteOnSuccess()
    }

    @Test
    internal fun constructorCoverage() {
        val metadata = IngestionMetadata(Instant.EPOCH, "source", "application", "applicationVersion")

        assertThat(metadata.startTime, equalTo(Instant.EPOCH))
        assertThat(metadata.source, equalTo("source"))
        assertThat(metadata.application, equalTo("application"))
        assertThat(metadata.applicationVersion, equalTo("applicationVersion"))
    }

}
